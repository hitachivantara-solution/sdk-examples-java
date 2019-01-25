package com.hitachivantara.example.hcp.content;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;

/**
 * S3 SDK方式列出版本示例
 * @author sohan
 *
 */
public class S3Example_CreateGetListVersions {

	public static void main(String[] args) throws IOException {
		AmazonS3 hs3Client = HCPClients.getInstance().getS3Client();

		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String key = "folder/subfolder/" + file.getName();
		String bucketName = Account.namespace;

		// 创建一些版本，请确认Versioning功能已经开启，否侧此处会出现异常。
		// 关于如何开启版本请参考帮助： Managing a Tenant and Its Namespaces > Managing namespaces > Configuring a namespace > Configuring object versioning
		// Create some versions of object.
		// **Make sure that [Versioning] option was enabled in HCP system.**
		{
			for (int i = 0; i < 5; i++) {
				hs3Client.putObject(bucketName, key, file);
			}
		}

		{
			try {
				// Listing versions of this object
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				VersionListing verListing = hs3Client.listVersions(bucketName, key);
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*

				// Printout objects
				List<S3VersionSummary> objs = verListing.getVersionSummaries();
				for (S3VersionSummary s3VersionSummary : objs) {
					System.out.println(s3VersionSummary.getVersionId() + "\t" + s3VersionSummary.getSize() + "\t" + s3VersionSummary.getETag() + "\t" + s3VersionSummary.getKey());
				}

				// Get specific version of object
				S3Object specificVersionOfs3Object = hs3Client.getObject(new GetObjectRequest(bucketName, key).withVersionId(objs.get(3).getVersionId()));
				// do something

			} catch (AmazonServiceException e) {
				e.printStackTrace();
				return;
			} catch (SdkClientException e) {
				e.printStackTrace();
				return;
			}
		}

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	}

}
