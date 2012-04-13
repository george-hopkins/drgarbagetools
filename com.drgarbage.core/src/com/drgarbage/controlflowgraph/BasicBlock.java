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

/**
 * Basic Block structure.
 *
 * @author Sergej Alekseev 
 * @version $Revision: 1523 $
 * $Id: BasicBlock.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
package com.drgarbage.controlflowgraph;

import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.IBasicBlock;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;

public class BasicBlock extends NodeExt implements IBasicBlock{

	public BasicBlock() {
		super();
		incoming = new EdgeListExt();
		outgoing = new EdgeListExt();
	}

	private INodeListExt vertices = GraphExtentionFactory.createNodeListExtention();

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.intf.IBasicBlock#addVertex(INodeExt)
	 */
	public void addVertex(INodeExt v) {
		vertices.add(v);
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.intf.IBasicBlock#getBasicBlockVertices()
	 */
	public INodeListExt getBasicBlockVertices() {
		return vertices;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.intf.IBasicBlock#getFirstBasicBlockVertex()
	 */
	public INodeExt getFirstBasicBlockVertex() throws ControlFlowGraphException {
		if(vertices.size() == 0)
			throw new ControlFlowGraphException("getFirstBasicBlockVertex: Basic Block is empty!");
		
		return vertices.getNodeExt(0);
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.intf.IBasicBlock#getLastBasicBlockVertex()
	 */
	public INodeExt getLastBasicBlockVertex() throws ControlFlowGraphException{
		if(vertices.size() == 0)
			throw new ControlFlowGraphException("getFirstBasicBlockVertex: Basic Block is empty!");
	
		return vertices.getNodeExt(vertices.size() - 1);
	}

}
