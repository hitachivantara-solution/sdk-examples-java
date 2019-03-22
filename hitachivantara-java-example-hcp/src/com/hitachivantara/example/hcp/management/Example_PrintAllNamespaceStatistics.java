package com.hitachivantara.example.hcp.management;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.util.FormatUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.management.api.HCPTenantManagement;
import com.hitachivantara.hcp.management.model.ContentStatistics;
import com.hitachivantara.hcp.management.model.NamespaceSettings;

/**
 * 打印所有桶的容量以及使用情况
 * @author sohan
 *
 */
public class Example_PrintAllNamespaceStatistics {

	public Example_PrintAllNamespaceStatistics() {
	}

	public static void main(String[] args) throws HSCException {
		// 需要HCP开启管理功能API,并使用管理用户
		HCPTenantManagement namespaceClient = HCPClients.getInstance().getHCPTenantManagementClient();

		String[] namespaces = namespaceClient.listNamespaces();
		for (String namespace : namespaces) {
			NamespaceSettings namespaceSetting = namespaceClient.getNamespaceSettings(namespace);
			ContentStatistics statistic = namespaceClient.getNamespaceStatistics(namespace);
			
			System.out.println("--------------------------------------------------------------------------");
			// 当前桶的名称
			System.out.println("NamespaceName                = " + namespace);
			// 桶总容量
			System.out.println("Total Capacity               = " + namespaceSetting.getHardQuota()+" "+namespaceSetting.getHardQuotaUnit());
			// 桶中对象的总数量
			System.out.println("Object Count                 = " + statistic.getObjectCount());
			// 已使用的容量信息
			System.out.println("Used Capacity Bytes          = " + FormatUtils.getPrintSize(statistic.getStorageCapacityUsed(), true));
			// 当前桶中自定义元数据的size以及数量
			System.out.println("Custom Metadata Object Count = " + statistic.getCustomMetadataCount());
			System.out.println("Custom Metadata Object Bytes = " + FormatUtils.getPrintSize(statistic.getCustomMetadataSize(), true));
			// 桶中准备要彻底清除的对象数量及大小
			System.out.println("Shred Object Count           = " + statistic.getShredCount());
			System.out.println("Shred Object Bytes           = " + FormatUtils.getPrintSize(statistic.getShredSize(), true));

		}
	}

}
