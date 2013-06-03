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

package com.drgarbage.algorithms;

import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;


/**
 *  Basis class for the Deth First Search algorithm.
 *
 *  @author Sergej Alekseev  
 *  @version $Revision$
 *  $Id$
 */
public abstract class DepthFirstSearchBaseVisitor {

	public void visit(IDirectedGraphExt graph) throws ControlFlowGraphException{
		INodeListExt nodeList = graph.getNodeList();
		
		if(	nodeList == null || nodeList.size() < 1){
			throw new ControlFlowGraphException("Can't start DFS. Vertex List is empty.");
		}

		for(int i = 0; i < nodeList.size(); i++ ){
			traverse(nodeList.getNodeExt(i));
		}
		
		postHandling();
	}

	
	public void visit(IDirectedGraphExt graph, INodeExt start) throws ControlFlowGraphException{
		
		if(!graph.getNodeList().contains(start)){
			throw new ControlFlowGraphException("Can't start DFS. Start Vertex '" + start.toString()+ "' not found." );
		}

		traverse(start);
		
		postHandling();
	}
	
	protected abstract void traverse(INodeExt node);

	protected abstract void postHandling() throws ControlFlowGraphException; 
	
	protected boolean stopRecurion = false;
	


}
