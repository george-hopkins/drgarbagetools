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

import java.util.Map;

/**
 * DirectedGraph extention structure.
 *
 * @author Sergej Alekseev  
 * @version $Revision$
 * $Id$
 */
public interface IDirectedGraphExt {

	/**
	 * Gets the list of edges.
	 * @return the edge list object
	 */
	public IEdgeListExt getEdgeList();

	/**
	 * Gets the list of nodes.
	 * @return the node list object
	 */
	public INodeListExt getNodeList();
	
	/**
	 * Returns user specific object previously
	 * set by setUserObject(). 
	 * @return object
	 */
	public Map<String, Object> getUserObject();
}
