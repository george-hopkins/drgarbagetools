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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.IDocumentProvider;


/**
 * Interface for a Sourcecode Viewer.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public interface ISourceCodeViewer extends ISynchronizable {

	/*
	 * @see IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException;
	
	
	/**
	 * Set the new editor input.
	 * @param input the editor inout.
	 * @throws CoreException if th einput could not be set.
	 */
	public void doSetInputInternal(IEditorInput input) throws CoreException;
	
	/*
	 * @see AbstractTextEditor#createPartControl(Composite)
	 */
	public void createPartControl(Composite parent);
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#setSelection(org.eclipse.jdt.core.IJavaElement)
	 */
	public void setSelection(IJavaElement element);
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#gotoMarker(org.eclipse.core.resources.IMarker)
	 */
	public void gotoMarker(IMarker marker);
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#selectAndReveal(int, int)
	 */	
	public void selectAndReveal(int start, int length);
	
	/**
	 * Selects Source code line. If true mark the current selected line.
	 * @param line
	 */
	public void selectSourceCodeLine(int line, boolean b);

	
	/**
	 * Gets the currently selected element in the source code
	 * viewer or null.
	 * @return element or null
	 */
	public IJavaElement getCurrentSelectedElement();
	/**
	 * Sets the class file edtor reference.
	 * @param classFileEditor
	 */
	public void addClassFileEditorReference(IClassFileEditor classFileEditor);
	
	/**
	 * Returns the root class object.
	 * @return root java element
	 */
	public IType getPrimaryType();
	
	/**
	 * Returns true if the source code is displayed by this viewer, 
	 * otherwise false. 
	 * @return true or false
	 */
	public boolean isSourceCodeLoaded();
	
	/**
	 * Returns document provider of the viewer.
	 * @return document provider
	 */
	public IDocumentProvider getDocumentProvider();

	 /**
	  * Returns editor input of the viewer.
	 * @return input
	 */
	public IEditorInput getEditorInput();
	
	/**
	 * Returns the viewer's vertical ruler. May return <code>null</code> before
	 * the editor's part has been created and after disposal.
	 *
	 * @return the editor's vertical ruler which may be <code>null</code>
	 */
	public IVerticalRuler getVerticalRulerOfSourceViewer();
	
	/**
	 * @throws JavaModelException
	 */
	public void verifyInput() throws JavaModelException;
	
	/**
	 * Returns the action
	 * @param text action id
	 * @return the action
	 */
	public IAction getAction(String text);
	
}
