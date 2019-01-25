package com.hitachivantara.example.hcp.util;

import com.hitachivantara.common.util.StreamUtils;
import com.hitachivantara.core.http.HttpResponse;
import com.hitachivantara.core.http.client.ClientConfiguration;
import com.hitachivantara.core.http.client.HttpClientBuilder;
import com.hitachivantara.core.http.client.impl.DefaultHttpClientBuilder;
import com.hitachivantara.core.http.content.HttpEntity;
import com.hitachivantara.core.http.content.StringEntity;
import com.hitachivantara.core.http.model.Forms;
import com.hitachivantara.core.http.model.Headers;
import com.hitachivantara.core.http.model.Parameters;
import com.hitachivantara.core.http.util.HttpUtils;
import com.hitachivantara.core.http.util.SimpleHttpClient;

public class HCIAuth {

	public HCIAuth() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ClientConfiguration configuration = new ClientConfiguration();
		HttpClientBuilder builder = new DefaultHttpClientBuilder(configuration);
		SimpleHttpClient client = new SimpleHttpClient(builder);

		Forms form = new Forms();
		Headers header = null;
		Parameters param = null;
		HttpEntity entity = new StringEntity("body");
		
		form.put("grant_type", "password");
		form.put("username", "admin");
		form.put("password", "P@ssw0rd");
		form.put("scope", "*");
		form.put("client_secret", "hci-client");
		form.put("client_id", "hci-client");
		// curl -ik -X POST ST https://10.129.215.95:8000/auth/oauth/ -d/ -d grant_type=password -d username=admin -d password=P@ssw0rd -d scope=*
		// -d client_secret=hci-client -d client_id=hci-client
		HttpResponse response = client.post("https://10.129.215.95:8000/auth/oauth", header, param, form, entity);
		HttpUtils.printHttpResponse(response, false);
//		StreamUtils.inputStreamToConsole(response.getEntity().getContent(), true);
		String token = StreamUtils.inputStreamToString(response.getEntity().getContent(), true);
		System.out.println(token);
	}

}
