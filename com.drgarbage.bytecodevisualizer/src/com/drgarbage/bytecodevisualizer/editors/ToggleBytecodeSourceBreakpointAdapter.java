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

import org.eclipse.jdt.internal.debug.ui.actions.ToggleBreakpointAdapter;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Toggles a breakpoint in a source code viewer of the bytecode editor.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: ToggleBytecodeSourceBreakpointAdapter.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
@SuppressWarnings("restriction")
public class ToggleBytecodeSourceBreakpointAdapter extends ToggleBreakpointAdapter {

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.debug.ui.actions.ToggleBreakpointAdapter#getTextEditor(org.eclipse.ui.IWorkbenchPart)
     */
    protected ITextEditor getTextEditor(IWorkbenchPart part) {
    	if (part instanceof BytecodeEditor) {
    		
    		BytecodeEditor editor = (BytecodeEditor) part;
    		
    		return (ITextEditor)editor.getSourceCodeViewer();
    	}
    	return (ITextEditor) part.getAdapter(ITextEditor.class);
    }
}
