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

import org.eclipse.draw2d.PrintFigureOperation;
import org.eclipse.gef.print.PrintGraphicalViewerOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.internal.WorkbenchImages;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.dialogs.ExportDialog;
import com.drgarbage.controlflowgraphfactory.editors.ControlFlowGraphEditor;
import com.drgarbage.utils.Messages;

/**
 * Action for printing.
 *
 * @author Sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: PrintAction.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class PrintAction extends RetargetAction{

	/** Active editor*/
	private ControlFlowGraphEditor editor = null;

	public final static String ID = ActionFactory.PRINT.getId();
	private final static String text = ControlFlowFactoryMessages.PrintAction_Text;
	private final static String toolTipText = ControlFlowFactoryMessages.PrintAction_ToolTipText;
	private final static String headerText = ControlFlowFactoryMessages.PrintAction_HeaderText;
	
	private final static String buttonTILE_Mode = ControlFlowFactoryMessages.PrintAction_buttonTILE_Mode;
	private final static String buttonFITPAGE_Mode = ControlFlowFactoryMessages.PrintAction_buttonFITPAGE_Mode;
	private final static String buttonFIT_WIDTH_Mode = ControlFlowFactoryMessages.PrintAction_buttonFIT_WIDTH_Mode;
	private final static String buttonFIT_HEIGHT_Mode = ControlFlowFactoryMessages.PrintAction_buttonFIT_HEIGHT_Mode;
	
	private int printMode = PrintFigureOperation.TILE;

	public PrintAction() {
		super(ID, text);
		setToolTipText(toolTipText);
		setImageDescriptor(WorkbenchImages.getImageDescriptor("IMG_ETOOL_PRINT_EDIT"));
		setEnabled(true);
		setToolTipText(toolTipText);
	}

	/**
	 * Sets active editor.
	 * @param editor the editor to set
	 */
	public void setActiveEditor(ControlFlowGraphEditor editor) {
		this.editor = editor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.RetargetAction#runWithEvent(org.eclipse.swt.widgets.Event)
	 */
	public void runWithEvent(Event event) {
		run();	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.RetargetAction#run()
	 */
	public void run() {
		if(editor != null){
			ExportDialog dlg = new ExportDialog(editor.getSite().getWorkbenchWindow().getShell());
			dlg.setTitle(headerText);
			dlg.setHeaderText(headerText);
			dlg.setSelectFilePanelVisible(false);
			
			/* install options */
			Group optGroup = new Group(dlg.getParent(), SWT.NONE);
			
			Button b1 = new Button(optGroup, SWT.RADIO);
			b1.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					printMode = PrintFigureOperation.TILE;
				}
			});    
			b1.setText(buttonTILE_Mode);
			b1.setSelection(true);/* default */

			Button b2 = new Button(optGroup, SWT.RADIO);
			b2.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					printMode = PrintFigureOperation.FIT_PAGE;
				}
			});
			b2.setText(buttonFITPAGE_Mode);
			
			
			Button b3 = new Button(optGroup, SWT.RADIO);
			b3.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					printMode = PrintFigureOperation.FIT_WIDTH;
				}
			});
			b3.setText(buttonFIT_WIDTH_Mode);
			
			Button b4 = new Button(optGroup, SWT.RADIO);
			b4.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					printMode = PrintFigureOperation.FIT_HEIGHT;
				}
			});
			b4.setText(buttonFIT_HEIGHT_Mode);
			
			dlg.setOptions(optGroup);
			
			String s = dlg.open();
			if(s == null)
				return;
			
			PrintDialog dialog = new PrintDialog(new Shell(), SWT.NONE);
			PrinterData data = dialog.open();
			if (data != null) {
				PrintGraphicalViewerOperation op = new PrintGraphicalViewerOperation(
						new Printer(data), editor.getGraphicalViewer());
				op.setPrintMode(printMode);
				op.run("print graph");
			}

		}
		else{
			Messages.error(ControlFlowFactoryMessages.ExecutionFailure);
		}
	}
}
