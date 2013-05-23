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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

/**
 * The configuration space of a class file editor.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class ClassFileConfiguration extends SourceViewerConfiguration {
	private ClassFileDoubleClickStrategy doubleClickStrategy;
	private ClassFileScanner scanner;
	private ColorManager colorManager;

	public ClassFileConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredContentTypes(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			ClassFilePartitionScanner.MULTILINE_COMMENT
			};
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getDoubleClickStrategy(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new ClassFileDoubleClickStrategy();
		return doubleClickStrategy;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getClassFileScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr =
				new NonRuleBasedDamagerRepairer(
					new TextAttribute(
						colorManager.getColor(ColorManager.COMMENT)));
		reconciler.setDamager(ndr, ClassFilePartitionScanner.MULTILINE_COMMENT);
		reconciler.setRepairer(ndr, ClassFilePartitionScanner.MULTILINE_COMMENT);

		return reconciler;
	}
	
	private ClassFileScanner getClassFileScanner() {
		if (scanner == null) {
			scanner = new ClassFileScanner(colorManager);
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(ColorManager.DEFAULT))));
		}
		return scanner;
	}
}