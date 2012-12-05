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

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.widgets.Shell;

import com.drgarbage.core.img.CoreImg;

/**
 * A simple input dialog for soliciting an input string from the user.
 * <p>
 * This concrete dialog class can be instantiated as is, or further subclassed as
 * required.
 * </p>
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: InputDialog.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class InputDialog extends org.eclipse.jface.dialogs.InputDialog {

    /**
     * Creates an input dialog with OK and Cancel buttons. Note that the dialog
     * will have no visual representation (no widgets) until it is told to open.
     * <p>
     * Note that the <code>open</code> method blocks for input dialogs.
     * </p>
     * 
     * @param parentShell
     *            the parent shell, or <code>null</code> to create a top-level
     *            shell
     * @param dialogTitle
     *            the dialog title, or <code>null</code> if none
     * @param dialogMessage
     *            the dialog message, or <code>null</code> if none
     * @param initialValue
     *            the initial input value, or <code>null</code> if none
     *            (equivalent to the empty string)
     * @param validator
     *            an input validator, or <code>null</code> if none
     */
	public InputDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue, IInputValidator validator) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
	}

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.InputDialog#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setImage(CoreImg.aboutDrGarbageIcon_16x16.createImage());
    }
	
}