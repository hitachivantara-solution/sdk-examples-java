package com.hitachivantara.example.hcp.content;

import java.io.IOException;
import java.util.List;

import com.hitachivantara.core.http.ClientConfiguration;
import com.hitachivantara.core.http.Protocol;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.build.HCPClientBuilder;
import com.hitachivantara.hcp.build.HCPStandardClientBuilder;
import com.hitachivantara.hcp.common.auth.BasicCredentials;
import com.hitachivantara.hcp.common.ex.HCPException;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.ObjectEntryIterator;
import com.hitachivantara.hcp.standard.body.HCPStandardClient;
import com.hitachivantara.hcp.standard.model.HCPObjectEntry;
import com.hitachivantara.hcp.standard.model.HCPObjectEntrys;
import com.hitachivantara.hcp.standard.model.request.impl.ListDirectoryRequest;

/**
 * 列出当前目录下所有对象的示例
 * @author sohan
 *
 */
public class RestExample_ListDirectory {

	public static void main(String[] args) throws IOException {
		{
			long i = 0;
			try {
				HCPStandardClient hcpClient = HCPClients.getInstance().getHCPClient();

				// Here is the folder path you want to list.
				String directoryKey = "sdk-test/moreThan100objs/";

				// 罗列指定目录中的所有对象
				// Request HCP to list all the objects in this folder.
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				HCPObjectEntrys entrys = hcpClient.listDirectory(directoryKey);
//				HCPObjectEntrys entrys = hcpClient.listDirectory(new ListDirectoryRequest(directoryKey).withDeletedObject(true));

				// Printout objects
				ObjectEntryIterator it = entrys.iterator();
				List<HCPObjectEntry> objs;
				while ((objs = it.next(100)) != null) {
					for (HCPObjectEntry objSummary : objs) {
						System.out.println(++i + "\t" + objSummary.getSize() + "\t" + objSummary.getETag() + "\t" + objSummary.getKey()+"\t"+objSummary.getType());
					}
				}
				
				//使用完毕一定要关闭
				it.close();
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				
			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HCPException e) {
				e.printStackTrace();
				return;
			}
		}
	}

}
