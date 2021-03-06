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

package com.drgarbage.bytecode.constant_pool;

import com.drgarbage.bytecode.ByteCodeConstants;
import com.drgarbage.javasrc.JavaLexicalConstants;

import java.io.*;

/**
  * Describes a <code>CONSTANT_String_info</code> constant pool data structure.
  *
  * @author Sergej Alekseev and Peter Palaga    
  * @version $Revision:395 $
  * $Id:ConstantStringInfo.java 395 2008-04-03 15:08:14Z Peter Palaga $
  */
public class ConstantStringInfo extends AbstractConstantPoolEntry {

    public ConstantStringInfo(AbstractConstantPoolEntry[] constantPool) {
		super(CONSTANT_STRING, constantPool);
	}

	/** Length of the constant pool data structure in bytes. */
    public static final int SIZE = 2;
    
    private int stringIndex;
    
    /**
        Get the index of the constant pool entry containing the
        string of this entry.
        @return the index
     */
    public int getStringIndex() {
        return stringIndex;
    }

    /**
        Set the index of the constant pool entry containing the
        string of this entry.
        @param stringIndex the index
     */
    public void setStringIndex(int stringIndex) {
        this.stringIndex = stringIndex;
    }

    public void read(DataInput in) throws IOException {
            
        stringIndex = in.readUnsignedShort();
    }
    
    public void write(DataOutput out)
        throws IOException {

        out.writeByte(ByteCodeConstants.TAG_STRING);
        out.writeShort(stringIndex);
    }

    public boolean equals(Object object) {
        if (!(object instanceof ConstantStringInfo)) {
            return false;
        }
        ConstantStringInfo constantStringInfo = (ConstantStringInfo)object;
        return super.equals(object) && constantStringInfo.stringIndex == stringIndex;
    }

    public int hashCode() {
        return super.hashCode() ^ stringIndex;
    }

	@Override
	public String getInfo() {
		return ByteCodeConstants.string_index + JavaLexicalConstants.EQUALS +  String.valueOf(stringIndex);
	}
    
}
