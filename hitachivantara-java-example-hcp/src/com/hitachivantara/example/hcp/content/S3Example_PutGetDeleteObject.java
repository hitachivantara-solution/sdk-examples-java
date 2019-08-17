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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.hitachivantara.common.util.DigestUtils;
import com.hitachivantara.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.Account;

/**
 * S3 对象文件存、删、取示例
 * @author sohan
 *
 */
public class S3Example_PutGetDeleteObject {

	public static void main(String[] args) throws IOException {
		AmazonS3 hs3Client = null;
		{
			// 创建S3客户端，只需要创建一次客户端，请将endpoint及用户名密码更改为您的HCP配置
			// Create s3 client
			// 指定需要登录的HCP 租户 及 桶
			String endpoint = Account.endpoint;
			// 登录需要的用户名
			// The access key encoded by Base64
			String accessKey = Account.accessKey;
			// 登录需要的密码
			// The AWS secret access key encrypted by MD5
			String secretKey = Account.secretKey;

			com.amazonaws.ClientConfiguration clientConfig = new com.amazonaws.ClientConfiguration();
			// Using HTTP protocol
			clientConfig.setProtocol(Protocol.HTTP);
			clientConfig.setSignerOverride("S3SignerType");
//			clientConfig.setMaxConnections(maxConnections);
			
			hs3Client = AmazonS3ClientBuilder.standard()
					.withClientConfiguration(clientConfig)
					.withEndpointConfiguration(new EndpointConfiguration(endpoint, ""))
					.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
					.build();
		}
		
		S3Object s3Object = null;
		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String key = "example-hcp/subfolder1/" + file.getName();
		String bucketName = Account.namespace;
		
		{
			try {
				// 上传文件至HCP
				// 上传前无需刻意创建目录，只需指定存储路径
				// Inject file into HCP system.
				hs3Client.putObject(bucketName, key, file);

				// Check whether object exist.
				boolean exist = hs3Client.doesObjectExist(bucketName, key);
				assertTrue(exist == true);

				// Get the object from HCP
				s3Object = hs3Client.getObject(bucketName, key);
			} catch (AmazonServiceException e) {
				e.printStackTrace();
				return;
			} catch (SdkClientException e) {
				e.printStackTrace();
				return;
			}
		}

		// ↓↓↓=*=*=* CODE JUST FOR DEMONSTRATE, UNNECESSARY IN PRODUCTION ENVIRONMENT *=*=*=↓↓↓
		// Verify result:
		S3ObjectInputStream in = s3Object.getObjectContent();
//		StreamUtils.inputStreamToFile(in, filePath, true);
//		StreamUtils.inputStreamToConsole(in, true);
		byte[] orginalFileMd5 = DigestUtils.calcMD5(file);
		byte[] objectFromHCPMd5 = DigestUtils.calcMD5(in);
		in.close();
//
		boolean equals = Arrays.equals(orginalFileMd5, objectFromHCPMd5);
		assertTrue(equals == true);
		// ↑↑↑=*=*=* CODE JUST FOR DEMONSTRATE, UNNECESSARY IN PRODUCTION ENVIRONMENT *=*=*=↑↑↑

		{
			hs3Client.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys("111","222"));
			// Delete object in HCP.
			hs3Client.deleteObject(bucketName, key);
			
			// Check whether object exist.
			boolean exist = hs3Client.doesObjectExist(bucketName, key);
			assertTrue(exist == false);
		}
		
		System.out.println("Well done!");
	}

}
