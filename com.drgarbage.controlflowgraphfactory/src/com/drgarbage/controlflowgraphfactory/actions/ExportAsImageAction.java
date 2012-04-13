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

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.actions.RetargetAction;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.dialogs.ExportDialog;
import com.drgarbage.controlflowgraphfactory.editors.ControlFlowGraphEditor;
import com.drgarbage.controlflowgraphfactory.export.ImageExport;
import com.drgarbage.controlflowgraphfactory.img.ControlFlowFactoryResource;
import com.drgarbage.utils.Messages;

/**
 * Export action.
 *
 * @author Sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: ExportAsImageAction.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class ExportAsImageAction extends RetargetAction {

	/** Active editor*/
	private ControlFlowGraphEditor editor = null;

	public static String ID = "com.drgarbage.controlflowgraphfactory.actions.exportasimage";
	private static String text = ControlFlowFactoryMessages.ExportAsImageAction_Text;
	private static String toolTipText = ControlFlowFactoryMessages.ExportAsImageAction_ToolTipText;
	private final static String headerText = ControlFlowFactoryMessages.ExportAsImageAction_HeaderText;
	
	/* Format constants */
	static class ImageFormat {
	    static final String JPEG = ".jpeg";
		static final String JPG = ".jpg";
	    static final String BMP = ".bmp"; 
	    static final String ICO = ".ico"; 
	    static final String GIF = ".gif"; 
	    static final String PNG = ".png";
	}
	
	/* button format constants */
	private final static String buttonExportAsJPEGText = "JPEG File Interchange Format (*.jpg, *.jpeg)";
	private final static String buttonExportAsBMPText = "Windows Bitmap (*.bmp)";
	private final static String buttonExportAsICOText = "Image Icon (*.ico)";
	private final static String buttonExportAsGIFText = "Graphics Interchange Format (*.gif)";
	private final static String buttonExportAsPNGText = "Portable Network Graphics (*.png)";
	
	/* selected format */
	private String exportAs = ImageFormat.JPEG;

	public ExportAsImageAction() {
		super(ID, text);
		setToolTipText(toolTipText);	
		setImageDescriptor(ControlFlowFactoryResource.export_as_image_16x16);
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
			/* init or reinit variable, see bug #21*/
			exportAs = ImageFormat.JPEG;
			
			ExportDialog dlg = new ExportDialog(editor.getSite().getWorkbenchWindow().getShell());
			dlg.setTitle(headerText);
			dlg.setHeaderText(headerText);
			String[] fileExtensions = new String[] {"*.jpeg", "*.jpg", "*.bmp", "*.ico", "*.png" /*, "*.gif"*/, "*.*"};
			dlg.setFileExtensions(fileExtensions);

			/* create options */
			Group optGroup = new Group(dlg.getParent(), SWT.NONE);

			Button b1 = new Button(optGroup, SWT.RADIO);
			b1.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					exportAs = ImageFormat.JPEG;
				}
			});    
			b1.setText(buttonExportAsJPEGText);
			b1.setSelection(true);/* default */

			Button b2 = new Button(optGroup, SWT.RADIO);
			b2.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					exportAs = ImageFormat.BMP;
				}
			});
			b2.setText(buttonExportAsBMPText);
			
			
			Button b3 = new Button(optGroup, SWT.RADIO);
			b3.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					exportAs = ImageFormat.ICO;
				}
			});
			b3.setText(buttonExportAsICOText);

//			Button b4 = new Button(optGroup, SWT.RADIO);
//			b4.addSelectionListener(new SelectionAdapter() {
//				public void widgetSelected(SelectionEvent event) {
//					exportAs = ImageFormat.GIF;
//				}
//			});
//			b4.setText(buttonExportAsGIFText);

			Button b5 = new Button(optGroup, SWT.RADIO);
			b5.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					exportAs = ImageFormat.PNG;
				}
			});
			b5.setText(buttonExportAsPNGText);
			
			dlg.setOptions(optGroup);
			
			/* open dialog and get the file name */
			String fname = dlg.open(); 

			if(fname == null){
				/* user canceled action*/
				return;
			}

			if(!fname.endsWith(exportAs)){
				if(!(fname.endsWith(ImageFormat.JPG) && exportAs.equals(ImageFormat.JPEG))){
						fname = fname + exportAs;
				}
			}

			/* graph file for default if the path not defined*/
			File f = ((IFileEditorInput)editor.getEditorInput()).getFile().getLocation().toFile();

	        if (fname != null){        	
	        	if(fname.endsWith(".bmp")){        	        	
	        		ImageExport.export(editor.getGraphicalViewer(), fname, f, SWT.IMAGE_BMP);
	        	}
	        	else if(fname.endsWith(".jpg") || fname.endsWith(".jpeg")){
	        		ImageExport.export(editor.getGraphicalViewer(), fname, f, SWT.IMAGE_JPEG);        		
	        	}
	        	else if(fname.endsWith(".ico")){
	        		ImageExport.export(editor.getGraphicalViewer(), fname, f, SWT.IMAGE_ICO);        		
	        	}
	        	else if(fname.endsWith(".png")){
	        		ImageExport.export(editor.getGraphicalViewer(), fname, f, SWT.IMAGE_PNG);
	        	}
	        	else if(fname.endsWith(".gif")){
	        		//ImageExport.export(editor.getGraphicalViewer(), fname, f, SWT.IMAGE_GIF);
	        		Messages.info("The GIF Format is not currently supported.");
	        		//TODO: implement gif format
	        	}
	        	else{
	        		Messages.warning(ControlFlowFactoryMessages.Warning_Unsuported_Image_Format);
	        	}
	        }
		}
        else{
			Messages.error(ControlFlowFactoryMessages.ExecutionFailure);
        }
	}
}
