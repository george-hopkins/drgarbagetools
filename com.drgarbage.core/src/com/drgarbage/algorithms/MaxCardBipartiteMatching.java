/**
 * Copyright (c) 2008-2014, Dr. Garbage Community
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.drgarbage.controlflowgraph.intf.GraphUtils;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.MarkEnum;

/**
 * The general idea of the algorithm for construction of a maximum matching 
 * <code>M</code> for the bipartite graph <code>G = (A + B, E)</code> 
 * is presented in the following figure:
 * <pre> 
 *  Maximum Bipartite Matching
 *    M = 0
 *    While ( exists an augmenting path p)
 *       M = M + (switch) p
 *    return M
 * </pre>
 *
 * A path <code>p</code> is called an augmenting path for the 
 * matching <code>M</code> if: <br>
 * 1. The two end points of <code>p</code> are unmatched by <code>M</code>. <br>
 * 2. The edges of <code>p</code> alternate between edges in <code>M</code> 
 * and edges not in <code>M</code>.<br>
 * 
 * If <code>M</code> has an augmenting path <code>p</code> then switching the edges
 * along the path <code>p</code> from in-to-out of M gives
 * a matching with one more edge.
 *
 * The current implementation of the algorithm is presented in the following figure:  
 * <pre>
 * Maximum Bipartite Matching
 * 1.  Start DFS for all vertices in A.
 * 2.  Visit in alternating order an edge e in M and e not in M 
 * 3.  If at any point the DFS visits an unmatched vertex from B 
 *     then augmenting path p is found.
 * 3.1   Stop DFS and switch the path p 
 * </pre>
 * 
 * Complexity of the algorithm is <code>O(mn)</code>, where <code>m</code> 
 * the number of edges and <code>n</code> the number of nodes 
 * in the graph <code>G</code>.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class MaxCardBipartiteMatching {

	private List<IEdgeExt> path = null;
	private Set<IEdgeExt> matchedEdges = new HashSet<IEdgeExt>(); 

	/**
	 * Default constructor.
	 */
	public MaxCardBipartiteMatching(){
		DEBUG = false;
	}
	
	/**
	 * Creates an algorithm object.
	 * @param b set to <code>true</code> for debugging
	 */
	public MaxCardBipartiteMatching(boolean b){
		DEBUG = b;
	}
	
	/**
	 * @return the matchedEdges
	 */
	public Set<IEdgeExt> getMatchedEdges() {
		return matchedEdges;
	}


	/**
	 * Starts matching algorithm.
	 * @param graph the bipartite graph
	 * @param partA the first partition
	 * @param partB the second partition
	 */
	public void start(IDirectedGraphExt graph, List<INodeExt> partA, List<INodeExt> partB) {
		debug("MaxCardBipartiteMatching start ...");
		
		for(INodeExt n: partA){
			path = new ArrayList<IEdgeExt>();
			GraphUtils.clearGraph(graph);
			n.setMark(MarkEnum.RED);
			dfs(n);
		}
		
		GraphUtils.clearGraph(graph);
		GraphUtils.clearGraphColorMarks(graph);
		
		debug("MaxCardBipartiteMatching finished");
	}

	/**
	 * Depth first search.
	 * @param node the start node
	 * @return <code>true</code> if the DFS has to be stopped, 
	 * otherwise <code>false</code>.
	 */
	protected boolean dfs(INodeExt node){	
		/* print some debugging information */
		printNodeInfo(node);
		printPathInfo(path);
		
		if(node.isVisited()){
			return false;
		}

		node.setVisited(true);
		
		/* 
		 * Follow edge, already matched in the previous step, 
		 * if the current node from the partition B is marked
		 * red (it belongs to the matched edge). 
		 */
		if( (path.size() % 2) == 1 &&
				node.getMark() == MarkEnum.RED){

			IEdgeExt e = findMatchedEdge(node);

			/* print debugging information */
			printEdgeInfo(e);
			
			if(e != null){
				e.setVisited(true);
				
				/* add the edge to the path */
				path.add(e);

				if(dfs(node.equals(e.getSource()) ? 
						e.getTarget() : e.getSource())){
					return true;
				}
				
				/* 
				 * back from the recursion:
				 * remove the edge from path 
				 */
				path.remove(e);
				
				return false;
			}
			else{
				/* should never happen */
				debug("ERROR: edge not found in the list of mached edges.");
			}
		}

		/* the depth first search recursion */
		
		/* for all incoming edges */
		IEdgeListExt inList = node.getIncomingEdgeList();
		for(int i = 0; i < inList.size(); i++){
			IEdgeExt e = inList.getEdgeExt(i);
			if(!e.isVisited()){
				e.setVisited(true);

				/* add the edge to path */
				path.add(e);
				
				/* for debugging */
				printPathInfo(path);
				
				/* edge visitor hook */
				if(visitEdge(e)){
					/* stop the dfs recursion*/
					return true;
				}

				/* 
				 * stop the recursion if the dfs returns true
				 */
				if(dfs(e.getSource())){
					return true; 
				}

				/* 
				 * back from the recursion:
				 * remove the edge from path 
				 */
				path.remove(e);
			}
		}

		/* for all outgoing edges */
		IEdgeListExt outList = node.getOutgoingEdgeList();
		for(int i = 0; i < outList.size(); i++){		
			IEdgeExt e = outList.getEdgeExt(i);
			if(!e.isVisited()){
				e.setVisited(true);

				/* add the edge to path */
				path.add(e);
				
				/* for debugging */
				printPathInfo(path);
				
				/* edge visitor hook */
				if(visitEdge(e)){ 
					/* stop the dfs recursion*/
					return true;
				}

				/* 
				 * stop the recursion if the dfs returns true
				 */
				if(dfs(e.getTarget())){
					return true;
				}

				/* 
				 * back from the recursion:
				 * remove the edge from path 
				 */
				path.remove(e);
			}
		}
		
		return false;
	}

	/**
	 * Processes an edge and returns <code>true</code>
	 * if the DFS has to be stopped, otherwise 
	 * <code>false</code>.
	 * @param edge the edge has to be processed
	 * @return <code>true</code> or <code>false</code>
	 */
	public boolean visitEdge(IEdgeExt edge) {

		edge.setVisited(true);

		if(path.size() % 2 == 1){ /* A -> B */
			INodeExt n = edge.getSource().isVisited() 
					? edge.getTarget() /* outgoing edge */
							: edge.getSource(); /* incoming edge */

			if(n.getMark() != MarkEnum.RED){ /* an augmenting path found */
				n.setMark(MarkEnum.RED);

				switchAugementingPath();

				/* stop dfs */
				return true;
			}
		}

		return false;
	}

	/**
	 * Alternates an augmenting path and actualizes 
	 * the list of matched edges.
	 */
	private void switchAugementingPath(){
		debug("Switch path ...");
		
		for(int i = path.size() - 1; i >= 0; i--){
			IEdgeExt e = path.get(i);
			if(i % 2 == 0){
				matchedEdges.add(e);
				
				/* mark the matched edge */
				e.setMark(MarkEnum.RED);
			}
			else{
				matchedEdges.remove(e);
				
				/* mark the matched edge */
				e.setMark(MarkEnum.DEFAULT);
			}
		}
	}
	
	/**
	 * Finds the edge already matched in the previous step.
	 * @param node the node
	 * @return the edge or null
	 */
	private IEdgeExt findMatchedEdge(INodeExt node) {
		IEdgeListExt inList = node.getIncomingEdgeList();
		
		/* for all incoming edges */
		for(int i = 0; i < inList.size(); i++){
			IEdgeExt e = inList.getEdgeExt(i);
			
			if(e.isVisited() == false && e.getMark() == MarkEnum.RED){
				return e;
			}
		}

		/* for all outgoing edges */
		IEdgeListExt outList = node.getOutgoingEdgeList();
		for(int i = 0; i < outList.size(); i++){		
			IEdgeExt e = outList.getEdgeExt(i);
			if(e.isVisited() == false && e.getMark() == MarkEnum.RED){
				return e;
			}
		}
		
		return null;
	}

	/**
	 * Debugging flag. Set <code>true</code> to enable printing
	 * debugging messages.
	 */
	protected static boolean DEBUG = true;
	
	/**
	 * Prints a message for debugging purposes.
	 * <br> 
	 * NOTE: The method is disabled if the debugging flag is false.
	 * 
	 * @param msg the text message 
	 * @see #DEBUG
	 */
	protected static void debug(String msg){
		if(!DEBUG) return;
		
		System.out.println(msg);
	}
	
	/**
	 * Prints the elements of paths for debugging purposes.
	 * <br> 
	 * NOTE: The method is disabled if the debugging flag is false.
	 * 
	 * @param path the ordered list of edges
	 * @see #DEBUG
	 */
	private void printPathInfo(List<IEdgeExt> path){
		if(!DEBUG) return;
	
		StringBuffer buf = new StringBuffer("Path: ");
		buf.append("size = ");
		buf.append(path.size());
		buf.append(" : ");
		
		for(IEdgeExt e: path){
			buf.append(e.getSource().getData());
			buf.append("-");
			buf.append(e.getTarget().getData());
			buf.append(", " );
		}
		
		debug(buf.toString());
	}
	
	/**
	 * Prints the property of a node.
	 * <br> 
	 * NOTE: The method is disabled if the debugging flag is false.
	 * 
	 * @param node the node
	 * @see #DEBUG
	 */
	private void printNodeInfo(INodeExt node){
		if(!DEBUG) return;
		
		debug("node " + node.getData() + " visited=" + node.isVisited()
				+ " red=" + (node.getMark() == MarkEnum.RED ? "true" : "false"));
	}
	
	/**
	 * Prints the property of an edge.
	 * <br> 
	 * NOTE: The method is disabled if the debugging flag is false.
	 * 
	 * @param e the edge
	 * @see #DEBUG
	 */
	private void printEdgeInfo(IEdgeExt e){
		if(!DEBUG) return;
		
		debug("Edge: " + (e == null ? 
				"null" : 
					e.getSource().getData() + "-" + e.getTarget().getData()
					+ " visited=" + e.isVisited()
					+ " red=" + (e.getMark() == MarkEnum.RED ? "true" : "false")
				));
	}
	
}
