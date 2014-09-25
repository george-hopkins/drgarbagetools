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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.containers.FolderSourceContainer;
import org.eclipse.debug.internal.ui.sourcelookup.SourceLookupManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugElement;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

import com.drgarbage.bytecode.jdi.JDIUtils;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.utils.Messages;
import com.sun.jdi.ReferenceType;

/**
 * Dialog for selection and export the classes from the JVM.
 * @author Than
 * @version $Revision$
 * $Id$
 */
@SuppressWarnings("restriction")
public class JDIExportFromJvmDialog {

	/**
	 * The main shell of the dialog.
	 */
	private final Shell shell = new Shell();
	
	/**
	 * The dialog size constants
	 */
	private Point dialogSize = new Point(500, 500);
	private int folderPromptWidth = 270;

	/* buttons */
	private Button selectAll;
	private Button deselectAll;
	private Button chooseDirBtn;
	private Button copyToPathBtn;
	private Button closeBtn;
	
	/** 
	 * The Checkbox viewer.
	 */
	private CheckboxTreeViewer viewer;
	
	/**
	 * Hint text of the filter rules.
	 */
	private Label hintLabel;
	
	/**
	 * The reference to the selected export class folder.
	 */
	private IFolder exportFolder;

	/**
	 * Constructs the Export Class dialog.
	 */
	public JDIExportFromJvmDialog() {
		shell.setLayout(new GridLayout(1, false));
		createControls();
		shell.setSize(dialogSize);

		/* Set the dialog to display at center width of the screen */
		Monitor primary = shell.getDisplay().getPrimaryMonitor();

		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();

		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = 0;

		shell.setLocation(x, y);
	}
	
	/**
	 * Makes the dialog visible, sets the focus and asks 
	 * the window manager to make the dialog active.
	 */
	public void open(){
		shell.open();
	}

	/**
	 * Creates dialog specific controls. 
	 */
	protected void createControls() {
		
		/* hint text */
		hintLabel = new Label(shell, SWT.NONE);
		hintLabel.setText(BytecodeVisualizerMessages.JDI_Export_Dialog_hint_text);

		/* Filtered list */
		PatternFilter pf = new PatternFilter();
		pf.setPattern("");
		FilteredCheckboxList ft = new FilteredCheckboxList(shell, SWT.BORDER | SWT.MULTI, pf);
		ft.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		/* initialize the viewer before filling the list */
		viewer = (CheckboxTreeViewer)ft.getViewer();
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		
		fillList(ft);
		
		Composite selectComposite = new Composite(shell, SWT.NONE);
		selectComposite.setLayout(new GridLayout(2, false));
		selectComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		selectAll = new Button(selectComposite, SWT.PUSH);
		selectAll.setText(BytecodeVisualizerMessages.JDI_Export_Dialog_Select_All);
		selectAll.addListener(SWT.Selection, new Listener() {
			
			/* (non-Javadoc)
			 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
			 */
			@SuppressWarnings("deprecation")
			public void handleEvent(Event event) {
				viewer.setAllChecked(true);
			}
		});
		
		deselectAll = new Button(selectComposite, SWT.PUSH);
		deselectAll.setText(BytecodeVisualizerMessages.JDI_Export_Dialog_Deselect_All);
		deselectAll.addListener(SWT.Selection, new Listener() {
			
			/* (non-Javadoc)
			 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
			 */
			@SuppressWarnings("deprecation")
			public void handleEvent(Event event) {
				viewer.setAllChecked(false);
			}
		});
	
		/* buttons */
		Composite dirPromptComposite = new Composite(shell, SWT.BORDER);
		dirPromptComposite.setLayout(new GridLayout(2, false));
		dirPromptComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Text pathText = new Text(dirPromptComposite, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData();
		gd.widthHint = folderPromptWidth;
		pathText.setLayoutData(gd);
		pathText.setEditable(false);		
		pathText.setText(BytecodeVisualizerMessages.JDI_Export_Dialog_Define_Class_Folder);

		chooseDirBtn = new Button(dirPromptComposite, SWT.PUSH);
		chooseDirBtn.setText(BytecodeVisualizerMessages.JDI_Export_Dialog_browse_btn_label);
		chooseDirBtn.setToolTipText(BytecodeVisualizerMessages.JDI_Export_Dialog_tooltip_browse_folder);
		chooseDirBtn.addListener(SWT.Selection, new Listener() {
			
			/* (non-Javadoc)
			 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
			 */
			public void handleEvent(Event event) {
				
				Object o = DebugUITools.getDebugContext();
				if (o instanceof JDIDebugElement) {
					JDIDebugElement jdiDebugElement = (JDIDebugElement) o;
					ILaunchConfiguration config= jdiDebugElement.getLaunch().getLaunchConfiguration();

					IJavaProject javaProject = null;
					try {
						javaProject = JavaRuntime.getJavaProject(config);
					} catch (CoreException e) {
						BytecodeVisualizerPlugin.getDefault().getLog().log(
								BytecodeVisualizerPlugin.createErrorStatus(e.getMessage(), e)
								);
					}

					ProjectBuildPathDialog selectFolderDlg = new ProjectBuildPathDialog(shell, javaProject);
					selectFolderDlg.open();
					IFolder f = selectFolderDlg.getSelectedFolder();
					if(f != null){
						exportFolder = f;
						pathText.setText(exportFolder.getFullPath().toOSString());
					}

				}				
			}
		});
		
		Composite footerBtnComposite = new Composite(shell, SWT.RIGHT_TO_LEFT);
		footerBtnComposite.setLayout(new GridLayout(2, false));
		
		copyToPathBtn = new Button(footerBtnComposite, SWT.NONE);
		copyToPathBtn.setText(BytecodeVisualizerMessages.JDI_Export_Dialog_copy_to_btn_text);
		copyToPathBtn.setEnabled(false);
		copyToPathBtn.addSelectionListener(new SelectionListener() {
			
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				
				final Object[] selection = viewer.getCheckedElements();
				
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
				try {
					dialog.run(false, true, new IRunnableWithProgress() {
						public void run(final IProgressMonitor monitor) throws InvocationTargetException {
							monitor.beginTask(BytecodeVisualizerMessages.JDI_Export_Dialog_Export_Selected_Classes, selection.length + 1);

							for(Object o: selection){
								String className = o.toString();
								String[] nameArray =  className.split(Pattern.quote("."));

								IPath path = exportFolder.getFullPath();
								try {
									for(int i = 0; i < nameArray.length - 1; i++){
										path = path.append(nameArray[i]);
										createFolder(path, null);
									}

									path = path.append(nameArray[nameArray.length - 1])
											.addFileExtension("class");

									monitor.subTask(className);
									byte[] content = getClassFileContent(className);
									
									createFile(path, content, null);
									monitor.worked(1);

								} catch (CoreException e1) {
									BytecodeVisualizerPlugin.getDefault().getLog().log(
											BytecodeVisualizerPlugin.createErrorStatus(e1.getMessage(), e1)
											);
								}
							}

							monitor.subTask("Saving Sourcelookup ...");
							
							JDIDebugElement jdiDebugElement = null;
							Object o1 = DebugUITools.getDebugContext();
							if (o1 instanceof JDIDebugElement) {
								jdiDebugElement = (JDIDebugElement) o1;

								/* add to source lookup */	
								ISourceLocator sourceLocator = jdiDebugElement.getLaunch().getSourceLocator();

								if(sourceLocator instanceof ISourceLookupDirector){

									boolean folderExists = false;

									/* get source lookup entries */
									ISourceLookupDirector sourceLookupDirector = (ISourceLookupDirector) sourceLocator;
									ISourceContainer[] sourceContainers = sourceLookupDirector.getSourceContainers();
									List<ISourceContainer> listContainers = new ArrayList<ISourceContainer>(sourceContainers.length);
									for(ISourceContainer isc: sourceContainers){

										/* check if source exists already in the source lookup */
										if(isc instanceof FolderSourceContainer){
											FolderSourceContainer fsc = (FolderSourceContainer) isc;
											IContainer ic = fsc.getContainer();
											if(ic instanceof IFolder){
												IFolder iFolder = (IFolder)ic;
												if(iFolder.equals(exportFolder)){
													folderExists = true;
													break;
												}
											}
										}

										listContainers.add(isc);
									}

									if(!folderExists){
										/* add the new source to the source lookup */
										ISourceContainer exportFolderContainer = new FolderSourceContainer(exportFolder, false);
										listContainers.add(exportFolderContainer);							
										sourceLookupDirector.setSourceContainers(listContainers.toArray(new ISourceContainer[listContainers.size()]));

										/* save the source lookup configuration */
										try {
											ILaunchConfigurationWorkingCopy workingCopy = sourceLookupDirector.getLaunchConfiguration().getWorkingCopy();
											workingCopy.setAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_MEMENTO, sourceLookupDirector.getMemento());
											workingCopy.setAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_ID, sourceLookupDirector.getId());								
											workingCopy.doSave();								
										} catch (CoreException e1) {
											BytecodeVisualizerPlugin.getDefault().getLog().log(
													BytecodeVisualizerPlugin.createErrorStatus(e1.getMessage(), e1)
													);
										}
									}
								}

								/* display source */
								IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
								SourceLookupManager.getDefault().displaySource(jdiDebugElement, page, true);
								
								monitor.worked(1);
							}
							
							monitor.done();
						}
					});
				} catch (InvocationTargetException e1) {
					BytecodeVisualizerPlugin.log(e1);
				} catch (InterruptedException e2) {
					BytecodeVisualizerPlugin.log(e2);
				}

				shell.close();
			}

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		closeBtn = new Button(footerBtnComposite,SWT.NONE);
		closeBtn.setText(BytecodeVisualizerMessages.JDI_Export_Dialog_close_text);
		closeBtn.addSelectionListener(new SelectionListener() {
			
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});	
		
		/* enable copy button if the export has been set */
		pathText.addModifyListener(
				new ModifyListener(){
					
					/* (non-Javadoc)
					 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
					 */
					public void modifyText(ModifyEvent e) {
						/* Default text: <Define a folder to class export> */
						if(!pathText.getText().startsWith("<")){
							copyToPathBtn.setEnabled(true);
						}
					}
		});
	}
	
	/**
	 * Creates an empty file in the given workspace path.
	 * 
	 * @param filePath the path of the file resource to be created
	 * @param content the file content as a byte array
	 * @param monitor monitor a progress monitor, or <code>null</code> 
	 *         if progress reporting is not desired
	 * @return the new file resource handle
	 * @throws CoreException 
	 * 
	 * @see {@link IContainer#getFile(IPath) }
	 */
	protected IFile createFile(IPath filePath, byte[] content, IProgressMonitor monitor) throws CoreException {
		IFile f = IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getFile(filePath);
		if(!f.exists()){
			InputStream contents = new ByteArrayInputStream(content);
			f.create(contents, true, monitor);
		}
		return f;
	}
	
	/**
	 * Creates a folder resource in the given workspace path.
	 * 
	 * @param filePath the path of the folder resource to be created
	 * @param monitor monitor a progress monitor, or <code>null</code> 
	 *         if progress reporting is not desired
	 * @return the new folder resource handle
	 * @throws CoreException 
	 * 
	 * @see {@link IContainer#getFolder(IPath) }
	 */
	protected IFolder createFolder(IPath filePath, IProgressMonitor monitor) throws CoreException {
		IFolder f = IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getFolder(filePath);
		if(!f.exists()){
			f.create(true, true, monitor);
		}
		return f;
		
	}

	/**
	 * Fill the {@link FilteredTree FilteredList} with classes from the JVM.<br />
	 * Will only display the first 100 classes
	 * 
	 * @param fl the filtered checkbox list
	 */
	private void fillList(FilteredTree fl) {
		Object o = DebugUITools.getDebugContext();
		List<String> listOfClasses = new ArrayList<String>();

		if (o instanceof JDIDebugElement) {
			JDIDebugElement jdiDebugElement = (JDIDebugElement) o;
			com.sun.jdi.VirtualMachine vm = jdiDebugElement
					.getJavaDebugTarget().getVM();

			List<ReferenceType> l = vm.allClasses();

			for (ReferenceType r : l) {
				listOfClasses.add(r.name());
			}
		}
		
		// fix for ticket 49
		if (listOfClasses.size() > 1000) {
			Collections.sort(listOfClasses);
			// create a dialog
			List<String> selectionList = new ArrayList<String>();
			int low = 1;
			for (int i = 1; i <= listOfClasses.size(); i++) {
				if (i % 1000 == 0 || i == listOfClasses.size()) {
					selectionList.add("Classes " + low + " to " + i 
							+ " (" + listOfClasses.get(low - 1) + " ... " 
							+ listOfClasses.get(i - 1) + ")");
					low = i;
				}
			}
			String selection = null;
			while (selection == null) {
				selection = Messages.openSelectDialog(BytecodeVisualizerMessages.JDI_Export_JVM_too_many_classes,
						JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CLASS), selectionList);
			}

			// only display selected range
			low = Integer.parseInt(selection.split(" ")[1]);
			low--;
			int high = Integer.parseInt(selection.split(" ")[3]);
			high--;
			for (int i = listOfClasses.size() - 1; i > high; i--) {
				listOfClasses.remove(i);
			}
			for (int i = 0; i < low; i ++) {
				listOfClasses.remove(0);
			}
		}
		
		viewer.setInput(listOfClasses.toArray());
	}
	
	/**
	 * Returns the class file content as a byte array for
	 * the given class name.
	 * 
	 * @param className the fully qualified class name
	 * @return byte array
	 * @see JDIUtils#getClassFileContent(ReferenceType)
	 */
	private byte[] getClassFileContent(String className) {
		Object o = DebugUITools.getDebugContext();
		
				ReferenceType ref = null;
				if (o instanceof JDIDebugElement) {
					JDIDebugElement jdiDebugElement = (JDIDebugElement) o;
					com.sun.jdi.VirtualMachine vm = jdiDebugElement
							.getJavaDebugTarget().getVM();
		
					List<ReferenceType> classes = vm.classesByName(className);
					if(classes.size() != 0){
						ref = classes.get(0);
					}
				}
		return JDIUtils.referenceTypetoByteArray(ref);
	}
	
	/**
	 * Simple implementation of the Tree content provider.
	 * @see {@link ITreeContentProvider}
	 */
	public class TreeContentProvider implements ITreeContentProvider {
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object arg0) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object arg0) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object arg0) {
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object arg0) {
			if(arg0 != null){
				return (Object[])arg0;
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}

	}
	
	/**
	 * Simple implementation of the LabelProvider.
	 * @see {@link ILabelProvider}
	 */
	public class LabelProvider implements ILabelProvider {

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void addListener(ILabelProviderListener listener)  {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose() {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
		 */
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener listener) {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object element) {
			return JavaPluginImages.DESC_OBJS_CLASS.createImage();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element) {
			return element.toString();
		}
	}
	
}
