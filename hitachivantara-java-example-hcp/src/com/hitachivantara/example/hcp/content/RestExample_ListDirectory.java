package com.hitachivantara.example.hcp.content;

import java.io.IOException;
import java.util.List;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.ObjectEntryIterator;
import com.hitachivantara.hcp.standard.body.HCPStandardClient;
import com.hitachivantara.hcp.standard.model.HCPObjectEntry;
import com.hitachivantara.hcp.standard.model.HCPObjectEntrys;

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
				// 需要列出的目录名
				String directoryKey = "sdk-test/moreThan100objs/";

				// 罗列指定目录中的所有对象
				// Request HCP to list all the objects in this folder.
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				HCPObjectEntrys entrys = hcpClient.listDirectory(directoryKey);
				// 列出目录中的对象包括被删除的对象（需要HCP开启版本功能）
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
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}
		}
	}

}
