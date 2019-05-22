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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.util.DigestUtils;
import com.hitachivantara.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.example.hcp.util.RandomInputStream;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;

/**
 * 使用多线程创建2000随机内容个文件
 * </p>
 * Create 2000 random content files using multiple threads
 * 
 * @author sohan
 *
 */
public class S3Example_Create2000RandomFiles {

	public static void main(String[] args) throws IOException {
		{
			AmazonS3 hcpClient = HCPClients.getInstance().getS3Client();

			// Here is the folder path you want to store files.
			final String directoryKey = "example-hcp/moreThan100objs/";

			// 使用多线程（10个线程每个线程创建200个）创建100随机内容个文件
			// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
			for (int i = 0; i < 100; i++) {
				final int id = i;

				new Thread(new Runnable() {
					@Override
					public void run() {

						for (int j = 0; j < 2000; j++) {
							String key = directoryKey + "file-" + id + "-" + j + ".txt";
							String cid = "("+id+"-"+j+")";
							 String content = cid;//+ (new Date().toString() + " " + RandomInputStream.randomInt(10000, 99999));
							//
							 ObjectMetadata metadata = new ObjectMetadata();
							 metadata.setContentLength(content.length());
							//
							 PutObjectRequest req = new PutObjectRequest(Account.namespace, key, new ByteArrayInputStream(content.getBytes()), metadata);
							 PutObjectResult result = hcpClient.putObject(req);

							// System.out.println("Create file: " + key + " " + result.getETag());

//							S3Object obj = hcpClient.getObject(Account.namespace, key);
//							S3ObjectInputStream in = obj.getObjectContent();
//							try {
//								String content = StreamUtils.inputStreamToString(in, true);
//							} catch (IOException e) {
//							}
//
//							System.out.println(Thread.currentThread().getId() + key + "-> ETAG OK!");

							// boolean exist = hcpClient.doesObjectExist(Account.namespace, key);
							// if (!exist)
							// System.out.println("Exist file: " + key + " " + exist);
						}
						
						System.out.println("Finished "+id);
					}
				}).start();

			}
			// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*

		}
	}

}
