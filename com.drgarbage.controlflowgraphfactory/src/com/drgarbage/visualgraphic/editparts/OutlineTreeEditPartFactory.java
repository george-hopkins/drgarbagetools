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

import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;
import com.drgarbage.visualgraphic.model.VertexBase;



/**
 * Factory that maps model elements to TreeEditParts.
 * TreeEditParts are used in the outline view of the ControlFlowGraphEditor.
 *  
 * @author Sergej Alekseev
 * @version $Revision:125 $
 * $Id:OutlineTreeEditPartFactory.java 125 2007-05-22 16:08:25Z aleks $
 */
public class OutlineTreeEditPartFactory implements EditPartFactory {

/* (non-Javadoc)
 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
 */
public EditPart createEditPart(EditPart context, Object model) {
	if (model instanceof VertexBase) {
		return new VertexBaseTreeEditPart((VertexBase) model);
	}	
	if (model instanceof ControlFlowGraphDiagram) {
		return new DiagramTreeEditPart((ControlFlowGraphDiagram) model);
	}
	return null; /* will not show an entry for the corresponding model instance */
}

}
