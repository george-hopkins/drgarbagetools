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
package com.drgarbage.bytecodevisualizer.operandstack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.runtime.IStatus;

import com.drgarbage.algorithms.Algorithms;
import com.drgarbage.asm.render.intf.IInstructionLine;
import com.drgarbage.asm.render.intf.ILocalVariableTable;
import com.drgarbage.bytecode.ByteCodeConstants;
import com.drgarbage.bytecode.BytecodeUtils;
import com.drgarbage.bytecode.ExceptionTableEntry;
import com.drgarbage.bytecode.constant_pool.AbstractConstantPoolEntry;
import com.drgarbage.bytecode.constant_pool.ConstantClassInfo;
import com.drgarbage.bytecode.constant_pool.ConstantDoubleInfo;
import com.drgarbage.bytecode.constant_pool.ConstantFieldrefInfo;
import com.drgarbage.bytecode.constant_pool.ConstantFloatInfo;
import com.drgarbage.bytecode.constant_pool.ConstantIntegerInfo;
import com.drgarbage.bytecode.constant_pool.ConstantLongInfo;
import com.drgarbage.bytecode.constant_pool.ConstantNameAndTypeInfo;
import com.drgarbage.bytecode.constant_pool.ConstantReference;
import com.drgarbage.bytecode.constant_pool.ConstantStringInfo;
import com.drgarbage.bytecode.constant_pool.ConstantUtf8Info;
import com.drgarbage.bytecode.instructions.AbstractInstruction;
import com.drgarbage.bytecode.instructions.ConstantPoolByteIndexInstruction;
import com.drgarbage.bytecode.instructions.ConstantPoolShortIndexInstruction;
import com.drgarbage.bytecode.instructions.IConstantPoolIndexProvider;
import com.drgarbage.bytecode.instructions.ILocalVariableIndexProvider;
import com.drgarbage.bytecode.instructions.ImmediateByteInstruction;
import com.drgarbage.bytecode.instructions.ImmediateShortInstruction;
import com.drgarbage.bytecode.instructions.MultianewarrayInstruction;
import com.drgarbage.bytecode.instructions.Opcodes;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.controlflowgraph.ControlFlowGraphGenerator;
import com.drgarbage.controlflowgraph.intf.GraphUtils;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;
import com.drgarbage.javasrc.JavaLexicalConstants;

/**
 * Operand Stack Algorithms.
 * 
 * @author Andreas Karoly and Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class OperandStack implements Opcodes{

	private AbstractConstantPoolEntry[] classConstantPool;
	private ILocalVariableTable localVariableTable;
	private ExceptionTableEntry[] exceptionTable;
	private IDirectedGraphExt graph;
	private IEdgeListExt backEdges;
	private int maxStackSize;
	private boolean stackError = false;
	private Map<Integer, String> tmpLocalVariableTable;
	
	/* statistic counters for time and memory complexity */
	private long elapsedTime = -1, memoryConsumption = -1;

	/**
	 * Stack representation format:
	 * <ul>
	 *  <li>SIMPLE -  stack entries are represented by '*' characters.</li>
	 *  <li>TYPES - a list of variable types of all entries on the stack separated by ',' is created.</li>
	 *  <li>ALL - the representation includes variable types, variable names and all possible 
	 *  combinations of variables</li>
	 * </ul>
	 */
	public static enum OpstackRepresenation{
		SIMPLE,
		TYPES,
		VALUES,
		ALL;
	};

	public static int UNKNOWN_SIZE = -1;

	/**
	 * Operand Stack property constants to
	 * address the node stack property 
	 * objects assigned to a node. 
	 */
	public static enum OperandStackPropertyConstants{
		NODE_INSTR_OBJECT,
		NODE_STACK,
		ERROR_EXCEPTION
	}
	
	/* Operand Stack specific constants */
	public static String ANY_EXCEPTION = "<any exception>";
	public static String RETURN_VALUE = "<return value>";
	public static String ADDRESS_VALUE = "<return address>";
	public static String EMPTY_STACK = "<empty>";
	
	/* Java class file constants */
	public static String B_BYTE = String.valueOf(ByteCodeConstants.B_BYTE);
	public static String C_CHAR = String.valueOf(ByteCodeConstants.C_CHAR);
	public static String D_DOUBLE = String.valueOf(ByteCodeConstants.D_DOUBLE);
	public static String F_FLOAT = String.valueOf(ByteCodeConstants.F_FLOAT);
	public static String I_INT = String.valueOf(ByteCodeConstants.I_INT);
	public static String J_LONG = String.valueOf(ByteCodeConstants.J_LONG);
	public static String S_SHORT = String.valueOf(ByteCodeConstants.S_SHORT);
	public static String Z_BOOLEAN = String.valueOf(ByteCodeConstants.Z_BOOLEAN);
	public static String L_REFERENCE = String.valueOf(ByteCodeConstants.L_REFERENCE);
	public static String VOID = "V";
	
	/**
	 * Converts the stack object into the string representation
	 * in the <code>SIMPLE</code> format (see {@link OpstackRepresenation}).
	 * @param stack - the stack object
	 * @return a string representation
	 */
	public static String stackToString(Stack<OperandStackEntry> stack){
		return stackToString(stack, OpstackRepresenation.SIMPLE);
	}

	/**
	 * Converts the stack object into the string representation.
	 * @param stack the stack object.
	 * @param format the stack representation format. Value is one of {@link OpstackRepresenation}.
	 * @return a string representation
	 */
	public static String stackToString(Stack<OperandStackEntry> stack, OpstackRepresenation format){
		StringBuffer buf = new StringBuffer();

		if(format == OpstackRepresenation.SIMPLE){
			/* convert stack to string */
			for (int i = 0; i < stack.size(); i++){
				buf.append("*");
			}

			return buf.toString();
		}

		/* convert stack to string */
		if(stack.size() == 0){
			return EMPTY_STACK;
		}

		for (Enumeration<OperandStackEntry> en = stack.elements(); en.hasMoreElements();){
			OperandStackEntry ose = en.nextElement();
			if(format == OpstackRepresenation.TYPES){
				buf.append(ose.getVarType());
			}
			else if(format == OpstackRepresenation.VALUES){
				buf.append(ose.getValue());
			}
			else
			{/* ALL */
				buf.append(ose.getVarType());
				buf.append(" ");
				buf.append(ose.getValue());
			}

			buf.append(", ");
		}

		if(buf.length() > 2){ /* cut the last ' ,' ' ' */
			buf.deleteCharAt(buf.length()-1);
			buf.deleteCharAt(buf.length()-1);
		}

		return buf.toString();

	}

	/**
	 * Converts the list of stack objects into the string representation
	 * in the <code>SIMPLE</code> format (see {@link OpstackRepresenation}).
	 * 
	 * @param stackList the list of stacks.
	 * @return a string representation
	 */
	public static String stackListToString(List<Stack<OperandStackEntry>> stackList){
		return stackListToString(stackList, OpstackRepresenation.ALL);
	}

	/**
	 * Converts the list of stack objects into the string representation.
	 * 
	 * @param stackList the list of stacks.
	 * @param format the stack representation format. Value is one of {@link OpstackRepresenation}.
	 * @return a string representation
	 */
	public static String stackListToString(List<Stack<OperandStackEntry>> stackList, OpstackRepresenation format){
		StringBuffer buf = new StringBuffer();
		for(Stack<OperandStackEntry> s: stackList){
			buf.append(stackToString(s, format));
			buf.append(" | ");
		}

		if(buf.length() >= 3){ /* cut the last ' ', '|' , ' ' */
			buf.deleteCharAt(buf.length()-1);
			buf.deleteCharAt(buf.length()-1);
			buf.deleteCharAt(buf.length()-1);
		}

		return buf.toString();
	}

	/**
	 * Creates the operand stack object for the given method.
	 * @param cPool reference to the constant pool of the class
	 * @param instructions byte code instructions of the method 
	 */
	public OperandStack(List<IInstructionLine> instructions,
			AbstractConstantPoolEntry[] cPool,
			ILocalVariableTable locVarTable,
			ExceptionTableEntry[] excepTable){
		classConstantPool = cPool;
		localVariableTable = locVarTable;
		exceptionTable = excepTable;
		maxStackSize = 0;
		
		/* local variable table */
		tmpLocalVariableTable = new TreeMap<Integer, String>();

		/* initialize time and memory counters */
		long start = System.currentTimeMillis();

		/* call garbage collection to free the memory */
		System.gc();
		long memory = Runtime.getRuntime().freeMemory();

		/* generate operand stack entries */
		generateOperandStack(instructions);

		/* store the elapsed time and consumed memory */
		elapsedTime = System.currentTimeMillis() - start;
		memoryConsumption = memory - Runtime.getRuntime().freeMemory();
	}

	/**
	 * Returns the transformed graph for displaying the
	 * stack structure in the operand stack view.
	 * @return graph
	 */
	public IDirectedGraphExt getOperandStackGraph() {
		return graph;
	}
	
	/**
	 * Returns the list of the back edges as identified 
	 * by the DFS algorithm.
	 * @return the list of back edges
	 */
	public IEdgeListExt getBackEdges(){
		return backEdges;
	}

	/**
	 * getter for the maximum stack size
	 * @return
	 */
	public int getMaxStackSize() {
		return maxStackSize;
	}

	/**
	 * Returns the elapsed time in ms of the
	 * operand stack generation. 
	 * 
	 * @return elapsed time in ms
	 */
	public long getElapsedTime() {
		return elapsedTime;
	}

	/**
	 * Returns the consumed memory for the generated
	 * operand stack entries in byte.
	 * @return memory in bytes
	 */
	public long getMemoryConsumption() {
		return memoryConsumption;
	}



	/**
	 * Returns the number of operand stacks.
	 * @return the number
	 */
	public long getNumberOfStacks() {

		long numberOfStacks = 0;

		INodeListExt nodeList = graph.getNodeList();
		for(int i = 0; i < nodeList.size(); i++){
			INodeExt n = nodeList.getNodeExt(i);
			Object o = n.getData();
			if(o instanceof Map){
				@SuppressWarnings("unchecked")
				Map<OperandStackPropertyConstants, Object> nodeMap = (Map<OperandStackPropertyConstants, Object>) o;
				o = nodeMap.get(OperandStackPropertyConstants.NODE_STACK);
				if(o != null){
					NodeStackProperty nsp = (NodeStackProperty)o;
					List<Stack<OperandStackEntry>> listOfStacks = nsp.getStackAfter();
					for( Stack<OperandStackEntry> s: listOfStacks){
						if(s.size() != 0){
							numberOfStacks++;
						}
					}
				}
			}
		}

		return numberOfStacks;
	}
	
	/**
	 * Returns the max number of operand stacks assigned to
	 * an instruction in the current operand stack.
	 * @return the number
	 */
	public int getMaxNumberOfStacks() {
		int maxNumberOfStacks = 0;

		INodeListExt nodeList = graph.getNodeList();
		for(int i = 0; i < nodeList.size(); i++){
			INodeExt n = nodeList.getNodeExt(i);
			Object o = n.getData();
			if(o instanceof Map){
				@SuppressWarnings("unchecked")
				Map<OperandStackPropertyConstants, Object> nodeMap = (Map<OperandStackPropertyConstants, Object>) o;
				o = nodeMap.get(OperandStackPropertyConstants.NODE_STACK);
				if(o != null){
					NodeStackProperty nsp = (NodeStackProperty)o;
					List<Stack<OperandStackEntry>> listOfStacks = nsp.getStackAfter();
					int numberOfStacks = 0;
					for( Stack<OperandStackEntry> s: listOfStacks){
						if(s.size() != 0){
							numberOfStacks++;
						}
					}
					if(maxNumberOfStacks < numberOfStacks){
						maxNumberOfStacks = numberOfStacks;
					}
				}
			}
		}

		return maxNumberOfStacks;
	}

	/**
	 * Calculates and returns the maximal number of stack combinations
	 * in the current operand stack.
	 * @return max number of stack combination
	 */
	public int getMaxNumberOfStackCombinations() {
		int maxNumberOfStacksCombinations = 0;

		INodeListExt nodeList = graph.getNodeList();
		for(int i = 0; i < nodeList.size(); i++){
			List<Stack<OperandStackEntry>> listOfStacks = getStackBefore(nodeList.getNodeExt(i));
			int numberOfStacks = 0;
			for( Stack<OperandStackEntry> s: listOfStacks){
				if(s.size() != 0){
					numberOfStacks++;
				}
			}
			if(maxNumberOfStacksCombinations < numberOfStacks){
				maxNumberOfStacksCombinations = numberOfStacks;
			}
		}

		return maxNumberOfStacksCombinations;
	}
	
	
	
	/**
	 * Returns the number of operand stack entries.
	 * @return the number
	 */
	public long getNumberOfStackEntries() {
		long numberOfStackEntries = 0;

		INodeListExt nodeList = graph.getNodeList();
		for(int i = 0; i < nodeList.size(); i++){
			INodeExt n = nodeList.getNodeExt(i);
			Object o = n.getData();
			if(o instanceof Map){
				@SuppressWarnings("unchecked")
				Map<OperandStackPropertyConstants, Object> nodeMap = (Map<OperandStackPropertyConstants, Object>) o;
				o = nodeMap.get(OperandStackPropertyConstants.NODE_STACK);
				if(o != null){
					NodeStackProperty nsp = (NodeStackProperty) o;
					List<Stack<OperandStackEntry>> listOfStacks = nsp.getStackAfter();
					for( Stack<OperandStackEntry> s: listOfStacks){
						numberOfStackEntries += s.size();
					}
				}
			}
		}
		return numberOfStackEntries;	
	}

	/**
	 * Returns the number of operand stack entries.
	 * @return the number
	 */
	public long getMaxNumberOfStackEntries() {
		long maxNumberOfStackEntries = 0;

		INodeListExt nodeList = graph.getNodeList();
		for(int i = 0; i < nodeList.size(); i++){
			INodeExt n = nodeList.getNodeExt(i);
			Object o = n.getData();
			if(o instanceof Map){
				@SuppressWarnings("unchecked")
				Map<OperandStackPropertyConstants, Object> nodeMap = (Map<OperandStackPropertyConstants, Object>) o;
				o = nodeMap.get(OperandStackPropertyConstants.NODE_STACK);
				if(o != null){
					NodeStackProperty nsp = (NodeStackProperty) o;
					List<Stack<OperandStackEntry>> listOfStacks = nsp.getStackAfter();
					for( Stack<OperandStackEntry> s: listOfStacks){
						if(maxNumberOfStackEntries < s.size()){
							maxNumberOfStackEntries = s.size();
						}
					}
				}
			}
		}
		return maxNumberOfStackEntries;	
	}

	/**
	 * Generates the operand stack.
	 * @param instructions list of instructions
	 */
	private void generateOperandStack(List<IInstructionLine> instructions){
		graph = ControlFlowGraphGenerator.generateSynchronizedControlFlowGraphFrom(instructions, true);

		/* remove back edges (loops) from the graph */
		removeBackEdges(graph);

		/* parse the graph */
		List<INodeExt> listOfStartNodes = getAllStartNodes(graph);
		for(INodeExt n: listOfStartNodes){
			stackError = false;
			parseGraph(n);
		}

	}

	/**
	 * Removes all back edges from the edge list and 
	 * incidence lists of nodes.
	 * @param graph control flow graph
	 */
	private void removeBackEdges(IDirectedGraphExt graph){
		
		backEdges = Algorithms.doFindBackEdgesAlgorithm(graph);
		GraphUtils.clearGraph(graph);
		GraphUtils.clearGraphColorMarks(graph);
		
		IEdgeListExt edges = graph.getEdgeList();
		for(int i = 0; i < backEdges.size(); i++){
			IEdgeExt e = backEdges.getEdgeExt(i);
			INodeExt source = e.getSource(); 
			INodeExt target = e.getTarget();
			
			source.getOutgoingEdgeList().remove(e);
			target.getIncomingEdgeList().remove(e);
			edges.remove(e);
		}
	}

	/**
	 * Returns the list of all nodes with incoming degree of 0.
	 * @param graph control flow graph
	 * @return list of start nodes
	 */
	private List<INodeExt> getAllStartNodes(IDirectedGraphExt graph){
		List<INodeExt> listOfStartNodes= new ArrayList<INodeExt>();
		INodeListExt nodes = graph.getNodeList();
		for(int i = 0; i < nodes.size(); i++){
			INodeExt n = nodes.getNodeExt(i);
			if(n.getIncomingEdgeList().size() == 0){
				listOfStartNodes.add(n);
			}
		}

		return listOfStartNodes;
	}

	/**
	 * Topological sort.
	 * @param n start node of the graph
	 */
	private void parseGraph(INodeExt n){
		INodeExt node = n;
		while(node != null){

			if(node.isVisited()){
				return;
			}

			IEdgeListExt inList = node.getIncomingEdgeList();
			for(int j = 0; j < inList.size(); j++){
				if(!inList.getEdgeExt(j).isVisited()){
					return;
				}
			}

			/* Calculate the operand stack and assign it to the current node */
			Object o = node.getData();    		
			if(o instanceof IInstructionLine){
				IInstructionLine iLine = (IInstructionLine) o;
				Map<OperandStackPropertyConstants, Object> nodeMap = new HashMap<OperandStackPropertyConstants, Object>();
				nodeMap.put(OperandStackPropertyConstants.NODE_INSTR_OBJECT, node.getData());
				node.setData(nodeMap);
				try{
					if(!stackError){
						calculateOperandStack(node, iLine.getInstruction());
					}
				}
				catch(EmptyStackException e){
					/* Stack underFlow */
					stackError = true;
					nodeMap.put(OperandStackPropertyConstants.ERROR_EXCEPTION, e);
				}
			}

			node.setVisited(true);

			IEdgeListExt outList = node.getOutgoingEdgeList();
			if(outList.size() == 0){
				return;
			}

			if(outList.size() == 1){
				IEdgeExt edge = outList.getEdgeExt(0);
				edge.setVisited(true);
				node = edge.getTarget();
			}
			else{ 		
				for(int i = 0; i < outList.size(); i++){
					IEdgeExt edge = outList.getEdgeExt(i);
					edge.setVisited(true);

					parseGraph(edge.getTarget());
				}
			}
		} 
	}

	/**
	 * Calculates the operand stack for the current byte code instruction.
	 * @param node
	 * @param i
	 */
	private void calculateOperandStack(INodeExt node, AbstractInstruction i){

		List<Stack<OperandStackEntry>> listOfStacks = getStackBefore(node, i); 

		for(Stack<OperandStackEntry> s: listOfStacks){
			processInstruction(i, s);
		}

		/* remove duplicates */
		Map<String, Stack<OperandStackEntry>> m = new TreeMap<String, Stack<OperandStackEntry>>();
		for(Stack<OperandStackEntry> s: listOfStacks){
			m.put(stackToString(s, OpstackRepresenation.ALL), s);
		}

		listOfStacks.clear();
		for(Stack<OperandStackEntry> s : m.values()){
			Stack<OperandStackEntry> stack = new Stack<OperandStackEntry>();    	
			stack.addAll(s);
			listOfStacks.add(stack);    		    	
		}

		/* update the maxStackSize */
		for(Stack<OperandStackEntry> e: listOfStacks){
			int stackSize = 0;
			for(OperandStackEntry ose: e){
				stackSize += (ose.getLength() / 4);
			}
			
			if(stackSize > maxStackSize){
				maxStackSize = stackSize;
			}
		}

		/* assign the property object */
		NodeStackProperty prop = new NodeStackProperty(listOfStacks);

		Object o = node.getData();
		if(o instanceof Map){
			@SuppressWarnings("unchecked")
			Map<OperandStackPropertyConstants, Object> nodeMap = (Map<OperandStackPropertyConstants, Object>) o;
			nodeMap.put(OperandStackPropertyConstants.NODE_STACK, prop);		
		}

	}

	
	/**
	 * Calculates all possible stack states for the given node before the corresponding 
	 * byte code instruction has been executed.
	 * @param node is a vertex in the control flow graph
	 * @return list of stacks
	 */
	public List<Stack<OperandStackEntry>> getStackBefore(INodeExt node){
		return getStackBefore(node, null);
	}
	
	/**
	 * Calculates all possible stack states for the given node before the corresponding 
	 * byte code instruction has been executed.
	 * @param node is a vertex in the control flow graph
	 * @param i byte instruction object
	 * @return list of stacks
	 */
	public List<Stack<OperandStackEntry>> getStackBefore(INodeExt node, AbstractInstruction i){
		List<Stack<OperandStackEntry>> listOfStacks = new ArrayList<Stack<OperandStackEntry>>();
		IEdgeListExt incEdgeList = node.getIncomingEdgeList();

		/* entry nodes */
		if(incEdgeList.size() == 0){
			if(node.getByteCodeOffset() != 0){
				/* 
				 * The start node is initialized with an empty stack.
				 * For start nodes of exception handlers the exception 
				 * objects have to be put onto the stack.
				 */
				Set<String> exceptionList = new TreeSet<String>();
				for(ExceptionTableEntry ete: exceptionTable){
					if(node.getByteCodeOffset() == ete.getHandlerPc()){
						
						if(ete.getCatchType() != 0){ /* index = 0 has no references in the constant pool */
							String className = getConstantPoolClassName(ete.getCatchType(), classConstantPool);
							exceptionList.add(JavaLexicalConstants.LT + className + JavaLexicalConstants.GT);
						}
						else{
							exceptionList.add(ANY_EXCEPTION);
							if(i != null){
								/* handle special case: store unnamed variable */
								if(isStoreIstruction(i.getOpcode())){
									String name = getLocalVariableName(i);
									if(name == null){
										if (i instanceof ILocalVariableIndexProvider){
											int index = ((ILocalVariableIndexProvider)i).getLocalVariableIndex();
											tmpLocalVariableTable.put(index, ANY_EXCEPTION);
										}
									}
								}
							}
						}
					}
				}
				
				for(String e: exceptionList){
					Stack<OperandStackEntry> startStack = new Stack<OperandStackEntry>();
					startStack.add(new OperandStackEntry(null, 4, L_REFERENCE, e));
					listOfStacks.add(startStack);
				}
			}
			
			/* add an empty stack entry */
			if(listOfStacks.size() == 0){
				listOfStacks.add(new Stack<OperandStackEntry>());
			}
			

		}
		else{
			Map<String, Stack<OperandStackEntry>> m = new TreeMap<String, Stack<OperandStackEntry>>();
			for(int j = 0; j < incEdgeList.size(); j++){
				Object o = incEdgeList.getEdgeExt(j).getSource().getData();
				if(o instanceof Map){
					@SuppressWarnings("unchecked")
					Map<OperandStackPropertyConstants, Object> nodeMap = (Map<OperandStackPropertyConstants, Object>) o;
					o = nodeMap.get(OperandStackPropertyConstants.NODE_STACK);
					if(o != null){
						NodeStackProperty nsp = (NodeStackProperty) o;
						List<Stack<OperandStackEntry>> sl = nsp.getStackAfter(); 				

						for(Stack<OperandStackEntry> s: sl){
							/* no duplicates */
							m.put(stackToString(s, OpstackRepresenation.ALL), s);
						}
					}
				}
			}

			/* copy to the list */
			for(Stack<OperandStackEntry> s : m.values()){
				Stack<OperandStackEntry> stack = new Stack<OperandStackEntry>();    	
				stack.addAll(s);
				listOfStacks.add(stack);    		    	
			}
		}

		return listOfStacks;
	}

	/**
	 * Updates the stack by interpreting the byte code
	 * operation.  
	 * @param i byte code instruction
	 */
	private void processInstruction(AbstractInstruction i, Stack<OperandStackEntry> stack){

		switch (i.getOpcode()) {

		/* arrayref, index -> value */
		case OPCODE_AALOAD:
		case OPCODE_BALOAD:
		case OPCODE_CALOAD:
		case OPCODE_DALOAD:
		case OPCODE_FALOAD:
		case OPCODE_IALOAD:
		case OPCODE_LALOAD:
		case OPCODE_SALOAD:	
		{
			OperandStackEntry value2 = stack.pop();
			OperandStackEntry value1 = stack.pop();

			StringBuffer val = new StringBuffer(); 
			val.append(value1.getValue());
			val.append(JavaLexicalConstants.LEFT_SQUARE_BRACKET);
			val.append(value2.getValue());
			val.append(JavaLexicalConstants.RIGHT_SQUARE_BRACKET);

			stack.push(new OperandStackEntry(i, 4, getOperandTypeOfLoadInstruction(i.getOpcode()), val.toString()));
			return;
		}

		/* -> objectref */
		case OPCODE_ALOAD:
		case OPCODE_ALOAD_0:
		case OPCODE_ALOAD_1:
		case OPCODE_ALOAD_2:
		case OPCODE_ALOAD_3:
			/* reference from localVariableTable */
			stack.push(new OperandStackEntry(i, 4, L_REFERENCE, getLocalVariableName(i)));
			return;

		case OPCODE_NEW:
			/* reference from classConstantPool */
			stack.push(new OperandStackEntry(i, 4, L_REFERENCE, getConstantPoolClassName(i, classConstantPool)));
			return;

			/* -> value */
		case OPCODE_ILOAD:
		case OPCODE_ILOAD_0:
		case OPCODE_ILOAD_1:
		case OPCODE_ILOAD_2:
		case OPCODE_ILOAD_3:

			stack.push(new OperandStackEntry(i, 4, I_INT, getLocalVariableName(i)));
			return;

		case OPCODE_DLOAD:
		case OPCODE_DLOAD_0:
		case OPCODE_DLOAD_1:
		case OPCODE_DLOAD_2:
		case OPCODE_DLOAD_3:

			stack.push(new OperandStackEntry(i, 8, D_DOUBLE, getLocalVariableName(i)));
			return;

		case OPCODE_FLOAD:
		case OPCODE_FLOAD_0:
		case OPCODE_FLOAD_1:
		case OPCODE_FLOAD_2:
		case OPCODE_FLOAD_3:

			stack.push(new OperandStackEntry(i, 4, F_FLOAT, getLocalVariableName(i)));
			return;

		case OPCODE_LLOAD:
		case OPCODE_LLOAD_0:
		case OPCODE_LLOAD_1:
		case OPCODE_LLOAD_2:
		case OPCODE_LLOAD_3:

			stack.push(new OperandStackEntry(i, 8, J_LONG, getLocalVariableName(i)));
			return;

			/* arrayref, index, value-> */
		case OPCODE_AASTORE:
		case OPCODE_BASTORE:
		case OPCODE_CASTORE:
		case OPCODE_DASTORE:
		case OPCODE_FASTORE:
		case OPCODE_IASTORE:
		case OPCODE_LASTORE:
		case OPCODE_SASTORE:

			stack.pop();
			stack.pop();
			stack.pop();
			return;

			/* value -> */
		case OPCODE_ASTORE:
		case OPCODE_ASTORE_0:
		case OPCODE_ASTORE_1:
		case OPCODE_ASTORE_2:
		case OPCODE_ASTORE_3:

		case OPCODE_DSTORE:
		case OPCODE_DSTORE_0:
		case OPCODE_DSTORE_1:
		case OPCODE_DSTORE_2:
		case OPCODE_DSTORE_3:

		case OPCODE_FSTORE:
		case OPCODE_FSTORE_0:
		case OPCODE_FSTORE_1:
		case OPCODE_FSTORE_2:
		case OPCODE_FSTORE_3:

		case OPCODE_ISTORE:
		case OPCODE_ISTORE_0:
		case OPCODE_ISTORE_1:
		case OPCODE_ISTORE_2:
		case OPCODE_ISTORE_3:

		case OPCODE_LSTORE:
		case OPCODE_LSTORE_0:
		case OPCODE_LSTORE_1:
		case OPCODE_LSTORE_2:
		case OPCODE_LSTORE_3:

		case OPCODE_POP:
			stack.pop();		
			return;

		case OPCODE_POP2:
			OperandStackEntry ose = stack.pop();
			if(ose.getLength() == 4){
				stack.pop();
			}
			return;

			/* value -> */
		case OPCODE_ARETURN:
		case OPCODE_DRETURN:
		case OPCODE_FRETURN:
		case OPCODE_IRETURN:
		case OPCODE_LRETURN:
			stack.pop();
			return;

		case OPCODE_RETURN:
			return;

			/* -> null */
		case OPCODE_ACONST_NULL:
			stack.push(new OperandStackEntry(i, 4, L_REFERENCE, ByteCodeConstants.NULL));
			return;

			/* -> const */
		case OPCODE_ICONST_0:
			stack.push(new OperandStackEntry(i, 4, I_INT, "0"));
			return;
		case OPCODE_ICONST_1:
			stack.push(new OperandStackEntry(i, 4, I_INT, "1"));
			return;
		case OPCODE_ICONST_2:
			stack.push(new OperandStackEntry(i, 4, I_INT, "2"));
			return;
		case OPCODE_ICONST_3:
			stack.push(new OperandStackEntry(i, 4, I_INT, "3"));
			return;
		case OPCODE_ICONST_4:
			stack.push(new OperandStackEntry(i, 4, I_INT, "4"));
			return;
		case OPCODE_ICONST_5:
			stack.push(new OperandStackEntry(i, 4, I_INT, "5"));
			return;
		case OPCODE_ICONST_M1:
			stack.push(new OperandStackEntry(i, 4, I_INT, "-1"));
			return;
		case OPCODE_DCONST_0:
			stack.push(new OperandStackEntry(i, 8, D_DOUBLE, "0.0"));
			return;
		case OPCODE_DCONST_1:
			stack.push(new OperandStackEntry(i, 8, D_DOUBLE, "1.0"));
			return;
		case OPCODE_FCONST_0:
			stack.push(new OperandStackEntry(i, 4, F_FLOAT, "0.0F"));
			return;
		case OPCODE_FCONST_1:
			stack.push(new OperandStackEntry(i, 4, F_FLOAT, "1.0F"));
			return;
		case OPCODE_FCONST_2:
			stack.push(new OperandStackEntry(i, 4, F_FLOAT, "2.0F"));
			return;
		case OPCODE_LCONST_0:
			stack.push(new OperandStackEntry(i, 8, J_LONG, "0L"));
			return;
		case OPCODE_LCONST_1:
			stack.push(new OperandStackEntry(i, 8, J_LONG, "1L"));
			return;

			/* objectref -> [empty], objectref to throwable */
		case OPCODE_ATHROW:
		{
			/*
			 * athrow byte code instruction throws an error or exception 
			 * The rest of the stack is cleared, leaving only a reference to the 
			 * throwable object
			 * */
			OperandStackEntry throwbleRef = stack.lastElement();
			stack.clear();
			stack.push(throwbleRef);
			return;
		}

			/* -> value */
		case OPCODE_BIPUSH:
			/* 
			 * The immediate byte is sign-extended to an int value. 
			 * That value is pushed onto the operand stack. 
			 */
			stack.push(new OperandStackEntry(i, 4, I_INT, 
					Integer.toString(((ImmediateByteInstruction)i).getImmediateByte()))
					);
			return;

		case OPCODE_SIPUSH:
			/* 
			 * The immediate unsigned byte1 and byte2 values are assembled
			 * into an intermediate short, the intermediate value is 
			 * then sign-extended to an int value
			 */
			stack.push(new OperandStackEntry(i, 4, I_INT, 
					Integer.toString(((ImmediateShortInstruction)i).getImmediateShort()))
					);
			return;

			/* objectref -> objectref */
		case OPCODE_CHECKCAST:
			stack.pop();
			stack.push(new OperandStackEntry(i, 4, L_REFERENCE, getConstantPoolClassName(i, classConstantPool)));
			return;

			/* value -> result */
		case OPCODE_D2F:
		case OPCODE_I2F:
		case OPCODE_L2F:

			stack.push(new OperandStackEntry(i, 4, F_FLOAT, stack.pop().getValue()));
			return;

		case OPCODE_D2I:
		case OPCODE_F2I:
		case OPCODE_L2I:
			stack.push(new OperandStackEntry(i, 4, I_INT, stack.pop().getValue()));
			return;

		case OPCODE_D2L:
		case OPCODE_F2L:
		case OPCODE_I2L:
			stack.push(new OperandStackEntry(i, 4, J_LONG, stack.pop().getValue()));
			return;

		case OPCODE_F2D:
		case OPCODE_I2D:
		case OPCODE_L2D:
			stack.push(new OperandStackEntry(i, 8, D_DOUBLE, stack.pop().getValue()));
			return;

		case OPCODE_I2C:
			stack.push(new OperandStackEntry(i, 4, C_CHAR, stack.pop().getValue()));
			return;

		case OPCODE_I2S:
			stack.push(new OperandStackEntry(i, 4, S_SHORT, stack.pop().getValue()));
			return;

			/* value1, value2 -> result */
		case OPCODE_DADD:
		case OPCODE_LADD:

		case OPCODE_DDIV:
		case OPCODE_LDIV:

		case OPCODE_DMUL:
		case OPCODE_LMUL:

		case OPCODE_DREM:
		case OPCODE_LREM:
			
		case OPCODE_DSUB:
		case OPCODE_LSUB:
		{
			OperandStackEntry value2 = stack.pop();
			OperandStackEntry value1 = stack.pop();

			stack.push(new OperandStackEntry(
					i, 
					8, 
					value1.getVarType(), 
					mathOperation(value1.getValue(), value2.getValue(), i))
					);
			return;
		}			
			
		case OPCODE_IADD:
		case OPCODE_FADD:

		case OPCODE_IDIV:
		case OPCODE_FDIV:

		case OPCODE_IMUL:
		case OPCODE_FMUL:

		case OPCODE_IREM:
		case OPCODE_FREM:

		case OPCODE_ISUB:
		case OPCODE_FSUB:
		{
			OperandStackEntry value2 = stack.pop();
			OperandStackEntry value1 = stack.pop();
			
			stack.push(new OperandStackEntry(
					i, 
					4, 
					value1.getVarType(), 
					mathOperation(value1.getValue(), value2.getValue(), i))
					);
			return;
		}	
		/* value1 -> result */				
		case OPCODE_DNEG:
		case OPCODE_INEG:
		case OPCODE_FNEG:
		case OPCODE_LNEG:
		{
			OperandStackEntry negValue = stack.pop();
			
			StringBuffer buf = new StringBuffer();
			buf.append(JavaLexicalConstants.LEFT_PARENTHESIS);
			buf.append(JavaLexicalConstants.MINUS);
			buf.append(negValue.getValue());
			buf.append(JavaLexicalConstants.RIGHT_PARENTHESIS);
			
			stack.push(new OperandStackEntry(i, 
									i.getOpcode() == OPCODE_DNEG || i.getOpcode() == OPCODE_LNEG ? 8 : 4,
									negValue.getVarType(), 
									buf.toString())
					);
			return;
		}
			/* value1, value2 -> result */
		case OPCODE_LCMP:
		case OPCODE_DCMPG:
		case OPCODE_DCMPL:
		case OPCODE_FCMPG:
		case OPCODE_FCMPL:
			stack.pop();
			stack.pop();
			stack.push(new OperandStackEntry(i, 4, I_INT, RETURN_VALUE));
			return;


			/* -> value (from one indexbyte) */
		case OPCODE_LDC: /* 8-bit index ConstantPoolByteIndexInstruction*/
		case OPCODE_LDC_W: /* 16-bit index ConstantPoolShortIndexInstruction*/
		case OPCODE_LDC2_W: /* 16-bit index and pushes a two-word constant on the stack ConstantPoolShortIndexInstruction*/
		{
			String const_ = "?";
			if (i instanceof ConstantPoolShortIndexInstruction || i instanceof ConstantPoolByteIndexInstruction) {
				AbstractConstantPoolEntry cpInfo = classConstantPool[((IConstantPoolIndexProvider)i).getConstantPoolIndex()];

				if (cpInfo instanceof ConstantFloatInfo) {
					const_ = String.valueOf(((ConstantFloatInfo)cpInfo).getFloat());
					stack.push(new OperandStackEntry(i, i.getOpcode() == OPCODE_LDC2_W ? 8 : 4, F_FLOAT, const_));
					return;
				}
				else if (cpInfo instanceof ConstantIntegerInfo) {
					const_ = String.valueOf(((ConstantIntegerInfo)cpInfo).getInt());
					stack.push(new OperandStackEntry(i, i.getOpcode() == OPCODE_LDC2_W ? 8 : 4, I_INT, const_));
					return;
				}
				else if (cpInfo instanceof ConstantDoubleInfo) {
					const_ = String.valueOf(((ConstantDoubleInfo)cpInfo).getDouble());
					stack.push(new OperandStackEntry(i, i.getOpcode() == OPCODE_LDC2_W ? 8 : 4, D_DOUBLE, const_));
					return;
				}
				else if (cpInfo instanceof ConstantLongInfo) {
					const_ = String.valueOf(((ConstantLongInfo)cpInfo).getLong());
					stack.push(new OperandStackEntry(i, i.getOpcode() == OPCODE_LDC2_W ? 8 : 4, J_LONG, const_));
					return;
				}
				else if (cpInfo instanceof ConstantStringInfo) {
					int j = ((ConstantStringInfo)cpInfo).getStringIndex();
					
					StringBuffer buf = new StringBuffer();
					BytecodeUtils.appendString(buf, ((ConstantUtf8Info)classConstantPool[j]).getString());
					const_ = buf.toString();
					stack.push(new OperandStackEntry(i, 4, L_REFERENCE, const_));
					return;
				}

			}
			/* should never happen because ldc pushes either a float, (long) int or String */
			stack.push(new OperandStackEntry(i, 4, "?", const_));
			return;

		}

			/* value -> value, value */
		case OPCODE_DUP:
		case OPCODE_DUP2:
			OperandStackEntry tmpDup = stack.pop();
			stack.push(tmpDup);
			stack.push(tmpDup);
			return;

			/* value2, value1 -> value1, value2, value1 */
		case OPCODE_DUP_X1:
		case OPCODE_DUP2_X1:
			OperandStackEntry tmpValue1DupX1 = stack.pop();
			OperandStackEntry tmpValue2DupX1 = stack.pop();
			stack.push(tmpValue1DupX1);
			stack.push(tmpValue2DupX1);
			stack.push(tmpValue1DupX1);
			return;

			/* value3, value2, value1 -> value1, value3, value2, value1 */
		case OPCODE_DUP_X2:
		case OPCODE_DUP2_X2:
			OperandStackEntry tmpValue1DupX2 = stack.pop();
			OperandStackEntry tmpValue2DupX2 = stack.pop();
			OperandStackEntry tmpValue3DupX2 = stack.pop();
			stack.push(tmpValue1DupX2);
			stack.push(tmpValue3DupX2);
			stack.push(tmpValue2DupX2);
			stack.push(tmpValue1DupX2);
			return;

			/* objectref -> value */
		case OPCODE_GETFIELD:
			stack.pop();

			/* -> value */
		case OPCODE_GETSTATIC:
			String[] fieldNameAndDescriptor = getFieldNameAndDescriptor(i, classConstantPool);
			stack.push(new OperandStackEntry(i, 4, fieldNameAndDescriptor[1], 
					fieldNameAndDescriptor[0]));
			return;

			/* value1, value2 -> result */
		case OPCODE_IAND:
		case OPCODE_IOR:
		case OPCODE_IXOR:
		case OPCODE_ISHL:
		case OPCODE_ISHR:
		case OPCODE_IUSHR:		
		{
			OperandStackEntry value2 = stack.pop();
			OperandStackEntry value1 = stack.pop();

			stack.push(new OperandStackEntry(i, 4, I_INT, mathOperation(value1.getValue(), value2.getValue(), i)));
			return;
		}

		case OPCODE_LAND:
		case OPCODE_LOR:
		case OPCODE_LXOR:
		case OPCODE_LSHL:
		case OPCODE_LSHR:
		case OPCODE_LUSHR:
		{
			OperandStackEntry value2 = stack.pop();
			OperandStackEntry value1 = stack.pop();
			
			stack.push(new OperandStackEntry(i, 8, J_LONG, mathOperation(value1.getValue(), value2.getValue(), i)));
			return;
		}

		/* value1, value2 -> */
		case OPCODE_IF_ACMPEQ:
		case OPCODE_IF_ACMPNE:
		case OPCODE_IF_ICMPEQ:
		case OPCODE_IF_ICMPNE:
		case OPCODE_IF_ICMPLT:
		case OPCODE_IF_ICMPGE:
		case OPCODE_IF_ICMPGT:
		case OPCODE_IF_ICMPLE:
			stack.pop();
			stack.pop();
			return;

			/* value -> */
		case OPCODE_IFEQ:
		case OPCODE_IFNE:
		case OPCODE_IFGE:
		case OPCODE_IFLT:
		case OPCODE_IFGT:
		case OPCODE_IFLE:
		case OPCODE_IFNONNULL:
		case OPCODE_IFNULL:
			stack.pop();
			return;

			/* objectref -> result */
		case OPCODE_INSTANCEOF:
			stack.pop();
			stack.push(new OperandStackEntry(i, 4, I_INT, RETURN_VALUE));
			return;

			/* objectref, [arg1, arg2, ...] -> */
		case OPCODE_INVOKEINTERFACE:
		case OPCODE_INVOKESPECIAL:
		case OPCODE_INVOKEVIRTUAL:
			stack.pop(); /* pop objectref */

			/* [arg1, [arg2 ...]] -> */
		case OPCODE_INVOKEDYNAMIC:
		case OPCODE_INVOKESTATIC:
			/* get number of arguments and pop them from the stack */
			/* get return value an push it onto the stack */
			String retVal = "?" ;
			int argi = 0;
			
			/* 
			 * descriptor for the method int max(int a, int b)
			 * has the following format (II)I
			 * */
			String descriptor = getInvokeMethodDescriptor(i);
			if(descriptor != null){
				
				/* get return type from the descriptor */
				int rightParenthesis = descriptor.indexOf(ByteCodeConstants.METHOD_DESCRIPTOR_RIGHT_PARENTHESIS);
				retVal = descriptor.substring(rightParenthesis + 1);
				if(retVal.startsWith("L")){ //TODO: implement long format L<object type>
					retVal = L_REFERENCE;
				}

				/*	get argument list from the descriptor */
				StringBuilder sb = new StringBuilder();
				int offset = 1;
				try {

					while ((descriptor.charAt(offset)) != ByteCodeConstants.METHOD_DESCRIPTOR_RIGHT_PARENTHESIS) {
						if (argi != 0) {
							sb.append(JavaLexicalConstants.COMMA);
							sb.append(JavaLexicalConstants.SPACE);
						}
						offset = BytecodeUtils.appendFieldDescriptor(descriptor, offset, sb);
						sb.append(JavaLexicalConstants.SPACE);

						argi++;
					}

				} catch (IOException e) {
					handleException(IOException.class.getName(), e);
				}
			}

			/* pop all arguments from the stack */
			for(int arg = 0; arg < argi; arg++){
				stack.pop();
			}

			/* push return value onto the stack */
			if(!retVal.equals(VOID)){ /* ignore void */
				/* double and long have the double length */
				stack.push(new OperandStackEntry(i, 
						(retVal.equals(J_LONG) || retVal.equals(D_DOUBLE)) ? 8 : 4, 
						retVal, RETURN_VALUE));
				
			}
			return;

			/* -> address */
		case OPCODE_JSR:
		case OPCODE_JSR_W:
			/*
			 * jsr
			 * jump to subroutine at branchoffset 
			 * (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2) 
			 * and place the return address (offset) on the stack
			 * 
			 * jsr_w
			 * jump to subroutine at branchoffset 
			 * (signed int constructed from unsigned bytes 
			 * branchbyte1 << 24 + branchbyte2 << 16 + branchbyte3 << 8 + branchbyte4) 
			 * and place the return address (wide offset) on the stack
			 * 
			 * Example:
			 * 
			 * // The bytecode sequence for the try block: 
			 *   0 iload_0 // Push local variable 0 (arg passed as divisor)
			 *   1 ifeq 11 // Push local variable 1 (arg passed as dividend)
			 *   4 iconst_1 // Push int 1
			 *   5 istore_3 // Pop an int (the 1), store into local variable 3
			 *   6 jsr 24 // Jump to the mini-subroutine for the finally clause
			 *   9 iload_3 // Push local variable 3 (the 1)
			 *  10 ireturn // Return int on top of the stack (the 1)
			 *  11 iconst_0 // Push int 0
			 *  12 istore_3 // Pop an int (the 0), store into local variable 3
			 *  13 jsr 24 // Jump to the mini-subroutine for the finally clause
			 *  16 iload_3 // Push local variable 3 (the 0)
			 *  17 ireturn // Return int on top of the stack (the 0)
			 *
			 * // The bytecode sequence for a catch clause that catches any kind of exception
			 * // thrown from within the try block.
			 *
			 *  18 astore_1 // Pop the reference to the thrown exception, store
			 *                           // into local variable 1
			 *  19 jsr 24 // Jump to the mini-subroutine for the finally clause
			 *  22 aload_1 // Push the reference (to the thrown exception) from
			 *                           // local variable 1
			 *  23 athrow // Rethrow the same exception
			 *
			 * // The miniature subroutine that implements the finally block.
			 *
			 *  24 astore_2 // Pop the return address, store it in local variable 2
			 *  25 getstatic #8 // Get a reference to java.lang.System.out
			 *  28 ldc #1 // Push <String "Got old fashioned."> from the constant pool
			 *  30 invokevirtual #7 // Invoke System.out.println()
			 *  33 ret 2 // Return to return address stored in local variable 2
			 * 
			 */
			stack.push(new OperandStackEntry(i, 4, L_REFERENCE, ADDRESS_VALUE));
			return;

			/* key -> */
		case OPCODE_LOOKUPSWITCH:
			stack.pop();
			return;

			/* objectref -> */
		case OPCODE_MONITORENTER:
		case OPCODE_MONITOREXIT:
			stack.pop();
			return;

			/* count1, [count2,...] -> arrayref */
		case OPCODE_MULTIANEWARRAY:

			int dims = 0;
			if(i instanceof MultianewarrayInstruction){
				MultianewarrayInstruction multiArray = (MultianewarrayInstruction) i;
				dims = multiArray.getDimensions();
			}

			while((dims--) != 0) stack.pop();

			stack.push(new OperandStackEntry(i, 4, L_REFERENCE, getConstantPoolClassName(i, classConstantPool)));
			return;

			/* count -> arrayref */
		case OPCODE_NEWARRAY:
		case OPCODE_ANEWARRAY:

			stack.pop();
			stack.push(new OperandStackEntry(i, 4, L_REFERENCE, getConstantPoolClassName(i, classConstantPool)));
			return;

			/* arrayref -> length (as int)*/
		case OPCODE_ARRAYLENGTH:
		{
			OperandStackEntry arrayLength = stack.pop();

			StringBuffer buf = new StringBuffer();
			buf.append(JavaLexicalConstants.LT );
			buf.append(arrayLength.getValue() );
			buf.append(JavaLexicalConstants.DOT);
			buf.append(ByteCodeConstants.LENGTH);
			buf.append(JavaLexicalConstants.LT );

			stack.push(new OperandStackEntry(i, 4, I_INT, buf.toString()));

			return;
		}
			/* objectref,value -> */
		case OPCODE_PUTFIELD:
			stack.pop();
			stack.pop();
			return;

			/* value -> */
		case OPCODE_PUTSTATIC:
		case OPCODE_TABLESWITCH:
			stack.pop();
			return;

			/* value2, value1 -> value1, value2 */
		case OPCODE_SWAP:
			OperandStackEntry tmpSwap = stack.get(stack.size() - 2);
			stack.set(stack.size() - 1, tmpSwap);
			stack.set(stack.size() - 2, stack.get(stack.size() - 1));
			return;		
		}

	}

	/**
	 * Returns the operand type type of an load 
	 * byte code instruction.
	 * @param opcode
	 * @return type
	 */
	private String getOperandTypeOfLoadInstruction(int opcode){
		switch (opcode){
		case OPCODE_AALOAD:
			return L_REFERENCE;
		case OPCODE_DALOAD:
			return D_DOUBLE;
		case OPCODE_FALOAD:
			return F_FLOAT;
		case OPCODE_IALOAD:
		case OPCODE_BALOAD:
		case OPCODE_CALOAD:
			return I_INT;
		case OPCODE_LALOAD:
			return J_LONG;
		case OPCODE_SALOAD:	
			return S_SHORT;
		}

		return "?";
	}

	/**
	 * Returns the argument name of the given Instruction in the LocalVariableTable
	 * @param i the Instruction to check
	 * @return ? if the name can't be acquired otherwise returns the name
	 */
	private String getLocalVariableName(AbstractInstruction i){
		if (i instanceof ILocalVariableIndexProvider) {
			int index = ((ILocalVariableIndexProvider)i).getLocalVariableIndex();
			String argName = 	localVariableTable.findArgName(index - 1, 
					i.getOffset(), 
					false, 
					false);

			if(argName == null){
				argName = tmpLocalVariableTable.get(index);
			}
			
			return argName;
		} 

		return "?";

	}

	/**
	 * Returns the resolved field name and the filed descriptor.
	 * @param i byte code instruction
	 * @param classConstantPool
	 * @return {field name, filed descriptor}
	 */
	private String[] getFieldNameAndDescriptor(AbstractInstruction i, AbstractConstantPoolEntry[] classConstantPool){
		String[] ret = new String[2];
		
		AbstractConstantPoolEntry cpInfo = classConstantPool[((IConstantPoolIndexProvider)i).getConstantPoolIndex()];
		String const_ = null;
		ConstantFieldrefInfo constantFieldrefInfo = (ConstantFieldrefInfo) cpInfo;
		if (i.getOpcode() == Opcodes.OPCODE_GETSTATIC) {
			ConstantClassInfo constantClassInfo = (ConstantClassInfo)classConstantPool[constantFieldrefInfo.getClassIndex()];
			String name = ((ConstantUtf8Info) classConstantPool[constantClassInfo.getNameIndex()]).getString();
			const_ = name.replace(ByteCodeConstants.CLASS_NAME_SLASH, JavaLexicalConstants.DOT);
		}
		else {
			const_ = "";
		}
		ConstantNameAndTypeInfo constantNameAndTypeInfo = (ConstantNameAndTypeInfo) classConstantPool[constantFieldrefInfo.getNameAndTypeIndex()];
		String fieldName = ((ConstantUtf8Info)classConstantPool[constantNameAndTypeInfo.getNameIndex()]).getString();
		const_ += JavaLexicalConstants.DOT + fieldName;
		
		/* field name */
		ret[0] = const_;
		
		/* filed descriptor */
		String descriptor = constantNameAndTypeInfo.getDescriptor();
		ret[1] = descriptor.startsWith(L_REFERENCE) ? L_REFERENCE : descriptor;

		/* 
		 * The type of every value stored by a putfield or putstatic instruction 
		 * must be compatible with the descriptor of the field (¤4.3.2) of the class 
		 * instance or class being stored into. If the descriptor type is boolean, 
		 * byte, char, short, or int, then the value must be an int. 
		 * If the descriptor type is float, long, or double, then the value must 
		 * be a float, long, or double, respectively. 
		 * If the descriptor type is a reference type, then the value must be of 
		 * a type that is assignment compatible (¤2.6.7) with the descriptor type.
		 */
		ret[1] = replaceTypes(ret[1]);
		
		return ret;
	}
	
	/**
	 * Replace the types 'B' (<code>boolean</code>), 'Z' (<code>byte</code>),
	 * 'C' (<code>char</code>) and 'S' (<code>short</code>) by 'I' (<code>int<code>) 
	 * according to the Java class file specification:
	 * <br>
	 * An instruction operating on values of type int is also 
	 * permitted to operate on values of type boolean, byte, char, and short. 
	 * As noted in ¤3.3.4 and ¤3.11.1 of the Class File specification, 
	 * the Java virtual machine internally converts values of types 
	 * boolean, byte, char, and short to type int.
	 * 
	 * @param a type list
	 * @return the new type list
	 */
	static public String replaceTypes(String arg){

		arg = arg.replace('B', 'I');
		arg = arg.replace('Z', 'I');
		arg = arg.replace('C', 'I');
		arg = arg.replace('S', 'I');

		return  arg;
	}
	/**
	 * Returns the resolved class name.
	 * @param i byte code instruction
	 * @param classConstantPool
	 * @return class name
	 */
	private String getConstantPoolClassName(AbstractInstruction i, AbstractConstantPoolEntry[] classConstantPool){
		if (i instanceof IConstantPoolIndexProvider) {
			return getConstantPoolClassName(((IConstantPoolIndexProvider) i)
					.getConstantPoolIndex(), 
					classConstantPool);
		}
		return "?";
	}

	/**
	 * Returns the resolved class name.
	 * @param index the constant pool index for the class entry
	 * @param classConstantPool
	 * @return class name
	 */
	private String getConstantPoolClassName(int index, AbstractConstantPoolEntry[] classConstantPool){

		AbstractConstantPoolEntry cpInfo = classConstantPool[index];

		ConstantClassInfo constantClassInfo;

		if(cpInfo instanceof ConstantFieldrefInfo){
			constantClassInfo = (ConstantClassInfo) classConstantPool[((ConstantFieldrefInfo) cpInfo).getClassIndex()];
		} else constantClassInfo = (ConstantClassInfo) cpInfo;

		String className = BytecodeUtils.resolveConstantPoolTypeName(
				constantClassInfo, classConstantPool);

		className = className.replace(
				ByteCodeConstants.CLASS_NAME_SLASH,
				JavaLexicalConstants.DOT);
		className = className.replace(
				";",
				"");

		StringBuilder sb = new StringBuilder();
		sb.append(className);

		className = sb.toString();

		return className;
	}


	/**
	 * Returns the method descriptor of the method
	 * invoked by the given invoke instruction.
	 * The descriptor has the following format:
	 * <pre>
	 * Method
	 *  int max(int a, int b)
	 * Descriptor
	 *  (II)I
	 * </pre>
	 * 
	 * @param i byte code instruction
	 * @return class name
	 */
	public String getInvokeMethodDescriptor(AbstractInstruction i){
		String descriptor = null;
		if (i instanceof IConstantPoolIndexProvider) {
			AbstractConstantPoolEntry cpInfo = 
					classConstantPool[((IConstantPoolIndexProvider)i).getConstantPoolIndex()];

			if (cpInfo instanceof ConstantReference) {
				ConstantReference constantMethodrefInfo = (ConstantReference) cpInfo;
				ConstantNameAndTypeInfo constantNameAndTypeInfo = 
						(ConstantNameAndTypeInfo) classConstantPool[constantMethodrefInfo.getNameAndTypeIndex()];
				descriptor = ((ConstantUtf8Info)classConstantPool[constantNameAndTypeInfo.getDescriptorIndex()]).getString();
			}
		}
		return descriptor;
	}
	
	/**
	 * Returns a logical representation of an mathematical 
	 * operation in format: 
	 * <code>
	 * (var1 operation var2)
	 * </code>
	 * <br> <br>
	 * 
	 * Example:
	 * <pre>
	 * var1 = a, var2 = b, operation = '+' => (a+b)
	 * var1 = (a+b), var2 = c, operation '=' - => ((a+b)-c)
	 * </pre>
	 * 
	 * @param var1 first variable
	 * @param var2 second variable
	 * @param operation bytecode instruction
	 * @return string representation
	 */
	private String mathOperation(String var1, String var2, AbstractInstruction operation){
		StringBuffer buf = new StringBuffer();
		buf.append(JavaLexicalConstants.LEFT_PARENTHESIS);
		buf.append(var1);
		buf.append(resolveMathOperation(operation));
		buf.append(var2);
		buf.append(JavaLexicalConstants.RIGHT_PARENTHESIS);
		return buf.toString();
	}

	/**
	 * Resolves the math operation of the given byte code instruction
	 * and returns the character representing the math operation.
	 * The character is one of +, -, *, /, %, &, |, ^, << or >>.
	 * @param i byte code instruction
	 * @return math operation string one of +,-,*,/ or %
	 */
	protected String resolveMathOperation(AbstractInstruction i){
		switch (i.getOpcode()) {
		case OPCODE_DADD:
		case OPCODE_IADD:
		case OPCODE_FADD:
		case OPCODE_LADD:
			return "+";

		case OPCODE_DDIV:
		case OPCODE_IDIV:
		case OPCODE_FDIV:
		case OPCODE_LDIV:
			return "/";

		case OPCODE_DMUL:
		case OPCODE_IMUL:
		case OPCODE_FMUL:
		case OPCODE_LMUL:
			return "*";
		case OPCODE_DREM:
		case OPCODE_IREM:
		case OPCODE_FREM:
		case OPCODE_LREM:
			return "%";

		case OPCODE_DSUB:
		case OPCODE_ISUB:
		case OPCODE_FSUB:
		case OPCODE_LSUB:
			return "-";

		case OPCODE_IAND:
		case OPCODE_LAND:
			return "&";

		case OPCODE_IOR:
		case OPCODE_LOR:
			return "|";

		case OPCODE_IXOR:
		case OPCODE_LXOR:
			return "^";

		case OPCODE_ISHL:
		case OPCODE_LSHL:
			return "<<";

		case OPCODE_ISHR:
		case OPCODE_LSHR:
		case OPCODE_IUSHR:
		case OPCODE_LUSHR:
			return ">>";

		default:
			return "?";
		}
	}

	private void handleException(String message, Throwable t){
		IStatus status = BytecodeVisualizerPlugin.createErrorStatus(message, t);
		BytecodeVisualizerPlugin.log(status);
	}

	/**
	 * The operand stack entry corresponding to the byte code instruction
	 * which has to be pushed onto the stack or popped from the stack.
	 */
	class OperandStackEntry{

		/**
		 * Reference to the byte code instruction.
		 */
		private AbstractInstruction bytecodeInstruction;
		private int length;
		private String varType;
		private String value;

		public OperandStackEntry(AbstractInstruction i, int length, String varType, String value) {
			super();
			bytecodeInstruction = i;
			this.length = length;
			this.varType = varType;
			this.value = value;
		}

		public AbstractInstruction getBytecodeInstruction() {
			return bytecodeInstruction;
		}

		public int getLength() {
			return length;
		}
		public String getVarType() {
			return varType;
		}
		public String getValue() {
			return value;
		}

		public String toString(){
			return value + "," + varType;
		}
	}

	/**
	 * A property stack class is used for assigning the stack
	 * states to the nodes in the control flow graph.
	 */
	public class NodeStackProperty {
		private List<Stack<OperandStackEntry>> _stackAfter = new ArrayList<Stack<OperandStackEntry>>();
		private int stackSize[] = null;

		/**
		 * Creates a property stack object
		 * @param stackAfter stack state after executing 
		 *        the corresponding byte code instruction
		 */
		public NodeStackProperty(List<Stack<OperandStackEntry>> stackAfter) {
			if(stackAfter != null){
				_stackAfter.addAll(stackAfter);
			}
		}

		public List<Stack<OperandStackEntry>> getStackAfter() {
			return _stackAfter;
		}

		/**
		 * Returns the stack size. The stack size is calculated
		 * by using the length attribute of the stack entries.
		 * @return stack size
		 */
		public int[] getStackSize() {
			
			if(stackSize != null){
				return stackSize;
			}
			
			if(_stackAfter.size() != 0){				
				int s[] = new int[_stackAfter.size()];
				
				for(int i = 0; i < _stackAfter.size(); i++){
					int stackSize = 0;
					for(OperandStackEntry ose: _stackAfter.get(i)){
						stackSize += (ose.getLength() / 4);
					}
					
					s[i] = stackSize;
				}
				
				return s;
			}

			return new int[] {UNKNOWN_SIZE};
		}
	}

	/**
	 * Returns <code>true</code> if the instruction is a store
	 * instruction, otherwise s<code>false</code>.
	 * @param opcode
	 * @return <code>true</code> or <code>false</code>
	 */
	public static boolean isStoreIstruction(int opcode) {
		if(OPCODE_ISTORE <= opcode && opcode <= OPCODE_SASTORE){
			return true;
		}
		
		return false;
	}
}

