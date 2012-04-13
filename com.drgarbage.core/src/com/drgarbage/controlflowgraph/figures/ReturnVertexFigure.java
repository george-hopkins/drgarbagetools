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
 *  Figure for the return vertex.
 *
 * @author Sergej Alekseev  
 *  @version $Revision:143 $
 *  $Id:ReturnVertexFigure.java 143 2007-05-28 10:10:03Z aleks $
 */
public class ReturnVertexFigure extends Shape {

	private boolean useGradientFillColor;
	
	/**
	 * Constructs a new shape figure with the default values.
	 * @param useGradientFillColor
	 */
	public ReturnVertexFigure(boolean useGradientFillColor){
		this.useGradientFillColor = useGradientFillColor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Shape#fillShape(org.eclipse.draw2d.Graphics)
	 */
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

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Shape#outlineShape(org.eclipse.draw2d.Graphics)
	 */
	@Override
	protected void outlineShape(Graphics graphics) {
//	    Rectangle r = getBounds();
//	    graphics.drawPolygon(getPoints(r));
	}
	
	/**
	 * Returns list of points of the shape polygon.
	 *  <pre>
	 *    +-------+
	 *    |       |
	 *     \     /
	 *       \ /
	 * </pre>   
	 * @param r - rectangle
	 * @return list of points
	 */
	private PointList getPoints(Rectangle r){
	    PointList pl = new PointList(5);
	    pl.addPoint(r.x, r.y);
	    pl.addPoint(r.x + r.width - 1, r.y);
	    pl.addPoint(r.x + r.width - 1, r.y + r.height/2 + r.height/4);
	    pl.addPoint(r.x + r.width/2, r.y + r.height - 1);
	    pl.addPoint(r.x, r.y + r.height/2 + r.height/4);
	    return pl;
	}
	
	/**
	 * Returns list of points outside of the shape polygon.
	 *  <pre>
	 *    |\     /|
	 *    |__\ /__|
	 * </pre>    
	 * @param r - rectangle
	 * @return list of points
	 */
	private PointList getPointsOutside(Rectangle r){
	    PointList pl = new PointList(5);
	    pl.addPoint(r.x, r.y + r.height/2 + r.height/4);
	    pl.addPoint(r.x + r.width/2, r.y + r.height - 1);
	    pl.addPoint(r.x + r.width + 2, r.y + r.height/2 + r.height/4);
	    pl.addPoint(r.x + r.width + 1, r.y + r.height + 2);
	    pl.addPoint(r.x, r.y + r.height + 2);
	    return pl;
	}

}
