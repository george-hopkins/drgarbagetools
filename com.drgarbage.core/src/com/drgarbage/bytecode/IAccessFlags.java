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

package com.drgarbage.bytecode;

/**
  * Defines access flags constants and verbose expressions as defined by
  * the java access modifiers.
  *
  * @author Sergej Alekseev and Peter Palaga  
  * @version $Revision$
  * $Id$
  */
public interface IAccessFlags {
	
	//TODO check if the field of ASM.Opcodes are the same

    public static final int ACC_PUBLIC = 0x0001;
    public static final int ACC_PRIVATE = 0x0002;
    public static final int ACC_PROTECTED = 0x0004;
    public static final int ACC_STATIC = 0x0008;
    public static final int ACC_FINAL = 0x0010;
    public static final int ACC_SYNCHRONIZED = 0x0020;
    /**
     * For ClassFile structures, 0x0020 is ACC_SUPER, which has historical significance only
     */
    public static final int ACC_SUPER = 0x0020;
    public static final int ACC_VOLATILE = 0x0040;
    public static final int ACC_TRANSIENT = 0x0080;

    public static final int ACC_NATIVE = 0x0100;
    public static final int ACC_INTERFACE = 0x0200;
    public static final int ACC_ABSTRACT = 0x0400;
    public static final int ACC_STRICT = 0x0800;

    /**
     * new in Java 1.4
     */
    public final static int ACC_SYNTHETIC = 0x1000;
    /**
     * new in Java 1.5
     */
    public final static int ACC_ANNOTATION = 0x2000;
    /**
     * new in Java 1.5
     */
    public final static int ACC_ENUM = 0x4000;
    /**
     * new in Java 1.5
     */
    public final static int ACC_BRIDGE = 0x0040;
    /**
     * new in Java 1.5
     */
    public final static int ACC_VARARGS = 0x0080;


}
