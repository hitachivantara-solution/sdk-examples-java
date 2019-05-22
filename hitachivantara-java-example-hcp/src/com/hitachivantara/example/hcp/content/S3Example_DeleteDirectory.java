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
import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.example.hcp.util.RandomInputStream;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;

/**
 * 使用S3 SDK删除空目录
 * </p>
 * Use S3 to delete a empty directory.
 * 
 * @author sohan
 *
 */
public class S3Example_DeleteDirectory {

	public static void main(String[] args) throws IOException, InvalidResponseException, HSCException {
		// The location in HCP where this file will be stored.
		final String directoryKey = "example-hcp/subfolder" + RandomInputStream.randomInt(100, 999);
		String bucketName = Account.namespace;

		// Create an folder for delete
		{
			AmazonS3 hs3Client = HCPClients.getInstance().getS3Client();
			S3Example_CreateDirectory.createFolder(bucketName, directoryKey, hs3Client);

			boolean exist = HCPClients.getInstance().getHCPClient().doesDirectoryExist(directoryKey);
			
			System.out.println("Directory " + (exist ? "exist!" : "not exist!"));
		}

		// Delete this empty folder.
		{
			AmazonS3 hs3Client = HCPClients.getInstance().getS3Client();

			hs3Client.deleteObject(bucketName, directoryKey);

			boolean exist = HCPClients.getInstance().getHCPClient().doesDirectoryExist(directoryKey);

			System.out.println("Directory " + (!exist ? "removed!" : "failed to deleted!"));

			System.out.println("Well done!");
		}
	}

}
