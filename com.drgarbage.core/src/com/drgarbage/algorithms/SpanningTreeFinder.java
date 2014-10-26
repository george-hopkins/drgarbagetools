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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.drgarbage.controlflowgraph.SpanningTree;
import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.ISpanningTree;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.core.CorePlugin;
import com.drgarbage.core.CoreMessages;

/**
 * @author Sergej Alekseev
 * @version $Revision$ $Id: Algorithms.java 651 2014-09-11 07:11:46Z
 *          salekseev $
 */
public class SpanningTreeFinder {
	
	private IDirectedGraphExt graph;
	private ISpanningTree spanningTree;
	private Set<INodeExt> unvisitedNodes;

	public SpanningTreeFinder(IDirectedGraphExt graph) {
		this.graph = graph;
	}

	/* static algorithms */
	private static FindBackEdgesDFS fFindBackEdgesDFS;
	
	/**
	 * Finds and returns a spanning tree (arborescence) of a directed graph.
	 * 
	 * @param graph
	 * @return spanning tree
	 * @throws ControlFlowGraphException
	 *             if graph has no single root
	 * @author Kevin Baxmann
	 */
	public ISpanningTree find()
			throws ControlFlowGraphException {

		spanningTree = new SpanningTree();
		unvisitedNodes = extractAllNodes(graph);
		spanningTree.setRoot(findRoot());

		Queue<INodeExt> queuedNodes = new LinkedList<INodeExt>();
		queuedNodes.add(spanningTree.getRoot());
		while (!queuedNodes.isEmpty()) {
			INodeExt node = queuedNodes.remove();
			unvisitedNodes.remove(node);
			spanningTree.getNodeList().add(node);
			queueNodesAndAddEdges(queuedNodes, node.getOutgoingEdgeList());
		}

		checkIfArborescenceComplete();
		return spanningTree;
	}

	private void checkIfArborescenceComplete()
			throws ControlFlowGraphException {
		if (!unvisitedNodes.isEmpty()) {
			throw new ControlFlowGraphException(
					CoreMessages.SpanningTreeFinder_cant_convert);
		}
	}

	private void queueNodesAndAddEdges(Queue<INodeExt> queuedNodes,
			IEdgeListExt outgoingEdges) {
		for (int i = 0; i < outgoingEdges.size(); i++) {
			IEdgeExt edge = outgoingEdges.getEdgeExt(i);
			if (unvisitedNodes.contains(edge.getTarget())) {
				spanningTree.getEdgeList().add(edge);
				queuedNodes.add(edge.getTarget());
			}
		}
	}

	private Set<INodeExt> extractAllNodes(IDirectedGraphExt graph) {
		Set<INodeExt> nodes = new HashSet<INodeExt>();
		for (int i = 0; i < graph.getNodeList().size(); i++) {
			INodeExt n = graph.getNodeList().getNodeExt(i);
			nodes.add(n);
		}
		return nodes;
	}

	/**
	 * Finds a possible root for an out-tree. It is not checked that all nodes
	 * are reachable from the root.
	 * 
	 * @return a node with no incoming edges (considers cycles)
	 * @author Kevin Baxmann
	 */
	private INodeExt findRoot() {
		Queue<IEdgeExt> queuedEdges = new LinkedList<IEdgeExt>();
		Set<INodeExt> usedRoots = new HashSet<INodeExt>();
		INodeExt root = graph.getNodeList().getNodeExt(0);
		usedRoots.add(root);
		for (int i = 0; i < root.getIncomingEdgeList().size(); i++) {
			queuedEdges.add(root.getIncomingEdgeList().getEdgeExt(i));
		}
		while (!queuedEdges.isEmpty()) {
			IEdgeExt edge = queuedEdges.remove();
			if (!usedRoots.contains(edge.getSource())) {
				root = edge.getSource();
				usedRoots.add(root);
				Boolean hasIncomingEdges = false;
				for (int i = 0; i < root.getIncomingEdgeList().size(); i++) {
					IEdgeExt e = root.getIncomingEdgeList().getEdgeExt(i);
					if (!usedRoots.contains(e.getSource())) {
						hasIncomingEdges = true;
					}
					queuedEdges.add(e);
				}
				if (!hasIncomingEdges) {
					return root;
				}
			}
		}

		return root;
	}

	/**
	 * Finds a set of back edges for the given graph. The set is empty if the
	 * graph doesn't contain any cycles.
	 * 
	 * @param graph
	 * @return the spanning tree graph
	 */
	public static IEdgeListExt findBackEdges(IDirectedGraphExt graph) {
		if (fFindBackEdgesDFS == null) {
			fFindBackEdgesDFS = new FindBackEdgesDFS();
		}

		try {
			fFindBackEdgesDFS.start(graph);
		} catch (ControlFlowGraphException e) {
			CorePlugin
					.getDefault()
					.getLog()
					.log(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, e
							.getMessage(), e));
		}

		return fFindBackEdgesDFS.getBackEdgeList();
	}
}
