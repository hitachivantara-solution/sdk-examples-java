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
import java.io.PrintStream;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.ex.ParseException;
import com.hitachivantara.common.util.DigestUtils;
import com.hitachivantara.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.example.hcp.util.multipartupload.MulitipartUploadException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.api.ObjectParser;
import com.hitachivantara.hcp.standard.api.event.PartialHandlingListener;
import com.hitachivantara.hcp.standard.internal.FileWriteHandler;
import com.hitachivantara.hcp.standard.model.HCPObject;
import com.hitachivantara.hcp.standard.model.request.impl.GetObjectRequest;
import com.hitachivantara.hcp.standard.model.request.impl.MultipartDownloadRequest;

/**
 * 演示通过HCP SDK分片下载大文件，分片下载大文件可提显著升带宽利用率，但本地磁盘性能要求较高
 * 
 * @author sohan
 *
 */
public class RestExample_HCPMultipartDownload {
	public static final PrintStream console = System.out;

	public static void main(String[] args) throws MulitipartUploadException, HSCException, InterruptedException, IOException {

		// PREPARE TEST DATA ----------------------------------------------------------------------
		// 下载至本地的文件
		final String f1path = "C:\\temp\\anyconnect-win-4.7.01076-predeploy-k9.zip-1";
		// 下载至本地的文件（验证用，实际开发时请勿添加）
		final String f2path = "C:\\temp\\anyconnect-win-4.7.01076-predeploy-k9.zip-2";

		File file1 = new File(f1path);
		if (file1.exists()) {
			file1.delete();
		}

		// 测试前请上传一个大文件最小50MB
		String key = "hcp-test/anyconnect-win-4.7.01076-predeploy-k9.zip";

		HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

		final CountDownLatch latch = new CountDownLatch(2);
		// PREPARE TEST DATA ----------------------------------------------------------------------

		// EXEC TEST FUNCTION ---------------------------------------------------------------------
		final long b = Calendar.getInstance().getTimeInMillis();
		MultipartDownloadRequest request = new MultipartDownloadRequest(key)
				// 配置启用分片下载最小的文件大小，此处设置100MB，也就是说需要下载的文件>=100MB才启用分片下载否则直接下载。
				// 此参数最小50MB
				.withMinimumObjectSize(1024 * 1024 * 100)
				// 指定分片数量
				.withParts(3)
				// 是否等待结束
				.withWaitForComplete(false);

		FileWriteHandler handler = new FileWriteHandler(file1);
		// 是否覆盖本地即存文件
		handler.setOverrideLocalFile(true);
		// 对于分片上传的文件无法验证内容，请设置为false
		handler.setVerifyContent(false);
		// 配置下载监听
		handler.setListener(new PartialHandlingListener() {
			double size = 0;

			public void catchException(HSCException e) {
				e.printStackTrace();
			}

			public void completed() {
				long e = Calendar.getInstance().getTimeInMillis();
				double time = e - b;

				double mbs = (size / 1024 / 1024) / (time / 1000);
				console.println("completed speed=" + mbs + "MB/s " + mbs * 8 + "Mbps/s");

				latch.countDown();
			}

			public void partCompleted(int partNumber, long beginOffset, long length) {
				size += length;
				console.println("partCompleted= " + partNumber + " " + beginOffset + " " + ((double) length) / 1024 / 1024);
			}

			public void outProgress(int id, long seekOffset, long length) {
				// console.println("progress=" + id + " " + seekOffset + " " + length);
			}
		});
		// 执行下载
		hcpClient.getObject(request, handler);
		// EXEC TEST FUNCTION ---------------------------------------------------------------------

		latch.countDown();
		// 等待结束
		latch.await();

		// 此处校验刚刚分片下载的数据是否与普通下载的数据一致，（此段代码为示例性代码，演示正确性。实际开发时请勿添加）
		// RESULT VERIFICATION --------------------------------------------------------------------
		File file2 = hcpClient.getObject(new GetObjectRequest(key), new ObjectParser<File>() {

			@Override
			public File parse(HCPObject object) throws ParseException {
				File file2 = new File(f2path);
				if (file2.exists()) {
					file2.delete();
				}
				try {
					StreamUtils.inputStreamToFile(object.getContent(), file2, true);
				} catch (IOException e) {
					throw new ParseException(e);
				}
				return file2;
			}

		});
		// DigestUtils.isMd5Equals(file1, file2);
		String file1_Md5 = DigestUtils.format2Hex(DigestUtils.calcMD5(file1));
		String file2_Md5 = DigestUtils.format2Hex(DigestUtils.calcMD5(file2));

		System.out.println("file1_Md5=" + file1_Md5);
		System.out.println("file2_Md5=" + file2_Md5);

		// RESULT VERIFICATION --------------------------------------------------------------------
	}

}
