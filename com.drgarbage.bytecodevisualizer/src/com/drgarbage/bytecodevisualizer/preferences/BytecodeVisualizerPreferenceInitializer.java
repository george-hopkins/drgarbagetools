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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;

public class BytecodeVisualizerPreferenceInitializer extends
		AbstractPreferenceInitializer implements BytecodeVisualizerPreferenceConstats {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = BytecodeVisualizerPlugin.getDefault().getPreferenceStore();
				
		/* General */
		store.setDefault(GRAPH_PANEL_ATTR_RENDER_GRAPHS, true);
		
		store.setDefault(CLASS_FILE_ATTR_SHOW_CONSTANT_POOL, false);
		store.setDefault(CLASS_FILE_ATTR_SHOW_LINE_NUMBER_TABLE, false);
		store.setDefault(CLASS_FILE_ATTR_SHOW_VARIABLE_TABLE, false);
		store.setDefault(CLASS_FILE_ATTR_SHOW_EXCEPTION_TABLE, false);
		store.setDefault(CLASS_FILE_ATTR_SHOW_MAXS, false);
		
		store.setDefault(RETRIEVE_CLASS_FROM, RETRIEVE_CLASS_FROM_FILE_SYSTEM);
		store.setDefault(BRANCH_TARGET_ADDRESS_RENDERING, BRANCH_TARGET_ADDRESS_RELATIVE);
		
		store.setDefault(CLASS_FILE_ATTR_RENDER_TRYCATCH_BLOCKS, true);
		store.setDefault(CLASS_FILE_ATTR_SHOW_SOURCE_LINE_NUMBERS, true);
		
		store.setDefault(CLASS_FILE_ATTR_SHOW_MAXS, false);
		
		/* Syntax Highlighting */
		/* default is black */
		PreferenceConverter.setDefault(store, BYTECODE_MNEMONIC, new RGB(0, 0, 0));
		store.setDefault(BYTECODE_MNEMONIC_BOLD, true);
		store.setDefault(BYTECODE_MNEMONIC_ITALIC, false);
		store.setDefault(BYTECODE_MNEMONIC_STRIKETHROUGH, false);
		store.setDefault(BYTECODE_MNEMONIC_UNDERLINE, false);
		
		/* Source Code */
		store.setDefault(SHOW_TAB, SHOW_SOURCECODE_IF_AVALIABLE);

	}

}
