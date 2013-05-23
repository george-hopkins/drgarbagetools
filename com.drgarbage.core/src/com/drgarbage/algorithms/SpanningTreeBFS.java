/**
 * Copyright (c) 2008- 2013, Dr. Garbage Community
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

import java.util.HashMap;
import java.util.Map;

import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;

/**
 * 	Finds a spanning tree for the given graph.
 * 
 *  @author Andreas Karoly, Sergej Alekseev  
 *  @version $Revision$
 *  $Id$
 */
public class SpanningTreeBFS extends BFSBase {
	
	/**
	 * Spanning tree graph.
	 */
	private IDirectedGraphExt spanningTree;
	
	/**
	 * Map of nodes <b>old graph</b> <-> <b>new graph</b>
	 */
	private Map<INodeExt, INodeExt> mapNodeList;
	
	/**
	 * If the variable is true a new graph for the spanning tree is created.
	 * Otherwise the original graph is modified to the spanning tree.
	 */
	private boolean createNewGraph = true;
	
	/**
	 * Set true if a new graph for the spanning tree has to be created.
	 * Otherwise the original graph is modified to the spanning tree. 
	 * @param createNewGraph - true or false
	 */
	public void setCreateNewGraph(boolean createNewGraph) {
		this.createNewGraph = createNewGraph;
	}

	/**
	 * Returns the spanning tree graph.
	 * @return the spanning tree graph
	 */
	public IDirectedGraphExt getSpanningTree(){
		return spanningTree;
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.algorithms.BFSBase#start(com.drgarbage.controlflowgraph.intf.IDirectedGraphExt)
	 */
	@Override
	public void start(IDirectedGraphExt graph) throws ControlFlowGraphException{
		if(createNewGraph){
			spanningTree = GraphExtentionFactory.createDirectedGraphExtention();
			mapNodeList = new HashMap<INodeExt, INodeExt>();

			INodeListExt oldNodeList = graph.getNodeList();
			INodeListExt newNodeList = spanningTree.getNodeList();

			/* create a map of the old nodes to the new nodes */
			for (int i = 0; i < oldNodeList.size(); i++){
				INodeExt oldNode = oldNodeList.getNodeExt(i);
				INodeExt newNode = GraphExtentionFactory.createNodeExtention(oldNode.getData());
				mapNodeList.put(oldNode,  newNode);

				/* copy node property */
				copyNodeProperties(oldNode, newNode);

				/* copy the list of the new nodes to the new graph */
				newNodeList.add(newNode);
			}
		}
		else{
			spanningTree = graph;
			mapNodeList = null;
		}
		
		/* start algorithm */
		super.start(graph);
	}

	/**
	 * Copies all properties of the old node to the new node
	 * @param oldNode - the old node
	 * @param newNode - the new node
	 */
	private void copyNodeProperties(INodeExt oldNode, INodeExt newNode){
		newNode.setByteCodeOffset(oldNode.getByteCodeOffset());
		newNode.setByteCodeString(oldNode.getByteCodeString());
		//TODO: copy all properties
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.algorithms.BFSBase#visitedEdge(com.drgarbage.controlflowgraph.intf.IEdgeExt)
	 */
	@Override
	protected void visitedEdge(IEdgeExt edge) {
		if(!edge.getTarget().isVisited()){
			if(createNewGraph){
				/* find the source and target node, create new edge */
				INodeExt source = mapNodeList.get(edge.getSource());
				INodeExt target = mapNodeList.get(edge.getTarget());
				IEdgeExt newedge = GraphExtentionFactory.createEdgeExtention(source, target);
				spanningTree.getEdgeList().add(newedge);
			}
		}
		else{
			if(!createNewGraph){
				edge.getSource().getOutgoingEdgeList().remove(edge);
				edge.getTarget().getIncomingEdgeList().remove(edge);
				spanningTree.getEdgeList().remove(edge);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.algorithms.BFSBase#visitedNode(com.drgarbage.controlflowgraph.intf.INodeExt)
	 */
	@Override
	protected void visitedNode(INodeExt node) {
		/* nothing to do */
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.algorithms.BFSBase#enqueue(com.drgarbage.controlflowgraph.intf.INodeExt)
	 */
	@Override
	protected void enqueue(INodeExt node) {
		/* nothing to do */
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.algorithms.BFSBase#dequeue(com.drgarbage.controlflowgraph.intf.INodeExt)
	 */
	@Override
	protected void dequeue(INodeExt node) {
		/* nothing to do */
	}

}
