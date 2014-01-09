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

import com.drgarbage.controlflowgraph.ControlFlowGraphException;

/**
 * Basic Block structure.
 *
 * @author Sergej Alekseev  
 * @version $Revision$
 * $Id$
 */
public interface IBasicBlock extends INodeExt {

	/**
	 * Gets the basicblock's vertices.
	 * @return list of vertices
	 */
	public INodeListExt getBasicBlockVertices();
	
	/**
	 * Add vertex to the basicblock.
	 * @param v
	 */	
	public void addVertex(INodeExt v);

	/**
	 * Gets the first vertex of the basic block.
	 * @return vertex
	 */
	public INodeExt getFirstBasicBlockVertex() throws ControlFlowGraphException;
	
	/**
	 * Gets the last vertex of the basic block.
	 * @return vertex
	 */	
	public INodeExt getLastBasicBlockVertex() throws ControlFlowGraphException;
	
}
