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

package com.drgarbage.bytecodevisualizer;

import java.lang.reflect.Field;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.internal.debug.core.IJDIEventListener;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugModelMessages;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIMethod;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.drgarbage.core.CoreConstants;
import com.drgarbage.core.CorePlugin;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.StepRequest;

/**
 * Debug support routines.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
@SuppressWarnings("restriction")
public class DebugSupport {
	
	/**
	 * Own step over action.
	 */
	public synchronized void stepOver(){

		Object o = DebugUITools.getDebugContext();

		/* the object is null if the debugger is not active */
		if(o instanceof JDIStackFrame){
			JDIStackFrame stackFrame = (JDIStackFrame)o; 
			try {
				final JDIThread jdiThread = (JDIThread) stackFrame.getThread();

				StepBytecodeHandler handler = new StepBytecodeHandler(jdiThread);
				handler.step();

			} catch (DebugException e) {
				exceptionHandler(e);
			} catch (SecurityException e) {
				exceptionHandler(e);
			} catch (IllegalArgumentException e) {
				exceptionHandler(e);
			} catch (IncompatibleThreadStateException e) {
				exceptionHandler(e);
			}
		}
	}

	
	/**
	 * Own step into action.
	 */
	public synchronized void stepInto(){
		Object o = DebugUITools.getDebugContext();

		/* the object is null if the debugger is not active */
		if(o instanceof JDIStackFrame){
			JDIStackFrame stackFrame = (JDIStackFrame)o; 
			try {
				stackFrame.stepInto();

			} catch (DebugException e) {
				exceptionHandler(e);
			} catch (SecurityException e) {
				exceptionHandler(e);
			} catch (IllegalArgumentException e) {
				exceptionHandler(e);
			}
		}
	}

	/**
	 * Exception handler.
	 * @param e
	 */
	private void exceptionHandler(Exception e){
		CorePlugin.getDefault().getLog().log(
				new Status(IStatus.ERROR, CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID, e.getMessage(), e)
				);
	}
	
	/**
	 * Handler class to perform single bytecode stepping.
	 */
	class StepBytecodeHandler implements IJDIEventListener {
		
		private JDIThread jdiThread;
		
		/**
		 * The JDI Location from which an original user-requested step began.
		 */
		private Location fOriginalStepLocation;
		
		protected void setOriginalStepLocation(Location location) {
			fOriginalStepLocation = location;
		}

		protected Location getOriginalStepLocation() {
			return fOriginalStepLocation;
		}
		
		public StepBytecodeHandler(JDIThread jdiThread) {
			super();
			this.jdiThread = jdiThread;
		}

		/**
		 * Request for stepping in the underlying VM
		 */
		private StepRequest fStepRequest;

		/**
		 * Initiates a step in the underlying VM by creating a step request of
		 * the appropriate kind (over, into, return), and resuming this thread.
		 * When a step is initiated it is registered with its thread as a
		 * pending step. A pending step could be cancelled if a breakpoint
		 * suspends execution during the step.
		 * <p>
		 * This thread's state is set to running and stepping, and stack frames
		 * are invalidated (but preserved to be re-used when the step
		 * completes). A resume event with a step detail is fired for this
		 * thread.
		 * </p>
		 * Note this method does nothing if this thread has no stack frames.
		 * 
		 * @exception DebugException
		 *                if this method fails. Reasons include:
		 *                <ul>
		 *                <li>Failure communicating with the VM. The
		 *                DebugException's status code contains the underlying
		 *                exception responsible for the failure.</li>
		 *                </ul>
		 * @throws IncompatibleThreadStateException 
		 */
		protected void step() throws DebugException, IncompatibleThreadStateException {
			ISchedulingRule rule = jdiThread.getThreadRule();
			try {
				Job.getJobManager().beginRule(rule, null);
				JDIStackFrame top = (JDIStackFrame) jdiThread.getTopStackFrame();
				if (top == null) {
					return;
				}
				
				int i = jdiThread.getUnderlyingThread().frameCount();
				if(i == 0){
					return;
				}
				Location location = jdiThread.getUnderlyingThread().frames().get(i - 1).location();
				setOriginalStepLocation(location);
				setStepRequest(createStepRequest());
				jdiThread.addJDIEventListener(this, getStepRequest());
				jdiThread.fireResumeEvent(DebugEvent.STEP_OVER);
				invokeThread();
			} finally {
				Job.getJobManager().endRule(rule);
			}
		}

		/**
		 * Resumes the underlying thread to initiate the step. By default the
		 * thread is resumed. Step handlers that require other actions can
		 * override this method.
		 * 
		 * @exception DebugException
		 *                if this method fails. Reasons include:
		 *                <ul>
		 *                <li>Failure communicating with the VM. The
		 *                DebugException's status code contains the underlying
		 *                exception responsible for the failure.</li>
		 *                </ul>
		 */
		protected void invokeThread() throws DebugException {
			try {
				jdiThread.getUnderlyingThread().resume();
			} catch (RuntimeException e) {
				stepEnd(null);
				jdiThread.fireSuspendEvent(DebugEvent.STEP_END);
				jdiThread.targetRequestFailed(MessageFormat.format(
						JDIDebugModelMessages.JDIThread_exception_stepping,
						e.toString()), e);
			}
		}

		/**
		 * Creates and returns a step request specific to this step handler.
		 * Subclasses must override <code>getStepKind()</code> to return the
		 * kind of step it implements.
		 * 
		 * @return step request
		 * @exception DebugException
		 *                if this method fails. Reasons include:
		 *                <ul>
		 *                <li>Failure communicating with the VM. The
		 *                DebugException's status code contains the underlying
		 *                exception responsible for the failure.</li>
		 *                </ul>
		 */
		protected StepRequest createStepRequest() throws DebugException {
			return createStepRequest(StepRequest.STEP_OVER);
		}

		/**
		 * Creates and returns a step request of the specified kind.
		 * 
		 * @param kind
		 *            of <code>StepRequest.STEP_INTO</code>,
		 *            <code>StepRequest.STEP_OVER</code>,
		 *            <code>StepRequest.STEP_OUT</code>
		 * @return step request
		 * @exception DebugException
		 *                if this method fails. Reasons include:
		 *                <ul>
		 *                <li>Failure communicating with the VM. The
		 *                DebugException's status code contains the underlying
		 *                exception responsible for the failure.</li>
		 *                </ul>
		 */
		protected StepRequest createStepRequest(int kind) throws DebugException {
			EventRequestManager manager = jdiThread.getEventRequestManager();
			if (manager == null) {
				jdiThread.requestFailed(
						JDIDebugModelMessages.JDIThread_Unable_to_create_step_request___VM_disconnected__1,
						null);
			}
			try {
				StepRequest request = manager.createStepRequest(jdiThread.getUnderlyingThread(),
						StepRequest.STEP_MIN, kind);
				request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
				request.addCountFilter(1);
				attachFiltersToStepRequest(request);
				request.enable();
				return request;
			} catch (RuntimeException e) {
				jdiThread.targetRequestFailed(
						MessageFormat.format(
								JDIDebugModelMessages.JDIThread_exception_creating_step_request,
								e.toString()), e);
			}
			// this line will never be executed, as the try block
			// will either return, or the catch block will throw
			// an exception
			return null;

		}

		/**
		 * Sets the step request created by this handler in the underlying VM.
		 * Set to <code>null<code> when
		 * this handler deletes its request.
		 * 
		 * @param request
		 *            step request
		 */
		protected void setStepRequest(StepRequest request) {
			fStepRequest = request;
		}

		/**
		 * Returns the step request created by this handler in the underlying
		 * VM.
		 * 
		 * @return step request
		 */
		protected StepRequest getStepRequest() {
			return fStepRequest;
		}

		/**
		 * Deletes this handler's step request from the underlying VM and
		 * removes this handler as an event listener.
		 */
		protected void deleteStepRequest() {
			try {
				StepRequest req = getStepRequest();
				if (req != null) {
					jdiThread.removeJDIEventListener(this, req);
					EventRequestManager manager = jdiThread.getEventRequestManager();
					if (manager != null) {
						manager.deleteEventRequest(req);
					}
				}
			} catch (RuntimeException e) {
				exceptionHandler(e);
			} finally {
				setStepRequest(null);
			}
		}

		/**
		 * Constant for the name of the default Java stratum
		 */
		private static final String JAVA_STRATUM_CONSTANT = "Java"; //$NON-NLS-1$
		
		/**
		 * If step filters are currently switched on and the current location is
		 * not a filtered location, set all active filters on the step request.
		 * 
		 * @param request
		 *            the request to augment
		 */
		protected void attachFiltersToStepRequest(StepRequest request) {

			if (applyStepFilters() && jdiThread.isStepFiltersEnabled()) {
				Location currentLocation = getOriginalStepLocation();
				if (currentLocation == null
						|| !JAVA_STRATUM_CONSTANT.equals(currentLocation
								.declaringType().defaultStratum())) {
					return;
				}
				// Removed the fix for bug 5587, to address bug 41510
				// //check if the user has already stopped in a filtered
				// location
				// //is so do not filter @see bug 5587
				// ReferenceType type= currentLocation.declaringType();
				// String typeName= type.name();
				String[] activeFilters = jdiThread.getJavaDebugTarget().getStepFilters();
				if (activeFilters != null) {
					for (String activeFilter : activeFilters) {
						request.addClassExclusionFilter(activeFilter);
					}
				}
			}
		}

		/**
		 * Returns whether this step handler should use step filters when
		 * creating its step request. By default, step filters can be used by
		 * any step request. Subclasses must override if/when required.
		 * 
		 * @return whether this step handler should use step filters when
		 *         creating its step request
		 */
		protected boolean applyStepFilters() {
			return true;
		}

		/**
		 * Notification the step request has completed. If the current location
		 * matches one of the user-specified step filter criteria (e.g.,
		 * synthetic methods, static initializers), then continue stepping.
		 * 
		 * @see IJDIEventListener#handleEvent(Event, JDIDebugTarget, boolean,
		 *      EventSet)
		 */
		public boolean handleEvent(Event event, JDIDebugTarget target,
				boolean suspendVote, EventSet eventSet) {
			try {
				StepEvent stepEvent = (StepEvent) event;
				Location currentLocation = stepEvent.location();


				// if the ending step location is filtered and we did not start
				// from
				// a filtered location, or if we're back where
				// we started on a step into, do another step of the same kind
				if (locationShouldBeFiltered(currentLocation)) {
					deleteStepRequest();
					createSecondaryStepRequest();
					return true;
					// otherwise, we're done stepping
				}
				stepEnd(eventSet);
				
				/* refresh the stack frame view */
				jdiThread.computeNewStackFrames();
			
				/* refresh variables */
				updateVariable();
				
				return false;
			} catch (DebugException e) {
				exceptionHandler(e);
				stepEnd(eventSet);
				return false;
			}
		}

		/**
		 * Refresh variables. Not really good solution, but works.
		 */
		private void updateVariable(){
			try {
				JDIStackFrame top = (JDIStackFrame) jdiThread.getTopStackFrame();

				Class<?> jdiStackFrameClass = top.getClass();	
				Field req = jdiStackFrameClass.getDeclaredField("fRefreshVariables");

				req.setAccessible(true);
				if(!req.getBoolean(top)){				
					req.setBoolean(top, true);
				}
				req.setAccessible(false);
				
				/* update variables by calling hasVariable() method */
				top.hasVariables();

			} catch (SecurityException e) {
				exceptionHandler(e);
			} catch (NoSuchFieldException e) {
				exceptionHandler(e);
			} catch (IllegalArgumentException e) {
				exceptionHandler(e);
			} catch (IllegalAccessException e) {
				exceptionHandler(e);
			} catch (DebugException e) {
				exceptionHandler(e);
			}
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jdt.internal.debug.core.IJDIEventListener#eventSetComplete
		 * (com.sun.jdi.event.Event,
		 * org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget, boolean)
		 */
		public void eventSetComplete(Event event, JDIDebugTarget target,
				boolean suspend, EventSet eventSet) {
			// do nothing
		}

		/**
		 * Returns <code>true</code> if the StepEvent's Location is a Method
		 * that the user has indicated (via the step filter preferences) should
		 * be filtered and the step was not initiated from a filtered location.
		 * Returns <code>false</code> otherwise.
		 * 
		 * @param location
		 *            the location to check
		 * @return if the given {@link Location} should be filtered
		 * @throws DebugException
		 *             if an exception occurs
		 */
		protected boolean locationShouldBeFiltered(Location location)
				throws DebugException {
			if (applyStepFilters()) {
				Location origLocation = getOriginalStepLocation();
				if (origLocation != null) {
					return !locationIsFiltered(origLocation.method())
							&& locationIsFiltered(location.method());
				}
			}
			return false;
		}

		/**
		 * Returns <code>true</code> if the StepEvent's Location is a Method
		 * that the user has indicated (via the step filter preferences) should
		 * be filtered. Returns <code>false</code> otherwise.
		 * 
		 * @param method
		 *            the {@link Method} location to check
		 * @return <code>true</code> if the {@link Method} {@link Location}
		 *         should be filtered, <code>false</code> otherwise
		 */
		protected boolean locationIsFiltered(Method method) {
			if (jdiThread.isStepFiltersEnabled()) {
				boolean filterStatics = jdiThread.getJavaDebugTarget()
						.isFilterStaticInitializers();
				boolean filterSynthetics = jdiThread.getJavaDebugTarget()
						.isFilterSynthetics();
				boolean filterConstructors = jdiThread.getJavaDebugTarget()
						.isFilterConstructors();
				boolean filterSetters = jdiThread.getJavaDebugTarget().isFilterSetters();
				boolean filterGetters = jdiThread.getJavaDebugTarget().isFilterGetters();
				if (!(filterStatics || filterSynthetics || filterConstructors
						|| filterGetters || filterSetters)) {
					return false;
				}

				if ((filterStatics && method.isStaticInitializer())
						|| (filterSynthetics && method.isSynthetic())
						|| (filterConstructors && method.isConstructor())
						|| (filterGetters && JDIMethod.isGetterMethod(method))
						|| (filterSetters && JDIMethod.isSetterMethod(method))) {
					return true;
				}
			}

			return false;
		}

		/**
		 * Cleans up when a step completes.
		 * <ul>
		 * <li>Thread state is set to suspended.</li>
		 * <li>Stepping state is set to false</li>
		 * <li>Stack frames and variables are incrementally updated</li>
		 * <li>The step request is deleted and removed as and event listener</li>
		 * <li>A suspend event is fired</li>
		 * </ul>
		 * 
		 * @param set
		 *            the remaining {@link EventSet} to queue
		 */
		protected void stepEnd(EventSet set) {
			deleteStepRequest();
			if (set != null) {
				jdiThread.queueSuspendEvent(DebugEvent.STEP_END, set);
			}
		}

		/**
		 * Creates another step request in the underlying thread of the
		 * appropriate kind (over, into, return). This thread will be resumed by
		 * the event dispatcher as this event handler will vote to resume
		 * suspended threads. When a step is initiated it is registered with its
		 * thread as a pending step. A pending step could be cancelled if a
		 * breakpoint suspends execution during the step.
		 * 
		 * @exception DebugException
		 *                if this method fails. Reasons include:
		 *                <ul>
		 *                <li>Failure communicating with the VM. The
		 *                DebugException's status code contains the underlying
		 *                exception responsible for the failure.</li>
		 *                </ul>
		 */
		protected void createSecondaryStepRequest() throws DebugException {
			createSecondaryStepRequest(StepRequest.STEP_OVER);
		}

		/**
		 * Creates another step request in the underlying thread of the
		 * specified kind (over, into, return). This thread will be resumed by
		 * the event dispatcher as this event handler will vote to resume
		 * suspended threads. When a step is initiated it is registered with its
		 * thread as a pending step. A pending step could be cancelled if a
		 * breakpoint suspends execution during the step.
		 * 
		 * @param kind
		 *            of <code>StepRequest.STEP_INTO</code>,
		 *            <code>StepRequest.STEP_OVER</code>,
		 *            <code>StepRequest.STEP_OUT</code>
		 * @exception DebugException
		 *                if this method fails. Reasons include:
		 *                <ul>
		 *                <li>Failure communicating with the VM. The
		 *                DebugException's status code contains the underlying
		 *                exception responsible for the failure.</li>
		 *                </ul>
		 */
		protected void createSecondaryStepRequest(int kind)
				throws DebugException {
			setStepRequest(createStepRequest(kind));
			jdiThread.addJDIEventListener(this, getStepRequest());
		}

		/**
		 * Aborts this step request if active. The step event request is deleted
		 * from the underlying VM.
		 */
		protected void abort() {
			if (getStepRequest() != null) {
				deleteStepRequest();
			}
		}
	}

}
