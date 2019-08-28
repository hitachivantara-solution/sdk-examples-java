package com.hitachivantara.example.hcp.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import org.apache.http.entity.ContentType;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.util.DateUtils;
import com.hitachivantara.common.util.DigestUtils;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.define.HashAlgorithm;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.api.MultipartUpload;
import com.hitachivantara.hcp.standard.model.HCPObject;
import com.hitachivantara.hcp.standard.model.PartETag;
import com.hitachivantara.hcp.standard.model.request.impl.MultipartUploadRequest;

/**
 * 演示如何通过HCP SDK分片上传大文件
 * 分片上传单个分片最小5MB，建议100MB，如果文件小于5GB不建议使用分片上传
 * @author sohan
 *
 */
public class RestExample_HCPMultipartUpload {
	public static void main(String[] args) throws IOException, HSCException, NoSuchAlgorithmException {
		//！！！！！！！！！时间偏移--测试用！！！！！！！！！！
		DateUtils.setTimeOffset(-43193*1000);

		HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();
		// 测试用大文件（**分片上传文件应为大文件至少500MB以上，小文件不建议使用分片方式上传**）
		final File file = new File("D:\\Downloads\\Soft\\anyconnect-win-4.7.01076-predeploy-k9.zip");
		// 上传key
		final String key = "hcp-test1/" + file.getName();

		if(hcpClient.doesObjectExist(key)) {
			hcpClient.deleteObject(key);
		}

		String uploadId = null;
		// 获得分片上传实例
		MultipartUploadRequest request = new MultipartUploadRequest(key);
		final MultipartUpload api = hcpClient.getMultipartUpload(request);

		// 初始化分片上传
		uploadId = api.initiate();

		System.out.println("key=" + key);
		System.out.println("uploadId=" + uploadId);

		final List<PartETag> partETags = Collections.synchronizedList(new ArrayList<PartETag>());

		// 计算本地要上传的文件的MD5（此处为示例性代码，实际开发不需要）
		final String orgMd5 = DigestUtils.format2Hex(DigestUtils.calcMD5(file));

		final long length = file.length();
		// 指定分片大小此处为8MB，分片上传单个分片最小5MB，建议100MB，如果文件小于5GB不建议使用分片上传
		final long partLength = 1024 * 1024 * 8; // Min 5M
		long remainLength = length;
		long startOffset = 0;

		int index = 1;
		final Queue<long[]> parts = new LinkedList<long[]>();
		while (remainLength > 0) {
			long uploadLength = Math.min(remainLength, partLength);

			parts.add(new long[] { index++, startOffset, uploadLength });

			startOffset += (uploadLength + 0);
			remainLength -= uploadLength;
		}
		final int partsSize = parts.size();

		CountDownLatch latch = new CountDownLatch(partsSize);

		for (int i = 0; i < partsSize; i++) {
			final int id = i;
			new Thread(new Runnable() {
				public void run() {
					InputStream in = null;
					try {
						long[] part = null;
						synchronized (parts) {
							part = parts.poll();
						}

						if (part != null) {
							in = new FileInputStream(file);
							in.skip(part[1]);

							System.out.println("Uploading Part... " + id);
							PartETag etag = api.uploadPart((int) part[0], in, part[2]).getPartETag();
							System.out.println("etag=" + etag.getPartNumber() + " " + etag.getETag());
							partETags.add(etag);
							System.out.println("Part... " + id+" Done");
						} else {
							System.out.println("null");
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (IOException e) {
							}
						}
					}

					latch.countDown();
				}

			}// .run();
			).start();
		}

		try {
			// 等待上传结束
			latch.await();
			
			// 合并分片完成分片上传
			api.complete(partETags);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 此处校验刚刚上传的数据是否与本地一致，（此段代码为示例性代码，演示上传正确性。实际开发时请勿添加）
		// RESULT VERIFICATION --------------------------------------------------------------------
		try {
			HCPObject obj = hcpClient.getObject(key);
			String destMd5 = DigestUtils.format2Hex(DigestUtils.calcMD5(obj.getContent()));

			System.out.println("orgMd5=" + orgMd5 + "Length=" + file.length());
			System.out.println("desMd5=" + destMd5 + "Length=" + obj.getSize());
		} catch (HSCException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// RESULT VERIFICATION --------------------------------------------------------------------
	}
}
