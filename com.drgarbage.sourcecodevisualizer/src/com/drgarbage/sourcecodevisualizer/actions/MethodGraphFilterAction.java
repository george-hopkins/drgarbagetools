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

package com.drgarbage.sourcecodevisualizer.actions;

import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.RetargetAction;


import com.drgarbage.core.img.CoreImg;
import com.drgarbage.sourcecodevisualizer.SourcecodeVisualizerMessages;
import com.drgarbage.sourcecodevisualizer.editors.JavaCodeEditor;

/**
 * Filter Action to make method graphs visible or not. 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class MethodGraphFilterAction extends RetargetAction {

	private JavaCodeEditor editor;
	
	public static String ID = "com.drgarbage.sourcecodevisualizer.actions.MEthodGraphFilter";
	private static String text = SourcecodeVisualizerMessages.Sourcecodevisualizer_MethodGraphFilterAction_Text;
	private static String toolTipText = SourcecodeVisualizerMessages.Sourcecodevisualizer_MethodGraphFilterAction_TooltipText;

	
	public MethodGraphFilterAction(JavaCodeEditor editor) {
		super(ID, text);
		this.editor = editor;
		setToolTipText(toolTipText);
		setImageDescriptor(CoreImg.hideMethodAction_16x16);
		this.setEnabled(true);		
	}
	
    /**
     * Invoked when an action occurs. 
     */
    public void run() {
    	if(editor != null){
    		editor.getCanvasControlFlowGraph().setVisibleGraphsRepresentingMethods(!isChecked());
    	}
    }

	public void runWithEvent(Event event) {
		run();	
	}
	
	/**
	 * Radio action.
	 */
	public int getStyle() {
		return AS_CHECK_BOX;
	}

}
