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

package com.drgarbage.sourcecodevisualizer.editors;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.ICompilationUnitDocumentProvider;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditorBreadcrumb;
import org.eclipse.jdt.ui.IContextMenuConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.core.CorePlugin;
import com.drgarbage.core.preferences.CorePreferenceConstants;
import com.drgarbage.core.views.ControlFlowGraphViewPage;
import com.drgarbage.core.views.IControlFlowGraphView;
import com.drgarbage.core.views.IControlFlowGraphViewPage;
import com.drgarbage.sourcecodevisualizer.SourcecodeVisualizerPlugin;
import com.drgarbage.sourcecodevisualizer.actions.ConstructorFilterAction;
import com.drgarbage.sourcecodevisualizer.actions.MethodGraphFilterAction;
import com.drgarbage.sourcecodevisualizer.actions.OpenGraphInControlflowgraphFactory;
import com.drgarbage.sourcecodevisualizer.actions.RefreshAction;

/**
 * Java Editor with an embeded sourcecode visualizer.
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: JavaCodeEditor.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class JavaCodeEditor extends CompilationUnitEditor implements IControlFlowGraphView{

	/**
	 * Parent composite. 
	 */
	private Composite parent;

	/**
	 * Preference property change listener.
	 */
	private IPropertyChangeListener preferenceListener;

	/**
	 * Control flow graph viewer.
	 */
	private SourceCodeGraphCanvas canvasControlFlowGraph;

	/**
	 * Text widget.
	 */
	private StyledText textWidget;

	/**
	 * Document line number counter.
	 */
	private int lineCount = 0;

	/**
	 * Flag if the graph has to be shown in the outline.
	 */
	private boolean showGraphInSeparateView = false;	
	
	/**
	 * Graph Factory Action.
	 */
	private OpenGraphInControlflowgraphFactory graphFactoryAction = null;
	
	/**
	 * Reference to the selcted Method.
	 */
	private IMethod selectedMethod = null;
	
	/**
	 * Default Contructor.
	 */
	public JavaCodeEditor() {
		super();
	}
	
	
    /**
     * The canonical empty selection. This selection should be used 
     * for bug correction bug#91 NullPointerException if license has been expired.
     */
    public final StructuredSelection EMPTY = new StructuredSelection2();
    
    /* bug#91 NullPointerException if license has been expired. */
    class StructuredSelection2 extends StructuredSelection {
        public Object getFirstElement() {
        	return new JavaEditorBreadcrumb(getEditor());
        }
    }

    /* bug#91 NullPointerException if license has been expired. */
    private JavaEditor getEditor(){
    	return this;
    }

	/**
	 * Creates the SWT controls for this editor.
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(final Composite parent) {
		this.parent = parent;

		/* init preferences */
		String s = CorePlugin.getDefault().getPreferenceStore().getString(CorePreferenceConstants.GRAPH_PANEL_LOCATION);
		if (s.equals(CorePreferenceConstants.GRAPH_PANEL_LOCATION_SEPARATE_VIEW)){
			showGraphInSeparateView = true;		
		}
		else if (s.equals(CorePreferenceConstants.GRAPH_PANEL_LOCATION_EDITOR)){
			showGraphInSeparateView = false;
		}
		else {
			showGraphInSeparateView = false;
			//FIXME Warn here
		}
		
		final IEditorInput editorInput = getEditorInput();
		final IWorkbenchPage page = getEditorSite().getPage();
		final IEditorPart part = this;
		final String editorID = getEditorSite().getId();
		
		createPartControlsImpl(parent);

		
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

	private void createPartControlsImpl(Composite parent) {
		/* create controls */
		if(showGraphInSeparateView){
			createPartControlWithGraphOutline(parent);
		}
		else{
			createPartControlWithSach(parent);			
		}
	}

	/**
	 * Creates the SWT controls for this editor.
	 */
	public void createPartControlWithGraphOutline(final Composite parent) {
		
		final StackLayout layout = new StackLayout();
		parent.setLayout(layout);
		
		/* create the control for the Byte Code Editor */
		Composite editorParent = new Composite(parent, SWT.NONE);	
		editorParent.setLayout(new FillLayout());
		super.createPartControl(editorParent);
		layout.topControl = editorParent;

		/* deactivate graph actions */
		IAction a = getAction(OpenGraphInControlflowgraphFactory.ID);
		a.setEnabled(false);		
	}

	/**
	 * Creates the SWT controls for this editor.
	 * @param parent
	 */
	public void createPartControlWithSach(final Composite parent) {
		
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
		textWidget = getSourceViewer().getTextWidget();

		/* FIX: unfold document, because of the line number count */
		final ProjectionViewer viewer =(ProjectionViewer)getSourceViewer();
		if(viewer != null){
			if(viewer.isProjectionMode())
				viewer.doOperation(ProjectionViewer.TOGGLE);
		}
		
		/* canvas for the control flow graph viewer */
		canvasControlFlowGraph = new SourceCodeGraphCanvas(parent);
		List<IDirectedGraphExt> graphs = createGraphs();
		lineCount = textWidget.getLineCount();
		canvasControlFlowGraph.init(lineCount, textWidget.getLineHeight(), graphs);
	
		/* Wrap the second view: Control Flow Graph Viewer */
		FormData editorParentFormData2 = new FormData ();
		editorParentFormData2.left = new FormAttachment (sash, 0);
		editorParentFormData2.right = new FormAttachment (100, 0);
		editorParentFormData2.top = new FormAttachment (0, 0);
		editorParentFormData2.bottom = new FormAttachment (100, 0);
		canvasControlFlowGraph.setLayoutData(editorParentFormData2);

		/* init control flow graph view */
		setControlFlowViewBackGround();

		/* Synchronize scroll bars in the left and right views */
		synchronizeScrollBars(textWidget, canvasControlFlowGraph);

		/* Synchronize line selection in the left and right views */
		synchronizeLineSelection(textWidget, canvasControlFlowGraph);

		/* synchronize current line color */
		synchronizeCurrentLineColor();

		/* default form data for the sash */
		final FormData sashFormData = new FormData ();
		sashFormData.left = new FormAttachment (0, 0);
		sashFormData.top = new FormAttachment (0, 0);
		sashFormData.bottom = new FormAttachment (100, 0);
		sash.setLayoutData (sashFormData);

		final int graphAreaWidht = canvasControlFlowGraph.getFigureWidth();
		final int sashWidthoffset = 24; //sash + balks

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
		
		getSourceViewer().addTextListener(new ITextListener(){

			public void textChanged(TextEvent event) {
				if(lineCount != textWidget.getLineCount()){
					lineCount = textWidget.getLineCount();
					canvasControlFlowGraph.setHeightByLineCount(lineCount);
				}
			}
		});

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#setAction(java.lang.String, org.eclipse.jface.action.IAction)
	 */
	public void setAction(String actionID, IAction action) {
		super.setAction(actionID, action);
	}

	/*
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#editorContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 * @since 3.1
	 */
	public void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		IAction graphFactoryAction = getAction(OpenGraphInControlflowgraphFactory.ID);
		if(graphFactoryAction != null)
			//menu.add(graphFactoryAction);
			menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, graphFactoryAction);
		
		/* set context menu in the control flow view */
		Menu m = ((MenuManager)menu).getMenu();
		if(m != null && !m.isDisposed())//TODO: menu is disposed if the control flow graph view is closed.
			canvasControlFlowGraph.setMenu(m);
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

	/**
	 * Gets current line color from preferences and sets the it in the
	 * control flow graph viewer.
	 * @param the control flow graph viewer.
	 */    
    private void synchronizeCurrentLineColor(){
		RGB rgb = PreferenceConverter.getColor(getPreferenceStore(), AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR);
		Color c = getSharedColors().getColor(rgb);		
		canvasControlFlowGraph.setLineSelectorColor(c);
    }

	/**
	 * Create a list of graphs.
	 * @return list of graphs
	 */
	private List<IDirectedGraphExt> createGraphs(){

        IDocumentProvider p = getDocumentProvider();
		if (p instanceof ICompilationUnitDocumentProvider) {
			ICompilationUnitDocumentProvider cp= (ICompilationUnitDocumentProvider) p;
			ICompilationUnit unit = cp.getWorkingCopy(getEditorInput());                   
			IJavaProject jp = unit.getJavaProject();
			
			return SourcecodeGraphUtils.createGraphs(unit, jp);
		}

		return null;
	}
	
	class JavaCodeEditorPropertyChangeListener implements IPropertyChangeListener{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent event) {
			//System.out.println("event=" + event.getProperty()); //TODO: log for debuging

			/* bytecode visualizer property */
			if(event.getProperty().startsWith(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND)){	
				setControlFlowViewBackGround();
			}
			else if(event.getProperty().equals(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR)){
				RGB rgb = PreferenceConverter.getColor(getPreferenceStore(), AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR);
				Color c = getSharedColors().getColor(rgb);			
				if(!canvasControlFlowGraph.getLineSelectorColor().equals(c)){
					canvasControlFlowGraph.setLineSelectorColor(c);
				}
			}
			else if(event.getProperty().equals(JFaceResources.TEXT_FONT)){ /* text fond has been changed */							
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
				textWidget.setFont(f);

				//TODO: define better update method without new generation of graphs
				List<IDirectedGraphExt> graphs = createGraphs();
				canvasControlFlowGraph.init(textWidget.getLineCount(), textWidget.getLineHeight(), graphs);
			}
			else if(event.getProperty().startsWith(CorePreferenceConstants.GRAPH_COLOR_PREFIX)){
				/* bytecode visualizer property */
				//TODO: define better update method without new generation of graphs
				List<IDirectedGraphExt> graphs = createGraphs();
				canvasControlFlowGraph.init(textWidget.getLineCount(), textWidget.getLineHeight(), graphs);
			}
			else{
				/* other references which are not matched before */
				//System.out.println("event=" + event.getProperty());
			}
		}
	};
	
	/*
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#initializeEditor()
	 */
	protected void initializeEditor() {
		super.initializeEditor();
		
		/* create preference property listener */
		preferenceListener = new JavaCodeEditorPropertyChangeListener();

		/* fond and color preferences */
		JFaceResources.getFontRegistry().addListener(preferenceListener);

		/* bytecode visualizer property */
		EditorsUI.getPreferenceStore().addPropertyChangeListener(preferenceListener);

		/* bytecode visualizer property */
		SourcecodeVisualizerPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(preferenceListener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor#dispose()
	 */
	public void dispose() {
		/* remove property listeners */

		/* fond and color preferences */
		JFaceResources.getFontRegistry().removeListener(preferenceListener);

		/* bytecode visualizer property */
		EditorsUI.getPreferenceStore().removePropertyChangeListener(preferenceListener);

		/* bytecode visualizer property */
		SourcecodeVisualizerPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(preferenceListener);
		
		/* call default dispose method */
		super.dispose();
	}
	
	/**
	 * Synchronisation of scrol bars in the left and right views.
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
			}
		};

		vBar2.addSelectionListener (listener2);
	}

	/**
	 * Synchronization of the line selection in the text editor and
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
    private void synchronizeLineSelection(final StyledText textWidget, final SourceCodeGraphCanvas canvasControlFlowGraph){
	 	
    	final ScrollBar vBar1 = textWidget.getVerticalBar();
//		final ScrollBar vBar2 = canvasControlFlowGraph.getVerticalBar();
		
		/* paint listener for the text editor view */
		textWidget.addPaintListener(new PaintListener(){

			public void paintControl(PaintEvent e) {
				if(canvasControlFlowGraph.isDisposed())
					return;

				canvasControlFlowGraph.selectLine(getSelectedLine());
				
				int y =  vBar1.getSelection();
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
				selectLine(line);
			}

			public void mouseReleased(MouseEvent me) {
				/* nothing to do */
			}
		});
	}
	
	private static final int ARROW_DOWN = SWT.KEYCODE_BIT + 2; 	//keyCode=16777218 (KeyEvent KeyDown)
	private static final int ARROW_UP = SWT.KEYCODE_BIT + 1; 	//keyCode=16777217 (KeyEvent KeyUp)

	/**
	 * Returns the number of the line currently selected
	 * in the text editor.
	 * @return the selected line
	 */
	public  int getSelectedLine() {
		ISourceViewer sourceViewer = this.getSourceViewer();
		
		if (sourceViewer == null)
			return -1;
		
		/* point.x is offset */
		org.eclipse.swt.graphics.Point selectedRange = sourceViewer.getSelectedRange();
		IDocument document = getDocumentProvider().getDocument(getEditorInput());

		int line = -1;
		try {		
			line = document.getLineOfOffset(selectedRange.x);
		} catch (BadLocationException e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.err);
		}

		return line;
	}
	
	
	/**
	 * Selects the given line.
	 * @param the number of the line to be selected. 
	 */
	public void selectLine(int line) {
		IDocument document= getDocumentProvider().getDocument(getEditorInput());
		try {
			int offset= document.getLineOffset(line);
			ISourceViewer sourceViewer = this.getSourceViewer();
			
			if (sourceViewer == null)
				return;

			sourceViewer.setSelectedRange(offset, 0);

		} catch (BadLocationException e) {/*nothing to do */}		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor progressMonitor) {
		super.doSave(progressMonitor);
		
		/* simple implementation. The compilation is done by separate thread.
		 * Just wait a second. It should be enough to compile a source. */
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
		
		//TODO: imlement synchronisation of the threads by using of JavaCore listener.
//		JavaCore.addPreProcessingResourceChangedListener(new IResourceChangeListener(){
//			public void resourceChanged(IResourceChangeEvent event) {
//			}
//		}, IResourceChangeEvent.POST_CHANGE);
		
		refreshGraphPanel();

	}
	
	public void refreshGraphPanel(){
		if(canvasControlFlowGraph !=null){
			List<IDirectedGraphExt> graphs = createGraphs();
			canvasControlFlowGraph.init(textWidget.getLineCount(), textWidget.getLineHeight(), graphs);
		}
	}
	
    /**
     * Creates this editor's standard actions and connects them with the global
     * workbench actions for context, toplevel menu and toolbar.
     * <p>
     * Subclasses may extend.</p>
     */
	protected void createActions() {
		super.createActions();
		
		/* open graph in the control flow factory action */
		graphFactoryAction = new OpenGraphInControlflowgraphFactory(this);
		setAction(OpenGraphInControlflowgraphFactory.ID, graphFactoryAction);
		
		/* own actions*/
		IAction a = new ConstructorFilterAction(this);
		setAction(ConstructorFilterAction.ID, a);
		
		a = new MethodGraphFilterAction(this);
		setAction(MethodGraphFilterAction.ID, a);
		
		a = new RefreshAction(this);
		setAction(RefreshAction.ID, a);
	}
	
	protected void setSelection(ISourceReference reference, boolean moveCursor) {
		super.setSelection(reference, moveCursor);

		/*
		 * FIX: if license expired
		 * java.lang.NullPointerException
		 * at com.drgarbage.sourcecodevisualizer.editors.JavaCodeEditor.setSelection(JavaCodeEditor.java:827)
		 * at org.eclipse.jdt.internal.ui.javaeditor.JavaEditor.setSelection(JavaEditor.java:2380)
		 * at org.eclipse.jdt.internal.ui.javaeditor.EditorUtility.revealInEditor(EditorUtility.java:251)
		 * at org.eclipse.jdt.ui.JavaUI.revealInEditor(JavaUI.java:729)
		 */
		if(graphFactoryAction == null){
			return;
		}
		
		if(reference instanceof IMethod){
			graphFactoryAction.setEnabled(true);
			selectedMethod = (IMethod)reference;
		}
		else{
			if(graphFactoryAction != null){
				graphFactoryAction.setEnabled(false);
			}
		}

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#saveState(org.eclipse.ui.IMemento)
	 */
	public void saveState(org.eclipse.ui.IMemento selection){
		/*
		 * FIX: NullPointerException
		 * java.lang.NullPointerException
		 * at org.eclipse.ui.texteditor.AbstractTextEditor.saveState(AbstractTextEditor.java:6939)
		 * at com.drgarbage.sourcecodevisualizer.editors.JavaCodeEditor.saveState(JavaCodeEditor.java:854)
		 * at org.eclipse.ui.internal.EditorManager$10.run(EditorManager.java:1602) 
		 **/
		if(selection.getTextData() != null)
			super.saveState(selection);
	}
	
	
	/**
	 * @return the selectedMethod
	 */
	public IMethod getSelectedMethod() {
		return selectedMethod;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class required) {

		if (IControlFlowGraphViewPage.class.equals(required)) {
			return createGraphControlFlowViewPage();
		}

		if (IContentOutlinePage.class.equals(required)) {
			return super.getAdapter(required);
		}		
		
		return super.getAdapter(required);
	}
	
	/**
	 * Creates and initialize control flow graph canvas
	 */
	private void createAndInitializeControlFlowGraphCanvas(){
		if(getSourceViewer() == null){
			canvasControlFlowGraph = new SourceCodeGraphCanvas(parent);
			return;
		}

		/* create and initialize control flow graph canvas */
		/* create the control flow graph viewer */
		textWidget = getSourceViewer().getTextWidget();

		/* canvas for the control flow graph viewer */
		canvasControlFlowGraph = new SourceCodeGraphCanvas(parent);
		List<IDirectedGraphExt> graphs = createGraphs();
		lineCount = textWidget.getLineCount();
		canvasControlFlowGraph.init(lineCount, textWidget.getLineHeight(), graphs);
		
		/* init control flow graph view */
		setControlFlowViewBackGround();

		/* init control flow graph view */
		setControlFlowViewBackGround();

		/* Synchronize scroll bars in the left and right views */
		synchronizeScrollBars(textWidget, canvasControlFlowGraph);

		/* Synchronize line selection in the left and right views */
		synchronizeLineSelection(textWidget, canvasControlFlowGraph);

		/* synchronize current line color */
		synchronizeCurrentLineColor();
	}
	
	protected ControlFlowGraphViewPage createGraphControlFlowViewPage() {
		if(canvasControlFlowGraph == null || canvasControlFlowGraph.isDisposed()){
			/* recreate control flow graph viewer */
			createAndInitializeControlFlowGraphCanvas();
		}

		ControlFlowGraphViewPage page = new ControlFlowGraphViewPage(canvasControlFlowGraph);		
		return page;
	}

	/**
	 * Returns canvas of the control flow garph.
	 * @return the canvasControlFlowGraph
	 */
	public SourceCodeGraphCanvas getCanvasControlFlowGraph() {
		return canvasControlFlowGraph;
	}

	/**
	 * returns flag if the graph has to be shown in the outline view.
	 * @return the showGraphInOutline
	 */
	public boolean isShowGraphInSeparateView() {
		return showGraphInSeparateView;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#doRestoreState(org.eclipse.ui.IMemento)
	 */
	protected void doRestoreState(IMemento memento) {
			super.doRestoreState(memento);
	}
}
