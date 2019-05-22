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
import java.util.List;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.util.DateUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.api.ObjectEntryIterator;
import com.hitachivantara.hcp.standard.model.HCPObjectEntry;
import com.hitachivantara.hcp.standard.model.HCPObjectEntrys;
import com.hitachivantara.hcp.standard.model.request.impl.ListDirectoryRequest;

/**
 * 列出当前目录下所有对象的示例
 * </p>
 * List all the objects in the current directory
 * 
 * @author sohan
 *
 */
public class RestExample_ListDirectory {

	public static void main(String[] args) throws IOException {
		{
			long i = 0;
			ObjectEntryIterator it = null;
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				// Here is the folder path you want to list.
				// 需要列出的目录名
				String directoryKey = "example-hcp/moreThan100objs/";

				// 罗列指定目录中的所有对象
				// Request HCP to list all the objects in this folder.
				// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				HCPObjectEntrys entrys = hcpClient.listDirectory(directoryKey);
				// 列出目录中的对象包括被删除的对象（需要HCP开启版本功能）
				// HCPObjectEntrys entrys = hcpClient.listDirectory(new ListDirectoryRequest(directoryKey).withDeletedObject(true));

				// Printout objects
				it = entrys.iterator();
				List<HCPObjectEntry> objs;
				while ((objs = it.next(100)) != null) {
					for (HCPObjectEntry obj : objs) {
						if (obj.isDirectory())  {
						System.out.println(++i
								+ "\t"
								+ " "
								+ "\t"
								+ obj.getKey()
								+ "\t"
								+ obj.getType());
						} else {
						System.out.println(++i
								+ "\t"
								+ obj.getSize()
								+ "\t"
								+ obj.getKey()
								+ "\t"
								+ obj.getType()
								+ "\t"
								+ DateUtils.ISO8601_DATE_FORMAT.format(new Date(obj.getIngestTime()))
								+ "\t"
								+ obj.getContentHash());
						}
					}
					
//					it.abort();
//					break;
//					it.close();
				}

				// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*

			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			} finally {
				// 使用完毕一定要关闭
				if (it != null) {
					it.close();
					it = null;
				}
			}
		}
	}

}
