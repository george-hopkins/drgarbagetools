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

import org.eclipse.debug.core.commands.IStepOverHandler;
import org.eclipse.debug.internal.ui.commands.actions.DebugCommandService;
import org.eclipse.debug.internal.ui.commands.actions.IEnabledTarget;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.debug.ui.contexts.DebugContextEvent;
import org.eclipse.debug.ui.contexts.IDebugContextListener;
import org.eclipse.debug.ui.contexts.IDebugContextService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;

import com.drgarbage.core.jface.BaseAction;

@SuppressWarnings("restriction")
public class DebugViewAction extends BaseAction implements
		IDebugContextListener, IVersion {
	protected IDebugView debugView;

	private DebugCommandService fUpdateService;
	/**
	 * The window this action is working for.
	 */
	private IWorkbenchWindow window;

	public DebugViewAction(String id, String text, IDebugView debugView) {
		super(id, text);
		this.debugView = debugView;

		window = debugView.getSite().getWorkbenchWindow();
		fUpdateService = DebugCommandService.getService(window);
		IDebugContextService service = getDebugContextService();
		String partId = debugView.getSite().getId();
		service.addDebugContextListener(this, partId);
		ISelection activeContext = service.getActiveContext(partId);
		if (activeContext != null) {
			fUpdateService.updateCommand(getCommandType(),
					(IEnabledTarget) this);
		} else {
			setEnabled(getInitialEnablement());
		}

		debugView.setAction(id, this);

	}

	/**
	 * @see org.eclipse.debug.ui.contexts.IDebugContextListener#debugContextChanged(org.eclipse.debug.ui.contexts.DebugContextEvent)
	 */
	public void debugContextChanged(DebugContextEvent event) {
		fUpdateService.postUpdateCommand(getCommandType(),
				(IEnabledTarget) this);
	}

	@Override
	public void dispose() {
		super.dispose();

		debugView.setAction(getId(), null);

		IDebugContextService service = getDebugContextService();
		if (debugView != null) {
			service.removeDebugContextListener(this, debugView.getSite()
					.getId());
		} else {
			service.removeDebugContextListener(this);
		}
		this.debugView = null;
	}

	protected Class getCommandType() {
		return IStepOverHandler.class;
	}

	/**
	 * Returns the context service this action linked to.
	 * 
	 * @return the debug context service object
	 */
	protected IDebugContextService getDebugContextService() {
		return DebugUITools.getDebugContextManager().getContextService(window);
	}

	/**
	 * Returns whether this action should be enabled when initialized and there
	 * is no active debug context.
	 * 
	 * @return false, by default
	 */
	protected boolean getInitialEnablement() {
		return false;
	}
}
