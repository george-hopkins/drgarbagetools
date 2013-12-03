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

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.OpenTypeSelectionDialog;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.utils.Messages;

/**
 * The action to select the class to compare from the project via
 * a class selection dialog.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
@SuppressWarnings("restriction")
public class OpenClassFileAction extends AbstractCompareClassFileAction implements IObjectActionDelegate {

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        /* get the first element. */
        IJavaElement[] resources = getSelectedResources();

        /* select the second element from the input dialog */
        IJavaElement element2 = selectJavaElement();
        if (element2 == null) {
            return;
        }
        try {
            run(resources[0], element2);
        } catch (Exception e) {
        	IStatus status = BytecodeVisualizerPlugin.createErrorStatus(IOException.class.getName(), e);
    		BytecodeVisualizerPlugin.log(status);
			Messages.error(IOException.class.getName() +
					CoreMessages.ExceptionAdditionalMessage);
        }
    }

    private IJavaElement selectJavaElement() {

        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        
        FilteredItemsSelectionDialog dialog = 
        		new OpenTypeSelectionDialog(shell, 
        				false, 
        				new ProgressMonitorDialog(shell), 
        				SearchEngine.createWorkspaceScope(), 
        				IJavaSearchConstants.TYPE);
        dialog.setTitle(ClassFileMergeViewer.CLASS_FILE_MERGEVIEWER_TITLE);
        dialog.setMessage(BytecodeVisualizerMessages.Message_file_to_compare); 

        int resultCode = dialog.open();
        if (resultCode != IDialogConstants.OK_ID) {
            return null;
        }

        Object[] result = dialog.getResult();
        
        if (result != null ){
        	if(result.length != 0) {
        		IJavaElement je = (IJavaElement)result[0];
        		return je; 
        	}
        }

        return null;
    }
}