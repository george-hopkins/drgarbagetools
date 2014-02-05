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

package com.drgarbage.asm.visitor;

import java.util.HashSet;
import java.util.Set;

import com.drgarbage.asm.AnnotationVisitor;
import com.drgarbage.asm.Attribute;
import com.drgarbage.asm.Label;
import com.drgarbage.asm.MethodVisitor;
import com.drgarbage.asm.Opcodes;
import com.drgarbage.asm_ext.ICodeVisitor;
import com.drgarbage.bytecode.ByteCodeConstants;
import com.drgarbage.bytecode.constant_pool.AbstractConstantPoolEntry;


/**
 * Basis class for visitor implementations.
 * @version $Revision$
 * $Id$
 */
public abstract class AbstractCodeVisitor extends MethodVisitor implements ICodeVisitor {
	protected AbstractConstantPoolEntry[] constantPool;
	protected Set<String> attributeNames;

	/**
	 * Constructor.
	 */
	public AbstractCodeVisitor() {
		super(Opcodes.ASM4);
		attributeNames = new HashSet<String>();
		attributeNames.add(ByteCodeConstants.LINE_NUMBER_TABLE);
	}


	/**
	 * Visitor pattern method.
	 * @param name
	 * @param desc
	 * @param constantPool
	 * @return <code>true</code or <code>false</code>
	 */
	public abstract boolean accept(String name, String desc, AbstractConstantPoolEntry[] constantPool);


	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitAnnotation(java.lang.String, boolean)
	 */
	public AnnotationVisitor visitAnnotation(String desc,
			boolean visible) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitAnnotationDefault()
	 */
	public AnnotationVisitor visitAnnotationDefault() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitAttribute(com.drgarbage.asm.Attribute)
	 */
	public void visitAttribute(Attribute attr) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitCode()
	 */
	public void visitCode() {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitEnd()
	 */
	public void visitEnd() {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitFieldInsn(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void visitFieldInsn(int opcode, String owner, String name,
			String desc) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitFrame(int, int, java.lang.Object[], int, java.lang.Object[])
	 */
	public void visitFrame(int type, int local, Object[] local2,
			int stack, Object[] stack2) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitIincInsn(int, int)
	 */
	public void visitIincInsn(int var, int increment) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitInsn(int)
	 */
	public void visitInsn(int opcode) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitIntInsn(int, int)
	 */
	public void visitIntInsn(int opcode, int operand) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitJumpInsn(int, com.drgarbage.asm.Label)
	 */
	public void visitJumpInsn(int opcode, Label label) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitLabel(com.drgarbage.asm.Label)
	 */
	public void visitLabel(Label label) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitLdcInsn(java.lang.Object)
	 */
	public void visitLdcInsn(Object cst) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitLineNumber(int, com.drgarbage.asm.Label)
	 */
	public void visitLineNumber(int line, Label start) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitLocalVariable(java.lang.String, java.lang.String, java.lang.String, com.drgarbage.asm.Label, com.drgarbage.asm.Label, int)
	 */
	public void visitLocalVariable(String name, String desc,
			String signature, Label start, Label end, int index) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitLookupSwitchInsn(com.drgarbage.asm.Label, int[], com.drgarbage.asm.Label[])
	 */
	public void visitLookupSwitchInsn(Label dflt, int[] keys,
			Label[] labels) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitMaxs(int, int)
	 */
	public void visitMaxs(int maxStack, int maxLocals) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitMethodInsn(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void visitMethodInsn(int opcode, String owner, String name,
			String desc) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitMultiANewArrayInsn(java.lang.String, int)
	 */
	public void visitMultiANewArrayInsn(String desc, int dims) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitParameterAnnotation(int, java.lang.String, boolean)
	 */
	public AnnotationVisitor visitParameterAnnotation(int parameter,
			String desc, boolean visible) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitTableSwitchInsn(int, int, com.drgarbage.asm.Label, com.drgarbage.asm.Label[])
	 */
	public void visitTableSwitchInsn(int min, int max, Label dflt,
			Label[] labels) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitTryCatchBlock(com.drgarbage.asm.Label, com.drgarbage.asm.Label, com.drgarbage.asm.Label, java.lang.String)
	 */
	public void visitTryCatchBlock(Label start, Label end,
			Label handler, String type) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitTypeInsn(int, java.lang.String)
	 */
	public void visitTypeInsn(int opcode, String type) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.MethodVisitor#visitVarInsn(int, int)
	 */
	public void visitVarInsn(int opcode, int var) {
	}

}