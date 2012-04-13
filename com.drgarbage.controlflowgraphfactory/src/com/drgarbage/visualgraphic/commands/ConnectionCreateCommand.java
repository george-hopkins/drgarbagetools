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

import org.eclipse.gef.commands.Command;

import com.drgarbage.visualgraphic.model.Connection;
import com.drgarbage.visualgraphic.model.VertexBase;

/**
 * A command to create a connection between two vertices.
 * The command can be undone or redone.
 * <p>
 * This command is designed to be used together with a GraphicalNodeEditPolicy.
 * To use this command properly, following steps are necessary:
 * </p>
 * <ol>
 * <li>Create a subclass of GraphicalNodeEditPolicy.</li>
 * <li>Override the <code>getConnectionCreateCommand(...)</code> method, 
 * to create a new instance of this class and put it into the CreateConnectionRequest.</li>
 * <li>Override the <code>getConnectionCompleteCommand(...)</code>  method,
 * to obtain the Command from the ConnectionRequest, call setTarget(...) to set the
 * target endpoint of the connection and return this command instance.</li>
 * </ol>
 * @see com.drgarbage.controlflowgraphfactory.plugin.editors.parts.VertexBaseEditPart#createEditPolicies() for an
 * 			 example of the above procedure.
 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy
 *
 * @version $Revision:204 $
 * $Id:ConnectionCreateCommand.java 204 2007-06-08 13:19:58Z aleks $
 */
public class ConnectionCreateCommand extends Command {
	/** The connection instance. */
	private Connection connection;
	
	/** The desired line style for the connection (dashed or solid). */
	private final int lineStyle;

	/** Start endpoint for the connection. */
	private final VertexBase source;
	
	/** Target endpoint for the connection. */
	private VertexBase target;
	
	/**
	 * Instantiate a command that can create a connection between two vertices.
	 * @param source the source endpoint (a non-null VertexBase instance)
	 * @param lineStyle the desired line style. See Connection#setLineStyle(int) for details
	 * @throws IllegalArgumentException if source is null
	 * @see Connection#setLineStyle(int)
	 */
	public ConnectionCreateCommand(VertexBase source, int lineStyle) {
		if (source == null) {
			throw new IllegalArgumentException();
		}
		setLabel("connection creation");
		this.source = source;
		this.lineStyle = lineStyle;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		// disallow source -> source connections
		if (source.equals(target)) {
			return false;
		}
		// return false, if the source -> target connection exists already
		for (Iterator iter = source.getSourceConnections().iterator(); iter.hasNext();) {
			Connection conn = (Connection) iter.next();
			if (conn.getTarget().equals(target)) {
				return false;
			}
		}

		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		// create a new connection between source and target
		connection = new Connection(source, target);

		// use the supplied line style
		connection.setLineStyle(lineStyle);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		connection.reconnect();
	}
	
	/**
	 * Set the target endpoint for the connection.
	 * @param target that target endpoint (a non-null VertexBase instance)
	 * @throws IllegalArgumentException if target is null
	 */
	public void setTarget(VertexBase target) {
		if (target == null) {
			throw new IllegalArgumentException();
		}
		this.target = target;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		connection.disconnect();
	}
}
