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

import java.text.MessageFormat;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.core.CoreConstants;
import com.drgarbage.core.CorePlugin;
import com.drgarbage.core.Links;
import com.drgarbage.core.preferences.HttpPathLinkField;
import com.drgarbage.core.preferences.IndentBooleanFieldEditor;
import com.drgarbage.core.preferences.LabelField;

/**
 * An empty wrapper class. It necessary for plug-in activation. 
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class GeneralPreferencePage
		extends
		AbstractBytecodeVisualizerPreferencePage
		implements BytecodeVisualizerPreferenceConstats {

	/**
	 * Default constructor.
	 */
	public GeneralPreferencePage() {
		super();
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	protected void createFieldEditors() {
		
		LabelField fld = new LabelField("Lbl"+ fieldIndex++, BytecodeVisualizerMessages.GeneralPreferencePage_chk_Graph_Panel, getFieldEditorParent());
		addField(fld);
		
		BooleanFieldEditor ed = new IndentBooleanFieldEditor(GRAPH_PANEL_ATTR_RENDER_GRAPHS, BytecodeVisualizerMessages.GeneralPreferencePage_chk_Render_Graphs, getFieldEditorParent());
		addField(ed);
		
		addVerticalSpace();
		
		fld = new LabelField("Lbl"+ fieldIndex++, BytecodeVisualizerMessages.GeneralPreferencePage_lbl_Select_class_file_parts, getFieldEditorParent());
		addField(fld);
		
		ed = new IndentBooleanFieldEditor(CLASS_FILE_ATTR_SHOW_CONSTANT_POOL, BytecodeVisualizerMessages.GeneralPreferencePage_chk_Show_Constant_Pool, getFieldEditorParent());
		addField(ed);
		
		ed = new IndentBooleanFieldEditor(CLASS_FILE_ATTR_SHOW_SOURCE_LINE_NUMBERS, BytecodeVisualizerMessages.GeneralPreferencePage_chk_Show_source_line_numbers, getFieldEditorParent());
		addField(ed);
		
		ed = new IndentBooleanFieldEditor(CLASS_FILE_ATTR_SHOW_LINE_NUMBER_TABLE, BytecodeVisualizerMessages.GeneralPreferencePage_chk_Show_Line_Number_Table, getFieldEditorParent());
		addField(ed);
		
		ed= new IndentBooleanFieldEditor(CLASS_FILE_ATTR_SHOW_VARIABLE_TABLE, BytecodeVisualizerMessages.GeneralPreferencePage_chk_Show_Local_Variable_Table, getFieldEditorParent());
		addField(ed);

		ed= new IndentBooleanFieldEditor(CLASS_FILE_ATTR_SHOW_MAXS, BytecodeVisualizerMessages.GeneralPreferencePage_chk_Show_Maxs, getFieldEditorParent());
		addField(ed);
		
		ed= new IndentBooleanFieldEditor(CLASS_FILE_ATTR_RENDER_TRYCATCH_BLOCKS, BytecodeVisualizerMessages.GeneralPreferencePage_chk_Render_try_catch_blocks, getFieldEditorParent());
		addField(ed);

		addVerticalSpace();
		
		RadioGroupFieldEditor red = new RadioGroupFieldEditor(BRANCH_TARGET_ADDRESS_RENDERING,
	    		BytecodeVisualizerMessages.GeneralPreferencePage_radioGroup_Branch_Target_Address_Rendering + ":", 
	    		1,
	            new String[][] { 
	    			{BytecodeVisualizerMessages.GeneralPreferencePage_radio_Relative, BRANCH_TARGET_ADDRESS_RELATIVE},
	    			{BytecodeVisualizerMessages.GeneralPreferencePage_radio_Absolute, BRANCH_TARGET_ADDRESS_ABSOLUTE }
	    			}, 
	    		getFieldEditorParent());
		addField(red);

		addVerticalSpace();
		
		/* Take from options */
		red = new RadioGroupFieldEditor(RETRIEVE_CLASS_FROM,
	    		BytecodeVisualizerMessages.GeneralPreferencePage_radioGroup_Retrieve_class_from + ":", 
	    		1,
	            new String[][] { 
	    			{BytecodeVisualizerMessages.GeneralPreferencePage_radio_File_System, RETRIEVE_CLASS_FROM_FILE_SYSTEM},
	    			{BytecodeVisualizerMessages.GeneralPreferencePage_radio_JDI, RETRIEVE_CLASS_FROM_JVM_JDI }
	    			}, 
	    		getFieldEditorParent());
		addField(red);
		
		String ln = MessageFormat.format(
				BytecodeVisualizerMessages.GeneralPreferencePage_link_JDI_limitations, 
				new Object[] {Links.JDI_LIMITATIONS}
				);
		addField(new HttpPathLinkField("Lbl2", ln, getFieldEditorParent()));
	}

	@Override
	public boolean performOk() {
		EventGroupingListener eventGroupingListener = createEventGroupingListener();
		IPreferenceStore store = getPreferenceStore();
		store.addPropertyChangeListener(eventGroupingListener);
		
		/* The following line eventually fires several Property Change Events.
		 * EventGroupingListener is able to detect if any of our events has changed */
		boolean superOk = super.performOk();
		
		if (superOk && eventGroupingListener.isSomePropertyChanged()) {
			/* reopen editors */
			CorePlugin.reopenEditors(new String[] {CoreConstants.BYTECODE_VISUALIZER_EDITOR_ID});
		}

		/* tidy up */
		store.removePropertyChangeListener(eventGroupingListener);
		
		return superOk;
		
	}
	
}
