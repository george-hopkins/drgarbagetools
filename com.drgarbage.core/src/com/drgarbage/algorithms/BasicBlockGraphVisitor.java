/**
 * Copyright (c) 2008-2012, Dr. Garbage Community
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
import java.util.List;

import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.IBasicBlock;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;
import com.drgarbage.core.CorePlugin;
import com.drgarbage.logger.TraceTopics;

/**
 *  Find Basic Block in the graph.
 *
 *  @author Sergej Alekseev  
 *  @version $Revision: 1523 $
 *  $Id: BasicBlockGraphVisitor.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class BasicBlockGraphVisitor extends DepthFirstSearchForward {

	private boolean openBB = false;
	
	private IDirectedGraphExt basicBlockGraph = null;
	private List<IBasicBlock> basicBlocks = new ArrayList<IBasicBlock>();
	
	
	private IBasicBlock bb = null;
	private int basicBlockCounter = 0;
	
	
	public BasicBlockGraphVisitor(){
		/* activate logger */
        debugOn = TraceTopics.LOG_VISUALGRAPHIC;
        if(debugOn)log("BasicBlockGraphVisitor: Create Basic Block Graph");
        
		basicBlockGraph = GraphExtentionFactory.createDirectedGraphExtention();
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.DepthFirstSearchForward#visitEdge(com.drgarbage.visualgraphic.controlflowgraph.intf.IEdgeExt)
	 */
	@Override
	public void visitEdge(IEdgeExt edge) {
		if(debugOn)log("visit EDGE: " + edge.getSource().getByteCodeOffset() + "->" + edge.getTarget().getByteCodeOffset());
		if(openBB){
			if(edge.getTarget().isVisited()){			
				openBB = false;
				if(debugOn)log("target visited");
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.DepthFirstSearchForward#visitNode(com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt)
	 */
	@Override
	public void visitNode(INodeExt node) {
		if(debugOn)log("visit NODE: " + node.getByteCodeOffset());
		
		int incdegree = node.getIncomingEdgeList().size();
		int outdegree = node.getOutgoingEdgeList().size();

		if(debugOn)log("incdegree: " + incdegree +", outdegree: " + outdegree);

		if( outdegree > 1 ){
			
			/* create new basicblock only if this node the first one */
			if(bb == null){
				bb = GraphExtentionFactory.createBasicBlock();			
				basicBlocks.add(bb);
				basicBlockCounter ++;
				bb.setData("B" + basicBlockCounter);
				if(debugOn)log("Create BB: " + bb.getData());
			}
			
			/* add vertex to the basicblock */
			bb.addVertex(node);
			node.setBasicBlockReference(bb);
			if(debugOn)log("   add " + node.getByteCodeOffset() + " to BB=" + bb.getData());
			
			/* close basic block */
			openBB = false;
			if(debugOn)log("openBB = " + openBB);
			
			return;
		}

		if( incdegree > 1){
			
			/* create new basicblock */
			bb = GraphExtentionFactory.createBasicBlock();			
			basicBlocks.add(bb);
			basicBlockCounter ++;
			bb.setData("B" + basicBlockCounter);
			if(debugOn)log("Create BB: " + bb.getData());
			
			/* close basic block */
			if(outdegree == 0){
				openBB = false;
			}
			else{
				openBB = true;
			}

			if(debugOn)log("openBB = " + openBB);
			
			/* add vertex to the basicblock */
			bb.addVertex(node);
			node.setBasicBlockReference(bb);
			if(debugOn)log("   add " + node.getByteCodeOffset() + " to BB=" + bb.getData());
			
			return;
		}
		
		if(incdegree == 0 ){
			/* create new basicblock */
			bb = GraphExtentionFactory.createBasicBlock();
			basicBlocks.add(bb);
			basicBlockCounter++;
			bb.setData("B" + basicBlockCounter);
			openBB = true;
			if(debugOn)log("Create BB: " + bb.getData());
			if(debugOn)log("openBB = " + openBB);
			
			/* add vertex to the basicblock */		
			bb.addVertex(node);
			node.setBasicBlockReference(bb);
			if(debugOn)log("   add " + node.getByteCodeOffset() + " to BB=" + bb.getData());

			return;
		}
		
		if(outdegree == 0 ){
			if(!openBB){
				/* create new basicblock */
				bb = GraphExtentionFactory.createBasicBlock();
				basicBlocks.add(bb);
				basicBlockCounter++;
				bb.setData("B" + basicBlockCounter);
				if(debugOn)log("Create BB: " + bb.getData());
			}
			
			/* add vertex to the basicblock	*/			
			bb.addVertex(node);
			node.setBasicBlockReference(bb);
			if(debugOn)log("   add " + node.getByteCodeOffset() + " to BB=" + bb.getData());			
			
		    /* close basicblock */
			openBB = false;
			
			return;
		}
		
		if(incdegree == 1 && outdegree == 1){
			
			if(!openBB){
				/* create new basicblock */
				bb = GraphExtentionFactory.createBasicBlock();
				basicBlocks.add(bb);
				basicBlockCounter++;
				bb.setData("B" + basicBlockCounter);
				openBB = true;
				if(debugOn)log("Create BB: " + bb.getData());
			}

			if(debugOn)log("openBB = " + openBB);
			
			/* add vertex to the basicblock	*/
			bb.addVertex(node);
			node.setBasicBlockReference(bb);
			if(debugOn)log("   add " + node.getByteCodeOffset() + " to BB=" + bb.getData());
		}
		
		
	}

	@Override
	protected void postHandling() throws ControlFlowGraphException {
		if(debugOn)log("POST HANDLING:");
		
		INodeListExt basicBlocksList = basicBlockGraph.getNodeList();
		IEdgeListExt basicBlockEdges = basicBlockGraph.getEdgeList();
		
		/* add nodes and edges to the basic block graph */
		INodeExt firstNode = null;
		IEdgeListExt incomimgList = null;
		IEdgeExt oldEdge = null;
		IEdgeExt newEdge = null;
		for(IBasicBlock n : basicBlocks){
			if(debugOn)log("add Basic block" + n.getData() + " to list.");
			
			basicBlocksList.add(n);
			firstNode = n.getFirstBasicBlockVertex();
			if(debugOn)log("  first node: " + firstNode.getByteCodeOffset());
			
			incomimgList = firstNode.getIncomingEdgeList();
			if(incomimgList.size() != 0){				
				for(int i = 0; i < incomimgList.size(); i++){
					oldEdge = incomimgList.getEdgeExt(i);
					newEdge = GraphExtentionFactory.createEdgeExtention(oldEdge.getSource().getBasicBlockReference(), n);
					newEdge.setData(oldEdge.getData()); /* copy edge label */
					basicBlockEdges.add(newEdge);
					oldEdge.setData(newEdge);
				}
			}
		}
		
		if(debugOn)
			printBasicBlockGraph(basicBlockGraph);
	}

	/**
	 * @return the basicBlockGraph
	 */
	public IDirectedGraphExt getBasicBlockGraph() {
		return basicBlockGraph;
	}
	
	
	private static void printBasicBlockGraph(IDirectedGraphExt graph){
		
		INodeListExt basicBlocksNodes = graph.getNodeList();
		IEdgeListExt basicBlockEdges = graph.getEdgeList();
		IBasicBlock n = null;
		INodeListExt basicBlockVertices = null;
		IEdgeExt e = null;
		
		log("START GRAPH");
		log("NODES:");
		for(int i = 0; i < basicBlocksNodes.size(); i++ ){
			n = (IBasicBlock)basicBlocksNodes.getNodeExt(i);
			log(n.getData() + "{");
			
			basicBlockVertices = n.getBasicBlockVertices();
			for(int j = 0; j < basicBlockVertices.size(); j++){
				log("   " + basicBlockVertices.getNodeExt(j));
			}

			log(" }");
		}
		
		log("EDGES:");
		for(int i = 0; i < basicBlockEdges.size(); i++ ){
			e = basicBlockEdges.getEdgeExt(i);
			log(e.getSource().getData() + "->" + e.getTarget().getData());
		}
		
		log("END GRAPH");
	}
	
    /**
     * Flag for logging while reading and writing class files.
     */
    protected static boolean debugOn = false;
    
    /**
     * Utility method for derived structures. Dump a specific debug message.
     *
     * @param message the debug message
     */
    protected static void log(String message) {
        if (debugOn) {
        	CorePlugin.log(CorePlugin.createInfoStatus(message));
        	//System.out.println(message);
        }
    }

}
