package com.hitachivantara.example.hcp.content;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.Protocol;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.hitachivantara.common.util.DigestUtils;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;

/**
 * S3 列出目录包括子目录示例
 * @author sohan
 *
 */
public class S3Example_ListObjects {

	public static void main(String[] args) throws IOException {
		AmazonS3 hs3Client = HCPClients.getInstance().getS3Client();

		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String bucketName = Account.namespace;

		// 此处准备一些object用来list
		// Prepare some objects for list.
		{
			for (int i = 0; i < 5; i++) {
				String key = "folder/L1TestObject" + i + ".doc";
				hs3Client.putObject(bucketName, key, file);
			}

			for (int i = 0; i < 10; i++) {
				String key = "folder/subfolder/L2TestObject" + i + ".doc";
				hs3Client.putObject(bucketName, key, file);
			}
		}

		{
			long i = 0;
			try {
				// Here is the folder path you want to list.
				String directoryKey = "sdk-test/moreThan100objs/";
//				String directoryKey = "";

				// 罗列指定目录中的所有对象
				// Request HCP to list all the objects in this folder.
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				ObjectListing objlisting = hs3Client.listObjects(new ListObjectsRequest().withBucketName(bucketName).withPrefix(directoryKey).withMaxKeys(9000));
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*

				// Printout objects
				List<S3ObjectSummary> objs = objlisting.getObjectSummaries();
				for (S3ObjectSummary s3ObjectSummary : objs) {
					System.out.println(++i + "\t" + s3ObjectSummary.getSize() + "\t" + s3ObjectSummary.getETag() + "\t" + s3ObjectSummary.getKey());
				}
				
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				ObjectListing nextObjlisting = hs3Client.listNextBatchOfObjects(objlisting);
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				// Printout objects
				List<S3ObjectSummary> nextobjs = nextObjlisting.getObjectSummaries();
				for (S3ObjectSummary s3ObjectSummary : nextobjs) {
					System.out.println(++i + "\t" + s3ObjectSummary.getSize() + "\t" + s3ObjectSummary.getETag() + "\t" + s3ObjectSummary.getKey());
				}

			} catch (AmazonServiceException e) {
				e.printStackTrace();
				return;
			} catch (SdkClientException e) {
				e.printStackTrace();
				return;
			}
		}

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	}

}
