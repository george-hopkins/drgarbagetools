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

package com.drgarbage.visualgraphic.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;
import com.drgarbage.visualgraphic.model.ModelElement;



/**
 * TreeEditPart for a ControlFlowGraphDiagram instance.
 * This is used in the Outline View of the ControlFlowGraphEditor.
 * <p>This edit part must implement the PropertyChangeListener interface, 
 * so it can be notified of property changes in the corresponding model element.
 * </p>
 * 
 * @author Sergej Alekseev
 * @version $Revision:125 $
 * $Id:DiagramTreeEditPart.java 125 2007-05-22 16:08:25Z aleks $
 */
class DiagramTreeEditPart extends AbstractTreeEditPart
	implements PropertyChangeListener {

/** 
 * Create a new instance of this edit part using the given model element.
 * @param model a non-null ControlFlowGraphDiagram instance
 */
DiagramTreeEditPart(ControlFlowGraphDiagram model) {
	super(model);
}

/**
 * Upon activation, attach to the model element as a property change listener.
 */
public void activate() {
	if (!isActive()) {
		super.activate();
		((ModelElement) getModel()).addPropertyChangeListener(this);
	}
}

/* (non-Javadoc)
 * @see com.drgarbage.controlflowgraphfactory.plugin.editors.parts.VertexBaseTreeEditPart#createEditPolicies()
 */
protected void createEditPolicies() {
	// If this editpart is the root content of the viewer, then disallow removal
	if (getParent() instanceof RootEditPart) {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
	}
}

/**
 * Upon deactivation, detach from the model element as a property change listener.
 */
public void deactivate() {
	if (isActive()) {
		super.deactivate();
		((ModelElement) getModel()).removePropertyChangeListener(this);
	}
}

private ControlFlowGraphDiagram getCastedModel() {
	return (ControlFlowGraphDiagram) getModel();
}

/**
 * Convenience method that returns the EditPart corresponding to a given child.
 * @param child a model element instance
 * @return the corresponding EditPart or null
 */
private EditPart getEditPartForChild(Object child) {
	return (EditPart) getViewer().getEditPartRegistry().get(child);
}

/* (non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
 */
protected List getModelChildren() {
	return getCastedModel().getChildren(); // a list of vertices
}

/* (non-Javadoc)
* @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
*/
public void propertyChange(PropertyChangeEvent evt) {
	String prop = evt.getPropertyName();
	if (ControlFlowGraphDiagram.CHILD_ADDED_PROP.equals(prop)) {
		// add a child to this edit part
		// causes an additional entry to appear in the tree of the outline view
		addChild(createChild(evt.getNewValue()), -1);
	} else if (ControlFlowGraphDiagram.CHILD_REMOVED_PROP.equals(prop)) {
		// remove a child from this edit part
		// causes the corresponding edit part to disappear from the tree in the outline view
		removeChild(getEditPartForChild(evt.getNewValue()));
	} else {
		refreshVisuals();
	}
}
}
