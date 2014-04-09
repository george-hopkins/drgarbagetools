/**
 * Copyright (c) 2008-2013, Dr. Garbage Community
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
import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;

/**
 * An iterative algorithm for finding a maximum matching in a  tree based on DFS.
 * 
 * @author Sergej Alekseev
 * 
 * @version $Revision$
 * 			$Id$
 */
public class MaxMatchTreeDFS extends DFSForward {

	/* graph reference */
	private IDirectedGraphExt graph;
	
	/**
	 * List of matched edges.
	 */
	private IEdgeListExt matchedEdges;
	
	/**
	 * Returns the list of matched edges.
	 * @return the matchedEdges
	 */
	public IEdgeListExt getMatchedEdges() {
		return matchedEdges;
	}

	public void start(IDirectedGraphExt g) throws ControlFlowGraphException{
		graph = g;
		
		matchedEdges = GraphExtentionFactory.createEdgeListExtention();
		
		/* initialize node counters */
		INodeListExt nodes = graph.getNodeList();
		for(int i = 0; i < nodes.size(); i++){
			nodes.getNodeExt(i).setCounter(0);
		}
		
		super.start(graph);
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.algorithms.DFSBase#postHandling()
	 */
	@Override
	protected void postHandling() throws ControlFlowGraphException {
		/* reset cpounters */
		INodeListExt nodes = graph.getNodeList();
		for(int i = 0; i < nodes.size(); i++){
			nodes.getNodeExt(i).setCounter(0);
		}
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.algorithms.DFSBase#visitNode(com.drgarbage.controlflowgraph.intf.INodeExt)
	 */
	@Override
	public void visitNode(INodeExt node) {
		/* nothing to do */
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.algorithms.DFSBase#postVisitNode(com.drgarbage.controlflowgraph.intf.INodeExt)
	 */
	@Override
	public void postVisitNode(INodeExt node) {
		/* nothing to do */
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.algorithms.DFSBase#visitEdge(com.drgarbage.controlflowgraph.intf.IEdgeExt)
	 */
	@Override
	public void visitEdge(IEdgeExt edge) {
		/* nothing to do */
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.algorithms.DFSBase#postVisitEdge(com.drgarbage.controlflowgraph.intf.IEdgeExt)
	 */
	@Override
	public void postVisitEdge(IEdgeExt edge) {
		INodeExt source = edge.getSource();
		INodeExt target = edge.getTarget();
		
		if(source.getCounter() == 0 
				&&  target.getCounter() == 0){
			matchedEdges.add(edge);
			source.setCounter(1);
			target.setCounter(1);
		}
	}

}
