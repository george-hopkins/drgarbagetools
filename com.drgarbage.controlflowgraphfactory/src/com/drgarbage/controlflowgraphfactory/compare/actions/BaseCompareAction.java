/**
 * Copyright (c) 2008-2013, Dr. Garbage Community
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

import org.eclipse.jface.action.Action;
import com.drgarbage.controlflowgraphfactory.compare.GraphMergeViewer;

/**
 * <p>
 * Some common functionality to deal with the compare merge viewer..
 * </p>
 * <p>
 * Subclasses must implement the <code>run()</code> method and a constructor
 * to initialize the compare merge viewer.
 * </p>
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 * 
 * @see #run()
 */
public abstract class BaseCompareAction extends Action {

	protected GraphMergeViewer viewer;
	
	/**
	 * Creates an action object and initializes 
	 * the compare merge viewer.
	 * @param cmv compare merge viewer
	 */
	public BaseCompareAction(GraphMergeViewer cmv){
		viewer = cmv;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public abstract void run();
}
