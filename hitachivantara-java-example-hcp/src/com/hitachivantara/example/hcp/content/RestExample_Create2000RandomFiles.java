package com.hitachivantara.example.hcp.content;

import java.io.IOException;
import java.util.Date;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.example.hcp.util.RandomInputStream;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.PutObjectResult;
import com.hitachivantara.hcp.standard.model.request.impl.PutObjectRequest;

/**
 * 使用多线程创建2000随机内容个文件
 * </p>
 * Create 2000 random content files using multiple threads
 * 
 * @author sohan
 *
 */
public class RestExample_Create2000RandomFiles {

	public static void main(String[] args) throws IOException {
		{
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				// Here is the folder path you want to store files.
				final String directoryKey = "example-hcp/moreThan100objs/";

				// 使用多线程（10个线程每个线程创建200个）创建100随机内容个文件
				// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				for (int i = 0; i < 100; i++) {
					final int id = i;

					new Thread(new Runnable() {
						@Override
						public void run() {

							for (int j = 0; j < 2000; j++) {
								String key = directoryKey + "file-" + id + "-" + j + ".txt";
								try {
									String content = new Date().toString() + " " + RandomInputStream.randomInt(10000, 99999);

									PutObjectResult result = hcpClient.putObject(new PutObjectRequest(key).withContent(content));

									System.out.println("Create file: " + key + " " + result.getETag());
								} catch (InvalidResponseException e) {
									e.printStackTrace();
								} catch (HSCException e) {
									e.printStackTrace();
								}
							}
						}
					}).start();

				}
				// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*

			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}
		}
	}

}
