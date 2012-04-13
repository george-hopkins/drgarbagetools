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

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;

import com.drgarbage.utils.Messages;

/**
 * Export Monitor dialog.
 * Sometimes necessary to avoid invalid access thread exception.
 * 
 * @author Serrej Alekseev
 * $Id: ExportProgressMonitorDialog.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $ 
 */
public class ExportProgressMonitorDialog extends ProgressMonitorDialog {

	private boolean errorDuringExecution = false;
	private String errorMsg;
	
	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public boolean isErrorDuringExecution() {
		return errorDuringExecution;
	}

	public void setErrorDuringExecution(boolean errorDuringExecution) {
		this.errorDuringExecution = errorDuringExecution;
	}

	public ExportProgressMonitorDialog(Shell parent) {
		super(parent);
	}
	
	protected void finishedRun() {
		super.finishedRun();
		
		if(isErrorDuringExecution()){
			Messages.warning(errorMsg);
		}

	}

}
