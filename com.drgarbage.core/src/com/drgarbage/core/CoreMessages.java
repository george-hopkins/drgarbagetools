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
package com.drgarbage.core;

import org.eclipse.osgi.util.NLS;

/**
 * Text messages for dialogs and buttons.
 * 
 * @author Peter Palaga
 * @version $Revision:235 $
 * $Id:PreferencesMessages.java 235 2007-06-22 18:48:35Z aleks $
 *
 */
public class CoreMessages extends NLS {

	private static final String BUNDLE_NAME= CoreMessages.class.getName();
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, CoreMessages.class);
	}
	
	/* disable constructor */
	private CoreMessages() {
	}

	public static String RestartRequiredDialog_text;
	public static String RestartRequiredDialog_title;
	public static String RestartRequiredDialog_act_Restart_Now;
	public static String RestartRequiredDialog_act_Restart_Later;
	
	/* general string in preferences */
	public static String GraphPanelLocationPreferencePage_radioGroup_Graph_Panel_Location;
	public static String GraphPanelLocationPreferencePage_radio_Separate_View;
	public static String GraphPanelLocationPreferencePage_radio_Editor;
	public static String GraphPanelLocationPreferencePage_lbl_Show_view_manually;
	public static String GraphPanelLocationPreferencePage_lbl_Show_view_path;
	
	public static String ControlFlowGraphView_title;
	public static String ControlFlowGraphView_lbl_not_available;
	
	/* outline texts */
	public static String OutlineShowGraphPanelAction;
	public static String OutlineShowOverviewPanelAction;
	
	/* Menu, toolbar and context menu texts */	
	public static String Bytecodevisualizer_AboutAction_Text;
	public static String Bytecodevisualizer_AboutAction_TooltipText;
	public static String Bytecodevisualizer_AboutAction_Description;
	public static String Bytecodevisualizer_AboutAction_Copyright;
	
	public static String Core_PreferencePage_Link;
	
	/* texts for message dialogs */
	public static String MessageDialogInfo;
	public static String MessageDialogWarning;
	public static String MessageDialogError;
	public static String MessageDialogQuestion;
	
	/* breakpoint actions */
	public static String lbl_Click_to_learn_more_;

	
	/* errors and exceptions */
	public static String LINE_NOT_FOUND_IN_SELECTED_TEXT;
	public static String CLASS_NAME_NOT_RESOLVED;
	
	/*Error handling*/
	public static String ERROR_CreateNested_Editor;
	public static String ERROR_Cannot_get_Sourcecode;
	public static String ERROR_ToggleBreakpointAction_1;
	public static String ERROR_ToggleBreakpointAction_2;
	public static String Error;
	public static String Warning;
	public static String Info;

	/* Source code attachment form */
	public static String SourceCodeUnavailable;
	public static String ErrorOpenSource;
	public static String ErrorOpenSourceMessage;
	public static String SourceAttachmentForm_message_noSource;
	public static String SourceAttachmentForm_cannotconfigure;
	public static String SourceAttachmentForm_notsupported;
	public static String SourceAttachmentForm_readonly;
	public static String SourceAttachmentForm_message_noSourceAttachment;
	public static String SourceAttachmentForm_message_pressButtonToAttach;
	public static String SourceAttachmentForm_button_attachSource;
	public static String SourceAttachmentForm_message_noSourceInAttachment;
	public static String SourceAttachmentForm_message_pressButtonToChange;
	public static String SourceAttachmentForm_button_changeAttachedSource;
	public static String RestartEditorMessage;
	
	public static String DefaultEditorText;
	
	public static String Bytecodevisualizer_ControlFlowGraphEditorMaxSizeReached;
	public static String CannotGenerateGraph_MethodIsAnAbstractMethod;
	public static String ExceptionAdditionalMessage;
	
	public static String ClassFileEditor_error_title;
	public static String ClassFileEditor_error_message;
	
	public static String GraphColorsPreferencePage_title;
	public static String lbl_Package_Explorer;
	public static String lbl_Open_Declaration;
	public static String lbl_Java_Development_Tools_JDT_Plugin;
	
	public static String ASTViewPage_Hide_PKG_DECL;
	public static String ASTViewPage_Hide_PKG_DECL_ID;
	public static String ASTViewPage_Hide_PKG_DECL_tooltip;
	public static String ASTViewPage_Hide_PKG_Imports;
	public static String ASTViewPage_Hide_PKG_Imports_tooltip;
	public static String ASTViewPage_Hide_JAVADOC;
	public static String ASTViewPage_Hide_JAVADOC_tooltip;
	public static String ASTViewPage_Hide_FIELDS;
	public static String ASTViewPage_Hide_FIELDS_tooltip;
	
}
