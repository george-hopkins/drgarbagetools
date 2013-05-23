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

package com.drgarbage.bytecodevisualizer.editors;

import java.util.Iterator;

import org.eclipse.debug.internal.ui.InstructionPointerAnnotation;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.contexts.DebugContextEvent;
import org.eclipse.debug.ui.contexts.IDebugContextListener;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.ClassFileDocumentProvider;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jdt.internal.ui.text.SimpleJavaSourceViewerConfiguration;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;


/**
 * A simple Java Source Editor.
 * 
 * @author Sergej Alekseev
 * 
 * @version $Revision$
 * $Id$
 */
public class JavaTextEditor extends TextEditor {

	/**
	 * Text viewer of the editor.
	 */
	private ISourceViewer viewer;
	
	private IDocument doc;
	
//	IDebugContextListener debugListener = new IDebugContextListener(){
//
//		public void debugContextChanged(DebugContextEvent event) {
//			if(DebugUITools.getDebugContext() == null){
//				IAnnotationModel annModel = viewer.getAnnotationModel();
//					if (annModel != null) {
//						Iterator<?> it = annModel.getAnnotationIterator();
//						while(it.hasNext()){
//							Annotation a = (Annotation)it.next();
//							annModel.removeAnnotation(a);
//						}
//					}
//			}
//		}
//		
//	};
	
	public JavaTextEditor() {
		super();
//		DebugUITools.getDebugContextManager().addDebugContextListener(debugListener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextEditor#dispose()
	 */
	public void dispose() {
		super.dispose();
//		DebugUITools.getDebugContextManager().removeDebugContextListener(debugListener);	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#setDocumentProvider(org.eclipse.ui.IEditorInput)
	 */
	protected void setDocumentProvider(IEditorInput input) {
		/* class file from archive */
		if(input instanceof IClassFileEditorInput){
			setDocumentProvider(new ClassFileDocumentProvider());			
		}
		else{/* other classes */
			setDocumentProvider(new FileDocumentProvider());
		}

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createSourceViewer(org.eclipse.swt.widgets.Composite, org.eclipse.jface.text.source.IVerticalRuler, int)
	 */
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		
		viewer = super.createSourceViewer(parent, ruler, styles);
		JavaSourceViewerConfiguration fViewerConfiguration = 
			       new SimpleJavaSourceViewerConfiguration(
			    		   JavaPlugin.getDefault().getJavaTextTools().getColorManager(), 
			    		   JavaPlugin.getDefault().getCombinedPreferenceStore(), 
			    		   null, 
			    		   IJavaPartitions.JAVA_PARTITIONING, 
			    		   true);
		viewer.configure(fViewerConfiguration);
		viewer.setEditable(false);
		
		viewer.showAnnotations(true);
		
		return viewer;
	}

	/**
	 * @return the viewer
	 */
	public ISourceViewer getViewer() {
		return viewer;
	}
	
	/**
	 * Set the text of the viewer. The Editor input is disabled.
	 * @param text
	 */
	public void setDocument(String text){
		doc = new Document(text);
		
		/* highlight java code*/
		JavaPlugin.getDefault().getJavaTextTools().setupJavaDocumentPartitioner( doc, IJavaPartitions.JAVA_PARTITIONING);
		viewer.setDocument(doc);
		//viewer.setDocument(doc, new BytecodeAnnotationModel());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#isEditable()
	 */
	public boolean isEditable() {
		/* disable editing of test*/
		return false;
	}
	
	protected void createActions() {
		super.createActions();
		

		MenuManager cmProvider = new JavaTextEditorMenuManager();
		Menu menu = cmProvider.createContextMenu(viewer.getTextWidget());
		getSourceViewer().getTextWidget().setMenu(menu);
		getSite().registerContextMenu(cmProvider, getSite().getSelectionProvider());
		
	  	/* edit actions */
    	IAction action = getAction(ActionFactory.COPY.getId());
    	action.setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
    	cmProvider.add(action);
    	
    	action = getAction(ITextEditorActionConstants.SELECT_ALL);
    	cmProvider.add(action);

    	cmProvider.add(new Separator());
    	
    	/* find actions */
    	action = getAction(ITextEditorActionConstants.FIND);
    	cmProvider.add(action);
    	action = getAction(ITextEditorActionConstants.FIND_NEXT);
    	cmProvider.add(action);
    	action = getAction(ITextEditorActionConstants.FIND_PREVIOUS);
    	cmProvider.add(action);
    	
    	//Doesn't work, becasuse the action is assigned to the editor input.
    	//action = getAction(ITextEditorActionConstants.GOTO_LINE);
    	//cmProvider.add(action);
    	
    	cmProvider.add(new Separator());
    	
    	/* print actions */
    	action = getAction(ITextEditorActionConstants.PRINT);
    	action.setImageDescriptor(WorkbenchImages.getImageDescriptor("IMG_ETOOL_PRINT_EDIT"));
    	cmProvider.add(action);

    	//cmProvider.add(new Separator());
    	//action = getAction(ITextEditorActionConstants.LINENUMBERS_TOGGLE);
    	//cmProvider.add(action);
    	
    	/* Create ruler context menu */
    	IVerticalRuler ruler = getVerticalRuler();
		MenuManager mm = new MenuManager();
		action = getAction(ITextEditorActionConstants.LINENUMBERS_TOGGLE);
		mm.add(action);		
		
		mm.add(new Separator());
		action = getAction(ITextEditorActionConstants.RULER_PREFERENCES);
		mm.add(action);
		
		Menu m = mm.createContextMenu(ruler.getControl());	
		ruler.getControl().setMenu(m);
	}

	class JavaTextEditorMenuManager extends MenuManager{

		public void addMenuListener(IMenuListener listener) {
	    	/* Hack: supress adding of the exention points to the context menu.
	    	 * To allow extentions delete comment in the next line. */
	    	//super.addMenuListener(listener);
	    }
	    
	}
	
	/**
	 * Selects the given line.
	 * @param the number of the line to be selected. 
	 */
	public void selectLine(int line) {
 
		try {
			int offset= doc.getLineOffset(line);
			ISourceViewer sourceViewer = this.getSourceViewer();
			
			if (sourceViewer == null)
				return;

			//sourceViewer.setSelectedRange(offset, 0);
			selectAndReveal(offset, 0);

		} catch (BadLocationException e) {/*nothing to do */}		
	}
	
	@Override
	public void selectAndReveal(int start, int length) {
		super.selectAndReveal(start, length);
		
		Object o = DebugUITools.getDebugContext();
		
		/* the object is null if the debuger is not active */
		if(o instanceof JDIStackFrame){
			JDIStackFrame stackFrame = (JDIStackFrame)o;

			/* create instruction pointer annotation */
			Annotation annotation = new InstructionPointerAnnotation(stackFrame, 
					"org.eclipse.debug.ui.currentIP", "Debug Current Instruction Pointer", 
					DebugUITools.getImage(IDebugUIConstants.IMG_OBJS_INSTRUCTION_POINTER_TOP));

			/* set the anotation in the anotation model */
			IAnnotationModel annModel = viewer.getAnnotationModel();
			if (annModel != null) {
				annModel.removeAnnotation(annotation);
				Position position = new Position(start, length);
				annModel.addAnnotation(annotation, position);
			}
		}
	}

}
