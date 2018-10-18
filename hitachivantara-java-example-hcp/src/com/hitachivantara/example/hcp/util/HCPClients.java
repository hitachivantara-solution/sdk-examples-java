package com.hitachivantara.example.hcp.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.core.http.ClientConfiguration;
import com.hitachivantara.hcp.build.HCPClientBuilder;
import com.hitachivantara.hcp.build.HCPStandardClientBuilder;
import com.hitachivantara.hcp.common.auth.BasicCredentials;
import com.hitachivantara.hcp.standard.body.HCPStandardClient;

public class HCPClients {

	private static HCPClients instance = new HCPClients();

	private AmazonS3 hs3Client = null;
	private HCPStandardClient hcpClient = null;

	private HCPClients() {
	}

	public static HCPClients getInstance() {
		return instance;
	}

	public AmazonS3 getS3Client() {
		if (hs3Client == null) {
			// Create s3 client
			String endpoint = "tn9.hcp8.hdim.lab";
			// The AWS access key (user1) encoded by Base64
			String accessKey = "dXNlcjE="; 
			// The AWS secret access key (hcp1234567) encrypted by MD5
			String secretKey = "c0658942779dfbd4b4d6e59735b0c846";

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
			// Create s3 client
			String endpoint = "tn9.hcp8.hdim.lab"; // "tenant1.hcp-demo.hcpdemo.com";// "tn9.hcp8.hdim.lab"; //
			String namespace = "anywhere";
			// The access key (user1) encoded by Base64
			String accessKey = "dXNlcjE="; 
			// The AWS secret access key (hcp1234567) encrypted by MD5
			String secretKey = "c0658942779dfbd4b4d6e59735b0c846";

			ClientConfiguration clientConfig = new ClientConfiguration();
			// Using HTTP protocol
			clientConfig.setProtocol(com.hitachivantara.core.http.Protocol.HTTPS);

			HCPStandardClientBuilder builder = HCPClientBuilder.defaultHCPClient();
			hcpClient = builder.withClientConfiguration(clientConfig)
					.withCredentials(new BasicCredentials(accessKey, secretKey))
					.withEndpoint(endpoint)
					.withNamespace(namespace)
					.bulid();
		}

		return hcpClient;
	}

}
