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

package com.drgarbage.core.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;

import com.drgarbage.core.jface.LinkFactory;

public class HttpPathLinkField extends FieldEditor {

	private Link link;
	
    /**
	 * Constructor.
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public HttpPathLinkField(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		createControl(parent);
	}

	public void addListener(int i, Listener listener) {
		link.addListener(i, listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
	 */
	@Override
	protected void adjustForNumColumns(int numColumns) {
		if (link != null) {
			((GridData) link.getLayoutData()).horizontalSpan = numColumns;
		}
	}
	protected Link createLink(Composite parent) {
		return LinkFactory.createHttpPathLink(parent, SWT.NONE);
	}
    /* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		Control control = getLinkControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		control.setLayoutData(gd);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	@Override
	protected void doLoad() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	@Override
	protected void doLoadDefault() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	@Override
	protected void doStore() {
	}
	
	public Link getLinkControl(Composite parent) {
        if (link == null) {
    		link = createLink(parent);
        	link.setFont(parent.getFont());
            String text = getLabelText();
            if (text != null) {
            	link.setText(text);
			}
            link.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                	link = null;
                }
            });
        } else {
            checkParent(link, parent);
        }
        return link;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	@Override
	public int getNumberOfControls() {
		return 1;
	}

	public void removeListener(int i, Listener listener) {
		link.removeListener(i, listener);
	}

	public void setLabelText(String text) {
        super.setLabelText(text);
        if (link != null) {
            link.setText(text);
        }
    }

}