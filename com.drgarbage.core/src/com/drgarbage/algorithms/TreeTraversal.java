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
import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;


/**
 * Class for Tree Traversal includes Post-order and Pre-order tree traversal methods on trees.
 * 
 * <br>
 * <b>Pre Order Tree Traversal: </b>
 * {@link #doPreorderTreeListTraversal(IDirectedGraphExt) }
 * <pre> 
 * Example:
 * 
 *
 *      ______ 1 __________ 
 *    /        |           \
 *   2         6          __14 __
 *  / \       / \        /   |   \
 * 3   4     7   8      15   16   17
 *     |       /  \               |
 *     5      9    13             18
 *            |
 *            10
 *           /  \
 *          11   12
 *      
 * </pre>
 * 
 * <br>
 * <b>Post order Tree Traversal: </b>
 * {@link #doPostorderTreeListTraversal(IDirectedGraphExt) }
 * <pre> 
 * Example:
 * 
 *
 *      ______ 18__________ 
 *    /        |           \
 *   4         12          __17___
 *  / \       / \        /   |    \
 * 1   3     5  11      13   14   16
 *     |       /  \               |
 *     2      9    10             15
 *            |
 *            8
 *           /  \
 *          6    7
 *      
 * </pre>
 * 
 * 
 * @author Artem Garishin, Adam Kajrys
 *
 * @version $Revision$
 * $Id$
 */
public class TreeTraversal {
	
	/**
	 * Traverses a graph in pre-order and returns a node list in that order.
	 * 
	 * @param graph the graph
	 * @return node list in pre-order
	 */
	 static INodeListExt doPreorderTreeListTraversal(IDirectedGraphExt graph) {
		INodeExt root = null;
		for (int i = 0; i < graph.getNodeList().size(); i++) {
			INodeExt n = graph.getNodeList().getNodeExt(i);
			if (n.getIncomingEdgeList().size() == 0) {
				root = n;
			}
		}

		if (root == null) {
			return null;
		}
		
		INodeListExt nodeList = GraphExtentionFactory.createNodeListExtention();
		
		recPreorderSubtreeTraversal(graph, root, nodeList);
		
		return nodeList;
	}
	
	/**
	 * Traverses a graph in pre-order with a given node as root and returns
	 * a node list in that order.
	 * 
	 * @param graph the graph
	 * @param root node where the pre-order traversal starts
	 * @return node list in pre-order
	 */
	public static INodeListExt doPreorderTreeListTraversal(IDirectedGraphExt graph, INodeExt root) {

		if (root == null) {
			return null;
		}
		
		INodeListExt nodeList = GraphExtentionFactory.createNodeListExtention();
		
		recPreorderSubtreeTraversal(graph, root, nodeList);
		
		return nodeList;
	}

	private static void recPreorderSubtreeTraversal(IDirectedGraphExt graph,
			INodeExt node, INodeListExt nodeList) {
		nodeList.add(node);
		
		for(int i = 0; i < node.getOutgoingEdgeList().size(); i++) {
			recPreorderSubtreeTraversal(graph, node.getOutgoingEdgeList().getEdgeExt(i).getTarget(), nodeList);
		}
	}

	/**
	 * Traverses a graph in post-order and returns a node list in that order.
	 * 
	 * @param graph the graph
	 * @return node list in post-order
	 */
	public static INodeListExt doPostorderTreeListTraversal(IDirectedGraphExt graph) {
		INodeExt root = null;
		for (int i = 0; i < graph.getNodeList().size(); i++) {
			INodeExt n = graph.getNodeList().getNodeExt(i);
			if (n.getIncomingEdgeList().size() == 0) {
				root = n;
			}
		}

		if (root == null) {
			return null;
		}
		
		INodeListExt nodeList = GraphExtentionFactory.createNodeListExtention();
		
		recPostorderSubtreeTraversal(graph, root, nodeList);
		
		return nodeList;
	}

	private static void recPostorderSubtreeTraversal(IDirectedGraphExt graph,
			INodeExt node, INodeListExt nodeList) {

		for(int i = 0; i < node.getOutgoingEdgeList().size(); i++) {
			recPostorderSubtreeTraversal(graph, node.getOutgoingEdgeList().getEdgeExt(i).getTarget(), nodeList);
		}
		
		nodeList.add(node);
		
	}
}
