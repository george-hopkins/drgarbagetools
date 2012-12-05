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

import java.util.List;

import org.eclipse.jdt.internal.debug.ui.console.JavaStackTraceHyperlink;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

import com.drgarbage.asm.render.intf.IClassFileDocument;
import com.drgarbage.asm.render.intf.IInstructionLine;
import com.drgarbage.asm.render.intf.IMethodSection;
import com.drgarbage.bytecode.ByteCodeConstants;

/**
 * Bytecode document.
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: BytecodeDocument.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class BytecodeDocument extends Document {
	
	private BytecodeDocumentProvider documentProvider;

	public BytecodeDocument(BytecodeDocumentProvider documentProvider) {
		super();
		this.documentProvider = documentProvider;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.IDocument#getLineInformation(int)
	 */
	public IRegion getLineInformation(int line) throws BadLocationException {
		
		/* to avoid BadLocation exception */
		if (line < 0) {
			return new Region(0, 0);			
		}
		
		if (invokedFromJavaStackTraceHyperlink(3)) {
			/* lookup line number table */
			
			IClassFileDocument doc = documentProvider.getClassFileDocument();
			if (doc != null) {
				IInstructionLine il = doc.findInstructionLine(line);
				if (il != null) {
					IRegion result = getTracker().getLineInformation(il.getLine());
					return result;
				}
			}
		}
		else {
			/* default handling */
			return getTracker().getLineInformation(line);
		}
		return new Region(0, 0);			
	}
	
	private boolean invokedFromJavaStackTraceHyperlink(int position) {
		Thread thread = Thread.currentThread();
		
		StackTraceElement[] elements = thread.getStackTrace();
		if (elements != null && elements.length > position) {
			return JavaStackTraceHyperlink.class.getName().equals(elements[position].getClassName());
		}
		
		return false;
		
	}
	
}
