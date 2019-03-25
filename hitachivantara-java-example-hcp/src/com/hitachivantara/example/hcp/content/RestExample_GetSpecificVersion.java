package com.hitachivantara.example.hcp.content;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.example.hcp.util.RandomInputStream;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.api.ObjectEntryIterator;
import com.hitachivantara.hcp.standard.model.HCPObject;
import com.hitachivantara.hcp.standard.model.HCPObjectEntry;
import com.hitachivantara.hcp.standard.model.HCPObjectEntrys;
import com.hitachivantara.hcp.standard.model.PutObjectResult;
import com.hitachivantara.hcp.standard.model.request.impl.DeleteObjectRequest;
import com.hitachivantara.hcp.standard.model.request.impl.GetObjectRequest;
import com.hitachivantara.hcp.standard.model.request.impl.PutObjectRequest;

/**
 * 列出版本，获取特定版本
 * </p>
 * Lists the version and gets the specific version
 * 
 * @author sohan
 *
 */
public class RestExample_GetSpecificVersion {

	public static void main(String[] args) throws IOException {
		final String key = "example-hcp/file-0000.txt";

		// 创建测试用对象文件
		{
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				hcpClient.deleteObject(new DeleteObjectRequest(key).withPurge(true));

				// Here is the folder path you want to store files.

				// 创建具有10个版本的对象
				for (int j = 1; j <= 10; j++) {
					try {
						String content = new Date().toString() + " " + RandomInputStream.randomInt(10000, 99999);

						System.out.println("Content of Version " + j + ":");
						System.out.println(content);

						PutObjectResult result = hcpClient.putObject(new PutObjectRequest(key).withContent(content));

					} catch (InvalidResponseException e) {
						e.printStackTrace();
					} catch (HSCException e) {
						e.printStackTrace();
					}
				}

			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}
		}

		// 列出版本，获取特定版本
		{
			int i = 1;
			ObjectEntryIterator it = null;
			String versionId = null;
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				// List all the versions
				HCPObjectEntrys entrys = hcpClient.listVersions(key);
				it = entrys.iterator();
				List<HCPObjectEntry> objs;
				while ((objs = it.next(10)) != null) {
					for (HCPObjectEntry obj : objs) {
						// System.out.println(++i + "\t" + obj.getSize() + "\t" + obj.getContentHash() + "\t" + obj.getKey() + "\t" + obj.getType());
						if (i == 3) {
							versionId = obj.getVersionId();
						}

						i++;
					}
				}

				// Get specific version of object
				HCPObject obj = hcpClient.getObject(key, versionId);

				System.out.println("Get Content of Version " + 3 + ":");
				StreamUtils.inputStreamToConsole(obj.getContent(), true);

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
