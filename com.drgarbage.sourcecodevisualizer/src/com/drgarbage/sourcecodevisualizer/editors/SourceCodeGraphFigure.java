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

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.graph.Edge;

import com.drgarbage.controlflowgraph.figures.FigureFactory;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;

/**
 * Embeded Control Flow Graph Viewer. It used for representation of method control flow graphs
 * from a text editor. The nodes of the control flow graphs are synchronized with method lines 
 * in the text editor.
 *
 * @version $Revision$
 * $Id: SourceCodeGraphFigure.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class SourceCodeGraphFigure extends com.drgarbage.draw2d.ControlFlowGraphFigure {

	IDirectedGraphExt sourcecodegraph = null;

	private int lineCount = 100;

	public SourceCodeGraphFigure(int lineCount, int lineH, IDirectedGraphExt graph) {
		super(lineH);
		this.lineCount = lineCount;
		sourcecodegraph = graph;
	}

	/**
	 * Creates control flow graphs and adds they to the view.
	 */
	protected void createControlFlowGraphs() {
		/* check the size of the Graph */
		if(!checkGraphSize(sourcecodegraph, 
				WARNING_MAX_GRAPH_NODE_COUNT/8, 
				WARNING_MAX_GRAPH_EDGE_COUNT/8))
		{
			return;
		}

		visualizeGraph(sourcecodegraph);
	}

	/**
	 * Update x and y-positions of the nodes.
	 */	
	protected void updateNodePositions(){
		List<?> children = verticesLayer.getChildren();
		IFigure f = null;
		for(Object o : children){
			f = (IFigure)o;
			f.setLocation(new Point(f.getClientArea().x + Math.abs(minX) + xMargin, f.getClientArea().y));
		}
			
		children = routingObjectsLayer.getChildren();
		for(Object o : children){
			f = (IFigure)o;
			f.setLocation(new Point(f.getClientArea().x + Math.abs(minX) + xMargin, f.getClientArea().y ));
		}
	}
	
	/**
	 * Set Size of the control flow graph viewer
	 */
	protected void setSize(){
		int height = (lineCount)* lineHight;
		lineWight = maxX - minX + xMargin * 2;

		if(lineWight < minWight)
			lineWight = minWight;

		this.setSize(lineWight, height);
	}
	
	/**
	 * Sets height.
	 * @param lineCount
	 */
	public void setHeightByLineCount(int lineCount){
		this.lineCount = lineCount;
		setSize();
	}

	/**
	 * Create an visual edges of the control flow graph.
	 * @param the edge of the control flow graph, {@link Edge}
	 */
	@Override
	protected void createBackEdge(IEdgeExt edge) {

		INodeExt sourceNode = edge.getSource();
		INodeExt targetNode = edge.getTarget();

		IFigure f1 = sourceNode.getFigure();
		IFigure f2 = targetNode.getFigure();
		
		/* target node is not a decigion node  and source node is not a start node of a constructor */
		if(targetNode.getOutgoingEdgeList().size() != 2 && sourceNode.getIncomingEdgeList().size() != 0){
			super.createBackEdge(edge);
			return;
		}

		PolylineConnection connection = new PolylineConnection();
		PolygonDecoration arrow = new PolygonDecoration();
		double arrowHight = lineHight / 5;
		arrow.setScale(arrowHight, arrowHight / 3);

		/* arrow at target endpoint */
		connection.setTargetDecoration(arrow);

		/* sorce Anchor */
		AbstractConnectionAnchor sourceAnchor = null;

		sourceAnchor = new ChopboxAnchor(f1);		
		connection.setSourceAnchor(sourceAnchor);

		/* target anchor */
		AbstractConnectionAnchor targetAnchor = null;
		targetAnchor = new ChopboxAnchor(f2);
		connection.setTargetAnchor(targetAnchor);

		/* start node of the constructor */
		if(sourceNode.getIncomingEdgeList().size() == 0){
			routedConnectionsLayer.add(connection);
			return;
		}
		
		/* routing */
		//if(sourceNode.getX() >= targetNode.getX() ){
			ArrayList<Bendpoint> list = new ArrayList<Bendpoint>();	

			RelativeBendpoint bp = new RelativeBendpoint();
			bp.setConnection(connection);
			bp.setWeight(0.0f);
			bp.setRelativeDimensions(new Dimension(-sourceNode.getWidth()*3/4, 0), new Dimension(0, 0));
//			AbsoluteBendpoint bp = new AbsoluteBendpoint(sourceNode.getX() + sourceNode.getWidth()+ sourceNode.getWidth()/2, 
//					                                     sourceNode.getY() + sourceNode.getHeight()/2);			
			list.add(bp);				
			router.setConstraint(connection, list);
			routedConnectionsLayer.add(connection);

			/* workaround: set unvisible routing objects */
			IFigure f = FigureFactory.createRectangularVertex(false);
			f.setBackgroundColor(ColorConstants.white);
			f.setVisible(false); //set true for testing
			f.setLocation(new Point(bp.getLocation().x - sourceNode.getWidth()/4, sourceNode.getY()));
			//f.setLocation(new Point(sourceNode.getX() - sourceNode.getWidth()/4, sourceNode.getY()));
			int w = sourceNode.getWidth() + sourceNode.getWidth()/2;
			f.setSize(w, targetNode.getHeight() * 2 + 2);
			routingObjectsLayer.add(f);	
			
			
		//}
	}

	/**
	 * @return the sourcecode graph
	 */
	public IDirectedGraphExt getSourcecodegraph() {
		return sourcecodegraph;
	}

	/**
	 * @param the sourcecode graph to set
	 */
	public void setSourcecodegraphs(IDirectedGraphExt graph) {
		this.sourcecodegraph = graph;
	}
}
