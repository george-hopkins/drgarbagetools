/**
 * Copyright (c) 2008-2013, Dr. Garbage Community
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

package com.drgarbage.controlflowgraphfactory.compare.actions;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.drgarbage.controlflowgraph.figures.DecisionVertexFigure;
import com.drgarbage.controlflowgraph.figures.RectangleFigure;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryPlugin;
import com.drgarbage.visualgraphic.model.VertexBase;

/**
 * Describes mouse events, to highlight mapped nodes
 * according to isomorphism algorithms
 * 
 * @author Artem Garishin
 * @version $Revision:$
 * $Id:$
 */
public class CompareMouseActions extends MouseAdapter implements  MouseListener, MouseMotionListener{

	/* Color constants */
	final static Color RED      		= new Color(null, 224, 0, 0);
	final static Color GREEN      		= new Color(null, 0, 224, 0);
	final static Color BLUE      		= new Color(null, 118, 178, 255);
	final static Color YELLOW      		= new Color(null, 255, 255, 0);
	final static Color DEFAULT      	= new Color(null, 0, 224, 0);
	
	/*define mapped nodes and figure which needs to attach mouse events*/
	public Map<INodeExt, INodeExt> MapEntry;
	public IFigure freeFormViewport;
	public boolean panel;
	
	/*Constructor*/
	public CompareMouseActions(Map<INodeExt, INodeExt> MapEntry, IFigure myFigure, boolean panel){
		this.MapEntry = MapEntry;
		this.freeFormViewport = myFigure;
		this.panel = panel;
	}
	
	public void addMouseListener(){
		freeFormViewport.addMouseListener(this);
	}
	public void removeListener(){
		freeFormViewport.removeMouseListener(this);
	}
	
	public void addMotionMouseListener(){
		freeFormViewport.addMouseMotionListener(this);
	}
	public void removeMotionListener(){
		freeFormViewport.removeMouseMotionListener(this);
	}
	
	/**
	 * Mouse Listeners methods 
	 */
	public void mouseDoubleClicked(MouseEvent arg0) {
	  
		Point p = arg0.getLocation();
		IFigure foundFigure = freeFormViewport.findFigureAt(p);
		IFigure figure = foundFigure.getParent();
		/*check if found figure belongs to shape */
		if(figure instanceof Shape){
	    
			try{
				VertexBase foundVertexBase = identifyMappedVertexBase((Shape)figure, MapEntry);
				if(foundVertexBase != null){
					foundVertexBase.setColor(DEFAULT);
					figure.setBackgroundColor(DEFAULT);
				}
			}
			catch(Exception e){
				ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, ControlFlowFactoryPlugin.PLUGIN_ID, CoreException.class.getName(), e));
			}
			
		}
		
		/*clear marking double clicking on panel*/
		if(foundFigure instanceof FreeformViewport){
			for (Map.Entry<INodeExt, INodeExt> entry : this.MapEntry.entrySet()){
				((VertexBase) entry.getKey().getData()).setColor(DEFAULT);
				((VertexBase) entry.getValue().getData()).setColor(DEFAULT);
			}
		}
			
	}

	public void mousePressed(MouseEvent arg0) {
		Point p = arg0.getLocation();
		IFigure foundFigure = freeFormViewport.findFigureAt(p);
		
		IFigure figure = foundFigure.getParent();
		/*check if found figure belongs to shape */
		if(figure instanceof Shape){
	    
			try{
				VertexBase foundVertexBase = identifyMappedVertexBase((Shape)figure, MapEntry);
				if(foundVertexBase != null){
					foundVertexBase.setColor(BLUE);
					figure.setBackgroundColor(BLUE);
				}
			}
			catch(Exception e){
				ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, ControlFlowFactoryPlugin.PLUGIN_ID, CoreException.class.getName(), e));
			}
			
		}
	}

	public void mouseReleased(MouseEvent arg0) {	
	}
	
	/**
	 * Mouse Motion Listeners methods 
	 */
	public void mouseDragged(MouseEvent arg0) {
		
	}

	public void mouseEntered(MouseEvent arg0) {
		
		
	}

	public void mouseExited(MouseEvent arg0) {
		
		
	}

	public void mouseHover(MouseEvent arg0) {
		Point p = arg0.getLocation();
		IFigure foundFigure = freeFormViewport.findFigureAt(p);
		IFigure figure = foundFigure.getParent();
		
		/*check if found figure is shape*/
		if(figure instanceof Shape){
			
			try{
				VertexBase foundVertexBase = identifyMappedVertexBase((Shape)figure, MapEntry);
				if(foundVertexBase != null){
					foundVertexBase.setColor(YELLOW);
					figure.setBackgroundColor(YELLOW);
				}
			}
			catch(Exception e){
				ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, ControlFlowFactoryPlugin.PLUGIN_ID, CoreException.class.getName(), e));
			}
		}
		
	}

	public void mouseMoved(MouseEvent arg0) {
			
	}
	
	/**
	 * gets a found figure from left or right viewer, 
	 * returns a corresponding mapped node to be later highlighted 
	 * 
	 * return null if vertexBase is not found
	 * @param Shape
	 * @param MapEntry
	 */
	public VertexBase identifyMappedVertexBase(Shape rectangleFigure, Map<INodeExt, INodeExt> MapEntry){
		
		/*get figure locations */
		int figurex = rectangleFigure.getBounds().x;
		int figurey = rectangleFigure.getBounds().y;
		int figureHeight = rectangleFigure.getBounds().height; 
		int figureWidth = rectangleFigure.getBounds().width;
		if(this.panel){
		/*find corresponding vertexBase node according figure location*/
			for (Map.Entry<INodeExt, INodeExt> entry : MapEntry.entrySet()) {
				
				/*get each location of mapped vertexBase*/
				VertexBase vb = ((VertexBase) entry.getKey().getData());
				int vbx = vb.getLocation().x;
				int vby = vb.getLocation().y;
				int vbSizeHeight = vb.getSize().height;
				int vbSizeWidth = vb.getSize().width;
		
				/*identify vertexBase by location of figure*/
				if(figurex == vbx && figurey == vby && figureHeight == vbSizeHeight && figureWidth == vbSizeWidth){
					return (VertexBase) entry.getValue().getData();
				}
				
			}
		}else
		{
			/*find corresponding vertexBase node according figure location*/
			for (Map.Entry<INodeExt, INodeExt> entry : MapEntry.entrySet()) {
				
				/*get each location of mapped vertexBase*/
				VertexBase vb = ((VertexBase) entry.getValue().getData());
				int vbx = vb.getLocation().x;
				int vby = vb.getLocation().y;
				int vbSizeHeight = vb.getSize().height;
				int vbSizeWidth = vb.getSize().width;
		
				/*identify vertexBase by location of figure*/
				if(figurex == vbx && figurey == vby && figureHeight == vbSizeHeight && figureWidth == vbSizeWidth){
					return (VertexBase) entry.getKey().getData();
				}
			}
		}
		return null;
	}
	
	/**
	 * find object IFigure
	 * @param vertex
	 * @return
	 */
	public IFigure idenifyFigure(VertexBase vertex){
		IFigure figure = null;
		figure = freeFormViewport.findFigureAt(vertex.getLocation().x, vertex.getLocation().y);
		return  figure;
	}

}
