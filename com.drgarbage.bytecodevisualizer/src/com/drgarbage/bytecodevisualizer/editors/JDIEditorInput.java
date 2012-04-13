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

package com.drgarbage.bytecodevisualizer.editors;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class JDIEditorInput implements IEditorInput, IClassFileEditorInput {
	
	private IEditorInput delegate;
	private JDIStackFrame stackFrame;

	public JDIEditorInput(JDIStackFrame stackFrame, IEditorInput delegate) {
		super();
		if (!(delegate instanceof IClassFileEditorInput)) {
			throw new RuntimeException("delegate must be a "+ IClassFileEditorInput.class.getName());
		}
		this.stackFrame = stackFrame;
		this.delegate = delegate;
	}


	public boolean exists() {
		return delegate.exists();
	}


	public Object getAdapter(Class adapter) {
		return delegate.getAdapter(adapter);
	}


	public IClassFile getClassFile() {
		if (delegate instanceof IClassFileEditorInput) {
			return ((IClassFileEditorInput) delegate).getClassFile();
		}
		else {
			throw new RuntimeException("delegate must be a "+ IClassFileEditorInput.class.getName());
		}
	}

	public IEditorInput getDelegate() {
		return delegate;
	}

	public ImageDescriptor getImageDescriptor() {
		return delegate.getImageDescriptor();
	}

	public String getName() {
		return delegate.getName();
	}

	public IPersistableElement getPersistable() {
		return delegate.getPersistable();
	}

	public JDIStackFrame getStackFrame() {
		return stackFrame;
	}

	public String getToolTipText() {
		return delegate.getToolTipText();
	}

}
