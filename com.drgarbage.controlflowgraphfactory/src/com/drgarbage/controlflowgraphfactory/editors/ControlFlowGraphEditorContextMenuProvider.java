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

package com.drgarbage.controlflowgraphfactory.editors;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.PopupMenuExtender;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.actions.ByteCodelLayoutAlgorithmAction;
import com.drgarbage.controlflowgraphfactory.actions.ExportGraphAction;
import com.drgarbage.controlflowgraphfactory.actions.ExportAsImageAction;
import com.drgarbage.controlflowgraphfactory.actions.HierarchicalLayoutAlgorithmAction;
import com.drgarbage.controlflowgraphfactory.actions.HorizontalCenterOrderAction;
import com.drgarbage.controlflowgraphfactory.actions.HorizontalLeftOrderAction;
import com.drgarbage.controlflowgraphfactory.actions.HorizontalRightOrderAction;
import com.drgarbage.controlflowgraphfactory.actions.MaxHeightAction;
import com.drgarbage.controlflowgraphfactory.actions.MaxWidhtAction;
import com.drgarbage.controlflowgraphfactory.actions.MinHeightAction;
import com.drgarbage.controlflowgraphfactory.actions.MinWidhtAction;
import com.drgarbage.controlflowgraphfactory.actions.PrintAction;
import com.drgarbage.controlflowgraphfactory.actions.VerticalBottomOrderAction;
import com.drgarbage.controlflowgraphfactory.actions.VerticalCenterOrderAction;
import com.drgarbage.controlflowgraphfactory.actions.VerticalTopOrderAction;


/**
 * Provides context menu actions for the ControlFlowGraphEditor.
 * 
 * @author Sergej Alekseev
 * @version $Revision:118 $
 * $Id:ControlFlowGraphEditorContextMenuProvider.java 118 2007-05-21 19:35:02Z aleks $
 */
class ControlFlowGraphEditorContextMenuProvider extends ContextMenuProvider {

	/** The editor's action registry. */
	private ActionRegistry actionRegistry;
		
	/**
	 * Instantiate a new menu context provider for the specified EditPartViewer 
	 * and ActionRegistry.
	 * @param viewer	the editor's graphical viewer
	 * @param registry	the editor's action registry
	 * @throws IllegalArgumentException if registry is <code>null</code>. 
	 */
	public ControlFlowGraphEditorContextMenuProvider(EditPartViewer viewer, ActionRegistry registry) {
		super(viewer);
		if (registry == null) {
			throw new IllegalArgumentException();
		}
		actionRegistry = registry;
	}
	
	/* getAction from registry */
	private IAction getAction(String actionId) {
		return actionRegistry.getAction(actionId);
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IMenuManager#addMenuListener(org.eclipse.jface.action.IMenuListener)
     */
    public void addMenuListener(IMenuListener listener) {
    	/* Hack: supress adding of the exention points to the context menu. */
    	if(!(listener instanceof PopupMenuExtender)){
    		super.addMenuListener(listener);
    	}
    }
	
	/**
	 * Called when the context menu is about to show. Actions, 
	 * whose state is enabled, will appear in the context menu.
	 * @see org.eclipse.gef.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void buildContextMenu(IMenuManager menu) {

		/* undo/redo actions */
		menu.add(new Separator(GEFActionConstants.GROUP_UNDO));
		
		menu.appendToGroup(
				GEFActionConstants.GROUP_UNDO, // target group id
				getAction(ActionFactory.UNDO.getId())); // action to add
		menu.appendToGroup(
				GEFActionConstants.GROUP_UNDO, 
				getAction(ActionFactory.REDO.getId()));

		/* copy and paste actions  */
		menu.add(new Separator());
		IAction  action = getAction(ActionFactory.COPY.getId());
		if(action != null)
			menu.add(action);
		
		action = getAction(ActionFactory.PASTE.getId());
		if(action != null)
			menu.add(action);
		
		action = getAction(ActionFactory.CUT.getId());
		if(action != null)
			menu.add(action);

		/* edit actions */
		menu.add(new Separator());
		
		menu.add(getAction(ActionFactory.DELETE.getId()));
		menu.add(getAction(ActionFactory.SELECT_ALL.getId()));

		/* direct editing of nodes action */
		action = getAction(GEFActionConstants.DIRECT_EDIT);
		action.setDescription(ControlFlowFactoryMessages.DirectTextEditAction_Text); 
		action.setText(ControlFlowFactoryMessages.DirectTextEditAction_Description);
		menu.add(action);
		
		/* Add zoom actions to the menu */
		menu.add(new Separator());
		menu.add(getAction(GEFActionConstants.ZOOM_IN));
		menu.add(getAction(GEFActionConstants.ZOOM_OUT));
		
		/* graph layout algorithm action */
		menu.add(new Separator());
		IMenuManager submenu = new MenuManager(ControlFlowFactoryMessages.ControlFlowFactory_SubMenuGrphAlgorithms_Text);
		menu.add(submenu);
		action = getAction(ByteCodelLayoutAlgorithmAction.ID);
		if(action != null)
			submenu.add(action);
		
		action = getAction(HierarchicalLayoutAlgorithmAction.ID);
		if(action != null)
			submenu.add(action);
		
		submenu = new MenuManager(ControlFlowFactoryMessages.ControlFlowFactory_SubMenuAlignElements_Text);
		menu.add(submenu);
		action = getAction(HorizontalLeftOrderAction.ID);
		if(action != null)
			submenu.add(action);
		
		action = getAction(HorizontalCenterOrderAction.ID);
		if(action != null)
			submenu.add(action);
		
		action = getAction(HorizontalRightOrderAction.ID);
		if(action != null)
			submenu.add(action);
		
		submenu.add(new Separator());
		
		action = getAction(VerticalTopOrderAction.ID);
		if(action != null)
			submenu.add(action);
		
		action = getAction(VerticalCenterOrderAction.ID);
		if(action != null)
			submenu.add(action);
		
		action = getAction(VerticalBottomOrderAction.ID);
		if(action != null)
			submenu.add(action);
		
		/* size action */
		submenu = new MenuManager(ControlFlowFactoryMessages.ControlFlowFactory_SubMenuResizeElements_Text);
		menu.add(submenu);
		action = getAction(MinWidhtAction.ID);
		if(action != null)
			submenu.add(action);
		
		action = getAction(MaxWidhtAction.ID);
		if(action != null)
			submenu.add(action);
		
		action = getAction(MinHeightAction.ID);
		if(action != null)
			submenu.add(action);
		
		action = getAction(MaxHeightAction.ID);
		if(action != null)
			submenu.add(action);
		
		/* print action */
		menu.add(new Separator());
    	
    	/* export actions */
		action = getAction(PrintAction.ID);
		if(action != null)
			menu.add(action);
		
		action = getAction(ExportAsImageAction.ID);
		if(action != null)
			menu.add(action);

    	action = getAction(ExportGraphAction.ID);
    	if(action != null)
    		menu.add(action);

	}

}
