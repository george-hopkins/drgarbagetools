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

package com.drgarbage.controlflowgraph.anchors;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
  * Anchor for the Rectangle vertex figure.
  *
  * @author Sergej Alekseev
  * @version $Revision:25 $
  * $Id:DecisionAnchor.java 25 2007-04-01 17:56:22Z aleks $
  */
public class SimpleAnchor extends AbstractConnectionAnchor {

	/**
	    * 0 - anchor_in, 1 - ancor_out
		*                                 
		*        anchor_in (typ = 0)       
		*                 |                
		*                 V                
		*        +--------+---------+
		*        |                  |
		*        |                  |        
		*        +--------+---------+
		*                 |
		*                 V 
		*         anchor_out (typ = 1)
		* 
	 */
	private int typ = 0;

	
	/**
	 * @see org.eclipse.draw2d.AbstractConnectionAnchor#AbstractConnectionAnchor(IFigure)
	 */
	public SimpleAnchor(IFigure owner) {
		super(owner);
	}
	
	/**
	 * @see org.eclipse.draw2d.AbstractConnectionAnchor#AbstractConnectionAnchor(IFigure)
	 */
	public SimpleAnchor(IFigure owner, int type) {
		super(owner);
		this.typ = type;
	}

	/**
	 * Returns a point on the Rectangle.
	 * @see org.eclipse.draw2d.ConnectionAnchor#getLocation(Point)
	 */
	public Point getLocation(Point reference) {
		Rectangle r = getOwner().getBounds();
		int x = r.x;
		int y = r.y;
		if(this.typ == 0){
			y = r.y;
			x = r.x + r.width/2;
		}
		else {
			y = r.y + r.height;
			x = r.x + r.width/2;
		}

		Point p = new Point(x, y);
		getOwner().translateToAbsolute(p);

		return p;	
	}
	
}
