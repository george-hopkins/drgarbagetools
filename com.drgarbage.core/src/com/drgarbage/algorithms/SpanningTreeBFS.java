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

import java.util.ArrayList;
import java.util.List;

import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;

/**
 *  @author Sergej Alekseev  
 *  @version $Revision$
 *  $Id: SpanningTreeBFS.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class SpanningTreeBFS extends BFSBase {

	List<IEdgeExt> spanningTreeEdges;

	/**
	 * Return spanning tree edges.
	 * @return list of edges
	 */
	public List<IEdgeExt> getSpanningTreeEdges() {
		return spanningTreeEdges;
	}
	
	public void start(IDirectedGraphExt graph) throws ControlFlowGraphException{
		spanningTreeEdges = new ArrayList<IEdgeExt>();
		
		INodeListExt nodeList = graph.getNodeList();
		
		/* mark start node as visited */
		for(int i = 0; i < nodeList.size(); i++ ){
			INodeExt node= nodeList.getNodeExt(i);
			List<INodeExt> list  = (List<INodeExt>)node.getData();

			/* for all original equivalenceClassList from the equivalence class node */
			for(INodeExt n: list){
				if(n.getByteCodeOffset() == 0){
					node.setVisited(true);
					if(debug)log("Start node: " + node.toString() + " " + node.getData());				
					bfs(node);
					break;
				}
			}
		}
		
		for(int i = 0; i < nodeList.size(); i++ ){
			INodeExt node= nodeList.getNodeExt(i);
			if(node.getIncomingEdgeList().size()== 0 && !node.isVisited()){
				if(debug)log("Start node: " + node.toString() + " " + node.getData());				
				bfs(node);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.BFSBase#dequeue(com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt)
	 */
	@Override
	protected void dequeue(INodeExt node) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.BFSBase#enqueue(com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt)
	 */
	@Override
	protected void enqueue(INodeExt node) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.BFSBase#visitedEdge(com.drgarbage.visualgraphic.controlflowgraph.intf.IEdgeExt)
	 */
	@Override
	protected void visitedEdge(IEdgeExt edge) {
		if(edge.getTarget().isVisited()){
			spanningTreeEdges.add(edge);
		}
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.BFSBase#visitedNode(com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt)
	 */
	@Override
	protected void visitedNode(INodeExt node) {
	}

}
