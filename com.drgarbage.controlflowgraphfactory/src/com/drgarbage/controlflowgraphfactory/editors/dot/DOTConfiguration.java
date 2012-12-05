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

package com.drgarbage.controlflowgraphfactory.editors.dot;

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
 * DOT Source code view configuration.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: DOTConfiguration.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class DOTConfiguration extends SourceViewerConfiguration {
	private DOTDoubleClickStrategy doubleClickStrategy;
	private DOTScanner scanner;
	private ColorManager colorManager;

	public DOTConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(
			ISourceViewer sourceViewer, String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new DOTDoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected DOTScanner getXMLScanner() {
		if (scanner == null) {
			scanner = new DOTScanner(colorManager);
			scanner.setDefaultReturnToken(new Token(new TextAttribute(
					colorManager.getColor(ColorManager.DEFAULT))));
		}
		return scanner;
	}

	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getXMLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(getXMLScanner());
		reconciler.setDamager(dr, DOTPartitionScanner.MULTILINE_COMMENT);
		reconciler.setRepairer(dr, DOTPartitionScanner.MULTILINE_COMMENT);

		return reconciler;
	}

	// public ITextHover getTextHover(ISourceViewer sv, String contentType) {
	// return new ITextHover(){
	//
	// public String getHoverInfo(ITextViewer textViewer,
	// IRegion hoverRegion) {
	// // TODO Auto-generated method stub
	// return "HEllo";
	// }
	//
	// public IRegion getHoverRegion(ITextViewer textViewer, final int offset) {
	// // TODO Auto-generated method stub
	// return new IRegion(){
	//
	// public int getLength() {
	// // TODO Auto-generated method stub
	// return 10;
	// }
	//
	// public int getOffset() {
	// // TODO Auto-generated method stub
	// return offset;
	// }
	//					
	// };
	// }
	//        	
	// };
	// }
}