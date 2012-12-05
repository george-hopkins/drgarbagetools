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

package com.drgarbage.bytecodevisualizer.preferences;

import org.eclipse.ui.IWorkbenchPreferencePage;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.core.preferences.AbstractFieldPreferencePage;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: AbstractBytecodeVisualizerPreferencePage.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public abstract class AbstractBytecodeVisualizerPreferencePage extends AbstractFieldPreferencePage  implements IWorkbenchPreferencePage {

	public AbstractBytecodeVisualizerPreferencePage() {
		super(BytecodeVisualizerPlugin.getDefault().getPreferenceStore());
	}
}