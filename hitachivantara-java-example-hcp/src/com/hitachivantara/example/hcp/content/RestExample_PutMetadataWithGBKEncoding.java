/*                                                                             
 * Copyright (C) 2019 Hitachi Vantara Inc.                                     
 *                                                                             
 * Licensed under the Apache License, Version 2.0 (the "License");           
 * you may not use this file except in compliance with the License.            
 * You may obtain a copy of the License at                                     
 *                                                                             
 *      http://www.apache.org/licenses/LICENSE-2.0                             
 *                                                                             
 * Unless required by applicable law or agreed to in writing, software         
 * distributed under the License is distributed on an "AS IS" BASIS,         
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    
 * See the License for the specific language governing permissions and         
 * limitations under the License.                                              
 */                                                                            
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
public class RestExample_PutMetadataWithGBKEncoding {
	
	//*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
	//*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
	// 请在[Run]设置->Run Configuration]中配置java vm参数 -Dfile.encoding=GBK
	//*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
	//*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*

	public static void main(String[] args) throws IOException {
		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
//		String key = "example-hcp/subfolder1/" + file.getName();
//		String key = "example-hcp/subfolder1/testFile4EncodingMeta.txt";
		String key = "example-hcp/subfolder1/testFile4EncodingMeta_GBK.txt";

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

				S3CompatibleMetadata metadata = new S3CompatibleMetadata("gbk");
				metadata.put("name", "Rison");
				metadata.put("company", "hitachi vantara");
				metadata.put("comment", "这是中文注释用来测试<GBK>编码的元数据是否《成功》！");
				metadata.put("file", "测试文件");

				// Put S3 Compatible METADATA with specific key
				hcpClient.putMetadata(key, metadata);

				// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=

				// 创建一个XML Document对象
				Document doc = createDocument();

				// Put Custom METADATA with specific key
				hcpClient.putMetadata(new PutMetadataRequest(key, "metadata2", MetadataUtils.toByteArray(doc, "gbk")));

				// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=

				// Get S3 compatible metadata from HCP
				S3CompatibleMetadata metadataFromHCP = hcpClient.getMetadata(key);
				// Verify contents.
				assertTrue("Rison".equals(metadataFromHCP.get("name")));
				assertTrue("hitachi vantara".equals(metadataFromHCP.get("company")));
				assertTrue("这是中文注释用来测试<GBK>编码的元数据是否《成功》！".equals(metadataFromHCP.get("comment")));

				// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
				
//				HCPMetadata meta1 = hcpClient.getMetadata(key, ".metapairs");
//				String metadata2Content1 = StreamUtils.inputStreamToString(meta1.getContent(), true);
//
//				System.out.println(".metapairs from " + key);
//				System.out.println(metadata2Content1);

				// Get custom metadata from HCP
				HCPMetadata meta = hcpClient.getMetadata(key, "metadata2");
				String metadata2Content = StreamUtils.inputStreamToString(meta.getContent(), true);

				System.out.println("Metadata from " + key);
				System.out.println(metadata2Content);

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
	
	private static Document createDocument() {
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
