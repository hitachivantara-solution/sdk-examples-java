/*                                                                             
 * Copyright (C) 2019 Hitachi Vantara Inc.                                     
 *                                                                             
 * Licensed under the Apache License, Version 2.0 (the "License");           
 * you may not use this file except in compliance with the License.            
 * You may obtain a copy of the License at                                     
 *                                                                             
 *      http://www.apache.org/licenses/LICENSE-2.0                             
 *                                                                             
 * Unless required by applicable law or agreed to in writing, software         
 * distributed under the License is distributed on an "AS IS" BASIS,         
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    
 * See the License for the specific language governing permissions and         
 * limitations under the License.                                              
 */                                                                            
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
public class RestExample_PrintAllNamespaceStatistics {

	public RestExample_PrintAllNamespaceStatistics() {
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
