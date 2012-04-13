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

package com.drgarbage.controlflowgraph.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Rectangle;

/**
  * Figure for the eleptical vertex.
  *
  * @author Sergej Alekseev   
  * @version $Revision:25 $
  * $Id:ElipseVertexFigure.java 25 2007-04-01 17:56:22Z aleks $
  */
public class ElipseVertexFigure extends Shape {

	/**
	 * Constructs a new Ellipse with the default values of a vertex.
	 */
	public ElipseVertexFigure() { 
		this.setBackgroundColor(ColorConstants.blue);
	}

	/**
	 * Returns <code>true</code> if the given point (x,y) is contained within this ellipse.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return <code>true</code>if the given point is contained
	 */
	public boolean containsPoint(int x, int y) {
		if (!super.containsPoint(x, y))
			return false;
		Rectangle r = getBounds();
		long ux = x - r.x - r.width / 2;
		long uy = y - r.y - r.height / 2;
		return ((ux * ux) << 10) / (r.width * r.width) 
			 + ((uy * uy) << 10) / (r.height * r.height) <= 256;
	}

	/**
	 * Fills the ellipse.
	 * @see org.eclipse.draw2d.Shape#fillShape(org.eclipse.draw2d.Graphics)
	 */
	protected void fillShape(Graphics graphics) {
		graphics.fillOval(getBounds());
	}

	/**
	 * Outlines the ellipse.
	 * @see org.eclipse.draw2d.Shape#outlineShape(org.eclipse.draw2d.Graphics)
	 */
	protected void outlineShape(Graphics graphics) {
		Rectangle r = Rectangle.SINGLETON;
		r.setBounds(getBounds());
		r.width--;
		r.height--;
		r.shrink((lineWidth - 1) / 2, (lineWidth - 1) / 2);
		graphics.drawOval(r);
	}

}
