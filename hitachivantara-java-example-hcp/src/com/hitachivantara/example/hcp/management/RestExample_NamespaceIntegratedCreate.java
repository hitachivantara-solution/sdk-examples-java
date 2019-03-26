package com.hitachivantara.example.hcp.management;

import java.io.File;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.util.DigestUtils;
import com.hitachivantara.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.management.api.HCPTenantManagement;
import com.hitachivantara.hcp.management.define.AclsUsage;
import com.hitachivantara.hcp.management.define.HashScheme;
import com.hitachivantara.hcp.management.define.OptimizedFor;
import com.hitachivantara.hcp.management.define.Permission;
import com.hitachivantara.hcp.management.define.QuotaUnit;
import com.hitachivantara.hcp.management.model.DataAccessPermissions;
import com.hitachivantara.hcp.management.model.HttpProtocolSettings;
import com.hitachivantara.hcp.management.model.NamespaceSettings;
import com.hitachivantara.hcp.management.model.UserAccount;
import com.hitachivantara.hcp.management.model.builder.SettingBuilders;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.HCPObject;

/**
 * 创建桶示例
 * 
 * @author sohan
 *
 */
public class RestExample_NamespaceIntegratedCreate {

	public static void main(String[] args) throws HSCException {
		// 需要HCP开启管理功能API,并使用管理用户
		HCPTenantManagement tenant = HCPClients.getInstance().getHCPTenantManagementClient();

		String bucketName = "notexist-bucket-1";
		// PREPARE TEST DATA ----------------------------------------------------------------------

		// 判断桶是否存在		
		boolean exist = tenant.doesNamespaceExist(bucketName);
		System.out.println("Namespece [" + bucketName + "] " + (exist ? "exist!" : "not exist!"));

		if (exist) {
			// 桶空间如果非空无法删除
			// 如无法删除请手动清除桶内所有文件、目录关闭Version/Search/Keep deletion records后执行Garbage Collection
			tenant.deleteNamespace(bucketName);
		}

		exist = tenant.doesNamespaceExist(bucketName);
		if (!exist) {
			System.out.println("Namespece [" + bucketName + "] deleted!");
		} else {
			System.out.println("Namespece [" + bucketName + "] failed to delete!");
			return;
		}

		// PREPARE TEST DATA ----------------------------------------------------------------------
		String localUserName1 = "user111";
		String localUserName2 = "user222";
		// EXEC TEST FUNCTION ---------------------------------------------------------------------
		
		// 创建桶空间，此处关闭了版本功能
		{
			// 创建桶配置
			NamespaceSettings namespaceSetting1 = SettingBuilders.createNamespaceBuilder()
					.withName(bucketName)
					.withHardQuota(4, QuotaUnit.GB)
					// 开启搜索以及元数据解析
					// Enable search and metadata parsing
					.withCustomMetadataIndexingEnabled(true)
					.withIndexingEnabled(true)
					.withCustomMetadataValidationEnabled(true)
					.withSearchEnabled(true)
					.withHashScheme(HashScheme.MD5)
					// 配置为CLOUD模式
					// Configure to CLOUD mode
					.withOptimizedFor(OptimizedFor.CLOUD)
					// 关闭版本
					// Disable versioning
					.withVersioningEnabled(false)
					.withVersioningKeepDeletionRecords(false)
					.withVersioningPrune(false)
					// 开启ACL
					// Open ACL for S3 API
					.withAclsUsage(AclsUsage.ENFORCED)
					.bulid();
			// 执行创建桶
			tenant.createNamespace(namespaceSetting1);
			
			System.out.println("Namespace [" + bucketName + "] created!");
		}
		
		// 开启协议
		// Open specific protocol 
		{
			HttpProtocolSettings httpProtocolSetting = SettingBuilders.modifyHttpProtocolBuilder()
					// 开启S3协议
					.withHs3Enabled(true)
					.withHs3RequiresAuthentication(true)
					// 开始Rest协议
					.withRestEnabled(true)
					.withRestRequiresAuthentication(true)
					// 开启Http以及Https
					.withHttpEnabled(true)
					.withHttpsEnabled(true)
					.bulid();
			
			tenant.changeNamespaceProtocol(bucketName, httpProtocolSetting);
			
			System.out.println("S3    API      enabled!");
			System.out.println("Rest  API      enabled!");
			System.out.println("Http  Protocol enabled!");
			System.out.println("Https Protocol enabled!");
		}

		// 判断有没有第一个用户，如果没有创建一个
		// Determine if there has the first user, trying to create if not exist
		{
			if (!tenant.doesUserAccountExist(localUserName1)) {
				UserAccount userAccountSetting = SettingBuilders.createUserAcccountBuilder()
					.withUserName(localUserName1, localUserName1)
					.withEnable(true)
					.withPassword("himitu123")
//					.withDescription("created by api")
					.withLocalAuthentication(true)
					.withForcePasswordChange(false)
					.bulid();
				
				tenant.createUserAccount(userAccountSetting);
			}

			// 配置第一个用户为只读用户
			DataAccessPermissions permissions1 = SettingBuilders.modifyDataAccessPermissionBuilder()
					.withPermission(bucketName, 
							Permission.BROWSE, 
							Permission.READ)
					.bulid();
			tenant.changeDataAccessPermissions(localUserName1, permissions1);
		}
		
		// 判断有没有第二个用户，如果没有创建一个
		// Determine if there has the second user, trying to create if not exist
		{
			if (!tenant.doesUserAccountExist(localUserName2)) {
				UserAccount userAccountSetting = SettingBuilders.createUserAcccountBuilder()
						.withUserName(localUserName2, localUserName2)
						.withEnable(true)
						.withPassword("himitu123")
//						.withDescription("created by api")
						.withLocalAuthentication(true)
						.withForcePasswordChange(false)
						// 可以赋予用户更多角色
//						.withRole(
//								Role.ADMINISTRATOR,
//								Role.MONITOR 
////							Role.SECURITY, 
////							Role.COMPLIANCE
//								)
						.bulid();
					
					tenant.createUserAccount(userAccountSetting);
			}
	
			// 配置第二个用户为读写用户
			// Configure the second user as a read-write user
			DataAccessPermissions permissions2 = SettingBuilders.modifyDataAccessPermissionBuilder()
					.withPermission(bucketName,
							Permission.BROWSE,
							Permission.READ,
							Permission.DELETE,
							Permission.PURGE,
							Permission.SEARCH,
							Permission.WRITE
//							Permission.READ_ACL,
//							Permission.WRITE_ACL
//							Permission.CHOWN,
//							Permission.PRIVILEGED
							)
					.bulid();
			tenant.changeDataAccessPermissions(localUserName2, permissions2);
		}
		
		// 通过Rest API访问HCP， 使用第二个用户登录HCP并试图上传下载删除文件
		// Using the second user account to upload/download/delete file.
		{
			String accessKey2 = DigestUtils.toBase64String(localUserName2);
			String secretKey2 = DigestUtils.calcMD5ToHex("himitu123").toLowerCase();
			HCPNamespace namespace2 = HCPClients.getInstance().newHCPClient(Account.endpoint, bucketName, accessKey2, secretKey2);
			
			// Here is the file will be uploaded into HCP
			File file = Account.localFile1;
			// The location in HCP where this file will be stored.
			String key = file.getName();
			try {
				namespace2.putObject(key, file);
				System.out.println("File uploaded by user [" + localUserName2 + "]");
	
				HCPObject obj = namespace2.getObject(key);
				File tempFile = File.createTempFile(file.getName(), "");
				StreamUtils.inputStreamToFile(obj.getContent(), tempFile, true);
				System.out.println("File downloaded by user [" + localUserName2 + "] " + tempFile.getPath());
				
//				namespace2.deleteObject(new DeleteObjectRequest(key).withPurge(true));
				namespace2.deleteObject(key);
				System.out.println("File deleted by user [" + localUserName2 + "]" );
			} catch (InvalidResponseException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// 通过Rest API访问HCP， 使用第一个用户登录HCP并试图上传文件，第一个用户为只读用户所以上传文件会导致权限异常
		// Using the first user account to upload the file. The first user just has reading permission. so there will be a permission exception
		{
			String accessKey1 = DigestUtils.toBase64String(localUserName1);
			String secretKey1 = DigestUtils.calcMD5ToHex("himitu123").toLowerCase();
			HCPNamespace namespace1 = HCPClients.getInstance().newHCPClient(Account.endpoint, bucketName, accessKey1, secretKey1);
			
			try {
				// Here is the file will be uploaded into HCP
				File file = Account.localFile1;
				// The location in HCP where this file will be stored.
				String key = file.getName() + ".11";
				System.out.println("User [" + localUserName1 + "] does not have [write] permission. So there will be exception with [Permission denied].");
				namespace1.putObject(key, file);
			} catch (InvalidResponseException e) {
				e.printStackTrace();
			} catch (HSCException e) {
				e.printStackTrace();
			}
		}
		
		// 通过S3 API访问HCP， 使用第一个用户登录HCP并试图上传文件，第一个用户为只读用户所以上传文件会导致权限异常
		// Using the first user account to upload the file. The first user just has reading permission. so there will be a permission exception
		{
			String accessKey1 = DigestUtils.toBase64String(localUserName1);
			String secretKey1 = DigestUtils.calcMD5ToHex("himitu123").toLowerCase();
			AmazonS3 namespace1 = HCPClients.getInstance().newS3Client(Account.endpoint, accessKey1, secretKey1);
			
			try {
				// Here is the file will be uploaded into HCP
				File file = Account.localFile1;
				// The location in HCP where this file will be stored.
				String key = file.getName() + ".111";
				System.out.println("User [" + localUserName1 + "] does not have [write] permission. So there will be exception with [AccessDenied].");
				namespace1.putObject(bucketName, key, file);
			} catch (AmazonServiceException e) {
				e.printStackTrace();
			} catch (SdkClientException e) {
				e.printStackTrace();
			}
		}	
		
		// You can remove user account by using deleteUserAccount
//		{
//			tenant.deleteUserAccount(localUserName1);
//			tenant.deleteUserAccount(localUserName2);
//		}
		
		System.out.println("Well done!");
	}
}
