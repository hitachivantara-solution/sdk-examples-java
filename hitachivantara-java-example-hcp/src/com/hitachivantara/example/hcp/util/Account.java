package com.hitachivantara.example.hcp.util;

import java.io.File;

public class Account {
	 public final static String endpoint = "tenant1.hcp-demo.hcpdemo.com";
	 public final static String namespace = "test1";
	 // The access key (user1) encoded by Base64
	 public final static String accessKey = "YWRtaW4=";
	 // The secret access key (hcp1234567) encrypted by MD5
	 public final static String secretKey = "161ebd7d45089b3446ee4e0d86dbcf92";
	
//	 public final static String endpoint = "tn9.hcp8.hdim.lab"; // "tenant1.hcp-demo.hcpdemo.com";// "tn9.hcp8.hdim.lab"; //
//	 public final static String namespace = "cloud";
//	 // The access key (user1) encoded by Base64
//	 public final static String accessKey = "YWRtaW4=";
//	 // The secret access key (hcp1234567) encrypted by MD5
//	 public final static String secretKey = "161ebd7d45089b3446ee4e0d86dbcf92";

	public final static String HCP_AUTHORIZATION = "HCP " + accessKey + ":" + secretKey;
	
	public static final File localFile1 = new File("C:\\VDisk\\DriverD\\Downloads\\Temp\\WeChat Image_20180716111626.doc");
}
