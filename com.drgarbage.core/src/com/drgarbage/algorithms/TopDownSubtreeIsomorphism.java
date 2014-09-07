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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.GraphUtils;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;


/**
 * Implements the Top-Down Unordered Subtree Isomorphism algorithm. The implementation is based 
 * on the algorithm published by Gabriel Valiente in his book "Algorithms on Trees and Graphs". 
 * The following example from this book is used as a reference:
 * <pre>
 *   T_1                  T_2
 *         _(v7)__             ______(w18)____________ 
 *        /   |   \           /        |              \
 *     (v1) (v5)  (v6)      (w4)      (w12)         _(w17)_
 *          /  \           /  \       /  \         /   |   \
 *       (v3) (v4)       w1    w3  (w5) (w11)    w13  w14  w16
 *        |                    |         /  \               |
 *      (v2)                   w2     (w9)  w10             w15
 *                                     |
 *                                     w8
 *                                    / \
 *                                  w6   w7
 *                                  
 * Nodes are numbered according to the order in which they are visited during a post order traversal.
 * The maximum common top-down subtree of <i>T_1</i> and <i>T_2</i> is depicted with enclosed brackets 
 * are mapped according to the algorithm.                                  
 * </pre> 
 * 
 * In order to find proper matched nodes according to the algorithm, the maximum cardinality bipartite 
 * matching problem {@link MaxCardBipartiteMatching } is used. 
 * 
 * @author Sergej Alekseev
 * 
 * @version $Revision$ 
 * $Id$
 */
public class TopDownSubtreeIsomorphism {

	/**
	 * The data structure for building the bipartite node relation according 
	 * to the Algorithm of Gabriel Valiente.
	 * <br>
	 * 
	 * The mapping is done as a matrix e.g:
	 * 	<pre>
	 *   ---- Matrix---
	 *   (v4 w11 1) (v4 w5 1) 
	 *   (v3 w11 1) (v3 w5 0) 
	 *   --------------
	 * </pre>
	 * Each element of the matrix is a set of (v_i, w_j,  <code>true</code> or <code>false</code> ). 
	 * <code>true</code> if the edge exists and <code>false</code> otherwise. 
	 */
	protected class MatrixEntry{
		protected INodeExt v;
		protected INodeExt w;
		protected int isomorph = -1;

	}
	
	Map<INodeExt, List<IEdgeExt>> B = null;

	/**
	 * Executes the Top Down Subtree isomorphism algorithm.
	 * 
	 * @param leftTree the tree <code>T_1</code>
	 * @param rightTree the tree <code>T_2</code>
	 * @return the map of matched nodes
	 * @throws ControlFlowGraphException 
	 */
	public Map<INodeExt, INodeExt> execute(
			IDirectedGraphExt leftTree, IDirectedGraphExt rightTree) throws ControlFlowGraphException {
		
		/* get root nodes */
		INodeExt rootLeft = null;
		for(int i = 0; i < leftTree.getNodeList().size(); i++){
			INodeExt n = leftTree.getNodeList().getNodeExt(i);
			if(n.getIncomingEdgeList().size() == 0){
				rootLeft = n;
			}
		}
		
		if(rootLeft == null){
			throw new ControlFlowGraphException("The left tree has no root. The graph is propably not a tree.");
		}
		
		INodeExt rootRight = null;
		for(int i = 0; i < rightTree.getNodeList().size(); i++){
			INodeExt n = rightTree.getNodeList().getNodeExt(i);
			if(n.getIncomingEdgeList().size() == 0){
				rootRight = n;
			}
		}
		
		if(rootRight == null){
			throw new ControlFlowGraphException("The right tree has no root. The graph is propably not a tree.");
		}

		return topDownUnorderedSubtreeIsomorphism(leftTree, rootLeft, rightTree, rootRight);
		
	}
	
	/**
	 * Executes the Top Down Subtree isomorphism algorithm.
	 * 
	 * @param leftTree the tree <code>T_1</code>
	 * @param rootLeft the root node of the left tree
	 * @param rightTree the tree <code>T_2</code>
	 * @param rootRight the root node of the right tree
	 * @return the map of matched nodes
	 */
	private Map<INodeExt, INodeExt> topDownUnorderedSubtreeIsomorphism(
			IDirectedGraphExt leftTree, 
			INodeExt rootLeft, 
			IDirectedGraphExt rightTree,
			INodeExt rootRight) {

		/* check tree size */
		if(leftTree.getNodeList().size() > rightTree.getNodeList().size()){
			return null;
		}
		
		/* clear tree graphs */
		GraphUtils.clearGraph(leftTree);
		GraphUtils.clearGraphColorMarks(leftTree);
		GraphUtils.clearGraph(rightTree);
		GraphUtils.clearGraphColorMarks(rightTree);

		/* compute size and heights for all nodes of both trees */
		compute_Size_Height(rootLeft);
		compute_Size_Height(rootRight);
		
		/* DEBUG */
		printtHeightSize(leftTree.getNodeList());
		printtHeightSize(rightTree.getNodeList());
		
		
		B = new HashMap<INodeExt, List<IEdgeExt>>();
		Map<INodeExt, INodeExt> M  = new HashMap<INodeExt, INodeExt>();
		traverseTopDown(rootLeft, rootRight);

		/* reconstruct the subtree */
		M.put(rootLeft, rootRight);
		reconstruct(rootLeft, M);
		
		return M;
	}

	/**
	 * Computes for each node of the tree the size of the subtree and
	 * the height. The values are assigned to the attributes: 
	 * <pre>
	 *  Width = <size>
	 *  Height = <height>
	 * </pre>
	 * @param root the root node of the tree
	 */
	private static void compute_Size_Height(INodeExt root){

		IEdgeListExt outEdges = root.getOutgoingEdgeList();
		for(int i = 0; i < outEdges.size(); i++){
			INodeExt node = outEdges.getEdgeExt(i).getTarget();
			compute_Size_Height(node); /* call recursive */

			IEdgeListExt out = node.getOutgoingEdgeList();
			/* child is a leaf */
			if(out.size() == 0){
				node.setWidth(1); /* set size */
				node.setHeight(0); /* set height */
			}
			else{
				int size = 1;
				int height = 0;
				for(int j = 0; j < out.size(); j++){
					INodeExt target = out.getEdgeExt(j).getTarget(); 
					size += target.getWidth();

					if(target.getHeight() > height){
						height = target.getHeight();	
					}
				}
				height++;
				node.setWidth(size); /* set size */
				node.setHeight(height); /* set height */
			}
		}
	}

	/**
	 * Citation from: "Algorithms on Trees and Graphs":
	 * <pre>
	 * Let p be the number of children of node v in T_1 and 
	 * let q be the number of children of the node w in T_2.
	 * Let also v_1, ... , v_p and w_1, ..., w_q be the children 
	 * of nodes v and w, respectively. Build a bipartite graph 
	 * G={ {v_1, ..., v_p }, { w_1, ..., w_q }, E } on p + q vertices,
	 * with an edge (w_i, w_j) in E if and only if the node v_i 
	 * can be mapped to the node w_j. Then, node v can be mapped to 
	 * node w if and only if G has a maximum cardinality bipartite 
	 * matching with p edges.
	 * </pre>
	 * 
	 * According to the {@link TopDownSubtreeIsomorphism example}
	 * the execution of the top-down traverse method solves following 
	 * maximum cardinality bipartite matching problems.
	 * 
	 * In order to decide if node v7 can be mapped to node w18
	 * the following matrix is created:
	 * <pre>
	 *   ---- Matrix---
	 *   (v6 w17 1) (v6 w12 1) (v6 w4 1) 
	 *   (v5 w17 1) (v5 w12 1) (v5 w4 1) 
	 *   (v1 w17 1) (v1 w12 1) (v1 w4 1) 
	 *   --------------
	 * </pre>
	 * and maximum cardinality bipartite matching is calculated: 
	 * <pre>
	 *   === Matching 
	 *   v1->w17
	 *   v6->w12
	 *   v5->w4
	 *   =======
	 * </pre>
	 * However, stating this bipartite matching problem involves (recursively)
	 * solving further maximum cardinality bipartite matching problems:
	 * <pre>
	 *   ---- Matrix---
	 *   (v4 w3 1) (v4 w1 1) 
	 *   (v3 w3 1) (v3 w1 0) 
	 *   --------------
	 *   === Matching 
	 *   v3->w3
	 *   v4->w1
	 *   =======
	 * </pre>
	 * 
	 * <pre>
	 *   ---- Matrix---
	 *   (v2 w2 1) 
	 *   --------------
	 *   === Matching 
	 *   v2->w2
	 *   =======
	 * </pre>
	 * 
	 * <pre>
	 *   ---- Matrix---
	 *   (v4 w11 1) (v4 w5 1)
	 *   (v3 w11 1) (v3 w5 0) 
	 *   --------------
	 *   === Matching 
	 *   v4->w5
	 *   v3->w11
	 *   =======
	 * </pre>
	 * 
	 * <pre>
	 *   ---- Matrix---
	 *   (v2 w9 1) (v2 w10 1)  
	 *   --------------
	 *   === Matching 
	 *   v2->w9
	 *   =======
	 * </pre>
	 * 
	 * <pre>
	 *   ---- Matrix---
	 *   (v4 w13 1) (v4 w16 1) (v4 w14 1) 
	 *   (v3 w13 0) (v3 w16 1) (v3 w14 0)   
	 *   --------------
	 *   === Matching 
	 *   v4->w13
	 *   v3->w16
	 *   =======
	 * </pre>
	 * 
	 * <pre>
	 *   ---- Matrix---
	 *   (v2 w15 1)   
	 *   --------------
	 *   === Matching 
	 *   v2->w15
	 *   =======
	 * </pre>
	 * 
	 * @param v node of the T_1
	 * @param w node of the T_2
	 * @return 0 or 1
	 */
	private int traverseTopDown(INodeExt v, INodeExt w){
		
		debug(v.getData().toString() + " <-> " + w.getData().toString());

		/* 
		 * p is number of children of v 
		 * q is number of chilfren of w
		 */
		int p  = v.getOutgoingEdgeList().size();
		int q = w.getOutgoingEdgeList().size();

		/* v is a leaf*/
		if(p == 0){
			return 1;
		}

		/* if( p > q || v.height > w.height || v.size > w.size */
		if(p > q || v.getHeight() > w.getHeight() || v.getWidth() > w.getWidth() ){
			return 0;
		}

		IEdgeListExt out1 = v.getOutgoingEdgeList();
		IEdgeListExt out2 = w.getOutgoingEdgeList();

		MatrixEntry matrix2[][] = new MatrixEntry[p][q];
		for(int i = 0; i < p; i++){
			for(int j = 0; j < q; j++){
				INodeExt child1 = out1.getEdgeExt(i).getTarget();
				INodeExt child2 = out2.getEdgeExt(j).getTarget();

				MatrixEntry me = new MatrixEntry();
				me.v = child1;
				me.w = child2;

				matrix2[i][j] = me;
				me.isomorph = traverseTopDown(child1, child2);
			}
		}

		if(p != 0 && q != 0){
			int size[] = {p, q};
			
			/* DEBUG */
			printMatrix(matrix2, size);
			
			/* create a bipartite graph from the matrix */
			List<INodeExt> part1 = new ArrayList<INodeExt>();
			List<INodeExt> part2 = new ArrayList<INodeExt>();
			IDirectedGraphExt graph = createBibartitGraph(matrix2, size, part1, part2);
			
			/* an optimization to avoid the creation of an empty matrix */
			if(graph.getEdgeList().size() == 0){
				return 1;
			}

			/* find max bipartite matching */
			MaxCardBipartiteMatching mbm = new MaxCardBipartiteMatching();
			mbm.start(graph, part1, part2);

			debug(" === Matching ");
			for(IEdgeExt e: mbm.getMatchedEdges()){

				debug(((INodeExt)e.getSource().getData()).getData()
						+ "->" 
						+ ((INodeExt)e.getTarget().getData()).getData());

				
				List<IEdgeExt> list = B.get(e.getSource().getData());
				if(list == null){
					list = new ArrayList<IEdgeExt>();
					B.put((INodeExt) e.getSource().getData(), list);
				}

				for(IEdgeExt ee: mbm.getMatchedEdges()){
					if(ee.getSource().getData().equals(e.getSource().getData())){
						list.add(ee);
					}
				}
			}
			debug(" =======");
		}

		return 1;
	}

	/**
	 * This method reconstructs the top-down unordered subtree isomorphism mapping
	 * <code>V_1 X V_2 subset M</code> included the solution 
	 * <code>V_1 X V_2 subset B</code> to all maximum cardinality bipartite
	 * matchings problems.
	 * <br>
	 * The root of <code>T_1</code> is mapped to the root of <code>T_2</code>
	 * in the previous step. Each nonroot node <code>v in V_1</code> is
	 * mapped to the unique node <code>v in V_2</code> with <code>(v, w) in B</code>
	 * and <code>(parent(v), parent(w)) in B</code>.
	 * <br>
	 * According the example above:
	 * <pre>
	 * v_7: !w_18!
	 * v_6: !w_17!
	 * v_5: !w_12!
	 * v_4:  w_1, !w5 !,  w13
	 * v_3:  w_3, !w11!, w16
	 * v_2:  w_2, !w9 !,  w15
	 * v_1: !w_4 !
	 * </pre>
	 * @see  TopDownSubtreeIsomorphism
	 * 
	 * @param root the root of the tree <code>T_1</code>
	 * @param M the map of the matched nodes
	 */
	private void reconstruct(INodeExt root, Map<INodeExt, INodeExt> M){
		IEdgeListExt outEdges = root.getOutgoingEdgeList();

		for(int i = 0; i < outEdges.size(); i++){
			INodeExt  node = outEdges.getEdgeExt(i).getTarget();

			/* for all matchings */
			debug("    -> Matches for " + node.getData());

			List<IEdgeExt> edges = B.get(node);
			if(edges == null){
				debug("ERROR: not matches for " + node.getData());
				return;
			}
			
			for(IEdgeExt e: edges){
				INodeExt nodeV = (INodeExt)e.getSource().getData();
				INodeExt nodeW = (INodeExt)e.getTarget().getData();
				debug("       +-> match " 
						+ nodeV.getData().toString() 
						+ "->" 
						+ nodeW.getData().toString());

				INodeExt parentV = nodeV.getIncomingEdgeList().getEdgeExt(0).getSource();
				INodeExt parentW = nodeW.getIncomingEdgeList().getEdgeExt(0).getSource();

				debug("         +-> Parent of :" + nodeV.getData() + " is " + parentV.getData());
				debug("         +-> Parent of :" + nodeW.getData() + " is " + parentW.getData());

				INodeExt vW = M.get(parentV);
				if(vW != null){


					if(vW.equals(parentW)){
						debug("!!! FOUND " + nodeV.getData() +  " " + nodeW.getData());
						M.put(nodeV, nodeW);
					}
				}
				else{
					debug("ERROR ..." + nodeV.getData() +  " " + nodeW.getData());
				}
			}
			reconstruct(node, M); /* call recursive */

		}
	}
	
	/**
	 * Creates a bipartite graph from the matrix.
	 * 
	 * @param matrix the matrix 
	 *  <pre>
	 *   ---- Matrix---
	 *   (v4 w11 1) (v4 w5 1) 
	 *   (v3 w11 1) (v3 w5 0) 
	 *   --------------
	 * 	</pre>
	 * @param size the matrix size <code>new int [][] = {2, 2}</code>
	 * @param part1 the list for the first bipartite partition
	 *  				The list is filled with the nodes of the second partition: 
	 * 	<pre>
	 *   PART1: v4 v3
	 * 	</pre>
	 *  
	 * @param part2 the list for the second bipartite partition
	 * 				The list is filled with the nodes of the second partition: 
	 * 	<pre>
	 *   PART2: w11 w5
	 * 	</pre>
	 * 
	 * @return the bipartite graph
	 * 	<pre>
	 * 	Graph:
	 * 	Nodes:
	 * 	  v4
	 * 	  v3
	 * 	  w11
	 * 	  w5
	 * 	Edges:
	 * 	  v4 -> w11
	 * 	  v4 -> w5
	 * 	  v3 -> w11
	 * 	</pre> 
	 */
	private static IDirectedGraphExt createBibartitGraph(MatrixEntry[][] matrix,
			int[] size, 
			List<INodeExt> part1, 
			List<INodeExt> part2){

		Set<INodeExt> set1 = new HashSet<INodeExt>();
		Set<INodeExt> set2 = new HashSet<INodeExt>();
		
		IDirectedGraphExt graph = GraphExtentionFactory.createDirectedGraphExtention();

		Map<INodeExt, INodeExt> newGraphnodeMap = new HashMap<INodeExt, INodeExt>();
		for(int i = 0; i < size[0]; i++){
			for(int j = 0; j < size[1]; j++){
				MatrixEntry me = matrix[i][j];
				if(me.isomorph != 0){
					INodeExt v = newGraphnodeMap.get(me.v);
					if(v == null){
						v = GraphExtentionFactory.createNodeExtention(me.v);
						newGraphnodeMap.put(me.v, v);
					}
					set1.add(v);

					INodeExt w = newGraphnodeMap.get(me.w);
					if(w == null){
						w = GraphExtentionFactory.createNodeExtention(me.w);
						newGraphnodeMap.put(me.w, w);
					}
					set2.add(w);

					IEdgeExt e = GraphExtentionFactory.createEdgeExtention(v, w);
					
					/* for debugging purposes */
					if(DEBUG){
						String str = me.v.getData().toString() + "->" + me.w.getData().toString(); 
						e.setData(str);
					}
					graph.getEdgeList().add(e);
				}
			}
		}

		for(INodeExt n: newGraphnodeMap.values()){
			graph.getNodeList().add(n);
		}

		Iterator<INodeExt> it = set1.iterator();
		while(it.hasNext()){
			part1.add(it.next());
		}
		
		it = set2.iterator();
		while(it.hasNext()){
			part2.add(it.next());
		}
		
		/* print the graph if */
		if(DEBUG){
			printGraph(graph);
			debug("PART1:");
			for(INodeExt n: part1){
				System.out.print(" " + ((INodeExt)n.getData()).getData());
			}
			debug("\n");
			debug("PART2:");
			for(INodeExt n: part2){
				System.out.print(" " + ((INodeExt)n.getData()).getData());
			}
			debug("\n");
		}
		
		return graph;
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
	private static void debug(String msg){
		if(!DEBUG) return;
		
		System.out.println(msg);
	}
	
	/**
	 * Prints the graph in the following format:
	 * <pre>
	 *   Print Graph:
	 *   Nodes:
	 *     w9
	 *     w10
	 *     v2
	 *   Edges:
	 *     v2 -> w9
	 *     v2 -> w10
	 * </pre>
	 * 
	 * NOTE: The method is disabled if the debugging flag set to false.
	 * 
	 * @param g the graph
	 * @see #DEBUG
	 */
	private static void printGraph(IDirectedGraphExt g) {
		if(!DEBUG) return;
		
		System.out.println("Print Graph:");

		System.out.println("Nodes:");
		for (int i = 0; i < g.getNodeList().size(); i++) {
			System.out.println("  " + ((INodeExt)g.getNodeList()
					.getNodeExt(i).getData()).getData().toString());
		}

		System.out.println("Edges:");
		for (int i = 0; i < g.getEdgeList().size(); i++) {
			IEdgeExt e = g.getEdgeList().getEdgeExt(i);
			System.out.println("  " 
					+ ((INodeExt)e.getSource().getData()).getData() 
					+ " -> "
					+ ((INodeExt)e.getTarget().getData()).getData());
		}
	}
	
	/**
	 * Prints the calculated height and size of each node 
	 * for the given graph node list in following format:
	 * <pre>
	 *   ...
	 *   v1 size=1 height=0
	 *   v5 size=4 height=2
	 *   v3 size=2 height=1
	 *   v4 size=1 height=0
	 *   v2 size=1 height=0
	 *   v6 size=1 height=0
	 *   ...
	 * </pre> 
	 * 
	 * NOTE: The method is disabled if the debugging flag set to false.
	 * 
	 * @param nodeList list of nodes
	 * @see #DEBUG
	 */
	private static void printtHeightSize(INodeListExt nodeList) {
		if(!DEBUG) return;
		
		for(int i = 0; i < nodeList.size(); i++){
			INodeExt n = nodeList.getNodeExt(i);
			System.out.println(n.getData().toString() 
					+ " size=" + n.getWidth()
					+ " height=" + n.getHeight());
		}
	}
	
	/**
	 * Prints the matrix in the following format:
	 * <pre>
	 *   ---- Matrix---
	 *   (v6 w17 1) (v6 w12 1) (v6 w4 1) 
	 *   (v5 w17 1) (v5 w12 1) (v5 w4 1) 
	 *   (v1 w17 1) (v1 w12 1) (v1 w4 1) 
	 *   --------------
	 * </pre>
	 * 
	 * NOTE: The method is disabled if the debugging flag set to false.
	 * 
	 * @param matrix the matrix
	 * @param size the matrix size <code>new int [][] = {10, 4}</code>
	 * @see #DEBUG
	 */
	private static void printMatrix(MatrixEntry[][] matrix, int[] size){	
		if(!DEBUG) return;
		
		System.out.println("---- Matrix---");
		for(int i = 0; i < size[0]; i++){
			for(int j = 0; j < size[1]; j++){
				MatrixEntry me = matrix[i][j];
				System.out.print("(" + me.v.getData().toString() 
						+ " " + me.w.getData().toString() 
						+ " " + me.isomorph
						+ ") ");
			}
			System.out.println();
		}
		System.out.println("--------------");
	}
}
