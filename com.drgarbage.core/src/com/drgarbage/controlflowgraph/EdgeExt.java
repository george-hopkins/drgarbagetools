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

import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;

import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.MarkEnum;


/**
 * Edge extention structure.
 *
 * @author Sergej Alekseev 
 * @version $Revision$
 * $Id$
 */
public class EdgeExt extends Edge implements IEdgeExt {

	/* Algorithms */
	private boolean visited = false;
	private MarkEnum mark = MarkEnum.DEFAULT;
	private int counter;

	/**
	 * Constructs a new edge extention object with the given source and target nodes.  
	 * All other fields will have their default values.
	 * @param source the source Node
	 * @param target the target Node
	 */
	public EdgeExt(Node source, Node target) {
		super(source, target);
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.IEdgeExt#getSource()
	 */
	public INodeExt getSource(){
		return (NodeExt)this.source;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.IEdgeExt#getTarget()
	 */
	public INodeExt getTarget(){
		return (NodeExt)this.target;
	}

	
	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.IEdgeExt#getData()
	 */
	public Object getData() {
		return data;
	}


	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.IEdgeExt#setData(Object)
	 */
	public void setData(Object data) {
		this.data = data;
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.IEdgeExt#isVisited()
	 */
	public boolean isVisited() {
		return visited;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.IEdgeExt#setVisited(boolean)
	 */
	public void setVisited(boolean b) {
		visited = b;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.IEdgeExt#getMark()
	 */
	public MarkEnum getMark() {
		return mark;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.IEdgeExt#setMark(MarkEnum)
	 */
	public void setMark(MarkEnum mark) {
		this.mark = mark;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.IEdgeExt#getCounter()
	 */
	public int getCounter() {
		return counter;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.IEdgeExt#setCounter(int)
	 */
	public void setCounter(int counter) {
		this.counter = counter;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.IEdgeExt#incrementCounter()
	 */
	public void incrementCounter() {
		this.counter++;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		StringBuffer buf= new StringBuffer("EDGE-EXT: ");
		buf.append(getSource().getByteCodeOffset());
		buf.append("->");
		buf.append(getTarget().getByteCodeOffset());
		buf.append(", Visited=");
		buf.append(isVisited());
		buf.append(", Mark=");
		buf.append(getMark());
		return buf.toString();
	}

}
