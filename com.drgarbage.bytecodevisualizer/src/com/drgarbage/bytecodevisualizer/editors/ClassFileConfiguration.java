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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICodeAssist;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaElementHyperlink;
import org.eclipse.jdt.internal.ui.text.AbstractJavaScanner;
import org.eclipse.jdt.internal.ui.text.JavaCommentScanner;
import org.eclipse.jdt.internal.ui.text.JavaPresentationReconciler;
import org.eclipse.jdt.internal.ui.text.JavaWordFinder;
import org.eclipse.jdt.internal.ui.text.SingleTokenJavaScanner;
import org.eclipse.jdt.internal.ui.text.java.JavaDoubleClickSelector;
import org.eclipse.jdt.ui.actions.SelectionDispatchAction;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaColorConstants;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultTextDoubleClickStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.URLHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

import com.drgarbage.asm.render.intf.IFieldSection;
import com.drgarbage.asm.render.intf.IInstructionLine;
import com.drgarbage.asm.render.intf.IMethodSection;
import com.drgarbage.bytecode.ByteCodeConstants;
import com.drgarbage.bytecode.instructions.BranchInstruction;
import com.drgarbage.core.CorePlugin;
import com.drgarbage.core.preferences.CorePreferenceConstants;

public class ClassFileConfiguration extends TextSourceViewerConfiguration {

	/**
	 * The Java source code scanner.
	 */
	private AbstractJavaScanner fCodeScanner;
	/**
	 * The color manager.
	 */
	private IColorManager fColorManager;
	/**
	 * The document partitioning.
	 */
	private String fDocumentPartitioning;
	/**
	 * The double click strategy.
	 * @since 3.1
	 */
	private JavaDoubleClickSelector fJavaDoubleClickSelector;
	/**
	 * The Java multi-line comment scanner.
	 */
	private AbstractJavaScanner fMultilineCommentScanner;
	/**
	 * The Java single-line comment scanner.
	 */
	private AbstractJavaScanner fSinglelineCommentScanner;

	/**
	 * The Java string scanner.
	 */
	private AbstractJavaScanner fStringScanner;

	/**
	 * Editor reference
	 */
	private ITextEditor fTextEditor;

	public ClassFileConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore, ITextEditor editor, String partitioning) {
		super(preferenceStore);
		fColorManager = colorManager;
		fTextEditor = editor;
		fDocumentPartitioning = partitioning;
		initializeScanners();
	}


	/**
	 * Returns the Java source code scanner for this configuration.
	 *
	 * @return the Java source code scanner
	 */
	protected RuleBasedScanner getCodeScanner() {
		return fCodeScanner;
	}


	/**
	 * Returns the color manager for this configuration.
	 *
	 * @return the color manager
	 */
	protected IColorManager getColorManager() {
		return fColorManager;
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
				IDocument.DEFAULT_CONTENT_TYPE,
				IJavaPartitions.JAVA_MULTI_LINE_COMMENT,
				IJavaPartitions.JAVA_SINGLE_LINE_COMMENT,
				IJavaPartitions.JAVA_STRING,
				IJavaPartitions.JAVA_CHARACTER,
				IJavaPartitions.JAVA_DOC
		};
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredDocumentPartitioning(org.eclipse.jface.text.source.ISourceViewer)
	 * @since 3.0
	 */
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		if (fDocumentPartitioning != null)
			return fDocumentPartitioning;
		return super.getConfiguredDocumentPartitioning(sourceViewer);
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		/*FIX: bug#115 Nicolas F. Rouquette: Problems with the DrGarbage plugins with the DLTK 1.0.0M4 build (Galileo) */
		//		if (IJavaPartitions.JAVA_DOC.equals(contentType))
		//			return new JavadocDoubleClickStrategy();
		if (IJavaPartitions.JAVA_MULTI_LINE_COMMENT.equals(contentType) ||
				IJavaPartitions.JAVA_SINGLE_LINE_COMMENT.equals(contentType))
			return new DefaultTextDoubleClickStrategy();
		//		else if (IJavaPartitions.JAVA_STRING.equals(contentType) ||
		//				IJavaPartitions.JAVA_CHARACTER.equals(contentType))
		//			return new JavaStringDoubleClickSelector(getConfiguredDocumentPartitioning(sourceViewer));
		if (fJavaDoubleClickSelector == null) {
			fJavaDoubleClickSelector= new JavaDoubleClickSelector();
			fJavaDoubleClickSelector.setSourceVersion(fPreferenceStore.getString(JavaCore.COMPILER_SOURCE));
		}
		return fJavaDoubleClickSelector;
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getHyperlinkDetectors(org.eclipse.jface.text.source.ISourceViewer)
	 * @since 3.1
	 */
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		MJavaElementHyperlinkDetector jhl = new MJavaElementHyperlinkDetector();
		jhl.setContext(fTextEditor);
		return new IHyperlinkDetector[] {  
				jhl,
				new ClassFileHyperlinkDetector(), 
				new URLHyperlinkDetector() };
	}

	/**
	 * Returns the Java multi-line comment scanner for this configuration.
	 *
	 * @return the Java multi-line comment scanner
	 * @since 2.0
	 */
	protected RuleBasedScanner getMultilineCommentScanner() {
		return fMultilineCommentScanner;
	}

	/*
	 * @see SourceViewerConfiguration#getPresentationReconciler(ISourceViewer)
	 */
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {

		PresentationReconciler reconciler= new JavaPresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr= new DefaultDamagerRepairer(getCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr= new DefaultDamagerRepairer(getMultilineCommentScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);

		dr= new DefaultDamagerRepairer(getSinglelineCommentScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);

		dr= new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_STRING);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_STRING);

		dr= new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_CHARACTER);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_CHARACTER);

		dr= new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_DOC);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_DOC);

		return reconciler;
	}

	/**
	 * Returns the Java single-line comment scanner for this configuration.
	 *
	 * @return the Java single-line comment scanner
	 * @since 2.0
	 */
	protected RuleBasedScanner getSinglelineCommentScanner() {
		return fSinglelineCommentScanner;
	}
	/**
	 * Returns the Java string scanner for this configuration.
	 *
	 * @return the Java string scanner
	 * @since 2.0
	 */
	protected RuleBasedScanner getStringScanner() {
		return fStringScanner;
	}
	/**
	 * Initializes the scanners.
	 *
	 */
	private void initializeScanners() {
		fCodeScanner = new RenderedBytecodeScanner(getColorManager(), fPreferenceStore);
		fMultilineCommentScanner = new JavaCommentScanner(getColorManager(), fPreferenceStore, IJavaColorConstants.JAVA_MULTI_LINE_COMMENT);
		fSinglelineCommentScanner = new JavaCommentScanner(getColorManager(), fPreferenceStore, IJavaColorConstants.JAVA_SINGLE_LINE_COMMENT);
		fStringScanner = new SingleTokenJavaScanner(getColorManager(), fPreferenceStore, IJavaColorConstants.JAVA_STRING);
	}

	/**
	 * Adapts the behavior of the contained components to the change
	 * encoded in the given event.
	 */
	public void adaptToPreferenceChange(PropertyChangeEvent event) {
		if (fCodeScanner.affectsBehavior(event))
			fCodeScanner.adaptToPreferenceChange(event);
		if (fMultilineCommentScanner.affectsBehavior(event))
			fMultilineCommentScanner.adaptToPreferenceChange(event);
		if (fSinglelineCommentScanner.affectsBehavior(event))
			fSinglelineCommentScanner.adaptToPreferenceChange(event);
		if (fStringScanner.affectsBehavior(event))
			fStringScanner.adaptToPreferenceChange(event);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextSourceViewerConfiguration#getReconciler(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		/* overwrite the implementation to deactivate spelling check */
		return null;
	}    


	/**
	 * HyperLink Detector for BYtecode Visualizer Documnts.
	 */
	class ClassFileHyperlinkDetector implements IHyperlinkDetector {

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.hyperlink.IHyperlinkDetector#detectHyperlinks(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion, boolean)
		 */
		public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
				IRegion region, boolean canShowMultipleHyperlinks) {

			IDocument document = fTextEditor.getDocumentProvider().getDocument(fTextEditor.getEditorInput()); 
			IRegion lineInfo = null; 
			String lineText = null;
			int lineNumber = -1;
			try { 
				lineInfo = document.getLineInformationOfOffset(region.getOffset()); 
				lineText = document.get(lineInfo.getOffset(), lineInfo.getLength()); 
				lineNumber = document.getLineOfOffset(region.getOffset());
			} catch (BadLocationException ex) { 
				return null; 
			}

			if(lineText.contains("goto") || lineText.contains("if")){
				/* mark a complete instruction as hyperlink  "12 goto 23;" ->goto 23*/
				int begin= lineText.indexOf(" ", 7); 
				int end = lineText.indexOf(";");
				if(end < 0 
						|| begin < 0 
						|| end == begin + 1
						|| region.getOffset() < lineInfo.getOffset() + begin + 1) 
					return null; 	

				String linkText = lineText.substring(begin + 1, end); 

				IRegion r2 = new Region(lineInfo.getOffset() + begin + 1, linkText.length());
				return new IHyperlink[] {new ClassFileLocalHyperLink(r2, linkText, lineNumber)}; 
			}
			else if(lineText.contains("/* .")){
				int begin= lineText.indexOf("/* .") + 2; 
				int end = lineText.indexOf("*/") - 1;
				if(end < 0 
						|| begin < 0 
						|| end == begin + 1
						|| region.getOffset() < lineInfo.getOffset() + begin + 1) 
					return null; 	


				String linkText = lineText.substring(begin + 2, end); 

				IRegion r2 = new Region(lineInfo.getOffset() + begin + 1, linkText.length());
				return new IHyperlink[] {new ClassFileFieldLocalHyperLink(r2, linkText, lineNumber)}; 

			}
			//    		else{
			//        		int begin= lineText.indexOf("/*"); 
			//        		int end = lineText.indexOf("*/");
			//        		if(end<0 || begin<0 || end==begin+1) 
			//        			return null; 
			//    			
			//        		String text = line.substring(begin+1,end+1); 
			//        		
			//        		IRegion r2 = new Region(lineInfo.getOffset() + begin + 1, text.length());
			//        		return new IHyperlink[] {new ClassFileLocalHyperLink(r2)}; 
			//
			//    		}

			return null;

		}

	}

	/**
	 * Local Hyperlink class for bytecode visualizer document.
	 */
	class ClassFileLocalHyperLink implements IHyperlink{

		/**
		 * LInk region.
		 */
		private IRegion region; 

		/**
		 * Text of the marked link.
		 */
		private String text;

		/**
		 * The line number in which the link has been detected.
		 */
		private int lineNumber;

		/**
		 * Constructor.
		 * @param region
		 * @param text
		 * @param lineNumber
		 */
		public ClassFileLocalHyperLink(IRegion region, String text, int lineNumber) {
			super();
			this.region = region;
			this.text = text;
			this.lineNumber = lineNumber;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getHyperlinkRegion()
		 */
		public IRegion getHyperlinkRegion() {
			return region;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getHyperlinkText()
		 */
		public String getHyperlinkText() {
			return text;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getTypeLabel()
		 */
		public String getTypeLabel() {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.hyperlink.IHyperlink#open()
		 */
		public void open() {

			BytecodeDocumentProvider bdp = (BytecodeDocumentProvider)fTextEditor.getDocumentProvider();
			IMethodSection method = bdp.getClassFileDocument().findMethodSection(lineNumber);
			List<IInstructionLine> instructions = method.getInstructionLines();

			/* find selected instruction */
			int targetOffset = ByteCodeConstants.INVALID_OFFSET;
			for(IInstructionLine i: instructions){
				if(i.getLine() == lineNumber){
					if(i.getInstruction() instanceof BranchInstruction){
						BranchInstruction bi = (BranchInstruction)i.getInstruction();

						targetOffset = bi.getBranchOffset() + bi.getOffset();
						
						break;
					}
				}

			}
			
			/* find target instruction */
			for(IInstructionLine i: instructions){
				if(i.getInstruction().getOffset() == targetOffset){
					IDocument document = fTextEditor.getDocumentProvider().getDocument(fTextEditor.getEditorInput()); 
					int destOffset, destLength;
					try {
						destOffset = document.getLineOffset(i.getLine());
						destLength = document.getLineLength(i.getLine());
					} catch (BadLocationException e) {
						return;
					}

					fTextEditor.selectAndReveal(destOffset, destLength - 1);
					break;
				}
			}


		}

	}

	/**
	 * Local Hyperlink class for bytecode visualizer document.
	 * The field within a class file document is assigned to the link.
	 */
	class ClassFileFieldLocalHyperLink implements IHyperlink{

		/**
		 * LInk region.
		 */
		private IRegion region; 

		/**
		 * Text of the marked link.
		 */
		private String text;

		/**
		 * The line number in which the link has been detected.
		 */
		private int lineNumber;

		public ClassFileFieldLocalHyperLink(IRegion region, String text, int lineNumber) {
			super();
			this.region = region;
			this.text = text;
			this.lineNumber = lineNumber;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getHyperlinkRegion()
		 */
		public IRegion getHyperlinkRegion() {
			return region;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getHyperlinkText()
		 */
		public String getHyperlinkText() {
			return text;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getTypeLabel()
		 */
		public String getTypeLabel() {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.hyperlink.IHyperlink#open()
		 */
		public void open() {

			BytecodeDocumentProvider bdp = (BytecodeDocumentProvider)fTextEditor.getDocumentProvider();
			IFieldSection field = bdp.getClassFileDocument().findFieldSection(text);

			IDocument document = fTextEditor.getDocumentProvider().getDocument(fTextEditor.getEditorInput()); 

			int destOffset, destLength, elementIndex, elementLength;;

			try {
				destOffset = document.getLineOffset(field.getBytecodeDocumentLine());
				destLength = document.getLineLength(field.getBytecodeDocumentLine());

				String lineString = document.get(destOffset, destLength);
				elementIndex = lineString.indexOf(" " + text + ";") + 1;
				elementLength = text.length();

			} catch (BadLocationException e) {
				return;
			}		

			fTextEditor.selectAndReveal(destOffset + elementIndex, elementLength);		
		}

	}

	/**
	 * Java Element hypelink class.
	 */
	class MJavaElementHyperlinkDetector extends AbstractHyperlinkDetector {

		/*
		 * @see org.eclipse.jface.text.hyperlink.IHyperlinkDetector#detectHyperlinks(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion, boolean)
		 */
		public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
			ITextEditor textEditor= (ITextEditor)getAdapter(ITextEditor.class);
			if (region == null || !(textEditor instanceof JavaEditor))
				return null;

			IAction openAction= textEditor.getAction("OpenEditor"); //$NON-NLS-1$
			if (!(openAction instanceof SelectionDispatchAction))
				return null;

			int offset= region.getOffset();

			IJavaElement input= EditorUtility.getEditorInputJavaElement(textEditor, false);
			if (input == null)
				return null;

			try {
				IDocument document= textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
				IRegion wordRegion= JavaWordFinder.findWord(document, offset);
				if (wordRegion == null || wordRegion.getLength() == 0)
					return null;


				BytecodeEditor ed = (BytecodeEditor)fTextEditor;
				BytecodeDocumentProvider byteCodeDocumentProvider = (BytecodeDocumentProvider)ed.getDocumentProvider();
				
				//FIXME: byteCodeDocumentProvider.getClassFile() can be null for File Inputs
				IClassFile cf = null;//byteCodeDocumentProvider.getClassFile();
				char[] src = document.get().toCharArray();

				/* change buffer content to Dr.garbage specific */
				IBuffer buf = cf.getBuffer();
				if(buf == null){ /* probably no source attached */
					return null;
				}

				String origSrc = changeBufferContent(buf, src);

				IJavaElement[] elements= null;
				elements= ((ICodeAssist) input).codeSelect(wordRegion.getOffset(), wordRegion.getLength());
				elements= selectOpenableElements(elements);

				/* change buffer content to original */
				changeBufferContent(buf, origSrc.toCharArray());

				if (elements.length == 0)
					return null;

				IHyperlink[] result= new IHyperlink[elements.length];
				for (int i= 0; i < elements.length; i++) {
					result[i]= new JavaElementHyperlink(wordRegion, (SelectionDispatchAction) openAction, elements[i], elements.length > 1);
				}
				return result;
			} catch (JavaModelException e) {
				return null;
			}
		}


		/**
		 * Change content of the buffer and returns the old content as a string.
		 * @param buffer
		 * @param newSrc
		 * @return oldSrc
		 */
		private String changeBufferContent(IBuffer buffer, char[] newSrc) {

			String oldContent = buffer.getContents();

			Method addMethod;
			try {

				if(buffer.isReadOnly()){
					/* make the buffer writable */
					addMethod = buffer.getClass().getDeclaredMethod("setReadOnly", new Class[]{boolean.class});
					addMethod.setAccessible(true);
					addMethod.invoke(buffer, false);

					buffer.setContents(newSrc);

					/* set read only again */
					addMethod.invoke(buffer, true);
				}
				else{
					buffer.setContents(newSrc);		
				}

			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			return oldContent;

		}


		/**
		 * Selects the openable elements out of the given ones.
		 * 
		 * @param elements the elements to filter
		 * @return the openable elements
		 * @since 3.4
		 */
		private IJavaElement[] selectOpenableElements(IJavaElement[] elements) {
			List result= new ArrayList(elements.length);
			for (int i= 0; i < elements.length; i++) {
				IJavaElement element= elements[i];
				switch (element.getElementType()) {
				case IJavaElement.PACKAGE_DECLARATION:
				case IJavaElement.PACKAGE_FRAGMENT:
				case IJavaElement.PACKAGE_FRAGMENT_ROOT:
				case IJavaElement.JAVA_PROJECT:
				case IJavaElement.JAVA_MODEL:
					break;
				default:
					result.add(element);
				break;
				}
			}
			return (IJavaElement[]) result.toArray(new IJavaElement[result.size()]);
		}
	}
}