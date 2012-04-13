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

public class DefaultGraphSpecification implements IGraphSpecification {
	private boolean exportComments = false;
	private boolean exportDecorations = false;
	private int exportFormat = GraphConstants.EXPORT_FORMAT_NONE;
	private boolean exportGeometry = false;
	private int graphType = GraphConstants.GRAPH_TYPE_SOURCE_GRAPH;
	
	private boolean openInEditor = false;
	private boolean overwriteAll = false;
	private boolean supressMessages = false;
	
	public DefaultGraphSpecification() {
		super();
	}

	public DefaultGraphSpecification(IGraphSpecification source) {
		super();
		
		exportComments = source.isExportComments();
		exportDecorations = source.isExportDecorations();
		exportFormat = source.getExportFormat();
		exportGeometry = source.isExportGeometry();
		graphType = source.getGraphType();
		overwriteAll = source.isOverwriteAll();
		supressMessages = source.isSupressMessages();
		openInEditor = source.isOpenInEditor();
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#getExportFormat()
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#getExportFormat()
	 */
	public int getExportFormat() {
		return exportFormat;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#getGraphType()
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#getGraphType()
	 */
	public int getGraphType() {
		return graphType;
	}

	
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isExportComments()
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isExportComments()
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isExportComments()
	 */
	public boolean isExportComments() {
		return exportComments;
	}
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isExportDecorations()
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isExportDecorations()
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isExportDecorations()
	 */
	public boolean isExportDecorations() {
		return exportDecorations;
	}
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isExportGeometry()
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isExportGeometry()
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isExportGeometry()
	 */
	public boolean isExportGeometry() {
		return exportGeometry;
	}
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isOpenInEditor()
	 */
	public boolean isOpenInEditor() {
		return openInEditor;
	}
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isOverwriteAll()
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isOverwriteAll()
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isOverwriteAll()
	 */
	public boolean isOverwriteAll() {
		return overwriteAll;
	}
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isSupressMessages()
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isSupressMessages()
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isSupressMessages()
	 */
	public boolean isSupressMessages() {
		return supressMessages;
	}
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setExportComments(boolean)
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setExportComments(boolean)
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setExportComments(boolean)
	 */
	public void setExportComments(boolean exportComments) {
		this.exportComments = exportComments;
	}
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setExportDecorations(boolean)
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setExportDecorations(boolean)
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setExportDecorations(boolean)
	 */
	public void setExportDecorations(boolean exportDecorations) {
		this.exportDecorations = exportDecorations;
	}
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setExportFormat(int)
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setExportFormat(int)
	 */
	public void setExportFormat(int exportFormat) {
		this.exportFormat = exportFormat;
	}
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setExportGeometry(boolean)
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setExportGeometry(boolean)
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setExportGeometry(boolean)
	 */
	public void setExportGeometry(boolean exportGeometry) {
		this.exportGeometry = exportGeometry;
	}
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setGraphType(int)
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setGraphType(int)
	 */
	public void setGraphType(int graphType) {
		this.graphType = graphType;
	}
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setOpenInEditor(boolean)
	 */
	public void setOpenInEditor(boolean openInEditor) {
		this.openInEditor = openInEditor;
	}
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setOverwriteAll(boolean)
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setOverwriteAll(boolean)
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setOverwriteAll(boolean)
	 */
	public void setOverwriteAll(boolean overwriteAll) {
		this.overwriteAll = overwriteAll;
	}
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setSupressMessages(boolean)
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setSupressMessages(boolean)
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setSupressMessages(boolean)
	 */
	public void setSupressMessages(boolean supressMessages) {
		this.supressMessages = supressMessages;
	}
	
	
}
