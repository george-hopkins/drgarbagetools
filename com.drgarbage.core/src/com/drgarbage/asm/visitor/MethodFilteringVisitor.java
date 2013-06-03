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

import com.drgarbage.asm.AnnotationVisitor;
import com.drgarbage.asm.Attribute;
import com.drgarbage.asm.ClassVisitor;
import com.drgarbage.asm.FieldVisitor;
import com.drgarbage.asm.MethodVisitor;
import com.drgarbage.asm.Opcodes;
import com.drgarbage.asm_ext.IConstantPoolVisitor;
import com.drgarbage.bytecode.ConstantPoolParser;
import com.drgarbage.bytecode.constant_pool.AbstractConstantPoolEntry;

/**
 * Method Filtering visitor class.
 * @version $Revision$
 * $Id$
 */
public class MethodFilteringVisitor extends ClassVisitor implements IConstantPoolVisitor {
	
	private AbstractCodeVisitor abstractCodeVisitor;
	private String className;
	
	protected AbstractConstantPoolEntry[] constantPool;

	/**
	 * @param abstractCodeVisitor
	 */
	public MethodFilteringVisitor(AbstractCodeVisitor abstractCodeVisitor) {
		super(Opcodes.ASM4);
		this.abstractCodeVisitor = abstractCodeVisitor;
	}
	
	/**
	 * Returns the class name. The name is set 
	 * only after visiting the class.
	 * @return name
	 */
	public String getClassName() {
		return className;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm_ext.IConstantPoolVisitor#visitConstantPool(byte[], int, int)
	 */
	public void visitConstantPool(final byte[] bytes, int offset, int entryCount) {
		ConstantPoolParser cpp = new ConstantPoolParser(bytes, offset, entryCount);
		constantPool = cpp.parse();
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.asm.ClassVisitor#visit(int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	public void visit(int version, int access, String name,
			String signature, String superName, String[] interfaces) {
		className = name;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.ClassVisitor#visitAnnotation(java.lang.String, boolean)
	 */
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.ClassVisitor#visitAttribute(com.drgarbage.asm.Attribute)
	 */
	public void visitAttribute(Attribute attr) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.ClassVisitor#visitEnd()
	 */
	public void visitEnd() {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.ClassVisitor#visitField(int, java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public FieldVisitor visitField(int access, String name, String desc,
			String signature, Object value) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.ClassVisitor#visitInnerClass(java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public void visitInnerClass(String name, String outerName,
			String innerName, int access) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.ClassVisitor#visitMethod(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		if (abstractCodeVisitor.accept(name, desc, constantPool)) {
			return abstractCodeVisitor;
		}
		else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.ClassVisitor#visitOuterClass(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void visitOuterClass(String owner, String name, String desc) {
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.asm.ClassVisitor#visitSource(java.lang.String, java.lang.String)
	 */
	public void visitSource(String source, String debug) {
	}

}