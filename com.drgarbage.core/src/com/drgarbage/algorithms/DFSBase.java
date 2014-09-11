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
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;
import com.drgarbage.core.CorePlugin;


/**
 *  The Depth First Search algorithm.
 *
 *  @author Sergej Alekseev  
 *  @version $Revision$
 *  $Id$
 */
public abstract class DFSBase {
	
    /**
     * Debug flag.
     */
    protected static boolean debug = false;
    
    /**
     * Prints debug messages.
     * @param message 
     */
    protected static void log(String message) {
        if (debug) {
        	CorePlugin.log(CorePlugin.createInfoStatus(message));
        }
    }

	/**
	 * Starts dfs from any node.
	 * @param graph the graph
	 * @throws ControlFlowGraphException
	 */
	public void start(IDirectedGraphExt graph) throws ControlFlowGraphException{
		INodeListExt nodeList = graph.getNodeList();
		
		if(	nodeList == null || nodeList.size() < 1){
			throw new ControlFlowGraphException("Can't start DFS. Vertex List is empty.");
		}

		for(int i = 0; i < nodeList.size(); i++ ){
			dfs(nodeList.getNodeExt(i));
		}
		
		postHandling();
	}

	/**
	 * Start bfs from the given node.
	 * @param graph the graph
	 * @param start the start node
	 * @throws ControlFlowGraphException
	 */
	public void start(IDirectedGraphExt graph, INodeExt start) throws ControlFlowGraphException{
		
		if(!graph.getNodeList().contains(start)){
			throw new ControlFlowGraphException("Can't start DFS. Start Vertex '" + start.toString()+ "' not found." );
		}

		dfs(start);
		
		postHandling();
	}
	
	protected abstract void dfs(INodeExt node);
	protected abstract void postHandling() throws ControlFlowGraphException; 
	
	protected boolean stopRecurion = false;
	
	/* public visitor hooks */
	public abstract void visitNode(INodeExt node);
	public abstract void postVisitNode(INodeExt node);
	public abstract void visitEdge(IEdgeExt edge);
	public abstract void postVisitEdge(IEdgeExt edge);
	


}
