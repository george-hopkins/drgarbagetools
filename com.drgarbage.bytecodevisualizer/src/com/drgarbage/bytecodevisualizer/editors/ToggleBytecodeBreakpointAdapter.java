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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.debug.ui.DebugWorkingCopyManager;
import org.eclipse.jdt.internal.debug.ui.actions.ToggleBreakpointAdapter;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPart;

import com.drgarbage.asm.render.intf.IClassFileDocument;
import com.drgarbage.asm.render.intf.IFieldSection;
import com.drgarbage.asm.render.intf.IMethodSection;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.utils.ClassFileDocumentsUtils;

/**
 * Toggles a breakpoint in a bytecode editor.
 * 
 * @author Sergej Alekseev
 * @version $Revision$ $Id: ToggleBytecodeBreakpointAdapter.java 1310
 *          2009-08-07 22:26:42Z Peter Palaga $
 */
@SuppressWarnings("restriction")
public class ToggleBytecodeBreakpointAdapter extends ToggleBreakpointAdapter {
	
	private boolean bytecodeDebugSupported = false;
	

	public ToggleBytecodeBreakpointAdapter(boolean bytecodeDebugSupported) {
		super();
		this.bytecodeDebugSupported = bytecodeDebugSupported;
	}

	@Override
	public boolean canToggleBreakpoints(IWorkbenchPart part, ISelection selection) {
		if (bytecodeDebugSupported) {
			return super.canToggleBreakpoints(part, selection);
		}
		else {
			return false;
		}
	}

	@Override
	public boolean canToggleLineBreakpoints(IWorkbenchPart part,
			ISelection selection) {
		if (bytecodeDebugSupported) {
			return super.canToggleLineBreakpoints(part, selection);
		}
		else {
			return false;
		}
	}

	@Override
	public boolean canToggleMethodBreakpoints(IWorkbenchPart part,
			ISelection selection) {
		if (bytecodeDebugSupported) {
			return super.canToggleMethodBreakpoints(part, selection);
		}
		else {
			return false;
		}
	}

	@Override
	public boolean canToggleWatchpoints(IWorkbenchPart part,
			ISelection selection) {
		if (bytecodeDebugSupported) {
			return super.canToggleWatchpoints(part, selection);
		}
		else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.internal.debug.ui.actions.ToggleBreakpointAdapter#
	 * toggleBreakpoints(org.eclipse.ui.IWorkbenchPart,
	 * org.eclipse.jface.viewers.ISelection)
	 */
	public void toggleBreakpoints(IWorkbenchPart part, ISelection selection)
			throws CoreException {

		ISelection sel = translateToMembers(part, selection);
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) sel;
			if (!structuredSelection.isEmpty()) {
				IMember member = (IMember) structuredSelection.getFirstElement();
				int mtype = member.getElementType();
				if (mtype == IJavaElement.FIELD) {
					toggleWatchpoints(part, sel);
				} else if (mtype == IJavaElement.METHOD) {
					toggleMethodBreakpoints(part, sel);
				} else if (member.getElementType() == IJavaElement.TYPE) {
					toggleClassBreakpoints(part, sel);
				} else {
					BytecodeVisualizerPlugin.log(new IllegalStateException("Uncovered element type "+ member.getElementType()));
//					/*
//					 * fall back to old behavior, always create a line
//					 * breakpoint
//					 */
//					/* should never occur */
//					toggleLineBreakpoints(part, selection, true);
				}
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.internal.debug.ui.actions.ToggleBreakpointAdapter#
	 * translateToMembers(org.eclipse.ui.IWorkbenchPart,
	 * org.eclipse.jface.viewers.ISelection)
	 */
	protected ISelection translateToMembers(IWorkbenchPart part,
			ISelection selection) throws CoreException {

		if (!(part instanceof BytecodeEditor)) {
			BytecodeVisualizerPlugin.log(new IllegalStateException(
					"part not an instance of BytecodeEditor"));
			return StructuredSelection.EMPTY;
		} else {

			BytecodeEditor bytecodeEditor = (BytecodeEditor) part;

			if (!(selection instanceof ITextSelection)) {
				BytecodeVisualizerPlugin.log(new IllegalStateException(
						"selection not an instance of ITextSelection"));
				return StructuredSelection.EMPTY;
			} else {
				ITextSelection textSelection = (ITextSelection) selection;

				IType primaryType = getPrimaryType(bytecodeEditor.getBytecodeEditorInput());
				BytecodeDocumentProvider documentProvider = (BytecodeDocumentProvider) bytecodeEditor
						.getDocumentProvider();

				if (primaryType == null) {
					primaryType = getPrimaryType(bytecodeEditor);
				}

				if (primaryType == null) {
					primaryType = getPrimaryType(documentProvider);
				}

				if (primaryType != null) {

					IClassFileDocument doc = documentProvider
							.getClassFileDocument();

					int line = textSelection.getStartLine();
					if (line == doc.getClassSignatureDocumentLine()) {
						return new StructuredSelection(primaryType);
					}

					if (doc.isLineInMethod(line)) {
						IMethodSection ms = doc.findMethodSection(line);
						if (ms != null) {
							IMethod m = ClassFileDocumentsUtils.findMethod(
									primaryType, ms.getName(), ms
											.getDescriptor());
							if (m != null) {
								return new StructuredSelection(m);
							}
							else {
								/* probably an implicit constructor */
								primaryType = getPrimaryType(documentProvider);
								if (primaryType != null) {
									m = ClassFileDocumentsUtils.findMethod(
											primaryType, ms.getName(), ms
													.getDescriptor());
									if (m != null) {
										return new StructuredSelection(m);
									}
								}
							}
						}
						return StructuredSelection.EMPTY;
					}

					if (doc.isLineInField(line)) {
						IFieldSection fs = doc.findFieldSection(line);
						if (fs != null) {
							IField f = primaryType.getField(fs.getName());
							if (f != null) {
								return new StructuredSelection(f);
							}
						}
						return StructuredSelection.EMPTY;
					}

				}
				return StructuredSelection.EMPTY;
			}

		}

	}

	private static IType getPrimaryType(BytecodeEditor bytecodeEditor) {
		return bytecodeEditor.getSourceCodeViewer().getPrimaryType();
	}

	private static IType getPrimaryType(
			BytecodeDocumentProvider documentProvider) {
		IJavaElement javaElement = documentProvider
				.getClassFileOutlineElement().getPrimaryElement();
		if (!(javaElement instanceof IType)) {
			BytecodeVisualizerPlugin.log(new IllegalStateException(
					"Could not get primary type out of ClassFileDocument"));
			return null;
		} else {
			return (IType) javaElement;
		}
	}

	private static IType getPrimaryType(IEditorInput input) {

		IClassFile classFile = (IClassFile) input.getAdapter(IClassFile.class);
		if (classFile != null) {
			IType result = classFile.findPrimaryType();
			if (result != null) {
				return result;
			}
		}

		ICompilationUnit cu = DebugWorkingCopyManager.getWorkingCopy(input,
				false);
		if (cu != null) {
			IType result = cu.findPrimaryType();
			if (result != null) {
				return result;
			}
		}

		return null;
	}

}
