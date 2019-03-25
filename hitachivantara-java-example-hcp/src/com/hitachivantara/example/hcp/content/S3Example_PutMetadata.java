package com.hitachivantara.example.hcp.content;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;

/**
 * S3 存取元数据示例
 * @author sohan
 *
 */
public class S3Example_PutMetadata {

	public static void main(String[] args) throws IOException {
		AmazonS3 hs3Client = HCPClients.getInstance().getS3Client();
		
		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String key = "example-hcp/subfolder1/" + file.getName();
		String bucketName = Account.namespace;

		{
			try {
				ObjectMetadata metadata = new ObjectMetadata();
				metadata.addUserMetadata("name", "Rison");
				metadata.addUserMetadata("company", "hitachi vantara");

				// Inject file into HCP system.
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				hs3Client.putObject(new PutObjectRequest(bucketName, key, file).withMetadata(metadata));
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*

				// Get metadata from HCP
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				ObjectMetadata metadataFromHCP = hs3Client.getObjectMetadata(bucketName, key);
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				Map<String, String> map = metadataFromHCP.getUserMetadata();
				// Verify contents.
				assertTrue("Rison".equals(map.get("name")));
				assertTrue("hitachi vantara".equals(map.get("company")));
			} catch (AmazonServiceException e) {
				e.printStackTrace();
				return;
			} catch (SdkClientException e) {
				e.printStackTrace();
				return;
			}
			
			System.out.println("Well done!");
		}
	}

}
