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

import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;

/**
 *  Depth first search algorithm. Traverse of arcs is allowed only in backward direction.
 *
 *  @author Sergej Alekseev  
 *  @version $Revision: 1523 $
 *  $Id: DepthFirstSearchBackward.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public abstract class DepthFirstSearchBackward extends DepthFirstSearchBaseVisitor {

	protected void traverse(INodeExt node){
		if(stopRecurion)
			return;

		if(node.isVisited())
			return;
		
		node.setVisited(true);

		//vertex visitor hook
		visitNode(node);
		
		IEdgeListExt inList = node.getIncomingEdgeList();
		IEdgeExt e = null;
		for(int i = 0; i < inList.size(); i++){
			e = inList.getEdgeExt(i);

			//edge visitor hook
			visitEdge(e);

			traverse(e.getSource());
		}
	}
	
	public abstract void visitNode(INodeExt node);
	
	public abstract void visitEdge(IEdgeExt edge);
}
