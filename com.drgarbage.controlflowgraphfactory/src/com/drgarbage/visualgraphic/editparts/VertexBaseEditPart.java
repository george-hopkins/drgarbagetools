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

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.EllipseAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryPlugin;
import com.drgarbage.controlflowgraphfactory.preferences.ControlFlowFactoryPreferenceConstants;
import com.drgarbage.visualgraphic.model.BasicBlockVertex;
import com.drgarbage.visualgraphic.model.CommentElement;
import com.drgarbage.visualgraphic.model.DecisionVertex;
import com.drgarbage.visualgraphic.model.ExitVertex;
import com.drgarbage.visualgraphic.model.GetVertex;
import com.drgarbage.visualgraphic.model.GotoJumpVertex;
import com.drgarbage.visualgraphic.model.IDirectEditLabelModel;
import com.drgarbage.visualgraphic.model.InvokeVertex;
import com.drgarbage.visualgraphic.model.ModelElement;
import com.drgarbage.visualgraphic.model.RectangularVertex;
import com.drgarbage.visualgraphic.model.ReturnVertex;
import com.drgarbage.visualgraphic.model.StartVertex;
import com.drgarbage.visualgraphic.model.SwitchVertex;
import com.drgarbage.visualgraphic.model.VertexBase;
import com.drgarbage.visualgraphic.policy.LabelDirectEditPolicy;
import com.drgarbage.visualgraphic.policy.VertexBaseComponentEditPolicy;
import com.drgarbage.visualgraphic.policy.VertexBaseGraphicalNodeEditPolicy;



/**
 * EditPart used for VertexBase instances.
 * <p>This edit part must implement the PropertyChangeListener interface, 
 * so it can be notified of property changes in the corresponding model element.
 * </p>
 * 
 * @version $Revision:125 $
 * $Id:VertexBaseEditPart.java 125 2007-05-22 16:08:25Z aleks $
 */
public class VertexBaseEditPart extends AbstractGraphicalEditPart 
	implements PropertyChangeListener, NodeEditPart, IDirectEditPart {
	
	private ConnectionAnchor anchor;
	
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
		// allow removal of the associated model element
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new VertexBaseComponentEditPolicy());

		// allow the creation of connections and 
		// and the reconnection of connections between VertexBase instances
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new VertexBaseGraphicalNodeEditPolicy());

		//allow the editing of labels
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,new LabelDirectEditPolicy());	
	}

	protected void performDirectEdit(){
		new VertexBaseDirectEditManager(this, 
				new VertexBaseCellEditorLocator (getLabel())).show();
	}

	public void performRequest(Request request){
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
			performDirectEdit();
	}
	
	/**
	 * Return a IFigure depending on the instance of the current model element.
	 * This allows this EditPart to be used for all sublasses of VertexBase. 
	 */
	protected IFigure createFigure() {
		
		IPreferenceStore store = ControlFlowFactoryPlugin.getDefault()
		.getPreferenceStore();
		
		boolean useGradientFillColor = store
		.getBoolean(ControlFlowFactoryPreferenceConstants.USE_GRADIENT_FILL_COLOR);

		Object o = getModel();	
		
		if (o instanceof DecisionVertex) {
			return 	VertexBaseFigureFactory.createDecisionVertex((DecisionVertex) o, useGradientFillColor);		
		} 
		else if(o instanceof GetVertex){
			return VertexBaseFigureFactory.createGetVertex((GetVertex) o, useGradientFillColor);
		}
		else if(o instanceof GotoJumpVertex){
			return VertexBaseFigureFactory.createGotoJumpVertex((GotoJumpVertex) o, useGradientFillColor);
		}
		else if(o instanceof InvokeVertex){
			return VertexBaseFigureFactory.createInvokeVertex((InvokeVertex) o, useGradientFillColor);
		}
		else if(o instanceof BasicBlockVertex){
			return VertexBaseFigureFactory.createBasicBlockVertex((BasicBlockVertex) o, useGradientFillColor);
		}
		else if(o instanceof RectangularVertex){
			return VertexBaseFigureFactory.createRectangularVertex((RectangularVertex) o, useGradientFillColor);
		}
		else if(o instanceof ReturnVertex){
			return VertexBaseFigureFactory.createReturnVertex((ReturnVertex)o, useGradientFillColor);
		}
		else if(o instanceof StartVertex){
			return VertexBaseFigureFactory.createStartVertex((StartVertex) o, useGradientFillColor);
		}
		else if(o instanceof ExitVertex){
			return VertexBaseFigureFactory.createExitVertex((ExitVertex) o, useGradientFillColor);
		}
		else if(o instanceof SwitchVertex){
			return VertexBaseFigureFactory.createSwitchVertex((SwitchVertex) o, useGradientFillColor);
		}
		else if(o instanceof CommentElement){
			return VertexBaseFigureFactory.createComment((CommentElement) o, useGradientFillColor);
		}
		else {
			// if figure gets extended the conditions above must be updated
			throw new IllegalArgumentException();
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
	
	/**
	 * return VertexBase instance.
	 */
	private VertexBase getCastedModel() {
		return (VertexBase) getModel();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
	 */
	protected List getModelTargetConnections() {
		return getCastedModel().getTargetConnections();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return createConnectionAnchor();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return createConnectionAnchor();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
	 */
	protected List getModelSourceConnections() {
		return getCastedModel().getSourceConnections();
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return createConnectionAnchor();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return createConnectionAnchor();
	}
	
	
	/**
	 * Create a source anchor for the vertex
	 */
	private ConnectionAnchor createConnectionAnchor() {
		Object o= getModel();
		if (anchor == null) {
			if (o instanceof DecisionVertex){
				anchor = new EllipseAnchor(getFigure());
			}
			else if (o instanceof VertexBase){
				anchor = new ChopboxAnchor(getFigure());
			}
			else{
				/* if a vertex gets extended the conditions above must be updated */
				throw new IllegalArgumentException("unexpected model");
			}
		}
		return anchor;
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (VertexBase.SIZE_PROP.equals(prop) || VertexBase.LOCATION_PROP.equals(prop)) {
			refreshVisuals();
		} else if (VertexBase.COLOR_PROP.equals(prop)) {
			refreshColors();
		}
		else if (VertexBase.SOURCE_CONNECTIONS_PROP.equals(prop)) {
			refreshSourceConnections();
		} else if (VertexBase.TARGET_CONNECTIONS_PROP.equals(prop)) {
			refreshTargetConnections();
		}else if(VertexBase.TEXT_LABEL_PROP.equals(prop)){
			String s = (String) evt.getNewValue();
			getLabel().setText(s);
		}
		
	
	}
	
	protected void refreshVisuals() {
		// notify parent container of changed position & location
		// if this line is removed, the XYLayoutManager used by the parent container 
		// (the Figure of the DiagramEditPart), will not know the bounds of this figure
		// and will not draw it correctly.
		Rectangle bounds = new Rectangle(getCastedModel().getLocation(),
				getCastedModel().getSize());
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), bounds);
	}
	
	/**
	 * Notifies parent container of changed color.
	 */
	protected void refreshColors() {
		Color c = getCastedModel().getColor();
		getFigure().setBackgroundColor(c);
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.editparts.IDirectEditPart#getLabel()
	 */
	public Label getLabel(){
		IFigure f = getFigure();
		if(f.getChildren().size() >= 1){
			Object o = f.getChildren().get(0);	
			if(o instanceof Label){
				return (Label)o;
			}
		}
		
		//create an empty label
		Label l = new Label("Label is missing in the figure!");
		l.setSize(f.getSize());
		l.setLocation(f.getClientArea().getLocation());
		return l;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.editparts.IDirectEditPart#getDirectEditModel()
	 */
	public IDirectEditLabelModel getDirectEditModel() {
		return (IDirectEditLabelModel) getModel();
	}
}