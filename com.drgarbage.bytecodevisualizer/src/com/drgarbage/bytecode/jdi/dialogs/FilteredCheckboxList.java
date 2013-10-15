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

package com.drgarbage.bytecode.jdi.dialogs;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * A FilteredChecboxList allows to filter elements 
 * and to get/set the checked state.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
class FilteredCheckboxList extends FilteredTree {

	/**
	 * The FilteredCheckboxList Constructor.
	 * @param parent The parent composite where this list will be placed.
	 * @param style styles
	 * @param filter The pattern filter that will be used to filter elements
	 */
	public FilteredCheckboxList(Composite parent, int style, PatternFilter filter) {
		super(parent, style, filter, true);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.FilteredTree#doCreateTreeViewer(org.eclipse.swt.widgets.Composite, int)
	 */
	protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
		return new CheckboxTreeViewer(parent, style);
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.FilteredTree#doCreateFilterText(org.eclipse.swt.widgets.Composite)
	 */
	protected Text doCreateFilterText(Composite parent) {
		Text text = new Text(parent, SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL
				| SWT.ICON_SEARCH);

		GridData data = new GridData();
		data.grabExcessVerticalSpace = false;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.BEGINNING;

		text.setLayoutData(data);
		text.setFont(parent.getFont());

		return text;
	}

}