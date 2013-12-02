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

package com.drgarbage.controlflowgraphfactory;

import org.eclipse.osgi.util.NLS;

/**
 * Text messages for dialogs and buttons.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: ControlFlowFactoryMessages.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 *
 */
public class ControlFlowFactoryMessages extends NLS {

	private static final String BUNDLE_NAME= ControlFlowFactoryMessages.class.getName();


	
	static {
		NLS.initializeMessages(BUNDLE_NAME, ControlFlowFactoryMessages.class);
	}
	
	/* disable constructor */
	private ControlFlowFactoryMessages() {
	}

	public static String ControlFlowFactory_ConfigurationBlock_link;
	
	/* Menu, toolbar and context menu texts */
	public static String ControlFlowFactory_Menu_Text;
	public static String ControlFlowFactory_SubMenuGrphAlgorithms_Text;
	public static String ControlFlowFactory_SubMenuAlignElements_Text;
	public static String HorizontalLeftOrderAction_Text;
	public static String HorizontalLeftOrderAction_ToolTipText;
	public static String HorizontalRightOrderAction_Text;
	public static String HorizontalRightOrderAction_ToolTipText;
	public static String HorizontalCenterOrderAction_Text;
	public static String HorizontalCenterOrderAction_ToolTipText;
	public static String VerticalTopOrderAction_Text;
	public static String VerticalTopOrderAction_ToolTipText;
	public static String VerticalCenterOrderAction_Text;
	public static String VerticalCenterOrderAction_ToolTipText;
	public static String VerticalBottomOrderAction_Text;
	public static String VerticalBottomOrderAction_ToolTipText;
	
	public static String ControlFlowFactory_SubMenuResizeElements_Text;
	public static String MinWidhtAction_Text;
	public static String MinWidhtAction_ToolTipText;
	public static String MinHeightAction_Text;
	public static String MinHeightAction_ToolTipText;
	public static String MaxWidhtAction_Text;
	public static String MaxWidhtAction_ToolTipText; 
	public static String MaxHeightAction_Text;
	public static String MaxHeightAction_ToolTipText;
	
	/* Action constants */
	public static String ByteCodelLayoutAlgorithmAction_Text;
	public static String ByteCodelLayoutAlgorithmAction_ToolTipText;
	public static String HierarchicalLayoutAlgorithmAction_Text;
	public static String HierarchicalLayoutAlgorithmAction_ToolTipText;
	
	public static String ExportAsImageAction_Text;
	public static String ExportAsImageAction_ToolTipText;
	public static String ExportAsImageAction_HeaderText;
	public static String Warning_Unsuported_Image_Format;
	public static String PrintAction_Text;
	public static String PrintAction_ToolTipText;
	public static String PrintAction_HeaderText;
	public static String PrintAction_buttonTILE_Mode;
	public static String PrintAction_buttonFITPAGE_Mode;
	public static String PrintAction_buttonFIT_WIDTH_Mode;
	public static String PrintAction_buttonFIT_HEIGHT_Mode;
	public static String DirectTextEditAction_Text;
	public static String DirectTextEditAction_Description;
	public static String ProgressDialogCreateGraphs;
	
	/* export Dialog*/
	public static String EXPORT_DIALOG_outputFileGroupText;
	public static String EXPORT_DIALOG_browserFileButtonText;
	public static String EXPORT_DIALOG_okButtonText;
	public static String EXPORT_DIALOG_cancelButtonText;
	public static String EXPORT_DIALOG_fileDialogTitle;
	public static String EXPORT_DIALOG_optionsGroupText;
	
	/* error handling*/
	public static String ExecutionFailure;
	public static String DiagramIsNullMessage;
	public static String CannotLoadGraphFile;
	public static String ClassFileInputNotCreated;
	public static String ERROR_ClassFile_is_Interface_Enum_Annotation;
	public static String ERROR_LineNumberTable_is_Missing;
	
	/* Graphical Palete Constants */
	public static String SOLID_TEXT_COMMAND;
	public static String DASHED_TEXT_COMMAND;

	/* tool tips of models commands. */
	public static String DECISION_TOOLTIP;
	public static String GET_TEXT_TOOLTIP;
	public static String GOTOJUMP_TOOLTIP;
	public static String INVOKE_TOOLTIP;
	public static String RECTANGLE_TOOLTIP;
	public static String RETURN_TOOLTIP;
	public static String ENTRY_TOOLTIP;
	public static String END_TOOLTIP;
	public static String SWITCH_TOOLTIP;
	public static String COMMENT_ELEMENT_TOOLTIP;
	public static String SOLID_CONNECTION_TOOLTIP;
	public static String DASHED_CONNECTION_TOOLTIP;
	
	/* tool tips of models commands. */
	public static String DECISION_NODE_TEXT;
	public static String GET_NODE_TEXT;
	public static String GOTOJUMP_NODE_TEXT;
	public static String INVOKE_NODE_TEXT;
	public static String RECTANGLE_NODE_TEXT;
	public static String RETURN_NODE_TEXT;
	public static String ROUNDEDRECT_NODE_TEXT;
	public static String SWITCH_NODE_TEXT;
	public static String COMMENT_ELEMENT_TEXT;
	
	/* Export action constants */
	public static String EXPORT_FILE_OVERWRITE_TITLE;
	public static String EXPORT_FILE_OVERWRITE_MESSAGE;
	public static String EXPORT_FILE_OVERWRITE_MESSAGE_2;
	public static String EXPORT_FILE_COULD_NOT_WRITE;
	public static String EXPORT_ERROR;
	public static String EXPORT_ERROR_WRONGPATH;
	public static String EXPORT_INFO;
	
	/* Preferences */
	public static String GenerateGraphOPtions_Label;
	public static String createStartNode;
	public static String createExitNode;
	public static String createBackEdge;
	public static String copyLineNumberTable;
	public static String GenerateGraphLongDescr_Label;
	public static String generateBasicBlockLongDescr;
	public static String generateSourcecodeBlockLongDescr;
	public static String UseGradientFillColor;
	
	/* Export Wizard */
	public static String ControlFlowGraphExportWizard_title;
	public static String ControlFlowGraphExportWizard_description;
	public static String ControlFlowGraphExportWizardMonitor_title;
	public static String ControlFlowGraphExportWizard_act_Browse;
	public static String ControlFlowGraphExportWizard_msg_Select_Output_Directory;
	public static String ControlFlowGraphExportWizardFileChooser_title;
	public static String ControlFlowGraphExportWizard_chk_Replicate_the_Input_Directory_Structure;
	public static String ControlFlowGraphExportWizard_chk_Create_only_selected_directories;
	public static String ControlFlowGraphExportWizard_chk_Overwrite_existing_files_without_warning;
	public static String ControlFlowGraphExportWizard_msg_Output_Directory_does_not_exist;
	public static String ControlFlowGraphExportWizard_err_Output_Directory_could_not_be_created;
	public static String ControlFlowGraphExportWizard_err_Output_Directory_already_exists_as_a_file;
	public static String ControlFlowGraphExportWizard_lbl_Output_Directory;
	public static String ControlFlowGraphExportWizard_err_Problems_encountered_during_export_;
	public static String ControlFlowGraphExportWizard_err_Export_Problems;
	public static String ControlFlowGraphExportWizard_err_Error_exporting_0_1;
	public static String ControlFlowGraphExportWizard_err_Cannot_overwrite_file_0;
	public static String ControlFlowGraphExportWizard_warn_damageWarning;
	public static String ControlFlowGraphExportWizard_err_ConflictingContainer;
	public static String ControlFlowGraphExportWizard_err_empty_selection;
	public static String ControlFlowGraphExportWizard_err_Select_Output_Directory;
	
	public static String ExportGraphAction_checkBox1Text;
	public static String ExportGraphAction_checkBox2Text;
	public static String ExportGraphAction_checkBox3Text;
	
	public static String ExportFormat_Dr_Garbage_Graph;
	public static String ExportFormat_DOT_Graph_Language;
	public static String ExportFormat_GraphXML_XML_Based;
	public static String ExportFormat_GraphML_XML_Based;
	
	public static String ExportGraphAction_Text;
	public static String ExportGraphAction_ToolTipText;
	public static String ExportGraphAction_HeaderText;
	
	public static String WARNING_CannotShowFileInPackageexplorer;
	public static String WARNING_CannotShowFolderInPackageexplorer;
	
	public static String GraphDOTExport_Cannot_transform_line_style_0; 
	public static String Export_Could_not_export_0_;
	
	public static String Open_newly_created_graph_file;
	
}
