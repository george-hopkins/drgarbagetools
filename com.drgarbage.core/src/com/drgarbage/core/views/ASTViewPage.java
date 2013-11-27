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
package com.drgarbage.core.views;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

import com.drgarbage.ast.ASTPanel;
import com.drgarbage.core.CorePlugin;

/**
 * Abstract syntax tree view page.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class ASTViewPage extends Page {

	/**
	 * The abstract syntax tree control panel.
	 */
	private ASTPanel astPanel;       
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		astPanel = new ASTPanel(parent, SWT.NONE);
		initializeActions();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	@Override
	public Control getControl() {
		return astPanel;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#setFocus()
	 */
	@Override
	public void setFocus() {
	}

	/**
	 * Sets the page input.
	 * @param part the reference to the editor
	 */
	public void setInput(IEditorPart part) {
		ITypeRoot typeRoot = EditorUtility.getEditorInputJavaElement(part, false);
		try {
			if(typeRoot instanceof ICompilationUnit){
				astPanel.setSource((ICompilationUnit)typeRoot);
			}
			else if(typeRoot instanceof IClassFile){
				astPanel.setSource((IClassFile)typeRoot);
			}
			astPanel.setEditorPart((AbstractDecoratedTextEditor) part);
			
		} catch (InterruptedException e1) {
			CorePlugin.getDefault().getLog().log(new Status(IStatus.ERROR,CorePlugin.PLUGIN_ID, e1.getMessage(), e1));
		} catch (InvocationTargetException e2) {
			CorePlugin.getDefault().getLog().log(new Status(IStatus.ERROR,CorePlugin.PLUGIN_ID, e2.getMessage(), e2));
		}
	}
	
	/**
	 * Creates and initializes the filter actions.
	 */
	private void initializeActions() {
		
		IActionBars bars = getSite().getActionBars();
		IToolBarManager tbm = bars.getToolBarManager();
		
		IAction a = new Action("Hide package declaration", IAction.AS_CHECK_BOX) {//TODO: define constant
			public void run() {
				astPanel.hidePackageDeclaration();
				setCheckedStatus(this);
			}
		};
		a.setImageDescriptor(JavaPluginImages.DESC_OBJS_PACKDECL);
		a.setId("com.drgarbge.HIDE_PACKGE_DECL"); //TODO: define constant
		a.setToolTipText("Hide package declaration");
		tbm.add(a);
		
		a = new Action("Hide package imports", IAction.AS_CHECK_BOX) { //TODO: define constant
			public void run() {
				astPanel.hidePackageImports();
				setCheckedStatus(this);
			}
		};
		a.setImageDescriptor(JavaPluginImages.DESC_OBJS_IMPDECL);
		a.setId("com.drgarbge.HIDE_PACKGE_IMPORTS"); //TODO: define constant
		a.setToolTipText("Hide package imports");//TODO: define constant
		tbm.add(a);
		
		 a = new Action("Hide java doc items", IAction.AS_CHECK_BOX) {//TODO: define constant
			public void run() {
				astPanel.hideJavaDoc();
				setCheckedStatus(this);
			}
		};
		a.setImageDescriptor(JavaPluginImages.DESC_OBJS_JAVADOCTAG);
		a.setId("com.drgarbge.HIDE_JAVADOC"); //TODO: define constant
		a.setToolTipText("Hide java doc items");//TODO: define constant
		tbm.add(a);
		
		a = new Action("Hide fields", IAction.AS_CHECK_BOX) {//TODO: define constant
			public void run() {
				astPanel.hideFields();
				setCheckedStatus(this);
			}
		};
		a.setImageDescriptor(JavaPluginImages.DESC_FIELD_PROTECTED);
		a.setId("com.drgarbge.HIDE_FIELDS"); //TODO: define constant
		a.setToolTipText("Hide fields"); //TODO: define constant
		tbm.add(a);
		
	}
	
	/**
	 * Changes the checked status.
	 * @param a the action object
	 */
	private void setCheckedStatus(IAction a){
		if(a.isChecked()){
			a.setChecked(true);
		}
		else{
			a.setChecked(false);
		}
	}

}
