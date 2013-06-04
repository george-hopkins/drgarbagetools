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

import java.util.LinkedList;
import java.util.Queue;

import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;
import com.drgarbage.core.CorePlugin;


/**
 *  Basis class for the Breadth First Search algorithm.
 *
 *  @author Sergej Alekseev  
 *  @version $Revision$
 *  $Id$
 */
public abstract class BFSBase {

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
        	//System.out.println(message);
        }
    }

	/**
	 * Starts bfs from any node.
	 * @param graph the graph
	 * @throws ControlFlowGraphException
	 */
	public void start(IDirectedGraphExt graph) throws ControlFlowGraphException{
		INodeListExt nodeList = graph.getNodeList();
		
		if(	nodeList == null || nodeList.size() < 1){
			throw new ControlFlowGraphException("Can't start BFS. Vertex List is empty.");
		}

		for(int i = 0; i < nodeList.size(); i++ ){
			bfs(nodeList.getNodeExt(i));
		}
		
	}
	
	/**
	 * Start bfs from the given node.
	 * @param graph the graph
	 * @param start the start node
	 * @throws ControlFlowGraphException
	 */
	protected void start(IDirectedGraphExt graph, INodeExt start) throws ControlFlowGraphException{
		if(!graph.getNodeList().contains(start)){
			throw new ControlFlowGraphException("Can't start BFS. Start Vertex '" + start.toString()+ "' not found." );
		}

		if(debug)log("Start node: " + start.toString() + " " + start.getData() + " " + start.getOutgoingEdgeList().toString());
		
		bfs(start);
	}
	
	/**
	 * Traverses the graph from the given node.
	 * @param start node
	 */
	protected void bfs(INodeExt startnode){
		Queue<INodeExt> queue = new LinkedList<INodeExt>();
		enqueue(queue, startnode);
		
		visitNode(startnode);
		
		while(!queue.isEmpty()){
			INodeExt node = dequeue(queue);
			IEdgeListExt outgoingEdges = node.getOutgoingEdgeList();
			for(int i = 0; i < outgoingEdges.size(); i++){
				IEdgeExt edge = outgoingEdges.getEdgeExt(i);
				if(edge.isVisited()){
					continue;
				}
				visitEdge(edge);
				
				INodeExt targetNode = edge.getTarget();
				if(!targetNode.isVisited()){
					enqueue(queue, targetNode);
					visitNode(targetNode);
				}
			}
		}
	}
	
	protected void visitNode(INodeExt node){
		node.setVisited(true);
		visitedNode(node);
		
		if(debug)log(node.toString() + " " + node.getData());
	}
	
	protected void visitEdge(IEdgeExt edge){
		edge.setVisited(true);
		visitedEdge(edge);
		if(debug)log(edge.getSource().getByteCodeOffset() /*+ " (" +  edge.getSource().getData().toString() + ") "*/ + 
				"->" + edge.getTarget().getByteCodeOffset() /*+ " (" +  edge.getTarget().getData().toString() + ") "*/);
	}
	
	protected void enqueue(Queue<INodeExt> queue, INodeExt node){
		queue.add(node);
		enqueue(node);
		if(debug)log("enqueue:" + node + " " + node.getData());
	}

	protected INodeExt dequeue(Queue<INodeExt> queue){
		INodeExt node = queue.poll();
		dequeue(node);
		if(debug)log("dequeue:" + node + " " + node.getData());
		return node;
	}

	/* call back methods */
	protected abstract void visitedNode(INodeExt node);
	protected abstract void visitedEdge(IEdgeExt edge);
	protected abstract void enqueue(INodeExt node);
	protected abstract void dequeue(INodeExt node);
	
}
