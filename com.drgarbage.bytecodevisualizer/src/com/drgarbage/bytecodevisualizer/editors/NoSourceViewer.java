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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.SourceAttachmentBlock;
import org.eclipse.jdt.ui.wizards.BuildPathDialogAccess;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.core.CoreMessages;


public class NoSourceViewer implements IPropertyChangeListener {

	private ISourceCodeViewer fSourceCodeViewer;
	private IWorkbenchPartSite fSite;
	private final IClassFile fFile;
	private Composite fComposite;
	private Color fBackgroundColor;
	private Color fForegroundColor;
	private Color fSeparatorColor;
	private List<Label> fBannerLabels= new ArrayList<Label>();
	private List<Label> fHeaderLabels= new ArrayList<Label>();
	private Font fFont;

	/**
	 * Creates a source attachment form for a class file.
	 * 
	 * @param file the class file
	 */
	public NoSourceViewer(IClassFile file, ISourceCodeViewer sourceCodeViewer, IWorkbenchPartSite site) {
		fFile= file;
		fSite = site;
		fSourceCodeViewer = sourceCodeViewer;
	}

	/**
	 * Returns the package fragment root of this file.
	 * 
	 * @param file the class file 
	 * @return the package fragment root of the given class file
	 */
	private IPackageFragmentRoot getPackageFragmentRoot(IClassFile file) {

		IJavaElement element= file.getParent();
		while (element != null && element.getElementType() != IJavaElement.PACKAGE_FRAGMENT_ROOT)
			element= element.getParent();

		return (IPackageFragmentRoot) element;
	}

	/**
	 * Creates the control of the source attachment form.
	 * 
	 * @param parent the parent composite 
	 * @return the creates source attachment form
	 */
	public Control createControl(Composite parent) {

		Display display= parent.getDisplay();
		fBackgroundColor= display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
		fForegroundColor= display.getSystemColor(SWT.COLOR_LIST_FOREGROUND);
		fSeparatorColor= new Color(display, 152, 170, 203);

		JFaceResources.getFontRegistry().addListener(this);

		fComposite= createComposite(parent);
		fComposite.setLayout(new GridLayout());
		fComposite.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				JFaceResources.getFontRegistry().removeListener(NoSourceViewer.this);
				fComposite= null;
				fSeparatorColor.dispose();
				fSeparatorColor= null;
				fBannerLabels.clear();
				fHeaderLabels.clear();
				if (fFont != null) {
					fFont.dispose();
					fFont= null;
				}
			}
		});

		createTitleLabel(fComposite, CoreMessages.SourceCodeUnavailable);
		createLabel(fComposite, null);

		Composite separator= createCompositeSeparator(fComposite);
		GridData data= new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint= 2;
		separator.setLayoutData(data);

		if(fFile == null){
			return fComposite;			
		}
		
		try {
			IPackageFragmentRoot root= getPackageFragmentRoot(fFile);
			if (root != null) {
				createSourceAttachmentControls(fComposite, root);
			}
		} catch (JavaModelException e) {
			String title= CoreMessages.ErrorOpenSource;
			String message= CoreMessages.ErrorOpenSourceMessage;
			ExceptionHandler.handle(e, fComposite.getShell(), title, message);//TODO: define own log
		}
		
		return fComposite;
	}

	private void createSourceAttachmentControls(Composite composite, IPackageFragmentRoot root) throws JavaModelException {
		IClasspathEntry entry;
		try {
			entry= root.getRawClasspathEntry();
		} catch (JavaModelException ex) {
			if (ex.isDoesNotExist())
				entry= null;
			else
				throw ex;
		}
		IPath containerPath= null;

		if (entry == null || root.getKind() != IPackageFragmentRoot.K_BINARY) {
			String s = CoreMessages.SourceAttachmentForm_message_noSource;
			createLabel(composite, MessageFormat.format(s, new Object[] {fFile.getElementName()}));
			return;
		}

		IJavaProject jproject= root.getJavaProject();
		if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
			containerPath= entry.getPath();
			ClasspathContainerInitializer initializer= JavaCore.getClasspathContainerInitializer(containerPath.segment(0));
			IClasspathContainer container= JavaCore.getClasspathContainer(containerPath, jproject);
			if (initializer == null || container == null) {
				createLabel(composite, MessageFormat.format(CoreMessages.SourceAttachmentForm_cannotconfigure, new Object[] {containerPath.toString()})); 
				return;
			}
			String containerName= container.getDescription();
			IStatus status= initializer.getSourceAttachmentStatus(containerPath, jproject);
			if (status.getCode() == ClasspathContainerInitializer.ATTRIBUTE_NOT_SUPPORTED) {
				createLabel(composite, MessageFormat.format(CoreMessages.SourceAttachmentForm_notsupported, new Object[] {containerName}));  
				return;
			}
			if (status.getCode() == ClasspathContainerInitializer.ATTRIBUTE_READ_ONLY) {
				createLabel(composite, MessageFormat.format(CoreMessages.SourceAttachmentForm_readonly, new Object[] {containerName}));  
				return;
			}
			entry= JavaModelUtil.findEntryInContainer(container, root.getPath());
			Assert.isNotNull(entry);
		}


		Button button;
		
		String msg = null;
		String btnText = null;

		IPath path= entry.getSourceAttachmentPath();
		if (path == null || path.isEmpty()) {
			msg = MessageFormat.format(CoreMessages.SourceAttachmentForm_message_noSourceAttachment, new Object[] {root.getElementName()});
			btnText = CoreMessages.SourceAttachmentForm_button_attachSource;
		} else {
			msg = MessageFormat.format(CoreMessages.SourceAttachmentForm_message_noSourceInAttachment, new Object[] {fFile.getElementName()});
			btnText = CoreMessages.SourceAttachmentForm_button_changeAttachedSource;
		}
		
		createLabel(composite, msg);
		createLabel(composite, CoreMessages.SourceAttachmentForm_message_pressButtonToAttach);
		createLabel(composite, null);
		
		button= createButton(composite, btnText);
		button.addSelectionListener(createButtonListener(entry, containerPath, jproject));
	}

	private SelectionListener createButtonListener(final IClasspathEntry entry, final IPath containerPath, final IJavaProject jproject) {
		return new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				Shell shell= fSite.getShell();
				try {
					IClasspathEntry result= BuildPathDialogAccess.configureSourceAttachment(shell, entry);
					if (result != null) {
						
						/* 3.4, 3.5 version */
//						applySourceAttachment(shell, result, jproject, containerPath);
						
						/* 3.6 version */
						applySourceAttachment(shell, result, jproject, containerPath, entry.getReferencingEntry() != null);
						
						if (fSourceCodeViewer != null) {
							fSourceCodeViewer.verifyInput();
						}

						com.drgarbage.utils.Messages.info(CoreMessages.RestartEditorMessage);
					}
				} catch (Throwable e) {
					BytecodeVisualizerPlugin.log(e);
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		};
	}

/* 3.4, 3.5 version */

//	protected void applySourceAttachment(Shell shell, IClasspathEntry newEntry, IJavaProject project, IPath containerPath) {
//		try {
//			IRunnableWithProgress runnable= SourceAttachmentBlock.getRunnable(shell, newEntry, project, containerPath);
//			PlatformUI.getWorkbench().getProgressService().run(true, true, runnable);
//
//		} catch (InvocationTargetException e) {
//			String title= CoreMessages.ErrorOpenSource;
//			String message= CoreMessages.ErrorOpenSourceMessage;
//			ExceptionHandler.handle(e, fComposite.getShell(), title, message);//TODO: define own log
//
//		} catch (InterruptedException e) {
//			/* Canceled */ 
//		}
//	}


/* 3.6 version*/	
	protected void applySourceAttachment(Shell shell, IClasspathEntry newEntry, IJavaProject project, IPath containerPath, boolean isReferencedEntry) {
		try {
			IRunnableWithProgress runnable= SourceAttachmentBlock.getRunnable(shell, newEntry, project, containerPath, isReferencedEntry);
			PlatformUI.getWorkbench().getProgressService().run(true, true, runnable);

		} catch (InvocationTargetException e) {
			String title= CoreMessages.ErrorOpenSource;
			String message= CoreMessages.ErrorOpenSourceMessage;
			ExceptionHandler.handle(e, fComposite.getShell(), title, message);//TODO: define own log

		} catch (InterruptedException e) {
			/* Canceled */ 
		}
	}

	/*
	 * @see IPropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {

		for (Label l : fBannerLabels) {
			l.setFont(JFaceResources.getBannerFont());
		}

		for (Label l : fHeaderLabels) {
			l.setFont(JFaceResources.getHeaderFont());
		}
		

		fComposite.layout(true);
		fComposite.redraw();
	}

	// --- copied from org.eclipse.update.ui.forms.internal.FormWidgetFactory

	private Composite createComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackground(fBackgroundColor);
		return composite;
	}

	private Composite createCompositeSeparator(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackground(fSeparatorColor);
		return composite;
	}

	private Label createLabel(Composite parent, String text) {
		Label label= new Label(parent, SWT.WRAP);
		if (text != null)
			label.setText(text);
		label.setBackground(fBackgroundColor);
		label.setForeground(fForegroundColor);
		GridData gd= new GridData(SWT.FILL, SWT.FILL, true, false);
		label.setLayoutData(gd);
		return label;
	}

	private Label createTitleLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.NONE);
		if (text != null)
			label.setText(text);
		label.setBackground(fBackgroundColor);
		label.setForeground(fForegroundColor);
		label.setFont(JFaceResources.getHeaderFont());
		fHeaderLabels.add(label);
		return label;
	}

	private Button createButton(Composite parent, String text) {
		Button button = new Button(parent, SWT.FLAT);
		button.setBackground(fBackgroundColor);
		button.setForeground(fForegroundColor);
		if (text != null)
			button.setText(text);
		return button;
	}

}	

