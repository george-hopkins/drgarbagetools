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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IOverwriteQuery;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryPlugin;
import com.drgarbage.controlflowgraphfactory.export.ExportException;
import com.drgarbage.graph.GraphConstants;
import com.drgarbage.io.FileExtensions;

/**
 * Export Operation. 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: ControlFlowGraphExportOperation.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class ControlFlowGraphExportOperation implements IRunnableWithProgress {
	private IPath path;

	private IProgressMonitor monitor;

	private ControlFlowGraphExporter exporter = new ControlFlowGraphExporter();

	private List<IResource> resourcesToExport;

	private IOverwriteQuery overwriteCallback;

	private IResource resource;

	private List<IStatus> errorTable = new ArrayList<IStatus>(1);

	/* The constants for the overwrite 3 state */
	private static final int OVERWRITE_NOT_SET = 0;

	private static final int OVERWRITE_NONE = 1;

	private static final int OVERWRITE_ALL = 2;

	private int overwriteState = OVERWRITE_NOT_SET;

	private boolean createLeadupStructure = true;

	private boolean createContainerDirectories = true;

	/* export graph options */
	private int exportFormat = GraphConstants.EXPORT_FORMAT_DOT;

	private boolean exportVisualProperty = false;
	private boolean exportGeometry = false;
	private boolean exportComments = false;

	/**
	 * @param exportFormat
	 */
	public void setExportFormat(int exportFormat) {
		this.exportFormat = exportFormat;
	}
	
	/**
	 * @param exportVisualProperty
	 */
	public void setExportVisualProperty(boolean exportVisualProperty) {
		this.exportVisualProperty = exportVisualProperty;
	}

	/**
	 * @param exportGeometry
	 */
	public void setExportGeometry(boolean exportGeometry) {
		this.exportGeometry = exportGeometry;
	}

	/**
	 * @param exportComments
	 */
	public void setExportComments(boolean exportComments) {
		this.exportComments = exportComments;
	}
	
	/**
	 *  Create an instance of this class.  Use this constructor if you wish to
	 *  recursively export a single resource
	 */
	public ControlFlowGraphExportOperation(IResource res, String destinationPath,
			IOverwriteQuery overwriteImplementor) {
		super();
		resource = res;
		path = new Path(destinationPath);
		overwriteCallback = overwriteImplementor;
	}

	/**
	 *  Create an instance of this class.  Use this constructor if you wish to
	 *  export specific resources with a common parent resource (affects container
	 *  directory creation)
	 */
	public ControlFlowGraphExportOperation(IResource res, List<IResource> resources,
			String destinationPath, IOverwriteQuery overwriteImplementor) {
		this(res, destinationPath, overwriteImplementor);
		resourcesToExport = resources;
	}

	/**
	 *  Answer the total number of file resources that exist at or below self in the
	 *  resources hierarchy.
	 *
	 *  @return int
	 *  @param parentResource org.eclipse.core.resources.IResource
	 */
	protected int countChildrenOf(IResource parentResource)
	throws CoreException {
		if (parentResource.getType() == IResource.FILE) {
			return 1;
		}

		int count = 0;
		if (parentResource.isAccessible()) {
			IResource[] children = ((IContainer) parentResource).members();
			for (int i = 0; i < children.length; i++) {
				count += countChildrenOf(children[i]);
			}
		}

		return count;
	}

	/**
	 *	Answer a boolean indicating the number of file resources that were
	 *	specified for export
	 *
	 *	@return int
	 */
	protected int countSelectedResources() throws CoreException {
		int result = 0;
		Iterator<IResource> resources = resourcesToExport.iterator();

		while (resources.hasNext()) {
			result += countChildrenOf((IResource) resources.next());
		}

		return result;
	}

	/**
	 *  Create the directories required for exporting the passed resource,
	 *  based upon its container hierarchy
	 *
	 *  @param childResource org.eclipse.core.resources.IResource
	 */
	protected void createLeadupDirectoriesFor(IResource childResource) {
		IPath resourcePath = childResource.getFullPath().removeLastSegments(1);

		for (int i = 0; i < resourcePath.segmentCount(); i++) {
			path = path.append(resourcePath.segment(i));
			exporter.createFolder(path);
		}
	}

	/**
	 *	Recursively export the previously-specified resource
	 */
	protected void exportAllResources() throws InterruptedException {
		if (resource.getType() == IResource.FILE) {
			exportFile((IFile) resource, path);
		} else {
			try {
				exportChildren(((IContainer) resource).members(), path);
			} catch (CoreException e) {
				/*
				 * not safe to show a dialog
				 * should never happen because the file system export wizard ensures that the
				 * single resource chosen for export is both existent and accessible
				 */
				errorTable.add(e.getStatus());
			}
		}
	}

	/**
	 *	Export all of the resources contained in the passed collection
	 *
	 *	@param children java.util.Enumeration
	 *	@param currentPath IPath
	 */
	protected void exportChildren(IResource[] children, IPath currentPath)
	throws InterruptedException {
		for (int i = 0; i < children.length; i++) {
			IResource child = children[i];
			if (!child.isAccessible()) {
				continue;
			}

			if (child.getType() == IResource.FILE) {
				exportFile((IFile) child, currentPath);
			} else {
				IPath destination = currentPath.append(child.getName());
				exporter.createFolder(destination);
				try {
					exportChildren(((IContainer) child).members(), destination);
				} catch (CoreException e) {
					/*
					 * not safe to show a dialog
					 * should never happen because:
				 	 * i. this method is called recursively iterating over the result of #members,
					 * which only answers existing children
					 * ii. there is an #isAccessible check done before #members is invoked
					 */
					errorTable.add(e.getStatus());
				}
			}
		}
	}

	/**
	 *  Export the passed file to the specified location
	 *
	 *  @param file org.eclipse.core.resources.IFile
	 *  @param location org.eclipse.core.runtime.IPath
	 */
	protected void exportFile(IFile file, IPath location) throws InterruptedException {
		if(!FileExtensions.GRAPH.equals(file.getFileExtension())) {
			monitor.worked(1);
			return;
		}

		IPath fullPath = location.append(file.getName());
		if (this.exportFormat == GraphConstants.EXPORT_FORMAT_DOT){
			fullPath = fullPath.addFileExtension(FileExtensions.DOT);
		}
		else if (this.exportFormat == GraphConstants.EXPORT_FORMAT_GRAPHXML){
			fullPath = fullPath.addFileExtension(FileExtensions.XML);
		}
		
		monitor.subTask(file.getFullPath().toString());
		String properPathString = fullPath.toOSString();
		
		
		File targetFile = new File(properPathString);
		
		if (targetFile.exists()) {
			if (!targetFile.canWrite()) {
				errorTable.add(new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID,
						0, NLS.bind(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_err_Cannot_overwrite_file_0, targetFile.getAbsolutePath()),
						null));
				monitor.worked(1);
				return;
			}

			if (overwriteState == OVERWRITE_NONE) {
				return;
			}

			if (overwriteState != OVERWRITE_ALL) {
				String overwriteAnswer = overwriteCallback.queryOverwrite(properPathString);

				if (IOverwriteQuery.CANCEL.equals(overwriteAnswer)) {
					throw new InterruptedException();
				}

				if (IOverwriteQuery.NO.equals(overwriteAnswer)) {
					monitor.worked(1);
					return;
				}

				if (IOverwriteQuery.NO_ALL.equals(overwriteAnswer)) {
					monitor.worked(1);
					overwriteState = OVERWRITE_NONE;
					return;
				}

				if (IOverwriteQuery.ALL.equals(overwriteAnswer)) {
					overwriteState = OVERWRITE_ALL;
				}
			}
		}

		try {
			exporter.write(file, fullPath);
		} catch (ExportException e) {
			errorTable.add(new Status(IStatus.ERROR, ControlFlowFactoryPlugin.PLUGIN_ID, 0,
					NLS.bind(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_err_Error_exporting_0_1, fullPath, e.getMessage()), e));
		} catch (IOException e) {
			errorTable.add(new Status(IStatus.ERROR, ControlFlowFactoryPlugin.PLUGIN_ID, 0,
					NLS.bind(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_err_Error_exporting_0_1, fullPath, e.getMessage()), e));
		} catch (CoreException e) {
			errorTable.add(new Status(IStatus.ERROR, ControlFlowFactoryPlugin.PLUGIN_ID, 0,
					NLS.bind(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_err_Error_exporting_0_1, fullPath, e.getMessage()), e));
		} catch (ClassNotFoundException e) {
			errorTable.add(new Status(IStatus.ERROR, ControlFlowFactoryPlugin.PLUGIN_ID, 0,
					NLS.bind(ControlFlowFactoryMessages.ControlFlowGraphExportWizard_err_Error_exporting_0_1, fullPath, e.getMessage()), e));

		}

		monitor.worked(1);
		ModalContext.checkCanceled(monitor);
	}

	/**
	 *	Export the resources contained in the previously-defined
	 *	resourcesToExport collection
	 */
	protected void exportSpecifiedResources() throws InterruptedException {
		Iterator<IResource> resources = resourcesToExport.iterator();
		IPath initPath = (IPath) path.clone();

		while (resources.hasNext()) {
			IResource currentResource = (IResource) resources.next();
			if (!currentResource.isAccessible()) {
				continue;
			}

			path = initPath;

			if (resource == null) {
				/*
				 * No root resource specified and creation of containment directories
				 * is required.  Create containers from depth 2 onwards (ie.- project's
				 * child inclusive) for each resource being exported. 
				 */
				if (createLeadupStructure) {
					createLeadupDirectoriesFor(currentResource);
				}

			} else {
				/* 
				 * Root resource specified.  Must create containment directories
				 * from this point onwards for each resource being exported 
				 */
				IPath containersToCreate = currentResource.getFullPath()
				.removeFirstSegments(
						resource.getFullPath().segmentCount())
						.removeLastSegments(1);

				for (int i = 0; i < containersToCreate.segmentCount(); i++) {
					path = path.append(containersToCreate.segment(i));
					exporter.createFolder(path);
				}
			}

			if (currentResource.getType() == IResource.FILE) {
				exportFile((IFile) currentResource, path);
			} else {
				if (createContainerDirectories) {
					path = path.append(currentResource.getName());
					exporter.createFolder(path);
				}

				try {
					exportChildren(((IContainer) currentResource).members(),
							path);
				} catch (CoreException e) {
					/* 
					 * should never happen because #isAccessible is called before #members is invoked,
					 * which implicitly does an existence check 
					 */
					errorTable.add(e.getStatus());
				}
			}
		}
	}

	/**
	 * Returns the status of the export operation.
	 * If there were any errors, the result is a status object containing
	 * individual status objects for each error.
	 * If there were no errors, the result is a status object with error code <code>OK</code>.
	 *
	 * @return the status
	 */
	public IStatus getStatus() {
		IStatus[] errors = new IStatus[errorTable.size()];
		errorTable.toArray(errors);
		return new MultiStatus(
				PlatformUI.PLUGIN_ID,
				IStatus.OK,
				errors,
				ControlFlowFactoryMessages.ControlFlowGraphExportWizard_err_Problems_encountered_during_export_,
				null);
	}

	/**
	 *  Answer a boolean indicating whether the passed child is a descendent
	 *  of one or more members of the passed resources collection
	 *
	 *  @return boolean
	 *  @param resources java.util.List
	 *  @param child org.eclipse.core.resources.IResource
	 */
	protected boolean isDescendent(List<IResource> resources, IResource child) {
		if (child.getType() == IResource.PROJECT) {
			return false;
		}

		IResource parent = child.getParent();
		if (resources.contains(parent)) {
			return true;
		}

		return isDescendent(resources, parent);
	}

	/**
	 *	Export the resources that were previously specified for export
	 *	(or if a single resource was specified then export it recursively)
	 */
	public void run(IProgressMonitor progressMonitor)
	throws InterruptedException {
		this.monitor = progressMonitor;

		exporter.setExportFormat(exportFormat);
		exporter.setExportGeometry(exportGeometry);
		exporter.setExportDecorations(exportVisualProperty);
		exporter.setExportComments(exportComments);

		if (resource != null) {
			if (createLeadupStructure) {
				createLeadupDirectoriesFor(resource);
			}

			if (createContainerDirectories
					&& resource.getType() != IResource.FILE) {
				/* ensure it's a container */
				path = path.append(resource.getName());
				exporter.createFolder(path);
			}
		}

		try {
			int totalWork = IProgressMonitor.UNKNOWN;
			try {
				if (resourcesToExport == null) {
					totalWork = countChildrenOf(resource);
				} else {
					totalWork = countSelectedResources();
				}
			} catch (CoreException e) {
				/* Should not happen */
				errorTable.add(e.getStatus());
			}
			monitor.beginTask(ControlFlowFactoryMessages.ControlFlowGraphExportWizardMonitor_title, totalWork);
			if (resourcesToExport == null) {
				exportAllResources();
			} else {
				exportSpecifiedResources();
			}
		} finally {
			monitor.done();
		}
	}

	/**
	 *	Set this boolean indicating whether a directory should be created for
	 *	Folder resources that are explicitly passed for export
	 *
	 *	@param value boolean
	 */
	public void setCreateContainerDirectories(boolean value) {
		createContainerDirectories = value;
	}

	/**
	 *	Set this boolean indicating whether each exported resource's complete path should
	 *	include containment hierarchies as dictated by its parents
	 *
	 *	@param value boolean
	 */
	public void setCreateLeadupStructure(boolean value) {
		createLeadupStructure = value;
	}

	/**
	 *	Set this boolean indicating whether exported resources should automatically
	 *	overwrite existing files when a conflict occurs. If not
	 *	query the user.
	 *
	 *	@param value boolean
	 */
	public void setOverwriteFiles(boolean value) {
		if (value) {
			overwriteState = OVERWRITE_ALL;
		}
	}
}
