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

import org.eclipse.swt.widgets.Text;

import org.eclipse.jface.viewers.CellEditor;

import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.tools.CellEditorLocator;

import com.drgarbage.controlflowgraph.figures.CommentFigure;

/**
 * Cell Editor Locator for the comment element.
 *  
 * @author Sergej Alekseev
 * @version $Revision:214 $
 * $Id:CommentCellEditorLocator.java 214 2007-06-13 17:41:49Z aleks $
 */
final public class CommentCellEditorLocator implements CellEditorLocator
{

	private CommentFigure stickyNote;
	
	public CommentCellEditorLocator(CommentFigure stickyNote) {
		setLabel(stickyNote);
	}
	
	public void relocate(CellEditor celleditor) {
		Text text = (Text)celleditor.getControl();
		Rectangle rect = stickyNote.getClientArea();
		stickyNote.translateToAbsolute(rect);
		org.eclipse.swt.graphics.Rectangle trim = text.computeTrim(0, 0, 0, 0);
		rect.translate(trim.x, trim.y);
		rect.width += trim.width;
		rect.height += trim.height;
		text.setBounds(rect.x, rect.y, rect.width, rect.height);
	}
	
	/**
	 * Returns the stickyNote figure.
	 */
	protected CommentFigure getLabel() {
		return stickyNote;
	}
	
	/**
	 * Sets the Sticky note figure.
	 * @param stickyNote The stickyNote to set
	 */
	protected void setLabel(CommentFigure stickyNote) {
		this.stickyNote = stickyNote;
	}

}