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

package com.drgarbage.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

import com.drgarbage.utils.Messages;
import com.drgarbage.utils.WebUtils;

/**
 * The plug-in class (singleton).
 * <p>
 * This instance can be shared between all extensions in the plug-in. Information
 * shared between extensions can be persisted by using the PreferenceStore.
 * </p>
 * @see org.eclipse.ui.plugin.AbstractUIPlugin#getPreferenceStore()
 *
 * @author Sergej Alekseev and Peter Palaga
 * @version $Revision$
 * $Id$
 */
public class CorePlugin extends AbstractUIPlugin {
	
	/**
	 * Table of interface objects for external communication. 
	 */
	private static Map<String, IExternalCommunication> externalCommunicationTable = new TreeMap<String, IExternalCommunication>();
	
	/** The plug-in ID */
	public static final String PLUGIN_ID = CoreConstants.CORE_PLUGIN_ID;

	/**
	 * Single plug-in instance. 
	 */
	private static CorePlugin singleton;

	/**
	 * Returns a new error status for this plug-in with the given message
	 * @param message the message to be included in the status
	 * @param exception the exception to be included in the status or <code>null</code> if none
	 * @return a new error status
	 */
	public static IStatus createErrorStatus(String message, Throwable exception) {
		return new Status(IStatus.ERROR, PLUGIN_ID, message, exception);
	}
	
	public static IStatus createWarningStatus(String message) {
		return new Status(IStatus.WARNING, PLUGIN_ID, message);
	}
	
	public static IStatus createInfoStatus(String message) {
		return new Status(IStatus.INFO, PLUGIN_ID, message);
	}

	/**
	 * Returns the shared plugin instance.
	 */
	public static CorePlugin getDefault() {
		return singleton;
	}
	

	/**
	 * Returns plugin instance from plugin registry.
	 * @param key
	 * @return plugin
	 */
	public static AbstractUIPlugin getPluginFromRegistry(String key) {
		Bundle b = Platform.getBundle(key);
		if(b == null){
			return null;
		}
		
		if(b.getState() != Bundle.ACTIVE){
			try { /*start bundle if not yet activated */
				b.start();
			} catch (BundleException e) {
				getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
				return null;
			}
		} 

    	
    	/* Get bundle singleton activator instance */
    	String activator = (String)b.getHeaders().get(Constants.BUNDLE_ACTIVATOR);
    	try {
    		Class<?> activatorClass = b.loadClass(activator);
    		Method method = activatorClass.getMethod("getDefault");
    		Object activatorInstance = method.invoke(null);
    		return (AbstractUIPlugin)activatorInstance;
    	} catch (ClassNotFoundException e) {
			getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
		} catch (SecurityException e) {
			getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
		} catch (NoSuchMethodException e) {
			getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
		} catch (IllegalArgumentException e) {
			getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
		} catch (IllegalAccessException e) {
			getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
		} catch (InvocationTargetException e) {
			getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
		}
		
		return null;
	}

	/**
	 * Logs the specified status with this plug-in's log.
	 * 
	 * @param status status to log
	 */
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * Logs the specified throwable with this plug-in's log.
	 * 
	 * @param t throwable to log
	 */
	public static void log(Throwable t) {
		log(createErrorStatus("Error logged from Dr. Garbage Core Plugin: ", t)); //$NON-NLS-1$
	}
	
	/**
	 * Logs an internal error with the specified message.
	 * 
	 * @param message the error message to log
	 */
	public static void logErrorMessage(String message) {
		// this message is intentionally not internationalized, as an exception may
		// be due to the resource bundle itself
		log(createErrorStatus("Internal message logged from Dr. Garbage Core Plugin: " + message, null)); //$NON-NLS-1$
	}
	
	/**
	 * Looks if the view <code>{@value CoreConstants#CONTROL_FLOW_VIEW_ID}</code> is open in 
	 * the active window and page and if not opens it.
	 */
	public static void ensureGraphViewShown() {
		
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				ensureGraphViewShown(page);
			}
		}
	}
	
	public static void ensureGraphViewShown(IWorkbenchPage page) {
		IViewReference vr = page.findViewReference(CoreConstants.CONTROL_FLOW_VIEW_ID);
		if (vr == null) {
			try {
				page.showView(CoreConstants.CONTROL_FLOW_VIEW_ID);
			} catch (PartInitException e) {
				log(e.getStatus());
	            MessageDialog.openError(page.getWorkbenchWindow().getShell(), CoreMessages.Error, e.getStatus().getMessage());
			}
		}
	}

	
	/**
	 * Reopens all opened editors with specified ids.
	 * 
	 * @param ids Array of editor ids which should be reopened.
	 */
	public static void reopenEditors(final String[] ids) {
		Display display = PlatformUI.getWorkbench().getDisplay();
		if (display != null && !display.isDisposed()) {
			display.asyncExec(new Runnable() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Runnable#run()
				 */
				public void run() {

					/* all windows */
					IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
					if (windows != null && windows.length > 0) {
						for (IWorkbenchWindow w : windows) {
							IWorkbenchPage page = w.getActivePage();
							IEditorReference[] editorReferences = page.getEditorReferences();
							if (editorReferences != null && editorReferences.length > 0) {
								for (IEditorReference er : editorReferences) {
									
									String editorId = null;
									for (String id : ids) {
										if (id.equals(er.getId())) {
											editorId = id;
											break;
										}
									}
									if (editorId != null) {
										try {
											IEditorPart editor = er.getEditor(false);
											IEditorInput input = er.getEditorInput();
											
											/* the following just resets the editor input
											 * but it does not rebuild the WorkbenchPart */
											/*
											if (editor instanceof IReusableEditor) {
												IReusableEditor reusableEditor = (IReusableEditor) editor;
												page.reuseEditor(reusableEditor, input);
											}
                                            */
											
											/* this is the stronger variant of the above */
											page.closeEditor(editor, false);
											page.openEditor(input, editorId, false);

										} catch (PartInitException e) {
											log(e.getStatus());
								            MessageDialog.openError(w.getShell(), CoreMessages.Error, e.getStatus().getMessage());
										}
									}
								}
							}
						}
					}
				}
			});			
		}
	}
	
	private Listener linkListener = new Listener () {
		public void handleEvent(Event event) {
			navigatePath(event.text);
		}
	};
    
	/** 
	 * The constructor. 
	 */
	public CorePlugin() {
		if (singleton == null) {
			singleton = this;
		}
	}

	/**
	 * Adds an external communication interface object.
	 * @param key
	 * @param communicationObject
	 */
	public void addExternalComunicationObject(String key, IExternalCommunication communicationObject) {
		externalCommunicationTable.put(key, communicationObject);
	}

	/**
	 * Returns the external communication object for the given key.
	 * @return the external communication object
	 * or <code>null</code> if no object for this key exists.
	 */
	public IExternalCommunication getExternalComunicationObject(String key) {
		return externalCommunicationTable.get(key);
	}
	
	public Listener getLinkListener() {
		return linkListener;
	}
	
	public void navigatePath(String path) {
		String urlBase = Platform.getResourceString(CorePlugin.getDefault().getBundle(), "%"+ CoreConstants.providerWww);
		WebUtils.openLink(urlBase + path);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		singleton = null;
		super.stop(context);
	}
	
	/**
	 * Returns the communication object or <code>null</code> 
	 * if the communication is not possible.
	 * 
	 * @return communication object or <code>null</code>
	 * 
	 * @see IExternalCommunication
	 */
	public static IExternalCommunication getExternalCommunication(){
		IExternalCommunication comunicationObject = CorePlugin.getDefault()
    			.getExternalComunicationObject(CoreConstants.CONTROL_FLOW_GRAPH_FACTORY_PLUGIN_ID);
    	
    	if(comunicationObject == null){
    		/* activate target plugin */
        	Bundle b = Platform.getBundle(CoreConstants.CONTROL_FLOW_GRAPH_FACTORY_PLUGIN_ID);
        	if(b != null){
        		if(b.getState() != Bundle.ACTIVE){
        			try {
        				b.start();
        			} catch (BundleException e) {
        				e.printStackTrace(System.err);
        			}
        		}  		

        		/* get communication object again*/
        		comunicationObject = CorePlugin.getDefault()
        				.getExternalComunicationObject(CoreConstants.CONTROL_FLOW_GRAPH_FACTORY_PLUGIN_ID);
        		
        		return comunicationObject;
        	}
        	String msg = CoreMessages.ERROR_Starting_CFGF_failed
        			+ '\n'
        			+ CoreMessages.ERROR_CFGF_is_not_installed;
        	CorePlugin.log(createWarningStatus(msg ));
        	return null;
    	}
    	
    	return comunicationObject;
	}
	
}