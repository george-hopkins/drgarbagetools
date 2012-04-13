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

package com.drgarbage.graph;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.io.FileExtensions;

public interface GraphConstants {

	/**
	 * Graph Type sufix.
	 */
	public static final String[] graphTypeSuffixes = {"byte", "bb", "src"};
	/**
	 * Graph type: BYTECODE_GRAPH, BASICBLOCK_GRAPH or SOURCE_GRAPH
	 */
	public static final int GRAPH_TYPE_BYTECODE_GRAPH = 0;
	public static final int GRAPH_TYPE_BASICBLOCK_GRAPH = 1;
	public static final int GRAPH_TYPE_SOURCE_GRAPH = 2;

	public static final int EXPORT_FORMAT_DOT = 1;
	public static final int EXPORT_FORMAT_DRGARBAGE_GRAPH = 0;
	public static final int EXPORT_FORMAT_GRAPHXML = 2;
	public static final int EXPORT_FORMAT_NONE = -1;
	public static final int EXPORT_FORMAT_GRAPHML = 3;
	
	public static final String[] SUPPORTED_EXPORT_FORMAT_EXTENSIONS = new String[] {
		FileExtensions.GRAPH,
		FileExtensions.DOT,
		FileExtensions.XML,
		FileExtensions.GRAPHML
	};

	public static final String[] SUPPORTED_EXPORT_FORMAT_LABELS = new String[] {
		ControlFlowFactoryMessages.ExportFormat_Dr_Garbage_Graph,
		ControlFlowFactoryMessages.ExportFormat_DOT_Graph_Language,
		ControlFlowFactoryMessages.ExportFormat_GraphXML_XML_Based,
		ControlFlowFactoryMessages.ExportFormat_GraphML_XML_Based
	};

}
