package com.hitachivantara.example.hcp.util.multipartupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 提供文件分片功能，根据指定分片大小将一个大文件分为若干小文件（FileInputstream）供分片上传
 * 
 * @author sohan
 *
 */
public class FilePartDataProvider implements PartDateProvider {

	private final File SOURCE_FILE;
	private final int PART_COUNT;
	private final long PART_SIZE;
	private final long FILE_SIZE;

	private int remainCount;
	// private long remainSize;
	private int currentIndex = 1;

	/**
	 * 文件分片Provider
	 * 
	 * @param file
	 * @param partSize
	 * @throws MulitipartUploadException
	 */
	public FilePartDataProvider(File file, long partSize) throws MulitipartUploadException {
		// Must >5M
		if (partSize < 5 * 1024 * 1024) {
			throw new MulitipartUploadException("Your proposed upload is smaller than the minimum allowed size(5M)");
		}

		this.SOURCE_FILE = file;

		this.FILE_SIZE = file.length();
		this.PART_SIZE = partSize;
		// 计算分片数量
		this.PART_COUNT = (int) (FILE_SIZE / partSize) + ((FILE_SIZE % partSize) != 0 ? 1 : 0);

		this.currentIndex = 1;
		this.remainCount = PART_COUNT;
		// this.remainSize = FILE_SIZE;
	}

	@Override
	public synchronized PartData nextPartData() throws MulitipartUploadException {
		if (remainCount <= 0) {
			return null;
		}

		PartData partData = partData(currentIndex);

		currentIndex++;
		remainCount--;
		// remainSize -= PART_SIZE;

		return partData;
	}

	@Override
	public PartData partData(int partNumber) throws MulitipartUploadException {
		if (partNumber <= 0 || partNumber > PART_SIZE) {
			throw new MulitipartUploadException("Part index out of range.");
		}

		InputStream in = null;
		long startIndex = 0;
		try {
			in = new FileInputStream(SOURCE_FILE);
			if (partNumber != 1) {
				startIndex = (partNumber - 1) * PART_SIZE;
				in.skip(startIndex);
			}
		} catch (Exception e) {
			throw new MulitipartUploadException(e);
		}

		long remainSize = FILE_SIZE - startIndex;

		long uploadPartsize = Math.min(PART_SIZE, remainSize);
		PartData partData = new PartData(partNumber, in, uploadPartsize);

		return partData;
	}

	public int getTotalPartCount() {
		return PART_COUNT;
	}

	public long getFileSize() {
		return FILE_SIZE;
	}

}
