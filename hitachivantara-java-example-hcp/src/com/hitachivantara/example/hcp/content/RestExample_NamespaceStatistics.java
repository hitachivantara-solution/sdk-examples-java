package com.hitachivantara.example.hcp.content;

import java.io.IOException;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.util.FormatUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.body.HCPStandardClient;
import com.hitachivantara.hcp.standard.model.NamespaceStatistics;

/**
 * 统计桶信息示例
 * 
 * @author sohan
 *
 */
public class RestExample_NamespaceStatistics {

	public static void main(String[] args) throws IOException {

		{
			try {
				// 获得HCP客户端实例
				HCPStandardClient hcpClient = HCPClients.getInstance().getHCPClient();

				// 获得当前登录namespace的统计信息
				NamespaceStatistics statistics = hcpClient.getNamespacesStatistics();
				// 获得当指定namespace的统计信息
				// hcpClient.getNamespacesStatistics("namespaceName");

				// 当前桶的名称
				System.out.println("NamespaceName = " + statistics.getNamespaceName());
				// 桶中对象的总数量
				System.out.println("ObjectCount = " + statistics.getObjectCount());
				// 桶的总容量
				System.out.println("TotalCapacityBytes = " + FormatUtils.getPrintSize(statistics.getTotalCapacityBytes(), true));
				// 已使用的容量信息
				System.out.println("UsedCapacityBytes = " + FormatUtils.getPrintSize(statistics.getUsedCapacityBytes(), true));
				// 当前桶中自定义元数据的size以及数量
				System.out.println("CustomMetadataObjectCount = " + statistics.getCustomMetadataObjectCount());
				System.out.println("CustomMetadataObjectBytes = " + FormatUtils.getPrintSize(statistics.getCustomMetadataObjectBytes(), true));
				// 桶中准备要彻底清除的对象数量及大小
				System.out.println("ShredObjectBytes = " + FormatUtils.getPrintSize(statistics.getShredObjectBytes(), true));
				System.out.println("ShredObjectCount = " + statistics.getShredObjectCount());
				// 默认的空间配比设置
				System.out.println("SoftQuotaPercent = " + statistics.getSoftQuotaPercent());

				// 测试环境打印结果示例：
				// NamespaceName = Account.namespace;
				// ObjectCount = 34872
				// TotalCapacityBytes = 50.0 GB ( 53,687,091,200 bytes )
				// UsedCapacityBytes = 13.2 GB ( 14,226,214,912 bytes )
				// CustomMetadataObjectCount = 1466
				// CustomMetadataObjectBytes = 175.0 KB ( 179,177 bytes )
				// ShredObjectBytes = 0 B
				// ShredObjectCount = 0
				// SoftQuotaPercent = 85.0
				// Well done!

			//通过捕捉InvalidResponseException可以获取HCP返回的错误信息
			} catch (InvalidResponseException e) {
				// 返回的错误代码
				e.getStatusCode();
				// 错误原因简述
				e.getReason();
				// 引发错误的情况详细
				e.getMessage();
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
