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

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.gef.EditPolicy;

import com.drgarbage.controlflowgraph.figures.BentCornerFigure;
import com.drgarbage.controlflowgraph.figures.CommentFigure;
import com.drgarbage.visualgraphic.model.CommentElement;
import com.drgarbage.visualgraphic.model.VertexBase;
import com.drgarbage.visualgraphic.policy.LabelDirectEditPolicy;
import com.drgarbage.visualgraphic.policy.VertexBaseComponentEditPolicy;

/**
 * Edit Part for the Comment element.
 *  
 * @author Sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: CommentEditPart.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class CommentEditPart extends VertexBaseEditPart {
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		// allow removal of the associated model element
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new VertexBaseComponentEditPolicy());

		// allow the creation of connections and 
		// and the reconnection of connections between VertexBase instances
		//installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new VertexBaseGraphicalNodeEditPolicy());

		//allow the editing of labels
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,new LabelDirectEditPolicy());	
	}

	public TextFlow getTextFlow(){
		CommentFigure f = (CommentFigure)getFigure();
		return f.getTextFlow();
	}

	protected void performDirectEdit(){
		new VertexBaseDirectEditManager(this, 
				new CommentCellEditorLocator ((CommentFigure)getFigure())).show();
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.editparts.VertexBaseEditPart#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if(VertexBase.TEXT_LABEL_PROP.equals(prop)){
			String s = (String) evt.getNewValue();
			CommentFigure f = (CommentFigure)getFigure();
			f.getTextFlow().setText(s);
			
			//validate layout for the new Text
			f.getTextFlow().getParent().validate();

			CommentElement comment = (CommentElement)getModel();
			
			int height = f.getTextFlow().getBounds().height + BentCornerFigure.DEFAULT_CORNER_SIZE * 2;
			Dimension newSize = comment.getSize();
			newSize.height = height;
			
			comment.setSize(newSize);
			
		}
		else{
			super.propertyChange(evt);
		}


	}
}
