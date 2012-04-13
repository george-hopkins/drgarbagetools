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

package com.drgarbage.sourcecodevisualizer.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.drgarbage.core.img.CoreImg;
import com.drgarbage.sourcecodevisualizer.SourcecodeVisualizerMessages;

/**
 * Main Page of the Sourcecode Visualizer Preferences.
 *
 * @author Sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: SourcecodeVizualizerPreferences.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class SourcecodeVizualizerPreferences extends PreferencePage implements
		IWorkbenchPreferencePage {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(final Composite parent) {
		/* set links to the default preferences */
		Link link= new Link(parent, SWT.NONE);
		link.setText(SourcecodeVisualizerMessages.Sourcecodevisualizer_File_Associations_Link);
		link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PreferencesUtil.createPreferenceDialogOn(parent.getShell(), e.text, null, null); 
			}
		});
		
		return link;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		/* set dr-garbage logo */
		this.setImageDescriptor(CoreImg.aboutDrGarbageIcon_16x16);
	}
}