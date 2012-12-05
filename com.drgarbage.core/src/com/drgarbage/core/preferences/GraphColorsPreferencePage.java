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

import com.drgarbage.controlflowgraph.VisualGraphicMessages;
import com.drgarbage.core.CorePlugin;

/**
 * Preferences for visual figures.
 * 
 * @author Peter Palaga
 * @version $Revision$
 * $Id: GraphColorsPreferencePage.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class GraphColorsPreferencePage extends AbstractFieldPreferencePage implements CorePreferenceConstants {

	/**
	 * Initialize preference store.
	 */
	public GraphColorsPreferencePage() {
		super(GRID, CorePlugin.getDefault().getPreferenceStore());
	}

	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new IndentColorFieldEditor(INSTRUCTION_BGCOLOR, VisualGraphicMessages.Instruction, getFieldEditorParent()));
		addField(new IndentColorFieldEditor(BASIC_BLOCK_BGCOLOR, VisualGraphicMessages.Basic_block, getFieldEditorParent()));
		addField(new IndentColorFieldEditor(GET_VERTEX_BGCOLOR, VisualGraphicMessages.Get, getFieldEditorParent()));
		addField(new IndentColorFieldEditor(INVOKE_VERTEX_BGCOLOR, VisualGraphicMessages.Invoke, getFieldEditorParent()));
		addField(new IndentColorFieldEditor(SWITCH_VERTEX_BGCOLOR, VisualGraphicMessages.Switch, getFieldEditorParent()));
		addField(new IndentColorFieldEditor(DECISION_VERTEX_BGCOLOR, VisualGraphicMessages.Decision, getFieldEditorParent()));		
		addField(new IndentColorFieldEditor(GOTO_JUMP_VERTEX_BGCOLOR, VisualGraphicMessages.Goto_jump, getFieldEditorParent()));
		addField(new IndentColorFieldEditor(RETURN_VERTEX_BGCOLOR, VisualGraphicMessages.Return, getFieldEditorParent()));
		addField(new IndentColorFieldEditor(ENTRY_END_BGCOLOR, VisualGraphicMessages.Entry, getFieldEditorParent()));
		addField(new IndentColorFieldEditor(ENTRY_END_BGCOLOR, VisualGraphicMessages.Exit, getFieldEditorParent()));
		addField(new IndentColorFieldEditor(COMMENT_BGCOLOR, VisualGraphicMessages.Comment, getFieldEditorParent()));
	}

}