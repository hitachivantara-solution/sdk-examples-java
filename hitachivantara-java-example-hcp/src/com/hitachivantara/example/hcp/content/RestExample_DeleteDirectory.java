package com.hitachivantara.example.hcp.content;

import java.io.IOException;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.body.HCPStandardClient;
import com.hitachivantara.hcp.standard.model.request.impl.DeleteDirectoryRequest;

/**
 * 使用多线程创建100随机内容个文件
 * 
 * @author sohan
 *
 */
public class RestExample_DeleteDirectory {

	public static void main(String[] args) throws IOException {
		{
			try {
				HCPStandardClient hcpClient = HCPClients.getInstance().getHCPClient();

				// Here is the folder path you want to list.
				// 需要列出的目录名
				final String directoryKey = "sdk-test/moreThan100objs/";

				// Request HCP to list all the objects in this folder.
				// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				hcpClient.deleteDirectory(new DeleteDirectoryRequest().withDirectory(directoryKey)
						// Purge=true HCP将清除历史版本清理出更多空间
						.withPurgeDelete(true)
						//是否删除子目录及其对象文件
						.withRecursiveDirectory(true)
						// 删除事件监听器
//						.withDeleteListener(new ObjectDeletingListener() {
//							// 删除动作前触发
//							@Override
//							public NextAction beforeDelete(HCPObjectEntry obj) {
//								return null;
//							}
//							// 删除后触发
//							@Override
//							public NextAction afterDelete(HCPObjectEntry obj, boolean deleted) {
//								System.out.println("Object " + obj.getKey() + (deleted ? " deleted. " : " count not be deleted."));
//								return null;
//							}
//						})
						);
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
