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

import java.io.IOException;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.ex.ParseException;
import com.hitachivantara.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.api.MetadataParser;
import com.hitachivantara.hcp.standard.api.event.ListObjectHandler;
import com.hitachivantara.hcp.standard.define.NextAction;
import com.hitachivantara.hcp.standard.model.HCPObjectSummary;
import com.hitachivantara.hcp.standard.model.metadata.Annotation;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadata;
import com.hitachivantara.hcp.standard.model.request.impl.ListObjectRequest;

/**
 * 列出指定目录下所有对象的示例，包括子目录
 * 
 * @author sohan
 *
 */
public class RestExample_ListAllMetadataFromFolder {

	public static void main(String[] args) throws IOException {
		{
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				// 需要列出的目录名
				// Here is the folder path you want to list.
				String directoryKey = "example-hcp/moreThan100objs/";

				// 遍历目录
				// Request HCP to list all the objects in this folder.
				// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				ListObjectRequest request = new ListObjectRequest(directoryKey)
						// 指定需要遍历此目录
						.withRecursiveDirectory(true)
						// 可以通过设置Filter过滤对象
//						.withObjectFilter(new ObjectFilter() {
//
//							@Override
//							public boolean accept(HCPObjectEntry arg0) {
//								//只有对象名称包含字母X的才被foundObject
//								return arg0.getName().contains("X");
//							}
//						})
						;
				hcpClient.listObjects(request, new ListObjectHandler() {

					// 发现的对象信息
					@Override
					public NextAction foundObject(HCPObjectSummary objectSummary) throws HSCException {
						//取得所有meta并打印
						Annotation[] metas = objectSummary.getMetadata();
						if (metas != null) {
							System.out.println("\n-------------------------------------------------------------------------------");
							System.out.println("Key=" + objectSummary.getKey());
							for (Annotation meta : metas) {
								
								hcpClient.getMetadata(objectSummary.getKey(), meta.getName(), new MetadataParser<String>() {

									@Override
									public String parse(HCPMetadata metadata) throws ParseException {
										try {
											System.out.println("\nContent of Metadata [" + meta.getName() + "]:");
											StreamUtils.inputStreamToConsole(metadata.getContent(), true);
											System.out.println();
										} catch (IOException e) {
											e.printStackTrace();
										}
										return null;
									}
								});
								
//								or
//								HCPMetadata hcpmeta = hcpClient.getMetadata(objectSummary.getKey(), meta.getName());
//								try {
//									System.out.println("\nContent of Metadata [" + meta.getName()+"]:");
//									StreamUtils.inputStreamToConsole(hcpmeta.getContent(), true);
//									System.out.println();
//								} catch (IOException e) {
//									e.printStackTrace();
//								}
							}
						}

						return null;
					}
				});
				// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*

			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}
		}
	}

}
