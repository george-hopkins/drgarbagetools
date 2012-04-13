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
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.ClassFileEditor;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jdt.internal.ui.javaeditor.IJavaEditorActionConstants;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.core.CoreMessages;


/**
 * Sourcecode Viewer for the class files.
 * 
 * @author Sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: ClassFileSourcecodeViewer.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class ClassFileSourcecodeViewer extends ClassFileEditor implements
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
	 * Indicates if the source is available.
	 */
	private boolean sourceLoaded = false;

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
	 * @see com.drgarbage.bytecodevisualizer.editors.ISourceCodeView#selectSourceCodeLine(int)
	 */
	public void selectSourceCodeLine(int line, boolean b) {

		if(getSourceViewer() == null){ /* to avoid NullPointer Exceptions */
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
	 * @see com.drgarbage.bytecodevisualizer.editors.ISourceCodeViewer#isHandleCursorPositionChanged()
	 */
	public boolean isHandleCursorPositionChanged() {
		return handleCursorPositionChanged;
	}


	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.editors.ISourceCodeViewer#setHandleCursorPositionChanged(boolean)
	 */
	public void setHandleCursorPositionChanged(boolean b) {
		handleCursorPositionChanged = b;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.editors.ISourceCodeViewer#getPrimaryType()
	 */
	public IType getPrimaryType() {
		IClassFile classFile = (IClassFile)getInputJavaElement();
		return classFile.findPrimaryType();
	}

	/**
	 * Returns the Java element wrapped by this editors input.
	 * FIX: bug#67 Ganymede: NoSuchMethodError
	 */
	protected ITypeRoot getInputJavaElement() {
		return EditorUtility.getEditorInputJavaElement(this, false);
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.editors.ISourceCodeViewer#doSetInputInternal(org.eclipse.ui.IEditorInput)
	 */
	public void doSetInputInternal(IEditorInput input) throws CoreException {
		doSetInput(input);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.ClassFileEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent){
		
		fParent = parent;
		fStackLayout = new StackLayout();
		fParent.setLayout(fStackLayout);
		
		fViewerComposite= new Composite(fParent, SWT.NONE);
		fViewerComposite.setLayout(new FillLayout());
		
		super.createPartControl(fViewerComposite);
		fStackLayout.topControl= fViewerComposite;
		fParent.layout();

		try {
			verifyInput();
		} catch (CoreException e) {
			String title= CoreMessages.ClassFileEditor_error_title;
			String message= CoreMessages.ClassFileEditor_error_message;
			ExceptionHandler.handle(e, fParent.getShell(), title, message);
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
		return sourceLoaded;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.editors.ISourceCodeViewer#getVerticalRulerOfSourceViewer()
	 */
	public IVerticalRuler getVerticalRulerOfSourceViewer() {
		return getVerticalRuler();
	}
	
	private StackLayout fStackLayout;
	private Composite fParent;
	private Composite fViewerComposite;
	private Control fSourceAttachmentForm;

	/**
	 * Checks if the class file input has no source attached. If so, a source attachment form is shown.
	 * 
	 * @param input the editor input
	 * @throws JavaModelException if an exception occurs while accessing its corresponding resource
	 */
	public void verifyInput() throws JavaModelException {
		IEditorInput input = getEditorInput();
		
		if (fParent == null || input == null)
			return;

		IClassFileEditorInput classFileEditorInput= (IClassFileEditorInput) input;
		IClassFile file= classFileEditorInput.getClassFile();
		
		IAction copyQualifiedName= getAction(IJavaEditorActionConstants.COPY_QUALIFIED_NAME);
		
		// show source attachment form if no source found
		if (file.getSourceRange() == null) {
			sourceLoaded = false;

			// dispose old source attachment form
			if (fSourceAttachmentForm != null)
				fSourceAttachmentForm.dispose();

			NoSourceViewer form= new NoSourceViewer(file, this, getSite());
			fSourceAttachmentForm= form.createControl(fParent);

			fStackLayout.topControl= fSourceAttachmentForm;
			fParent.layout();

		} else { // show source viewer
			sourceLoaded = true;

			if (fSourceAttachmentForm != null) {
				fSourceAttachmentForm.dispose();
				fSourceAttachmentForm= null;

				fStackLayout.topControl= fViewerComposite;
				fParent.layout();
			}

		}
		
	}

}
