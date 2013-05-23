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

public interface BytecodeVisualizerPreferenceConstats {
	public static final String bytecodeMnemonicPreferencesPrefix = "BYTECODEMNEMONIC_";
	public static final String BYTECODE_MNEMONIC = bytecodeMnemonicPreferencesPrefix + "bytecodeMnemonic";
	public static final String BYTECODE_MNEMONIC_BOLD = bytecodeMnemonicPreferencesPrefix + "bytecodeMnemonic_bold";
	public static final String BYTECODE_MNEMONIC_ITALIC = bytecodeMnemonicPreferencesPrefix + "bytecodeMnemonic_italic";
	public static final String BYTECODE_MNEMONIC_STRIKETHROUGH = bytecodeMnemonicPreferencesPrefix + "bytecodeMnemonic_strikethrough";
	public static final String BYTECODE_MNEMONIC_UNDERLINE = bytecodeMnemonicPreferencesPrefix + "bytecodeMnemonic_underline";

	public static final String graphPanelAttributePreferencesPrefix = "GRAPH_PANEL_ATTR_";
	public static final String GRAPH_PANEL_ATTR_RENDER_GRAPHS = graphPanelAttributePreferencesPrefix + "render_graphs";
	
	public static final String classFileAttributePreferencesPrefix = "CLASS_FILE_ATTR_";
	public static final String CLASS_FILE_ATTR_SHOW_CONSTANT_POOL = classFileAttributePreferencesPrefix + "show_constantPool";
	public static final String CLASS_FILE_ATTR_SHOW_LINE_NUMBER_TABLE = classFileAttributePreferencesPrefix + "show_lineNumberTable";
	public static final String CLASS_FILE_ATTR_SHOW_VARIABLE_TABLE = classFileAttributePreferencesPrefix + "show_localVariableTable";
	public static final String CLASS_FILE_ATTR_SHOW_MAXS = classFileAttributePreferencesPrefix + "show_maxs";
	public static final String CLASS_FILE_ATTR_RENDER_TRYCATCH_BLOCKS = classFileAttributePreferencesPrefix + "render_tryCatchBlocks";
	public static final String CLASS_FILE_ATTR_SHOW_SOURCE_LINE_NUMBERS = classFileAttributePreferencesPrefix + "render_sourceLineNumbers";

	
	public static final String tabHandlingPrefix = "TAB_HANDLING_";	
	public static final String SHOW_TAB = tabHandlingPrefix + "ShowTab";
	public static final String SHOW_ALWAYS_BYTECODE_TAB = tabHandlingPrefix + "ShowAlwaysBytecodeTab";	
	public static final String SHOW_SOURCECODE_IF_AVALIABLE = tabHandlingPrefix + "ShowSourceCodeIfAvailable";
	public static final String SHOW_ALWAYS_SOURCECODE_TAB = tabHandlingPrefix + "ShowAlwaysSourceTab";

	public static final String takeClassFromPrefix = "TAKE_CLASS_FROM_";
	public static final String RETRIEVE_CLASS_FROM = takeClassFromPrefix + "HANDLER";	
	public static final String RETRIEVE_CLASS_FROM_FILE_SYSTEM = takeClassFromPrefix + "FILE_SYSTEM";
	public static final String RETRIEVE_CLASS_FROM_JVM_JDI = takeClassFromPrefix + "JVM_JDI";
	
	public static final String BYTECODE_VISUALIZER_GENERAL_PREFERENCE_PAGE_ID = "com.drgarbage.plugin.preferences.ClassFileAttributesPreferences";
	public static final String BRANCH_TARGET_ADDRESS_RENDERING = "BRANCH_TARGET_ADDRESS_RENDERING";
	public static final String BRANCH_TARGET_ADDRESS_ABSOLUTE = BRANCH_TARGET_ADDRESS_RENDERING +"_ABSOLUTE";
	public static final String BRANCH_TARGET_ADDRESS_RELATIVE = BRANCH_TARGET_ADDRESS_RENDERING +"_RELATIVE";
}
