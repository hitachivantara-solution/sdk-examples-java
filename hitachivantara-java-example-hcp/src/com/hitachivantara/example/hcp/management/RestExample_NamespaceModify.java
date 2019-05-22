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

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.management.api.HCPTenantManagement;
import com.hitachivantara.hcp.management.define.AclsUsage;
import com.hitachivantara.hcp.management.define.HashScheme;
import com.hitachivantara.hcp.management.define.OptimizedFor;
import com.hitachivantara.hcp.management.define.OwnerType;
import com.hitachivantara.hcp.management.define.QuotaUnit;
import com.hitachivantara.hcp.management.model.NamespaceSettings;
import com.hitachivantara.hcp.management.model.builder.SettingBuilders;

public class RestExample_NamespaceModify {

	public RestExample_NamespaceModify() {
	}

	public static void main(String[] args) throws HSCException {
		// 需要HCP开启管理功能API,并使用管理用户
		HCPTenantManagement namespaceClient = HCPClients.getInstance().getHCPTenantManagementClient();
		String ns = "notexist-bucket-1";
		String ns2 = "notexist-bucket-2";
		// PREPARE TEST DATA ----------------------------------------------------------------------
		// 桶空间如果非空无法删除
		namespaceClient.deleteNamespace(ns);
		namespaceClient.deleteNamespace(ns2);

		boolean exist = namespaceClient.doesNamespaceExist(ns);
		assertTrue(exist == false);
		// PREPARE TEST DATA ----------------------------------------------------------------------

		String localUserName1 = "user1";
		// EXEC TEST FUNCTION ---------------------------------------------------------------------
		
		// 配置一个新的桶空间
		NamespaceSettings namespaceSetting1 = SettingBuilders.createNamespaceBuilder()
				.withName(ns)
				.withHardQuota(1.2, QuotaUnit.GB)
				.withSoftQuota(66)
				.withDescription("DDD")
				.withHashScheme(HashScheme.SHA512)
				.withMultipartUploadAutoAbortDays(6)
				.withOptimizedFor(OptimizedFor.CLOUD)
				.withAclsUsage(AclsUsage.ENFORCED)
				.withCustomMetadataIndexingEnabled(false)
				.withSearchEnabled(false)
				.withVersioningEnabled(true)
				.withVersioningKeepDeletionRecords(false)
				.withVersioningPrune(false)
				.withVersioningPruneDays(9)
				.withIndexingEnabled(false)
				//.withOwner(OwnerType.LOCAL, localUserName1 )
				//.withEnterpriseMode(true)
				//.withTags("AAA","BBB","中文")
				.bulid();
		// 执行空间创建
		namespaceClient.createNamespace(namespaceSetting1);
		// 验证是否创建
		exist = namespaceClient.doesNamespaceExist(ns);
		System.out.println("Namespece [" + ns + "] " + (exist ? "created!" : "create failed!"));
		
		// 获得并验证桶空间配置信息
		NamespaceSettings namespaceSetting_before_modified = namespaceClient.getNamespaceSettings(ns);

		// 创建一个空间配置，用于修改刚才创建的空间
		// 空间大小扩充至3GB
		NamespaceSettings namespaceSetting2 = SettingBuilders.modifyNamespaceBuilder()
//				.withName(ns)
				// 修改为新的名字
				.withName(ns2)
				// 修改为新容量
				.withHardQuota(3, QuotaUnit.GB)
				// 修改注释
//				.withDescription("xxxxxxxxx")
				// 修改多版本中未完成上传对象的分片保留期
//				.withMultipartUploadAutoAbortDays(3)
				// 优化为CLOUD模式（Cifs NFS等功能将被禁用，性能可提升）
//				.withOptimizedFor(OptimizedFor.CLOUD)
//				.withSoftQuota(10)
//				.withAclsUsage(AclsUsage.ENFORCED)
				// 开启meta解析
//				.withCustomMetadataIndexingEnabled(true)
				// 开启搜索
				.withSearchEnabled(true)
				// 开启index功能
				.withIndexingEnabled(true)
//				.withTags("CCC","DDD","中文")
				// 修改桶的Owner
//				.withOwner(OwnerType.LOCAL, localUserName2)
				.bulid();

		// 执行桶配置修改
		namespaceClient.changeNamespace(ns, namespaceSetting2);

		System.out.println("Namespece [" + ns + "] configuration modified!");

		// 获得并验证桶空间配置信息
		NamespaceSettings namespaceSetting_modified = namespaceClient.getNamespaceSettings(ns2);

		System.out.println("------------------------------------");
		System.out.println("Namespace [" + ns + "->" + ns2 + "]:");
		System.out.println("Capacity Before: " + namespaceSetting_before_modified.getHardQuota() + " " + namespaceSetting_before_modified.getHardQuotaUnit());
		System.out.println("Capacity After : " + namespaceSetting_modified.getHardQuota() + " " + namespaceSetting_modified.getHardQuotaUnit());
		System.out.println("------------------------------------");

		System.out.println("Well done!");
}
}
