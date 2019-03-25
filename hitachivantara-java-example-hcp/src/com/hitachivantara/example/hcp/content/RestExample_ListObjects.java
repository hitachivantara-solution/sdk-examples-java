package com.hitachivantara.example.hcp.content;

import java.io.IOException;
import java.io.InputStream;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.api.event.ListObjectHandler;
import com.hitachivantara.hcp.standard.define.NextAction;
import com.hitachivantara.hcp.standard.model.HCPObjectSummary;
import com.hitachivantara.hcp.standard.model.metadata.Annotation;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadata;
import com.hitachivantara.hcp.standard.model.request.impl.ListObjectRequest;

/**
 * 列出指定目录下所有对象的示例，包括子目录
 * </p>
 * List all objects in the specified directory including sub directories
 * 
 * @author sohan
 *
 */
public class RestExample_ListObjects {

	public static void main(String[] args) throws IOException {
		{
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				// 需要列出的目录名
				// Here is the folder path you want to list.
				String directoryKey = "example-hcp/moreThan100objs/";

				// 遍历目录
				// Request HCP to list all the objects in this folder.
				// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				ListObjectRequest request = new ListObjectRequest(directoryKey)
						// 指定需要遍历此目录
						.withRecursiveDirectory(true)
						// 可以通过设置Filter过滤对象
//						.withObjectFilter(new ObjectFilter() {
//
//							@Override
//							public boolean accept(HCPObjectEntry arg0) {
//								//只有对象名称包含字母X的才被foundObject
//								return arg0.getName().contains("X");
//							}
//						})
						;
				
				hcpClient.listObjects(request, new ListObjectHandler() {
					 int i = 0;

					// 发现的对象信息
					@Override
					public NextAction foundObject(HCPObjectSummary obj) throws HSCException {
						 System.out.println(++i + "\t" + obj.getSize() + "\t" + obj.getKey() + "\t" + obj.getType() + "\t" + obj.getContentHash());

						// 做一些事情，例如打印文件内容
						// You can do something more...
//						try {
//							InputStream content = hcpClient.getObject(obj.getKey()).getContent();
//							System.out.print("Content:");
//							StreamUtils.inputStreamToConsole(content, true);
//							System.out.println();
//						} catch (IOException e) {
//						}

						// 如需要可以停止列出目录
						// You can add specific conditions to stop the listing action
//						if (i == 88) {
//							return NextAction.stop;
//						}
						 
						return null;
					}
				});
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
