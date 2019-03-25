package com.hitachivantara.example.hcp.util.multipartupload;

import java.io.PrintStream;
import java.util.UUID;

/**
 * 处理单个分片上传的线程
 * 
 * @author sohan
 *
 */
public class UploadRunnable implements Runnable {
	private final String ID = UUID.randomUUID().toString().replaceAll("-", "");
	private final PrintStream log = System.out;

	final private S3MultipartUploader uploader;
	final private PartDateProvider provider;

	public UploadRunnable(S3MultipartUploader uploader, PartDateProvider provider) {
		this.uploader = uploader;
		this.provider = provider;
	}

	@Override
	public void run() {
		PartData partData = null;
		try {
			partData = provider.nextPartData();
			while (partData != null) {
				// 上传分片数据数据
				uploader.upload(partData.getIndex(), partData.getInputStream(), partData.getSize(), " by " + ID);
				partData.close();

				partData = provider.nextPartData();
			}
		} catch (Exception e) {
			if (partData != null) {
				partData.close();
			}

			log.println("Error occured when get part data. Index:" + partData.getIndex());
			e.printStackTrace();
			return;
		}

	}
}