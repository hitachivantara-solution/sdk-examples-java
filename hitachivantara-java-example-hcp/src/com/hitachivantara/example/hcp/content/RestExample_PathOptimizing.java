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
import com.hitachivantara.hcp.standard.api.KeyAlgorithm;
import com.hitachivantara.hcp.standard.model.HCPObject;

/**
 * HCP存储路径最佳实践示例
 * </p>
 * Example of key path best practices for HCP storage
 * 
 * @author sohan
 *
 */
public class RestExample_PathOptimizing {
	public static void main(String[] args) throws IOException, HSCException {
		HCPNamespace hcpClient = null;
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		{
			// Create s3 client
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

			HCPNamespaceClientBuilder builder = HCPClientBuilder.defaultHCPClient();
			hcpClient = builder.withClientConfiguration(clientConfig)
					.withCredentials(new LocalCredentials(accessKey, secretKey))
					.withEndpoint(endpoint).withNamespace(namespace)
					.bulid();
		}

		{
			// ★★★路径优化设置★★★
			// 指定Key算法后SDK将自动修正路径为优化路径，此方式适合不关心存储路径的需求，可以以Key Value方式存取数据。
			// The actual path stored in HCP will be different with the key you specified. 
			hcpClient.setKeyAlgorithm(KeyAlgorithm.CONSERVATIVE_KEY_HASH_D32);
		}
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		HCPObject hcpObject = null;
		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String key = file.getName();

		{
			// Check whether object exist.
			boolean exist = hcpClient.doesObjectExist(key);
			if (exist) {
				// Delete object in HCP.
				boolean deleted = hcpClient.deleteObject(key);
				System.out.println("Orginal object was deleted! " + deleted);
			}
		}

		{
			// Put这个文件至HCP
			// Inject file into HCP system.
			try {
				hcpClient.putObject(key, file);

				// Check whether object exist.
				boolean exist = hcpClient.doesObjectExist(key);
				assertTrue(exist == true);

				// Get the object from HCP
				hcpObject = hcpClient.getObject(key);
			} catch (InvalidResponseException e) {
				e.printStackTrace();
			} catch (HSCException e) {
				e.printStackTrace();
			}
		}

		// Verify result:
//		InputStream in = hcpObject.getContent();
//		byte[] orginalFileMd5 = DigestUtils.calcMD5(file);
//		byte[] objectFromHCPMd5 = DigestUtils.calcMD5(in);
//		in.close();
//
//		boolean equals = Arrays.equals(orginalFileMd5, objectFromHCPMd5);
//		assertTrue(equals == true);

		System.out.println("Well done!");
	}

}
