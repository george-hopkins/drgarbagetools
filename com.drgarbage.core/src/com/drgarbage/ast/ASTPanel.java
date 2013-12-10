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

package com.drgarbage.ast;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.core.CorePlugin;
import com.drgarbage.core.IExternalCommunication;
import com.drgarbage.core.img.CoreImg;
import com.drgarbage.utils.Messages;

/**
 * Parses a source file and creates a tree control.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class ASTPanel extends Composite {
	
	/**
	 * AST Eclipse parser.
	 */
	private final ASTParser parser = ASTParser.newParser(AST.JLS4);
	
	private AbstractDecoratedTextEditor editorPart;

	private TreeViewer treeViewer;
	private TreeModel selectedModelElement = null;	
	
	private String treeName = "ASTtree";
	
	/**
	 * Constructs a control panel with a tree object..
	 * @param parent parent a composite control which will be the 
	 *               parent of the new instance (cannot be null)
	 * @param style the style of control to construct
	 */
	public ASTPanel(Composite parent,int style) {
		super(parent,style);		

		GridLayout gridLayout = new GridLayout();
		super.setLayout(gridLayout);
	
		treeViewer = new TreeViewer(this, SWT.BORDER | SWT.SINGLE | SWT.FILL);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;		
		treeViewer.getControl().setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | 
				GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			public void selectionChanged(SelectionChangedEvent event) {
				/* select text in the referenced resource */
				ISelection sel = event.getSelection();
				if(sel instanceof TreeSelection){
					TreeSelection ts = (TreeSelection) sel;
					Object o = ts.getFirstElement();
					if(o != null){
						selectedModelElement = (TreeModel) o;
						ASTNode node = selectedModelElement.getNode();
						if (node != null) {
							editorPart.selectAndReveal(node.getStartPosition(),node.getLength());
						}
					}
				}
			}
		});
		
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setLabelProvider(new ILabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
			 */
			public void addListener(ILabelProviderListener listener) {	
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
			 */
			public void dispose() {
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
			 */
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
			 */
			public void removeListener(ILabelProviderListener listener) {
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
			 */
			public Image getImage(Object element) {
				ASTNode n;
				if(element instanceof TreeModel){
					TreeModel tm = (TreeModel)element;
					n = tm.getNode();
				}
				else {
					n = (ASTNode)element;
				}
				Image i = ASTGraphUtil.getImage(n);
				return i;
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
			 */
			public String getText(Object element) {
				TreeModel tm = (TreeModel) element;
				ASTNode n = tm.getNode();
				String s = ASTGraphUtil.getNodeDescr(n);
				return s;
			}

		});
				
		MenuManager mm = new MenuManager();
		
		IAction action = new Action(CoreMessages.ASTView_Action_Generate_AST_Graph){
			
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			public void run() {
				if(selectedModelElement != null){
					IDirectedGraphExt graph = 	ASTGraphUtil.createGraphFromASTtree(selectedModelElement);

			    	IExternalCommunication comunicationObject =  CorePlugin.getExternalCommunication();
			    	if(comunicationObject == null){
			    		String msg = CoreMessages.ERROR_Opening_Graph_in_CFGF_failed
			    				+ '\n'
			    				+ CoreMessages.ERROR_CFGF_is_not_installed;
			    		Messages.error(msg);
			    		return;
			    	}
			    	
			    	String fileName = treeName + "-ast-tree.graph";
			    	comunicationObject.generateDiagramFromGraph(fileName, graph);
				}
			}
		};
		action.setImageDescriptor(CoreImg.astview_new_16x16);
		mm.add(action);
		Menu menu = mm.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
	}
	
	
	/**
	 * Simple implementation of the Tree content provider.
	 * @see {@link ITreeContentProvider}
	 */
	public class TreeContentProvider implements ITreeContentProvider {
		/**
		 * 
		 */
		List<Integer> hiddenItems = new ArrayList<Integer>();
		
		/**
		 * TODO: describe method
		 * @param e
		 */
		public void hide(int e){
			hiddenItems.add(e);
		}
		
		/**
		 * TODO: describe method
		 * @param e
		 */
		public void show(int e){
			if(hiddenItems.contains(e)){
				hiddenItems.remove((Integer) e);
			}
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement) {
			List<Object> objects = new ArrayList<Object>();
			List<TreeModel> children = ((TreeModel)parentElement).getChildren();
			for(TreeModel t: children){
				if( !hiddenItems.contains(t.getNode().getNodeType()) ){
					objects.add(t);
				}
			}
			return objects.toArray();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object arg0) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element) {
			if (element instanceof TreeModel){
				return ((TreeModel)element).getChildren().size() != 0;
			}

			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object nodes) {
			return getChildren(nodes);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}

	}
	
	
	/**
	 * Sets the active editor part reference.
	 * 
	 * @param ep the editor part
	 */
	public void setEditorPart(AbstractDecoratedTextEditor ep) {
		editorPart = ep;
	}
		
	/**
	 * Sets the source for the tree control. 
	 * @param source the compilation unit object.
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public void setSource(ICompilationUnit source) throws InterruptedException, InvocationTargetException {
		treeName = source.getElementName();
		createContent(source, null);
	}
	
	/**
	 * Sets the source for the tree control.
	 * @param source the class file object.
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public void setSource(IClassFile source) throws InterruptedException, InvocationTargetException {
		treeName = source.getElementName();
		createContent(null, source);
	}
	
	/**
	 * Clears the panel content.
	 */
	public void clearContent() {
		treeViewer.setInput(null);
	}
	
	/**
	 * Creates content for the tree viewer.
	 * @param compilationUnit the compilation unit object (may be <code>null</code>).
	 * @param classFile the class file object (may be <code>null</code>).
	 * 
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	private void createContent(final ICompilationUnit compilationUnit, final IClassFile classFile) throws InvocationTargetException, InterruptedException{
		clearContent();
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
		dialog.run(true, true, new IRunnableWithProgress() {
			public void run(final IProgressMonitor monitor) throws InvocationTargetException {
					
				parser.setKind(ASTParser.K_COMPILATION_UNIT);
				if (monitor.isCanceled()) return;
				
				if(compilationUnit != null){
					parser.setSource(compilationUnit);
					if (monitor.isCanceled()) return;
				}
				else{
					parser.setSource(classFile);
					if (monitor.isCanceled()) return;
				}
				
					final CompilationUnit node = (CompilationUnit) parser.createAST(monitor);
					if (monitor.isCanceled()) return;
					getDisplay().syncExec(new Runnable() {
						public void run() {
							TreeModel t = new TreeModel();
							ASTVisitorImpl visitor = new ASTVisitorImpl(t, monitor);
							node.accept(visitor);
							treeViewer.setInput(t);
						}
					});
			}
		});	
	}

	/**
	 * Hides the package declarations.
	 */
	public void hidePackageDeclaration(boolean hidden) {
		if(hidden) {
			((TreeContentProvider)treeViewer.getContentProvider()).hide(ASTNode.PACKAGE_DECLARATION);
		}
		else {
			((TreeContentProvider)treeViewer.getContentProvider()).show(ASTNode.PACKAGE_DECLARATION);
		}
		treeViewer.refresh();
	}

	/**
	 * Hides the package imports.
	 */
	public void hidePackageImports(boolean hidden) {
		if(hidden) {
			((TreeContentProvider)treeViewer.getContentProvider()).hide(ASTNode.IMPORT_DECLARATION);
		}
		else {
			((TreeContentProvider)treeViewer.getContentProvider()).show(ASTNode.IMPORT_DECLARATION);
		}
		treeViewer.refresh();
	}

	/**
	 * Hides java doc items.
	 */
	public void hideJavaDoc(boolean hidden) {
		if(hidden) {
			((TreeContentProvider)treeViewer.getContentProvider()).hide(ASTNode.JAVADOC);
		}
		else {
			((TreeContentProvider)treeViewer.getContentProvider()).show(ASTNode.JAVADOC);
		}
		treeViewer.refresh();
	}

	/**
	 * Hide Fields.
	 */
	public void hideFields(boolean hidden) {
		if(hidden) {
			((TreeContentProvider)treeViewer.getContentProvider()).hide(ASTNode.FIELD_DECLARATION);
		}
		else {
			((TreeContentProvider)treeViewer.getContentProvider()).show(ASTNode.FIELD_DECLARATION);
		}
		treeViewer.refresh();
	}

}