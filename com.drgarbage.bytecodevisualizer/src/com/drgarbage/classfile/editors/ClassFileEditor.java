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

package com.drgarbage.classfile.editors;

import org.eclipse.ui.editors.text.TextEditor;

/**
 * The class file editor.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class ClassFileEditor extends TextEditor {

	private ColorManager colorManager;

	public ClassFileEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new ClassFileConfiguration(colorManager));
		setDocumentProvider(new ClassFileDocumentProvider());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextEditor#dispose()
	 */
	public void dispose() {
		colorManager.dispose();
		super.dispose();

	}
}
