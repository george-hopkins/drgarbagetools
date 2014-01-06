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

package com.drgarbage.controlflowgraph;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.drgarbage.algorithms.BasicBlockGraphVisitor;
import com.drgarbage.asm.ClassReader;
import com.drgarbage.asm.render.intf.IInstructionLine;
import com.drgarbage.asm.visitor.AllCodeVisitor;
import com.drgarbage.asm.visitor.FilteringCodeVisitor;
import com.drgarbage.asm.visitor.MethodFilteringVisitor;
import com.drgarbage.bytecode.ByteCodeConstants;
import com.drgarbage.bytecode.LineNumberTableEntry;
import com.drgarbage.bytecode.instructions.AbstractInstruction;
import com.drgarbage.bytecode.instructions.BranchInstruction;
import com.drgarbage.bytecode.instructions.LookupSwitchInstruction;
import com.drgarbage.bytecode.instructions.Opcodes;
import com.drgarbage.bytecode.instructions.TableSwitchInstruction;
import com.drgarbage.bytecode.instructions.LookupSwitchInstruction.MatchOffsetEntry;
import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.GraphUtils;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;
import com.drgarbage.controlflowgraph.intf.INodeType;
import com.drgarbage.javalang.JavaLangUtils;

/**
 * Collection of methods for generating control flow graphs from the byte code
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class ControlFlowGraphGenerator  implements Opcodes{
	
	private static final boolean debug = false;
	
	public static final String VIRTUAL_START_NODE_TEXT = "START";
	public static final String VIRTUAL_EXIT_NODE_TEXT = "EXIT";
	
	public static final String VIRTUAL_START_NODE_TOOLTIP_TEXT = "Virtual start vertex";
	public static final String VIRTUAL_EXIT_NODE_TOOLTIP_TEXT = "Virtual exit vertex";
	public static final String VIRTUAL_BACK_EDGE_TOOLTIP_TEXT = "Virtual back edge";
	
	
	
	/**
      Dump a specific debug message.
      @param message the debug message
	 */
	private static void debug(String message) {
		if(debug){
			System.out.println(message);
		}
	}  

	/** 
	 * Generates a control flow graph whose nodes are synchronized 
	 * with the line numbers in the bytecode view.
	 * @param instructions - list of synchronized instructions
	 * @return the control flow graph 
	 */    
	public static IDirectedGraphExt generateSynchronizedControlFlowGraphFrom (List<IInstructionLine> instructions){
		return generateSynchronizedControlFlowGraphFrom(instructions, false);
	}

	/** 
	 * Generates a control flow graph whose nodes are synchronized 
	 * with the line numbers in the bytecode view.
	 * @param instructions - list of synchronized instructions
	 * @param setReferenceToIntsrutionList - flag if a reference to the original instruction object has to be set
	 * @return the control flow graph 
	 */    
	public static IDirectedGraphExt generateSynchronizedControlFlowGraphFrom (List<IInstructionLine> instructions,
			boolean setReferenceToIntsrutionList){
		AbstractInstruction currentInstruction = null;
		HashMap<Integer, INodeExt> hashTable = new HashMap<Integer, INodeExt>();
		List<AbstractInstruction> instructionList = new ArrayList<AbstractInstruction>();

		/* Create new Control Flow Graph */
		IDirectedGraphExt cfg = GraphExtentionFactory.createDirectedGraphExtention();
		INodeListExt nodes = cfg.getNodeList();
		IEdgeListExt edges = cfg.getEdgeList();
		if(debug) debug("create new Graph"); //$NON-NLS-1$

		/* add vertices */
		Integer vertex = null;
		int nodeType = -1;
		String instrVerbose = null;
		IInstructionLine instrLine = null;
		for(int i=0; i< instructions.size();i++){
			instrLine = instructions.get(i);
			currentInstruction = instrLine.getInstruction();
			instructionList.add(currentInstruction); /* create instruction list */
			vertex = new Integer(currentInstruction.getOffset());

			nodeType = ControlFlowGraphUtils.getInstructionNodeType(currentInstruction.getOpcode());
			instrVerbose = currentInstruction.getOpcodeMnemonic();

			/* add vertex */
			INodeExt  node= setReferenceToIntsrutionList ?
					GraphExtentionFactory.createNodeExtention(instrLine) :
						GraphExtentionFactory.createNodeExtention(null);
			node.setY(instrLine.getLine() +1);
			node.setByteCodeOffset(currentInstruction.getOffset());
			node.setByteCodeString(instrVerbose);
			node.setToolTipText(" " + node.getByteCodeOffset() + " - " + node.getByteCodeString() + " ");
			node.setVertexType(nodeType);

			nodes.add(node);
			hashTable.put(vertex, node);

			if(debug)debug("add Vertex:" + vertex);//$NON-NLS-1$
		}

		parseInstructionList(instructionList, edges, hashTable);

		return cfg;
	}

	/** 
	 * Generates a control flow graph from an instruction list.
	 * @param instructions - list of synchronized instructions
	 * @return The control flow graph 
	 * @throws ControlFlowGraphException
	 * @throws IOException
	 */
	public static IDirectedGraphExt generateControlFlowGraph (List<AbstractInstruction> instructions)
	throws ControlFlowGraphException, IOException
	{
		return generateControlFlowGraph(instructions, null, false, false, false, false);
	}
	
	/** 
	 * Generates a control flow graph from an instruction list.
	 * @param instructions - list of synchronized instructions
	 * @param setReferenceToIntsrutionList - flag if a reference to the original instruction object has to be set
	 * @return The control flow graph 
	 * @throws ControlFlowGraphException
	 * @throws IOException
	 */
	public static IDirectedGraphExt generateControlFlowGraph (List<AbstractInstruction> instructions, boolean setReferenceToIntsrutionList)
	throws ControlFlowGraphException, IOException
	{
		return generateControlFlowGraph(instructions, null, false, false, false, setReferenceToIntsrutionList);
	}
	
	/**
	 * Generates a control flow graph from an instruction list.
	 * <br>
	 * NOTE: This method is used for compatibility. 
	 * Used the method 
	 * {@link #generateControlFlowGraph(List,LineNumberTableEntry[],boolean,boolean,boolean,boolean) generateControlFlowGraph}
	 * instead.
	 * @param instructions - list of synchronized instructions
	 * @param lineNumberTable - the line number table
	 * @param createStartVertex - flag if a virtual start node has to be created
	 * @param createExitvertex - flag if a virtual exit node has to be created
	 * @param createBackEdge - flag if a virtual back edge has to be created
	 * @return The control flow graph 
	 * @throws ControlFlowGraphException
	 * @throws IOException
	 */
	public static IDirectedGraphExt generateControlFlowGraph (List<AbstractInstruction> instructions,
			LineNumberTableEntry[] lineNumberTable,
			boolean createStartVertex,
			boolean createExitvertex,
			boolean createBackEdge)
	throws ControlFlowGraphException, IOException
	{
		return generateControlFlowGraph(instructions, null, false, false, false, false);
	}
	
	/**
	 * Generates a control flow graph from an instruction list.
	 * @param instructions - list of synchronized instructions
	 * @param lineNumberTable - the line number table
	 * @param createStartVertex - flag if a virtual start node has to be created
	 * @param createExitvertex - flag if a virtual exit node has to be created
	 * @param createBackEdge - flag if a virtual back edge has to be created
	 * @param setReferenceToIntsrutionList - flag if a reference to the original instruction object has to be set
	 * @return The control flow graph 
	 * @throws ControlFlowGraphException
	 * @throws IOException
	 */
	public static IDirectedGraphExt generateControlFlowGraph (List<AbstractInstruction> instructions,
			LineNumberTableEntry[] lineNumberTable,
			boolean createStartVertex,
			boolean createExitvertex,
			boolean createBackEdge,
			boolean setReferenceToIntsrutionList)
	throws ControlFlowGraphException, IOException
	{
		AbstractInstruction currentInstruction = null;
		HashMap<Integer, INodeExt> hashTable = new HashMap<Integer, INodeExt>();

		/* Create new Control Flow Graph */
		IDirectedGraphExt cfg = GraphExtentionFactory.createDirectedGraphExtention();
		INodeListExt nodes = cfg.getNodeList();
		IEdgeListExt edges = cfg.getEdgeList();
		if(debug)debug("create new Graph");//$NON-NLS-1$

		/* add vertices */
		Integer vertex = null;
		int nodeType = -1;
		String instrVerbose = null;
		for(int i = 0; i< instructions.size(); i++){

			currentInstruction = (AbstractInstruction)instructions.get(i);
			vertex = new Integer(currentInstruction.getOffset());

			/* create vertex property Object */
			nodeType = ControlFlowGraphUtils.getInstructionNodeType(currentInstruction.getOpcode());
			instrVerbose = currentInstruction.getOpcodeMnemonic();

			/* add vertex */
			INodeExt  node = setReferenceToIntsrutionList ? 
					GraphExtentionFactory.createNodeExtention(currentInstruction):
						GraphExtentionFactory.createNodeExtention(null);
			node.setByteCodeOffset(currentInstruction.getOffset());
			node.setByteCodeString(instrVerbose);
			node.setToolTipText(" " + node.getByteCodeOffset() + " - " + node.getByteCodeString() + " ");
			node.setVertexType(nodeType);

			nodes.add(node);
			hashTable.put(vertex, node);

			if(debug) debug("add Vertex:" + vertex);//$NON-NLS-1$
		}

		parseInstructionList(instructions, edges, hashTable);
		
		INodeExt  startNode = null, exitNode = null;
		/* create start Vertex */
		if(createStartVertex){
			startNode = GraphExtentionFactory.createNodeExtention(null);
			startNode.setToolTipText(VIRTUAL_START_NODE_TOOLTIP_TEXT);
			startNode.setVertexType(INodeType.NODE_TYPE_START);
			nodes.add(startNode);
			
			/* create edge to the first node */
			edges.add(GraphExtentionFactory.createEdgeExtention(startNode, hashTable.get(0)));
		}

		/* create exit Vertex */
		if(createExitvertex){
			exitNode = GraphExtentionFactory.createNodeExtention(null);
			exitNode.setToolTipText(VIRTUAL_EXIT_NODE_TOOLTIP_TEXT);
			exitNode.setVertexType(INodeType.NODE_TYPE_EXIT);
			nodes.add(exitNode);
			
			/* create edges to all exit vertices */
			for(int i = 0; i < nodes.size(); i++){
				INodeExt n = nodes.getNodeExt(i);
				if(n.getVertexType() == INodeType.NODE_TYPE_RETURN){
					/* create an edge */
					edges.add(GraphExtentionFactory.createEdgeExtention(n, exitNode));
				}
			}
			
		}
	
		/* create back edge */
		if(createBackEdge){ 
			if(startNode != null & exitNode != null){
				IEdgeExt edge = GraphExtentionFactory.createEdgeExtention(exitNode, startNode);
				edge.setData(VIRTUAL_BACK_EDGE_TOOLTIP_TEXT);
				edges.add(edge);
			}
			else{
				throw new ControlFlowGraphException("Cannot create virtual back edge. Start or Exit node missing.");//$NON-NLS-1$
			}
		}
		
		/* use user object to store the line number table representation */
		if(lineNumberTable != null){
			String lineNumberTableStr = createLineNumberTableRespresenation(lineNumberTable);
			cfg.getUserObject().put(ByteCodeConstants.LINE_NUMBER_TABLE, lineNumberTableStr);
		}
		
		return cfg;
	}

	/** 
	 * Generates a control flow graph for the defined method.
	 * @param classpath
	 * @param package name
	 * @param class name
	 * @param method name
	 * @param method signature
	 * @return the control flow graph 
	 */
	public static IDirectedGraphExt generateControlFlowGraph(String classPath[],
			String packageName,
			String className,
			String methodName,
			String methodSig,
			boolean createStartVertex,
			boolean createExitvertex,
			boolean createBackEdge) 
	throws ControlFlowGraphException, IOException
	{
		FilteringCodeVisitor codeVisitor = getInstructionList(classPath, packageName, className, methodName, methodSig);
		return ControlFlowGraphGenerator.generateControlFlowGraph(codeVisitor.getInstructions(), codeVisitor.getLineNumberTable(), createStartVertex, createExitvertex, createBackEdge);
	}

	/**
	 * Generates a source code graph for the defined method.
	 * @param classpath
	 * @param package name
	 * @param class name
	 * @param method name
	 * @param method signature
	 * @return the control flow graph
	 * @throws ControlFlowGraphException, IOException, InvalidByteCodeException
	 */
	public static IDirectedGraphExt generateSourceCodeGraph(String classPath[],
			String packageName,
			String className,
			String methodName,
			String methodSig,
			boolean createStartVertex,
			boolean createExitvertex,
			boolean createBackEdge)
	throws ControlFlowGraphException, IOException
	{
		FilteringCodeVisitor codeVisitor = getInstructionList(classPath, packageName, className, methodName, methodSig);
		return generateSourceCodeGraph(codeVisitor.getInstructions(), codeVisitor.getLineNumberTable(), createStartVertex, createExitvertex, createBackEdge);

	}

	/** 
	 * Generates a source code graph for the defined method.
	 * @param List of instructions
	 * @param code of the methode
	 * @return the control flow graph 
	 * @throws ControlFlowGraphException
	 * @throws IOException 
	 */    
	@SuppressWarnings("unchecked")
	public static IDirectedGraphExt generateSourceCodeGraph (List<AbstractInstruction> instructions,
			LineNumberTableEntry[] lineNumberTable,
			boolean createStartVertex,
			boolean createExitvertex,
			boolean createBackEdge)
	throws ControlFlowGraphException, IOException
	{
		if (lineNumberTable == null){
			throw new ControlFlowGraphException("Line number table attribute missing."); //$NON-NLS-1$
		}

		/* generate bytecode graph */
		IDirectedGraphExt cfg = ControlFlowGraphGenerator.generateControlFlowGraph(instructions, null, false, false, false);


		/* create bytecode node tree structure */
		Map<Integer, INodeExt> treeNodeTable = new TreeMap<Integer, INodeExt>();
		INodeListExt byteCodeNodes = cfg.getNodeList();
		INodeExt byteCodeNode= null, sourceCodeNode = null;
		int line = -1;
		Integer lineNumber = null;
		for(int i = 0; i < byteCodeNodes.size(); i++){
			byteCodeNode = byteCodeNodes.getNodeExt(i);
			line = getSourceCodeLine(lineNumberTable, byteCodeNode.getByteCodeOffset());
		
			lineNumber = new Integer(line);
			
			/* assign line number to the byte code node */
			byteCodeNode.setData(lineNumber);

			sourceCodeNode  = treeNodeTable.get(lineNumber);
			if(sourceCodeNode == null){
				List<INodeExt> byteCode = new ArrayList<INodeExt>();
				byteCode.add(byteCodeNode);                                    
				
				/* create new source code node */
				sourceCodeNode = GraphExtentionFactory.createNodeExtention(byteCode);
				
				/* set list of bytecode instructions */
				sourceCodeNode.setData(byteCode);
				
				/* set line number */
				sourceCodeNode.setByteCodeOffset(line);
				
				/* set text */
				/* bug #108 �N line� to just �N�*/
				/* sourceCodeNode.setByteCodeString("line"); */

				/* set line number as y coordinate*/
				sourceCodeNode.setY(line - 1);

				treeNodeTable.put(lineNumber, sourceCodeNode);
			}
			else{
				/* add byte code instruction to existing source code node */
				List<INodeExt> byteCode = (List<INodeExt>)sourceCodeNode.getData();
				byteCode.add(byteCodeNode);
			}
		}

		/* create Source Code Graph */
		IDirectedGraphExt sourceCodeGraph = GraphExtentionFactory.createDirectedGraphExtention();
		INodeListExt nodes = sourceCodeGraph.getNodeList();
		IEdgeListExt edges = sourceCodeGraph.getEdgeList();

		/* initialize source code edges */                
		IEdgeExt byteCodeEdge = null;
		Integer source, target;
		IEdgeExt newEdge = null;
		INodeExt sourceNode = null, targetNode = null;
		Map<String, IEdgeExt> treeEdgeTable = new HashMap();
		IEdgeListExt byteCodeEdges = cfg.getEdgeList();
		for(int i = 0; i < byteCodeEdges.size(); i++){
			byteCodeEdge = byteCodeEdges.getEdgeExt(i);
			source = (Integer)byteCodeEdge.getSource().getData();
			target = (Integer)byteCodeEdge.getTarget().getData();

			sourceNode = treeNodeTable.get(source);
			targetNode = treeNodeTable.get(target);
			if(sourceNode == targetNode){
				continue;
			}

			/* 
			 * check if there are a some edges assigned to the node with the line = 1. 
			 * It is a virtual line set by compiler 
			 */
//			if(sourceNode.getByteCodeOffset() == 1 || targetNode.getByteCodeOffset() == 1){
//				continue;
//			}

			/* check if the edge already exists */
			String key = createKey(sourceNode, targetNode);
			if(!treeEdgeTable.containsKey(key)){
				newEdge = GraphExtentionFactory.createEdgeExtention(sourceNode, targetNode);
				newEdge.setData(byteCodeEdge.getData());/* copy text label */
				treeEdgeTable.put(key, newEdge);
				edges.add(newEdge);
			}
			else{
				IEdgeExt e = treeEdgeTable.get(key);
				Object o1 =  byteCodeEdge.getData();
				if(o1 != null){
					Object o2 = e.getData();
					if(o2 != null){
						String s = o2.toString() +  ", " + o1.toString();
						e.setData(s);
					}
					else{
						e.setData(o1);
					}
				}
			}
		}	

		/* add nodes */
		Iterator<INodeExt> it = treeNodeTable.values().iterator();
		INodeExt node = null;
		while(it.hasNext()){
			node = it.next();
			
			/* 
			 * check if there is a node with the line = 1. 
			 * It is a virtual line set by compiler 
			 */
//			if(node.getByteCodeOffset() == 1){
//				//System.out.println(node);
//				continue;
//			}

			String s = createToolTip( node);
			node.setToolTipText(s);
			node.setByteCodeString(s);

			if(node.getOutgoingEdgeList().size() == 0){
				node.setVertexType(INodeType.NODE_TYPE_RETURN);
//			} else if(node.getOutgoingEdgeList().size() == 2){
//				node.setVertexType(INodeType.NODE_TYPE_IF);
//			}else if(node.getOutgoingEdgeList().size() > 2){
//				node.setVertexType(INodeType.NODE_TYPE_SWITCH);
			}else{
				node.setVertexType(getNodeType(node));
			}
			nodes.add(node);
		}
		
		
		
		INodeExt  startNode = null, exitNode = null;
		/* create start Vertex */
		if(createStartVertex){
			startNode = GraphExtentionFactory.createNodeExtention(null);
			startNode.setToolTipText(VIRTUAL_START_NODE_TOOLTIP_TEXT);
			startNode.setVertexType(INodeType.NODE_TYPE_START);
			nodes.add(startNode);
			
			/* get line number of the first node */
			line = getSourceCodeLine(lineNumberTable, 0);
			
			/* create edge to the first node */
			INodeExt first = treeNodeTable.get(line);
			IEdgeExt e = GraphExtentionFactory.createEdgeExtention(startNode, first);
			edges.add(e);
			
		}

		/* create exit Vertex */
		if(createExitvertex){
			exitNode = GraphExtentionFactory.createNodeExtention(null);
			exitNode.setToolTipText(VIRTUAL_EXIT_NODE_TOOLTIP_TEXT);
			exitNode.setVertexType(INodeType.NODE_TYPE_EXIT);
			nodes.add(exitNode);
			
			/* create edges to all exit vertices */
			for(int i = 0; i < nodes.size(); i++){
				INodeExt n = nodes.getNodeExt(i);
				if(n.getVertexType() == INodeType.NODE_TYPE_RETURN){
					/* create an edge */
					edges.add(GraphExtentionFactory.createEdgeExtention(n, exitNode));
				}
			}
			
		}
	
		/* create back edge */
		if(createBackEdge){ 
			if(startNode != null & exitNode != null){
				IEdgeExt edge = GraphExtentionFactory.createEdgeExtention(exitNode, startNode);
				edge.setData(VIRTUAL_BACK_EDGE_TOOLTIP_TEXT);
				edges.add(edge);
			}
			else{
				throw new ControlFlowGraphException("Cannot create virtual back edge. Start or Exit node missing.");//$NON-NLS-1$
			}
		}
		
		/* use user object to store the line number table representation */
		String lineNumberTableStr = createLineNumberTableRespresenation(lineNumberTable);
		sourceCodeGraph.getUserObject().put(ByteCodeConstants.LINE_NUMBER_TABLE, lineNumberTableStr);
		
		return sourceCodeGraph;
	}
	
	/**
	 * 
	 * Calls {@link #generateSourceCodeGraph(List)} for each method of each class from the given <code>classList</code>
	 * 
	 * @param classList
	 * @param classPath
	 * @param packageName
	 * @return
	 * @throws IOException
	 * @throws ControlFlowGraphException
	 */
	@SuppressWarnings("unchecked")
	public static List<IDirectedGraphExt> generateSourceCodeGraphs (
			List<String> classList,
			String[] classPath, 
			String packageName,
			boolean createStartVertex,
			boolean createExitvertex,
			boolean createBackEdge) 
	throws IOException, ControlFlowGraphException 
	{
		List<IDirectedGraphExt> graphs = new ArrayList<IDirectedGraphExt>();

		for(String l: classList){

			InputStream in = JavaLangUtils.findResource(classPath, packageName, l);
			if(in == null){
				throw new ControlFlowGraphException(ControlFlowGraphGenerator.class.getSimpleName() + ": Class '" + l +"' not found in the CLASSPATH.");//$NON-NLS-1$
			}

			if (!(in instanceof BufferedInputStream)) {
				/* buffer only if necessary */
				in = new BufferedInputStream(in);
			}
			
			
	        AllCodeVisitor codeVisitor = new AllCodeVisitor();
	        MethodFilteringVisitor classVisitor = new MethodFilteringVisitor(codeVisitor);
	        ClassReader cr = new ClassReader(in, classVisitor);
	        cr.accept(classVisitor, 0);
	        
	        for (Map<String, Object> attr : codeVisitor.getInstructionLists()) {
	        	List<AbstractInstruction> instructions = (List<AbstractInstruction>) attr.get(ByteCodeConstants.CODE);
	        	LineNumberTableEntry[] lineNumberTable = (LineNumberTableEntry[]) attr.get(ByteCodeConstants.LINE_NUMBER_TABLE);
	        	
	        	IDirectedGraphExt cfg = ControlFlowGraphGenerator.generateSourceCodeGraph(instructions, lineNumberTable, createStartVertex, createExitvertex, createBackEdge);
				cfg.getUserObject().put(ByteCodeConstants.NAME, attr.get(ByteCodeConstants.NAME));
				cfg.getUserObject().put(ByteCodeConstants.DESCRIPTOR, attr.get(ByteCodeConstants.DESCRIPTOR));
				cfg.getUserObject().put(ByteCodeConstants.Class_retrieved_from, l); /* set class reference */

				graphs.add(cfg);
			}

		}

		
		return graphs;
	}

	
	/** 
	 * Generates a basic block graph graph from an instruction list.
	 * @param List of synchronized instructions
	 * @return the control flow graph 
	 * @throws IOException 
	 * @throws ControlFlowGraphException 
	 */    
	public static IDirectedGraphExt generateBasicBlockGraph(List<AbstractInstruction> instructions,
			LineNumberTableEntry[] lineNumberTable,
			boolean createStartVertex,
			boolean createExitVertex,
			boolean createBackEdge) 
	throws ControlFlowGraphException, IOException
	{	
		IDirectedGraphExt graph = generateControlFlowGraph(instructions, null, false, false, false);
		
		/* find basic blocks */
		GraphUtils.clearGraph(graph);
		BasicBlockGraphVisitor basicBlockVisitor = new BasicBlockGraphVisitor();
		basicBlockVisitor.start(graph);

		IDirectedGraphExt basicBlockGraph = basicBlockVisitor.getBasicBlockGraph();

		int nodeWeigth = 48;
		int nodeHeight = 36;
		INodeExt node = null;
		INodeListExt nodes = basicBlockGraph.getNodeList();
	  	for (int i = 0; i < nodes.size(); i++) {
	  		node = nodes.getNodeExt(i);
	  		node.setWidth(nodeWeigth);
	  		node.setHeight(nodeHeight);
	  	}

	  	IEdgeListExt edges = basicBlockGraph.getEdgeList();
		INodeExt  startNode = null, exitNode = null;

		/* create exit Vertex */
		if(createExitVertex){
			exitNode = GraphExtentionFactory.createNodeExtention(VIRTUAL_EXIT_NODE_TEXT);
			exitNode.setToolTipText(VIRTUAL_EXIT_NODE_TOOLTIP_TEXT);
			exitNode.setVertexType(INodeType.NODE_TYPE_EXIT);
			
			/* create edges to all exit vertices */
			for(int i = 0; i < nodes.size(); i++){
				INodeExt n = nodes.getNodeExt(i);
				if(n.getOutgoingEdgeList().size() == 0){
					/* create an edge */
					edges.add(GraphExtentionFactory.createEdgeExtention(n, exitNode));
				}
			}
		}
		
		/* create start Vertex */
		if(createStartVertex){
			startNode = GraphExtentionFactory.createNodeExtention(null);
			startNode.setToolTipText(VIRTUAL_START_NODE_TOOLTIP_TEXT);
			startNode.setVertexType(INodeType.NODE_TYPE_START);
			
			/* create edge to the first node */
			for(int i = 0; i < nodes.size(); i++){
				INodeExt n = nodes.getNodeExt(i);
				if(n.getIncomingEdgeList().size() == 0){
					
					if(n.getData()!= null && n.getData().equals("B1")){
					/* create an edge */
					edges.add(GraphExtentionFactory.createEdgeExtention(startNode, n));
					break;
					}
				}
			}
		}
	
		if(createExitVertex && exitNode != null){
			nodes.add(exitNode);
		}
		
		if(createStartVertex && startNode != null){
			nodes.add(startNode);
		}
		
		/* create back edge */
		if(createBackEdge){ 
			if(startNode != null & exitNode != null){
				IEdgeExt edge = GraphExtentionFactory.createEdgeExtention(exitNode, startNode);
				edge.setData(VIRTUAL_BACK_EDGE_TOOLTIP_TEXT);
				edges.add(edge);
			}
			else{
				throw new ControlFlowGraphException("Cannot create virtual back edge. Start or Exit node missing.");//$NON-NLS-1$
			}
		}
		/* use user object to store the line number table representation */
		if(lineNumberTable != null){
			String lineNumberTableStr = createLineNumberTableRespresenation(lineNumberTable);
			basicBlockGraph.getUserObject().put(ByteCodeConstants.LINE_NUMBER_TABLE, lineNumberTableStr);
		}
	
		
	  	return basicBlockGraph;
	}
	
	/** 
	 * Generates a control flow graph for the defined method.
	 * @param classpath
	 * @param package name
	 * @param class name
	 * @param method name
	 * @param method signature
	 * @return the control flow graph 
	 */
	public static IDirectedGraphExt generateBasicBlockGraph(String classPath[],
			String packageName,
			String className,
			String methodName,
			String methodSig,
			boolean createStartVertex,
			boolean createExitvertex,
			boolean createBackEdge)
			
	throws ControlFlowGraphException, IOException
	{
		FilteringCodeVisitor codeVisitor = getInstructionList(classPath, packageName, className, methodName, methodSig);
		return ControlFlowGraphGenerator.generateBasicBlockGraph(codeVisitor.getInstructions(), codeVisitor.getLineNumberTable(), createStartVertex, createExitvertex, createBackEdge);
	}
	
	/**
	 * Returns a code visitor instance. Used for generation 
	 * all graphs of a class.
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static AllCodeVisitor getClassFileVisitor(InputStream in) throws IOException{
		AllCodeVisitor codeVisitor = new AllCodeVisitor();
        MethodFilteringVisitor classVisitor = new MethodFilteringVisitor(codeVisitor);
        ClassReader cr = new ClassReader(in, classVisitor);
        cr.accept(classVisitor, 0);
        
        return codeVisitor;
	}
	
	/**
	 * Returns instruction list. 
	 * @param classPath
	 * @param packageName
	 * @param className
	 * @param methodName
	 * @param methodSig
	 * @return instructions
	 * @throws ControlFlowGraphException
	 * @throws IOException
	 */
	private static FilteringCodeVisitor getInstructionList(String classPath[],
			String packageName,
			String className,
			String methodName,
			String methodSig) 
	throws ControlFlowGraphException, IOException
	{
		InputStream in = JavaLangUtils.findResource(classPath, packageName, className);
		if(in == null){
			String msg = "Class '" + className +"' not found in the CLASSPATH.";
			throw new ControlFlowGraphException(msg);
		}
		
		if (!(in instanceof BufferedInputStream)) {
			/* buffer only if necessary */
			in = new BufferedInputStream(in);
		}
		
		
        FilteringCodeVisitor codeVisitor = new FilteringCodeVisitor(methodName, methodSig);
        MethodFilteringVisitor classVisitor = new MethodFilteringVisitor(codeVisitor);
        ClassReader cr = new ClassReader(in, classVisitor);
        cr.accept(classVisitor, 0);
		if (codeVisitor.getInstructions() == null) {
			throw new ControlFlowGraphException(
					"ControlFlowGraphGenerator: can't get method info of the "
							+ methodName + methodSig);

		}
		
		return codeVisitor;
	}

	/**
	 * Parse instruction list and generates the list of edges for the control flow graph.
	 * @param instructions
	 * @param edges
	 * @param hashTable
	 */
	private  static void parseInstructionList(List<AbstractInstruction> instructions, IEdgeListExt edges, HashMap<Integer, INodeExt> hashTable){
		
		AbstractInstruction currentInstruction = null;

		/* get first instruction */
		if(instructions.size()!= 0){
			currentInstruction = (AbstractInstruction)instructions.get(0);
		}
		
		/* start loop */                    
		for(int i = 1; i< instructions.size();i++){
			if(debug) debug("------------- LOOP ------------------");

			if(debug) debug("curentInstr = " + currentInstruction.getOffset() + " " + currentInstruction.getOpcodeMnemonic());
			
			/* handle TableSwitchInstruction */
			if (currentInstruction instanceof TableSwitchInstruction){
				if(debug) debug("curentInstr: " + currentInstruction.getOffset() + " is a TableSwitchInstruction");
				
				TableSwitchInstruction tableSwitchInstruction = (TableSwitchInstruction)currentInstruction;
				int   start = tableSwitchInstruction.getOffset();                  
				int[] switchOffsets = tableSwitchInstruction.getJumpOffsets();
				int   defaultOffset = tableSwitchInstruction.getDefaultOffset();

				/* get node reference */
				Integer startVertex = new Integer(start);
				INodeExt startNode= hashTable.get(startVertex);
				
				int low = tableSwitchInstruction.getLow();
				
				StringBuffer buf = new StringBuffer();
//				buf.append(startNode.getByteCodeString());
				for(int off = 0; off < switchOffsets.length; off++){
					buf.append("      ");
					buf.append(String.valueOf(low + off));
					buf.append(" => ");
					buf.append(switchOffsets[off] + start);
					buf.append("\n");
				}
				buf.append("      default: ");
				buf.append(defaultOffset + start);
				
				startNode.setLongDescr(buf.toString());				
				
				/* insert switch arcs */
				Integer switchVertex = null;
				IEdgeExt newEdge;
//				int low = tableSwitchInstruction.getLow();
				for(int j = 0; j < switchOffsets.length; j++){
					/* switch offsets are relative */
					switchVertex = new Integer((switchOffsets[j] + start));
					newEdge = GraphExtentionFactory.createEdgeExtention(startNode, hashTable.get(switchVertex));
					newEdge.setData(String.valueOf(low + j)); /* set Text label */
					edges.add(newEdge);      	     			
					if(debug) debug("addArc: source=" + startVertex + " target=" + switchVertex); 
				}                  

				/* default */
				switchVertex = new Integer((defaultOffset + start));
				newEdge = GraphExtentionFactory.createEdgeExtention(startNode, hashTable.get(switchVertex));
				newEdge.setData("default"); /*set text label */
				edges.add(newEdge);  
				if(debug) debug("default addArc: source=" + startVertex + " target=" + switchVertex);
				
				if(i < instructions.size()){
					currentInstruction = (AbstractInstruction)instructions.get(i);
				}
				
				continue;
			}       
			/* handle LookupSwitchInstruction */
			else if (currentInstruction instanceof LookupSwitchInstruction){
				if(debug) debug("curentInstr: " + currentInstruction.getOffset() + " is a LookupSwitchInstruction");
				
				LookupSwitchInstruction lookupSwitchInstruction = (LookupSwitchInstruction)currentInstruction;
				
				int   start = lookupSwitchInstruction.getOffset();
				List<MatchOffsetEntry> matchOffsetPairs = lookupSwitchInstruction.getMatchOffsetPairs();
				int   defaultOffset = lookupSwitchInstruction.getDefaultOffset(); 

				/* get node reference */
				Integer startVertex = new Integer(start);
				INodeExt startNode= hashTable.get(startVertex);
				
				
				
				StringBuffer buf = new StringBuffer();
//				buf.append(startNode.getByteCodeString());
//				buf.append("\n");
				for(MatchOffsetEntry e: matchOffsetPairs){
					buf.append("      ");
					buf.append(e.getMatch());
					buf.append(" => ");
					
					int val = e.getOffset();
					buf.append(String.valueOf(val + start));
					buf.append("\n");
					
				}
				buf.append("      default: ");
				buf.append(defaultOffset + start);
				
				startNode.setLongDescr(buf.toString());
				
				Integer offsetVertex = null;
				IEdgeExt newEdge;
				for (int k = 0; k < matchOffsetPairs.size(); k++) {
					MatchOffsetEntry matchOffsetEntry = (MatchOffsetEntry)matchOffsetPairs.get(k);
					offsetVertex = new Integer(matchOffsetEntry.getOffset() + start);
					newEdge = GraphExtentionFactory.createEdgeExtention(startNode, hashTable.get(offsetVertex));
					newEdge.setData(String.valueOf(matchOffsetEntry.getMatch()));/* set text label */
					edges.add(newEdge);         
					if(debug) debug("default addArc: source=" + startVertex + " target=" + offsetVertex);               
				}

				/* default off*/
				offsetVertex = new Integer((defaultOffset + start));
				newEdge = GraphExtentionFactory.createEdgeExtention(startNode, hashTable.get(offsetVertex));
				newEdge.setData("default");
				edges.add(newEdge); 
				if(debug) debug("default addArc: source=" + startVertex + " target=" + offsetVertex);
				
				if(i < instructions.size()){
					currentInstruction = (AbstractInstruction)instructions.get(i);
				}
				
				continue;
			}
			else if (currentInstruction instanceof BranchInstruction) {
				/* handle BranchInstruction if_ne, if_icmpeq ... 
				 * goto is branch instruction as well */
				
				if(debug) debug("curentInstr: " + currentInstruction.getOffset() + " is BranchInstruction");                                                  
				
				/* create arc */
				Integer start = new Integer(currentInstruction.getOffset());
				
				/*
				 * The branch target is a relative byte code address.
				 * 13 ifle 11;
  				 * 16 new 37;
				 * 19 dup;
				 * 20 invokespecial 39;
				 * 23 athrow;
				 * 24 return;
				 */
				Integer branchIntruction = new Integer((((BranchInstruction)currentInstruction).getBranchOffset()
						+ ((BranchInstruction)currentInstruction).getOffset()));
				IEdgeExt newEdge = GraphExtentionFactory.createEdgeExtention(hashTable.get(start), hashTable.get(branchIntruction));
				edges.add(newEdge);				
				if(debug) debug("addArc: source=" + start + " target=" + branchIntruction); 

				/* create second arc only if the instruction not a goto*/
				if(!ControlFlowGraphUtils.isJumpInstruction(currentInstruction.getOpcode())){
					newEdge.setData("true");
					currentInstruction = (AbstractInstruction)instructions.get(i);
					newEdge = GraphExtentionFactory.createEdgeExtention(hashTable.get(start), hashTable.get(currentInstruction.getOffset()));
					newEdge.setData("false");
					edges.add(newEdge);				
					if(debug) debug("addArc: source=" + start + " target=" + currentInstruction.getOffset());		
				}
				else{
					currentInstruction = (AbstractInstruction)instructions.get(i);	
				}

				/* to the begin of the loop */
				continue;
			}  
		
			/* handle return instruction return and athrow */
			if(ControlFlowGraphUtils.isReturn(currentInstruction.getOpcode())){
				
				/* no arc has to be created */
				
				currentInstruction = (AbstractInstruction)instructions.get(i);
				/* to the begin of the loop */
				continue;
			}
			
			/* Default 
			 * create an arc between two sequent instructions.
			 * For example:
  			 *   16 new 37;
			 *   19 dup;
			 *   20 invokespecial 39;
			 * 
			 *   Arcs 16 -> 19
			 *        19 -> 20
			 */
			if(i < instructions.size()){
				Integer start = new Integer(currentInstruction.getOffset());
				currentInstruction = (AbstractInstruction)instructions.get(i);
			
				edges.add(GraphExtentionFactory.createEdgeExtention(hashTable.get(start), hashTable.get(currentInstruction.getOffset())));				
				if(debug) debug("addArc: source=" + start + " target=" + currentInstruction.getOffset());
				
				/* to the begin of the loop */
			}
			
			if(debug) debug("------------- END LOOP ---------------");
			
		}/* end for main loop */
		
		/* check if the last instruction is a goto instruction */
		if(debug) debug("curentInstr = " + currentInstruction.getOffset() + " " + currentInstruction.getOpcodeMnemonic());
		
		if(ControlFlowGraphUtils.isJumpInstruction(currentInstruction.getOpcode())){
			
			/* create arc */
			Integer start = new Integer(currentInstruction.getOffset());
			
			/*
			 * The branch target is a relative byte code address.
			 * 10 goto -10;
			 */
			Integer branchIntruction = new Integer((((BranchInstruction)currentInstruction).getBranchOffset()
					+ ((BranchInstruction)currentInstruction).getOffset()));
			IEdgeExt newEdge = GraphExtentionFactory.createEdgeExtention(hashTable.get(start), hashTable.get(branchIntruction));
			edges.add(newEdge);				
			if(debug) debug("addArc: source=" + start + " target=" + branchIntruction); 

		}

	}

	
	/**
	 * Create key of the edge.
	 * @param sourceNode
	 * @param targetNode
	 * @return
	 */
	private static String createKey(INodeExt sourceNode, INodeExt targetNode){
		StringBuffer buf = new StringBuffer();
		buf.append(sourceNode.getByteCodeOffset());
		buf.append(targetNode.getByteCodeOffset());
		return buf.toString();
	}
	
	/**
	 * Returns the node type defined in INodeType
	 * (@see com.drgarbage.visualgraphic.controlflowgraph.intf.INodeType).
	 * @param node
	 * @return type
	 */
	@SuppressWarnings("unchecked")
	private static int getNodeType(INodeExt node){
		int type = INodeType.NODE_TYPE_SIMPLE;
		List<INodeExt> data = (List<INodeExt>)node.getData();
		for(INodeExt n: data){
			/* if the number of the outgoing edges < 2 then you will recive an exception during layouting */
			if(n.getVertexType() == INodeType.NODE_TYPE_IF && node.getOutgoingEdgeList().size() == 2){
				type = INodeType.NODE_TYPE_IF;
				return type;
			}
			else if(n.getVertexType() == INodeType.NODE_TYPE_SWITCH){
				type = INodeType.NODE_TYPE_SWITCH;
				return type;
			}
			else if(n.getVertexType() == INodeType.NODE_TYPE_RETURN){
				type = INodeType.NODE_TYPE_RETURN;
				return type;
			}			
//			else if(n.getVertexType() == INodeType.NODE_TYPE_INVOKE){
//				type = INodeType.NODE_TYPE_INVOKE;			
//			}
		}	
		
		return type;
	}

	/**
	 * Gets line number from number table for given bytecode address.
	 * @param lntb
	 * @param bycodeindex
	 * @return line number
	 */
	private static int getSourceCodeLine(LineNumberTableEntry[] lntb, int bycodeindex){
		if(lntb == null)
			return -1;

		for(int i = lntb.length - 1; i >= 0 ; i--){
			if(lntb[i].getStartPc() <= bycodeindex)
				return lntb[i].getLineNumber();
		}

		return -1;
	}
	
	/**
	 * Creates a tooltip string for a source code node.
	 * @param node
	 * @return tooltip String
	 */
	@SuppressWarnings("unchecked")
	private static String createToolTip(INodeExt node){
		StringBuffer buf = new StringBuffer();
		buf.append(" Line: ");
		buf.append(node.getByteCodeOffset());		
		List<INodeExt> data = (List<INodeExt>)node.getData();
		for(INodeExt n: data){
//			Object o = n.getData();
			buf.append("\n");
			buf.append("  ");
			buf.append(n.getByteCodeOffset());
			buf.append(" ");
			buf.append(n.getByteCodeString());
			if(n.getLongDescr() != null){
				buf.append("\n");
				buf.append(n.getLongDescr());
//				buf.append("\n");
			}
			
			buf.append(" ");
		}
		return buf.toString();
	}
	
	/**
	 * TODO: fix representation
	 * @param lineNumberTable
	 * @return string
	 */
	private static String createLineNumberTableRespresenation(LineNumberTableEntry[] lineNumberTable){
		if(lineNumberTable == null){
			return null;
		}

		StringBuffer buf = new StringBuffer();
		buf.append(" Line Number Table:\n");
		buf.append("--------------------\n");
		for(LineNumberTableEntry lne: lineNumberTable ){
			buf.append("|\t");
			buf.append(lne.getLineNumber());
			buf.append("\t|\t");
			buf.append(lne.getStartPc());
			buf.append("|\n");
			
		}
		buf.append("--------------------\n");
		
		return buf.toString();
	}
	
}
