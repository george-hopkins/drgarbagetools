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

package com.drgarbage.controlflowgraphfactory.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;
import com.drgarbage.controlflowgraph.intf.INodeType;
import com.drgarbage.visualgraphic.model.Connection;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;
import com.drgarbage.visualgraphic.model.StartVertex;
import com.drgarbage.visualgraphic.model.VertexBase;

/**
 * Help methods for generation of graphs from a model.
 *
 * @author Sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: LayoutAlgorithmsUtils.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class LayoutAlgorithmsUtils {

	/**
	 * Generates a control flow graph from a diagram.
	 * @param controlFlowGraphDiagram
	 * @return control flow graph 
	 */
	public static IDirectedGraphExt generateGraph(ControlFlowGraphDiagram controlFlowGraphDiagram){
		IDirectedGraphExt graph = GraphExtentionFactory.createDirectedGraphExtention();
        INodeListExt nodes = graph.getNodeList();
        IEdgeListExt edges = graph.getEdgeList();
        
        HashMap<VertexBase, INodeExt> hashTable = new HashMap<VertexBase, INodeExt>();   
        List<Connection> connections = new ArrayList<Connection>();
        
        /* nodes */
		List<VertexBase> children = controlFlowGraphDiagram.getChildren();
		
		/* WORKAROUND: move the start vertex to the first position */
		int size = children.size();
		if(size == 2){ /* at least one graph node and START or EXIT node */
			VertexBase vb = children.get(1); /* the last element */
			if(vb instanceof StartVertex) {
				children.set(1, children.get(0)); /* just exchange the positions of elements */
				children.set(0, vb);
			}
		}
		else if(children.size() > 2){
			
			for(int i = children.size() - 1; i >= 0; i--){
				VertexBase vb = children.get(i);
				if(vb instanceof StartVertex) {
					
					List<VertexBase> listNew = new ArrayList<VertexBase>();
					listNew.add(vb);
					for(int j = 0; j < i; j++){
						listNew.add(children.get(j));
					}
					
					for(int j = i + 1; j < children.size(); j++){
						listNew.add(children.get(j));
					}

					children = listNew;					
					break;
				}
			}
		}
		
		Iterator<VertexBase> it = children.iterator();
		VertexBase vb = null;
		INodeExt node = null;
		while(it.hasNext()){
			vb = it.next();			
			node = GraphExtentionFactory.createNodeExtention(vb);
			node.setWidth(vb.getSize().width);
			node.setHeight(vb.getSize().height);
			
			int outgoingDegree = vb.getSourceConnections().size();
			if(outgoingDegree == 2){
				node.setVertexType(INodeType.NODE_TYPE_IF);
			}
			else if(outgoingDegree > 2){
				node.setVertexType(INodeType.NODE_TYPE_SWITCH);
			}
			else{
				node.setVertexType(INodeType.NODE_TYPE_SIMPLE);
			}

			nodes.add(node);
			hashTable.put(vb, node);
			
			List<Connection> targetConnection = vb.getTargetConnections();
			Iterator<Connection> itTargetConnections = targetConnection.iterator();
			while(itTargetConnections.hasNext()){
				connections.add(itTargetConnections.next());
			}
		}

		/* edges */
		Connection con = null;
		INodeExt source = null;
		INodeExt target = null;
		IEdgeExt edge = null;		

		/* backward iteration, important for bytecode placing algorithm. (mirror effect) */
		for(int i = connections.size() - 1; i >= 0; i--){
			con = connections.get(i);
			source = hashTable.get(con.getSource());
			target = hashTable.get(con.getTarget());
			edge = GraphExtentionFactory.createEdgeExtention(source, target);
			edges.add(edge);
		}
		
		return graph;
	}
}
