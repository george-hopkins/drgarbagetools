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

package com.drgarbage.controlflowgraphfactory.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.ide.dialogs.FileFolderSelectionDialog;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.graph.DefaultGraphSpecification;
import com.drgarbage.graph.GraphConstants;
import com.drgarbage.graph.IGraphSpecification;

/**
 * Export Dialog for graphs from class and packages.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class ExportGraphFileFolderSelectionDialog extends FileFolderSelectionDialog {

	/* widgets */
	private Button checkButton1, checkButton2, checkButton3;
	
	private IGraphSpecification graphSpecification;

	public ExportGraphFileFolderSelectionDialog(Shell parent, boolean multiSelect,
			int type) {
		super(parent, multiSelect, type);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.SelectionStatusDialog#createButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createButtonBar(Composite parent) {
		
		/* create options */
		Group optGroup = new Group(parent, SWT.NONE);
		GridLayout layout0 = new GridLayout();
		layout0.numColumns = 1; 
		optGroup.setLayout(layout0);
		GridData data0 = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING
				| GridData.VERTICAL_ALIGN_CENTER);
		
		data0.horizontalIndent = 16;
		optGroup.setLayoutData(data0);

		final Combo exportFormatCombo = new Combo(optGroup, SWT.READ_ONLY);
	    exportFormatCombo.setItems(GraphConstants.SUPPORTED_EXPORT_FORMAT_LABELS);
	    exportFormatCombo.select(getGraphSpecification().getExportFormat());
	    exportFormatCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int format = exportFormatCombo.getSelectionIndex();
				getGraphSpecification().setExportFormat(format);
					
					switch(format){
					case GraphConstants.EXPORT_FORMAT_DRGARBAGE_GRAPH:
						
						/* disable properties */
						checkButton1.setEnabled(false);
						checkButton2.setEnabled(false);
						checkButton3.setEnabled(false);
						
						break;
					case GraphConstants.EXPORT_FORMAT_DOT:
					case GraphConstants.EXPORT_FORMAT_GRAPHXML:					
					case GraphConstants.EXPORT_FORMAT_GRAPHML:
						
						/* enable properties */
						checkButton1.setEnabled(true);
						checkButton2.setEnabled(true);
						checkButton3.setEnabled(true);
						
						break;
					default:
						throw new IllegalStateException("Unexpected export format '"+ getGraphSpecification().getExportFormat() +"'");
					}
			}
	    });
	    
		checkButton1 = new Button(optGroup, SWT.CHECK);
		checkButton1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				getGraphSpecification().setExportDecorations(checkButton1.getSelection());
			}
		});    
		checkButton1.setText(ControlFlowFactoryMessages.ExportGraphAction_checkBox1Text);

		checkButton2 = new Button(optGroup, SWT.CHECK);
		checkButton2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				getGraphSpecification().setExportGeometry(checkButton2.getSelection());
			}
		});
		checkButton2.setText(ControlFlowFactoryMessages.ExportGraphAction_checkBox2Text);

		checkButton3 = new Button(optGroup, SWT.CHECK);
		checkButton3.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				getGraphSpecification().setExportComments(checkButton3.getSelection());
			}
		});
		checkButton3.setText(ControlFlowFactoryMessages.ExportGraphAction_checkBox3Text);
		
		/* disable properties */
		checkButton1.setEnabled(false);
		checkButton2.setEnabled(false);
		checkButton3.setEnabled(false);
		
		Composite composite = new Composite(parent, SWT.NONE);
		
		// create a layout with spacing and margins appropriate for the font
		// size.
		GridLayout layout = new GridLayout();
		layout.numColumns = 0; // this is incremented by createButton
		layout.makeColumnsEqualWidth = true;
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		composite.setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END
				| GridData.VERTICAL_ALIGN_CENTER);
		composite.setLayoutData(data);
		composite.setFont(parent.getFont());
		
		
		// Add the buttons to the button bar.
		return super.createButtonBar(parent);
	}
	
	
	public IGraphSpecification getGraphSpecification() {
		if (graphSpecification == null) {
			graphSpecification = new DefaultGraphSpecification();
			graphSpecification
					.setExportFormat(GraphConstants.EXPORT_FORMAT_DRGARBAGE_GRAPH);
		}
		return graphSpecification;
	}

}
