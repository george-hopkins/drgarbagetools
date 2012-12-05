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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;

import com.drgarbage.core.CorePlugin;

/**
 * Class used to initialize default preference values.
 * 
 * @author Peter Palaga
 * @version $Revision$
 * $Id: PreferenceInitializer.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer implements CorePreferenceConstants {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();
		
		store.setDefault(CorePreferenceConstants.GRAPH_PANEL_LOCATION, CorePreferenceConstants.GRAPH_PANEL_LOCATION_EDITOR);
		
		PreferenceConverter.setDefault(store, CorePreferenceConstants.INSTRUCTION_BGCOLOR, CorePreferenceConstants.DEFAULT_INSTRUCTION_BGCOLOR);
		PreferenceConverter.setDefault(store, CorePreferenceConstants.BASIC_BLOCK_BGCOLOR, CorePreferenceConstants.DEFAULT_BASIC_BLOCK_BGCOLOR);

		PreferenceConverter.setDefault(store, CorePreferenceConstants.GET_VERTEX_BGCOLOR, CorePreferenceConstants.DEFAULT_GET_VERTEX_BGCOLOR);
		PreferenceConverter.setDefault(store, CorePreferenceConstants.INVOKE_VERTEX_BGCOLOR, CorePreferenceConstants.DEFAULT_INVOKE_VERTEX_BGCOLOR);
		PreferenceConverter.setDefault(store, CorePreferenceConstants.SWITCH_VERTEX_BGCOLOR, CorePreferenceConstants.DEFAULT_SWITCH_VERTEX_BGCOLOR);
		PreferenceConverter.setDefault(store, CorePreferenceConstants.DECISION_VERTEX_BGCOLOR, CorePreferenceConstants.DEFAULT_DECISION_VERTEX_BGCOLOR);
		PreferenceConverter.setDefault(store, CorePreferenceConstants.GOTO_JUMP_VERTEX_BGCOLOR, CorePreferenceConstants.DEFAULT_GOTO_JUMP_VERTEX_BGCOLOR);
		PreferenceConverter.setDefault(store, CorePreferenceConstants.RETURN_VERTEX_BGCOLOR, CorePreferenceConstants.DEFAULT_RETURN_VERTEX_BGCOLOR);
		PreferenceConverter.setDefault(store, CorePreferenceConstants.ENTRY_END_BGCOLOR, CorePreferenceConstants.DEFAULT_ENTRY_END_BGCOLOR);
		PreferenceConverter.setDefault(store, CorePreferenceConstants.COMMENT_BGCOLOR, CorePreferenceConstants.DEFAULT_COMMENT_BGCOLOR);
	}
}
