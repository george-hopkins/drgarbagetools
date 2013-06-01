package com.drgarbage.bytecodevisualizer.view;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;


public class OperandStackReportDialog extends Composite {
	
	/** Font size in the report dialog */
	public static int REPORT_FONT_SIZE = 11;
	
	/** size of the report dialog */
	private Point dialogSize = new Point(800, 480);
	
	
	public OperandStackReportDialog() {
		super(new Shell(SWT.APPLICATION_MODAL | SWT.CLOSE | SWT.ON_TOP), SWT.NONE);
		final Shell shell = getShell();
		shell.setLayout(new FillLayout());
		shell.setText(BytecodeVisualizerMessages.OpenOpstackAnalyseWindowLabel);
		
		/* set content layout and size */
		setLayout(new FillLayout());
		setSize(dialogSize);

		/* Set the report to display at center of the screen */
		Monitor primary = getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = getBounds();

		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);

		/* Allow ESC key to close the report */
		shell.addListener(SWT.Traverse, new Listener() {
			public void handleEvent(Event event) {
				switch (event.detail) {
				case SWT.TRAVERSE_ESCAPE:
					shell.close();
					event.detail = SWT.TRAVERSE_NONE;
					event.doit = false;
					break;
				}
			}
		});
		
		
	}
	
	public void setText(String txt){
		StyledText styledText = new StyledText(this, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		
		/* Use the default text font */
		Font f = JFaceResources.getFont(JFaceResources.TEXT_FONT);
		styledText.setFont(f);
		
		styledText.setText(txt);
		
		styledText.setLayoutData(new GridData(GridData.FILL_BOTH));
		styledText.setEditable(false);

		StyleRange Error = new StyleRange();
		StyleRange Warning = new StyleRange();
//		StyleRange Passed = new StyleRange();
		
		formatColor(Error, styledText,"Error",OperandStackViewPage.RED);
		formatColor(Warning, styledText,"Warning", OperandStackViewPage.ORANGE);
		formatColor(Error, styledText,"Errors",OperandStackViewPage.RED);
		formatColor(Warning, styledText,"Warnings", OperandStackViewPage.ORANGE);

//		styledText.setLineBackground(0, 1, OperandStackViewPage.BLUE);
//		int count = 0;
//		for (int i = -1; (i = styledText.getText().indexOf("\n", i + 1)) != -1; ){
//			count++;
//			if(styledText.getText().indexOf("=== Type based analysis:")==(i+1)) {
//				styledText.setLineBackground(count, 1, OperandStackViewPage.BLUE);
//			}
//		}
//
//		int count2 = 0;
//		for (int i = -1; (i = styledText.getText().indexOf("\n", i + 1)) != -1; ){
//			count2++;
//			if(styledText.getText().indexOf("=== Content based analysis:")==(i+1)) {
//				styledText.setLineBackground(count2, 1, OperandStackViewPage.BLUE);
//			}
//		}
//		
//		int count3 = 0;
//		for (int i = -1; (i = styledText.getText().indexOf("\n", i + 1)) != -1; ){
//			count3++;
//			if(styledText.getText().indexOf("Statistics:")==(i+1)) {
//				styledText.setLineBackground(count3, 1, OperandStackViewPage.BLUE);
//			}
//		}
//			
//		formatColor(Error,styledText,"Size based analysis completed with Errors/Warnings.",OperandStackViewPage.RED);
//		formatColor(Error,styledText,"Type based analysis completed with Errors/Warning.",OperandStackViewPage.RED);
//		formatColor(Passed,styledText,"Size based analysis SUCCESSFULLY PASSED.", OperandStackViewPage.GREEN);
//		formatColor(Passed,styledText,"Type based analysis SUCCESSFULLY PASSED.", OperandStackViewPage.GREEN);
		
	}
	

	/**
	 * Method to format specific string in a StyledText area
	 * @param styleRange
	 * @param styledText
	 * @param toBeFormatted
	 * @param color
	 */
	protected void formatColor(StyleRange styleRange, StyledText styledText,
			String toBeFormatted, Color color) {

		for (int i = -1; (i = styledText.getText().indexOf(toBeFormatted, i + 1)) != -1; ) {
			styleRange.start = i;
			styleRange.length = toBeFormatted.length();
			styleRange.fontStyle = SWT.BOLD;
			styleRange.foreground = color;
			styledText.setStyleRange(styleRange);
		} 
	}

	public void open() {
		getShell().open();
	}

}
