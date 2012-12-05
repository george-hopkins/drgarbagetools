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
package com.drgarbage.core.views;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.views.ViewsPlugin;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

import com.drgarbage.core.CoreMessages;

/**
 * The View for the control flow graph visualization.
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: ControlFlowGraphView.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class ControlFlowGraphView extends PageBookView {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.PageBookView#createDefaultPage(org.eclipse.ui.part.PageBook)
	 */
	protected IPage createDefaultPage(PageBook book) {
        MessagePage page = new MessagePage();
        initPage(page);
        page.createControl(book);
        page.setMessage(CoreMessages.ControlFlowGraphView_lbl_not_available);
        return page;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.PageBookView#doCreatePage(org.eclipse.ui.IWorkbenchPart)
	 */
	protected PageRec doCreatePage(IWorkbenchPart part) {
        // Try to get a view page.
        Object obj = ViewsPlugin.getAdapter(part, IControlFlowGraphViewPage.class, false);
        if (obj instanceof IControlFlowGraphViewPage) {
            IControlFlowGraphViewPage page = (IControlFlowGraphViewPage) obj;
            if (page instanceof IPageBookViewPage) {
				initPage((IPageBookViewPage) page);
			}
            page.createControl(getPageBook());
            return new PageRec(part, page);
        }
        // There is no page
        return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.PageBookView#doDestroyPage(org.eclipse.ui.IWorkbenchPart, org.eclipse.ui.part.PageBookView.PageRec)
	 */
	protected void doDestroyPage(IWorkbenchPart part, PageRec rec) {
        IControlFlowGraphViewPage page = (IControlFlowGraphViewPage) rec.page;
        page.dispose();
        rec.dispose();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.PageBookView#getBootstrapPart()
	 */
	protected IWorkbenchPart getBootstrapPart() {
        IWorkbenchPage page = getSite().getPage();
        if (page != null) {
			return page.getActiveEditor();
		}

        return null;
	}
	
 
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.PageBookView#isImportant(org.eclipse.ui.IWorkbenchPart)
	 */
	protected boolean isImportant(IWorkbenchPart part) {        
		boolean b = false;
		
		/* we are interested only on an editor */
		if(part instanceof IEditorPart){
			b = true;
		}
		
		if(part instanceof IControlFlowGraphView){
			/* turn off if the graph has to be shown in the editor */
			b = ((IControlFlowGraphView)part).isShowGraphInSeparateView();
		}
		
        return b;
	}

}
