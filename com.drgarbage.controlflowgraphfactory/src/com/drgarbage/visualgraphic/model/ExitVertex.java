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

package com.drgarbage.visualgraphic.model;

import com.drgarbage.controlflowgraph.ControlFlowGraphGenerator;

public class ExitVertex extends RoundedRectangularVertex {

	private static final long serialVersionUID = -5032112843002603408L;

	public ExitVertex() {
		super();
		setLabel(ControlFlowGraphGenerator.VIRTUAL_EXIT_NODE_TEXT);
		setToolTip(ControlFlowGraphGenerator.VIRTUAL_EXIT_NODE_TOOLTIP_TEXT);
	}

}
