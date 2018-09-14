package com.hitachivantara.example.hcp.content;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.hitachivantara.common.util.DigestUtils;
import com.hitachivantara.example.hcp.content.multipartupload.MulitipartUploadException;
import com.hitachivantara.example.hcp.content.multipartupload.MulitipartUploaderExecutor;
import com.hitachivantara.example.hcp.content.multipartupload.S3MultipartUploader;
import com.hitachivantara.example.hcp.content.multipartupload.UploadEventHandler;
import com.hitachivantara.example.hcp.util.HCPClients;

public class S3Example_HCPMultipartUpload_UploadMissingPart {
	public static void main(String[] args) throws MulitipartUploadException {
		// 取得客户端Instance
		final AmazonS3 s3Client = HCPClients.getInstance().getS3Client();

		// 分片大小（**万兆带宽推荐设置100MB**，此处示例设置为10MB）
		final int PART_SIZE = 10 * 1024 * 1024; // Set part size to 10 MB.

		// 测试用大文件（**分片上传文件应为大文件至少500MB以上，小文件不建议使用分片方式上传**）
		final File tobeUploadFile = new File("C:\\VDisk\\DriverD\\Downloads\\Libs\\cosbench-master.zip");
		// 上传key
		final String objectPath = "hcp-test/" + tobeUploadFile.getName() + "2";
		// 桶名称
		final String bucketName = "cloud";

		// ==========================================================================================================================
		// =========================================================================================================================
		S3MultipartUploader uploader = new S3MultipartUploader(s3Client, bucketName, objectPath, tobeUploadFile.length());

	}

}
