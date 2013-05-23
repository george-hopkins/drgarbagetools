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

package com.drgarbage.classfile.editors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.eclipse.core.filebuffers.manipulation.ContainerCreator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.utils.Messages;

/**
 * A sharable document provider. The document content is generated from the class file.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class ClassFileDocumentProvider extends FileDocumentProvider {
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.StorageDocumentProvider#createDocument(java.lang.Object)
	 */
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new ClassFilePartitionScanner(),
					new String[] {
						ClassFilePartitionScanner.MULTILINE_COMMENT 
						});
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.StorageDocumentProvider#setDocumentContent(org.eclipse.jface.text.IDocument, java.io.InputStream, java.lang.String)
	 */
	protected void setDocumentContent(IDocument document, InputStream contentStream, String encoding) throws CoreException {

		try {
			int max = contentStream.available();
			byte[] bytes = new byte[max];
			for(int i = 0; i < max; i++){
				bytes[i] = (byte) contentStream.read();
			}
			
			ClassFileParser cfp = new ClassFileParser();
			String s = cfp.parseClassFile(bytes);
			document.set(s);
			
		} catch (IOException e) {
			handleException(IOException.class.getName(), e);
			Messages.error(IOException.class.getName() +
					CoreMessages.ExceptionAdditionalMessage);
			return;
		} catch (ParseException e) {
			handleException(ParseException.class.getName(), e);
			Messages.error(ParseException.class.getName() +
					CoreMessages.ExceptionAdditionalMessage);
			return;
		} catch(IllegalArgumentException e){
			handleException(IllegalArgumentException.class.getName(), e);
			Messages.error(IllegalArgumentException.class.getName() +
					CoreMessages.ExceptionAdditionalMessage);
			return;
		}catch(IllegalStateException e){
			handleException(IllegalStateException.class.getName(), e);
			Messages.error(IllegalStateException.class.getName() +
					CoreMessages.ExceptionAdditionalMessage);
			return;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.FileDocumentProvider#doSaveDocument(org.eclipse.core.runtime.IProgressMonitor, java.lang.Object, org.eclipse.jface.text.IDocument, boolean)
	 */
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite) throws CoreException {
		if (element instanceof IFileEditorInput) {

			IFileEditorInput input= (IFileEditorInput) element;
			IFile file= input.getFile();

			FileInfo info= (FileInfo) getElementInfo(element);

			byte[] bytes = null;
			try {
				bytes = ClassFileParser.getClasFileBytesFromString(document.get());
			} catch (ParseException e) {
				handleException(ParseException.class.getName(), e);
				Messages.error(ParseException.class.getName() +
						CoreMessages.ExceptionAdditionalMessage);
			}
			
			InputStream stream= new ByteArrayInputStream(bytes);

			if (file.exists()) {

				if (info != null && !overwrite)
					checkSynchronizationState(info.fModificationStamp, file);

				/* inform about the upcoming content change */
				fireElementStateChanging(element);
				try {
					file.setContents(stream, overwrite, true, monitor);
				} catch (CoreException x) {
					/* inform about failure */
					fireElementStateChangeFailed(element);
					throw x;
				} catch (RuntimeException x) {
					/* inform about failure */
					fireElementStateChangeFailed(element);
					throw x;
				}
 
				/* 
				 * If here, the editor state will be flipped to "not dirty".
				 * Thus, the state changing flag will be reset. 
				 */

				if (info != null) {
					ResourceMarkerAnnotationModel model= (ResourceMarkerAnnotationModel) info.fModel;
					if (model != null)
						model.updateMarkers(info.fDocument);

					info.fModificationStamp= computeModificationStamp(file);
				}
			} else {
				try {
					monitor.beginTask("Saving", 2000);
					ContainerCreator creator = new ContainerCreator(file.getWorkspace(), file.getParent().getFullPath());
					creator.createContainer(new SubProgressMonitor(monitor, 1000));
					file.create(stream, false, new SubProgressMonitor(monitor, 1000));
				}
				finally {
					monitor.done();
				}
			}

		} else {
			super.doSaveDocument(monitor, element, document, overwrite);
		}
	}

	private void handleException(String message, Throwable t){
		IStatus status = BytecodeVisualizerPlugin.createErrorStatus(message, t);
		BytecodeVisualizerPlugin.log(status);
	}
}