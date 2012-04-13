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

package com.drgarbage.bytecodevisualizer.sourcelookup;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.ui.sourcelookup.ISourceDisplay;

public class SourceDisplayAdapterFactory implements IAdapterFactory {
	private static Class<?>[] adapterList = new Class<?>[] {
		ISourceDisplay.class
	};
	
	private static ISourceDisplay fgStackFrameSourceDisplayAdapter = new JDIStackFrameSourceDisplayAdapter();

	public Object getAdapter(Object adaptableObject, Class adapterType) {

        if (adapterType.equals(ISourceDisplay.class)) {
        	if (adaptableObject instanceof IStackFrame) {
        		//org.eclipse.jdt.internal.debug.core.model.JDIStackFrame
        		return fgStackFrameSourceDisplayAdapter;
        	}
        }
        return null;
	}

	public Class<?>[] getAdapterList() {
		return adapterList;
	}

}
