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

/**
 * Common graph methods.
 *
 * @author Sergej Alekseev  
 * @version $Revision$
 * $Id$
 */
public class GraphUtils {
	
	public static void clearGraph(IDirectedGraphExt graph){
		clearNodes(graph.getNodeList());
		clearEdges(graph.getEdgeList());
	}
	
	public static void clearNodes(INodeListExt list){
		for(int i = 0; i < list.size(); i++){
			list.getNodeExt(i).setVisited(false);
		}	
	}

	public static void clearEdges(IEdgeListExt list){
		for(int i = 0; i < list.size(); i++){
			list.getEdgeExt(i).setVisited(false);
		}
	}
	
	public static void clearGraphColorMarks(IDirectedGraphExt graph){
		clearNodesColorMarks(graph.getNodeList());
		clearEdgesColorMarks(graph.getEdgeList());
	}
	
	public static void clearNodesColorMarks(INodeListExt list){
		for(int i = 0; i < list.size(); i++){
			list.getNodeExt(i).setMark(MarkEnum.DEFAULT);
		}	
	}

	public static void clearEdgesColorMarks(IEdgeListExt list){
		for(int i = 0; i < list.size(); i++){
			list.getEdgeExt(i).setMark(MarkEnum.DEFAULT);
		}
	}
}
