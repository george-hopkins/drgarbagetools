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

package com.drgarbage.draw2d;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;

import com.drgarbage.algorithms.BasicBlockGraphVisitor;
import com.drgarbage.algorithms.ByteCodeSimpleLayout;
import com.drgarbage.asm.render.intf.IInstructionLine;
import com.drgarbage.asm.render.intf.IMethodSection;
import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.ControlFlowGraphGenerator;
import com.drgarbage.controlflowgraph.anchors.DecisionAnchor;
import com.drgarbage.controlflowgraph.anchors.SimpleAnchor;
import com.drgarbage.controlflowgraph.figures.FigureFactory;
import com.drgarbage.controlflowgraph.intf.GraphUtils;
import com.drgarbage.controlflowgraph.intf.IBasicBlock;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;
import com.drgarbage.controlflowgraph.intf.INodeType;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.utils.Messages;

/**
 * Embeded Control Flow Graph Viewer. It used for representation of method control flow graphs
 * from a text editor.The nodes of the control flow graphs are synchronized with method lines 
 * in the text editor.
 *
 * @author Sergej Alekseev
 * @version $Revision:25 $
 * $Id:ControlFlowGraphFigure.java 235 2007-06-22 18:48:35Z aleks $
 */
public class ControlFlowGraphFigure extends LayeredPane {

	private final int WARNING_MAX_GRAPH_NODE_COUNT = 3000;
	private final int WARNING_MAX_GRAPH_EDGE_COUNT = 1000;

    /**
     * The height of the line, default 17 pixel.
     */
	protected int lineHight = 17;
	
	/**
	 * THe count of lines in the document.
	 */
	private int lineCount = -1;

    /**
     * The wight of the line, default 500 pixel.
     */
    protected int lineWight = 500;

    /**
     * Minimal height of the line.
     */
    protected final int minWight = 200;

	/**
	 * Reference to the Method Object including instruction list
	 * and other information needed for visualization.
	 */
	private IMethodSection methodRef = null;

	/**
	 * min x coordinate.
	 */
	protected int minX = 0;

	/**
	 * max x coordinate.
	 */
	protected int maxX = 0;

	/**
	 * x-margin for the graph picture.
	 */
	protected int xMargin = 40;

	
	/**
	 * Layers for the used stack layout
	 */
	protected Layer verticesLayer = null;
	protected Layer routingObjectsLayer = null;
	protected ConnectionLayer routedConnectionsLayer = null;
	protected Layer notRoutetConnectionsLayer = null;

	/**
	 * Connection Router.
	 */
	protected ShortestPathConnectionRouter router = null;
	
	/**
	 * Create control flow graph view.
	 * @param the size of the line in the text editor.
	 * @param array of synchronized instructions, see {@link ILineInstructionSynchronizer}.
	 */
	public ControlFlowGraphFigure(int lineH, IMethodSection method) {
		super();

		/* set hight */
		lineHight = lineH;

        /* set background */
        this.setBackgroundColor(ColorConstants.white);
        
        /* non-transparent figure */
        this.setOpaque(false);

        /* update document */
        documentUpdated(method);
	}

	public ControlFlowGraphFigure(int lineH) {
		super();

		/* set hight */
		lineHight = lineH;

        /* set background */
        this.setBackgroundColor(ColorConstants.white);
        
        /* non-transparent figure */
        this.setOpaque(false);
	}

	/**
	 * Activates the view and makes it visible.
	 */
	public void activateView(){
		setEnabled(true);
		setVisible(true);
	}

	/**
	 * De-activates the view and makes it unvisible.
	 */
	public void deactivateView(){
		setVisible(false);
		setEnabled(false);
	}
	
	/**
	 * Sets visible bytecode graph layer.
	 */
	public void viewByteCodeGraph(){
        verticesLayer.setVisible(true);
        routingObjectsLayer.setVisible(false);
	}

	/**
	 * Sets visible basicblock graph layer.
	 */
	public void viewBasicBlockGraph(){
        verticesLayer.setVisible(false);
        routingObjectsLayer.setVisible(true);
	}

	/**
	 * Redraw graphics.
	 */
	public void documentUpdated(IMethodSection method){
		if(method != null){
			methodRef = method;
			lineCount = method.getLastLine() + method.getFirstLine();
		}
		
		documentUpdated();
	}
	
	/**
	 * Redraw graphics.
	 */
	private void documentUpdated(){
		this.removeAll();

		/* create visual layers */
		verticesLayer = new Layer();
		routingObjectsLayer = new Layer();
		routedConnectionsLayer = new ConnectionLayer();
		notRoutetConnectionsLayer = new Layer();

		this.add(verticesLayer);
	    this.add(routedConnectionsLayer );
        this.add(notRoutetConnectionsLayer);
        this.add(routingObjectsLayer);
        
        viewByteCodeGraph();
        
	    router = new ShortestPathConnectionRouter( routingObjectsLayer );
	    routedConnectionsLayer.setConnectionRouter( router );
	    
	    /*	    
  		set edge routing space for the common font sizes:
	      8  - 15 pixel  - space 2
	      9  - 16 pixel  - space 2  (routing changed)
	     10  - 17 pixel  - space 3
	     11  - 18 pixel  - space 3
	     12  - 19 pixel  - space 4  (routing changed)
	     14  - 22 pixel  - space 4  (routing changed)
	     16  - 24 pixel  - space 4  (routing changed)
	     18  - 28 pixel  - space 5
	     20  - 31 pixel  - space 5
	     22  - 34 pixel  - space 5
	     24  - 37 pixel  - space 5
	     ...
	     other - space 5
	     */

	    int space = 4;
	    switch(lineHight){
	    	case 15:
	    	case 16:
	    		space = 2;
	    		break;
	    	case 17:
	    	case 18:
	    		space = 3;
	    		break;
	    	case 19:
	    	case 22:
	    	case 24:
	    		space = 4;
	    		break;
	    	case 28:
	    	case 31:
	    	case 34:
	    	case 37:
	    		space = 5;
	    		break;
	    }
	    router.setSpacing(space);

	    createControlFlowGraphs();

		updateNodePositions();
		
		setSize();
	}
	
	/**
	 * Update x and y-positions of the nodes.
	 */
	protected void updateNodePositions(){
		int offsetY = (methodRef.getFirstLine() + 1) * lineHight;
		List<?> children = verticesLayer.getChildren();
		IFigure f = null;
		for(Object o : children){
			f = (IFigure)o;
			f.setLocation(new Point(f.getClientArea().x + Math.abs(minX) + xMargin, f.getClientArea().y - offsetY));
		}
			
		children = routingObjectsLayer.getChildren();
		for(Object o : children){
			f = (IFigure)o;
			f.setLocation(new Point(f.getClientArea().x + Math.abs(minX) + xMargin, f.getClientArea().y - offsetY));
		}
	}

	/**
	 * Returns the height of the line.
	 * @return the lineHight
	 */
	public int getLineHight() {
		return lineHight;
	}

	/**
	 * Sets the heght of the line.
	 * @param lineHight the lineHight to set.
	 */
	public void setLineHight(int lineHight) {
		this.lineHight = lineHight;
	}

	/**
	 * Set Size of the control flow graph viewer
	 */
	protected void setSize(){
        int height = (lineCount + 1) * lineHight;
        lineWight = maxX - minX + xMargin * 2;
        
        if(lineWight < minWight)
        	lineWight = minWight;
        
        this.setSize(lineWight, height);
	}
	
	/**
	 * Creates control flow graphs and adds they to the view.
	 */
	protected void createControlFlowGraphs() {
		IDirectedGraphExt graph = null;
		List<IInstructionLine> instructions = null;
		instructions = methodRef.getInstructionLines();
		graph = ControlFlowGraphGenerator.generateSynchronizedControlFlowGraphFrom(instructions);

		/* check the size of the Graph */
		if(graph.getNodeList().size() > WARNING_MAX_GRAPH_NODE_COUNT 
			|| graph.getEdgeList().size()	> WARNING_MAX_GRAPH_EDGE_COUNT){ 
			
			
			StringBuffer buf = new StringBuffer("The Graph '");
			
			/* get graph name */
			if(methodRef!= null){
				buf.append(methodRef.getName()); 
			}
			 buf.append("' is very large. It has ");
			 buf.append(graph.getNodeList().size());
			 buf.append(" nodes and ");
			 buf.append(graph.getEdgeList().size());
			 buf.append(" edges. ");
			 buf.append(CoreMessages.Bytecodevisualizer_ControlFlowGraphEditorMaxSizeReached);

			
			boolean b = Messages.openConfirm(buf.toString());
			if(!b){
				return;
			}
		}
		
		
		visualizeGraph(graph);
	}

	/**
	 * Visualize the graph.
	 * @param control flow graph
	 */
	protected void visualizeGraph(IDirectedGraphExt graph){
		ByteCodeSimpleLayout byteCodeSimpleLayout = null;
		
		/* set node layout */	
		try {
			byteCodeSimpleLayout = new ByteCodeSimpleLayout(graph, lineHight);
			byteCodeSimpleLayout.visit();
		} catch (ControlFlowGraphException e) {
			e.printStackTrace(System.err);
			return;
		}
		
		/* add nodes */
		INodeExt node = null;
  		INodeListExt nodes = graph.getNodeList();
		for(int i = 0; i< nodes.size(); i++){
			node = nodes.getNodeExt(i);

			IFigure f = createVisualNode(node, node.getVertexType());
			f.setToolTip(new Label(node.getToolTipText()));
			verticesLayer.add(f);
			
			/* set figure reference */
			node.setFigure(f);
		}

		/* add edges */
		IEdgeExt edge = null;
		IEdgeListExt edges = graph.getEdgeList();
		for(int i = 0; i< edges.size(); i++){
			edge = edges.getEdgeExt(i); 
			createEdge(edge);
		}

		/* update min max values */
		if(minX > byteCodeSimpleLayout.getMinX()){
			minX = byteCodeSimpleLayout.getMinX();
		}

		if(maxX < byteCodeSimpleLayout.getMaxX()){
			maxX = byteCodeSimpleLayout.getMaxX();
		}
		
		createRoutingObjects(graph);
	}
	
	/**
	 * Creates basic blocks and put they into the routing layer.
	 * @param graph
	 */
	private void createRoutingObjects(IDirectedGraphExt graph){
		/* find basic blocks */
		GraphUtils.clearGraph(graph);
		BasicBlockGraphVisitor basicBlockVisitor = new BasicBlockGraphVisitor();
		try {
			basicBlockVisitor.visit(graph);
		} catch (ControlFlowGraphException e) {
			e.printStackTrace(System.err);
		}
		
		IDirectedGraphExt basicBlockGraph = basicBlockVisitor.getBasicBlockGraph();
		
		INodeListExt basicBlocks = basicBlockGraph.getNodeList();
		INodeListExt verticesList = null;
		INodeExt n = null;
		int bbMinX = -1;
		int bbMaxX = -1;
		int bbMinY = -1;
		int bbMaxY = -1;
		IBasicBlock bb = null;
		for(int j = 0; j < basicBlocks.size(); j++){
			bb = (IBasicBlock)basicBlocks.getNodeExt(j);	
			verticesList = bb.getBasicBlockVertices();
			
			n = verticesList.getNodeExt(0);
			bbMinX = n.getX();
			bbMinY = n.getY();
			bbMaxX = n.getX();
			bbMaxY = n.getY();

			for(int i = 1; i< verticesList.size(); i++){
				n = verticesList.getNodeExt(i);
				
				if(bbMinX > n.getX()){
					bbMinX = n.getX();
				}
				
				if(bbMaxX < n.getX()){
					bbMaxX = n.getX();
				}

				if(bbMinY > n.getY()){
					bbMinY = n.getY();
				}
				
				if(bbMaxY < n.getY()){
					bbMaxY = n.getY();
				}
			}

			n.getWidth();
			
			bb.setX(bbMinX);
			bb.setY(bbMinY);
			bb.setHeight(bbMaxY - bbMinY + n.getHeight());
			bb.setWidth(bbMaxX - bbMinX + n.getWidth());
			
			StringBuffer buf = new StringBuffer(" ");
			buf.append(bb.getData().toString());
			INodeListExt basicBlockvertices = bb.getBasicBlockVertices();
			for(int k = 0; k < basicBlockvertices.size(); k++){
				buf.append("\n ");
				buf.append(basicBlockvertices.getNodeExt(k).getByteCodeOffset());
				buf.append(" - ");
				buf.append(basicBlockvertices.getNodeExt(k).getByteCodeString());
				buf.append(" ");
			}
			
			IFigure f = FigureFactory.createSimpleBasicBlockVertex(buf.toString(), false);
			f.setLocation(new Point(bb.getX(), bb.getY()));
			f.setSize(bb.getWidth(), bb.getHeight());
			
			
			
			//f.setToolTip(new Label(buf.toString()));

			
			routingObjectsLayer.add(f);
		}
	}
	
	/**
	 * Create verticesLayer for nodes of the control flow graph.
	 * @param the node of the control flow graph, {@link Node}
	 * @param the node type
	 */
	protected IFigure createVisualNode(INodeExt node, int nodeType){
		IFigure f = null;

  		switch(nodeType){
	  		case INodeType.NODE_TYPE_SIMPLE:	
		  						f = FigureFactory.createRectangularVertex(false);
		  						break;
	  		case INodeType.NODE_TYPE_IF:
	  							f = FigureFactory.createDecisionVertex(false);
	  							break;
	  		case INodeType.NODE_TYPE_RETURN:
	  							f = FigureFactory.createReturnVertex(false);
	  							break;
	  		case INodeType.NODE_TYPE_GOTO_JUMP: 
	  							f = FigureFactory.createGotoJumpVertex(false);
	  							break;
	  		case INodeType.NODE_TYPE_SWITCH:
	  							f = FigureFactory.createSwitchVertex(false);
	  							
	  							/* routing objects */
	  							IFigure f2 = FigureFactory.createRectangularVertex(false);
	  							f2.setBackgroundColor(ColorConstants.white);
	  							f2.setVisible(false);

	  							IEdgeListExt edges = node.getOutgoingEdgeList();
	  							int x1 = edges.getEdgeExt(0).getTarget().getX();
	  							int x2 = edges.getEdgeExt(edges.size() -1 ).getTarget().getX();
	  							f2.setLocation(new Point(x1, node.getY()));
	  							f2.setSize(x2 - x1, node.getHeight() * 2 + 2 );

	  							routingObjectsLayer.add(f2);

	  							
	  							break;
	  		case INodeType.NODE_TYPE_INVOKE:
	  							f = FigureFactory.createInvokeVertex(false);
	  							break;
	  		case INodeType.NODE_TYPE_GET:
								f = FigureFactory.createGetVertex(false);
								break;
			default:
								f = FigureFactory.createRectangularVertex(false);
								break;
  		}
  		
		f.setSize(node.getWidth(),node.getHeight());
		f.setLocation(new Point(node.getX(),node.getY()));

  		return f;
	}
 	
	/**
	 * Create an visual edges of the control flow graph.
	 * @param the edge of the control flow graph, {@link Edge}
	 */
	protected void createEdge(IEdgeExt edge) {		
		INodeExt sourceNode = edge.getSource();
		INodeExt targetNode = edge.getTarget();
	
		/* create backedge */
		if(sourceNode.getByteCodeOffset() > targetNode.getByteCodeOffset()){
			createBackEdge(edge);
			return;
		}
		
		IFigure f1 = sourceNode.getFigure();
		IFigure f2 = targetNode.getFigure();
		
		PolylineConnection connection = new PolylineConnection();
		PolygonDecoration arrow = new PolygonDecoration();
		double arrowHight = lineHight / 5;
		arrow.setScale(arrowHight, arrowHight / 3);
		
		connection.setTargetDecoration(arrow); // arrow at target endpoint
		
		AbstractConnectionAnchor sourceAnchor = null;
		
  		switch(sourceNode.getVertexType()){
  		case INodeType.NODE_TYPE_IF:
  				ArrayList<RelativeBendpoint> list = new ArrayList<RelativeBendpoint>();	
  			
  				IEdgeExt e2 = null;
  				IEdgeListExt edgeList = sourceNode.getOutgoingEdgeList();
  				if(edgeList.getEdgeExt(0).equals(edge)){
  					e2 = edgeList.getEdgeExt(1);
  				}
  				else {
  					e2 = edgeList.getEdgeExt(0);
  				}
  				
  				if(edge.getTarget().getX() >= e2.getTarget().getX()){
  					sourceAnchor = new DecisionAnchor(f1);
	  	  			RelativeBendpoint bp = new RelativeBendpoint();
	  				bp.setConnection(connection);
	  				list.add(bp);
	  				bp.setWeight(0.0f);
	  				bp.setRelativeDimensions(new Dimension(sourceNode.getWidth()/2 + sourceNode.getWidth()/4 - 2, 0), 
				                 new Dimension(0, 0));
  				}
  				else {
  					sourceAnchor = new DecisionAnchor(f1);
 					
  		  			RelativeBendpoint bp = new RelativeBendpoint();
	  				bp.setConnection(connection);
	  				list.add(bp);
	  				bp.setWeight(0.0f);
	  				bp.setRelativeDimensions(new Dimension(-sourceNode.getWidth()/2 - sourceNode.getWidth()/4 + 2, 0), 
	  						                 new Dimension(0, 0));

  					/* workaround: set unvisible routing objects */
					IFigure f = FigureFactory.createRectangularVertex(false);
					f.setBackgroundColor(ColorConstants.white);
					f.setVisible(false); //set true for testing
					f.setLocation(new Point(sourceNode.getX() - sourceNode.getWidth()/2, sourceNode.getY() - sourceNode.getHeight() - 2));
					f.setSize(sourceNode.getWidth() + sourceNode.getWidth(), sourceNode.getHeight() * 3 + 4 );
					routingObjectsLayer.add(f);		
  				}
 				 				
  				router.setConstraint(connection, list);
  				
  				break;
  			case INodeType.NODE_TYPE_SWITCH:
  					sourceAnchor = new SimpleAnchor(f1, 1);  					
  					break;  				
	  		case INodeType.NODE_TYPE_SIMPLE:	
	  		case INodeType.NODE_TYPE_RETURN:
	  		case INodeType.NODE_TYPE_GOTO_JUMP: 
	  		case INodeType.NODE_TYPE_INVOKE:
	  		case INodeType.NODE_TYPE_GET:
			default:
				sourceAnchor = new ChopboxAnchor(f1);	
			
				ArrayList<RelativeBendpoint> list2 = new ArrayList<RelativeBendpoint>();
		  		RelativeBendpoint bp = new RelativeBendpoint();
	  			bp.setConnection(connection);
	  			list2.add(bp);
	  			bp.setWeight(0.0f);
	  			bp.setRelativeDimensions(new Dimension(0, sourceNode.getHeight()), 
	  					                 new Dimension(0, 0));
	  			router.setConstraint(connection, list2);
				break;
  		}		
		connection.setSourceAnchor(sourceAnchor);
		
		AbstractConnectionAnchor targetAnchor = null;
  		switch(targetNode.getVertexType()){
  		case INodeType.NODE_TYPE_IF:
  				targetAnchor = new SimpleAnchor(f2, 0);
				break;
  			case INodeType.NODE_TYPE_SWITCH:			
	  		case INodeType.NODE_TYPE_SIMPLE:	
	  		case INodeType.NODE_TYPE_RETURN:
	  		case INodeType.NODE_TYPE_GOTO_JUMP: 
	  		case INodeType.NODE_TYPE_INVOKE:
	  		case INodeType.NODE_TYPE_GET:
			default:
				targetAnchor = new ChopboxAnchor(f2);			
				break;
  		}
		connection.setTargetAnchor(targetAnchor);
		
		if(sourceNode.getX() == targetNode.getX()
				&& (targetNode.getY() - sourceNode.getY()) <= lineHight * 2){

			notRoutetConnectionsLayer.add(connection);
			return;	
		}

		routedConnectionsLayer.add(connection);

	}
	
	/**
	 * Create an visual edges of the control flow graph.
	 * @param the edge of the control flow graph, {@link Edge}
	 */
	protected void createBackEdge(IEdgeExt edge) {

		INodeExt sourceNode = edge.getSource();
		INodeExt targetNode = edge.getTarget();
		
		IFigure f1 = sourceNode.getFigure();
		IFigure f2 = targetNode.getFigure();
		
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
  		switch(targetNode.getVertexType()){
  		case INodeType.NODE_TYPE_IF:
  				targetAnchor = new SimpleAnchor(f2, 0);
				break;
  			case INodeType.NODE_TYPE_SWITCH:			
	  		case INodeType.NODE_TYPE_SIMPLE:	
	  		case INodeType.NODE_TYPE_RETURN:
	  		case INodeType.NODE_TYPE_GOTO_JUMP: 
	  		case INodeType.NODE_TYPE_INVOKE:
	  		case INodeType.NODE_TYPE_GET:
			default:
				targetAnchor = new ChopboxAnchor(f2);
				break;
  		}
		connection.setTargetAnchor(targetAnchor);
		
		/* routing */
		if(sourceNode.getX() >= targetNode.getX() || 
				sourceNode.getVertexType() == INodeType.NODE_TYPE_GOTO_JUMP){/* fix of the bug with a jump vertices */
			ArrayList<RelativeBendpoint> list = new ArrayList<RelativeBendpoint>();	
	
			RelativeBendpoint bp = new RelativeBendpoint();
			bp.setConnection(connection);
			list.add(bp);
			bp.setWeight(0.0f);
			
			if(targetNode.getX() <= sourceNode.getX()){
				bp.setRelativeDimensions(new Dimension(targetNode.getX() - sourceNode.getX() - targetNode.getWidth() , 0), 
					                 new Dimension(0, 0));
			}
			else{
				bp.setRelativeDimensions(new Dimension(-sourceNode.getWidth()*3/4, 0), 
		                 new Dimension(0, 0));				
			}

			bp = new RelativeBendpoint();
			bp.setConnection(connection);
			list.add(bp);
			bp.setWeight(1.0f);
			bp.setRelativeDimensions(new Dimension(0, 0), 
					                 new Dimension(-targetNode.getWidth(), 0));
	
			router.setConstraint(connection, list);
			routedConnectionsLayer.add(connection);
	
			/* workaround: set unvisible routing objects */
			IFigure f = null;
			if(sourceNode.getVertexType() != INodeType.NODE_TYPE_GOTO_JUMP){/* fix of the bug with a jump vertices */
				f = FigureFactory.createRectangularVertex(false);
				f.setBackgroundColor(ColorConstants.white);
				f.setVisible(false); //set true for testing
				f.setLocation(new Point(targetNode.getX() - targetNode.getWidth()/4 - targetNode.getWidth()/2, targetNode.getY()));
				f.setSize(targetNode.getWidth() + targetNode.getWidth()/2, targetNode.getHeight());
				routingObjectsLayer.add(f);	
	
				f = FigureFactory.createRectangularVertex(false);
				f.setBackgroundColor(ColorConstants.white);
				f.setVisible(false); //set true for testing
				f.setLocation(new Point(targetNode.getX() - targetNode.getWidth()/4 - targetNode.getWidth()/2, sourceNode.getY()));
				int w = sourceNode.getX() - targetNode.getX() + sourceNode.getWidth() * 2 + sourceNode.getWidth()/2 + sourceNode.getWidth()/8;
				f.setSize(w, targetNode.getHeight() * 2);
				routingObjectsLayer.add(f);	
			}
			else {/* only source routing object */
				f = FigureFactory.createRectangularVertex(false);
				f.setBackgroundColor(ColorConstants.white);
				f.setVisible(false); //set true for testing
				f.setLocation(new Point(sourceNode.getX() - sourceNode.getWidth()/2, sourceNode.getY()));
				int w = sourceNode.getWidth() + sourceNode.getWidth()/2;
				f.setSize(w, targetNode.getHeight() * 2);
				routingObjectsLayer.add(f);					
			}
		}
		else{
			ArrayList<RelativeBendpoint> list = new ArrayList<RelativeBendpoint>();	
			
			RelativeBendpoint bp = new RelativeBendpoint();
			bp.setConnection(connection);
			list.add(bp);
			bp.setWeight(0.0f);
			bp.setRelativeDimensions(new Dimension(targetNode.getX() - sourceNode.getX() + targetNode.getWidth() , 0), 
					                 new Dimension(0, 0));
				
			bp = new RelativeBendpoint();
			bp.setConnection(connection);
			list.add(bp);
			bp.setWeight(1.0f);
			bp.setRelativeDimensions(new Dimension(0, 0), 
					                 new Dimension(targetNode.getWidth(), 0));
	
			router.setConstraint(connection, list);
			routedConnectionsLayer.add(connection);
		}
	}

}
