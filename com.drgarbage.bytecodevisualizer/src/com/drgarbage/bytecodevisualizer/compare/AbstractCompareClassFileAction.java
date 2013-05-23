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

package com.drgarbage.bytecodevisualizer.compare;


import java.util.ArrayList;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * The abstract action to implement action for comparing class files.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public abstract class AbstractCompareClassFileAction implements IObjectActionDelegate {
	protected IStructuredSelection selection;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection newSelection) {
		if (newSelection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) newSelection;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		/* nothing to do */
	}

	/**
	 * Opens a compare editor.
	 * @param element1 The first element that has to be compared.
	 * @param element2 the second element that has to be compared.
	 * @throws Exception
	 */
	protected void run(IJavaElement element1, IJavaElement element2) throws Exception {
		CompareUI.openCompareEditor(new ClassFileCompareInput(
				new CompareElement(element1),
				new CompareElement(element2), 
				new CompareConfiguration())
		);
	}

	/**
	 * Returns an array of selected resources.
	 * @return array of selected resources
	 */
	protected IJavaElement[] getSelectedResources() {
		ArrayList<Object> resources = null;
		if (!selection.isEmpty()) {
			resources = new ArrayList<Object>();

			Object[] elements = selection.toArray();
			for(Object o: elements){
				if (o instanceof IFile) {
					resources.add(JavaCore.create((IFile)o));
				} 
				else if (o instanceof IJavaElement) {
					resources.add(o);
				} 
				else if (o instanceof IAdaptable) {
					IAdaptable a = (IAdaptable) o;
					Object adapter = a.getAdapter(IFile.class);
					if (adapter instanceof IFile) {
						resources.add(JavaCore.create((IFile)adapter));
					}
					else {
						adapter = a.getAdapter(ICompilationUnit.class);
						if (adapter instanceof ICompilationUnit) {
							resources.add(adapter);
						}
						else{
							adapter = a.getAdapter(IClassFile.class);
							if (adapter instanceof IClassFile) {
								resources.add(adapter);
								
							}
						}
					}
				}
			}
		}

		if (resources != null && !resources.isEmpty()) {
			return resources.toArray(new IJavaElement[resources.size()]);
		}

		return new IJavaElement[0];
	}
}
