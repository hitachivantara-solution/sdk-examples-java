package com.hitachivantara.example.hcp.util.multipartupload;

import java.io.IOException;
import java.io.InputStream;

public class PartData {
	private int index;
	private InputStream in;
	private long size;

	public PartData(int index, InputStream in, long size) {
		super();
		this.index = index;
		this.in = in;
		this.size = size;
	}

	public int getIndex() {
		return index;
	}

	public InputStream getInputStream() {
		return in;
	}

	public long getSize() {
		return size;
	}

	public void close() {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}