package com.hitachivantara.example.hcp.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class RandomInputStream extends InputStream {
	private final static Random rand = new Random();
	private int length;

	private byte rangeB = -128;
	private byte rangeE = 127;

	public RandomInputStream(int length) {
		this.length = length;
	}
	
	public RandomInputStream(byte start, byte end, int length) {
		this.length = length;
		this.rangeB = start;
		this.rangeE = end;
	}

	@Override
	public int read() throws IOException {
		if (length <= 0) {
			return -1;
		}

		length--;

		return randomInt(rangeB, rangeE);
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (length <= 0) {
			return -1;
		}

		final int genlen = Math.min(len, length);
		for (int i = 0; i < genlen; i++) {
			b[i] = (byte) randomInt(rangeB, rangeE);
		}
		length -= genlen;

		return genlen;
	}

	public static int randomInt(int from, int to) {
		return rand.nextInt(to - from + 1) + from;
	}

}
