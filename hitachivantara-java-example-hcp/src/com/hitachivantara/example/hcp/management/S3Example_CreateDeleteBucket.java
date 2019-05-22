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
package com.hitachivantara.example.hcp.management;

import java.io.IOException;

import com.amazonaws.services.s3.AmazonS3;
import com.hitachivantara.example.hcp.util.HCPClients;

/**
 * 演示如何通过S3创建桶 通过S3创建桶的过程无法配置桶参数，建议参考RestExample_NamespaceCreate.java创建桶空间
 * </p>
 * Example to show how to create a bucket via S3, the bucket parameter cannot be configured by the S3 bucket creation procedure;
 * </p>
 * It is RECOMMENDED to refer to RestExample_NamespaceCreate.Java to create a bucket
 * 
 * @author sohan
 *
 */
public class S3Example_CreateDeleteBucket {

	public static void main(String[] args) throws IOException {
		{
			AmazonS3 hs3Client = HCPClients.getInstance().getS3Client();

			String bucketName = "bucket000";
			// Need to [Enable management through APIs] and login user has [Allow namespace management] in HCP
//			hs3Client.deleteBucket(bucketName);
			hs3Client.createBucket(bucketName);

		}

	}

}
