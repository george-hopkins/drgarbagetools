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

package com.drgarbage.visualgraphic.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import com.drgarbage.algorithms.HierarchicalLayout;
import com.drgarbage.asm.visitor.AllCodeVisitor;
import com.drgarbage.bytecode.ByteCodeConstants;
import com.drgarbage.bytecode.LineNumberTableEntry;
import com.drgarbage.bytecode.instructions.AbstractInstruction;
import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.ControlFlowGraphGenerator;
import com.drgarbage.controlflowgraph.intf.IBasicBlock;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;
import com.drgarbage.controlflowgraph.intf.INodeType;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryPlugin;
import com.drgarbage.controlflowgraphfactory.actions.LayoutAlgorithmsUtils;
import com.drgarbage.controlflowgraphfactory.export.AbstractExport;
import com.drgarbage.controlflowgraphfactory.export.AbstractExport2;
import com.drgarbage.controlflowgraphfactory.export.ExportException;
import com.drgarbage.controlflowgraphfactory.export.GraphDOTExport;
import com.drgarbage.controlflowgraphfactory.export.GraphMlExport;
import com.drgarbage.controlflowgraphfactory.export.GraphXMLExport;
import com.drgarbage.controlflowgraphfactory.preferences.ControlFlowFactoryPreferenceConstants;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.core.img.CoreImg;
import com.drgarbage.graph.GraphConstants;
import com.drgarbage.graph.IGraphSpecification;
import com.drgarbage.io.FileExtensions;
import com.drgarbage.javalang.JavaLangUtils;
import com.drgarbage.utils.Messages;

/**
 * Control flow Graph diagram factory.
 * 
 * @author Sergej Alekseev
 * @version $Revision$ $Id: ControlFlowGraphDiagramFactory.java 1390
 *          2009-10-25 15:41:10Z Peter Palaga $
 */
public class ControlFlowGraphDiagramFactory {

	public enum Result {
		OK, YES_TO_ALL, NO_TO_ALL, CANCELED, ERROR, YES, NO
	};

	/**
	 * Build bytecode graph diagram.
	 * 
	 * @param instruction
	 *            list
	 * @throws IOException
	 * @throws ControlFlowGraphException
	 */
	public static ControlFlowGraphDiagram buildByteCodeControlFlowDiagram(
			List<AbstractInstruction> instructions)
			throws ControlFlowGraphException, IOException {
		/* get preferences */
		IPreferenceStore store = ControlFlowFactoryPlugin.getDefault()
				.getPreferenceStore();
		boolean createStartNode = store
				.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_START_NODE);
		boolean createExitNode = store
				.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_EXIT_NODE);
		boolean createBackEdge = store
				.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_BACK_EDGE);

		IDirectedGraphExt graph = ControlFlowGraphGenerator
				.generateControlFlowGraph(instructions, null, createStartNode,
						createExitNode, createBackEdge);
		return createControlFlowDiagram(graph);
	}

	/**
	 * Build bytecode graph diagram.
	 * 
	 * @param classPath
	 *            , the class path
	 * @param packageName
	 *            , the name of the package
	 * @param className
	 *            , the name of the class
	 * @param methodName
	 *            , the name of the method
	 * @param methodSig
	 *            , the method signature
	 * @throws ControlFlowGraphException
	 *             , IOException, InvalidByteCodeException
	 */
	public static ControlFlowGraphDiagram buildByteCodeControlFlowDiagram(
			String[] classPath, String packageName, String className,
			String methodName, String methodSig)
			throws ControlFlowGraphException, IOException {
		/* get preferences */
		IPreferenceStore store = ControlFlowFactoryPlugin.getDefault()
				.getPreferenceStore();
		boolean createStartNode = store
				.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_START_NODE);
		boolean createExitNode = store
				.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_EXIT_NODE);
		boolean createBackEdge = store
				.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_BACK_EDGE);

		IDirectedGraphExt graph = ControlFlowGraphGenerator
				.generateControlFlowGraph(classPath, packageName, className,
						methodName, methodSig, createStartNode, createExitNode,
						createBackEdge);
		Map<String, Object> attr = graph.getUserObject();
		attr.put(ByteCodeConstants.NAME, className + "." + methodName
				+ methodSig);
		return createControlFlowDiagram(graph);
	}

	/**
	 * Build basic block graph diagram.
	 * 
	 * @param instruction
	 *            list
	 * @throws IOException
	 * @throws ControlFlowGraphException
	 */
	public static ControlFlowGraphDiagram buildBasicblockGraphDiagram(
			List<AbstractInstruction> instructions)
			throws ControlFlowGraphException, IOException {
		/* get preferences */
		IPreferenceStore store = ControlFlowFactoryPlugin.getDefault()
				.getPreferenceStore();
		boolean createStartNode = store
				.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_START_NODE);
		boolean createExitNode = store
				.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_EXIT_NODE);
		boolean createBackEdge = store
				.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_BACK_EDGE);

		/* generate control flow graph */
		IDirectedGraphExt basicBlockGraph = ControlFlowGraphGenerator
				.generateBasicBlockGraph(instructions, null, createStartNode,
						createExitNode, createBackEdge);
		return createBasicBlockDiagram(basicBlockGraph);
	}

	/**
	 * Build basicblock graph diagram.
	 * 
	 * @param classPath
	 *            , the class path
	 * @param packageName
	 *            , the name of the package
	 * @param className
	 *            , the name of the class
	 * @param methodName
	 *            , the name of the method
	 * @param methodSig
	 *            , the method signature
	 * @throws ControlFlowGraphException
	 *             , IOException, InvalidByteCodeException
	 */
	public static ControlFlowGraphDiagram buildBasicblockGraphDiagram(
			String[] classPath, String packageName, String className,
			String methodName, String methodSig)
			throws ControlFlowGraphException, IOException {
		/* get preferences */
		IPreferenceStore store = ControlFlowFactoryPlugin.getDefault()
				.getPreferenceStore();

		boolean createStartNode = store
				.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_START_NODE);
		boolean createExitNode = store
				.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_EXIT_NODE);
		boolean createBackEdge = store
				.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_BACK_EDGE);

		/* generate control flow graph */
		IDirectedGraphExt basicBlockGraph = ControlFlowGraphGenerator
				.generateBasicBlockGraph(classPath, packageName, className,
						methodName, methodSig, createStartNode, createExitNode,
						createBackEdge);
		Map<String, Object> attr = basicBlockGraph.getUserObject();
		attr.put(ByteCodeConstants.NAME, className + "." + methodName
				+ methodSig);
		return createBasicBlockDiagram(basicBlockGraph);
	}

	/**
	 * Build sourcecode graph diagram.
	 * 
	 * @param instruction
	 *            list
	 */
	public static ControlFlowGraphDiagram buildSourceControlFlowDiagram(
			List<AbstractInstruction> instructions,
			LineNumberTableEntry[] lineNumberTable)
			throws ControlFlowGraphException, IOException {
		/* get preferences */
		IPreferenceStore store = ControlFlowFactoryPlugin.getDefault()
				.getPreferenceStore();
		boolean createStartNode = store
				.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_START_NODE);
		boolean createExitNode = store
				.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_EXIT_NODE);
		boolean createBackEdge = store
				.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_BACK_EDGE);

		/* generate control flow graph */
		IDirectedGraphExt graph = ControlFlowGraphGenerator
				.generateSourceCodeGraph(instructions, lineNumberTable,
						createStartNode, createExitNode, createBackEdge);
		return createSourceCodeGraphDiagram(graph);
	}

	/**
	 * Build sourcecode graph diagram.
	 * 
	 * @param classPath
	 *            , the class path
	 * @param packageName
	 *            , the name of the package
	 * @param className
	 *            , the name of the class
	 * @param methodName
	 *            , the name of the method
	 * @param methodSig
	 *            , the method signature
	 * @throws ControlFlowGraphException
	 *             , IOException, InvalidByteCodeException
	 */
	public static ControlFlowGraphDiagram buildSourceControlFlowDiagram(
			String[] classPath, String packageName, String className,
			String methodName, String methodSig)
			throws ControlFlowGraphException, IOException {
		/* get preferences */
		IPreferenceStore store = ControlFlowFactoryPlugin.getDefault()
				.getPreferenceStore();
		boolean createStartNode = store
				.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_START_NODE);
		boolean createExitNode = store
				.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_EXIT_NODE);
		boolean createBackEdge = store
				.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_BACK_EDGE);

		/* generate control flow graph */
		IDirectedGraphExt graph = ControlFlowGraphGenerator
				.generateSourceCodeGraph(classPath, packageName, className,
						methodName, methodSig, createStartNode, createExitNode,
						createBackEdge);
		Map<String, Object> attr = graph.getUserObject();
		attr.put(ByteCodeConstants.NAME, className + "." + methodName
				+ methodSig);
		return createSourceCodeGraphDiagram(graph);
	}

	/* private methods */

	/**
	 * Creates graph diagram.
	 * 
	 * @param graph
	 * @return diagram
	 */
	private static ControlFlowGraphDiagram createControlFlowDiagram(
			IDirectedGraphExt graph) {
		int nodeWeigth = 78;
		int nodeHeight = 36;
		INodeExt node = null;

		/* build Diagram */
		ControlFlowGraphDiagram diagram = new ControlFlowGraphDiagram();

		VertexBase r = null;
		for (int i = 0; i < graph.getNodeList().size(); i++) {
			/* get Node */
			node = graph.getNodeList().getNodeExt(i);

			/* create visual Node */
			switch (node.getVertexType()) {
			case INodeType.NODE_TYPE_SIMPLE:
				r = new RectangularVertex();
				break;
			case INodeType.NODE_TYPE_IF:
				r = new DecisionVertex();
				break;
			case INodeType.NODE_TYPE_RETURN:
				r = new ReturnVertex();
				break;
			case INodeType.NODE_TYPE_GOTO_JUMP:
				r = new GotoJumpVertex();
				break;
			case INodeType.NODE_TYPE_SWITCH:
				r = new SwitchVertex();
				break;
			case INodeType.NODE_TYPE_INVOKE:
				r = new InvokeVertex();
				break;
			case INodeType.NODE_TYPE_GET:
				r = new GetVertex();
				break;
			case INodeType.NODE_TYPE_START:
				r = new StartVertex();
				break;
			case INodeType.NODE_TYPE_EXIT:
				r = new ExitVertex();
				break;
			default:
				r = new RectangularVertex();
			}

			r.setSize(new Dimension(node.getWidth(), node.getHeight()));
			r.setLocation(new Point(node.getX(), node.getY()));
			if (node.getVertexType() == INodeType.NODE_TYPE_START
					|| node.getVertexType() == INodeType.NODE_TYPE_EXIT) {
				/* do nothing */
			} else {
				r.setLabel(" " + node.getByteCodeOffset() + "  "
						+ node.getByteCodeString() + " ");
			}

			r.setSize(new Dimension(nodeWeigth, nodeHeight));

			node.setData(r);

			diagram.addChild(r);
		}

		/* create connections */
		createConnections(graph.getEdgeList());

		/* create additions */
		createAdditions(graph, diagram);

		/* layout graph */
		IDirectedGraphExt graph2 = LayoutAlgorithmsUtils.generateGraph(diagram);
		new HierarchicalLayout().visit(graph2);

		INodeListExt listNode = graph2.getNodeList();
		VertexBase vb = null;
		for (int i = 0; i < listNode.size(); i++) {
			node = listNode.getNodeExt(i);
			vb = (VertexBase) node.getData();
			vb.setLocation(new Point(node.getX(), node.getY()));
		}

		return diagram;
	}

	/**
	 * Build basic block graph diagram.
	 * 
	 * @param graph
	 * @return diagram
	 */
	private static ControlFlowGraphDiagram createBasicBlockDiagram(
			IDirectedGraphExt basicBlockGraph) {

		boolean createBasicBlockLongDescr = ControlFlowFactoryPlugin
		.getDefault()
		.getPreferenceStore()
		.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_BASIC_BLOCK_LONG_DESCR);

		
		/* build Diagram */
		ControlFlowGraphDiagram diagram = new ControlFlowGraphDiagram();

		VertexBase r = null;
		INodeExt node = null;
		for (int i = 0; i < basicBlockGraph.getNodeList().size(); i++) {
			node = basicBlockGraph.getNodeList().getNodeExt(i);

			if (node.getVertexType() == INodeType.NODE_TYPE_START) {
				r = new StartVertex();
				r.setSize(new Dimension(node.getWidth(), node.getHeight()));
				
			} else if (node.getVertexType() == INodeType.NODE_TYPE_EXIT) {
				r = new ExitVertex();
				r.setSize(new Dimension(node.getWidth(), node.getHeight()));				
			} else {				
				if(createBasicBlockLongDescr){					
					StringBuffer buf = new StringBuffer();
					buf.append(getBasicBlockContext(node));					
					
					String s = buf.toString();					
//					BasicBlockVertex bbv = new BasicBlockVertex();
					RectangularVertex bbv = new RectangularVertex();					
					bbv.setLabel(s);
					bbv.setToolTip("Basic Block: " + node.getData().toString());
					
					StringLine sl = new StringLine(s);
					//TODO: define capital letter hight = 15 and width = 7
					bbv.setSize(new Dimension(7 * sl.getMaxLineLenght(), (15 * sl.getNumberOfLines()) + 10));
					
					r = bbv;
					r.setLongDescrUsed(true);					
				}
				else {
					r = new RectangularVertex();
					r.setLabel(node.getData().toString());
					r.setToolTip(getBasicBlockContext(node));
					r.setSize(new Dimension(node.getWidth(), node.getHeight()));
				}
			}
			
			r.setLocation(new Point(node.getX(), node.getY()));
			
			node.setData(r);
			diagram.addChild(r);
		}

		/* create connections */
		createConnections(basicBlockGraph.getEdgeList());

		/* create additions */
		createAdditions(basicBlockGraph, diagram);

		/* layout graph */
		IDirectedGraphExt graph2 = LayoutAlgorithmsUtils.generateGraph(diagram);
		new HierarchicalLayout().visit(graph2);

		INodeListExt listNode = graph2.getNodeList();
		VertexBase vb = null;
		for (int i = 0; i < listNode.size(); i++) {
			node = listNode.getNodeExt(i);
			vb = (VertexBase) node.getData();
			vb.setLocation(new Point(node.getX(), node.getY()));
		}

		return diagram;
	}

	/**
	 * Creates source code graph diagram.
	 * 
	 * @param graph
	 * @return diagram
	 */
	private static ControlFlowGraphDiagram createSourceCodeGraphDiagram(
			IDirectedGraphExt graph) {
		
		boolean createSourcecodeBlockLongDescr = ControlFlowFactoryPlugin
		.getDefault()
		.getPreferenceStore()
		.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_SOURSECODE_BLOCK_LONG_DESCR);
		
		int nodeWeigth = 78;
		int nodeHeight = 36;
		INodeExt node = null;

		/* build Diagram */
		ControlFlowGraphDiagram diagram = new ControlFlowGraphDiagram();

		VertexBase r = null;
		for (int i = 0; i < graph.getNodeList().size(); i++) {
			node = graph.getNodeList().getNodeExt(i);
			
			StringBuffer buf = new StringBuffer();
			if(createSourcecodeBlockLongDescr){
				buf.append(node.getToolTipText());		
			}
			else{
				buf.append(String.valueOf(node.getByteCodeOffset()));
			}
			
			String lb = buf.toString();
			
			/* create visual Node */
			switch (node.getVertexType()) {
			case INodeType.NODE_TYPE_SIMPLE:
//				r = new BasicBlockVertex();
				r = new RectangularVertex();

				if(createSourcecodeBlockLongDescr){
					//TODO: define capital letter height = 15 and width = 8
					StringLine sl = new StringLine(lb);
					nodeWeigth = 7 * sl.getMaxLineLenght() + 20;
					nodeHeight = 15 * sl.getNumberOfLines() + 10;
				}
				
				break;
			case INodeType.NODE_TYPE_IF:
				r = new DecisionVertex();
				if(createSourcecodeBlockLongDescr){
					//TODO: define capital letter height = 15 and width = 8
					StringLine sl = new StringLine(lb);
					nodeWeigth = (7 * sl.getMaxLineLenght() ) * 2;
					nodeHeight = (15 * sl.getNumberOfLines()) * 2;
				}
				break;
			case INodeType.NODE_TYPE_RETURN:
				r = new ReturnVertex();
				if(createSourcecodeBlockLongDescr){
					//TODO: define capital letter height = 15 and width = 8
					StringLine sl = new StringLine(lb);
					nodeWeigth = 7 * sl.getMaxLineLenght() + 20;
					nodeHeight = 15 * sl.getNumberOfLines() + 10;
				}
				
				break;
			case INodeType.NODE_TYPE_GOTO_JUMP:
				r = new GotoJumpVertex();
				break;
			case INodeType.NODE_TYPE_SWITCH:
				r = new SwitchVertex();
				if(createSourcecodeBlockLongDescr){
					StringLine sl = new StringLine(lb);
					//TODO: define capital letter height = 15 and width = 8
					nodeWeigth = (7 * sl.getMaxLineLenght() ) * 2;
					nodeHeight = (15 * sl.getNumberOfLines()) + 10;
				}
				break;
			case INodeType.NODE_TYPE_INVOKE:
				r = new InvokeVertex();
				break;
			case INodeType.NODE_TYPE_GET:
				r = new GetVertex();
				break;
			case INodeType.NODE_TYPE_START:
				r = new StartVertex();
				break;
			case INodeType.NODE_TYPE_EXIT:
				r = new ExitVertex();
				break;
			case INodeType.NODE_TYPE_COMMENT:
				r = new CommentElement();
				break;
			default:
				r = new RectangularVertex();
			}

			if (node.getVertexType() == INodeType.NODE_TYPE_START
					|| node.getVertexType() == INodeType.NODE_TYPE_EXIT) {
				/* do nothing */
			} else {
					/*
					 * r.setLabel(" " + node.getByteCodeOffset() + "  " +
					 * node.getByteCodeString() + " ");
					 */
					r.setLabel(lb);
					if(createSourcecodeBlockLongDescr){
						r.setLongDescrUsed(true);
						r.setToolTip("Source line: " + node.getByteCodeOffset());
					}
					else{
						r.setToolTip(node.getToolTipText());
					}
					r.setSize(new Dimension(nodeWeigth, nodeHeight));
			}

			node.setData(r);
			diagram.addChild(r);
		}

		/* create connections */
		createConnections(graph.getEdgeList());

		/* create additions */
		createAdditions(graph, diagram);

		/* layout graph */
		IDirectedGraphExt graph2 = LayoutAlgorithmsUtils.generateGraph(diagram);
		new HierarchicalLayout().visit(graph2);

		INodeListExt listNode = graph2.getNodeList();
		VertexBase vb = null;
		for (int i = 0; i < listNode.size(); i++) {
			node = listNode.getNodeExt(i);
			vb = (VertexBase) node.getData();
			vb.setLocation(new Point(node.getX(), node.getY()));
		}

		return diagram;
	}

	public static ControlFlowGraphDiagram createControlFlowGraphDiagram(
			IDirectedGraphExt graph) {
		int nodeWeigth = 78;
		int nodeHeight = 36;
		INodeExt node = null;

		/* build Diagram */
		ControlFlowGraphDiagram diagram = new ControlFlowGraphDiagram();

		VertexBase r = null;
		for (int i = 0; i < graph.getNodeList().size(); i++) {
			node = graph.getNodeList().getNodeExt(i);

			// * create visual Node */
			switch (node.getVertexType()) {
			case INodeType.NODE_TYPE_SIMPLE:
				r = new RectangularVertex();
				break;
			case INodeType.NODE_TYPE_IF:
				r = new DecisionVertex();
				break;
			case INodeType.NODE_TYPE_RETURN:
				r = new ReturnVertex();
				break;
			case INodeType.NODE_TYPE_GOTO_JUMP:
				r = new GotoJumpVertex();
				break;
			case INodeType.NODE_TYPE_SWITCH:
				r = new SwitchVertex();
				break;
			case INodeType.NODE_TYPE_INVOKE:
				r = new InvokeVertex();
				break;
			case INodeType.NODE_TYPE_GET:
				r = new GetVertex();
				break;
			case INodeType.NODE_TYPE_START:
				r = new StartVertex();
				break;
			case INodeType.NODE_TYPE_EXIT:
				r = new ExitVertex();
				break;
			case INodeType.NODE_TYPE_COMMENT:
				r = new CommentElement();
				break;
			default:
				r = new RectangularVertex();
			}

			if (node.getVertexType() == INodeType.NODE_TYPE_START
					|| node.getVertexType() == INodeType.NODE_TYPE_EXIT) {
				/* do nothing */
			} else {

				/*
				 * r.setLabel(" " + node.getByteCodeOffset() + "  " +
				 * node.getByteCodeString() + " ");
				 */
				r.setLabel(String.valueOf(node.getByteCodeString()));
				r.setToolTip(node.getToolTipText());

				// r.setLabel(node.getByteCodeString());

			}

			r.setSize(new Dimension(nodeWeigth, nodeHeight));

			// String t = node.getByteCodeString();
			// if(t != null){
			// StringLine strLine = new StringLine(node.getByteCodeString());
			// r.setSize(new Dimension(6 * strLine.getMaxLineLenght(), 18 *
			// strLine.getNumberOfLines()));
			// }
			// else{
			// r.setSize(new Dimension(nodeWeigth, nodeHeight));
			// }

			node.setData(r);

			diagram.addChild(r);
		}

		/* create connections */
		createConnections(graph.getEdgeList());

		/* create additions */
		createAdditions(graph, diagram);

		/* layout graph */
		IDirectedGraphExt graph2 = LayoutAlgorithmsUtils.generateGraph(diagram);
		new HierarchicalLayout().visit(graph2);

		INodeListExt listNode = graph2.getNodeList();
		VertexBase vb = null;
		for (int i = 0; i < listNode.size(); i++) {
			node = listNode.getNodeExt(i);
			vb = (VertexBase) node.getData();
			vb.setLocation(new Point(node.getX(), node.getY()));
		}

		return diagram;
	}

	/**
	 * Creates diagram connections for the given edge list.
	 * 
	 * @param edges
	 */
	private static void createConnections(IEdgeListExt edges) {
		Connection c = null;
		for (int i = 0; i < edges.size(); i++) {
			IEdgeExt edge = edges.getEdgeExt(i);

			c = new Connection((VertexBase) edge.getSource().getData(),
					(VertexBase) edge.getTarget().getData());

			if (edge.getData() != null) {
				c.setLabel((String) edge.getData());
			}

			if (edge.getSource().getVertexType() == INodeType.NODE_TYPE_EXIT
					|| edge.getSource().getVertexType() == INodeType.NODE_TYPE_START
					|| edge.getTarget().getVertexType() == INodeType.NODE_TYPE_EXIT) {
				c.setLineStyle(Graphics.LINE_DASH);
			} else {
				c.setLineStyle(Graphics.LINE_SOLID);
			}
		}
	}

	private static void createAdditions(IDirectedGraphExt graph,
			ControlFlowGraphDiagram diagram) {

		Map<String, Object> attr = graph.getUserObject();
		Object name = attr.get(ByteCodeConstants.NAME);
		if (name != null) {
			diagram.setPropertyValue(ByteCodeConstants.NAME, (String) name);
		}

		/* create additions */
		boolean copyLineNumberTable = ControlFlowFactoryPlugin
				.getDefault()
				.getPreferenceStore()
				.getBoolean(
						ControlFlowFactoryPreferenceConstants.COPY_LINE_NUMBER_TABLE);

		if (copyLineNumberTable) {
			Object o = attr.get(ByteCodeConstants.LINE_NUMBER_TABLE);
			if (o != null && o instanceof String) {
				String txt = (String) o;
				VertexBase r = new CommentElement();
				r.setLabel(txt);

				StringLine strLine = new StringLine(txt);
				r.setSize(new Dimension(8 * strLine.getMaxLineLenght() + 10,
						12 * strLine.getNumberOfLines() + 20));
				diagram.addChild(r);
			}
		}
	}

	
	private static String getBasicBlockContext(INodeExt node) {
		if (node instanceof IBasicBlock) {
			StringBuffer buf = new StringBuffer();
			buf.append(" ");
			buf.append(node.getData().toString());
			buf.append(" := {");
			IBasicBlock bb = null;
			INodeListExt basicblockVertices = null;
			bb = (IBasicBlock) node;
			basicblockVertices = bb.getBasicBlockVertices();
			for (int j = 0; j < basicblockVertices.size(); j++) {
				INodeExt b = basicblockVertices.getNodeExt(j);
				buf.append("\n  ");
				buf.append(b.getByteCodeOffset());
				buf.append("  ");
				buf.append(b.getByteCodeString());			
				if(b.getLongDescr() != null){
					buf.append("\n");				
					buf.append(b.getLongDescr());
				}
			}
			buf.append("\n }");
			return buf.toString();
		} else {
			return node.getData().toString();
		}
	}

	/**
	 * Return the input stream for the given class. The result may be null, if
	 * the class has not been compiled.
	 * 
	 * @param type
	 * @return input stream
	 * @throws CoreException
	 * @throws IOException
	 */
	private static InputStream getInputStream(IType type) throws CoreException,
			IOException {

		if (type.isBinary()) {
			IClassFile classFile = type.getClassFile();
			byte[] bytes = classFile.getBytes();
			return new ByteArrayInputStream(bytes);
		} else {
			String[] classpath = JavaRuntime
					.computeDefaultRuntimeClassPath(type.getJavaProject());
			String packageName = type.getPackageFragment().getElementName();

			// if(type.isAnonymous()){
			// Messages.info("Generate graph",
			// "Cannot generate graph for anonymous source class '"+
			// type.getTypeQualifiedName()
			// +"', because the name is unknown. The comiler generates the name automatically by inserting a $ with a constant for example $1, $2 ...");
			// return null;
			// }

			String name = type.getTypeQualifiedName();

			return JavaLangUtils.findResource(classpath, packageName, name);
		}
	}

	/**
	 * Generates graphs for the given type.
	 * 
	 * @param shell
	 * @param folder
	 * @param type
	 * @param graphType
	 * @param createMonitor
	 * @return Result: OK, YES_TO_ALL, NO_TO_ALL, ERROR, CANCELED
	 * @throws CoreException
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static Result builAndSavedControlFlowDiagrams(final Shell shell,
			final IFolder folder, final IType type, final IGraphSpecification spec,
			boolean createMonitor)
			throws CoreException, InvocationTargetException,
			InterruptedException, IOException {
		final InputStream in = getInputStream(type);

		if (in == null) {
			String msg = MessageFormat.format(
					ControlFlowFactoryMessages.ClassFileInputNotCreated,
					new Object[] { type.getElementName() });
			ControlFlowFactoryPlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR,
							ControlFlowFactoryPlugin.PLUGIN_ID, msg));
			return Result.ERROR;
		}

		Result res = null;

		if (createMonitor) {
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
			monitor.run(false, true, new WorkspaceModifyOperation() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org
				 * .eclipse.core.runtime.IProgressMonitor)
				 */
				@Override
				protected void execute(IProgressMonitor monitor)
						throws CoreException, InvocationTargetException,
						InterruptedException {

					final int ticks = type.getMethods().length;
					monitor
							.beginTask(
									ControlFlowFactoryMessages.ProgressDialogCreateGraphs,
									ticks);
					try {
						generateControlFlowGraphs(monitor, folder,
								getElementName(type), in, spec);
					} catch (IOException e) {
						ControlFlowFactoryPlugin.getDefault().getLog().log(
								new Status(IStatus.ERROR,
										ControlFlowFactoryPlugin.PLUGIN_ID, e
												.getMessage(), e));
						if (!spec.isSupressMessages()) {
							Messages.error(e.getMessage()
									+ CoreMessages.ExceptionAdditionalMessage);
						}
						return;
					} catch (ControlFlowGraphException e) {
						ControlFlowFactoryPlugin.getDefault().getLog().log(
								new Status(IStatus.ERROR,
										ControlFlowFactoryPlugin.PLUGIN_ID, e
												.getMessage(), e));
						if (!spec.isSupressMessages()) {
							Messages.error(e.getMessage()
									+ CoreMessages.ExceptionAdditionalMessage);
						}
						return;
					} finally {
						monitor.done();
					}
				}
			});
		} else {
			try {
				res = generateControlFlowGraphs(null, folder,
						getElementName(type), in, spec);
			} catch (ControlFlowGraphException e) {
				ControlFlowFactoryPlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR,
								ControlFlowFactoryPlugin.PLUGIN_ID, e
										.getMessage(), e));
				if (!spec.isSupressMessages()) {
					Messages.error(e.getMessage()
							+ CoreMessages.ExceptionAdditionalMessage);
				}

				return Result.ERROR;
			}
		}

		return res;
	}

	/**
	 * Return element name for the given type.
	 * 
	 * @param type
	 * @return name
	 * @throws JavaModelException
	 */
	private static String getElementName(IType type) throws JavaModelException {
		String name = type.getFullyQualifiedName();
		int index = name.lastIndexOf('.');
		name = name.substring(++index);
		return name;
	}

	/**
	 * Starts process.
	 * 
	 * @param monitor
	 * @param folder
	 * @param elementName
	 * @param in
	 * @param graphType
	 * @param exportDecorations 
	 * @param exportGeometry 
	 * @param exportComments 
	 * @return result: OK, YES_TO_ALL, NO_TO_ALL, ERROR
	 * @throws IOException
	 * @throws ControlFlowGraphException
	 */
	private static Result generateControlFlowGraphs(IProgressMonitor monitor,
			final IFolder folder, final String elementName,
			final InputStream in,
			IGraphSpecification spec) throws IOException, ControlFlowGraphException {

		boolean yes_To_All = false;
		if (spec.isOverwriteAll()) {
			yes_To_All = true;
		}

		AllCodeVisitor codeVisitor = ControlFlowGraphGenerator
				.getClassFileVisitor(in);
		for (Map<String, Object> attr : codeVisitor.getInstructionLists()) {

			if (monitor != null && monitor.isCanceled()) {
				return Result.CANCELED;
			}

			String name = (String) attr.get(ByteCodeConstants.NAME);
			String signature = (String) attr.get(ByteCodeConstants.DESCRIPTOR);

			if (monitor != null)
				monitor.subTask(name);

			List<AbstractInstruction> instructions = (List<AbstractInstruction>) attr.get(ByteCodeConstants.CODE);

			/* get preferences */
			IPreferenceStore store = ControlFlowFactoryPlugin.getDefault()
					.getPreferenceStore();
			boolean createStartNode = store
					.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_START_NODE);
			boolean createExitNode = store
					.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_EXIT_NODE);
			boolean createBackEdge = store
					.getBoolean(ControlFlowFactoryPreferenceConstants.GENERATE_BACK_EDGE);

			IDirectedGraphExt cfg = null;
			ControlFlowGraphDiagram diagram = null;
			LineNumberTableEntry[] lineNumberTable = null;
			Object o;
			switch (spec.getGraphType()) {
			case GraphConstants.GRAPH_TYPE_BYTECODE_GRAPH:

				o = attr.get(ByteCodeConstants.LINE_NUMBER_TABLE);

				if (o != null) {
					lineNumberTable = (LineNumberTableEntry[]) o;
				}

				cfg = ControlFlowGraphGenerator.generateControlFlowGraph(
						instructions, lineNumberTable, createStartNode,
						createExitNode, createBackEdge);

				if (cfg == null) {
					// Messages.error(ControlFlowFactoryMessages.DiagramIsNullMessage);
					// return Result.ERROR;
					throw new ControlFlowGraphException(
							ControlFlowFactoryMessages.DiagramIsNullMessage);
				}

				diagram = createControlFlowDiagram(cfg);
				break;
			case GraphConstants.GRAPH_TYPE_BASICBLOCK_GRAPH:
				o = attr.get(ByteCodeConstants.LINE_NUMBER_TABLE);

				if (o != null) {
					lineNumberTable = (LineNumberTableEntry[]) o;
				}

				cfg = ControlFlowGraphGenerator.generateBasicBlockGraph(
						instructions, lineNumberTable, createStartNode,
						createExitNode, createBackEdge);

				if (cfg == null) {
					// Messages.error(ControlFlowFactoryMessages.DiagramIsNullMessage);
					// return Result.ERROR;
					throw new ControlFlowGraphException(
							ControlFlowFactoryMessages.DiagramIsNullMessage);
				}

				diagram = createBasicBlockDiagram(cfg);
				break;
			case GraphConstants.GRAPH_TYPE_SOURCE_GRAPH:

				o = attr.get(ByteCodeConstants.LINE_NUMBER_TABLE);

				if (o == null) {
					// Messages.error(ControlFlowFactoryMessages.ERROR_LineNumberTable_is_Missing
					// + name + signature);
					// return Result.ERROR;
					throw new ControlFlowGraphException(
							ControlFlowFactoryMessages.ERROR_LineNumberTable_is_Missing
									+ name + signature);
				}

				lineNumberTable = (LineNumberTableEntry[]) o;
				cfg = ControlFlowGraphGenerator.generateSourceCodeGraph(
						instructions, lineNumberTable, createStartNode,
						createExitNode, createBackEdge);

				if (cfg == null) {
					// Messages.error(ControlFlowFactoryMessages.DiagramIsNullMessage);
					// return Result.ERROR;
					throw new ControlFlowGraphException(
							ControlFlowFactoryMessages.DiagramIsNullMessage);
				}

				diagram = createSourceCodeGraphDiagram(cfg);
				break;
			default:
				throw new IllegalStateException("Unexpected graph type '"+ spec.getGraphType() +"'");
			}

			if (diagram == null) {
				// Messages.error(ControlFlowFactoryMessages.DiagramIsNullMessage);
				// return Result.ERROR;
				throw new ControlFlowGraphException(
						ControlFlowFactoryMessages.DiagramIsNullMessage);
			}

			if (name.equals(ByteCodeConstants.INIT)) {
				name = "init"; //$NON-NLS-1$
			}

			if (name.equals(ByteCodeConstants.CLINIT)) {
				name = "clinit"; //$NON-NLS-1$
			}

			/* set name property */
			diagram.setPropertyValue(ByteCodeConstants.NAME, elementName + "."
					+ name + signature);

			String fileName = elementName + "." + name
					+ signature.replace('/', '.') + "."
					+ GraphConstants.graphTypeSuffixes[spec.getGraphType()];
			IPath pathToFile = folder.getFullPath().append(fileName)
					.addFileExtension(FileExtensions.GRAPH);

			try {
				Result r;
				
				switch (spec.getExportFormat()) {
				case GraphConstants.EXPORT_FORMAT_DRGARBAGE_GRAPH:
					final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(pathToFile);
					r = saveDiagram(file, diagram, monitor, yes_To_All);
					break;
				default:
					
					AbstractExport2 exporter = null;
					
					switch (spec.getExportFormat()) {
					case GraphConstants.EXPORT_FORMAT_DOT:
						exporter = new GraphDOTExport();
						pathToFile = pathToFile.addFileExtension(FileExtensions.DOT);
						break;
					case GraphConstants.EXPORT_FORMAT_GRAPHXML:
						exporter = new GraphXMLExport();
						pathToFile = pathToFile.addFileExtension(FileExtensions.XML);
						break;
					case GraphConstants.EXPORT_FORMAT_GRAPHML:
						exporter = new GraphMlExport();
						pathToFile = pathToFile.addFileExtension(FileExtensions.GRAPHML);
						break;
					default:
						throw new IllegalStateException("Unexpected export format.");
					}
					
					
					exporter.setGraphSpecification(spec);
					StringWriter sb = new StringWriter();
					try {
						exporter.write(diagram, sb);
					} catch (ExportException e) {
						/*
						 * This will never happen as StringWriter.append(*)
						 * does not throw IOException
						 */
						throw new RuntimeException(e);
					}

					final IFile file2 = ResourcesPlugin.getWorkspace().getRoot()
							.getFile(pathToFile);
					r = saveContentToFile(file2, sb.toString(), monitor,
							yes_To_All);
				}
				
				if (r == Result.NO) {
					continue;
				} else if (r == Result.NO_TO_ALL) {
					return Result.NO_TO_ALL;
				} else if (r == Result.YES_TO_ALL) {
					yes_To_All = true;
				} else if( r == Result.CANCELED){
					return Result.CANCELED;
				}

			} catch (CoreException e) {
				ControlFlowFactoryPlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR,
								ControlFlowFactoryPlugin.PLUGIN_ID, e
										.getMessage(), e));
				// Messages.error(e.getMessage() +
				// CoreMessages.ExceptionAdditionalMessage);
				return Result.ERROR;
			}

		}

		if (yes_To_All) {
			return Result.YES_TO_ALL;
		}

		return Result.OK;
	}

	private static Result saveDiagram(IFile file,
			ControlFlowGraphDiagram diagram, IProgressMonitor monitor,
			boolean yes_To_All) throws IOException, CoreException {

		/* save graph */
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(diagram);
		oos.close();

		/* delete if exists */
		if (file.exists()) {
			if (!yes_To_All) {
				String msg = MessageFormat.format(
						ControlFlowFactoryMessages.EXPORT_FILE_OVERWRITE_MESSAGE_2,
						new Object[] { file.getName() });
				
				int res = openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						ControlFlowFactoryMessages.EXPORT_FILE_OVERWRITE_TITLE, msg);
				if (res == 0) { /* YES */
					file.delete(false, monitor);
				} else if (res == 1) { /* YES_TO_ALL */
					yes_To_All = true;
					file.delete(false, monitor);
				} else if (res == 2) { /* NO */
					if (monitor != null)
						monitor.worked(1);
					return Result.NO;
				} else if (res == 3) { /* NO_TO_ALL */
					return Result.NO_TO_ALL;
				}
			} else {
				file.delete(false, monitor);
			}

		}
		
		try{
			file.create(new ByteArrayInputStream(out.toByteArray()), /* contents */
					true, /* keep saving, even if IFile is out of sync with the Workspace */
					monitor); /* progress monitor */

		}
		catch(CoreException ce){			
			ControlFlowFactoryPlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR,
							ControlFlowFactoryPlugin.PLUGIN_ID,ce.getMessage(), ce));

			String msg = MessageFormat.format(
					ControlFlowFactoryMessages.EXPORT_FILE_COULD_NOT_WRITE,
					new Object[] { file.getName() });
			
			int res = openStop(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"File could not be written", msg);
			
			
			if (res == 1) { /* YES */
				return Result.CANCELED;
			}
		}
		
		if (monitor != null)
			monitor.worked(1);

		if (yes_To_All) {
			return Result.YES_TO_ALL;
		}

		return Result.OK;
	}

	private static Result saveContentToFile(IFile file, String content,
			IProgressMonitor monitor, boolean yes_To_All) throws IOException,
			CoreException {

		/* delete if exists */
		if (file.exists()) {
			if (!yes_To_All) {
				String msg = MessageFormat.format(
						ControlFlowFactoryMessages.EXPORT_FILE_OVERWRITE_MESSAGE_2,
						new Object[] { file.getName() });
				
				int res = openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						ControlFlowFactoryMessages.EXPORT_FILE_OVERWRITE_TITLE, msg);
				if (res == 0) { /* YES */
					file.delete(false, monitor);
				} else if (res == 1) { /* YES_TO_ALL */
					yes_To_All = true;
					file.delete(false, monitor);
				} else if (res == 2) { /* NO */
					if (monitor != null)
						monitor.worked(1);
					return Result.NO;
				} else if (res == 3) { /* NO_TO_ALL */
					return Result.NO_TO_ALL;
				}
			} else {
				file.delete(false, monitor);
			}

		}

		file.create(new ByteArrayInputStream(content.getBytes()), /* contents */
		true, /* keep saving, even if IFile is out of sync with the Workspace */
		monitor); /* progress monitor */

		if (monitor != null)
			monitor.worked(1);

		if (yes_To_All) {
			return Result.YES_TO_ALL;
		}

		return Result.OK;

	}

	public static int openConfirm(Shell parent, String title, String message) {
		MessageDialog dialog = new MessageDialog(parent, title,
				CoreImg.aboutDrGarbageIcon_16x16.createImage(), message,
				MessageDialog.QUESTION, new String[] {
						IDialogConstants.YES_LABEL,
						IDialogConstants.YES_TO_ALL_LABEL,
						IDialogConstants.NO_LABEL,
						IDialogConstants.NO_TO_ALL_LABEL }, 3); /*
																 * OK is the
																 * default
																 */
		return dialog.open();
	}

	public static int openStop(Shell parent, String title, String message) {
		MessageDialog dialog = new MessageDialog(parent, title,
				CoreImg.aboutDrGarbageIcon_16x16.createImage(), message,
				MessageDialog.QUESTION, new String[] {
						IDialogConstants.NO_LABEL,
						IDialogConstants.YES_LABEL }, 1); /*
														   * YES is the
														   * default
														   */
		return dialog.open();
	}
	
	public static VertexBase clone(VertexBase vb) {
		VertexBase vb2 = null;

		if (vb instanceof CommentElement) {
			vb2 = new CommentElement();
		} else if (vb instanceof DecisionVertex) {
			vb2 = new DecisionVertex();
		} else if (vb instanceof GetVertex) {
			vb2 = new GetVertex();
		} else if (vb instanceof GotoJumpVertex) {
			vb2 = new GotoJumpVertex();
		} else if (vb instanceof InvokeVertex) {
			vb2 = new InvokeVertex();
		} else if (vb instanceof RectangularVertex) {
			vb2 = new RectangularVertex();
		} else if (vb instanceof ReturnVertex) {
			vb2 = new ReturnVertex();
		} else if (vb instanceof StartVertex) {
			vb2 = new StartVertex();
		} else if (vb instanceof ExitVertex) {
			vb2 = new ExitVertex();
		} else if (vb instanceof SwitchVertex) {
			vb2 = new SwitchVertex();
		}

		if (vb2 == null) {
			return null;
		}

		vb2.setLabel(vb.getLabel());
		vb2.setSize(vb.getSize());

		Point p = new Point();
		p.x = vb.getLocation().x + 10;
		p.y = vb.getLocation().y + 10;
		vb2.setLocation(p);

		return vb2;
	}

}
