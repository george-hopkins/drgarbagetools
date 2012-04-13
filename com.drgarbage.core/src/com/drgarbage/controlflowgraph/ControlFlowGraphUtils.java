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

package com.drgarbage.controlflowgraph;

import com.drgarbage.bytecode.instructions.Opcodes;
import com.drgarbage.controlflowgraph.intf.INodeType;

 /*
  * Utilities for handling of the byte code
  *
  * @author Sergej Alekseev
  * @version $Revision:25 $
  * $Id:ControlFlowGraphUtils.java 25 2007-04-01 17:56:22Z aleks $
  */
public class ControlFlowGraphUtils implements Opcodes, INodeType{
	
	/** check if the instruction a jump instruction */
	public static boolean isJumpInstruction (int code){
		           
		switch(code){
			case OPCODE_GOTO    :
			case OPCODE_GOTO_W  :
			case OPCODE_JSR     :
			case OPCODE_JSR_W   :
				return  true;
		}
		                           
		return false;
	}
		
	/** check if the instruction a return instruction */
	public static boolean isReturn (int code){
        
		switch(code){
			case OPCODE_RET     :
			case OPCODE_IRETURN :
			case OPCODE_LRETURN :
			case OPCODE_FRETURN :
			case OPCODE_DRETURN :
			case OPCODE_ARETURN :
			case OPCODE_RETURN  :
			case OPCODE_ATHROW	:
				return  true;
		}
		                           
		return false;
	}
	
	/** check if the instruction a jump or return instruction */
	public static boolean isJumpOrReturn (int code){
        
		switch(code){
			case OPCODE_GOTO    :
			case OPCODE_GOTO_W  :
			case OPCODE_JSR     :
			case OPCODE_JSR_W   :
			case OPCODE_RET     :
			case OPCODE_IRETURN :
			case OPCODE_LRETURN :
			case OPCODE_FRETURN :
			case OPCODE_DRETURN :
			case OPCODE_ARETURN :
			case OPCODE_RETURN  :
			case OPCODE_ATHROW	:
				return  true;
		}
		                           
		return false;
	}
		   
		// OPCODE_NOP 
		// OPCODE_ACONST_NULL
		// OPCODE_ICONST_M1
		// OPCODE_ICONST_0 
		// OPCODE_ICONST_1 
		// OPCODE_ICONST_2 
		// OPCODE_ICONST_3 
		// OPCODE_ICONST_4 
		// OPCODE_ICONST_5 
		// OPCODE_LCONST_0 
		// OPCODE_LCONST_1 
		// OPCODE_FCONST_0 
		// OPCODE_FCONST_1 
		// OPCODE_FCONST_2 
		// OPCODE_DCONST_0 
		// OPCODE_DCONST_1 
		// OPCODE_BIPUSH
		// OPCODE_SIPUSH
		// OPCODE_LDC
		// OPCODE_LDC_W
		// OPCODE_LDC2_W
		// OPCODE_ILOAD
		// OPCODE_LLOAD
		// OPCODE_FLOAD
		// OPCODE_DLOAD
		// OPCODE_ALOAD
		// OPCODE_ILOAD_0
		// OPCODE_ILOAD_1
		// OPCODE_ILOAD_2
		// OPCODE_ILOAD_3
		// OPCODE_LLOAD_0
		// OPCODE_LLOAD_1
		// OPCODE_LLOAD_2
		// OPCODE_LLOAD_3
		// OPCODE_FLOAD_0
		// OPCODE_FLOAD_1
		// OPCODE_FLOAD_2
		// OPCODE_FLOAD_3
		// OPCODE_DLOAD_0
		// OPCODE_DLOAD_1
		// OPCODE_DLOAD_2
		// OPCODE_DLOAD_3
		// OPCODE_ALOAD_0
		// OPCODE_ALOAD_1
		// OPCODE_ALOAD_2
		// OPCODE_ALOAD_3
		// OPCODE_IALOAD
		// OPCODE_LALOAD
		// OPCODE_FALOAD
		// OPCODE_DALOAD
		// OPCODE_AALOAD
		// OPCODE_BALOAD
		// OPCODE_CALOAD
		// OPCODE_SALOAD
		// OPCODE_ISTORE
		// OPCODE_LSTORE
		// OPCODE_FSTORE
		// OPCODE_DSTORE
		// OPCODE_ASTORE
		// OPCODE_ISTORE_0 
		// OPCODE_ISTORE_1 
		// OPCODE_ISTORE_2 
		// OPCODE_ISTORE_3 
		// OPCODE_LSTORE_0 
		// OPCODE_LSTORE_1 
		// OPCODE_LSTORE_2 
		// OPCODE_LSTORE_3 
		// OPCODE_FSTORE_0 
		// OPCODE_FSTORE_1 
		// OPCODE_FSTORE_2 
		// OPCODE_FSTORE_3 
		// OPCODE_DSTORE_0 
		// OPCODE_DSTORE_1 
		// OPCODE_DSTORE_2 
		// OPCODE_DSTORE_3 
		// OPCODE_ASTORE_0 
		// OPCODE_ASTORE_1 
		// OPCODE_ASTORE_2 
		// OPCODE_ASTORE_3 
		// OPCODE_IASTORE 
		// OPCODE_LASTORE 
		// OPCODE_FASTORE 
		// OPCODE_DASTORE 
		// OPCODE_AASTORE 
		// OPCODE_BASTORE 
		// OPCODE_CASTORE 
		// OPCODE_SASTORE 
		// OPCODE_POP
		// OPCODE_POP2
		// OPCODE_DUP
		// OPCODE_DUP_X1
		// OPCODE_DUP_X2
		// OPCODE_DUP2
		// OPCODE_DUP2_X1
		// OPCODE_DUP2_X2
		// OPCODE_SWAP
		// OPCODE_IADD
		// OPCODE_LADD
		// OPCODE_FADD
		// OPCODE_DADD
		// OPCODE_ISUB
		// OPCODE_LSUB
		// OPCODE_FSUB
		// OPCODE_DSUB
		// OPCODE_IMUL
		// OPCODE_LMUL
		// OPCODE_FMUL
		// OPCODE_DMUL
		// OPCODE_IDIV
		// OPCODE_LDIV
		// OPCODE_FDIV
		// OPCODE_DDIV
		// OPCODE_IREM
		// OPCODE_LREM
		// OPCODE_FREM
		// OPCODE_DREM
		// OPCODE_INEG
		// OPCODE_LNEG
		// OPCODE_FNEG
		// OPCODE_DNEG
		// OPCODE_ISHL
		// OPCODE_LSHL
		// OPCODE_ISHR
		// OPCODE_LSHR
		// OPCODE_IUSHR
		// OPCODE_LUSHR
		// OPCODE_IAND
		// OPCODE_LAND
		// OPCODE_IOR
		// OPCODE_LOR
		// OPCODE_IXOR
		// OPCODE_LXOR
		// OPCODE_IINC
		// OPCODE_I2L
		// OPCODE_I2F
		// OPCODE_I2D
		// OPCODE_L2I
		// OPCODE_L2F
		// OPCODE_L2D
		// OPCODE_F2I
		// OPCODE_F2L
		// OPCODE_F2D
		// OPCODE_D2I
		// OPCODE_D2L
		// OPCODE_D2F
		// OPCODE_I2B
		// OPCODE_I2C
		// OPCODE_I2S
		// OPCODE_LCMP
		// OPCODE_FCMPL
		// OPCODE_FCMPG
		// OPCODE_DCMPL
		// OPCODE_DCMPG
		// OPCODE_IFEQ
		// OPCODE_IFNE
		// OPCODE_IFLT
		// OPCODE_IFGE
		// OPCODE_IFGT
		// OPCODE_IFLE
		// OPCODE_IF_ICMPEQ
		// OPCODE_IF_ICMPNE
		// OPCODE_IF_ICMPLT
		// OPCODE_IF_ICMPGE
		// OPCODE_IF_ICMPGT
		// OPCODE_IF_ICMPLE
		// OPCODE_IF_ACMPEQ
		// OPCODE_IF_ACMPNE
		// OPCODE_GOTO
		// OPCODE_JSR
		// OPCODE_RET
		// OPCODE_TABLESWITCH
		// OPCODE_LOOKUPSWITCH
		// OPCODE_IRETURN
		// OPCODE_LRETURN
		// OPCODE_FRETURN
		// OPCODE_DRETURN
		// OPCODE_ARETURN
		// OPCODE_RETURN
		// OPCODE_GETSTATIC
		// OPCODE_PUTSTATIC
		// OPCODE_GETFIELD
		// OPCODE_PUTFIELD
		// OPCODE_INVOKEVIRTUAL
		// OPCODE_INVOKESPECIAL
		// OPCODE_INVOKESTATIC
		// OPCODE_INVOKEINTERFACE
		// OPCODE_XXXUNUSEDXXX
		// OPCODE_NEW
		// OPCODE_NEWARRAY
		// OPCODE_ANEWARRAY
		// OPCODE_ARRAYLENGTH
		// OPCODE_ATHROW
		// OPCODE_CHECKCAST
		// OPCODE_INSTANCEOF
		// OPCODE_MONITORENTER
		// OPCODE_MONITOREXIT
		// OPCODE_WIDE
		// OPCODE_MULTIANEWARRAY
		// OPCODE_IFNULL
		// OPCODE_IFNONNULL
		// OPCODE_GOTO_W
		// OPCODE_JSR_W
		// OPCODE_BREAKPOINT
		// OPCODE_IMPDEP1
		// OPCODE_IMPDEP2              
		// NEWARRAY_T_BOOLEAN
		// NEWARRAY_T_CHAR
		// NEWARRAY_T_FLOAT
		// NEWARRAY_T_DOUBLE
		// NEWARRAY_T_BYTE
		// NEWARRAY_T_SHORT
		// NEWARRAY_T_INT
		// NEWARRAY_T_LONG		   
		public static int getInstructionNodeType(int code){
			  switch(code){
				  case OPCODE_IFEQ	:
				  case OPCODE_IFNE	:
				  case OPCODE_IFLT	:
				  case OPCODE_IFGE	:
				  case OPCODE_IFGT	:
				  case OPCODE_IFLE	:
				  case OPCODE_IF_ICMPEQ	:
				  case OPCODE_IF_ICMPNE	:
				  case OPCODE_IF_ICMPLT	:
				  case OPCODE_IF_ICMPGE	:
				  case OPCODE_IF_ICMPGT	:
				  case OPCODE_IF_ICMPLE	:
				  case OPCODE_IF_ACMPEQ	:
				  case OPCODE_IF_ACMPNE	:
				  case OPCODE_IFNULL	:
				  case OPCODE_IFNONNULL	:
					  				return NODE_TYPE_IF;
	              case OPCODE_RET     :
	              case OPCODE_IRETURN :
	              case OPCODE_LRETURN :
	              case OPCODE_FRETURN :
	              case OPCODE_DRETURN :
	              case OPCODE_ARETURN :
	              case OPCODE_RETURN  :
	              case OPCODE_ATHROW  :
	            	  				return NODE_TYPE_RETURN;
	            
	              case OPCODE_GOTO    :
	              case OPCODE_GOTO_W  :
	              case OPCODE_JSR     :
	              case OPCODE_JSR_W   :
	            	  				return NODE_TYPE_GOTO_JUMP;
	              case OPCODE_TABLESWITCH :
	              case OPCODE_LOOKUPSWITCH :
	            	  				return NODE_TYPE_SWITCH;

	              case OPCODE_INVOKEVIRTUAL	:
	              case OPCODE_INVOKESPECIAL	:
	              case OPCODE_INVOKESTATIC	:
	              case OPCODE_INVOKEINTERFACE	:
	            	  				return NODE_TYPE_INVOKE;
	              case  OPCODE_GETSTATIC :
	              case  OPCODE_GETFIELD  :
	            	  				return NODE_TYPE_GET;
			  }
			  
			  return NODE_TYPE_SIMPLE; 
		}
}
