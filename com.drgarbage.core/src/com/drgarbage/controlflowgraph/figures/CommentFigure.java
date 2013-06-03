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

import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;


/**
 * A Figure with a bent corner and an embedded TextFlow within a FlowPage that contains
 * text.
 * 
 * @author Sergej Alekseev  
 * @version $Revision$
 * $Id$
 */
public class CommentFigure extends BentCornerFigure
{
	
	/** The inner TextFlow **/
	private TextFlow textFlow;
	
	/**
	 *  Creates a new CommentFigure with a default MarginBorder size of DEFAULT_CORNER_SIZE
	 *  - 3 and a FlowPage containing a TextFlow with the style WORD_WRAP_SOFT.
	 */
	public CommentFigure() {
		this(BentCornerFigure.DEFAULT_CORNER_SIZE - 3);
	}

	/** 
	 * Creates a new CommentFigure with a MarginBorder that is the given size and a
	 * FlowPage containing a TextFlow with the style WORD_WRAP_SOFT.
	 * 
	 * @param borderSize the size of the MarginBorder
	 */
	public CommentFigure(int borderSize) {
		setBorder(new MarginBorder(borderSize));
		FlowPage flowPage = new FlowPage();
	
		textFlow = new TextFlow();

		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow,
						ParagraphTextLayout.WORD_WRAP_SOFT));//.WORD_WRAP_SOFT));
	
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

}
