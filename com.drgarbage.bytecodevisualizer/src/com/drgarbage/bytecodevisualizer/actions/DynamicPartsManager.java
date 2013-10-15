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

package com.drgarbage.bytecodevisualizer.actions;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener4;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerConstants;
import com.drgarbage.core.CoreConstants;
import com.drgarbage.core.CorePlugin;
import com.drgarbage.core.preferences.CorePreferenceConstants;

public class DynamicPartsManager {
	
	private static class DebugActionContributionItem extends ActionContributionItem {

		public DebugActionContributionItem(IAction action) {
			super(action);
		}

		@Override
		public boolean isVisible() {
			return true;
		}
		
	}
	
	private static void addActions(IDebugView viewPart) {		
		IToolBarManager tbm = viewPart.getViewSite().getActionBars().getToolBarManager();
		
		/* FIX: bug, adding of actions */
		IContributionItem ci = tbm.find(BytecodeVisualizerConstants.BYTECODE_VISUALIZER_GROUP);
		if(ci != null){
			return;
		}
		
		tbm.add(new Separator(BytecodeVisualizerConstants.BYTECODE_VISUALIZER_GROUP));
        tbm.appendToGroup(
        		BytecodeVisualizerConstants.BYTECODE_VISUALIZER_GROUP, 
        		new DebugActionContributionItem(new StepIntoBytecodeAction(viewPart)));
        tbm.appendToGroup(
        		BytecodeVisualizerConstants.BYTECODE_VISUALIZER_GROUP, 
        		new DebugActionContributionItem(new StepOverSingleInstructionAction(viewPart)));
        viewPart.getViewSite().getActionBars().updateActionBars();
        
        tbm.appendToGroup(
        		BytecodeVisualizerConstants.BYTECODE_VISUALIZER_GROUP, 
        		new DebugActionContributionItem(new GetClassesFromJVMAction(viewPart)));
        viewPart.getViewSite().getActionBars().updateActionBars();

	}
	
	private static void removeActions(IDebugView viewPart) {
		
		IToolBarManager tbm = viewPart.getViewSite().getActionBars().getToolBarManager();
		tbm.remove(CoreConstants.ACTION_STEP_OVER_SINGLE_INSTRUCTION);
        tbm.remove(CoreConstants.ACTION_STEP_INTO_BYTECODE);
        tbm.remove(GetClassesFromJVMAction.ACTION_READ_CLASSES_FROM_JVM);
//		tbm.remove(id)
//		(new Separator(BytecodeVisualizerCommonConstants.BYTECODE_VISUALIZER_GROUP));
//        tbm.appendToGroup(BytecodeVisualizerCommonConstants.BYTECODE_VISUALIZER_GROUP, new RefreshAction(viewPart.getSite().getWorkbenchWindow()));
//        viewPart.getViewSite().getActionBars().updateActionBars();

	}
	
	private IPerspectiveListener4 perspectiveListener = new IPerspectiveListener4 () {

		public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
			IDebugView debugView = (IDebugView)page.findView(IDebugUIConstants.ID_DEBUG_VIEW);
			if (debugView != null) {
				addActions(debugView);
			}
		}

		public void perspectiveChanged(IWorkbenchPage page,
				IPerspectiveDescriptor perspective,
				IWorkbenchPartReference partRef, String changeId) {
			if (IWorkbenchPage.CHANGE_VIEW_SHOW.equals(changeId)) {
				if (IDebugUIConstants.ID_DEBUG_VIEW.equals(partRef.getId())) {
					IDebugView wp = (IDebugView)partRef.getPart(false);
					addActions(wp);
				}
			}
			else if (IWorkbenchPage.CHANGE_VIEW_HIDE.equals(changeId)) {
				if (IDebugUIConstants.ID_DEBUG_VIEW.equals(partRef.getId())) {
					IDebugView wp = (IDebugView)partRef.getPart(false);
					removeActions(wp);
				}
			}
			else if (IWorkbenchPage.CHANGE_EDITOR_OPEN.equals(changeId)) {
				if (CoreConstants.BYTECODE_VISUALIZER_EDITOR_ID.equals(partRef.getId())) {
					/* Open the graph view if necessary */
					String s = CorePlugin.getDefault().getPreferenceStore().getString(CorePreferenceConstants.GRAPH_PANEL_LOCATION);
					if (CorePreferenceConstants.GRAPH_PANEL_LOCATION_SEPARATE_VIEW.equals(s)) {
						CorePlugin.ensureGraphViewShown(page);
					}
				}
			}
			
		}

		public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
		}

		public void perspectiveClosed(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		}

		public void perspectiveDeactivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
			IDebugView debugView = (IDebugView)page.findView(IDebugUIConstants.ID_DEBUG_VIEW);
			if (debugView != null) {
				removeActions(debugView);
			}
		}

		public void perspectiveOpened(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		}

		public void perspectiveSavedAs(IWorkbenchPage page,
				IPerspectiveDescriptor oldPerspective,
				IPerspectiveDescriptor newPerspective) {
		}

		public void perspectivePreDeactivate(IWorkbenchPage page,
				IPerspectiveDescriptor perspective) {
		}
		
	};
	private IWindowListener windowListener = new IWindowListener() {

		public void windowActivated(IWorkbenchWindow window) {
		}

		public void windowClosed(IWorkbenchWindow window) {
			window.removePerspectiveListener(perspectiveListener);
		}

		public void windowDeactivated(IWorkbenchWindow window) {
		}

		public void windowOpened(IWorkbenchWindow window) {
			window.addPerspectiveListener(perspectiveListener);
		}
		
	};
	public DynamicPartsManager() {
		super();
		setup();
	}
	public void dispose() {
		
		final IWorkbench workbench = PlatformUI.getWorkbench();
		Display display = workbench.getDisplay();
		if (display != null && ! display.isDisposed()) {
			display.asyncExec(new Runnable() {
				public void run() {
					
					/* remove window listener from all existing windows */
					try {
						IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
						if (windows != null) {
							for (IWorkbenchWindow window : windows) {
								try {
									IWorkbenchPage[] pages = window.getPages();
									for (IWorkbenchPage page : pages) {
										IDebugView debugView = (IDebugView)page.findView(IDebugUIConstants.ID_DEBUG_VIEW);
										if (debugView != null) {
											removeActions(debugView);
										}
									}
									windowListener.windowClosed(window);
								} catch (Exception ignored) {
								}
							}
						}
					} catch (Exception ignored) {
					}

					/* on all future windows */
					try {
						workbench.removeWindowListener(windowListener);
					} catch (Exception ignored) {
					}

				}
			});
		}
	}
	
	private void setup() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				
				/* on all future windows */
				workbench.addWindowListener(windowListener);
				
				/* on all existing windows */
				IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
				if (windows != null) {
					for (IWorkbenchWindow window : windows) {
						IWorkbenchPage[] pages = window.getPages();
						for (IWorkbenchPage page : pages) {
							IDebugView debugView = (IDebugView)page.findView(IDebugUIConstants.ID_DEBUG_VIEW);
							if (debugView != null) {
								addActions(debugView);
							}
						}
						window.addPerspectiveListener(perspectiveListener);
					}
				}
				
			}
		});
	}
	
}
