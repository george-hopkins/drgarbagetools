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

package com.drgarbage.core.views;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

import com.drgarbage.ast.ASTExplorer;
import com.drgarbage.core.CoreConstants;
import com.drgarbage.core.CorePlugin;

/**
 * Simple AST View
 */
public class ASTView extends ViewPart {
	
	private ITypeRoot activeTypeRoot;	
	private ASTExplorer astExplorer;

	/**
	 * Windows listener implementation.
	 */
	private IWindowListener windowListener = new IWindowListener(){

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IWindowListener#windowActivated(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowActivated(IWorkbenchWindow window) {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IWindowListener#windowClosed(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowClosed(IWorkbenchWindow window) {
			window.removePageListener(pageListener);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IWindowListener#windowDeactivated(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowDeactivated(IWorkbenchWindow window) {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IWindowListener#windowOpened(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowOpened(IWorkbenchWindow window) {
			addPageListener(window);
		}
		
	};

	/**
	 * Page listener implementation.
	 */
	private IPageListener pageListener = new IPageListener(){

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPageListener#pageActivated(org.eclipse.ui.IWorkbenchPage)
		 */
		public void pageActivated(IWorkbenchPage page) {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPageListener#pageClosed(org.eclipse.ui.IWorkbenchPage)
		 */
		public void pageClosed(IWorkbenchPage page) {
			page.removePostSelectionListener(selectionListener);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPageListener#pageOpened(org.eclipse.ui.IWorkbenchPage)
		 */
		public void pageOpened(IWorkbenchPage page) {
			addSelecionListener(page);	
		}		
	};
	
	/**
	 * Selection listener implementation.
	 */
	ISelectionListener selectionListener = new ISelectionListener(){

		/* (non-Javadoc)
		 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
		 */
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if(!(part instanceof IEditorPart)){
				return;
			}
			
			if(!(part instanceof CompilationUnitEditor) &&
					!part.getSite().getId().equals(CoreConstants.BYTECODE_VISUALIZER_EDITOR_ID)){
				return;
			}

			IEditorPart editorPart = (IEditorPart)part;
			ITypeRoot typeRoot = EditorUtility.getEditorInputJavaElement(editorPart, false);
			
			if(activeTypeRoot == null || activeTypeRoot != typeRoot){
				activeTypeRoot = typeRoot;

				try {
					if(typeRoot instanceof ICompilationUnit){
						astExplorer.setSource((ICompilationUnit)typeRoot);
					}
					else if(typeRoot instanceof IClassFile){
						astExplorer.setSource((IClassFile)typeRoot);
					}
					astExplorer.setEditorPart((AbstractDecoratedTextEditor) part);
					
				} catch (InterruptedException e1) {
					CorePlugin.getDefault().getLog().log(new Status(IStatus.ERROR,CorePlugin.PLUGIN_ID, e1.getMessage(), e1));
				} catch (InvocationTargetException e2) {
					CorePlugin.getDefault().getLog().log(new Status(IStatus.ERROR,CorePlugin.PLUGIN_ID, e2.getMessage(), e2));
				}
			}
		}

	};
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		/* create control */
		astExplorer = new ASTExplorer(parent, SWT.NONE);
		
		/* initialize listener */
		IWorkbench workbench = getSite().getPage().getWorkbenchWindow().getWorkbench();
		
		IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
		for(IWorkbenchWindow window: windows){
			addPageListener(window);
		}
		
		workbench.addWindowListener(windowListener);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		IWorkbench workbench = getSite().getPage().getWorkbenchWindow().getWorkbench();
		workbench.removeWindowListener(windowListener);
	}

	/**
	 * Adds page listener to the window.
	 * @param window
	 */
	private void addPageListener(IWorkbenchWindow window){
		window.addPageListener(pageListener);
		
		IWorkbenchPage[] pages = window.getPages();
		for(IWorkbenchPage page: pages){
			addSelecionListener(page);
		}
	}
	
	/**
	 * Adds selection listener to rhe page.
	 * @param page
	 */
	private void addSelecionListener(IWorkbenchPage page){
		page.addPostSelectionListener(selectionListener);		

	}
}
