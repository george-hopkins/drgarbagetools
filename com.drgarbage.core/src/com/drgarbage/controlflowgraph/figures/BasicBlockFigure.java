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

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.swt.graphics.Color;


/**
 * A Figure with a bent corner and an embedded TextFlow within a FlowPage that contains
 * text.
 * 
 * @author Sergej Alekseev  
 * @version $Revision$
 * $Id$
 */
public class BasicBlockFigure extends Figure
{
	
	private boolean useGradientFillColor;
	
	/** The inner TextFlow **/
	private TextFlow textFlow;
	
	/**
	 *  Creates a new basicBlock Figure
	 */
	public BasicBlockFigure(boolean useGradientFillColor) {
		this.useGradientFillColor = useGradientFillColor;
		
		FlowPage flowPage = new FlowPage();
	
		textFlow = new TextFlow();

		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow,
						ParagraphTextLayout.WORD_WRAP_TRUNCATE));//WORD_WRAP_HARD));//.WORD_WRAP_SOFT));
	
//		textFlow.setLayoutManager(new SimpleTextLayout(textFlow));
		
		flowPage.add(textFlow);
	
		setLayoutManager(new StackLayout());
		add(flowPage);
	}
	
	/**
	 * Returns the text inside the TextFlow.
	 * 
	 * @return the text flow inside the text.
	 */
	public String getText() {
		return textFlow.getText();
	}
	
	/**
	 * Sets the text of the TextFlow to the given value.
	 * 
	 * @param newText the new text value.
	 */
	public void setText(String newText) {
		textFlow.setText(newText);
	}

	/**
	 * @return the textFlow
	 */
	public TextFlow getTextFlow() {
		return textFlow;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics graphics) {
		Rectangle r = getBounds();

		if(useGradientFillColor){
			Color bg = graphics.getBackgroundColor();		
			Color fg = new Color(null, bg.getRed()/3, bg.getGreen()/2, bg.getBlue()/2);
			graphics.setForegroundColor(bg);
			graphics.setBackgroundColor(fg);

			graphics.fillGradient(r, false);

			graphics.setForegroundColor(fg);
		}
		else{
			graphics.fillRectangle(r);
		}
		
		r = Rectangle.SINGLETON.setBounds(r);
		r.x += 1;
		r.y += 1;
		r.width -= 2;
		r.height -= 2;

		graphics.drawRectangle(r);
	}

}
