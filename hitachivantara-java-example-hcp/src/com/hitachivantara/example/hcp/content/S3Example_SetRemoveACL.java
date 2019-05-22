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
package com.hitachivantara.example.hcp.content;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CanonicalGrantee;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.Grantee;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.Permission;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;

/**
 * ACL操作示例
 * </p>
 * Example of how to use ACL
 * 
 * @author sohan
 *
 */
public class S3Example_SetRemoveACL {

	public static void main(String[] args) throws IOException {
		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String key = "example-hcp/subfolder1/" + file.getName();
		String bucketName = Account.namespace;

		// Create a file for below metadata operation.
		{
			AmazonS3 hs3Client = HCPClients.getInstance().getS3Client();
			hs3Client.putObject(bucketName, key, file);
		}

		{
			try {
				AmazonS3 hs3Client = HCPClients.getInstance().getS3Client();

				{
					AccessControlList acl = hs3Client.getObjectAcl(bucketName, key);
					List<Grant> grants = acl.getGrantsAsList();
					for (Grant grant : grants) {
						System.out.format("%s: %s\n", grant.getGrantee().getIdentifier(), grant.getPermission().toString());
					}
				}

				System.out.println("--------------------------------------------------");

				// Start to set ACL
				{
					// !!!!!!!!!!This is the id of HCP local user, Please change to your ID!!!!!!!!!!
					// user1 4f0e935f-feba-4b1e-98eb-c52872a66938
					// admin 4f0e935f-feba-48e4-98eb-c52872a66938
					CanonicalGrantee grantee = new CanonicalGrantee("4f0e935f-feba-4b1e-98eb-c52872a66938");
//					grantee.setDisplayName("user1");
					AccessControlList acl = new AccessControlList();
					acl.setOwner(new Owner("4f0e935f-feba-48e4-98eb-c52872a66938","admin"));
					acl.grantPermission(grantee, Permission.Read);
					acl.grantPermission(grantee, Permission.Write);
					acl.grantPermission(grantee, Permission.ReadAcp);
					hs3Client.setObjectAcl(bucketName, key, acl);
					System.out.println("ACL created!");
				}
				
				{
//					hs3Client.setObjectAcl(bucketName, key, CannedAccessControlList.AuthenticatedRead);
//					hs3Client.setObjectAcl(bucketName, key, CannedAccessControlList.PublicReadWrite);
				}
				
				System.out.println("--------------------------------------------------");
				
				// Print ACL again
				{
					AccessControlList acl = hs3Client.getObjectAcl(bucketName, key);
					List<Grant> grants = acl.getGrantsAsList();
					for (Grant grant : grants) {
						System.out.format("%s: %s\n", grant.getGrantee().getIdentifier(), grant.getPermission().toString());
					}
				}
				
			} catch (AmazonServiceException e) {
				e.printStackTrace();
				return;
			}

			System.out.println("Well done!");
		}
	}

}
