/**
 * Copyright (c) 2008-2017, Dr. Garbage Community
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

package com.drgarbage.bytecodevisualizer;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.ui.IStartup;

import com.drgarbage.bytecodevisualizer.actions.DynamicPartsManager;
import com.drgarbage.bytecodevisualizer.sourcelookup.SourceDisplayAdapterFactory;

/**
 * The startup class overwrites some adapters of the debug view
 */
public class BytecodeVisualizerStartup implements IStartup {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		/* make sure the org.eclipse.debug.internal.ui.DebugUIPlugin gets loaded
		 * so that it registers its org.eclipse.core.runtime.adapters */
		org.eclipse.debug.internal.ui.DebugUIPlugin.getDefault();

		/* In the following, we want to overwrite one of the DebugUIPlugin's adapters */
		IAdapterManager manager= Platform.getAdapterManager();
		SourceDisplayAdapterFactory actionFactory = new SourceDisplayAdapterFactory();
		manager.registerAdapters(actionFactory, JDIStackFrame.class);

		new DynamicPartsManager();
	}

}
