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

import org.eclipse.draw2d.graph.EdgeList;

import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;

/**
 * Edge List extention structure.
 *
 * @author Sergej Alekseev 
 * @version $Revision$
 * $Id: EdgeListExt.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class EdgeListExt extends EdgeList implements IEdgeListExt{

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.controlflowgraph.algorithms.intf.IEdgeListExt#getEdgeExt(int)
	 */
	public IEdgeExt getEdgeExt(int index) {
		return (IEdgeExt)super.get(index);
	}
}
