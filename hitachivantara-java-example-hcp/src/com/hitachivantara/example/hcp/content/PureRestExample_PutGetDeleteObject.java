package com.hitachivantara.example.hcp.content;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.digest.DigestUtils;

import com.hitachivantara.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.Account;

/**
 * 未使用sdk操作对象存储取得删除示例 
 * 
 * @author sohan
 *
 */
public class PureRestExample_PutGetDeleteObject {
	/**
	 * * 跳过证书验证
	 */
	private static final TrustManager[] DUMMY_TRUST_MGR = new TrustManager[] { new X509TrustManager() {
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
	 * 跳过主机验证
	 */
	private static final HostnameVerifier DUMMY_HOST_NAME_VERIFIER = new HostnameVerifier() {
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
	private static SSLSocketFactory trustAll(HttpsURLConnection connection) {
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

	public static void main(String[] args) throws MalformedURLException {
		final String protocol = "https";// "http"
		String endpoint = Account.endpoint;
		String namespace = Account.namespace;
		final String rest = protocol + "://" + namespace + "." + endpoint + "/rest/"; // 要提交的目标地址
		String key = "hcp-test1/Test1-1.log";
		final URL url = new URL(rest + key);
		HttpURLConnection connection = null;

		// ------------------------------------------------------------------------------------------------------------------------
		// 通过REST上传一个文件至HCP
		// Using a Namespace > HTTP > Working with objects and versions > Request contents
		// ------------------------------------------------------------------------------------------------------------------------
		try {
			connection = (HttpURLConnection) url.openConnection(); // 创建一个HTTP连接

			if (url.getProtocol().equalsIgnoreCase("https")) {
				trustAll((HttpsURLConnection) connection);
			}

			connection.setRequestMethod("PUT"); // 指定使用PUT请求方式
			connection.setDoInput(true); // 向连接中写入数据
			connection.setDoOutput(true); // 从连接中读取数据
			connection.setUseCaches(false); // 禁止缓存
			connection.setInstanceFollowRedirects(true); // 自动执行HTTP重定向
			connection.setRequestProperty("Authorization", Account.HCP_AUTHORIZATION); // 设置认证信息
			OutputStream out = connection.getOutputStream(); // 获取输出流
			byte[] data = ("TimeMillis=" + System.currentTimeMillis() + " Time=" + new Date().toString()).getBytes("utf-8");
			out.write(data);// 将要传递的数据写入数据输出流
			out.flush(); // 输出缓存
			out.close(); // 关闭数据输出流

			// 更多返回代码参见帮助
			// Using a Namespace > HTTP reference > HTTP return codes
			if (connection.getResponseCode() == 201) { // 判断是否创建成功
				{
					// 通过数据签名验证验证本地数据与上传成功的数据一致
					String etag = connection.getHeaderField("ETag");
					String localEtag = "\"" + DigestUtils.md5Hex(data) + "\""; // 此处为MD5计算，无需第三方lib
					if (etag.equals(localEtag)) {
						System.out.println("Verify contents=" + (etag.equals(localEtag)));
					} else {
						// 验证失败处理.....
					}
				}

				System.out.println("Object created!");
			} else {
				System.out.println("Failed to create object! " + connection.getResponseCode() + " " + connection.getResponseMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect(); // 断开连接
				connection = null;
			}
		}

		// ------------------------------------------------------------------------------------------------------------------------
		// 通过REST从HCP下载一个文件
		// Using a Namespace > HTTP > Working with objects and versions > Request contents
		// ------------------------------------------------------------------------------------------------------------------------
		try {
			connection = (HttpURLConnection) url.openConnection(); // 创建一个HTTP连接

			if (url.getProtocol().equalsIgnoreCase("https")) {
				trustAll((HttpsURLConnection) connection);
			}

			connection.setRequestMethod("GET"); // 指定使用GET请求方式
			connection.setDoInput(true); // 向连接中写入数据
			connection.setDoOutput(true); // 从连接中读取数据
			connection.setUseCaches(false); // 禁止缓存
			connection.setInstanceFollowRedirects(true); // 自动执行HTTP重定向
			connection.setRequestProperty("Authorization", Account.HCP_AUTHORIZATION); // 设置认证信息

			// 更多返回代码参见帮助
			// Using a Namespace > HTTP reference > HTTP return codes
			if (connection.getResponseCode() == 200) { // 判断是否响应成功
				InputStream in = connection.getInputStream();
				// 打印取得的数据并关闭in
				System.out.println("------------------------------------------------------------------");
				StreamUtils.inputStreamToConsole(in, true);
				System.out.println("\n------------------------------------------------------------------");
				// in.close();
				System.out.println("Object retrieved!");
			} else {
				System.out.println("Failed to retrieve object! " + connection.getResponseCode() + " " + connection.getResponseMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect(); // 断开连接
				connection = null;
			}
		}

		// ------------------------------------------------------------------------------------------------------------------------
		// 通过REST删除一个文件
		// Using a Namespace > HTTP > Working with objects and versions > Request contents
		// ------------------------------------------------------------------------------------------------------------------------
		try {
			connection = (HttpURLConnection) url.openConnection(); // 创建一个HTTP连接

			if (url.getProtocol().equalsIgnoreCase("https")) {
				trustAll((HttpsURLConnection) connection);
			}

			connection.setRequestMethod("DELETE"); // 指定使用GET请求方式
			connection.setDoInput(true); // 向连接中写入数据
			connection.setDoOutput(true); // 从连接中读取数据
			connection.setUseCaches(false); // 禁止缓存
			connection.setInstanceFollowRedirects(true); // 自动执行HTTP重定向
			connection.setRequestProperty("Authorization", Account.HCP_AUTHORIZATION); // 设置认证信息

			// 更多返回代码参见帮助
			// Using a Namespace > HTTP reference > HTTP return codes
			if (connection.getResponseCode() == 200) { // 判断是否响应成功
				System.out.println("Object deleted!");
			} else {
				System.out.println("Failed to deleted object! " + connection.getResponseCode() + " " + connection.getResponseMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect(); // 断开连接
				connection = null;
			}
		}

		System.out.println("Well done!");
	}

}
