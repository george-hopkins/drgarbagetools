/**
 * Copyright (c) 2008-2012, Dr. Garbage Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.drgarbage.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class InputableByteArrayOutputStream extends ByteArrayOutputStream {

	public InputableByteArrayOutputStream() {
	}

	public InputableByteArrayOutputStream(int size) {
		super(size);
	}
	
	public ByteArrayInputStream createInputStream() {
		return new ByteArrayInputStream(buf, 0, size());
	}
	
}
