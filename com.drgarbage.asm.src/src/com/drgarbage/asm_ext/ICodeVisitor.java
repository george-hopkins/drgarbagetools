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

package com.drgarbage.asm_ext;

public interface ICodeVisitor {
    /**
     * Visits the bytecode of a method.
     * 
     * @param bytes a byte array containing the bytecode of a method 
     * @param offset an index telling where is the first instruction in the bytes-array  
     * @param length how many byte belong to the given method
     * @return <code>true</code> if the caller should proceed in visiting the bytecode himself; 
     *          <code>false</code> if the caller himself should not parse the instructions. 
     */
    public boolean visitCode(final byte[] bytes, int offset, int length);
}
