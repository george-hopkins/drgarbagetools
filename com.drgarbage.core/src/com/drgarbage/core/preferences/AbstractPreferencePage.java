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

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.drgarbage.core.img.CoreImg;

/**
 * Common parent for the most of Dr. Garbage preference pages. Notably 
 * it sets the Gr. Garbage logo as image descriptor.
 * 
 * @author Peter Palaga
 * @version $Revision: 1523 $
 * $Id: AbstractPreferencePage.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 *
 */
public abstract class AbstractPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public AbstractPreferencePage() {
		this("");
	}

	public AbstractPreferencePage(String title) {
		super(title, CoreImg.aboutDrGarbageIcon_16x16);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}
