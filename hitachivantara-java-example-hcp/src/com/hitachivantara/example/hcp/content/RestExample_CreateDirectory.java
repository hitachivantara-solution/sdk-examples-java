package com.hitachivantara.example.hcp.content;

import java.io.IOException;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.example.hcp.util.RandomInputStream;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.api.event.ObjectDeletingListener;
import com.hitachivantara.hcp.standard.define.NextAction;
import com.hitachivantara.hcp.standard.model.HCPObjectEntry;
import com.hitachivantara.hcp.standard.model.HCPObjectSummary;
import com.hitachivantara.hcp.standard.model.request.impl.DeleteDirectoryRequest;

/**
 * 创建目录
 * </p>
 * Create directorys
 * 
 * @author sohan
 *
 */
public class RestExample_CreateDirectory {

	public static void main(String[] args) throws IOException {
		{
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				final String directoryKey = "example-hcp/subfolder" + RandomInputStream.randomInt(100, 999);
				
				boolean exist = hcpClient.doesDirectoryExist(directoryKey);
				
				if (exist) {
					hcpClient.deleteDirectory(new DeleteDirectoryRequest(directoryKey).withDeleteContainedObjects(true));
				}

				// Create folder
				hcpClient.createDirectory(directoryKey);
				
				System.out.println("Folder created!");
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
