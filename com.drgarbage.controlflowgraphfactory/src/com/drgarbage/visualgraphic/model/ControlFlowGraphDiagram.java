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

package com.drgarbage.visualgraphic.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A container for control flow graphs This is the "root" of the model data
 * structure.
 * 
 * @author Sergej Alekseev
 * @version $Revision:125 $ 
 * $Id:ControlFlowGraphDiagram.java 125 2007-05-22 16:08:25Z aleks $
 */
public class ControlFlowGraphDiagram extends ModelElement {

	private static final long serialVersionUID = 1;
	
	/** ID for the name property value (used for by the corresponding property descriptor).  */
	public static final String NAME_PROP = "name";

	/** Property ID to use when a child is added to this diagram. */
	public static final String CHILD_ADDED_PROP = "ControlFlowGraphDiagram.ChildAdded";

	/** Property ID to use when a child is removed from this diagram. */
	public static final String CHILD_REMOVED_PROP = "ControlFlowGraphDiagram.ChildRemoved";
	
	/** List of vertices */
	private List<VertexBase> vertices = new ArrayList<VertexBase>();
	
	/**
	 * Diagram name.
	 */
	private String name = null;
	
	/**
	 * Counter to generate unique id of this diagram.
	 */
	private int uniqueIdCounter = 1;

	/**
	 * Constructor.
	 */
	public ControlFlowGraphDiagram() {
		super();
		this.setId(0);
	}

	/**
	 * Add a vertex to this diagram.
	 * @param s, a non-null vertex instance
	 * @return true, if the vertex has been added, false otherwise
	 */
	public boolean addChild(VertexBase s) {
		s.setId(getUniqueIdCounter());
		boolean add = vertices.add(s);
		if (s != null && add) {
			firePropertyChange(CHILD_ADDED_PROP, null, s);
			return true;
		}
		return false;
	}
	
	/**
	 * Returns unique id of this diagram for its children. 
	 * @return id
	 */
	private int getUniqueIdCounter() {
		uniqueIdCounter++;
		return uniqueIdCounter;
	}

	/**
	 * Return a List of vertices in this diagram. The returned List should not
	 * be modified.
	 */
	public List<VertexBase> getChildren() {
		return vertices;
	}

	/**
	 * Remove a vertex from this diagram.
	 * @param s, a non-null vertex instance;
	 * @return true, if the vertex was removed, false otherwise
	 */
	public boolean removeChild(VertexBase s) {
		if (s != null && vertices.remove(s)) {
			firePropertyChange(CHILD_REMOVED_PROP, null, s);
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.model.ModelElement#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object propertyId, Object value) {
		if (NAME_PROP.equals(propertyId)) {
			name = value.toString();
		}
		
		super.setPropertyValue(propertyId, value);
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.model.ModelElement#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object propertyId) {
		if (NAME_PROP.equals(propertyId)) {
			return name;
		}
		
		return super.getPropertyValue(propertyId);
	}
}