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

package com.drgarbage.controlflowgraph;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.graph.Node;

import com.drgarbage.controlflowgraph.intf.IBasicBlock;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.MarkEnum;

/**
 * Node extention structure.
 *
 * @author Sergej Alekseev 
 * @version $Revision: 1523 $
 * $Id: NodeExt.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class NodeExt extends Node implements INodeExt {

	/* Variables for Graph Algorithms*/
	private boolean visited = false;
	private MarkEnum mark = MarkEnum.DEFAULT;
	private IBasicBlock basicBlock = null;
	private int counter = 0;

	/* byte code variables */
	private int byteCodeOffset = -1;
	private String byteCodeString = null;
	private int vertexType = -1;
	
	/* Extension */
	private String toolTipText = null;
	
	/* Figure for visualization of node */
	private IFigure figure = null;
	
	/* long description variable */
	private String longDescr = null;
	
	/** Default constructor */
	public NodeExt(){
		new NodeExt(null);
	}
	
	/**
	 * Constructs a node with the given data object
	 * @param data an arbitrary data object
	 */
	public NodeExt(Object data){
		super(data);
		incoming = new EdgeListExt();
		outgoing = new EdgeListExt();
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.intf.INodeExt#getIncomingEdgeList()
	 */
	public IEdgeListExt getIncomingEdgeList(){
		return (IEdgeListExt)this.incoming;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.intf.INodeExt#getOutgoingEdgeList()
	 */
	public IEdgeListExt getOutgoingEdgeList(){
		return (IEdgeListExt)this.outgoing;
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.intf.INodeExt#isVisited()
	 */
	public boolean isVisited() {
		return visited;
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#setVisited(boolean)
	 */
	public void setVisited(boolean b) {
		visited = b;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#getData()
	 */
	public Object getData() {
		return data;
	}


	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#setData(Object)
	 */
	public void setData(Object data) {
		this.data = data;
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#getX()
	 */
	public int getX() {
		return x;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#setX(int)
	 */
	public void setX(int x) {
		this.x = x;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#getY()
	 */
	public int getY() {
		return y;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#setY(int)
	 */
	public void setY(int y) {
		this.y = y;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#getHeight()
	 */
	public int getHeight() {
		return height;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#setHeight(int)
	 */
	public void setHeight(int h) {
		height = h;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#setWidth(int)
	 */
	public void setWidth(int w) {
		width = w;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#getWidth()
	 */
	public int getWidth() {
		return width;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#getFigure()
	 */
	public IFigure getFigure() {
		return figure;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#setFigure(IFigure)
	 */
	public void setFigure(IFigure f) {
		figure = f;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#getMark()
	 */
	public MarkEnum getMark() {
		return mark;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#setMark(MarkEnum)
	 */
	public void setMark(MarkEnum mark) {
		this.mark = mark;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#getBasicBlockReference()
	 */
	public IBasicBlock getBasicBlockReference() {
		return basicBlock;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#setBasicBlockReference()
	 */
	public void setBasicBlockReference(IBasicBlock bb) {
		basicBlock = bb;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#setByteCodeOffset()
	 */
	public void setByteCodeOffset(int byteCodeOffset) {
		this.byteCodeOffset = byteCodeOffset;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#getByteCodeOffset(int)
	 */
	public int getByteCodeOffset() {
		return byteCodeOffset;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#setByteCodeString(String)
	 */
	public void setByteCodeString(String byteCodeString) {
		this.byteCodeString = byteCodeString;	
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#getByteCodeString()
	 */
	public String getByteCodeString() {
		return byteCodeString;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#getToolTipText()
	 */
	public String getToolTipText() {
		return toolTipText;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#setToolTipText(java.lang.String)
	 */
	public void setToolTipText(String text) {
		toolTipText = text;	
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#setVertexType(int)
	 */
	public void setVertexType(int type) {
		this.vertexType = type;
	}	

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#getVertexType()
	 */
	public int getVertexType() {
		return vertexType;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#getCounter()
	 */
	public int getCounter() {
		return counter;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#setCounter(int)
	 */
	public void setCounter(int counter) {
		this.counter = counter;
		
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeExt#incrementCounter()
	 */
	public void incrementCounter() {
		this.counter++;	
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.controlflowgraph.intf.INodeExt#getLongDescr()
	 */
	public String getLongDescr() {
		return longDescr;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.controlflowgraph.intf.INodeExt#setLongDescr(java.lang.String)
	 */
	public void setLongDescr(String text) {
		this.longDescr = text;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.graph.Node#toString()
	 */
	@Override
	public String toString(){
		StringBuffer buf= new StringBuffer("NODE-EXT: ");
		buf.append(", Visited=");
		buf.append(isVisited());
		buf.append(" ");
		buf.append(byteCodeOffset);
		buf.append(" ");
		buf.append(byteCodeString);
		return buf.toString();
	}


}
