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

public interface IGraphSpecification {

	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#getExportFormat()
	 */
	public abstract int getExportFormat();

	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#getGraphType()
	 */
	public abstract int getGraphType();

	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isExportComments()
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isExportComments()
	 */
	public abstract boolean isExportComments();

	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isExportDecorations()
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isExportDecorations()
	 */
	public abstract boolean isExportDecorations();

	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isExportGeometry()
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isExportGeometry()
	 */
	public abstract boolean isExportGeometry();

	public abstract boolean isOpenInEditor();

	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isOverwriteAll()
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isOverwriteAll()
	 */
	public abstract boolean isOverwriteAll();

	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isSupressMessages()
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#isSupressMessages()
	 */
	public abstract boolean isSupressMessages();

	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setExportComments(boolean)
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setExportComments(boolean)
	 */
	public abstract void setExportComments(boolean exportComments);

	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setExportDecorations(boolean)
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setExportDecorations(boolean)
	 */
	public abstract void setExportDecorations(boolean exportDecorations);

	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setExportFormat(int)
	 */
	public abstract void setExportFormat(int exportFormat);

	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setExportGeometry(boolean)
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setExportGeometry(boolean)
	 */
	public abstract void setExportGeometry(boolean exportGeometry);

	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setGraphType(int)
	 */
	public abstract void setGraphType(int graphType);

	public abstract void setOpenInEditor(boolean openInEditor);

	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setOverwriteAll(boolean)
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setOverwriteAll(boolean)
	 */
	public abstract void setOverwriteAll(boolean overwriteAll);

	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setSupressMessages(boolean)
	 */
	/* (non-Javadoc)
	 * @see com.drgarbage.graph.IGraphSpecification#setSupressMessages(boolean)
	 */
	public abstract void setSupressMessages(boolean supressMessages);

}