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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.drgarbage.core.CorePlugin;

public class LinkFactory {

	public static Link createHttpPathLink(Composite parent, int style) {
		Link result = new Link(parent, style);
		result.addListener(SWT.Selection, CorePlugin.getDefault().getLinkListener());
		return result;
	}
	
	public static Link createHttpPathLink(Composite parent, int style, String text) {
		Link result = createHttpPathLink(parent, style);
		result.setText(text);
		return result;
	}
	
	public static Link createPreferencePageLink(Composite parent, int style, String text) {
		Link result = createPreferencePageLink(parent, style);
		result.setText(text);
		return result;
	}
	
	public static Link createPreferencePageLink(final Composite parent, int style) {
		Link result = new Link(parent, style);
		result.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				PreferencesUtil.createPreferenceDialogOn(parent.getShell(), e.text, null, null).open();
			}
		});
		return result;
	}

}
