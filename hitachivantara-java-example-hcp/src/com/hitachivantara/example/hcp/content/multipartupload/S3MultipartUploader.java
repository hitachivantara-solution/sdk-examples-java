package com.hitachivantara.example.hcp.content.multipartupload;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;

/**
 * 封装S3分片上传功能
 * 
 * @author sohan
 *
 */
public class S3MultipartUploader {

	private final AmazonS3 hs3Client;
	private final String bucketName;
	private final String objectPath;
	private final long expectObjectSize;
	private Long uploadedSize = new Long(0);

	private String uploadId = null;
	private final List<PartETag> partETags = new ArrayList<PartETag>();

	private UploadEventHandler handler = null;

	private long startTime;
	private long endTime;

	public S3MultipartUploader(AmazonS3 hs3Client, String bucketName, String objectPath, long expectObjectSize) {
		this.hs3Client = hs3Client;
		this.bucketName = bucketName;
		this.objectPath = objectPath;
		this.expectObjectSize = expectObjectSize;
	}

	public void setHandler(UploadEventHandler handler) {
		this.handler = handler;
	}

	public String init() {
		startTime = System.currentTimeMillis();

		// Step 1: Initialize.
		if (uploadId == null) {
			InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, objectPath);
			InitiateMultipartUploadResult initResponse = hs3Client.initiateMultipartUpload(initRequest);
			uploadId = initResponse.getUploadId();
		}

		if (handler != null) {
			handler.init(bucketName, objectPath, uploadId);
		}

		return uploadId;
	}

	public PartETag upload(int partNumber, InputStream in, long uploadPartsize, String executor) throws MulitipartUploadException {
		// Create request to upload a part.
		UploadPartRequest uploadRequest = new UploadPartRequest();
		uploadRequest.withBucketName(bucketName);
		uploadRequest.withKey(objectPath);
		uploadRequest.withUploadId(uploadId);
		uploadRequest.withPartNumber(partNumber);
		uploadRequest.withInputStream(in);
		uploadRequest.withPartSize(uploadPartsize);

		long startTime, endTime = 0;
		startTime = System.currentTimeMillis();
		if (handler != null) {
			handler.beforePartUpload(bucketName, objectPath, uploadId, partNumber, uploadPartsize, startTime);
		}

		PartETag result = null;
		try {
			//上传分片
			result = hs3Client.uploadPart(uploadRequest).getPartETag();
			endTime = System.currentTimeMillis();
			partETags.add(result);
		} catch (Exception e) {
			endTime = System.currentTimeMillis();

			if (handler != null) {
				handler.caughtPartUploadException(bucketName, objectPath, uploadId, partNumber, uploadPartsize, e);
			}
		}

		if (handler != null) {
			handler.afterPartUpload(bucketName, objectPath, uploadId, partNumber, uploadPartsize, startTime, endTime);
		}

		synchronized (uploadedSize) {
			uploadedSize += uploadPartsize;
		}

		return result;
	}

	public CompleteMultipartUploadResult complete() throws MulitipartUploadException {
		if (uploadedSize == expectObjectSize) {
			// Step 3: Complete.
			CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName, objectPath, uploadId, partETags);

			// 合并各个分片为一个整体文件
			CompleteMultipartUploadResult result = hs3Client.completeMultipartUpload(compRequest);

			endTime = System.currentTimeMillis();
			
			if (handler != null) {
				handler.complete(bucketName, objectPath, uploadId, uploadedSize, startTime, endTime);
			}

			return result;
		} else {
			throw new MulitipartUploadException(objectPath + " uncompleted! " + uploadedSize + "/" + expectObjectSize);
		}
	}

	public void abortMultipartUpload() {
		//清除分片上传的数据（当希望清除失败的分片上传任务时才调用此函数）
		hs3Client.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, objectPath, uploadId));
		uploadId = null;
	}

	public String getUploadId() {
		return uploadId;
	}

	public List<PartETag> getPartETags() {
		return partETags;
	}

}