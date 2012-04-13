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

package com.drgarbage.controlflowgraphfactory.wizards;


import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryPlugin;

/**
 * GraphXML Export Wizard.
 * 
 * @author Sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: ControlFlowGraphExportWizard.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
@SuppressWarnings("restriction")
public class ControlFlowGraphExportWizard extends Wizard implements IExportWizard {
	private IStructuredSelection selection;

	private ControlFlowGraphExportWizardPage mainPage;

	/**
	 * Creates a wizard for exporting workspace resources to the local file system.
	 */
	public ControlFlowGraphExportWizard() {
		IDialogSettings workbenchSettings = ControlFlowFactoryPlugin.getDefault().getDialogSettings();
		IDialogSettings section = workbenchSettings.getSection("ControlFlowGraphFactoryExportWizard");//$NON-NLS-1$
		if (section == null) {
			section = workbenchSettings.addNewSection("ControlFlowGraphFactoryExportWizard");//$NON-NLS-1$
		}
		setDialogSettings(section);
	}

	/* (non-Javadoc)
	 * Method declared on IWizard.
	 */
	public void addPages() {
		super.addPages();
		mainPage = new ControlFlowGraphExportWizardPage(selection);
		addPage(mainPage);
	}


	/* (non-Javadoc)
	 * Method declared on IWorkbenchWizard.
	 */
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		this.selection = currentSelection;
		List<?> selectedResources = IDE.computeSelectedResources(currentSelection);
		if (!selectedResources.isEmpty()) {
			this.selection = new StructuredSelection(selectedResources);
		}

		/* look it up if current selection (after resource adapting) is empty */
		if (selection.isEmpty() && workbench.getActiveWorkbenchWindow() != null) {
			IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
			.getActivePage();
			if (page != null) {
				IEditorPart currentEditor = page.getActiveEditor();
				if (currentEditor != null) {
					Object selectedResource = currentEditor.getEditorInput()
					.getAdapter(IResource.class);
					if (selectedResource != null) {
						selection = new StructuredSelection(selectedResource);
					}
				}
			}
		}

		setWindowTitle(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_title);
		setDefaultPageImageDescriptor(IDEWorkbenchPlugin.getIDEImageDescriptor("wizban/exportdir_wiz.png"));//$NON-NLS-1$
		setNeedsProgressMonitor(true);
	}

	/* (non-Javadoc)
	 * Method declared on IWizard.
	 */
	public boolean performFinish() {
		return mainPage.finish();
	}
}