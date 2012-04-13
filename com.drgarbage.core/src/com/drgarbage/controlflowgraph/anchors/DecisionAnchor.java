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
  * Anchor for the decision vertex figure.
  *
  * @author Sergej Alekseev  
  * @version $Revision:25 $
  * $Id:DecisionAnchor.java 25 2007-04-01 17:56:22Z aleks $
  */
public class DecisionAnchor extends AbstractConnectionAnchor {

	/**
	  *   Anchorns for connections
	  *                               
	  *        	fix_anchor_in       
	  *                 |                
	  *                 V                
	  *                 +                
	  *                /|\               
	  *               / | \              
	  *              /  |  \             
	  *             /   |   \            
	  *         +--+----+----+ --+       
	  *         |   \   |   /    |       
	  *         V    \  |  /     V       
	  *  flow_anchor  \ | /  flow_anchor
	  *   (typ = 1)    \|/    (typ = 2)  
	  *                 + 
	  *           flow_anchor (typ 3)
	*/

	
	/**
	 * unvisible constructor
	 */
	private DecisionAnchor() { }

	/**
	 * @see org.eclipse.draw2d.AbstractConnectionAnchor#AbstractConnectionAnchor(IFigure)
	 */
	public DecisionAnchor(IFigure owner) {
		super(owner);
	}

	/**
	 * Returns a point on the decision figure (defined by the owner's bounding box) where the
	 * connection should be anchored.
	 * @see org.eclipse.draw2d.ConnectionAnchor#getLocation(Point)
	 */
	public Point getLocation(Point reference) {
	
		Rectangle r = Rectangle.SINGLETON;
		r.setBounds(getOwner().getBounds());
		r.translate(-1, -1);
		r.resize(1, 1);

		getOwner().translateToAbsolute(r);

		if(reference.y <= r.y + r.height/2){			
			if(reference.x <= (r.x + r.width/2) ){
				return new Point(r.x + 1, r.y + r.height/2);	
			}
			else{
				return new Point(r.x + r.width, r.y + r.height/2);	
			}
		}
		else{
			
			if(reference.x <= r.x){
				return new Point(r.x + 1, r.y + r.height/2);
			}
			else if(reference.x >= r.x + r.width ){
				return new Point(r.x + r.width, r.y + r.height/2);			
			}
			else {
				return new Point(r.x + r.width/2, r.y + r.height);	
			}
			
		}
	}
}
