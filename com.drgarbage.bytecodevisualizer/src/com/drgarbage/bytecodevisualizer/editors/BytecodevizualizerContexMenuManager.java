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

package com.drgarbage.bytecodevisualizer.editors;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.bytecodevisualizer.actions.ActivateBasicblockGraphViewAction;
import com.drgarbage.bytecodevisualizer.actions.ActivateBytecodeGraphViewAction;
import com.drgarbage.bytecodevisualizer.actions.ExportGraphAndOpenWithControlflowgraphFactoryAction;

/**
 * Context Menu Manager.
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: BytecodevizualizerContexMenuManager.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class BytecodevizualizerContexMenuManager extends MenuManager {

	private BytecodeEditor editor = null;
	
	public BytecodevizualizerContexMenuManager(BytecodeEditor editor){
		this.editor = editor;
		buildContextMenu();
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IMenuManager#addMenuListener(org.eclipse.jface.action.IMenuListener)
     */
    public void addMenuListener(IMenuListener listener) {
    	/* Hack: supress adding of the exention points to the context menu.
    	 * To allow extentions delete comment in the next line. */
    	//super.addMenuListener(listener);
    }
    
    /* (non-Javadoc)
     * build context menu, add actions.
     */
    protected void buildContextMenu() {
		/* own actions */
    	setupOwnActions();
		
    	add(new Separator());

    	/*
    	ITextEditorActionConstants.UNDO,
    	ITextEditorActionConstants.REDO,
    	ITextEditorActionConstants.CUT,
    	ITextEditorActionConstants.COPY,
    	ITextEditorActionConstants.PASTE,
    	ITextEditorActionConstants.DELETE,
    	ITextEditorActionConstants.SELECT_ALL,
    	ITextEditorActionConstants.FIND,
    	ITextEditorActionConstants.PRINT,
    	ITextEditorActionConstants.PROPERTIES,
    	ITextEditorActionConstants.REVERT
    	*/
    	/* edit actions */
    	IAction action = editor.getAction(ActionFactory.COPY.getId());
    	action.setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
    	add(action);
    	
    	action = editor.getAction(ITextEditorActionConstants.SELECT_ALL);
    	add(action);

    	add(new Separator());
    	
    	//find actions
    	action = editor.getAction(ITextEditorActionConstants.FIND);
    	add(action);
    	action = editor.getAction(ITextEditorActionConstants.FIND_NEXT);
    	add(action);
    	action = editor.getAction(ITextEditorActionConstants.FIND_PREVIOUS);
    	add(action);
    	action = editor.getAction(ITextEditorActionConstants.GOTO_LINE);
    	add(action);

    	add(new Separator());
    	
    	/* print actions */
    	action = editor.getAction(ITextEditorActionConstants.PRINT);
    	action.setImageDescriptor(WorkbenchImages.getImageDescriptor("IMG_ETOOL_PRINT_EDIT"));
    	add(action);

    }

	private void setupOwnActions(){
		/* open control flow factory action */
		IAction graphFactoryAction = editor.getAction(ExportGraphAndOpenWithControlflowgraphFactoryAction.ID);
		if(graphFactoryAction != null)
			add(graphFactoryAction);

		/* bytecode, basicblock view actions as submenu */
		IAction a = editor.getAction(ActivateBytecodeGraphViewAction.ID);
		IAction a2 = editor.getAction(ActivateBasicblockGraphViewAction.ID);
		MenuManager viewMenu = new MenuManager(BytecodeVisualizerMessages.BytecodevizualizerActionBar_contextMenu_Graph_View);
    	add(viewMenu);
    	if(a != null)
    		viewMenu.add(a);
    	if(a2 != null)
    		viewMenu.add(a2);
	}
}
