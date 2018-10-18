package com.hitachivantara.example.hcp.content;

import java.io.File;
import java.io.IOException;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.body.HCPStandardClient;
import com.hitachivantara.hcp.standard.model.metadata.HCPSystemMetadata;

/**
 * 设置系统元数据示例
 * 
 * @author sohan
 *
 */
public class RestExample_SetSystemMetadata {

	public static void main(String[] args) throws IOException {
		HCPStandardClient hcpClient;

		// Here is the file will be uploaded into HCP
		File file = new File("C:\\VDisk\\DriverD\\Downloads\\Temp\\WeChat Image_20180716111626.doc");
		// The location in HCP where this file will be stored.
		String key = "folder/subfolder/" + file.getName();

		// 创建测试用对象文件
		{
			try {
				hcpClient = HCPClients.getInstance().getHCPClient();

				//如果对象在retention模式下或者hold为true，对象无法被覆盖
				 hcpClient.putObject(key, file);
			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}
		}

		// 修改系统metadata
		{
			try {

				HCPSystemMetadata metadata = new HCPSystemMetadata();
				metadata.setShred(true);
				metadata.setHold(true);
				//设置保留期限 
//				 metadata.setRetention(new Retention("A+1000d+20m"));

				hcpClient.setSystemMetadata(key, metadata);
			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}
		}

		// 试图删除HOLD属性对象时，HCP将拒绝此请求
		{
			try {
				hcpClient.deleteObject(key);
			} catch (Exception e) {
				System.out.println("Yon can not delete this object, Because it's in HOLD");
			}

			try {
				hcpClient.putObject(key, file);
			} catch (Exception e) {
				System.out.println("Yon can not put a new object to this key, Because it's in HOLD");
			}
		}

		// 删除Hold对象
		{
			try {

				HCPSystemMetadata metadata = new HCPSystemMetadata();
				metadata.setHold(false);

				hcpClient.setSystemMetadata(key, metadata);

				hcpClient.deleteObject(key);
				//删除在retention下的对象
//				hcpClient.deleteObject(new DeleteObjectRequest(key).withPrivilegedDelete(true, "I said"));
			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}
		}

		System.out.println("Well done!");
	}

}
