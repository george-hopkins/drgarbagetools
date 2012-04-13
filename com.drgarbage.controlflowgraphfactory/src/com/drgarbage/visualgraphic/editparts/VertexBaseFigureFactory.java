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

package com.drgarbage.visualgraphic.editparts;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.jface.resource.JFaceResources;

import com.drgarbage.controlflowgraph.figures.FigureFactory;
import com.drgarbage.visualgraphic.model.BasicBlockVertex;
import com.drgarbage.visualgraphic.model.CommentElement;
import com.drgarbage.visualgraphic.model.DecisionVertex;
import com.drgarbage.visualgraphic.model.ExitVertex;
import com.drgarbage.visualgraphic.model.GetVertex;
import com.drgarbage.visualgraphic.model.GotoJumpVertex;
import com.drgarbage.visualgraphic.model.InvokeVertex;
import com.drgarbage.visualgraphic.model.RectangularVertex;
import com.drgarbage.visualgraphic.model.ReturnVertex;
import com.drgarbage.visualgraphic.model.RoundedRectangularVertex;
import com.drgarbage.visualgraphic.model.StartVertex;
import com.drgarbage.visualgraphic.model.SwitchVertex;


/**
 * Factory of the vertex Ffgures
 *  
 * @author Sergej Alekseev
 * @version $Revision:125 $
 * $Id:VertexBaseFigureFactory.java 125 2007-05-22 16:08:25Z aleks $
 */
public class VertexBaseFigureFactory {
	
	public final  static String consolenFont = "org.eclipse.debug.ui.consoleFont";
	
	public static IFigure createDecisionVertex(DecisionVertex o, boolean useGradientFillColor){
		IFigure f = FigureFactory.createDecisionVertex(useGradientFillColor);
		
		BorderLayout b = new BorderLayout();
		f.setLayoutManager(b);

	  	Label label = new Label(o.getLabel());
	  	Label toolTipLabel = new Label(o.getToolTip());
	  	if(o.isLongDescrUsed()){
		  	label.setFont(JFaceResources.getFont(consolenFont));		  	
		  	f.add(label, BorderLayout.CENTER);
	  	}	  	
	  	else {
	  		f.add(label, BorderLayout.CENTER);
	  		toolTipLabel.setFont(JFaceResources.getFont(consolenFont));
	  		
	  		f.add(new Label("   "), BorderLayout.LEFT); //left margin
		  	f.add(new Label("   "), BorderLayout.RIGHT);//right margin
		  	
	  	}
	  	
	  	if(o.getToolTip() != null){
	  		f.setToolTip(new Label(o.getToolTip()));
	  	}

	  	return f;
	}
	
	public static IFigure createGetVertex(GetVertex o, boolean useGradientFillColor){
		IFigure f = FigureFactory.createGetVertex(useGradientFillColor);
		
		BorderLayout b = new BorderLayout();
		f.setLayoutManager(b);

	  	Label label=new Label();
	  	label.setText(o.getLabel());
//	  	label.setFont(JFaceResources.getFont(consolenFont));
	  	f.add(label, BorderLayout.CENTER);

	  	f.add(new Label("   "), BorderLayout.LEFT); //left margin
	  	f.add(new Label("   "), BorderLayout.RIGHT);//right margin
	  	
		/* set tool tip only for rectangular vertex */
	  	if(o.getToolTip() != null){
	  		f.setToolTip(new Label(o.getToolTip()));
	  	}
		
		return f;
	}
	
	public static IFigure createGotoJumpVertex(GotoJumpVertex o, boolean useGradientFillColor){
		IFigure f = FigureFactory.createGotoJumpVertex(useGradientFillColor);

		BorderLayout b = new BorderLayout();
		f.setLayoutManager(b);

	  	Label label=new Label();
	  	label.setText(o.getLabel());
//	  	label.setFont(JFaceResources.getFont(consolenFont));
	  	f.add(label, BorderLayout.CENTER);
	  	
	  	f.add(new Label("       "), BorderLayout.LEFT); //left margin 7 characters
	  	f.add(new Label("   "), BorderLayout.RIGHT);//right margin
		
	  	if(o.getToolTip() != null){
	  		f.setToolTip(new Label(o.getToolTip()));
	  	}
		
		return f;
	}
	
	public static IFigure createInvokeVertex(InvokeVertex o, boolean useGradientFillColor){
		IFigure f = FigureFactory.createInvokeVertex(useGradientFillColor);

		BorderLayout b = new BorderLayout();
		f.setLayoutManager(b);

	  	Label label=new Label();
	  	label.setText(o.getLabel());
//	  	label.setFont(JFaceResources.getFont(consolenFont));
	  	f.add(label, BorderLayout.CENTER);
	  	
	  	f.add(new Label(" "), BorderLayout.LEFT); //left margin
	  	f.add(new Label("   "), BorderLayout.RIGHT);//right margin
	  	
	  	if(o.getToolTip() != null){
	  		f.setToolTip(new Label(o.getToolTip()));
	  	}
	  	
		return f;
	}
	
	public static IFigure createBasicBlockVertex(BasicBlockVertex o, boolean useGradientFillColor){
		IFigure f = FigureFactory.createBasicBlockVertex(o.getLabel(), useGradientFillColor);
		
	  	if(o.getToolTip() != null){
	  		f.setToolTip(new Label(o.getToolTip()));
	  	}
		
		return f;
	}
	
	public static IFigure createRectangularVertex(RectangularVertex o, boolean useGradientFillColor){
		IFigure f = FigureFactory.createRectangularVertex(useGradientFillColor);

		BorderLayout b = new BorderLayout();
		f.setLayoutManager(b);
		
	  	Label label = new Label(o.getLabel());
	  	Label toolTipLabel = new Label(o.getToolTip());
	  	if(o.isLongDescrUsed()){
		  	label.setFont(JFaceResources.getFont(consolenFont));		  	
		  	f.add(label, BorderLayout.LEFT);
	  	}	  	
	  	else {
	  		f.add(label, BorderLayout.CENTER);		  	
		  	f.add(new Label(" "), BorderLayout.LEFT); //left margin
		  	f.add(new Label("   "), BorderLayout.RIGHT);//right margin
	  		toolTipLabel.setFont(JFaceResources.getFont(consolenFont));
	  	}

	  	if(o.getToolTip() != null){
	  		f.setToolTip(toolTipLabel);
	  	}
		
		return f;
	}
	
	public static IFigure createReturnVertex(ReturnVertex o, boolean useGradientFillColor){
		IFigure f = FigureFactory.createReturnVertex(useGradientFillColor);

		BorderLayout b = new BorderLayout();
		f.setLayoutManager(b);

	  	Label label = new Label(o.getLabel());
	  	Label toolTipLabel = new Label(o.getToolTip());
	  	if(o.isLongDescrUsed()){
		  	label.setFont(JFaceResources.getFont(consolenFont));		  	
		  	f.add(label, BorderLayout.TOP);
	  	}	  	
	  	else {
	  		f.add(label, BorderLayout.CENTER);		  	
		  	f.add(new Label(" "), BorderLayout.LEFT); //left margin
		  	f.add(new Label("   "), BorderLayout.RIGHT);//right margin
	  		toolTipLabel.setFont(JFaceResources.getFont(consolenFont));
	  	}

	  	if(o.getToolTip() != null){
	  		f.setToolTip(toolTipLabel);
	  	}
		
		return f;
	}
	
	public static IFigure createStartVertex(StartVertex o, boolean useGradientFillColor){
		return createRoundedRectangularVertex(o, useGradientFillColor);
	}
	public static IFigure createExitVertex(ExitVertex o, boolean useGradientFillColor){
		return createRoundedRectangularVertex(o, useGradientFillColor);
	}
	private static IFigure createRoundedRectangularVertex(RoundedRectangularVertex o, boolean useGradientFillColor){
		IFigure f = FigureFactory.createEntryEndVertex(useGradientFillColor);
		
		BorderLayout b = new BorderLayout();
		f.setLayoutManager(b);

	  	Label label=new Label();
	  	label.setText(o.getLabel());
	  	f.add(label, BorderLayout.CENTER);
	  	
//	  	f.add(new Label(" "), BorderLayout.LEFT); //left margin
		
	  	if(o.getToolTip() != null){
	  		f.setToolTip(new Label(o.getToolTip()));
	  	}
		
		return f;
	}
	
	public static IFigure createSwitchVertex(SwitchVertex o, boolean useGradientFillColor){
		IFigure f = FigureFactory.createSwitchVertex(useGradientFillColor);
		
		BorderLayout b = new BorderLayout();
		f.setLayoutManager(b);

	  	Label label = new Label(o.getLabel());
	  	Label toolTipLabel = new Label(o.getToolTip());
	  	if(o.isLongDescrUsed()){
		  	label.setFont(JFaceResources.getFont(consolenFont));		  	
		  	f.add(label, BorderLayout.CENTER);
	  	}	  	
	  	else {
	  		f.add(label, BorderLayout.CENTER);
	  		f.add(new Label("   "), BorderLayout.LEFT); /* left margin */
	  		toolTipLabel.setFont(JFaceResources.getFont(consolenFont));
	  	}

	  	if(o.getToolTip() != null){
	  		f.setToolTip(toolTipLabel);
	  	}
		
		return f;
	}

	public static IFigure createComment(CommentElement o, boolean useGradientFillColor){
		IFigure f = FigureFactory.createCommentFigure(o.getLabel(), useGradientFillColor);
		return f;
	}
}
