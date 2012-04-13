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

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;

import com.drgarbage.core.CorePlugin;
import com.drgarbage.core.preferences.CorePreferenceConstants;

/**
 * Factory for figures of vertices
 *
 * @author Sergej Alekseev  
 * @version $Revision:143 $
 * $Id:FigureFactory.java 143 2007-05-28 10:10:03Z aleks $
 */
public class FigureFactory implements CorePreferenceConstants {
	
	public static IFigure createRectangularVertex(boolean useGradientFillColor) {
		RectangleFigure r = new RectangleFigure(useGradientFillColor);
		r.setBackgroundColor(createColor(INSTRUCTION_BGCOLOR));	
		return r;	
	}

	public static IFigure createEntryEndVertex(boolean useGradientFillColor){
		RoundedRectangle r = new RoundedRectangle(useGradientFillColor);
		r.setBackgroundColor(createColor(ENTRY_END_BGCOLOR));	
		return r;
	}

	public static IFigure createDecisionVertex(boolean useGradientFillColor){
		DecisionVertexFigure r = new DecisionVertexFigure(useGradientFillColor);
		r.setBackgroundColor(createColor(DECISION_VERTEX_BGCOLOR));	
		return r;
	}

	public static IFigure createReturnVertex(boolean useGradientFillColor){
		ReturnVertexFigure r = new ReturnVertexFigure(useGradientFillColor);
		r.setBackgroundColor(createColor(RETURN_VERTEX_BGCOLOR));	
		return r;
	}
	
	public static IFigure createInvokeVertex(boolean useGradientFillColor){
		InvokeVertexFigure r = new InvokeVertexFigure(useGradientFillColor);
		r.setBackgroundColor(createColor(INVOKE_VERTEX_BGCOLOR));	
		return r;
	}
	
	public static IFigure createGetVertex(boolean useGradientFillColor){
		GetVertexFigure r = new GetVertexFigure(useGradientFillColor);
		r.setBackgroundColor(createColor(GET_VERTEX_BGCOLOR));
		return r;
	}
	
	public static IFigure createSwitchVertex(boolean useGradientFillColor){
		SwitchVertexFigure r = new SwitchVertexFigure(useGradientFillColor);
		r.setBackgroundColor(createColor(SWITCH_VERTEX_BGCOLOR));
		return r;
	}
	
	public static IFigure createGotoJumpVertex(boolean useGradientFillColor) {
		CotoJumpVertexFigure r = new CotoJumpVertexFigure(useGradientFillColor);
		r.setBackgroundColor(createColor(GOTO_JUMP_VERTEX_BGCOLOR));
		return r;
	}
	
	public static IFigure createCommentFigure(String s, boolean useGradientFillColor) {
		CommentFigure r = new CommentFigure();
		r.setBackgroundColor(createColor(COMMENT_BGCOLOR));
		r.setText(s);
		
		r.setFont(JFaceResources.getFont(IDebugUIConstants.PREF_CONSOLE_FONT));
		return r;
	}
	
	public static IFigure createBasicBlockVertex(String multipleLine, boolean useGradientFillColor) {
		BasicBlockFigure r = new BasicBlockFigure(useGradientFillColor);
		r.setBackgroundColor(createColor(BASIC_BLOCK_BGCOLOR));
		r.setText(multipleLine);
		
		r.setFont(JFaceResources.getFont(IDebugUIConstants.PREF_CONSOLE_FONT));
		return r;	
	}
	
	public static IFigure createSimpleBasicBlockVertex(String s, boolean useGradientFillColor) {
		RectangleFigure r = new RectangleFigure(useGradientFillColor);
		r.setBackgroundColor(createColor(BASIC_BLOCK_BGCOLOR));
		r.setToolTip(new Label(s));
		return r;	
	}
	
	/*
	 * Moved to com.drgarbage.bytecodevisualizer.preferences.BytecodeVizualizerPreferenceConstants
	 * Misc. colors
	final static Color rectangleColor      		= new Color(null, 128, 128, 255);
	final static Color roundedRectangleColor  	= new Color(null, 230, 230, 230);
	final static Color elipseColor       		= new Color(null, 255, 255, 255);
	final static Color decisionVertexColor   	= new Color(null, 128, 255, 128);
	final static Color returnVertexColor      	= new Color(null, 255, 179, 128);
	final static Color InvokeVertexColor      	= new Color(null, 255, 255, 128);
	final static Color getVertexColor	        = new Color(null, 255,  60,  60);
	final static Color switchVerteColor     	= new Color(null, 222, 205, 135);
	final static Color gotoJumpVertexColor     	= new Color(null, 128, 255, 255);
	 */
	
	private static Color createColor(String preferencesKey) {
		IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();
		return new Color(null, PreferenceConverter.getColor(store, preferencesKey));
	}
	
	public static Color makeLight(Color oldColor){
		int red = oldColor.getRed();
		int green = oldColor.getGreen();
		int blue = oldColor.getBlue();
		
		int delta = 0;
		
		if(red > green){
			if(red > blue){
				/* Red has the highest value */
				delta = 255 - red;
			}
			else{
				/* Blue has the highest value */
				delta = 255 - blue;
			}
			
		}
		else if( green > blue) {
			/* Green has the highest value */
			delta = 255 - green;
		}
		else{
			/* Blue has the highest value */
			delta = 255 - blue;
		}
		
		
		return new Color(oldColor.getDevice(), oldColor.getRed() + delta, oldColor.getGreen() + delta, oldColor.getBlue() + delta);
	
	}

}
