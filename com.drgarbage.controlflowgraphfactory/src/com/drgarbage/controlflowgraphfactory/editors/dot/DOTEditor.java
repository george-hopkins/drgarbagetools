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

package com.drgarbage.controlflowgraphfactory.editors.dot;

import org.eclipse.ui.editors.text.TextEditor;

/**
 * Simple DOT text editor with highlighting.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: DOTEditor.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class DOTEditor extends TextEditor {

	private ColorManager colorManager;

	public DOTEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new DOTConfiguration(colorManager));
		
		setDocumentProvider(new DOTDocumentProvider());
	}

	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
}
