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
package com.drgarbage.core.jface;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * A Message box with a list of classes. The selected class 
 * is returned as a string. 
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class SelectDialog extends MessageDialog {
	
	/**
	 * The text selected by the user.
	 */
	private String selectedText = null;
	
	/* custom widget */
	private TableViewer listViewer;
	private Image sListImage;

	/**
     * Create a message dialog. Note that the dialog will have no visual
     * representation (no widgets) until it is told to open.
     * <p>
     * The labels of the buttons to appear in the button bar are supplied in
     * this constructor as an array. The <code>open</code> method will return
     * the index of the label in this array corresponding to the button that was
     * pressed to close the dialog.
     * </p>
     * <p>
     * <strong>Note:</strong> If the dialog was dismissed without pressing
     * a button (ESC key, close box, etc.) then {@link SWT#DEFAULT} is returned.
     * Note that the <code>open</code> method blocks.
     * </p>
     *
     * @param parentShell
     *            the parent shell
     * @param dialogTitle
     *            the dialog title, or <code>null</code> if none
     * @param dialogTitleImage
     *            the dialog title image, or <code>null</code> if none
     * @param dialogMessage
     *            the dialog message
     * @param dialogImageType
     *            one of the following values:
     *            <ul>
     *            <li><code>MessageDialog.NONE</code> for a dialog with no
     *            image</li>
     *            <li><code>MessageDialog.ERROR</code> for a dialog with an
     *            error image</li>
     *            <li><code>MessageDialog.INFORMATION</code> for a dialog
     *            with an information image</li>
     *            <li><code>MessageDialog.QUESTION </code> for a dialog with a
     *            question image</li>
     *            <li><code>MessageDialog.WARNING</code> for a dialog with a
     *            warning image</li>
     *            </ul>
     * @param dialogButtonLabels
     *            an array of labels for the buttons in the button bar
     * @param defaultIndex
     *            the index in the button label array of the default button
     * @param selectionListImage
     *            the selection list image, or <code>null</code> if none
     */
	public SelectDialog(Shell parentShell, String dialogTitle,
			Image dialogTitleImage, String dialogMessage,
			int dialogImageType, String[] dialogButtonLabels,
			int defaultIndex,
			Image selectionListImage) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage,
				dialogImageType, dialogButtonLabels, defaultIndex);
		sListImage = selectionListImage;
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createCustomArea(Composite parent) {
    	listViewer = new TableViewer(parent);
    	listViewer.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	listViewer.setContentProvider(new ArrayContentProvider());
    	listViewer.setLabelProvider(new LabelProvider() {

    		/* (non-Javadoc)
    		 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
    		 */
			public Image getImage(Object element) {
    			return sListImage;
    		}

    		/* (non-Javadoc)
    		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
    		 */
    		public String getText(Object element) {
    			return element.toString();
    		}
    	});
    	
    	return listViewer.getControl();
    }
	
	/**
	 * Adds elements have to be displayed in the list
	 * for selection.
	 * @param classList the list of elements
	 */
	public void addElementsToList(java.util.List<String> classList) {
		listViewer.setInput(classList);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#close()
	 */
	public boolean close() {
		selectedText = null;

		if(getReturnCode() != CANCEL){
			IStructuredSelection selection = (IStructuredSelection)listViewer.getSelection();
			if(selection != null){
				Object o = selection.getFirstElement();
				if(o != null){
					selectedText = o.toString();
				}
			}
		}

		return super.close();
	}
	
	
    /**
     * Returns the text selected by the user.
	 * @return the selected text
	 */
	public String getSelectedText() {
		return selectedText;
	}
}