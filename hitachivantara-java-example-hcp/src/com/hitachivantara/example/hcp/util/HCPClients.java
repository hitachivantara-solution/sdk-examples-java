package com.hitachivantara.example.hcp.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.core.http.client.ClientConfiguration;
import com.hitachivantara.hcp.build.HCPClientBuilder;
import com.hitachivantara.hcp.build.HCPQueryClientBuilder;
import com.hitachivantara.hcp.build.HCPStandardClientBuilder;
import com.hitachivantara.hcp.common.auth.LocalCredentials;
import com.hitachivantara.hcp.query.body.HCPQueryClient;
import com.hitachivantara.hcp.standard.body.HCPStandardClient;

public class HCPClients {

	private static HCPClients instance = new HCPClients();

	private AmazonS3 hs3Client = null;
	private HCPStandardClient hcpClient = null;
	private HCPQueryClient hcpQueryClient = null;

	private HCPClients() {
	}

	public static HCPClients getInstance() {
		return instance;
	}

	public AmazonS3 getS3Client() {
		if (hs3Client == null) {
			// Create s3 client
			// 指定需要登录的HCP 租户 及 桶
			String endpoint = Account.endpoint;
			// 登录需要的用户名
			// The access key encoded by Base64
			String accessKey = Account.accessKey;
			// 登录需要的密码
			// The AWS secret access key encrypted by MD5
			String secretKey = Account.secretKey;

			com.amazonaws.ClientConfiguration clientConfig = new com.amazonaws.ClientConfiguration();
			// Using HTTP protocol
			clientConfig.setProtocol(com.amazonaws.Protocol.HTTP);
			clientConfig.setSignerOverride("S3SignerType");

			hs3Client = AmazonS3ClientBuilder.standard()
					.withClientConfiguration(clientConfig)
					.withEndpointConfiguration(new EndpointConfiguration(endpoint, ""))
					.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
					.build();
		}

		return hs3Client;
	}

	public HCPStandardClient getHCPClient() throws HSCException {
		if (hcpClient == null) {
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
			clientConfig.setProtocol(com.hitachivantara.core.http.Protocol.HTTPS);

			HCPStandardClientBuilder builder = HCPClientBuilder.defaultHCPClient();
			hcpClient = builder.withClientConfiguration(clientConfig)
					.withCredentials(new LocalCredentials(accessKey, secretKey))
					.withEndpoint(endpoint)
					.withNamespace(namespace)
					.bulid();
		}

		return hcpClient;
	}
	
	public HCPQueryClient getHCPQueryClient() throws HSCException {
		if (hcpQueryClient == null) {
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
			clientConfig.setProtocol(com.hitachivantara.core.http.Protocol.HTTP);

			 HCPQueryClientBuilder builder = HCPClientBuilder.queryClient();
			hcpQueryClient = builder.withClientConfiguration(clientConfig)
					.withCredentials(new LocalCredentials(accessKey, secretKey))
					.withEndpoint(endpoint)
					.bulid();
		}

		return hcpQueryClient;
	}

}
