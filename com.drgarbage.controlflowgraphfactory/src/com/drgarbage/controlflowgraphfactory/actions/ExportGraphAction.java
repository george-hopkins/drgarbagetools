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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.actions.RetargetAction;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryPlugin;
import com.drgarbage.controlflowgraphfactory.dialogs.ExportDialog;
import com.drgarbage.controlflowgraphfactory.editors.ControlFlowGraphEditor;
import com.drgarbage.controlflowgraphfactory.export.AbstractExport2;
import com.drgarbage.controlflowgraphfactory.export.GraphDOTExport;
import com.drgarbage.controlflowgraphfactory.export.GraphMlExport;
import com.drgarbage.controlflowgraphfactory.export.GraphXMLExport;
import com.drgarbage.controlflowgraphfactory.img.ControlFlowFactoryResource;
import com.drgarbage.graph.DefaultGraphSpecification;
import com.drgarbage.graph.GraphConstants;
import com.drgarbage.graph.IGraphSpecification;
import com.drgarbage.io.FileExtensions;
import com.drgarbage.io.IoUtils;
import com.drgarbage.javalang.JavalangConstants;
import com.drgarbage.utils.Messages;

/**
 * Export graph diagram as file.
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: ExportGraphAction.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class ExportGraphAction extends RetargetAction {
	private IGraphSpecification graphSpecification;

	public IGraphSpecification getGraphSpecification() {
		if (graphSpecification == null) {
			graphSpecification = new DefaultGraphSpecification();
			graphSpecification.setExportFormat(GraphConstants.EXPORT_FORMAT_DOT);
			graphSpecification.setOpenInEditor(true);
		}
		return graphSpecification;
	}

	/** Active editor*/
	private ControlFlowGraphEditor editor = null;

	/* Constants */
	public  final static String ID = "com.drgarbage.controlflowgraphfactory.actions.exportasgraph";

	/**
	 * Constructor.
	 */
	public ExportGraphAction() {
		super(ID, ControlFlowFactoryMessages.ExportGraphAction_Text);
		setToolTipText(ControlFlowFactoryMessages.ExportGraphAction_ToolTipText);	
		setImageDescriptor(ControlFlowFactoryResource.export_as_graphxml_16x16);
		setEnabled(true);
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
			dlg.setTitle(ControlFlowFactoryMessages.ExportGraphAction_HeaderText);
			dlg.setHeaderText(ControlFlowFactoryMessages.ExportGraphAction_HeaderText);
			dlg.setFileExtensions(IoUtils.toFilters(GraphConstants.SUPPORTED_EXPORT_FORMAT_EXTENSIONS, true));

			/* create options */
			Group optGroup = new Group(dlg.getParent(), SWT.NONE);

			final Combo exportFormatCombo = new Combo(optGroup, SWT.READ_ONLY);
		    exportFormatCombo.setItems(GraphConstants.SUPPORTED_EXPORT_FORMAT_LABELS);
		    exportFormatCombo.select(getGraphSpecification().getExportFormat());
		    exportFormatCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					//FIXME: does this work?
					getGraphSpecification().setExportFormat(exportFormatCombo.getSelectionIndex());
				}
		    });
			
			final Button checkButton1 = new Button(optGroup, SWT.CHECK);
			checkButton1.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					getGraphSpecification().setExportDecorations(checkButton1.getSelection());
				}
			});    
			checkButton1.setText(ControlFlowFactoryMessages.ExportGraphAction_checkBox1Text);

			final Button checkButton2 = new Button(optGroup, SWT.CHECK);
			checkButton2.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					getGraphSpecification().setExportGeometry(checkButton2.getSelection());
				}
			});
			checkButton2.setText(ControlFlowFactoryMessages.ExportGraphAction_checkBox2Text);

			final Button checkButton3 = new Button(optGroup, SWT.CHECK);
			checkButton3.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					getGraphSpecification().setExportComments(checkButton3.getSelection());
				}
			});
			checkButton3.setText(ControlFlowFactoryMessages.ExportGraphAction_checkBox3Text);
			
			dlg.setOptions(optGroup);

			String exportFileName = dlg.open();

			if(exportFileName == null)
				return;

			/* export graph */
			AbstractExport2 exporter = null;
			switch (getGraphSpecification().getExportFormat()) {
			case GraphConstants.EXPORT_FORMAT_DOT:
				exporter = new GraphDOTExport();
				exportFileName = IoUtils.setExtension(exportFileName, FileExtensions.DOT);
				break;
			case GraphConstants.EXPORT_FORMAT_GRAPHXML:
				exporter = new GraphXMLExport();
				exportFileName = IoUtils.setExtension(exportFileName, FileExtensions.XML);
				break;
			case GraphConstants.EXPORT_FORMAT_GRAPHML:
				exporter = new GraphMlExport();
				exportFileName = IoUtils.setExtension(exportFileName, FileExtensions.GRAPHML);
				break;
			default:
				throw new IllegalStateException("Unexpected export format '"+ getGraphSpecification().getExportFormat() +"'");
			}
			exporter.setGraphSpecification(getGraphSpecification());
			
			File editorFile = ((IFileEditorInput)editor.getEditorInput()).getFile().getLocation().toFile();
			
			
			File exportFile = new File(exportFileName);

			/* check if the path exist */
			File p = exportFile.getParentFile();
			if(p == null){
				/* use project directory */
				String newFileName = editorFile.getParentFile().getPath() + File.separator +  exportFileName;
				exportFile = new File(newFileName);				
			}
			else if(!p.exists()){
					Messages.error(ControlFlowFactoryMessages.EXPORT_ERROR_WRONGPATH + ":\n" + p.toString());
					return;
			}

			if (exportFile.exists()){
				if (!Messages.openConfirm(ControlFlowFactoryMessages.EXPORT_FILE_OVERWRITE_TITLE, ControlFlowFactoryMessages.EXPORT_FILE_OVERWRITE_MESSAGE))
				{
					return;
				}
			}
			else{
				/* create a directory if nessesary*/
				exportFile.getParentFile().mkdirs();

			}

			try{

				BufferedOutputStream out = null;
				
				Writer w = null;
				try {
					out = new BufferedOutputStream(new FileOutputStream(exportFile));
					w = new OutputStreamWriter(out, JavalangConstants.UTF_8);
					exporter.write(editor.getModel(), w);
				} finally {
					if (w != null) {
						try {
							w.close();
						} catch (Throwable e) {
						}
					}
					if (out != null) {
						try {
							out.close();
						} catch (Throwable e) {
						}
					}
				}


				Messages.info(ControlFlowFactoryMessages.ExportGraphAction_HeaderText, ControlFlowFactoryMessages.EXPORT_INFO + exportFile.toString());

			} catch (Exception e){
				ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
				Messages.error(ControlFlowFactoryMessages.EXPORT_ERROR);
			}

			
		}
		else{
			Messages.error(ControlFlowFactoryMessages.ExecutionFailure);
		}
	}
}
