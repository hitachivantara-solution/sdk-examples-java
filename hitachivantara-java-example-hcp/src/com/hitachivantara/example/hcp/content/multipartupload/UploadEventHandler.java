package com.hitachivantara.example.hcp.content.multipartupload;

public interface UploadEventHandler {
//	void partUploaded(int partNumber, long size, long startTime, long endTime);

//	void complete(String key, long size, long startTime, long endTime);

	void init(String bucketName, String objectPath, String uploadId);

	void beforePartUpload(String bucketName, String objectPath, String uploadId, int partNumber, long uploadPartsize, long startTime);

	void caughtPartUploadException(String bucketName, String objectPath, String uploadId, int partNumber, long uploadPartsize, Exception e);

	void afterPartUpload(String bucketName, String objectPath, String uploadId, int partNumber, long uploadPartsize, long startTime, long endTime);

	void complete(String bucketName, String objectPath, String uploadId, Long uploadedSize, long startTime, long endTime);
}
