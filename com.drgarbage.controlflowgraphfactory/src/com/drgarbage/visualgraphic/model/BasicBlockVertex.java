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

package com.drgarbage.visualgraphic.model;

import org.eclipse.swt.graphics.Image;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.img.ControlFlowFactoryResource;

/**
 * A basic block vertex.
 * 
 * @author Sergej Alekseev
 * @version $Revision:125 $
 * $Id:RectangularVertex.java 125 2007-05-22 16:08:25Z aleks $
 */
public class BasicBlockVertex extends VertexBase {
	private static final long serialVersionUID = 1;

	public BasicBlockVertex() {
		super();
		this.setLabel(ControlFlowFactoryMessages.RECTANGLE_NODE_TEXT);
	}	

	public Image getIcon() {
		return ControlFlowFactoryResource.rectangular_icon_16x16.createImage();
	}
}
