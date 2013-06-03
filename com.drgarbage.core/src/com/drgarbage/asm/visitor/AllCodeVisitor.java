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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.drgarbage.bytecode.ByteCodeConstants;
import com.drgarbage.bytecode.InstructionParser;
import com.drgarbage.bytecode.constant_pool.AbstractConstantPoolEntry;
import com.drgarbage.bytecode.instructions.AbstractInstruction;

/**
 * All Code Visitor class
 * @version $Revision$
 * $Id$
 */
public class AllCodeVisitor extends AbstractCodeVisitor {
	private List<Map<String, Object>> instructionLists = new ArrayList<Map<String,Object>>();
	private String name;
	private String desc;
	
	public boolean accept(String name, String desc, AbstractConstantPoolEntry[] constantPool) {
		this.constantPool = constantPool;
		this.name = name;
		this.desc = desc;
		return true;
	}
	/* (non-Javadoc)
	 * @see com.drgarbage.asm_ext.ICodeVisitor#visitCode(byte[], int, int)
	 */
	public boolean visitCode(byte[] bytes, int offset, int length) {
		InstructionParser parser = new InstructionParser(bytes, offset, length);

		List<AbstractInstruction> instructions = parser.parse();
		parser.parseExceptionTable();
		Map<String,Object> attrs = parser.parseAttributes(attributeNames, constantPool);
		attrs.put(ByteCodeConstants.CODE, instructions);
		attrs.put(ByteCodeConstants.NAME, name);
		attrs.put(ByteCodeConstants.DESCRIPTOR, desc);
		
		instructionLists.add(attrs);
		return true;
	}
	/**
	 * @return the instructionLists
	 */
	public List<Map<String, Object>> getInstructionLists() {
		return instructionLists;
	}
}