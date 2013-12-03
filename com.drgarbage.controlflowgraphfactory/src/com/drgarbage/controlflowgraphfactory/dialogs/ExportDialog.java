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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.img.ControlFlowFactoryResource;
import com.drgarbage.core.img.CoreImg;

/**
 * Export Dialog of the GraphXML export action.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class ExportDialog extends Dialog {

	/**
	 * The entered file name or null.
	 */
	private String input = null;

	/**
	 * Options of the dialog.
	 */
	private Group optionsGroup = null;

	/**
	 * The title of the dialog.
	 */
	private String title = "Export";
	
	/**
	 * The image in the title line.
	 */
	private Image titleImage = CoreImg.drgarbage_16x16.createImage();

	/**
	 * The image in the header.
	 */
	private Image headerImage = ControlFlowFactoryResource.export_540x64.createImage();
	
	/**
	 * The Text in the header.
	 */
	private String headerText = "";
	
	/**
	 * The panel for selection of the output files.
	 */
	private boolean selectFilePanel = true;
	
	/**
	 * The file extentions used in the file dialog
	 */
	private String[] fileExtensions = new String[] { "*.*" };
	
	/* Constants */
	private final String outputFileGroupText = ControlFlowFactoryMessages.EXPORT_DIALOG_outputFileGroupText;
	private final String browserFileButtonText = ControlFlowFactoryMessages.EXPORT_DIALOG_browserFileButtonText;
	private final String okButtonText = ControlFlowFactoryMessages.EXPORT_DIALOG_okButtonText;
	private final String cancelButtonText = ControlFlowFactoryMessages.EXPORT_DIALOG_cancelButtonText;
	private final String fileDialogTitle = ControlFlowFactoryMessages.EXPORT_DIALOG_fileDialogTitle;
	private final String optionsGroupText = ControlFlowFactoryMessages.EXPORT_DIALOG_optionsGroupText;

	/**
	 * Constructor.
	 * @param parent
	 */
	public ExportDialog(Shell parent) {
		super(parent,  SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		setText(title);               
	}

	/**
	 * Sets the options widget.
	 * @param the options group widget
	 */
	public void setOptions(Group optionsGroup) {
		this.optionsGroup = optionsGroup;
	}

	/**
	 * Opens the dialog and returns the input
	 *
	 * @return String
	 */
	public String open() {
		/*  Create the dialog window */
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(title);
		shell.setImage(titleImage);
		createContents(shell);
		shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		/* Return the entered value, or null */
		return input;
	}

	/**
	 * Creates the dialog's contents
	 *
	 * @param shell the dialog window
	 */
	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(1, true));

		/* header*/
		Rectangle rect = createHeader(shell);

		/* file output field */
		final Text fileOut = createOutputFilePanel(shell, rect);

		/* options if set*/
		createOptions(shell);

		/* footer */
		createFooter(shell, fileOut);
	}

	/**
	 * Creates the header of the dialog and returns rectangle object.
	 * Rectangle object can be used for calculation of the dialog size. 
	 * @param shell
	 * @return bound
	 */
	private Rectangle createHeader(final Shell shell){    
		Composite comp = new Composite(shell, SWT.NONE);

		/* add text label */
		Label label = new Label(comp, SWT.NONE);

		Font initialFont = label.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(10);
			fontData[i].setStyle(SWT.BOLD);//SWT.ITALIC
		}

		Font newFont = new Font(label.getDisplay(), fontData);
		label.setFont(newFont);

		label.setText(headerText);
		label.setBackground(new Color(null, 255, 255, 255));
		label.setBounds(20, 20, 160, 40);

		/* add image */
		Label lImage = new Label(comp, SWT.NONE);
		lImage.setImage(headerImage);
		lImage.setBounds(headerImage.getBounds());

		return lImage.getBounds();
	}

	/**
	 * Creates the fileInput filed object and returns
	 * reference to this field.
	 * @param shell
	 * @param rect
	 * @return fileOut String reference
	 */
	private Text createOutputFilePanel(final Shell shell, final Rectangle rect){
		
		if(!selectFilePanel)
			return null; 
		
		/* Display the input box */
		Group fileGroup = new Group(shell, SWT.NONE);
		fileGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		fileGroup.setText(outputFileGroupText);

		final Text text = new Text(fileGroup, SWT.BORDER);
		text.setBounds(10, 20, rect.width / 2 + rect.width / 4, text.getLineHeight()* 2 - text.getLineHeight() / 4);

		/**
		 * Create the define file button and add a handler so that pressing it
		 * will set input to the entered value
		 */
		Button file = new Button(fileGroup, SWT.PUSH);
		file.setText(browserFileButtonText);
		file.setBounds(text.getBounds().width + 40, text.getBounds().y, 80, 24);
		file.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
				fileDialog.setText(fileDialogTitle);
				fileDialog.setFilterExtensions(fileExtensions);

				String fname = fileDialog.open();
				if(fname != null)
					text.setText(fname);
			}
		});

		return text;
	}

	/**
	 * Creates options if they set.
	 * @param shell
	 */
	private void createOptions(final Shell shell){
		if(optionsGroup == null)
			return;

		/* Create options */
		optionsGroup.setParent(shell);

		optionsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		optionsGroup.setText(optionsGroupText);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		optionsGroup.setLayout(layout);
	}


	/**
	 * Creates the footer with two buttons Ok and Cansel.
	 * @param shell
	 * @param fileOut
	 */
	private void createFooter(final Shell shell,  final Text fileOut){
		Composite comp = new Composite(shell, SWT.NONE);

		/**
		 * Create the OK button and add a handler  
		 * so that pressing it will set input              
		 * to the entered value
		 */
		Button ok = new Button(comp, SWT.PUSH);
		ok.setText(okButtonText);
		ok.setBounds(180, 0, 80, 24);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				
				if(fileOut != null){
					input = fileOut.getText();
				}
				else
					input = "";
				shell.close();
			}
		});

		/**
		 * Create the cancel button and add a handler
		 * so that pressing it will set input to null
		 */
		Button cancel = new Button(comp, SWT.PUSH);
		cancel.setText(cancelButtonText);;
		cancel.setBounds(270, 0, 80, 24);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				input = null;
				shell.close();
			}
		});

		/**
		 * Set the OK button as the default, so
		 * user can type input and press Enter
		 * to dismiss
		 */
		shell.setDefaultButton(ok);
	}

	/**
	 * Gets the title.
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the files extentions used in the file dialog.
	 * @return fileExtensions
	 */
	public String[] getFileExtensions() {
		return fileExtensions;
	}

	/**
	 * Sets the files extentions used in the file dialog.
	 * @param fileExtensions
	 */
	public void setFileExtensions(String[] fileExtensions) {
		this.fileExtensions = fileExtensions;
	}

	/**
	 * Gets the title image.
	 * @return the titleImage
	 */
	public Image getTitleImage() {
		return titleImage;
	}

	/**
	 * Stes the title image.
	 * @param titleImage
	 */
	public void setTitleImage(Image titleImage) {
		this.titleImage = titleImage;
	}

	/**
	 * Gets the image from the header.
	 * @return headerImage
	 */
	public Image getHeaderImage() {
		return headerImage;
	}

	/**
	 * Set the image in the header.
	 * @param headerImage 
	 */
	public void setHeaderImage(Image headerImage) {
		this.headerImage = headerImage;
	}

	/**
	 * Gets the header text.
	 * @return the headerText
	 */
	public String getHeaderText() {
		return headerText;
	}

	/**
	 * Sets the header text.
	 * @param headerText
	 */
	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}

	/**
	 * Returns true if the select file panel is visible.
	 * @return true or false
	 */
	public boolean isSelectFilePanelVisible() {
		return selectFilePanel;
	}

	/**
	 * Sets the select file panel visible.
	 * @param true for visible otherweise false
	 */
	public void setSelectFilePanelVisible(boolean b) {
		this.selectFilePanel = b;
	}
}
