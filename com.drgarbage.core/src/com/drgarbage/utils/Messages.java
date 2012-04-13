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

package com.drgarbage.utils;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.drgarbage.core.CoreMessages;
import com.drgarbage.core.img.CoreImg;

/**
 * Message Boxes.
 * 
 * @author Sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: Messages.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class Messages {

	/* Info Dialogs */
	public static int info(String message){
		return info(CoreMessages.MessageDialogInfo, message);
	}
	
	public static int info(String title, String message){
		return info(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				title,
				message);
	}

	public static int info(Shell shell, String title, String message){
		MessageDialog dlg = new MessageDialog(shell, title, 
				CoreImg.aboutDrGarbageIcon_16x16.createImage(), 
				message, MessageDialog.INFORMATION, 
				new String[] { IDialogConstants.OK_LABEL }, 0);
		return dlg.open();
	}
		
	/* Warning Dialogs */
	public static int warning(String message){
		return warning(CoreMessages.MessageDialogWarning, message);
	}

	public static int warning(String title, String message){	
		return warning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				title,
				message);
	}
	
	public static int warning(Shell shell, String title, String message){		
		MessageDialog dlg = new MessageDialog(shell, title, 
				CoreImg.aboutDrGarbageIcon_16x16.createImage(), 
				message, MessageDialog.WARNING, 
				new String[] { IDialogConstants.OK_LABEL }, 0);
		return dlg.open();
	}

	/* Error Dialogs */
	public static int error(String message){
		return error(CoreMessages.MessageDialogError, message);
	}

	public static int error(String text, String message){
		return error(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				text,
				message);
	}

	public static int error(Shell shell, String title, String message){		
		MessageDialog dlg = new MessageDialog(shell, title, 
				CoreImg.aboutDrGarbageIcon_16x16.createImage(), 
				message, MessageDialog.ERROR, 
				new String[] { IDialogConstants.OK_LABEL }, 0);
		return dlg.open();
	}
	
    /**
     * Convenience method to open a simple confirm (OK/Cancel) dialog.
     * 
     * @param parent
     *            the parent shell of the dialog, or <code>null</code> if none
     * @param title
     *            the dialog's title, or <code>null</code> if none
     * @param message
     *            the message
     * @return <code>true</code> if the user presses the OK button,
     *         <code>false</code> otherwise
     */
    public static boolean openConfirm(Shell parent, String title, String message, String[] buttons) {
        MessageDialog dialog = new MessageDialog(parent, title, 
        		CoreImg.aboutDrGarbageIcon_16x16.createImage(),
                message, MessageDialog.QUESTION, buttons , 0); /* OK is the default */
        return dialog.open() == 0;
    }

    public static boolean openConfirm(Shell parent, String title, String message) {
    	return openConfirm(parent, title, message, new String[] { IDialogConstants.OK_LABEL,
                IDialogConstants.CANCEL_LABEL});
    }
    
    public static boolean openConfirm(String title, String message) {
    	return openConfirm(null, title, message);
    }
 
    public static boolean openConfirm(String message) {
    	return openConfirm(null, CoreMessages.MessageDialogQuestion, message);
    }
}
