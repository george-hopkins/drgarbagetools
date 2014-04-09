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

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.draw2d.Graphics;

/**
 * A connection between two distinct vertices.
 *  
 * @author Sergej Alekseev
 * @version $Revision:125 $
 * $Id:Connection.java 125 2007-05-22 16:08:25Z aleks $
 */
public class Connection extends ModelElement implements IDirectEditLabelModel {

	/** 
	 * Used for indicating that a Connection with solid line style should be created.
	 * @see com.drgarbage.controlflowgraphfactory.plugin.editors.parts.VertexBaseEditPart#createEditPolicies() 
	 */
	public static final Integer SOLID_CONNECTION = new Integer(Graphics.LINE_SOLID);
	/**
	 * Used for indicating that a Connection with dashed line style should be created.
	 * @see com.drgarbage.controlflowgraphfactory.plugin.editors.parts.VertexBaseEditPart#createEditPolicies()
	 */
	public static final Integer DASHED_CONNECTION = new Integer(Graphics.LINE_DASH);
	
	/** Color of the connection */
	private Color connectionColor = null;
	
	/** Property ID to use when the line style of this connection is modified. */
	public static final String LINESTYLE_PROP = "LineStyle";
	
	/** Property ID to use when the text label of this edge is modified. */
	public static final String TEXT_LABEL_PROP = "Edge.TextLabel";
	
	/** Property ID to use then the color of this connection is modified. */
	public static final String COLOR_PROP = "Conection.Color";

	private static IPropertyDescriptor[] descriptors;
	private static final String SOLID_STR = "Solid";
	private static final String DASHED_STR = "Dashed";
	private static final long serialVersionUID = 1;
		
	/** True, if the connection is attached to its endpoints. */ 
	private boolean isConnected;
	
	/** Line drawing style for this connection. */
	private int lineStyle = Graphics.LINE_SOLID;
	
	/** Connection's source endpoint. */
	private VertexBase source;
	
	/** Connection's target endpoint. */
	private VertexBase target;
	
	/** Label of this connection */
	private String label = "";
	
	static {
//		descriptors[0] = new ComboBoxPropertyDescriptor(LINESTYLE_PROP, LINESTYLE_PROP, 
//				new String[] {SOLID_STR, DASHED_STR});
		
		descriptors = new IPropertyDescriptor[] {
				new TextPropertyDescriptor(TEXT_LABEL_PROP, "Label"), // id and description pair
				new ComboBoxPropertyDescriptor(LINESTYLE_PROP, LINESTYLE_PROP, 
						new String[] {SOLID_STR, DASHED_STR}),
		};
	}
	
	/** 
	 * Create a (solid) connection between two distinct vertices.
	 * @param source a source endpoint for this connection (non null)
	 * @param target a target endpoint for this connection (non null)
	 * @throws IllegalArgumentException if any of the parameters are null or source == target
	 * @see #setLineStyle(int) 
	 */
	public Connection(VertexBase source, VertexBase target) {
		reconnect(source, target);
	}
	
	/** 
	 * Disconnect this connection from the vertices it is attached to.
	 */
	public void disconnect() {
		if (isConnected) {
			source.removeConnection(this);
			target.removeConnection(this);
			isConnected = false;
		}
	}
	
	/**
	 * Returns the line drawing style of this connection.
	 * @return an int value (Graphics.LINE_DASH or Graphics.LINE_SOLID)
	 */
	public int getLineStyle() {
		return lineStyle;
	}
	
	/**
	 * Returns the descriptor for the lineStyle property
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}
	
	/**
	 * Returns the lineStyle as String for the Property Sheet
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		if (id.equals(LINESTYLE_PROP)) {
			if (getLineStyle() == Graphics.LINE_DASH)
				// Dashed is the second value in the combo dropdown
				return new Integer(1);
			// Solid is the first value in the combo dropdown
			return new Integer(0);
		}
		if (TEXT_LABEL_PROP.equals(id)) {
			return label;
		}
		
		return super.getPropertyValue(id);
	}
	
	/**
	 * Returns the source endpoint of this connection.
	 * @return a non-null VertexBase instance
	 */
	public VertexBase getSource() {
		return source;
	}
	
	/**
	 * Returns the target endpoint of this connection.
	 * @return a non-null VertexBase instance
	 */
	public VertexBase getTarget() {
		return target;
	}
	
	/** 
	 * Reconnect this connection. 
	 * The connection will reconnect with the vertices it was previously attached to.
	 */  
	public void reconnect() {
		if (!isConnected) {
			source.addConnection(this);
			target.addConnection(this);
			isConnected = true;
		}
	}
	
	/**
	 * Reconnect to a different source and/or target vertex.
	 * The connection will disconnect from its current attachments and reconnect to 
	 * the new source and target. 
	 * @param newSource a new source endpoint for this connection (non null)
	 * @param newTarget a new target endpoint for this connection (non null)
	 * @throws IllegalArgumentException if any of the paramers are null or newSource == newTarget
	 */
	public void reconnect(VertexBase newSource, VertexBase newTarget) {
		if (newSource == null || newTarget == null || newSource == newTarget) {
			throw new IllegalArgumentException();
		}
		disconnect();
		this.source = newSource;
		this.target = newTarget;
		reconnect();
	}
	
	/**
	 * Set the line drawing style of this connection.
	 * @param lineStyle one of following values: Graphics.LINE_DASH or Graphics.LINE_SOLID
	 * @see Graphics#LINE_DASH
	 * @see Graphics#LINE_SOLID
	 * @throws IllegalArgumentException if lineStyle does not have one of the above values
	 */
	public void setLineStyle(int lineStyle) {
		if (lineStyle != Graphics.LINE_DASH && lineStyle != Graphics.LINE_SOLID) {
			throw new IllegalArgumentException();
		}
		this.lineStyle = lineStyle;
		firePropertyChange(LINESTYLE_PROP, null, new Integer(this.lineStyle));
	}

	/**
	 * Sets the lineStyle based on the String provided by the PropertySheet
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		if (id.equals(LINESTYLE_PROP)){
			setLineStyle(new Integer(1).equals(value) 
					? Graphics.LINE_DASH : Graphics.LINE_SOLID);
		}
		else if (TEXT_LABEL_PROP.equals(id)) {
			setLabel(value.toString());
		}
		else
			super.setPropertyValue(id, value);
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.model.IDirectEditLabelModel#getLabel()
	 */
	public String getLabel() {
		return label;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.model.IDirectEditLabelModel#setLabel(java.lang.String)
	 */
	public void setLabel(String label) {
		this.label = label;
		firePropertyChange(TEXT_LABEL_PROP, null, label); //$NON-NLS-2$//$NON-NLS-1$
	}
	
	/**
	 * Sets the Color of this connection.
	 * Will not modify the color if newColor is null.
	 * @param newColor a non-null Color instance or null
	 */
	public void setColor(Color newColor) {
		if (newColor != null) {
			connectionColor = newColor;
			firePropertyChange(COLOR_PROP, null, connectionColor);
		}
	}
	
	/**
	 * Returns the color of this connection.
	 * @return a non-null Color instance
	 */
	public Color getColor() {
		return connectionColor;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.getLabel();
	}

}