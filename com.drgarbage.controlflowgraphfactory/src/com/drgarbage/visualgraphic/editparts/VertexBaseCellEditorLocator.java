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

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;

/**
 * Cell Editor Location.
 *  
 * @author Sergej Alekseev
 * @version $Revision:214 $
 * $Id:VertexBaseCellEditorLocator.java 214 2007-06-13 17:41:49Z aleks $
 */
final public class VertexBaseCellEditorLocator implements CellEditorLocator
{
	
	private Label label;
	
	public VertexBaseCellEditorLocator(Label label) {
		this.label = label;
	}
	
	public void relocate(CellEditor celleditor) {
		Text text = (Text)celleditor.getControl();
		Rectangle rect = label.getTextBounds();
		label.translateToAbsolute(rect);
		org.eclipse.swt.graphics.Rectangle trim = text.computeTrim(0, 0, 0, 0);
		rect.translate(trim.x, trim.y);
		rect.width += trim.width;
		rect.height += trim.height;
		
		/* fix MAC OS specific exception */
		if(rect.width <= 16){
			rect.width = 32;
		}

		text.setBounds(rect.x, rect.y, rect.width, rect.height);
	}

}