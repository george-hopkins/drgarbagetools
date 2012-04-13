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

package com.drgarbage.sourcecodevisualizer;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.osgi.framework.internal.core.AbstractBundle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.drgarbage.core.CoreConstants;
import com.drgarbage.core.CorePlugin;
import com.drgarbage.core.preferences.CorePreferenceConstants;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("restriction")
public class SourcecodeVisualizerPlugin extends AbstractUIPlugin {

	/** 
	 * The plug-in ID 
	 */
	public static final String PLUGIN_ID = "com.drgarbage.sourcecodevisualizer";
	
	/**
	 *  The plug-in version 
	 */
	public static String PLUGIN_VERSION;

	/** 
	 * The shared instance
	 */
	private static SourcecodeVisualizerPlugin plugin;

	/**
	 * The constructor
	 */
	public SourcecodeVisualizerPlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		Bundle bundle = context.getBundle();
		if(bundle instanceof AbstractBundle){
			AbstractBundle aBundle = (AbstractBundle) bundle;
			PLUGIN_VERSION = aBundle.getVersion().toString();
		}
		
		/*own properties */
		CorePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener(){
			
			public void propertyChange(PropertyChangeEvent event) {
				
				/* Restart all active editors and open Control flow graph view if necessary*/
				if(event.getProperty().equals(CorePreferenceConstants.GRAPH_PANEL_LOCATION)){
				
					if(!isPropertyUpdateRuning()){
						setPropertyUpdateRuning(true);
						IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();					
						Thread t = new PropertyUpdateRunner(page, page.getWorkbenchWindow().getShell().getDisplay());
						t.start();
					}
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static SourcecodeVisualizerPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Indicates if the property update is running.
	 */
	private boolean propertyUpdateRuning = false;
	
	/**
	 * Returns true if the property update is running.
	 * @return true or false
	 */
	public synchronized final boolean isPropertyUpdateRuning() {
		return propertyUpdateRuning;
	}

	/**
	 * Sets property update running flag.
	 * @param true or false
	 */
	public synchronized final void setPropertyUpdateRuning(boolean b) {
		propertyUpdateRuning = b;
	}
	
	/**
	 * Property Update Runner. Restart all open ClassFle Editor
	 * and shows the status in the progress dialog.
	 */
	class PropertyUpdateRunner extends Thread{

		/*Editor ID of the editors  have to be restarted. No other way to get the ID. */
		final static String editorID = "com.drgarbage.sourcecodevisualizer.editors.JavaCodeEditor";
		
		/**
		 * Current Page.
		 */
		private IWorkbenchPage page;
		
		/**
		 * Display of the page. 
		 */
		private Display disp;

		public PropertyUpdateRunner(IWorkbenchPage page, Display disp) {
			super();
			this.page = page;
			this.disp = disp;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run(){
			disp.asyncExec(new Runnable () {
				
				/* (non-Javadoc)
				 * @see java.lang.Runnable#run()
				 */
				public void run () {
		
					ProgressMonitorDialog monitor = new ProgressMonitorDialog(page.getWorkbenchWindow().getShell());
					try {
						monitor.run(
								false, 	/* don't fork */ 
								true, 	/* can be canceled */
								new WorkspaceModifyOperation(){

								@Override
								protected void execute(	IProgressMonitor monitor) throws CoreException,	InvocationTargetException, InterruptedException {
									
									synchronized (new Object()){ /* to avoid java.util.ConcurrentModificationException*/

										/* Get editors have to be restarted */
										IEditorReference[] editors = page.getEditorReferences();									
										
										monitor.beginTask("Restart editors", editors.length + 1); /* number of open editors +  1 steps */
										
										Thread.sleep(1000); /* wait  a second. All property updates should be send */
										
										boolean showGraphInSeparateView = false;
										String s = CorePlugin.getDefault().getPreferenceStore().getString(CorePreferenceConstants.GRAPH_PANEL_LOCATION);
										if(s.equals(CorePreferenceConstants.GRAPH_PANEL_LOCATION_SEPARATE_VIEW)){
											showGraphInSeparateView = true;		
										}
	
										if(showGraphInSeparateView){
											try {
												page.showView(CoreConstants.CONTROL_FLOW_VIEW_ID);
											} catch (PartInitException e1) {
												SourcecodeVisualizerPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, SourcecodeVisualizerPlugin.PLUGIN_ID, e1.getMessage(), e1));
											}
										}
										
										/* first step restart view */
										monitor.worked(1);
										
										for(IEditorReference e : editors){
											
											/* check if canceled */
								              if(monitor.isCanceled()) {
								                  monitor.done();
								                  return;
								             }
											
											String id = e.getId();
											if(id.equals(editorID)){
												try {
													IEditorInput inp = e.getEditorInput();
													page.closeEditor(e.getEditor(false), false);	
													IDE.openEditor(page,  inp, id, true);
												} catch (PartInitException e1) {
													SourcecodeVisualizerPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, SourcecodeVisualizerPlugin.PLUGIN_ID, e1.getMessage(), e1));
												}
											}
								         
											monitor.worked(1);
										}					
										
										monitor.done();
										
									}/* synchronized */				
								}
							}
						);
					} catch (InvocationTargetException e2) {
						SourcecodeVisualizerPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, SourcecodeVisualizerPlugin.PLUGIN_ID, e2.getMessage(), e2));
					} catch (InterruptedException e2) {
						SourcecodeVisualizerPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, SourcecodeVisualizerPlugin.PLUGIN_ID, e2.getMessage(), e2));
					}
				}
			});

			setPropertyUpdateRuning(false);
		} /* run */
		
	}

}
