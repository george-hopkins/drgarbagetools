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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryPlugin;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.io.FileExtensions;
import com.drgarbage.utils.Messages;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;

/**
 * Utils for control flow factory actions.
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: ActionUtils.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class ActionUtils {

	/**
	 * Save Diagram in file and open Editor.
	 * @param path
	 * @param parent
	 * @param page
	 * @param controlFlowGraphDiagram
	 */
	public static void saveDiagramInFileAndOpenEditor(IPath path, 
			final Shell parent, 
			final IWorkbenchPage page, 
			final ControlFlowGraphDiagram controlFlowGraphDiagram,
			final boolean openGraphInEditor){

		/* check file extension, if not equals to graph set again*/
		if(!path.getFileExtension().endsWith(FileExtensions.GRAPH)){
			path = path.addFileExtension(FileExtensions.GRAPH);
		}

		final IFile file= ResourcesPlugin.getWorkspace().getRoot().getFile(path);		
		ProgressMonitorDialog monitor = new ProgressMonitorDialog(parent);
			try {				
				monitor.run(
						false, 	/* don't fork */
						false, 	/* not cancelable */
						new WorkspaceModifyOperation() { /* run this operation */
							public void execute(final IProgressMonitor monitor) {
								try {
									ByteArrayOutputStream out = new ByteArrayOutputStream();

									ObjectOutputStream oos = new ObjectOutputStream(out);
									oos.writeObject(controlFlowGraphDiagram);
									oos.close();
									
									/* delete if exists */
									if(file.exists()){
										file.delete(false, monitor);
									} 

									file.create(
										new ByteArrayInputStream(out.toByteArray()), /* contents */
										true, 		/* keep saving, even if IFile is out of sync with the Workspace */
										monitor); 	/* progress monitor */
								} catch (StackOverflowError e) {
									//Messages.error(StackOverflowError.class.getName() + PreferencesMessages.ExceptionAdditionalMessage);
									ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, ControlFlowFactoryPlugin.PLUGIN_ID, StackOverflowError.class.getName(), e));
									return;
								} catch (CoreException ce) {
									Messages.error(CoreException.class.getName() + CoreMessages.ExceptionAdditionalMessage);
									ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, ControlFlowFactoryPlugin.PLUGIN_ID, CoreException.class.getName(), ce));
									return;
								} catch (IOException ioe) {
									Messages.error(IOException.class.getName() + CoreMessages.ExceptionAdditionalMessage);
									ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, ControlFlowFactoryPlugin.PLUGIN_ID, IOException.class.getName(), ioe));
									return;
								} 
							}
						});
			} catch (InterruptedException ie) {				
				Messages.error(InterruptedException.class.getName() + CoreMessages.ExceptionAdditionalMessage);
				ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, ControlFlowFactoryPlugin.PLUGIN_ID, InterruptedException.class.getName(), ie));
				return;
			} catch (InvocationTargetException ite) {
				Messages.error(InvocationTargetException.class.getName() + CoreMessages.ExceptionAdditionalMessage);
				ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, ControlFlowFactoryPlugin.PLUGIN_ID, InvocationTargetException.class.getName(), ite));
				return;
			}
			finally
			{
				monitor.close();
			}
		
		/* open newly created file in the editor */
			if(openGraphInEditor){
				if (file != null && page != null) {
					try {
						/* close editor if already open for this file */
						IEditorPart part = page.findEditor(new FileEditorInput(file));
						if(part != null)
							page.closeEditor(part, false);
						
						IDE.openEditor(page, file, true);
					} catch (PartInitException e) {
						Messages.error(PartInitException.class.getName() + CoreMessages.ExceptionAdditionalMessage);
						ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, ControlFlowFactoryPlugin.PLUGIN_ID, PartInitException.class.getName(), e));
						return;
					}
				}
			}
			else{
				showInPackageExplorer(file, false);
			}
	}
	
	/**
	 * Save a text content in a file and open an Editor.
	 * @param path
	 * @param parent
	 * @param page
	 * @param content
	 */
	public static void saveContentInFileAndOpenEditor(IPath path, 
			final Shell parent, 
			final IWorkbenchPage page, 
			final String content,
			final boolean openEditor){

		final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);        
		ProgressMonitorDialog monitor = new
		ProgressMonitorDialog(parent);
		try {                
			monitor.run(
					false,     /* don't fork */
					false,     /* not cancelable */
					new
					WorkspaceModifyOperation() { /* run this operation */
						public void
						execute(final IProgressMonitor monitor) {
							try {

								/* delete if exists */
								if(file.exists()){
									file.delete(false, monitor);
								} 

								file.create(
										new ByteArrayInputStream(content.getBytes()), /* contents */
										true,         /* keep saving, even if IFile is out of sync with the Workspace */
										monitor);     /* progress monitor */
							} catch
							(StackOverflowError e) {
								//Messages.error(StackOverflowError.class.getName() + PreferencesMessages.ExceptionAdditionalMessage);
								ControlFlowFactoryPlugin.getDefault().getLog().log(new
										Status(IStatus.ERROR, ControlFlowFactoryPlugin.PLUGIN_ID,
												StackOverflowError.class.getName(), e));

								return;
							} catch
							(CoreException ce) {
								Messages.error(CoreException.class.getName() +	CoreMessages.ExceptionAdditionalMessage);

								ControlFlowFactoryPlugin.getDefault().getLog().log(new
										Status(IStatus.ERROR, ControlFlowFactoryPlugin.PLUGIN_ID,
												CoreException.class.getName(), ce));

								return;
							}
						}
					});
		} catch (InterruptedException ie) {
			Messages.error(InterruptedException.class.getName() + CoreMessages.ExceptionAdditionalMessage);

			ControlFlowFactoryPlugin.getDefault().getLog().log(new
					Status(IStatus.ERROR, ControlFlowFactoryPlugin.PLUGIN_ID,
							InterruptedException.class.getName(), ie));
			return;
		} catch (InvocationTargetException ite) {
			Messages.error(InvocationTargetException.class.getName() +
					CoreMessages.ExceptionAdditionalMessage);

			ControlFlowFactoryPlugin.getDefault().getLog().log(new
					Status(IStatus.ERROR, ControlFlowFactoryPlugin.PLUGIN_ID,
							InvocationTargetException.class.getName(), ite));
			return;
		}
		finally
		{
			monitor.close();
		}

		/* open newly created file in the editor */
		if(openEditor){
			if (file != null && page != null) {
				try {
					/* close editor if already open for this file */
					IEditorPart part =
						page.findEditor(new FileEditorInput(file));
					if(part != null)

						page.closeEditor(part, false);

					IDE.openEditor(page,
							file, true);
				} catch (PartInitException e) {

					Messages.error(PartInitException.class.getName() +
							CoreMessages.ExceptionAdditionalMessage);

					ControlFlowFactoryPlugin.getDefault().getLog().log(new
							Status(IStatus.ERROR, ControlFlowFactoryPlugin.PLUGIN_ID,
									PartInitException.class.getName(), e));
					return;
				}
			}
		}
		else{
			showInPackageExplorer(file, false);
		}
	}
	
	public static boolean showInPackageExplorer(Object file, boolean supressMessages){
		PackageExplorerPart view = PackageExplorerPart.openInActivePerspective();
		if (!reveal(view, file)) {
			if(!supressMessages){
				IResource file_resource = (IResource) file;
				String path = file_resource.getFullPath().toString();
				showWarning(path);
			}
			return false;
		}
		return true;
	}
	
	private static void showWarning(String file){
		Messages.warning(
				MessageFormat.format(
						ControlFlowFactoryMessages.WARNING_CannotShowFileInPackageexplorer, 
						new Object[] {file})
		);
	}
	
	private static boolean reveal(PackageExplorerPart view, Object element) {
		if (view == null) {
			return false;
		}
		view.selectReveal(new StructuredSelection(element));
		IElementComparer comparer= view.getTreeViewer().getComparer();
		Object selected= getSelectedElement(view);
		if (comparer != null ? comparer.equals(element, selected) : element.equals(selected)) {
			return true;
		}
		return false;
	}
	
	private static Object getSelectedElement(PackageExplorerPart view) {
		return ((IStructuredSelection) view.getSite().getSelectionProvider().getSelection()).getFirstElement();
	}
	
	/**
	 * Resolves a type name in the context of the declaring type.
	 * 
	 * @param refTypeSig the type name in signature notation (for example 'QVector') this can also be an array type, but dimensions will be ignored.
	 * @param declaringType the context for resolving (type where the reference was made in)
	 * @return returns the fully qualified type name or build-in-type name. if a unresolved type couldn't be resolved null is returned
	 */
	public static String getResolvedTypeName(String refTypeSig, IType declaringType) throws JavaModelException, IllegalArgumentException {
		return com.drgarbage.core.ActionUtils.getResolvedTypeName(refTypeSig, declaringType);
	}
	
	/**
	 * Concatenates two names. Uses a slash '/' for separation.
	 * Terminates the string with a semicolon ';'. 
	 * Both strings can be empty or <code>null</code>.
	 */
	public static String concatenateName(String name1, String name2) {
		return com.drgarbage.core.ActionUtils.concatenateName(name1, name2);
	}
}
