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

import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;

import com.drgarbage.visualgraphic.model.VertexBase;


/**
 * A command to resize and/or move a vertexBase.
 * The command can be undone or redone.
 *
 *  @version $Revision:204 $
 *  $Id:VertexBaseSetConstraintCommand.java 204 2007-06-08 13:19:58Z aleks $
 */
public class VertexBaseSetConstraintCommand extends Command {
	/** Stores the new size and location. */
	private final Rectangle newBounds;

	/** Stores the old size and location. */
	private Rectangle oldBounds;

	/** A request to move/resize an edit part. */
	private final ChangeBoundsRequest request;

	/** VertexBase to manipulate. */
	private final VertexBase vertexBase;
		
	/**
	 * Create a command that can resize and/or move a vertexBase. 
	 * @param vertexBase	the vertexBase to manipulate
	 * @param req		the move and resize request
	 * @param newBounds the new size and location
	 * @throws IllegalArgumentException if any of the parameters is null
	 */
	public VertexBaseSetConstraintCommand(VertexBase vertexBase, ChangeBoundsRequest req, 
			Rectangle newBounds) {
		if (vertexBase == null || req == null || newBounds == null) {
			throw new IllegalArgumentException();
		}
		
		this.newBounds = newBounds.getCopy();		
		this.vertexBase = vertexBase;
		this.request = req;
		setLabel("move / resize");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		Object type = request.getType();
		// make sure the Request is of a type we support:
		return (RequestConstants.REQ_MOVE.equals(type)
				|| RequestConstants.REQ_MOVE_CHILDREN.equals(type) 
				|| RequestConstants.REQ_RESIZE.equals(type)
				|| RequestConstants.REQ_RESIZE_CHILDREN.equals(type));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		oldBounds = new Rectangle(vertexBase.getLocation(), vertexBase.getSize());
		redo();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		vertexBase.setSize(newBounds.getSize());
		vertexBase.setLocation(oldBounds.getLocation(), newBounds.getLocation());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		vertexBase.setSize(oldBounds.getSize());
		vertexBase.setLocation(oldBounds.getLocation());
	}
}
