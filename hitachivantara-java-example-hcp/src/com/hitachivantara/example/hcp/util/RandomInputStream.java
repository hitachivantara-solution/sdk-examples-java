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
