/*                                                                             
 * Copyright (C) 2019 Hitachi Vantara Inc.                                     
 *                                                                             
 * Licensed under the Apache License, Version 2.0 (the "License");           
 * you may not use this file except in compliance with the License.            
 * You may obtain a copy of the License at                                     
 *                                                                             
 *      http://www.apache.org/licenses/LICENSE-2.0                             
 *                                                                             
 * Unless required by applicable law or agreed to in writing, software         
 * distributed under the License is distributed on an "AS IS" BASIS,         
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    
 * See the License for the specific language governing permissions and         
 * limitations under the License.                                              
 */
package com.hitachivantara.example.hcp.content;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.Protocol;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.hitachivantara.example.hcp.util.Account;

/**
 * @author sohan
 *
 */
public class S3Example_AWS {

	public static void main(String[] args) throws IOException {
		AmazonS3 hs3Client = null;
		{
			hs3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_1)
					.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("AKIAZTNNKGQV6OSMSSEA", "8cMoJv7oFF7C15077uSqGlAFVz7oZXG5MRzBMymH"))).build();

		}

		S3Object s3Object = null;
		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		//s3://song99/20190927.log
		String key = "example/" + file.getName();
		String bucketName = "song99";
		
		{
			long i = 0;
			try {
				ObjectMetadata metadata = new ObjectMetadata();
				metadata.addUserMetadata("name", "Rison");
				metadata.addUserMetadata("company", "hitachi vantara");
//				metadata.addUserMetadata("chs", "中文测试");
				metadata.addUserMetadata("en", "The request signature we calculated does not match the signature you provided. Check your key and signing method. (Service: Amazon S3; Status Code: 403; Error Code: SignatureDoesNotMatch; Request ID: CB1394C661B37D4F; S3 Extended Request ID: Gctj84g8sJ0sv2sfkqHKr+O+nyruI85jNGKXL5tMqFEHOqEOvGQuXFrj45GPf9M8+xMQU4Fk1Wo=), S3 Extended Request ID: Gctj84g8sJ0sv2sfkqHKr+O+nyruI85jNGKXL5tMqFEHOqEOvGQuXFrj45GPf9M8+xMQU4Fk1Wo=");

				// Inject file into HCP system.
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				hs3Client.putObject(new PutObjectRequest(bucketName, key, file).withMetadata(metadata));
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
			} catch (AmazonServiceException e) {
				e.printStackTrace();
				return;
			} catch (SdkClientException e) {
				e.printStackTrace();
				return;
			}
		}

//		{
//			long i = 0;
//			try {
//				// Here is the folder path you want to list.
//				String directoryKey = "";
//
//				// Request HCP to list all the objects in this folder.
//				// 罗列此目录以及子目录的所有对象，包括目录本身
//				ObjectListing objlisting = hs3Client.listObjects(new ListObjectsRequest().withBucketName(bucketName).withPrefix(directoryKey));
//				// 仅罗列此目录所有对象，包括目录本身
////				ObjectListing objlisting = hs3Client.listObjects(new ListObjectsRequest().withBucketName(bucketName).withPrefix(directoryKey).withDelimiter("/"));
//
//				// Printout objects
//				do {
//					List<S3ObjectSummary> objs = objlisting.getObjectSummaries();
//					for (S3ObjectSummary obj : objs) {
//						System.out.println(++i + "\t" + obj.getSize() + "\t" + obj.getETag() + "\t" + obj.getKey());
//					}
//					objlisting = hs3Client.listNextBatchOfObjects(objlisting);
//				} while (objlisting.isTruncated());
//				
//				// Printout remain items
//				List<S3ObjectSummary> objs = objlisting.getObjectSummaries();
//				for (S3ObjectSummary obj : objs) {
//					System.out.println(++i + "\t" + obj.getSize() + "\t" + obj.getETag() + "\t" + obj.getKey());
//				}
//				
//			} catch (AmazonServiceException e) {
//				e.printStackTrace();
//				return;
//			} catch (SdkClientException e) {
//				e.printStackTrace();
//				return;
//			}
//		}
		
//		{
//			hs3Client.deleteObject(bucketName, "a1/");
//			hs3Client.deleteObject(bucketName, "a1/b1/c1/");
//		}

		//		{
//			try {
//				// 上传文件至HCP
//				// 上传前无需刻意创建目录，只需指定存储路径
//				// Inject file into HCP system.
//				hs3Client.putObject(bucketName, key, file);
//
//				// Check whether object exist.
//				boolean exist = hs3Client.doesObjectExist(bucketName, key);
//
//				// Get the object from HCP
//				// s3Object = hs3Client.getObject(bucketName, key);
//			} catch (AmazonServiceException e) {
//				e.printStackTrace();
//				return;
//			} catch (SdkClientException e) {
//				e.printStackTrace();
//				return;
//			}
//		}

		// ↓↓↓=*=*=* CODE JUST FOR DEMONSTRATE, UNNECESSARY IN PRODUCTION ENVIRONMENT *=*=*=↓↓↓
		// Verify result:
		// S3ObjectInputStream in = s3Object.getObjectContent();
		//// StreamUtils.inputStreamToFile(in, filePath, true);
		//// StreamUtils.inputStreamToConsole(in, true);
		// byte[] orginalFileMd5 = DigestUtils.calcMD5(file);
		// byte[] objectFromHCPMd5 = DigestUtils.calcMD5(in);
		// in.close();
		////
		// boolean equals = Arrays.equals(orginalFileMd5, objectFromHCPMd5);
		// assertTrue(equals == true);
		// // ↑↑↑=*=*=* CODE JUST FOR DEMONSTRATE, UNNECESSARY IN PRODUCTION ENVIRONMENT *=*=*=↑↑↑
		//
		// {
		// // Delete object in HCP.
		// hs3Client.deleteObject(bucketName, key);
		//
		// // Check whether object exist.
		// boolean exist = hs3Client.doesObjectExist(bucketName, key);
		// assertTrue(exist == false);
		// }

		System.out.println("Well done!");
	}

}
