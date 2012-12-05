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
import java.util.ArrayList;

import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.Request;

import com.drgarbage.controlflowgraph.anchors.DecisionAnchor;
import com.drgarbage.controlflowgraph.anchors.SimpleAnchor;
import com.drgarbage.visualgraphic.model.DecisionVertex;

/**
 * EditPart used for DecisionVertex instances.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: DecisionVertexEditPart.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class DecisionVertexEditPart extends VertexBaseEditPart {

	/**
	 *   Anchorns for connections
	 *                               
	 *        	fix_anchor_in       
	 *                 |                
	 *                 V                
	 *                 +                
	 *                /|\               
	 *               / | \              
	 *              /  |  \             
	 *             /   |   \            
	 *         +--+----+----+ --+       
	 *         |   \   |   /    |       
	 *         V    \  |  /     V       
	 *  flow_anchor  \ | /  flow_anchor
	 *   (typ = 1)    \|/    (typ = 2)  
	 *                 + 
	 *           flow_anchor (typ 3)
	 */
	private DecisionAnchor flow_anchor;
	private SimpleAnchor fix_anchor_in;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return createTargetConnectionAnchor(connection);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return createTargetConnectionAnchor(null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return createSourceConnectionAnchor(connection);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return createSourceConnectionAnchor(null);
	}


	/**
	 * Create a source anchor for the vertex
	 */
	private ConnectionAnchor createSourceConnectionAnchor(ConnectionEditPart connection) {

		if(flow_anchor == null){
			flow_anchor = new DecisionAnchor(getFigure());
			return flow_anchor;
		}

		return flow_anchor;
	}

	/**
	 * Create a source anchor for the vertex
	 */
	private ConnectionAnchor createTargetConnectionAnchor(ConnectionEditPart connection) {
		Object o = getModel();
		if(fix_anchor_in == null){
			if (o instanceof DecisionVertex){
				IFigure f = getFigure();
				fix_anchor_in = new SimpleAnchor(f, 0);
			}
			else {
				// if a vertex gets extended the conditions above must be updated
				throw new IllegalArgumentException("unexpected model");
			}
		}

		if(connection != null){
			/* set relative bend point for incoming connection */
			if(connection.getFigure() instanceof PolylineConnection){

				final DecisionVertex dv = (DecisionVertex) o;

				final PolylineConnection polylineConnection = (PolylineConnection) connection.getFigure();

				final RelativeBendpoint bp = new RelativeBendpoint();
				bp.setConnection(polylineConnection);
				bp.setWeight(1.0f);				
				ArrayList<Bendpoint> list = new ArrayList<Bendpoint>();	
				list.add(bp);
				polylineConnection.setRoutingConstraint(list);

				final int delataY = 20;
				polylineConnection.addPropertyChangeListener(new PropertyChangeListener(){

					public void propertyChange(PropertyChangeEvent evt) {

						int startX = polylineConnection.getStart().x;
						int endX = polylineConnection.getEnd().x;

						if(dv.getTargetConnections().size() == 1){
							bp.setRelativeDimensions(new Dimension( 0 , -delataY - dv.getSize().height/2), 
									new Dimension( 0, -delataY - dv.getSize().height/2));
						}

						else{
							int startY = polylineConnection.getStart().y;
							int endY = polylineConnection.getEnd().y;
							int a = 0;
							if(startY > endY){
								a = startY - endY ;	
							}
							else{
								a = endY - startY;
							}

							int p = a / delataY;

							if(p == 0){
								p = 1;
							}

							int deltaX = 0;

							if(startX < endX){
								int b = endX - startX;
								deltaX = b / p;	
								bp.setRelativeDimensions(new Dimension( -deltaX , -delataY - dv.getSize().height/2), 
										new Dimension(  -deltaX, -delataY - dv.getSize().height/2));
							}
							else{
								int b = startX - endX;
								deltaX = b / p;
								bp.setRelativeDimensions(new Dimension( deltaX , -delataY - dv.getSize().height/2), 
										new Dimension( deltaX, -delataY - dv.getSize().height/2));
							}
						}
					}
				});

				bp.setRelativeDimensions(new Dimension(0 , -delataY - dv.getSize().height/2), 
						new Dimension(0, -delataY - dv.getSize().height/2));

			}
		}

		return fix_anchor_in;
	}

}
