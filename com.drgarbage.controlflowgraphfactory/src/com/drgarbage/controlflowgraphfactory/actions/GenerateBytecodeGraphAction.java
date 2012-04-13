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

package com.drgarbage.controlflowgraphfactory.actions;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryPlugin;
import com.drgarbage.graph.GraphConstants;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagramFactory;

/**
  * Action for generation of the control flow graph from the byte code
  *
  * @author Sergej Alekseev
  * @version $Revision: 1523 $
  * $Id: GenerateBytecodeGraphAction.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
  */
public class GenerateBytecodeGraphAction extends GenerateGraphAction {

	public GenerateBytecodeGraphAction() {
		super(GraphConstants.GRAPH_TYPE_BYTECODE_GRAPH);
	}

	/**
	 * Creates the diagrapm.
	 */
	protected ControlFlowGraphDiagram createDiagram( String[] classPath, String mPackage, String mClassName, String mMethodName, String mMethodSignature){
		ControlFlowGraphDiagram controlFlowGraphDiagram = null;
		try {
			controlFlowGraphDiagram = ControlFlowGraphDiagramFactory.buildByteCodeControlFlowDiagram(classPath, mPackage, mClassName, mMethodName, mMethodSignature);
		} catch (ControlFlowGraphException e) {
			ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));				
//			Messages.error(e.getMessage()  + CoreMessages.ExceptionAdditionalMessage);
			return null;
		} catch (IOException e) {
			ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
//			Messages.error(e.getMessage() + CoreMessages.ExceptionAdditionalMessage);
			return null;
		}
		
		return controlFlowGraphDiagram;
	}
}
