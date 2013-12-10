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

package com.drgarbage.bytecodevisualizer;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.osgi.framework.internal.core.AbstractBundle;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.drgarbage.bytecodevisualizer.actions.DynamicPartsManager;
import com.drgarbage.bytecodevisualizer.preferences.BytecodeVisualizerPreferenceConstats;
import com.drgarbage.bytecodevisualizer.sourcelookup.SourceDisplayAdapterFactory;
import com.drgarbage.core.CoreConstants;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("restriction")
public class BytecodeVisualizerPlugin extends AbstractUIPlugin implements BytecodeVisualizerPreferenceConstats, IStartup {

	private static String ICONS_PATH = "img/"; //$NON-NLS-1$
	
	/* The shared instance */
	private static BytecodeVisualizerPlugin plugin;

	/* The plug-in ID */
	public static final String PLUGIN_ID = CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID;
	
	/* The plug-in version */
	public static String PLUGIN_VERSION;
	
	/**
	 * Returns a new error status for this plug-in with the given message
	 * @param message the message to be included in the status
	 * @param exception the exception to be included in the status or <code>null</code> if none
	 * @return a new error status
	 */
	public static IStatus createErrorStatus(String message, Throwable exception) {
		return new Status(IStatus.ERROR, PLUGIN_ID, CoreConstants.INTERNAL_ERROR, message, exception);
	}
	
	/**
	 * Returns a new warning status for this plug-in with the given message
	 * @param message the message to be included in the status
	 * @return a new warning status
	 */
	public static IStatus createWarningStatus(String message) {
		return new Status(IStatus.WARNING, PLUGIN_ID, CoreConstants.WARNING, message, null);
	}

	public static BytecodeVisualizerPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
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
		log(createErrorStatus("Error logged from Bytecode Visualizer: ", t)); //$NON-NLS-1$
	}

	/**
	 * Logs an internal error with the specified message.
	 * 
	 * @param message the error message to log
	 */
	public static void logErrorMessage(String message) {
		// this message is intentionally not internationalized, as an exception may
		// be due to the resource bundle itself
		log(createErrorStatus("Internal message logged from Bytecode Visualizer: " + message, null)); //$NON-NLS-1$
	}
	

	/**
	 * Debugger support object.
	 */
	private DebugSupport d = new DebugSupport();

	private DynamicPartsManager debugActionManager;

	/**
	 * The constructor
	 */
	public BytecodeVisualizerPlugin() {
		plugin = this;
	}

    /* (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		
		/* make sure the org.eclipse.debug.internal.ui.DebugUIPlugin gets loaded
		 * so that it registers its org.eclipse.core.runtime.adapters */
		org.eclipse.debug.internal.ui.DebugUIPlugin.getDefault();

		/* In the following, we want to overwrite one of the DebugUIPlugin's adapters */
		IAdapterManager manager= Platform.getAdapterManager();
		SourceDisplayAdapterFactory actionFactory = new SourceDisplayAdapterFactory();
		manager.registerAdapters(actionFactory, JDIStackFrame.class);

		debugActionManager = new DynamicPartsManager();

	}

	/**
	 * Returns the debugger support object.
	 * 
	 * @return d
	 */
	public DebugSupport getD() {
		return d;
	}

	protected void initializeImageRegistry(ImageRegistry registry) {
    	registerImage(BytecodeVisualizerConstants.IMG16E_STEP_OVER_SINGLE_INSTRUCTION, registry);
    	registerImage(BytecodeVisualizerConstants.IMG16E_STEP_INTO_BYTECODE, registry);
    	registerImage(BytecodeVisualizerConstants.IMG16E_TOGGLE_BREAKPOINT, registry);
    	registerImage(BytecodeVisualizerConstants.IMG16E_COMPARE_ACTION, registry);
    }
	
	private void registerImage(String path, ImageRegistry registry) {
		ImageDescriptor desc = ImageDescriptor.getMissingImageDescriptor();
		Bundle bundle = getBundle();
		URL url = null;
		if (bundle != null){
			url = FileLocator.find(bundle, new Path(ICONS_PATH + path), null);
			desc = ImageDescriptor.createFromURL(url);
		}
		registry.put(path, desc);
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
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		
		if (debugActionManager != null) {
			debugActionManager.dispose();
		}
		
		plugin = null;
		super.stop(context);
	}
}
