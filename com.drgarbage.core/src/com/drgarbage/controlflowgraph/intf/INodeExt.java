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

package com.drgarbage.controlflowgraph.intf;

import org.eclipse.draw2d.IFigure;

/**
 * Vertex properties for graph algorithms.
 *
 * @author Sergej Alekseev  
 * @version $Revision$
 * $Id: INodeExt.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public interface INodeExt {

	/**
	 * Gets the node's x coordinate.
	 * @return x coordinate
	 */
	public int getX();
	
	/**
	 * Sets the node's x coordinate.
	 * @param x coordinate
	 */	
	public void setX(int x);
	
	/**
	 * Gets the node's y coordinate.
	 * @return y coordinate
	 */
	public int getY();

	/**
	 * Gets the node's y coordinate.
	 * @param y coordinate
	 */	
	public void setY(int y);
	
	/**
	 * Gets the node's width.
	 * @return the wight
	 */
	public int getWidth();
	
	/**
	 * Sets the node's width.
	 * @param the wigth
	 */
	public void setWidth(int w);
	
	/**
	 * Gets the height of the node.
	 * @return the height
	 */
	public int getHeight();

	/**
	 * Sets the node's height..
	 * @param the height
	 */
	public void setHeight(int h);
	
	/**
	 * Gets the arbitrary data object.
	 * @return data object
	 */
	public Object getData();

	/**
	 * Sets the arbitrary data object.
	 * @param data object
	 */
	public void setData(Object data);
	
	/**
	 * Gets the list of the outgoing edges.
	 * @return EdgeListExt object
	 */
	public IEdgeListExt getOutgoingEdgeList();
	
	/**
	 * Gets the list of the incoming edges.
	 * @return EdgeListExt object
	 */
	public IEdgeListExt getIncomingEdgeList();
	
	/** 
	 * Gets visited status.
	 * NOTE: used for graph algorithms.
	 * @return true if visited
	 */
	public boolean isVisited();

	/** 
	 * Sets visited flag.
	 * NOTE: used for graph algorithms.
	 * @param visited status
	 */
	public void setVisited(boolean b);

	/** 
	 * Gets the node figure.
	 * NOTE: used for graph visualisation.
	 * @return the figure object
	 */
	public IFigure getFigure();
	
	/** 
	 * Sets the node figure.
	 * NOTE: used for graph visualisation.
	 * @param the figure object
	 */	
	public void setFigure(IFigure f);
	
	/** 
	 * Gets mark flag.
	 * NOTE: For internal use only
	 * @return marker
	 */
	public MarkEnum getMark();

	/** 
	 * Sets mark flag.
	 * NOTE: For internal use only
	 * @param mark
	 */
	public void setMark(MarkEnum mark);
	
	/**
	 * Assigns a counter to the node.
	 * For graph algorithms.
	 * NOTE: For internal use only
	 * @param counter
	 */
	public void setCounter(int counter);
	
	/**
	 * Returns the counter of the node.
	 * For graph algorithms.
	 * NOTE: For internal use only
	 * @return counter
	 */
	public int getCounter();
	
	/**
	 * Increments the node counter by 1.
	 * For graph algorithms.
	 * NOTE: For internal use only
	 */
	public void incrementCounter();
	
	/** 
	 * Gets the basic block reference.
	 * NOTE: used for basic block graph creation.
	 * @return the figure object
	 */
	public IBasicBlock getBasicBlockReference();
	
	/** 
	 * Sets the basicblocks reference.
	 * NOTE: used for basic block graph creation.
	 * @param the basic block object
	 */	
	public void setBasicBlockReference(IBasicBlock bb);

	/** 
	 * Sets the bytecode adress of the node.
	 * @param the bytecode adress
	 * */
	public void setByteCodeOffset(int byteCodeOffset);
	
	/** 
	 * Gets the bytecode adress of the node.
	 * @return the bytecode adress
	 * */
	public int getByteCodeOffset();

	/** 
	 * Sets the bytecode string. For example iload_1, iload_2 ...
	 * @param the bytecode string
	 */
	public void setByteCodeString(String byteCodeString);

	
	/** 
	 * Gets the long description string
	 * @return text
	 */
	public String getLongDescr();
	
	/** 
	 * Sets the long description string
	 * @param text
	 */
	public void setLongDescr(String text);
	
	/** 
	 * Gets the bytecode string. For example iload_1, iload_2 ...
	 * @return the bytecode string
	 */
	public String getByteCodeString();

	/**
	 * Sets tooltip text.
	 * @param text
	 */
	public void setToolTipText(String text);

	/**
	 * Gets tooltip text.
	 * @return tooltip text
	 */
	public String getToolTipText();

	/** 
	  * Sets the vertex type in the control flow diagram: Simple, Decision, Goto ...
	  * @see com.drgarbage.controlflowgraph.intf.INodeType
	  */
	public void setVertexType(int type);
	
	/** 
	  * Vertex type in the control flow diagram: Simple, Decision, Goto ...
	  * @see com.drgarbage.controlflowgraph.intf.INodeType
	  */
	public int getVertexType();

}
