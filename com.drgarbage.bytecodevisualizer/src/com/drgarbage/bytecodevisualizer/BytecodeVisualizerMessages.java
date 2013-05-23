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
package com.drgarbage.bytecodevisualizer;

import org.eclipse.osgi.util.NLS;

/**
 * Text messages for dialogs and buttons.
 * 
 * @author Peter Palaga
 * @version $Revision:235 $
 * $Id:PreferencesMessages.java 235 2007-06-22 18:48:35Z aleks $
 *
 */
public class BytecodeVisualizerMessages extends NLS {
	private static final String BUNDLE_NAME= BytecodeVisualizerMessages.class.getName();
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, BytecodeVisualizerMessages.class);
	}
	
	/* disable constructor */
	private BytecodeVisualizerMessages() {
	}

	public static String BytecodeVisualizerPreferencePage_link_File_Associations;
	
	/* ClassFile Attributes Preferences */
	public static String GeneralPreferencePage_title;
	public static String GeneralPreferencePage_lbl_Select_class_file_parts;
	public static String GeneralPreferencePage_chk_Show_Line_Number_Table;
	public static String GeneralPreferencePage_chk_Show_source_line_numbers;
	public static String GeneralPreferencePage_chk_Show_Local_Variable_Table;
	public static String GeneralPreferencePage_chk_Show_Constant_Pool;
	public static String GeneralPreferencePage_chk_Render_try_catch_blocks;

	public static String GeneralPreferencePage_radioGroup_Branch_Target_Address_Rendering;
	public static String GeneralPreferencePage_radio_Absolute;
	public static String GeneralPreferencePage_radio_Relative;

	public static String GeneralPreferencePage_radioGroup_Retrieve_class_from;
	public static String GeneralPreferencePage_radio_File_System;
	public static String GeneralPreferencePage_radio_JDI;
	public static String GeneralPreferencePage_link_JDI_limitations;

	
	/* Source/Bytecode Tab */
	public static String SourceCodePreferencePage_radioGroup_Which_editor_tab_should_be_active;
	public static String SourceCodePreferencePage_radio_Source_code_if_available;
	public static String SourceCodePreferencePage_radio_Always_source_code;
	public static String SourceCodePreferencePage_radio_Always_bytecode;	
	
	/* Syntax Highlighting */
	public static String SyntaxHighlightingPreferencePage_Color;
	public static String SyntaxHighlightingPreferencePage_chk_Bold;
	public static String SyntaxHighlightingPreferencePage_chk_Italic;
	public static String SyntaxHighlightingPreferencePage_chk_Strikethrough;
	public static String SyntaxHighlightingPreferencePage_chk_Underline;
	public static String SyntaxHighlightingPreferencePage_link_Editor_fonts_and_colors;
	public static String SyntaxHighlightingPreferencePage_link_Colors_of_Java_keywords_and_comments;
	public static String SyntaxHighlightingPreferencePage_lbl_Opcode_mnemonics_style;

	public static String SelectDebugTargetDialog_title;
	public static String SelectDebugTargetDialog_text;
	public static String SelectDebugTargetDialog_act_Filesystem;
	public static String SelectDebugTargetDialog_link_adjustPreferences;
	public static String SelectDebugTargetDialog_text_fileSystemFallback;
	
	public static String SelectJavaTypeDialog_title;
	public static String SelectJavaTypeDialog_text;
	public static String SelectJavaTypeDialog_column_Type;
	public static String SelectJavaTypeDialog_column_Class_Loader;
	
	public static String ActivateBasicblockGraphViewAction_text;
	public static String ActivateBasicblockGraphViewAction_tooltipText;
	public static String ActivateBytecodeGraphViewAction_text;
	public static String ActivateBytecodeGraphViewAction_tooltipText;

	public static String BytecodevizualizerActionBar_menu_Bytecode;
	public static String BytecodevizualizerActionBar_contextMenu_Graph_View;

	public static String OpenGraphInControlflowgraphFactoryAction_text;
	public static String OpenGraphInControlflowgraphFactoryAction_tooltipText;

	public static String OpenGraphInControlflowgraphFactoryAction_error_Opening_failed;

	public static String StepOverSingleInstructionAction_text;
	public static String StepIntoBytecodeAction_text;

	public static String ToggleBreakpointAction_text;
	public static String ToggleBreakpointAction_tooltipText_Debug_functionality_is_unavailable_in_this_context_;

	public static String BytecodeEditor_tab_Bytecode;
	public static String BytecodeEditor_tab_Source;

	public static String Feature_lbl_Debug_classes_without_source_code;
	public static String Feature_lbl_Debug_methods_without_Line_Number_Table;
	public static String Feature_lbl_0_and_1;
	public static String Feature_lbl_Retrieving_classes_directly_from_a_running_JVM_during_debugging;
	public static String Feature_link_0_1_2_3_4_see_5_6_;
	
	public static String DebugFunctionalityInfoDialog_lbl_Your_class_or_jar_file_needs_;
	public static String DebugFunctionalityInfoDialog_lbl_i_included_in_some_java_projects_build_path_and;
	public static String DebugFunctionalityInfoDialog_lbl_ii_opened_using_0_1_or_similar_feature_of_2_;
	public static String DebugFunctionalityInfoDialog_link_Please_read_0_this_tutorial_for_more_details_;

}
