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

package com.drgarbage.visualgraphic.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import com.drgarbage.visualgraphic.commands.ConnectionCreateCommand;
import com.drgarbage.visualgraphic.commands.ConnectionReconnectCommand;
import com.drgarbage.visualgraphic.editparts.VertexBaseEditPart;
import com.drgarbage.visualgraphic.editparts.VertexBaseTreeEditPart;
import com.drgarbage.visualgraphic.model.Connection;
import com.drgarbage.visualgraphic.model.VertexBase;


/**
 * This edit policy enables the the creation of connections and 
 * the reconnection of connections between VertexBase instances.
 * @see VertexBaseEditPart#createEditPolicies()
 * @see VertexBaseTreeEditPart#createEditPolicies()
 *  
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: VertexBaseGraphicalNodeEditPolicy.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class VertexBaseGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCompleteCommand(org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		ConnectionCreateCommand cmd 
			= (ConnectionCreateCommand) request.getStartCommand();
		cmd.setTarget((VertexBase) getHost().getModel());
		return cmd;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCreateCommand(org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		VertexBase source = (VertexBase) getHost().getModel();
		int style = ((Integer) request.getNewObjectType()).intValue();
		ConnectionCreateCommand cmd = new ConnectionCreateCommand(source, style);
		request.setStartCommand(cmd);
		return cmd;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectSourceCommand(org.eclipse.gef.requests.ReconnectRequest)
	 */
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		Connection conn = (Connection) request.getConnectionEditPart().getModel();
		VertexBase newSource = (VertexBase) getHost().getModel();
		ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(conn);
		cmd.setNewSource(newSource);
		return cmd;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectTargetCommand(org.eclipse.gef.requests.ReconnectRequest)
	 */
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		Connection conn = (Connection) request.getConnectionEditPart().getModel();
		VertexBase newTarget = (VertexBase) getHost().getModel();
		ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(conn);
		cmd.setNewTarget(newTarget);
		return cmd;
	}

}