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

package com.drgarbage.controlflowgraphfactory.preferences;

import java.text.MessageFormat;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryPlugin;
import com.drgarbage.core.CoreConstants;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.core.preferences.AbstractFieldPreferencePage;
import com.drgarbage.core.preferences.IndentBooleanFieldEditor;
import com.drgarbage.core.preferences.PreferencePageLinkField;

/**
 * Main Page of the Control Flow Factory Preferences.
 *
 * @author sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: ControlFlowFactoryPreferencePage.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class ControlFlowFactoryPreferencePage extends AbstractFieldPreferencePage {

	public ControlFlowFactoryPreferencePage(){
		super(GRID, ControlFlowFactoryPlugin.getDefault().getPreferenceStore());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		
		/* set links to the default preferences */
		String msg = MessageFormat.format(
				ControlFlowFactoryMessages.ControlFlowFactory_ConfigurationBlock_link, 
				new Object [] {CoreConstants.PREFPAGEID_FIGURE_PREFERENCES, CoreMessages.GraphColorsPreferencePage_title});

		addField(new PreferencePageLinkField("Lbl1", msg, getFieldEditorParent()));
		
		addField( new IndentBooleanFieldEditor(ControlFlowFactoryPreferenceConstants.USE_GRADIENT_FILL_COLOR, ControlFlowFactoryMessages.UseGradientFillColor, getFieldEditorParent()));
		
	}

}
