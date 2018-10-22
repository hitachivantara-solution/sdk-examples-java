package com.hitachivantara.example.hcp.content;

import java.io.IOException;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.event.ListObjectHandler;
import com.hitachivantara.hcp.standard.body.HCPStandardClient;
import com.hitachivantara.hcp.standard.define.NextAction;
import com.hitachivantara.hcp.standard.model.HCPObjectEntry;
import com.hitachivantara.hcp.standard.model.request.impl.ListObjectRequest;

/**
 * 列出指定目录下所有对象的示例，包括子目录
 * 
 * @author sohan
 *
 */
public class RestExample_ListObjects {

	public static void main(String[] args) throws IOException {
		{
			try {
				HCPStandardClient hcpClient = HCPClients.getInstance().getHCPClient();

				// 需要列出的目录名
				// Here is the folder path you want to list.
				String directoryKey = "sdk-test/moreThan100objs/";

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
					public NextAction foundObject(HCPObjectEntry objectEntry) {
						System.out.println(++i + "\t" + objectEntry.getSize() + "\t" + objectEntry.getContentHash() + "\t" + objectEntry.getKey() + "\t" + objectEntry.getType());
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
