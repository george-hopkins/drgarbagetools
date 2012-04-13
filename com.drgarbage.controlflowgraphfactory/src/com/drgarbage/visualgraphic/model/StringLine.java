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

package com.drgarbage.visualgraphic.model;

/**
 * Calculate number of lines and max line length for
 * the given string.
 * <br>
 * Typical use:<br>
 * <pre>
 * 	StringLine sl = new StringLine("Hello World\nHello World 2\nHello World 33");
 * 	int a = sl.getNumberOfLines();
 * 	int b = sl.getMaxLineLenght();
 * </pre>
 * @author Sergej Alekseev
 * @version $Revision: 1523 $ 
 * $Id: StringLine.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */

public class StringLine{

	private int numberOfLines = 1;
	private int maxLineLenght = 0;
	
	/**
	 * Creates an object and calculates the number
	 * of lines and the longest line length for
	 * the given string.
	 * @param t
	 */
	public StringLine(String t) {
		int  counterLineLength = 1;
		if(t != null){
			char[] ca = t.toCharArray();

			for(char c: ca){
				counterLineLength++;
				if(c == 10){
					numberOfLines++;
					if(counterLineLength > maxLineLenght){
						maxLineLenght = counterLineLength; 
					}
					counterLineLength = 1;
				}
			}
		}
	}

	/**
	 * Returns the number of lines in the given string.
	 * @return numberOFLines
	 */
	public int getNumberOfLines() {
		return numberOfLines;
	}

	/**
	 * Returns the length of the longest line 
	 * in the given string. 
	 * @return max line length
	 */
	public int getMaxLineLenght() {
		return maxLineLenght;
	}
	
}