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

package com.drgarbage.visualgraphic.commands;

import org.eclipse.gef.commands.Command;

import com.drgarbage.visualgraphic.model.IDirectEditLabelModel;

/**
 *  DirectEdit command.
 *  @version $Revision: 1523 $
 *  $Id: DirectEditCommand.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class DirectEditCommand extends Command
{
	private String newName, oldName;
	private IDirectEditLabelModel editPart;
	
	public DirectEditCommand(IDirectEditLabelModel l, String s) {
	editPart = l;
	if (s != null)
		newName = s;	
	else
		newName = "";  //$NON-NLS-1$
	}
	
	public void execute() {
		oldName = editPart.getLabel();
		editPart.setLabel(newName);
	}
	
	public void undo() {
		editPart.setLabel(oldName);
	}
}