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
package com.drgarbage.bytecodevisualizer.view;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.views.ViewsPlugin;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.drgarbage.asm.render.intf.IClassFileDocument;
import com.drgarbage.asm.render.intf.IMethodSection;
import com.drgarbage.bytecodevisualizer.editors.BytecodeDocumentProvider;
import com.drgarbage.bytecodevisualizer.editors.BytecodeEditor;


/**
 * Operand Stack View.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: OperandStackView.java 45 2013-02-08 19:03:38Z salekseev $
 */
public class OperandStackView extends PageBookView {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.PageBookView#doCreatePage(org.eclipse.ui.IWorkbenchPart)
	 */
	protected PageRec doCreatePage(IWorkbenchPart part) {
		
		/* set reference in the editor */
        if(part instanceof BytecodeEditor){
        	BytecodeEditor be = (BytecodeEditor) part;
        	OperandStackViewPage page = null;
        	Object obj = ViewsPlugin.getAdapter(part, OperandStackViewPage.class, false);
            if (obj instanceof OperandStackViewPage) {
            	page = (OperandStackViewPage) obj;
            }
            else {
            	/* should never happen */
            	page = new OperandStackViewPageIml();
                page.setEditor(be);
            }
        	
			initPage((IPageBookViewPage) page);
            page.createControl(getPageBook());
            
            /* set input */
            IDocumentProvider docProvider = be.getDocumentProvider();
            if(docProvider instanceof BytecodeDocumentProvider){
            	BytecodeDocumentProvider byteCodeDocumentProvider = (BytecodeDocumentProvider) be.getDocumentProvider();
            	if(byteCodeDocumentProvider!= null){
            		IClassFileDocument classFileDoc = byteCodeDocumentProvider.getClassFileDocument(); 
            		int line = be.getSelectedLine();
            		if(classFileDoc.isLineInMethod(line/* changed to 0-based */)){
            			IMethodSection m = classFileDoc.findMethodSection(line/* changed to 0-based */);
            			if(m!= null){
            				page.setInput(m);
            			}
            		}
            	}
            }
            
            return new PageRec(part, page);
        }

        return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.PageBookView#getBootstrapPart()
	 */
	@Override
	protected IWorkbenchPart getBootstrapPart() {
        IWorkbenchPage page = getSite().getPage();
        if (page != null) {
			return page.getActiveEditor();
		}

        return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.AbstractDebugView#isImportant(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		/* we are interested only on an editor */
		return part instanceof IEditorPart ? true : false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.PageBookView#createDefaultPage(org.eclipse.ui.part.PageBook)
	 */
	protected IPage createDefaultPage(PageBook book) {
        MessagePage page = new MessagePage();
		page = new MessagePage();
        initPage(page);
        page.createControl(book);
        page.setMessage("OperandStack View is not available");//TODO: define constant for default page
        
        return page;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.PageBookView#doDestroyPage(org.eclipse.ui.IWorkbenchPart, org.eclipse.ui.part.PageBookView.PageRec)
	 */
	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec rec) {
        rec.page.dispose();
	}
}
