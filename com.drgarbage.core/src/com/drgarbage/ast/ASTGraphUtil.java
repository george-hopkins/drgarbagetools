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

import org.eclipse.jdt.core.dom.ASTNode;
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
import org.eclipse.swt.graphics.Image;

import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;
import com.drgarbage.controlflowgraph.intf.INodeType;

/**
 * Utility methods for abstract syntax tree handling.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class ASTGraphUtil {
	
	static private Image blockImage = JavaPluginImages.DESC_OBJS_SOURCE_ATTACH_ATTRIB.createImage();

	/**
	 * Creates a graph from the selected subtree.
	 * @param treeRoot the tree item
	 * @return control flow graph
	 */
	public static IDirectedGraphExt createGraphFromASTtree(TreeModel treeRoot){
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
	private static void createGraphFromASTtree(TreeModel parent, TreeModel child, INodeListExt nodes, IEdgeListExt edges){
		
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
	
	private static void log(String s){
//		System.out.println(s);
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
			return blockImage;
		
		case ASTNode.MODIFIER:
			return JavaPluginImages.get(JavaPluginImages.IMG_FIELD_DEFAULT);
		}
		
		/* default */
		return JavaPluginImages.get(JavaPluginImages.IMG_FIELD_PRIVATE);
	}
}
