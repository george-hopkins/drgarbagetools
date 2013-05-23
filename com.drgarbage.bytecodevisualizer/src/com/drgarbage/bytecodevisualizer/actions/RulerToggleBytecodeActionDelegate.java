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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.AbstractRulerActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Toggles a breakpoint when ruler is double-clicked. This action is as a 
 * factory that creates another action that performs the actual breakpoint 
 * toggling. The created action acts on the editor's
 * <code>IToggleBreakpointsTagret</code> to toggle breakpoints.
 * <p>
 * Instead, the label of the created action is displayed. 
 * <pre>
 * &lt;extension point="org.eclipse.ui.editorActions"&gt;
 *    &lt;editorContribution
 *          targetID="example.editor"
 *          id="example.rulerActions"&gt;
 *       &lt;action
 *             label="Not Used"
 *             class="org.eclipse.debug.ui.actions.RulerToggleBreakpointActionDelegate"
 *             style="push"
 *             actionID="RulerDoubleClick"
 *             id="example.doubleClickBreakpointAction"/&gt;
 *    &lt;/editorContribution&gt;
 * &lt;/extension&gt;
 * </pre>
 * </p>
 * <p>
 * This action can also be contributed to a vertical ruler context menu via the
 * <code>popupMenus</code> extension point, by referencing the ruler's context
 * menu identifier in the <code>targetID</code> attribute.
 * <pre>
 * &lt;extension point="org.eclipse.ui.popupMenus"&gt;
 *   &lt;viewerContribution
 *     targetID="example.rulerContextMenuId"
 *     id="example.RulerPopupActions"&gt;
 *       &lt;action
 *         label="Toggle Breakpoint"
 *         class="org.eclipse.debug.ui.actions.RulerToggleBreakpointActionDelegate"
 *         menubarPath="additions"
 *         id="example.rulerContextMenu.toggleBreakpointAction"&gt;
 *       &lt;/action&gt;
 *   &lt;/viewerContribution&gt;
 * </pre>
 * </p>
 * <p>
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class RulerToggleBytecodeActionDelegate extends
		AbstractRulerActionDelegate {

	private IEditorPart fEditor = null;
	private ToggleBytecodeBreakpointAction fDelegate = null;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractRulerActionDelegate#createAction(org.eclipse.ui.texteditor.ITextEditor, org.eclipse.jface.text.source.IVerticalRulerInfo)
	 */
	protected IAction createAction(ITextEditor editor, IVerticalRulerInfo rulerInfo) {
		fDelegate = new ToggleBytecodeBreakpointAction(editor, null, rulerInfo);
		return fDelegate;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction, org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IAction callerAction, IEditorPart targetEditor) {
		if (fEditor != null) {
			if (fDelegate != null) {
				fDelegate.dispose();
				fDelegate = null;
			}
		}
		fEditor = targetEditor;
		super.setActiveEditor(callerAction, targetEditor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate2#dispose()
	 */
	public void dispose() {
		if (fDelegate != null) {
			fDelegate.dispose();
		}
		fDelegate = null;
		fEditor = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate2#runWithEvent(org.eclipse.jface.action.IAction, org.eclipse.swt.widgets.Event)
	 */
	public void runWithEvent(IAction action, Event event) {
		run(action);
	}
}
