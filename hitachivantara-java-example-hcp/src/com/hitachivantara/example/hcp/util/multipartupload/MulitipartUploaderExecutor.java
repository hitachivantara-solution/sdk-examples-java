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
package com.hitachivantara.example.hcp.util.multipartupload;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.amazonaws.services.s3.AmazonS3;

public class MulitipartUploaderExecutor {
	private final S3MultipartUploader uploader;
	private final FilePartDataProvider provider;

	public MulitipartUploaderExecutor(AmazonS3 s3Client, String bucketName, String objectPath, File uploadFile, int partSize) throws MulitipartUploadException {
		this(s3Client, bucketName, objectPath, uploadFile, partSize, null);
	}

	public MulitipartUploaderExecutor(AmazonS3 s3Client, String bucketName, String objectPath, File uploadFile, int partSize, String uploadId) throws MulitipartUploadException {
		this.provider = new FilePartDataProvider(uploadFile, partSize);
		this.uploader = new S3MultipartUploader(s3Client, bucketName, objectPath, provider.getFileSize(), uploadId);
	}

	/**
	 * 多线程上传
	 * 
	 * @param threadCount
	 */
	public void multiThreadUpload(final int threadCount, UploadEventHandler handler) {
		uploader.setHandler(handler);

		uploader.init();

		final int count = Math.min(provider.getTotalPartCount(), threadCount);

		// 多线程上传-线程池
		final ThreadPoolExecutor TP = new ThreadPoolExecutor(threadCount, threadCount + 10, 5, TimeUnit.SECONDS, new SynchronousQueue<Runnable>()) {
			private Integer runningThreadCount = count;

			@Override
			protected void afterExecute(Runnable r, Throwable t) {
				synchronized (runningThreadCount) {
					runningThreadCount--;
					if (runningThreadCount == 0) {
						try {
							uploader.autocomplete();
						} catch (MulitipartUploadException e) {
							e.printStackTrace();
						}
						this.shutdown();
					}
				}
			}
		};

		for (int i = 0; i < count; i++) {
			TP.execute(new UploadRunnable(uploader, provider));
		}
	}

	/**
	 * 单独上传某一块
	 * 
	 * @param partNumber
	 * @throws MulitipartUploadException
	 */
	public void uploadPart(final int partNumber) throws MulitipartUploadException {
		PartData partData = null;
		try {
			partData = provider.partData(partNumber);

			if (partData != null) {
				uploader.upload(partData.getIndex(), partData.getInputStream(), partData.getSize(), "");
			}
		} finally {
			if (partData != null) {
				partData.close();
			}
		}
	}

	public void complete() throws MulitipartUploadException {
		uploader.complete();
	}

}
