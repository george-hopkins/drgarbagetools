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

package com.drgarbage.controlflowgraphfactory.compare;

import java.awt.Scrollbar;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JScrollPane;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.contentmergeviewer.ContentMergeViewer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jdt.core.dom.Message;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;

import com.drgarbage.algorithms.BottomUpMaxCommonSubtreeIsomorphism;
import com.drgarbage.algorithms.BottomUpSubtreeIsomorphism;
import com.drgarbage.algorithms.TopDownMaxCommonSubTreeIsomorphism;
import com.drgarbage.algorithms.TopDownSubtreeIsomorphism;
import com.drgarbage.algorithms.Algorithms;
import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.GraphUtils;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;
import com.drgarbage.controlflowgraph.intf.MarkEnum;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryPlugin;
import com.drgarbage.controlflowgraphfactory.actions.LayoutAlgorithmsUtils;
import com.drgarbage.controlflowgraphfactory.compare.actions.BottomUpMaxCommonAlgAction;
import com.drgarbage.controlflowgraphfactory.compare.actions.BottomUpSubtreeAlgAction;
import com.drgarbage.controlflowgraphfactory.compare.actions.ResetCompareGraphsViewAction;
import com.drgarbage.controlflowgraphfactory.compare.actions.CompareZoomInAction;
import com.drgarbage.controlflowgraphfactory.compare.actions.CompareZoomOutAction;
import com.drgarbage.controlflowgraphfactory.compare.actions.SwapGraphsAction;
import com.drgarbage.controlflowgraphfactory.compare.actions.TopDownAlgAction;
import com.drgarbage.controlflowgraphfactory.compare.actions.TopDownMaxCommonAlgAction;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.utils.Messages;
import com.drgarbage.visualgraphic.editparts.DiagramEditPartFactory;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;
import com.drgarbage.visualgraphic.model.VertexBase;


/**
 * The graph merge viewer.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class GraphMergeViewer extends ContentMergeViewer {
	
	private static final String BUNDLE_NAME= "org.eclipse.compare.contentmergeviewer.TextMergeViewerResources"; //$NON-NLS-1$
	
	/* diagram reference */
	private ControlFlowGraphDiagram diagramLeft;
	private ControlFlowGraphDiagram diagramRight;

	/* graphical viewer references */
	private GraphicalViewer fLeft;
	private GraphicalViewer fRight;
	
	/* set to true if the content has been swapped */ 
	private boolean swaped = false;
	
	/* Color constants */
	final static Color RED      		= new Color(null, 224, 0, 0);
	final static Color GREEN      		= new Color(null, 0, 224, 0);
	
	/**
	 * Creates a graph merge viewer.
	 * 
	 * @param parent parent composite
	 * @param styles SWT style
	 * @param mp configuration object
	 */
	public GraphMergeViewer(Composite parent, int styles, CompareConfiguration mp) {
		super(styles, ResourceBundle.getBundle(BUNDLE_NAME), mp); /* default actions */

		buildControl(parent);
		String title= "Graph Compare";
		getControl().setData(CompareUI.COMPARE_VIEWER_TITLE, title);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.compare.contentmergeviewer.ContentMergeViewer#createControls(org.eclipse.swt.widgets.Composite)
	 */
	public void createControls(Composite composite) {
		/* creates the left viewer */
		fLeft = new ScrollingGraphicalViewer();
		fLeft.createControl(composite);
		
		fLeft.getControl().setBackground(ColorConstants.listBackground);
		fLeft.setEditPartFactory(new DiagramEditPartFactory());

		ScalableFreeformRootEditPart root = new ScalableFreeformRootEditPart();
		fLeft.setRootEditPart(root);
		
		/* creates the right viewer */
		fRight = new ScrollingGraphicalViewer();
		fRight.createControl(composite);
		
		fRight.getControl().setBackground(ColorConstants.listBackground);
		fRight.setEditPartFactory(new DiagramEditPartFactory());

		ScalableFreeformRootEditPart root2 = new ScalableFreeformRootEditPart();
		fRight.setRootEditPart(root2);
		
		/*synchronize sub-windows*/
		FigureCanvas scrolledCanvasLeft = (FigureCanvas)fLeft.getControl();
		FigureCanvas scrolledCanvasRight = (FigureCanvas)fRight.getControl();
		synchronizeScrollBars(scrolledCanvasLeft, scrolledCanvasRight);
	}
	
	/**
	 * Method to synchronize scrolling of two graph-compare view presentations.
	 * @param scrolledCanvasLeft 
	 * @param scrolledCanvasRight
	 */
	private void synchronizeScrollBars(final FigureCanvas scrolledCanvasLeft, final FigureCanvas scrolledCanvasRight)
	{
		final ScrollBar verticalScrollBarRight = scrolledCanvasRight.getVerticalBar();
		final ScrollBar horizontalScrollBarRight = scrolledCanvasRight.getHorizontalBar();
		
		/*get control under vertical scrolling */
		SelectionListener verticalListener = new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				int verticalPosition =  verticalScrollBarRight.getSelection();
				scrolledCanvasLeft.scrollSmoothTo(scrolledCanvasLeft.getHorizontalBar().getSelection(), verticalPosition);
			}
		};
		
		/*get control under horizontal scrolling */
		SelectionListener horizontalListener = new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				int horizontalPosition =  scrolledCanvasRight.getHorizontalBar().getSelection();
				scrolledCanvasLeft.scrollSmoothTo(horizontalPosition, scrolledCanvasLeft.getVerticalBar().getSelection());
			}
		};

		horizontalScrollBarRight.addSelectionListener(horizontalListener);
		verticalScrollBarRight.addSelectionListener(verticalListener);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.compare.contentmergeviewer.ContentMergeViewer#createToolItems(org.eclipse.jface.action.ToolBarManager)
	 */
	protected void createToolItems(ToolBarManager toolBarManager) {

		/* remove not needed standard components*/
		toolBarManager.removeAll();
	
		/* graph compare algorithms actions */
		//toolBarManager.add(new TopDownAlgAction(this));
		toolBarManager.add(new TopDownMaxCommonAlgAction(this));
		//toolBarManager.add(new BottomUpSubtreeAlgAction(this));
		toolBarManager.add(new BottomUpMaxCommonAlgAction(this));
		
		toolBarManager.add(new Separator());
		toolBarManager.add(new ResetCompareGraphsViewAction(this));
		toolBarManager.add(new SwapGraphsAction(this));
		
		/* zoom actions */
		toolBarManager.add(new Separator());
		
		ScalableFreeformRootEditPart rootLeft = (ScalableFreeformRootEditPart) fLeft.getRootEditPart();
		ScalableFreeformRootEditPart rootRight = (ScalableFreeformRootEditPart) fRight.getRootEditPart();
		
		CompareZoomInAction zoomIn = new CompareZoomInAction(rootLeft.getZoomManager(), rootRight.getZoomManager());
		zoomIn.setAccelerator(SWT.CTRL | 'I');
		toolBarManager.add(zoomIn);
		
		CompareZoomOutAction zoomOut = new CompareZoomOutAction(rootLeft.getZoomManager(), rootRight.getZoomManager());
		zoomOut.setAccelerator(SWT.CTRL | 'O');
		toolBarManager.add(zoomOut);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.compare.contentmergeviewer.ContentMergeViewer#handleResizeLeftRight(int, int, int, int, int, int)
	 */
	protected void handleResizeLeftRight(int x, int y, int width1, int centerWidth, int width2, int height) {
		fLeft.getControl().setBounds(x, y, width1, height);
		fRight.getControl().setBounds(x + width1 + centerWidth, y, width2, height);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.compare.contentmergeviewer.ContentMergeViewer#handleResizeAncestor(int, int, int, int)
	 */
	protected void handleResizeAncestor(int x, int y, int width, int height) {
		/* nothing to do */
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.compare.contentmergeviewer.ContentMergeViewer#updateContent(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	protected void updateContent(Object ancestor, Object left, Object right) {
		if(diagramLeft == null){
			diagramLeft = getControlFlowGraphDiagramFromInput(left);
		}		
		
		if(diagramRight == null){
			diagramRight = getControlFlowGraphDiagramFromInput(right);
		}		
		
		setInput(fLeft, diagramLeft);
		setInput(fRight, diagramRight);
	}

	/**
	 * Reads the Input Object and returns a control flow graph diagram object.
	 * 
	 * @param input the diagram object
	 * @return a control flow graph diagram object
	 */
	private ControlFlowGraphDiagram getControlFlowGraphDiagramFromInput(Object input) {
		if (input != null) {
			InputStream stream= null;
			ControlFlowGraphDiagram diagram = null;
			if (input instanceof IStreamContentAccessor) {
				IStreamContentAccessor sca= (IStreamContentAccessor) input;
				if (sca != null) {
					try {
						stream = sca.getContents();
						ObjectInputStream in = new ObjectInputStream(stream);
						diagram = (ControlFlowGraphDiagram) in.readObject();
						in.close();
						stream.close();

					} catch (CoreException ex) {
						ControlFlowFactoryPlugin.log(ex);
						Messages.error(ControlFlowFactoryMessages.GraphCompare_Error_Coudl_not_open_or_create_diagram 
								+ CoreMessages.ExceptionAdditionalMessage);;
					} catch (IOException e) {
						ControlFlowFactoryPlugin.log(e);
						Messages.error(ControlFlowFactoryMessages.GraphCompare_Error_Coudl_not_open_or_create_diagram 
								+ CoreMessages.ExceptionAdditionalMessage);;
					} catch (ClassNotFoundException e) {
						ControlFlowFactoryPlugin.log(e);
						Messages.error(ControlFlowFactoryMessages.GraphCompare_Error_Coudl_not_open_or_create_diagram 
								+ CoreMessages.ExceptionAdditionalMessage);;
					}
				}
			}
			
			return diagram;
		}
		
		return null;
	}
	
	/**
	 * Sets the input for the viewer (left or right).
	 * 
	 * @param viewer the graphical viewer
	 * @param input the diagram object
	 */
	private void setInput(GraphicalViewer viewer, ControlFlowGraphDiagram diagram){
		if(viewer != null && diagram != null)
			viewer.setContents(diagram);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.compare.contentmergeviewer.ContentMergeViewer#getContents(boolean)
	 */
	protected byte[] getContents(boolean left) {		
		try{
			if(left){
				return modelToByteArray(diagramLeft);
			}
			else{
				return modelToByteArray(diagramRight);
			}
		} catch (IOException e) {
			ControlFlowFactoryPlugin.log(e);
			Messages.error(ControlFlowFactoryMessages.GraphCompare_Error_Can_not_save_diagram 
					+ CoreMessages.ExceptionAdditionalMessage);
		}

		/* We can't modify the contents of right or left side, just return null. */
		return null;
	}
	
	/**
	 * Converts a graph diagram to a byte array.
	 * @param model the control flow graph diagram
	 * @return byte array
	 * @throws IOException
	 */
	private byte[] modelToByteArray(ControlFlowGraphDiagram model) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(model);
		oos.close();
		return out.toByteArray();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.compare.contentmergeviewer.ContentMergeViewer#copy(boolean)
	 */
	protected void copy(boolean leftToRight) {
		if (leftToRight) {			
			diagramRight = diagramLeft;
			setInput(fRight, diagramRight);
			
			setRightDirty(true);
		} else {			
			diagramLeft = diagramRight;
			setInput(fLeft, diagramLeft);
			
			setLeftDirty(true);
		}
	}
	
	/**
	 * Swaps the right and left contents of the compare viewer.
	 */
	public void doSwapGraphs() {
		ControlFlowGraphDiagram tmp = diagramRight;
		diagramRight = diagramLeft;
		diagramLeft = tmp;
		
		setInput(fRight, diagramRight);
		setInput(fLeft, diagramLeft);
		
		/* update header */
		swapHeader();
		
		/* set the swaped flag */
		swaped = swaped ? false: true;
	}
	
	
	/**
	 * Swaps the header configuration.
	 */
	protected void swapHeader(){
		CompareConfiguration mp  = getCompareConfiguration();
		String lTmp = mp.getLeftLabel(getInput());
		mp.setLeftLabel(mp.getRightLabel(getInput()));
		mp.setRightLabel(lTmp);
		
		updateHeader();
	}
	
	/**
	 * Executes the top down subtree algorithm.
	 */
	public void doTopDownAlg() {
	
		doResetViewer();
		IDirectedGraphExt cfgLeft = LayoutAlgorithmsUtils.generateGraph(diagramLeft);		
		IDirectedGraphExt cfgRight = LayoutAlgorithmsUtils.generateGraph(diagramRight);
		
		TopDownSubtreeIsomorphism  compare = new TopDownSubtreeIsomorphism();
		
		/* start to compare graphs */
		Map<INodeExt, INodeExt> map = null;
		try {
			map = compare.topDownUnorderedSubtreeIsomorphism(cfgLeft, cfgRight);
		} catch (ControlFlowGraphException e) {
			ControlFlowFactoryPlugin.log(e);
			Messages.error(e.getMessage());
		}
		
		if (map == null) {
			Messages.info("Map containing equivalent nodes was null", 
							"The left graph might have more nodes than the right graph.\n" + 
							"Try swapping the graphs.");
			
			return;
		}
		
		if (map.isEmpty()) {
			Messages.info("No equivalent nodes found", 
					"No equivalent nodes could be found.");
	
			return;
		}
		
		for (Map.Entry<INodeExt, INodeExt> entry : map.entrySet()) {
			((VertexBase) entry.getKey().getData()).setColor(GREEN);
			((VertexBase) entry.getValue().getData()).setColor(GREEN);
		}
	}
	
	/**
	 * Executes the top down subtree algorithm.
	 */
	public void doTopDownMaxCommonAlg() {
		
		doResetViewer();
		IDirectedGraphExt cfgLeft = LayoutAlgorithmsUtils.generateGraph(diagramLeft);		
		IDirectedGraphExt cfgRight = LayoutAlgorithmsUtils.generateGraph(diagramRight);
		
		GraphUtils.clearGraphColorMarks(cfgLeft);
		GraphUtils.clearGraphColorMarks(cfgRight);
		
		TopDownMaxCommonSubTreeIsomorphism compare = new TopDownMaxCommonSubTreeIsomorphism();
		/* start to compare graphs */
		Map<INodeExt, INodeExt> map = null;
		try {
			map = compare.topDownMaxCommonUnorderedSubtreeIsomorphism(cfgLeft, cfgRight);
		} catch (ControlFlowGraphException e) {
			ControlFlowFactoryPlugin.log(e);
			Messages.error(e.getMessage());
		}
		
		if (map == null) {
			Messages.info("Map containing equivalent nodes was null", 
							"The left graph might have more nodes than the right graph.\n" + 
							"Try swapping the graphs.");
			
			return;
		}
		
		if (map.isEmpty()) {
			Messages.info("No equivalent nodes found", 
					"No equivalent nodes could be found.");
	
			return;
		}
		
		for (Map.Entry<INodeExt, INodeExt> entry : map.entrySet()) {
			((VertexBase) entry.getKey().getData()).setColor(GREEN);
			((VertexBase) entry.getValue().getData()).setColor(GREEN);
		}
	}

	/**
	 * Executes the bottom up subtree algorithm.
	 */
	public void doBottomUpSubtreeAlg() {
		
		doResetViewer();
		IDirectedGraphExt cfgLeft = LayoutAlgorithmsUtils.generateGraph(diagramLeft);		
		IDirectedGraphExt cfgRight = LayoutAlgorithmsUtils.generateGraph(diagramRight);
		
		BottomUpSubtreeIsomorphism compare = new BottomUpSubtreeIsomorphism();
		
		/* start to compare graphs */
		Map<INodeExt, INodeExt> map = null;
		try {
			map = compare.bottomUpUnorderedSubreeIsomorphism(cfgLeft, cfgRight);
		} catch (ControlFlowGraphException e) {
			ControlFlowFactoryPlugin.log(e);
			Messages.error(e.getMessage());
		}
		
		if (map == null) {
			Messages.info("Map containing equivalent nodes was null", 
							"The left graph might have more nodes than the right graph.\n" + 
							"Try swapping the graphs.");
			
			return;
		}
		
		if (map.isEmpty()) {
			Messages.info("No equivalent nodes found", 
					"No equivalent nodes could be found.");
	
			return;
		}
		
		for (Map.Entry<INodeExt, INodeExt> entry : map.entrySet()) {
			((VertexBase) entry.getKey().getData()).setColor(GREEN);
			((VertexBase) entry.getValue().getData()).setColor(GREEN);
		}
	}
	
	/**
	 * Executes the bottom up maximum common subtree algorithm
	 * @throws ControlFlowGraphException 
	 */
	@SuppressWarnings("restriction")
	public void doBottomUpMaxCommonAlg() throws ControlFlowGraphException {
		
		doResetViewer();
		IDirectedGraphExt cfgLeft = LayoutAlgorithmsUtils.generateGraph(diagramLeft);		
		IDirectedGraphExt cfgRight = LayoutAlgorithmsUtils.generateGraph(diagramRight);
		
		BottomUpMaxCommonSubtreeIsomorphism compare = new BottomUpMaxCommonSubtreeIsomorphism();

		/*convert graphs to trees */
		IDirectedGraphExt leftTree = Algorithms.ConvertInputGraphsToTree(cfgLeft);
		IDirectedGraphExt rightTree = Algorithms.ConvertInputGraphsToTree(cfgRight);
		
		/* start to compare graphs */
		Map<INodeExt, INodeExt> map = null;
		try {
			map = compare.bottomUpUnorderedMaxCommonSubreeIsomorphism(leftTree, rightTree);
		} catch (ControlFlowGraphException e) {
			ControlFlowFactoryPlugin.log(e);
			Messages.error(e.getMessage());
		}
		
		if (map == null) {
			Messages.info("Map containing equivalent nodes was null", 
							"The left graph might have more nodes than the right graph.\n" + 
							"Try swapping the graphs.");
			
			return;
		}
		
		if (map.isEmpty()) {
			Messages.info("No equivalent nodes found", 
					"No equivalent nodes could be found.");
	
			return;
		}
		
		for (Map.Entry<INodeExt, INodeExt> entry : map.entrySet()) {
			((VertexBase) entry.getKey().getData()).setColor(GREEN);
			((VertexBase) entry.getValue().getData()).setColor(GREEN);
		}
		
	}
	
	/**
	 * Resets the viewer and update the input.
	 */
	public void doResetViewer(){
		setInput(fRight, diagramRight);
		setInput(fLeft, diagramLeft);
		
		if(swaped){
			swapHeader();
			swaped = false;
		}
	}
	
	/**
	 * Sets the nodes color in the diagram according the coloring marks.
	 */
	public static void colorNodesByMarks(IDirectedGraphExt graph) {
		for (int i = 0; i < graph.getNodeList().size(); i++) {
			if (graph.getNodeList().getNodeExt(i).getMark() != null){
				INodeExt node = graph.getNodeList().getNodeExt(i);
				VertexBase vb = (VertexBase) node.getData();
				
				if(node.getMark() == MarkEnum.GREEN ) {
					vb.setColor(GREEN);
				}
				else{
					if(node.getMark() == MarkEnum.RED ) {
						vb.setColor(RED);
					}
				}
			}
		}
	}

}
