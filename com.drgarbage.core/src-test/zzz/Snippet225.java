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

package zzz;/*

 
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 * 
 * @since 3.2
 */
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

/**
 * Tooltip example snippet: create a balloon tooltip for a tray item.
 */
public class Snippet225 {

public static void main(String[] args) {
	Display display = new Display();
	Shell shell = new Shell(display);
	//Image image = null;
	final ToolTip tip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION);
	tip.setMessage("Here is a message for the user. When the message is too long it wraps. I should say something cool but nothing comes to my mind.");
	Tray tray = display.getSystemTray();
	if (tray != null) {
		TrayItem item = new TrayItem(tray, SWT.NONE);
		//image = new Image(display, Snippet225.class.getResourceAsStream("eclipse.png"));
		//item.setImage(image);
		tip.setText("Notification from a tray item");
		item.setToolTip(tip);
	} else {
		tip.setText("Notification from anywhere");
		tip.setLocation(400, 400);
	}
	Button button = new Button (shell, SWT.PUSH);
	button.setText("Press for balloon tip");
	button.addListener(SWT.Selection, new Listener() {
		public void handleEvent(Event event) {
			tip.setVisible(true);
		}
	});
	button.pack();
	shell.setBounds(50, 50, 300, 200);
	shell.open();
	while (!shell.isDisposed()) {
		if (!display.readAndDispatch()) display.sleep();
	}
	//if (image != null) image.dispose();
	display.dispose();
}
}
