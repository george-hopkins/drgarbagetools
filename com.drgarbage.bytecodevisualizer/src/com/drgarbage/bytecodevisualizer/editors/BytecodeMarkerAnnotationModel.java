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

import java.lang.reflect.Field;
import java.text.MessageFormat;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.internal.ui.DynamicInstructionPointerAnnotation;
import org.eclipse.debug.internal.ui.InstructionPointerAnnotation;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.jdt.internal.ui.javaeditor.ClassFileMarkerAnnotationModel;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.MarkerUtilities;

import com.drgarbage.asm.render.intf.IClassFileDocument;
import com.drgarbage.asm.render.intf.IFieldSection;
import com.drgarbage.asm.render.intf.IInstructionLine;
import com.drgarbage.asm.render.intf.IMethodSection;
import com.drgarbage.bytecode.ByteCodeConstants;
import com.drgarbage.bytecode.BytecodeUtils;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.core.CoreConstants;
import com.drgarbage.core.CorePlugin;
import com.drgarbage.utils.ClassFileDocumentsUtils;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.StackFrame;

/**
 * Annotation model. Used to set instruction pointer annotations during
 * debugging and to show breakpoint marker.
 * 
 * @author Sergej Alekseev
 * @version $Revision: 1523 $ $Id: BytecodeMarkerAnnotationModel.java 1265
 *          2009-07-27 19:17:51Z Peter Palaga $
 */
@SuppressWarnings("restriction")
public class BytecodeMarkerAnnotationModel extends
		ClassFileMarkerAnnotationModel {

	/**
	 * Return the index of selected frame.
	 * 
	 * @param selectedFrame
	 * @return index
	 * @throws DebugException
	 */
	private static int findFrameIndex(IStackFrame selectedFrame) {
		try {
			IThread thread = selectedFrame.getThread();
			if (thread.hasStackFrames()) {
				IStackFrame frames[] = thread.getStackFrames();
				for (int i = 0; i < frames.length; i++) {
					if (frames[i].equals(selectedFrame)) {
						return i;
					}
				}
			}
		} catch (DebugException e) {
			BytecodeVisualizerPlugin.log(e);
		}
		return ByteCodeConstants.INVALID_LINE;
	}

	/**
	 * Returns the bytecode porsition of the current frame.
	 * 
	 * @param stackFrame
	 * @param classFileDocument
	 * @param document
	 * @return bytecode position
	 * @throws DebugException
	 * @throws IncompatibleThreadStateException
	 * @throws BadLocationException
	 */
	private static Position getBytecodePosition(IStackFrame stackFrame,
			IClassFileDocument classFileDocument, IDocument document) {

		int offset = 0;
		int length = 0;
		StackFrame frame = getStackFrame(stackFrame);
		
		if (frame != null) {
			Location loc = frame.location();
			long byteOffset = loc.codeIndex();
			
			Method method = loc.method();

			IMethodSection methodSection = classFileDocument.findMethodSection(method.name(), method.signature());

			if (methodSection != null) {
				int line = IInstructionLine.INVALID_LINE;
				if (methodSection.hasCode()) {
					line = methodSection.findOffsetLine((int) byteOffset);
				}
				
				if (line == IInstructionLine.INVALID_LINE) {
					line = methodSection.getFirstLine();
				}

				try {
					IRegion region = document.getLineInformation(line);
					offset = region.getOffset();
					length = region.getLength();
				} catch (BadLocationException e) {
					BytecodeVisualizerPlugin.log(new Exception("Unable to locate line "+ line +" in a org.eclipse.jface.text.IDocument.", e));
				}

			}
		}

		Position newPos = new Position(offset, length);

		return newPos;
	}

	private static StackFrame getStackFrame(IStackFrame frame) {
		try {
			IThread thread = frame.getThread();
			if (thread instanceof JDIThread) {
				JDIThread jdiThread = (JDIThread) thread;
				int index = findFrameIndex(frame);
				if (index >= 0) {
					return jdiThread.getUnderlyingThread().frame(index);
				}
			}
		} catch (IncompatibleThreadStateException e) {
			BytecodeVisualizerPlugin.log(new Exception("Unable to access the corresponding com.sun.jdi.StackFrame.", e));
		}
		return null;
	}

	private IClassFileDocument classFileDocument;

	private BytecodeEditor fClassFileEditor;

	/**
	 * Constructor.
	 * 
	 * @param markerResource
	 * @param part
	 */
	public BytecodeMarkerAnnotationModel(IResource markerResource, BytecodeEditor part) {
		super(markerResource);
		this.fClassFileEditor = part;
	}
	/*
	 * @see AbstractMarkerAnnotationModel#isAcceptable(IMarker)
	 */
	protected boolean isAcceptable(IMarker marker) {
		boolean result = super.isAcceptable(marker);
		if (result) {
			return true;
		}
		else if (marker == null || fClassFile == null) {
			return false;
		}
		else {
			return BytecodeUtils.isSourceOf(marker.getResource(), fClassFile);
		}
	}
	
	protected boolean isAffected(IMarkerDelta markerDelta) {
		boolean result = super.isAffected(markerDelta);
		if (result) {
			return true;
		}
		else if (markerDelta == null || fClassFile == null) {
			return false;
		}
		else {
			return BytecodeUtils.isSourceOf(markerDelta.getResource(), fClassFile);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.source.AnnotationModel#addAnnotation(org.eclipse
	 * .jface.text.source.Annotation, org.eclipse.jface.text.Position)
	 */
	public void addAnnotation(Annotation annotation, Position position) {

		if (annotation instanceof DynamicInstructionPointerAnnotation
		/* the following should help when the license is invalid */
		&& fDocument != null) {

			DynamicInstructionPointerAnnotation instructionPointerAnnotation = (DynamicInstructionPointerAnnotation) annotation;
			IStackFrame frame = getStackFrame(instructionPointerAnnotation);
			if (frame instanceof IJavaStackFrame) {
				IJavaStackFrame stackFrame = (IJavaStackFrame) frame;

				int line = ByteCodeConstants.INVALID_LINE;
				try {
					line = stackFrame.getLineNumber();
				} catch (DebugException ignore) {
				}
					
				try {
					String typeName = classFileDocument.getClassName();
					String declaringTypeName = null;
					declaringTypeName = stackFrame.getDeclaringTypeName();

					if (declaringTypeName != null && !declaringTypeName.equals(typeName)) {
						/* check for nested classes */
						if (declaringTypeName.indexOf(ByteCodeConstants.CLASS_NAME_DOLLAR) >= 0) {

							BytecodeEditor newClassFileEditor = (BytecodeEditor) fClassFileEditor
									.startEditorForAnonymousClassAndReval(declaringTypeName);
							if (newClassFileEditor == null) {
								BytecodeVisualizerPlugin.log(new Status(
										IStatus.ERROR,
										CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID,
										"Could not start editor for an anonymous class."));
							}
							else {
								BytecodeDocumentProvider bdp = (BytecodeDocumentProvider) newClassFileEditor
										.getDocumentProvider();
								IDocument doc = bdp.getBytecodeDocument(newClassFileEditor.getBytecodeEditorInput());
								Position newPos = getBytecodePosition(
										stackFrame, bdp.getClassFileDocument(),
										doc);

								IAnnotationModel annModel = bdp
										.getAnnotationModel(newClassFileEditor
												.getEditorInput());
								if (annModel != null) {
									
									if (annModel == this) {
										/* Very dirty Fix for BUG#215 */
										super.removeAnnotation(annotation);
										super.addAnnotation(annotation, newPos);
									}
									else {
										annModel.removeAnnotation(annotation);
										annModel.addAnnotation(annotation, newPos);
									}
								}

								/* reval new part */
								newClassFileEditor.selectAndRevealBytecode(newPos.offset, 0);
							}
						}
						else {
							//TODO: check what this position actually means
						}
					}
					else {
						/* we do not need to resolve nested classes */
						Position newPosition = getBytecodePosition(stackFrame, classFileDocument, fDocument);
						super.addAnnotation(annotation, newPosition);

						/* revaluate in editor */
						fClassFileEditor.selectAndRevealBytecode(newPosition.offset, 0);
					}
				} catch (DebugException e) {
					BytecodeVisualizerPlugin.log(e);
				}
			}
		} else {
			/* default handling */
			super.addAnnotation(annotation, position);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.texteditor.AbstractMarkerAnnotationModel#
	 * createPositionFromMarker(org.eclipse.core.resources.IMarker)
	 */
	protected Position createPositionFromMarker(IMarker marker) {

		if (classFileDocument == null) {
			return null;
		}

		String markerType = null;
		try {
			markerType = marker.getType();
		} catch (CoreException e1) {
			CorePlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR,
							CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID, e1
									.getMessage(), e1));
		}
		int l = ByteCodeConstants.INVALID_LINE;

		if (markerType != null) {
			if (markerType
					.equals("org.eclipse.jdt.debug.javaMethodBreakpointMarker")) {
				int line = MarkerUtilities.getLineNumber(marker);
				String methodName = marker.getAttribute(
						"org.eclipse.jdt.debug.core.methodName", "NULL");
				String methodSignature = marker.getAttribute(
						"org.eclipse.jdt.debug.core.methodSignature", "NULL");

				IMethodSection m = classFileDocument.findMethodSection(line);
				if (m == null) {
					m = classFileDocument.findMethodSection(methodName,
							methodSignature);
				}
				if (m != null) {
					l = m.getFirstLine();
				}
			} else if (markerType
					.equals("org.eclipse.jdt.debug.javaClassPrepareBreakpointMarker")) {
				l = classFileDocument.getClassSignatureDocumentLine();
			} else if (markerType
					.equals("org.eclipse.jdt.debug.javaWatchpointMarker")) {
				String fieldName = marker.getAttribute(
						"org.eclipse.jdt.debug.core.fieldName", "NULL");

				IFieldSection f = classFileDocument.findFieldSection(fieldName);
				l = f.getBytecodeDocumentLine();
			} else if (markerType
					.equals("org.eclipse.jdt.debug.javaLineBreakpointMarker")) {
				int line = MarkerUtilities.getLineNumber(marker);

				int start = marker.getAttribute(
						"org.eclipse.jdt.debug.ui.member_start",
						ByteCodeConstants.INVALID_LINE);

				try {
					IJavaElement element = fClassFile.getElementAt(start);
					if (element != null && element.getElementType() == IJavaElement.METHOD) {
						IMethod method = (IMethod) element;

						String methodName;
						if (method.isConstructor()) {
							methodName = "<init>";
						} else {
							methodName = method.getElementName();
						}

						String methodSignature = ClassFileDocumentsUtils
								.resolveMethodSignature(method);
						IMethodSection methodSection = classFileDocument
								.findMethodSection(methodName, methodSignature);

						if (methodSection != null) {
							l = methodSection.getBytecodeLine(line) - 1;
						} else {
							return null;
						}

					} else {
						return null;
					}

				} catch (JavaModelException e) {
					CorePlugin
							.getDefault()
							.getLog()
							.log(
									new Status(
											IStatus.ERROR,
											CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID,
											e.getMessage(), e));
				}
			}
		}

		int start;
		try {
			start = fDocument.getLineOffset(l);
		} catch (BadLocationException e) {
			start = 0;
		}
		return new Position(start, 0);
	}
	
	/**
	 * Returns the reference to the class file document.
	 * 
	 * @return class file document
	 */
	public IClassFileDocument getClassFileDocument() {
		return classFileDocument;
	}
	
	private IStackFrame getStackFrame(DynamicInstructionPointerAnnotation a) {
		IStackFrame result = null;
		try {
			Class<?> cl = a.getClass();
			Field f = null;

			if (cl.equals(InstructionPointerAnnotation.class)) {
				cl = cl.getSuperclass();
			} else if (cl.equals(DynamicInstructionPointerAnnotation.class)) {
				/* nothing to do */
			} else {
				throw new RuntimeException("Unexpected annotattion type '"
						+ a.getClass().getName() + "'");
			}
			f = cl.getDeclaredField("fStackFrame");
			// set accessible true
			f.setAccessible(true);
			result = (IStackFrame) f.get(a);
			f.setAccessible(false);
		} catch (Throwable e) {
			BytecodeVisualizerPlugin.log(e);
		}

		if (result == null) {
			/*
			 * IStackFrame not found yet - fallback to DebugContext
			 */
			Object o = DebugUITools.getDebugContext();
			if (o instanceof IStackFrame) {
				result = (IStackFrame) o;
			}
		}

		return result;
	}

	/**
	 * Set the reference to the class file document.
	 * 
	 * @param classFileDocument
	 */
	public void setClassFileDocument(IClassFileDocument classFileDocument) {
		this.classFileDocument = classFileDocument;
	}
}
