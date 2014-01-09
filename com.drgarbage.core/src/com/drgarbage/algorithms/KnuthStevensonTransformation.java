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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;

/**
 * Knuth-Stevenson has been published:
 * BIT 13 (1973), 313-322, Optimal Measurement Points for Program Frequency Counts.
 * <br>  
 * 
 * Conceptually, the Knuth-Stevenson algorithm is a graph transformation 
 * followed by an application of a spanning tree algorithm. 
 * Given a graph <tt>G</tt> with a set <tt>V</tt> of vertices and a set <tt>E</tt>
 * of edges. The relation ~ between vertices is defined to be the smallest 
 * equivalence relation such that <tt>a ~ b</tt> if there exists vertex <tt>c</tt> 
 * and arcs <tt>c -> a</tt> and <tt>c -> b</tt>. A reduced graph is produced whose
 * vertices <tt>Vr</tt> are the equivalence classes of the original graph, 
 * and whose edges <tt>Er</tt> correspond one-to-one with the vertices of the 
 * original graph; for each vertex <tt>b from V</tt> , there exists an edge
 * in <tt>Vr</tt> from the equivalence class containing <tt>b</tt> to the 
 * class containing the successors of <tt>b</tt> (by construction, they are all 
 * in the same class). The Spanning Tree Algorithm is applied to the reduced 
 * graph to find a spanning tree, and those edges of <tt>Er</tt> not in 
 * the spanning tree specify the equivalenceClassList in <tt>V</tt> that need to be monitored.  
 * <br>  
 * An example of Knuth-Stevenson Transformation:
 * <pre>
 *   +------+                    +------------+                                      
 *   |      Y                    |            Y                                      
 *   |    +---+                  |          +---+                      +---+         
 *   |    | 1 |                  |          | 1 |                      | 1 |         
 *   |    +---+                  |          +---+                      +---+         
 *   |      |                    |            |(1)                       |(1)        
 *   |      Y                    |            Y                          Y           
 *   |    +---+                  |          +---+                      +---+         
 *   |    | 2 |---------+        |          | 2 |                      | 2 |         
 *   |    +---+         |        |          +---+                      +---+         
 *   |      |           |        |            |(2)                       |(2)        
 *   |      Y           Y        |            Y                          Y           
 *   |    +---+       +---+      |     +----------------+         +----------------+ 
 *   |    | 3 |   +-->| 7 |      |     |    3, 7, 5     |         |    3, 7, 5     | 
 *   |    +---+   |   +---+      |     +----------------+         +----------------+ 
 *   |      |     |     |        |      A |(3) |    A |(7)           |(3) |      |(7)
 *   |      Y     |     Y        |   (4)| Y    | (8)| Y              Y    | (    Y   
 *   |    +---+   |   +---+      |     +---+   |   +---+          +---+   |   +---+  
 *   |    | 4 |   +-- | 8 |      |     | 4 |   |   | 8 |          | 4 |   |   | 8 |  
 *   |    +---+       +---+      |     +---+   |   +---+          +---+   |   +---+  
 *   |      |           |        |             |                          |          
 *   |      Y           |        |             |(5)                       |(5)       
 *   |    +---+         |        |             Y                          Y          
 *   |    | 5 |<--------+        |           +---+                      +---+        
 *   |    +---+                  |(6)        | 6 |                      | 6 |        
 *   |      |                    |           +---+                      +---+        
 *   |      Y                    |             |                                     
 *   |    +---+                  +-------------+                   Result: 4, 8, 6   
 *   |    | 6 |                                                                      
 *   |    +---+                                                                      
 *   |      |                                                                        
 *   +------+                                                                        
 *                                                                                   
 *   a) Original graph G             b) transformed Graph           c) Spanning tree 
 *   </pre>
 *   
 *  @author Sergej Alekseev  
 *  @version $Revision$
 *  $Id$
 */
public class KnuthStevensonTransformation extends BFSBase {
	
	INodeExt equivalenceClassNode;
	Set<INodeExt> equivalenceClassList;

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.BFSBase#start(com.drgarbage.visualgraphic.controlflowgraph.intf.IDirectedGraphExt)
	 */
	public void start(IDirectedGraphExt graph) throws ControlFlowGraphException{
		if(graph.getNodeList().size()== 0){
			throw new ControlFlowGraphException("Node List is empty." );
		}
		
		start(graph, graph.getNodeList().getNodeExt(0));
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.BFSBase#start(com.drgarbage.visualgraphic.controlflowgraph.intf.IDirectedGraphExt, com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt)
	 */
	public void start(IDirectedGraphExt graph, INodeExt start) throws ControlFlowGraphException{
		if(debug)log("------- KnuthStevenson Transformation --------");
		
		equivalenceClassList = new HashSet<INodeExt>();
		createEquivalenceClassNode();
		addNodeToEquivalenceClass(start);
		super.start(graph, start);
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.BFSBase#dequeue(com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt)
	 */
	@Override
	protected void dequeue(INodeExt node) {
		IEdgeListExt outgoingEdges = node.getOutgoingEdgeList();
		if(outgoingEdges.size()== 0){
			return;
		}
		
		/* create a new equivalence class */
		createEquivalenceClassNode();

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
		INodeExt node = edge.getTarget();
		if(node.getData() == null){
			addNodeToEquivalenceClass(node);	
		}
		else{ /* already assigned to an equivalence class */
			mergeEquivalenceClasses((INodeExt)node.getData());
		}
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.BFSBase#visitedNode(com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt)
	 */
	@Override
	protected void visitedNode(INodeExt node) {
	}

	/**
	 * Creates a new node which represents an equivalence class.
	 * @param node
	 */
	private void  createEquivalenceClassNode(){
		if(debug)log("KST: createEquivalenceClassNode");
		List<INodeExt> list = new ArrayList<INodeExt>();
		equivalenceClassNode = GraphExtentionFactory.createNodeExtention(list);
		equivalenceClassList.add(equivalenceClassNode);
	}
	
	/**
	 * Adds a node to the active equivalence class.
	 * @param node
	 */
	@SuppressWarnings("unchecked")
	private void addNodeToEquivalenceClass(INodeExt node){
		if(debug)log("KST: addNodeToEquivalenceClass" + node);
		/* add to the equivalence class */
		List<INodeExt> list  = (List<INodeExt>)equivalenceClassNode.getData();
		list.add(node);
		
		/* set reference to the equivalence class */
		node.setData(equivalenceClassNode);
	}
	
	/**
	 * Merges two equivalence classes. The given class with the 
	 * active equivalence class. The given equivalence class
	 * is removed from the node list.
	 * @param classToBeMerged
	 */
	@SuppressWarnings("unchecked")
	private void mergeEquivalenceClasses(INodeExt classToBeMerged){
		if(debug)log("KST: mergeEquivalenceClasses");
		List<INodeExt> list  = (List<INodeExt>)classToBeMerged.getData();
		for(INodeExt node: list){
			addNodeToEquivalenceClass(node);	
		}
		removeaddNodeToEquivalenceClass(classToBeMerged);
	}
	
	/**
	 * Removes a equivalence class from the node list.
	 * @param equivalenceClassNode
	 */
	private void removeaddNodeToEquivalenceClass(INodeExt equivalenceClassNode){
		equivalenceClassList.remove(equivalenceClassNode);
	}
	
	/**
	 * Creates the set of transformed edges.
	 * @return set of edges
	 */
	@SuppressWarnings("unchecked")
	private Set<IEdgeExt> createTransformedEdges(){
		if(debug)log("KST: createTransformedEdges");
		Set<IEdgeExt> edges = new HashSet<IEdgeExt>();
		
		INodeExt startEquivalenceClass = null;
		boolean startFound = false;
		
		/* for all equivalence classes */
		for(INodeExt equivalenceClass: equivalenceClassList){
			if(debug)log("KST: equivalenceClass");
			List<INodeExt> list  = (List<INodeExt>)equivalenceClass.getData();
			
			/* for all original nodes from the equivalence class node */
			for(INodeExt node: list){
				if(debug)log("  " + node);
				IEdgeListExt outgoingEdgeList = node.getOutgoingEdgeList();
				if(outgoingEdgeList.size() != 0){
					/* just get an edge*/
					IEdgeExt e = outgoingEdgeList.getEdgeExt(0);
					INodeExt target = e.getTarget();
					
					/* get the equivalence class of the target */
					INodeExt targetEquivalenceClass = (INodeExt)target.getData();
					
					/* create a new Edge */
					IEdgeExt newEdge = GraphExtentionFactory.createEdgeExtention(equivalenceClass, targetEquivalenceClass);
					newEdge.setData(node); /* set reference to the original node */
					
					edges.add(newEdge);
					
					if(debug)log("KST: create new edge for " + node);
				}
		
				/* find the start equivalence class */
				if(!startFound && node.getByteCodeOffset() == 0){
					if(debug)log("KST: start found "
							+ " node.getByteCodeOffset()=" + node.getByteCodeOffset());
					startEquivalenceClass = equivalenceClass;
					startFound = true; /*  to avoid some checks */
				}
				else{
					if(debug)log("KST: start node not found startFound=" 
							+ startFound
							+ " node.getByteCodeOffset()=" + node.getByteCodeOffset());
				}
				
			}
		}

		/* create for all return edges back edge to start node */
		if(debug)log("KST: create for all return edges back edge to start node ");
		for(INodeExt equivalenceClass: equivalenceClassList){
//			if(equivalenceClass.getOutgoingEdgeList().size() == 0){
//
//				/* only one reference has to be assigned, because a return node */
//				List<INodeExt> list  = (List<INodeExt>)equivalenceClass.getData();
//				INodeExt node = list.get(0);
//
//				/* create a new back edge */
//				IEdgeExt newEdge = GraphExtentionFactory.createEdgeExtention(equivalenceClass, startEquivalenceClass);
//				newEdge.setData(node); /* set reference to the original node */
//
//				edges.add(newEdge);
//			}
			
			
			List<INodeExt> list  = (List<INodeExt>)equivalenceClass.getData();
			/* for all original nodes from the equivalence class node */
			for(INodeExt node: list){
				if(node.getOutgoingEdgeList().size() == 0){
					if(debug)log("KST: equivalence class includes a return node" + node);			
					
					/* create a new back edge */
					IEdgeExt newEdge = GraphExtentionFactory.createEdgeExtention(equivalenceClass, startEquivalenceClass);
					newEdge.setData(node); /* set reference to the original node */
					edges.add(newEdge);
					
					if(debug)log("KST: create new back edge for " + node);
				}
			}
			
		}

		return edges;
	}
	
	/**
	 * Returns the transformed graph
	 * @return transformedGraph
	 */
	public IDirectedGraphExt getTransformedGraph() {
		IDirectedGraphExt transformedGraph = GraphExtentionFactory.createDirectedGraphExtention();
		INodeListExt nodeList = transformedGraph.getNodeList();
		for(INodeExt n: equivalenceClassList){
			nodeList.add(n);
		}
		
		IEdgeListExt edgeList = transformedGraph.getEdgeList();
		for(IEdgeExt e: createTransformedEdges()){
			edgeList.add(e);
		}

		return transformedGraph;
	}
}
