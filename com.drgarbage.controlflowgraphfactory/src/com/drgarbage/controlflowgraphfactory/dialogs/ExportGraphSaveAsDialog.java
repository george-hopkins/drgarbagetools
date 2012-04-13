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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SaveAsDialog;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.graph.DefaultGraphSpecification;
import com.drgarbage.graph.GraphConstants;
import com.drgarbage.graph.IGraphSpecification;
import com.drgarbage.io.FileExtensions;
import com.drgarbage.io.IoUtils;

/**
 * Export Dialog for graphs from methods.
 * 
 * @author Sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: ExportGraphSaveAsDialog.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class ExportGraphSaveAsDialog extends SaveAsDialog {
	private IGraphSpecification graphSpecification;
	
	public IGraphSpecification getGraphSpecification() {
		if (graphSpecification == null) {
			graphSpecification = new DefaultGraphSpecification();
			graphSpecification.setExportFormat(GraphConstants.EXPORT_FORMAT_DRGARBAGE_GRAPH);
			graphSpecification.setOpenInEditor(true);
		}
		return graphSpecification;
	}

	/* widgets */
	private Button checkButton1, checkButton2, checkButton3;
	private Text t;
	
	public ExportGraphSaveAsDialog(Shell parentShell) {
		super(parentShell);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.SaveAsDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Control c =  super.createDialogArea(parent);
		
		/* not a gut way to access the input text field, 
		   but it works. Another possibility is to
		   use reflection or reimplement the hole 
		   dialog class */
		Composite com = (Composite)c;
		Control[] children = com.getChildren();
		com = (Composite)children[1];
		children = com.getChildren();
		com = (Composite)children[0];
		children = com.getChildren();
		com = (Composite)children[1];
		children = com.getChildren();
		t = (Text)children[1];
		
		return c;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TrayDialog#createButtonBar(org.eclipse.swt.widgets.Composite)
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
					int exportFormat = exportFormatCombo.getSelectionIndex();
					getGraphSpecification().setExportFormat(exportFormat);
					switch(exportFormat){
					case GraphConstants.EXPORT_FORMAT_DRGARBAGE_GRAPH:
						t.setText(IoUtils.setExtension(t.getText(), FileExtensions.GRAPH));
						
						/* disable properties */
						checkButton1.setEnabled(false);
						checkButton2.setEnabled(false);
						checkButton3.setEnabled(false);
						
						break;
					case GraphConstants.EXPORT_FORMAT_DOT:
						t.setText(IoUtils.setExtension(t.getText(), FileExtensions.DOT));

						/* enable properties */
						checkButton1.setEnabled(true);
						checkButton2.setEnabled(true);
						checkButton3.setEnabled(true);
						
						break;
					case GraphConstants.EXPORT_FORMAT_GRAPHXML:
						t.setText(IoUtils.setExtension(t.getText(), FileExtensions.XML));
						
						/* enable properties */
						checkButton1.setEnabled(true);
						checkButton2.setEnabled(true);
						checkButton3.setEnabled(true);
						
						break;
					case GraphConstants.EXPORT_FORMAT_GRAPHML:
						t.setText(IoUtils.setExtension(t.getText(), FileExtensions.GRAPHML));
						
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
		
		/* Start editor check box */
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = 1; 
		GridData data1 = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING
				| GridData.VERTICAL_ALIGN_CENTER);
		
		data1.horizontalIndent = 16;
		
		Composite composite1 = new Composite(parent, SWT.NONE);
		composite1.setLayout(layout1);
		composite1.setLayoutData(data1);
		
		final Button checkButton4 = new Button(composite1, SWT.CHECK);
		checkButton4.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				getGraphSpecification().setOpenInEditor(checkButton4.getSelection());
			}
		});
		checkButton4.setSelection(true);
		checkButton4.setText("Open newly created graph file in an editor.");//TODO: define constant
		
		/* Buttons */
		Composite composite = new Composite(parent, SWT.NONE);
    	GridLayout layout = new GridLayout();
    	layout.marginWidth = 0;
    	layout.marginHeight = 0;
    	layout.horizontalSpacing = 0;
    	composite.setLayout(layout);
    	composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
    	composite.setFont(parent.getFont());

        Control buttonSection = super.createButtonBar(composite);
        ((GridData) buttonSection.getLayoutData()).grabExcessHorizontalSpace = true;
        
        return composite;
	}

}
