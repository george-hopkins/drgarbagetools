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

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;

import com.drgarbage.controlflowgraph.VisualGraphicMessages;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.img.ControlFlowFactoryResource;
import com.drgarbage.visualgraphic.model.CommentElement;
import com.drgarbage.visualgraphic.model.Connection;
import com.drgarbage.visualgraphic.model.DecisionVertex;
import com.drgarbage.visualgraphic.model.ExitVertex;
import com.drgarbage.visualgraphic.model.GetVertex;
import com.drgarbage.visualgraphic.model.GotoJumpVertex;
import com.drgarbage.visualgraphic.model.InvokeVertex;
import com.drgarbage.visualgraphic.model.RectangularVertex;
import com.drgarbage.visualgraphic.model.ReturnVertex;
import com.drgarbage.visualgraphic.model.RoundedRectangularVertex;
import com.drgarbage.visualgraphic.model.StartVertex;
import com.drgarbage.visualgraphic.model.SwitchVertex;


/**
 * Utility class that can create a Palette.
 * @see #createPalette() 
 * @version $Revision:118 $
 * $Id:ControlFlowGraphEditorPaletteFactory.java 118 2007-05-21 19:35:02Z aleks $
 */
final class ControlFlowGraphEditorPaletteFactory {

	/**
	 * Creates the PaletteRoot and adds all palette elements.
	 * Use this factory method to create a new palette for your graphical editor.
	 * @return a new PaletteRoot
	 */
	static PaletteRoot createPalette() {
		PaletteRoot palette = new PaletteRoot();
		palette.add(createToolsGroup(palette));
		palette.add(createConectionGroup(palette));
		palette.add(createShapesDrawer());
		
		return palette;
	}

	
	/** Create the "Tools" group. */
	private static PaletteContainer createToolsGroup(PaletteRoot palette) {
		PaletteGroup toolGroup = new PaletteGroup("Tools");
	
		// Add a selection tool to the group
		ToolEntry tool = new PanningSelectionToolEntry();
		toolGroup.add(tool);
		palette.setDefaultEntry(tool);
		
		return toolGroup;
	}

	/** Create the "Tools" group. */
	private static PaletteContainer createConectionGroup(PaletteRoot palette) {
		//PaletteGroup toolGroup = new PaletteGroup("Connections");
		PaletteDrawer componentsDrawer = new PaletteDrawer("Connections");
	
		// Add (solid-line) connection tool 
		ToolEntry tool = new ConnectionCreationToolEntry(
				ControlFlowFactoryMessages.SOLID_TEXT_COMMAND,
				ControlFlowFactoryMessages.SOLID_CONNECTION_TOOLTIP,
				new CreationFactory() {
					public Object getNewObject() { return null; }
					// see ControlFlowGraphEditPart#createEditPolicies() 
					// this is abused to transmit the desired line style 
					public Object getObjectType() { return Connection.SOLID_CONNECTION; }
				},
				ControlFlowFactoryResource.solid_connection_icon_16x16,
				ControlFlowFactoryResource.solid_connection_icon_24x24);
		componentsDrawer.add(tool);
		
		// Add (dashed-line) connection tool
		tool = new ConnectionCreationToolEntry(
				ControlFlowFactoryMessages.DASHED_TEXT_COMMAND,
				ControlFlowFactoryMessages.DASHED_CONNECTION_TOOLTIP,
				new CreationFactory() {
					public Object getNewObject() { return null; }
					// see ControlFlowGraphEditPart#createEditPolicies()
					// this is abused to transmit the desired line style 
					public Object getObjectType() { return Connection.DASHED_CONNECTION; }
				},
				ControlFlowFactoryResource.dashed_connection_16x16,
				ControlFlowFactoryResource.dashed_connection_24x24);
		componentsDrawer.add(tool);
		
		return componentsDrawer;
	}
	
	/** Create the "Node" drawer. */
	private static PaletteContainer createShapesDrawer() {
		PaletteDrawer componentsDrawer = new PaletteDrawer("Nodes");

		CombinedTemplateCreationEntry component = new CombinedTemplateCreationEntry(
				VisualGraphicMessages.Decision,
				ControlFlowFactoryMessages.DECISION_TOOLTIP, 
				DecisionVertex.class,
				new SimpleFactory(DecisionVertex.class), 
				ControlFlowFactoryResource.decision_icon_16x16, 
				ControlFlowFactoryResource.decision_icon_24x24);
		componentsDrawer.add(component);
		
		component = new CombinedTemplateCreationEntry(
				VisualGraphicMessages.Get, 
				ControlFlowFactoryMessages.GET_TEXT_TOOLTIP, 
				GetVertex.class,
				new SimpleFactory(GetVertex.class), 
				ControlFlowFactoryResource.get_icon_16x16, 
				ControlFlowFactoryResource.get_icon_24x24);
		componentsDrawer.add(component);
		
		component = new CombinedTemplateCreationEntry(
				VisualGraphicMessages.Goto_jump,
				ControlFlowFactoryMessages.GOTOJUMP_TOOLTIP, 
				GotoJumpVertex.class,
				new SimpleFactory(GotoJumpVertex.class), 
				ControlFlowFactoryResource.gotojump_icon_16x16, 
				ControlFlowFactoryResource.gotojump_icon_24x24);
		componentsDrawer.add(component);

		component = new CombinedTemplateCreationEntry(
				VisualGraphicMessages.Invoke,
				ControlFlowFactoryMessages.INVOKE_TOOLTIP, 
				InvokeVertex.class,
				new SimpleFactory(InvokeVertex.class), 
				ControlFlowFactoryResource.invoke_icon_16x16, 
				ControlFlowFactoryResource.invoke_icon_24x24);
		componentsDrawer.add(component);

		component = new CombinedTemplateCreationEntry(
				VisualGraphicMessages.Instruction,
				ControlFlowFactoryMessages.RECTANGLE_TOOLTIP, 
				RectangularVertex.class,
				new SimpleFactory(RectangularVertex.class), 
				ControlFlowFactoryResource.rectangular_icon_16x16, 
				ControlFlowFactoryResource.rectangular_icon_24x24);
		componentsDrawer.add(component);

		component = new CombinedTemplateCreationEntry(
				VisualGraphicMessages.Return,
				ControlFlowFactoryMessages.RETURN_TOOLTIP, 
				ReturnVertex.class,
				new SimpleFactory(ReturnVertex.class), 
				ControlFlowFactoryResource.return_icon_16x16, 
				ControlFlowFactoryResource.return_icon_24x24);
		componentsDrawer.add(component);
		
		component = new CombinedTemplateCreationEntry(
				VisualGraphicMessages.Entry,
				ControlFlowFactoryMessages.ENTRY_TOOLTIP, 
				StartVertex.class,
				new SimpleFactory(StartVertex.class), 
				ControlFlowFactoryResource.roundedrect_icon_16x16, 
				ControlFlowFactoryResource.roundedrect_icon_24x24);
		componentsDrawer.add(component);

		component = new CombinedTemplateCreationEntry(
				VisualGraphicMessages.Exit,
				ControlFlowFactoryMessages.END_TOOLTIP, 
				ExitVertex.class,
				new SimpleFactory(ExitVertex.class), 
				ControlFlowFactoryResource.roundedrect_icon_16x16, 
				ControlFlowFactoryResource.roundedrect_icon_24x24);
		componentsDrawer.add(component);

		component = new CombinedTemplateCreationEntry(
				VisualGraphicMessages.Switch,
				ControlFlowFactoryMessages.SWITCH_TOOLTIP, 
				SwitchVertex.class,
				new SimpleFactory(SwitchVertex.class), 
				ControlFlowFactoryResource.switch_icon_16x16, 
				ControlFlowFactoryResource.switch_icon_24x24);
		componentsDrawer.add(component);
	
		component = new CombinedTemplateCreationEntry(
				VisualGraphicMessages.Comment,
				ControlFlowFactoryMessages.COMMENT_ELEMENT_TOOLTIP, 
				CommentElement.class,
				new SimpleFactory(CommentElement.class), 
				ControlFlowFactoryResource.comment_icon_16x16, 
				ControlFlowFactoryResource.comment_icon_24x24);
		componentsDrawer.add(component);

		return componentsDrawer;
	}

	
	/** Utility class. */
	private ControlFlowGraphEditorPaletteFactory() {
		// Utility class
	}

}