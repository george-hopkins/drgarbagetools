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

package com.drgarbage.controlflowgraphfactory.actions;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.actions.RetargetAction;

import com.drgarbage.controlflowgraphfactory.editors.ControlFlowGraphEditor;

/**
 * Abstract Action for ordering the vertices.
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: OrderAbstractAction.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public abstract class OrderAbstractAction extends RetargetAction{

	/** Active editor*/
	protected ControlFlowGraphEditor editor = null;

	
	/**
	 * Constructor.
	 * @param actionID
	 * @param text
	 */
	public OrderAbstractAction(String actionID, String text) {
		super(actionID, text);
		setEnabled(false);
	}

	/**
	 * Sets active Editor.
	 * @param editor the editor to set
	 */
	public void setActiveEditor(ControlFlowGraphEditor editor) {
		this.editor = editor;
		
		editor.getGraphicalViewer().addSelectionChangedListener(new ISelectionChangedListener(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			public void selectionChanged(SelectionChangedEvent event) {
				Object o = event.getSelection();
				if(o instanceof IStructuredSelection){
					IStructuredSelection sel = (IStructuredSelection)o;
					if(sel.size() > 1){
						setEnabled(true);
					}
					else{
						setEnabled(false);
					}
				}
			}
			
		});
	}
	
}
