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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;
import com.drgarbage.controlflowgraph.intf.INodeType;
import com.drgarbage.core.CorePlugin;
import com.drgarbage.core.IExternalCommunication;
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
	private Tree treeControl;
	private TreeItem[] selectedItems = null;	
		
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
		this.treeControl = new Tree(this, SWT.BORDER | SWT.SINGLE | SWT.FILL);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		treeControl.setLayout(gridLayout);
		treeControl.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | 
		GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		
		treeControl.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				// select text in the referenced resource
				Tree tree = (Tree) e.widget;
				selectedItems = tree.getSelection();
				for (int i=0; i < selectedItems.length; ++i) {
					ASTNode node = (ASTNode) selectedItems[i].getData(ASTVisitorImpl.NODE);
					if (node != null) {
						editorPart.selectAndReveal(node.getStartPosition(),node.getLength());
					}
				}
			}
		});
		
		
		MenuManager mm = new MenuManager();
		
		IAction action = new Action("Generate AST tree Graph"){//TODO: define constant
			
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			public void run() {
				if(selectedItems != null && selectedItems.length == 1){
					selectedItems[0].setExpanded(true);
					IDirectedGraphExt graph = 	createGraphFromASTtree(selectedItems[0]);

					final String controlFlowFactoryID = "com.drgarbage.controlflowgraphfactory";
					
			    	IExternalCommunication comunicationObject = CorePlugin.getDefault().getExternalComunicationObject(controlFlowFactoryID);
			    	
			    	if(comunicationObject == null){
			    		/* activate target plugin */
			        	Bundle b = Platform.getBundle(controlFlowFactoryID);
			        	if(b != null){
				        	if(b.getState() != Bundle.ACTIVE){
				    	    	try {
				    				b.start();
				    			} catch (BundleException e) {
				    				CorePlugin.log(e);
				    			}
				        	}  		
				    		
				        	/* get communication object again*/
				    		comunicationObject = CorePlugin.getDefault().getExternalComunicationObject(controlFlowFactoryID);
				        	}
			    	}
			    	
			    	if(comunicationObject == null){
			    		Messages.error("OpenGraphInControlflowgraphFactoryAction_error_Opening_failed");
			    		return;
			    	}
			    	
			    	comunicationObject.generateDiagramFromGraph("AST.graph", graph);
				}
			}
		};
		mm.add(action);
		
		Menu menu = mm.createContextMenu(treeControl);
		treeControl.setMenu(menu);
		
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
	 * Creates a graph from the selected subtree.
	 * @param item the tree item
	 * @return control flow graph
	 */
	private IDirectedGraphExt createGraphFromASTtree(TreeItem item){
		IDirectedGraphExt cfg = GraphExtentionFactory.createDirectedGraphExtention();
		INodeListExt nodes = cfg.getNodeList();
		IEdgeListExt edges = cfg.getEdgeList();

		INodeExt root = GraphExtentionFactory.createNodeExtention(null);
		root.setByteCodeString(item.getText().substring(0, 8));
		root.setToolTipText(item.getText());
		root.setVertexType(INodeType.NODE_TYPE_SIMPLE);
		nodes.add(root);
		item.setData(root); /* set reference*/	
		
		for(TreeItem i: item.getItems()){
			createGraphFromASTtree(item, i, nodes, edges);
		}
		
		return cfg;
	}
		
	/**
	 * Creates graphs from the tree view.
	 * The method is called recursively.
	 * @param parent the parent of the current tree item
	 * @param item the current tree item
	 * @param nodes the list of nodes
	 * @param edges the list of edges
	 */
	private void createGraphFromASTtree(TreeItem parent, TreeItem item, INodeListExt nodes, IEdgeListExt edges){
		
		log("CR NODE: " + item.getText());	
		INodeExt node = GraphExtentionFactory.createNodeExtention(null);
		node.setByteCodeString(item.getText().substring(0, 8));
		node.setToolTipText(item.getText());
		node.setVertexType(INodeType.NODE_TYPE_SIMPLE);
		nodes.add(node);
		item.setData(node); /* set reference*/
		
		log("CR EDGE: " + parent.getText() + " -> " + item.getText());
		IEdgeExt edge = GraphExtentionFactory.createEdgeExtention((INodeExt)parent.getData(), node);
		edges.add(edge);
		
		if(item.getItemCount() == 0){
			return;
		}
		
		for(TreeItem i: item.getItems()){
			createGraphFromASTtree(item, i, nodes, edges);
		}
	}
	
	private void log(String s){
		System.out.println(s);
	}
		
	/**
	 * Sets the source for the tree control. 
	 * @param source the compilation unit object.
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public void setSource(ICompilationUnit source) throws InterruptedException, InvocationTargetException {
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
		createContent(null, source);
	}
	
	/**
	 * Clears the panel content.
	 */
	public void clearContent() {
		treeControl.removeAll();
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
							ASTVisitor visitor = new ASTVisitorImpl(treeControl,monitor);
							node.accept(visitor);	
						}
					});
			}
		});	
	}
}