package com.hitachivantara.example.hcp.content;

import java.io.IOException;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.api.event.ObjectDeletingListener;
import com.hitachivantara.hcp.standard.define.NextAction;
import com.hitachivantara.hcp.standard.model.HCPObjectEntry;
import com.hitachivantara.hcp.standard.model.HCPObjectSummary;
import com.hitachivantara.hcp.standard.model.request.impl.DeleteDirectoryRequest;

/**
 * 使用HCP SDK删除目录包括子目录下的所有文件
 * </p>
 * Use the HCP SDK to delete all files in a directory including subdirectories
 * 
 * @author sohan
 *
 */
public class RestExample_DeleteDirectory {

	public static void main(String[] args) throws IOException {
		{
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				// Here is the folder path you want to list.
				// 需要删除的目录
				final String directoryKey = "example-hcp/moreThan100objs/";
				
				// After execute folder "moreThan100objs" will be removed from HCP
				// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				hcpClient.deleteDirectory(new DeleteDirectoryRequest().withDirectory(directoryKey)
						// 是否清除历史版本,立即释放空间
						// You cannot delete specific old versions of an object, but if you have purge permission, you can purge the object to delete all its versions.
//						.withPurge(true)
						// Support privileged delete
//						.withPrivileged(true, "I Said!")
						// Delete the objects in folder/subfolder.
						.withDeleteContainedObjects(true)
						// 可以添加删除事件监听器，监听每个对象的删除事件
//						.withDeleteListener(new ObjectDeletingListener() {
//							// 删除后触发
//							@Override
//							public NextAction afterDeleting(HCPObjectSummary obj, boolean deleted) {
//								System.out.println("Object " + obj.getKey() + (deleted ? " deleted. " : " count not be deleted."));
//								return null;
//							}
//							// 删除动作前触发
//							@Override
//							public NextAction beforeDeleting(HCPObjectSummary objectSummary) {
//								// TODO Auto-generated method stub
//								return null;
//							}
//						})
						);
				// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				
				boolean exist = hcpClient.doesDirectoryExist(directoryKey);
				
				System.out.println("Folder " + directoryKey + (exist ? " failed to deleted!" : " deleted!"));
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
