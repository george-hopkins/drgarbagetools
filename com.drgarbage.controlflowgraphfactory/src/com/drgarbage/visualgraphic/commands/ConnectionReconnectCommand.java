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
 * A command to reconnect a connection to a different start point or end point.
 * The command can be undone or redone.
 * <p>
 * This command is designed to be used together with a GraphicalNodeEditPolicy.
 * To use this command propertly, following steps are necessary:
 * </p>
 * <ol>
 * <li>Create a subclass of GraphicalNodeEditPolicy.</li>
 * <li>Override the <code>getReconnectSourceCommand(...)</code> method.
 * Here you need to obtain the Connection model element from the ReconnectRequest,
 * create a new ConnectionReconnectCommand, set the new connection <i>source</i> by calling
 * the <code>setNewSource(VertexBase)</code> method and return the command instance.
 * <li>Override the <code>getReconnectTargetCommand(...)</code> method.</li>
 * Here again you need to obtain the Connection model element from the ReconnectRequest,
 * create a new ConnectionReconnectCommand, set the new connection <i>target</i> by calling
 * the <code>setNewTarget(VertexBase)</code> method and return the command instance.</li>
 * </ol>
 * @see com.drgarbage.controlflowgraphfactory.plugin.editors.parts.VertexBaseEditPart#createEditPolicies() for an
 * 			 example of the above procedure.
 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy
 * @see #setNewSource(VertexBase)
 * @see #setNewTarget(VertexBase)
 *
 * @version $Revision:204 $
 * $Id:ConnectionReconnectCommand.java 204 2007-06-08 13:19:58Z aleks $
 */
public class ConnectionReconnectCommand extends Command {
	
	/** The connection instance to reconnect. */
	private Connection connection;
	
	/** The new source endpoint. */
	private VertexBase newSource;
	
	/** The new target endpoint. */
	private VertexBase newTarget;
	
	/** The original source endpoint. */
	private final VertexBase oldSource;
	
	/** The original target endpoint. */
	private final VertexBase oldTarget;
	
	/**
	 * Instantiate a command that can reconnect a Connection instance to a different source
	 * or target endpoint.
	 * @param conn the connection instance to reconnect (non-null)
	 * @throws IllegalArgumentException if conn is null
	 */
	public ConnectionReconnectCommand(Connection conn) {
		if (conn == null) {
			throw new IllegalArgumentException();
		}
		this.connection = conn;
		this.oldSource = conn.getSource();
		this.oldTarget = conn.getTarget();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	
	public boolean canExecute() {
		if (newSource != null) {
			return checkSourceReconnection();
		} else if (newTarget != null) {
			return checkTargetReconnection();
		}
		return false;
	}
	
	/**
	 * Return true, if reconnecting the connection-instance to newSource is allowed.
	 */
	private boolean checkSourceReconnection() {
		// connection endpoints must be different vertices
		if (newSource.equals(oldTarget)) {
			return false;
		}
		// return false, if the connection exists already
		for (Iterator iter = newSource.getSourceConnections().iterator(); iter.hasNext();) {
			Connection conn = (Connection) iter.next();
			// return false if a newSource -> oldTarget connection exists already
			// and it is a different instance than the connection-field
			if (conn.getTarget().equals(oldTarget) &&  !conn.equals(connection)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Return true, if reconnecting the connection-instance to newTarget is allowed. 
	 */
	private boolean checkTargetReconnection() {
		// connection endpoints must be different vertices
		if (newTarget.equals(oldSource)) {
			return false;
		}
		// return false, if the connection exists already
		for (Iterator iter = newTarget.getTargetConnections().iterator(); iter.hasNext();) {
			Connection conn = (Connection) iter.next();
			// return false if a oldSource -> newTarget connection exists already
			// and it is a differenct instance that the connection-field
			if (conn.getSource().equals(oldSource) && !conn.equals(connection)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Reconnect the connection to newSource (if setNewSource(...) was invoked before)
	 * or newTarget (if setNewTarget(...) was invoked before).
	 */
	public void execute() {
		if (newSource != null) {
			connection.reconnect(newSource, oldTarget);
		} else if (newTarget != null) {
			connection.reconnect(oldSource, newTarget);
		} else {
			throw new IllegalStateException("Should not happen");
		}
	}
	
	/**
	 * Set a new source endpoint for this connection.
	 * When execute() is invoked, the source endpoint of the connection will be attached
	 * to the supplied VertexBase instance.
	 * <p>
	 * Note: Calling this method, deactivates reconnection of the <i>target</i> endpoint.
	 * A single instance of this command can only reconnect either the source or the target 
	 * endpoint.
	 * </p>
	 * @param connectionSource a non-null VertexBase instance, to be used as a new source endpoint
	 * @throws IllegalArgumentException if connectionSource is null
	 */
	public void setNewSource(VertexBase connectionSource) {
		if (connectionSource == null) {
			throw new IllegalArgumentException();
		}
		setLabel("move connection startpoint");
		newSource = connectionSource;
		newTarget = null;
	}
	
	/**
	 * Set a new target endpoint for this connection
	 * When execute() is invoked, the target endpoint of the connection will be attached
	 * to the supplied VertexBase instance.
	 * <p>
	 * Note: Calling this method, deactivates reconnection of the <i>source</i> endpoint.
	 * A single instance of this command can only reconnect either the source or the target 
	 * endpoint.
	 * </p>
	 * @param connectionTarget a non-null VertexBase instance, to be used as a new target endpoint
	 * @throws IllegalArgumentException if connectionTarget is null
	 */
	public void setNewTarget(VertexBase connectionTarget) {
		if (connectionTarget == null) {
			throw new IllegalArgumentException();
		}
		setLabel("move connection endpoint");
		newSource = null;
		newTarget = connectionTarget;
	}
	
	/**
	 * Reconnect the connection to its original source and target endpoints.
	 */
	public void undo() {
		connection.reconnect(oldSource, oldTarget);
	}
		
}
