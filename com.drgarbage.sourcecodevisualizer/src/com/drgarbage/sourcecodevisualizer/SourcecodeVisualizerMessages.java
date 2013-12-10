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

package com.drgarbage.sourcecodevisualizer;

import org.eclipse.osgi.util.NLS;

/**
 * Text messages for dialogs and buttons.
 * 
 * @author Sergej Alekseev
 * @version $Revision:235 $
 * $Id:PreferencesMessages.java 235 2007-06-22 18:48:35Z aleks $
 *
 */
public class SourcecodeVisualizerMessages extends NLS {

	private static final String BUNDLE_NAME= SourcecodeVisualizerMessages.class.getName();
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, SourcecodeVisualizerMessages.class);
	}
	
	/* disable constructor */
	private SourcecodeVisualizerMessages() {
	}

	public static String JavaEditorColoringFigureColoringConfigurationBlock_link;
	public static String Sourcecodevisualizer_File_Associations_Link;
	
	/* Menu, toolbar and context menu texts */
	public static String Sourcecodevisualizer_Menu_Text;
	public static String Sourcecodevisualizer_AboutAction_Text;
	public static String Sourcecodevisualizer_AboutAction_TooltipText;
	public static String Sourcecodevisualizer_AboutAction_Description;
	public static String Sourcecodevisualizer_AboutAction_Copyright;
	public static String Sourcecodevisualizer_CreateBytecodeGraphAction_Text;
	public static String Sourcecodevisualizer_CreateBytecodeGraphAction_TooltipText;

	/* actions */
	public static String Sourcecodevisualizer_ConstructorFilterAction_Text;
	public static String Sourcecodevisualizer_ConstructorFilterAction_TooltipText; 
	public static String Sourcecodevisualizer_MethodGraphFilterAction_Text;
	public static String Sourcecodevisualizer_MethodGraphFilterAction_TooltipText;
	public static String Sourcecodevisualizer_RefreshAction_Text;
	public static String Sourcecodevisualizer_RefreshAction_TooltipText;
	
	/* error handling */
	public static String ExceptionAdditionalMessage;
	public static String GraphNotGenerated_ClassFileNotCompiled;
	
}
