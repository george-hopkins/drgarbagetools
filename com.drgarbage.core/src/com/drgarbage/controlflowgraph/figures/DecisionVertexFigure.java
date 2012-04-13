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

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

/**
 * Figure for the decision instruction.
 *
 * @author Sergej Alekseev   
 * @version $Revision:25 $
 * $Id:DecisionVertexFigure.java 25 2007-04-01 17:56:22Z aleks $
 */
public class DecisionVertexFigure extends Shape {

	private boolean useGradientFillColor;

	/**
	 * Constructs a new shape figure with the default values.
	 * @param useGradientFillColor
	 */
	public DecisionVertexFigure(boolean useGradientFillColor){
		this.useGradientFillColor = useGradientFillColor;
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

		//////////////////////////
		// A 	= r.x, r.y
		// A-B	= r.height
		// A-C	= r.weight             
		//  
		//  A            C
		//   +----+----+------> 
		//   |   /|\   |
		//   |  / | \  |
		//   | /  |  \ |
		//   |/   |   \|
		//   +----+----+
		//   |\   |   /|      
		//   | \  |  / |
		//   |  \ | /  |     
		//   |   \|/   | 
		// B +----+----+  D
		//   |
		//   V
		// 
		//  Point (ux, uy) is apoint inside of the rectangle A-B-C-D

		float cx = r.width/2;
		float cy= r.height/2;
		float k= cy / cx;

		long ux = x - r.x;//relative coordinate	
		long uy = y - r.y;//relative coordinate				

		if(ux <= cx){
			float e1= cy - ux * k;//qudrant I
			float e2= cy + ux * k;//qudrant II

			if(uy >= e1 && uy <= e2){
				return true;
			}
		}
		else{
			float e3= cy - (r.width-ux) * k;//qudrant III
			float e4= cy + (r.width-ux) * k;//qudrant III
			if(uy >= e3 && uy <= e4){
				return true;
			}

		}
		return false;

	}		  

	@Override
	protected void fillShape(Graphics graphics) {

		Rectangle r = getBounds();
	    
	    if(useGradientFillColor){
//	    	Color bg = graphics.getBackgroundColor();
			Color bg = FigureFactory.makeLight(graphics.getBackgroundColor());

			Color fg = new Color(null, bg.getRed()/3, bg.getGreen()/3, bg.getBlue()/3);
			graphics.setForegroundColor(bg);
			graphics.setBackgroundColor(fg);
			
	    	graphics.fillGradient(r, false);
	    	
	    	bg = new Color(null, 255, 255, 255);
			graphics.setBackgroundColor(bg);
			
	    	graphics.fillPolygon(getPointsOutside(r));
	    	
	    	graphics.setForegroundColor(new Color(null, 0, 0, 0));
			graphics.drawPolygon(getPoints(r));
	    }
	    else{
	    	graphics.fillPolygon(getPoints(r));
	    	graphics.drawPolygon(getPoints(r));	
	    }

	}
	
	/**
	 * Returns list of points of the shape polygon.
	 *  <pre>
	 *       / \
	 *     /     \
	 *     \     /
	 *       \ /
	 * </pre>   
	 * @param r - rectangle
	 * @return list of points
	 */
	private PointList getPoints(Rectangle r){
	    PointList pl = new PointList(5);
		pl.addPoint(r.x + r.width / 2, r.y);
		pl.addPoint(r.x, r.y + r.height / 2);
		pl.addPoint(r.x + r.width / 2, r.y + r.height );
		pl.addPoint(r.x + r.width, r.y + r.height / 2);
	    return pl;
	}
	
	/**
	 * Returns list of points outside of the shape polygon.
	 *  <pre>
	 *     _______
	 *    |  / \  |
	 *    |/  x  \|
	 *    |\     /|
	 *    |__\ /__|
	 * </pre>    
	 * @param r - rectangle
	 * @return list of points
	 */
	private PointList getPointsOutside(Rectangle r){
	    PointList pl = new PointList(9);
		pl.addPoint(r.x, r.y);
		pl.addPoint(r.x + r.width + 1, r.y);
		pl.addPoint(r.x + r.width + 1, r.y + r.height + 2);		
		pl.addPoint(r.x, r.y + r.height + 2);
		
		pl.addPoint(r.x, r.y + r.height/2);
		pl.addPoint(r.x + r.width/2, r.y + r.height);
		pl.addPoint(r.x + r.width, r.y + r.height/2);
		pl.addPoint(r.x + r.width/2, r.y);
		
		pl.addPoint(r.x, r.y + r.height/2);
		
	    return pl;
	}

	@Override
	protected void outlineShape(Graphics graphics) {
//		Rectangle r = getBounds();
//		graphics.drawPolygon(getPoints(r));	

		//graphics.drawOval(r);//for testing
	}
}