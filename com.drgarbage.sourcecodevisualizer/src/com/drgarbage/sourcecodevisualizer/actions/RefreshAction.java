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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.RetargetAction;


import com.drgarbage.core.img.CoreImg;
import com.drgarbage.sourcecodevisualizer.SourcecodeVisualizerMessages;
import com.drgarbage.sourcecodevisualizer.editors.JavaCodeEditor;
import com.drgarbage.sourcecodevisualizer.img.SourcecodeVisualzerImg;

/**
 * Refresh action to fire the manual update of the graph. 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: RefreshAction.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class RefreshAction extends RetargetAction {

	private JavaCodeEditor editor;
	
	public static String ID = "com.drgarbage.sourcecodevisualizer.actions.Refresh";
	private static String text = SourcecodeVisualizerMessages.Sourcecodevisualizer_RefreshAction_Text;
	private static String toolTipText = SourcecodeVisualizerMessages.Sourcecodevisualizer_RefreshAction_TooltipText;
	

	
	public RefreshAction(JavaCodeEditor editor) {
		super(ID, text);
		this.editor = editor;
		setToolTipText(toolTipText);
		setImageDescriptor(SourcecodeVisualzerImg.refreshAction_16x16);
		this.setEnabled(true);		
	}
	
    /**
     * Invoked when an action occurs. 
     */
    public void run() {
    	if(editor != null){
    		//editor.getCanvasControlFlowGraph().setVisibleGraphsRepresentingConstructors(!isChecked());
    		editor.refreshGraphPanel();
    	}
    }

	public void runWithEvent(Event event) {
		run();	
	}

}
