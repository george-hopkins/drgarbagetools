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

package com.drgarbage.controlflowgraphfactory.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.internal.filesystem.local.LocalFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryPlugin;
import com.drgarbage.controlflowgraphfactory.dialogs.ExportGraphFileFolderSelectionDialog;
import com.drgarbage.controlflowgraphfactory.dialogs.ExportProgressMonitorDialog;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.graph.DefaultGraphSpecification;
import com.drgarbage.graph.GraphConstants;
import com.drgarbage.graph.IGraphSpecification;
import com.drgarbage.utils.Messages;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagramFactory;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagramFactory.Result;

/**
 * Action for generation control flow graphs for selected class.
 *
 * @author Sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: GenerateGraphsAction.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public abstract class GenerateGraphsAction implements IObjectActionDelegate {
	
	private static IFolder createFolder(IWorkspaceRoot root, String path) throws CoreException{
		IFolder folder = null;
		
		IPath newPath = new Path(path);
		folder = root.getFolder(newPath);
		if(folder.exists()){
			return folder;
		}
		
		int count = newPath.segmentCount();
		if(count < 1){
			return folder;
		}
		
		String[] s = newPath.segments();
		newPath = new Path(s[0]);
		for(int i = 1; i < count; i++){
			newPath = newPath.append(s[i]);
			
			if(!root.getFolder(newPath).exists()){
				folder = (IFolder)root.getFolder(newPath);				
				folder.create(true, true, null);
			}
		}

		return folder;
	} 
	
	protected static ExportGraphFileFolderSelectionDialog openSelectFolderdialog(Shell shell, IWorkspaceRoot root){
		ExportGraphFileFolderSelectionDialog  dlg = new ExportGraphFileFolderSelectionDialog (shell, false, IResource.FOLDER);
		dlg.setEmptyListMessage("no folder selected");
		dlg.setMessage("Select a folder:");
		dlg.setTitle("Select a folder");
		dlg.setHelpAvailable(false);
		dlg.addFilter(new ViewerFilter(){
			
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				String labelText = element.toString();

				if (labelText == null){
					return false;
				}

				if(labelText.endsWith(".metadata")){
					return false;
				}
				
				return true;
			}
			
		});

		try {
			IFileStore fileStore = EFS.getStore(root.getLocationURI());
			dlg.setInput(fileStore);
		} catch (CoreException e) {
			ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
			Messages.error(e.getMessage() + CoreMessages.ExceptionAdditionalMessage);
			return null;
		}
		
		return dlg;
	}
	
	
	private boolean FULLY_QUALIFIED_NAME = true;
	private IGraphSpecification graphSpecification;
	
	private int graphType = GraphConstants.GRAPH_TYPE_SOURCE_GRAPH;
	
	private ISelection selection;
	
	private IWorkbenchPart targetPart;

	public GenerateGraphsAction(int graphType) {
		super();
		this.graphType = graphType;
	}

	/**
	 * Abstract create method. It has to implemented by clients.
	 *
	 * @param shell
	 * @param folder4
	 * @param type
	 * @param createMonitor
	 * @param overwriteAll
	 * @param supressMessages
	 * @return
	 * @throws CoreException
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	protected Result createGraphs(
			Shell shell, 
			IFolder folder, 
			IType type, 
			boolean createMonitor, 
			boolean overwriteAll, 
			boolean supressMessages)
	throws CoreException, InvocationTargetException,
	InterruptedException, IOException 
	{
		
		IGraphSpecification spec = new DefaultGraphSpecification(getGraphSpecification());
		spec.setGraphType(graphType);
	
		return ControlFlowGraphDiagramFactory.builAndSavedControlFlowDiagrams(
				new Shell(), 
				folder, type, 
				spec,
				supressMessages
		);
	}

	/**
	 * Creates graphs for the given type.
	 * @param type
	 * @param target directory as LocalFile
	 * @param workspace root
	 * @param monitor
	 * @throws JavaModelException 
	 */
	private Result createGraphsForType(IType type, 
			LocalFile lf, 
			IWorkspaceRoot root, 
			boolean createMonitor, 
			boolean overwriteAll, 
			boolean suppressMessages) 
	throws JavaModelException{
		
		try {
			if(type.isInterface() || type.isEnum() || type.isAnnotation()){
				String msg = MessageFormat.format(
						ControlFlowFactoryMessages.ERROR_ClassFile_is_Interface_Enum_Annotation, 
						new Object[] {type.getFullyQualifiedName()});
				if(!suppressMessages){	
					Messages.info(msg);
				}
				else{
					ControlFlowFactoryPlugin.getDefault().getLog().log(
							new Status(IStatus.WARNING,
									ControlFlowFactoryPlugin.PLUGIN_ID, 
									msg
									)
					);
				}
				return Result.OK;
			}
		} catch (JavaModelException e) {
			ControlFlowFactoryPlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e)
			);
		
			if(!suppressMessages){
				Messages.error(e.getMessage() + CoreMessages.ExceptionAdditionalMessage);
			}
			
			return Result.ERROR;
		}
		
		URI localFileURI = lf.toURI();
		URI rootURI = root.getLocationURI();
		
		/* The local file URI has to be always longer than root URI*/
		int res = localFileURI.compareTo(rootURI);
		
		/* cut the prefix and make path relative to root */
		String localFilePath = localFileURI.getPath();
		String relativePath = localFilePath.substring(localFilePath.length() - res + 1, localFilePath.length());
		
		/* setup path */
		StringBuffer buf = new StringBuffer();
		buf.append(relativePath);
		buf.append(IPath.SEPARATOR);
		if(FULLY_QUALIFIED_NAME){
			String packageFragment = type.getPackageFragment().getElementName();
			buf.append(packageFragment.replace('.', IPath.SEPARATOR));
		}
		buf.append(IPath.SEPARATOR);
		if(type.isAnonymous() || type.isMember()){
			String name = type.getFullyQualifiedName();
			int index = name.lastIndexOf('.');
			name = name.substring(++index);
			name = name.replace('$', IPath.SEPARATOR);
			buf.append(name);
		}
		else{
			buf.append(type.getElementName());
		}

		/* create folder structure */
		try {
			IFolder folder = createFolder(root, buf.toString());
			Result result = createGraphs(new Shell(), folder, type, createMonitor, overwriteAll, suppressMessages);
			ActionUtils.showInPackageExplorer(folder, suppressMessages);
			return result;
		} catch (CoreException e) {
			ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
			if(!suppressMessages){
				Messages.error(e.getMessage() + CoreMessages.ExceptionAdditionalMessage);
			}
		} catch (InvocationTargetException e) {
			ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
			if(!suppressMessages){
				Messages.error(e.getMessage() + CoreMessages.ExceptionAdditionalMessage);
			}
		} catch (InterruptedException e) {
			ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
			if(!suppressMessages){
				Messages.error(e.getMessage() + CoreMessages.ExceptionAdditionalMessage);
			}
		} catch (IOException e) {
			ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
			if(!suppressMessages){
				Messages.error(e.getMessage() + CoreMessages.ExceptionAdditionalMessage);
			}
		}		
		
		return Result.ERROR;
	}

	public IGraphSpecification getGraphSpecification() {
		return graphSpecification;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if(selection == null || !(selection instanceof TreeSelection)){
			return;
		}
		
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		
		ExportGraphFileFolderSelectionDialog dlg = openSelectFolderdialog(targetPart.getSite().getShell(), root);
		dlg.open();
		Object ret = dlg.getFirstResult();
		if(ret == null){
			return;
		}
		
		final LocalFile localFile = (LocalFile) ret;
		graphSpecification = dlg.getGraphSpecification();

		
		TreeSelection treeSel = (TreeSelection)selection;
		final Object o = treeSel.getFirstElement();
		IType type = null;
		if(o instanceof IType){
			type = (IType)o;
		}
		
		try {
			if(type == null){		
				final IPackageFragment pack = (IPackageFragment)o;
				final ExportProgressMonitorDialog monitor0 = new ExportProgressMonitorDialog(targetPart.getSite().getShell());
				monitor0.setErrorMsg("An error has occured while processing the package '" + pack.getElementName() + "'." + CoreMessages.ExceptionAdditionalMessage);
				monitor0.run(
						false, 
						true, 
						new WorkspaceModifyOperation() {

					/* (non-Javadoc)
					 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
					 */
					@Override
					protected void execute(IProgressMonitor monitor)
					throws CoreException,
					InvocationTargetException, InterruptedException {

						List<IType> listOfTypes= new ArrayList<IType>();

						/*
						 * It is possible that a package fragment contains only compilation units 
						 * (in other words, its kind is K_SOURCE), in which case this method returns 
						 * an empty collection.
						 */
						IClassFile[] classFiles = pack.getClassFiles();
						for(IClassFile f: classFiles){
							listOfTypes.add(f.getType());
						}

						ICompilationUnit[] compilationUnits = pack.getCompilationUnits();
						for(ICompilationUnit u: compilationUnits){
							IType unitTypes[] = u.getTypes();
							for(IType type : unitTypes){
								listOfTypes.add(type);	
							}
						}

						final int ticks = classFiles.length;
						monitor.beginTask(ControlFlowFactoryMessages.ProgressDialogCreateGraphs, ticks);

						boolean yes_To_All = false;
						for(IType t: listOfTypes){
							monitor.subTask(t.getElementName());
							Result res = createGraphsForType(t, localFile, root, false, yes_To_All, true);
							if(res ==  Result.NO_TO_ALL){ 
								monitor.done();
								return;
							}
							else if(res == Result.YES_TO_ALL){
								yes_To_All = true;
							}
							else if(res == Result.ERROR){
								monitor0.setErrorDuringExecution(true);
							}

							monitor.worked(1);
						}								

						monitor.done();
					}
				});

			}
			else{
				Result res = createGraphsForType(type, localFile, root, true, false, false);
				if(res == Result.ERROR){
					Messages.warning("An error has occured while processing the class file '" + type.getElementName() + "'." + CoreMessages.ExceptionAdditionalMessage);
				}
			}
			
		} catch (InvocationTargetException e) {
			StringBuffer buf = new StringBuffer("InvocationTargetException: ");
			Throwable th = e.getTargetException();
			if(th != null){
				buf.append(th.toString());
			}

			ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, buf.toString() , e));
			Messages.error(buf.toString() + CoreMessages.ExceptionAdditionalMessage);
			return;
		} catch (InterruptedException e) {
			ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
			Messages.error(e.getMessage() + CoreMessages.ExceptionAdditionalMessage);
			return;
		} catch (JavaModelException e) {
			ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
			Messages.error(e.getMessage() + CoreMessages.ExceptionAdditionalMessage);
			return;
		}
		finally{
			dlg = null;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	
	public void setGraphSpecification(IGraphSpecification exportSpecification) {
		this.graphSpecification = exportSpecification;
	}
}
