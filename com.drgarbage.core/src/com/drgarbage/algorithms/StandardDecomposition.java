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

import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.GraphUtils;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;
import com.drgarbage.controlflowgraph.intf.MarkEnum;

/**
 *  Algorithms for graph decomposition.
 *
 * @author Sergej Alekseev
 *  @version $Revision$
 *  $Id: StandardDecomposition.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class StandardDecomposition {
	
	private static int global_counter = 0;
	
	public static void decompose(IDirectedGraphExt graph){
		global_counter = 0;
		
		INodeExt node = null;
		INodeListExt ptreeAttachmentPoints = pheripheraltrees(graph.getNodeList());
		for(int i = 0; i < ptreeAttachmentPoints.size(); i++){
			node = ptreeAttachmentPoints.getNodeExt(i);
			if(!node.isVisited()){
				//System.out.println("Start: " + node);
				stopfreekernel(node);
			}
		}
		
		GraphUtils.clearGraph(graph);
	}

	public static void stopfreekernel(INodeExt node){
		int local_counter = -1; 
		
		if(node.isVisited())
			return;
		
		node.setVisited(true);
		local_counter = global_counter;
		//System.out.println(" >STOPFREEKERN: " + node + " g=" + global_counter);
		
		IEdgeListExt outgoingList =node.getOutgoingEdgeList();
		IEdgeExt edge = null;
		for(int i = 0; i < outgoingList.size(); i++){
			edge = outgoingList.getEdgeExt(i);

			if(edge.isVisited())
				continue;

			edge.setVisited(true);
			if(edge.getTarget().isVisited()){
				
				if(edge.getTarget().getMark() == MarkEnum.GREEN){
					
				}else{
					edge.getTarget().setMark(MarkEnum.ORANGE);
					edge.setMark(MarkEnum.ORANGE); //backedge
				}

				edge.getTarget().incrementCounter();
				global_counter++;
			}
			else{
				stopfreekernel(edge.getTarget());
			}

			if(local_counter == global_counter){
				node.setMark(MarkEnum.GREEN);
			}
		}
		
		IEdgeListExt incommingList = node.getIncomingEdgeList();
		for(int i = 0; i < incommingList.size(); i++){
			edge = incommingList.getEdgeExt(i);

			if(edge.isVisited())
				continue;

			edge.setVisited(true);
			if(edge.getSource().isVisited()){
				if(edge.getSource().getMark() == MarkEnum.GREEN){
					
				}else{
					edge.getSource().setMark(MarkEnum.ORANGE);
					edge.setMark(MarkEnum.ORANGE); //backedge
				}

				edge.getSource().incrementCounter();				
				global_counter++;
			}
			else{
				stopfreekernel(edge.getSource());
			}

			if(local_counter == global_counter){
					node.setMark(MarkEnum.GREEN);
			}
		}

		//step bacwards
		global_counter = global_counter - node.getCounter();
		//System.out.println(" <STOPFREEKERN: " + node + " g=" + global_counter + " l=" + local_counter);

		if(local_counter == global_counter){
			if(node.getMark() == MarkEnum.ORANGE){
				node.setMark(MarkEnum.RED);
				//System.out.println("STOPFREEKERN: RED " + node);				
			}
			else{
				node.setMark(MarkEnum.GREEN);
				//System.out.println("STOPFREEKERN: GREEN " + node);
			}
		}

		if(local_counter < global_counter){
			//node.setMark(MarkEnum.BLUE);
			//System.out.println("STOPFREEKERN: BLUE " + node);
			
			if(node.getMark() == MarkEnum.GREEN){
				node.setMark(MarkEnum.RED);
				//System.out.println("STOPFREEKERN: RED " + node);				
			}
			else if(node.getMark() == MarkEnum.DEFAULT){
				node.setMark(MarkEnum.BLUE);
				//System.out.println("STOPFREEKERN: BLUE" + node);
			}
		}
		
	}

	public static INodeListExt pheripheraltrees(INodeListExt nodeList){
		INodeListExt pTreeAttachmentPoints = GraphExtentionFactory.createNodeListExtention();
		INodeExt node = null;
		IEdgeListExt incomingList = null;
		IEdgeListExt outgoingList = null;
		IEdgeListExt uncoloredIncomingList = null;
		IEdgeListExt uncoloredOutgoingList = null;
		IEdgeExt edge = null;
		for(int i = 0; i < nodeList.size(); i++){
			node = nodeList.getNodeExt(i);
			if(node.getMark() == MarkEnum.DEFAULT){
				incomingList = node.getIncomingEdgeList();
				outgoingList = node.getOutgoingEdgeList();
				if(incomingList.size() == 0 || outgoingList.size() == 0){
				
					//System.out.println("Start: " + node);
					
					uncoloredIncomingList = collectUncoloredEdges(incomingList);
					uncoloredOutgoingList = collectUncoloredEdges(outgoingList);
					//System.out.println("inc.size()=" + uncoloredIncomingList.size() + " out.size()" + uncoloredOutgoingList.size());
					while((uncoloredIncomingList.size() + uncoloredOutgoingList.size()) == 1)
					{
						node.setMark(MarkEnum.GREEN);
						node.setVisited(true);
						//System.out.println("GREEN: " + node);
						
						if(uncoloredIncomingList.size() == 0){
							edge = uncoloredOutgoingList.getEdgeExt(0);
							edge.setMark(MarkEnum.GREEN);
							edge.setVisited(true);
							node = edge.getTarget();
						}
						else{
							edge = uncoloredIncomingList.getEdgeExt(0);
							edge.setMark(MarkEnum.GREEN);
							edge.setVisited(true);
							node = edge.getSource();
						}
						
						incomingList = node.getIncomingEdgeList();
						outgoingList = node.getOutgoingEdgeList();
						uncoloredIncomingList = collectUncoloredEdges(incomingList);
						uncoloredOutgoingList = collectUncoloredEdges(outgoingList);
					}
					
					if((uncoloredIncomingList.size() + uncoloredOutgoingList.size()) != 0){
						node.setMark(MarkEnum.LILA);
						node.setVisited(false);
						pTreeAttachmentPoints.add(node);
						//System.out.println("LILA: " + node);
					}
				}
			}
		}
		
		return pTreeAttachmentPoints;
	}
	
	private static IEdgeListExt collectUncoloredEdges(IEdgeListExt edgeList){
		IEdgeListExt l = GraphExtentionFactory.createEdgeListExtention();
		IEdgeExt edge = null;
		for(int i = 0; i < edgeList.size(); i++){
			edge = edgeList.getEdgeExt(i);
			if(edge.getMark() == MarkEnum.DEFAULT){
				l.add(edge);
			}
		}
		return l;
	}

}
