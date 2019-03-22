package com.hitachivantara.example.hcp.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.core.http.client.ClientConfiguration;
import com.hitachivantara.hcp.build.HCPClientBuilder;
import com.hitachivantara.hcp.build.HCPNamespaceClientBuilder;
import com.hitachivantara.hcp.build.HCPQueryClientBuilder;
import com.hitachivantara.hcp.common.auth.LocalCredentials;
import com.hitachivantara.hcp.management.api.HCPSystemManagement;
import com.hitachivantara.hcp.management.api.HCPTenantManagement;
import com.hitachivantara.hcp.query.api.HCPQuery;
import com.hitachivantara.hcp.standard.api.HCPNamespace;

public class HCPClients {

	private static HCPClients instance = new HCPClients();

	private AmazonS3 hs3Client = null;
	private HCPNamespace hcpClient = null;
	private HCPQuery hcpQueryClient = null;

	private HCPTenantManagement tenantMgrClient;

	private HCPSystemManagement systemMgrClient;

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

	public HCPNamespace getHCPClient() throws HSCException {
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
			clientConfig.setConnectTimeout(2000);
			// Using HTTP protocol
			clientConfig.setProtocol(com.hitachivantara.core.http.Protocol.HTTPS);

			HCPNamespaceClientBuilder builder = HCPClientBuilder.defaultHCPClient();
			hcpClient = builder.withClientConfiguration(clientConfig)
					.withCredentials(new LocalCredentials(accessKey, secretKey))
					.withEndpoint(endpoint)
					.withNamespace(namespace)
					.bulid();
		}

		return hcpClient;
	}
	
	public HCPTenantManagement getHCPTenantManagementClient() throws HSCException {
		if (tenantMgrClient == null) {
			// 指定需要登录的HCP 租户 及 桶
			String hcpdomain = Account.hcpdomain;
			String tenant = Account.tenant;
			// 登录需要的用户名
			// The access key encoded by Base64
			String accessKey = Account.accessKey;
			// 登录需要的密码
			// The AWS secret access key encrypted by MD5
			String secretKey = Account.secretKey;

			ClientConfiguration myClientConfig1 = new ClientConfiguration();
			tenantMgrClient = HCPClientBuilder.tenantManagementClient()
					.withEndpoint(hcpdomain)
					.withTenant(tenant)
					.withCredentials(new LocalCredentials(accessKey, secretKey))
					.withClientConfiguration(myClientConfig1)
					.bulid();

		}

		return tenantMgrClient;
	}
	
	public HCPSystemManagement getHCPSystemManagementClient() throws HSCException {
		if (systemMgrClient == null) {
			// 指定需要登录的HCP 租户 及 桶
			String hcpdomain = Account.hcpdomain;
			// 登录需要的用户名
			// The access key encoded by Base64
			String accessKey = Account.system_accessKey;
			// 登录需要的密码
			// The AWS secret access key encrypted by MD5
			String secretKey = Account.system_secretKey;

			ClientConfiguration myClientConfig1 = new ClientConfiguration();
			systemMgrClient = HCPClientBuilder.systemManagementClient()
					.withEndpoint(hcpdomain)
					.withCredentials(new LocalCredentials(accessKey, secretKey))
					.withClientConfiguration(myClientConfig1)
					.bulid();

		}

		return systemMgrClient;
	}
	
	public HCPQuery getHCPQueryClient() throws HSCException {
		if (hcpQueryClient == null) {
			// 指定需要登录的HCP 租户 及 桶
			String endpoint = Account.endpoint;
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
