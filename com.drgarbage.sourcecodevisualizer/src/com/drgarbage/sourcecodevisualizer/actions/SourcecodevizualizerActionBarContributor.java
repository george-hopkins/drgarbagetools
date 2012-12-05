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

package com.drgarbage.sourcecodevisualizer.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;

import com.drgarbage.sourcecodevisualizer.SourcecodeVisualizerMessages;
import com.drgarbage.sourcecodevisualizer.editors.JavaCodeEditor;

/**
 * ActionBar Contributor.
 *
 * @version $Revision$
 * $Id: SourcecodevizualizerActionBarContributor.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class SourcecodevizualizerActionBarContributor extends
		BasicTextEditorActionContributor {
	
	private JavaCodeEditor textEditor = null;
	private IToolBarManager toolBarManager = null;
	private MenuManager viewMenu = null;
	
	private static String menuText = SourcecodeVisualizerMessages.Sourcecodevisualizer_Menu_Text;
	
	/*
	 * @see EditorActionBarContributor#contributeToMenu(IMenuManager)
	 */
	public void contributeToMenu(IMenuManager menu) {
		/* sourcecode visualizer  menu */
		viewMenu = new MenuManager(menuText);		
		menu.insertAfter(IWorkbenchActionConstants.M_EDIT, viewMenu);

	}

    /**
     * Contributes to the given tool bar.
     * <p>
     * The <code>EditorActionBarContributor</code> implementation of this method
     * does nothing. Subclasses may reimplement to add to the tool bar portion of
     * this contribution.
     * </p>
     *
     * @param toolBarManager the manager that controls the workbench tool bar
     */
    public void contributeToToolBar(IToolBarManager toolBarManager) {
		this.toolBarManager = toolBarManager;
    }
	
	/*
	 * @see IEditorActionBarContributor#setActiveEditor(org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);
		doSetActiveEditor(part);
	}
	
	/**
	 * Internally sets the active editor to the actions provided by this contributor.
	 * Cannot be overridden by subclasses.
	 *
	 * @param part the editor
	 */
	private void doSetActiveEditor(IEditorPart part) {
		if (part instanceof JavaCodeEditor)
			textEditor = (JavaCodeEditor) part;
		
		/* setup own action */
		setupOwnActions(textEditor);
		
	}
	
	private void setupOwnActions(JavaCodeEditor editor){
		
		viewMenu.removeAll();
		toolBarManager.removeAll();
		
		/* contribute to the top level menu */
		/* open control flow factory action */
		IAction graphFactoryAction = editor.getAction(OpenGraphInControlflowgraphFactory.ID);
		if(graphFactoryAction != null){
			viewMenu.add(graphFactoryAction);
		}

		/* add action open in control flow graph factory */		
		toolBarManager.add(new Separator());
		viewMenu.add(new Separator());
		if(graphFactoryAction!= null){
			toolBarManager.add(graphFactoryAction);
		}

		/* Other actions */
		toolBarManager.add(new Separator());
		IAction a = editor.getAction(ConstructorFilterAction.ID);
		if(a != null){
			viewMenu.add(a);
			toolBarManager.add(a);
		}

		a = editor.getAction(MethodGraphFilterAction.ID);
		if(a != null){
			viewMenu.add(a);
			toolBarManager.add(a);
		}
		
		toolBarManager.add(new Separator());
		viewMenu.add(new Separator());
		a = editor.getAction(RefreshAction.ID);
		if(a != null){
			viewMenu.add(a);
			toolBarManager.add(a);
		}
		
		toolBarManager.update(true);
	}

	
}
