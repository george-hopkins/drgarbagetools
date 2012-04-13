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

import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.internal.WorkbenchImages;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.actions.ByteCodelLayoutAlgorithmAction;
import com.drgarbage.controlflowgraphfactory.actions.ExportAsImageAction;
import com.drgarbage.controlflowgraphfactory.actions.ExportGraphAction;
import com.drgarbage.controlflowgraphfactory.actions.HierarchicalLayoutAlgorithmAction;
import com.drgarbage.controlflowgraphfactory.actions.HorizontalCenterOrderAction;
import com.drgarbage.controlflowgraphfactory.actions.HorizontalLeftOrderAction;
import com.drgarbage.controlflowgraphfactory.actions.HorizontalRightOrderAction;
import com.drgarbage.controlflowgraphfactory.actions.MaxHeightAction;
import com.drgarbage.controlflowgraphfactory.actions.MaxWidhtAction;
import com.drgarbage.controlflowgraphfactory.actions.MinHeightAction;
import com.drgarbage.controlflowgraphfactory.actions.MinWidhtAction;
import com.drgarbage.controlflowgraphfactory.actions.OrderAbstractAction;
import com.drgarbage.controlflowgraphfactory.actions.PrintAction;
import com.drgarbage.controlflowgraphfactory.actions.SellectAllAction;
import com.drgarbage.controlflowgraphfactory.actions.VerticalBottomOrderAction;
import com.drgarbage.controlflowgraphfactory.actions.VerticalCenterOrderAction;
import com.drgarbage.controlflowgraphfactory.actions.VerticalTopOrderAction;
import com.drgarbage.controlflowgraphfactory.img.ControlFlowFactoryResource;

/**
 * Contributes actions to a toolbar and top level menu.
 * This class is tied to the editor in the definition of editor-extension (see plugin.xml).
 * 
 * @author Sergej Alekseev
 * @version $Revision:118 $
 * $Id:ControlFlowGraphEditorActionBarContributor.java 118 2007-05-21 19:35:02Z aleks $
 */
public class ControlFlowGraphEditorActionBarContributor extends ActionBarContributor {

	/*
	 * @see IEditorActionBarContributor#setActiveEditor(org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);

		/* set active editor for graph layout algorithms actions */
		ByteCodelLayoutAlgorithmAction a = (ByteCodelLayoutAlgorithmAction)getAction(ByteCodelLayoutAlgorithmAction.ID);
		a.setActiveEditor((ControlFlowGraphEditor)part);

		HierarchicalLayoutAlgorithmAction a2 = (HierarchicalLayoutAlgorithmAction)getAction(HierarchicalLayoutAlgorithmAction.ID);
		a2.setActiveEditor((ControlFlowGraphEditor)part);
		
		/* set active editor for vertex order action */
		OrderAbstractAction oaa = (OrderAbstractAction)getAction(HorizontalLeftOrderAction.ID);
		oaa.setActiveEditor((ControlFlowGraphEditor)part);

		oaa = (OrderAbstractAction)getAction(HorizontalRightOrderAction.ID);
		oaa.setActiveEditor((ControlFlowGraphEditor)part);
		
		oaa = (OrderAbstractAction)getAction(HorizontalCenterOrderAction.ID);
		oaa.setActiveEditor((ControlFlowGraphEditor)part);
		
		oaa = (OrderAbstractAction)getAction(VerticalTopOrderAction.ID);
		oaa.setActiveEditor((ControlFlowGraphEditor)part);
		
		oaa = (OrderAbstractAction)getAction(VerticalCenterOrderAction.ID);
		oaa.setActiveEditor((ControlFlowGraphEditor)part);
		
		oaa = (OrderAbstractAction)getAction(VerticalBottomOrderAction.ID);
		oaa.setActiveEditor((ControlFlowGraphEditor)part);
		
		/* size actions */
		MaxHeightAction maxHa = (MaxHeightAction)getAction(MaxHeightAction.ID);
		maxHa.setActiveEditor((ControlFlowGraphEditor)part);
		
		MinHeightAction minHa = (MinHeightAction)getAction(MinHeightAction.ID);
		minHa.setActiveEditor((ControlFlowGraphEditor)part);
		
		MaxWidhtAction maxWa = (MaxWidhtAction)getAction(MaxWidhtAction.ID);
		maxWa.setActiveEditor((ControlFlowGraphEditor)part);
		
		MinWidhtAction minWa = (MinWidhtAction)getAction(MinWidhtAction.ID);
		minWa.setActiveEditor((ControlFlowGraphEditor)part);
	
		/* set active editor for export actions */
		PrintAction p = (PrintAction) getAction(PrintAction.ID);
		p.setActiveEditor((ControlFlowGraphEditor)part);
		
		ExportAsImageAction a3 = (ExportAsImageAction)getAction(ExportAsImageAction.ID);
		a3.setActiveEditor((ControlFlowGraphEditor)part);
		
		ExportGraphAction a4 = (ExportGraphAction)getAction(ExportGraphAction.ID);
		a4.setActiveEditor((ControlFlowGraphEditor)part);

	}
	
	/**
	 * Create actions managed by this contributor.
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
	 */
	protected void buildActions() {
	
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		addRetargetAction((RetargetAction) ActionFactory.COPY.create(window));
		addRetargetAction((RetargetAction) ActionFactory.PASTE.create(window));
		addRetargetAction((RetargetAction) ActionFactory.CUT.create(window));
		
		/* undo/redo actions */
		addRetargetAction(new UndoRetargetAction());
		addRetargetAction(new RedoRetargetAction());
		
		/* edit actions */
		addRetargetAction(new DeleteRetargetAction());
		addRetargetAction(new SellectAllAction());
		
		/* Zoom Actions */
		ZoomInRetargetAction zoomIn = new ZoomInRetargetAction();
		zoomIn.enableAccelerator(true);
		addRetargetAction(zoomIn);
		
		addRetargetAction(new ZoomOutRetargetAction());
		
		/* print action */
		//addRetargetAction(new DefaultPrintAction());
		addRetargetAction(new PrintAction());
		
		/* graph layout algorithm actions */
		addRetargetAction(new ByteCodelLayoutAlgorithmAction());
		addRetargetAction(new HierarchicalLayoutAlgorithmAction());
		
		/* vertex order actions */
		addRetargetAction(new HorizontalLeftOrderAction());
		addRetargetAction(new HorizontalCenterOrderAction());
		addRetargetAction(new HorizontalRightOrderAction());
		addRetargetAction(new VerticalTopOrderAction());
		addRetargetAction(new VerticalCenterOrderAction());
		addRetargetAction(new VerticalBottomOrderAction());
		
		/* size actions */
		addRetargetAction(new MinWidhtAction());
		addRetargetAction(new MaxWidhtAction());
		addRetargetAction(new MinHeightAction());
		addRetargetAction(new MaxHeightAction());
		
		/* export actions */
		addRetargetAction(new ExportAsImageAction());
		addRetargetAction(new ExportGraphAction());

	}
	
	/**
	 * Add actions to the statndard toolbar.
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
	 */
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
	
		/* copy and paste actions */
		toolBarManager.add(getAction(ActionFactory.COPY.getId()));
		toolBarManager.add(getAction(ActionFactory.PASTE.getId()));
		toolBarManager.add(getAction(ActionFactory.CUT.getId()));

		/* undo/redo actions */	
		toolBarManager.add(new Separator());
		toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
		toolBarManager.add(getAction(ActionFactory.REDO.getId()));
		
		/* zoom actions */
		toolBarManager.add(new Separator());
		
		RetargetAction zoomIn = (RetargetAction)getAction(GEFActionConstants.ZOOM_IN);
		zoomIn.setAccelerator(SWT.CTRL | 'I');
		zoomIn.enableAccelerator(true);
		ImageDescriptor zoomInImage = ControlFlowFactoryResource.zoom_in_16x16;
		zoomIn.setImageDescriptor(zoomInImage);
		toolBarManager.add(zoomIn);

		RetargetAction zoomOut = (RetargetAction)getAction(GEFActionConstants.ZOOM_OUT);
		zoomOut.setAccelerator(SWT.CTRL | 'O');
		zoomOut.enableAccelerator(true);
		ImageDescriptor zoomOutImage = ControlFlowFactoryResource.zoom_out_16x16;
		zoomOut.setImageDescriptor(zoomOutImage);
		toolBarManager.add(zoomOut);
		
		String[] zoomStrings = new String[] {ZoomManager.FIT_ALL, 
											 ZoomManager.FIT_HEIGHT, 
											 ZoomManager.FIT_WIDTH };
	
		toolBarManager.add(new ZoomComboContributionItem(getPage(), zoomStrings));
		
		/* graph layout algorithm actions */
		toolBarManager.add(new Separator());
		IAction a = getAction(ByteCodelLayoutAlgorithmAction.ID);
		if(a != null)
			toolBarManager.add(a);
		
		a = getAction(HierarchicalLayoutAlgorithmAction.ID);
		if(a != null)
			toolBarManager.add(a);
		
		/* vertex order ections */
		toolBarManager.add(new Separator());
		a = getAction(HorizontalLeftOrderAction.ID);
		if(a != null)
			toolBarManager.add(a);
		
		a = getAction(HorizontalCenterOrderAction.ID);
		if(a != null)
			toolBarManager.add(a);
		
		a = getAction(HorizontalRightOrderAction.ID);
		if(a != null)
			toolBarManager.add(a);

		a = getAction(VerticalTopOrderAction.ID);
		if(a != null)
			toolBarManager.add(a);
		
		a = getAction(VerticalCenterOrderAction.ID);
		if(a != null)
			toolBarManager.add(a);
		
		a = getAction(VerticalBottomOrderAction.ID);
		if(a != null)
			toolBarManager.add(a);
		
		/* size actions */
		toolBarManager.add(new Separator());
		a = getAction(MinWidhtAction.ID);
		if(a != null)
			toolBarManager.add(a);
		
		a = getAction(MaxWidhtAction.ID);
		if(a != null)
			toolBarManager.add(a);
		
		a = getAction(MinHeightAction.ID);
		if(a != null)
			toolBarManager.add(a);
		
		a = getAction(MaxHeightAction.ID);
		if(a != null)
			toolBarManager.add(a);
		
		/* export actions */
		toolBarManager.add(new Separator());
		a = getAction(ExportAsImageAction.ID);
		if(a != null)
			toolBarManager.add(a);
		
		a = getAction(ExportGraphAction.ID);
		if(a != null)
			toolBarManager.add(a);

	}
	
    /**
     * Contributes to the given menu.
     * <p>
     * The <code>EditorActionBarContributor</code> implementation of this method
     * does nothing. Subclasses may reimplement to add to the menu portion of this
     * contribution.
     * </p>
     *
     * @param menuManager the manager that controls the menu
     */
	@Override
    public void contributeToMenu(IMenuManager menuManager) {
		/* bytecode menu */
    	MenuManager viewMenu = new MenuManager(ControlFlowFactoryMessages.ControlFlowFactory_Menu_Text);		
    	menuManager.insertAfter(IWorkbenchActionConstants.M_EDIT, viewMenu);
    	
		/* undo/redo actions */	
    	viewMenu.add(getAction(ActionFactory.UNDO.getId()));
    	viewMenu.add(getAction(ActionFactory.REDO.getId()));
		
		/* zoom actions */
    	viewMenu.add(new Separator());
		
		IAction zoomIn = getAction(GEFActionConstants.ZOOM_IN);
		ImageDescriptor zoomInImage = ControlFlowFactoryResource.zoom_in_16x16;
		zoomIn.setImageDescriptor(zoomInImage);
		viewMenu.add(zoomIn);
		
		IAction zoomOut = getAction(GEFActionConstants.ZOOM_OUT);
		ImageDescriptor zoomOutImage = ControlFlowFactoryResource.zoom_out_16x16;
		zoomOut.setImageDescriptor(zoomOutImage);
		viewMenu.add(zoomOut);
		
		/* graph layout algorithm actions */
    	viewMenu.add(new Separator());
    	IMenuManager graphAlgorithms = new MenuManager(ControlFlowFactoryMessages.ControlFlowFactory_SubMenuGrphAlgorithms_Text);
    	viewMenu.add(graphAlgorithms);
		IAction a = getAction(ByteCodelLayoutAlgorithmAction.ID);
		if(a != null)
			graphAlgorithms.add(a);
		
		a = getAction(HierarchicalLayoutAlgorithmAction.ID);
		if(a != null)
			graphAlgorithms.add(a);
		
		/* print and export actions */
		viewMenu.add(new Separator());

		a = getAction(PrintAction.ID);
    	a.setImageDescriptor(WorkbenchImages.getImageDescriptor("IMG_ETOOL_PRINT_EDIT")); 
		if(a != null)
			viewMenu.add(a);

		a = getAction(ExportAsImageAction.ID);
		if(a != null)
			viewMenu.add(a);
		
		a = getAction(ExportGraphAction.ID);
		if(a != null)
			viewMenu.add(a);
		
		/* About Dialog */
		/* undesired anymore */

    }
	
    /**
     * Contributes to the given status line.
     * <p>
     * The <code>EditorActionBarContributor</code> implementation of this method
     * does nothing. Subclasses may reimplement to add to the status line portion of
     * this contribution.
     * </p>
     *
     * @param statusLineManager the manager of the status line
     */
    @Override
    public void contributeToStatusLine(IStatusLineManager statusLineManager) {
    	/* currently none */
    }
    
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
	 */
	protected void declareGlobalActionKeys() {
    	/* currently none */
	}

}