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

package com.drgarbage.bytecodevisualizer.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.bytecodevisualizer.editors.BytecodeEditor;
import com.drgarbage.bytecodevisualizer.editors.IClassFileEditor;

/**
 * ActionBar Contributor.
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: BytecodevizualizerActionBarContributor.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class BytecodevizualizerActionBarContributor extends BasicTextEditorActionContributor{
	
	private BytecodeEditor textEditor = null;
	private IToolBarManager toolBarManager = null;
	private MenuManager viewMenu = null;
	private MenuManager graphViewMenu = null;

	/**
	 * The find actions.
	 */
	private RetargetTextEditorAction fFindNext;
	private RetargetTextEditorAction fFindPrevious;
	private RetargetTextEditorAction fGotoLine;
	
	private static final String BUNDLE_FOR_CONSTRUCTED_KEYS= "org.eclipse.ui.texteditor.ConstructedEditorMessages";//$NON-NLS-1$
	private static ResourceBundle fgBundleForConstructedKeys= ResourceBundle.getBundle(BUNDLE_FOR_CONSTRUCTED_KEYS);
	
	/**
	 * Own actions
	 */	
	
	/*
	 * @see EditorActionBarContributor#contributeToMenu(IMenuManager)
	 */
	public void contributeToMenu(IMenuManager menu) {
		IMenuManager editMenu= menu.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
		if (editMenu != null) {
			//additional find actions
			fFindNext = new RetargetTextEditorAction(fgBundleForConstructedKeys, "Editor.FindNext."); //$NON-NLS-1$
			fFindNext.setActionDefinitionId(IWorkbenchActionDefinitionIds.FIND_NEXT);
			editMenu.prependToGroup(IWorkbenchActionConstants.FIND_EXT, fFindNext);
			
			fFindPrevious = new RetargetTextEditorAction(fgBundleForConstructedKeys, "Editor.FindPrevious."); //$NON-NLS-1$
			fFindPrevious.setActionDefinitionId(IWorkbenchActionDefinitionIds.FIND_PREVIOUS);
			editMenu.prependToGroup(IWorkbenchActionConstants.FIND_EXT, fFindPrevious);
			
			fGotoLine= new RetargetTextEditorAction(fgBundleForConstructedKeys, "Editor.GotoLine."); //$NON-NLS-1$
			fGotoLine.setActionDefinitionId(ITextEditorActionDefinitionIds.LINE_GOTO);
			//editMenu.prependToGroup(IWorkbenchActionConstants.FIND_EXT, fGotoLine);
		}

		IMenuManager navigateMenu= menu.findMenuUsingPath(IWorkbenchActionConstants.M_NAVIGATE);
		if (navigateMenu != null) {
			navigateMenu.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, fGotoLine);
		}
		
		/* bytecode menu */
		viewMenu = new MenuManager(BytecodeVisualizerMessages.BytecodevizualizerActionBar_menu_Bytecode);		
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
		if (part instanceof IClassFileEditor)
			textEditor = ((IClassFileEditor) part).getClassFileEditor();
		
		if(textEditor == null){
			return;
		}

		/* The global actions to be connected with editor actions */
		IActionBars actionBars= getActionBars();
		
		/* setup own action */
		setupOwnActions(textEditor);
		
		/* edit actions */
		actionBars.setGlobalActionHandler(ITextEditorActionConstants.COPY, getAction(textEditor, ITextEditorActionConstants.COPY));
		
		/* select actions */
		actionBars.setGlobalActionHandler(ITextEditorActionConstants.SELECT_ALL, getAction(textEditor, ITextEditorActionConstants.SELECT_ALL));
		
		/* find actions */
		actionBars.setGlobalActionHandler(ITextEditorActionConstants.FIND, getAction(textEditor, ITextEditorActionConstants.FIND));
		fFindNext.setAction(getAction(textEditor, ITextEditorActionConstants.FIND_NEXT));
		fFindPrevious.setAction(getAction(textEditor, ITextEditorActionConstants.FIND_PREVIOUS));
		fGotoLine.setAction(getAction(textEditor, ITextEditorActionConstants.GOTO_LINE));
		
		/* bookmark and task actions */
		//actionBars.setGlobalActionHandler(IDEActionFactory.ADD_TASK.getId(), getAction(textEditor, IDEActionFactory.ADD_TASK.getId()));
		//actionBars.setGlobalActionHandler(IDEActionFactory.BOOKMARK.getId(), getAction(textEditor, IDEActionFactory.BOOKMARK.getId()));

		/* print actions */
		actionBars.setGlobalActionHandler(ITextEditorActionConstants.PRINT, getAction(textEditor, ITextEditorActionConstants.PRINT));
	}
	
	private void setupOwnActions(BytecodeEditor editor){
		/* contribute to the toplevel menu */
		viewMenu.removeAll();
		
		/* open control flow factory action */
		IAction graphFactoryAction = editor.getAction(ExportGraphAndOpenWithControlflowgraphFactoryAction.ID);
		if(graphFactoryAction != null)
			viewMenu.add(graphFactoryAction);
		
		/* graph view submenu */
		graphViewMenu = new MenuManager(BytecodeVisualizerMessages.BytecodevizualizerActionBar_contextMenu_Graph_View);
		viewMenu.add(graphViewMenu);

		/* radio action */
		IAction a = editor.getAction(ActivateBytecodeGraphViewAction.ID);
		IAction a2 = editor.getAction(ActivateBasicblockGraphViewAction.ID);

		/* contribute to the menu */
		graphViewMenu.removeAll();	
		if(a != null)
			graphViewMenu.add(a);
		if(a2 != null)
			graphViewMenu.add(a2);
		
		/* About action */
		/* not desired anymore */
		
		/* contribute to the toolbar */
		toolBarManager.removeAll();
		if(a!= null)
			toolBarManager.add(a);
		if(a2 != null)
			toolBarManager.add(a2);

		/* add action open in control flow graph factory */
		toolBarManager.add(new Separator());
		if(graphFactoryAction!= null)
			toolBarManager.add(graphFactoryAction);
		
		toolBarManager.update(true);
	}
}
