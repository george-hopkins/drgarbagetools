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

package com.drgarbage.controlflowgraphfactory.editors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.draw2d.parts.Thumbnail;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DirectEditAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryPlugin;
import com.drgarbage.controlflowgraphfactory.actions.ByteCodelLayoutAlgorithmAction;
import com.drgarbage.controlflowgraphfactory.actions.CopyAction;
import com.drgarbage.controlflowgraphfactory.actions.CutAction;
import com.drgarbage.controlflowgraphfactory.actions.ExportAsImageAction;
import com.drgarbage.controlflowgraphfactory.actions.ExportGraphAction;
import com.drgarbage.controlflowgraphfactory.actions.HierarchicalLayoutAlgorithmAction;
import com.drgarbage.controlflowgraphfactory.actions.HorizontalCenterOrderAction;
import com.drgarbage.controlflowgraphfactory.actions.HorizontalLeftOrderAction;
import com.drgarbage.controlflowgraphfactory.actions.HorizontalRightOrderAction;
import com.drgarbage.controlflowgraphfactory.actions.MaxHeightAction;
import com.drgarbage.controlflowgraphfactory.actions.MaxWidhtAction;
import com.drgarbage.controlflowgraphfactory.actions.MinHeightAction;
import com.drgarbage.controlflowgraphfactory.actions.MinWidhtAction;
import com.drgarbage.controlflowgraphfactory.actions.OrderAbstractAction;
import com.drgarbage.controlflowgraphfactory.actions.PasteAction;
import com.drgarbage.controlflowgraphfactory.actions.PrintAction;
import com.drgarbage.controlflowgraphfactory.actions.VerticalBottomOrderAction;
import com.drgarbage.controlflowgraphfactory.actions.VerticalCenterOrderAction;
import com.drgarbage.controlflowgraphfactory.actions.VerticalTopOrderAction;
import com.drgarbage.controlflowgraphfactory.img.ControlFlowFactoryResource;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.utils.Messages;
import com.drgarbage.visualgraphic.editparts.DiagramEditPartFactory;
import com.drgarbage.visualgraphic.editparts.OutlineTreeEditPartFactory;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagramFactory;

/**
 * A graphical editor with flyout palette that can edit .garph files. The
 * binding between the .graph file extension and this editor is done in
 * plugin.xml
 *  
 * @version $Revision: 1523 $ 
 * $Id: ControlFlowGraphEditor.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class ControlFlowGraphEditor extends GraphicalEditorWithFlyoutPalette
// GraphicalEditorWithPalette
// GraphicalEditor
{
	/** This is the root of the editor's model. */
	private ControlFlowGraphDiagram diagram;

	/** Palette component, holding the tools and verticesLayer. */
	private static PaletteRoot PALETTE_MODEL;

	/** Key */
	private KeyHandler sharedKeyHandler;

	/** Outline Page for this editor */
	private ControlFlowGraphEditorOutlinePage outlinePage;

	/** Flag for saving the content of the editor */
	private boolean editorSaving = false;

	/**
	 * Create a new ControlFlowGraphEditor instance. This is called by the
	 * Workspace.
	 */
	public ControlFlowGraphEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}


	/**
	 * @param parent
	 */
	protected void re2CreateGraphicalViewer(Composite parent) {
		super.createGraphicalViewer(parent);
	}

	/**
	 * Returns the KeyHandler with common bindings for both the Outline and
	 * Graphical Views. For example, delete is a common action.
	 */
	protected KeyHandler getCommonKeyHandler() {
		if (sharedKeyHandler == null) {
			sharedKeyHandler = new KeyHandler();
			sharedKeyHandler.put(KeyStroke.getPressed(SWT.F2, 0),
					getActionRegistry().getAction(
							GEFActionConstants.DIRECT_EDIT));
		}
		return sharedKeyHandler;
	}

	/**
	 * Returns the graphical editor.
	 * 
	 * @return the graphical editor
	 */
	public FigureCanvas getEditor() {
		return (FigureCanvas) getGraphicalViewer().getControl();
	}

	/**
	 * Returns the graphical viewer.
	 * 
	 * @return the graphical viewer
	 */
	public GraphicalViewer getGraphicalViewer() {
		return super.getGraphicalViewer();
	}

	/**
	 * Returns the editor edit domain.
	 * 
	 * @return the editor edit domain
	 */
	public EditDomain getControlFlowGraphEditorEditDomain() {
		return this.getEditDomain();
	}

	/**
	 * Configure the graphical viewer before it receives contents.
	 * <p>
	 * This is the place to choose an appropriate RootEditPart and
	 * EditPartFactory for your editor. The RootEditPart determines the behavior
	 * of the editor's "work-area". For example, GEF includes zoomable and
	 * scrollable root edit parts. The EditPartFactory maps model elements to
	 * edit parts (controllers).
	 * </p>
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();

		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(new DiagramEditPartFactory());

		ScalableFreeformRootEditPart root = new ScalableFreeformRootEditPart();
		viewer.setRootEditPart(root);

		/* configure Zoom Manager */
		List<String> zoomLevels = new ArrayList<String>(3);
		zoomLevels.add(ZoomManager.FIT_ALL);
		zoomLevels.add(ZoomManager.FIT_WIDTH);
		zoomLevels.add(ZoomManager.FIT_HEIGHT);
		root.getZoomManager().setZoomLevelContributions(zoomLevels);

		/* define and register zoom actions */
		ZoomInAction zoomIn = new ZoomInAction(root.getZoomManager());
		zoomIn.setImageDescriptor(ControlFlowFactoryResource.zoom_in_16x16);
		zoomIn.setAccelerator(SWT.CTRL | 'I');

		ZoomOutAction zoomOut = new ZoomOutAction(root.getZoomManager());
		zoomOut.setImageDescriptor(ControlFlowFactoryResource.zoom_out_16x16);
		zoomOut.setAccelerator(SWT.CTRL | 'O');

		getActionRegistry().registerAction(zoomIn);
		getActionRegistry().registerAction(zoomOut);

		/* define and register direct edit action */
		IAction action = new DirectEditAction((IWorkbenchPart) this);
		getActionRegistry().registerAction(action);
		getSelectionActions().add(action.getId());
		
		
		action = new CopyAction(this);
	    getEditorSite().getActionBars().setGlobalActionHandler(action.getId(), action);
	    getActionRegistry().registerAction(action);
	    //getSelectionActions().add(action.getId());
	    getGraphicalViewer().addSelectionChangedListener((ISelectionChangedListener) action);
	    
	    action = new PasteAction(this);
	    action.setEnabled(true);
	    getEditorSite().getActionBars().setGlobalActionHandler(action.getId(), action);
	    getActionRegistry().registerAction(action);
	    getSelectionActions().add(action.getId());
	    
	    action = new CutAction(this);
	    action.setEnabled(true);
	    getEditorSite().getActionBars().setGlobalActionHandler(action.getId(), action);
	    getActionRegistry().registerAction(action);
//	    getSelectionActions().add(action.getId());
	    getGraphicalViewer().addSelectionChangedListener((ISelectionChangedListener) action);
	    
		/* define and register graph layout algorithm actions */
		ByteCodelLayoutAlgorithmAction algorithmAction = new ByteCodelLayoutAlgorithmAction();
		algorithmAction.setActiveEditor(this);
		getActionRegistry().registerAction(algorithmAction);

		HierarchicalLayoutAlgorithmAction algorithmAction2 = new HierarchicalLayoutAlgorithmAction();
		algorithmAction2.setActiveEditor(this);
		getActionRegistry().registerAction(algorithmAction2);
		
		OrderAbstractAction oaa = new HorizontalLeftOrderAction();
		oaa.setActiveEditor(this);
		getActionRegistry().registerAction(oaa);

		oaa = new HorizontalCenterOrderAction();
		oaa.setActiveEditor(this);
		getActionRegistry().registerAction(oaa);
		
		oaa = new HorizontalRightOrderAction();
		oaa.setActiveEditor(this);
		getActionRegistry().registerAction(oaa);
		
		oaa = new VerticalTopOrderAction();
		oaa.setActiveEditor(this);
		getActionRegistry().registerAction(oaa);
		
		oaa = new VerticalCenterOrderAction();
		oaa.setActiveEditor(this);
		getActionRegistry().registerAction(oaa);
		
		oaa = new VerticalBottomOrderAction();
		oaa.setActiveEditor(this);
		getActionRegistry().registerAction(oaa);
		
		/* size actions */
		oaa = new MaxHeightAction();
		oaa.setActiveEditor(this);
		getActionRegistry().registerAction(oaa);
		
		oaa = new MinHeightAction();
		oaa.setActiveEditor(this);
		getActionRegistry().registerAction(oaa);
		
		oaa = new MaxWidhtAction();
		oaa.setActiveEditor(this);
		getActionRegistry().registerAction(oaa);
		
		oaa = new MinWidhtAction();
		oaa.setActiveEditor(this);
		getActionRegistry().registerAction(oaa);		
		
		/* define and register export actions */
		PrintAction printAction = new PrintAction();
		printAction.setActiveEditor(this);
		getActionRegistry().registerAction(printAction);

		ExportAsImageAction exportAsImage = new ExportAsImageAction();
		exportAsImage.setActiveEditor(this);
		getActionRegistry().registerAction(exportAsImage);

		ExportGraphAction exportAsGraphXML = new ExportGraphAction();
		exportAsGraphXML.setActiveEditor(this);
		getActionRegistry().registerAction(exportAsGraphXML);

		/* configure Key handler */
		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));

		/* configure the context menu provider */
		ContextMenuProvider cmProvider = new ControlFlowGraphEditorContextMenuProvider(
				viewer, getActionRegistry());
		viewer.setContextMenu(cmProvider);
		getSite().registerContextMenu(cmProvider, viewer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#commandStackChanged(java.util.EventObject)
	 */
	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}

	private void createOutputStream(OutputStream os) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(getModel());
		oos.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#createPaletteViewerProvider()
	 */
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()) {
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				/*
				 * create a drag source listener for this palette viewer
				 * together with an appropriate transfer drop target listener,
				 * this will enable model element creation by dragging a
				 * CombinatedTemplateCreationEntries from the palette into the
				 * editor
				 * 
				 * @see ControlFlowGraphEditor#createTransferDropTargetListener()
				 */
				viewer
						.addDragSourceListener(new TemplateTransferDragSourceListener(
								viewer));
			}
		};
	}

	/**
	 * Create a transfer drop target listener. When using a
	 * CombinedTemplateCreationEntry tool in the palette, this will enable model
	 * element creation by dragging from the palette.
	 * 
	 * @see #createPaletteViewerProvider()
	 */
	private TransferDropTargetListener createTransferDropTargetListener() {
		return new TemplateTransferDropTargetListener(getGraphicalViewer()) {
			protected CreationFactory getFactory(Object template) {
				return new SimpleFactory((Class<?>) template);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		editorSaving = true;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			createOutputStream(out);
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			file.setContents(new ByteArrayInputStream(out.toByteArray()), true, /*
																				 * keep
																				 * saving,
																				 * even
																				 * if
																				 * IFile
																				 * is
																				 * out
																				 * of
																				 * sync
																				 * with
																				 * the
																				 * Workspace
																				 */
			false, /* dont keep history */
			monitor); /* progress monitor */
			getCommandStack().markSaveLocation();
		} catch (CoreException ce) {
			ce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		editorSaving = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
		/* Show a SaveAs dialog */
		Shell shell = getSite().getWorkbenchWindow().getShell();
		SaveAsDialog dialog = new SaveAsDialog(shell);
		dialog.setOriginalFile(((IFileEditorInput) getEditorInput()).getFile());
		dialog.open();

		IPath path = dialog.getResult();
		if (path != null) {
			/* try to save the editor's contents under a different file name */
			final IFile file = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(path);
			try {
				new ProgressMonitorDialog(shell).run(false, /* don't fork */
				false, /* not cancelable */
				new WorkspaceModifyOperation() { /* run this operation */
					public void execute(final IProgressMonitor monitor) {
						try {
							ByteArrayOutputStream out = new ByteArrayOutputStream();
							createOutputStream(out);
							file.create(new ByteArrayInputStream(out
									.toByteArray()), /* contents */
							true, /*
									 * keep saving, even if IFile is out of sync
									 * with the Workspace
									 */
							monitor); /* progress monitor */
						} catch (CoreException ce) {
							Messages.error(CoreException.class.getName() + CoreMessages.ExceptionAdditionalMessage);								
							ControlFlowFactoryPlugin.getDefault().getLog().log(
									new Status(IStatus.ERROR, 
											ControlFlowFactoryPlugin.PLUGIN_ID, 
											CoreException.class.getName(), ce)
									);
							
						} catch (IOException ioe) {
							Messages.error(IOException.class.getName() + CoreMessages.ExceptionAdditionalMessage);								
							ControlFlowFactoryPlugin.getDefault().getLog().log(
									new Status(IStatus.ERROR, 
											ControlFlowFactoryPlugin.PLUGIN_ID, 
											IOException.class.getName(), ioe)
									);
						}
					}
				});
				/* set input to the new file */
				setInput(new FileEditorInput(file));
				getCommandStack().markSaveLocation();
			} catch (InterruptedException ie) {
				/* should not happen, since the monitor dialog is not cancelable */
				ie.printStackTrace();
			} catch (InvocationTargetException ite) {
				ite.printStackTrace();
			}
		}
	}

	public Object getAdapter(Class type) {
		if (type == IContentOutlinePage.class) {
			outlinePage = new ControlFlowGraphEditorOutlinePage(
					new TreeViewer());
			return outlinePage;
		}
		if (type == ZoomManager.class)
			return getGraphicalViewer().getProperty(
					ZoomManager.class.toString());

		return super.getAdapter(type);
	}

	/**
	 * Gets the model.
	 */
	public ControlFlowGraphDiagram getModel() {
		return diagram;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot()
	 */
	protected PaletteRoot getPaletteRoot() {
		if (PALETTE_MODEL == null)
			PALETTE_MODEL = ControlFlowGraphEditorPaletteFactory
					.createPalette();
		return PALETTE_MODEL;
	}

	/**
	 * Handling of load exception. If an exception occurs the the empty diagram
	 * is shown.
	 */
	private void handleLoadException(Exception e) {
		Messages.error(ControlFlowFactoryMessages.CannotLoadGraphFile
				+ CoreMessages.ExceptionAdditionalMessage);

		ControlFlowFactoryPlugin.getDefault().getLog().log(
				new Status(IStatus.ERROR, 
						ControlFlowFactoryPlugin.PLUGIN_ID, 
						ControlFlowFactoryMessages.CannotLoadGraphFile, e)
				);
		
		diagram = new ControlFlowGraphDiagram();
	}

	/**
	 * Set up the editor's inital content (after creation).
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#initializeGraphicalViewer()
	 */
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents(getModel()); /* set the contents of this editor */

		/* listen for dropped parts */
		viewer.addDropTargetListener(createTransferDropTargetListener());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		try {
			IFile file = ((IFileEditorInput) input).getFile();
			ObjectInputStream in = new ObjectInputStream(file.getContents());
			diagram = (ControlFlowGraphDiagram) in.readObject();
			in.close();
			setPartName(file.getName());
			
			/* init name */
			diagram.setPropertyValue("name", file.getName());

		} catch (IOException e) {
			handleLoadException(e);
		} catch (CoreException e) {
			handleLoadException(e);
		} catch (ClassNotFoundException e) {
			handleLoadException(e);
		}

		if (!editorSaving) {
			if (getGraphicalViewer() != null) {
				getGraphicalViewer().setContents(getModel());
			}
			if (outlinePage != null) {
				outlinePage.setContents(getModel());
			}
		}
		
	}

	/**
	 * Creates the diagrapm
	 */
	protected ControlFlowGraphDiagram createDiagram(String[] classPath,
			String mPackage, String mClassName, String mMethodName,
			String mMethodSignature) {
		ControlFlowGraphDiagram controlFlowGraphDiagram = null;
		try {
			controlFlowGraphDiagram = ControlFlowGraphDiagramFactory
					.buildByteCodeControlFlowDiagram(classPath, mPackage,
							mClassName, mMethodName, mMethodSignature);
		} catch (ControlFlowGraphException e) {
			ControlFlowFactoryPlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR, 
							ControlFlowFactoryPlugin.PLUGIN_ID, 
							ControlFlowGraphException.class.getName(), e)
					);
			return null;
		} catch (IOException e) {
			ControlFlowFactoryPlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR, 
							ControlFlowFactoryPlugin.PLUGIN_ID, 
							IOException.class.getName(), e)
					);
			return null;
		}

		return controlFlowGraphDiagram;

	}

	/**
	 * Creates an outline pagebook for this editor.
	 */
	public class ControlFlowGraphEditorOutlinePage extends ContentOutlinePage
			implements IAdaptable {

		private PageBook pageBook;
		private Control outline;
		private Canvas overview;
		private IAction showOutlineAction, showOverviewAction;
		static final int ID_OUTLINE = 0;
		static final int ID_OVERVIEW = 1;
		private Thumbnail thumbnail;
		private DisposeListener disposeListener;

		/**
		 * Create a new outline page for the control flow graph editor.
		 * 
		 * @param viewer
		 *            a viewer (TreeViewer instance) used for this outline page
		 * @throws IllegalArgumentException
		 *             if editor is null
		 */
		public ControlFlowGraphEditorOutlinePage(EditPartViewer viewer) {
			super(viewer);
		}

		/**
		 * @see org.eclipse.ui.part.IPageBookViewPage#init(org.eclipse.ui.part.IPageSite)
		 */
		public void init(IPageSite pageSite) {
			super.init(pageSite);
			ActionRegistry registry = getActionRegistry();
			IActionBars bars = pageSite.getActionBars();
			String id = ActionFactory.UNDO.getId();
			bars.setGlobalActionHandler(id, registry.getAction(id));
			id = ActionFactory.REDO.getId();
			bars.setGlobalActionHandler(id, registry.getAction(id));
			id = ActionFactory.DELETE.getId();
			bars.setGlobalActionHandler(id, registry.getAction(id));
			
			bars.updateActionBars();
		}

		protected void configureOutlineViewer() {
			getViewer().setEditDomain(getEditDomain());
			getViewer().setEditPartFactory(new OutlineTreeEditPartFactory());
			ContextMenuProvider provider = new ControlFlowGraphEditorContextMenuProvider(
					getViewer(), getActionRegistry());
			getViewer().setContextMenu(provider);
			getSite()
					.registerContextMenu(
							"com.drgarbage.controlflowgraphfactory.plugin.outline.contextmenu", //$NON-NLS-1$
							provider, getSite().getSelectionProvider());
			getViewer().setKeyHandler(getCommonKeyHandler());
			getViewer()
					.addDropTargetListener(
							(TransferDropTargetListener) new TemplateTransferDropTargetListener(
									getViewer()));
			IToolBarManager tbm = getSite().getActionBars().getToolBarManager();
			showOutlineAction = new Action() {
				public void run() {
					showPage(ID_OUTLINE);
				}
			};
			showOutlineAction
					.setImageDescriptor(ControlFlowFactoryResource.icon_outline_16x16); //$NON-NLS-1$
			tbm.add(showOutlineAction);
			showOverviewAction = new Action() {
				public void run() {
					showPage(ID_OVERVIEW);
				}
			};
			showOverviewAction
					.setImageDescriptor(ControlFlowFactoryResource.icon_overview_16x16); //$NON-NLS-1$
			tbm.add(showOverviewAction);
			/* set overview as a default view */
			showPage(ID_OVERVIEW);
		}

		public void createControl(Composite parent) {
			pageBook = new PageBook(parent, SWT.NONE);
			outline = getViewer().createControl(pageBook);
			overview = new Canvas(pageBook, SWT.NONE);
			pageBook.showPage(outline);
			configureOutlineViewer();
			hookOutlineViewer();
			initializeOutlineViewer();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.part.IPage#dispose()
		 */
		public void dispose() {
			unhookOutlineViewer();
			if (thumbnail != null) {
				thumbnail.deactivate();
				thumbnail = null;
			}
			super.dispose();
			ControlFlowGraphEditor.this.outlinePage = null;
			outlinePage = null;
		}

		public Object getAdapter(Class adapter) {
			if (adapter == ZoomManager.class)
				return getGraphicalViewer().getProperty(
						ZoomManager.class.toString());
			return null;
		}

		public Control getControl() {
			return pageBook;
		}

		protected void hookOutlineViewer() {
			getSelectionSynchronizer().addViewer(getViewer());
		}

		protected void initializeOutlineViewer() {
			setContents(getModel());
		}

		protected void initializeOverview() {
			LightweightSystem lws = new LightweightSystem(overview);
			RootEditPart rep = getGraphicalViewer().getRootEditPart();
			if (rep instanceof ScalableFreeformRootEditPart) {
				ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart) rep;
				thumbnail = new ScrollableThumbnail((Viewport) root.getFigure());
				thumbnail.setBorder(new MarginBorder(3));
				thumbnail.setSource(root
						.getLayer(LayerConstants.PRINTABLE_LAYERS));
				lws.setContents(thumbnail);
				disposeListener = new DisposeListener() {
					public void widgetDisposed(DisposeEvent e) {
						if (thumbnail != null) {
							thumbnail.deactivate();
							thumbnail = null;
						}
					}
				};
				getEditor().addDisposeListener(disposeListener);
			}
		}

		public void setContents(Object contents) {
			getViewer().setContents(contents);
		}

		protected void showPage(int id) {
			if (id == ID_OUTLINE) {
				showOutlineAction.setChecked(true);
				showOverviewAction.setChecked(false);
				pageBook.showPage(outline);
				if (thumbnail != null)
					thumbnail.setVisible(false);
			} else if (id == ID_OVERVIEW) {
				if (thumbnail == null)
					initializeOverview();
				showOutlineAction.setChecked(false);
				showOverviewAction.setChecked(true);
				pageBook.showPage(overview);
				if (thumbnail != null) {
					thumbnail.setVisible(true);
				}
			}
		}

		protected void unhookOutlineViewer() {
			getSelectionSynchronizer().removeViewer(getViewer());
			if (disposeListener != null && getEditor() != null
					&& !getEditor().isDisposed())
				getEditor().removeDisposeListener(disposeListener);
		}
	}
}