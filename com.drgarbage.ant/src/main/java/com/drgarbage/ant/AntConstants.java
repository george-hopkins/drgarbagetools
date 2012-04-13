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

package com.drgarbage.ant;

public interface AntConstants {
	
	char LIST_SEPARATOR = ',';
	String LIST_SEPARATOR_STRING = String.valueOf(LIST_SEPARATOR);
	String VERSION_PROPERTIES_FILE_NAME = "version_{0}.properties";
	char VERSION_PROPERTIES_FIELD_SEPARATOR = ';';
	String META_INF_MANIFEST_MF = "{0}{1}META-INF{1}MANIFEST.MF";
	String PATH_FEATURE_XML = "{0}{1}{2}.feature{1}feature.xml";
	String BUNDLE_VERSION = "Bundle-Version";
	String REQUIRE_BUNDLE = "Require-Bundle";
	String COM_DRGARBAGE_VERSION = "com.drgarbage.version";
	String MATCH_EQUIVALENT = "equivalent";
	String MATCH_PERFECT = "perfect";
	String MATCH_GREATER_OR_EQUAL = "greaterOrEqual";

	char INTERVAL_QUOTE = '"';
	char INTERVAL_COMMA = ',';
	char INTERVAL_LEFT_SQUARE_BRACKET = '[';
	char INTERVAL_RIGHT_SQUARE_BRACKET = ']';
	char INTERVAL_LEFT_PARENTHESIS = '(';
	char INTERVAL_RIGHT_PARENTHESIS = ')';

	char VERSION_SEPARATOR = '.';
	String VERSION_QUALIFIER = "qualifier";
	
	char TARGET_m = 'm';
	char TARGET_f = 'f';
	char TARGET_i = 'i';
	char TARGET_p = 'p';
	String TARGET_default = "" + TARGET_m + TARGET_i;
	char MANIFEST_SEMICOLON = ';';
	char MANIFEST_EQUAL = '=';
	String MANIFEST_BUNDLE_VERSION = "bundle-version";
	char MANIFEST_COMMA = ',';
	String COM_DRGARBAGE_ = "com.drgarbage.";
}
