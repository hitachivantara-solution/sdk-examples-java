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
import com.hitachivantara.hcp.standard.api.event.ListObjectHandler;
import com.hitachivantara.hcp.standard.body.HCPStandardClient;
import com.hitachivantara.hcp.standard.define.NextAction;
import com.hitachivantara.hcp.standard.model.HCPObjectEntry;
import com.hitachivantara.hcp.standard.model.HCPObjectEntrys;
import com.hitachivantara.hcp.standard.model.request.impl.ListObjectRequest;

public class RestExample03_ListObjects {

	public static void main(String[] args) throws IOException {
		{
			try {
				HCPStandardClient hcpClient = HCPClients.getInstance().getHCPClient();

				// Here is the folder path you want to list.
				String directoryKey = "sdk-test/moreThan100objs/";

				// 遍历目录
				// Request HCP to list all the objects in this folder.
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				ListObjectRequest request = new ListObjectRequest(directoryKey).withRecursiveDirectory(true);// .withObjectFilter(objectFilter);
				hcpClient.listObjects(request, new ListObjectHandler() {
					int i = 0;

					@Override
					public NextAction foundObject(HCPObjectEntry objectEntry) throws HCPException {
						System.out.println(++i + "\t" + objectEntry.getSize() + "\t" + objectEntry.getETag() + "\t" + objectEntry.getKey() + "\t" + objectEntry.getType());
						return null;
					}
				});
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
