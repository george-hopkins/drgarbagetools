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

package com.drgarbage.bytecodevisualizer.editors;

/**
 * Interface to the class file editor.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: IClassFileEditor.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public interface IClassFileEditor {
	
	public static int INVALID_LINE = -1;
	public static int METHOD_DECLARATION_LINE = -2;

	/**
	 * Returns the class file editor.
	 * @return 
	 */
	public BytecodeEditor getClassFileEditor();
}
