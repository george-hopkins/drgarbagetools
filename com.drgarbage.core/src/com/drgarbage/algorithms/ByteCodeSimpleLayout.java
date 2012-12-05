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

import java.util.Iterator;
import java.util.TreeMap;

import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;
import com.drgarbage.controlflowgraph.intf.INodeType;

/**
 *  Simple Layout for placing of nodes.
 *
 *  @author Sergej Alekseev  
 *  @version $Revision$
 *  $Id: ByteCodeSimpleLayout.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class ByteCodeSimpleLayout extends DepthFirstSearchBaseVisitor {

	/* default */
	private int offset = 39;
	private boolean calculateYfromLineNumber = true;
	
	private int minX = 0;
	private int maxX = 0;
	
	IDirectedGraphExt graph = null;
	
	INodeExt activeNode = null;

	/**
	 * Constructor.
	 * @throws ControlFlowGraphException 
	 */
	public ByteCodeSimpleLayout(IDirectedGraphExt graph) throws ControlFlowGraphException{
		this.graph = graph;

		initialize();
	}
	
	/**
	 * Constructor.
	 * @throws ControlFlowGraphException 
	 */
	public ByteCodeSimpleLayout(IDirectedGraphExt graph, int offset) throws ControlFlowGraphException{
		this.graph = graph;
		this.offset = offset;

		initialize();
	}
	
	/**
	 * Constructor.
	 * @throws ControlFlowGraphException 
	 */
	public ByteCodeSimpleLayout(IDirectedGraphExt graph, int offset, boolean overwriteYCoordinate) throws ControlFlowGraphException{
		this.graph = graph;
		this.offset = offset;
		this.calculateYfromLineNumber = overwriteYCoordinate;

		initialize();
	}
	
	/**
	 * @return the maxX
	 */
	public int getMaxX() {
		return maxX;
	}


	/**
	 * @return the minX
	 */
	public int getMinX() {
		return minX;
	}
	
	private void initialize() throws ControlFlowGraphException{

		//init all start nodes
		INodeListExt nodeList = graph.getNodeList();

		if(nodeList.size() == 0){
			throw new ControlFlowGraphException("The node list is empty.");
		}
		
		/* initialize nodes */
		//first instruction
		INodeExt node = nodeList.getNodeExt(0);
		node.setX(offset);
		node.setHeight(0); /* initialize height */
		
		
		int startOffset = offset * 4;
		for(int i = 1; i < nodeList.size(); i++){
			node = nodeList.getNodeExt(i);
			
			node.setHeight(0);/* initialize height */
			
			/* other start nodes */
			if(node.getIncomingEdgeList().size() == 0){
				node.setX(startOffset);
				startOffset = startOffset + offset * 2;
			 }
			else{
				node.setX(-1);
			}
		}
		
  		/* get node Properties */
  		INodeListExt nodes = graph.getNodeList();
  		int offsetY = 16;
		for(int i = 0; i< nodes.size(); i++){
			node = nodes.getNodeExt(i);

			if(calculateYfromLineNumber){
				node.setY(node.getY() * offset + offset/4);
			}
			else{
				if(i != 0){
					int h = nodes.getNodeExt(i - 1).getHeight();
					offsetY = offsetY + h + offset;
				}
				node.setY(offsetY);
			}
			
			//set size of the node
	  		node.setWidth(offset + offset/2);
	  		if(node.getHeight() == 0){
	  			node.setHeight(offset/2);	
	  		}
		}

	}
	
	/**
	 * Start method.
	 */
	public void visit(){
		try {
			visit(graph);
		} catch (ControlFlowGraphException e) {
			e.printStackTrace(System.err);
		}
	}
	
	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offsetX) {
		this.offset = offsetX;
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.DepthFirstSearchBaseVisitor#visit(com.drgarbage.visualgraphic.controlflowgraph.intf.IDirectedGraphExt)
	 */
	@Override	
	public void visit(IDirectedGraphExt graph) throws ControlFlowGraphException{
		INodeListExt nodeList = graph.getNodeList();
		
		if(	nodeList == null || nodeList.size() < 1){
			throw new ControlFlowGraphException("Can't start DFS. Vertex List is empty.");
		}
		
		INodeExt node = null;
		for(int i = 0; i < nodeList.size(); i++ ){
			node = nodeList.getNodeExt(i);
			
			if(node.getX() == -1){
				node.setX(activeNode.getX() - offset * 2);
			}
			
			traverse(node);
		}
	}
	

	public void visitNode(INodeExt node) {
		activeNode = node;
		
		//set x coordinate
		IEdgeListExt outList = node.getOutgoingEdgeList();
		
		switch(node.getVertexType()) {
			case INodeType.NODE_TYPE_IF:
					IEdgeExt e1 = outList.getEdgeExt(0);
					IEdgeExt e2 = outList.getEdgeExt(1);
	
							if(e1.getTarget().getX() == -1 && e2.getTarget().getX() == -1){
								e1.getTarget().setX(node.getX() + offset);
								e2.getTarget().setX(node.getX() - offset);
							}
							else if(e1.getTarget().getX() != -1 && e2.getTarget().getX() == -1){
								if(e1.getTarget().getX() > node.getX()){
									e2.getTarget().setX(node.getX() - offset);
								}
								else{
									e2.getTarget().setX(node.getX() + offset);
								}
							}
							else if(e1.getTarget().getX() == -1 && e2.getTarget().getX() != -1){
								if(e2.getTarget().getX() > node.getX()){
									e1.getTarget().setX(node.getX() - offset);
								}
								else{
									e1.getTarget().setX(node.getX() + offset);
								}
							}
					break;
			case INodeType.NODE_TYPE_SWITCH:
					//sort nodes by y coordinate
	             	TreeMap<Object, INodeExt> tm = new TreeMap<Object, INodeExt>();
	        		IEdgeExt e = null;
					for (int j = 0; j < outList.size(); j++){			 
						 e = outList.getEdgeExt(j);
						 if(!e.getTarget().isVisited() && e.getTarget().getX() == -1){
							 tm.put(new Integer(e.getTarget().getByteCodeOffset()), e.getTarget()); 
						 }
					}

					Object[] array = tm.values().toArray();
					
					int positionX = node.getX() - (tm.size() * offset * 2)/2;
					
					for(int j = 0; j < array.length; j++){
						positionX = positionX + (offset * 2);
						((INodeExt)array[j]).setX(positionX);
					}	

			default:
				if( outList.size() > 0){
					e = outList.getEdgeExt(0);
					if(!e.getTarget().isVisited() && e.getTarget().getX() == -1){
						e.getTarget().setX(node.getX());
					}
				}

		}

		//actualize min max coordinates
		if(minX > node.getX()){
			minX = node.getX();		
		}
		
		if(maxX < node.getX()){
			maxX = node.getX();
		}
	 
	}

	@Override
	protected void postHandling() throws ControlFlowGraphException{
		//nothing to do
		activeNode = null;
	}

	protected void traverse(INodeExt node){
		if(stopRecurion)
			return;

		if(node.isVisited())
			return;
		
		node.setVisited(true);

		//vertex visitor hook
		visitNode(node);
		
		if(node.getVertexType() == INodeType.NODE_TYPE_SWITCH){

			IEdgeListExt outList = node.getOutgoingEdgeList();
			IEdgeExt e = null;
			//sort by Y
	     	TreeMap<Object, IEdgeExt> tm = new TreeMap<Object, IEdgeExt>();
			for(int i = 0; i < outList.size(); i++){		
				e = outList.getEdgeExt(i);
				tm.put(new Integer(e.getTarget().getY()), e); 
			}
			
			Iterator<IEdgeExt> it = tm.values().iterator();
			while(it.hasNext()){
				e = it.next();

				if(allIncomingEdgesVisited(e.getTarget()))
					traverse(e.getTarget());
			}
		
		}
		else{
			IEdgeListExt outList = node.getOutgoingEdgeList();
			IEdgeExt e = null;
			for(int i = 0; i < outList.size(); i++){		
				e = outList.getEdgeExt(i);
				e.setVisited(true);

				if(allIncomingEdgesVisited(e.getTarget()))
					traverse(e.getTarget());
			}
		}
	
	}
	
	//TODO: optimize
	private boolean allIncomingEdgesVisited(INodeExt n){
		IEdgeListExt edgeList = n.getIncomingEdgeList();
		for(int i = 0; i < edgeList.size(); i++){
			if(!edgeList.getEdgeExt(i).isVisited())
				return false;
		}
		
		return true;		
	}
	
}
