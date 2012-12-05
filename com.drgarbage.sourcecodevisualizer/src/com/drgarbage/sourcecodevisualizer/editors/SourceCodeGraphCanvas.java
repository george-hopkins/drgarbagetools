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

package com.drgarbage.sourcecodevisualizer.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.drgarbage.bytecode.ByteCodeConstants;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.core.img.CoreImg;
import com.drgarbage.sourcecodevisualizer.SourcecodeVisualizerMessages;
import com.drgarbage.utils.Messages;

/**
 * Canvas for source code graph figure.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: SourceCodeGraphCanvas.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class SourceCodeGraphCanvas extends FigureCanvas {

	private final int WARNING_MAX_GRAPH_NODE_COUNT = 3000;
	private final int WARNING_MAX_GRAPH_EDGE_COUNT = 1000;
	
	/* weight and height of the canvas after initialization */
	private int w = 200;
	private int h = 200;

    /**
     * The height of the line, default 17 pixel.
     */
	protected int lineHight = 17;
	
    /**
     * The weight of the line, default 500 pixel.
     */
    protected int lineWight = 1500;	

	/**
	 * Am empty root figure.
	 */
	private Figure rootFigure = new Figure();

    /**
     * Line-Selector  Object.
     */
    private final Figure lineSelector = new Figure();
    
	/**
	 * The color of the line selector.
	 */
	private Color lineSelectorColor = new Color(null,  232, 242, 254);

	
	/**
	 * Dr. Garbage image.
	 */
	private Label label = new Label();
	
	/**
	 * List of figures representing the constructor graphs.
	 */
	private List<Figure> constructorGraphsList = new ArrayList<Figure>();
	
	
	/**
	 * List of figures representing other methods.
	 */
	private List<Figure> methodGraphsList = new ArrayList<Figure>();
	
	
	private boolean showContructorGraphs = true;
	private boolean showMethodGraphs = true;
	
	/**
	 * Constructor.
	 * NOTE: the object has to be initialized. see ControlFlowGraphCanvas.init();
	 * @param parent
	 */
	public SourceCodeGraphCanvas(Composite parent) {
		super(parent);

		LightweightSystem lws = this.getLightweightSystem();
		lws.setContents(this.getViewport());
		
		setScrollBarVisibility(FigureCanvas.ALWAYS);
		
		/** 
		 * FIX: bug#29: Graph window: SIze, scrollbar, marked line
		 * Other solution doesn't work here. 
		 */
		this.addControlListener(new ControlListener(){

			public void controlMoved(ControlEvent e) {
			}

			public void controlResized(ControlEvent e) {
				rootFigure.setSize(w, h);
			}
			
		});
		
		/* init Dr. Garbage label */
		Image image = CoreImg.labelDrGarbage_148x30.createImage();
		label.setIcon(image);
		label.setSize(new Dimension(148, 30));
		label.setLocation(new Point(5,5));		
		
		/* create line selector */
		createLineSelector();
		
		/* set border of the root figure */
		LineBorder b = new LineBorder();
		b.setColor(ColorConstants.white);
		rootFigure.setBorder(b);

	}

	
	/**
	 * Create line selector figure and add it to the view.
	 */
	private void createLineSelector(){
		lineSelector.setBackgroundColor(lineSelectorColor);
		lineSelector.setOpaque(true); 				/* non-transparent figure*/
		lineSelector.setSize(lineWight, lineHight);
		lineSelector.setLocation(new Point(0, -1));
		lineSelector.setVisible(true);
	}
	
	/**
	 * Selects the given line.
	 * @param the number of the line to be selected. 
	 */
	public void selectLine(int line){
		lineSelector.setLocation(new Point(0, line * lineHight));
	}
	
	/**
	 * Returns the number of the line currently selected
	 * in the control flow graph view.
	 * @return the selected line.
	 */
	public int getSelectedLine() {
		return lineSelector.getLocation().y * lineHight;
	}	
	
	/**
	 * Initialization of the canvas. The canvas object can be used only
	 * after its initialization. 
	 * @param lineCount
	 * @param lineHeight
	 * @param sourcecodegraph
	 */
	public void init(int lineCount, int lineHeight, List<IDirectedGraphExt> sourcecodegraphs){
		this.lineHight = lineHeight;
		
		/* first initialization */
		if(rootFigure.getChildren().size() == 0){
			setContents(rootFigure);
		}

		rootFigure.removeAll(); //TODO: update only changed graphs
		
		/* add line selector */
		rootFigure.add(lineSelector);
		
		/* Add Dr. Garbage image */
		rootFigure.add(label);
		
		int x = 0;
		int y = 0;
		if(sourcecodegraphs != null){
			for(IDirectedGraphExt graph: sourcecodegraphs){
				
				/* 
				 * The graph is empty:
				 * possible reason for that is a virtual 
				 * node in line = 1 set by compiler.
				 * Such graphs are ignored by visualizer.  
				 */
				if(graph.getNodeList().size() == 0){
					continue;
				}
				
				/* check the size of the Graph */
				if(graph.getNodeList().size() > WARNING_MAX_GRAPH_NODE_COUNT 
						|| graph.getEdgeList().size()	> WARNING_MAX_GRAPH_EDGE_COUNT){ 
						
					StringBuffer buf = new StringBuffer("The Graph '");
					/* get graph name */
					Object o = graph.getUserObject();
					if(o != null){
						String userInfo[] = (String[])o;
						buf.append(userInfo[0]);  
					}
					 buf.append("' is very large. It has ");
					 buf.append(graph.getNodeList().size());
					 buf.append(" nodes and ");
					 buf.append(graph.getEdgeList().size());
					 buf.append(" edges. ");
					 buf.append(CoreMessages.Bytecodevisualizer_ControlFlowGraphEditorMaxSizeReached);

					boolean b = Messages.openConfirm(buf.toString());
					if(!b){
						continue;
					}
				}
				
				SourceCodeGraphFigure controlFlowGraphFigure = new SourceCodeGraphFigure(lineCount, lineHeight, graph);

				/* get user info including:
				 * - method name
				 * - method descriptor
				 */
				Map<String, Object> attr = graph.getUserObject();

				String methodName = (String) attr.get(ByteCodeConstants.NAME);
				//String methodDescription = (String) attr.get(ByteCodeConstants.DESCRIPTOR);

				/* find constructor */
				if(methodName.startsWith("<") && methodName.endsWith(">")){
					constructorGraphsList.add(controlFlowGraphFigure);
				}
				else{
					methodGraphsList.add(controlFlowGraphFigure);
				}

				/* check if an anonymous class */
				String classReference = (String) attr.get(ByteCodeConstants.Class_retrieved_from);
				int nestedIndex = isAnonumus(classReference);
				if(nestedIndex != 0){
					x = nestedIndex * 50;
				}
				else{
					x = 0;
				}

				/* 
				 * All figures added to the (0, 0) - location
				 * But the graph is synchronized with the lines.
				 */
				controlFlowGraphFigure.setLocation(new Point(x, y));

				/* set line height if the property has been changed */
				controlFlowGraphFigure.setLineHight(lineHight);

				controlFlowGraphFigure.documentUpdated(null);
				rootFigure.add(controlFlowGraphFigure);	

				if( w < controlFlowGraphFigure.getSize().width + x){
					w = controlFlowGraphFigure.getSize().width + x;
				}				
			}
		}
		else{
			Messages.warning(SourcecodeVisualizerMessages.GraphNotGenerated_ClassFileNotCompiled);
		}
		
		/* update size */
		setHeightByLineCount(lineCount);
		
		/* set graph visibility */
		setVisibleGraphsRepresentingConstructors(showContructorGraphs);
		setVisibleGraphsRepresentingMethods(showMethodGraphs);
	}
	
	private static int isAnonumus(String text){
		String[] array = text.split("\\$");
		int i = 0;
		for(String s: array){
			if(isDigits(s)){
				i++;
			}
		}
		
		return  i;
	}
	
	   private static boolean isDigits(String str) {
	        if ((str == null) || (str.length() == 0)) {
	            return false;
	        }
	        for (int i = 0; i < str.length(); i++) {
	            if (!Character.isDigit(str.charAt(i))) {
	                return false;
	            }
	        }
	        return true;
	    }
	
	/**
	 * Gets the width of the root figure.
	 * @return width
	 */
	public int getFigureWidth(){
		return rootFigure.getSize().width;
	}

	/**
	 * Gets the height of the root figure.
	 * @return height
	 */
	public int getFigureHeight(){
		return rootFigure.getSize().height;
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#setBackground(org.eclipse.swt.graphics.Color)
	 */
	public void setBackground (Color color) {
		super.setBackground(color);
	}

	/**
	 * Sets Height of the canvas. This method is only used
	 * to actualize the height during editing of the document.
	 * @param lineCount
	 */
	public void setHeightByLineCount(int lineCount){
		h = lineCount * lineHight;
		rootFigure.setSize(w, h);
	}
	
	/**
	 * Returns the root figure.
	 * @return root figure
	 */
	public Figure getRootFigure() {
		return rootFigure;
	}

	/**
	 * Gets the color of the line selctor.
	 * @return the color of the line selector.
	 */
	public Color getLineSelectorColor() {
		return lineSelectorColor;
	}

	/**
	 * Sets the color of the line selector.
	 * @param lineSelectorColor the lineSelectorColor to set.
	 */
	public void setLineSelectorColor(Color lineSelectorColor) {
		this.lineSelectorColor = lineSelectorColor;
		lineSelector.setBackgroundColor(lineSelectorColor); //update color
	}	

	/**
	 * Sets visible or un-visible all graphs representing constructors. 
	 * @param b
	 */
	public void setVisibleGraphsRepresentingConstructors(boolean b){
		showContructorGraphs = b;
		for( Figure f: constructorGraphsList){
			f.setVisible(b);
		}		
	}

	/**
	 * Sets visible or un-visible all graphs representing methods. 
	 * @param b
	 */
	public void setVisibleGraphsRepresentingMethods(boolean b){
		showMethodGraphs = b;
		for( Figure f: methodGraphsList){
			f.setVisible(b);
		}
	}
}
