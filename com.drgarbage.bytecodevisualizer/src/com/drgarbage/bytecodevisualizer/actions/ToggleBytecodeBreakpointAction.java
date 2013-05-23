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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTargetExtension;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.IUpdate;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerConstants;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.bytecodevisualizer.editors.BytecodeDocumentProvider;
import com.drgarbage.bytecodevisualizer.editors.BytecodeEditor;
import com.drgarbage.bytecodevisualizer.editors.ISourceCodeViewer;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.core.img.CoreImg;

/**
 * Action to toggle a breakpoint in a vertical ruler of a workbench part
 * containing a document. The part must provide an
 * <code>IToggleBreakpointsTarget</code> adapter which may optionally be an
 * instance of an <code>IToggleBreakpointsTargetExtension</code>.
 * <p>
 * 
 * @see org.eclipse.debug.ui.actions.RulerToggleBreakpointActionDelegate
 * @noextend This class is not intended to be subclassed by clients.
 * @author Sergej Alekseev
 * @version $Revision$ 
 * $Id$
 */
public class ToggleBytecodeBreakpointAction extends Action implements IUpdate {

	public static enum BytecodeDebugSupport {
		AVAILABLE, BAD_DOCUMENT_LOCATION, BAD_EDITOR_INPUT
	};

	private static class SelectionData {
		private BytecodeDebugSupport bytecodeDebugSupport = BytecodeDebugSupport.BAD_DOCUMENT_LOCATION;
		private ITextSelection selection;

		public SelectionData(BytecodeDebugSupport bytecodeDebugSupport,
				ITextSelection selection) {
			super();
			this.bytecodeDebugSupport = bytecodeDebugSupport;
			this.selection = selection;
		}

	}

	private IDocument fDocument;
	private IWorkbenchPart fPart;
	private IVerticalRulerInfo fRulerInfo;

	/**
	 * Constructs a new action to toggle a breakpoint in the given part
	 * containing the given document and ruler.
	 * 
	 * @param part
	 *            the part in which to toggle the breakpoint - provides an
	 *            <code>IToggleBreakpointsTarget</code> adapter
	 * @param document
	 *            the document breakpoints are being set in or <code>null</code>
	 *            when the document should be derived from the given part
	 * @param rulerInfo
	 *            specifies location the user has double-clicked
	 */
	public ToggleBytecodeBreakpointAction(IWorkbenchPart part,
			IDocument document, IVerticalRulerInfo rulerInfo) {
		super(BytecodeVisualizerMessages.ToggleBreakpointAction_text);
		fPart = part;
		fDocument = document;
		fRulerInfo = rulerInfo;
		setImageDescriptor(BytecodeVisualizerPlugin.getDefault()
				.getImageRegistry().getDescriptor(
						BytecodeVisualizerConstants.IMG16E_TOGGLE_BREAKPOINT));

		setImageDescriptor(CoreImg.breakpoint_action_icon);
	}

	/**
	 * Disposes this action. Clients must call this method when this action is
	 * no longer needed.
	 */
	public void dispose() {
		fDocument = null;
		fPart = null;
		fRulerInfo = null;
	}

	/**
	 * Returns the document on which this action operates.
	 * 
	 * @return the document or <code>null</code> if none
	 */
	private IDocument getDocument() {
		if (fDocument != null) {
			return fDocument;
		}

		if (!(fPart instanceof BytecodeEditor)) {
			BytecodeVisualizerPlugin.log(new IllegalStateException(
					"fPart not na instanceof BytecodeEditor"));
			return null;
		} else {
			BytecodeEditor bytecodeEditor = (BytecodeEditor) fPart;
			int page = bytecodeEditor.getActiveTabIndex();
			if (page != BytecodeEditor.TAB_INDEX_BYTECODE) {
				BytecodeVisualizerPlugin
						.log(new IllegalStateException(
								"BytecodeEditor must have BytecodeEditor.TAB_INDEX_BYTECODE selected."));
			}

			BytecodeDocumentProvider provider = (BytecodeDocumentProvider) bytecodeEditor
					.getDocumentProvider();
			return provider.getBytecodeDocument(bytecodeEditor
					.getBytecodeEditorInput());
		}

	}

	private SelectionData getTextSelection() {
		if (!(fPart instanceof BytecodeEditor)) {
			BytecodeVisualizerPlugin.log(new IllegalStateException(
					"fPart not na instanceof BytecodeEditor"));
			return new SelectionData(
					BytecodeDebugSupport.BAD_DOCUMENT_LOCATION, null);
		} else {
			BytecodeEditor bytecodeEditor = (BytecodeEditor) fPart;
			if (!bytecodeEditor.isBytecodeDebugSupported()) {
				return new SelectionData(BytecodeDebugSupport.BAD_EDITOR_INPUT,
						null);
			} else {
				int page = bytecodeEditor.getActiveTabIndex();
				if (page != BytecodeEditor.TAB_INDEX_BYTECODE) {
					BytecodeVisualizerPlugin
							.log(new IllegalStateException(
									"BytecodeEditor must have BytecodeEditor.TAB_INDEX_BYTECODE selected."));
				}

				int line = fRulerInfo.getLineOfLastMouseButtonActivity();
				if (line >= 0) {
					/* valid line */

					IDocument document = getDocument();
					if (document == null) {
						BytecodeVisualizerPlugin.log(new IllegalStateException(
								"Could not get Document."));
						return null;
					}

					ITextSelection textSelection = null;
					try {
						IRegion region = document.getLineInformation(line);
						textSelection = new TextSelection(document, region
								.getOffset(), 0);
					} catch (BadLocationException e) {
						BytecodeVisualizerPlugin.log(e);
					}

					if (textSelection == null) {
						BytecodeVisualizerPlugin.log(new IllegalStateException(
								"Could not get TextSelection out of a document for line "
										+ line + "."));
						return new SelectionData(
								BytecodeDebugSupport.BAD_DOCUMENT_LOCATION,
								null);
					}
					return new SelectionData(BytecodeDebugSupport.AVAILABLE,
							textSelection);
				}
				return new SelectionData(
						BytecodeDebugSupport.BAD_DOCUMENT_LOCATION, null);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {

		if (fPart instanceof BytecodeEditor) {
			BytecodeEditor bytecodeEditor = (BytecodeEditor) fPart;

			if(bytecodeEditor.getActiveTabIndex() == BytecodeEditor.TAB_INDEX_SOURCE){
				ISourceCodeViewer sourceCodeViewer = bytecodeEditor.getSourceCodeViewer();
				
				int line = sourceCodeViewer.getVerticalRulerOfSourceViewer().getLineOfLastMouseButtonActivity();//fRulerInfo.getLineOfLastMouseButtonActivity();
				if (line >= 0) { /* valid line */

					IDocument document = sourceCodeViewer.getDocumentProvider().getDocument(sourceCodeViewer.getEditorInput());
					if (document == null) {
						BytecodeVisualizerPlugin.log(new IllegalStateException(
								"Could not get Document."));
						return;
					}

					ITextSelection textSelection = null;
					try {
						IRegion region = document.getLineInformation(line);
						textSelection = new TextSelection(document, region.getOffset(), 0);
					} catch (BadLocationException e) {
						BytecodeVisualizerPlugin.log(e);
					}

					IToggleBreakpointsTarget adapter = (IToggleBreakpointsTarget) fPart
							.getAdapter(IToggleBreakpointsTarget.class);
					if (!(adapter instanceof IToggleBreakpointsTargetExtension)) {
						BytecodeVisualizerPlugin.log(new IllegalStateException(
								"Could not get IToggleBreakpointsTarget out ouf BytecodeEditor."));
						return;
					}
					IToggleBreakpointsTargetExtension adapterExtension = (IToggleBreakpointsTargetExtension) adapter;
					if (adapterExtension.canToggleBreakpoints(fPart, textSelection)) {
						try {
							adapterExtension.toggleBreakpoints(fPart, textSelection);
						} catch (CoreException e) {
							BytecodeVisualizerPlugin.log(e);
						}
					}

					return;
				}
			}
		}
		
		
		SelectionData selectionData = getTextSelection();
		switch (selectionData.bytecodeDebugSupport) {
		case AVAILABLE:
			IToggleBreakpointsTarget adapter = (IToggleBreakpointsTarget) fPart
					.getAdapter(IToggleBreakpointsTarget.class);
			if (!(adapter instanceof IToggleBreakpointsTargetExtension)) {
				BytecodeVisualizerPlugin.log(new IllegalStateException(
								"Could not get IToggleBreakpointsTarget out ouf BytecodeEditor."));
				return;
			}
			IToggleBreakpointsTargetExtension adapterExtension = (IToggleBreakpointsTargetExtension) adapter;
			if (adapterExtension.canToggleBreakpoints(fPart, selectionData.selection)) {
				try {
					adapterExtension.toggleBreakpoints(fPart, selectionData.selection);
				} catch (CoreException e) {
					BytecodeVisualizerPlugin.log(e);
				}
			}
			break;
		case BAD_DOCUMENT_LOCATION:
			break;
		case BAD_EDITOR_INPUT:
			/* alert */
			new DebugFunctionalityInfoDialog().open();
			break;
		default:
			BytecodeVisualizerPlugin.log(new IllegalStateException(
					"Uncovered option '" + selectionData.bytecodeDebugSupport + "'"));
			break;
		}
	}

	public void setBytecodedebugSupport(BytecodeDebugSupport bds) {
		switch (bds) {
		case AVAILABLE:
			setToolTipText(BytecodeVisualizerMessages.ToggleBreakpointAction_text);
			setEnabled(true);
			setImageDescriptor(BytecodeVisualizerPlugin
					.getDefault()
					.getImageRegistry()
					.getDescriptor(
							BytecodeVisualizerConstants.IMG16E_TOGGLE_BREAKPOINT));

			break;
		case BAD_DOCUMENT_LOCATION:
			setToolTipText(BytecodeVisualizerMessages.ToggleBreakpointAction_text);
			setEnabled(false);
			setImageDescriptor(BytecodeVisualizerPlugin
					.getDefault()
					.getImageRegistry()
					.getDescriptor(
							BytecodeVisualizerConstants.IMG16E_TOGGLE_BREAKPOINT));
			break;
		case BAD_EDITOR_INPUT:
			setToolTipText(BytecodeVisualizerMessages.ToggleBreakpointAction_tooltipText_Debug_functionality_is_unavailable_in_this_context_
					+ " " + CoreMessages.lbl_Click_to_learn_more_);
			setEnabled(true);
			setImageDescriptor(BytecodeVisualizerPlugin
					.getDefault()
					.getImageRegistry()
					.getDescriptor(
							BytecodeVisualizerConstants.IMG16E_TOGGLE_BREAKPOINT_RESTRICTED));
			break;
		default:
			BytecodeVisualizerPlugin.log(new IllegalStateException(
					"Uncovered option '" + bds + "'"));
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IUpdate#update()
	 */
	public void update() {
		if (fPart instanceof BytecodeEditor) {
			BytecodeEditor bytecodeEditor = (BytecodeEditor) fPart;
			ToggleBytecodeBreakpointAction target = bytecodeEditor
					.getToggleBytecodeBreakpointAction();
			if (target != this) {
				target.update();
			}

			if(bytecodeEditor.getActiveTabIndex() == BytecodeEditor.TAB_INDEX_BYTECODE){
				SelectionData selectionData = getTextSelection();
				setBytecodedebugSupport(selectionData.bytecodeDebugSupport);
			}
		}
	}

}
