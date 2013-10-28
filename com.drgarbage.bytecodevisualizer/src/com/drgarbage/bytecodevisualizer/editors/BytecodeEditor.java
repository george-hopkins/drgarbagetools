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
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.IInstructionPointerPresentation;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jdt.internal.ui.javaeditor.InternalClassFileEditorInput;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditorBreadcrumb;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.internal.texteditor.LineNumberColumn;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.rulers.IColumnSupport;
import org.eclipse.ui.texteditor.rulers.RulerColumnDescriptor;
import org.eclipse.ui.texteditor.rulers.RulerColumnRegistry;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.drgarbage.asm.render.intf.IClassFileDocument;
import com.drgarbage.asm.render.intf.IDocumentUpdateListener;
import com.drgarbage.asm.render.intf.IFieldSection;
import com.drgarbage.asm.render.intf.IMethodSection;
import com.drgarbage.asm.render.intf.IOutlineElementField;
import com.drgarbage.bytecode.ByteCodeConstants;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.bytecodevisualizer.actions.ActivateBasicblockGraphViewAction;
import com.drgarbage.bytecodevisualizer.actions.ActivateBytecodeGraphViewAction;
import com.drgarbage.bytecodevisualizer.actions.BytecodevizualizerActionBarContributor;
import com.drgarbage.bytecodevisualizer.actions.ExportGraphAndOpenWithControlflowgraphFactoryAction;
import com.drgarbage.bytecodevisualizer.actions.ToggleBytecodeBreakpointAction;
import com.drgarbage.bytecodevisualizer.preferences.BytecodeVisualizerPreferenceConstats;
import com.drgarbage.bytecodevisualizer.view.OperandStackView;
import com.drgarbage.bytecodevisualizer.view.OperandStackViewPage;
import com.drgarbage.bytecodevisualizer.view.OperandStackViewPageIml;
import com.drgarbage.core.CoreConstants;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.core.CorePlugin;
import com.drgarbage.core.img.CoreImg;
import com.drgarbage.core.preferences.CorePreferenceConstants;
import com.drgarbage.core.views.ControlFlowGraphView;
import com.drgarbage.core.views.ControlFlowGraphViewPage;
import com.drgarbage.core.views.IControlFlowGraphView;
import com.drgarbage.core.views.IControlFlowGraphViewPage;
import com.drgarbage.utils.ClassFileDocumentsUtils;
import com.drgarbage.utils.Messages;

/**
  * Byte Code Editor.
  *
  * @author Sergej Alekseev and Peter Palaga
  * @version $Revision:25 $
  * $Id:ClassFileEditor.java 25 2007-04-01 17:56:22Z aleks $
  */
public class BytecodeEditor extends JavaEditor 
	implements IDocumentUpdateListener, 
		IControlFlowGraphView, 
		IClassFileEditor,
		BytecodeVisualizerPreferenceConstats
	{

//	/* bug#91 NullPointerException if license has been expired. */
//	class StructuredSelection2 extends StructuredSelection {
//		public Object getFirstElement() {
//			return new JavaEditorBreadcrumb(getEditor());
//		}
//	}
	
	/** Key constants */
	private static final int ARROW_DOWN = SWT.KEYCODE_BIT + 2; 	//keyCode=16777218 (KeyEvent KeyDown)
	
	

	private static final int ARROW_UP = SWT.KEYCODE_BIT + 1; 	//keyCode=16777217 (KeyEvent KeyUp)

	public static final int TAB_INDEX_BYTECODE = 0;
	public static final int TAB_INDEX_SOURCE = 1;


	/**
	 * Returns true if the bytecode visualizer is a default editor for 
	 * *.class files otherwise false.
	 * @return true or false
	 */
	public static boolean isBytecodeVisualizerDefault(){

		try {
			IEditorDescriptor editor = IDE.getEditorDescriptor("x.class"); /* just a class file name */

			if(editor.getId().equals(CoreConstants.BYTECODE_VISUALIZER_EDITOR_ID)){
				return true;
			}
		} catch (PartInitException e) {
			BytecodeVisualizerPlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR, BytecodeVisualizerPlugin.PLUGIN_ID, e.getMessage(), e)
			);
		}

		return false;
	}
	/**
	 * Resolves the source code number into the bytecode line.
	 * @param methods
	 * @param sourceLine
	 * @return bytecode number
	 */
	protected static int resolveLineNumberIntoBytecode(List<IMethodSection> methods, int sourceLine){
		int byteCodeLine = ByteCodeConstants.INVALID_OFFSET;
		for(IMethodSection m: methods){
			if(!m.hasLineNumberTable()){
				break;
			}
			
			byteCodeLine = m.getBytecodeLine(sourceLine);
			if(byteCodeLine != ByteCodeConstants.INVALID_OFFSET){
				return byteCodeLine;
			}
		}
		
		return IClassFileEditor.INVALID_LINE;
	}

	/**
	 * Document provider.
	 */
	protected BytecodeDocumentProvider byteCodeDocumentProvider = new BytecodeDocumentProvider(this);

	/**
	 * Canvas if the control flow graph figure.
	 */
	private ControlFlowGraphCanvas canvasControlFlowGraph = null;
	
	/**
	 * Menu provider.
	 */
	private MenuManager cmProvider;
	
//	/**
//	 * The canonical empty selection. This selection should be used 
//	 * for bug correction bug#91 NullPointerException if license has been expired.
//	 */
//	public final StructuredSelection EMPTY = new StructuredSelection2();

	/**
	 * Outline page of the class file editor.
	 */
	protected BytecodeOutlinePage fOutlinePage = null;
	
	/**
	 * Operand Stack View reference
	 */
	private OperandStackViewPage operandStackViewPage = null;

	/**
	 * Used to generate annotations for stack frames
	 */
//	protected IInstructionPointerPresentation fPresentation = (IInstructionPointerPresentation) DebugUITools.newDebugModelPresentation();	

	/**
     * Whether to re-use editors when displaying source during debugging.
     */
    private boolean fReuseEditor = DebugUIPlugin.getDefault().getPreferenceStore().getBoolean(IDebugUIConstants.PREF_REUSE_EDITOR);
	/**
	 * Open in control flow factory.
	 */
	private ExportGraphAndOpenWithControlflowgraphFactoryAction graphFactoryAction = null;

	/**
	 * The flag which indicates if the cursor update handler 
	 * is enabled or disabled.
	 */
	private boolean handleCursorPositionChanged = true;
	
	/**
	 * Reference to the action contributor. 
	 */
	private BytecodevizualizerActionBarContributor actionContributor = null;
	
	/**
	 * List of the line selection listeners.
	 */
	protected  List<IClassFileEditorSelectionListener> lineSelectionListener = new ArrayList<IClassFileEditorSelectionListener>();
	
	private ISelectionChangedListener outlineListener = new ISelectionChangedListener(){

		public void selectionChanged(SelectionChangedEvent event) {
			TreeSelection sel = (TreeSelection)event.getSelection();
			Object o = sel.getFirstElement();

			if(o instanceof IJavaElement){
				IJavaElement element = (IJavaElement)o;

				switch(element.getElementType()){
				case IJavaElement.PACKAGE_DECLARATION:
					IType t0 = sourceCodeViewer.getPrimaryType();
					if(!t0.isBinary()){ /*  Doesn't work for binary class files */

						ICompilationUnit cu = t0.getCompilationUnit();
						IPackageFragment p = t0.getPackageFragment();
						if(cu != null && p != null){	
							IPackageDeclaration d = t0.getCompilationUnit().getPackageDeclaration(p.getElementName());

							sourceCodeViewer.setSelection(d);
						}
					}
					break;

				case IJavaElement.TYPE:
					sourceCodeViewer.setSelection(sourceCodeViewer.getPrimaryType());					
					break;

				case IJavaElement.FIELD:
					IOutlineElementField f = (IOutlineElementField)o;
					String fieldName = f.getFieldSection().getName();
					IType t = sourceCodeViewer.getPrimaryType();
					if(t != null && fieldName!= null){
						IField field = t.getField(fieldName);
						sourceCodeViewer.setSelection(field);
					}
					break;

				case IJavaElement.METHOD:
					IMethod method = (IMethod)o;

					IType t2 = sourceCodeViewer.getPrimaryType();
					try {									
						IMethod m = ClassFileDocumentsUtils.findMethod(t2, method.getElementName(), method.getSignature());
						if(m!= null){
							sourceCodeViewer.setSelection(m);
							return;
						}
						else{
							if(method.isConstructor()){
								sourceCodeViewer.setSelection(t2);
								return;
							}
						}

					} catch (JavaModelException e) {
						BytecodeVisualizerPlugin.getDefault().getLog().log(
								new Status(IStatus.ERROR, BytecodeVisualizerPlugin.PLUGIN_ID, e.getMessage(), e)
						);
					}
					break;
				}
			}
		}
	};

	/**
	 * Parent composite. 
	 */
	protected Composite parent;

	/**
	 * Preference property change listener.
	 */
	private IPropertyChangeListener preferenceListener = null;
	
	/**
	 * Modify property: reuse editor during debugging.
	 */
	private IPropertyChangeListener reuseDebugPropertyChangeListener = new IPropertyChangeListener(){

		/* (non-Javadoc)
		 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent event) {
	        String property = event.getProperty();
			if (property.equals(IDebugUIConstants.PREF_REUSE_EDITOR)) {
				fReuseEditor = DebugUIPlugin.getDefault().getPreferenceStore().getBoolean(IDebugUIConstants.PREF_REUSE_EDITOR);
			}
		}
		
	};

	/**
	 * Flag if the graph has to be shown in the outline.
	 */
	protected boolean showGraphInSeparateView = false;

	/**
	 * Sourcecode Viewer.
	 */
	private ISourceCodeViewer sourceCodeViewer;

	/**
	 * Source code view container.
	 */
	protected Composite sourceCodeViewerComposite;

	/**
	 * Editor's preference store.
	 */
	private IPreferenceStore store = null;
	
	/**
	 * The container widget.
	 */
	private CTabFolder tabFolder;

	private ToggleBytecodeBreakpointAction toggleBytecodeBreakpointAction;
	
	/**
	 * The standard constructor of the text editor for file resources.
	 */
	public BytecodeEditor() {
		super();
		setDocumentProvider(byteCodeDocumentProvider);

		/* reuse editor during debugging */
		BytecodeVisualizerPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(reuseDebugPropertyChangeListener);
	}

	/**
	 * Set the reference to the action contributor. 
	 * @param actionContributor
	 */
	public void setActionContributor( BytecodevizualizerActionBarContributor contributor) {
		actionContributor = contributor;
	}

	/**
	 * Sets active the source code view.
	 */
	public void activateBytecodeTab(){
		setActivePage(TAB_INDEX_BYTECODE);

		updateOutlineGraphView();
	}
	
	/**
	 * Activate graph factory action, if the selector 
	 * location is within the method area
	 * 
	 * @param line 0-based line index
	 */
	private void activateGraphFactoryAction(int line/* changed to 0-based */){
		IClassFileDocument doc = byteCodeDocumentProvider.getClassFileDocument();
		if (doc != null) {
			graphFactoryAction.setEnabled(doc.isLineInMethod(line/* changed to 0-based */));
		}
	}
	

	/**
	 * Sets active the byte code view.
	 */
	public void activateSourceCodeTab(){
		setActivePage(TAB_INDEX_SOURCE);

		updateOutlineGraphView();
	}
	/**
	 * Creates and adds a new page containing the given control to this
	 * multi-page editor. The control may be <code>null</code>, allowing it
	 * to be created and set later using <code>setControl</code>.
	 * 
	 * @param control
	 *            the control, or <code>null</code>
	 * @return the index of the new page
	 * 
	 * @see MultiPageEditorPart#setControl(int, Control)
	 */
	public int addPage(Control control) {
		int index = getPageCount();
		addPage(index, control);
		return index;
	}
	
	/**
	 * Creates and adds a new page containing the given control to this
	 * multi-page editor. The page is added at the given index. The control may
	 * be <code>null</code>, allowing it to be created and set later using
	 * <code>setControl</code>.
	 * 
	 * @param index
	 *            the index at which to add the page (0-based)
	 * @param control
	 *            the control, or <code>null</code>
	 * 
	 * @see MultiPageEditorPart#setControl(int, Control)
	 */
	public void addPage(int index, Control control) {
		createItem(index, control);
	}
	
	/**
	 * Adds Line Selection change listener.
	 * @param lineSelectionListener
	 */
	public void addtLineSelectionListener(IClassFileEditorSelectionListener listener) {
		lineSelectionListener.add(listener);
	}
	
	/*
	 * @see AbstractTextEditor#affectsTextPresentation(PropertyChangeEvent)
	 */
	protected boolean affectsTextPresentation(PropertyChangeEvent event) {		
		return false;
	}

	/**
     * Creates this editor's standard actions and connects them with the global
     * workbench actions for context, toplevel menu and toolbar.
     * <p>
     * Subclasses may extend.</p>
     */
	protected void createActions() {
		super.createActions();

		/* radio action bytecode/basicblock view */
		IAction a = new ActivateBytecodeGraphViewAction(this);
		a.setChecked(true);		
		setAction(ActivateBytecodeGraphViewAction.ID, a);
		IAction a2 = new ActivateBasicblockGraphViewAction(this);	
		setAction(ActivateBasicblockGraphViewAction.ID, a2);

		/* open graph in the control flow factory action */
		graphFactoryAction = new ExportGraphAndOpenWithControlflowgraphFactoryAction(this, byteCodeDocumentProvider.getClassFileDocument());
		setAction(ExportGraphAndOpenWithControlflowgraphFactoryAction.ID, graphFactoryAction);

		/* About Action */
		/* not desired anymore */

		/* Create ruler context menu */
		IVerticalRuler ruler = getVerticalRuler();

		MenuManager mm = new MenuManager();
		
		mm.add(getToggleBytecodeBreakpointAction());
		
		IAction action = getAction(ITextEditorActionConstants.LINENUMBERS_TOGGLE);
		mm.add(action);		

		mm.add(new Separator());
		action = getAction(ITextEditorActionConstants.RULER_PREFERENCES);
		mm.add(action);

		Menu m = mm.createContextMenu(ruler.getControl());	
		ruler.getControl().setMenu(m);
	}
    
	/**
	 * Creates and initialize control flow graph canvas
	 */
	private void createAndInitializeControlFlowGraphCanvas(){
		
		StyledText textWidget = getSourceViewer().getTextWidget();
		canvasControlFlowGraph = new ControlFlowGraphCanvas(parent, this);
		canvasControlFlowGraph.init(textWidget.getLineHeight(), byteCodeDocumentProvider.getClassFileDocument());

		/* initialize control flow graph view */
		setControlFlowViewBackGround();

		/* set menu for control flow graph*/
		Menu menu = getSourceViewer().getTextWidget().getMenu();
		/* 
		 * NOTE: menu is disposed if the control flow graph view 
		 * is closed. The menu is cloned.
		 */		
		Menu menu2 = new Menu(canvasControlFlowGraph);
		menu2.setData(menu.getData());
		canvasControlFlowGraph.setMenu(menu2);

		/* Synchronize scroll bars in the left and right views */
		synchronizeScrollBars(textWidget, canvasControlFlowGraph);

		/* Synchronize line selection in the left and right views */	
		synchronizeLineSelection(textWidget, canvasControlFlowGraph);

		/* synchronize current line color */
		synchronizeCurrentLineColor(canvasControlFlowGraph);

		/* deactivate export graph action */
		IAction a = getAction(ExportGraphAndOpenWithControlflowgraphFactoryAction.ID);
		a.setEnabled(false);
		
		final IAction a1 = getAction(ActivateBytecodeGraphViewAction.ID);
		final IAction a2 = getAction(ActivateBasicblockGraphViewAction.ID);
		
		/* set status of action to true if the view is visible */
		if(isControlFlowgraphViewVisible()){
			a1.setEnabled(true);
			a2.setEnabled(true);
		}
		else{
			a1.setEnabled(false);
			a2.setEnabled(false);			
		}

		/* 
		 * activate actions only if focus in the editor or separate view
		 * is set and the control flow graph view is visible. 
		 * add listener to control flow graph panel and the 
		 * text editor 
		 */
		canvasControlFlowGraph.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				a1.setEnabled(true);
				a2.setEnabled(true);
			}

			public void focusLost(FocusEvent e) {
				a1.setEnabled(false);
				a2.setEnabled(false);
			}
		});
		
		textWidget.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				if(canvasControlFlowGraph.isDisposed())
					return;

//				System.out.print("gain: ");
//				System.out.println(isControlFlowgraphViewVisible());				

				if(isControlFlowgraphViewVisible()){
					a1.setEnabled(true);
					a2.setEnabled(true);
				}
				else{
					a1.setEnabled(false);
					a2.setEnabled(false);
				}
			}

			public void focusLost(FocusEvent e) {
				if(canvasControlFlowGraph.isDisposed())
					return;

//				System.out.print("lost: ");
//				System.out.println(isControlFlowgraphViewVisible());

				if(!isControlFlowgraphViewVisible()){
					a1.setEnabled(false);
					a2.setEnabled(false);
				}
			}
		});
		
		/* deactivate actions if the view is closed*/
		canvasControlFlowGraph.addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e) {				
				a1.setEnabled(false);
				a2.setEnabled(false);		
			}
		});
		
	}

	/**
	 * Creates page 0 of the multi-page editor,
	 * which contains a text editor.
	 */
	private void createBytecodeTab() {

		/* initialize preferences */
		String s = CorePlugin.getDefault().getPreferenceStore().getString(CorePreferenceConstants.GRAPH_PANEL_LOCATION);
		if(s.equals(CorePreferenceConstants.GRAPH_PANEL_LOCATION_SEPARATE_VIEW)){
			showGraphInSeparateView = true;		
		}
		else if(s.equals(CorePreferenceConstants.GRAPH_PANEL_LOCATION_EDITOR)){
			showGraphInSeparateView = false;
		}
		else{
			showGraphInSeparateView = false;
		}

		/* create content*/
		Composite c = null;
		if(showGraphInSeparateView){
			c = createPartControlWithGraphOutline(getTabFolder());
		}
		else{
			c = createPartControlWithSach(getTabFolder());			
		}

		/* disable folding */
		final ProjectionViewer viewer =(ProjectionViewer)getSourceViewer();
		if(viewer != null){
			if(viewer.isProjectionMode()){
				viewer.doOperation(ProjectionViewer.TOGGLE);
			}

			viewer.addProjectionListener(new IProjectionListener(){
				public void projectionDisabled() {
					/* nothing to do */
				}

				public void projectionEnabled() {
					if(viewer.isProjectionMode()){
						viewer.doOperation(ProjectionViewer.TOGGLE);
					}
				}
			});
		}

		int index = addPage(c);

		setPageText(index, BytecodeVisualizerMessages.BytecodeEditor_tab_Bytecode);
		setPartName(getTitle());
		CTabItem item = getItem(index);
		item.setImage(CoreImg.bytecodeViewer_16x16.createImage());

	}

	/**
	 * Creates the outline page used with this editor.
	 * @return the created Java outline page
	 */
	protected BytecodeOutlinePage createBytecodeVisualizerOutlinePage() {
		BytecodeOutlinePage page = new BytecodeOutlinePage(this);		
		page.setInput(byteCodeDocumentProvider.getClassFileOutlineElement());
		return page;
	}
	
	/**
	 * Creates and returns the preference store for this Java editor with the given input.
	 *
	 * @param input The editor input for which to create the preference store
	 * @return the preference store for this editor
	 */
	private IPreferenceStore createCombinedPreferenceStore(IEditorInput input) {
		List<IPreferenceStore> stores= new ArrayList<IPreferenceStore>(3);

		/* own bytecode visualizer preferences */
		stores.add(BytecodeVisualizerPlugin.getDefault().getPreferenceStore());

		/* fond and color preferences */
		stores.add(JavaPlugin.getDefault().getPreferenceStore());
		
		/* background and current line color preferences */
		stores.add(EditorsUI.getPreferenceStore());

		return new ChainedPreferenceStore((IPreferenceStore[]) stores.toArray(new IPreferenceStore[stores.size()]));		
	}
	
	/**
	 * Creates a control flow graph page.
	 * @return the control flow graph page
	 */
	protected ControlFlowGraphViewPage createGraphControlFlowViewPage() {
		if(!showGraphInSeparateView){
			return null;
		}
		
		if(canvasControlFlowGraph == null || canvasControlFlowGraph.isDisposed()){
			/* recreate control flow graph viewer */
			createAndInitializeControlFlowGraphCanvas();
		}

		ControlFlowGraphViewPage page = new ControlFlowGraphViewPage(canvasControlFlowGraph);
		return page;
	}

	/**
	 * Creates a tab item at the given index and places the given control in the
	 * new item. The item is a CTabItem with no style bits set.
	 * 
	 * @param index
	 *            the index at which to add the control
	 * @param control
	 *            is the control to be placed in an item
	 * @return a new item
	 */
	private CTabItem createItem(int index, Control control) {
		CTabItem item = new CTabItem(getTabFolder(), SWT.NONE, index);
		item.setControl(control);
		return item;
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createBytecodeTab();
		createSourceCodeTab();

	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.core.editors.ClassFileEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public final void createPartControl(final Composite parent) {
		this.parent = parent;

		parent.setLayout(new FillLayout());
		this.tabFolder = createTabFolder(parent);

		createPages();

		String showTab = BytecodeVisualizerPlugin.getDefault().getPreferenceStore().getString(BytecodeVisualizerPreferenceConstats.SHOW_TAB);
		if(showTab.equals(BytecodeVisualizerPreferenceConstats.SHOW_ALWAYS_BYTECODE_TAB)) {
			activateBytecodeTab(); /* byte code tab */
		}
		else if (showTab.equals(BytecodeVisualizerPreferenceConstats.SHOW_ALWAYS_SOURCECODE_TAB)) {
			activateSourceCodeTab(); /* source code tab */
		}
		/* default s.equals(SourceCodePreferences.SHOW_SOURCECODE_IF_AVALIABLE) */
		else {
			if (sourceCodeViewer.isSourceCodeLoaded()) {
				/* source code tab */
				activateSourceCodeTab();
			}
			else {
				/* byte code tab */
				activateBytecodeTab();
			}
		}

		/*  set the active page (page 0 by default), unless it has already been done */
		if (getActiveTabIndex() == -1) {
			setActivePage(0);
		}

		addtLineSelectionListener(new IClassFileEditorSelectionListener(){

			public void lineSelectionChanged(int newLine, Object o) {
				if(o instanceof IMethodSection){
					IMethodSection method = (IMethodSection)o;

					if(method != null){
						if(method.getFirstLine() == newLine){
							IType t = sourceCodeViewer.getPrimaryType();
							try {
								IMethod m = ClassFileDocumentsUtils.findMethod(t, method.getName(), method.getDescriptor());
								if(m!= null){
									sourceCodeViewer.setSelection(m);
									return;
								}
								else{
									if(method.isConstructor()){
										sourceCodeViewer.setSelection(t);
										return;
									}
								}								
							} catch (JavaModelException e) {
								BytecodeVisualizerPlugin.getDefault().getLog().log(
										new Status(IStatus.ERROR, BytecodeVisualizerPlugin.PLUGIN_ID, e.getMessage(), e)
								);
							}
						}

						int line = method.getSourceCodeLine(newLine -  1/* changed to 0-based */);
						if (line != ByteCodeConstants.INVALID_LINE){
							sourceCodeViewer.selectSourceCodeLine(line - 1 /* changed to 0-based */, false);
						}
					}

					return;
				}

				if(o instanceof IFieldSection){
					IFieldSection f = (IFieldSection)o;
					String fieldName = f.getName();
					IType t = sourceCodeViewer.getPrimaryType();
					if(t != null && fieldName!= null){
						IField field = t.getField(fieldName);
						sourceCodeViewer.setSelection(field);
					}

					return;
				}

				if(o instanceof IClassFileDocument){
					IType t = sourceCodeViewer.getPrimaryType();
					if(t != null ){
						sourceCodeViewer.setSelection(t);
					}

					return;					
				}

			}

		});

		/* FIX: bug#84 Recursive attempt to create itself */
		///* called only the first time */
		//selectAndRevealX(0, IClassFileEditor.INVALID_LINE);

		/* DON'T USE selectAndreval Method directly from createPart
		 * to avoid RECURSIVE WARNING: bug#84 Recursive attempt to create itself
		 * Instead overwrite the follwing methods
		 * @see org.eclipse.ui.texteditor.AbstractTextEditor#selectAndReveal(int, int)
		 * or 
		 * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#doSetSelection(org.eclipse.jface.viewers.ISelection)
		 */

	}
	
	/**
	 * Creates the SWT controls for this editor.
	 */
	public Composite createPartControlWithGraphOutline(final Composite parent) {

		final StackLayout layout = new StackLayout();
		parent.setLayout(layout);

		/* create the control for the Byte Code Editor */
		Composite editorParent = new Composite(parent, SWT.NONE);	
		editorParent.setLayout(new FillLayout());
		super.createPartControl(editorParent);
		//createSuperPartControl(editorParent);
		layout.topControl = editorParent;
		
		/* install context menu */
		cmProvider = new BytecodevizualizerContexMenuManager(this);              
		Menu menu = cmProvider.createContextMenu(editorParent);
		getSourceViewer().getTextWidget().setMenu(menu);
		getSite().registerContextMenu(cmProvider, getSite().getSelectionProvider());
		
		/* add update listener */
		byteCodeDocumentProvider.addDocumentUpdateListener(this);

		/* create and initialize control flow graph canvas */
		//createAndInitializeControlFlowGraphCanvas();

		return editorParent;
	}
	/**
	 * Creates the SWT controls for this editor.
	 */
	public Composite createPartControlWithSach(final Composite parent0) {
		
		final Composite parent = new Composite(parent0, SWT.NONE);	
		parent.setLayout(new FillLayout());
		
		/* set parent Layout */
		final FormLayout form = new FormLayout ();
		parent.setLayout (form);

		/* Create sash and set its layout */
		final Sash sash = new Sash (parent, SWT.VERTICAL);

		/* Wrap the first view: Byte Code Editor */
		Composite editorParent = new Composite(parent, SWT.NONE);
		FormData editorParentFormData = new FormData ();
		editorParentFormData.left = new FormAttachment (0, 0);
		editorParentFormData.right = new FormAttachment (sash, 0);
		editorParentFormData.top = new FormAttachment (0, 0);
		editorParentFormData.bottom = new FormAttachment (100, 0);
		editorParent.setLayoutData (editorParentFormData);
		editorParent.setLayout(new FillLayout());

		/* create the control for the Byte Code Editor */
		super.createPartControl(editorParent);

		/* create the control flow graph viewer */
		StyledText textWidget = getSourceViewer().getTextWidget();

		/* create and initialize control flow graph canvas */
		canvasControlFlowGraph = new ControlFlowGraphCanvas(parent, this);
		canvasControlFlowGraph.init(textWidget.getLineHeight(), byteCodeDocumentProvider.getClassFileDocument());
				
		/* add update listener */
		byteCodeDocumentProvider.addDocumentUpdateListener(this);
		
		/* Wrap the second view: Control Flow Graph Viewer */
		FormData editorParentFormData2 = new FormData ();
		editorParentFormData2.left = new FormAttachment (sash, 0);
		editorParentFormData2.right = new FormAttachment (100, 0);
		editorParentFormData2.top = new FormAttachment (0, 0);
		editorParentFormData2.bottom = new FormAttachment (100, 0);
		canvasControlFlowGraph.setLayoutData(editorParentFormData2);

		/* init control flow graph view */
		setControlFlowViewBackGround();

		/* install context menu */
		MenuManager cmProvider = new BytecodevizualizerContexMenuManager(this);              
		Menu menu = cmProvider.createContextMenu(editorParent);        
		getSite().registerContextMenu(cmProvider, getSite().getSelectionProvider());

		this.getSourceViewer().getTextWidget().setMenu(menu);
		canvasControlFlowGraph.setMenu(menu);

		/* Synchronize scroll bars in the left and right views */
		synchronizeScrollBars(textWidget, canvasControlFlowGraph);

		/* Synchronize line selection in the left and right views */
		synchronizeLineSelection(textWidget, canvasControlFlowGraph);

		/* synchronize current line color */
		synchronizeCurrentLineColor(canvasControlFlowGraph);

		/* default form data for the sash */
		final FormData sashFormData = new FormData ();
		sashFormData.left = new FormAttachment (0, 0);
		sashFormData.top = new FormAttachment (0, 0);
		sashFormData.bottom = new FormAttachment (100, 0);
		sash.setLayoutData (sashFormData);

		final int graphAreaWidht = canvasControlFlowGraph.getFigureWidth();
		final int sashWidthoffset = 24; /* sash + balks */

		/* resize of the editor shell */
		parent.addControlListener(new ControlListener(){
			public void controlMoved(ControlEvent e) {
				/* nothing to do */
			}

			/* set the left are = size of the graph view, but maximal 50% of the whole view */
			public void controlResized(ControlEvent e) {
				Rectangle shellRect = parent.getClientArea ();
				int right = shellRect.width - graphAreaWidht - sashWidthoffset;
				right = Math.max(right, shellRect.width/2);
				sashFormData.left.offset = right;
				parent.layout ();
			}			
		});

		/* dynamic resizing of the sash area */
		final int limit = 40;
		sash.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				Rectangle sashRect = sash.getBounds ();
				Rectangle shellRect = parent.getClientArea ();
				int right = shellRect.width - sashRect.width - limit;
				e.x = Math.max (Math.min (e.x, right), limit);

				if((shellRect.width - e.x - sashWidthoffset) < 0){/*check the minimum size*/
					e.x = shellRect.width  - graphAreaWidht - sashWidthoffset;
				}

				if (e.x != sashRect.x)  {
					sashFormData.left.offset = e.x;
					parent.layout ();
				}
			}
		});

		return parent;
	}

	/**
	 * Creates page: SourceCode View
	 */
	protected void createSourceCodeTab() {
		try {

			sourceCodeViewerComposite = new Composite(getTabFolder(), SWT.NONE);
			sourceCodeViewerComposite.setLayout(new FillLayout());

			IEditorInput sourceCodeViewerInput = getSourceCodeViewerInput();
			if (sourceCodeViewerInput instanceof IFileEditorInput) {

				/* a local class file */
				IFileEditorInput fileEditorInput = (IFileEditorInput) sourceCodeViewerInput;
				IFile file = fileEditorInput.getFile();
				IProject project = file.getProject();

				/* create java project */
				IJavaProject javaProject = JavaCore.create(project);

				try {
					IPath wspacePath = file.getFullPath();
					IPath outputDir = javaProject.getOutputLocation();

					IPath classToFind = null;
					if (outputDir.matchingFirstSegments(wspacePath) == outputDir.segmentCount()) {
						/* if we are in the output directory
						 * strip the output directory 
						 * as we want the project relative path */
						classToFind = wspacePath.removeFirstSegments(outputDir.segmentCount());
					}

					if (classToFind != null) {
						classToFind = classToFind.removeFileExtension();
						String typeToFind = classToFind.toString();

						int indx = typeToFind.indexOf('$');
						if(indx != -1){
							typeToFind = typeToFind.substring(0, indx);
						}

						typeToFind = typeToFind.replace(IPath.SEPARATOR, '.');

						sourceCodeViewer = new JavaSourceCodeViewer();

						IType t = javaProject.findType(typeToFind);
						if (t != null) {

							/* create file input */
							IFile sourceFile = (IFile) t.getCompilationUnit().getResource();
							IEditorInput input = new FileEditorInput(sourceFile);

							sourceCodeViewer.init(getEditorSite(), input);
						}
					}
					else {
						/* try with class file viewer */
						sourceCodeViewer =  new  ClassFileSourcecodeViewer();
						sourceCodeViewer.init(getEditorSite(), sourceCodeViewerInput);						
					}

				} catch (JavaModelException e) {
					BytecodeVisualizerPlugin.getDefault().getLog().log(
							new Status(IStatus.WARNING, 
									BytecodeVisualizerPlugin.PLUGIN_ID,
									CoreMessages.ERROR_Cannot_get_Sourcecode, 
									e));

					/* try with class file viewer */
					sourceCodeViewer =  new  ClassFileSourcecodeViewer();
					sourceCodeViewer.init(getEditorSite(), sourceCodeViewerInput);
				}					

			}
			else {	
				sourceCodeViewer = new  ClassFileSourcecodeViewer();
				sourceCodeViewer.init(getEditorSite(), sourceCodeViewerInput);
			}

			sourceCodeViewer.addClassFileEditorReference(this);

			/* 
			 * hook the editor part. It is necessary for initialization of
			 * actions: Toggle Breakpoint, Enable Breakpoint ...
			 */
			PartSite p = (PartSite)getSite();
			p.setPart((IWorkbenchPart)sourceCodeViewer);

			sourceCodeViewer.createPartControl(sourceCodeViewerComposite);

			p.setPart(this); /* set the bytecode visualzer part */

			/* create item for page only after createPartControl has succeeded */	
			int i = addPage(sourceCodeViewerComposite);
			setPageText(i, BytecodeVisualizerMessages.BytecodeEditor_tab_Source);

			CTabItem item = getItem(i);			
			Image img = WorkbenchImages.getImage(ISharedImages.IMG_OBJ_FILE);
			item.setImage(img);


		} catch (PartInitException e) {
			Messages.error(CoreMessages.ERROR_CreateNested_Editor);

			AbstractUIPlugin p = CorePlugin.getPluginFromRegistry(CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID);
			p.getLog().log(e.getStatus());
		}

	}
	
    /**
	 * Creates the SWT controls for this editor.
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createSuperPartControl(final Composite parent) {
		this.parent = parent;	
		
		/* init preferences */
		String s = CorePlugin.getDefault().getPreferenceStore().getString(CorePreferenceConstants.GRAPH_PANEL_LOCATION);
		if(s.equals(CorePreferenceConstants.GRAPH_PANEL_LOCATION_SEPARATE_VIEW)){
			showGraphInSeparateView = true;		
		}
		else if(s.equals(CorePreferenceConstants.GRAPH_PANEL_LOCATION_EDITOR)){
			showGraphInSeparateView = false;
		}
		else{
			showGraphInSeparateView = false;
		}

		/* create content*/
		if(showGraphInSeparateView){
			createPartControlWithGraphOutline(parent);
		}
		else{
			createPartControlWithSach(parent);			
		}

		/* disable folding */
		final ProjectionViewer viewer =(ProjectionViewer)getSourceViewer();
		if(viewer != null){
			if(viewer.isProjectionMode())
				viewer.doOperation(ProjectionViewer.TOGGLE);

			viewer.addProjectionListener(new IProjectionListener(){
				public void projectionDisabled() {
					/* nothing to do */
				}

				public void projectionEnabled() {
					viewer.doOperation(ProjectionViewer.TOGGLE);
				}
			});
		}

	}
	
	/**
	 * Creates an empty container. Creates a CTabFolder with no style bits set,
	 * and hooks a selection listener which calls <code>pageChange()</code>
	 * whenever the selected tab changes.
	 * 
	 * @param parent
	 *            The composite in which the container tab folder should be
	 *            created; must not be <code>null</code>.
	 * @return a new container
	 */
	private CTabFolder createTabFolder(Composite parent) {
		/* 
		 * use SWT.FLAT style so that an extra 1 pixel border is not reserved
		 * inside the folder 
		 */
		final CTabFolder result = new CTabFolder(parent, SWT.BOTTOM | SWT.FLAT);
		result.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int newPageIndex = result.indexOf((CTabItem) e.item);
				pageChange(newPageIndex);
			}
		});
		return result;
	}
	
	/*
	 * @see IWorkbenchPart#dispose()
	 * @since 2.0
	 */
	public void dispose() {
		super.dispose();

		BytecodeVisualizerPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(reuseDebugPropertyChangeListener);
		
		/* remove preference store listener */
		if(store != null){
			store.removePropertyChangeListener(preferenceListener);
		}

		JFaceResources.getFontRegistry().removeListener(preferenceListener);

	}

	/* (non-Javadoc)
	 * @see com.drgarbage.classfile.render.intf.IDocumentUpdateListener#documentUpdated(com.drgarbage.classfile.render.intf.IClassFileDocument)
	 */
	public void documentUpdated(IClassFileDocument classFileDocument) {
		if(canvasControlFlowGraph == null){
			/* should never happen */
			return;
		}

		canvasControlFlowGraph.documentUpdated(classFileDocument);
		
		if(fOutlinePage != null){
			fOutlinePage.setInput(byteCodeDocumentProvider.getClassFileOutlineElement());
		}
		
		if(operandStackViewPage != null){
			operandStackViewPage.setInput(null);
		}
	}


	private void doHandleCursorPositionChanged(){
		IClassFileDocument doc = byteCodeDocumentProvider.getClassFileDocument(); 
		if(doc != null){
			/* set selection in the outline*/
			int line = getSelectedLine();
			
			if(doc.isLineInMethod(line/* changed to 0-based */)){
				IMethodSection m = byteCodeDocumentProvider.getClassFileDocument().findMethodSection(line/* changed to 0-based */);
				if(m!= null){
					if(fOutlinePage!= null) {
						fOutlinePage.setSelection(m);
					}
					
					if(operandStackViewPage != null){
						operandStackViewPage.setInput(m);
					}
					
					updateLineSectionListener(line/* changed to 0-based */, m);
					return;
				}
			}
	
			if(doc.isLineInField(line/* changed to 0-based */)){
				IFieldSection f = byteCodeDocumentProvider.getClassFileDocument().findFieldSection(line /* changed to 0-based */);
				if(f != null){				
					if(fOutlinePage!= null) {
						fOutlinePage.setSelection(f);
					}
					
					/* set input null for operand stack if the line not in the method */
					if(operandStackViewPage != null){
						operandStackViewPage.setInput(null);
					}
					
					updateLineSectionListener(line/* changed to 0-based */, f);
					return;
				}
			}
			
			/* select class */
			if(fOutlinePage!= null){
				fOutlinePage.setSelection(doc);		
			}
			
			/* set input null for operand stack if the line not in the method */
			if(operandStackViewPage != null){
				operandStackViewPage.setInput(null);
			}
			
			updateLineSectionListener(line/* changed to 0-based */, doc);
		}
	
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#doRestoreState(org.eclipse.ui.IMemento)
	 */
	protected void doRestoreState(IMemento memento) {
		/* FIX: initialization after restoring the workspace */
		activateBytecodeTab();  

		setHandleCursorPositionChanged(true);
		super.doRestoreState(memento);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#doSetInput(org.eclipse.ui.IEditorInput)
	 */
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		if (sourceCodeViewer != null) {
			if (input instanceof IFileEditorInput){
				//TODO: implement for local class file
				//The code may copied from createPageSourceView method
				//This case may probably only occur if the 
				//local source code of the class file is changed. 
			}
			if (input instanceof JDIEditorInput) {
				JDIEditorInput jdiEditorInput = (JDIEditorInput) input;
				sourceCodeViewer.doSetInputInternal(jdiEditorInput.getDelegate());
			}
			else {
				sourceCodeViewer.doSetInputInternal(input);
			}
		}

		if (sourceCodeViewer!= null) {
			selectAndReveal(0, IClassFileEditor.INVALID_LINE);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#doSetSelection(org.eclipse.jface.viewers.ISelection)
	 */
	/* FIX: bug#84 Recursive attempt to create itself
	 * FIX: restore selection */
	protected void doSetSelection(ISelection selection) {
		if (selection instanceof ITextSelection) {
			ITextSelection textSelection= (ITextSelection) selection;
			super.selectAndReveal(textSelection.getOffset(), textSelection.getLength());
			doHandleCursorPositionChanged();
		}
	}

	
	/**
	 * Returns the index of the currently active page, or -1 if there is no
	 * active page.
	 * <p>
	 * Subclasses should not override this method
	 * </p>
	 * 
	 * @return the index of the active page, or -1 if there is no active page
	 */
	public int getActiveTabIndex() {
		CTabFolder tabFolder = getTabFolder();
		if (tabFolder != null && !tabFolder.isDisposed()) {
			return tabFolder.getSelectionIndex();
		}
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.core.editors.ClassFileEditor#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (fOutlinePage == null){
				fOutlinePage = createBytecodeVisualizerOutlinePage();
				fOutlinePage.addSelectionChangedListener(outlineListener );
			}			
			return fOutlinePage;
		}
		else if (IControlFlowGraphViewPage.class.equals(required)) {
			Object o = createGraphControlFlowViewPage();
			updateOutlineGraphView();
			return o;
		}
		else if (IToggleBreakpointsTarget.class.equals(required)) {
			if(getActiveTabIndex() == TAB_INDEX_BYTECODE){
				return new ToggleBytecodeBreakpointAdapter(isBytecodeDebugSupported());
			}
			else if(getActiveTabIndex() == TAB_INDEX_SOURCE){
				return new ToggleBytecodeSourceBreakpointAdapter();
			}
		} 
		else if (OperandStackViewPage.class.equals(required)) {
			if (operandStackViewPage == null){
				operandStackViewPage = new OperandStackViewPageIml();
            	operandStackViewPage.setEditor(this);
			}			
			return operandStackViewPage;
		}
		
		return super.getAdapter(required);
	}

	public IEditorInput getBytecodeEditorInput() {
		return super.getEditorInput();
	}

	/**
	 * Return canvas of the control flow graph.
	 * @return the canvasControlFlowGraph
	 */
	public ControlFlowGraphCanvas getCanvasControlFlowGraph() {
		return canvasControlFlowGraph;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.core.editors.IClassFileEditor#getClassFileEditor()
	 */
	public BytecodeEditor getClassFileEditor() {
		return this;
	}

	/**
	 * @param textSelection
	 * @return
	 */
	protected String getClassName(TextSelection textSelection){

		String text = null;

		IWorkbenchPage page = getEditorSite().getPage();	
		IViewPart view = page.findView("org.eclipse.ui.console.ConsoleView");
		
		if(view instanceof IConsoleView){
			IConsoleView cView = (IConsoleView)view;
			IConsole console = cView.getConsole();

			if(console instanceof TextConsole){
				TextConsole textConsole = (TextConsole)console;
				text = textConsole.getDocument().get();

			}
		}
		
		/* Get class.method string */
		
		/* find end */
		int endIndex = 0;		
		for(int i = textSelection.getOffset(); i > 0; i--){
			if(text.charAt(i)== '('){
				endIndex = i;
				break;
			}	
		}
		
		/* find start */
		int beginIndex = 0;	
		char ch[] = new char[3];
		for(int i = endIndex; i > 2; i--){
			text.getChars(i - 3, i, ch, 0);
			if(ch[0]== 'a' && ch[1] == 't' && ch[2] == ' '){
				beginIndex = i;
				break;
			}
		}

		text = text.substring(beginIndex, endIndex);
		
		/* separate class name and method name */
		endIndex = text.lastIndexOf('.');
		
		String className = text.substring(0, endIndex);
		
		if(className != null){
			return className.trim();
		}
		
		CorePlugin.getDefault().getLog().log(
				new Status(IStatus.ERROR, CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID, CoreMessages.CLASS_NAME_NOT_RESOLVED) 
				);

		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#getCorrespondingElement(org.eclipse.jdt.core.IJavaElement)
	 */
	@Override
	protected IJavaElement getCorrespondingElement(IJavaElement element) {
		/* nothing to do */
		return null;
	}

	/* bug#91 NullPointerException if license has been expired. */
	private JavaEditor getEditor(){
		return this;
	}
	
	@Override
	public IEditorInput getEditorInput() {
		return getBytecodeEditorInput();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#getElementAt(int)
	 */
	@Override
	protected IJavaElement getElementAt(int offset) {
		/* nothing to do */
		return null;
	}

	private int getIndexOfSelectedFrame(IJavaStackFrame selectedFrame) 
	throws DebugException{
		IThread thread = selectedFrame.getThread();
		if(thread.hasStackFrames()){
			IStackFrame s[] = thread.getStackFrames();
			for(int i = 0; i < s.length; i++)
			{
				if(s[i].equals(selectedFrame)){
					return i;
				}
			}
		}

		return -1;
	}

	/**
	 * Returns the tab item for the given page index (page index is 0-based).
	 * The page index must be valid.
	 * 
	 * @param pageIndex
	 *            the index of the page
	 * @return the tab item for the given page index
	 */
	private CTabItem getItem(int pageIndex) {
		return getTabFolder().getItem(pageIndex);
	}

	/**
	 * Returns the outline page of the editor.
	 * @return outline
	 */
	public BytecodeOutlinePage getOutlinePage() {			
		return fOutlinePage;
	}

	/**
	 * Returns the number of pages in this multi-page editor.
	 * 
	 * @return the number of pages
	 */
	protected int getPageCount() {
		CTabFolder folder = getTabFolder();
		/* May not have been created yet, or may have been disposed. */
		if (folder != null && !folder.isDisposed()) {
			return folder.getItemCount();
		}
		return 0;
	}

	/**
	 * Returns parent composite.
	 * @return the parent
	 */
	public Composite getParent() {
		return parent;
	}
	/**
	 * Returns the 0-based number of the line currently selected
	 * in the text editor.
	 * @return the 0-based selected line
	 */
	public int getSelectedLine() {
		ISourceViewer sourceViewer = this.getSourceViewer();
		if (sourceViewer == null) {
			return ByteCodeConstants.INVALID_LINE;
		}
		
		/* point.x is offset */
		org.eclipse.swt.graphics.Point selectedRange = sourceViewer.getSelectedRange();

		int line = ByteCodeConstants.INVALID_LINE;
		
		IDocument document = byteCodeDocumentProvider.getDocument(getEditorInput());
		if (document != null) {
			try {
				/* changed to 0-based */
				line = document.getLineOfOffset(selectedRange.x);
				
			} catch (BadLocationException e) {
				CorePlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR, CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID, e.getMessage(), e)
						);
			}
		}

		activateGraphFactoryAction(line);

		return line;
	}
	/**
	 * Returns the source line number.
	 * @param textSelection
	 * @return source line number
	 */
	protected int getSourceCodeLine(TextSelection textSelection){

		String text = null;

		IWorkbenchPage page = getEditorSite().getPage();	
		IViewPart view = page.findView("org.eclipse.ui.console.ConsoleView");
		
		if(view instanceof IConsoleView){
			IConsoleView cView = (IConsoleView)view;
			IConsole console = cView.getConsole();

			if(console instanceof TextConsole){
				TextConsole textConsole = (TextConsole)console;
				text = textConsole.getDocument().get();

			}
		}		

		/* find start */
		int beginIndex = 0;		
		for(int i = textSelection.getOffset(); i > 0; i--){
			if(text.charAt(i)== '('){
				beginIndex = i;
				break;
			}	
		}
		
		/* find end */
		int endIndex = text.length();
		for(int i = textSelection.getOffset(); i < text.length(); i++){
			if(text.charAt(i)== ')'){
				endIndex = i;
				break;
			}			
		}	
		
		String linkText = text.substring(beginIndex, endIndex);
		
		/* Get LIne Number from the link string */
		int index = linkText.lastIndexOf(':');
		if (index >= 0) {
			String numText = linkText.substring(index + 1);
			index = numText.indexOf(')');
			if (index >= 0) {
				numText = numText.substring(0, index);
			}
			try {
				return Integer.parseInt(numText);
			} catch (NumberFormatException e) {
				CorePlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR, CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID, e.getMessage(), e)
						);
			}		
		}
		
		CorePlugin.getDefault().getLog().log(
				new Status(IStatus.ERROR, CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID, CoreMessages.LINE_NOT_FOUND_IN_SELECTED_TEXT)
				);

		return IClassFileEditor.INVALID_LINE;
	}

	/**
	 * Returns a reference to the sourcecode viwer interface.
	 * @return  sourceCodeViewer
	 */
	public ISourceCodeViewer getSourceCodeViewer() {
		return sourceCodeViewer;
	}

	public IEditorInput getSourceCodeViewerInput() {
		IEditorInput result = super.getEditorInput();
		if (result instanceof JDIEditorInput) {
			return ((JDIEditorInput)result).getDelegate();
		}
		else {
			return result;
		}
	}
	/**
	 * Returns the tab folder containing this multi-page editor's pages.
	 * 
	 * @return the tab folder, or <code>null</code> if
	 *         <code>createPartControl</code> has not been called yet
	 */
	private CTabFolder getTabFolder() {
		return tabFolder;
	}

	public ToggleBytecodeBreakpointAction getToggleBytecodeBreakpointAction() {
		if (toggleBytecodeBreakpointAction == null) {
			toggleBytecodeBreakpointAction = new ToggleBytecodeBreakpointAction(this, null, getVerticalRuler());
		}
		return toggleBytecodeBreakpointAction;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#gotoMarker(org.eclipse.core.resources.IMarker)
	 */
	/** @deprecated visibility will be reduced, use <code>getAdapter(IGotoMarker.class) for accessing this method</code> */
	public void gotoMarker(IMarker marker) {//TODO: use getAdapter instead of this method 
		/* synchronize source code view */
		if(sourceCodeViewer != null){
			sourceCodeViewer.gotoMarker(marker);
			/* activate source code view */
			if(getActiveTabIndex() != TAB_INDEX_SOURCE){
				activateSourceCodeTab();
			}
		}
		else{
			super.gotoMarker(marker);
		}
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.core.editors.ClassFileEditor#handleCursorPositionChanged()
	 */
	protected void handleCursorPositionChanged() {
		if(handleCursorPositionChanged) {
			super.handleCursorPositionChanged();
			doHandleCursorPositionChanged();
		}
	}

	/*
	 * @see AbstractTextEditor#handlePreferenceStoreChanged(PropertyChangeEvent)
	 */
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
		/* disable default handling because of ClassCast Exception 
		 * JavaEditor.handlePreferenceStoreChanged() uses cast
		 * ((JavaSourceViewerConfiguration)getSourceViewerConfiguration()).handlePropertyChangeEvent(event);
		 */
		
		handlePreferenceStoreChangedX(event);
	}

	/**
	 * A wrapper for handling of property changes.
	 * @param event
	 */
	private void handlePreferenceStoreChangedX(PropertyChangeEvent event) {

		if (AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER.equals(event.getProperty())) {
			IColumnSupport columnSupport= (IColumnSupport)getAdapter(IColumnSupport.class);
			RulerColumnDescriptor lineNumberColumnDescriptor= RulerColumnRegistry.getDefault().getColumnDescriptor(LineNumberColumn.ID);
			if (isLineNumberRulerVisible()) {
				if (lineNumberColumnDescriptor != null){
					columnSupport.setColumnVisible(lineNumberColumnDescriptor, true);
				}
			} else if (!isLineNumberRulerVisible()) {
				if (lineNumberColumnDescriptor != null){
					columnSupport.setColumnVisible(lineNumberColumnDescriptor, true);
				}
			}
		}
	}

	/*
	 * @see IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		super.init(site, input);

		store = createCombinedPreferenceStore(input);
		setPreferenceStore(store);
		JavaTextTools textTools = JavaPlugin.getDefault().getJavaTextTools();
		final IColorManager colorManager = textTools.getColorManager();
		final ClassFileConfiguration classFileConfiguration = new ClassFileConfiguration(colorManager, store, this, IJavaPartitions.JAVA_PARTITIONING);
		setSourceViewerConfiguration(classFileConfiguration);
		
		/* create preference property listener */
		preferenceListener = new IPropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent event) {
				//System.out.println("event=" + event.getProperty()); //TODO: log for debuging
				
				/* Text Editor current line selector */
				if(event.getProperty().equals(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR)){
					RGB rgb = PreferenceConverter.getColor(getPreferenceStore(), AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR);
					Color c = getSharedColors().getColor(rgb);
					if(!canvasControlFlowGraph.getLineSelectorColor().equals(c)){
						canvasControlFlowGraph.setLineSelectorColor(c);
					}
				}
				/* Text Editor background */
				else if(event.getProperty().startsWith(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND)){	
					setControlFlowViewBackGround();
				}
				/* figure colors */
				else if(event.getProperty().startsWith(CorePreferenceConstants.GRAPH_COLOR_PREFIX)){				
					canvasControlFlowGraph.documentUpdated(byteCodeDocumentProvider.getClassFileDocument());
				}
				/* text fond has been changed (size and Fonts)*/
				else if(event.getProperty().equals(JFaceResources.TEXT_FONT)){						
					/*
					 * Hack: The default handler has a bug.
					 * The JavaPlugin.getDefault().getPreferenceStore() is not updated synchronously.
					 * This code copy the new font value to the default preference store.  
					 */
					Font f = JFaceResources.getTextFont();
					FontData data = f.getFontData()[0];
					//System.out.println(data); //TODO: Log for debuging
					JavaPlugin.getDefault().getPreferenceStore().setValue(event.getProperty(), data.toString());

					/* 
					 * Set new font in the text widget. The font will be overwriten later with
					 * the same value. But we have to set font to calculate the line heght.
					 */
					getSourceViewer().getTextWidget().setFont(f);
					int h = getSourceViewer().getTextWidget().getLineHeight();
					canvasControlFlowGraph.setLineHight(h);
					
					/* update control flow graph figure */
					canvasControlFlowGraph.documentUpdated(byteCodeDocumentProvider.getClassFileDocument());
				}
				/* Bytecode Mnemonics */
				else if(event.getProperty().startsWith(bytecodeMnemonicPreferencesPrefix)){
					classFileConfiguration.adaptToPreferenceChange(event);
					getSourceViewer().invalidateTextPresentation();

				}
				/* Syntax and Coloring */
				else if(event.getProperty().startsWith("java_")){
					classFileConfiguration.adaptToPreferenceChange(event);
					getSourceViewer().invalidateTextPresentation();				
				}
				/* classfile attributes */
//				else if(event.getProperty().startsWith(BytecodeVizualizerPreferenceConstants.classFileAttributePreferencesPrefix)){				
//					//Do not support. The document has to be reloaded.
//				}				
				else{
					/* other references which are not matched befor */
					//System.out.println("event=" + event.getProperty());//TODO: log
				}
			}
		};
	
		/* add preference store listener */		
		store.addPropertyChangeListener(preferenceListener);

		/* add listener */
		JFaceResources.getFontRegistry().addListener(preferenceListener);
	}

	/*
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#initializeEditor()
	 */
	protected void initializeEditor() {
		/* deactivate default initialization */
		//super.initializeEditor();
	}

	public boolean isBytecodeDebugSupported() {
		IEditorInput input = getBytecodeEditorInput();
		
		if (input instanceof IClassFileEditorInput) {
			return true;
		}
		else {
			return false;
		}
		
	}

	/**
	 * Returns true if the control flow graph view is visible,
	 * otherwise false.
	 * @return true or false
	 */
	private boolean isControlFlowgraphViewVisible(){
		IWorkbenchWindow workbench = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if(workbench == null){
			return false;
		}
		IWorkbenchPage page = workbench.getActivePage();
		if(page != null){
			IViewReference[] views = page.getViewReferences();
			for(IViewReference ref: views){
				if(ref.getId().equals(CoreConstants.CONTROL_FLOW_VIEW_ID)){
					IViewPart viewPart = ref.getView(false);
					if(viewPart instanceof ControlFlowGraphView){
						ControlFlowGraphView cfgView = (ControlFlowGraphView) viewPart;
						return cfgView.getCurrentPage().getControl().isVisible();
					}		
				}
			}
		}		
		
		return false;
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.core.editors.ClassFileEditor#isGraphViewVisible()
	 */
	public boolean isGraphViewVisible(){
		if(getActiveTabIndex() == TAB_INDEX_BYTECODE){
			return true;
		}
		else if(getActiveTabIndex() == TAB_INDEX_SOURCE){
			return false;
		}

		/* default */
		return false;
	}


	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.editors.ISynchronizable#isHandleCursorPositionChanged()
	 */
	public boolean isHandleCursorPositionChanged() {
		return handleCursorPositionChanged;
	}

	/**
	 * Returns whether quick diff info should be visible upon opening an editor. 
	 * Always returns <code>false</code> in this implementation 
	 *
	 * @return always <code>false</code>
	 */
	protected boolean isPrefQuickDiffAlwaysOn() {
		return false;
	}

	/**
	 * Flag if the control flow graph panel has to be shown in the outline view.
	 * @return the showGraphInOutline
	 */
	public boolean isShowGraphInSeparateView() {
		return showGraphInSeparateView;
	}
	/**
	 * Returns true if the source code is displayed by this viewer, 
	 * otherwise false. 
	 * @return true or false
	 */
	public boolean isSourceCodeLoaded() {
		return sourceCodeViewer != null && sourceCodeViewer.isSourceCodeLoaded();
	}

	/**
	 * Page changed by mouse click.
	 */
	protected void pageChange(int newPageIndex) {

		if(newPageIndex == TAB_INDEX_BYTECODE){

			updateOutlineGraphView();

			sourceCodeViewer.setHandleCursorPositionChanged(false);
			setHandleCursorPositionChanged(true);

		}
		else if(newPageIndex == TAB_INDEX_SOURCE){			

			updateOutlineGraphView();

			sourceCodeViewer.setHandleCursorPositionChanged(true);
			setHandleCursorPositionChanged(false);

			//TODO: the following probably needs to be fixed
			/* if the debugger is active */
			if(DebugUITools.getDebugContext() != null){
				Object o = DebugUITools.getDebugContext();

				/* the object is null if the debugger is not active */
				if(o instanceof JDIStackFrame){
					JDIStackFrame stackFrame = (JDIStackFrame)o;
					int line = -1;
					try {
						line = stackFrame.getLineNumber() - 1;
					} catch (DebugException e) {
						
						e.printStackTrace();
						BytecodeVisualizerPlugin.log(e);
					}
					sourceCodeViewer.selectSourceCodeLine(line, false); /* adapted to 0-based */
				}
			}

		}
		
		if(actionContributor != null)
			actionContributor.pageChanged(newPageIndex);

	}

	/**
	 * Removes changed line selection listener. 
	 */
	public void removeLineSelectionListener(IClassFileEditorSelectionListener listener) {
		lineSelectionListener.remove(listener);		
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#selectAndReveal(int, int)
	 */
	public void selectAndReveal(int start, int length) {
		selectAndRevealBytecode(start, length);
	}

	public void selectAndRevealBytecode(int start, int length) {
		super.selectAndReveal(start, length);
	}

	/**
	 * Selects and reveals the specified ranges in this text editor.
	 * The original method is used for debugging.
	 * @deprecated
	 */
	public void selectAndRevealOrigLine(int start, int length){
		super.selectAndReveal(start, length);
	}

	public void selectAndRevealSourceCode(int start, int length) {
		if (sourceCodeViewer != null) {
			sourceCodeViewer.selectAndReveal(start, length);
		}
	}

	/**
	 * Select a line from the stack trace.
	 * @param start
	 * @param length
	 */
	public boolean selectAndRevealStackTrace(int start, int length) {

		/* TODO: Select JAva Stack not supported if debugging is running */
		if(DebugUITools.getDebugContext() == null &&
				length != IClassFileEditor.INVALID_LINE){

			/* 
			 * FIX: bug#56
			 * Check if the StackTrace has been selected.  
			 */
			IWorkbenchPage page = getEditorSite().getPage();
			ISelection pageSel = page.getSelection("org.eclipse.ui.console.ConsoleView");

			/* if pageSel not null then the StackTrace selection is running */
			if(pageSel instanceof TextSelection){
				TextSelection textPagesel = (TextSelection) pageSel;

				/* check class name */
				String className = getClassName(textPagesel);
				String typeName = byteCodeDocumentProvider.getClassFileDocument().getClassName();			
				if(!className.equals(typeName)){
					startEditorForAnonymousClassAndReval(className);

					/* OK return method */
					return true;
				}

				int line = getSourceCodeLine(textPagesel) - 1; /* 0-based line number */

				if(line == IClassFileEditor.INVALID_LINE){
					AbstractUIPlugin p = CorePlugin.getPluginFromRegistry(CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID);
					p.getLog().log(
							new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Could not find the source code line.") //TODO: define constant
					);
					return false;
				}

				if(sourceCodeViewer.isSourceCodeLoaded()){				
					sourceCodeViewer.selectSourceCodeLine(line, true);

					IJavaElement element = sourceCodeViewer.getCurrentSelectedElement();
					if (element != null){
						setSelection(element, line + 1, true);
					}
				}
				else{
					/* selects the byte code line if the lineNumberTable is available */
					int sourceCodeLine = line;
					
					/* resolve sourceCodeLine into the ByteCodeLine */
					List<IMethodSection> methods = byteCodeDocumentProvider.getClassFileDocument().getMethodSections();
		
					int bytecodeLine = resolveLineNumberIntoBytecode(methods, sourceCodeLine + 1);
					if (bytecodeLine != ByteCodeConstants.INVALID_OFFSET) {
						selectLineAndReveal(bytecodeLine - 1); /* convert to 0-based lines */
					}
					else {
						//TODO: implement handler.
					}
					
					return true;
				}
			}
			else{ /* simple selection, should never happen */
				sourceCodeViewer.selectAndReveal(start, length);
			}

		}
		
		return false;
	}

	/**
	 * Selects the given line and revaluate visible position.
	 * @param the number of the line to be selected. 
	 */
	public void selectBytecodeLineAndReveal(int bytecodeDocumentLine, String elementName, int elementType) {
		IDocument document = byteCodeDocumentProvider.getBytecodeDocument(getBytecodeEditorInput());
		try {
			/* get line information */
			IRegion region = document.getLineInformation(bytecodeDocumentLine);
			int lineStartOffset = region.getOffset();
			int lineLenght = region.getLength();
			String lineString = document.get(lineStartOffset, lineLenght);
			
			if(elementName == null){
				super.selectAndReveal(lineStartOffset, 0);
			}
			
			int elementIndex, elementLength;
			switch(elementType){
			case IJavaElement.CLASS_FILE:
				elementIndex = lineString.indexOf(" " + elementName + " {") + 1;
				break;
			case IJavaElement.FIELD:
				elementIndex = lineString.indexOf(" " + elementName + ";") + 1;
				break;
			case IJavaElement.METHOD:
				elementIndex = lineString.indexOf(" " + elementName + "(") + 1;
				break;
			default:
				elementIndex= 0;
				elementLength= 0;
			}
			
			/* name not found */
			if(elementIndex == 0){
				elementLength = 0;
			}
			else{
				elementLength = elementName.length();
			}

			super.selectAndReveal(lineStartOffset + elementIndex, elementLength);

		} catch (BadLocationException e) {/*nothing to do */}		
	}

	/**
	 * Selects the given line.
	 * @param a 0-based number of the line to be selected. 
	 */
	public void selectLine(int line) {
		
		ISourceViewer sourceViewer = this.getSourceViewer();
		if (sourceViewer == null){
			return;
		}
		
		IDocument document= byteCodeDocumentProvider.getDocument(getEditorInput());
		try {		
			int offset= document.getLineOffset(line);
			sourceViewer.setSelectedRange(offset, 0);			
			
		} catch (BadLocationException e) {/*nothing to do */}		
	}

	/**
	 * Selects the line in the byte code document. 
	 * @param line
	 */
	public void selectLineAndRevaluate2(int line) {
		selectLineAndRevaluate2(line, false);
	}

	/**
	 * Selects the line in the byte code document.
	 * @param bytecodeLine
	 * @param b true if the line has to be marked
	 */
	public void selectLineAndRevaluate2(int bytecodeLine, boolean b) {
		IDocument document= byteCodeDocumentProvider.getBytecodeDocument(getBytecodeEditorInput());
		try {
			int offset= document.getLineOffset(bytecodeLine);
			ISourceViewer sourceViewer = this.getSourceViewer();
			
			if (sourceViewer == null)
				return;

			int len = 0;
			if (b) {
				len = document.getLineLength(bytecodeLine) - 1;
			}
			
			super.selectAndReveal(offset, len);

		} catch (BadLocationException e) {/*nothing to do */}		
	}

	/**
	 * Selects the given line and reval
	 * @param a 0-based number of the line to be selected. 
	 */
	public void selectLineAndReveal(int bytecodeLine) {
		
		ISourceViewer sourceViewer = this.getSourceViewer();
		if (sourceViewer == null){
			return;
		}
		
		IDocument document = byteCodeDocumentProvider.getBytecodeDocument(getBytecodeEditorInput());
		try {		
			int offset= document.getLineOffset(bytecodeLine);
			int len = document.getLineLength(bytecodeLine);
			sourceViewer.revealRange(offset, len);
			sourceViewer.setSelectedRange(offset, len);			
			
		} catch (BadLocationException e) {/*nothing to do */}		
	}

	/**
	 * Sets the currently active page.
	 * 
	 * @param pageIndex
	 *            the index of the page to be activated; the index must be valid
	 */
	protected void setActivePage(int pageIndex) {
		Assert.isTrue(pageIndex >= 0 && pageIndex < getPageCount());
		getTabFolder().setSelection(pageIndex);
		pageChange(pageIndex);
	}

	/**
	 * Gets the backroud color from preferencies and
	 * sets it in the control flow graph view.
	 */
	private void setControlFlowViewBackGround(){
	    RGB rgb = PreferenceConverter.getColor(getPreferenceStore(), AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND);
		Color c = getSharedColors().getColor(rgb);
		canvasControlFlowGraph.setBackground(c);
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.editors.ISynchronizable#setHandleCursorPositionChanged(boolean)
	 */
	public void setHandleCursorPositionChanged(boolean b) {
		handleCursorPositionChanged = b;
	}

	/**
	 * Sets the text label for the page with the given index. The page index
	 * must be valid. The text label must not be null.
	 * 
	 * @param pageIndex
	 *            the index of the page
	 * @param text
	 *            the text label
	 */
	protected void setPageText(int pageIndex, String text) {
		getItem(pageIndex).setText(text);
	}

	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.core.editors.ClassFileEditor#setSelection(org.eclipse.jdt.core.IJavaElement)
	 */
	public void setSelection(IJavaElement element) {
		setSelection(element, IClassFileEditor.INVALID_LINE, false);

		/* synchronize source code view */
		if(sourceCodeViewer != null){
			sourceCodeViewer.setSelection(element);
		}
	}

	/**
	 * Selects the line for the given element:
	 * - ClassFileSection
	 * - MethodSection
	 * - Variable
	 * 
	 * @param element
	 * @param line
	 * @param markLine true if the line has to be marked
	 */
	public void setSelection(IJavaElement element, int line, boolean markLine) {		

		if(element == null){
			return;
		}
		
		switch(element.getElementType()){
		case IJavaElement.FIELD: /* select field */
			String fieldName = element.getElementName();
			IFieldSection fieldSection = byteCodeDocumentProvider.getClassFileDocument().findFieldSection(fieldName);
			if (fieldSection!= null) {
					selectBytecodeLineAndReveal(fieldSection.getBytecodeDocumentLine(),
							fieldName,
							IJavaElement.FIELD);
			}
			break;
		case IJavaElement.METHOD:  /* select method */
			IMethod method = (IMethod) element;
			try {
				String methodName;
				if(method.isConstructor()){
					methodName = "<init>";
				}
				else{
					methodName = method.getElementName();
				}
				
				String methodSignature = ClassFileDocumentsUtils.resolveMethodSignature(method);
				IMethodSection methodSection = byteCodeDocumentProvider.getClassFileDocument().findMethodSection(methodName, methodSignature);

				if(methodSection!= null){
					if(line ==  IClassFileEditor.INVALID_LINE || 
							line ==  IClassFileEditor.METHOD_DECLARATION_LINE){
						
						if(method.isConstructor()){
							methodName = method.getElementName();
						}
						selectBytecodeLineAndReveal(methodSection.getFirstLine(),
								methodName,
								IJavaElement.METHOD);
					}
					else{						
						selectLineAndRevaluate2(methodSection.getBytecodeLine(line) - 1, markLine);
					}
					break;
				}

			} catch (JavaModelException e) {
				CorePlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR, CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID, e.getMessage(), e)
						);
			}
			
		default:
			/* select class */
			IClassFileDocument doc = byteCodeDocumentProvider.getClassFileDocument();
			if (doc != null) {
				int docLine = doc.getClassSignatureDocumentLine();
			    selectBytecodeLineAndReveal(docLine,
						element.getElementName(),
						IJavaElement.CLASS_FILE);
			}
		}

		/* call cursor hook */
		doHandleCursorPositionChanged();
	}


	/**
	 * Starts an editor for the given nested or anonymous class 
	 * of the current loaded class. The class name is a fully 
	 * qualified name separated by '.'. For example: java.lang.String 
	 * @param annonymousClassName
	 */
	protected IEditorPart startEditorForAnonymousClassAndReval(String annonymousClassName){
		
		IEditorPart result = null;
		
		IClassFileEditorInput classFileEditorInput = (IClassFileEditorInput) getEditorInput();

		/* get main class file from the edtor input */
		IClassFile origFile = classFileEditorInput.getClassFile();
		
		/* get package element*/
		IJavaElement packageElement = origFile.getParent();
		if(packageElement instanceof IPackageFragment){
			IPackageFragment packageFragment = (IPackageFragment)packageElement;
			
			/* calculate the anonymous class name */
			String packageString = packageFragment.getElementName();
			String className = annonymousClassName.subSequence(
					packageString.length() + 1, 
					annonymousClassName.length()
			).toString() + ".class";

			/* get class from the package */
			IClassFile f = packageFragment.getClassFile(className);

			/* Open editor */
			try {
			
				if(fReuseEditor){
					IWorkbenchPage page = getEditorSite().getPage();
					
					result = EditorUtility.isOpenInEditor(f);
					
					/* activate the editor we want to reuse */
					if(result != null){
						page.bringToTop(result);
					}
					else{ /* no editor open*/
						if ( isDirty() || page.isEditorPinned(this)) {
						    /* open a new editor */
							result = EditorUtility.openInEditor(f, true);
						} else if (this instanceof IReusableEditor) {
						    /* re-use editor */
							page.reuseEditor((IReusableEditor)this, new InternalClassFileEditorInput(f));
		                    if(!page.isPartVisible(result)) {
		                        page.bringToTop(result);
		                    }
		                    
		                    return this;

						} else {
						    // close editor, open a new one
							result = EditorUtility.openInEditor(f, true);
							page.closeEditor(this, false);
						}

					}
				}
				else{
					result = EditorUtility.openInEditor(f, true);
				}
			
			
			} catch (PartInitException e) {
				CorePlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR, CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID, e.getMessage(), e)
						);
			}
		}
		
		return result;
	}

	/**
	 * Gets current line color from preferences and sets the it in the
	 * control flow graph viewer.
	 * @param the control flow graph viewer.
	 */    
    private void synchronizeCurrentLineColor( ControlFlowGraphCanvas controlFlowGraphCanvas){
		RGB rgb = PreferenceConverter.getColor(getPreferenceStore(), AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR);
		Color c = getSharedColors().getColor(rgb);
		controlFlowGraphCanvas.setLineSelectorColor(c);
    }

	/**
	 * Synchronisation of the line selection in the text editor and
	 * the control flow graph view.
	 * The following table represents the implementation of listeners.
	 * 
	 * <p><code>  Text Editor             |    Control Flow Graph Viewer </code></p>
	 * <p><code>  ------------------------+----------------------------- </code></p>                      
	 * <p><code>  update line selection  <---  Key Listener	 			 </code></p>
	 * <p><code>  update line selection  <---  Mouse Listener			 </code></p>
	 * <p><code>  SWT.Paint Listener     ---->  update line selection 	 </code></p>
	 *  
	 * @param the control object of the first view. (text editor)
	 * @param the control object of the second view. (control flow graph view)
	 * @param the control flow graph viewer.
	 * @param the canvas of the control flow graph viewer.
	 */
    private void synchronizeLineSelection(final StyledText textWidget, final ControlFlowGraphCanvas canvasControlFlowGraph){
 	
    	final ScrollBar vBar1 = textWidget.getVerticalBar();
//		final ScrollBar vBar2 = canvasControlFlowGraph.getVerticalBar();
		
		/* paint listener for the text editor view */
		textWidget.addPaintListener(new PaintListener(){

			public void paintControl(PaintEvent e) {
				if(canvasControlFlowGraph.isDisposed())
					return;
				
				
				canvasControlFlowGraph.selectLine(getSelectedLine()/* changed to 0-based */);
				
				int y =  vBar1.getSelection();


				/* 
				 * FIX: bug52 Graph View does not focus the proper row
				 * 
				 * Scroll and check if the line selection is visible 
				 * 
				 *    A    (A)+----------------+/\                                                    
				 *    |       |                |||                          
				 *    |       |                |==  - Scroll bar Position    
				 *   (H)      |                |||                          
				 *    |       (B)              |||                          
				 *    |   (h) |IIIIIIIIIIIIIIII|||  - Selected line         
				 *    |       |                |||                          
				 *    V       +----------------+\/                          
				 * 
				 *  (A) - ViewPort Position P_vp = (vp_x, vp_y), vp_x is always 0
				 *  (B) - Selected Line Position P_sl = (sl_x, sl_y)
				 *  (H) - Height of the ViewPort vp_h
				 *  (h) - Height of the line selector sl_h
				 */

//				int vp_y = canvasControlFlowGraph.getViewport().getViewLocation().y ;
//				int vp_h = canvasControlFlowGraph.getViewport().getClientArea().height;
//				int sl_y = canvasControlFlowGraph.getSelectedLineLocation().y;
//				int sl_h = canvasControlFlowGraph.getLineHight();
//
//				int viewPortBottom = vp_y + vp_h;
//								
//				if(viewPortBottom - sl_h <  sl_y ){
//					canvasControlFlowGraph.scrollSmoothTo(canvasControlFlowGraph.getHorizontalBar().getSelection(), sl_y - vp_h + sl_h );				
//				}
//				else{
//					canvasControlFlowGraph.scrollSmoothTo(canvasControlFlowGraph.getHorizontalBar().getSelection(), y);
//
//					/* check after scroll if the line selector is visible */
//					vp_y = canvasControlFlowGraph.getViewport().getViewLocation().y ;
//					vp_h = canvasControlFlowGraph.getViewport().getClientArea().height;
//					viewPortBottom = vp_y + vp_h;	
//					sl_y = canvasControlFlowGraph.getSelectedLineLocation().y;
//					if(viewPortBottom - sl_h <  sl_y){
//						canvasControlFlowGraph.scrollSmoothTo(canvasControlFlowGraph.getHorizontalBar().getSelection(), sl_y - vp_h + sl_h );						
//					}					
//				}
				
				canvasControlFlowGraph.scrollSmoothTo(canvasControlFlowGraph.getHorizontalBar().getSelection(), y);
				
				/* by scrolling in the left window the canvas is not updated. 
				 * MAC OS specific correction. Not need for SWT 3.5 */
				canvasControlFlowGraph.getViewport().repaint();
			}
			
		});
		
		
		
		/* key listener for the control flow graph view */
		canvasControlFlowGraph.addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e) {
				switch(e.keyCode){
						case ARROW_UP: /* Up */	
						{
							StyledText widget = getSourceViewer().getTextWidget();
							widget.invokeAction(ST.LINE_UP);
							widget.redraw();
							selectLine(getSelectedLine());
							break;
						}
						case ARROW_DOWN: /* Down */
						{							
							StyledText widget = getSourceViewer().getTextWidget();
							widget.invokeAction(ST.LINE_DOWN);
							widget.redraw();
							selectLine(getSelectedLine());
							break;	
						}
				}
			}

			public void keyReleased(KeyEvent e) {
				/* nothing to do */
			}

        });
		
		/* mouse listener for the control flow graph view */
		canvasControlFlowGraph.getRootFigure().addMouseListener(new MouseListener(){
			public void mouseDoubleClicked(MouseEvent me) {
				/* nothing to do */
			}

			public void mousePressed(MouseEvent me) {
				Point p = me.getLocation();
				int line = p.y / textWidget.getLineHeight();
				selectLine(line);	/*  the line is 0-based */
			}

			public void mouseReleased(MouseEvent me) {
				/* nothing to do */
			}
		});
	}
	
	/**
	 * Synchronization of scroll bars in the left and right views.
	 * @param the control object of the first view. (text editor)
	 * @param the control object of the second view. (control flow graph view)
	 */
	private void synchronizeScrollBars(final StyledText textWidget, final FigureCanvas controlFlowGraphCanvas){
		/* Synchronize scroll bars */
		//final ScrollBar vBar1 = textWidget.getVerticalBar();
		final ScrollBar vBar2 = controlFlowGraphCanvas.getVerticalBar();

		/* 
		 * The listener for left scroll bar. 
		 * The right scroll bar is synchronized by the paint listener. 
		 */
		SelectionListener listener2 = new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				//int y =  vBar2.getSelection() * (vBar1.getMaximum() - vBar1.getThumb()) / Math.max(1, vBar2.getMaximum() - vBar2.getThumb());
				int y =  vBar2.getSelection();

				textWidget.setTopPixel( y);
				
				/* FIX: bug#41 Synchronization of Debug Current Instruction Pointer and Graph is broken */
				getVerticalRuler().update();
			}
		};

		vBar2.addSelectionListener (listener2);
	}

	
	/* (non-Javadoc)
	 * @see com.drgarbage.bytecodevisualizer.core.editors.ClassFileEditor#updateLineSectionListener(int, java.lang.Object)
	 */
	protected void updateLineSectionListener(int line, Object o){
		if (handleCursorPositionChanged) {
			for(IClassFileEditorSelectionListener listener: lineSelectionListener){
				listener.lineSelectionChanged(line/* changed to 0-based */, o);
			}
		}		
	}
	
	/**
	 * Updates Graph Outline.
	 * Disable the graph outline if the source code 
	 * view is active or enable if the byte code view
	 * is active. 
	 */
	private void updateOutlineGraphView(){
		int index = getActiveTabIndex();
		switch (index) {
		case TAB_INDEX_BYTECODE:
			/* enable graph view in the outline  */
			if(getCanvasControlFlowGraph() != null){
				getCanvasControlFlowGraph().setVisible(true);
			}
			break;
		case TAB_INDEX_SOURCE:
			/* disable graph view in the outline  */
			if(getCanvasControlFlowGraph() != null){
				getCanvasControlFlowGraph().setVisible(false);
			}
			break;
		default:
			/* other views */
			BytecodeVisualizerPlugin.log(new Status(IStatus.WARNING, CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID, "Uncovered tab index "+ index));
			break;
		}
	}
}
