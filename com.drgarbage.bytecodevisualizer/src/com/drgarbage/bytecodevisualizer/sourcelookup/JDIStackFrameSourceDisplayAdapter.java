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

package com.drgarbage.bytecodevisualizer.sourcelookup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.internal.ui.InstructionPointerManager;
import org.eclipse.debug.internal.ui.elements.adapters.StackFrameSourceDisplayAdapter;
import org.eclipse.debug.internal.ui.sourcelookup.SourceLookupResult;
import org.eclipse.debug.internal.ui.views.launch.DecorationManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.sourcelookup.ISourceDisplay;
import org.eclipse.debug.ui.sourcelookup.ISourceLookupResult;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.progress.UIJob;

import com.drgarbage.bytecodevisualizer.editors.JDIEditorInput;
import com.drgarbage.core.CoreConstants;

/**
 * @since 3.2
 */
public class JDIStackFrameSourceDisplayAdapter implements ISourceDisplay {
	
	private static class JDISourceLookupResult implements ISourceLookupResult {
		private ISourceLookupResult delegate;

		public JDISourceLookupResult(ISourceLookupResult delegate) {
			super();
			this.delegate = delegate;
		}

		public boolean equals(Object obj) {
			return delegate.equals(obj);
		}

		public Object getArtifact() {
			return delegate.getArtifact();
		}

		public String getEditorId() {
			return delegate.getEditorId();
		}

		public IEditorInput getEditorInput() {
			
			if (CoreConstants.BYTECODE_VISUALIZER_EDITOR_ID.equals(delegate.getEditorId())
					&& getArtifact() instanceof JDIStackFrame) {
				/* only Bytecode Visualizer editor will be handled specially */
				return new JDIEditorInput((JDIStackFrame) getArtifact(), delegate.getEditorInput());
			}
			else {
				/* do not mislead other editors */
				return delegate.getEditorInput();
			}
		}

		public Object getSourceElement() {
			return delegate.getSourceElement();
		}

		public int hashCode() {
			return delegate.hashCode();
		}

		public String toString() {
			return delegate.toString();
		}

		public void updateArtifact(Object artifact) {
			if (delegate instanceof SourceLookupResult) {
				((SourceLookupResult)delegate).updateArtifact(artifact);
			}
		}
		
	}
	
	class SourceDisplayJob extends UIJob {
		
		private IWorkbenchPage fPage;
		private JDISourceLookupResult fResult;

		public SourceDisplayJob(JDISourceLookupResult result, IWorkbenchPage page) {
			super("Debug Source Display");  //$NON-NLS-1$
			setSystem(true);
			setPriority(Job.INTERACTIVE);
			fResult = result;
			fPage = page;
		}
		

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#belongsTo(java.lang.Object)
		 */
		public boolean belongsTo(Object family) {
			// source display jobs are a family per workbench page
			if (family instanceof SourceDisplayJob) {
				SourceDisplayJob sdj = (SourceDisplayJob) family;
				return sdj.fPage.equals(fPage);
			}
			return false;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
		 */
		public IStatus runInUIThread(IProgressMonitor monitor) {
			if (!monitor.isCanceled() && fResult != null) {
				DebugUITools.displaySource(fResult, fPage);
				// termination may have occurred while displaying source
				if (monitor.isCanceled()) {
					Object artifact = fResult.getArtifact();
					if (artifact instanceof IStackFrame) {
						clearSourceSelection(((IStackFrame)artifact).getThread());
					}
				}
			}
			
			return Status.OK_STATUS;
		}
		
	}

	/**
	 * A job to perform source lookup on the currently selected stack frame.
	 */
	class SourceLookupJob extends Job {
		
		private ISourceLocator fLocator;
		private IWorkbenchPage fPage;
		private IStackFrame fTarget;

		/**
		 * Constructs a new source lookup job.
		 */
		public SourceLookupJob(IStackFrame frame, ISourceLocator locator, IWorkbenchPage page) {
			super("Debug Source Lookup");  //$NON-NLS-1$
			setPriority(Job.INTERACTIVE);
			setSystem(true);	
			fTarget = frame;
			fLocator = locator;
			fPage = page;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#belongsTo(java.lang.Object)
		 */
		public boolean belongsTo(Object family) {
			// source lookup jobs are a family per workbench page
			if (family instanceof SourceLookupJob) {
				SourceLookupJob slj = (SourceLookupJob) family;
				return slj.fPage.equals(fPage);
			}
			return false;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		protected IStatus run(IProgressMonitor monitor) {
			if (!monitor.isCanceled()) {				
				if (!fTarget.isTerminated()) {
					JDISourceLookupResult result = new JDISourceLookupResult(DebugUITools.lookupSource(fTarget, fLocator));
					synchronized (JDIStackFrameSourceDisplayAdapter.this) {
						fPrevResult = result;
						fPrevFrame = fTarget;
					}
					if (!monitor.isCanceled() && !fTarget.isTerminated()) {
						new SourceDisplayJob(result, fPage).schedule();
					}
				}
			}
			return Status.OK_STATUS;
		}
		
	}
	private static StackFrameSourceDisplayAdapter compatibilityDelegate = new StackFrameSourceDisplayAdapter();
	
	private IStackFrame fPrevFrame;
	
	private JDISourceLookupResult fPrevResult;
	
	/**
	 * Constructs singleton source display adapter for stack frames.
	 */
	public JDIStackFrameSourceDisplayAdapter() {
		DebugPlugin.getDefault().addDebugEventListener(new IDebugEventSetListener() {
			public void handleDebugEvents(DebugEvent[] events) {
				for (int i = 0; i < events.length; i++) {
					final DebugEvent event = events[i];
					switch (event.getKind()) {
						case DebugEvent.TERMINATE:
							clearCachedModel(event.getSource());
							// fall through
						case DebugEvent.RESUME:
							if (!event.isEvaluation()) {
								Job uijob = new UIJob("clear source selection"){ //$NON-NLS-1$
									public IStatus runInUIThread(
											IProgressMonitor monitor) {
										clearSourceSelection(event.getSource());
										return Status.OK_STATUS;
									}
									
								};
								uijob.setSystem(true);
								uijob.schedule();
							}
							break;
						case DebugEvent.CHANGE:
							if (event.getSource() instanceof IStackFrame) {
								if (event.getDetail() == DebugEvent.CONTENT) {
									// force source lookup if a stack frame fires a content change event
									clearCachedModel(event.getSource());
								}
							}
							break;
					}
				}
			}
		});
	}	

	/**
	 * Clear any cached results associated with the given object.
	 * 
	 * @param source
	 */
	private synchronized void clearCachedModel(Object source) {
		if (fPrevFrame != null) {
			IDebugTarget target = null;
			if (source instanceof IDebugElement) {
				target = ((IDebugElement)source).getDebugTarget();
			}
			if (fPrevFrame.getDebugTarget().equals(target)) {
				fPrevFrame = null;
				fPrevResult = null;
			}
		}
	}
	
	/**
	 * Clears any source decorations associated with the given thread or
	 * debug target.
	 * 
	 * @param source thread or debug target
	 */
	private void clearSourceSelection(Object source) {		
		if (source instanceof IThread) {
			IThread thread = (IThread)source;
			DecorationManager.removeDecorations(thread);
			InstructionPointerManager.getDefault().removeAnnotations(thread);
		} else if (source instanceof IDebugTarget) {
			IDebugTarget target = (IDebugTarget)source;
			DecorationManager.removeDecorations(target);
			InstructionPointerManager.getDefault().removeAnnotations(target);
		}
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.ISourceDisplayAdapter#displaySource(java.lang.Object, org.eclipse.ui.IWorkbenchPage, boolean)
	 */
	public synchronized void displaySource(Object context, IWorkbenchPage page, boolean force) {
		
		if (context instanceof JDIStackFrame) {
			JDIStackFrame frame = (JDIStackFrame)context;
			if (!force && frame.equals(fPrevFrame)) {
				fPrevResult.updateArtifact(context);
				SourceDisplayJob sdj = new SourceDisplayJob(fPrevResult, page);
				// cancel any existing source display jobs for this page
				Job.getJobManager().cancel(sdj);
				sdj.schedule();
			} else {
				SourceLookupJob slj = new SourceLookupJob(frame, frame.getLaunch().getSourceLocator(), page);
				// cancel any existing source lookup jobs for this page
				Job.getJobManager().cancel(slj);
				slj.schedule();
			}
		}
		else {
			/* this should actually never happen
			 * as we registered this adapter only for JDIStackFrame.
			 * We are doing this just to be sure */
			compatibilityDelegate.displaySource(context, page, force);
		}
		
	}
	
}
