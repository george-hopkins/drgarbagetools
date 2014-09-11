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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.GraphUtils;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;
import com.drgarbage.core.CorePlugin;

/**
 *  @author Sergej Alekseev  
 *  @version $Revision$
 *  $Id$
 */
public class Algorithms {

	/* static algorithms */
	private static SpanningTreeBFS fSpanningTreeBFS;
	private static FindBackEdgesDFS fFindBackEdgesDFS;

	/**
	 * Finds a spanning tree for the given graph. The original graph is 
	 * modified to the spanning tree.
	 * @param graph
	 * @return the spanning tree graph
	 */
	public static IDirectedGraphExt doSpanningTreeAlgorithm(IDirectedGraphExt graph){
		return doSpanningTreeAlgorithm(graph, false);
	}

	/**
	 * Finds a spanning tree for the given graph. A new graph is created if
	 * the <b>createNewGraph</b> is true, otherwise the original graph is 
	 * modified
	 * @param graph
	 * @param createNewGraph - true or false 
	 * @return the spanning tree graph
	 */
	public static IDirectedGraphExt doSpanningTreeAlgorithm(IDirectedGraphExt graph, boolean createNewGraph){
		if(fSpanningTreeBFS == null){
			fSpanningTreeBFS = new SpanningTreeBFS();
		}
		
		try {
			fSpanningTreeBFS.setCreateNewGraph(createNewGraph);
			fSpanningTreeBFS.start(graph);
		} catch (ControlFlowGraphException e) {
			CorePlugin.getDefault().getLog().log(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, e.getMessage(), e));
		}

		return fSpanningTreeBFS.getSpanningTree();
	}

	
	/**
	 * Finds a set of back edges for the given graph. 
	 *  The set is empty if the graph doesn't contain
	 *  any cycles.
	 * @param graph
	 * @return the spanning tree graph
	 */
	public static IEdgeListExt doFindBackEdgesAlgorithm(IDirectedGraphExt graph){
		if(fFindBackEdgesDFS == null){
			fFindBackEdgesDFS = new FindBackEdgesDFS();
		}
		
		try {
			fFindBackEdgesDFS.start(graph);
		} catch (ControlFlowGraphException e) {
			CorePlugin.getDefault().getLog().log(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, e.getMessage(), e));
		}

		return fFindBackEdgesDFS.getBackEdgeList();
	}
	
	private static boolean debug = false;
	private static void log(String s){
		if(debug){
			System.out.println(s);
		}
	}

	
	/**
	 * Prints the graph to the standard output.
	 * @param graph
	 */
	public static void printGraph(IDirectedGraphExt graph){
		System.out.println("Graph := {");
		System.out.println(" nodes := {");
		INodeListExt nodes = graph.getNodeList();
		for(int i = 0; i < nodes.size(); i++){
			System.out.println("  " + nodes.getNodeExt(i).getByteCodeOffset());
		}
		System.out.println(" }");

		System.out.println(" edges := {");
		IEdgeListExt edges = graph.getEdgeList();
		for(int i = 0; i < edges.size(); i++ ){
			System.out.println("  " + edges.getEdgeExt(i).getSource().getByteCodeOffset() 
					+ "->" + edges.getEdgeExt(i).getTarget().getByteCodeOffset());
		}

		System.out.println(" }");
		System.out.println("}");
	}
	
	/**
	 * Clears and transforms graph into tree structure using Spanning Tree Algorithm 
	 * @param IDiredctedGraph
	 * @return IDirectedGraphExt
	 * @throws ControlFlowGraphException
	 */
	public static IDirectedGraphExt ConvertInputGraphsToTree(
			IDirectedGraphExt IDiredctedGraph)
			throws ControlFlowGraphException {
		
		GraphUtils.clearGraph(IDiredctedGraph);
		GraphUtils.clearGraphColorMarks(IDiredctedGraph);
		
		/*create spanning tree to avoid loops */
		IDirectedGraphExt spanningTree = Algorithms.doSpanningTreeAlgorithm(IDiredctedGraph, false);
		
		return spanningTree;
	}
}
