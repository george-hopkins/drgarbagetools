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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;


/**
 * Sourcecode Viewer for the class files.
 * 
 * @author Sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: JavaSourceCodeViewer.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class JavaSourceCodeViewer extends CompilationUnitEditor implements
		ISourceCodeViewer {

	/**
	 * The flag which indicates if the cursor update handler 
	 * is enabled or disabled.
	 */
	private boolean handleCursorPositionChanged = true;

	/**
	 * Reference to the class file editor.
	 */
	private IClassFileEditor classFileEditorRef;
	
	/**
	 * Indicates if the source code is available.
	 */
	private boolean sourceCodeAvailable = false;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#handleCursorPositionChanged()
	 */
	protected void handleCursorPositionChanged() {
		if(handleCursorPositionChanged){
			super.handleCursorPositionChanged();

			/* get current position */
			StyledText styledText= getSourceViewer().getTextWidget();
			int caret= widgetOffset2ModelOffset(getSourceViewer(), styledText.getCaretOffset());

			/* get element at the current position */
			IJavaElement element = getElementAt(caret);

			int line = BytecodeVisualizerUtils.verifyLine(getSourceViewer(), element, caret);

			if(classFileEditorRef != null){
				classFileEditorRef.getClassFileEditor().setSelection(element, line, false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.editors.ISourceCodeViewer#selectSourceCodeLine(int)
	 */
	public void selectSourceCodeLine(int line, boolean b) {
		if(getSourceViewer() == null){
			return;
		}
		
		IDocument document= getSourceViewer().getDocument();
		if (document == null){
			return;
		}

		try {
			int offset= document.getLineOffset(line); /* changed to 0-based */
			ISourceViewer sourceViewer = getSourceViewer();
			
			if (sourceViewer == null)
				return;

			int len = 0;
			if(b){
				len = document.getLineLength(line) - 1;
			}
				
			this.selectAndReveal(offset, len);

		} catch (BadLocationException e) {/*nothing to do */}
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.editors.ISourceCodeViewer#addClassFileEditorReference(com.drgarbage.bytecodevisualizer.core.editors.IClassFileEditor)
	 */
	public void addClassFileEditorReference(IClassFileEditor classFileEditor) {
		classFileEditorRef = classFileEditor;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.editors.ISynchronizable#isHandleCursorPositionChanged()
	 */
	public boolean isHandleCursorPositionChanged() {
		return handleCursorPositionChanged;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.editors.ISynchronizable#setHandleCursorPositionChanged(boolean)
	 */
	public void setHandleCursorPositionChanged(boolean b) {
		handleCursorPositionChanged = b;
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.editors.ISourceCodeViewer#getPrimaryType()
	 */
	public IType getPrimaryType(){
		ICompilationUnit unit= (ICompilationUnit)getInputJavaElement();
		if(unit == null){
			return null;
		}

		return unit.findPrimaryType();
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.editors.ISourceCodeViewer#doSetInputInternal(org.eclipse.ui.IEditorInput)
	 */
	public void doSetInputInternal(IEditorInput input) throws CoreException {
		doSetInput(input);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent){
		if(getEditorInput() == null){
			/* show source attachment form if no source found */
			NoSourceViewer form= new NoSourceViewer(null, this, getSite());
			form.createControl(parent);
		}
		else{
			super.createPartControl(parent);
			sourceCodeAvailable = true;
		}
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.editors.ISourceCodeViewer#getCurrentSelectedElement()
	 */
	public IJavaElement getCurrentSelectedElement() {

		/* get current position */
		StyledText styledText= getSourceViewer().getTextWidget();
		int caret= widgetOffset2ModelOffset(getSourceViewer(), styledText.getCaretOffset());

		/* get element at the current position */
		return  getElementAt(caret);
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.editors.ISourceCodeViewer#isSourceCodeAvaliable()
	 */
	public boolean isSourceCodeLoaded() {
		return sourceCodeAvailable;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#isEditable()
	 */
	public boolean isEditable() {
		/*FIX: bug# 71 - The source in the source tab of the bytecode editor editable but not savable*/
		return false;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.editors.ISourceCodeViewer#getVerticalRulerOfSourceViewer()
	 */
	public IVerticalRuler getVerticalRulerOfSourceViewer() {
		return getVerticalRuler();
	}

	public void verifyInput() throws JavaModelException {
		/* hopefully nothing to do */
	}
}
