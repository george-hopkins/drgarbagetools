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

package com.drgarbage.bytecodevisualizer.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.RetargetAction;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import com.drgarbage.asm.render.intf.IClassFileDocument;
import com.drgarbage.asm.render.intf.IInstructionLine;
import com.drgarbage.asm.render.intf.IMethodSection;
import com.drgarbage.bytecode.instructions.AbstractInstruction;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.bytecodevisualizer.editors.BytecodeEditor;
import com.drgarbage.core.CoreConstants;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.core.CorePlugin;
import com.drgarbage.core.IExternalCommunication;
import com.drgarbage.core.img.CoreImg;
import com.drgarbage.utils.Messages;

/**
 * Creates a diagram and open the control flow graph editor 
 * from Controlflow Factory Plugin.
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class ExportGraphAndOpenWithControlflowgraphFactoryAction extends RetargetAction {

	/**
	 * Text constants from preferences.
	 */
	public static String ID = "com.drgarbage.bytecodevizualizer.actions.createbytecodegraph";

	/**
	 * Active class file editor.
	 */
	private BytecodeEditor editor = null;

	/**
	 * Class file document of the editor.
	 */
	private IClassFileDocument classFileDocument = null;
	
	/**
	 * String of the Constructor.
	 */
	private static final String constructorString = "<init>";
	
	/**
	 * Constructor.
	 * @param editor
	 * @param classFileDocument
	 */
	public ExportGraphAndOpenWithControlflowgraphFactoryAction(BytecodeEditor editor, IClassFileDocument classFileDocument){
		super(ID, BytecodeVisualizerMessages.OpenGraphInControlflowgraphFactoryAction_text);
		this.editor = editor;
		this.classFileDocument = classFileDocument;
		setToolTipText(BytecodeVisualizerMessages.OpenGraphInControlflowgraphFactoryAction_tooltipText);	
		setImageDescriptor(CoreImg.createBytecodeGraphIcon_16x16);
	}

    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.RetargetAction#run()
     */
    public void run() {
    	IExternalCommunication comunicationObject =  CorePlugin.getExternalCommunication();
    	if(comunicationObject == null){
    		
    		String msg = CoreMessages.ERROR_Opening_Graph_in_CFGF_failed
    				+ '\n'
    				+ CoreMessages.ERROR_CFGF_is_not_installed;
    		Messages.error(msg);
    		return;
    	}
    	
    	IMethodSection ms = classFileDocument.findMethodSection(editor.getSelectedLine()/* changed to 0-based */);
    	
    	/* convert instruction lines to instruction list */
    	List<AbstractInstruction> instructions = new ArrayList<AbstractInstruction>();
    	List<IInstructionLine> instructionLines = ms.getInstructionLines();
    	for(IInstructionLine l: instructionLines){
    		instructions.add(l.getInstruction());
    	}

    	StringBuffer buf = new StringBuffer();
		String className = classFileDocument.getClassSimpleName();
		buf.append(className);
    	buf.append('.');	
		
		String methodName = ms.getName();
		if(methodName.equals(constructorString)){
			buf.append(className);
		}
		else{
			buf.append(methodName);	
		}
    	
    	if(editor.getCanvasControlFlowGraph().isBasicblockViewActive()){
        	buf.append(".bb.graph");
    		comunicationObject.generateGraphfromInstructionList(buf.toString(), instructions, IExternalCommunication.BASICBLOCK_GRAPH);
    	}
    	else{	
        	buf.append(".byte.graph");
    		comunicationObject.generateGraphfromInstructionList(buf.toString(), instructions, IExternalCommunication.BYTECODE_GRAPH);
    	}
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.RetargetAction#runWithEvent(org.eclipse.swt.widgets.Event)
	 */
	public void runWithEvent(Event event) {
		run();	
	}
}
