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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.WizardExportResourcesPage;
import org.eclipse.ui.internal.ide.DialogUtil;
import org.eclipse.ui.ide.dialogs.ResourceTreeAndListGroup;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.graph.GraphConstants;

/**
 * PAge for the Graph Export Wizard.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: ControlFlowGraphExportWizardPage.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
@SuppressWarnings("restriction")
public class ControlFlowGraphExportWizardPage extends
WizardExportResourcesPage implements Listener {

	/* widgets */
	private Combo destinationNameField;
	private Button destinationBrowseButton;
	protected Button overwriteExistingFilesCheckbox;
	protected Button createDirectoryStructureButton;
	protected Button createSelectionOnlyButton;
	protected Combo exportFormatCombo;
	protected Button exportVisualPropertyCheckbox;
	protected Button exportGeometryCheckbox;
	protected Button exportGeommentsCheckbox;

	/* dialog store id constants */
	private static final String STORE_DESTINATION_NAMES_ID = "ControlFlowGraphExportWizardPage1.STORE_DESTINATION_NAMES_ID"; //$NON-NLS-1$
	private static final String STORE_OVERWRITE_EXISTING_FILES_ID = "ControlFlowGraphExportWizardPage1.STORE_OVERWRITE_EXISTING_FILES_ID"; //$NON-NLS-1$
	private static final String STORE_CREATE_STRUCTURE_ID = "ControlFlowGraphExportWizardPage1.STORE_CREATE_STRUCTURE_ID"; //$NON-NLS-1$

	/* resource varaibles */
	private ResourceTreeAndListGroup resourceGroup2;
	private IStructuredSelection initialResourceSelection;

	/**
	 *	Create an instance of this class
	 */
	protected ControlFlowGraphExportWizardPage(String name,
			IStructuredSelection selection) {
		super(name, selection);
		this.initialResourceSelection = selection;
	}

	/**
	 * Create an instance of this class.
	 *
	 * @param selection the selection
	 */
	public ControlFlowGraphExportWizardPage(IStructuredSelection selection) {
		this("ControlFlowGraphWizardExportPage1", selection); //$NON-NLS-1$
		setTitle(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_title);
		setDescription(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_description);
	}

	/**
	 *	Add the passed value to self's destination widget's history
	 *
	 *	@param value java.lang.String
	 */
	protected void addDestinationItem(String value) {
		destinationNameField.add(value);
	}

	/** (non-Javadoc)
	 * Method declared on IDialogPage.
	 */
	public void createControl(Composite parent) {

		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL));
		composite.setFont(parent.getFont());

		createResourcesGroup2(composite);

		createDestinationGroup(composite);

		createOptionsGroup(composite);
		createGraphOptionsGroup(composite);

		restoreResourceSpecificationWidgetValues(); /* ie.- local */
		restoreWidgetValues(); /* ie.- subclass hook */


		if (this.initialResourceSelection != null) {
			setupBasedOnInitialSelections();
		}

		updateWidgetEnablements();
		setPageComplete(determinePageCompletion());
		setErrorMessage(null);	/* should not initially have error message */

		setControl(composite);
		giveFocusToDestination();
	}

	/**
	 *	Creates the options specification widgets.
	 *
	 *	@param parent org.eclipse.swt.widgets.Composite
	 */
	protected void createGraphOptionsGroup(Composite parent) {
		/* options group */
		Group optionsGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		optionsGroup.setLayout(layout);
		optionsGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL));
		optionsGroup.setText(ControlFlowFactoryMessages.ExportGraphAction_HeaderText);
		optionsGroup.setFont(parent.getFont());

		createGraphOptionsGroupButtons(optionsGroup, parent.getFont());

	}

	/**
	 * Creates the Graph option widgets.
	 * @param parent org.eclipse.swt.widgets.Composite
	 * @param font
	 */
	protected void createGraphOptionsGroupButtons(Group optionsGroup, Font font) {
		/* checkbox */
		exportFormatCombo = new Combo(optionsGroup, SWT.READ_ONLY);
	    exportFormatCombo.setItems(GraphConstants.SUPPORTED_EXPORT_FORMAT_LABELS);
	    exportFormatCombo.select(GraphConstants.EXPORT_FORMAT_DOT); /*set default DOT fromat */
		
		exportVisualPropertyCheckbox = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
		exportVisualPropertyCheckbox.setText(ControlFlowFactoryMessages.ExportGraphAction_checkBox1Text);
		exportVisualPropertyCheckbox.setFont(font);
		
		exportGeometryCheckbox = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
		exportGeometryCheckbox.setText(ControlFlowFactoryMessages.ExportGraphAction_checkBox2Text);
		exportGeometryCheckbox.setFont(font);
		
		exportGeommentsCheckbox = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
		exportGeommentsCheckbox.setText(ControlFlowFactoryMessages.ExportGraphAction_checkBox3Text);
		exportGeommentsCheckbox.setFont(font);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardExportResourcesPage#setupBasedOnInitialSelections()
	 */
	protected void setupBasedOnInitialSelections() {

		Iterator<?> it = initialResourceSelection.iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if(!(o instanceof IResource)){
				continue;
			}

			IResource currentResource = (IResource)o; 
			if (currentResource.getType() == IResource.FILE) {
				resourceGroup2.initialCheckListItem(currentResource);
			} else {
				resourceGroup2.initialCheckTreeItem(currentResource);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardExportResourcesPage#getSelectedResourcesIterator()
	 */
	protected Iterator<?> getSelectedResourcesIterator() {
		return resourceGroup2.getAllCheckedListItems().iterator();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardExportResourcesPage#getWhiteCheckedResources()
	 */
	protected List<?> getWhiteCheckedResources() {

		return resourceGroup2.getAllWhiteCheckedItems();
	}

    /**
     * Creates the checkbox tree and list for selecting resources.
     *
     * @param parent the parent control
     */
	protected final void createResourcesGroup2(Composite parent) {
		/*
		 * create the input element, which has the root resource
		 * as its only child
		 */
		List<IProject> input = new ArrayList<IProject>();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
		.getProjects();
		for (int i = 0; i < projects.length; i++) {
			if (projects[i].isOpen()) {
				input.add(projects[i]);
			}
		}

		this.resourceGroup2 = new ResourceTreeAndListGroup(parent, input,
				getResourceProvider(IResource.FOLDER | IResource.PROJECT),
				WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(),
				getResourceProvider(IResource.FILE), WorkbenchLabelProvider
				.getDecoratingWorkbenchLabelProvider(), SWT.NONE,
				DialogUtil.inRegularFontMode(parent));

		ICheckStateListener listener = new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				updateWidgetEnablements();
			}
		};

		this.resourceGroup2.addCheckStateListener(listener);
	}

	/**
	 * Creates a content provider.
	 * @param resourceType
	 * @return provider
	 */
	private ITreeContentProvider getResourceProvider(final int resourceType) {
		return new WorkbenchContentProvider() {
			public Object[] getChildren(Object o) {
				if (o instanceof IContainer) {
					IResource[] members = null;
					try {
						members = ((IContainer) o).members();
					} catch (CoreException e) {
						/* just return an empty set of children */
						return new Object[0];
					}

					/* filter out the desired resource types */
					List<IResource> results = new ArrayList<IResource>();
					for (int i = 0; i < members.length; i++) {
						//And the test bits with the resource types to see if they are what we want
						if ((members[i].getType() & resourceType) > 0) {

							if(resourceType == IResource.FILE){
								String ext = members[i].getFileExtension();
								if(ext != null && ext.endsWith("graph")){
									results.add(members[i]);
								}
							}
							else{
								results.add(members[i]);
							}
						}
					}
					return results.toArray();
				} 
				/* input element case */
				if (o instanceof List) {
					return ((List<?>) o).toArray();
				} 
				return new Object[0];
			}
		};
	}



	/**
	 *	Create the export destination specification widgets
	 *
	 *	@param parent org.eclipse.swt.widgets.Composite
	 */
	protected void createDestinationGroup(Composite parent) {

		Font font = parent.getFont();
		/* destination specification group */
		Composite destinationSelectionGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		destinationSelectionGroup.setLayout(layout);
		destinationSelectionGroup.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		destinationSelectionGroup.setFont(font);

		Label destinationLabel = new Label(destinationSelectionGroup, SWT.NONE);
		destinationLabel.setText(getDestinationLabel());
		destinationLabel.setFont(font);

		/* destination name entry field */
		destinationNameField = new Combo(destinationSelectionGroup, SWT.SINGLE
				| SWT.BORDER);
		destinationNameField.addListener(SWT.Modify, this);
		destinationNameField.addListener(SWT.Selection, this);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		destinationNameField.setLayoutData(data);
		destinationNameField.setFont(font);

		/* destination browse button */
		destinationBrowseButton = new Button(destinationSelectionGroup,
				SWT.PUSH);
		destinationBrowseButton.setText(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_act_Browse);
		destinationBrowseButton.addListener(SWT.Selection, this);
		destinationBrowseButton.setFont(font);
		setButtonLayoutData(destinationBrowseButton);

		new Label(parent, SWT.NONE); /* vertical spacer */
	}

	/**
	 * Create the buttons in the options group.
	 */

	protected void createOptionsGroupButtons(Group optionsGroup) {

		Font font = optionsGroup.getFont();
		createOverwriteExisting(optionsGroup, font);

		createDirectoryStructureOptions(optionsGroup, font);
	}

	/**
	 * Create the buttons for the group that determine if the entire or
	 * selected directory structure should be created.
	 * @param optionsGroup
	 * @param font
	 */
	protected void createDirectoryStructureOptions(Composite optionsGroup, Font font) {
		/* create directory structure radios */
		createDirectoryStructureButton = new Button(optionsGroup, SWT.RADIO
				| SWT.LEFT);
		createDirectoryStructureButton.setText(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_chk_Replicate_the_Input_Directory_Structure);
		createDirectoryStructureButton.setSelection(false);
		createDirectoryStructureButton.setFont(font);

		/* create directory structure radios */
		createSelectionOnlyButton = new Button(optionsGroup, SWT.RADIO
				| SWT.LEFT);
		createSelectionOnlyButton.setText(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_chk_Create_only_selected_directories);
		createSelectionOnlyButton.setSelection(true);
		createSelectionOnlyButton.setFont(font);
	}

	/**
	 * Create the button for checking if we should ask if we are going to
	 * overwrite existing files.
	 * @param optionsGroup
	 * @param font
	 */
	protected void createOverwriteExisting(Group optionsGroup, Font font) {
		/* overwrite... checkbox */
		overwriteExistingFilesCheckbox = new Button(optionsGroup, SWT.CHECK
				| SWT.LEFT);
		overwriteExistingFilesCheckbox.setText(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_chk_Overwrite_existing_files_without_warning);
		overwriteExistingFilesCheckbox.setFont(font);
	}

	/**
	 * Attempts to ensure that the specified directory exists on the local file system.
	 * Answers a boolean indicating success.
	 *
	 * @return boolean
	 * @param directory java.io.File
	 */
	protected boolean ensureDirectoryExists(File directory) {
		if (!directory.exists()) {
			if (!queryYesNoQuestion(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_msg_Output_Directory_does_not_exist)) {
				return false;
			}

			if (!directory.mkdirs()) {
				displayErrorDialog(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_err_Output_Directory_could_not_be_created);
				giveFocusToDestination();
				return false;
			}
		}

		return true;
	}

	/**
	 *	If the target for export does not exist then attempt to create it.
	 *	Answer a boolean indicating whether the target exists (ie.- if it
	 *	either pre-existed or this method was able to create it)
	 *
	 *	@return boolean
	 */
	protected boolean ensureTargetIsValid(File targetDirectory) {
		if (targetDirectory.exists() && !targetDirectory.isDirectory()) {
			displayErrorDialog(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_err_Output_Directory_already_exists_as_a_file);
			giveFocusToDestination();
			return false;
		}

		return ensureDirectoryExists(targetDirectory);
	}

	/**
	 *  Set up and execute the passed Operation.  Answer a boolean indicating success.
	 *
	 *  @return boolean
	 */
	protected boolean executeExportOperation(ControlFlowGraphExportOperation op) {
		op.setCreateLeadupStructure(createDirectoryStructureButton
				.getSelection());
		op.setOverwriteFiles(overwriteExistingFilesCheckbox.getSelection());

		/*set export graph options */
		op.setExportFormat(this.exportFormatCombo.getSelectionIndex());
		op.setExportVisualProperty(this.exportVisualPropertyCheckbox.getSelection());
		op.setExportGeometry(this.exportGeometryCheckbox.getSelection());
		op.setExportComments(this.exportGeommentsCheckbox.getSelection());
		

		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			displayErrorDialog(e.getTargetException());
			return false;
		}

		IStatus status = op.getStatus();
		if (!status.isOK()) {
			ErrorDialog.openError(getContainer().getShell(),
					ControlFlowFactoryMessages.ControlFlowGraphExportWizard_err_Export_Problems,
					null, /* no special message */
					status);
			return false;
		}

		return true;
	}

	/**
	 *	The Finish button was pressed.  Try to do the required work now and answer
	 *	a boolean indicating success.  If false is returned then the wizard will
	 *	not close.
	 *
	 *	@return boolean
	 */
	@SuppressWarnings("unchecked")
	public boolean finish() {
		List<IResource> resourcesToExport = (List<IResource>) getWhiteCheckedResources();
		if (!ensureTargetIsValid(new File(getDestinationValue()))) {
			return false;
		}


		/* Save dirty editors if possible but do not stop if not all are saved */
		saveDirtyEditors();
		/* about to invoke the operation so save our state */
		saveWidgetValues();

		return executeExportOperation(new ControlFlowGraphExportOperation(null,
				resourcesToExport, getDestinationValue(), this));
	}

	/**
	 *	Answer the string to display in self as the destination type
	 *
	 *	@return java.lang.String
	 */
	protected String getDestinationLabel() {
		return ControlFlowFactoryMessages.ControlFlowGraphExportWizard_lbl_Output_Directory;
	}

	/**
	 *	Answer the contents of self's destination specification widget
	 *
	 *	@return java.lang.String
	 */
	protected String getDestinationValue() {
		return destinationNameField.getText().trim();
	}

	/**
	 *	Set the current input focus to self's destination entry field
	 */
	protected void giveFocusToDestination() {
		destinationNameField.setFocus();
	}

	/**
	 *	Open an appropriate destination browser so that the user can specify a source
	 *	to import from
	 */
	protected void handleDestinationBrowseButtonPressed() {
		DirectoryDialog dialog = new DirectoryDialog(getContainer().getShell(),
				SWT.SAVE);
		dialog.setMessage(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_msg_Select_Output_Directory);
		dialog.setText(ControlFlowFactoryMessages.ControlFlowGraphExportWizardFileChooser_title);
		dialog.setFilterPath(getDestinationValue());
		String selectedDirectoryName = dialog.open();

		if (selectedDirectoryName != null) {
			setErrorMessage(null);
			setDestinationValue(selectedDirectoryName);
		}
	}

	/**
	 * Handle all events and enablements for widgets in this page
	 * @param e Event
	 */
	public void handleEvent(Event e) {
		Widget source = e.widget;

		if (source == destinationBrowseButton) {
			handleDestinationBrowseButtonPressed();
		}

		updatePageCompletion();
	}

	/**
	 *	Hook method for saving widget values for restoration by the next instance
	 *	of this class.
	 */
	protected void internalSaveWidgetValues() {
		/* update directory names history */
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			String[] directoryNames = settings
			.getArray(STORE_DESTINATION_NAMES_ID);
			if (directoryNames == null) {
				directoryNames = new String[0];
			}

			directoryNames = addToHistory(directoryNames, getDestinationValue());
			settings.put(STORE_DESTINATION_NAMES_ID, directoryNames);

			/* options */
			settings.put(STORE_OVERWRITE_EXISTING_FILES_ID,
					overwriteExistingFilesCheckbox.getSelection());

			settings.put(STORE_CREATE_STRUCTURE_ID,
					createDirectoryStructureButton.getSelection());

		}
	}

	/**
	 *	Hook method for restoring widget values to the values that they held
	 *	last time this wizard was used to completion.
	 */
	protected void restoreWidgetValues() {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			String[] directoryNames = settings
			.getArray(STORE_DESTINATION_NAMES_ID);
			if (directoryNames == null) {
				return; /* ie.- no settings stored */
			}

			/* destination */
			setDestinationValue(directoryNames[0]);
			for (int i = 0; i < directoryNames.length; i++) {
				addDestinationItem(directoryNames[i]);
			}

			/* options */
			overwriteExistingFilesCheckbox.setSelection(settings
					.getBoolean(STORE_OVERWRITE_EXISTING_FILES_ID));

			boolean createDirectories = settings
			.getBoolean(STORE_CREATE_STRUCTURE_ID);
			createDirectoryStructureButton.setSelection(createDirectories);
			createSelectionOnlyButton.setSelection(!createDirectories);
		}
	}

	/**
	 *	Set the contents of the receivers destination specification widget to
	 *	the passed value
	 *
	 */
	protected void setDestinationValue(String value) {
		destinationNameField.setText(value);
	}

	/**
	 *	Answer a boolean indicating whether the receivers destination specification
	 *	widgets currently all contain valid values.
	 */
	protected boolean validateDestinationGroup() {
		String destinationValue = getDestinationValue();
		if (destinationValue.length() == 0) {
			setMessage(destinationEmptyMessage());
			return false;
		}

		String conflictingContainer = getConflictingContainerNameFor(destinationValue);
		if (conflictingContainer == null) {
			/* no error message, but warning may exists */
			String threatenedContainer = getOverlappingProjectName(destinationValue);
			if(threatenedContainer == null)
				setMessage(null);
			else
				setMessage(
						NLS.bind(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_warn_damageWarning, threatenedContainer),
						WARNING);

		} else {
			setErrorMessage(NLS.bind(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_err_ConflictingContainer, conflictingContainer));
			giveFocusToDestination();
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardDataTransferPage#validateSourceGroup()
	 */
	protected boolean validateSourceGroup() {
		// there must be some resources selected for Export
		boolean isValid = true;
		List<?> resourcesToExport = getWhiteCheckedResources();
		if (resourcesToExport.size() == 0){
			setErrorMessage(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_err_empty_selection);
			isValid =  false;
		} else {
			setErrorMessage(null);
		}
		return super.validateSourceGroup() && isValid;
	}

	/**
	 * Get the message used to denote an empty destination.
	 */
	protected String destinationEmptyMessage() {
		return ControlFlowFactoryMessages.ControlFlowGraphExportWizard_err_Select_Output_Directory;
	}

	/**
	 * Returns the name of a container with a location that encompasses targetDirectory.
	 * Returns null if there is no conflict.
	 * 
	 * @param targetDirectory the path of the directory to check.
	 * @return the conflicting container name or <code>null</code>
	 */
	protected String getConflictingContainerNameFor(String targetDirectory) {

		IPath rootPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		IPath testPath = new Path(targetDirectory);
		/* cannot export into workspace root */
		if(testPath.equals(rootPath))
			return rootPath.lastSegment();

		/* Are they the same? */
		if(testPath.matchingFirstSegments(rootPath) == rootPath.segmentCount()){
			String firstSegment = testPath.removeFirstSegments(rootPath.segmentCount()).segment(0);
			if(!Character.isLetterOrDigit(firstSegment.charAt(0)))
				return firstSegment;
		}

		return null;

	}

	/**
	 * Returns the name of a {@link IProject} with a location that includes
	 * targetDirectory. Returns null if there is no such {@link IProject}.
	 * 
	 * @param targetDirectory
	 *            the path of the directory to check.
	 * @return the overlapping project name or <code>null</code>
	 */
	private String getOverlappingProjectName(String targetDirectory){
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IPath testPath = new Path(targetDirectory);
		IContainer[] containers = root.findContainersForLocation(testPath);
		if(containers.length > 0){
			return containers[0].getProject().getName();
		}
		return null;
	}



}
