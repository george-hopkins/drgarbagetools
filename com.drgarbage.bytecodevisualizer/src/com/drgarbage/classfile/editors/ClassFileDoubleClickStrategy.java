/**
 * Copyright (c) 2008-2013, Dr. Garbage Community
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

package com.drgarbage.classfile.editors;

import org.eclipse.jface.text.*;

/**
 * A text double click strategy defines the reaction of 
 * the class file editor to mouse double click events.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class ClassFileDoubleClickStrategy implements ITextDoubleClickStrategy {
	protected ITextViewer fText;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextDoubleClickStrategy#doubleClicked(org.eclipse.jface.text.ITextViewer)
	 */
	public void doubleClicked(ITextViewer part) {
		int pos = part.getSelectedRange().x;

		if (pos < 0)
			return;

		fText = part;

		selectWord(pos);
	}

	/**
	 * Selects a word in the document on the caret postion.
	 * @param caretPos position in the document
	 * @return <code>true</code> or <code>false</code>
	 */
	protected boolean selectWord(int caretPos) {

		IDocument doc = fText.getDocument();
		int startPos, endPos;

		try {

			int pos = caretPos;
			char c;

			while (pos >= 0) {
				c = doc.getChar(pos);
				if (!Character.isJavaIdentifierPart(c))
					break;
				--pos;
			}

			startPos = pos;

			pos = caretPos;
			int length = doc.getLength();

			while (pos < length) {
				c = doc.getChar(pos);
				if (!Character.isJavaIdentifierPart(c))
					break;
				++pos;
			}

			endPos = pos;
			selectRange(startPos, endPos);
			return true;

		} catch (BadLocationException x) {
		}

		return false;
	}

	/**
	 * Sets the selection to the specified range.
	 * @param startPos
	 * @param stopPos
	 */
	private void selectRange(int startPos, int stopPos) {
		int offset = startPos + 1;
		int length = stopPos - offset;
		fText.setSelectedRange(offset, length);
	}
}