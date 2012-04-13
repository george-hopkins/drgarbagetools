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

package com.drgarbage.dot;

import java.io.IOException;

public class DotUtils {
	/**
	 * 
	 * An ID is one of the following:
	 * 
	 * # Any string of alphabetic ([a-zA-Z\200-\377]) characters, underscores
	 * ('_') or digits ([0-9]), not beginning with a digit;
	 * 
	 * # a number [-]?(.[0-9]+ | [0-9]+(.[0-9]*)? ); # any double-quoted string
	 * ("...") possibly containing escaped quotes (\")1;
	 * 
	 * # an HTML string (<...>).
	 * 
	 * In quoted strings in DOT, the only escaped character is double-quote
	 * ("). That is, in quoted strings, the dyad \" is converted to "; all other
	 * characters are left unchanged. In particular, \\ remains \\. Layout
	 * engines may apply additional escape sequences.
	 * 
	 * @param id
	 * @param out
	 * @throws IOException
	 */
	public static void appendEscapedLabel(String id, Appendable out)
			throws IOException {
		for (int i = 0; i < id.length(); i++) {
			char ch = id.charAt(i);
			if (ch == DotLexicalConstants.QUOTE) {
				out.append(DotLexicalConstants.BACKSLASH);
			}
			out.append(ch);
		}
	}

	public static void appendQuotedLabel(String id, Appendable out)
			throws IOException {
		out.append(DotLexicalConstants.QUOTE);
		appendEscapedLabel(id, out);
		out.append(DotLexicalConstants.QUOTE);
	}
	
	public static String toHexColor(int r, int g, int b) {
		return String.format("" + DotLexicalConstants.HASH_MARK + "%02X%02X%02X", r, g, b);
	}

}
