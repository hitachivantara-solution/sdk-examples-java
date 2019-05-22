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