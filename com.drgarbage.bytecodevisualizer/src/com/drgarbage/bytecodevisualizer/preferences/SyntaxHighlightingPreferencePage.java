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

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.core.CoreConstants;
import com.drgarbage.core.preferences.AbstractFieldPreferencePage;
import com.drgarbage.core.preferences.IndentBooleanFieldEditor;
import com.drgarbage.core.preferences.IndentColorFieldEditor;
import com.drgarbage.core.preferences.LabelField;
import com.drgarbage.core.preferences.PreferencePageLinkField;

/**
 * 
 * 
 * @author Peter Palaga
 * @version $Revision$
 * $Id$
 */
public class SyntaxHighlightingPreferencePage extends AbstractFieldPreferencePage implements BytecodeVisualizerPreferenceConstats {
	
	/**
	 * Initialize preference store.
	 */
	public SyntaxHighlightingPreferencePage() {
		super(GRID, BytecodeVisualizerPlugin.getDefault().getPreferenceStore());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	public void createFieldEditors() {
		
		String msg = null;
		
		msg = MessageFormat.format(
				BytecodeVisualizerMessages.SyntaxHighlightingPreferencePage_link_Editor_fonts_and_colors, 
				new Object[] {
						CoreConstants.PREFPAGEID_GENERAL_TEXT_EDITOR,
						CoreConstants.PREFPAGEID_COLORS_AND_FONTS
				});
		addField(new PreferencePageLinkField("Lbl1", msg, getFieldEditorParent()));

		msg = MessageFormat.format(
				BytecodeVisualizerMessages.SyntaxHighlightingPreferencePage_link_Colors_of_Java_keywords_and_comments, 
				new Object[] {
						CoreConstants.PREFPAGEID_JAVA_COLORING
				});
		addField(new PreferencePageLinkField("Lbl2", msg, getFieldEditorParent()));

		/* vertical space */
		addField(new LabelField("Lbl3", "", getFieldEditorParent()));

		addField(new LabelField("Lbl4", BytecodeVisualizerMessages.SyntaxHighlightingPreferencePage_lbl_Opcode_mnemonics_style, getFieldEditorParent()));
		addField(new IndentColorFieldEditor(BYTECODE_MNEMONIC, BytecodeVisualizerMessages.SyntaxHighlightingPreferencePage_Color, getFieldEditorParent()));
		addField(new IndentBooleanFieldEditor(BYTECODE_MNEMONIC_BOLD, BytecodeVisualizerMessages.SyntaxHighlightingPreferencePage_chk_Bold, getFieldEditorParent()));
		addField(new IndentBooleanFieldEditor(BYTECODE_MNEMONIC_ITALIC, BytecodeVisualizerMessages.SyntaxHighlightingPreferencePage_chk_Italic, getFieldEditorParent()));
		addField(new IndentBooleanFieldEditor(BYTECODE_MNEMONIC_STRIKETHROUGH, BytecodeVisualizerMessages.SyntaxHighlightingPreferencePage_chk_Strikethrough, getFieldEditorParent()));
		addField(new IndentBooleanFieldEditor(BYTECODE_MNEMONIC_UNDERLINE, BytecodeVisualizerMessages.SyntaxHighlightingPreferencePage_chk_Underline, getFieldEditorParent()));
	}

}