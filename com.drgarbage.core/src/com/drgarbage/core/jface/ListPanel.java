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

package com.drgarbage.core.jface;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

public class ListPanel {
	
	public static final String BULL = "\u2022";
	protected Composite control;
	protected List<Control> items = new ArrayList<Control>(8);
	protected int itemStyle;
	protected int itemWidthHint = IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH;
	private boolean numbered = false;
	protected int index = 1;
	protected Composite parent;
	protected int style;

	public ListPanel(Composite parent, int style, int itemStyle) {
		this(parent, style, itemStyle, IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
	}
	public ListPanel(Composite parent, int style, int itemStyle, int itemWidthHint) {
		super();
		this.parent = parent;
		this.style = style;
		this.itemStyle = itemStyle;
		this.itemWidthHint = itemWidthHint;
	}
	public void addHttpPathLink(String text) {
		
		createBullet();
		
		Link link = createHttpPathLinkItem();
		link.setText(text);
		
		items.add(link);
	}
	public void addPreferencePageLink(String text) {
		
		createBullet();
		
		Link link = createPreferencePageLinkItem();
		link.setText(text);
		
		items.add(link);
	}
	
	public void addText(String text) {
		createBullet();
		
		Label lbl = createTextItem();
		lbl.setText(text);
		
		items.add(lbl);
	}

	protected void adjustStyle(Control control) {
		control.setFont(parent.getFont());
		control.setBackground(parent.getBackground());
		control.setForeground(parent.getForeground());
		control.setLayoutData(createItemGridData());
	}

	protected Label createBullet() {
		Label lbl = createTextItem();
		
		if (numbered) {
			lbl.setText(String.valueOf(index++) +".");
		}
		else {
			lbl.setText(BULL);
		}
		
		GridData gd = new GridData();
		gd.horizontalIndent = IDialogConstants.HORIZONTAL_MARGIN;
		gd.verticalAlignment = SWT.BEGINNING;
		gd.verticalIndent = IDialogConstants.VERTICAL_SPACING;
		lbl.setLayoutData(gd);
		return lbl;
	}
	
	protected Composite createControl() {
		Composite result = new Composite(parent, style);
		result.setLayout(createControlLayout());
		return result;
	}
	
	protected GridLayout createControlLayout() {
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		gl.horizontalSpacing = IDialogConstants.HORIZONTAL_SPACING;
		gl.verticalSpacing = IDialogConstants.VERTICAL_SPACING;
		return gl;
	}
	
	protected Link createHttpPathLinkItem() {
		Composite parent = getControl();
		Link result = LinkFactory.createHttpPathLink(parent, itemStyle);
		adjustStyle(result);
		return result;
	}

	protected GridData createItemGridData() {
		GridData gd = new GridData();
		gd.horizontalIndent = SWT.FILL;
		gd.verticalAlignment = SWT.BEGINNING;
		gd.verticalIndent = IDialogConstants.VERTICAL_SPACING;
		gd.widthHint = itemWidthHint;
		
		return gd;
	}

	protected Link createPreferencePageLinkItem() {
		Composite parent = getControl();
		Link result = LinkFactory.createPreferencePageLink(parent, itemStyle);
		adjustStyle(result);
		return result;
	}
	
	protected Label createTextItem() {
		Composite parent = getControl();
		Label result = new Label(parent, itemStyle);
		adjustStyle(result);
		return result;
	}
	
	public Composite getControl() {
		if (control == null) {
			control = createControl();
		}
		return control;
	}
	
	public boolean isNumbered() {
		return numbered;
	}

	public void setNumbered(boolean numbered) {
		this.numbered = numbered;
	}

}
