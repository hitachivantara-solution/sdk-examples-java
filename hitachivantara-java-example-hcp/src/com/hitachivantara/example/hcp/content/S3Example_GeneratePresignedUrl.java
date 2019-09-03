package com.hitachivantara.example.hcp.content;

import java.net.URL;
import java.util.Date;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;

/**
 * 通过于预签名可以生成临时访问链接，达到无需密钥访问数据的需求
 * 
 * @author sohan
 *
 */
public class S3Example_GeneratePresignedUrl {

	public static void main(String[] args) {
		AmazonS3 hs3Client = HCPClients.getInstance().getS3Client();

		// Here is the file will be uploaded into HCP
		// String objectKey = "/7-Zip/apache-tomcat-7.0.78/RUNNING.txt";
		String objectKey = "/7-Zip/apache-tomcat-7.0.78/lib/catalina.jar";
		// The location in HCP where this file will be stored.
		String bucketName = Account.namespace;

		// 公开链接有效期截至时间
		// Add 1 minute.
		Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 1);

		// 生成预签名时间
		URL url = hs3Client.generatePresignedUrl(new GeneratePresignedUrlRequest(bucketName, objectKey).withExpiration(expiration).withMethod(HttpMethod.GET));

		System.out.println(url.toString());
		// http://ns2.tenant1.hcp.changhongx.com/%2F7-Zip/apache-tomcat-7.0.78/RUNNING.txt?AWSAccessKeyId=YWRtaW4%3D&Expires=1567500297&Signature=f%2BrBwKYg6%2BPjAAgE7DSLk%2FcbS48%3D
	}

}
