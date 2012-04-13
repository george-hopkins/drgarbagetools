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

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import com.drgarbage.visualgraphic.commands.VertexBaseCreateCommand;
import com.drgarbage.visualgraphic.commands.VertexBaseSetConstraintCommand;
import com.drgarbage.visualgraphic.model.CommentElement;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;
import com.drgarbage.visualgraphic.model.DecisionVertex;
import com.drgarbage.visualgraphic.model.ExitVertex;
import com.drgarbage.visualgraphic.model.GetVertex;
import com.drgarbage.visualgraphic.model.GotoJumpVertex;
import com.drgarbage.visualgraphic.model.InvokeVertex;
import com.drgarbage.visualgraphic.model.ModelElement;
import com.drgarbage.visualgraphic.model.RectangularVertex;
import com.drgarbage.visualgraphic.model.ReturnVertex;
import com.drgarbage.visualgraphic.model.StartVertex;
import com.drgarbage.visualgraphic.model.SwitchVertex;
import com.drgarbage.visualgraphic.model.VertexBase;


/**
 * EditPart for the a ControlFlowGraphDiagram instance.
 * <p>This edit part server as the main diagram container, the white area where
 * everything else is in. Also responsible for the container's layout (the
 * way the container rearanges is contents) and the container's capabilities
 * (edit policies).
 * </p>
 * <p>This edit part must implement the PropertyChangeListener interface, 
 * so it can be notified of property changes in the corresponding model element.
 * </p>
 * 
 * @author Sergej Alekseev
 * @version $Revision:125 $
 * $Id:DiagramEditPart.java 125 2007-05-22 16:08:25Z aleks $
 */
class DiagramEditPart extends AbstractGraphicalEditPart 
	implements PropertyChangeListener  {

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
 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
 */
protected void createEditPolicies() {
	// disallows the removal of this edit part from its parent
	installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
	// handles constraint changes (e.g. moving and/or resizing) of model elements
	// and creation of new model elements
	installEditPolicy(EditPolicy.LAYOUT_ROLE,  new DiagramXYLayoutEditPolicy());
}

/* (non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
 */
protected IFigure createFigure() {
	Figure f = new FreeformLayer();
	f.setBorder(new MarginBorder(3));
	f.setLayoutManager(new FreeformLayout());

	// Create the static router for the connection layer
	ConnectionLayer connLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
	connLayer.setConnectionRouter(new ShortestPathConnectionRouter(f));
	
	return f;
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

/* (non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
 */
protected List getModelChildren() {
	return getCastedModel().getChildren(); // return a list of vertices
}

/* (non-Javadoc)
 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
 */
public void propertyChange(PropertyChangeEvent evt) {
	String prop = evt.getPropertyName();
	// these properties are fired when vertices are added into or removed from 
	// the controll flow diagram instance and must cause a call of refreshChildren()
	// to update the diagram's contents.
	if (ControlFlowGraphDiagram.CHILD_ADDED_PROP.equals(prop)
			|| ControlFlowGraphDiagram.CHILD_REMOVED_PROP.equals(prop)) {
		refreshChildren();
	}
}

/**
 * EditPolicy for the Figure used by this edit part.
 * Children of XYLayoutEditPolicy can be used in Figures with XYLayout.
 *  @version $Revision:125 $
 *  $Id:DiagramEditPart.java 125 2007-05-22 16:08:25Z aleks $
 */
private static class DiagramXYLayoutEditPolicy extends XYLayoutEditPolicy {
	
	/* (non-Javadoc)
	 * @see ConstrainedLayoutEditPolicy#createChangeConstraintCommand(ChangeBoundsRequest, EditPart, Object)
	 */
	protected Command createChangeConstraintCommand(ChangeBoundsRequest request,
			EditPart child, Object constraint) {
		if (child instanceof VertexBaseEditPart	&& constraint instanceof Rectangle) {
			// return a command that can move and/or resize a VertexBase
			return new VertexBaseSetConstraintCommand(
					(VertexBase) child.getModel(), request, (Rectangle) constraint);
		}
		return super.createChangeConstraintCommand(request, child, constraint);
	}
	
	/* (non-Javadoc)
	 * @see ConstrainedLayoutEditPolicy#createChangeConstraintCommand(EditPart, Object)
	 */
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		// not used in this example
		return null;
	}
	
	/* (non-Javadoc)
	 * @see LayoutEditPolicy#getCreateCommand(CreateRequest)
	 */
	protected Command getCreateCommand(CreateRequest request) {
		Object childClass = request.getNewObjectType();
		if (childClass == DecisionVertex.class || 
			childClass == GetVertex.class ||
			childClass == GotoJumpVertex.class ||
			childClass == InvokeVertex.class ||
			childClass == RectangularVertex.class ||
			childClass == ReturnVertex.class ||
			childClass == StartVertex.class ||
			childClass == ExitVertex.class ||
			childClass == SwitchVertex.class ||
			childClass == CommentElement.class) {
			// return a command that can add a VertexBase to a ControlFlowGraphDiagram 
			return new VertexBaseCreateCommand((VertexBase)request.getNewObject(), 
					(ControlFlowGraphDiagram)getHost().getModel(), (Rectangle)getConstraintFor(request));
		}
			
		return null;
	}
	
}

}