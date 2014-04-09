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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MidpointLocator;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.swt.graphics.Color;

import com.drgarbage.visualgraphic.commands.ConnectionDeleteCommand;
import com.drgarbage.visualgraphic.model.Connection;
import com.drgarbage.visualgraphic.model.IDirectEditLabelModel;
import com.drgarbage.visualgraphic.model.ModelElement;
import com.drgarbage.visualgraphic.policy.LabelDirectEditPolicy;


/**
 * Edit part for Connection model elements.
 * <p>This edit part must implement the PropertyChangeListener interface, 
 * so it can be notified of property changes in the corresponding model element.
 * </p>
 *
 * @author Sergej Alekseev
 * @version $Revision:125 $
 * $Id:EdgeEditPart.java 125 2007-05-22 16:08:25Z aleks $
 */
class EdgeEditPart extends AbstractConnectionEditPart implements PropertyChangeListener, IDirectEditPart {

	/**
	 * Editable label
	 */
	private Label label = null;
	
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
		// Selection handle edit policy. 
		// Makes the connection show a feedback, when selected by the user.
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
		// Allows the removal of the connection model element
		installEditPolicy(EditPolicy.CONNECTION_ROLE, new ConnectionEditPolicy() {
			protected Command getDeleteCommand(GroupRequest request) {
				return new ConnectionDeleteCommand(getCastedModel());
			}
		});
		
		//allow the editing of labels
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,new LabelDirectEditPolicy());	
	}
	
	protected void performDirectEdit(){
		new VertexBaseDirectEditManager(this, 
				new VertexBaseCellEditorLocator(getLabel())).show();
	}

	public void performRequest(Request request){
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
			performDirectEdit();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		PolylineConnection connection = (PolylineConnection) super.createFigure();
		connection.setTargetDecoration(new PolygonDecoration()); // arrow at target endpoint
		connection.setLineStyle(getCastedModel().getLineStyle());  // line drawing style
		
		String labelText = "empty";
		Object o = getModel();
		if(o instanceof IDirectEditLabelModel){
			labelText = ((IDirectEditLabelModel)o).getLabel();
		}
		label = new Label(labelText);
		label.setOpaque(true);
		
	  	 MidpointLocator ml = new MidpointLocator(connection,0);
	  	 connection.add(label,ml);
	  	
		return connection;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.editparts.IDirectEditPart#getLabel()
	 */
	public Label getLabel(){
		
		if(label != null){
			return label;
		}
		
		IFigure f = getFigure();
		
		//create an empty label
		Label l = new Label("Label is missing in the figure!");
		l.setSize(f.getSize());
		l.setLocation(f.getClientArea().getLocation());
		return l;
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
	
	private Connection getCastedModel() {
		return (Connection) getModel();
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getPropertyName();
		if (Connection.LINESTYLE_PROP.equals(property)) {
			((PolylineConnection) getFigure()).setLineStyle(getCastedModel().getLineStyle());
		}
		else if (Connection.COLOR_PROP.equals(property)) {
			refreshColors();
		}
		else if(Connection.TEXT_LABEL_PROP.equals(property)){
			String s = (String) event.getNewValue();
			getLabel().setText(s);
		}
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.editparts.IDirectEditPart#getDirectEditModel()
	 */
	public IDirectEditLabelModel getDirectEditModel() {
		return (IDirectEditLabelModel) getModel();
	}
	
	/**
	 * Notifies parent container of changed color.
	 */
	protected void refreshColors() {
		Color c = getCastedModel().getColor();
		getFigure().setForegroundColor(c);
	}

}