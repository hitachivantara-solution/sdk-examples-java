package com.hitachivantara.example.hcp.content;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.Protocol;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.hitachivantara.common.util.DigestUtils;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;

/**
 * 删除多个对象
 * @author sohan
 *
 */
public class S3Example_DeleteMultiObjects {

	public static void main(String[] args) throws IOException {
		AmazonS3 hs3Client = HCPClients.getInstance().getS3Client();

		//此功能需要HCP Version8.1 or later才支持
		hs3Client.deleteObjects(new DeleteObjectsRequest(Account.namespace).withKeys("/loadjdv-992.txt","/loadjdv-991.txt"));
		
		System.out.println("Well done!");
	}

}
