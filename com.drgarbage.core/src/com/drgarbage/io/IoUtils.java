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

import java.io.File;

public class IoUtils {
	
	public static String getFilenameWithoutExtension(String filename) {
		int directoryDelimiterIndex = filename.lastIndexOf(File.separatorChar);
		if (directoryDelimiterIndex == filename.length() -1) {
			/* filename ends with delimiter */
			return filename;
		}
		else {
			int dotIndex = -1;
			if (directoryDelimiterIndex >= 0) {
				/* filename contains a delimiter */
				dotIndex = filename.substring(directoryDelimiterIndex + 1)
					.lastIndexOf(FileExtensions.EXTENSION_DELIMITER);
				if (dotIndex >= 0){
					dotIndex += directoryDelimiterIndex + 1;
				}
			}
			else {
				/* filename does not contain a delimiter */
				dotIndex = filename.lastIndexOf(FileExtensions.EXTENSION_DELIMITER);
			}
			
			if (dotIndex == 0) {
				return "";
			}
			if (dotIndex > 0) {
				/* filename contains a dot */
				return filename.substring(0, dotIndex);
			}
			else {
				/* filename does not contain a dot */
				return filename;
			}
			
		}
	}
	
	public static String setExtension(String filename, String extensionWihoutDot) {
		return getFilenameWithoutExtension(filename) + FileExtensions.EXTENSION_DELIMITER + extensionWihoutDot;
	}
	
	public static String toFilter(String extensionWithoutDot) {
		return new StringBuilder(extensionWithoutDot.length() + 2)
			.append(FileExtensions.WILD_CHAR_ASTERISK)
			.append(FileExtensions.EXTENSION_DELIMITER)
			.append(extensionWithoutDot)
			.toString();
	}
	
	public static String[] toFilters(String[] extensionsWithoutDot) {
		return toFilters(extensionsWithoutDot, false);
	}
	public static String[] toFilters(String[] extensionsWithoutDot, boolean appendAsteriskFilter) {
		int newSize = appendAsteriskFilter ? extensionsWithoutDot.length + 1 : extensionsWithoutDot.length;
		String[] result = new String[newSize];
		for (int i = 0; i < extensionsWithoutDot.length; i++) {
			result[i] = toFilter(extensionsWithoutDot[i]);
		}
		if (appendAsteriskFilter) {
			result[newSize -1] = toFilter(String.valueOf(FileExtensions.WILD_CHAR_ASTERISK));
		}
		return result;
	}
	
	
	public static void main(String[] args) {
		test("/root/something");
		test("/ro.ot/something");
		test("/a/b/");
		test("/");
		test(".");
		test("/.");
		test("");
		test("/12.45/7.");
		test("/12.45/7.9");
		test("/12.45");
		test("012.45");
		test("/12345/789");
	}
	
	private static void test(String filename) {
		System.out.println("getFilenameWithoutExtension('"+ filename +"') = '" + getFilenameWithoutExtension(filename)+"'");
	}
}
