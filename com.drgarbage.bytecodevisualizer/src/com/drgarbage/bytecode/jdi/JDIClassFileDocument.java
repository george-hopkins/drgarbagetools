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

package com.drgarbage.bytecode.jdi;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.drgarbage.asm.ClassVisitor;
import com.drgarbage.asm.MethodVisitor;
import com.drgarbage.asm.render.impl.AbstractClassFileDocument;
import com.drgarbage.asm.render.intf.ILocalVariableTable;
import com.drgarbage.asm.signature.SignatureReader;
import com.drgarbage.bytecode.ByteCodeConstants;
import com.drgarbage.bytecode.LocalVariableTableEntry;
import com.drgarbage.bytecode.LocalVariableTypeTableEntry;
import com.drgarbage.bytecode.ByteCodeConstants.Align;
import com.drgarbage.javasrc.JavaKeywords;
import com.drgarbage.javasrc.JavaLexicalConstants;
import com.drgarbage.javasrc.JavaSourceConstants;
import com.drgarbage.javasrc.JavaSourceUtils;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;

public class JDIClassFileDocument extends AbstractClassFileDocument {
	private class JDILocalVariableTable implements ILocalVariableTable {

		private class JDIEntry extends LocalVariableTableEntry {
			private LocalVariable localVariable;

			public JDIEntry(LocalVariable lv) {
				super();
				this.localVariable = lv;
				if ("org.eclipse.jdi.internal.LocalVariableImpl".equals(lv.getClass().getName())) {
					
					startPc = (int) getReflectedLong(lv, "fCodeIndex");
					length = getReflectedInt(lv, "fLength");
				    Integer ii = (Integer)invokeReflectedMethod(lv, "slot");
					index = ii == null ? ByteCodeConstants.INVALID_OFFSET : ii.intValue();

				}
			}
		}

		private boolean available = true;
		private HashMap<Integer, JDIEntry[]> indexedJdiEntries;
		private List<LocalVariableTableEntry> jdiEntries;
		
		public JDILocalVariableTable(List<LocalVariable> localVariables, boolean available) {
			super();
			this.available = available;
			if (localVariables != null && localVariables.size() > 0) {
				for (LocalVariable lv : localVariables) {
					JDIEntry e = new JDIEntry(lv);
					addLocalVariableTableEntry(e);
				}
			}
		}
		
		public void addLocalVariableTableEntry(LocalVariableTableEntry entry) {
			if (!(entry instanceof JDIEntry)) {
				throw new RuntimeException("Entry must be a JDIEntry.");
			}
			JDIEntry e = (JDIEntry) entry;
			if (jdiEntries == null) {
				jdiEntries = new ArrayList<LocalVariableTableEntry>(4);
			}
			jdiEntries.add(e);
			
			if (indexedJdiEntries == null) {
				indexedJdiEntries = new HashMap<Integer, JDIEntry[]>();
			}
			
			Integer key = Integer.valueOf(e.getIndex());
			
			JDIEntry[] entries = indexedJdiEntries.get(key);
			if (entries == null) {
				entries = new JDIEntry[1];
				entries[0] = e;
			}
			else {
				/* realloc */
				JDIEntry[] newEntries = new JDIEntry[entries.length +1];
				System.arraycopy(entries, 0, newEntries, 0, entries.length);
				entries = newEntries;
				entries[entries.length - 1] = e;
				
				/* check if reordering is necessary */
				LocalVariableTableEntry last = entries[entries.length -2];
				if (ILocalVariableTable.END_OFFSET_COMPARATOR.compare(last, entry) > 0) {
					/* last > entry: reordering needed */
					Arrays.sort(entries, ILocalVariableTable.END_OFFSET_COMPARATOR);
				}

			}
			
			indexedJdiEntries.put(key, entries);
		}

		public void addLocalVariableTypeTableEntry(
				LocalVariableTypeTableEntry entry) {
		}
		

		public String findArgName(int i, int offset, boolean isConstructor, boolean isStatic) {
			if (!isStatic) {
				if (i == 0) {
					return JavaKeywords.THIS;
				}
			}
			if (indexedJdiEntries == null) {
				return null;
			}
			
			
			JDIEntry[] entries = indexedJdiEntries.get(Integer.valueOf(i));
			JDIEntry match = null;
			if (entries != null) {
				if (entries.length == 1) {
					match = entries[0];
				}
				else if (entries.length > 1) {
					/* search */
					for (JDIEntry e : entries) {
						if (offset < e.getStartPc() + e.getLength()) {
							/* take the first which is just lower than
							 * we can do it, as entries are sorted according to 
							 * their end offsets. 
							 * We do not check the start offset:
							 * 
							 *   "The given local variable must have a value at indices into 
							 *   the code array in the interval [start_pc, start_pc+length], 
							 *   that is, between start_pc and start_pc+length inclusive."
							 *   
							 *   http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#5956
							 *   
							 *  Note that the specification mentions only where the variable 
							 *  must have a value. As a matter of fact, a variable is initialized
							 *  at offsets where it is still does not have a value. 
							 *  So the name of the variable is valid even before the start_pc.
							 *  
							 *  This has to do with BUG#118
							 *  
							 *   */
							match = e;
							break;
						}
					}
				}
			}
			
			if (match != null) {
				return match.localVariable.name();
			}
			return null;
			
		}

		public Align getAlignment(int column) {
			return column == HEADER.length - 1 ? Align.LEFT : Align.RIGHT;
		}

		public List<LocalVariableTableEntry> getEntries() {
			return jdiEntries;
		}

		public String[] getHeader() {
			return HEADER;
		}

		public int getLength() {
			return jdiEntries == null ? 0 : jdiEntries.size();
		}

		public String[] getRow(int index) {
			LocalVariableTableEntry en = jdiEntries.get(index);
			LocalVariable lv = ((JDIEntry)en).localVariable;
			return new String[] {
					String.valueOf(en.getIndex()),
					String.valueOf(en.getStartPc()), 
					String.valueOf(en.getLength()),
					String.valueOf(lv.typeName() + JavaLexicalConstants.SPACE + lv.name()),

//										((ConstantUtf8Info)constantPool[en.getNameIndex()]).getString(),
//										((ConstantUtf8Info)constantPool[en.getDescriptorIndex()]).getString()
			};
		}

		public boolean isAvailable() {
			return available;
		}
		
	}

	protected class JDIMethodRenderer extends AbstractMethodRenderer {

		private List<LocalVariable> localVariables;
		
		public JDIMethodRenderer(int access, String name, String descriptor,
				String signature, String[] exceptions, List<LocalVariable> localVariables) {
			this(access, name, descriptor, signature, exceptions, null, localVariables);
		}

		public JDIMethodRenderer(int access, String name, String descriptor,
				String signature, String[] exceptions, MethodVisitor mv, List<LocalVariable> localVariables) {
			super(access, name, descriptor, signature, exceptions, mv);
			this.localVariables = localVariables;
		}

		@Override
		protected ILocalVariableTable createLocalVariableTable(boolean available) {
			return new JDILocalVariableTable(localVariables, localVariables != null);
		}
		
	}
	
	/**
	 * Root of the outline elements.
	 */
	private ClassVisitor classVisitor;
	
	private static final String[] HEADER = new String[] {
		ByteCodeConstants.INDEX,
		ByteCodeConstants.START_PC, 
		ByteCodeConstants.LENGTH,
		ByteCodeConstants.TYPE_AND_NAME
//				ByteCodeConstants.NAME,
//				ByteCodeConstants.DESCRIPTOR
};
	
	private static Object invokeReflectedMethod(Object inst, String methodName) {
		try {
			java.lang.reflect.Method m = null;
			m = inst.getClass().getMethod(methodName, new Class<?>[]{});
			return m.invoke(inst, new Object[] {});
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return null;
	}
	
	private String debugTargetName;
	private ReferenceType referenceType;
	
	public JDIClassFileDocument(ReferenceType referenceType, String debugTargetName, ClassVisitor classVisitor) {
		super();
		this.referenceType = referenceType;
		this.debugTargetName = debugTargetName;
		this.classVisitor = classVisitor;
	}
	
	/**
	 * Creates JDI content.
	 */
	@SuppressWarnings("unchecked")
	public void createJDIcontent(){
		byte[] constPool = (byte[]) invokeReflectedMethod(referenceType, "constantPool");
		Integer constPoolCount = (Integer) invokeReflectedMethod(referenceType, "constantPoolCount");
		if (constPoolCount == null) {
			constPoolCount = Integer.valueOf(0);
		}
		
		visitConstantPool(constPool, 0, constPoolCount.intValue());
		
		
		appendHeaderComment(ByteCodeConstants.JDI, debugTargetName);
		appendNewline();
		
		Integer majorVersion = (Integer) invokeReflectedMethod(referenceType, "majorVersion");
		if (majorVersion == null) {
			majorVersion = Integer.valueOf(ByteCodeConstants.INVALID_OFFSET);
		}
		Integer minorVersion = (Integer) invokeReflectedMethod(referenceType, "minorVersion");
		if (minorVersion == null) {
			minorVersion = Integer.valueOf(ByteCodeConstants.INVALID_OFFSET);
		}
		appendClassFileFormatVersion(majorVersion.intValue(), minorVersion.intValue());
		
	    this.name = referenceType.name(); 
	    classSimpleName = JavaSourceUtils.getSimpleName(name);
		appendPackage(name);
		appendNewline();

		int access = referenceType.modifiers();
		
		/* AFAIK there is no way to access deprecated over JDI 
		 * appendDeprecated(access);
		 * */
	
	    appendAccess(access & ~ACC_SUPER);
	    if ((access & ACC_ANNOTATION) != 0) {
	    	appendAnnotation(JavaKeywords.INTERFACE);
	    	appendSpace();
	    } else if ((access & ACC_INTERFACE) != 0) {
	    	sb.append(JavaKeywords.INTERFACE);
	    	appendSpace();
	    } else if ((access & ACC_ENUM) == 0) {
	    	sb.append(JavaKeywords.CLASS);
	    	appendSpace();
	    }

	    sb.append(classSimpleName);
	    classSignatureDocumentLine = lineCount; /* set class file line number reference */
	    
	    String signature = referenceType.genericSignature();
	    if (signature != null) {
	        SignatureRenderer sv = new SignatureRenderer(access);
	        SignatureReader r = new SignatureReader(signature);
	        r.accept(sv);
	        sb.append(sv.getDeclaration());
	    }
	    else {
	    	/* null signature */
	    	if (referenceType instanceof ClassType) {
	    		ClassType classType = (ClassType) referenceType;
		    	String superName = classType.superclass().name();
			    if (superName != null && !JavaSourceConstants.JAVA_LANG_OBJECT.equals(superName)) {
					appendSpace();
					append(JavaKeywords.EXTENDS);
					appendSpace();
					appendJavaSourcePath(superName);
			    }
			    
			    List<?> interfaces = classType.interfaces();
			    if (interfaces != null && interfaces.size() > 0) {
					appendSpace();
					append(JavaKeywords.IMPLEMENTS);

			        for (int i = 0; i < interfaces.size(); ++i) {
						if (i != 0) {
							appendComma();
						}
						appendSpace();
						append(interfaces.get(i));
			        }
			    }
	    	}
	    	
	    	if (referenceType instanceof InterfaceType) {
	    		InterfaceType interfaceType = (InterfaceType) referenceType;
			    List<?> interfaces = interfaceType.superinterfaces();
			    if (interfaces != null && interfaces.size() > 0) {
					appendSpace();
					append(JavaKeywords.EXTENDS);

			        for (int i = 0; i < interfaces.size(); ++i) {
						if (i != 0) {
							appendComma();
						}
						appendSpace();
						append(interfaces.get(i));
			        }
			    }
	    	}
	    }
	
	    /* fill outline */
		if (classVisitor != null) {
			classVisitor.visit(majorVersion,
			    access,
			    name,
			    signature,
			    null,
			    null);
		}
	    
		appendSpace();
		appendLeftBrace();
		appendNewline();
		incrementIndent();
		
		appendConstantPool();
		
	    try {
			String file = referenceType.sourceName();
			if (file != null) {
				appendSourcePathComment(file);
			}
		} catch (AbsentInformationException e) {
		}
		
		
		/* AFAIK there is no debug info in JDI */
		/*
	    if (debugInfo != null) {
	    	appendDebugInfoComment(debugInfo);
	    }
		*/
		
		/* TODO: outer class */
		/* AFAIK there is no way to access the following information over JDI:
		 *  - class attributes 
		 *  - Annotations*/
		
		/* inner classes */
		List<?> nestedTypes = referenceType.nestedTypes();
		if (nestedTypes != null && nestedTypes.size() > 0) {
			
			appendNewline();
	    	appendCommentBegin();
	    	appendSpace();
	    	sb.append(ByteCodeConstants.LOADED_INNER_TYPES);
	    	appendSpace();
	    	appendCommentEnd();
	    	appendNewline();
			
			for (Object o : nestedTypes) {
				ReferenceType rt = (ReferenceType) o;
			    String innerClassSimpleName = JavaSourceUtils.getSimpleName(rt.name());

		    	
		    	appendCommentBegin();
		    	appendSpace();
			    appendAccess(rt.modifiers() & ~ACC_SUPER);
		    	sb.append(innerClassSimpleName);
		    	appendSpace();
		    	appendCommentEnd();
		    	appendNewline();
		    }
		}
		
		
		/* fields */
		List<?> fields = referenceType.fields();
		if (fields != null && fields.size() > 0) {
			for (Object o : fields) {
				Field f = (Field) o;
			    super.visitField(f.modifiers(), f.name(), f.signature(), f.genericSignature());
				
			    /* fill outline */
			    if (classVisitor != null) {
					classVisitor.visitField(f.modifiers(), f.name(), f.signature(), null, null);
				}

			}
		}
		
		/* methods */
		List<?> methods = referenceType.methods();
		if (methods != null && methods.size() > 0) {
			for (Object o : methods) {
				Method m = (Method) o;
				
				List<LocalVariable> vars = null;
				try {
					vars = m.variables();
				} catch (AbsentInformationException e) {
				}
				AbstractMethodRenderer mr = new JDIMethodRenderer(m.modifiers(), m.name(), m.signature(), m.genericSignature(), null, vars);
				
				/* fill outline */
				if (classVisitor != null) {
					classVisitor.visitMethod(
							m.modifiers(), 
							m.name(),
						    m.signature(), 
						    m.genericSignature(),
						    null);
				}
				
//				mr.visitTryCatchBlock(start,
//                        end,
//                        handler,
//                        readUTF8(items[type], c));

				
//                if (mr instanceof ILocalVariableTableVisitor) {
//                	((ILocalVariableTableVisitor)mr).visitLocalVariableTable(b, w, k);
//                }

				if (mr.hasCode()) {
					byte[] instructions = m.bytecodes();
					mr.visitCode(instructions);
				}
                
				//mv.visitAttribute(cattrs);
                //mr.visitMaxs(maxStack, maxLocals);
                mr.visitEnd();
			}
		}
		
		/* end up */
		decrementIndent();
		appendRightBrace();
		appendNewline();
		
		methodBorderLines = new int[methodBorderLinesList.size()];
		for (int i = 0; i < methodBorderLinesList.size(); i++) {
			methodBorderLines[i] = methodBorderLinesList.get(i).intValue();
		}
		methodBorderLinesList = null;
		
	}
	private int getReflectedInt(Object inst, String fieldName) {
		try {
			java.lang.reflect.Field fld = inst.getClass().getDeclaredField(fieldName);
			fld.setAccessible(true);
			return fld.getInt(inst);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
		}
		return ByteCodeConstants.INVALID_OFFSET;
	}

	private long getReflectedLong(Object inst, String fieldName) {
		try {
			java.lang.reflect.Field fld = inst.getClass().getDeclaredField(fieldName);
			fld.setAccessible(true);
			return fld.getLong(inst);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
		}
		return ByteCodeConstants.INVALID_OFFSET;
	} 

}
