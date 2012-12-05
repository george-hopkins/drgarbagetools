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

package com.drgarbage.visualgraphic.policy;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

import com.drgarbage.controlflowgraph.figures.BentCornerFigure;
import com.drgarbage.visualgraphic.commands.DirectEditCommand;
import com.drgarbage.visualgraphic.editparts.CommentEditPart;
import com.drgarbage.visualgraphic.editparts.IDirectEditPart;
import com.drgarbage.visualgraphic.model.CommentElement;

/**
 * Direct edit Policy.
 *  
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: LabelDirectEditPolicy.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class LabelDirectEditPolicy extends DirectEditPolicy {

	/**
	 * @see DirectEditPolicy#getDirectEditCommand(DirectEditRequest)
	 */
	protected Command getDirectEditCommand(DirectEditRequest edit) {
		String labelText = (String)edit.getCellEditor().getValue();
		IDirectEditPart editPart = (IDirectEditPart) getHost();
		DirectEditCommand command = new DirectEditCommand(editPart.getDirectEditModel(),labelText);
		return command;
	}
	
	/**
	 * @see DirectEditPolicy#showCurrentEditValue(DirectEditRequest)
	 */
	protected void showCurrentEditValue(DirectEditRequest request) {
		String value = (String)request.getCellEditor().getValue();
		
		Object o = getHost();
		if(o instanceof CommentEditPart){
			CommentEditPart editPart = (CommentEditPart) o;
			editPart.getTextFlow().setText(value);
			
			//validate layout for the new Text
			editPart.getTextFlow().getParent().validate();

			CommentElement comment = (CommentElement)editPart.getModel();

			int height = editPart.getTextFlow().getBounds().height + BentCornerFigure.DEFAULT_CORNER_SIZE * 2;
			Dimension newSize = comment.getSize();
			newSize.height = height;
			
			comment.setSize(newSize);
		}
		else{	
			IDirectEditPart editPart = (IDirectEditPart) o;
			editPart.getLabel().setText(value);
		}
		//hack to prevent async layout from placing the cell editor twice.
		getHostFigure().getUpdateManager().performUpdate();
	}
}
