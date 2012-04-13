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

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.draw2d.graph.CompoundDirectedGraph;

import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;

/**
 * Control Flow Graph extention structure.
 *
 * @author Sergej Alekseev 
 * @version $Revision: 1523 $
 * $Id: DirectedGraphExt.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class DirectedGraphExt extends CompoundDirectedGraph implements IDirectedGraphExt{

	/**
	 * User specific object;
	 */
	private Map<String, Object> o = null;

	/**
	 * Default constructor.
	 */
	public DirectedGraphExt(){
		edges = new EdgeListExt();
		nodes = new NodeListExt();
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.intf.IDirectedGraphExt#getEdgeList()
	 */
	public IEdgeListExt getEdgeList() {
		return (IEdgeListExt)edges;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.intf.IDirectedGraphExt#getNodeList()
	 */
	public INodeListExt getNodeList() {
		return (INodeListExt)nodes;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.intf.IDirectedGraphExt#getUserObject()
	 */
	public Map<String, Object> getUserObject() {
		if(this.o == null){
			this.o = new TreeMap<String, Object>();
		}

		return this.o;
	}
	
}
