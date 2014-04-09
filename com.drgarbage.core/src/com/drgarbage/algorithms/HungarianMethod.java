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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.GraphUtils;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;

/**
 * An implementation of the Hungarian method for solving Optimal Assignment
 * Problems (Finding the minimum weighted matching in a bipartite graph).
 * 
 * Complexity of the algorithm is <code>O(mn^2)</code>, where <code>m</code> 
 * the number of edges and <code>n</code> the number of nodes 
 * in the graph <code>G</code>.
 * <br>
 * 
 * NOTE: The edge property counter is used for storing weights. 
 * 
 * @author Sergej Alekssev, Artem Garishin
 * @version $Revision$
 * $Id$
 */
public class HungarianMethod {
		
	/**
	 * Map to store original weights.
	 */
	private Map<IEdgeExt, Integer> weights = new HashMap<IEdgeExt, Integer>(); 

	/**
	 * Default constructor.
	 */
	public HungarianMethod(){
		DEBUG = false;
	}
	
	/**
	 * Creates an algorithm object.
	 * @param b set to <code>true</code> for debugging
	 */
	public HungarianMethod(boolean b){
		DEBUG = b;
	}

	/**
	 * Executes the algorithm Hungarian Method.
	 * @param graph the bipartite graph
	 * @param partA the first partition
	 * @param partB the second partition
	 * 
	 * @return the list of matched edges 
	 */
	public List<IEdgeExt>  execute(IDirectedGraphExt graph, List<INodeExt> partA, List<INodeExt> partB) {
		GraphUtils.clearGraph(graph);
		
		/* store weights in a map */
		IEdgeListExt edgeList1 = graph.getEdgeList();
		for(int k = 0; k < edgeList1.size(); k++){
			IEdgeExt e = edgeList1.getEdgeExt(k);
			weights.put(e, e.getCounter());
		}

		/* subtract minimum value from rows and columns to create lots of zeroes */
		reduceGraph(partA, partB);
		printBipartiteGraph(partA, partB);

		/* start iteration */
		List<INodeExt> coveredNodes = getMin(graph, partA, partB);
		debug("Min Number of covered rows/column=" + coveredNodes.size());

		while(coveredNodes.size() != partA.size()){

			updateGraph(graph, coveredNodes);
			printBipartiteGraph(partA, partB);

			GraphUtils.clearGraph(graph);
			
			coveredNodes = getMin(graph, partA, partB);
			debug("Min Number of covered rows/column=" + coveredNodes.size());
		}

		GraphUtils.clearGraph(graph);
		return findMatching(graph, partA, partB);

	}

	/**
	 * Returns the list of nodes as minimum coverage
	 * of the zero elements.
	 * <br>
	 * Example: Input graph (matrix)
	 *	<pre>
	 *  	  b1  b2  b3
	 *  	  |   |
	 *  a1	--7---0---0--
	 *  	  |   |
	 *  a2	  0   3   3
	 *  	  |   |
	 *  a3	  0   0   5
	 *  	  |   |
	 *  </pre>
	 *  The minimum list of nodes includes <code>{a1, b1, b2}</code>.
	 *   
	 * @param graph the bipartite graph
	 * @param partA the first partition
	 * @param partB the second partition
	 * @return
	 */
	private List<INodeExt> getMin(IDirectedGraphExt graph, List<INodeExt> partA, List<INodeExt> partB){

		List<INodeExt> coveredNodes = new ArrayList<INodeExt>();
		INodeExt n = null;
		do{
			n = countNumberOfZeros(graph.getNodeList());
			if(n != null){
				coveredNodes.add(n);
				debug("Node with most zeros: " + n.getData());

				IEdgeListExt inList = n.getIncomingEdgeList();
				IEdgeListExt outList = n.getOutgoingEdgeList();
				for(int i = 0; i < inList.size(); i++){
					IEdgeExt e = inList.getEdgeExt(i);
					e.setVisited(true);
				}

				for(int i = 0; i < outList.size(); i++){
					IEdgeExt e = outList.getEdgeExt(i);
					e.setVisited(true);
				}
			}

		}
		while(n != null);

		return coveredNodes;
	}

	/**
	 * Counts the number of edges with zero weights for each node.
	 * @param nodes the list of nodes
	 * @return the node with highest number of edges with zero weights 
	 */
	private static INodeExt countNumberOfZeros(INodeListExt nodes){
		INodeExt nodeWithMostZeros = null;
		int max = 0;
		for(int j = 0; j < nodes.size(); j++){
			INodeExt n = nodes.getNodeExt(j);
			if(!n.isVisited()){
				int count = 0;

				IEdgeListExt inList = n.getIncomingEdgeList();
				IEdgeListExt outList = n.getOutgoingEdgeList();

				/* for all incoming edges */
				for(int i = 0; i < inList.size(); i++){
					IEdgeExt e = inList.getEdgeExt(i);
					if(!e.isVisited() && e.getCounter() == 0){
						count++;
					}
				}

				/* for all outgoing edges */
				for(int i = 0; i < outList.size(); i++){
					IEdgeExt e = outList.getEdgeExt(i);
					if(!e.isVisited() && e.getCounter() == 0){
						count++;
					}
				}

				if(max < count){
					max = count;
					nodeWithMostZeros = n;
				}
			}
		}

		return nodeWithMostZeros;
	}

	/**
	 * Reduction of the graph is done in two steps: <br>
	 * <ol>
	 * <li>
	 * Find in the first partition for each node the minimum value, 
	 * assigned to it's edges and subtract this value from all edges 
	 * of that node.
	 * </li>
	 * <li>
	 * Find in the second partition for each node the minimum value, 
	 * assigned to it's edges and subtract this value from all edges 
	 * of that node.
	 * </li>
	 * </ol>
	 * 
	 * NOTE:
	 * The reduction is equivalent to the reduction in the matrix: 
	 * <ol>
	 * <li>
	 * Find the smallest element in each row and subtract 
     * it's values from all elements of that row.
     * </li>
     * <li>
     * Find the smallest element in each column and subtract 
     * it's values from all elements of that column.
	 * </li>
	 * </ol>
	 *  
	 * @param partA the first partition
	 * @param partB the second partition
	 */
	private static void reduceGraph(List<INodeExt> partA, List<INodeExt> partB) {

		/* find the minimum value in the first partition for each node */
		for(INodeExt n: partA){
			int minValInRow = Integer.MAX_VALUE;

			IEdgeListExt inList = n.getIncomingEdgeList();
			IEdgeListExt outList = n.getOutgoingEdgeList();
			List<IEdgeExt> allEdges = new ArrayList<IEdgeExt>(inList.size() + outList.size());

			/* for all incoming edges */
			for(int i = 0; i < inList.size(); i++){
				IEdgeExt e = inList.getEdgeExt(i);
				allEdges.add(e);
				if (minValInRow > e.getCounter()) {
					minValInRow = e.getCounter();
				}
			}

			/* for all outgoing edges */
			for(int i = 0; i < outList.size(); i++){
				IEdgeExt e = outList.getEdgeExt(i);
				allEdges.add(e);
				if (minValInRow > e.getCounter()) {
					minValInRow = e.getCounter();
				}
			}

			/* subtract the minimum value from all edges of the current node */
			for(IEdgeExt e: allEdges){
				e.setCounter(e.getCounter() - minValInRow);
			}

		}

		/* find the minimum value in the second partition for each node*/
		for(INodeExt n: partB){
			int minValInCol = Integer.MAX_VALUE;

			IEdgeListExt inList = n.getIncomingEdgeList();
			IEdgeListExt outList = n.getOutgoingEdgeList();
			List<IEdgeExt> allEdges = new ArrayList<IEdgeExt>(inList.size() + outList.size());

			/* for all incoming edges */
			for(int i = 0; i < inList.size(); i++){
				IEdgeExt e = inList.getEdgeExt(i);
				allEdges.add(e);
				if (minValInCol > e.getCounter()) {
					minValInCol = e.getCounter();
				}
			}

			/* for all outgoing edges */
			for(int i = 0; i < outList.size(); i++){
				IEdgeExt e = outList.getEdgeExt(i);
				allEdges.add(e);
				if (minValInCol > e.getCounter()) {
					minValInCol = e.getCounter();
				}
			}

			/* subtract the minimum value from all edges of the current node */
			for(IEdgeExt e: allEdges){
				e.setCounter(e.getCounter() - minValInCol);
			}

		}
	}

	/**
	 * Updates the edge weights. 
	 * <ul>
	 * <li>
	 * Find the minimum weight value of the uncovered edges and 
	 * add this minimum value to every covered edge. If an element is 
	 * covered twice, add the minimum element to it twice.
	 * </li>
	 * <li>
	 * Find the minimum weight value of all edges in the graph and
	 * subtract this minimum value from every edge in the graph.
	 * </li>
	 * </ul>
	 * <br>
	 * Example as matrix representation:
	 *	<pre>
	 *  	  b1  b2  b3
	 *  	  |
	 *  a1	--6---0---0--
	 *  	  |
	 *  a2	  0   4   4
	 *  	  |
	 *  a3	  0   1   6
	 *  	  |
	 *  </pre> 
	 *  Add the minimum value of the uncovered elements to every covered element. 
	 *  If an element is covered twice, add the minimum element to it twice.
	 *  <pre>
	 *  	b1  b2  b3
	 *  a1	8   1   1
	 *  a2	1   4   4
	 *  a3	1   1   6
	 *  </pre> 
	 * Subtract the minimum value from every element in the matrix.
	 * 	<pre>
	 *  	b1  b2  b3
	 *  a1	7   0   0
	 *  a2	0   3   3
	 *  a3	0   0   5
	 *  </pre> 
	 *  
	 * @param graph the bipartite graph
	 * @param coveredNodes the set of covered nodes (row or columns in the matrix representation)
	 */
	private void updateGraph(IDirectedGraphExt graph, List<INodeExt> coveredNodes){
		
		/*	find the minimum weight value of the uncovered edges. */
		int minUncovered = Integer.MAX_VALUE;
		List<IEdgeExt> uncoveredEdges = new ArrayList<IEdgeExt>();
		List<IEdgeExt> coveredEdges = new ArrayList<IEdgeExt>();
		IEdgeListExt edges = graph.getEdgeList();
		for(int j = 0; j < edges.size(); j++){
			IEdgeExt e = edges.getEdgeExt(j);
			if(e.isVisited()){
				coveredEdges.add(e);
			}
			else{
				uncoveredEdges.add(e);
				if(minUncovered > e.getCounter()){
					minUncovered = e.getCounter();
				}
			}
		}
		debug("MinUncovered=" + minUncovered);

		/*	
		 * add the minimum value to every covered edge. If an element is 
		 * covered twice, add the minimum element to it twice.
		 */
		for(INodeExt n: coveredNodes){
			IEdgeListExt inList = n.getIncomingEdgeList();
			IEdgeListExt outList = n.getOutgoingEdgeList();
			for(int i = 0; i < inList.size(); i++){
				IEdgeExt e = inList.getEdgeExt(i);
				e.setCounter(e.getCounter() + minUncovered);
			}

			for(int i = 0; i < outList.size(); i++){
				IEdgeExt e = outList.getEdgeExt(i);
				e.setCounter(e.getCounter() + minUncovered);
			}
		}

		/* find the minimum weight value of all edges in the graph */
		int min = Integer.MAX_VALUE;
		edges = graph.getEdgeList();
		for(int j = 0; j < edges.size(); j++){
			IEdgeExt e = edges.getEdgeExt(j);
			if(min > e.getCounter()){
				min = e.getCounter();
			}
		}

		debug("MinElement=" + min);

		/* subtract the minimum element from every element in the graph. */
		for(int j = 0; j < edges.size(); j++){
			IEdgeExt e = edges.getEdgeExt(j);
			e.setCounter(e.getCounter() - min);
		}
	}

	/**
	 * To find the matching in the matrix a set of zeros has to be chosen
	 * so that each row or column has only one selected.
	 * <br>
	 * We construct a bipartite graph by selecting the edges with zero weights
	 * and apply the augmenting path algorithm to find the matching 
	 * of maximum cardinality.
	 * 
	 * @param graph1 the bipartite graph
	 * @param partA1 the first partition
	 * @param partB1 the second partition
	 * 
	 * @return the list of matched edges
	 * 
	 * @see MaxCardBipartiteMatching
	 */
	private List<IEdgeExt> findMatching(IDirectedGraphExt graph1, List<INodeExt> partA1, List<INodeExt> partB1){
		debug("findMatching ...");
		
		/* create a bipartite subgraph */
		IDirectedGraphExt graph = GraphExtentionFactory.createDirectedGraphExtention();

		/* create nodes */
		List<INodeExt> partA2 = new ArrayList<INodeExt>(); 
		for(INodeExt n: partA1){
			INodeExt a1 = GraphExtentionFactory.createNodeExtention(n.getData());
			graph.getNodeList().add(a1);
			partA2.add(a1); 
		}

		List<INodeExt> partB2 = new ArrayList<INodeExt>();
		for(INodeExt n: partB1){
			INodeExt b1 = GraphExtentionFactory.createNodeExtention(n.getData());
			graph.getNodeList().add(b1);
			partB2.add(b1); 
		}

		/* create edges */
		IEdgeListExt edgeList1 = graph1.getEdgeList();
		for(int k = 0; k < edgeList1.size(); k++){
			IEdgeExt e = edgeList1.getEdgeExt(k);
			if(e.getCounter() == 0){
				INodeExt a = e.getSource();
				int i = partA1.indexOf(a);
				INodeExt b = e.getTarget();
				int j = partB1.indexOf(b);
				IEdgeExt edge = GraphExtentionFactory.createEdgeExtention(partA2.get(i), partB2.get(j));				
				graph.getEdgeList().add(edge);
				
				/* assign the reference to the original edge */
				edge.setData(e);
			}
		}

		printGraph(graph);
		printBipartiteGraph(partA2, partB2);

		/* execute the augmenting path algorithm */
		MaxCardBipartiteMatching mbm = new MaxCardBipartiteMatching(DEBUG);
		mbm.start(graph, partA2, partB2);

		Set<IEdgeExt> edges = mbm.getMatchedEdges();
		
		/* copy the references of the original edges to the list */
		List<IEdgeExt> origEdges = new ArrayList<IEdgeExt>();
		for(IEdgeExt e : edges){
			IEdgeExt oe = (IEdgeExt) e.getData();
			
			/* restore original weights */
			oe.setCounter(weights.get(oe));
			
			origEdges.add(oe);
			
			debug(oe.getSource().getData() + "-" + oe.getTarget().getData());
		}

		return origEdges;
	}
	
	/* 
	 * The Methods in this section are used for purely debugging purposes 
	 */
	
	/**
	 * Debugging flag. Set <code>true</code> to enable printing the
	 * debugging messages.
	 */
	protected static boolean DEBUG = true;
	
	/**
	 * Prints a message for debugging purposes.
	 * <br> 
	 * NOTE: The method is disabled if the debugging flag set to false.
	 * 
	 * @param msg the text message 
	 * @see #DEBUG
	 */
	protected static void debug(String msg){
		if(!DEBUG) return;
		
		System.out.println(msg);
	}
	
	/**
	 * Prints the bipartite graph as a matrix for
	 * debugging purposes.
	 * @param partA the first node partition  
	 * @param partB the second node partition
	 */
	protected static void printBipartiteGraph(List<INodeExt> partA, List<INodeExt> partB) {
		if(!DEBUG){
			return;
		}
		
		StringBuffer buf = new StringBuffer();
		buf.append('\t');
		
		for(INodeExt n: partB){
			buf.append(n.getData().toString());
			buf.append(' ');	
		}
		buf.append('\n');
		
		for(INodeExt n: partA){
			buf.append(n.getData().toString());
			buf.append('\t');
			
			IEdgeListExt edges = n.getIncomingEdgeList();
			for (int i = 0; i < edges.size(); i++) {
				IEdgeExt e = edges.getEdgeExt(i);
				buf.append(e.getCounter());
				buf.append("  ");
			}
			
			edges = n.getOutgoingEdgeList();
			for (int i = 0; i < edges.size(); i++) {
				IEdgeExt e = edges.getEdgeExt(i);
				buf.append(e.getCounter());
				buf.append("  ");
			}
			
			buf.append('\n');	
		}
		
		debug(buf.toString());
	}

	/**
	 * Prints the graph.
	 * @param g the graph
	 */
	private static void printGraph(IDirectedGraphExt g) {
		if(!DEBUG){
			return;
		}
		
		System.out.println("Print Graph:");

		System.out.println("Nodes:");
		for (int i = 0; i < g.getNodeList().size(); i++) {
			System.out.println("  " + g.getNodeList().getNodeExt(i).getData());
		}

		System.out.println("Edges:");
		for (int i = 0; i < g.getEdgeList().size(); i++) {
			IEdgeExt e = g.getEdgeList().getEdgeExt(i);
			System.out.println("  " 
					+ e.getSource().getData() 
					+ " - "
					+ e.getTarget().getData());
		}
	}
}