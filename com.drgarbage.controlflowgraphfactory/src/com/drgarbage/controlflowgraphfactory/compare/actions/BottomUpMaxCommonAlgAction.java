/**
 * Copyright (c) 2008-2014, Dr. Garbage Community
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

package com.drgarbage.controlflowgraphfactory.compare.actions;

import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.compare.GraphMergeViewer;
import com.drgarbage.controlflowgraphfactory.img.ControlFlowFactoryResource;

/**
 * <p>
 * Implementation of the action to call Bottop-Up Max Common Subtree algorithm.
 * </p>
 * 
 * @author Adam Kajrys
 * @version $Revision$
 * $Id$
 * 
 * @see BaseCompareAction
 */
public class BottomUpMaxCommonAlgAction extends BaseCompareAction {

	/**
	 * Creates an action.
	 * @param cmv
	 */
	public BottomUpMaxCommonAlgAction(GraphMergeViewer cmv) {
		super(cmv);
		setImageDescriptor(ControlFlowFactoryResource.graph_compare_bottom_up_max_common_subtree_16x16);
		setToolTipText(ControlFlowFactoryMessages.GraphCompare_BottomUpMaxCommonAlgorithm_Text);
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.controlflowgraphfactory.compare.actions.BaseCompareAction#run()
	 */
	@Override
	public void run() {
		try {
			viewer.doBottomUpMaxCommonAlg();
		} catch (ControlFlowGraphException e) {
			e.printStackTrace();
		}
	}
}
