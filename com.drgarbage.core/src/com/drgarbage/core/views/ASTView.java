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
import java.util.List;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jface.text.ITextSelection;
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

/**
 * @author sa
 *
 */
public class ASTView extends ViewPart {
	
	private ITypeRoot activeTypeRoot;
	private IJavaElement activeJavaElement;
	private static ASTParser parser;
	
	static {
		parser = ASTParser.newParser(AST.JLS3);
	}
	
	ASTExplorer astExplorer;

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
			
//			System.out.println("post selectionChanged: " + part.getSite().getId());
//			System.out.println("post selection: " + selection);

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
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			ITextSelection textSelection = (ITextSelection) selection;			
			int position = textSelection.getOffset(); 
			try {
				IJavaElement javaElement = typeRoot.getElementAt(position);
				System.out.println("javaElement: " + javaElement);
				if(activeJavaElement == null ||activeJavaElement != javaElement){
					activeJavaElement = javaElement;
				}
				else{
					return;
				}
				
				
				if(javaElement != null && javaElement.getElementType() == IJavaElement.METHOD){
					IMethod method = (IMethod)javaElement;
					try {
						System.out.println(method.getSource());
						parser.setSource(method.getSource().toCharArray());
						parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
						ASTNode astNode = parser.createAST(null);
						
						System.out.println(astNode);
						
						createGraphFromAST((TypeDeclaration) astNode);
						
					} catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		System.out.println("setFocus");
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
	
	
	public static void createGraphFromAST(TypeDeclaration astNode){
		MethodDeclaration[] methods =  astNode.getMethods();
		if(methods.length != 1){
			//TODO: ERROR
			return;
		}

		Block block = methods[0].getBody();
		System.out.println(block);
		
		List statements = block.statements();
		for(int i = 0; i < statements.size(); i++){
			Object o = statements.get(i);
			System.out.println(o);
			Statement statement = (Statement)o;
			System.out.println(statement.getNodeType());
		}
		
	}
}
