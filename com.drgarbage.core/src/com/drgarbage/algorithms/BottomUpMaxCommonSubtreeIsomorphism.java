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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.GraphUtils;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;

/**
 * The Bottom-Up Unordered Maximum Common Subtree Isomorphism algorithm.
 * The implementation is based on the algorithm published by Gabriel
 * Valiente in his book "Algorithms on Trees and Graphs".
 * The following example from this book is used as a reference:
 * 
 * <pre>
 *   T_1                    T_2
 *         __ v15 __                  _______ w18 _____________ 
 *        /         \                /         |               \
 *      (v8)        v14             w4       (w12)           _ w17 _
 *      /  \        /  \           /  \       /  \          /   |   \
 *    (v6) (v7)   v12  v13       w1    w3   (w5) (w11)    w13   w14  w16
 *    /  \         |                   |          /  \                |
 *  (v1) (v5)     v11                  w2      (w9)  (w10)           w15
 *        |       /  \                          |
 *       (v4)   v9   v10                       (w8)
 *       /  \                                  /  \
 *     (v2) (v3)                            (w6)  (w7)
 *      
 * </pre>
 * 
 * @author Adam Kajrys
 * 
 * @version $Revision$
 * $Id$
 */
public class BottomUpMaxCommonSubtreeIsomorphism {
	
	private int knownEquivalenceClasses = 1;
	private HashMap<ArrayList<Integer>, Integer> childrenclassesToParentclassMap = new HashMap<ArrayList<Integer>, Integer>();
	
	/**
	 * Structure needed for node prioritization.
	 * 
	 * For the ordering see {@link BottomUpMaxCommonSubtreeIsomorphism.NodePriorityComparator}
	 */
	protected class PriorityTuple {
		protected INodeExt node;
		protected int size;
		protected int equivalenceClass;
	}
	
	/**
	 * Comparator used to prioritize nodes.
	 * 
	 * Citation from: "Algorithms on Trees and Graphs":
	 * <pre>
	 * [...] equivalent nodes v &isin; V_1 and w &isin; V_2 of largest size can
	 * be found by simultaneous traversal of a list of nodes of T_1 and a list
	 * of nodes of T_2, sorted by nonincreasing order of subtree size and, 
	 * within size, by (nondecreasing) order of equivalence classes of bottom-
	 * up subtree isomorphism.
	 * </pre>
	 * 
	 * @see java.util.Comparator
	 */
	private class NodePriorityComparator implements Comparator<PriorityTuple> {

		public int compare(PriorityTuple pt1, PriorityTuple pt2) {
			
			if (pt2.size - pt1.size == 0) {
				return pt1.equivalenceClass - pt2.equivalenceClass;
			}

			return pt2.size - pt1.size;
		}
	}
	/**
	 * gets a root from tree
	 * @param tree
	 * @return
	 * @throws ControlFlowGraphException
	 */
	public INodeExt getRootFromTree(IDirectedGraphExt tree) throws ControlFlowGraphException{
		
		INodeExt root = null;
		for (int i = 0; i < tree.getNodeList().size(); i++) {
			INodeExt n = tree.getNodeList().getNodeExt(i);
			if (n.getIncomingEdgeList().size() == 0) {
				root = n;
			}
		}

		if (root == null) {
			throw new ControlFlowGraphException(
					"The left tree has no root. The graph is propably not a tree.");
		}
		
		return root;
	}
	
	/**
	 * Starts the Bottom-Up Unordered Maximum Common Subtree Isomorphism Algorithm.
	 * 
	 * The Algorithm works only with trees.
	 * Gets root of input trees
	 * Invokes method {@link #executeBottomUpUnorderedMaxCommonSubtreeIsomorphism(IDirectedGraphExt, INodeExt, IDirectedGraphExt, INodeExt)}
	 * 
	 * @param leftTree the graph <code>T_1</code>
	 * @param rightTree the graph <code>T_2</code>
	 * @return the map of matched nodes
	 * @throws ControlFlowGraphException
	 */
	public Map<INodeExt, INodeExt> start(
			IDirectedGraphExt leftTree, IDirectedGraphExt rightTree)
			throws ControlFlowGraphException {

		/* get root nodes */
		INodeExt leftRoot = getRootFromTree(leftTree);
		INodeExt rightRoot = getRootFromTree(rightTree);
		
		return executeBottomUpUnorderedMaxCommonSubtreeIsomorphism(leftTree, leftRoot, rightTree, rightRoot);
	}

	/**
	 * Executes the Bottom-Up Unordered Maximum Common Subtree Isomorphism Algorithm.
	 * 
	 * @param leftGraph the graph <code>T_1</code>
	 * @param leftRoot the root node of the left graph
	 * @param rightGraph the graph <code>T_2</code>
	 * @param rightRoot the root node of the right graph
	 * @return the map of matched nodes
	 */
	public Map<INodeExt, INodeExt> executeBottomUpUnorderedMaxCommonSubtreeIsomorphism(
			IDirectedGraphExt leftSpanningTree, INodeExt leftRoot,
			IDirectedGraphExt rightSpanningTree, INodeExt rightRoot) {
		
		/* partition the sets of nodes of both graphs in bottom-up subtree isomorphism equivalence classes */
		HashMap<INodeExt, Integer> leftNodeToClassMap = partitionInIsomorphismEquivalenceClasses(leftSpanningTree);
		HashMap<INodeExt, Integer> rightNodeToClassMap = partitionInIsomorphismEquivalenceClasses(rightSpanningTree);
		
		/* find largest common subtree */
		Map<INodeExt, INodeExt> M;
		M = findLargestCommonSubtreeRoot(leftSpanningTree, leftNodeToClassMap, rightSpanningTree, rightNodeToClassMap);
		
		/* if no common subtree root was found */
		if (M == null) {
			return null;
		}
		
		/* fill map with equivalent nodes */
		mapIsomorphicNodes(leftSpanningTree, leftNodeToClassMap, rightNodeToClassMap, M);
		
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
	 *   T_1                    T_2
	 *         __ 6 __                  ______ 10 __________ 
	 *        /       \                /        |           \
	 *      (5)        4              8        (5)         _ 9 _
	 *      / \       / \            / \       / \        /  |  \
	 *    (4) (1)    3   1          1   7    (1) (4)     1   1   7
	 *    / \        |                  |        / \             |
	 *  (1) (3)      2                  1      (3)  (1)          1
	 *       |      / \                         |
	 *      (2)    1   1                       (2)
	 *      / \                                / \
	 *    (1) (1)                            (1) (1)
	 *      
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
	 * 		[1,4]        5
	 * 		[4,5]        6
	 * 		[1]          7
	 * 		[1,7]        8
	 * 		[1,1,7]      9
	 * 		[5,8,9]     10
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
		
		INodeListExt nodeList = TreeTraversal.doPostorderTreeListTraversal(graph);
		
		for (int i = 0; i < nodeList.size(); i++) {
			INodeExt node = nodeList.getNodeExt(i);
			
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
	 * Finds the root nodes in the trees T_1 and T_2 of the largest common
	 * bottom-up Subtree.
	 * 
	 * Citation from: "Algorithms on Trees and Graphs":
	 * <pre>
	 * The following procedure uses [...] priority queues of nodes of T_1 and
	 * T_2, implementing the simultaneous traversal by selectively deleting
	 * nodes of largest subtree size until the nodes with highest priority 
	 * in both queues belong to the same equivalence class.
	 * </pre>
	 * 
	 * In Valientes example the returned map contains the nodes (v8, w12).
	 * 
	 * For the ordering see {@link BottomUpMaxCommonSubtreeIsomorphism.NodePriorityComparator}
	 * 
	 * @param leftGraph the graph <code>T_1</code>
	 * @param leftNodeToClassMap node to class map of left tree
	 * @param rightGraph the graph <code>T_2</code>
	 * @param rightNodeToClassMap node to class map of right tree
	 * @return returns map containing the equivalent subtree root nodes of both trees
	 */
	private Map<INodeExt, INodeExt> findLargestCommonSubtreeRoot(
			IDirectedGraphExt leftGraph,
			HashMap<INodeExt, Integer> leftNodeToClassMap,
			IDirectedGraphExt rightGraph,
			HashMap<INodeExt, Integer> rightNodeToClassMap) {
		
		PriorityQueue<PriorityTuple> leftQ;
		PriorityQueue<PriorityTuple> rightQ;
		
		leftQ = prioritizeNodes(leftGraph, leftNodeToClassMap);
		rightQ = prioritizeNodes(rightGraph, rightNodeToClassMap);
		
		NodePriorityComparator priorityComparator = new NodePriorityComparator();
		
		while (!leftQ.isEmpty() && !rightQ.isEmpty()) {
			PriorityTuple leftPT = leftQ.peek();
			PriorityTuple rightPT = rightQ.peek();
			
			if (leftNodeToClassMap.get(leftPT.node) == rightNodeToClassMap.get(rightPT.node)) {
				Map<INodeExt, INodeExt> M = new HashMap<INodeExt, INodeExt>();
				M.put(leftPT.node, rightPT.node);
				
				return M;
			}
			
			/* if leftPT has a lesser priority than rightPT, remove leftPT from leftQ */
			if (priorityComparator.compare(leftPT, rightPT) < 0) {
				leftQ.poll();
			} else { /* otherwise remove rightPT from rightQ */
				rightQ.poll();
			}
		}
		
		return null;
	}

	/**
	 * Prioritizes nodes in a Tree.
	 * 
	 * Uses a {@link java.util.PriorityQueue}
	 * 
	 * For the ordering see {@link BottomUpMaxCommonSubtreeIsomorphism.NodePriorityComparator}
	 * 
	 * @param graph contains the nodes which are to prioritize
	 * @param nodeToClassMap node equivalence classes, needed for prioritization
	 * @return returns priority queue containing all nodes of the passed graph
	 * 
	 * @see java.util.PriorityQueue
	 */
	private PriorityQueue<PriorityTuple> prioritizeNodes(
			IDirectedGraphExt graph,
			HashMap<INodeExt, Integer> nodeToClassMap) {
		
		PriorityQueue<PriorityTuple> Q = new PriorityQueue<PriorityTuple>(50, new NodePriorityComparator());
		
		INodeListExt postorderNodeList = TreeTraversal.doPostorderTreeListTraversal(graph);
		Map<INodeExt, Integer> sizes= new HashMap<INodeExt, Integer>();
		
		for (int i = 0; i < postorderNodeList.size(); i++) {
			INodeExt node = postorderNodeList.getNodeExt(i);
			
			/* calculate sizes */
			int size = 1; /* leaves have size of 1 */
			
			/* if node is not a leaf */
			for (int j = 0; j < node.getOutgoingEdgeList().size(); j++) {
				INodeExt child = node.getOutgoingEdgeList().getEdgeExt(j).getTarget();
				
				size += sizes.get(child);
			}
			
			sizes.put(node, size);
			
			PriorityTuple pt = new PriorityTuple();
			
			pt.node = node;
			pt.size = size;
			pt.equivalenceClass = nodeToClassMap.get(node);
			
			Q.add(pt);
		}
		
		return Q;
	}
	
	/**
	 * Inserts equivalent nodes of T_1 and T_2 into the passed map M.
	 * 
	 * @param leftGraph the left tree
	 * @param leftNodeToClassMap node equivalence classes of left tree
	 * @param rightNodeToClassMap node equivalence classes of right tree
	 * @param M map prefilled with the root nodes of the largest common subtree of T_1 and T_2
	 */
	private void mapIsomorphicNodes(IDirectedGraphExt leftGraph,
			HashMap<INodeExt, Integer> leftNodeToClassMap,
			HashMap<INodeExt, Integer> rightNodeToClassMap,
			Map<INodeExt, INodeExt> M) {
		
		/* build preordered list of the nodes of the left subtree */
		INodeExt leftSubtreeRoot = (INodeExt)M.keySet().toArray()[0]; /* map contains only one set with subtree roots */
		INodeListExt leftPreorderNodeList = TreeTraversal.doPreorderTreeListTraversal(leftGraph, leftSubtreeRoot);
		
		Set<INodeExt> isMapped = new HashSet<INodeExt>();
		
		/* map maximum common subtree isomorphic nodes */
		for (int i = 1; i < leftPreorderNodeList.size(); i++) {
			INodeExt leftNode = leftPreorderNodeList.getNodeExt(i);
			
			INodeExt leftNodeParent = leftNode.getIncomingEdgeList().getEdgeExt(0).getSource();
			
			INodeExt rightNode = M.get(leftNodeParent);
			
			for (int j = 0; j < rightNode.getOutgoingEdgeList().size(); j++) {
				INodeExt rightChild = rightNode.getOutgoingEdgeList().getEdgeExt(j).getTarget();
				
				if (leftNodeToClassMap.get(leftNode) == rightNodeToClassMap.get(rightChild) && !isMapped.contains(rightChild)) {
					M.put(leftNode, rightChild);
					isMapped.add(rightChild);
					break;
				}
			}
		}
	}
	
	/* 
	 * The Methods in this section are used for purely debugging purposes 
	 */

	/**
	 * Debugging flag. Set <code>true</code> to enable printing the
	 * debugging messages.
	 */
	protected static boolean DEBUG = false;
	
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
