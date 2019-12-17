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
import com.hitachivantara.core.http.Protocol;
import com.hitachivantara.core.http.client.ClientConfiguration;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.hcp.build.HCPClientBuilder;
import com.hitachivantara.hcp.build.HCPNamespaceClientBuilder;
import com.hitachivantara.hcp.common.auth.LocalCredentials;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.HCPObject;
import com.hitachivantara.hcp.standard.model.request.impl.DeleteObjectRequest;
import com.hitachivantara.hcp.standard.model.request.impl.PutObjectRequest;

/**
 * 对象存储取得删除示例 包括创建client端
 * </p>
 * Examples of how to upload, retrieve, delete object include creating a client side
 * 
 * @author sohan
 *
 */
public class RestExample_PutGetDeleteObject {

	public static void main(String[] args) throws IOException, HSCException {
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// 创建HCP访问客户端，客户端仅需要创建一次
		// Create an HCP access client. The client needs to be created only once
		HCPNamespace hcpClient = null;
		{
			// 指定需要登录的HCP 租户 及 桶
			String endpoint = Account.endpoint;
			String namespace = Account.namespace;
			// 登录需要的用户名
			// The access key encoded by Base64
			String accessKey = Account.accessKey;
			// 登录需要的密码
			// The AWS secret access key encrypted by MD5
			String secretKey = Account.secretKey;

			ClientConfiguration clientConfig = new ClientConfiguration();
			// Using HTTP protocol
			clientConfig.setProtocol(Protocol.HTTP);
//			clientConfig.setProxy("localhost", 8080);

			HCPNamespaceClientBuilder builder = HCPClientBuilder.defaultHCPClient();
			hcpClient = builder.withClientConfiguration(clientConfig)
					.withCredentials(new LocalCredentials(accessKey, secretKey))
					.withEndpoint(endpoint)
					.withNamespace(namespace)
					.bulid();
		}

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		HCPObject hcpObject = null;
		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String key = "example-hcp/subfolder1/" + file.getName();

		{
			// 上传文件至HCP
			// Inject file into HCP system.
			try {
				// 上传前无需刻意创建目录，只需指定存储路径,如需创建目录也可使用createDirectory方法
				// hcpClient.createDirectory("folder/subfolder/123");

				hcpClient.putObject(key, file);

				// Check whether object exist.
//				boolean exist = hcpClient.doesObjectExist(key);
//				assertTrue(exist == true);

				// Get the object from HCP
				hcpObject = hcpClient.getObject(key);
			} catch (InvalidResponseException e) {
//				e.getReason()
//				e.getStatusCode()
				e.printStackTrace();
			} catch (HSCException e) {
				e.printStackTrace();
			}
		}
		
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// 获得数据流
		// Get the stream
		InputStream in = hcpObject.getContent();
		// 可以将文件保存至本地目录
		// StreamUtils.inputStreamToFile(in, "C:\\myfile.doc", true)

		// 以下为验证上传数据与本地数据一致性测示例，SDK已集成此功能，实际开发时不需要以下代码！
		{
			byte[] orginalFileMd5 = DigestUtils.calcMD5(file);
			byte[] objectFromHCPMd5 = DigestUtils.calcMD5(in);
			in.close();

			boolean equals = Arrays.equals(orginalFileMd5, objectFromHCPMd5);
			assertTrue(equals == true);
		}
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		{
			// 通过delete方法可以删除对象
			hcpClient.deleteObject(key);
			// or
			// 可以使用Purge删除对象
//			 hcpClient.deleteObject(new DeleteObjectRequest(key).withPurge(true));
			//
			// Check whether object exist.
//			boolean exist = hcpClient.doesObjectExist(key);
//			assertTrue(exist == false);
		}
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		System.out.println("Well done!");
	}

}
