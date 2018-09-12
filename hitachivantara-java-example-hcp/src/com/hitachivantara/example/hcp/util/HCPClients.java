package com.hitachivantara.example.hcp.util;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class HCPClients {

	private static HCPClients instance = new HCPClients();

	private AmazonS3 hs3Client = null;

	private HCPClients() {
	}

	public static HCPClients getInstance() {
		return instance;
	}

	public AmazonS3 getS3Client() {
		if (hs3Client == null) {
			// Create s3 client
			String endpoint = "tn9.hcp8.hdim.lab";
			// The AWS access key (admin) encoded by Base64
			String accessKey = "YWRtaW4=";
			// The AWS secret access key (P@ssw0rd) encrypted by MD5
			String secretKey = "161ebd7d45089b3446ee4e0d86dbcf92";

			com.amazonaws.ClientConfiguration clientConfig = new com.amazonaws.ClientConfiguration();
			// Using HTTP protocol
			clientConfig.setProtocol(Protocol.HTTP);
			clientConfig.setSignerOverride("S3SignerType");

			hs3Client = AmazonS3ClientBuilder.standard()
					.withClientConfiguration(clientConfig)
					.withEndpointConfiguration(new EndpointConfiguration(endpoint, ""))
					.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
					.build();
		}

		return hs3Client;
	}

}
