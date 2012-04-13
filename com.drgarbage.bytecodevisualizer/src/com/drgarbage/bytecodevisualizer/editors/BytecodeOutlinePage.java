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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.viewsupport.AppearanceAwareLabelProvider;
import org.eclipse.jdt.internal.ui.viewsupport.DecoratingJavaLabelProvider;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.drgarbage.asm.render.impl.OutlineElement;
import com.drgarbage.asm.render.impl.OutlineElementField;
import com.drgarbage.asm.render.impl.OutlineElementMethod;
import com.drgarbage.asm.render.impl.OutlineElementType;
import com.drgarbage.asm.render.intf.IClassFileDocument;
import com.drgarbage.asm.render.intf.IOutlineElementField;

/**
 * The content outline page of the Class File Editor. It does not react on domain changes.
 * It is specified to show the content of class  files by using the IJavaElement interface.
 * ClassFile -> IType-> IField, IMethod ...
 * 
 * @author Sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: BytecodeOutlinePage.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class BytecodeOutlinePage extends Page implements IContentOutlinePage {

	/**
	 * Root element of the outline tree.
	 */
	private IJavaElement fInput;

	/**
	 * The outline viewer.
	 */
	private JavaOutlineViewer fOutlineViewer;

	/**
	 * The editor.
	 */
	private BytecodeEditor fEditor;
	
	/**
	 * Selection Listener of the tree outline.
	 */
	private List<ISelectionChangedListener> outlineChangeListener = new ArrayList<ISelectionChangedListener>();

	/**
	 * Constructs an outline.
	 * @param editor
	 */
	public BytecodeOutlinePage(BytecodeEditor editor) {
		super();
		Assert.isNotNull(editor);
		fEditor = editor;
	}

	/* (non-Javadoc)
	 * Method declared on Page
	 */
	public void init(IPageSite pageSite) {
		super.init(pageSite);
	}

	/*
	 * @see IPage#createControl
	 */
	public void createControl(Composite parent) {
		Tree tree= new Tree(parent, SWT.MULTI);

		/* copied from Java editor */
		AppearanceAwareLabelProvider lprovider= new AppearanceAwareLabelProvider(
				AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS |  JavaElementLabels.F_APP_TYPE_SIGNATURE | JavaElementLabels.ALL_CATEGORY,
				AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS
		);

		fOutlineViewer= new JavaOutlineViewer(tree);
		fOutlineViewer.setContentProvider(new ChildrenProvider());
		fOutlineViewer.setLabelProvider(new DecoratingJavaLabelProvider(lprovider));
		fOutlineViewer.setInput(fInput);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	public void dispose() {
		super.dispose();
		
		if (fEditor == null)
			return;

		fOutlineViewer= null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	public Control getControl() {
		return fOutlineViewer.getControl();	
	}

	/**
	 * Sets the model of the class file content.
	 * @param inputElement
	 */
	public void setInput(IJavaElement inputElement) {
		fInput= inputElement;

		if (fOutlineViewer != null) {
			fOutlineViewer.setInput(fInput);
		}
	}

	/*
	 * @see Page#setFocus()
	 */
	public void setFocus() {
		if (fOutlineViewer != null)
			fOutlineViewer.getControl().setFocus();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		outlineChangeListener.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		outlineChangeListener.remove(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		/* ignore */
	}

	/**
	 * Sets selection of the method selected in the editor.
	 * @param selection
	 */
	public void setSelection(Object selection) {

		if(fOutlineViewer == null){
			return;
		}

		TreeItem []  treeItems = fOutlineViewer.getTree().getItems();
		for(TreeItem item: treeItems){
			Object o = item.getData();
			
			/* if class item is found */
			if(o instanceof OutlineElementType){
				/* check if classis selected */
				if(selection instanceof IClassFileDocument){
						fOutlineViewer.getTree().setSelection(item);
						fOutlineViewer.refresh(true);
						break;
				}
				
				/* for all methods  and fields */
				treeItems = item.getItems();
				for(TreeItem item2: treeItems){
					o = item2.getData();
				
					if(o instanceof OutlineElementField){
						OutlineElementField f = (OutlineElementField) o;
						if(f.getFieldSection().equals(selection)){
							fOutlineViewer.getTree().setSelection(item2);
							fOutlineViewer.refresh(true);
							break;
						}
					}

					if(o instanceof OutlineElementMethod){
						OutlineElementMethod m = (OutlineElementMethod)o;
						if(m.getMethodSection().equals(selection)){
							fOutlineViewer.getTree().setSelection(item2);
							fOutlineViewer.refresh(true);
							break;
						}
					}
					
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		return null;
	}

	/**
	 * Outline viewer class.
	 */
	class JavaOutlineViewer extends TreeViewer {
		public JavaOutlineViewer(Tree tree) {
			super(tree);
			setAutoExpandLevel(ALL_LEVELS);
			setUseHashlookup(true);
			
			addSelectionChangedListener(new ISelectionChangedListener(){

				/* (non-Javadoc)
				 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
				 */
				public void selectionChanged(SelectionChangedEvent event) {
					TreeSelection sel = (TreeSelection)event.getSelection();
					Object o = sel.getFirstElement();
					if(o instanceof OutlineElement){
						OutlineElement element = (OutlineElement)o;
						int bytecodeDocumentLine = element.getBytecodeDocumentLine();

						if(element instanceof IType){
							IType type = (IType)o;
							String name = type.getElementName();
							fEditor.selectBytecodeLineAndReveal(bytecodeDocumentLine, name, IJavaElement.CLASS_FILE);
						}
						else if(element instanceof IOutlineElementField){
							IOutlineElementField field = (IOutlineElementField)o;
							String name = field.getFieldSection().getName();
							fEditor.selectBytecodeLineAndReveal(bytecodeDocumentLine, name, IJavaElement.FIELD);
						}
						else if(element instanceof IMethod){
							IMethod method = (IMethod)o;
							String name = method.getElementName();
							fEditor.selectBytecodeLineAndReveal(bytecodeDocumentLine, name, IJavaElement.METHOD);
						}
						else{
							/* default */
							fEditor.selectLineAndRevaluate2(bytecodeDocumentLine);
						}
					}
					
					/* update registered listener*/
					Iterator<ISelectionChangedListener> it = outlineChangeListener.iterator();
					while(it.hasNext()){
						it.next().selectionChanged(event);
					}
				}

			});
		}
	}

	/**
	 * Content provider for the children of an ICompilationUnit or
	 * an IClassFile
	 * @see ITreeContentProvider
	 */
	static class ChildrenProvider implements ITreeContentProvider {

		/**
		 * Empty array.
		 */
		static Object[] NO_CHILDREN= new Object[0];

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parent) {
			if (parent instanceof IParent /*&& parent instanceof ClassFile */) {
				IParent c= (IParent) parent;
				try {
					return c.getChildren();
				} catch (JavaModelException e) {
					e.printStackTrace(System.err);
				}
			}
			return NO_CHILDREN;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object parent) {
			return getChildren(parent);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
			/* ignore */
		}

		/*
		 * @see IContentProvider#inputChanged(Viewer, Object, Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			/* ignore */
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object parent) {
			if (parent instanceof IParent) {
				IParent c= (IParent) parent;
				try {
					IJavaElement[] children= c.getChildren();
					return (children != null && children.length > 0);
				} catch (JavaModelException x) {
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=38341
					// don't log NotExist exceptions as this is a valid case
					// since we might have been posted and the element
					// removed in the meantime.
					if (JavaPlugin.isDebug() || !x.isDoesNotExist())
						JavaPlugin.log(x);
				}
			}
			return false;
		}
	}

	/**
	 * Returns the tree outline viewer.
	 * @return the viewer
	 */
	public JavaOutlineViewer getOutlineViewer() {
		return fOutlineViewer;
	}
	
}
