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

package com.drgarbage.controlflowgraphfactory.export;

import java.io.Writer;

import com.drgarbage.graph.IGraphSpecification;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;

public abstract class AbstractExport2 {
	protected IGraphSpecification graphSpecification;


	public AbstractExport2() {
		super();
	}


	public IGraphSpecification getGraphSpecification() {
		return graphSpecification;
	}


	public void setGraphSpecification(IGraphSpecification graphSpecification) {
		this.graphSpecification = graphSpecification;
	}


	public abstract void write(ControlFlowGraphDiagram diagram, Writer out) throws ExportException; 

}
