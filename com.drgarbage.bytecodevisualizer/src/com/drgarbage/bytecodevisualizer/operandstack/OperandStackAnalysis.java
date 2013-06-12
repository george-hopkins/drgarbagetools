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

package com.drgarbage.bytecodevisualizer.operandstack;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import com.drgarbage.asm.render.intf.IInstructionLine;
import com.drgarbage.asm.render.intf.IMethodSection;
import com.drgarbage.bytecode.ByteCodeConstants;
import com.drgarbage.bytecode.BytecodeUtils;
import com.drgarbage.bytecode.instructions.AbstractInstruction;
import com.drgarbage.bytecode.instructions.Opcodes;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.bytecodevisualizer.operandstack.OperandStack.NodeStackProperty;
import com.drgarbage.bytecodevisualizer.operandstack.OperandStack.OperandStackEntry;
import com.drgarbage.bytecodevisualizer.operandstack.OperandStack.OperandStackPropertyConstants;
import com.drgarbage.bytecodevisualizer.operandstack.OperandStack.OpstackRepresenation;
import com.drgarbage.controlflowgraph.ControlFlowGraphUtils;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;
import com.drgarbage.controlflowgraph.intf.INodeType;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.javasrc.JavaLexicalConstants;

/**
 * A collection of operand stack analysis methods.
 * 
 * @author Sergej Alekseev
 * @version $Revision$ $Id: OperandStackAnalysis.java 189 2013-06-04
 *          08:07:27Z salekseev $
 */
public class OperandStackAnalysis {

	public static String OFFSET_COL_LABEL = "Offset";
	public static String BYTE_CODE_COL_LABEL = "Bytecode";
	public static String STACK_SIZE_COL_LABEL = "Stack Size";
	public static String STACK_BEFORE_COL_LABEL = "Stack before";
	public static String STACK_AFTER_COL_LABEL = "Stack after";

	public static int OFFSET_COLWIDTH = 8;
	public static int BYTECODESTRING_COLWIDTH = 16;
	public static int OPSTACK_SIZE_COLWIDTH = STACK_SIZE_COL_LABEL.length();

	/*
	 * The width should be always equal (width -2) % 3 = 0, e.g: 14, 17, 20 ...
	 */
	public static int OPSTACK_BEFORE_COLWIDTH = 26;
	public static int OPSTACK_AFTER_COLWIDTH = 26;

	public static int TSC_COLWIDTH = 24;

	public static String executeAll(OperandStack opStack, IMethodSection method) {
		StringBuffer buf = new StringBuffer();
		buf.append(sizeBasedAnalysis(opStack, method));
		buf.append(JavaLexicalConstants.NEWLINE);

		buf.append(typeBasedAnalysis(opStack, method));
		buf.append(JavaLexicalConstants.NEWLINE);

		buf.append(contentBasedAnalysis(opStack, method));
		buf.append(JavaLexicalConstants.NEWLINE);

		buf.append(loopBasedAnalysis(opStack, method));
		buf.append(JavaLexicalConstants.NEWLINE);

		buf.append(statistics(opStack, method));
		buf.append(JavaLexicalConstants.NEWLINE);

		return buf.toString();
	}

	/**
	 * Returns a text representing the size based analysis of the current
	 * operand stack object.
	 * 
	 * @return string
	 */
	public static String sizeBasedAnalysis(OperandStack opStack,
			IMethodSection method) {
		boolean errorOrWarning = false;

		StringBuffer buf = new StringBuffer(
				BytecodeVisualizerMessages.OSA_Size_Based_Analysis);
		buf.append(method.getName());
		buf.append(method.getDescriptor());
		buf.append(JavaLexicalConstants.NEWLINE);

		buf.append(ByteCodeConstants.MAX_STACK);
		buf.append(JavaLexicalConstants.COLON);
		buf.append(JavaLexicalConstants.SPACE);
		buf.append(JavaLexicalConstants.SPACE);
		buf.append(method.getMaxStack());
		buf.append(JavaLexicalConstants.NEWLINE);

		buf.append(ByteCodeConstants.MAX_LOCALS);
		buf.append(JavaLexicalConstants.COLON);
		buf.append(JavaLexicalConstants.SPACE);
		buf.append(method.getMaxLocals());
		buf.append(JavaLexicalConstants.NEWLINE);

		/* verify max stack overflow or underflow */
		if (opStack.getMaxStackSize() > method.getMaxStack()) {
			errorOrWarning = true;
			buf.append(CoreMessages.Error);
			buf.append(JavaLexicalConstants.COLON);
			buf.append(JavaLexicalConstants.SPACE);
			buf.append(BytecodeVisualizerMessages.OperandStackAnalysis_Error_StackOverflow);
			buf.append(JavaLexicalConstants.COMMA);
			buf.append(JavaLexicalConstants.SPACE);
			String msg = MessageFormat
					.format(BytecodeVisualizerMessages.OperandStackAnalysis_MaxStackSize_Info,
							new Object[] {
									String.valueOf(method.getMaxStack()),
									String.valueOf(opStack.getMaxStackSize()) });
			buf.append(msg);
			buf.append(JavaLexicalConstants.NEWLINE);
		} else if (opStack.getMaxStackSize() < method.getMaxStack()) {
			errorOrWarning = true;
			buf.append(CoreMessages.Warning);
			buf.append(JavaLexicalConstants.COLON);
			buf.append(JavaLexicalConstants.SPACE);
			buf.append(BytecodeVisualizerMessages.OperandStackAnalysis_Warning_StackUnderflow);
			buf.append(JavaLexicalConstants.COMMA);
			buf.append(JavaLexicalConstants.SPACE);
			String msg = MessageFormat
					.format(BytecodeVisualizerMessages.OperandStackAnalysis_CurrentStackSize_Info,
							new Object[] { String.valueOf(opStack
									.getMaxStackSize()) });
			buf.append(msg);

			buf.append(JavaLexicalConstants.DOT);
			buf.append(JavaLexicalConstants.NEWLINE);
		}
		buf.append(JavaLexicalConstants.NEWLINE);

		String header = createHeaderSizedBasedAnalysis();
		buf.append(header);
		buf.append(JavaLexicalConstants.NEWLINE);
		String headerLine = createHeaderLine(header.length());
		buf.append(headerLine);
		buf.append(JavaLexicalConstants.NEWLINE);

		INodeListExt nodeList = opStack.getOperandStackGraph().getNodeList();
		for (int i = 0; i < nodeList.size(); i++) {
			INodeExt n = nodeList.getNodeExt(i);

			buf.append(n.getByteCodeOffset());
			buf.append(formatCol(OFFSET_COLWIDTH,
					String.valueOf(n.getByteCodeOffset()).length()));

			buf.append(n.getByteCodeString());
			buf.append(formatCol(BYTECODESTRING_COLWIDTH, n.getByteCodeString()
					.length()));

			Object o = n.getData();
			if (o instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<OperandStackPropertyConstants, Object> nodeMap = (Map<OperandStackPropertyConstants, Object>) o;
				o = nodeMap.get(OperandStackPropertyConstants.NODE_STACK);

				if (o != null) {
					NodeStackProperty nsp = (NodeStackProperty) o;

					int depth[] = nsp.getStackSize();
					int stackSize = OperandStack.UNKNOWN_SIZE;

					if (depth.length == 1) {
						stackSize = depth[0];
					}

					List<Integer> listOfStacksSizes = new ArrayList<Integer>();
					if (depth.length > 1) {
						for (int s : depth) {
							if (stackSize != s) {
								if (s > stackSize) {
									stackSize = s;
								}
								listOfStacksSizes.add(s);
							}
						}
					}

					if (listOfStacksSizes.size() > 1) {
						errorOrWarning = true;
						buf.append(JavaLexicalConstants.NEWLINE);
						buf.append(spacesErr(OFFSET_COLWIDTH
								+ BYTECODESTRING_COLWIDTH
								+ OPSTACK_SIZE_COLWIDTH / 2));
						buf.append(CoreMessages.Error);
						buf.append(JavaLexicalConstants.COLON);
						buf.append(JavaLexicalConstants.SPACE);
						buf.append(BytecodeVisualizerMessages.OperandStackAnalysis_Error_Different_StackSizes);
						buf.append(JavaLexicalConstants.SPACE);

						Iterator<Integer> it = listOfStacksSizes.iterator();
						buf.append(it.next());
						while (it.hasNext()) {
							buf.append(JavaLexicalConstants.PIPE);
							buf.append(it.next());
						}

						buf.append(JavaLexicalConstants.SPACE);
						buf.append(JavaLexicalConstants.DOT);
					} else {
						/* add size */
						buf.append(spaces(OPSTACK_SIZE_COLWIDTH / 2));
						buf.append(stackSize);
					}

					if (stackSize > method.getMaxStack()) {
						errorOrWarning = true;
						buf.append(JavaLexicalConstants.SPACE);
						buf.append(CoreMessages.Error);
						buf.append(JavaLexicalConstants.COLON);
						buf.append(JavaLexicalConstants.SPACE);
						buf.append(BytecodeVisualizerMessages.OperandStackAnalysis_Error_StackOverflow);
						buf.append(JavaLexicalConstants.DOT);
					}

					if (n.getVertexType() == INodeType.NODE_TYPE_RETURN) {
						if (stackSize != 0) {

							/*
							 * check if the if the object on stack represents a
							 * reference to the Throwable leaving on the stack
							 * after the rest of the stack is cleared by the
							 * athrow byte code instruction.
							 */
							o = nodeMap
									.get(OperandStackPropertyConstants.NODE_INSTR_OBJECT);
							int opCode = -1;
							if (o != null) {
								AbstractInstruction instr = ((IInstructionLine) o)
										.getInstruction();
								opCode = instr.getOpcode();
							}
							if (!(stackSize == 1 && opCode == Opcodes.OPCODE_ATHROW)) {
								errorOrWarning = true;
								buf.append(JavaLexicalConstants.NEWLINE);
								buf.append(spacesErr(OFFSET_COLWIDTH
										+ BYTECODESTRING_COLWIDTH
										+ OPSTACK_SIZE_COLWIDTH / 2));
								buf.append(CoreMessages.Warning);
								buf.append(JavaLexicalConstants.COLON);
								buf.append(JavaLexicalConstants.SPACE);
								String msg = MessageFormat
										.format(BytecodeVisualizerMessages.OperandStackAnalysis_Warning_StackNonEmpty,
												new Object[] { String
														.valueOf(stackSize) });
								buf.append(msg);

								buf.append(JavaLexicalConstants.NEWLINE);
								buf.append(spacesErr(OFFSET_COLWIDTH
										+ BYTECODESTRING_COLWIDTH
										+ OPSTACK_SIZE_COLWIDTH / 2));

								/*
								 * get reference to the corresponding byte code
								 * instruction
								 */
								buf.append(BytecodeVisualizerMessages.OperandStackAnalysis_Possible_unused_bytecodes);
								buf.append(JavaLexicalConstants.COLON);
								buf.append(JavaLexicalConstants.SPACE);
								Iterator<Stack<OperandStackEntry>> it = nsp
										.getStackAfter().iterator();
								while (it.hasNext()) {
									Iterator<OperandStackEntry> opS = it.next()
											.iterator();
									if (opS.hasNext()) {
										OperandStackEntry ose = opS.next();
										AbstractInstruction bi = ose
												.getBytecodeInstruction();
										if (bi != null) {
											buf.append(bi.getOffset());
										} else {
											buf.append(BytecodeVisualizerMessages.OperandStackView_Unknown);
										}
									}
									while (opS.hasNext()) {
										buf.append(JavaLexicalConstants.COMMA);
										buf.append(JavaLexicalConstants.SPACE);
										OperandStackEntry ose = opS.next();
										AbstractInstruction bi = ose
												.getBytecodeInstruction();
										if (bi != null) {
											buf.append(bi.getOffset());
										} else {
											buf.append(ose.getValue());
										}
									}
								}
								buf.append(JavaLexicalConstants.NEWLINE);
							}
						}
					}
				} else { /* stack not found */
					errorOrWarning = true;

					/* add size */
					buf.append(spaces(OPSTACK_SIZE_COLWIDTH
							/ 2
							- BytecodeVisualizerMessages.OperandStackView_Unknown
									.length() / 2));
					buf.append(BytecodeVisualizerMessages.OperandStackView_Unknown);

					/* print error */
					buf.append(JavaLexicalConstants.SPACE);
					buf.append(CoreMessages.Error);
					buf.append(JavaLexicalConstants.COLON);
					buf.append(JavaLexicalConstants.SPACE);

					o = nodeMap
							.get(OperandStackPropertyConstants.ERROR_EXCEPTION);
					if (o != null && o instanceof Throwable) {
						Throwable th = (Throwable) o;
						buf.append(th);
					} else {
						buf.append("Undefined stack");
					}
					buf.append(JavaLexicalConstants.DOT);
				}
			}
			buf.append(JavaLexicalConstants.NEWLINE);
		}
		buf.append(headerLine);

		if (errorOrWarning == false) {
			buf.append(JavaLexicalConstants.NEWLINE);
			buf.append(BytecodeVisualizerMessages.OSA_Size_Based_Success);
		} else {
			buf.append(JavaLexicalConstants.NEWLINE);
			buf.append(BytecodeVisualizerMessages.OSA_Size_Based_Error);
		}

		buf.append(JavaLexicalConstants.NEWLINE);
		buf.append(JavaLexicalConstants.NEWLINE);
		return buf.toString();

	}

	/**
	 * Returns a text representing the type based analysis of the current
	 * operand stack object.
	 * 
	 * @return string
	 */
	public static String typeBasedAnalysis(OperandStack opStack,
			IMethodSection method) {
		boolean errorOrWarning = false;

		StringBuffer buf = new StringBuffer(
				BytecodeVisualizerMessages.OSA_Type_Based_Analysis);
		buf.append(method.getName());
		buf.append(method.getDescriptor());
		buf.append(JavaLexicalConstants.NEWLINE);

		String header = createHeaderTypeBasedAnalysis();
		buf.append(header);
		buf.append(JavaLexicalConstants.NEWLINE);
		String headerLine = createHeaderLine(header.length());
		buf.append(headerLine);
		buf.append(JavaLexicalConstants.NEWLINE);

		INodeListExt nodeList = opStack.getOperandStackGraph().getNodeList();
		for (int i = 0; i < nodeList.size(); i++) {
			INodeExt n = nodeList.getNodeExt(i);

			buf.append(n.getByteCodeOffset());
			buf.append(formatCol(OFFSET_COLWIDTH,
					String.valueOf(n.getByteCodeOffset()).length()));
			buf.append(n.getByteCodeString());
			buf.append(formatCol(BYTECODESTRING_COLWIDTH, n.getByteCodeString()
					.length()));

			Object o = n.getData();
			if (o instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<OperandStackPropertyConstants, Object> nodeMap = (Map<OperandStackPropertyConstants, Object>) o;
				o = nodeMap.get(OperandStackPropertyConstants.NODE_STACK);
				if (o != null) {
					NodeStackProperty nsp = (NodeStackProperty) o;

					/*
					 * verify if all stack entries contains the same list of
					 * types.
					 */
					List<String> listOfTypes = new ArrayList<String>();
					if (nsp.getStackAfter().size() > 1) {
						Iterator<Stack<OperandStackEntry>> it = opStack
								.getStackBefore(n).iterator();
						String tmpTypeList = "";
						while (it.hasNext()) {
							String typeList = getStackTypes(it.next());
							if (!tmpTypeList.equals(typeList)) {
								if (typeList.equals("")) {
									tmpTypeList = OperandStack.EMPTY_STACK;
								} else {
									tmpTypeList = typeList;
								}
								listOfTypes.add(tmpTypeList);
							}
						}
					}

					if (listOfTypes.size() > 1) {
						errorOrWarning = true;
						buf.append(CoreMessages.Error);
						buf.append(JavaLexicalConstants.COLON);
						buf.append(JavaLexicalConstants.SPACE);
						buf.append(BytecodeVisualizerMessages.OperandStackAnalysis_Error_Different_StackTypes);
						buf.append(JavaLexicalConstants.SPACE);

						Iterator<String> it = listOfTypes.iterator();
						buf.append(it.next());
						while (it.hasNext()) {
							buf.append(JavaLexicalConstants.SPACE);
							buf.append(JavaLexicalConstants.PIPE);
							buf.append(JavaLexicalConstants.SPACE);
							buf.append(it.next());
						}

						buf.append(JavaLexicalConstants.DOT);
					} else {
						List<Stack<OperandStackEntry>> listOfStacksBefore = opStack
								.getStackBefore(n);
						List<Stack<OperandStackEntry>> listOfStacks = nsp
								.getStackAfter();

						/*
						 * print list of types for the current byte code
						 * instruction.
						 */
						String stackStr = OperandStack.stackToString(
								listOfStacksBefore.get(0),
								OpstackRepresenation.TYPES);
						buf.append(formatColContent(OFFSET_COLWIDTH
								+ BYTECODESTRING_COLWIDTH,
								OPSTACK_BEFORE_COLWIDTH, stackStr));

						stackStr = OperandStack
								.stackToString(listOfStacks.get(0),
										OpstackRepresenation.TYPES);
						buf.append(formatColContent(OFFSET_COLWIDTH
								+ BYTECODESTRING_COLWIDTH
								+ OPSTACK_BEFORE_COLWIDTH,
								OPSTACK_AFTER_COLWIDTH, stackStr));

						/* get instruction object associated with the node */
						o = nodeMap
								.get(OperandStackPropertyConstants.NODE_INSTR_OBJECT);
						if (o != null) {
							AbstractInstruction instr = ((IInstructionLine) o)
									.getInstruction();

							/*
							 * verify the the type on stack for if instructions
							 * byte code instruction.
							 */
							// TODO: implement

							/*
							 * verify if the list of stack types equals to
							 * invoke byte instruction arguments.
							 */
							// FIXME: implement verification of all combination
							if (n.getVertexType() == INodeType.NODE_TYPE_INVOKE) {
								String descriptor = opStack
										.getInvokeMethodDescriptor(instr);

								int leftParenthesis = descriptor
										.indexOf(ByteCodeConstants.METHOD_DESCRIPTOR_LEFT_PARENTHESIS);
								int rightParenthesis = descriptor
										.indexOf(ByteCodeConstants.METHOD_DESCRIPTOR_RIGHT_PARENTHESIS);
								String methodArgumentList = descriptor
										.substring(leftParenthesis + 1,
												rightParenthesis);

								/* verify only if the argument list not empty */
								if (!methodArgumentList.equals("")) {
									Stack<OperandStackEntry> se = listOfStacksBefore.get(0);
									if (se.size() != 0) {

										/*
										 * Normalize the argument to operand
										 * stack format
										 */
										StringBuffer sb = new StringBuffer();
										int argi = 0;

										int offset = 0;
										while (methodArgumentList.length() > offset) {

											try {
												offset = BytecodeUtils
														.appendFieldDescriptor(
																methodArgumentList,
																offset, sb);
											} catch (IOException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
											argi++;
										}
										offset = 0;

										// TODO: check long L_REFERENCE format
										sb = new StringBuffer();
										for (int j = 0; j < methodArgumentList
												.length(); j++) {
											if (methodArgumentList.charAt(j) == 'L') {
												sb.append('L');
												j = methodArgumentList.indexOf(
														';', j);
											} else {
												sb.append(methodArgumentList
														.charAt(j));
											}
										}

										methodArgumentList = sb.toString();
										
										methodArgumentList = OperandStack.replaceTypes(methodArgumentList);

										/* get return type from stack */
										List<String> sttackTypeListReverseOrder = new ArrayList<String>();
										for (int j = 1; j <= argi; j++) {
											sttackTypeListReverseOrder.add(se
													.get((se.size() - j))
													.getVarType());
										}

										/* reverse list */
										StringBuffer opStackType = new StringBuffer();
										for (int j = (sttackTypeListReverseOrder
												.size() - 1); j >= 0; j--) {
											opStackType
													.append(sttackTypeListReverseOrder
															.get(j));
										}

										if (!methodArgumentList
												.equals(opStackType.toString())) {
											errorOrWarning = true;
											buf.append(JavaLexicalConstants.NEWLINE);
											buf.append(spacesErr(OFFSET_COLWIDTH
													+ BYTECODESTRING_COLWIDTH));
											buf.append(CoreMessages.Error);
											buf.append(JavaLexicalConstants.COLON);
											buf.append(JavaLexicalConstants.SPACE);
											String msg = MessageFormat
													.format(BytecodeVisualizerMessages.OSA_expect_find_onStack_type_onStack,
															new Object[] { 
															String.valueOf(methodArgumentList),
															String.valueOf(opStackType)});
											buf.append(msg);
											buf.append(JavaLexicalConstants.DOT);
											buf.append(JavaLexicalConstants.SPACE);
											buf.append(JavaLexicalConstants.NEWLINE);
										}
									} else {
										errorOrWarning = true;
										buf.append(JavaLexicalConstants.NEWLINE);
										buf.append(spacesErr(OFFSET_COLWIDTH
												+ BYTECODESTRING_COLWIDTH));
										buf.append(CoreMessages.Error);
										buf.append(JavaLexicalConstants.COLON);
										buf.append(JavaLexicalConstants.SPACE);
										buf.append(BytecodeVisualizerMessages.Undefined_type);
										buf.append(JavaLexicalConstants.DOT);
										buf.append(JavaLexicalConstants.SPACE);
										buf.append(JavaLexicalConstants.NEWLINE);
									}
								}

							}

							/*
							 * verify the the type on stack for store byte code
							 * instruction.
							 */
							if (n.getVertexType() == INodeType.NODE_TYPE_SIMPLE) {
								String storeType = "?";
								boolean storeInstruction = true;

								switch (instr.getOpcode()) {
								case Opcodes.OPCODE_ISTORE:
								case Opcodes.OPCODE_ISTORE_0:
								case Opcodes.OPCODE_ISTORE_1:
								case Opcodes.OPCODE_ISTORE_2:
								case Opcodes.OPCODE_ISTORE_3:
								case Opcodes.OPCODE_IASTORE:
									
									/* As noted in ¤3.3.4 and ¤3.11.1, 
									 * the Java virtual machine internally 
									 * converts values of types boolean, 
									 * byte, char, and short to type int. 
									 */
								case Opcodes.OPCODE_BASTORE:
								case Opcodes.OPCODE_CASTORE:
								case Opcodes.OPCODE_SASTORE:
									storeType = OperandStack.I_INT;
									break;

								case Opcodes.OPCODE_DSTORE:
								case Opcodes.OPCODE_DSTORE_0:
								case Opcodes.OPCODE_DSTORE_1:
								case Opcodes.OPCODE_DSTORE_2:
								case Opcodes.OPCODE_DSTORE_3:
								case Opcodes.OPCODE_DASTORE:
									storeType = OperandStack.D_DOUBLE;
									break;

								case Opcodes.OPCODE_LSTORE:
								case Opcodes.OPCODE_LSTORE_0:
								case Opcodes.OPCODE_LSTORE_1:
								case Opcodes.OPCODE_LSTORE_2:
								case Opcodes.OPCODE_LSTORE_3:
								case Opcodes.OPCODE_LASTORE:
									storeType = OperandStack.J_LONG;
									break;

								case Opcodes.OPCODE_FSTORE:
								case Opcodes.OPCODE_FSTORE_0:
								case Opcodes.OPCODE_FSTORE_1:
								case Opcodes.OPCODE_FSTORE_2:
								case Opcodes.OPCODE_FSTORE_3:
								case Opcodes.OPCODE_FASTORE:
									storeType = OperandStack.F_FLOAT;
									break;

								case Opcodes.OPCODE_ASTORE:
								case Opcodes.OPCODE_ASTORE_0:
								case Opcodes.OPCODE_ASTORE_1:
								case Opcodes.OPCODE_ASTORE_2:
								case Opcodes.OPCODE_ASTORE_3:
								case Opcodes.OPCODE_AASTORE:
									storeType = OperandStack.L_REFERENCE;
									break;

								default:
									storeInstruction = false;
								}

								/* verify only if instruction of type store */
								if (storeInstruction) {
									/* get return type from stack */
									Stack<OperandStackEntry> se = listOfStacksBefore
											.get(0);
									if (se.size() != 0) {
										String opStackType = se.lastElement()
												.getVarType();
										if (!storeType.equals(opStackType)) {
											errorOrWarning = true;
											buf.append(JavaLexicalConstants.NEWLINE);
											buf.append(spacesErr(OFFSET_COLWIDTH
													+ BYTECODESTRING_COLWIDTH));
											buf.append(CoreMessages.Error);
											buf.append(JavaLexicalConstants.COLON);
											buf.append(JavaLexicalConstants.SPACE);
											String msg = MessageFormat
													.format(BytecodeVisualizerMessages.OSA_expect_find_onStack_type_onStack,
															new Object[] { 
															String.valueOf(storeType),
															String.valueOf(opStackType)});
											buf.append(msg);
											buf.append(JavaLexicalConstants.DOT);
											buf.append(JavaLexicalConstants.SPACE);
											buf.append(JavaLexicalConstants.NEWLINE);
										}
									} else {
										errorOrWarning = true;
										buf.append(JavaLexicalConstants.NEWLINE);
										buf.append(spacesErr(OFFSET_COLWIDTH
												+ BYTECODESTRING_COLWIDTH));
										buf.append(CoreMessages.Error);
										buf.append(JavaLexicalConstants.COLON);
										buf.append(JavaLexicalConstants.SPACE);
										buf.append(BytecodeVisualizerMessages.Undefined_type);
										buf.append(JavaLexicalConstants.DOT);
										buf.append(JavaLexicalConstants.SPACE);
										buf.append(JavaLexicalConstants.NEWLINE);
									}
								}
							}

							/*
							 * verify the the type on stack for return byte code
							 * instruction.
							 */
							if (n.getVertexType() == INodeType.NODE_TYPE_RETURN) {
								ArrayList<String> returnType = new ArrayList<String>();
								boolean voidReturn = false;
								switch (instr.getOpcode()) {
								case Opcodes.OPCODE_ARETURN:
								case Opcodes.OPCODE_ATHROW:
									returnType.add(OperandStack.L_REFERENCE);
									break;
								case Opcodes.OPCODE_DRETURN:
									returnType.add(OperandStack.D_DOUBLE);
									break;
								case Opcodes.OPCODE_FRETURN:
									returnType.add(OperandStack.F_FLOAT);
									break;
								case Opcodes.OPCODE_IRETURN:
									returnType.add(OperandStack.I_INT);
									returnType.add(OperandStack.Z_BOOLEAN);
									returnType.add(OperandStack.B_BYTE);
									returnType.add(OperandStack.S_SHORT);
									returnType.add(OperandStack.C_CHAR);
									break;
								case Opcodes.OPCODE_LRETURN:
									returnType.add(OperandStack.J_LONG);
									break;
								case Opcodes.OPCODE_RETURN:
								case Opcodes.OPCODE_RET:
									voidReturn = true;
								}

								/* verify all return instruction accept void */
								if (!voidReturn) {
									/* get return type from stack */
									Stack<OperandStackEntry> se = listOfStacksBefore
											.get(0);
									if (se.size() != 0) {
										String opStackType = se.lastElement()
												.getVarType();
										if (!returnType.contains(opStackType)) {
											errorOrWarning = true;
											buf.append(JavaLexicalConstants.NEWLINE);
											buf.append(spacesErr(OFFSET_COLWIDTH
													+ BYTECODESTRING_COLWIDTH));
											buf.append(CoreMessages.Error);
											buf.append(JavaLexicalConstants.COLON);
											buf.append(JavaLexicalConstants.SPACE);
											buf.append(BytecodeVisualizerMessages.OSA_return_type_mismatched);
											buf.append(BytecodeVisualizerMessages.OSA_expected_type
													+ returnType.toString());
											buf.append(BytecodeVisualizerMessages.OSA_type_on_stack
													+ opStackType);
											buf.append(JavaLexicalConstants.DOT);
											buf.append(JavaLexicalConstants.SPACE);
											buf.append(JavaLexicalConstants.NEWLINE);
										}
									} else {
										errorOrWarning = true;
										buf.append(JavaLexicalConstants.NEWLINE);
										buf.append(spacesErr(OFFSET_COLWIDTH
												+ BYTECODESTRING_COLWIDTH));
										buf.append(CoreMessages.Error);
										buf.append(JavaLexicalConstants.COLON);
										buf.append(JavaLexicalConstants.SPACE);
										buf.append(BytecodeVisualizerMessages.Undefined_type);
										buf.append(JavaLexicalConstants.DOT);
										buf.append(JavaLexicalConstants.SPACE);
										buf.append(JavaLexicalConstants.NEWLINE);
									}
								}
							}
						} else {
							errorOrWarning = true;
							buf.append(CoreMessages.Error);
							buf.append(JavaLexicalConstants.COLON);
							buf.append(JavaLexicalConstants.SPACE);
							buf.append(BytecodeVisualizerMessages.OSA_instruction_obj_missing);
							buf.append(JavaLexicalConstants.DOT);
							buf.append(JavaLexicalConstants.SPACE);
							buf.append(JavaLexicalConstants.NEWLINE);
						}
					}
				} else { /* stack not found */
					errorOrWarning = true;
					buf.append(CoreMessages.Error);
					buf.append(JavaLexicalConstants.COLON);
					buf.append(JavaLexicalConstants.SPACE);

					o = nodeMap
							.get(OperandStackPropertyConstants.ERROR_EXCEPTION);
					if (o != null && o instanceof Throwable) {
						Throwable th = (Throwable) o;
						buf.append(th);
					} else {
						buf.append(BytecodeVisualizerMessages.Undefined_stack);
					}
					buf.append(JavaLexicalConstants.DOT);
				}
			}

			buf.append(JavaLexicalConstants.NEWLINE);
		}

		buf.append(headerLine);

		if (errorOrWarning == false) {
			buf.append(JavaLexicalConstants.NEWLINE);
			buf.append(BytecodeVisualizerMessages.OSA_Type_Based_Success);
		} else {
			buf.append(JavaLexicalConstants.NEWLINE);
			buf.append(BytecodeVisualizerMessages.OSA_Type_Based_Error);
		}
		buf.append(JavaLexicalConstants.NEWLINE);
		buf.append(JavaLexicalConstants.NEWLINE);
		return buf.toString();
	}
	
	/**
	 * Returns a text representing the content based analysis of the current
	 * operand stack object.
	 * 
	 * @return string
	 */
	public static String contentBasedAnalysis(OperandStack opStack,
			IMethodSection method) {
		boolean errorOrWarning = false;

		StringBuffer buf = new StringBuffer(
				BytecodeVisualizerMessages.OSA_Content_Based_Analysis);
		buf.append(method.getName());
		buf.append(method.getDescriptor());
		buf.append(JavaLexicalConstants.NEWLINE);

		String header = createHeaderTypeBasedAnalysis();
		buf.append(header);
		buf.append(JavaLexicalConstants.NEWLINE);
		String headerLine = createHeaderLine(header.length());
		buf.append(headerLine);
		buf.append(JavaLexicalConstants.NEWLINE);

		INodeListExt nodeList = opStack.getOperandStackGraph().getNodeList();
		for (int i = 0; i < nodeList.size(); i++) {
			INodeExt n = nodeList.getNodeExt(i);
			buf.append(n.getByteCodeOffset());
			buf.append(formatCol(OFFSET_COLWIDTH,
					String.valueOf(n.getByteCodeOffset()).length()));

			buf.append(n.getByteCodeString());
			buf.append(formatCol(BYTECODESTRING_COLWIDTH, n.getByteCodeString()
					.length()));

			Object o = n.getData();
			if (o instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<OperandStackPropertyConstants, Object> nodeMap = (Map<OperandStackPropertyConstants, Object>) o;
				o = nodeMap.get(OperandStackPropertyConstants.NODE_STACK);
				if (o != null) {
					NodeStackProperty nsp = (NodeStackProperty) o;

					List<Stack<OperandStackEntry>> listOfStacksBefore = opStack
							.getStackBefore(n);
					List<Stack<OperandStackEntry>> listOfStacks = nsp
							.getStackAfter();

					/*
					 * print list of types for the current byte code
					 * instruction.
					 */
					String stackStr = OperandStack.stackListToString(
							listOfStacksBefore, OpstackRepresenation.VALUES);
					buf.append(formatColContent(OFFSET_COLWIDTH
							+ BYTECODESTRING_COLWIDTH, OPSTACK_BEFORE_COLWIDTH,
							stackStr));

					stackStr = OperandStack.stackListToString(listOfStacks,
							OpstackRepresenation.VALUES);
					buf.append(formatColContent(
							OFFSET_COLWIDTH + BYTECODESTRING_COLWIDTH
									+ OPSTACK_BEFORE_COLWIDTH,
							OPSTACK_AFTER_COLWIDTH, stackStr));

				} else { /* stack not found */
					errorOrWarning = true;
					buf.append(CoreMessages.Error);
					buf.append(JavaLexicalConstants.COLON);
					buf.append(JavaLexicalConstants.SPACE);

					o = nodeMap
							.get(OperandStackPropertyConstants.ERROR_EXCEPTION);
					if (o != null && o instanceof Throwable) {
						Throwable th = (Throwable) o;
						buf.append(th);
					} else {
						buf.append(BytecodeVisualizerMessages.Undefined_stack);
					}
					buf.append(JavaLexicalConstants.DOT);
				}
			}

			/* check duplicates */
			IEdgeListExt incEdgeList = n.getIncomingEdgeList();
			if (incEdgeList.size() > 1) {
				Map<String, Stack<OperandStackEntry>> m = new TreeMap<String, Stack<OperandStackEntry>>();
				for (int k = 0; k < incEdgeList.size(); k++) {
					IEdgeExt e = incEdgeList.getEdgeExt(k);
					INodeExt source = e.getSource();

					o = source.getData();
					if (o instanceof Map) {
						@SuppressWarnings("unchecked")
						Map<OperandStackPropertyConstants, Object> nodeMap = (Map<OperandStackPropertyConstants, Object>) o;
						o = nodeMap
								.get(OperandStackPropertyConstants.NODE_STACK);
						if (o != null) {
							NodeStackProperty nsp = (NodeStackProperty) o;
							List<Stack<OperandStackEntry>> sl = nsp
									.getStackAfter();

							for (Stack<OperandStackEntry> s : sl) {
								if (s.size() != 0) {

									/* no duplicates */
									Stack<OperandStackEntry> ret = m.put(
											OperandStack.stackToString(s,
													OpstackRepresenation.ALL),
											s);
									if (ret != null) {
										errorOrWarning = true;
										buf.append(JavaLexicalConstants.NEWLINE);
										buf.append(spacesErr(OFFSET_COLWIDTH
												+ BYTECODESTRING_COLWIDTH));
										buf.append(CoreMessages.Warning);
										buf.append(JavaLexicalConstants.COLON);
										buf.append(JavaLexicalConstants.SPACE);

										buf.append("Duplicate operand stack entry detected '");
										buf.append(OperandStack.stackToString(
												ret,
												OpstackRepresenation.VALUES));
										buf.append("'");
										buf.append(JavaLexicalConstants.NEWLINE);
										buf.append(spacesErr(OFFSET_COLWIDTH
												+ BYTECODESTRING_COLWIDTH
												+ CoreMessages.Warning.length()
												+ 2));
										buf.append("Bytcode addresses: ");
										buf.append(ret.lastElement()
												.getBytecodeInstruction()
												.getOffset());
										buf.append(" and ");
										buf.append(s.lastElement()
												.getBytecodeInstruction()
												.getOffset());
										buf.append(JavaLexicalConstants.COMMA);
										buf.append(JavaLexicalConstants.SPACE);
										buf.append("possibly dead branch.");

										buf.append(JavaLexicalConstants.NEWLINE);
										buf.append(spacesErr(OFFSET_COLWIDTH
												+ BYTECODESTRING_COLWIDTH));
									}
								}
							}
						}
					}
				}
			}
			buf.append(JavaLexicalConstants.NEWLINE);
		}
		buf.append(headerLine);

		if (errorOrWarning == false) {
			buf.append(JavaLexicalConstants.NEWLINE);
			buf.append(BytecodeVisualizerMessages.OSA_Content_Based_Analysis_success);
		} else {
			buf.append(JavaLexicalConstants.NEWLINE);
			buf.append(BytecodeVisualizerMessages.OSA_Content_Based_Analysis_error);
		}
		buf.append(JavaLexicalConstants.NEWLINE);
		buf.append(JavaLexicalConstants.NEWLINE);

		return buf.toString();
	}

	/**
	 * Returns a text representing the loop based analysis of the current
	 * operand stack object.
	 * 
	 * @return string
	 */
	public static String loopBasedAnalysis(OperandStack opStack,
			IMethodSection method) {
		boolean errorOrWarning = false;

		String header = BytecodeVisualizerMessages.OSA_Loop_Based_Analysis;
		StringBuffer buf = new StringBuffer(header);
		buf.append(JavaLexicalConstants.NEWLINE);
		String headerLine = createHeaderLine(header.length() * 3);
		buf.append(headerLine);
		buf.append(JavaLexicalConstants.NEWLINE);

		IEdgeListExt backEdges = opStack.getBackEdges();
		for (int i = 0; i < backEdges.size(); i++) {
			IEdgeExt e = backEdges.getEdgeExt(i);
			INodeExt sourceNode = e.getSource();
			INodeExt targetNode = e.getTarget();

			/* get operand stack from the source */
			String tmpStack = "";
			Object o = sourceNode.getData();
			if (o instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<OperandStackPropertyConstants, Object> nodeMap = (Map<OperandStackPropertyConstants, Object>) o;
				o = nodeMap.get(OperandStackPropertyConstants.NODE_STACK);
				if (o != null) {
					NodeStackProperty nsp = (NodeStackProperty) o;
					String s = OperandStack.stackListToString(
							nsp.getStackAfter(), OpstackRepresenation.ALL);
					tmpStack = s;
				}
			}

			/* check all incoming edges */
			List<String> listOfStacks = new ArrayList<String>();
			IEdgeListExt incEdgeList = targetNode.getIncomingEdgeList();
			for (int j = 0; j < incEdgeList.size(); j++) {
				IEdgeExt incEdge = incEdgeList.getEdgeExt(j);
				INodeExt n = incEdge.getSource();
				/* get operand stack from the source */
				o = n.getData();
				if (o instanceof Map) {
					@SuppressWarnings("unchecked")
					Map<OperandStackPropertyConstants, Object> nodeMap = (Map<OperandStackPropertyConstants, Object>) o;
					o = nodeMap.get(OperandStackPropertyConstants.NODE_STACK);
					if (o != null) {
						NodeStackProperty nsp = (NodeStackProperty) o;
						String s = OperandStack.stackListToString(
								nsp.getStackAfter(), OpstackRepresenation.ALL);
						if (!tmpStack.equals(s)) {
							listOfStacks.add(s);
							tmpStack = s;
						}
					}
				}

				if (listOfStacks.size() != 0) {
					errorOrWarning = true;
					buf.append(JavaLexicalConstants.NEWLINE);
					buf.append(CoreMessages.Error);
					buf.append(JavaLexicalConstants.COLON);
					buf.append(JavaLexicalConstants.SPACE);
					buf.append(BytecodeVisualizerMessages.OSA_Loop_failed);
					buf.append(JavaLexicalConstants.DOT);
					buf.append(JavaLexicalConstants.SPACE);
					buf.append(JavaLexicalConstants.NEWLINE);
				}
			}
		}

		if (backEdges.size() == 0) {
			buf.append(BytecodeVisualizerMessages.OSA_No_Loop);
			buf.append(JavaLexicalConstants.NEWLINE);
		} else {
			buf.append(backEdges.size());
			buf.append(backEdges.size() == 1 ? " loop has" : " loops have");
			buf.append(" been detected.");
			buf.append(JavaLexicalConstants.NEWLINE);
		}

		buf.append(headerLine);

		if (errorOrWarning == false) {
			buf.append(JavaLexicalConstants.NEWLINE);
			buf.append(BytecodeVisualizerMessages.OSA_Loop_Based_success);
		} else {
			buf.append(JavaLexicalConstants.NEWLINE);
			buf.append(BytecodeVisualizerMessages.OSA_Loop_Based_error);
		}
		buf.append(JavaLexicalConstants.NEWLINE);
		buf.append(JavaLexicalConstants.NEWLINE);

		return buf.toString();
	}

	/**
	 * Returns statistics of the current operand stack object.
	 * 
	 * @return string
	 */
	public static String statistics(OperandStack opStack, IMethodSection method) {
		StringBuffer buf = new StringBuffer(
				BytecodeVisualizerMessages.OSA_statistic);
		buf.append(JavaLexicalConstants.NEWLINE);

		String headerLine = createHeaderLine(64);
		buf.append(headerLine);
		buf.append(JavaLexicalConstants.NEWLINE);

		buf.append("Elapsed time of the operand stack generation:   "
				+ opStack.getElapsedTime() + " ms");
		buf.append(JavaLexicalConstants.NEWLINE);
		buf.append("Memory consumption:                             "
				+ opStack.getMemoryConsumption() + " Bytes");
		buf.append(JavaLexicalConstants.NEWLINE);

		// buf.append("Number of generated stacks:                     " +
		// opStack.getNumberOfStacks());
		// buf.append(JavaLexicalConstants.NEWLINE);

		buf.append("Max Number of stack combinations:               "
				+ opStack.getMaxNumberOfStackCombinations());
		buf.append(JavaLexicalConstants.NEWLINE);

		buf.append("Max Number of assined stacks to an instruction: "
				+ opStack.getMaxNumberOfStacks());
		buf.append(JavaLexicalConstants.NEWLINE);

		// buf.append("Number of generated stack etries:               " +
		// opStack.getNumberOfStackEntries());
		// buf.append(JavaLexicalConstants.NEWLINE);
		buf.append("Max Number of stack etries:                     "
				+ opStack.getMaxNumberOfStackEntries());
		buf.append(JavaLexicalConstants.NEWLINE);

		buf.append("Number of method instructions:                  "
				+ method.getInstructionLines().size());
		buf.append(JavaLexicalConstants.NEWLINE);

		buf.append("Number of if instructions:                      ");
		buf.append(countIfInstrunctions(method.getInstructionLines()));

		buf.append(JavaLexicalConstants.NEWLINE);
		buf.append("Number of switch instructions:                  ");
		buf.append(countSwitchInstrunctions(method.getInstructionLines()));

		buf.append(JavaLexicalConstants.NEWLINE);
		buf.append(headerLine);
		buf.append(JavaLexicalConstants.NEWLINE);
		return buf.toString();
	}

	/**
	 * Return the list of types for the given stack in Java class file format.
	 * 
	 * @param stack
	 * @return
	 */
	public static String getStackTypes(Stack<OperandStackEntry> stack) {
		StringBuffer buf = new StringBuffer();
		for (Enumeration<OperandStackEntry> en = stack.elements(); en
				.hasMoreElements();) {
			OperandStackEntry ose = en.nextElement();
			buf.append(ose.getVarType());
		}
		return buf.toString();
	}

	/**
	 * Counts the if instructions in the given instruction list.
	 * 
	 * @param instructionList
	 * @return number of if instruction
	 */
	private static int countIfInstrunctions(
			List<IInstructionLine> instructionList) {
		int counter = 0;
		for (IInstructionLine i : instructionList) {
			int type = ControlFlowGraphUtils.getInstructionNodeType(i
					.getInstruction().getOpcode());
			if (type == INodeType.NODE_TYPE_IF) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Counts the switch instructions in the given instruction list.
	 * 
	 * @param instructionList
	 * @return number of switch instruction
	 */
	private static int countSwitchInstrunctions(
			List<IInstructionLine> instructionList) {
		int counter = 0;
		for (IInstructionLine i : instructionList) {
			int type = ControlFlowGraphUtils.getInstructionNodeType(i
					.getInstruction().getOpcode());
			if (type == INodeType.NODE_TYPE_SWITCH) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * TODO: description
	 * 
	 * @param startOffset
	 * @param colWidth
	 * @param data
	 * @return string
	 */
	public static String formatColContent(int startOffset, int colWidth,
			String data) {
		int w = colWidth - 2;
		StringBuffer buf = new StringBuffer();
		if (data.length() > w) {
			int start = 0, end = w;
			String s = data.substring(start, end);
			buf.append(s);
			start = end;
			end = (data.length() - end) > w ? end + w : data.length();
			while (start < data.length()) {
				buf.append(JavaLexicalConstants.NEWLINE);
				buf.append(formatCol(startOffset, 0));
				s = data.substring(start, end);
				buf.append(s);
				buf.append(formatCol(colWidth, s.length()));

				start = end;
				end = (data.length() - end) > w ? end + w : data.length();
			}
		} else {
			buf.append(data);
			buf.append(formatCol(colWidth, data.length()));
		}
		return buf.toString();
	}

	/**
	 * Generate a number of spaces for formatting purposes in byte code String
	 * column
	 * 
	 * @param columnWidth
	 * @param dataLength
	 * @return spaces
	 */
	public static String formatCol(int colWidth, int dataLength) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < colWidth - dataLength; i++) {
			buf.append(JavaLexicalConstants.SPACE);
		}
		return buf.toString();
	}

	/**
	 * Generate a number of spaces for formatting purposes
	 * 
	 * @param numOfSpace
	 * @return String
	 */
	public static String spacesErr(int numOfSpace) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < numOfSpace; i++) {
			buf.append(JavaLexicalConstants.SPACE);
		}
		return buf.toString();
	}

	public static String spaces(int numOfSpace) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < numOfSpace; i++) {
			buf.append(JavaLexicalConstants.SPACE);
		}
		return buf.toString();
	}

	/**
	 * Create header for size based analysis
	 * 
	 * <pre>
	 * |  -8-  |      -16-     |  -10-   |         
	 * Offset  Bytecode        Size Stack
	 * </pre>
	 * 
	 * @return text
	 */
	public static String createHeaderSizedBasedAnalysis() {
		StringBuffer buf = new StringBuffer();
		buf.append(OFFSET_COL_LABEL);
		buf.append(spaces(OFFSET_COLWIDTH - OFFSET_COL_LABEL.length()));

		buf.append(BYTE_CODE_COL_LABEL);
		buf.append(spaces(BYTECODESTRING_COLWIDTH
				- BYTE_CODE_COL_LABEL.length()));

		buf.append(STACK_SIZE_COL_LABEL);

		return buf.toString();
	}

	/**
	 * Create header for type based analysis
	 * 
	 * <pre>
	 * |  -8-  |      -16-     |          -24-         |           -24-         
	 * Offset  Bytecode        Stack Before            Stack After
	 * </pre>
	 * 
	 * @return text
	 */
	public static String createHeaderTypeBasedAnalysis() {
		StringBuffer buf = new StringBuffer();
		buf.append(OFFSET_COL_LABEL);
		buf.append(spaces(OFFSET_COLWIDTH - OFFSET_COL_LABEL.length()));

		buf.append(BYTE_CODE_COL_LABEL);
		buf.append(spaces(BYTECODESTRING_COLWIDTH
				- BYTE_CODE_COL_LABEL.length()));

		buf.append(STACK_BEFORE_COL_LABEL);
		buf.append(spaces(OPSTACK_BEFORE_COLWIDTH
				- STACK_BEFORE_COL_LABEL.length()));

		buf.append(STACK_AFTER_COL_LABEL);
		buf.append(spaces(OPSTACK_AFTER_COLWIDTH
				- STACK_AFTER_COL_LABEL.length()));

		return buf.toString();
	}

	/**
	 * Create header line with minus symbol
	 * 
	 * @param lenght
	 * @return text
	 */
	public static String createHeaderLine(int lenght) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i <= lenght - 1; i++) {
			buf.append(JavaLexicalConstants.MINUS);
		}

		return buf.toString();
	}

}
