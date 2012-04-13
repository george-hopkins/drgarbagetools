/**
 * Copyright (c) 2012, Dr. Garbage Community
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;

/**
 *  @author Sergej Alekseev  
 *  @version $Revision: 1523 $
 *  $Id: Algorithms.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class Algorithms {

	/* static algorithms */
	private static KnuthStevensonTransformation fKnuthStevensonTransformation = new KnuthStevensonTransformation();
	private static SpanningTreeBFS fSpanningTreeBFS = new SpanningTreeBFS();

	public static List<INodeExt> doKnuthstevensonAlgorithm(IDirectedGraphExt graph){
		IDirectedGraphExt transformedGraph = doKnuthStevensonTransformation(graph);

		if(debug)log("-- doKnuthStevensonTransformation ---");
		if(debug)printTransformedGraph(transformedGraph);
		if(debug)log("-------------------------------------");

		List<IEdgeExt> edges = doSpaningTreeAlgorithm(transformedGraph);
		if(debug)log("--- doSpaningTreeAlgorithm ---");
		if(debug)log(" edges.size()=" + edges.size());
		if(debug)log("------------------------------");

		/* get original nodes, they are assigned to the edges */
		List<INodeExt> nodes = new ArrayList<INodeExt>();
		for(IEdgeExt edge: edges){
			INodeExt originalNode = (INodeExt)edge.getData();
			nodes.add(originalNode);	
		}

		if(debug)log("--- original nodes ---");
		if(debug)log(" nodes.size()=" + nodes.size());
		if(debug)log("----------------------");


		return nodes;
	}

	public static IDirectedGraphExt doKnuthStevensonTransformation(IDirectedGraphExt graph){

		try {
			fKnuthStevensonTransformation.start(graph);
		} catch (ControlFlowGraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fKnuthStevensonTransformation.getTransformedGraph();
	}

	public static List<IEdgeExt> doSpaningTreeAlgorithm(IDirectedGraphExt graph){
		try {
			fSpanningTreeBFS.start(graph);
		} catch (ControlFlowGraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fSpanningTreeBFS.getSpanningTreeEdges();
	}

	/**
	 * <tt>edgeMap</tt> is mapping of byte code addresses to edge objects.
	 * @param graph
	 * @param counters
	 * @param edgeMap
	 */
	public static void propagateEdgeCounts(IDirectedGraphExt graph, int[] configuredBreakpoints, int[][] coveredBreakpoints){
		
		if(debug)log("-- propagateCounts ---");
		
		/* create mapping transformed edges to original nodes */
		Map<Integer, IEdgeExt> edgeMap = new HashMap<Integer, IEdgeExt>();
		IEdgeListExt edges = graph.getEdgeList();
		for(int i = 0; i < edges.size(); i++){
			IEdgeExt edge = edges.getEdgeExt(i);
			INodeExt node = (INodeExt)edge.getData();

			edgeMap.put(node.getByteCodeOffset(), edge);
		}
		
		if(debug)log("========");
		if(debug)log(" configuredCounters:");
		/* mark counters */
		for(int n: configuredBreakpoints){
			if(debug)log( " " + n);
			IEdgeExt edge = edgeMap.get(n);
			edge.setCounter(0);
			edge.setVisited(true);
		}
		if(debug)log("\n========");
		
		if(debug)log(" executedCounters:");
		/* [0] - instruction address [1] - counter  */
		for(int i = 0; i < coveredBreakpoints.length; i++){
			if(debug)log(" " + coveredBreakpoints[i][0] + "(" + coveredBreakpoints[i][1] + "),");
			IEdgeExt edge = edgeMap.get(coveredBreakpoints[i][0]);
			edge.setCounter(coveredBreakpoints[i][1]);
		}
		if(debug)log("\n========");

		/* start bfs from each configured node 
		 * using backwards bfs. Backwards traversing 
		 * should find faster the empty edges,
		 * because the configuration has been done 
		 * by forwards traversing. The result should 
		 * be always the same */
		for(int c: configuredBreakpoints){
			IEdgeExt edge = edgeMap.get(c);
			
			INodeExt node = edge.getSource();
			node.setCounter(node.getCounter() + edge.getCounter());
			
			if(debug)log("BFS: start bfsBackWards from ");
			if(debug)printTransformedNode(node);
			if(debug)log("BFS: set counter to " + node.getCounter());
			
			bfsBackwarts(node);
		}

		if(debug)log("-----Print graoh------");
		if(debug)printTransformedGraph(graph);
		if(debug)log("----------------------");
	}

	private static void bfsBackwarts(INodeExt startnode){
		Queue<INodeExt> queue = new LinkedList<INodeExt>();
		queue.add(startnode);
		startnode.setVisited(true);

		while(!queue.isEmpty()){
			INodeExt node = queue.poll();
			if(debug)log("BFS: poll ");
			if(debug)printTransformedNode(node);

			if(debug)log("BFS: check outgoing edges");
			IEdgeListExt outgoingEdges = node.getOutgoingEdgeList();
			int numberOfVisitedOutgoingEdges = 0;
			for(int i = 0; i < outgoingEdges.size(); i++){
				IEdgeExt edge = outgoingEdges.getEdgeExt(i);
				if(debug)log("BFS: edge ");
				if(debug)printTransformedEdge(edge);
			
				if(!edge.isVisited()){
					if(debug)log("BFS: visited false");
				}
				else{
					if(debug)log("BFS: visited true");
					numberOfVisitedOutgoingEdges++;
				}
			}
			
			/* go on only if no unvisited outgoing edges exist */
			if(debug)log("BFS: numberOfVisitedOutgoingEdges=" + numberOfVisitedOutgoingEdges);
			if((outgoingEdges.size() - numberOfVisitedOutgoingEdges) != 0){
				node.setVisited(false); /* the node has to be visited again */
				if(debug)log("BFS: numberOfVisitedOutgoingEdges != 0 ");
				continue;
			}
			
			if(debug)log("BFS: check incoming edges");
			IEdgeListExt incommingEdges = node.getIncomingEdgeList();
			int numberOfVisitedIncommingEdges = 0;
			int incomingEdgeCounter = 0;
			IEdgeExt unvisitedEdge = null;
			for(int i = 0; i < incommingEdges.size(); i++){
				IEdgeExt edge = incommingEdges.getEdgeExt(i);
				if(debug)log("BFS: edge ");
				if(debug)printTransformedEdge(edge);
			
				if(!edge.isVisited()){
					if(debug)log("BFS: visited false");
					unvisitedEdge = edge;
				}
				else{
					if(debug)log("BFS: visited true");
					numberOfVisitedIncommingEdges++;
					incomingEdgeCounter += edge.getCounter();
				}
			}
			
			/* go on only if one unvisited incoming edge exist */
			if(debug)log("BFS: incommingEdges.size()=" + incommingEdges.size() + " numberOfVisitedIncommingEdges=" + numberOfVisitedIncommingEdges );
			if((incommingEdges.size() - numberOfVisitedIncommingEdges) != 1){
				node.setVisited(false); /* the node has to be visited again */
				if(debug)log("BFS: numberOfVisitedIncommingEdges != 1 ");
				continue;
			}
			unvisitedEdge.setVisited(true);
			
			if(debug)log("BFS: incomingEdgeCounter " + incomingEdgeCounter);
			unvisitedEdge.setCounter(node.getCounter() - incomingEdgeCounter);
			if(debug)log("BFS: set counter to " + unvisitedEdge.getCounter() + " for");
			if(debug)printTransformedEdge(unvisitedEdge);
			
			INodeExt sourceNode = unvisitedEdge.getSource();
			if(!sourceNode.isVisited()){
				queue.add(sourceNode);	
				if(debug)log("BFS: add to queue ");
				if(debug)printTransformedNode(sourceNode);
			
				sourceNode.setVisited(true);	
				sourceNode.setCounter(sourceNode.getCounter() + unvisitedEdge.getCounter());
				if(debug)log("BFS: set counter " + sourceNode.getCounter());
			}
		}
	}

	public static void propagateEdgeCounts(IDirectedGraphExt graph){
		
		/* for all nodes */		
		INodeListExt nodes = graph.getNodeList();
		for(int i = 0; i <nodes.size(); i++){
			/* incoming edges */
			INodeExt node = nodes.getNodeExt(i);
			IEdgeListExt out = node.getOutgoingEdgeList();
			if(out.size() == 1){
				IEdgeExt edge = out.getEdgeExt(0);
				edge.setCounter(node.getCounter());
				edge.setVisited(true);
			}

			/* outgoing edges */
			IEdgeListExt in = node.getIncomingEdgeList();
			if(in.size() == 1){
				IEdgeExt edge = in.getEdgeExt(0);
				edge.setCounter(node.getCounter());
				edge.setVisited(true);
			}
			
			node.setVisited(true); /* just for formal marking*/
		}
		
		/* for all edges */
		IEdgeListExt edges = graph.getEdgeList();
		for(int i = 0; i < edges.size(); i++){
			IEdgeExt edge = edges.getEdgeExt(i);
			if(!edge.isVisited()){
				
				boolean hasUnvisitedEdge = false;
				
				/* try with source */
				INodeExt source = edge.getSource();
				
				/* check if the source has other unvisited outgoing edges */
				IEdgeListExt out = source.getOutgoingEdgeList();
				int outEdgeCounter = 0;
				for(int j = 0; j < out.size(); j++){
					IEdgeExt e = out.getEdgeExt(j);
					if(e != edge){ 
						if(!e.isVisited()){
							hasUnvisitedEdge = true;
							break;
						}
						else{
							outEdgeCounter += e.getCounter();
						}
					}
				}
				
				if(!hasUnvisitedEdge){
					edge.setCounter(source.getCounter() - outEdgeCounter);
					edge.setVisited(true); /* just formal marking */
					continue;
				}
				
				/* try with target */
				INodeExt target = edge.getTarget();
				
				/* check if the target has other unvisited incoming edges */
				hasUnvisitedEdge = false;
				IEdgeListExt in = target.getIncomingEdgeList();
				int inEdgeCounter = 0;
				for(int j = 0; j < out.size(); j++){
					IEdgeExt e = in.getEdgeExt(j);
					if(e != edge){ 
						if(!e.isVisited()){
							hasUnvisitedEdge = true;
							break;
						}
						else{
							inEdgeCounter += e.getCounter();
						}
					}
				}
				
				if(!hasUnvisitedEdge){
					edge.setCounter(target.getCounter() - inEdgeCounter);
					edge.setVisited(true); /* just formal marking */
				}
				else{
					//TODO: ERROR ??
				}
			}
		}
		

	}

	
	public static void resetVisitFlags(IDirectedGraphExt graph){
		/* nodes */
		INodeListExt nodes = graph.getNodeList();
		for(int i = 0; i < nodes.size(); i++){
			nodes.getNodeExt(i).setVisited(false);
		}

		/* edges */
		IEdgeListExt edges  = graph.getEdgeList();
		for(int i = 0; i < edges.size(); i++){
			edges.getEdgeExt(i).setVisited(false);
		}
	}

	private static boolean debug = false;
	private static void log(String s){
		if(debug){
			System.out.println(s);
		}
	}

	private static void printTransformedEdge(IEdgeExt edge){
		if(!debug)return;
		INodeExt source =  edge.getSource();
		List<INodeExt> list = (List<INodeExt>)source.getData();
		StringBuffer sourceBuf = new StringBuffer(" ");
		for(INodeExt n: list){
			sourceBuf.append("_");
			sourceBuf.append(n.getByteCodeOffset());
		} 

		INodeExt target = edge.getTarget();	
		List<INodeExt> list2 = (List<INodeExt>)target.getData();
		StringBuffer targetBuf = new StringBuffer(" ");
		for(INodeExt n: list2){
			targetBuf.append("_");
			targetBuf.append(n.getByteCodeOffset());
		} 

		System.out.println("  " + sourceBuf.toString() 
				+ "->" + targetBuf.toString()
				+ " counter=" + edge.getCounter());
	}

	private static void printTransformedNode(INodeExt node){
		if(!debug)return;
		List<INodeExt> list = (List<INodeExt>)node.getData();
		StringBuffer buf = new StringBuffer(" ");
		for(INodeExt n: list){
			buf.append("_");
			buf.append(n.getByteCodeOffset());
		}
		buf.append(" counter=");
		buf.append(node.getCounter());
		System.out.println("  " + buf.toString());
	}

	private static void printTransformedGraph(IDirectedGraphExt graph){
		if(!debug)return;
		System.out.println("Graph := {");
		System.out.println(" equivalenceClassList := {");
		INodeListExt nodes = graph.getNodeList();
		for(int i = 0; i < nodes.size(); i++){
			INodeExt node = nodes.getNodeExt(i);
			printTransformedNode(node);

		}
		System.out.println(" }");

		System.out.println(" edges := {");
		IEdgeListExt edges = graph.getEdgeList();
		for(int i = 0; i < edges.size(); i++ ){
			IEdgeExt edge = edges.getEdgeExt(i);
			printTransformedEdge(edge);
		}

		System.out.println(" }");
		System.out.println("}");
	}

	public static void printGraph(IDirectedGraphExt graph){
		System.out.println("Graph := {");
		System.out.println(" nodes := {");
		INodeListExt nodes = graph.getNodeList();
		for(int i = 0; i < nodes.size(); i++){
			System.out.println("  " + nodes.getNodeExt(i));
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
}
