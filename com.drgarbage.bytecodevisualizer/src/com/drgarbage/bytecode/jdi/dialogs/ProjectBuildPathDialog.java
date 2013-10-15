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

package com.drgarbage.bytecode.jdi.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.CPListElement;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.NewFolderDialog;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;

/**
 * A dialog for selecting the export class folder and editing 
 * the build path class folders in the given java project.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
@SuppressWarnings("restriction")
public class ProjectBuildPathDialog extends TitleAreaDialog {
	
	/**
	 * The current java project. 
	 */
	private IJavaProject fJavaProject;
	
	/**
	 * The tree viewer to show the build path class folders.
	 */
	private ProjectBuildPathViewer fPathViewer;
	
	/**
	 * The selected class folder for class export.
	 */
	private IFolder selectedFolder = null;
	
	/**
	 * Returns the selected class folder for the class export or 
	 * <code>null</code>.
	 * @return selected class folder
	 */
	protected synchronized IFolder getSelectedFolder() {
		return selectedFolder;
	}

	/**
	 * Constructs a dialog to select the class export folder.
	 * 
	 * @param shell shell to parent the dialog
	 * @param javaProject java project root
	 */
	public ProjectBuildPathDialog(Shell shell, IJavaProject javaProject ) {
		super(shell);	
        setShellStyle(getShellStyle() | SWT.RESIZE);
		fJavaProject = javaProject;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		/* create a composite with standard margins and spacing */
		getShell().setText(BytecodeVisualizerMessages.Buildpath_Dialog_Abr);
		
		setTitle(BytecodeVisualizerMessages.Buildpath_Dialog_Title); 
		setMessage(BytecodeVisualizerMessages.Buildpath_Dialog_Message);
		setTitleImage(IDEWorkbenchPlugin.getIDEImageDescriptor("wizban/exportdir_wiz.png").createImage());
		
		Composite composite = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.marginHeight =
			convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth =
			convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing =
			convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing =
			convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);			
		composite.setLayoutData(data);
		composite.setFont(parent.getFont());
		
		/* create a tree viewer*/
		fPathViewer = new ProjectBuildPathViewer(composite, fJavaProject);
		fPathViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		fPathViewer.getControl().setFont(parent.getFont());
		fPathViewer.setInput(fJavaProject.getResource().getWorkspace().getRoot());
		fPathViewer.expandAll();
		
		/*create a new folder button */
		Button button = new Button(composite, SWT.PUSH);
		button.setText(NewWizardMessages.FolderSelectionDialog_button);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newFolderButtonPressed();
			}
		});
		button.setFont(parent.getFont());
		
		/*set help ID */
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, null);
		
		return composite;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		TreeSelection treeSel = (TreeSelection)fPathViewer.getSelection();
		Object o = treeSel.getFirstElement();
		if(o instanceof IFolder){
			selectedFolder = (IFolder)o;
		}
		
		super.okPressed();
	}
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#getDialogBoundsSettings()
     */
    protected IDialogSettings getDialogBoundsSettings() {
    	/* memorize dialog settings */
    	 IDialogSettings settings = DebugUIPlugin.getDefault().getDialogSettings();
         IDialogSettings section = settings.getSection(getClass().getName());
         if (section == null) {
             section = settings.addNewSection(getClass().getName());
         } 
         return section;
    }
	
	/**
	 * Open a dialog to create a new workspace folder.
	 * @see NewFolderDialog
	 */
	private void newFolderButtonPressed() {
		
		NewFolderDialog dialog= new NewFolderDialog(getShell(), (IContainer) fJavaProject.getProject());
		
		if (dialog.open() == Window.OK) {
			Object createdFolder= dialog.getResult()[0];

			try {
				IResource res = (IResource) createdFolder;
				CPListElement classPathElement = 
					new CPListElement(fJavaProject, IClasspathEntry.CPE_LIBRARY, res.getFullPath(), res);
				
				IClasspathEntry[] rawClassPath = fJavaProject.getRawClasspath();
				List<IClasspathEntry> classPathList = new ArrayList<IClasspathEntry>(rawClassPath.length);
				for(IClasspathEntry ice: rawClassPath){
					classPathList.add(ice);
				}

				/* add the new folder to the class path */
				classPathList.add(classPathElement.getClasspathEntry());

				fJavaProject.setRawClasspath( classPathList.toArray(new IClasspathEntry[0]), new NullProgressMonitor());
				
			} catch (JavaModelException e) {
				BytecodeVisualizerPlugin.getDefault().getLog().log(
						BytecodeVisualizerPlugin.createErrorStatus(e.getMessage(), e)
						);
			}
			
			/* refresh the input */
			fPathViewer.setInput(fJavaProject.getResource().getWorkspace().getRoot());
			fPathViewer.refresh();
		}
	}
	
	
}