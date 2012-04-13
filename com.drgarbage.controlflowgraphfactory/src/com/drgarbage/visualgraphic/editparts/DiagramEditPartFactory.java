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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import com.drgarbage.visualgraphic.model.BasicBlockVertex;
import com.drgarbage.visualgraphic.model.CommentElement;
import com.drgarbage.visualgraphic.model.Connection;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;
import com.drgarbage.visualgraphic.model.DecisionVertex;
import com.drgarbage.visualgraphic.model.VertexBase;


/**
 * Factory that maps model elements to edit parts.
 *  
 * @author Sergej Alekseev
 * @version $Revision:125 $
 * $Id:DiagramEditPartFactory.java 125 2007-05-22 16:08:25Z aleks $
 */
public class DiagramEditPartFactory implements EditPartFactory {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart context, Object modelElement) {
		// get EditPart for model element
		EditPart part = getPartForElement(modelElement);
		
		// store model element in EditPart
		part.setModel(modelElement);
		return part;
	}
	
	/**
	 * Maps an object to an EditPart. 
	 * @throws RuntimeException if no match was found (programming error)
	 */
	private EditPart getPartForElement(Object modelElement) {
		if (modelElement instanceof ControlFlowGraphDiagram) {
			return new DiagramEditPart();
		}
		if (modelElement instanceof CommentElement) {
			return new CommentEditPart();
		}
		if (modelElement instanceof BasicBlockVertex) {
			return new BasicBlockEditPart();
		}
		if (modelElement instanceof DecisionVertex) {
			return new DecisionVertexEditPart();
		}
		if (modelElement instanceof VertexBase) {
			return new VertexBaseEditPart();
		}
		if (modelElement instanceof Connection) {
			return new EdgeEditPart();
		}
		
		throw new RuntimeException(
				"Can't create part for model element: "
				+ ((modelElement != null) ? modelElement.getClass().getName() : "null"));
	}

}