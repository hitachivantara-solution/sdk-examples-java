package com.hitachivantara.example.hcp.content;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.core.http.Protocol;
import com.hitachivantara.core.http.client.ClientConfiguration;
import com.hitachivantara.core.http.client.impl.InMemoryDnsResolver;
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
public class RestExample_MemoryDNS {

	public static void main(String[] args) throws IOException, HSCException {
		HCPNamespace hcpClient = null;
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		{
			// Create s3 client
			// 指定需要登录的HCP 租户 及 桶
			String endpoint = "tn1.hcpvm.bjlab.poc";
			String namespace = "song1";
			// 登录需要的用户名
			// The access key encoded by Base64
			String accessKey = Account.accessKey;
			// 登录需要的密码
			// The AWS secret access key encrypted by MD5
			String secretKey = Account.secretKey;

			ClientConfiguration clientConfig = new ClientConfiguration();
			// Using HTTP protocol
			clientConfig.setProtocol(Protocol.HTTP);

			//配置内置dns解析
			InMemoryDnsResolver dnsResolver = new InMemoryDnsResolver();
			//true当出现无法解析的url是异常退出，设置为false时将试图使用系统dns继续解析
			dnsResolver.setUnsolvableException(true);
			//添加解析地址，此处配置的4个ip将被轮询使用
			dnsResolver.add("song1.tn1.hcpvm.bjlab.poc", "10.129.215.61");
			dnsResolver.add("song1.tn1.hcpvm.bjlab.poc", "10.129.215.62");
			dnsResolver.add("song1.tn1.hcpvm.bjlab.poc", "10.129.215.63");
			dnsResolver.add("song1.tn1.hcpvm.bjlab.poc", "10.129.215.64");
			clientConfig.setDnsResolver(dnsResolver);

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
