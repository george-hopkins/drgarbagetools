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
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;

import com.drgarbage.bytecode.instructions.AbstractInstruction;
import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryPlugin;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.core.IExternalCommunication;
import com.drgarbage.utils.Messages;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagramFactory;

/**
 * Generate graph. This action is called from external plugin.
 *
 * @author Sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: ExternalActionGenerateGraph.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class ExternalActionGenerateGraph implements IExternalCommunication {

	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.plugin.IExternalCommunication#generateGraphfromInstructionList(java.lang.String, java.util.List, int)
	 */
	public void generateGraphfromInstructionList(String graphName, List<AbstractInstruction> instructions, int graphtype) {
		final ControlFlowGraphDiagram controlFlowGraphDiagram = createGraphDiagram(instructions, graphtype);
		
		if(controlFlowGraphDiagram == null){
			Messages.info(ControlFlowFactoryMessages.DiagramIsNullMessage);
			return;
		}
		
		/* get active page */
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		/* create empty shell */
		Shell shell = page.getActivePart().getSite().getShell();
		
		/* Show a SaveAs dialog */
		SaveAsDialog dialog = new SaveAsDialog(shell);

		dialog.setOriginalName(graphName);
		dialog.open();
		
		IPath path = dialog.getResult();		
		if (path == null) {/* action canceled */
			return;
		}

		ActionUtils.saveDiagramInFileAndOpenEditor(path, shell, page, controlFlowGraphDiagram, true);

	}

	/**
	 * Creates a graph diagram: Bytecode or Soucesode diagram.
	 * @param instructions
	 * @param graphtype
	 * @return
	 */
	private ControlFlowGraphDiagram createGraphDiagram(List<AbstractInstruction> instructions, int graphtype){
		ControlFlowGraphDiagram controlFlowGraphDiagram;

		/* create control flow gaph diagram */
		try{
			if(graphtype == IExternalCommunication.BYTECODE_GRAPH)
				controlFlowGraphDiagram = ControlFlowGraphDiagramFactory.buildByteCodeControlFlowDiagram(instructions);
			else
				controlFlowGraphDiagram = ControlFlowGraphDiagramFactory.buildBasicblockGraphDiagram(instructions);

		} catch (ControlFlowGraphException e) {
			ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
			Messages.error(e.getMessage() + CoreMessages.ExceptionAdditionalMessage);
			return null;
		} catch (IOException e) {
			ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
			Messages.error(e.getMessage() + CoreMessages.ExceptionAdditionalMessage);
			return null;
		}

		return controlFlowGraphDiagram;
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.plugin.IExternalCommunication#generateGraph(java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void generateGraph(String graphName, String[] classPath, String packageName, String className, String methodName, String methodSig) {
		final ControlFlowGraphDiagram controlFlowGraphDiagram = createGraphDiagram(graphName, classPath, packageName, className, methodName, methodSig);
		
		if(controlFlowGraphDiagram == null){
			Messages.info(ControlFlowFactoryMessages.DiagramIsNullMessage);
			return;
		}

		/* get active page */
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		/* create empty shell */
		Shell shell = page.getActivePart().getSite().getShell();
		
		/* Show a SaveAs dialog */
		SaveAsDialog dialog = new SaveAsDialog(shell);

		dialog.setOriginalName(graphName);
		dialog.open();

		IPath path = dialog.getResult();		
		if (path == null) {/* action canceled */
			return;
		}

		ActionUtils.saveDiagramInFileAndOpenEditor(path, shell, page, controlFlowGraphDiagram, true);

	}
	
	/**
	 * Creates diagram for the given method.
	 * @param graphName
	 * @param classPath
	 * @param packageName
	 * @param className
	 * @param methodName
	 * @param methodSig
	 * @return
	 */
	private ControlFlowGraphDiagram createGraphDiagram(String graphName, String[] classPath, String packageName, String className, String methodName, String methodSig){
		ControlFlowGraphDiagram controlFlowGraphDiagram = null;;
		
		try {
			controlFlowGraphDiagram = ControlFlowGraphDiagramFactory.buildSourceControlFlowDiagram(classPath, packageName, className, methodName, methodSig);
		} catch (ControlFlowGraphException e) {
			ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
			Messages.error(e.getMessage() + CoreMessages.ExceptionAdditionalMessage);
			return null;
		} catch (IOException e) {
			ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
			Messages.error(e.getMessage() + CoreMessages.ExceptionAdditionalMessage);
			return null;
		}

		return controlFlowGraphDiagram;
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.core.IExternalCommunication#generateDiagramFromGraph(java.lang.String, com.drgarbage.controlflowgraph.intf.IDirectedGraphExt)
	 */
	public void generateDiagramFromGraph(String graphName, IDirectedGraphExt graph) {
		final ControlFlowGraphDiagram controlFlowGraphDiagram = ControlFlowGraphDiagramFactory.createControlFlowGraphDiagram(graph);
		
		if(controlFlowGraphDiagram == null){
			Messages.info(ControlFlowFactoryMessages.DiagramIsNullMessage);
			return;
		}

		/* get active page */
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		/* create empty shell */
		Shell shell = page.getActivePart().getSite().getShell();
		
		/* Show a SaveAs dialog */
		SaveAsDialog dialog = new SaveAsDialog(shell);

		dialog.setOriginalName(graphName);
		dialog.open();

		IPath path = dialog.getResult();		
		if (path == null) {/* action canceled */
			return;
		}

		ActionUtils.saveDiagramInFileAndOpenEditor(path, shell, page, controlFlowGraphDiagram, true);

		
	}
}