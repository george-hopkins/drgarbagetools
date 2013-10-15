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

package com.drgarbage.bytecode.jdi;


import java.util.List;

import com.drgarbage.asm.ClassWriter;
import com.drgarbage.asm.FieldVisitor;
import com.drgarbage.asm.MethodVisitor;
import com.drgarbage.asm.Opcodes;
import com.drgarbage.bytecode.ByteCodeConstants;
import com.drgarbage.javasrc.JavaLexicalConstants;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;

/**
 * The utility routings for JDI handling.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class JDIUtils {

	/**
	 * Returns the class file content as a byte array for
	 * the given JDI ReferenceType object.
	 * 
	 * @param ref ReferenceType object
	 * @return byte array
	 */
	public static byte[] referenceTypetoByteArray(ReferenceType ref) {
		
	/*
		ClassFile {
		u4             magic;
		u2             minor_version;
		u2             major_version;
		u2             constant_pool_count;
		cp_info        constant_pool[constant_pool_count-1];
		u2             access_flags;
		u2             this_class;
		u2             super_class;
		u2             interfaces_count;
		u2             interfaces[interfaces_count];
		u2             fields_count;
	    field_info     fields[fields_count];
	    u2             methods_count;
	    method_info    methods[methods_count];
	    u2             attributes_count;
	    attribute_info attributes[attributes_count];
	 }
	*/
		
		ClassWriter cw = new ClassWriter(0);

		int version = (ref.minorVersion() << 16) + (ref.majorVersion());
		int access = ref.modifiers();
		String name = toFullyQualifiedName(ref.name());
		String signature = ref.genericSignature();
		
		String superName = null;
		String[] interfacesList = null; 
		if (ref instanceof ClassType) {
			ClassType classType = (ClassType) ref;
			superName = toFullyQualifiedName(classType.superclass().name());

			List<InterfaceType> interfaces = classType.interfaces();
			if (interfaces != null && interfaces.size() > 0) {
				interfacesList = new String[interfaces.size()];
				for (int i = 0; i < interfaces.size(); ++i) {
					InterfaceType iType  = interfaces.get(i);
					interfacesList[i] = toFullyQualifiedName(iType.name());
				}
			}
		}
   	
		if (ref instanceof InterfaceType) {
			superName = "java/lang/Object";
			
			InterfaceType interfaceType = (InterfaceType) ref;

			List<InterfaceType> interfaces = interfaceType.superinterfaces();
			if (interfaces != null && interfaces.size() > 0) {
				interfacesList = new String[interfaces.size()];
				for (int i = 0; i < interfaces.size(); ++i) {
					InterfaceType iType  = interfaces.get(i);
					interfacesList[i] = toFullyQualifiedName(iType.name());
				}
			}
		}
	
		cw.visit(version, 
				access, 
				name, 
				signature, 
				superName, 
				interfacesList);
		
	/*
		u2             fields_count;
	    field_info     fields[fields_count];
	*/
		FieldVisitor fv;	
		List<Field> fields = ref.allFields();
		for(Field f : fields){
			fv = cw.visitField(
					f.modifiers(), 
					f.name(),
					f.signature(),
					f.genericSignature(),
					null);
			fv.visitEnd();
		}

	/*
	    u2             methods_count;
	    method_info    methods[methods_count];
	*/
		MethodVisitor mv;
		List<Method> methods = ref.allMethods();
		for(Method m: methods){
			mv = cw.visitMethod(
					m.modifiers(), 
					m.name(), 
					m.signature(), 
					m.genericSignature(), 
					null);
			
			if(hasCode(m)){
				//TODO: visit code
				mv.visitInsn(Opcodes.NOP);
				
			}
			
			mv.visitMaxs(0, 0); /* no access via JDI */
			mv.visitEnd();
		}

		
	/*
	    u2             attributes_count;
	    attribute_info attributes[attributes_count];
	*/
	
		//TODO: implement attributes  
		
		/* finish the class */
		cw.visitEnd();
		return cw.toByteArray();
	}
	
	public static boolean hasCode(Method m){
		if(m.isAbstract() || m.isNative()){
			return false;
		}
		
		if(m.bytecodes() != null && m.bytecodes().length > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the fully qualified name (as returned by Class.getName(), 
	 * where '.' are replaced by '/'. 
	 * @param name the class name in dot notation e.g <code>java.lang.Object</code>
	 * @return the fully qualified name e.g <code>java/lang/Object</code>
	 */
	public static String toFullyQualifiedName(String name) {
		return name.replace(JavaLexicalConstants.DOT, ByteCodeConstants.CLASS_NAME_SLASH);
	}

}
