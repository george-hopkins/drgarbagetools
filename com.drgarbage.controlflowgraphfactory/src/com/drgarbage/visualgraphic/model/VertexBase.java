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

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Abstract prototype of a vertex.
 * Has a size (width and height), a location (x and y position) and a list of incoming
 * and outgoing connections. Use subclasses to instantiate a specific vertex.
 * 
 * @author Sergej Alekseev
 * @version $Revision:125 $
 * $Id:VertexBase.java 125 2007-05-22 16:08:25Z aleks $
 */
public abstract class VertexBase extends ModelElement implements IDirectEditLabelModel {
	
	private static final long serialVersionUID = 1;
	
	/** 
	 * A static array of property descriptors.
	 * There is one IPropertyDescriptor entry per editable property.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	private static IPropertyDescriptor[] descriptors;

	/** ID for the Height property value (used for by the corresponding property descriptor). */
	private static final String HEIGHT_PROP = "VertexBase.Height";

	/** Property ID to use when the location of this vertex is modified. */
	public static final String LOCATION_PROP = "VertexBase.Location";

	/** Property ID to use then the size of this vertex is modified. */
	public static final String SIZE_PROP = "VertexBase.Size";

	/** Property ID to use when the list of outgoing connections is modified. */
	public static final String SOURCE_CONNECTIONS_PROP = "VertexBase.SourceConn";

	/** Property ID to use when the list of incoming connections is modified. */
	public static final String TARGET_CONNECTIONS_PROP = "VertexBase.TargetConn";

	/** ID for the Width property value (used for by the corresponding property descriptor). */
	private static final String WIDTH_PROP = "VertexBase.Width";
	
	/** ID for the X property value (used for by the corresponding property descriptor).  */
	private static final String XPOS_PROP = "VertexBase.xPos";

	/** ID for the Y property value (used for by the corresponding property descriptor).  */
	private static final String YPOS_PROP = "VertexBase.yPos";

	/** Property ID to use when the text label of this vertex is modified. */
	public static final String TEXT_LABEL_PROP = "VertexBase.TextLabel";

	/*
	 * Initializes the property descriptors array.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	static {
		descriptors = new IPropertyDescriptor[] { 
				new TextPropertyDescriptor(XPOS_PROP, "X"), // id and description pair
				new TextPropertyDescriptor(YPOS_PROP, "Y"),
				new TextPropertyDescriptor(WIDTH_PROP, "Width"),
				new TextPropertyDescriptor(HEIGHT_PROP, "Height"),
		};
		// use a custom cell editor validator for all four array entries
		for (int i = 0; i < descriptors.length; i++) {
			((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
				public String isValid(Object value) {
					int intValue = -1;
					try {
						intValue = Integer.parseInt((String) value);
					} catch (NumberFormatException exc) {
						return "Not a number";
					}
					return (intValue >= 0) ? null : "Value must be >=  0";
				}
			});
		}
	} // static
	
	/** Location of this vertex. */
	private Point location = new Point(0, 0);

	/** Size of this vertex. */
	private Dimension size = new Dimension(50, 50);

	/** List of outgoing Connections. */
	private List<Connection> sourceConnections = new ArrayList<Connection>();

	/** List of incoming Connections. */
	private List<Connection> targetConnections = new ArrayList<Connection>();
	
	/** Label of this VertexBase*/
	private String label = "null";
	
	/** Tooltip of this VertexBase*/
	private String toolTip;
	
	/** Set if the long description should be shown */
	private boolean longDescr = false;
	
	/**
	 * Returns the use long description used flag.
	 * @return longDescr - true or false
	 */
	public boolean isLongDescrUsed() {
		return longDescr;
	}

	/**
	 * Sets the use long description flag
	 * @param longDescr - true or false
	 */
	public void setLongDescrUsed(boolean longDescr) {
		this.longDescr = longDescr;
	}

	/**
	 * Add an incoming or outgoing connection to this vertex.
	 * @param conn a non-null connection instance
	 * @throws IllegalArgumentException if the connection is null or has not distinct endpoints
	 */
	void addConnection(Connection conn) {
		if (conn == null || conn.getSource() == conn.getTarget()) {
			throw new IllegalArgumentException();
		}
		if (conn.getSource() == this) {
			sourceConnections.add(conn);
			firePropertyChange(SOURCE_CONNECTIONS_PROP, null, conn);
		} else if (conn.getTarget() == this) {
			targetConnections.add(conn);
			firePropertyChange(TARGET_CONNECTIONS_PROP, null, conn);
		}
	}
	
	/**
	 * Return a pictogram (small icon) describing this model element.
	 * Children should override this method and return an appropriate Image.
	 * @return a 16x16 Image or null
	 */
	public abstract Image getIcon();
	
	/**
	 * Return the Location of this vertex.
	 * @return a non-null location instance
	 */
	public Point getLocation() {
		return location.getCopy();
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
	 * Returns the tooltip of this vertex.
	 * @return the toolTip
	 */
	public String getToolTip() {
		return toolTip;
	}

	/**
	 * Sets the tooltip of this vertex.
	 * @param toolTip, the toolTip to set
	 */
	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}
	
	/**
	 * Returns an array of IPropertyDescriptors for this vertex.
	 * <p>The returned array is used to fill the property view, when the edit-part corresponding
	 * to this model element is selected.</p>
	 * @see #descriptors
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}
	
	/**
	 * Return the property value for the given propertyId, or null.
	 * <p>The property view uses the IDs from the IPropertyDescriptors array 
	 * to obtain the value of the corresponding properties.</p>
	 * @see #descriptors
	 * @see #getPropertyDescriptors()
	 */
	public Object getPropertyValue(Object propertyId) {
		if (XPOS_PROP.equals(propertyId)) {
			return Integer.toString(location.x);
		}
		if (YPOS_PROP.equals(propertyId)) {
			return Integer.toString(location.y);
		}
		if (HEIGHT_PROP.equals(propertyId)) {
			return Integer.toString(size.height);
		}
		if (WIDTH_PROP.equals(propertyId)) {
			return Integer.toString(size.width);
		}
		return super.getPropertyValue(propertyId);
	}
	
	/**
	 * Return the Size of this vertex.
	 * @return a non-null Dimension instance
	 */
	public Dimension getSize() {
		return size.getCopy();
	}
	
	/**
	 * Return a List of outgoing Connections.
	 */
	public List<Connection> getSourceConnections() {
		return new ArrayList<Connection>(sourceConnections);
	}
	
	/**
	 * Return a List of incoming Connections.
	 */
	public List<Connection> getTargetConnections() {
		return new ArrayList<Connection>(targetConnections);
	}
	
	/**
	 * Remove an incoming or outgoing connection from this vertex.
	 * @param conn a non-null connection instance
	 * @throws IllegalArgumentException if the parameter is null
	 */
	void removeConnection(Connection conn) {
		if (conn == null) {
			throw new IllegalArgumentException();
		}
		if (conn.getSource() == this) {
			sourceConnections.remove(conn);
			firePropertyChange(SOURCE_CONNECTIONS_PROP, null, conn);
		} else if (conn.getTarget() == this) {
			targetConnections.remove(conn);
			firePropertyChange(TARGET_CONNECTIONS_PROP, null, conn);
		}
	}
	public void setLocation(Point newLocation) {
		setLocation(null, newLocation);
	}
	
	/**
	 * Set the Location of this vertex.
	 * @param newLocation a non-null Point instance
	 * @throws IllegalArgumentException if the parameter is null
	 */
	
	public void setLocation(Point oldLocation, Point newLocation) {
		if (newLocation == null) {
			throw new IllegalArgumentException();
		}
		location.setLocation(newLocation);
		firePropertyChange(LOCATION_PROP, oldLocation, location);
	}
	
	
	/**
	 * Set the property value for the given property id.
	 * If no matching id is found, the call is forwarded to the superclass.
	 * <p>The property view uses the IDs from the IPropertyDescriptors array to set the values
	 * of the corresponding properties.</p>
	 * @see #descriptors
	 * @see #getPropertyDescriptors()
	 */
	public void setPropertyValue(Object propertyId, Object value) {
		if (XPOS_PROP.equals(propertyId)) {
			int x = Integer.parseInt((String) value);
			setLocation(new Point(x, location.y));
		} else if (YPOS_PROP.equals(propertyId)) {
			int y = Integer.parseInt((String) value);
			setLocation(new Point(location.x, y));
		} else if (HEIGHT_PROP.equals(propertyId)) {
			int height = Integer.parseInt((String) value);
			setSize(new Dimension(size.width, height));
		} else if (WIDTH_PROP.equals(propertyId)) {
			int width = Integer.parseInt((String) value);
			setSize(new Dimension(width, size.height));
		} else {
			super.setPropertyValue(propertyId, value);
		}
	}
	
	/**
	 * Set the Size of this vertex.
	 * Will not modify the size if newSize is null.
	 * @param newSize a non-null Dimension instance or null
	 */
	public void setSize(Dimension newSize) {
		if (newSize != null) {
			size.setSize(newSize);
			firePropertyChange(SIZE_PROP, null, size);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.getLabel();
	}

}