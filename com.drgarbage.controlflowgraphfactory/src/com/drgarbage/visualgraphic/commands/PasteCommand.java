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

import java.util.List;

import org.eclipse.gef.commands.Command;

import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;
import com.drgarbage.visualgraphic.model.VertexBase;



/**
 * A command to paste copied elements.
 * The command can be undone or redone.
 *
 *  @version $Revision$
 *  $Id$
 */
public class PasteCommand 
	extends Command 
{
	/** Control Flow Diagram to add to. */
	private final ControlFlowGraphDiagram parent;
	
	/** The elements to paste. */
	List<VertexBase> elements;
	
	
	/**
	 * Create a command that will add elements to a ControlFlowGraphDiagram.
	 * @throws IllegalArgumentException if any parameter is null, or the request
	 * 						  does not provide a new VertexBase instance
	 */
	public PasteCommand(ControlFlowGraphDiagram model, List<VertexBase> elements) {
		parent = model;
		this.elements = elements;
	}

	/**
	 * Can execute if all the necessary information has been provided. 
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		return elements != null && parent != null && elements.size() != 0;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		redo();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		for(VertexBase vb: elements){
				parent.addChild(vb);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		for(VertexBase vb: elements){
				parent.removeChild(vb);
		}
	}
		
}