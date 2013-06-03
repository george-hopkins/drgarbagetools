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

import org.eclipse.draw2d.graph.Node;

import com.drgarbage.controlflowgraph.BasicBlock;
import com.drgarbage.controlflowgraph.DirectedGraphExt;
import com.drgarbage.controlflowgraph.EdgeExt;
import com.drgarbage.controlflowgraph.EdgeListExt;
import com.drgarbage.controlflowgraph.NodeExt;
import com.drgarbage.controlflowgraph.NodeListExt;

/**
 * Factory for graph extentions.
 *
 * @author Sergej Alekseev  
 * @version $Revision$
 * $Id$
 */
public class GraphExtentionFactory {

	/**
	 * Create a directed graph extention.
	 * @return DirectedGraphExt object
	 */
	public static IDirectedGraphExt createDirectedGraphExtention(){
		return new DirectedGraphExt();
	}
	
	/**
	 * Create a node extention with the given data object
	 * @param data an arbitrary data object
	 * @return NodeExt object
	 */
	public static INodeExt createNodeExtention(Object data){
		return new NodeExt(data);
	}
	
	/**
	 * Create a new edge extention object with the given source and target nodes.  
	 * All other fields will have their default values.
	 * @param source the source Node
	 * @param target the target Node
	 * @return EdgeExt object
	 */
	public static IEdgeExt createEdgeExtention(INodeExt source, INodeExt target){
		return new EdgeExt((Node)source, (Node)target);
	}

	/**
	 * Create a new edge list extention object.
	 * @return IEdgeListExt object
	 */
	public static IEdgeListExt createEdgeListExtention(){
		return new EdgeListExt();
	}

	/**
	 * Create a new node list extention object.
	 * @return INodeListExt object
	 */
	public static INodeListExt createNodeListExtention(){
		return new NodeListExt();
	}
	
	/**
	 * Create a new basicblock object.
	 * @return IBasicBlock object
	 */
	public static IBasicBlock createBasicBlock(){
		return new BasicBlock();
	}
}
