package com.hitachivantara.example.hcp.content;

import java.io.IOException;
import java.util.Date;

import com.hitachivantara.common.util.DateUtils;
import com.hitachivantara.common.util.FormatUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.HCPException;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.body.HCPStandardClient;
import com.hitachivantara.hcp.standard.model.HCPObjectSummary;

/**
 * 统计桶信息示例
 * 
 * @author sohan
 *
 */
public class RestExample_GetObjectSummary {

	public static void main(String[] args) throws IOException {

		{
			try {
				HCPStandardClient hcpClient = HCPClients.getInstance().getHCPClient();

//				HCPObjectSummary summary = hcpClient.getObjectSummary("folder/subfolder/WeChat Image_20180716111626.doc");
				HCPObjectSummary summary = hcpClient.getObjectSummary("folder/subfolder/");
//				HCPObjectSummary summary = hcpClient.getObjectSummary("/");

				System.out.println("Name = " + summary.getName());
				if (summary.isDirectory()) {
					System.out.println("Is folder.");
				} else if (summary.isObject()) {
					System.out.println("Size = " + FormatUtils.getPrintSize(summary.getSize()));
					System.out.println("IngestTime = " + DateUtils.ISO8601_DATE_FORMAT.format(new Date(summary.getIngestTime())));
				}

			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HCPException e) {
				e.printStackTrace();
				return;
			}

			System.out.println("Well done!");
		}
	}

}
