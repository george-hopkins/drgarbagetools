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

/**
 * @author Sergej Alekseev
 * @version $Revision$ $Id: Algorithms.java 651 2014-09-11 07:11:46Z
 *          salekseev $
 */
public class ArborescenceFinder {

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
		INodeExt root = findRoot(graph);

		Set<INodeExt> nodesToVisit = new HashSet<INodeExt>();
		for (int i = 0; i < graph.getNodeList().size(); i++) {
			INodeExt n = graph.getNodeList().getNodeExt(i);
			nodesToVisit.add(n);
		}

		// Traverse graph (breath first), add nodes and edges (not cycles)
		Queue<INodeExt> queuedNodes = new LinkedList<INodeExt>();
		queuedNodes.add(root);
		while (!queuedNodes.isEmpty()) {
			INodeExt node = queuedNodes.remove();
			nodesToVisit.remove(node);
			arborescence.getNodeList().add(node);
			IEdgeListExt outgoingEdges = node.getOutgoingEdgeList();
			for (int i = 0; i < outgoingEdges.size(); i++) {
				IEdgeExt e = outgoingEdges.getEdgeExt(i);
				if (nodesToVisit.contains(e.getTarget())) {
					arborescence.getEdgeList().add(e);
					queuedNodes.add(e.getTarget());
				}
			}
		}

		if (!nodesToVisit.isEmpty()) {
			throw new ControlFlowGraphException(
					"Can't convert graph to arborescence.");
		}

		arborescence.setRoot(root);
		return arborescence;

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
	public static IEdgeListExt doFindBackEdgesAlgorithm(IDirectedGraphExt graph) {
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
