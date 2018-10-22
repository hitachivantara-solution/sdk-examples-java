package com.hitachivantara.example.hcp.content;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.util.DigestUtils;
import com.hitachivantara.core.http.Protocol;
import com.hitachivantara.core.http.client.ClientConfiguration;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.hcp.build.HCPClientBuilder;
import com.hitachivantara.hcp.build.HCPStandardClientBuilder;
import com.hitachivantara.hcp.common.auth.LocalCredentials;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.body.HCPStandardClient;
import com.hitachivantara.hcp.standard.io.HCPInputStream;
import com.hitachivantara.hcp.standard.model.HCPObject;

/**
 * 对象存储取得删除示例
 * 包括创建client端
 * @author sohan
 *
 */
public class RestExample_PutGetDeleteObject {

	public static void main(String[] args) throws IOException, HSCException {
		HCPStandardClient hcpClient = null;
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		{
			//创建HCP访问客户端，客户端仅需要创建一次
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

			HCPStandardClientBuilder builder = HCPClientBuilder.defaultHCPClient();
			hcpClient = builder
					.withClientConfiguration(clientConfig)
					.withCredentials(new LocalCredentials(accessKey, secretKey))
					.withEndpoint(endpoint)
					.withNamespace(namespace)
					.bulid();
		}

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		HCPObject hcpObject = null;
		// Here is the file will be uploaded into HCP
		File file = new File("C:\\VDisk\\DriverD\\Downloads\\Temp\\WeChat Image_20180716111626.doc");
		// The location in HCP where this file will be stored.
		String key = "folder/subfolder/" + file.getName();

		{
			// 上传文件至HCP
			// Inject file into HCP system.
			try {
				// 上传前无需刻意创建目录，只需指定存储路径,如需创建目录也可使用createDirectory方法
				// hcpClient.createDirectory("folder/subfolder/123");

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
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		//以下为验证上传数据与本地数据一致性测示例，SDK已集成此功能，实际开发时不需要以下代码！
		// Verify result:
		HCPInputStream in = hcpObject.getContent();
		byte[] orginalFileMd5 = DigestUtils.calcMD5(file);
		byte[] objectFromHCPMd5 = DigestUtils.calcMD5(in);
		in.close();

		boolean equals = Arrays.equals(orginalFileMd5, objectFromHCPMd5);
		assertTrue(equals == true);

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

//		{
//			// Delete object in HCP.
//			hcpClient.deleteObject(key);
//
//			// Check whether object exist.
//			boolean exist = hcpClient.doesObjectExist(key);
//			assertTrue(exist == false);
//		}

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		System.out.println("Well done!");
	}

}
