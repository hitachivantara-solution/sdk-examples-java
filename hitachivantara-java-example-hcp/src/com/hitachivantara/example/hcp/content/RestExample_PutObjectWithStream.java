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

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.util.DigestUtils;
import com.hitachivantara.common.util.StreamUtils;
import com.hitachivantara.core.http.Protocol;
import com.hitachivantara.core.http.client.ClientConfiguration;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.build.HCPClientBuilder;
import com.hitachivantara.hcp.build.HCPNamespaceClientBuilder;
import com.hitachivantara.hcp.common.auth.LocalCredentials;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.HCPObject;
import com.hitachivantara.hcp.standard.model.request.impl.DeleteObjectRequest;
import com.hitachivantara.hcp.standard.model.request.impl.PutObjectRequest;

/**
 * 通过数据流Put对象
 * </p>
 * Examples of how to upload object by stream
 * 
 * @author sohan
 *
 */
public class RestExample_PutObjectWithStream {

	public static void main(String[] args) throws IOException, HSCException {
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// 创建HCP访问客户端，客户端仅需要创建一次
		// Create an HCP access client. The client needs to be created only once
		HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		// The location in HCP where this file will be stored.
		String key = "example-hcp/subfolder1/file_upload_by_stream.txt";

		{
			// Inject file into HCP system.
			InputStream in = null;
			try {
				in = getInputStream();
			
				hcpClient.putObject(key, in);
			} catch (InvalidResponseException e) {
//				e.getReason()
//				e.getStatusCode()
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				//关闭原始数据流
				StreamUtils.close(in);
			}
		}

		System.out.println("Well done!");
	}
	
	private static InputStream getInputStream() throws IOException {
		byte[] bytes = null;
		bytes = "这段文本将作为一个对象文件保存至HCP".getBytes();
		return StreamUtils.bytesToInputStream(bytes);
	}

}
