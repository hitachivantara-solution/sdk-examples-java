package com.hitachivantara.example.hcp.content;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.body.HCPStandardClient;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadata;
import com.hitachivantara.hcp.standard.model.metadata.S3CompatibleMetadata;
import com.hitachivantara.hcp.standard.model.request.impl.PutObjectRequest;
import com.hitachivantara.hcp.standard.util.MetadataUtils;

/**
 * 存取自定义元数据Metadata示例
 * @author sohan
 *
 */
public class RestExample_PutMetadata {

	public static void main(String[] args) throws IOException {
		// Here is the file will be uploaded into HCP
		File file = new File("C:\\VDisk\\DriverD\\Downloads\\Temp\\WeChat Image_20180716111626.doc");
		// The location in HCP where this file will be stored.
		String key = "folder/subfolder/" + file.getName();

		{
			try {
				HCPStandardClient hcpClient = HCPClients.getInstance().getHCPClient();

				S3CompatibleMetadata metadata = new S3CompatibleMetadata();
				metadata.put("name", "Rison");
				metadata.put("company", "hitachi vantara");

				Document doc = createDocument();

				// Inject file with 2 pattern of metadata into HCP system.
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				hcpClient.putObject(new PutObjectRequest(key, file).withMetadata(metadata).withMetadata("moreInfo", MetadataUtils.toByteArray(doc)));
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*

				// Get metadata from HCP
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				S3CompatibleMetadata metadataFromHCP = hcpClient.getMetadata(key);
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				HCPMetadata meta = hcpClient.getMetadata(key, "moreInfo");
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				String xmlContent = StreamUtils.inputStreamToString(meta.getContent(), true);

				// Verify contents.
				assertTrue("Rison".equals(metadataFromHCP.get("name")));
				assertTrue("hitachi vantara".equals(metadataFromHCP.get("company")));

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				OutputFormat format = OutputFormat.createPrettyPrint(); // 转换成字符串
				format.setEncoding("UTF-8");
				XMLWriter writer = new XMLWriter(out, format);
				writer.write(doc);
				assertTrue(xmlContent.equals(out.toString()));

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

	public static Document createDocument() {
		Document document = DocumentHelper.createDocument();

		Element root = document.addElement("result");
		root.addElement("code").addText("1");
		Element data = root.addElement("data");

		Element person1 = data.addElement("person");
		person1.addElement("name").setText("张三");
		person1.addElement("id").setText("1");
		person1.addElement("url").setText("http://192.168.191.1:9999/TestWeb/c7fe21616d2a5e2bd1e84bd453a5b30f.jpg");
		Element courses1 = person1.addElement("courses");
		Element course1 = courses1.addElement("course");
		course1.addElement("courseName").setText("语文");
		course1.addElement("courseMarks").setText("90");
		course1.addElement("courseId").setText("1");
		Element course2 = courses1.addElement("course");
		course2.addElement("courseName").setText("数学");
		course2.addElement("courseMarks").setText("80");
		course2.addElement("courseId").setText("2");
		Element course3 = courses1.addElement("course");
		course3.addElement("courseName").setText("英语");
		course3.addElement("courseMarks").setText("70");
		course3.addElement("courseId").setText("3");

		Element person2 = data.addElement("person").addAttribute("name", "李四").addAttribute("id", "2").addAttribute("url",
				"http://192.168.191.1:9999/TestWeb/4052858c526002a712ef574ccae1948f.jpg");
		person2.addElement("course").addAttribute("courseName", "语文").addAttribute("courseMarks", "91").addAttribute("courseId", "1");
		person2.addElement("course").addAttribute("courseName", "数学").addAttribute("courseMarks", "82").addAttribute("courseId", "1");
		person2.addElement("course").addAttribute("courseName", "英语").addAttribute("courseMarks", "73").addAttribute("courseId", "1");

		return document;
	}

}
