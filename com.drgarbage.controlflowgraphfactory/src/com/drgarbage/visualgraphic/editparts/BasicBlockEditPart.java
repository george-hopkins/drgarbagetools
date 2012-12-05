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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.gef.EditPolicy;

import com.drgarbage.controlflowgraph.figures.BasicBlockFigure;
import com.drgarbage.visualgraphic.model.BasicBlockVertex;
import com.drgarbage.visualgraphic.model.VertexBase;
import com.drgarbage.visualgraphic.policy.LabelDirectEditPolicy;
import com.drgarbage.visualgraphic.policy.VertexBaseComponentEditPolicy;

/**
 * Edit Part for the basic block element.
 *  
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: CommentEditPart.java 1266 2009-07-27 19:32:33Z Sergej Alekseev $
 */
public class BasicBlockEditPart extends VertexBaseEditPart {
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
		BasicBlockFigure f = (BasicBlockFigure)getFigure();
		return f.getTextFlow();
	}

	protected void performDirectEdit(){
		new VertexBaseDirectEditManager(this, 
				new BasicBlockCellEditorLocator ((BasicBlockFigure)getFigure())).show();
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.visualgraphic.editparts.VertexBaseEditPart#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if(VertexBase.TEXT_LABEL_PROP.equals(prop)){
			String s = (String) evt.getNewValue();
			BasicBlockFigure f = (BasicBlockFigure)getFigure();
			f.getTextFlow().setText(s);
			
			/* validate layout for the new Text */
			f.getTextFlow().getParent().validate();

			BasicBlockVertex basicBlock = (BasicBlockVertex)getModel();
//			
//			int height = f.getTextFlow().getBounds().height;
//			int width = f.getTextFlow().getBounds().width;
//			Dimension newSize = basicBlock.getSize();
//			newSize.height = height;
//			newSize.width = width;
			
			basicBlock.setSize(f.getTextFlow().getSize());
			
		}
		else{
			super.propertyChange(evt);
		}


	}
	
	public Label getLabel(){
		IFigure f = getFigure();
		
		/* create an empty label */
		String txt =  getTextFlow().getText();
		Label l = new Label(txt);
		l.setSize(f.getSize());
		l.setLocation(f.getClientArea().getLocation());
		return l;
	}
}
