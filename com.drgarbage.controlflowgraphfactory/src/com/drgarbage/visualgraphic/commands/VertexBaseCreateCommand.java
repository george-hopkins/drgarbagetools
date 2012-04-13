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

package com.drgarbage.visualgraphic.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;
import com.drgarbage.visualgraphic.model.VertexBase;



/**
 * A command to add a VertexBase to a control flow diagram.
 * The command can be undone or redone.
 *
 *  @version $Revision:204 $
 *  $Id:VertexBaseCreateCommand.java 204 2007-06-08 13:19:58Z aleks $
 */
public class VertexBaseCreateCommand 
	extends Command 
{
	
	/** The new vertex. */ 
	private VertexBase newShape;
	
	/** Control Flow Diagram to add to. */
	private final ControlFlowGraphDiagram parent;
	
	/** The bounds of the new VertexBase. */
	private Rectangle bounds;
	
	/**
	 * Create a command that will add a new VertexBase to a ControlFlowGraphDiagram.
	 * @param newShape the new VertexBase that is to be added
	 * @param parent the ControlFlowGraphDiagram that will hold the new element
	 * @param bounds the bounds of the new vertex; the size can be (-1, -1) if not known
	 * @throws IllegalArgumentException if any parameter is null, or the request
	 * 						  does not provide a new VertexBase instance
	 */
	public VertexBaseCreateCommand(VertexBase newShape, ControlFlowGraphDiagram parent, Rectangle bounds) {
		this.newShape = newShape;
		this.parent = parent;
		this.bounds = bounds;
		setLabel("vertex creation");
	}

	public VertexBaseCreateCommand(VertexBase newShape, ControlFlowGraphDiagram parent, Rectangle bounds, String label) {
		this.newShape = newShape;
		this.parent = parent;
		this.bounds = bounds;
		setLabel(label);
	}

	/**
	 * Can execute if all the necessary information has been provided. 
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		return newShape != null && parent != null && bounds != null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		newShape.setLocation(bounds.getLocation());
		Dimension size = bounds.getSize();
		if (size.width > 0 && size.height > 0)
			newShape.setSize(size);
		redo();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		parent.addChild(newShape);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		parent.removeChild(newShape);
	}
		
}