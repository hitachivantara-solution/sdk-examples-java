package com.hitachivantara.example.hcp.content;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadata;
import com.hitachivantara.hcp.standard.model.metadata.S3CompatibleMetadata;
import com.hitachivantara.hcp.standard.model.request.impl.PutMetadataRequest;
import com.hitachivantara.hcp.standard.util.MetadataUtils;

/**
 * 存取自定义元数据Metadata示例
 * </p>
 * Access custom Metadata example
 * 
 * @author sohan
 *
 */
public class RestExample_PutMetadata1 {

	public static void main(String[] args) throws IOException {
		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String key = "example-hcp/subfolder1/" + file.getName();
		
		// Create a file for below metadata operation.
		{
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();
				hcpClient.putObject(key, file);
			} catch (HSCException e) {
				e.printStackTrace();
			}
		}

		{
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				S3CompatibleMetadata metadata = new S3CompatibleMetadata();
				metadata.put("name", "Rison");
				metadata.put("company", "hitachi vantara");

				// Put S3 Compatible METADATA with specific key
				hcpClient.putMetadata(key, metadata);

				// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=

				// 创建一个XML Document对象
				Document doc = RestExample_PutMetadata2.createDocument();

				// Put Custom METADATA with specific key
				hcpClient.putMetadata(new PutMetadataRequest(key, "metadata2", MetadataUtils.toByteArray(doc)));

				// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=

				// Get S3 compatible metadata from HCP
				S3CompatibleMetadata metadataFromHCP = hcpClient.getMetadata(key);
				// Verify contents.
				assertTrue("Rison".equals(metadataFromHCP.get("name")));
				assertTrue("hitachi vantara".equals(metadataFromHCP.get("company")));

				// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=

				// Get custom metadata from HCP
				HCPMetadata meta = hcpClient.getMetadata(key, "metadata2");
				String metadata2Content = StreamUtils.inputStreamToString(meta.getContent(), true);

				System.out.println("Metadata from " + key);
				System.out.println(metadata2Content);

				// ByteArrayOutputStream out = new ByteArrayOutputStream();
				// OutputFormat format = OutputFormat.createPrettyPrint(); // 转换成字符串
				// format.setEncoding("UTF-8");
				// XMLWriter writer = new XMLWriter(out, format);
				// writer.write(doc);
				// assertTrue(metadata2Content.equalsIgnoreCase(out.toString()));

			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}

			System.out.println("Well done!");
		}
	}

}