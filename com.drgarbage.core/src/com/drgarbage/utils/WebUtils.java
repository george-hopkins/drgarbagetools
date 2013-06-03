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

package com.drgarbage.utils;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.WorkbenchPlugin;

/**
 * Open web browser with the geven link.
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class WebUtils {

	/**
	 * Open a link
	 */
	public static void openLink(String href) {
		/*
		 * format the href for an html file (file:///<filename.html>
		 * required for Mac only.
		 */
		if (href.startsWith("file:")) { /* $NON-NLS-1$ */
			href = href.substring(5);
			while (href.startsWith("/")) { /* $NON-NLS-1$ */
				href = href.substring(1);
			}
			href = "file:///" + href; /* $NON-NLS-1$ */
		}
		IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
		try {
			IWebBrowser browser = support.getExternalBrowser();
			browser.openURL(new URL(urlEncodeForSpaces(href.toCharArray())));
		}
		catch (MalformedURLException e) {
			openWebBrowserError(href, e);
		}
		catch (PartInitException e) {
			openWebBrowserError(href, e);
		}
	}

	/**
	 * This method encodes the url, removes the spaces from the url and replaces
	 * the same with <code>"%20"</code>. This method is required to fix Bug
	 * 77840.
	 * 
	 * FIXME: use URLEncoder.encode(string, "UTF-8") for URL encoding 
	 * 
	 * @since 3.0.2
	 */
	private static String urlEncodeForSpaces(char[] input) {
		StringBuffer retu = new StringBuffer(input.length);
		for (int i = 0; i < input.length; i++) {
			if (input[i] == ' ') {
				retu.append("%20"); //$NON-NLS-1$
			} else {
				retu.append(input[i]);
			}
		}
		return retu.toString();
	}


	/**
	 * display an error message
	 */
	private static void openWebBrowserError(final String href, final Throwable t) {
		final Shell shell = new Shell();
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				String title = WorkbenchMessages.ProductInfoDialog_errorTitle;
				String msg = NLS.bind(WorkbenchMessages.ProductInfoDialog_unableToOpenWebBrowser, href);
				IStatus status = WorkbenchPlugin.getStatus(t);
				ErrorDialog.openError(shell, title, msg, status);
			}
		});
	}
}
