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

package com.drgarbage.controlflowgraphfactory.preferences;

import org.eclipse.jface.preference.FieldEditor;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryPlugin;
import com.drgarbage.core.preferences.AbstractFieldPreferencePage;
import com.drgarbage.core.preferences.IndentBooleanFieldEditor;
import com.drgarbage.core.preferences.LabelField;

/**
 * The class file attributes preference page.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class GenerateGraphOptionsPreferencePage extends AbstractFieldPreferencePage {

	/**
	 * Default Constructor.
	 * @param style
	 * @param preferenceStore
	 */
	public GenerateGraphOptionsPreferencePage(){
		super(GRID, ControlFlowFactoryPlugin.getDefault().getPreferenceStore());
	}

	public void createFieldEditors() {
		
		/* create fields */
		FieldEditor fe = null;
		
		fe = new LabelField("Lbl", ControlFlowFactoryMessages.GenerateGraphOPtions_Label, getFieldEditorParent());
		fe.setEnabled(true, getFieldEditorParent());
		addField(fe);

		fe = new IndentBooleanFieldEditor(ControlFlowFactoryPreferenceConstants.GENERATE_START_NODE, ControlFlowFactoryMessages.createStartNode, getFieldEditorParent());
		fe.setEnabled(true, getFieldEditorParent());
		addField(fe);

		fe = new IndentBooleanFieldEditor(ControlFlowFactoryPreferenceConstants.GENERATE_EXIT_NODE, ControlFlowFactoryMessages.createExitNode, getFieldEditorParent());
		fe.setEnabled(true, getFieldEditorParent());
		addField(fe);

		fe = new IndentBooleanFieldEditor(ControlFlowFactoryPreferenceConstants.GENERATE_BACK_EDGE, ControlFlowFactoryMessages.createBackEdge, getFieldEditorParent());
		fe.setEnabled(true, getFieldEditorParent());
		addField(fe);

		fe = new IndentBooleanFieldEditor(ControlFlowFactoryPreferenceConstants.COPY_LINE_NUMBER_TABLE, ControlFlowFactoryMessages.copyLineNumberTable, getFieldEditorParent());
		fe.setEnabled(true, getFieldEditorParent());
		addField(fe);

		
		fe = new LabelField("Lbl", "\n" + ControlFlowFactoryMessages.GenerateGraphLongDescr_Label, getFieldEditorParent());
		fe.setEnabled(true, getFieldEditorParent());
		addField(fe);
		
		fe = new IndentBooleanFieldEditor(ControlFlowFactoryPreferenceConstants.GENERATE_BASIC_BLOCK_LONG_DESCR, ControlFlowFactoryMessages.generateBasicBlockLongDescr, getFieldEditorParent());
		fe.setEnabled(true, getFieldEditorParent());
		addField(fe);
		
		fe = new IndentBooleanFieldEditor(ControlFlowFactoryPreferenceConstants.GENERATE_SOURSECODE_BLOCK_LONG_DESCR, ControlFlowFactoryMessages.generateSourcecodeBlockLongDescr, getFieldEditorParent());
		fe.setEnabled(true, getFieldEditorParent());
		addField(fe);
	}
		
	
}