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

import java.util.List;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.management.api.HCPTenantManagement;
import com.hitachivantara.hcp.management.define.Protocols;
import com.hitachivantara.hcp.management.model.HttpProtocolSettings;
import com.hitachivantara.hcp.management.model.IPSettings;
import com.hitachivantara.hcp.management.model.builder.SettingBuilders;

/**
 * 展示如何修改桶中Protocol的相关配置
 * @author sohan
 *
 */
public class RestExample_ProtocolSettingsModify {

	public RestExample_ProtocolSettingsModify() {
	}

	public static void main(String[] args) {
		try {
			// 需要HCP开启管理功能API,并使用管理用户
			HCPTenantManagement namespaceClient = HCPClients.getInstance().getHCPTenantManagementClient();

			// 可以列出当前teantn下所有namespace后分别配置
			// String[] namespacpes = namespaceClient.listNamespaces();
			// for (String namespace : namespacpes) {
			// modifyConfigs(namespace);
			// }

			// 判断当前桶是否存在，此步骤为示例，可根据实际情况判断与否
			boolean exist = namespaceClient.doesNamespaceExist(Account.namespace);
			assertTrue(exist == true);

			// 修改桶配置
			modifyConfigs(Account.namespace);

			// 清除桶IP配置
			clearIPConfigs(Account.namespace);

		} catch (InvalidResponseException e) {
			e.printStackTrace();
		} catch (HSCException e) {
			e.printStackTrace();
		}

		System.out.println("Well done!");
	}

	/**
	 * 修改Protocol配置
	 * @param namespace
	 * @throws InvalidResponseException
	 * @throws HSCException
	 */
	private static void modifyConfigs(String namespace) throws InvalidResponseException, HSCException {
		HCPTenantManagement namespaceClient = HCPClients.getInstance().getHCPTenantManagementClient();

		// 获取当前协议配置
		HttpProtocolSettings currentHttpSettings = namespaceClient.getNamespaceProtocol(namespace, Protocols.HTTP);
		IPSettings currentIPSettings = currentHttpSettings.getIpSettings();

		// 创建协议配置
		HttpProtocolSettings httpSettings = SettingBuilders.modifyHttpProtocolBuilder()
				// .withHs3Enabled(!httpSettings1.getHs3Enabled())
				// .withHs3RequiresAuthentication(!httpSettings1.getHs3RequiresAuthentication())
				// .withHswiftEnabled(!httpSettings1.getHswiftEnabled())
				// .withHswiftRequiresAuthentication(!httpSettings1.getHswiftRequiresAuthentication())
				// .withHttpActiveDirectorySSOEnabled(!httpSettings1.getHttpActiveDirectorySSOEnabled())
				// .withHttpEnabled(!httpSettings1.getHttpEnabled())
				// .withHttpsEnabled(!httpSettings1.getHttpsEnabled())
				// .withRestEnabled(!httpSettings1.getRestEnabled())
				// .withRestRequiresAuthentication(!httpSettings1.getRestRequiresAuthentication())
				// .withWebdavBasicAuth("webdavBasicAuthUser1", "!QAZ1qaz")
				// .withWebdavCustomMetadata(!httpSettings1.getWebdavCustomMetadata())
				// .withWebdavEnabled(!httpSettings1.getWebdavEnabled())
				// 添加即存的配置
				.withIpSettings(currentIPSettings)
				// 指定可以访问的白名单地址，此处可添加多个IP地址，或掩码地址，也可以通过List<String>配置
				.withAllowAddressees("10.10.10.99", "192.168.1.111/27")
				// 添加即存的配置
				// 指定禁止访问的黑名单地址，此处可添加多个IP地址，或掩码地址，也可以通过List<String>配置
				.withDenyAddresses("10.10.10.1", "192.168.111.123/27", "192.168.9.123/26")
				// 配置如果相同的IP在白名单也在黑名单，将被允许访问
				.withAllowIfInBothLists(true)
				.bulid();

		namespaceClient.changeNamespaceProtocol(namespace, httpSettings);

		System.out.println("Namespece [" + namespace + "] configuration modified!");

		// ----------------------------------------------------------------------------------------------
		printCurrentIPSettings(namespace);
		// ----------------------------------------------------------------------------------------------
	}
	
	/**
	 * 清除IP配置
	 * @param namespace
	 * @throws InvalidResponseException
	 * @throws HSCException
	 */
	private static void clearIPConfigs(String namespace) throws InvalidResponseException, HSCException {
		HCPTenantManagement namespaceClient = HCPClients.getInstance().getHCPTenantManagementClient();

		// 创建协议配置
		HttpProtocolSettings httpSettings = SettingBuilders.modifyHttpProtocolBuilder()
				// 可以单独清空配置
				 .withClearAllowAddressees()
				 .withClearDenyAddresses()
				// 配置如果相同的IP在白名单也在黑名单，将被允许访问
//				.withAllowIfInBothLists(true)
				.bulid();

		namespaceClient.changeNamespaceProtocol(namespace, httpSettings);

		System.out.println("Namespece [" + namespace + "] IP configuration cleared!");

		// ----------------------------------------------------------------------------------------------
		printCurrentIPSettings(namespace);
		// ----------------------------------------------------------------------------------------------
	}
	
	/**
	 * 显示当前IP配置
	 * @param namespace
	 * @throws InvalidResponseException
	 * @throws HSCException
	 */
	private static void printCurrentIPSettings(String namespace) throws InvalidResponseException, HSCException {
		HCPTenantManagement namespaceClient = HCPClients.getInstance().getHCPTenantManagementClient();
		// ----------------------------------------------------------------------------------------------
		HttpProtocolSettings httpSettings2 = namespaceClient.getNamespaceProtocol(namespace, Protocols.HTTP);

		List<String> allows = httpSettings2.getIpSettings().getAllowAddresses();
		System.out.println("AllowAddresses of Namespece [" + namespace + "] count:" + allows.size());
		for (String ip : allows) {
			System.out.println(" " + ip);
		}

		List<String> denys = httpSettings2.getIpSettings().getDenyAddresses();
		System.out.println("DenyAddresses of Namespece [" + namespace + "] count:" + denys.size());
		for (String ip : denys) {
			System.out.println(" " + ip);
		}
		// ----------------------------------------------------------------------------------------------
	}
	
	

}
