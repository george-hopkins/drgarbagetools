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

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;


/**
 * Collection of util method.
 * 
 * @author Sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: BytecodeVisualizerUtils.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class BytecodeVisualizerUtils {
	
	public static int verifyLine(ISourceViewer sourceViewer, IJavaElement element, int caret) {
			if(element == null){
				return IClassFileEditor.INVALID_LINE;
			}
		
			if(element == null){
				return IClassFileEditor.INVALID_LINE;
			}
			
			IDocument document= sourceViewer.getDocument();
			if (document == null){
				return IClassFileEditor.INVALID_LINE;
			}

			int line = IClassFileEditor.INVALID_LINE;
			try {
				/* get current line */
				line = document.getLineOfOffset(caret);
				
				/* check if the method declaration is selected */
				if( element.getElementType() == IJavaElement.METHOD){
					IMethod method = (IMethod) element;
					int startOffset = method.getSourceRange().getOffset();
					int startLine = document.getLineOfOffset(startOffset);
					if(startLine == line){
						line = IClassFileEditor.METHOD_DECLARATION_LINE;
					}
					else {
						line++;
					}
				}

			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return line;
	}
}
