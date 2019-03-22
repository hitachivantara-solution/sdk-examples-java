package com.hitachivantara.example.hcp.content;

import java.io.IOException;
import java.util.Date;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.util.DateUtils;
import com.hitachivantara.common.util.FormatUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.HCPObjectSummary;

/**
 * 获取对象概要信息
 * 
 * @author sohan
 *
 */
public class RestExample_GetObjectSummary {

	public static void main(String[] args) throws IOException {

		{
			try {
				// 获得HCP客户端实例
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				// 获取当前对象的摘要信息
				 HCPObjectSummary summary = hcpClient.getObjectSummary("folder/subfolder/WeChat Image_20180716111626.doc");

				// 获取指定对象的摘要信息
//				HCPObjectSummary summary = hcpClient.getObjectSummary(new CheckObjectRequest()
//						// 指定对象Key
//						.withKey("folder/subfolder/WeChat Image_20180716111626.doc")
//						// 获取已经删除的对象信息（当开启版本功能，并通过DeleteObject功能删除对象后）
//						.withDeletedObject(true)
//						// 指定版本，获取过去版本的对象摘要
//						.withVersionId("1212121341313")
//						// 取得其他namespace的对象（当前登录用户需要拥有此namespace的访问权限）
//						.withNamespace("otherNamespace"));
				
				// 摘要中可以获得以下信息
				// getChangeTime()
				// getContentHash()
				// getDomain()
				// getDpl()
				// getETag()
				// getHashAlgorithmName()
				// getIngestProtocol()
				// getIngestTime()
				// getKey()
				// getMetadata()
				// getName()
				// getOwner()
				// getPosixGroupIdentifier()
				// getPosixUserID()
				// getRetention()
				// getRetentionClass()
				// getRetentionString()
				// getSize()
				// getType()
				// getVersionId()

				// getContentLength()
				// getContentType()
				// getDate()
				// getHcpTime()
				// getHcpVersion()
				// getRequestId()
				// getResponseCode()
				// getServer()
				// getServicedBySystem()

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
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}

			System.out.println("Well done!");
		}
	}

}
