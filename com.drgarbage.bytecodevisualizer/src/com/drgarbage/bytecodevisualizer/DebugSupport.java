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
import java.util.HashSet;
import java.util.Hashtable;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdi.internal.VirtualMachineImpl;
import org.eclipse.jdi.internal.request.EventRequestManagerImpl;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.drgarbage.core.CoreConstants;
import com.drgarbage.core.CorePlugin;
import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.DuplicateRequestException;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.StepRequest;

/**
 * Debug support routines.
 * 
 * @author Sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: DebugSupport.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
@SuppressWarnings("restriction")
public class DebugSupport {

	/**
	 * Own implementation of the Request event manager. 
	 */
	private final class PrivateEventRequestManagerImpl extends EventRequestManagerImpl {

		/* Request counter for step-into action.        */
		/* Step into action invokes always two requests */
		private int requestCounter = 0;
		
		/**
		 * Constructor.
		 * @param vmImpl
		 */
		public PrivateEventRequestManagerImpl(VirtualMachineImpl vmImpl) {
			super(vmImpl);
		}

		/**
		 * Creates StepRequest.
		 */ 
		public StepRequest createStepRequest(ThreadReference thread, int size, int depth) throws DuplicateRequestException, ObjectCollectedException {

			if(isStepOver()){
				return super.createStepRequest(thread, StepRequest.STEP_MIN, depth);
			}
			else if(isStepInto()){
				/* disable min step after second request */
				requestCounter++;
				if(requestCounter >= 2){
					requestCounter = 0;
					setStepInto(false);
				}
				return super.createStepRequest(thread, StepRequest.STEP_MIN, depth);
			}
			else { 
				return super.createStepRequest(thread, size, depth);
			}
		} 
	}	

	private VirtualMachineImpl currentVM;
	private boolean stepOver = false;
	
	private boolean stepInto = false;

	/**
	 * Creates new Event manager and assigned it to the current VM. 
	 * 
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	private void createEventManager() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {

		/* create new Event Manager*/
		PrivateEventRequestManagerImpl mgr2 = new PrivateEventRequestManagerImpl(currentVM);

		/* set new Manager */
		replaceEventManager(currentVM, mgr2);
	}
	
	/**
	 * This is a hack to replace the eclipse Event Manager by own one.
	 *      
	 * @param vmImpl
	 * @param newEventManager
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private synchronized void replaceEventManager( VirtualMachineImpl vmImpl, EventRequestManagerImpl newEventManager) 
	throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		
		EventRequestManagerImpl oldEventManager = vmImpl.eventRequestManagerImpl();		
		copyRequests(oldEventManager, newEventManager);

		/* replace manager */
		Class<? extends VirtualMachineImpl> cl = vmImpl.getClass();

		Field fld = cl.getDeclaredField("fEventReqMgr");
		fld.setAccessible(true);
		fld.set(vmImpl, newEventManager);
		fld.setAccessible(false);
	}
	
	private void copyRequests(EventRequestManager oldEventManager, EventRequestManagerImpl newEventManager) 
	throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
	
		/* alternative initialization of request list */
//		/* Initialize list of requests.*/
//		HashSet[] fRequests = new HashSet[MONITOR_WAIT_INDEX + 1];
//		for (int i = 0; i < fRequests.length; i++)
//			fRequests[i] = new HashSet();
//
//		/** Set of all existing requests per request type. */
//		List<EventRequest> requestList = oldEventManager.accessWatchpointRequests();
//		for(EventRequest req: requestList){
//			fRequests[ACCESS_WATCHPOINT_INDEX].add(req);
//		}
//		
//		requestList = oldEventManager.breakpointRequests();
//		for(EventRequest req: requestList){
//			fRequests[BREAKPOINT_INDEX].add(req);
//		}
//		
//		requestList = oldEventManager.classPrepareRequests();
//		for(EventRequest req: requestList){
//			fRequests[CLASS_PREPARE_INDEX].add(req);
//		}	
//
//		requestList = oldEventManager.classUnloadRequests();
//		for(EventRequest req: requestList){
//			fRequests[CLASS_UNLOAD_INDEX].add(req);
//		}
//
//		requestList = oldEventManager.exceptionRequests();
//		for(EventRequest req: requestList){
//			fRequests[EXCEPTION_INDEX].add(req);
//		}
//
//		requestList = oldEventManager.methodEntryRequests();
//		for(EventRequest req: requestList){
//			fRequests[METHOD_ENTRY_INDEX].add(req);
//		}
//
//		requestList = oldEventManager.methodExitRequests();
//		for(EventRequest req: requestList){
//			fRequests[METHOD_EXIT_INDEX].add(req);
//		}
//
//		requestList = oldEventManager.modificationWatchpointRequests();
//		for(EventRequest req: requestList){
//			fRequests[MODIFICATION_WATCHPOINT_INDEX].add(req);
//		}
//
//		requestList = oldEventManager.stepRequests();
//		for(EventRequest req: requestList){
//			fRequests[STEP_INDEX].add(req);
//		}
//
//		requestList = oldEventManager.threadDeathRequests();
//		for(EventRequest req: requestList){
//			fRequests[THREAD_DEATH_INDEX].add(req);
//		}
//
//		requestList = oldEventManager.threadStartRequests();
//		for(EventRequest req: requestList){
//			fRequests[THREAD_START_INDEX].add(req);
//		}
//
//		requestList = oldEventManager.vmDeathRequests();
//		for(EventRequest req: requestList){
//			fRequests[VM_DEATH_INDEX].add(req);
//		}
//
//		requestList = oldEventManager.monitorContendedEnteredRequests();
//		for(EventRequest req: requestList){
//			fRequests[MONITOR_CONTENDED_ENTERED_INDEX].add(req);
//		}
//
//		requestList = oldEventManager.monitorContendedEnterRequests();
//		for(EventRequest req: requestList){
//			fRequests[MONITOR_CONTENDED_ENTER_INDEX].add(req);
//		}
//
//		requestList = oldEventManager.monitorWaitedRequests();
//		for(EventRequest req: requestList){
//			fRequests[MONITOR_WAITED_INDEX].add(req);
//		}
//		
//		requestList = oldEventManager.monitorWaitRequests();
//		for(EventRequest req: requestList){
//			fRequests[MONITOR_WAIT_INDEX].add(req);
//		}
		
		/* copy requests array */
		Class<?> classOldEventMgr = oldEventManager.getClass();
		Field req = classOldEventMgr.getDeclaredField("fRequests");
		req.setAccessible(true);
		Object o = req.get(oldEventManager);
		if(o != null){
			HashSet<?>[] fRequests = (HashSet[])o;
			req.set(newEventManager, fRequests);
		}
		req.setAccessible(false);
		
		/* copy fEnabledRequests map*/
		Field reqEnabled = classOldEventMgr.getDeclaredField("fEnabledRequests");
		reqEnabled.setAccessible(true);
		o = reqEnabled.get(oldEventManager);
		if(o != null){
			Hashtable<?,?>[] fEnabledRequests =(Hashtable[]) o;
			reqEnabled.set(newEventManager, fEnabledRequests);
		}
		reqEnabled.setAccessible(false);

	}

	/**
	 * Indicates that the stepOver is running.
	 * @return the minStep
	 */
	private final synchronized boolean isStepOver() {
		return stepOver;
	}

	/**
	 * Sets the stepInto flag to indicate that the StepOver is running.
	 * Used for synchronization wit the debugging thread. 
	 * @param minStep the minStep to set
	 */
	private final synchronized void setStepOver(boolean stepOver) {
		this.stepOver = stepOver;
	}    

	/**
	 * Indicates that the stepInto is running. 
	 * Used for synchronization wit the debugging thread. 
	 * @return the stepInto
	 */
	private final synchronized boolean isStepInto() {
		return stepInto;
	}

	/**
	 * Sets the stepInto flag to indicate that the StepInto is running.
	 * Used for synchronization with the debugging thread.
	 * @param stepInto the stepInto to set
	 */
	private final synchronized void setStepInto(boolean stepInto) {
		this.stepInto = stepInto;
	}	
	
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
				final VirtualMachine myVM = jdiThread.getUnderlyingThread().virtualMachine();
				VirtualMachineImpl vmImpl = (VirtualMachineImpl)myVM;  

				if(currentVM == null || !currentVM.equals(vmImpl)){
					currentVM = vmImpl;
					createEventManager();
				}

				/* Synchronized the second call with other debugging thread */
				setStepOver(true);
				
				stackFrame.stepOver();
				
				/* free flag s*/
				setStepOver(false);


			} catch (DebugException e) {
				exceptionHandler(e);
			} catch (SecurityException e) {
				exceptionHandler(e);
			} catch (NoSuchFieldException e) {
				exceptionHandler(e);
			} catch (IllegalArgumentException e) {
				exceptionHandler(e);
			} catch (IllegalAccessException e) {
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

				final JDIThread jdiThread = (JDIThread) stackFrame.getThread(); 
				final VirtualMachine myVM = jdiThread.getUnderlyingThread().virtualMachine();
				VirtualMachineImpl vmImpl = (VirtualMachineImpl)myVM;  

				if(currentVM == null || !currentVM.equals(vmImpl)){
					currentVM = vmImpl;
					createEventManager();
				}

				/* Synchronized the second call with other debugging thread */
				setStepInto(true);
				
				stackFrame.stepInto();

				/* free of the flag is done by event manager */

			} catch (DebugException e) {
				exceptionHandler(e);
			} catch (SecurityException e) {
				exceptionHandler(e);
			} catch (NoSuchFieldException e) {
				exceptionHandler(e);
			} catch (IllegalArgumentException e) {
				exceptionHandler(e);
			} catch (IllegalAccessException e) {
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

}
