package com.hitachivantara.example.hcp.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLUtils {

	/**
	 * 跳过SSL证书验证
	 * </p>
	 * Skip certificate validation for SSL
	 */
	public static final TrustManager[] DUMMY_TRUST_MGR = new TrustManager[] { new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[] {};
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
	} };

	/**
	 * 跳过SSL主机验证
	 */
	public static final HostnameVerifier DUMMY_HOST_NAME_VERIFIER = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	/**
	 * 信任所有
	 * 
	 * @param connection
	 * @return
	 */
	public static SSLSocketFactory trustAll(HttpsURLConnection connection) {
		connection.setHostnameVerifier(DUMMY_HOST_NAME_VERIFIER);

		SSLSocketFactory oldFactory = connection.getSSLSocketFactory();
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, DUMMY_TRUST_MGR, new java.security.SecureRandom());
			SSLSocketFactory newFactory = sc.getSocketFactory();
			connection.setSSLSocketFactory(newFactory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return oldFactory;
	}
}
