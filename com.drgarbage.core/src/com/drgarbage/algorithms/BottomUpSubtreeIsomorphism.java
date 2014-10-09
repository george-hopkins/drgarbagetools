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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.GraphUtils;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;

/**
 * Implements the Bottom-Up Unordered Subtree Isomorphism algorithm. The implementation is
 * based on the algorithm published by Gabriel Valiente in his book
 * "Algorithms on Trees and Graphs". The following example from this book is
 * used as a reference:
 * 
 * <pre>
 *   T_1                  T_2
 *         (v6)               ______ w18 _____________ 
 *         /  \              /        |               \
 *       (v1) (v5)          w4       w12            _ w17 _
 *             |           /  \      /  \          /   |   \
 *            (v4)       w1    w3   w5 (w11)     w13  w14  w16
 *            /  \             |        /  \                |
 *          (v2) (v3)          w2     (w9) (w10)           w15
 *                                     |
 *                                    (w8)
 *                                    /  \
 *                                  (w6) (w7)
 * 
 * Nodes are numbered according to the order in which they are visited during a post order traversal.
 * The maximum common top-down subtree of <i>T_1</i> and <i>T_2</i> is depicted with enclosed brackets 
 * are mapped according to the algorithm. 
 * </pre>
 * The algorithm uses Post Order Tree Traversal: {@link TreeTraversal#doPostorderTreeListTraversal(IDirectedGraphExt)}
 * <br>
 * 
 * @author Adam Kajrys
 * @version $Revision$
 * $Id$
 */
public class BottomUpSubtreeIsomorphism {
	
	private int knownEquivalenceClasses = 1;
	private HashMap<ArrayList<Integer>, Integer> childrenclassesToParentclassMap = new HashMap<ArrayList<Integer>, Integer>();

	/**
	 * Executes the Bottom-Up Unordered Subtree Isomorphism Algorithm.
	 * 
	 * @param leftGraph the graph <code>T_1</code>
	 * @param rightGraph the graph <code>T_2</code>
	 * @return the map of matched nodes
	 * @throws ControlFlowGraphException in case The left or right tree has no root
	 */
	public Map<INodeExt, INodeExt> execute(
			IDirectedGraphExt leftGraph, IDirectedGraphExt rightGraph)
			throws ControlFlowGraphException {

		/* get root nodes */
		INodeExt leftRoot = null;
		for (int i = 0; i < leftGraph.getNodeList().size(); i++) {
			INodeExt n = leftGraph.getNodeList().getNodeExt(i);
			if (n.getIncomingEdgeList().size() == 0) {
				leftRoot = n;
			}
		}

		if (leftRoot == null) {
			throw new ControlFlowGraphException(
					"The left tree has no root. The graph is propably not a tree.");
		}

		INodeExt rightRoot = null;
		for (int i = 0; i < rightGraph.getNodeList().size(); i++) {
			INodeExt n = rightGraph.getNodeList().getNodeExt(i);
			if (n.getIncomingEdgeList().size() == 0) {
				rightRoot = n;
			}
		}

		if (rightRoot == null) {
			throw new ControlFlowGraphException(
					"The right tree has no root. The graph is propably not a tree.");
		}
		
		return bottomUpUnorderedSubtreeIsomorphism(leftGraph, leftRoot, rightGraph, rightRoot);
	}

	/**
	 * Executes the Bottom-Up Unordered Subtree Isomorphism Algorithm.
	 * 
	 * @param leftGraph the graph <code>T_1</code>
	 * @param leftRoot the root node of the left graph
	 * @param rightGraph the graph <code>T_2</code>
	 * @param rightRoot the root node of the right graph
	 * @return the map of matched nodes
	 */
	public Map<INodeExt, INodeExt> bottomUpUnorderedSubtreeIsomorphism(
			IDirectedGraphExt leftGraph, INodeExt leftRoot,
			IDirectedGraphExt rightGraph, INodeExt rightRoot) {
		
		/* check tree size */
		if(leftGraph.getNodeList().size() > rightGraph.getNodeList().size()){
			return null;
		}
		
//		IEdgeListExt backEdgesCfgLeft = removeBackEdges(leftGraph);
//		IEdgeListExt backEdgesCfgRight = removeBackEdges(rightGraph);
//		
//		IDirectedGraphExt leftSpanningTree = Algorithms.doSpanningTreeAlgorithm(leftGraph, false);
//		IDirectedGraphExt rightSpanningTree = Algorithms.doSpanningTreeAlgorithm(rightGraph, false);
		
		/* clear tree graphs */
		GraphUtils.clearGraph(leftGraph);
		GraphUtils.clearGraphColorMarks(leftGraph);
		GraphUtils.clearGraph(rightGraph);
		GraphUtils.clearGraphColorMarks(rightGraph);
		
		/* partition the sets of nodes of both graphs in bottom-up subtree isomorphism equivalence classes */
		HashMap<INodeExt, Integer> leftNodeToClassMap = partitionInIsomorphismEquivalenceClasses(leftGraph);
		HashMap<INodeExt, Integer> rightNodeToClassMap = partitionInIsomorphismEquivalenceClasses(rightGraph);
		
		/* test equivalence classes of the root of T_1 and each of the nodes of T_2
		 * and create map of equivalent nodes
		 */
		Map<INodeExt, INodeExt> M;
		
		M = mapIsomorphicNodes(leftGraph, leftRoot, leftNodeToClassMap, rightGraph, rightNodeToClassMap);
		
		return M;
	}

	/**
	 * Partitions a tree in bottom-up isomorphism equivalence classes.
	 * 
	 * Citation from: "Algorithms on Trees and Graphs":
	 * <pre>
	 * Let the number of known equivalence classes be initially equal to
	 * 1, corresponding to the equivalence class of all leaves in the trees.
	 * For all nodes v of T_1 and T_2 in postorder, set the equivalence class
	 * of v to 1 if node v is a leaf. Otherwise, look up in the dictionary
	 * the ordered list of equivalent classes to which the children of node
	 * v belong. If the ordered list (key) is found in the dictionary, set
	 * the equivalence class off node v to the value (element) found.
	 * Otherwise, increment by one the number of known equivalence classes,
	 * insert the ordered list together with the number of known equivalence
	 * classes in the dictionary, and set the equivalence class of node v to
	 * the number of known equivalence classes.
	 * </pre>
	 * 
	 * The equivalence classes of the unordered trees T_1 and T_2:
	 * <pre>
	 *   T_1                  T_2
	 *          4               ______  9  _______ 
	 *         / \             /        |         \
	 *        1   3           6         7          8
	 *            |          / \       / \       / | \
	 *            2         1   5     1   4     1  1  5
	 *           / \            |        / \          |
	 *          1   1           1       3   1         1
	 *                                  |
	 *                                  2
	 *                                 / \
	 *                                1   1
	 * </pre>
	 * 
	 * Contents of the map of known equivalence classes 
	 * (<code>childrenclassesToParentclassMap</code>) after partitioning
	 * of T_1 and T_2:
	 * <pre>
	 * 		key      value
	 * 		--------------
	 * 		[1,1]        2
	 * 		[2]          3
	 * 		[1,3]        4
	 * 		[1]          5
	 * 		[1,5]        6
	 * 		[1,4]        7
	 * 		[1,1,5]      8
	 * 		[6,7,8]      9
	 * </pre>
	 * The map contains a list of integers (equivalence classes of children
	 * nodes) as keys and integers (equivalence class of the children's
	 * parent) as values.
	 * 
	 * @param graph tree to be partitioned into equivalence classes
	 * @return returns node to class map
	 */
	private HashMap<INodeExt, Integer> partitionInIsomorphismEquivalenceClasses(
			IDirectedGraphExt graph) {
		
		HashMap<INodeExt, Integer> nodeToClassMap = new HashMap<INodeExt, Integer>();
		
		INodeListExt postorderNodeList = TreeTraversal.doPostorderTreeListTraversal(graph);
		
		for (int i = 0; i < postorderNodeList.size(); i++) {
			INodeExt node = postorderNodeList.getNodeExt(i);
			
			/* all leaves have the equivalence class of 1 */
			if (node.getOutgoingEdgeList().size() == 0) {
				nodeToClassMap.put(node, 1);
			}

			else {
				/* equivalence classes of children of current node */
				ArrayList<Integer> childclassesList = new ArrayList<Integer>();

				for (int j = 0; j < node.getOutgoingEdgeList().size(); j++) {
					INodeExt child = node.getOutgoingEdgeList().getEdgeExt(j).getTarget();

					/* fill list with equivalence classes of children nodes */
					childclassesList.add(nodeToClassMap.get(child));
				}
				
				Collections.sort(childclassesList);
				
				/* if a node with the same equivalence classes of children nodes already exists,
				 * assign the same equivalence class to that node */
				if (childrenclassesToParentclassMap.containsKey(childclassesList)) {
					nodeToClassMap.put(node, childrenclassesToParentclassMap.get(childclassesList));
				}

				/* otherwise create new equivalence class */
				else {
					childrenclassesToParentclassMap.put(childclassesList, ++knownEquivalenceClasses);
					nodeToClassMap.put(node, knownEquivalenceClasses);
				}
			}
		}
		
		debug(" === equivalence classes:");
		debug("node to class map:");
		printNodeToClassMap(nodeToClassMap);
		debug("\nchildrenclasses to parentclass map:");
		debug(childrenclassesToParentclassMap.toString());
		debug(" ========================");
		
		return nodeToClassMap;
	}
	
	/**
	 * Creates a map of bottom-up subtree isomorphic nodes.
	 * 
	 * Citation from: "Algorithms on Trees and Graphs":
	 * <pre>
	 * An actual bottom-up subtree isomorphism mapping M &sube; V_1 &times; V_2
	 * of T_1 = (V_1, E_1) into the subtree of T_2 = (V_2, E_2) rooted at node
	 * v [<code>rightNode</code>] can be constructed by mapping the root of T_1
	 * to node v, and then mapping the remaining nodes in T_1 to the remaining
	 * nodes in the subtree of T_2 rooted at node v, such that mapped nodes
	 * belong to the same equivalence class of bottom-up subtree isomorphism.
	 * </pre>
	 * 
	 * The returned map of the unordered trees T_1 and T_2:
	 * <pre>
	 * 		key   value
	 * 		-----------
	 * 		v3      w7
	 *		v2      w6
	 *		v6      w11
	 *		v4      w8
	 *		v5      w9
	 *		v1      w10
	 * </pre>
	 * 
	 * @param leftGraph the left graph
	 * @param leftRoot root of the left graph
	 * @param leftNodeToClassMap map containing the equivalence class of the nodes in the left graph
	 * @param rightGraph the right graph
	 * @param rightNodeToClassMap map containing the equivalence class of the nodes in the right graph
	 * @return returns map with isomorphic nodes
	 */
	private Map<INodeExt, INodeExt> mapIsomorphicNodes(
			IDirectedGraphExt leftGraph, INodeExt leftRoot,
			HashMap<INodeExt, Integer> leftNodeToClassMap,
			IDirectedGraphExt rightGraph,
			HashMap<INodeExt, Integer> rightNodeToClassMap) {
		
		Map<INodeExt, INodeExt> M = new HashMap<INodeExt, INodeExt>();
		
		for (int i = 0; i < rightGraph.getNodeList().size(); i++) {
			INodeExt rightNode = rightGraph.getNodeList().getNodeExt(i);
			
			if (leftNodeToClassMap.get(leftRoot).equals(rightNodeToClassMap.get(rightNode))) {
				M.put(leftRoot, rightNode);
				M.putAll(buildMap(leftRoot, leftNodeToClassMap, rightNode, rightNodeToClassMap));
			}
			
			if (M.size() == leftGraph.getNodeList().size()) {
				break;
			}
		}
		
		return M;
	}
	
	/**
	 * Mapping the nodes of the left graph to equivalent nodes in the
	 * subtree of the right graph rooted in the right graph at node v.
	 * 
	 * Citation from: "Algorithms on Trees and Graphs":
	 * <pre>
	 * Mapping the nodes of T_1 to equivalent nodes in the subtree of
	 * T_2 during preorder traversal of T_1, guarantees that the
	 * bottom-up subtree isomorphism mapping preserves he structure of
	 * tree T_1. In the following recurse procedure, each of the
	 * children of node r_1 &isin; V_1 is mapped to some unmapped child
	 * of node r_2 &isin; V_2 belonging to the same equivalence class.
	 * </pre>
	 * 
	 * @param leftNode node in the left graph
	 * @param leftNodeToClassMap map containing the equivalence class of the nodes in the left graph
	 * @param rightNode node in the right graph
	 * @param rightNodeToClassMap map containing the equivalence class of the nodes in the right graph
	 * @return returns map, key is a node in the left graph, value the equivalent node in the subtree of the right graph
	 */
	private Map<INodeExt, INodeExt> buildMap (
			INodeExt leftNode,
			HashMap<INodeExt, Integer> leftNodeToClassMap,
			INodeExt rightNode,
			HashMap<INodeExt, Integer> rightNodeToClassMap) {
		
		HashMap<INodeExt, INodeExt> map = new HashMap<INodeExt, INodeExt>();
		ArrayList<INodeExt> l = new ArrayList<INodeExt>();

		for (int i = 0; i < rightNode.getOutgoingEdgeList().size(); i++) {
			l.add(rightNode.getOutgoingEdgeList().getEdgeExt(i).getTarget());
		}

		INodeExt v, w;

		for (int i = 0; i < leftNode.getOutgoingEdgeList().size(); i++) {
			v = leftNode.getOutgoingEdgeList().getEdgeExt(i).getTarget();

			Iterator<INodeExt> items = l.iterator();
			while (items.hasNext()) {
				w = items.next();
				if (leftNodeToClassMap.get(v) == rightNodeToClassMap.get(w)) {
					map.put(v, w);
					items.remove();
					
					map.putAll(buildMap(v, leftNodeToClassMap, w, rightNodeToClassMap));
										
					break;
				}
			}
		}
		return map;
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
			System.out.println("  " + g.getNodeList().getNodeExt(i).getData());
		}

		System.out.println("Edges:");
		for (int i = 0; i < g.getEdgeList().size(); i++) {
			IEdgeExt e = g.getEdgeList().getEdgeExt(i);
			System.out.println("  " 
					+ e.getSource().getData()
					+ " -> "
					+ e.getTarget().getData());
		}
	}
	
	/**
	 * Prints a map of nodes and their equivalence class.
	 * @param nodeToClassMap the map
	 */
	static void printNodeToClassMap(HashMap<INodeExt, Integer> nodeToClassMap){
		if(!DEBUG) return;
		
		for(Entry<INodeExt, Integer> entry : nodeToClassMap.entrySet()){
			System.out.println(entry.getKey().getData()
					+ " = "
					+ entry.getValue());
		}
	}
}
