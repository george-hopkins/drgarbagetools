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

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jdt.internal.ui.javaeditor.InternalClassFileEditorInput;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.utils.Messages;

/**
 * Action for opening the bytecode visualizer.
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class OpenWithBytecodeVisualizerAction implements IObjectActionDelegate {

	protected Object selection = null;
	protected IWorkbenchPage page = null;


	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
		page = targetPart.getSite().getWorkbenchWindow().getActivePage();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if(selection instanceof TreeSelection){
			TreeSelection treeSelection = (TreeSelection)selection;
			IClassFile classFile = (IClassFile)treeSelection.getFirstElement();		
			IClassFileEditorInput editorInput = new InternalClassFileEditorInput(classFile);
			try {
				page.openEditor(editorInput, BytecodeVisualizerPlugin.PLUGIN_ID + ".editor", true);
			} catch (PartInitException e) {
				e.printStackTrace(System.err); //FIXME: write to the log
				Messages.error("PartInitException. For more details see log trace.");//TODO: define constant
			}
		}
		else{
			Messages.error("TreeSelection-Interface is not compartible.");//TODO: define constant
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
