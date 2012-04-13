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

import org.eclipse.debug.ui.IDebugView;
import org.eclipse.swt.widgets.Event;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerConstants;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.core.CoreConstants;

public class StepIntoBytecodeAction extends DebugViewAction {

	public StepIntoBytecodeAction(IDebugView debugView) {
		super(
				CoreConstants.ACTION_STEP_INTO_BYTECODE, 
				BytecodeVisualizerMessages.StepIntoBytecodeAction_text, 
				debugView
				);

		setImageDescriptor(BytecodeVisualizerPlugin.getDefault().getImageRegistry().getDescriptor(BytecodeVisualizerConstants.IMG16E_STEP_INTO_BYTECODE));
	}

	@Override
	public void run() {
		//TODO: Activate byte code view
		BytecodeVisualizerPlugin.getDefault().getD().stepInto();
	}

	@Override
	public void runWithEvent(Event event) {
		run();
	} 
}