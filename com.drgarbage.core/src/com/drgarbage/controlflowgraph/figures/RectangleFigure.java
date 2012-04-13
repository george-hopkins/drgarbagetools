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
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

/**
 *  Figure for the rectangel vertex.
 *
 * @author Sergej Alekseev  
 *  @version $Revision$
 *  $Id$
 */
public class RectangleFigure extends org.eclipse.draw2d.RectangleFigure {
	
	private boolean useGradientFillColor = false;
	
	public RectangleFigure(boolean useGradientFillColor) {
		super();
		this.useGradientFillColor = useGradientFillColor;
	}



	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.RectangleFigure#fillShape(org.eclipse.draw2d.Graphics)
	 */
	protected void fillShape(Graphics graphics){
		
		if(useGradientFillColor){			
			Color bg = FigureFactory.makeLight(graphics.getBackgroundColor());
			
			Color fg = new Color(null, bg.getRed()/3, bg.getGreen()/3, bg.getBlue()/3);
			graphics.setForegroundColor(bg);
			graphics.setBackgroundColor(fg);

			Rectangle r = getBounds();
			graphics.fillGradient(r, false);

			graphics.setForegroundColor(new Color(null, 0, 0, 0));
			graphics.drawRectangle(r);
		}
		else{
			Rectangle r = getBounds();
			graphics.fillRectangle(r);
		}
	}
}
