package com.drgarbage.algorithms;

import java.util.HashMap;
import java.util.Map;

import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.GraphUtils;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;

public class GetGraphToTree {

	public IDirectedGraphExt ConvertInputGraphsToTree(
			IDirectedGraphExt IDiredctedGraph)
			throws ControlFlowGraphException {
		
		GraphUtils.clearGraph(IDiredctedGraph);
		GraphUtils.clearGraphColorMarks(IDiredctedGraph);
		
		/*create spanning tree to avoid loops */
		IDirectedGraphExt spanningTree = Algorithms.doSpanningTreeAlgorithm(IDiredctedGraph, false);
		
		return spanningTree;
	}
	
}
