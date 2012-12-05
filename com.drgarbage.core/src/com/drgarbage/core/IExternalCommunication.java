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

package com.drgarbage.core;

import java.util.List;

import com.drgarbage.bytecode.instructions.AbstractInstruction;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;

/**
 * Interface for external communication with other plugins.
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: IExternalCommunication.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public interface IExternalCommunication {
	
	public final static int BYTECODE_GRAPH = 1;
	public final static int BASICBLOCK_GRAPH = 2;

	/**
	 * Generates graph from instruction list.
	 * @param graphName
	 * @param instructions
	 * @param graphtype
	 */
	public void generateGraphfromInstructionList(String graphName, List<AbstractInstruction> instructions, int graphtype);
	
	/**
	 * Generates graph for the given method.
	 * @param graphName
	 * @param classPath
	 * @param packageName
	 * @param className
	 * @param methodName
	 * @param methodSig
	 */
	public void generateGraph(String graphName,  String[] classPath, String packageName, String className, String methodName, String methodSig);

	
	/**
	 * Generates the Control Flow Graph Diagram for the given graph.
	 * @param graphName
	 * @param graph
	 */
	public void generateDiagramFromGraph(String graphName, IDirectedGraphExt graph);
	
}
