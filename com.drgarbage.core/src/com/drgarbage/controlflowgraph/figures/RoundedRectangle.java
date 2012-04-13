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
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Display;

/**
 * @author sa
 *
 */
public class RoundedRectangle extends org.eclipse.draw2d.RoundedRectangle {

	private boolean useGradientFillColor = true;
	private int connerSize = 20;//corner.height * 3;
	
	public RoundedRectangle(boolean useGradientFillColor, int c){
		this.useGradientFillColor = useGradientFillColor;
		connerSize = c;
		this.setCornerDimensions(new Dimension(connerSize,connerSize));
	}
	
	public RoundedRectangle(boolean useGradientFillColor){
		this.useGradientFillColor = useGradientFillColor;
		this.setCornerDimensions(new Dimension(connerSize,connerSize));
	}
	
	protected void fillShape(Graphics graphics) {

//		if(useGradientFillColor){
////			Color bg = graphics.getBackgroundColor();
//			Color bg = FigureFactory.makeLight(graphics.getBackgroundColor());
//
//			Color fg = new Color(null, bg.getRed()/3, bg.getGreen()/3, bg.getBlue()/3);
//			graphics.setForegroundColor(bg);
//			graphics.setBackgroundColor(fg);
//
//			Rectangle r = getBounds();
//			graphics.fillGradient(r, false);
//
//			graphics.setBackgroundColor(new Color(null, 255, 255, 255));
//
//			
//			Path pt = new Path( Display.getDefault( ) );  
//			pt.addArc( r.x, r.y, connerSize, connerSize, 90f, 90f ); 
//			pt.lineTo( r.x, r.y );
//
//			pt.addArc( r.x + r.width - connerSize - 1, r.y, connerSize, connerSize, 90f, -90f );
//			pt.lineTo( r.x + r.width, r.y - 1); 
//			
//			pt.addArc( r.x, r.y + r.height - connerSize - 1, connerSize, connerSize, 180f, 90f ); 
//			pt.lineTo( r.x, r.y + r.height - 1 ); 
//						
//			pt.addArc( r.x + r.width - connerSize - 0.5f, r.y + r.height - connerSize -1, connerSize, connerSize, 0f, -90f ); 
//			pt.lineTo( r.x + r.width, r.y + r.height );
//			
//			graphics.fillPath( pt );
//			
//			/* draw outline */
//			graphics.setForegroundColor(new Color(null, 0, 0, 0));		
//			super.outlineShape(graphics);
//			
//		}
//		else{
			super.fillShape(graphics);
			super.outlineShape(graphics);
//		}
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.draw2d.RoundedRectangle#outlineShape(org.eclipse.draw2d.Graphics)
     */
	@Override
    protected void outlineShape(Graphics gc) {
//    	super.outlineShape(gc);
//		Rectangle r = getBounds();
//    	gc.drawRectangle(r);
    }
    	 
}
