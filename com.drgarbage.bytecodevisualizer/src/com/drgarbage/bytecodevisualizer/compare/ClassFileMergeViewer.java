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

package com.drgarbage.bytecodevisualizer.compare;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ResourceNode;
import org.eclipse.compare.contentmergeviewer.IMergeViewerContentProvider;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.classfile.editors.ClassFileConfiguration;
import com.drgarbage.classfile.editors.ClassFileParser;
import com.drgarbage.classfile.editors.ColorManager;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.javalang.JavaLangUtils;
import com.drgarbage.javasrc.JavaSourceUtils;
import com.drgarbage.utils.Messages;

/**
 * The compare and merge viewer with two side-by-side content areas.
 * The viewer modifies the input to represent the class file in 
 * a readable format by using the {@link ClassFileParser} and provides 
 * the syntax highlighting from the {@link ClassFileConfiguration}.
 *
 * @see IMergeViewerContentProvider
 * @see TextMergeViewer
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class ClassFileMergeViewer extends TextMergeViewer{

	protected SourceViewer ansestorSourceViewer = null;
	protected SourceViewer sourceViewerLeft = null;
	protected SourceViewer sourceViewerRight = null;
	
	public static String CLASS_FILE_MERGEVIEWER_TITLE = "Class File Compare";//$NON-NLS-1$
	
	public ClassFileMergeViewer(Composite parent,
			CompareConfiguration configuration) {
		super(parent, configuration);
		
		/* set display string */
		getControl().setData(CompareUI.COMPARE_VIEWER_TITLE, CLASS_FILE_MERGEVIEWER_TITLE);
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.compare.contentmergeviewer.TextMergeViewer#configureTextViewer(org.eclipse.jface.text.TextViewer)
	 */
	protected void configureTextViewer(TextViewer textViewer) {
		ColorManager colorManager = new ColorManager();
		ClassFileConfiguration conf = new ClassFileConfiguration(colorManager);
		
		if (textViewer instanceof SourceViewer) {
			SourceViewer sourceViewer = (SourceViewer) textViewer;
			sourceViewer.configure(conf);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.compare.contentmergeviewer.TextMergeViewer#createSourceViewer(org.eclipse.swt.widgets.Composite, int)
	 */
	protected SourceViewer createSourceViewer(Composite parent, int textOrientation) {
		
		if(ansestorSourceViewer == null){
			ansestorSourceViewer =  super.createSourceViewer(parent, textOrientation);
			return ansestorSourceViewer;
		}
		if(sourceViewerLeft == null){
			sourceViewerLeft = createSourceViewerLeft(parent,textOrientation);
			return sourceViewerLeft;
		}
		else if(sourceViewerRight == null){
			sourceViewerRight = createSourceViewerRight(parent,textOrientation);
			return sourceViewerRight;
		}

		return super.createSourceViewer(parent, textOrientation);
	}
	
	
	/**
	 * Creates a new left source viewer.
	 * @param parent
	 * @param parent
	 *            the parent of the viewer's control
	 * @param textOrientation
	 *            style constant bit for text orientation
	 */
	private SourceViewer createSourceViewerLeft(Composite parent, int textOrientation){
	
		SourceViewer sourceViewer =  new SourceViewer(parent, 
				new CompositeRuler(), textOrientation | SWT.H_SCROLL | SWT.V_SCROLL){
			
			/* (non-Javadoc)
			 * @see org.eclipse.jface.text.source.SourceViewer#setDocument(org.eclipse.jface.text.IDocument)
			 */
			@Override
			public void setDocument( IDocument document ) { 
				if( document != null ) {
					IDocument newDocument = new Document();
					
					IJavaElement	elementLetft = getLeftElement();
					if(elementLetft != null){
						String s = generateClassFileInput(elementLetft);
						if(s!= null){
							newDocument.set(s);
							newDocument.addDocumentListener(
									new IDocumentListener(){

										/* (non-Javadoc)
										 * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
										 */
										public void documentAboutToBeChanged(
												DocumentEvent arg0) {
										}

										/* (non-Javadoc)
										 * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
										 */
										public void documentChanged(DocumentEvent arg0) {
											setLeftDirty(true);
										}

									});
						}	
					}   

					super.setDocument( newDocument );
				} else {
					super.setDocument(null);
				}
			}
		};
		
		return sourceViewer;
	}

	/**
	 * Creates a new right source viewer.
	 * @param parent
	 * @param parent
	 *            the parent of the viewer's control
	 * @param textOrientation
	 *            style constant bit for text orientation
	 */
	private SourceViewer createSourceViewerRight(Composite parent, int textOrientation){
		
		SourceViewer sourceViewer =  new SourceViewer(parent, 
				new CompositeRuler(), textOrientation | SWT.H_SCROLL | SWT.V_SCROLL){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.text.source.SourceViewer#setDocument(org.eclipse.jface.text.IDocument)
			 */
			@Override
            public void setDocument( IDocument document ) { 
				if( document != null ) {
					IDocument newDocument = new Document();
					
					IJavaElement elementRight = getRightElement();
					if(elementRight != null){
						String s = generateClassFileInput(elementRight);
						if(s!= null){
							newDocument.set(s);
							newDocument.addDocumentListener(
									new IDocumentListener(){

										public void documentAboutToBeChanged(
												DocumentEvent arg0) {
										}

										public void documentChanged(DocumentEvent arg0) {
											setRightDirty(true);
										}

									});
						}
					}

					super.setDocument( newDocument );
				} else {
                    super.setDocument(null);
                }
            }
		};
		
		return sourceViewer;
	}
	
	/**
	 * Returns <code>true</code> if the content of the left
	 * source viewer is empty.  
	 * @return <code>true</code> or <code>false</code>
	 */
	protected boolean isContentLeftEmpty(){
		return (getContentLeft() == null || getContentLeft().equals(""));
	}
	
	/**
	 * Returns <code>true</code> if the content of the right
	 * source viewer is empty.  
	 * @return <code>true</code> or <code>false</code>
	 */
	protected boolean isContentRightEmpty(){
		return (getContentRight() == null || getContentRight().equals(""));
	}
	
	/**
	 * Returns the contents of the underlying document from the 
	 * left source viewer using the current workbench encoding.
	 * 
	 * @return the contents as an array of bytes or null
	 */
	protected byte[] getContentLeft(){
		return getContents(true);
	}
	
	/**
	 * Returns the contents of the underlying document from the 
	 * right source viewer using the current workbench encoding.
	 * 
	 * @return the contents as an array of bytes or null
	 */
	protected byte[] getContentRight(){
		return getContents(false);
	}
	
	/**
	 * Returns the underlying java resource element of the left 
	 * source viewer.
	 * @return resource object 
	 * 
	 * @see IMergeViewerContentProvider
	 */
	protected IJavaElement getLeftElement(){
		Object input = getInput();
		IMergeViewerContentProvider content = 
				(IMergeViewerContentProvider) getContentProvider();
		Object o = content.getLeftContent(input);
		if(o instanceof ResourceNode){
			ResourceNode r = (ResourceNode) o;
			IJavaElement javaElement = getJavaElementFromResource(r.getResource());
			return javaElement;
		}
		
		if(o instanceof CompareElement){
			CompareElement ce = (CompareElement)o;
			return ce.getJavaElement();
		}

		return null;
	}
	
	/**
	 * Returns the underlying java resource element of the right 
	 * source viewer.
	 * @return resource object 
	 * 
	 * @see IMergeViewerContentProvider
	 */
	protected IJavaElement getRightElement(){
		Object input = getInput();
		IMergeViewerContentProvider content = 
				(IMergeViewerContentProvider) getContentProvider();
		Object o = content.getRightContent(input);
		if(o instanceof ResourceNode){
			ResourceNode r = (ResourceNode) o;
			IJavaElement javaElement = getJavaElementFromResource(r.getResource());
			return javaElement;
		}
		
		if(o instanceof CompareElement){
			CompareElement ce = (CompareElement)o;
			return ce.getJavaElement();
		}

		return null;
	}

	/**
	 * Returns an java element if the resource is a java object,
	 * otherwise <code>null</code>.
	 * @param res
	 * @return java element
	 */
	public static IJavaElement getJavaElementFromResource(IResource res) {
		if (res instanceof IFile) {
			return JavaCore.create((IFile)res);
		} 

		if (res instanceof IJavaElement) {
			return (IJavaElement)res;
		} 
		
		if (res instanceof IAdaptable) {
			IAdaptable a = (IAdaptable) res;
			Object adapter = a.getAdapter(IFile.class);
			if (adapter instanceof IFile) {
				return JavaCore.create((IFile)adapter);
			}

			adapter = a.getAdapter(ICompilationUnit.class);
			if (adapter instanceof ICompilationUnit) {
				return (IJavaElement)adapter;
			}

			adapter = a.getAdapter(IClassFile.class);
			if (adapter instanceof IClassFile) {
				return (IJavaElement)adapter;
			}
		}

		return null;
	}
	
	/**
	 * Generates a readable text representation of the class file.
	 * @param element
	 * @return string
	 */
	private String generateClassFileInput (IJavaElement javaElement){
		byte[] bytes = null;
		try {
			InputStream	stream = createStream(javaElement);
			if(stream == null){
				return null;
			}

			int max = stream.available();
			bytes = new byte[max];
			for(int i = 0; i < max; i++){
				bytes[i] = (byte) stream.read();
			}
		} catch (CoreException e) {
			handleException(CoreException.class.getName(), e);
			Messages.error(CoreException.class.getName() +
					CoreMessages.ExceptionAdditionalMessage);
			return null;
		} catch (IOException e) {
			handleException(IOException.class.getName(), e);
			Messages.error(IOException.class.getName() +
					CoreMessages.ExceptionAdditionalMessage);
		} 

		ClassFileParser cfp = new ClassFileParser();
		try {
			String s = cfp.parseClassFile(bytes);
			return s;
		} catch (ParseException e) {
			handleException(ParseException.class.getName(), e);
			Messages.error(ParseException.class.getName() +
					CoreMessages.ExceptionAdditionalMessage);
		}

		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.compare.contentmergeviewer.ContentMergeViewer#flushLeft(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void flushLeft(IProgressMonitor monitor) {
		Object input = getInput();
		IMergeViewerContentProvider content = (IMergeViewerContentProvider) getContentProvider();

		boolean rightEmpty = content.getRightContent(input) == null;

		if (getCompareConfiguration().isLeftEditable() && isLeftDirty()) {
			byte[] bytes = getContents(true);
			if (rightEmpty && bytes != null && bytes.length == 0)
				bytes = null;
			setLeftDirty(false);
			
			doSaveContent(monitor, bytes, 
					(ResourceNode) content.getLeftContent(input));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.compare.contentmergeviewer.ContentMergeViewer#flushRight(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void flushRight(IProgressMonitor monitor) {
		Object input = getInput();
		IMergeViewerContentProvider content = (IMergeViewerContentProvider) getContentProvider();

		boolean leftEmpty = content.getLeftContent(input) == null;

		if (getCompareConfiguration().isRightEditable() && isRightDirty()) {
			byte[] bytes = getContents(false);
			if (leftEmpty && bytes != null && bytes.length == 0)
				bytes = null;
			setRightDirty(false);
						
			doSaveContent(monitor, bytes, 
					(ResourceNode) content.getRightContent(input));
		}
		
		
	}
	
	/**
	 * Flushes the modified content back to class file.
	 * @param monitor a progress monitor
	 * @param contentToSave new content
	 * @param resource the class file has to be saved
	 */
	protected void doSaveContent(IProgressMonitor monitor, 
			byte[] contentToSave, 
			ResourceNode resource)
	{
		byte[] bytesToSave = null;
		try {
			bytesToSave = ClassFileParser.getClasFileBytesFromString(new String(contentToSave));

			IPath path = resource.getResource().getFullPath(); 
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

			file.setContents(new ByteArrayInputStream(bytesToSave), 
					true,   /* keep saving, even if IFile is out 
					         * of sync with the Workspace */ 
					false,  /* dont keep history */
					monitor /* progress monitor */
					);

		} catch (ParseException e) {
			handleException(ParseException.class.getName(), e);
			Messages.error(ParseException.class.getName() +
					CoreMessages.ExceptionAdditionalMessage);
		} catch (CoreException e) {
			handleException(ParseException.class.getName(), e);
			Messages.error(CoreException.class.getName() +
					CoreMessages.ExceptionAdditionalMessage);
		}
	}
	
	/**
	 * Simple exception handler.
	 * @param message
	 * @param t exception object
	 */
	private void handleException(String message, Throwable t){
		IStatus status = BytecodeVisualizerPlugin.createErrorStatus(message, t);
		BytecodeVisualizerPlugin.log(status);
	}
	
	public static InputStream createStream(IJavaElement javaElement) throws CoreException{
		IClassFile classFile = (IClassFile) javaElement
				.getAncestor(IJavaElement.CLASS_FILE);
		
		InputStream	stream = null;
    	if (classFile != null) {
    		stream = new ByteArrayInputStream(classFile.getBytes());	
    	}
    	else{
    		if (javaElement.getParent().getElementType() 
    				== IJavaElement.COMPILATION_UNIT){
    			IType t = (IType) javaElement;
    			IJavaProject javaProject = t.getJavaProject();
    			String fullyQualifiedName = t.getFullyQualifiedName();
    			String className = JavaSourceUtils.getSimpleName(fullyQualifiedName);
    			String packageName = JavaSourceUtils.getPackage(fullyQualifiedName);

    			String classPath[] = JavaLangUtils.computeRuntimeClassPath(javaProject);    			
    			try {
    				stream = JavaLangUtils.findResource(classPath, packageName, className);
    			} catch (IOException e) {
    				throw new CoreException(new Status(IStatus.ERROR, 
        					BytecodeVisualizerPlugin.PLUGIN_ID, 
        					e.getMessage(), 
        					e));
    			}
    		}
    		else{
    			return null;
    		}
    	}
    	
    	return stream;
	}
	
}