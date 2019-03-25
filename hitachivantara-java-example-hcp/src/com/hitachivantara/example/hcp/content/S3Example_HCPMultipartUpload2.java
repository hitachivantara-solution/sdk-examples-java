package com.hitachivantara.example.hcp.content;

import java.io.File;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListMultipartUploadsRequest;
import com.amazonaws.services.s3.model.ListPartsRequest;
import com.amazonaws.services.s3.model.MultipartUpload;
import com.amazonaws.services.s3.model.MultipartUploadListing;
import com.amazonaws.services.s3.model.PartListing;
import com.amazonaws.services.s3.model.PartSummary;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.example.hcp.util.multipartupload.MulitipartUploadException;

/**
 * S3 分片上传示例
 * @author sohan
 *
 */
public class S3Example_HCPMultipartUpload2 {
	public static void main(String[] args) throws MulitipartUploadException {
		// 取得客户端Instance
		final AmazonS3 s3Client = HCPClients.getInstance().getS3Client();

		// 分片大小（**万兆带宽推荐设置100MB**，此处示例设置为10MB）
		final int PART_SIZE = 10 * 1024 * 1024; // Set part size to 10 MB.

		// 测试用大文件（**分片上传文件应为大文件至少500MB以上，小文件不建议使用分片方式上传**）
		final File tobeUploadFile = new File("C:\\VDisk\\DriverD\\Downloads\\Libs\\tika-app-1.7.jar");
		// 上传key
		final String objectPath = "hcp-test/" + tobeUploadFile.getName() + "6";
		// 桶名称
		final String bucketName = Account.namespace;

		// ==========================================================================================================================
		MultipartUploadListing mpoListing = s3Client.listMultipartUploads(new ListMultipartUploadsRequest(bucketName));
		List<MultipartUpload> ups = mpoListing.getMultipartUploads();
		for (MultipartUpload multipartUpload : ups) {
			System.out.println(multipartUpload.getKey()+"\t"+multipartUpload.getUploadId());
		}
		
		InitiateMultipartUploadResult initResult = s3Client.initiateMultipartUpload(new InitiateMultipartUploadRequest(bucketName, objectPath));
		String uploadId = initResult.getUploadId();
		for (int partNumber = 1; partNumber < 4; partNumber++) {
			s3Client.uploadPart(new UploadPartRequest()
					.withKey(objectPath)
					.withBucketName(bucketName)
					.withPartNumber(partNumber)
					.withFile(tobeUploadFile)
					.withPartSize(PART_SIZE)
					.withUploadId(uploadId)
					);
			System.out.println(partNumber);
		}
		
		PartListing partListing = s3Client.listParts(new ListPartsRequest(bucketName, objectPath, uploadId));
		List<PartSummary> parts = partListing.getParts();
		for (PartSummary partSummary : parts) {
			System.out.println(partSummary.getPartNumber()+"\t"+partSummary.getSize()+"\t"+partSummary.getETag());
		}
		// =========================================================================================================================
		// =========================================================================================================================

	}

}
