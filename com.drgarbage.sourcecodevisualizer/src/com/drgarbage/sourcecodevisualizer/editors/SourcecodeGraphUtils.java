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

package com.drgarbage.sourcecodevisualizer.editors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.launching.JavaRuntime;

import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.ControlFlowGraphGenerator;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.sourcecodevisualizer.SourcecodeVisualizerPlugin;
import com.drgarbage.utils.ClassFileDocumentsUtils;

/**
 * Utilities for generation of source code graphs.
 * 
 * @author Sergej Alekseev
 * @version $Revision: 1523 $
 * $Id: SourcecodeGraphUtils.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class SourcecodeGraphUtils {

	/**
	 * Create a list of graphs.
	 * @return list of graphs
	 */
	public static List<IDirectedGraphExt> createGraphs(ICompilationUnit unit, IJavaProject jp){

		String[] classpath = null;
		String packageName = "";
		List<String> classList = new ArrayList<String>();

		try {
			/* get class path */
			classpath = JavaRuntime.computeDefaultRuntimeClassPath(jp);

			/* get package name and classes */
			IJavaElement[] elements = unit.getChildren();
			for(IJavaElement element: elements){
				if(element.getElementType() == IJavaElement.PACKAGE_DECLARATION){
					packageName = element.getElementName();
				} 
				else if(element.getElementType() == IJavaElement.TYPE){                                               
					ClassFileDocumentsUtils.collectNestedClasses((SourceType) element, classList);                                          
				}
			}

		} catch (JavaModelException e) {
			handleException(e);
			return null;
		} catch (CoreException e) {
			handleException(e);
			return null;
		}

		List<IDirectedGraphExt> graphs = null;

		/* Load compiled class files */
		try {
			graphs = ControlFlowGraphGenerator.generateSourceCodeGraphs(classList, classpath, packageName, false, false, false);
		} catch (IOException e) {
			handleException(e);
			return null;
		} catch (ControlFlowGraphException e) {
			handleException(e);
			return null;
		}

		return graphs;
	}

	/**
	 * Exceptions handler. Just to reduce number of code lines.
	 * @param e
	 */
	private static void handleException(Throwable e){
		SourcecodeVisualizerPlugin.getDefault().getLog().log(
				new Status(IStatus.ERROR, SourcecodeVisualizerPlugin.PLUGIN_ID, e.getMessage(), e));

	}
}
