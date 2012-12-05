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

package com.drgarbage.core.views;

import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;

/**
 * The implementation of the Control Flow Graph View Page.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: ControlFlowGraphViewPage.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class ControlFlowGraphViewPage extends Page implements IControlFlowGraphViewPage {
	
	/**
	 * Control flow graph page.
	 */
	private FigureCanvas canvasControlFlowGraph;

	
	/**
	 * Constructs an outline.
	 * @param editor
	 */
	public ControlFlowGraphViewPage(FigureCanvas controlFlowGraphCanvas) {
		super();
		Assert.isNotNull(controlFlowGraphCanvas);
		canvasControlFlowGraph = controlFlowGraphCanvas;
	}

	/* (non-Javadoc)
	 * Method declared on Page
	 */
	public void init(IPageSite pageSite) {
		super.init(pageSite);
	}

	/*
	 * @see IPage#createControl
	 */
	public void createControl(Composite parent) {
		
		/* attach control flow canvas */
		canvasControlFlowGraph.setParent(parent);
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	public void dispose() {
		super.dispose();
		canvasControlFlowGraph.dispose();
		canvasControlFlowGraph = null;

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	public Control getControl() {
		return canvasControlFlowGraph;	
	}

	@Override
	public void setFocus() {
		/* nothing to do */		
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		/* nothing to do */
	}

	public ISelection getSelection() {
		/* nothing to do */
		return null;
	}

	public void removeSelectionChangedListener(	ISelectionChangedListener listener) {
		/* nothing to do */
		
	}

	public void setSelection(ISelection selection) {
		/* nothing to do */
	}
	
}
