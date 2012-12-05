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

package com.drgarbage.core.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.drgarbage.core.img.CoreImg;

/**
 * Common parent for the most of Dr. Garbage preference pages. Notably 
 * it sets the Gr. Garbage logo as image descriptor.
 * 
 * @author Peter Palaga
 * @version $Revision$
 * $Id: AbstractFieldPreferencePage.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 *
 */
public abstract class AbstractFieldPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	protected int fieldIndex = 0;
	
	
	protected List<FieldEditor> fieldList = new ArrayList<FieldEditor>(8);

	public AbstractFieldPreferencePage(int style, IPreferenceStore preferenceStore) {
		this("", style, preferenceStore);
	}

	/**
	 * Creates a new {@link AbstractFieldPreferencePage} with empty string
	 * title, Gr. Garbage logo as image descriptor and FLAT style. 
	 */
	public AbstractFieldPreferencePage(IPreferenceStore preferenceStore) {
		this("", CoreImg.aboutDrGarbageIcon_16x16, FLAT, preferenceStore);
	}

	public AbstractFieldPreferencePage(String title, ImageDescriptor image,
			int style, IPreferenceStore preferenceStore) {
		super(title, image, style);
		setPreferenceStore(preferenceStore);
	}

	public AbstractFieldPreferencePage(String title, int style, IPreferenceStore preferenceStore) {
		this(title, CoreImg.aboutDrGarbageIcon_16x16, style, preferenceStore);
	}
	
	@Override
	protected void addField(FieldEditor editor) {
		super.addField(editor);
		fieldList.add(editor);
	}

	@Override
	public void dispose() {
		super.dispose();
		fieldList = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
	protected EventGroupingListener createEventGroupingListener() {
		return new EventGroupingListener();
	}
	
	protected class EventGroupingListener implements IPropertyChangeListener {
		private boolean propertyChanged = false;
		public boolean isSomePropertyChanged() {
			return propertyChanged;
		}
		public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
			if (!propertyChanged) {
				for (FieldEditor ed : fieldList) {
					if (event.getProperty().equals(ed.getPreferenceName())) {
						propertyChanged = true;
						break;
					}
				}
			}
		}
	}
	
	public void addVerticalSpace() {
		addField(new LabelField("Dummy"+ fieldIndex++, "", getFieldEditorParent()));
	}

}
