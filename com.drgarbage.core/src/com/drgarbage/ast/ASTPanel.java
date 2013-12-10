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
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
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

	private TreeViewer treeViewer;
	private TreeModel selectedModelElement = null;	
	
	@SuppressWarnings("unused")
	private ICompilationUnit sourceCompilationUnit = null;
	@SuppressWarnings("unused")
	private IClassFile sourceClassFile = null;
	
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
				Image i = ASTPanel.getImage(n);
				return i;
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
			 */
			public String getText(Object element) {
				TreeModel tm = (TreeModel) element;
				ASTNode n = tm.getNode();
				String s = getNodeDescr(n);
				return s;
			}
			
		});
				
		MenuManager mm = new MenuManager();
		
		IAction action = new Action("Generate AST tree Graph"){//TODO: define constant
			
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			public void run() {
				if(selectedModelElement != null){
					IDirectedGraphExt graph = 	createGraphFromASTtree(selectedModelElement);

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
			    	
			    	comunicationObject.generateDiagramFromGraph("AST.graph", graph);//TODO: use the class name
				}
			}
		};
//		action.setImageDescriptor(newImage); //TODO: create a new image
		mm.add(action);
		Menu menu = mm.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
	}
	
	
	/**
	 * Simple implementation of the Tree content provider.
	 * @see {@link ITreeContentProvider}
	 */
	public class TreeContentProvider implements ITreeContentProvider {
		List<Integer> hiddenItems = new ArrayList<Integer>();
		
		public void hide(int e){
			hiddenItems.add(e);
		}
		
		public void show(int e){
			if(hiddenItems.contains(e)) hiddenItems.remove((Integer) e);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement) {
			
			List<Object> objects = new ArrayList<Object>();
			List<TreeModel>children = ((TreeModel)parentElement).getChildren();
			for(TreeModel t: children){
				if( !hiddenItems.contains(t.getNode().getNodeType()) ){
					objects.add(t);
				}
			}
			return objects.toArray();
			
			/*
			if (parentElement instanceof TreeModel){
				return ((TreeModel)parentElement).getChildren().toArray();
			}

			return new Object[0];
			*/
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
	 * Creates a graph from the selected subtree.
	 * @param treeRoot the tree item
	 * @return control flow graph
	 */
	private IDirectedGraphExt createGraphFromASTtree(TreeModel treeRoot){
		IDirectedGraphExt cfg = GraphExtentionFactory.createDirectedGraphExtention();
		INodeListExt nodes = cfg.getNodeList();
		IEdgeListExt edges = cfg.getEdgeList();

		INodeExt root = GraphExtentionFactory.createNodeExtention(null);
		ASTNode n = treeRoot.getNode();
		String s = getNodeDescr(n);
		root.setByteCodeString(s.substring(0, 8));
		root.setToolTipText(s);
		root.setVertexType(INodeType.NODE_TYPE_SIMPLE);
		nodes.add(root);
		treeRoot.setData(root); /* set reference*/	
		
		for(TreeModel i: treeRoot.getChildren()){
			createGraphFromASTtree(treeRoot, i, nodes, edges);
		}
		
		return cfg;
	}
		
	/**
	 * Creates graphs from the tree view.
	 * The method is called recursively.
	 * @param parent the parent of the current tree item
	 * @param child the current tree item
	 * @param nodes the list of nodes
	 * @param edges the list of edges
	 */
	private void createGraphFromASTtree(TreeModel parent, TreeModel child, INodeListExt nodes, IEdgeListExt edges){
		
		ASTNode n = child.getNode();
		String s = getNodeDescr(n);
		
		log("CR NODE: " + s);	
		INodeExt node = GraphExtentionFactory.createNodeExtention(null);
		node.setByteCodeString(s.substring(0, 8));
		node.setToolTipText(s);
		node.setVertexType(INodeType.NODE_TYPE_SIMPLE);
		nodes.add(node);
		child.setData(node); /* set reference*/
		
		ASTNode p = parent.getNode();
		String ps = getNodeDescr(p);
		
		log("CR EDGE: " + ps + " -> " + s);
		IEdgeExt edge = GraphExtentionFactory.createEdgeExtention((INodeExt)parent.getData(), node);
		edges.add(edge);
		
		if(child.getChildren().size() == 0){
			return;
		}
		
		for(TreeModel i: child.getChildren()){
			createGraphFromASTtree(child, i, nodes, edges);
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
		sourceCompilationUnit = source;
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
		sourceClassFile = source;
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
	
	
	/**
	 * Returns an image corresponding to the AST element.
	 * 
	 * @param node the AST-node
	 * @return the image
	 */
	@SuppressWarnings("restriction")
	public static Image getImage(ASTNode node){
		switch(node.getNodeType()){
		case ASTNode.COMPILATION_UNIT:
			return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CUNIT);
			
		case ASTNode.PACKAGE_DECLARATION:
			return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_PACKDECL);
			
		case ASTNode.IMPORT_DECLARATION:
			return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_IMPDECL);
			
		case ASTNode.TYPE_DECLARATION:
			return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CLASS);
		
		case ASTNode.ANNOTATION_TYPE_DECLARATION:
			return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_ANNOTATION);
			
		case ASTNode.ANONYMOUS_CLASS_DECLARATION:
			return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_INNER_CLASS_DEFAULT);
			
		case ASTNode.ENUM_DECLARATION:
			return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_ENUM);
			
		case ASTNode.FIELD_DECLARATION:
		case ASTNode.ENUM_CONSTANT_DECLARATION:
			return JavaPluginImages.get(JavaPluginImages.IMG_FIELD_PROTECTED);
			
		case ASTNode.METHOD_DECLARATION:
			return JavaPluginImages.get(JavaPluginImages.IMG_MISC_PUBLIC);
			
		case ASTNode.JAVADOC:
			return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_JAVADOCTAG);
		
		case ASTNode.VARIABLE_DECLARATION_STATEMENT:
			return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_LOCAL_VARIABLE);
		
		case ASTNode.BLOCK:
			//TODO: use registry for the image
			return JavaPluginImages.DESC_OBJS_SOURCE_ATTACH_ATTRIB.createImage();
//			return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_SOURCE_ATTACH_ATTRIB);
		
		case ASTNode.MODIFIER:
			return JavaPluginImages.get(JavaPluginImages.IMG_FIELD_DEFAULT);
		}
		
		/* default */
		return JavaPluginImages.get(JavaPluginImages.IMG_FIELD_PRIVATE);
	}
	
	/**
	 * Returns nodes description.
	 * 
	 * @param node the AST-node
	 * @return description string
	 */
	public static String getNodeDescr(ASTNode node) {		
		StringBuffer elementDescr = new StringBuffer(node.getClass().getSimpleName());
		elementDescr.append(": ");
		
		int nodeType = node.getNodeType();
		switch(nodeType){
			case ASTNode.COMPILATION_UNIT:
				CompilationUnit cu = (CompilationUnit)node;
				elementDescr.append(cu.getJavaElement().getElementName());
				break;
				
			case ASTNode.PACKAGE_DECLARATION:
				PackageDeclaration pd = (PackageDeclaration)node;
				elementDescr.append(pd.getName());
				break;
				
			case ASTNode.TYPE_DECLARATION:
				TypeDeclaration td = (TypeDeclaration)node;
				appendModifiers(td.getModifiers(), elementDescr);
				elementDescr.append(" class ");
				elementDescr.append(td.getName());
				break;
				
			case ASTNode.METHOD_DECLARATION:
				MethodDeclaration md = (MethodDeclaration)node;				
				appendModifiers(md.getModifiers(), elementDescr);
				elementDescr.append(md.getReturnType2() == null?
						"" : md.getReturnType2().toString());
				elementDescr.append(' ');
				elementDescr.append(md.getName());
				elementDescr.append("()");
				break;
				
			case ASTNode.BLOCK:
				elementDescr.append("{...}");
				break;
			
			case ASTNode.IF_STATEMENT:
				IfStatement is = (IfStatement) node;
				elementDescr.append("if( ");
				elementDescr.append(is.getExpression().toString());
				elementDescr.append(")");
				break;
				
			case ASTNode.FOR_STATEMENT:
				ForStatement fs = (ForStatement) node;
				elementDescr.append("for (...; ");
				elementDescr.append(fs.getExpression().toString());
				elementDescr.append("; ...){...}");
				break;
			
			case ASTNode.WHILE_STATEMENT:
				WhileStatement ws = (WhileStatement) node;
				elementDescr.append("while ( ");
				elementDescr.append(ws.getExpression().toString());
				elementDescr.append("){...}");
				break;
				
			case ASTNode.DO_STATEMENT:
				DoStatement ds = (DoStatement) node;
				elementDescr.append("do {...} while (");
				elementDescr.append(ds.getExpression().toString());
				elementDescr.append(")");
				break;
				
			case ASTNode.LABELED_STATEMENT:
				LabeledStatement ls = (LabeledStatement) node;
				elementDescr.append(ls.getLabel().toString());
				elementDescr.append(":");
				break;
				
			case ASTNode.CATCH_CLAUSE:
				CatchClause cs = (CatchClause) node;
				elementDescr.append("catch (");
				elementDescr.append(cs.getException().toString());
				elementDescr.append("){...}");
				break;
			
			case ASTNode.SWITCH_STATEMENT:
				SwitchStatement ss = (SwitchStatement) node;
				elementDescr.append("switch (");
				elementDescr.append(ss.getExpression().toString());
				elementDescr.append("){...}");
				break;
				
			case ASTNode.SWITCH_CASE:
				SwitchCase sc = (SwitchCase) node;
				elementDescr.append("case ");
				elementDescr.append(sc.getExpression() == null? 
						"default" : sc.getExpression().toString());
				elementDescr.append(":");
				break;
			case ASTNode.JAVADOC:
			case ASTNode.BLOCK_COMMENT:
			case ASTNode.LINE_COMMENT:
			case ASTNode.TRY_STATEMENT:
				/* nothing to do */
				break;
				
			default:
					elementDescr.append(node.toString());
		}

		/* cut the string if it is too long */
		String str = elementDescr.toString();
		if(str.length() > 128){
			str = str.substring(0, 128) + " ... "; 
		}
		str = str.replaceAll("\n", "");
		return str;
	}
	
	private static void appendModifiers(int mod, StringBuffer buf){
		if (Modifier.isAbstract(mod)){
			buf.append(Modifier.ModifierKeyword.ABSTRACT_KEYWORD.toString());
			buf.append(' ');
		}
		
		if (Modifier.isFinal(mod)){
			buf.append(Modifier.ModifierKeyword.FINAL_KEYWORD.toString());
			buf.append(' ');
		}
		
		if (Modifier.isNative(mod)){
			buf.append(Modifier.ModifierKeyword.NATIVE_KEYWORD.toString());
			buf.append(' ');
		}
		
		if (Modifier.isPrivate(mod)){
			buf.append(Modifier.ModifierKeyword.PRIVATE_KEYWORD.toString());
			buf.append(' ');
		}
		
		if (Modifier.isProtected(mod)){
			buf.append(Modifier.ModifierKeyword.PROTECTED_KEYWORD.toString());
			buf.append(' ');
		}
		
		if (Modifier.isPublic(mod)){
			buf.append(Modifier.ModifierKeyword.PUBLIC_KEYWORD.toString());
			buf.append(' ');
		}
		
		if (Modifier.isStatic(mod)){
			buf.append(Modifier.ModifierKeyword.STATIC_KEYWORD.toString());
			buf.append(' ');
		}
		
		if (Modifier.isStrictfp(mod)){
			buf.append(Modifier.ModifierKeyword.STRICTFP_KEYWORD.toString());
			buf.append(' ');
		}
		
		if (Modifier.isSynchronized(mod)){
			buf.append(Modifier.ModifierKeyword.SYNCHRONIZED_KEYWORD.toString());
			buf.append(' ');
		}
		
		if (Modifier.isTransient(mod)){
			buf.append(Modifier.ModifierKeyword.TRANSIENT_KEYWORD.toString());
			buf.append(' ');
		}
		
		if (Modifier.isVolatile(mod)){
			buf.append(Modifier.ModifierKeyword.VOLATILE_KEYWORD.toString());
			buf.append(' ');
		}
	}
}