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
import java.util.Date;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.util.DatetimeFormat;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.api.event.ListObjectHandler;
import com.hitachivantara.hcp.standard.define.NextAction;
import com.hitachivantara.hcp.standard.define.ObjectType;
import com.hitachivantara.hcp.standard.model.HCPObjectSummary;
import com.hitachivantara.hcp.standard.model.request.impl.ListObjectRequest;

/**
 * 列出指定目录下所有对象的示例，包括子目录
 * </p>
 * List all objects in the specified directory including sub directories
 * 
 * @author sohan
 *
 */
public class RestExample_ListObjects {

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
				// .withObjectFilter(new ObjectFilter() {
				//
				// @Override
				// public boolean accept(HCPObjectEntry arg0) {
				// //只有对象名称包含字母X的才被foundObject
				// return arg0.getName().contains("X");
				// }
				// })
				;

				hcpClient.listObjects(request, new ListObjectHandler() {
					int i = 0;

					// 发现的对象信息
					@Override
					public NextAction foundObject(HCPObjectSummary obj) throws HSCException {
						if (obj.isDirectory()) {
							System.out.println(
									++i + "\t \t" + obj.getType() + "\t" + DatetimeFormat.ISO8601_DATE_FORMAT.format(new Date(obj.getChangeTime())) + "\t" + obj.getKey());
						} else {
							System.out.println(++i
									+ "\t"
									+ obj.getSize()
									+ "\t"
									+ obj.getType()
									+ "\t\t"
									+ DatetimeFormat.ISO8601_DATE_FORMAT.format(new Date(obj.getIngestTime()))
									+ "\t"
									+ obj.getKey()
									+ "\t"
									+ obj.getContentHash());
						}

						// 做一些事情，例如打印文件内容
						// You can do something more...
						// try {
						// InputStream content = hcpClient.getObject(obj.getKey()).getContent();
						// System.out.print("Content:");
						// StreamUtils.inputStreamToConsole(content, true);
						// System.out.println();
						// } catch (IOException e) {
						// }

						// 如需要可以停止列出目录
						// You can add specific conditions to stop the listing action
						// if (i == 88) {
						// return NextAction.stop;
						// }

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
