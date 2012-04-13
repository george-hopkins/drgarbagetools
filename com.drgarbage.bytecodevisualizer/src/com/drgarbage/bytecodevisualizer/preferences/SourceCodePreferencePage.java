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

import org.eclipse.jface.preference.RadioGroupFieldEditor;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;

/**
 * An empty wrapper class. It necessary for plug-in activation. 
 * 
 * @author Sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: SourceCodePreferencePage.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class SourceCodePreferencePage extends
		AbstractBytecodeVisualizerPreferencePage
		implements BytecodeVisualizerPreferenceConstats {

	/**
	 * Default Constructor.
	 */
	public SourceCodePreferencePage() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	public void createFieldEditors() {
		RadioGroupFieldEditor reditF = new RadioGroupFieldEditor(SHOW_TAB,
				BytecodeVisualizerMessages.SourceCodePreferencePage_radioGroup_Which_editor_tab_should_be_active + ":", 
	    		1,
	            new String[][] { 
	    			{BytecodeVisualizerMessages.SourceCodePreferencePage_radio_Source_code_if_available, SHOW_SOURCECODE_IF_AVALIABLE},
	    			{BytecodeVisualizerMessages.SourceCodePreferencePage_radio_Always_source_code, SHOW_ALWAYS_SOURCECODE_TAB },
	    			{BytecodeVisualizerMessages.SourceCodePreferencePage_radio_Always_bytecode, SHOW_ALWAYS_BYTECODE_TAB }
	    			}, 
	    		getFieldEditorParent());
		addField(reditF);
	}
}
