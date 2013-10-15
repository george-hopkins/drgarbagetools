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

import java.text.MessageFormat;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.bytecodevisualizer.preferences.BytecodeVisualizerPreferenceConstats;

/**
 * The message dialog for selecting the debug target.
 * 
 * @author Peter Palaga
 * @version $Revision$
 * $Id$
 */
public class SelectDebugTargetDialog extends MessageDialog {

    private static final String DIALOG_HEIGHT = "DIALOG_HEIGHT"; //$NON-NLS-1$
    private static final String DIALOG_ORIGIN_X = "DIALOG_X_ORIGIN"; //$NON-NLS-1$
    private static final String DIALOG_ORIGIN_Y = "DIALOG_Y_ORIGIN"; //$NON-NLS-1$
    private static final String DIALOG_SETTINGS_SECTION = "SelectDebugTargetDialogSettings"; //$NON-NLS-1$
    private static final String DIALOG_WIDTH = "DIALOG_WIDTH"; //$NON-NLS-1$
    
    public static final int INVALID_INDEX = -1;

    private ArrayList<IJavaType[]> displayCandidates;
    
    private IJavaType[] selection;
    
    private TableViewer tableViewer;
    
	/**
     * Creates a new clean dialog.
     * 
     * @param window the window to create it in
     * @param selection the currently selected projects (may be empty)
     */
    public SelectDebugTargetDialog(String className, ArrayList<IJavaType[]> displayCandidates) {
        super(
                null,
                BytecodeVisualizerMessages.SelectDebugTargetDialog_title, 
                null, 
                MessageFormat.format(BytecodeVisualizerMessages.SelectDebugTargetDialog_text, new Object[]{className}), 
                NONE, 
                new String[] {
                	IDialogConstants.OK_LABEL, 
                	BytecodeVisualizerMessages.SelectDebugTargetDialog_act_Filesystem
                },
                0);
        this.displayCandidates = displayCandidates;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#close()
     */
    public boolean close() {
        persistDialogSettings(getShell(), DIALOG_SETTINGS_SECTION);
        return super.close();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createCustomArea(Composite parent) {
        Composite area = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = layout.marginHeight = 0;
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = true;
        area.setLayout(layout);
        area.setLayoutData(new GridData(GridData.FILL_BOTH));

        //second row
        createDebugTargetSelectionTable(area);
        
        
        //third row
        //only prompt for immediate build if autobuild is off
        
        Label lbl = new Label(area, SWT.NONE);
        lbl.setText(
        		MessageFormat.format(
        				BytecodeVisualizerMessages.SelectDebugTargetDialog_text_fileSystemFallback,
        				new Object[] {BytecodeVisualizerMessages.SelectDebugTargetDialog_act_Filesystem}
    				)
    				);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 2;
        lbl.setLayoutData(data);

        Link link = new Link(area, SWT.NONE);
        link.setText(BytecodeVisualizerMessages.SelectDebugTargetDialog_link_adjustPreferences);
        data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 2;
        link.setLayoutData(data);
        link.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				PreferenceDialog prefs = PreferencesUtil.createPreferenceDialogOn(null, BytecodeVisualizerPreferenceConstats.BYTECODE_VISUALIZER_GENERAL_PREFERENCE_PAGE_ID, null, null);
				prefs.open();
			}
        	
        });

        return area;
    }

    private void createDebugTargetSelectionTable(Composite parent) {
        tableViewer = new TableViewer(parent, SWT.BORDER);
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				buttonPressed(OK);
			}
        	
        });
        
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				updateEnablement();
			}
        	
        });
        
        tableViewer.setContentProvider(new ArrayContentProvider());
//        vmsTableViewer.setLabelProvider(new DefaultLabelProvider());
        tableViewer.setLabelProvider(new ITableLabelProvider() {

			public void addListener(ILabelProviderListener listener) {
			}
			public void dispose() {
			}
			
			public Image getColumnImage(Object element, int columnIndex) {
				if (columnIndex == 0 && element instanceof IJavaType[]) {
					IJavaType[] jts = (IJavaType[]) element;
					if (jts.length > 0) {
						IJavaType firstType = jts[0];
						// determine the image from the launch config type
						String imageKey = getImageKey(firstType.getDebugTarget().getLaunch());
						
						if (imageKey != null) {
							return DebugPluginImages.getImage(imageKey);
						}
					}
				}

				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				
				if (element instanceof IJavaType[]) {
					IJavaType[] jts = (IJavaType[]) element;
					if (jts.length > 0) {
						IJavaType firstType = jts[0];
						try {
							return firstType.getDebugTarget().getName();
						} catch (DebugException e) {
						}
					}
				}
				return null;
			}

			private String getImageKey(ILaunch launch) {
				ILaunchConfiguration configuration = launch.getLaunchConfiguration();
				if (configuration != null) {
					try {
						return configuration.getType().getIdentifier();
					} catch (CoreException e) {
						BytecodeVisualizerPlugin.log(e);
						return null;
					}
				}
				// if no config, use the old "mode" way
				if (launch.getLaunchMode().equals(ILaunchManager.DEBUG_MODE)) {
					return IDebugUIConstants.IMG_OBJS_LAUNCH_DEBUG;
				} else if (launch.isTerminated()) {
					return IDebugUIConstants.IMG_OBJS_LAUNCH_RUN_TERMINATED;
				} else {
					return IDebugUIConstants.IMG_OBJS_LAUNCH_RUN;
				}	
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
			}
        	
        });
        tableViewer.addFilter(new ViewerFilter() {
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                return true;
            }
        });
        tableViewer.setInput(displayCandidates);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 2;
        data.widthHint = IDialogConstants.ENTRY_FIELD_WIDTH;
        data.heightHint = IDialogConstants.ENTRY_FIELD_WIDTH;
        tableViewer.getTable().setLayoutData(data);
        
    }
    
    private IDialogSettings getDialogSettings(String dialogSettingsSectionName) {
        IDialogSettings settings = BytecodeVisualizerPlugin.getDefault().getDialogSettings();
        IDialogSettings section = settings.getSection(dialogSettingsSectionName);
        if (section == null) {
            section = settings.addNewSection(dialogSettingsSectionName);
        } 
        return section;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#getInitialLocation(org.eclipse.swt.graphics.Point)
     */
    protected Point getInitialLocation(Point initialSize) {
        Point p = getInitialLocation(DIALOG_SETTINGS_SECTION);
        return p != null ? p : super.getInitialLocation(initialSize);
    }
    
    /**
     * Returns the initial location which is persisted in the IDE Plugin dialog settings
     * under the provided dialog setttings section name.
     * If location is not persisted in the settings, the <code>null</code> is returned. 
     * 
     * @param dialogSettingsSectionName The name of the dialog settings section
     * @return The initial location or <code>null</code>
     */
    public Point getInitialLocation(String dialogSettingsSectionName) {
        IDialogSettings settings = getDialogSettings(dialogSettingsSectionName);
        try {
            int x= settings.getInt(DIALOG_ORIGIN_X);
            int y= settings.getInt(DIALOG_ORIGIN_Y);
            return new Point(x,y);
        } catch (NumberFormatException e) {
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#getInitialSize()
     */
    protected Point getInitialSize() {
        Point p = super.getInitialSize();
        return getInitialSize(DIALOG_SETTINGS_SECTION, p);
    }
    
    /**
     * Returns the initial size which is the larger of the <code>initialSize</code> or
     * the size persisted in the IDE UI Plugin dialog settings under the provided dialog setttings section name.
     * If no size is persisted in the settings, the <code>initialSize</code> is returned. 
     * 
     * @param initialSize The initialSize to compare against
     * @param dialogSettingsSectionName The name of the dialog settings section
     * @return the initial size
     */
    private Point getInitialSize(String dialogSettingsSectionName, Point initialSize) {
        IDialogSettings settings = getDialogSettings(dialogSettingsSectionName);
        try {
            int x, y;
            x = settings.getInt(DIALOG_WIDTH);
            y = settings.getInt(DIALOG_HEIGHT);
            return new Point(Math.max(x, initialSize.x), Math.max(y, initialSize.y));
        } catch (NumberFormatException e) {
        }
        return initialSize;
    }
    public IJavaType[] getSelection() {
    	return selection;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#isResizable()
     */
    protected boolean isResizable() {
        return true;
    }

    /**
     * Persists the location and dimensions of the shell and other user settings in the
     * plugin's dialog settings under the provided dialog settings section name
     * 
     * @param shell The shell whose geometry is to be stored
     * @param dialogSettingsSectionName The name of the dialog settings section
     */
    private void persistDialogSettings(Shell shell, String dialogSettingsSectionName) {
        Point shellLocation = shell.getLocation();
        Point shellSize = shell.getSize();
        IDialogSettings settings = getDialogSettings(dialogSettingsSectionName);
        settings.put(DIALOG_ORIGIN_X, shellLocation.x);
        settings.put(DIALOG_ORIGIN_Y, shellLocation.y);
        settings.put(DIALOG_WIDTH, shellSize.x);
        settings.put(DIALOG_HEIGHT, shellSize.y);

    }
    
    /**
     * Updates the enablement of the dialog's ok button based
     * on the current choices in the dialog.
     */
    protected void updateEnablement() {
    	
    	Object newSel = null;
    	if (displayCandidates != null) {
    		if (tableViewer != null) {
				IStructuredSelection sel = (IStructuredSelection)tableViewer.getSelection();
				if (sel != null && !sel.isEmpty()) {
					newSel = sel.getFirstElement();
				}
    		}
    	}
    	
    	if (newSel instanceof IJavaType[]) {
    		selection = (IJavaType[]) newSel;
    	}
    	else {
    		selection = null;
    	}

        boolean enabled = selection != null;
        getButton(OK).setEnabled(enabled);
    }
}
