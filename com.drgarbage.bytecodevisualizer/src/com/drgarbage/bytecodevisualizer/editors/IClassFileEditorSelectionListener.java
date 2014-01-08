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
 * Interface for Line Selection Listener.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public interface IClassFileEditorSelectionListener {

	/**
	 * Line Selection event. The method can be null if
	 * The current line is not within a valid method. 
	 * @param newLine 
	 * @param o
	 */
	//FIXME check if all implementors see that newLine is 0-based
	public void lineSelectionChanged(int newLine, Object o);
}
