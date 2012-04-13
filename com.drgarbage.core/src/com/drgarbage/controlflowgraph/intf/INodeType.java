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

package com.drgarbage.controlflowgraph.intf;

/**
  *  Defines a vertex type in the control flow diagram.
  *
  *  @author Sergej Alekseev   
  *  @version $Revision:25 $
  *  $Id:INodeType.java 25 2007-04-01 17:56:22Z aleks $
  */
public interface INodeType {
	 public static final int NODE_TYPE_SIMPLE		= 0;
	 public static final int NODE_TYPE_IF			= 1;
	 public static final int NODE_TYPE_RETURN		= 2;
	 public static final int NODE_TYPE_GOTO_JUMP	= 3;
	 public static final int NODE_TYPE_SWITCH		= 4;
	 public static final int NODE_TYPE_INVOKE		= 5;
	 public static final int NODE_TYPE_GET			= 6;
	 public static final int NODE_TYPE_START		= 7;
	 public static final int NODE_TYPE_EXIT			= 8;
	 public static final int NODE_TYPE_COMMENT		= 9;
}
