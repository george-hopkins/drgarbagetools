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

public interface CoreConstants {
	
	public static final String providerWww = "providerWww";
	public static final String providerNameLabel = "providerNameLabel";
	public static final String pluginName = "pluginName";
	
	
	/**
	 * Unique identifier constant (value <code>"com.drgarbage.bytecodevisualizer"</code>)
	 * for the standard Bytecode Visualizer Plugin.
	 */
	public static final String BYTECODE_VISUALIZER_PLUGIN_ID = "com.drgarbage.bytecodevisualizer";
	
	/* constant */
	public static final String BYTECODE_VISUALIZER_CONTEXT_MENU_ID  = BYTECODE_VISUALIZER_PLUGIN_ID + ".context";
	
	/* Editor IDs have to be restarted. No other way to access the ID. */
	public final static String BYTECODE_VISUALIZER_EDITOR_ID = "com.drgarbage.bytecodevisualizer.editor";
	public final static String SOURCECODE_VISUALIZER_EDITOR_ID = "com.drgarbage.sourcecodevisualizer.editors.JavaCodeEditor";
	public static final String ISO_DATE_TIME_FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String ISO_DATE_TIME_FORMAT_HH_mm = "yyyy-MM-dd HH:mm";
	
	/**
	 * ID necessary for communication with control flow graph view.
	 */
	public final static String CONTROL_FLOW_VIEW_ID = "com.drgabage.bytecodevisualizer.view.controlflowgraph_view";
	
	public final static String  PREFPAGEID_GENERAL_TEXT_EDITOR = "org.eclipse.ui.preferencePages.GeneralTextEditor";
	public final static String  PREFPAGEID_COLORS_AND_FONTS = "org.eclipse.ui.preferencePages.ColorsAndFonts";
	public final static String  PREFPAGEID_JAVA_COLORING = "org.eclipse.jdt.ui.preferences.JavaEditorColoringPreferencePage";
	public final static String  PREFPAGEID_FILE_ASSOCIATIONS = "org.eclipse.ui.preferencePages.FileEditors";
	public final static String  PREFPAGEID_FIGURE_PREFERENCES = "com.drgarbage.bytecodevisualizer.preferences.FigurePreferences";

	public final static String PLUGIN_BYTECODE_VISUALIZER = "Bytecode Visualizer";
	public final static String PLUGIN_BYTECODE_VISUALIZER_LT = "Bytecode Visualizer LT";
	public final static String PLUGIN_SOURCECODE_VISUALIZER = "Sourcecode Visualizer";
	public final static String PLUGIN_CONTROL_FLOW_GRAPH_FACTORY = "Control Flow Graph Factory";
	public static final String CONTROL_FLOW_GRAPH_FACTORY = "Control Flow Graph Factory";
	public static final String SOURCECODE_VISUALIZER = "Sourcecode Visualizer";
	public static final String ACTION_STEP_OVER_SINGLE_INSTRUCTION = "com.drgarbage.bytecodevisualizer.common.actions.StepOverSingleInstructionCommandAction";
	public static final String ACTION_STEP_INTO_BYTECODE = "com.drgarbage.bytecodevisualizer.common.actions.StepIntoBytecodeAction";
	public static final int INTERNAL_ERROR = 0;
	public static final int WARNING = 1;
	public static final String PREFPAGEID_GENERAL = "com.drgarbage.bytecodevisualizer.preferences.GeneralPreferencesPage";

}
