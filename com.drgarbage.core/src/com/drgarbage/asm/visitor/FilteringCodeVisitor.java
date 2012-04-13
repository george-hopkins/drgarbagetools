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

import java.util.List;
import java.util.Map;

import com.drgarbage.bytecode.ByteCodeConstants;
import com.drgarbage.bytecode.InstructionParser;
import com.drgarbage.bytecode.LineNumberTableEntry;
import com.drgarbage.bytecode.constant_pool.AbstractConstantPoolEntry;
import com.drgarbage.bytecode.instructions.AbstractInstruction;

/**
 * Filtering code visitor class.
 * @version $Revision: 1523 $
 * $Id: FilteringCodeVisitor.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class FilteringCodeVisitor extends AbstractCodeVisitor {
	private String name;
	private String descriptor;
	private List<AbstractInstruction> instructions;

	private LineNumberTableEntry[] lineNumberTable;
	
	public FilteringCodeVisitor(String name, String descriptor) {
		super();
		this.name = name;
		this.descriptor = descriptor;
	}
	public boolean accept(String name, String desc, AbstractConstantPoolEntry[] constantPool) {
		this.constantPool = constantPool;
		return this.name.equals(name) && this.descriptor.equals(desc);
	}
	/* (non-Javadoc)
	 * @see com.drgarbage.asm_ext.ICodeVisitor#visitCode(byte[], int, int)
	 */
	public boolean visitCode(byte[] bytes, int offset, int length) {
		InstructionParser parser = new InstructionParser(bytes, offset, length);
		instructions = parser.parse();
		parser.parseExceptionTable();
		Map<String,Object> attrs = parser.parseAttributes(attributeNames, constantPool);

		if (attrs != null) {
			lineNumberTable = (LineNumberTableEntry[]) attrs.get(ByteCodeConstants.LINE_NUMBER_TABLE);
		}
		
		return true;
	}

	/**
	 * Returns the instruction list of the method.
	 * @return instructions
	 */
	public List<AbstractInstruction> getInstructions() {
		return instructions;
	}
	
	/**
	 * returns the line number table of the method.
	 * @return lineNumberTable
	 */
	public LineNumberTableEntry[] getLineNumberTable() {
		return lineNumberTable;
	}
}
