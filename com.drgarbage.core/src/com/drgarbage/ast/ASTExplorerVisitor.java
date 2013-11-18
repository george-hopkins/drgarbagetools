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

import java.util.Stack;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

/**
 * The implementation of a visitor for abstract syntax trees.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 * 
 * @see ASTVisitor
 */
public class ASTExplorerVisitor extends ASTVisitor {
	
	/**
	 * The tree node data key.
	 */
	static final String NODE = "%NODE%";
	
	/**
	 * Stack structure for tree elements.
	 */
	private Stack<Widget> stack;
	
	/**
	 * The monitor object to control the progress of 
	 * AST-Tree creation. 
	 */
	private IProgressMonitor monitor;
		
	/**
	 * Constructs an AST visitor and initialize the tree object.
	 * @param treeControl
	 * @param monitor
	 */
	public ASTExplorerVisitor(Tree treeControl, IProgressMonitor m) {
		super(true);
		
		if (treeControl == null){
			throw new IllegalArgumentException();
		}
		
		stack = new Stack<Widget>();
		monitor = m;
		stack.push(treeControl);
	}
	
	/**
	 * Returns <code>false</code> if the user has canceled the
	 * operation, and <code>true</code> otherwise.
	 * 
	 * @return <code>true</code> or <code>false</code>
	 */
	private boolean isVisitChildren() {
		return !(this.monitor.isCanceled());  
	}
	
	/**
	 * Visits the given AST node prior to the type-specific visit
	 * creates a new tree item and pushes it onto the stack.
	 * 
	 * @param node the node to visit
	 * @see ASTVisitor#preVisit(ASTNode)
	 */
	public void preVisit(ASTNode node) {
		Object parent = stack.peek();
		
		TreeItem child = null;
		if (parent instanceof Tree){
			child = new TreeItem((Tree)parent, SWT.NONE);
		} else {
			child = new TreeItem((TreeItem)parent, SWT.NONE);
		}
		
		stack.push(child);
		child.setData(NODE, node);
		
		/* set text and corresponding image */
		child.setText(getNodeDescr(node));
		child.setImage(getImage(node));
	}	
	
	/**
	 * Returns an image corresponding to the AST element.
	 * 
	 * @param node the AST-node
	 * @return the image
	 */
	@SuppressWarnings("restriction")
	private Image getImage(ASTNode node){
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
	static private String getNodeDescr(ASTNode node) {		
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
	 * Visits the given AST node following the type-specific visit
	 * and pops one node from the stack.
	 * 
	 * @param node the node to visit
	 * @see ASTVisitor#postVisit(ASTNode)
	 */
	public void postVisit(ASTNode node) {
		this.stack.pop();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.AnnotationTypeDeclaration)
	 */
	public boolean visit(AnnotationTypeDeclaration node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration)
	 */
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.AnonymousClassDeclaration)
	 */
	public boolean visit(AnonymousClassDeclaration node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ArrayAccess)
	 */
	public boolean visit(ArrayAccess node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ArrayCreation)
	 */
	public boolean visit(ArrayCreation node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ArrayInitializer)
	 */
	public boolean visit(ArrayInitializer node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ArrayType)
	 */
	public boolean visit(ArrayType node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.AssertStatement)
	 */
	public boolean visit(AssertStatement node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Assignment)
	 */
	public boolean visit(Assignment node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Block)
	 */
	public boolean visit(Block node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.BlockComment)
	 */
	public boolean visit(BlockComment node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.BooleanLiteral)
	 */
	public boolean visit(BooleanLiteral node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.BreakStatement)
	 */
	public boolean visit(BreakStatement node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.CastExpression)
	 */
	public boolean visit(CastExpression node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.CatchClause)
	 */
	public boolean visit(CatchClause node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.CharacterLiteral)
	 */
	public boolean visit(CharacterLiteral node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ClassInstanceCreation)
	 */
	public boolean visit(ClassInstanceCreation node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	public boolean visit(CompilationUnit node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ConditionalExpression)
	 */
	public boolean visit(ConditionalExpression node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ConstructorInvocation)
	 */
	public boolean visit(ConstructorInvocation node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ContinueStatement)
	 */
	public boolean visit(ContinueStatement node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.DoStatement)
	 */
	public boolean visit(DoStatement node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.EmptyStatement)
	 */
	public boolean visit(EmptyStatement node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.EnhancedForStatement)
	 */
	public boolean visit(EnhancedForStatement node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.EnumConstantDeclaration)
	 */
	public boolean visit(EnumConstantDeclaration node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.EnumDeclaration)
	 */
	public boolean visit(EnumDeclaration node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ExpressionStatement)
	 */
	public boolean visit(ExpressionStatement node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.FieldAccess)
	 */
	public boolean visit(FieldAccess node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.FieldDeclaration)
	 */
	public boolean visit(FieldDeclaration node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ForStatement)
	 */
	public boolean visit(ForStatement node) {
		return isVisitChildren();
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.IfStatement)
	 */
	public boolean visit(IfStatement node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ImportDeclaration)
	 */
	public boolean visit(ImportDeclaration node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.InfixExpression)
	 */
	public boolean visit(InfixExpression node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.InstanceofExpression)
	 */
	public boolean visit(InstanceofExpression node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Initializer)
	 */
	public boolean visit(Initializer node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Javadoc)
	 */
	public boolean visit(Javadoc node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.LabeledStatement)
	 */
	public boolean visit(LabeledStatement node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.LineComment)
	 */
	public boolean visit(LineComment node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MarkerAnnotation)
	 */
	public boolean visit(MarkerAnnotation node) {
		return isVisitChildren();
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MemberRef)
	 */
	public boolean visit(MemberRef node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MemberValuePair)
	 */
	public boolean visit(MemberValuePair node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodRef)
	 */
	public boolean visit(MethodRef node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodRefParameter)
	 */
	public boolean visit(MethodRefParameter node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodDeclaration)
	 */
	public boolean visit(MethodDeclaration node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodInvocation)
	 */
	public boolean visit(MethodInvocation node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Modifier)
	 */
	public boolean visit(Modifier node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.NormalAnnotation)
	 */
	public boolean visit(NormalAnnotation node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.NullLiteral)
	 */
	public boolean visit(NullLiteral node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.NumberLiteral)
	 */
	public boolean visit(NumberLiteral node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.PackageDeclaration)
	 */
	public boolean visit(PackageDeclaration node) {
		return isVisitChildren();
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ParameterizedType)
	 */
	public boolean visit(ParameterizedType node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ParenthesizedExpression)
	 */
	public boolean visit(ParenthesizedExpression node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.PostfixExpression)
	 */
	public boolean visit(PostfixExpression node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.PrefixExpression)
	 */
	public boolean visit(PrefixExpression node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.PrimitiveType)
	 */
	public boolean visit(PrimitiveType node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.QualifiedName)
	 */
	public boolean visit(QualifiedName node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.QualifiedType)
	 */
	public boolean visit(QualifiedType node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ReturnStatement)
	 */
	public boolean visit(ReturnStatement node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SimpleName)
	 */
	public boolean visit(SimpleName node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SimpleType)
	 */
	public boolean visit(SimpleType node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SingleMemberAnnotation)
	 */
	public boolean visit(SingleMemberAnnotation node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SingleVariableDeclaration)
	 */
	public boolean visit(SingleVariableDeclaration node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.StringLiteral)
	 */
	public boolean visit(StringLiteral node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SuperConstructorInvocation)
	 */
	public boolean visit(SuperConstructorInvocation node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SuperFieldAccess)
	 */
	public boolean visit(SuperFieldAccess node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SuperMethodInvocation)
	 */
	public boolean visit(SuperMethodInvocation node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SwitchCase)
	 */
	public boolean visit(SwitchCase node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SwitchStatement)
	 */
	public boolean visit(SwitchStatement node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SynchronizedStatement)
	 */
	public boolean visit(SynchronizedStatement node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TagElement)
	 */
	public boolean visit(TagElement node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TextElement)
	 */
	public boolean visit(TextElement node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ThisExpression)
	 */
	public boolean visit(ThisExpression node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ThrowStatement)
	 */
	public boolean visit(ThrowStatement node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TryStatement)
	 */
	public boolean visit(TryStatement node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TypeDeclaration)
	 */
	public boolean visit(TypeDeclaration node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TypeDeclarationStatement)
	 */
	public boolean visit(TypeDeclarationStatement node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TypeLiteral)
	 */
	public boolean visit(TypeLiteral node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TypeParameter)
	 */
	public boolean visit(TypeParameter node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.VariableDeclarationExpression)
	 */
	public boolean visit(VariableDeclarationExpression node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.VariableDeclarationStatement)
	 */
	public boolean visit(VariableDeclarationStatement node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.VariableDeclarationFragment)
	 */
	public boolean visit(VariableDeclarationFragment node) {
		return isVisitChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.WhileStatement)
	 */
	public boolean visit(WhileStatement node) {
		return isVisitChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.WildcardType)
	 */
	public boolean visit(WildcardType node) {
		return isVisitChildren();
	}
}