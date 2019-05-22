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

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.define.ACLDefines.ACLPermission;
import com.hitachivantara.hcp.standard.model.metadata.AccessControlList;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadata;
import com.hitachivantara.hcp.standard.model.metadata.S3CompatibleMetadata;
import com.hitachivantara.hcp.standard.model.request.impl.DeleteObjectRequest;
import com.hitachivantara.hcp.standard.model.request.impl.PutMetadataRequest;
import com.hitachivantara.hcp.standard.util.MetadataUtils;

/**
 * ACL操作示例
 * </p>
 * Example of how to use ACL
 * 
 * @author sohan
 *
 */
public class RestExample_SetRemoveACL {

	public static void main(String[] args) throws IOException {
		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String key = "example-hcp/subfolder1/" + file.getName();

		// Create a file for below metadata operation.
		{
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();
				hcpClient.putObject(key, file);
			} catch (HSCException e) {
				e.printStackTrace();
			}
		}

		{
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				boolean hasACL = hcpClient.doesObjectACLExist(key);
				System.out.println(hasACL ? "Object has ACL" : "Object does not has ACL");
				if (hasACL) {
					hcpClient.deleteObjectACL(key);

					hasACL = hcpClient.doesObjectACLExist(key);

					if (hasACL) {
						System.out.println("Object ACL failed to removed.");
						return;
					} else {
						System.out.println("Object ACL removed.");
					}
				}

				AccessControlList acl = new AccessControlList();
				acl.grantPermissionToUser("user1", ACLPermission.DELETE, ACLPermission.READ, ACLPermission.WRITE);
				acl.grantPermissionToUser("user2", ACLPermission.READ);

				// Support other operations:
				// acl.grantPermissionToAllUsers(permissions);
				// acl.grantPermissionToAuthenticatedUsers(permissions);
				// acl.grantPermissionToGroup(groupName, domain, permissions);
				// acl.grantPermissionToUser(userName, domain, permissions);

				// Add current acl to key
				hcpClient.addObjectACL(key, acl);
				// Reset exist acl to current acl.
				// hcpClient.setObjectACL(key, acl);

				hasACL = hcpClient.doesObjectACLExist(key);
				System.out.println(hasACL ? "Object ACL granted" : "Failed to add ACL.");

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
