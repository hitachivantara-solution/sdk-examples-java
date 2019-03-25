package com.hitachivantara.example.hcp.content;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hitachivantara.core.http.util.URLUtils;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.example.hcp.util.RandomInputStream;

/**
 * 展示如何创建目录，！默认无需创建目录！
 * </p>
 * Show how to create a directory! There is no need to create a directory by default!
 * 
 * @author sohan
 *
 */
public class S3Example_CreateDirectory {

	public static void main(String[] args) throws IOException {
		// The location in HCP where this file will be stored.
		final String directoryKey = "example-hcp/subfolder" + RandomInputStream.randomInt(100, 999);
		String bucketName = Account.namespace;

		AmazonS3 hs3Client = HCPClients.getInstance().getS3Client();

		createFolder(bucketName, directoryKey, hs3Client);

		System.out.println("Well done!");
	}

	public static void createFolder(String bucketName, String folderName, AmazonS3 client) {
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);

		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, URLUtils.catPath(folderName, "/"), emptyContent, metadata);

		// send request to S3 to create folder
		client.putObject(putObjectRequest);
	}

}
