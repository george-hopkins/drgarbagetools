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


import org.eclipse.swt.widgets.Event;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.img.ControlFlowFactoryResource;
import com.drgarbage.utils.Messages;
import com.drgarbage.visualgraphic.commands.MaxWidthCommand;

/**
 * Action for resizing the vertices.
 *
 * @author Sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: MaxWidhtAction.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class MaxWidhtAction extends OrderAbstractAction {

	public static String ID = "com.drgarbage.controlflowgraphfactory.actions.MaxWidhtAction";

	public MaxWidhtAction() {
		super(ID, ControlFlowFactoryMessages.MaxWidhtAction_Text);
		setToolTipText(ControlFlowFactoryMessages.MaxWidhtAction_ToolTipText);	
		setImageDescriptor(ControlFlowFactoryResource.MaxWidhtAction_16x16);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.RetargetAction#runWithEvent(org.eclipse.swt.widgets.Event)
	 */
	public void runWithEvent(Event event) {
		run();	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.RetargetAction#run()
	 */
	public void run() {
		if(editor != null){			
			MaxWidthCommand cmd = new MaxWidthCommand(editor.getGraphicalViewer().getSelectedEditParts());
			editor.getControlFlowGraphEditorEditDomain().getCommandStack().execute(cmd);
		}
		else{
			Messages.error(ControlFlowFactoryMessages.ExecutionFailure);
		}
	}
	
}
