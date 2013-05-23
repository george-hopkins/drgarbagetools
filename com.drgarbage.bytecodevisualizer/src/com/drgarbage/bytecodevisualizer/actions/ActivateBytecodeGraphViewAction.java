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

import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.RetargetAction;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.bytecodevisualizer.editors.BytecodeEditor;
import com.drgarbage.core.img.CoreImg;


/**
 * Action Change Graph View (Bytecode/Basickblock View).
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: ActivateBytecodeGraphViewAction.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class ActivateBytecodeGraphViewAction extends RetargetAction {

	/** Assigned editor*/
	private BytecodeEditor editor = null;
	
	public static String ID = "com.drgarbage.bytecodevizualizer.actions.activatebytecodeview";

	public ActivateBytecodeGraphViewAction(BytecodeEditor editor) {
		super(ID, BytecodeVisualizerMessages.ActivateBytecodeGraphViewAction_text);
		this.editor = editor;
		setToolTipText(BytecodeVisualizerMessages.ActivateBytecodeGraphViewAction_tooltipText);
		setImageDescriptor(CoreImg.bytecodeViewIcon_16x16);
		this.setEnabled(true);
	}

	
    /**
     * Invoked when an action occurs. 
     */
    public void run() {
    	if(editor != null) {
    		editor.getCanvasControlFlowGraph().changeGraphView(true);
    	}
    }

	public void runWithEvent(Event event) {
		run();	
	}
	
	/**
	 * Radio action.
	 */
	public int getStyle() {
		return AS_RADIO_BUTTON;
	}

}
