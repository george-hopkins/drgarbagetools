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

package com.drgarbage.bytecodevisualizer.actions;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.core.Links;
import com.drgarbage.core.jface.AbstractMessageDialog;
import com.drgarbage.core.jface.JFaceUtils;
import com.drgarbage.core.jface.LinkFactory;
import com.drgarbage.core.jface.ListPanel;

/**
 * TODO: add Dr. Garbage icons
 */
public class DebugFunctionalityInfoDialog extends AbstractMessageDialog {

    
	/**
     * Creates a new clean dialog.
     * 
     * @param window the window to create it in
     * @param selection the currently selected projects (may be empty)
     */
    public DebugFunctionalityInfoDialog() {
        super(
                null,
                BytecodeVisualizerMessages.ToggleBreakpointAction_tooltipText_Debug_functionality_is_unavailable_in_this_context_, 
                null, 
                null, 
                WARNING, 
                new String[] {
                	IDialogConstants.OK_LABEL
                },
                0,
                DebugFunctionalityInfoDialog.class.getName()
        );

    }


    @Override
	protected Control createMessageArea(Composite composite) {
    	Composite result = (Composite) super.createMessageArea(composite);
		
	        Composite area = new Composite(result, SWT.NONE);
			GridDataFactory
			.fillDefaults()
			.align(SWT.FILL, SWT.BEGINNING)
			.grab(true, false)
			.hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH),
					SWT.DEFAULT).applyTo(area);

	        
	        GridLayout layout = new GridLayout();
	        layout.marginWidth = layout.marginHeight = 0;
	        layout.numColumns = 1;
	        area.setLayout(layout);
	        
	        Label lbl = null;
	        Link link = null;
	        
	        lbl = new Label(area, SWT.NONE);
	        lbl.setText(BytecodeVisualizerMessages.ToggleBreakpointAction_tooltipText_Debug_functionality_is_unavailable_in_this_context_); 
	        lbl.setLayoutData(new GridData(GridData.FILL_BOTH));
	        JFaceUtils.toBold(lbl);
	        
	        lbl = new Label(area, SWT.NONE);
	        lbl.setText(BytecodeVisualizerMessages.DebugFunctionalityInfoDialog_lbl_Your_class_or_jar_file_needs_); 
	        lbl.setLayoutData(new GridData(GridData.FILL_BOTH));
	        
			ListPanel lp = new ListPanel(area, SWT.NONE, SWT.WRAP);
			lp.setNumbered(true);
			GridData gd = new GridData();
			gd.horizontalAlignment = SWT.FILL; 
			gd.verticalAlignment = SWT.BEGINNING;
			gd.horizontalIndent = IDialogConstants.HORIZONTAL_MARGIN *2;

			lp.getControl().setLayoutData(gd);
			//lp.getControl().setBackground(infoBg);

			lp.addText(BytecodeVisualizerMessages.DebugFunctionalityInfoDialog_lbl_i_included_in_some_java_projects_build_path_and);
			
			String msg = MessageFormat.format(
					BytecodeVisualizerMessages.DebugFunctionalityInfoDialog_lbl_ii_opened_using_0_1_or_similar_feature_of_2_, 
					new Object[]{
							CoreMessages.lbl_Package_Explorer,
							CoreMessages.lbl_Open_Declaration,
							CoreMessages.lbl_Java_Development_Tools_JDT_Plugin
					});
			lp.addText(msg);

			msg = MessageFormat.format(
					BytecodeVisualizerMessages.DebugFunctionalityInfoDialog_link_Please_read_0_this_tutorial_for_more_details_, 
					new Object[] {
							Links.HOW_TO_DEBUG_BYTECODE_WITH_BYTECODE_VISUALIZER
							});
			link = LinkFactory.createHttpPathLink(area, SWT.NONE);
			link.setText(msg);
			gd = new GridData();
			gd.horizontalAlignment = SWT.FILL; 
			gd.verticalAlignment = SWT.BEGINNING;
			gd.verticalIndent = IDialogConstants.VERTICAL_SPACING;
			link.setLayoutData(gd);
	        
			return result;
	}

}
