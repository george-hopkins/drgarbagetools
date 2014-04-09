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

import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;

/**
 * Algorithm for finding a maximum weighted matching in a bipartite graph.
 * The maximum weighted matching is defined as a matching where the sum 
 * of the edge values in the matching have a maximal value. 
 * This algorithm uses the {@link HungarianMethod Hungarian method}.
 * Complexity of the algorithm is <code>O(mn^2)</code>.
 * If the graph is not complete bipartite, missing edges are inserted 
 * with zero value.
 * 
 * NOTE: The edge property counter is used for storing weights. 
 *  
 * @see HungarianMethod
 * 
 * @author Artem Garishin, Sergej Alekssev
 * @version $Revision$
 * $Id$
 */
public class MaxWeightedBipartiteMatching extends HungarianMethod{
	
	/**
	 * Default constructor.
	 */
	public MaxWeightedBipartiteMatching() {
		DEBUG = false;
	}
	
	/**
	 * Creates an algorithm object.
	 * @param b set to <code>true</code> for debugging
	 */
	public MaxWeightedBipartiteMatching(boolean b) {
		super(b);
	}

	/**
	 * Executes the algorithm Max Weighted Bipartite Matching.
	 * @param graph the bipartite graph
	 * @param partA the first partition
	 * @param partB the second partition
	 * 
	 * @return the list of matched edges 
	 */
	public List<IEdgeExt>  execute(IDirectedGraphExt graph, List<INodeExt> partA, List<INodeExt> partB) {

		List<INodeExt> partAnew = new ArrayList<INodeExt>();
		List<INodeExt> partBnew = new ArrayList<INodeExt>();
		IDirectedGraphExt graphNew = createSymetricalCompleteBipartiteGraph(partA, partAnew, partB, partBnew);
		printBipartiteGraph(partAnew, partBnew);
		
		convertMinToMax(graphNew);
		printBipartiteGraph(partAnew, partBnew);
		
		debug("Execute Hungarian Method");
		
		List<IEdgeExt>  list = super.execute(graphNew, partAnew, partBnew);
		List<IEdgeExt> listOrig = new ArrayList<IEdgeExt>(list.size());
		for(IEdgeExt e: list){
			if(e.getData() != null){ /* skip dummy edges */
				listOrig.add((IEdgeExt) e.getData());
			}
		}
		
		return listOrig;
	}
	
	/**
	 * Creates a <b>symmetrical complete bipartite graph</b> <code>G=(A + B, E)</code>. 
	 * <br>
	 * The <b>complete</b> bipartite graph is graph where every vertex
	 * of the first partition is connected to every vertex of 
	 * the second partition
	 * <code>G=(A + B, E)</code> and <code>E = A x B</code>.
	 * <br>
	 * The <b>symmetrical</b> bipartite graph is graph where the number 
	 * of vertices in the first partition is equal to the number
	 * of vertices in the second partition.
	 * <code>|A| = |B|</code>.
	 * <br> 
	 * The dummy vertices and dummy edges with the value zero 
	 * are added to make the graph symmetrical and complete.
	 * 
	 * @param graph the bipartite graph
	 * @param partAnew the reference to the new list
	 * @param partA the first partition
	 * @param partBnew the reference to the new list
	 * @return the symmetrical complete bipartite graph 
	 */
	private IDirectedGraphExt createSymetricalCompleteBipartiteGraph(
			List<INodeExt> partA, 
			List<INodeExt> partAnew, 
			List<INodeExt> partB, 
			List<INodeExt> partBnew){

		/* create a  graph */
		IDirectedGraphExt graph = GraphExtentionFactory.createDirectedGraphExtention();

		/* copy vertices */
		for(INodeExt n: partA){
			INodeExt a = GraphExtentionFactory.createNodeExtention(n);
			graph.getNodeList().add(a);
			partAnew.add(a); 
		}

		for(INodeExt n: partB){
			INodeExt b = GraphExtentionFactory.createNodeExtention(n);
			graph.getNodeList().add(b);
			partBnew.add(b); 
		}
		
		/* make the graph symmetrical by adding dummy vertices */
		if(partA.size() > partB.size()){
			int diff = partA.size() - partB.size();
			for(int i = 0; i < diff; i++){
				INodeExt b = GraphExtentionFactory.createNodeExtention(null);
				graph.getNodeList().add(b);
				partBnew.add(b);
			}
		}
		else{
			int diff = partB.size() - partA.size();
			for(int i = 0; i < diff; i++){
				INodeExt a = GraphExtentionFactory.createNodeExtention(null);
				graph.getNodeList().add(a);
				partAnew.add(a); 
			}
		}
		
		/* create edges */
		IEdgeListExt edges = graph.getEdgeList();
		
		for(INodeExt aNew: partAnew){
			
			Set<INodeExt> s = new HashSet<INodeExt>();
			s.addAll(partBnew);
			
			Object o = aNew.getData();
			if(o != null){
				INodeExt n = (INodeExt)o;

				IEdgeListExt in = n.getIncomingEdgeList();
				for(int i = 0; i < in.size(); i++){
					IEdgeExt e = in.getEdgeExt(i);

					int b = partB.indexOf(e.getSource());
					INodeExt bNew = partBnew.get(b);
					
					IEdgeExt edgeNew = GraphExtentionFactory.createEdgeExtention(bNew, aNew);
					edgeNew.setData(e);
					edgeNew.setCounter(e.getCounter());
					edges.add(edgeNew);
					
					s.remove(bNew);
				}

				IEdgeListExt out = n.getOutgoingEdgeList();
				for(int i = 0; i < out.size(); i++){
					IEdgeExt e = out.getEdgeExt(i);

					int b = partB.indexOf(e.getTarget());
					INodeExt bNew = partBnew.get(b);
					
					IEdgeExt edgeNew = GraphExtentionFactory.createEdgeExtention(aNew, bNew);
					edgeNew.setData(e);
					edgeNew.setCounter(e.getCounter());
					edges.add(edgeNew);
					
					s.remove(bNew);
				}
			}
			
			/*make the graph complete by adding the dummy edges */
			for(INodeExt n: s){
				IEdgeExt edgeNew = GraphExtentionFactory.createEdgeExtention(aNew, n);
				edgeNew.setData(null);
				edgeNew.setCounter(0);
				edges.add(edgeNew);
			}
			
			
		}		
		
		return graph;
	}
	
	/**
	 * Conversion for maximization is realized by multiplication 
	 * all weights by -1 and adding a constant value (max value + 1)
	 * to each weight in order to have positive numbers.
	 *  
	 * @param graph the bipartite graph
	 */
	private void convertMinToMax(IDirectedGraphExt graph) {

		int max = 0;
		IEdgeListExt edges = graph.getEdgeList();
		
		/* find the maximum value */
		for(int j = 0; j < edges.size(); j++){
			IEdgeExt e = edges.getEdgeExt(j);
			if(max < e.getCounter()){
				max = e.getCounter();
			}
		}
		
		debug("Multiply by -1 and add maximum value: " + max);
		
		for(int j = 0; j < edges.size(); j++){
			IEdgeExt e = edges.getEdgeExt(j);
			e.setCounter((e.getCounter() * -1) + max + 1);
		}
	}
}
