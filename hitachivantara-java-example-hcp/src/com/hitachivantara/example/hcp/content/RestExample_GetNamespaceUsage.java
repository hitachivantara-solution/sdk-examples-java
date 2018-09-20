package com.hitachivantara.example.hcp.content;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.hitachivantara.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.HCPException;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.body.HCPStandardClient;
import com.hitachivantara.hcp.standard.model.NamespaceStatistics;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadata;
import com.hitachivantara.hcp.standard.model.metadata.S3CompatibleMetadata;
import com.hitachivantara.hcp.standard.model.request.impl.PutMetadataRequest;
import com.hitachivantara.hcp.standard.model.request.impl.PutObjectRequest;

/**
 * 统计桶信息示例
 * 
 * @author sohan
 *
 */
public class RestExample_GetNamespaceUsage {

	public static void main(String[] args) throws IOException {

		{
			try {
				HCPStandardClient hcpClient = HCPClients.getInstance().getHCPClient();

				NamespaceStatistics statistics = hcpClient.getNamespacesStatistics();

				System.out.println("CustomMetadataObjectBytes = " + statistics.getCustomMetadataObjectBytes());
				System.out.println("CustomMetadataObjectCount = " + statistics.getCustomMetadataObjectCount());
				System.out.println("NamespaceName = " + statistics.getNamespaceName());
				System.out.println("ObjectCount = " + statistics.getObjectCount());
				System.out.println("ShredObjectBytes = " + statistics.getShredObjectBytes());
				System.out.println("ShredObjectCount = " + statistics.getShredObjectCount());
				System.out.println("SoftQuotaPercent = " + statistics.getSoftQuotaPercent());
				System.out.println("TotalCapacityBytes = " + statistics.getTotalCapacityBytes());
				System.out.println("UsedCapacityBytes = " + statistics.getUsedCapacityBytes());

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
