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

package com.drgarbage.bytecodevisualizer.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.drgarbage.algorithms.ArborescenceFinder;
import com.drgarbage.algorithms.BasicBlockGraphVisitor;
import com.drgarbage.asm.render.intf.IInstructionLine;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.ControlFlowGraphGenerator;
import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.GraphUtils;
import com.drgarbage.controlflowgraph.intf.IBasicBlock;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;
import com.drgarbage.controlflowgraph.intf.INodeType;
import com.drgarbage.controlflowgraph.intf.MarkEnum;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.utils.Messages;

/**
 * The implementation of the Operand Stack View Page.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class OperandStackViewPageIml extends OperandStackViewPage {
	

	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.view.OperandStackViewPage#generateInput(java.util.List, com.drgarbage.bytecodevisualizer.view.OperandStackView.OperandStackView_ID)
	 */
	@Override
	protected Object generateInput(List<IInstructionLine> instructions,
			OperandStackView_ID id) {

		if(id == OperandStackView_ID.INSTR_LIST_VIEW)
			return generateInstructionListInput(instructions);
		else if (id == OperandStackView_ID.BASICBKLOCK_VIEW)
			return generatebasicBlockInput(instructions);
		else if (id == OperandStackView_ID.TREE_VIEW)
			return generateTreeInput(instructions);
		
		return null;
	}
	
    /**
     * Creates a structure for the operand stack view.
     * @param instructions list of byte code instructions
     * @return structure as a list
     */
    private  Object  generateInstructionListInput(List<IInstructionLine> instructions) {
		Node root = new Node();
		
		for(IInstructionLine n: instructions){
			Node child = new Node();
    		child.setObject(n);
    		child.setParent(root);
        	root.addhild(child);
		}
		
		return root;
    }
    
    /**
     * Creates a structure for the operand stack view.
     * @param instructions list of byte code instructions
     * @return  structure as a list of basic blocks
     */
	private  Object  generatebasicBlockInput(List<IInstructionLine> instructions) {
		IDirectedGraphExt graph = ControlFlowGraphGenerator.generateSynchronizedControlFlowGraphFrom(instructions, true);

		/* find basic blocks */
		GraphUtils.clearGraph(graph);
		BasicBlockGraphVisitor basicBlockVisitor = new BasicBlockGraphVisitor();
		try {
			basicBlockVisitor.start(graph);
		} catch (ControlFlowGraphException e) {
			BytecodeVisualizerPlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR, BytecodeVisualizerPlugin.PLUGIN_ID, e.getMessage(), e)
			);
		}

		IDirectedGraphExt basicBlockGraph = basicBlockVisitor.getBasicBlockGraph();

		return parseBasicBlockGraph(basicBlockGraph);
	}

	private Node parseBasicBlockGraph(IDirectedGraphExt basicBlockGraph){

		Node root = new Node();

		/* sort basic blocks */
		INodeListExt nodes = basicBlockGraph.getNodeList();
		Map<Integer, IBasicBlock> basicBlockList = new TreeMap<Integer, IBasicBlock>();
		for(int i = 0; i < nodes.size(); i++){
			IBasicBlock basicBlock = (IBasicBlock)nodes.getNodeExt(i);
			try {
				basicBlockList.put(basicBlock.getFirstBasicBlockVertex().getByteCodeOffset(), basicBlock);
			} catch (ControlFlowGraphException e) {
				BytecodeVisualizerPlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR, BytecodeVisualizerPlugin.PLUGIN_ID, e.getMessage(), e)
				);
			}
		}

		/* for all basic blocks */
		for(IBasicBlock basicBlock: basicBlockList.values()){
			Node child = new Node();
			child.setObject(basicBlock.getData());
			child.setParent(root);
			root.addhild(child);

			INodeListExt basicBlockInstructions = basicBlock.getBasicBlockVertices();
			for(int j = 0; j < basicBlockInstructions.size(); j++){
				INodeExt node = basicBlockInstructions.getNodeExt(j);
				Node instr = new Node();
				instr.setObject(node.getData());
				instr.setParent(child);
				child.addhild(instr);
			}
		}

		return root;
	}

    /**
     * Creates a structure for the operand stack view.
     * @param instructions list of byte code instructions
     * @return  structure as a tree
     */
    private  Object  generateTreeInput(List<IInstructionLine> instructions)
    {
		IDirectedGraphExt graph = ControlFlowGraphGenerator.generateSynchronizedControlFlowGraphFrom(instructions, true);
		
		/* remove back edges (loops) from the graph */
		removeBackEdges(graph);
		GraphUtils.clearGraph(graph);
		GraphUtils.clearGraphColorMarks(graph);
		
		/* 
		 * Mark nodes and create spanning tree.
		 * Marking should be done before the spanning tree is created, 
		 * because some information gets to be missing after execution 
		 * of the spanning tree algorithm.
		 */
		markNodes(graph);
		GraphUtils.clearGraph(graph);
		
		try {
		graph = ArborescenceFinder.find(graph);
		} catch (ControlFlowGraphException e) {
			Messages.error(e.toString());
		}
		GraphUtils.clearGraph(graph);
		
		Node root = new Node();
		List<INodeExt> listOfStartNodes = getAllStartNodes(graph);
		for(INodeExt n: listOfStartNodes){
			parseGraph(root, n);
		}

		/* 
		 * FIX: #13 NullPointerException when operand stack 
		 *      representation is created
		 *      
		 * Check if some nodes are still unvisited.
		 */
		
		INodeListExt nodeList = graph.getNodeList();
		for(int i = 0; i < nodeList.size(); i++){
			if(!nodeList.getNodeExt(i).isVisited()){
				parseGraph(root, nodeList.getNodeExt(i));
			}
		}
		
		GraphUtils.clearGraph(graph);
		GraphUtils.clearGraphColorMarks(graph);
		
		return root;
    }
    
    /**
     * Removes all back edges from the edge list and 
     * incidence lists of nodes.
     * @param graph control flow graph
     */
    private void removeBackEdges(IDirectedGraphExt graph){    	
    	IEdgeListExt backEdges = ArborescenceFinder.doFindBackEdgesAlgorithm(graph);
		
		IEdgeListExt edges = graph.getEdgeList();
		for(int i = 0; i < backEdges.size(); i++){
			IEdgeExt e = backEdges.getEdgeExt(i);
			INodeExt source = e.getSource(); 
			INodeExt target = e.getTarget();
			
			source.getOutgoingEdgeList().remove(e);
			target.getIncomingEdgeList().remove(e);
			edges.remove(e);
		}
    }
    
    /**
     * Returns the list of all nodes with incoming degree of 0.
     * @param graph control flow graph
     * @return list of start nodes
     */
    private List<INodeExt> getAllStartNodes(IDirectedGraphExt graph){
    	List<INodeExt> listOfStartNodes= new ArrayList<INodeExt>();
    	INodeListExt nodes = graph.getNodeList();
    	for(int i = 0; i < nodes.size(); i++){
    		INodeExt n = nodes.getNodeExt(i);
    		if(n.getIncomingEdgeList().size() == 0){
    			listOfStartNodes.add(n);
    		}
    	}
    	
    	return listOfStartNodes;
    }
    
    /**
     * Creates a tree structure for operand stack view.
     * @param work parent node
     * @param node current node or start node
     */
	private void parseGraph(Node work, INodeExt node){

    	int out = 0;
    	do{
    		IEdgeExt e;
    		
    		if(node.isVisited()){
    			return;
    		}
    		
    		Node child = new Node();
    		child.setObject(node.getData());

    		/* end of the block (if or switch ) */
    		if(node.getMark() == MarkEnum.RED){
    			if(work.getParent() != null){
    				work = work.getParent().getParent();
    			}
    		}
    		
    		child.setParent(work);
        	work.addhild(child);
    		node.setVisited(true);
    		
    		out = node.getOutgoingEdgeList().size();
    		if(out == 0){ /* last node */
    			return;
    		}
    		
    		/* switch or if block block */
    		if(node.getMark() == MarkEnum.ORANGE || node.getMark() == MarkEnum.GREEN) { 
    			IEdgeListExt edgeList = node.getOutgoingEdgeList();
    			
    			/* sort edges in the increasing order of the target offset */
    			Map<Integer, IEdgeExt> sortedEdgeList = new TreeMap<Integer, IEdgeExt>();
    			for(int j = 0; j < edgeList.size(); j++){
    				e = node.getOutgoingEdgeList().getEdgeExt(j);
    				sortedEdgeList.put(e.getTarget().getByteCodeOffset(), e);
    			}
    			
    			/* for all edges from the sorted list */
    			for(IEdgeExt e1: sortedEdgeList.values()){
    				Node switchChild = new Node();
        			switchChild.setParent(child);
        			if(e1.getData()!=null){
        				switchChild.setObject(e1.getData().toString());
        			}
        			else{
        				switchChild.setObject(CoreMessages.Error);
        			}
        			child.addhild(switchChild);
        			
        			e1.setVisited(true);
        			parseGraph(switchChild, e1.getTarget());
    			}
    		}

    		e = node.getOutgoingEdgeList().getEdgeExt(0);
    		e.setVisited(true);
    		node = e.getTarget();
    		
    	} while(out != 0);
    }
	
    /**
     * Mark following nodes: if, switch and nodes with incoming degree
     * greater than 1 (end of block).
     * @param graph control flow graph
     */
    private void markNodes(IDirectedGraphExt graph){
    	INodeListExt graphNodelist = graph.getNodeList();
    	
    	for(int i = 0; i < graphNodelist.size(); i++){
    		INodeExt node = graphNodelist.getNodeExt(i);

    		if(node.isVisited()){
    			if(node.getOutgoingEdgeList().size() == 0){
    				/* last node has been reached */
    				return;
    			}
    			
    			IEdgeExt e = node.getOutgoingEdgeList().getEdgeExt(0);
    			e.setVisited(true);
    			node = e.getTarget();
    			continue;
    		}
    		
    		if(node.getIncomingEdgeList().size() > 1){
    			/* end of block */
    			node.setMark(MarkEnum.RED);
    		}
    		
    		int out = node.getOutgoingEdgeList().size();
    		if(out == 2){ /* if */
    			node.setMark(MarkEnum.GREEN);
    			IEdgeListExt outEdges = graphNodelist.getNodeExt(i).getOutgoingEdgeList();
    			outEdges.getEdgeExt(1).setMark(MarkEnum.BLACK); /* true */
    			outEdges.getEdgeExt(0).setMark(MarkEnum.WHITE); /* false */
    		}
    		else if(out > 2){ /* switch */
    			node.setMark(MarkEnum.ORANGE);
    			markSwitchNodes(graphNodelist, i, node.getOutgoingEdgeList().size());
    		}
    		else if(out == 1 && node.getVertexType() == INodeType.NODE_TYPE_IF){
    			/* special case: do- while loop */
    			node.setMark(MarkEnum.GREEN);
    			IEdgeListExt outEdges = graphNodelist.getNodeExt(i).getOutgoingEdgeList();
    			outEdges.getEdgeExt(0).setMark(MarkEnum.BLACK); /* true */
    			
    			/* false - create pseudo arc */
//    			INodeExt loopTargetNode = GraphExtentionFactory.createNodeExtention("LOOP");
//    			graphNodelist.add(loopTargetNode);
//    			IEdgeExt pseudoEdge = GraphExtentionFactory.createEdgeExtention(node, loopTargetNode);
    			
    			IEdgeExt pseudoEdge = GraphExtentionFactory.createEdgeExtention(node, node);
    			pseudoEdge.setData("false");
    			outEdges.add(pseudoEdge);
    		}
    		
    		node.setVisited(true);
    	}
    }
    
    /**
     * Recursive method for marking nodes in the switch block.
     * A token is initialized with the number of the outgoing
     * edges. The token value is reduced by the nodes with the 
     * number of incoming edges > 1. The recursion ends if the
     * token value reaches 0. 
     * @param graphNodelist
     * @param nodeIndex
     * @param count - token
     * @return token value
     */
    private int markSwitchNodes(INodeListExt graphNodelist, int nodeIndex, int count){
    	for(int i = (nodeIndex + 1); i < graphNodelist.size(); i++){
    		INodeExt node = graphNodelist.getNodeExt(i);

    		if(node.isVisited()){
    			continue;
    		}
    		
    		int out = node.getOutgoingEdgeList().size();
    		if(out > 1){ /* new switch block found */
    			node.setMark(MarkEnum.ORANGE);
    			count -= markSwitchNodes(graphNodelist, i, node.getOutgoingEdgeList().size()) + 1;
    		}
    		
    		int inc = node.getIncomingEdgeList().size();
    		
    		int res = (count - inc);
    		if(res == 0){
    			/* end of block */
    			node.setMark(MarkEnum.RED);
    			node.setVisited(true);
    			return res;
    		}
    		
    		if(res < 0){		
    			return res;
    		}

    		if(inc > 1){
    			count -= inc -1;
    		}

    		node.setVisited(true);
    	}
    	
    	return 0;
    }
}


