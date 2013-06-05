/**
 * Copyright (c) 2008-2013, Dr. Garbage Community
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;

import com.drgarbage.asm.Opcodes;
import com.drgarbage.asm.render.intf.IClassFileDocument;
import com.drgarbage.asm.render.intf.IInstructionLine;
import com.drgarbage.asm.render.intf.IMethodSection;
import com.drgarbage.bytecode.ByteCodeConstants;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.bytecodevisualizer.editors.BytecodeDocumentProvider;
import com.drgarbage.bytecodevisualizer.editors.BytecodeEditor;
import com.drgarbage.bytecodevisualizer.editors.IClassFileEditorSelectionListener;
import com.drgarbage.bytecodevisualizer.operandstack.OperandStack;
import com.drgarbage.bytecodevisualizer.operandstack.OperandStack.NodeStackProperty;
import com.drgarbage.bytecodevisualizer.operandstack.OperandStack.OperandStackPropertyConstants;
import com.drgarbage.bytecodevisualizer.operandstack.OperandStack.OpstackRepresenation;
import com.drgarbage.bytecodevisualizer.operandstack.OperandStackAnalysis;
import com.drgarbage.controlflowgraph.ControlFlowGraphUtils;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;
import com.drgarbage.controlflowgraph.intf.INodeType;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.core.img.CoreImg;
import com.drgarbage.javasrc.JavaLexicalConstants;

/**
 * The abstract Operand Stack View Page.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public abstract class OperandStackViewPage extends Page {

	/**
	 * Tree Viewer of the OperandStack view Page.
	 */
	private TreeViewer treeViewer;

	/**
	 * Tree Column of the OperandStack view.
	 */
	private TreeColumn column, column3, column4, column5, column6;

	/**
	 * Reference to the selected method instance.
	 */
	private IMethodSection methodInput;

	/**
	 * reference to the operand stack.
	 */
	private OperandStack operandStack;

	/* Menu actions */
	private IAction showTreeViewAction, showBasicBlockViewAction, showInstructioneListViewAction, 
	showOSBeforeColumnAction, showOSAfterColumnAction, showDescriptionColumnAction, showOSDepthColumnAction, 
	displayAllAction, displaySimpleAction, displayTypesAction, displayValuesAction, showAnalyseReportAction;

	static enum OperandStackView_ID{
		TREE_VIEW,
		BASICBKLOCK_VIEW,
		INSTR_LIST_VIEW;
	};

	/**
	 * Kind of the view. The value is one of the  REE_VIEW, 
	 * BASICBKLOCK_VIEW or INSTR_LIST_VIEW.
	 */
	OperandStackView_ID view_ID = OperandStackView_ID.TREE_VIEW;

	static enum OperandStackDisplayFormat_ID{
		DISPLAY_SIMPLE,
		DISPLAY_TYPES,
		DISPLAY_VALUES,
		DISPLAY_ALL
	};

	static enum ColumnIndex{
		OFFSET(0),
		OPCODEMNEMONIC(1),
		OPSTACKBEFORE(2),
		OPSTACKAFTER(3),
		OPSTACKDEPTH(4),
		DESCRIPTION(5);

		private final int INDEX;

		private ColumnIndex(int INDEX){
			this.INDEX = INDEX;
		}
		public int getIndex(){
			return INDEX;
		}

	};
	
	public static Color RED = new Color(null,255,0,0);
	public static Color ORANGE = new Color(null,255,127,0);
	public static Color GREEN = new Color(null, 50, 205, 50);
	public static Color BLUE = new Color(null, 120, 178, 255);

	/**
	 * Default presentation format is SIMPLE
	 */
	private OpstackRepresenation opstackRepresenationFormat = OpstackRepresenation.SIMPLE;

	/**
	 * The standard value for displaying all information in 2 columns "Operand Stack before" and "Operand Stack after"
	 */
	OperandStackDisplayFormat_ID displayFormat_ID = OperandStackDisplayFormat_ID.DISPLAY_ALL;

	/**
	 * Map of the tree items to the byte code line numbers in the editor.
	 */
	private Map<Integer, Node> treeMap;

	/**
	 * Listener for synchronization of lines with the BCV.
	 */
	private IClassFileEditorSelectionListener classFileEditorSelectionListener;

	/**
	 * Use the mutex variable to avoid call backs from the editor view. 
	 */
	private boolean treeViewerSelectionMutex = false; 

	/**
	 * Reference to the active byte code editor.
	 */
	private BytecodeEditor editor;

	private synchronized boolean isTreeViewerSelectionMutex() {
		return treeViewerSelectionMutex;
	}

	private synchronized void setTreeViewerSelectionMutex(boolean b) {
		treeViewerSelectionMutex = b;
	}

	/**
	 * Constructs an outline.
	 * @param editor
	 */
	public OperandStackViewPage() {
		super();
	}

	/**
	 * Returns the TreeTable view object.
	 * @return treeView
	 */
	public TreeViewer getTreeView() {
		return treeViewer;

	}

	/**
	 * Returns the current view id, one of the REE_VIEW, BASICBKLOCK_VIEW, INSTR_LIST_VIEW. 
	 * @return view_ID
	 */
	public OperandStackView_ID getView_ID() {
		return view_ID;
	}

	/**
	 * Sets the view id, one of the REE_VIEW, BASICBKLOCK_VIEW, INSTR_LIST_VIEW.
	 * @param view_ID
	 */
	public void setView_ID(OperandStackView_ID view_ID) {
		this.view_ID = view_ID;
	}

	/**
	 * Returns the current displayFormat id, one of the 
	 * <code>DISPLAY_ALL</code>, 
	 * <code>DISPLAY_SIMPLE</code>, 
	 * <code>DISPLAY_TYPES</code> or
	 * <code>DISPLAY_VALUES</code>
	 * 
	 * @return displayFormat_ID
	 */
	public OperandStackDisplayFormat_ID getDisplayFormat_ID(){
		return displayFormat_ID;
	}

	/**
	 * Sets the display format id, one of the 
	 * <code>DISPLAY_ALL</code>, 
	 * <code>DISPLAY_SIMPLE</code>, 
	 * <code>DISPLAY_TYPES</code> or
	 * <code>DISPLAY_VALUES</code>
	 * 
	 * @param displayFormat_ID
	 */
	public void setDisplayFormat_ID(OperandStackDisplayFormat_ID displayFormat_ID){
		this.displayFormat_ID = displayFormat_ID;
	}

	private void setMesssageInStatusLine(){
		if(methodInput == null){
			return;
		}

		IActionBars bars = getSite().getActionBars();

		IStatusLineManager slm = bars.getStatusLineManager();

		/* 
		 * DO NOT DELETE THIS LINE. 
		 * This is probably a bug in the StatusLineManeger implementation.
		 * The setMessage() is first visible if the error message has 
		 * been cleaned by the setErrorMessage(""); 
		 */
		slm.setErrorMessage("");

		if(operandStack.getMaxStackSize() > methodInput.getMaxStack()){

			StringBuffer buf = new StringBuffer();
			buf.append(CoreMessages.Error);
			buf.append(JavaLexicalConstants.COLON);
			buf.append(JavaLexicalConstants.SPACE);
			buf.append(BytecodeVisualizerMessages.OperandStackAnalysis_Error_StackOverflow);
			buf.append(JavaLexicalConstants.COMMA);
			buf.append(JavaLexicalConstants.SPACE);
			buf.append("max_stack is "); //$NON-NLS-1$
			buf.append(methodInput.getMaxStack());
			buf.append(", calculated max stack size is "); //$NON-NLS-1$
			buf.append(operandStack.getMaxStackSize());
			buf.append(JavaLexicalConstants.DOT);

			slm.setErrorMessage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK),
					buf.toString());
		}
		else if(operandStack.getMaxStackSize() < methodInput.getMaxStack()){
			StringBuffer buf = new StringBuffer();
			buf.append(CoreMessages.Warning);
			buf.append(JavaLexicalConstants.COLON);
			buf.append(JavaLexicalConstants.SPACE);
			buf.append(BytecodeVisualizerMessages.OperandStackAnalysis_Warning_StackUnderflow);
			buf.append(JavaLexicalConstants.COMMA);
			buf.append(JavaLexicalConstants.SPACE);
			buf.append("max_stack is "); //$NON-NLS-1$
			buf.append(methodInput.getMaxStack());
			buf.append(", calculated max stack size is "); //$NON-NLS-1$
			buf.append(operandStack.getMaxStackSize());
			buf.append(JavaLexicalConstants.DOT);

			slm.setMessage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK),
					buf.toString());
		}
		else {
			StringBuffer buf = new StringBuffer();
			buf.append("max_stack: "); //$NON-NLS-1$
			buf.append(methodInput.getMaxStack());
			buf.append(", max_locals: "); //$NON-NLS-1$
			buf.append(methodInput.getMaxLocals());

			slm.setMessage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK),
					buf.toString());
		}

		slm.update(true);
	}

	private void configureToolBar(){

		IActionBars bars = getSite().getActionBars();
		IToolBarManager tbm = bars.getToolBarManager();

		showTreeViewAction = new Action() {
			public void run() {
				activateView(OperandStackView_ID.TREE_VIEW);
			}
		};
		showTreeViewAction
		.setImageDescriptor(CoreImg.bytecodeViewIcon_16x16);
		tbm.add(showTreeViewAction);
		showTreeViewAction.setText(BytecodeVisualizerMessages.TreeViewAction_Text);
		showTreeViewAction.setToolTipText(BytecodeVisualizerMessages.TreeViewAction_Text);
		showTreeViewAction.setChecked(true);

		showBasicBlockViewAction = new Action() {
			public void run() {
				activateView(OperandStackView_ID.BASICBKLOCK_VIEW);
			}
		};
		showBasicBlockViewAction
		.setImageDescriptor(CoreImg.basicblockViewIcon_16x16);
		tbm.add(showBasicBlockViewAction);
		showBasicBlockViewAction.setText(BytecodeVisualizerMessages.BasicViewAction_Text);
		showBasicBlockViewAction.setToolTipText(BytecodeVisualizerMessages.BasicViewAction_Text);
		showBasicBlockViewAction.setChecked(false);

		showInstructioneListViewAction = new Action() {
			public void run() {
				activateView(OperandStackView_ID.INSTR_LIST_VIEW);
			}
		};
		showInstructioneListViewAction
		.setImageDescriptor(CoreImg.bytecode_listview_16x16);
		showInstructioneListViewAction.setText(BytecodeVisualizerMessages.InstructionListView_Text);
		showInstructioneListViewAction.setToolTipText(BytecodeVisualizerMessages.InstructionListView_Text);
		tbm.add(showInstructioneListViewAction);
		showInstructioneListViewAction.setChecked(false);

		enableActions(false);

		showAnalyseReportAction = new Action(){
			public void run(){
				OperandStackReportDialog analyseReport = new OperandStackReportDialog();
				analyseReport.setText(OperandStackAnalysis.executeAll(operandStack, methodInput));
				analyseReport.open();
			}
		};

		showAnalyseReportAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		showAnalyseReportAction.setText(BytecodeVisualizerMessages.OpenOpstackAnalyseAction_Text);
		showAnalyseReportAction.setToolTipText(BytecodeVisualizerMessages.OpenOpstackAnalyseAction_Tooltip);

		tbm.add(new Separator());
		tbm.add(showAnalyseReportAction);

		tbm.update(true);

		/* menu */
		final IMenuManager imb = bars.getMenuManager();

		/* submenu */
		MenuManager subMenuViewLayout = new MenuManager(BytecodeVisualizerMessages.subMenuViewLayout_Text, null);
		final MenuManager subMenuShowColumn = new MenuManager(BytecodeVisualizerMessages.subMenuShowColumn_Text,null);
		final MenuManager subMenuFormat = new MenuManager(BytecodeVisualizerMessages.subMenuFormat_Text,null);

		imb.add(subMenuViewLayout);
		imb.add(subMenuShowColumn);
		imb.add(subMenuFormat);
		imb.add(new Separator());
		imb.add(showAnalyseReportAction);

		subMenuViewLayout.add(showTreeViewAction);
		subMenuViewLayout.add(showBasicBlockViewAction);
		subMenuViewLayout.add(showInstructioneListViewAction);

		showOSBeforeColumnAction = new Action() {
			public void run() {
				if(column3.getWidth() == 0){
					column3.setWidth(100);
					column3.setResizable(true);
					setChecked(true);
				}
				else{
					column3.setWidth(0);
					column3.setResizable(false);
					setChecked(false);
				}
				subMenuShowColumn.update(true);
			}
		};
		showOSBeforeColumnAction.setText(BytecodeVisualizerMessages.OpstackBeforeColumnName);


		showOSAfterColumnAction = new Action() {
			public void run() {
				if(column4.getWidth() == 0){
					column4.setWidth(100);
					column4.setResizable(true);
					setChecked(true);
				}
				else{
					column4.setWidth(0);
					column4.setResizable(false);
					setChecked(false);
				}
				subMenuShowColumn.update(true);
			}
		};
		showOSAfterColumnAction.setText(BytecodeVisualizerMessages.OpstackAfterColumnName);

		showOSDepthColumnAction = new Action() {
			public void run() {
				if(column5.getWidth() == 0){
					column5.setWidth(40);
					column5.setResizable(true);
					setChecked(true);
				}
				else{
					column5.setWidth(0);
					column5.setResizable(false);
					setChecked(false);
				}
				subMenuShowColumn.update(true);
			}
		};
		showOSDepthColumnAction.setText(BytecodeVisualizerMessages.OpstackDepthColumnName);	

		showDescriptionColumnAction = new Action() {
			public void run() {
				if(column6.getWidth() == 0){
					column6.setWidth(100);
					column6.setResizable(true);
					setChecked(true);
				}
				else{
					column6.setWidth(0);
					column6.setResizable(false);
					setChecked(false);
				}
				subMenuShowColumn.update(true);
			}
		};
		showDescriptionColumnAction.setText(BytecodeVisualizerMessages.OpstackDescriptionColumnName);

		showOSBeforeColumnAction.setChecked(true);
		showOSAfterColumnAction.setChecked(true);
		showOSDepthColumnAction.setChecked(true);

		subMenuShowColumn.add(showOSBeforeColumnAction);
		subMenuShowColumn.add(showOSAfterColumnAction);
		subMenuShowColumn.add(showOSDepthColumnAction);
		subMenuShowColumn.add(showDescriptionColumnAction);

		displaySimpleAction = new Action() {
			public void run() {
				opstackRepresenationFormat = OpstackRepresenation.SIMPLE;
				activateDisplayFormat(OperandStackDisplayFormat_ID.DISPLAY_SIMPLE);
				subMenuFormat.update(true);
			}

			public String getText(){
				return BytecodeVisualizerMessages.DisplayFormatSIMPLE;
			}
		};
		displaySimpleAction.setChecked(true);

		displayAllAction = new Action() {
			public void run() {
				opstackRepresenationFormat = OpstackRepresenation.ALL;
				activateDisplayFormat(OperandStackDisplayFormat_ID.DISPLAY_ALL);
				subMenuFormat.update(true);
			}

			public String getText(){
				return BytecodeVisualizerMessages.DisplayFormatALL;
			}
		};
		displayAllAction.setChecked(false);

		displayValuesAction = new Action() {
			public void run() {
				opstackRepresenationFormat = OpstackRepresenation.VALUES;
				activateDisplayFormat(OperandStackDisplayFormat_ID.DISPLAY_VALUES);
				subMenuFormat.update(true);
			}

			public String getText(){
				return BytecodeVisualizerMessages.DisplayFormatVALUES;
			}
		};
		displayValuesAction.setChecked(false);
		
		displayTypesAction = new Action() {
			public void run() {
				opstackRepresenationFormat = OpstackRepresenation.TYPES;
				activateDisplayFormat(OperandStackDisplayFormat_ID.DISPLAY_TYPES);
				subMenuFormat.update(true);
			}

			public String getText(){
				return BytecodeVisualizerMessages.DisplayFormatTYPES;
			}
		};
		displayTypesAction.setChecked(false);

		subMenuFormat.add(displaySimpleAction);
		subMenuFormat.add(displayTypesAction);
		subMenuFormat.add(displayValuesAction);
		subMenuFormat.add(displayAllAction);

	}

	/**
	 * Enables or disables the actions.
	 * @param b true or false
	 */
	private void enableActions(boolean b){
		showTreeViewAction.setEnabled(b);
		showBasicBlockViewAction.setEnabled(b);
		showInstructioneListViewAction.setEnabled(b);
	}

	/**
	 * Activates selected view
	 * @param id - id of the view
	 */
	private void activateView(OperandStackView_ID id) {

		if (id == OperandStackView_ID.TREE_VIEW) {
			showTreeViewAction.setChecked(true);
			showBasicBlockViewAction.setChecked(false);
			showInstructioneListViewAction.setChecked(false);
		} 
		else if (id == OperandStackView_ID.BASICBKLOCK_VIEW) {
			showTreeViewAction.setChecked(false);
			showBasicBlockViewAction.setChecked(true);
			showInstructioneListViewAction.setChecked(false);
		}
		else if( id == OperandStackView_ID.INSTR_LIST_VIEW){
			showTreeViewAction.setChecked(false);
			showBasicBlockViewAction.setChecked(false);
			showInstructioneListViewAction.setChecked(true);
		}

		/* update input */
		if(methodInput != null){
			setView_ID(id);
			setInput(methodInput.getInstructionLines());
		}
	}

	/**
	 * Activates selected Display Format
	 * @param id - id of the format
	 */
	private void activateDisplayFormat(OperandStackDisplayFormat_ID id){

		if(id == OperandStackDisplayFormat_ID.DISPLAY_ALL){
			displayAllAction.setChecked(true);
			displaySimpleAction.setChecked(false);
			displayValuesAction.setChecked(false);
			displayTypesAction.setChecked(false);
		}
		else if(id == OperandStackDisplayFormat_ID.DISPLAY_SIMPLE){
			displayAllAction.setChecked(false);
			displaySimpleAction.setChecked(true);
			displayValuesAction.setChecked(false);
			displayTypesAction.setChecked(false);
		}
		else if(id == OperandStackDisplayFormat_ID.DISPLAY_VALUES){
			displayAllAction.setChecked(false);
			displaySimpleAction.setChecked(false);
			displayValuesAction.setChecked(true);
			displayTypesAction.setChecked(false);
		}
		else if(id == OperandStackDisplayFormat_ID.DISPLAY_TYPES){
			displayAllAction.setChecked(false);
			displaySimpleAction.setChecked(false);
			displayValuesAction.setChecked(false);
			displayTypesAction.setChecked(true);
		}

		/* update input */
		if(methodInput != null){
			setDisplayFormat_ID(id);
			setInput(methodInput.getInstructionLines());
		}	
	}

	/**
	 * Sets the input - the list of the byte code instructions
	 * for the table Viewer. 
	 * @param methodSection
	 */
	public void setInput(IMethodSection m) {
		if(m == null && methodInput == null){
			return;
		}

		if(m.isAbstract()){
			return;
		}
		
		if(m!= null && m.equals(methodInput)){
			return;
		}

		methodInput = m;

		if(methodInput == null){
			enableActions(false);
			getTreeView().setInput(null);
		}
		else{
			setInput(methodInput.getInstructionLines());
			enableActions(true);
		}

		setMesssageInStatusLine();
	}

	/**
	 * Set the reference to the active byte code editor.
	 * @param editor byte code editor
	 */
	public void setEditor(BytecodeEditor editor) {
		this.editor = editor;

		/* Synchronize tree selection with lines in the editor */		
		classFileEditorSelectionListener = new IClassFileEditorSelectionListener(){

			/* (non-Javadoc)
			 * @see com.drgarbage.bytecodevisualizer.editors.IClassFileEditorSelectionListener#lineSelectionChanged(int, java.lang.Object)
			 */
			public void lineSelectionChanged(int newLine, Object o) {
				if(isTreeViewerSelectionMutex()){
					setTreeViewerSelectionMutex(false);
					return;
				}

				if(treeMap == null){
					return;
				}

				Node node  = treeMap.get(newLine);
				if(node != null){
					treeViewer.expandToLevel(node, 1);
					Widget w = treeViewer.testFindItem(node);
					if(w != null){
						TreeItem t = (TreeItem)w;
						treeViewer.getTree().select(t);
						treeViewer.refresh(true);
					}
				}
			}
		};
		editor.addtLineSelectionListener(classFileEditorSelectionListener);

	}

	/* (non-Javadoc)
	 * Method declared on Page
	 */
	public void init(IPageSite pageSite) {
		super.init(pageSite);
		configureToolBar();
	}

	/*
	 * @see IPage#createControl
	 */
	public void createControl(Composite parent) {		
		createTreeViewer(parent);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	public void dispose() {
		super.dispose();
		treeViewer = null;
		editor.removeLineSelectionListener(classFileEditorSelectionListener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	public Control getControl() {
		if(treeViewer == null){
			return null;
		}
		return treeViewer.getControl();	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#setFocus()
	 */
	@Override
	public void setFocus() {
		treeViewer.getTree().setFocus();
	}

	/**
	 * Creates the tree viewer of the operand stack view.
	 * @param parent composite
	 */
	void createTreeViewer(Composite parent){
		treeViewer = new TreeViewer(parent, SWT.BORDER);

		Tree tree = treeViewer.getTree();
		column = new TreeColumn(tree, SWT.LEFT, 0);
		column.setMoveable(true);
		column.setText(BytecodeVisualizerMessages.OpstackBytecodeInstrColumnName); 
		column.setWidth(200);

		column = new TreeColumn(tree, SWT.LEFT, 1);
		column.setMoveable(true);
		column.setText(BytecodeVisualizerMessages.OpstackOffsetColumnName);
		column.setWidth(40);

		column3 = new TreeColumn(tree, SWT.RIGHT);
		column3.setAlignment(SWT.LEFT);
		column3.setText(BytecodeVisualizerMessages.OpstackBeforeColumnName);
		column3.setWidth(100);

		column4 = new TreeColumn(tree, SWT.RIGHT);
		column4.setAlignment(SWT.LEFT);
		column4.setText(BytecodeVisualizerMessages.OpstackAfterColumnName);
		column4.setWidth(100);

		column5 = new TreeColumn(tree, SWT.RIGHT);
		column5.setAlignment(SWT.LEFT);
		column5.setText(BytecodeVisualizerMessages.OpstackDepthColumnName);
		column5.setWidth(100);

		column6 = new TreeColumn(tree, SWT.RIGHT);
		column6.setAlignment(SWT.LEFT);
		column6.setText(BytecodeVisualizerMessages.OpstackDescriptionColumnName);
		column6.setWidth(0); /* Description Column is hidden as default */

		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		int order[] = {ColumnIndex.OPCODEMNEMONIC.getIndex(), 
				ColumnIndex.OFFSET.getIndex(),
				ColumnIndex.OPSTACKBEFORE.getIndex(), 
				ColumnIndex.OPSTACKAFTER.getIndex(), 
				ColumnIndex.OPSTACKDEPTH.getIndex(),
				ColumnIndex.DESCRIPTION.getIndex()};
		tree.setColumnOrder(order);

		treeViewer.setContentProvider(new TreeViewContentProvider());
		treeViewer.setLabelProvider(new TreeTableLabelProvider());

		treeViewer.expandAll();

		/* selection listener for line synchronization */
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			public void selectionChanged(SelectionChangedEvent arg0) {
				ISelection sel =  arg0.getSelection();
				if(!sel.isEmpty()){
					/* set a mutex to avoid the call back from the editor */
					setTreeViewerSelectionMutex(true);

					TreeSelection treeSel = (TreeSelection) sel;
					Node n = (Node)treeSel.getFirstElement();
					Object o = n.getObject();

					if(o instanceof IInstructionLine){
						IInstructionLine i = (IInstructionLine)o;
						editor.selectLineAndRevaluate2(i.getLine());
					}

					if(o instanceof String){ /* use parent of the true, false or switch value nodes */
						IInstructionLine i = (IInstructionLine)n.getParent().getObject();
						if(i != null){
							editor.selectLineAndRevaluate2(i.getLine());
						}
					}
				}
			}
		});

	}

	/**
	 * Content provider for the operand stack view.
	 */
	class TreeViewContentProvider implements ITreeContentProvider{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement){
			if (parentElement instanceof Node){
				return ((Node)parentElement).getChildren().toArray();
			}

			return new Object[0];
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element){
			if (element instanceof Node){
				return ((Node)element).getParent();
			}

			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element){
			if (element instanceof Node){
				return ((Node)element).hasChildren();
			}

			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object nodes){
			return getChildren(nodes);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose(){
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
		}
	}

	/**
	 * Label provider for the operand stack view
	 */
	class TreeTableLabelProvider implements ITableLabelProvider{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex){
			if (columnIndex == 0) {
				if(element instanceof Node){
					Object o = ((Node)element).getObject();
					if(o != null && o instanceof IInstructionLine){
						IInstructionLine i = (IInstructionLine)o;

						switch(ControlFlowGraphUtils.getInstructionNodeType(i.getInstruction().getOpcode())){
						case INodeType.NODE_TYPE_SIMPLE:
							return CoreImg.roundedrect_instr_16x16.createImage();
						case INodeType.NODE_TYPE_IF:
							return CoreImg.decision_instr_16x16.createImage();
						case INodeType.NODE_TYPE_RETURN:
							return CoreImg.return_instr_16x16.createImage();
						case INodeType.NODE_TYPE_GOTO_JUMP:
							return CoreImg.goto_instr_16x16.createImage();
						case INodeType.NODE_TYPE_SWITCH:
							return CoreImg.switch_instr_16x16.createImage();
						case INodeType.NODE_TYPE_INVOKE:
							return CoreImg.invoke_instr_16x16.createImage();
						case INodeType.NODE_TYPE_GET:
							return CoreImg.get_instr_16x16.createImage();
						default:
							return CoreImg.roundedrect_instr_16x16.createImage();

						}
					}
					if(o instanceof String){
						return JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PROTECTED);
					}
				}

				return JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_LOCAL_VARIABLE);
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex){

			if(element instanceof Node){
				Node node = (Node)element;
				Object o = node.getObject();
				if(o != null && o instanceof IInstructionLine){
					IInstructionLine i = (IInstructionLine)o;

					if (columnIndex == ColumnIndex.OFFSET.getIndex()) {
						return i.getInstruction().getOpcodeMnemonic();
					}
					else if (columnIndex == ColumnIndex.OPCODEMNEMONIC.getIndex()) {						
						return String.valueOf(i.getInstruction().getOffset());
					}
					else if (columnIndex == ColumnIndex.OPSTACKBEFORE.getIndex()) { /* operand stack before */
						return node.getOperandStackBefore();
					}
					else if (columnIndex == ColumnIndex.OPSTACKAFTER.getIndex()) { /* operand stack after */
						return node.getOperandStackAfter();
					}
					else if (columnIndex == ColumnIndex.OPSTACKDEPTH.getIndex()) { /* stack depth */
						if(node.getDepth() != null){
							int stackSize = OperandStack.UNKNOWN_SIZE;

							if(node.getDepth().length == 1){
								stackSize = node.getDepth()[0];
							}

							List<Integer> listOfStacksSizes = new ArrayList<Integer>();
							if(node.getDepth().length > 1){
								for(int s: node.getDepth()){
									if(stackSize != s){
										if(s > stackSize){
											stackSize = s;
										}
										listOfStacksSizes.add(s);
									}
								}
							}

							/* execute some trivial size based checks */
							if(stackSize > methodInput.getMaxStack() 
									|| stackSize ==  OperandStack.UNKNOWN_SIZE
									|| listOfStacksSizes.size() > 1
									){

								Widget w = treeViewer.testFindItem(node);
								if(w != null){
									TreeItem t = (TreeItem)w;
									t.setForeground(RED);

								}
							}

							if(stackSize > methodInput.getMaxStack() ){
								StringBuffer buf = new StringBuffer();
								buf.append(CoreMessages.Error);
								buf.append(JavaLexicalConstants.COLON);
								buf.append(JavaLexicalConstants.SPACE);
								buf.append(BytecodeVisualizerMessages.OperandStackAnalysis_Error_StackOverflow);
								buf.append(JavaLexicalConstants.COMMA);
								buf.append(JavaLexicalConstants.SPACE);
								String msg = MessageFormat.format(
										BytecodeVisualizerMessages.OperandStackAnalysis_CurrentStackSize_Info, 
										new Object[]{
												String.valueOf(stackSize)
										});
								buf.append(msg);
								buf.append(JavaLexicalConstants.DOT);
								return buf.toString();
							}

							if(listOfStacksSizes.size() > 1){
								StringBuffer buf = new StringBuffer();
								buf.append(CoreMessages.Error);
								buf.append(JavaLexicalConstants.COLON);
								buf.append(JavaLexicalConstants.SPACE);
								buf.append(BytecodeVisualizerMessages.OperandStackAnalysis_Error_Different_StackSizes);
								buf.append(JavaLexicalConstants.SPACE);

								Iterator<Integer> it = listOfStacksSizes.iterator();
								buf.append(it.next());
								while(it.hasNext()){
									buf.append(JavaLexicalConstants.PIPE);
									buf.append(it.next());	
								}

								buf.append(JavaLexicalConstants.SPACE);
								buf.append(JavaLexicalConstants.DOT);

								return buf.toString();
							}

							/* check if the stack is empty in the return nodes */
							if(ControlFlowGraphUtils.
									getInstructionNodeType(i.getInstruction().getOpcode()) 
									== INodeType.NODE_TYPE_RETURN){
								if(stackSize != 0){
									/*
									 * check if the if the object on stack represents 
									 * a reference to the Throwable leaving on the 
									 * stack after the rest of the stack is cleared 
									 * by the athrow byte code instruction.
									 */
									if(!(stackSize == 1 && 
											i.getInstruction().getOpcode() == Opcodes.ATHROW))
									{									


										Widget w = treeViewer.testFindItem(node);
										if(w != null){
											TreeItem t = (TreeItem)w;
											t.setForeground(ORANGE);
										}

										StringBuffer buf = new StringBuffer(); 
										buf.append(CoreMessages.Warning);
										buf.append(JavaLexicalConstants.COLON);
										buf.append(JavaLexicalConstants.SPACE);
										buf.append(BytecodeVisualizerMessages.OperandStackAnalysis_Warning_StackNonEmpty);
										buf.append(JavaLexicalConstants.COMMA);
										buf.append(JavaLexicalConstants.SPACE);
										String msg = MessageFormat.format(
												BytecodeVisualizerMessages.OperandStackAnalysis_CurrentStackSize_Info, 
												new Object[]{
														String.valueOf(stackSize)
												});
										buf.append(msg);
										buf.append(JavaLexicalConstants.DOT);

										return buf.toString();
									}
								}
							}

							return String.valueOf(stackSize);
						}
					}
					else if (columnIndex == ColumnIndex.DESCRIPTION.getIndex()) { /* opcode description   */
						return ByteCodeConstants.OPCODE_OPERANDSTACK_DESCR[i.getInstruction().getOpcode()];
					}

				}
				else{
					if (columnIndex == ColumnIndex.OFFSET.getIndex()) {
						return o.toString();
					}
					else{
						return "";
					}
				}
			}

			return BytecodeVisualizerMessages.OperandStackView_Unknown;

		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void addListener(ILabelProviderListener listener){
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose(){
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
		 */
		public boolean isLabelProperty(Object element, String property){
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener listener){
		}
	}		

	/**
	 * Sets input of the tree viewer.
	 * @param instructions list of byte code instructions
	 * @param id kind of the view
	 */
	private void setInput(List<IInstructionLine> instructions){
		Object input = generateInput(instructions, view_ID);

		if(input == null){
			return;
		}

		/* fill tree map for synchronization */
		treeMap = new TreeMap<Integer, Node>();
		fillTreeMap((Node)input);

		BytecodeDocumentProvider byteCodeDocumentProvider = (BytecodeDocumentProvider) editor.getDocumentProvider();
		if(byteCodeDocumentProvider!= null){
			IClassFileDocument ic = byteCodeDocumentProvider.getClassFileDocument();


			/* when the methodInput changes, a new stack is generated */
			/* later, we can add the reference to the previous stack to the new stack */
			operandStack = new OperandStack(instructions, 
					ic.getConstantPool(), 
					methodInput.getLocalVariableTable(),
					methodInput.getExceptionTable());
			INodeListExt nodeList = operandStack.getOperandStackGraph().getNodeList();
			for(int i = 0; i < nodeList.size(); i++){
				INodeExt n = nodeList.getNodeExt(i);
				Object o = n.getData();
				if(o instanceof Map){
					@SuppressWarnings("unchecked")
					Map<OperandStackPropertyConstants, Object> nodeMap = (Map<OperandStackPropertyConstants, Object>) o;
					o = nodeMap.get(OperandStackPropertyConstants.NODE_INSTR_OBJECT);
					
					IInstructionLine iLine;
					if(o != null){
						iLine = (IInstructionLine) o;
						Node node = treeMap.get(iLine.getLine());

						o = nodeMap.get(OperandStackPropertyConstants.NODE_STACK);
						if(o != null){
							NodeStackProperty nsp = (NodeStackProperty)o;
							if(opstackRepresenationFormat == OpstackRepresenation.SIMPLE){
								node.setOperandStackBefore(OperandStack.stackToString(operandStack.getStackBefore(n).get(0)));
								node.setOperandStackAfter(OperandStack.stackToString(nsp.getStackAfter().get(0)));
							}
							else if(opstackRepresenationFormat == OpstackRepresenation.ALL){
								node.setOperandStackBefore(OperandStack.stackListToString(operandStack.getStackBefore(n)));
								node.setOperandStackAfter(OperandStack.stackListToString(nsp.getStackAfter()));							
							}
							else if(opstackRepresenationFormat == OpstackRepresenation.TYPES){
								node.setOperandStackBefore(OperandStack.stackToString(operandStack.getStackBefore(n).get(0), OpstackRepresenation.TYPES));
								node.setOperandStackAfter(OperandStack.stackToString(nsp.getStackAfter().get(0), OpstackRepresenation.TYPES));
							}
							else if(opstackRepresenationFormat == OpstackRepresenation.VALUES){
								node.setOperandStackBefore(OperandStack.stackToString(operandStack.getStackBefore(n).get(0), OpstackRepresenation.VALUES));
								node.setOperandStackAfter(OperandStack.stackToString(nsp.getStackAfter().get(0), OpstackRepresenation.VALUES));
							}
							else{
								node.setOperandStackBefore(OperandStack.stackToString(operandStack.getStackBefore(n).get(0)));
								node.setOperandStackAfter(OperandStack.stackToString(nsp.getStackAfter().get(0)));
							}
							
							node.setDepth(nsp.getStackSize());
						}
						else{
							node.setOperandStackBefore(BytecodeVisualizerMessages.OperandStackView_Unknown);
							node.setOperandStackAfter(BytecodeVisualizerMessages.OperandStackView_Unknown);
							node.setDepth(new int[] { OperandStack.UNKNOWN_SIZE });
						}
					}
				}
			}
		}

		treeViewer.setInput(input);
		treeViewer.expandAll();

		/* set current selection */
		int newLine = editor.getSelectedLine();
		Node node  = treeMap.get(newLine);
		if(node != null){
			Widget w = treeViewer.testFindItem(node);
			if(w != null){
				TreeItem t = (TreeItem)w;
				treeViewer.getTree().select(t);
				treeViewer.refresh(true);
			}
		}
	}

	private void fillTreeMap(Node root){
		for(Node n: root.getChildren()){
			fillTreeMap(n);
			Object nodeObj = n.getObject();
			if(nodeObj instanceof IInstructionLine){
				IInstructionLine i = (IInstructionLine) nodeObj;				
				treeMap.put(i.getLine(), n);
			}
		}
	}

	/**
	 * Creates the tree structure for the operand stack view.
	 * @param instructions list of instructions
	 * @param id kind of the view
	 * @return the tree structure
	 */
	protected abstract  Object  generateInput(List<IInstructionLine> instructions, OperandStackView_ID id);

	/**
	 * Element of the operand stack view structure.
	 */
	class Node {		  
		Node parent = null;
		List<Node> children = new ArrayList<Node>();
		Object obj;
		String operandStackBefore, operandStackAfter;
		int depth[];

		public Object getObject() {
			return obj;
		}

		public void setObject(Object obj) {
			this.obj = obj;
		}

		public Node getParent() {
			return parent;
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}

		public boolean hasParent() {
			return parent != null ;
		}

		public List<Node> getChildren() {
			return children;
		}

		public int[] getDepth() {
			return depth;
		}

		public void setChildren(List<Node> children) {
			this.children = children;
		}

		public void addhild(Node child) {
			children.add(child);
		}

		public boolean hasChildren() {
			return children.size() > 0 ;
		}

		public String getOperandStackBefore() {
			return operandStackBefore;
		}

		public String getOperandStackAfter() {
			return operandStackAfter;
		}

		public void setOperandStackBefore(String operandStack) {
			this.operandStackBefore = operandStack;
		}

		public void setOperandStackAfter(String operandStack) {
			this.operandStackAfter = operandStack;
		}

		public void setDepth(int depth[]) {
			this.depth = depth;
		}
	}
}


