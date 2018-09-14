package com.hitachivantara.example.hcp.content.multipartupload;

public interface PartDateProvider {
	
	PartData nextPartData() throws Exception;

	PartData partData(int partIndex) throws Exception;
}
