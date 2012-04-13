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
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import com.drgarbage.visualgraphic.commands.VertexBaseDeleteCommand;
import com.drgarbage.visualgraphic.editparts.VertexBaseEditPart;
import com.drgarbage.visualgraphic.editparts.VertexBaseTreeEditPart;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;
import com.drgarbage.visualgraphic.model.VertexBase;


/**
 * This edit policy enables the removal of a vertex instance from its container. 
 * @see VertexBaseEditPart#createEditPolicies()
 * @see VertexBaseTreeEditPart#createEditPolicies()
 *  
 * @author Sergej Alekseev
 * @version $Revision:125 $
 * $Id:VertexBaseComponentEditPolicy.java 125 2007-05-22 16:08:25Z aleks $
 */
public class VertexBaseComponentEditPolicy extends ComponentEditPolicy {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ComponentEditPolicy#createDeleteCommand(org.eclipse.gef.requests.GroupRequest)
	 */
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Object parent = getHost().getParent().getModel();
		Object child = getHost().getModel();
		if (parent instanceof ControlFlowGraphDiagram && child instanceof VertexBase) {
			return new VertexBaseDeleteCommand((ControlFlowGraphDiagram) parent, (VertexBase) child);
		}
		return super.createDeleteCommand(deleteRequest);
	}
}