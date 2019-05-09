package com.hitachivantara.example.hcp.content;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.util.StreamUtils;
import com.hitachivantara.core.http.Protocol;
import com.hitachivantara.core.http.client.ClientConfiguration;
import com.hitachivantara.core.http.client.impl.InMemoryDnsResolver;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.RandomInputStream;
import com.hitachivantara.hcp.build.HCPClientBuilder;
import com.hitachivantara.hcp.build.HCPNamespaceClientBuilder;
import com.hitachivantara.hcp.common.auth.LocalCredentials;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.api.KeyAlgorithm;
import com.hitachivantara.hcp.standard.model.HCPObject;
import com.hitachivantara.hcp.standard.model.PutObjectResult;
import com.hitachivantara.hcp.standard.model.request.impl.PutObjectRequest;

/**
 * HCP存储路径最佳实践示例
 * </p>
 * Example of key path best practices for HCP storage
 * 
 * @author sohan
 *
 */
public class RestExample_MemoryDNS {

	public static void main(String[] args) throws IOException, HSCException, InterruptedException {
		final HCPNamespace hcpClient;
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

			// 配置内置dns解析
			InMemoryDnsResolver dnsResolver = new InMemoryDnsResolver();
			// true当出现无法解析的url是异常退出，设置为false时，优先使用memorydns，无法解析是将试图使用系统dns继续解析
			dnsResolver.setUnsolvableException(true);
			// 添加解析地址，此处配置的4个ip将被轮询使用
			dnsResolver.add("song1.tn1.hcpvm.bjlab.poc", "10.129.215.61");
			dnsResolver.add("song1.tn1.hcpvm.bjlab.poc", "10.129.215.62");
			dnsResolver.add("song1.tn1.hcpvm.bjlab.poc", "10.129.215.63");
			dnsResolver.add("song1.tn1.hcpvm.bjlab.poc", "10.129.215.64");
			clientConfig.setDnsResolver(dnsResolver);

			HCPNamespaceClientBuilder builder = HCPClientBuilder.defaultHCPClient();
			hcpClient = builder
					.withClientConfiguration(clientConfig)
					.withCredentials(new LocalCredentials(accessKey, secretKey))
					.withEndpoint(endpoint)
					.withNamespace(namespace)
					.bulid();
		}

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		{
			// Here is the folder path you want to store files.
			final String directoryKey = "example-hcp/moreThan100objs/";

			// 使用多线程（10个线程每个线程创建200个）创建100随机内容个文件
			// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
			for (int i = 0; i < 1; i++) {
				final int id = i;

				new Thread(new Runnable() {
					@Override
					public void run() {
						PutObjectResult result = null;
						for (int j = 0; j < 2000; j++) {
							String key = directoryKey + "file-" + id + "-" + j + ".txt";
							try {
								String content = new Date().toString() + " " + RandomInputStream.randomInt(10000, 99999);

								PutObjectRequest req = new PutObjectRequest(key).withContent(content);
//								req.customHeader().put("Connection", "Keep-alive");
//								req.customHeader().put("Connection", "close");
								result = hcpClient.putObject(req);
								
//								InputStream in = hcpClient.getObject(key).getContent();
//								try {
//									StreamUtils.inputStream2None(in, true);
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}

								System.out.println(key);
							} catch (InvalidResponseException e) {
								System.out.println("Create file: " + key + " " + result.getETag());
								e.printStackTrace();
							} catch (HSCException e) {
								System.out.println("Create file: " + key + " " + result.getETag());
								e.printStackTrace();
//							} catch (IOException e) {
//								System.out.println("Create file: " + key + " " + result.getETag());
//								e.printStackTrace();
							}
						}
						System.out.println("Finished 2000 " + this);
					}
				}).start();
			}
		}
		
		Thread.sleep(Integer.MAX_VALUE);

		System.out.println("Well done!");
	}

}
