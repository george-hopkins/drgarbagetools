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
 * Edge extension structure.
 *
 * @author Sergej Alekseev  
 * @version $Revision$
 * $Id$
 */
public interface IEdgeExt {
	
	/**
	 * Gets the target Node extension object.
	 * @return the NodeExt object
	 */
	public INodeExt getTarget();
	
	/**
	 * Gets the source Node extension object.
	 * @return the NodeExt object
	 */
	public INodeExt getSource();
	
	/**
	 * Gets the arbitrary data object.
	 * @return data object
	 */
	public Object getData();

	/**
	 * Sets the arbitrary data object.
	 * @param data object
	 */
	public void setData(Object data);
	
	/** 
	 * Gets visited status.
	 * NOTE: For internal use only.
	 * @return visited flag
	 */
	public boolean isVisited();

	/** 
	 * Sets visited flag.
	 * NOTE: For internal use only
	 * @param b visited flag to set
	 */
	public void setVisited(boolean b);

	/** 
	 * Gets mark flag.
	 * NOTE: For internal use only
	 * @return marker
	 */
	public MarkEnum getMark();

	/** 
	 * Sets mark flag.
	 * NOTE: For internal use only
	 * @param mark
	 */
	public void setMark(MarkEnum mark);
	
	/**
	 * Assigns a counter to the edge.
	 * For graph algorithms.
	 * NOTE: For internal use only
	 * @param counter
	 */
	public void setCounter(int counter);
	
	/**
	 * Returns the counter of the edge.
	 * For graph algorithms.
	 * NOTE: For internal use only
	 * @return the counter value
	 */
	public int getCounter();
	
	/**
	 * Increments the edge counter by 1.
	 * For graph algorithms.
	 * NOTE: For internal use only
	 */
	public void incrementCounter();

}
