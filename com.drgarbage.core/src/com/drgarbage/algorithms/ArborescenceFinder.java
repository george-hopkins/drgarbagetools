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

import com.drgarbage.controlflowgraph.Arborescence;
import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.IArborescence;
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
public class ArborescenceFinder {
	
	// Suppress default constructor for noninstantiability
	private ArborescenceFinder() {
		throw new AssertionError();
	}

	/* static algorithms */
	private static FindBackEdgesDFS fFindBackEdgesDFS;

	/**
	 * Finds and returns an arborescence of a directed graph.
	 * 
	 * @param graph
	 * @return an arborescence of the given graph
	 * @throws ControlFlowGraphException
	 *             if graph has no single root
	 * @author Kevin Baxmann
	 */
	public static IArborescence find(IDirectedGraphExt graph)
			throws ControlFlowGraphException {
		IArborescence arborescence = new Arborescence();
		arborescence.setRoot(findRoot(graph));

		Set<INodeExt> unvisitedNodes = extractAllNodes(graph);

		Queue<INodeExt> queuedNodes = new LinkedList<INodeExt>();
		queuedNodes.add(arborescence.getRoot());
		while (!queuedNodes.isEmpty()) {
			INodeExt node = queuedNodes.remove();
			unvisitedNodes.remove(node);
			arborescence.getNodeList().add(node);
			queueNodesAndAddEdges(queuedNodes, unvisitedNodes, node.getOutgoingEdgeList(),
					arborescence);
		}

		checkIfArborescenceComplete(unvisitedNodes);
		return arborescence;
	}

	private static void checkIfArborescenceComplete(Set<INodeExt> unvisitedNodes)
			throws ControlFlowGraphException {
		if (!unvisitedNodes.isEmpty()) {
			throw new ControlFlowGraphException(
					CoreMessages.ArborescenceFinder_cant_convert);
		}
	}

	private static void queueNodesAndAddEdges(Queue<INodeExt> queuedNodes,
			Set<INodeExt> unvisitedNodes, IEdgeListExt outgoingEdges,
			IArborescence arborescence) {
		for (int i = 0; i < outgoingEdges.size(); i++) {
			IEdgeExt edge = outgoingEdges.getEdgeExt(i);
			if (unvisitedNodes.contains(edge.getTarget())) {
				arborescence.getEdgeList().add(edge);
				queuedNodes.add(edge.getTarget());
			}
		}
	}

	private static Set<INodeExt> extractAllNodes(IDirectedGraphExt graph) {
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
	 * @param graph
	 * @return a node with no incoming edges (considers cycles)
	 * @author Kevin Baxmann
	 */
	private static INodeExt findRoot(IDirectedGraphExt graph) {
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
