/**
 * Copyright (c) 2008-2013, Dr. Garbage Community
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

import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

import com.drgarbage.bytecode.jdi.dialogs.JDIExportFromJvmDialog;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerConstants;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.core.img.CoreImg;

/**
 * An action for reading classes from the JVM. 
 * 
 * @author Sergej Alekseev
 * @version $Revision$ $Id$
 */
public class GetClassesFromJVMAction extends DebugViewAction {

	public static final String ACTION_READ_CLASSES_FROM_JVM = "com.drgarbage.bytecodevisualizer.actions.Read_Classes_From_JVM";
	
	@SuppressWarnings("restriction")
	public GetClassesFromJVMAction(IDebugView debugView) {
		super(
				ACTION_READ_CLASSES_FROM_JVM, 
				BytecodeVisualizerMessages.Read_Classes_From_JVM_text, 
				debugView
				);

		setImageDescriptor(IDEWorkbenchPlugin.getIDEImageDescriptor("etool16/exportdir_wiz.gif"));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		new JDIExportFromJvmDialog().open();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#runWithEvent(org.eclipse.swt.widgets.Event)
	 */
	@Override
	public void runWithEvent(Event event) {
		run();
	} 
}