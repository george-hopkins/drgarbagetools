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

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.commands.Command;

import com.drgarbage.visualgraphic.model.Connection;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;
import com.drgarbage.visualgraphic.model.VertexBase;


/**
 * A command to remove a vertex from its parent.
 * The command can be undone or redone.
 *
 *  @version $Revision:204 $
 *  $Id:VertexBaseDeleteCommand.java 204 2007-06-08 13:19:58Z aleks $
 */
public class VertexBaseDeleteCommand extends Command {
	
	/** VertexBase to remove. */
	private final VertexBase child;
	
	/** Control Flow Diagram to remove from. */
	private final ControlFlowGraphDiagram parent;
	
	/** Holds a copy of the outgoing connections of child. */
	private List sourceConnections;
	
	/** Holds a copy of the incoming connections of child. */
	private List targetConnections;
	
	/** True, if child was removed from its parent. */
	private boolean wasRemoved;
	
	/**
	 * Create a command that will remove the vertex from its parent.
	 * @param parent the ControlFlowGraphDiagram containing the child
	 * @param child    the VertexBase to remove
	 * @throws IllegalArgumentException if any parameter is null
	 */
	public VertexBaseDeleteCommand(ControlFlowGraphDiagram parent, VertexBase child) {
		if (parent == null || child == null) {
			throw new IllegalArgumentException();
		}
		setLabel("vertex deletion");
		this.parent = parent;
		this.child = child;
	}
	
	/**
	 * Reconnects a List of Connections with their previous endpoints.
	 * @param connections a non-null List of connections
	 */
	private void addConnections(List connections) {
		for (Iterator iter = connections.iterator(); iter.hasNext();) {
			Connection conn = (Connection) iter.next();
			conn.reconnect();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		return wasRemoved;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		// store a copy of incoming & outgoing connections before proceeding 
		sourceConnections = child.getSourceConnections();
		targetConnections = child.getTargetConnections();
		redo();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		// remove the child and disconnect its connections
		wasRemoved = parent.removeChild(child);
		if (wasRemoved) {
			removeConnections(sourceConnections);
			removeConnections(targetConnections);
		}
	}
	
	/**
	 * Disconnects a List of Connections from their endpoints.
	 * @param connections a non-null List of connections
	 */
	private void removeConnections(List connections) {
		for (Iterator iter = connections.iterator(); iter.hasNext();) {
			Connection conn = (Connection) iter.next();
			conn.disconnect();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		// add the child and reconnect its connections
		if (parent.addChild(child)) {
			addConnections(sourceConnections);
			addConnections(targetConnections);
		}
	}
}