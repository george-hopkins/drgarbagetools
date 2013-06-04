/**
 * Copyright (c) 2008 - 2013, Dr. Garbage Community
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
import com.drgarbage.controlflowgraph.intf.MarkEnum;

/**
 * 	Finds a set of back edges for the given graph. 
 *  The set is empty if the graph doesn't contain
 *  any cycles.<br>
 *  <br>
 *  
 *  The DFS colors a vertex WHITE if it visits the 
 *  vertex first time. The vertex is colored BLACK
 *  when its edge list has been examined completely.
 *  The recursion has been finished at this vertex.
 *  So any edges to a BLACK vertex cannot be back 
 *  edges. If the DFS revisit the vertex while it
 *  is still WHITE, a back edge has been detected.
 * 
 *  @author Sergej Alekseev  
 *  @version $Revision$
 *  $Id$
 */
public class FindBackEdgesDFS extends DFSForward {

	/**
	 * The list of back edges.
	 */
	private IEdgeListExt backEdgeList;

	/* (non-Javadoc)
	 * @see com.drgarbage.algorithms.DFSBase#start(com.drgarbage.controlflowgraph.intf.IDirectedGraphExt)
	 */
	public void start(IDirectedGraphExt graph) throws ControlFlowGraphException{
		backEdgeList = GraphExtentionFactory.createEdgeListExtention();
		super.start(graph);
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.algorithms.DFSBase#start(com.drgarbage.controlflowgraph.intf.IDirectedGraphExt, com.drgarbage.controlflowgraph.intf.INodeExt)
	 */
	public void start(IDirectedGraphExt graph, INodeExt start) throws ControlFlowGraphException{
		backEdgeList = GraphExtentionFactory.createEdgeListExtention();
		super.start(graph, start);
	}
	
	/**
	 * Returns the list of back edges.
	 * @return the list of back edges
	 */
	public IEdgeListExt getBackEdgeList() {
		return backEdgeList;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.algorithms.DFSForward#visitNode(com.drgarbage.controlflowgraph.intf.INodeExt)
	 */
	@Override
	public void visitNode(INodeExt node) {
		node.setMark(MarkEnum.WHITE);	
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.algorithms.DFSForward#visitEdge(com.drgarbage.controlflowgraph.intf.IEdgeExt)
	 */
	@Override
	public void visitEdge(IEdgeExt edge) {
		if(edge.getTarget().getMark() == MarkEnum.WHITE){
			log("FOUND: " + edge);
			backEdgeList.add(edge);
		}
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.algorithms.DFSBase#postHandling()
	 */
	@Override
	protected void postHandling() throws ControlFlowGraphException {
		/* nothing to do */
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.algorithms.DFSBase#postVisitNode(com.drgarbage.controlflowgraph.intf.INodeExt)
	 */
	public void postVisitNode(INodeExt node){
		node.setMark(MarkEnum.BLACK);
	}

}
