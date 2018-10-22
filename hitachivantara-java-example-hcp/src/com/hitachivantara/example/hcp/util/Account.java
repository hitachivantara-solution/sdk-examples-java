package com.hitachivantara.example.hcp.util;

public class Account {
//	public final static String endpoint = "tn9.hcp8.hdim.lab"; // "tenant1.hcp-demo.hcpdemo.com";// "tn9.hcp8.hdim.lab"; //
//	public final static String namespace = "cloud";
//	// The access key (user1) encoded by Base64
//	public final static String accessKey = "dXNlcjE=";
//	// The secret access key (hcp1234567) encrypted by MD5
//	public final static String secretKey = "c0658942779dfbd4b4d6e59735b0c846";
	
	//中意人寿专用HCP测试账户有效期截至至2019-2月
	//139.159.3.234		generalichina.northcommmercial.hcp1.hdslab.net

	public final static String endpoint = "northcommmercial.hcp1.hdslab.net";
	public final static String namespace = "generalichina";
	// The access key (generalichina) encoded by Base64
	public final static String accessKey = "Z2VuZXJhbGljaGluYQ=="; 
	// The AWS secret access key (Welcome2HCP) encrypted by MD5
	public final static String secretKey = "343a0162f85e7dea812357c4b298d5f2";

	public final static String HCP_AUTHORIZATION = "HCP Z2VuZXJhbGljaGluYQ==:343a0162f85e7dea812357c4b298d5f2";
}
