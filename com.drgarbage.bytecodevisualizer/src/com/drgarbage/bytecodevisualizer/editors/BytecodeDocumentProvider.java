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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.debug.core.model.JDIReferenceType;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.ui.IResourceLocator;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.drgarbage.asm.ClassReader;
import com.drgarbage.asm.render.impl.ClassFileDocument;
import com.drgarbage.asm.render.impl.ClassFileOutlineElement;
import com.drgarbage.asm.render.intf.IClassFileDocument;
import com.drgarbage.asm.render.intf.IDocumentUpdateListener;
import com.drgarbage.bytecode.BytecodeUtils;
import com.drgarbage.bytecode.jdi.JDIClassFileDocument;
import com.drgarbage.bytecode.jdi.dialogs.SelectDebugTargetDialog;
import com.drgarbage.bytecode.jdi.dialogs.SelectJavaTypeDialog;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.bytecodevisualizer.preferences.BytecodeVisualizerPreferenceConstats;
import com.drgarbage.core.CoreConstants;
import com.drgarbage.io.FileExtensions;
import com.drgarbage.javasrc.JavaLexicalConstants;
import com.sun.jdi.ReferenceType;

/**
 * Except for file resources handled by the superclass, it can hanle also 
 * class files coming from jar archives.
 *
 * @author Peter Palaga
 * @version $Revision:25 $
 * $Id:ByteCodeDocumentProvider.java 25 2007-04-01 17:56:22Z aleks $
 */
@SuppressWarnings("restriction")
public class BytecodeDocumentProvider extends FileDocumentProvider {

	/**
	 * reference to the editor part.
	 */
	private BytecodeEditor classFileEditor;

	/**
	 * Reference to the class file document object.
	 */
	private IClassFileDocument classFileDocument = null;
	
	/**
	 * Reference to the class file outline element.
	 */
	private IJavaElement classFileOutlineElement;
	


	//private IClassFile classFile;

//	public IClassFile getClassFile() {
//		return classFile;
//	}

	/**
	 * List of the document update listeners. <code>BytecodeDocumentProvider</code> fires on each 
	 * update of this document an event for all registered listeners.
	 */
	private ArrayList<IDocumentUpdateListener> documentUpdateListeners;

	/**
	 * Constructor.
	 * @param editor part
	 */
	public BytecodeDocumentProvider(BytecodeEditor part) {
		super();
		this.classFileEditor = part;
	}
	
	/**
	 * Returns the reference to the outline element.
	 * @return the classFileOutlineElement
	 */
	public IJavaElement getClassFileOutlineElement() {
		return classFileOutlineElement;
	}
	
	/**
	 * returns the reference to the class file document.
	 * @return the class file document
	 */
	public IClassFileDocument getClassFileDocument() {
		return classFileDocument;
	}

	/**
	 * Adds <code>listener</code> to the list that will be fired 
	 * on each update of this document.
	 * @param listener
	 */
	public void addDocumentUpdateListener(IDocumentUpdateListener listener) {
		if (listener != null) {
			if (documentUpdateListeners == null) {
				documentUpdateListeners = new ArrayList<IDocumentUpdateListener>();
			}
			documentUpdateListeners.add(listener);
		}
	}

	/**
	 * Removes the given <code>listener</code> from the list.
	 * @param listener
	 */
	public void removeDocumentUpdateListener(IDocumentUpdateListener listener) {
		if (listener != null && documentUpdateListeners != null) {
			Iterator<IDocumentUpdateListener> it = documentUpdateListeners.iterator();
			while (it.hasNext()) {
				IDocumentUpdateListener l = it.next();
				if (l == listener) {
					it.remove();
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.StorageDocumentProvider#createDocument(java.lang.Object)
	 */
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document= super.createDocument(element);

		if (document != null) {
			JavaTextTools tools= JavaPlugin.getDefault().getJavaTextTools();
			tools.setupJavaDocumentPartitioner(document, IJavaPartitions.JAVA_PARTITIONING);
		}
		return document;
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.StorageDocumentProvider#createEmptyDocument()
	 */
	protected IDocument createEmptyDocument() {
		return new BytecodeDocument(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.FileDocumentProvider#setDocumentContent(org.eclipse.jface.text.IDocument, org.eclipse.ui.IEditorInput, java.lang.String)
	 */
	protected boolean setDocumentContent(IDocument document, IEditorInput editorInput, String encoding) throws CoreException {

		if (editorInput instanceof IClassFileEditorInput) {

			/* Some kind of IClassFileEditorInput internal, external or a JAR entry */
			IClassFileEditorInput classFileEditorInput = (IClassFileEditorInput) editorInput;
			IClassFile classFile = classFileEditorInput.getClassFile();
			
			String fileName = classFile.getElementName();
			String className = classFile.getType().getFullyQualifiedName();
			
			String retrieveFrom = BytecodeVisualizerPlugin.getDefault().getPreferenceStore().getString(BytecodeVisualizerPreferenceConstats.RETRIEVE_CLASS_FROM);
			if (BytecodeVisualizerPreferenceConstats.RETRIEVE_CLASS_FROM_JVM_JDI.equals(retrieveFrom)) {
				/* Use JDI */

				/* referenceType will be the class to display over JDI */
				ReferenceType referenceType = null;
				String debugTargetName = null;
				
				if (editorInput instanceof JDIEditorInput) {
					JDIEditorInput jdiEditorInput = (JDIEditorInput) editorInput;
					JDIStackFrame stackFrame = jdiEditorInput.getStackFrame();
					if (stackFrame != null 
							&& !stackFrame.isDisconnected()
							&& !stackFrame.isTerminated()) {
						String stackType = stackFrame.getDeclaringTypeName();
						if (className.equals(stackType)) {
							/* yes, we have to open the same type 
							 * so we assume, we have to open the one 
							 * selected on the stack */
							referenceType = stackFrame.getUnderlyingMethod().declaringType();
							debugTargetName = stackFrame.getDebugTarget().getName();
						}
					}
				}
				
				if (referenceType == null ) {
					/* we were not able to use JDIEditorInput */

					/* try to use one of the running debug targets to lookup the class */
					ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
					IDebugTarget[] dts = lm.getDebugTargets();
					if (dts != null && dts.length > 0) {
						/* There are some running debug sessions */
						
						/* Find those ones, which contain the given class */
						ArrayList<IJavaType[]> displayCandidates = new ArrayList<IJavaType[]>(dts.length);
						for (int i = 0; i < dts.length; i++) {
							if (dts[i] instanceof IJavaDebugTarget && !((IJavaDebugTarget)dts[i]).isTerminated()) {
								
								IJavaDebugTarget jdt = (IJavaDebugTarget) dts[i];
								IJavaType[] jts = jdt.getJavaTypes(className);
								if (jts != null && jts.length > 0) {
									displayCandidates.add(jts);
								}
							}
						}
						
						/* select one IJavaDebugTarget */
						IJavaType[] jts = null;
						if (displayCandidates.size() == 1) {
							jts = displayCandidates.get(0);
						}
						else if (displayCandidates.size() > 1) {
							/* There are several possible debug targets with 
							 * the given class loaded.
							 * Let the user select one debug target from a list. */
							SelectDebugTargetDialog d = new SelectDebugTargetDialog(className, displayCandidates);
							int btn = d.open();
							if (btn == SelectDebugTargetDialog.OK) {
								IJavaType[] sel = d.getSelection();
								if (sel != null) {
									jts = sel;
								}
							}
							else {
								/* Dialog closed or Filesystem button clicked */
							}
						}

						if (jts != null && jts.length > 0) {
							
							IJavaType jt = null;
							if (jts.length == 1) {
								/* there is only one such class - take it */
								jt = jts[0];
							}
							else {
								/* let the user select one */
								SelectJavaTypeDialog d = new SelectJavaTypeDialog(className, jts[0].getDebugTarget().getName(), jts);
								d.open();
								IJavaType sel = d.getSelection();
								if (sel != null) {
									jt = sel;
								}
							}
							
							if (jt != null && jt instanceof JDIReferenceType) {
								referenceType = (ReferenceType) ((JDIReferenceType) jt).getUnderlyingType();
								debugTargetName = jt.getDebugTarget().getName();
							}
						}
					}

				}
				
				
				
				if (referenceType != null) {
					/* we were able to read the referenceType over JDI */
					
					ClassFileOutlineElement outlineElement = new ClassFileOutlineElement();
					JDIClassFileDocument doc = new JDIClassFileDocument(referenceType, debugTargetName, outlineElement);
			        outlineElement.setClassFileDocument(doc);
			        doc.createJDIcontent();
			        
					document.set(doc.toString());
					
					classFileDocument = doc;
					classFileOutlineElement = outlineElement;
					
					/* fire update document events */
					if (documentUpdateListeners != null) {
						Iterator<IDocumentUpdateListener> it = documentUpdateListeners.iterator();
						while (it.hasNext()) {
							IDocumentUpdateListener l = it.next();
							l.documentUpdated(classFileDocument);
						}
					}
					
					return true;
				}
				
			}
			
			/* If we reached this point it means that JDI is not prefered or that JDI did not work. */
			
			IPath filePath =  classFile.getPath();
			String ext = filePath.getFileExtension();
			if (FileExtensions.JAR.equalsIgnoreCase(ext)) {
				/* really a jar, check if it is an external or internal resource 
				 * FIX: bug#50 "java.util.zip.ZipException: error 
				 * in opening zip file" on openening*/				
				File f = null;
				IResource res = classFile.getResource();
				if(res != null){
					f = new File(res.getLocationURI());
				}
				else{
					f = filePath.toFile();
				}

				try {
					JarFile jarFile = new JarFile(f);
					String packageName = classFile.getParent().getElementName();
					try {
						
						String entryName = null;
						if (packageName.length() == 0) {
							/* default package
							 * FIX for BUG#213 */
							entryName = fileName;
						}
						else {
							entryName = packageName.replace(JavaLexicalConstants.DOT, JavaLexicalConstants.SLASH) + JavaLexicalConstants.SLASH + fileName;
						}
						
						JarEntry jarEntry = jarFile.getJarEntry(entryName);
						if (jarEntry != null) {
							InputStream contentStream = jarFile.getInputStream(jarEntry);
							setDocumentContent(document, contentStream, encoding);
							return true;
						}
					} finally {
						jarFile.close();
					}
				} catch (IOException e) {
					throw new CoreException(new Status(IStatus.ERROR, CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID, IStatus.OK, BytecodeVisualizerMessages.Error_could_not_load_a_class, e));
				}
			}

			/* external or internal file archive */
			if(FileExtensions.CLASS.equalsIgnoreCase(ext)){
				try {
					File f = filePath.toFile();

					if(f.exists()){/* external file archive */
						InputStream contentStream = new FileInputStream(f);
						setDocumentContent(document, contentStream, encoding);
						return true;
					}
					else{/* internal file archive */
						IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
						InputStream contentStream = file.getContents();
						setDocumentContent(document, contentStream, encoding);
						return true;
					}
				} catch (FileNotFoundException e) {
					throw new CoreException(new Status(IStatus.ERROR, CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID, IStatus.OK, BytecodeVisualizerMessages.Error_could_not_load_a_class, e));
				}			
			}

		}
		else if (editorInput instanceof FileStoreEditorInput){ 
			/* external class file */
			FileStoreEditorInput fileStoreEditorInput = (FileStoreEditorInput) editorInput;
			try {
				File f = new File(fileStoreEditorInput.getURI());
				InputStream contentStream = new FileInputStream(f);
				setDocumentContent(document, contentStream, encoding);
			} catch (FileNotFoundException e) {
				throw new CoreException(new Status(IStatus.ERROR, CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID, IStatus.OK, BytecodeVisualizerMessages.Error_could_not_load_a_class, e));
			}

			return true;
		}
		else{
			/* the super class should be able to handle this kind of editorInput */
			return super.setDocumentContent(document, editorInput, encoding);
		}
		
		return false;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.StorageDocumentProvider#setDocumentContent(org.eclipse.jface.text.IDocument, java.io.InputStream, java.lang.String)
	 */
	protected void setDocumentContent(IDocument document, InputStream contentStream, String encoding) throws CoreException {		
		DataInputStream in = null;
		try {
			/* buffer only if necessary */
			if (contentStream instanceof BufferedInputStream) {
				in = new DataInputStream(contentStream);
			}
			else {
				in = new DataInputStream(new BufferedInputStream(contentStream));
			}
			ClassFileOutlineElement outlineElement = new ClassFileOutlineElement();
	        ClassFileDocument doc = new ClassFileDocument(outlineElement);
	        outlineElement.setClassFileDocument(doc);
	        ClassReader cr = new ClassReader(in, doc);
	        cr.accept(doc, 0);

			document.set(doc.toString());
			
			classFileDocument = doc;
			classFileOutlineElement = outlineElement;

		} catch (IOException e) {
			e.printStackTrace();
			throw new CoreException(new Status(IStatus.ERROR, CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID, IStatus.OK, BytecodeVisualizerMessages.Error_not_load_file, e));
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
		}

		/* fire update document events */
		if (documentUpdateListeners != null) {
			Iterator<IDocumentUpdateListener> it = documentUpdateListeners.iterator();
			while (it.hasNext()) {
				IDocumentUpdateListener l = it.next();
				l.documentUpdated(classFileDocument);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.FileDocumentProvider#isModifiable(java.lang.Object)
	 */
	@Override
	public boolean isModifiable(Object element) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.FileDocumentProvider#createAnnotationModel(java.lang.Object)
	 */
	protected IAnnotationModel createAnnotationModel(Object element) throws CoreException {
		if(element instanceof IEditorInput){
			IEditorInput input = (IEditorInput) element;

			if (input instanceof IFileEditorInput) {

				/* a local class file */
				IFileEditorInput fileEditorInput = (IFileEditorInput)input;
				IFile file = fileEditorInput.getFile();
				
				String fqTypeName = BytecodeUtils.toFullyQualifiedTypeName(file);
				
				if (fqTypeName != null) {
					/* FIX for BUG#214:
					 *  NullPointerException when opening a Class which is 
					 *  not in build path from Navigator */
					
					IProject project = file.getProject();

					/* create java project */
					IJavaProject javaProject = JavaCore.create(project);

					try {
						IType t = javaProject.findType(fqTypeName);
						if (t != null) {
							IResource res = t.getCompilationUnit().getResource();
							
							BytecodeMarkerAnnotationModel model= new BytecodeMarkerAnnotationModel(res, classFileEditor);
							
							IClassFile classFile = t.getClassFile();
							if (classFile == null) {
								classFile = JavaModelManager.createClassFileFrom(file, javaProject);
							}
							
							model.setClassFile(classFile);
							model.setClassFileDocument(classFileDocument);
							
							return model;
						}
					} catch (JavaModelException e) {
						throw new CoreException(new Status(IStatus.ERROR, CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID, IStatus.OK, BytecodeVisualizerMessages.Error_not_load_file, e));
					}	
				}
				
			}
		}
		
		
		if(element instanceof IClassFileEditorInput){
			return createClassFileAnnotationModel((IClassFileEditorInput)element);
		}
		
		return null;
	}
	
	/**
	 * Creates a class file based model.
	 * @param classFileEditorInput
	 * @return
	 * @throws CoreException
	 */
	private IAnnotationModel createClassFileAnnotationModel(IClassFileEditorInput classFileEditorInput) throws CoreException {
		
		if (classFileDocument != null) {
			IResource resource= null;
			IClassFile classFile= classFileEditorInput.getClassFile();

			IResourceLocator locator= (IResourceLocator) classFile.getAdapter(IResourceLocator.class);
			if (locator != null) {
				resource= locator.getContainingResource(classFile);
			}

			if (resource != null) {
				BytecodeMarkerAnnotationModel model= new BytecodeMarkerAnnotationModel(resource, classFileEditor);
				model.setClassFile(classFile);
				model.setClassFileDocument(classFileDocument);
				return model;
			}
		}
		
		return null;
	}

	@Override
	public IDocument getDocument(Object element) {
		return getBytecodeDocument(element);
	}

	public IDocument getBytecodeDocument(Object input) {
		return super.getDocument(input);
	}
	
	public IDocument getSourcecodeDocument(Object input) {
		if (classFileEditor != null) {
			ISourceCodeViewer sourceCodeViewer = classFileEditor.getSourceCodeViewer();
			if (sourceCodeViewer != null) {
				IDocumentProvider documentProvider = sourceCodeViewer.getDocumentProvider();
				if (documentProvider != null) {
					return documentProvider.getDocument(input);
				}
			}
		}
		return null;
	}

}
