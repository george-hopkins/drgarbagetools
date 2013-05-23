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

package com.drgarbage.bytecodevisualizer.editors;

import java.util.List;

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

import com.drgarbage.asm.render.intf.IClassFileDocument;
import com.drgarbage.asm.render.intf.IDocumentUpdateListener;
import com.drgarbage.asm.render.intf.IMethodSection;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.core.img.CoreImg;
import com.drgarbage.draw2d.ControlFlowGraphFigure;
import com.drgarbage.utils.Messages;


/**
 * Canvas for control flow graph figure.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: ControlFlowGraphCanvas.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class ControlFlowGraphCanvas extends FigureCanvas implements IDocumentUpdateListener {

	/* weight and height of the canvas after initialization */
	private int w = 200;
	private int h = 200;

	/**
	 * Reference to the editor.
	 */
	IClassFileEditor classFileEditor;

	/**
	 * Am empty root figure.
	 */
	private Figure rootFigure = new Figure();
	
    /**
     * The height of the line, default 17 pixel.
     */
	private int lineHight = 17;
	
    /**
     * The weight of the line, default 500 pixel.
     */
    private int lineWight = 500;	
	
    /**
     * Line-Selector  Object.
     */
    private final Figure lineSelector = new Figure();
    
	/**
	 * The color of the line selector.
	 */
	private Color lineSelectorColor = new Color(null,  232, 242, 254);
	
	/**
	 * BasicBlck View or Bytecode Graph.
	 */
	private boolean visibleLayer = false;

	/**
	 * Constructor.
	 * NOTE: the object has to be initialized. see ControlFlowGraphCanvas.init();
	 * @param parent
	 */
	public ControlFlowGraphCanvas(Composite parent, IClassFileEditor editor) {
		super(parent);
		classFileEditor = editor;

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
		rootFigure.add(lineSelector);
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
	 * Returns the curent location of the line selector.
	 * @return location
	 */
	public Point getSelectedLineLocation(){
		return lineSelector.getLocation();
	}

	/**
	 * Initialize the canvas object before use.
	 * @param lineHeght
	 * @param classFileDocument
	 */
	public void init(int lineHeght, IClassFileDocument classFileDocument){
		this.lineHight = lineHeght;

		rootFigure.removeAll();
		createLineSelector();
		
		if (classFileDocument != null) {
			List<IMethodSection> methods = classFileDocument.getMethodSections();
			for(IMethodSection m : methods){
				if(m.hasCode()){

					ControlFlowGraphFigure controlFlowGraphFigure = new ControlFlowGraphFigure(lineHeght, m);
					controlFlowGraphFigure.setLocation(new Point(0, m.getFirstLine() * lineHeght));
					rootFigure.add(controlFlowGraphFigure);
					
					if( w < controlFlowGraphFigure.getSize().width){
						w = controlFlowGraphFigure.getSize().width;
					}
				}
			}
		}

		addHeader();

		LineBorder b = new LineBorder();
		b.setColor(ColorConstants.white);
		rootFigure.setBorder(b);
		
		setContents(rootFigure);

		if (classFileDocument == null) {
			h = lineHeght;
		}
		else {
			h = (classFileDocument.getLineCount() + 1) * lineHeght;
		}
		rootFigure.setSize(w, h);
	}

	/**
	 * Add Dr. Garbage image.
	 */
	private void addHeader(){
		Label l = new Label();
		Image image = CoreImg.labelDrGarbage_148x30.createImage();
		l.setIcon(image);
		l.setSize(new Dimension(148, 30));
		l.setLocation(new Point(5,5));
		rootFigure.add(l);
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

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.render.intf.IDocumentUpdateListener#documentUpdated(com.drgarbage.asm.render.intf.IClassFileDocument)
	 */
	public void documentUpdated(IClassFileDocument classFileDocument){
		/* Redraw control flow graph figure. */
		init(lineHight, classFileDocument);
	}
	
	/**
	 * Returns true if the basicblock view is active.
	 * @return true or false
	 */
	public boolean isBasicblockViewActive(){
		return visibleLayer;
	}
	
	/**
	 * Sets visible basicblock graph or bytecode layer.
	 */
	public void changeGraphView(){
		changeGraphView(visibleLayer);
	}

	/**
	 * Sets visible basicblock graph or bytecode layer.
	 */
	public void changeGraphView(boolean b){
        if(b){
        	viewByteCodeGraph();
        	visibleLayer = false;
        }
        else{
        	viewBasicBlockGraph();
        	visibleLayer = true;
        }
	}
	
	/**
	 * Activates the Bytecode View in all graphs
	 */	
	private void viewByteCodeGraph(){
		List<?> children = rootFigure.getChildren();
		ControlFlowGraphFigure f = null;
		for(Object o : children){
			if(o instanceof ControlFlowGraphFigure){
				f = (ControlFlowGraphFigure)o;
				f.viewByteCodeGraph();
			}
			
		}
	}
	
	/**
	 * Activates the Basic Block View in all graphs
	 */
	private void viewBasicBlockGraph(){
		List<?> children = rootFigure.getChildren();
		ControlFlowGraphFigure f = null;
		for(Object o : children){
			if(o instanceof ControlFlowGraphFigure){
				f = (ControlFlowGraphFigure)o;
				f.viewBasicBlockGraph();
			}
			
		}
	}
	
	/**
	 * Sets the heght of the line.
	 * @param lineHight the lineHight to set.
	 */
	public void setLineHight(int lineHight) {
		this.lineHight = lineHight;
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
	 * Returns the current height of the line selector.
	 * @return height
	 */
	public int getLineHight() {
		return lineHight;
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#setVisible(boolean)
	 */
	public void setVisible (boolean visible) {
		if(classFileEditor.getClassFileEditor().isGraphViewVisible()){
			super.setVisible(visible);	
		}
		else{
			super.setVisible(false);
		}		
	}
}
