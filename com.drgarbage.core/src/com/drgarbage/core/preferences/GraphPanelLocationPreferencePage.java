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

package com.drgarbage.core.preferences;

import java.text.MessageFormat;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;

import com.drgarbage.core.CoreConstants;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.core.CorePlugin;

/**
 * Control Flow View Page. 
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: GraphPanelLocationPreferencePage.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class GraphPanelLocationPreferencePage extends AbstractFieldPreferencePage {

	public GraphPanelLocationPreferencePage() {
		super(CorePlugin.getDefault().getPreferenceStore());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		// not required anymore
		//addField(new BooleanFieldEditor(BytecodeVizualizerPreferenceConstants.GENERAL_SHOW_SOURCECODE_IF_AVALIABLE, BytecodeVisualizerPreferencesMessages.GeneralShowSourceCodeIfAvailable, getFieldEditorParent()));		
	    addField(new RadioGroupFieldEditor(CorePreferenceConstants.GRAPH_PANEL_LOCATION,
	    		"\n" + CoreMessages.GraphPanelLocationPreferencePage_radioGroup_Graph_Panel_Location, 
	    		1,
	            new String[][] { 
	    			{CoreMessages.GraphPanelLocationPreferencePage_radio_Editor, CorePreferenceConstants.GRAPH_PANEL_LOCATION_EDITOR},
	    			{
	    				CoreMessages.GraphPanelLocationPreferencePage_radio_Separate_View, 
	    				CorePreferenceConstants.GRAPH_PANEL_LOCATION_SEPARATE_VIEW }}, 
	    		getFieldEditorParent()));
	    
	    String path = MessageFormat.format(
	    		CoreMessages.GraphPanelLocationPreferencePage_lbl_Show_view_path, 
	    		new Object[] {CoreMessages.ControlFlowGraphView_title});
	    
	    String msg = MessageFormat.format(
	    		CoreMessages.GraphPanelLocationPreferencePage_lbl_Show_view_manually, 
	    		new Object[] {
	    				CoreMessages.ControlFlowGraphView_title,
	    				path
	    				});
	    addField(new LabelField("Lbl", msg, getFieldEditorParent()));
	}
	
	@Override
	public boolean performOk() {
		EventGroupingListener eventGroupingListener = createEventGroupingListener();
		IPreferenceStore store = getPreferenceStore();
		store.addPropertyChangeListener(eventGroupingListener);
		
		/* The following line eventually fires several Property Change Events.
		 * EventGroupingListener is able to detect if any of our properties has changed */
		boolean superOk = super.performOk();
		
		if (superOk && eventGroupingListener.isSomePropertyChanged()) {
			/* reopen editors */
			CorePlugin.reopenEditors(new String[] {CoreConstants.BYTECODE_VISUALIZER_EDITOR_ID, CoreConstants.SOURCECODE_VISUALIZER_EDITOR_ID});
			
			/* Open the graph view if necessary */
			String s = getPreferenceStore().getString(CorePreferenceConstants.GRAPH_PANEL_LOCATION);
			if (CorePreferenceConstants.GRAPH_PANEL_LOCATION_SEPARATE_VIEW.equals(s)) {
				CorePlugin.ensureGraphViewShown();
			}
		}

		/* tidy up */
		store.removePropertyChangeListener(eventGroupingListener);
		
		return superOk;
		
	}


}
