package com.hitachivantara.example.hcp.util.multipartupload;

public interface PartDateProvider {
	
	PartData nextPartData() throws Exception;

	PartData partData(int partIndex) throws Exception;
}
