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

package com.drgarbage.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;

import com.drgarbage.bytecode.ByteCodeConstants;
import com.drgarbage.core.CoreConstants;
import com.drgarbage.core.CorePlugin;
import com.drgarbage.javalang.JavaLangUtils;

/**
 * Some utility methods for class file objects.
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class ClassFileDocumentsUtils {

	public static IMethod findMethod(IType type, String methodName, String methodSignature) 
	throws JavaModelException{
		if(type == null){
			return null;
		}

		if(methodName.equals(ByteCodeConstants.INIT)){
			methodName = type.getElementName();
		}
		
		IMethod[] methods = type.getMethods();
		for(IMethod m: methods){
			String sig = resolveMethodSignature(m);
			if(m.getElementName().equals(methodName)
					&& sig.equals(methodSignature)){

				return m;
			}
		}

		return null;
	}

	public  static String resolveMethodSignature(IMethod iMethod){

		String mMethodSignature = null;
		try{

			/* 
			 * Method Signature:
			 * NOTE: if class file is selected then the method signature is resolved. 
			 */
			if(iMethod.isBinary()){
				mMethodSignature = iMethod.getSignature();
			}
			else{

				/* resolve parameter signature */
				StringBuffer buf = new StringBuffer("(");
				String[] parameterTypes = iMethod.getParameterTypes();
				String res = null;
				for(int i = 0; i < parameterTypes.length; i++){
					res = com.drgarbage.core.ActionUtils.getResolvedTypeName(parameterTypes[i], iMethod.getDeclaringType());
					buf.append(res);
				}						
				buf.append(")");

				res = com.drgarbage.core.ActionUtils.getResolvedTypeName(iMethod.getReturnType(), iMethod.getDeclaringType());
				buf.append(res);

				mMethodSignature=buf.toString();


			}

		}catch(IllegalArgumentException e){
			handleException(e);
			return null;
		} catch (JavaModelException e) {
			handleException(e);
			return null;
		}

		return mMethodSignature;
	}

	/**
	 * Collects recursively nested classes in the compilation unit.
	 * The nested Classes are append with a prefix of the main class.
	 * For example:
	 *      HEllo
	 *      HEllo$C1
	 *      HEllo$C1$C3
	 *  
	 *  The classes are stored in the list.
	 *  
	 * @param start element
	 * @param list of classes
	 * @throws JavaModelException
	 */
	public static void collectNestedClasses(IJavaElement element, List<String> classList) 
	throws JavaModelException{
		if(element instanceof IType) {
			IType t = (IType) element;
			classList.add(t.getTypeQualifiedName());
			
			/* start recursion */
			IJavaElement[] elements = t.getChildren();
			for(IJavaElement e: elements){
				collectNestedClasses(e, classList);
			}
		}
	}
	
	/**
	 * Returns the list of InputStreams instances for
	 * the given classes.
	 * @param list of classes
	 * @param classpath
	 * @param package name
	 * @return
	 */
	public static List<InputStream> getInputStreams(List<String> classList, String[] classPath, String packageName){
		List<InputStream> streams = new ArrayList<InputStream>();
		for(String l: classList){
			InputStream in;
			try {
				in = JavaLangUtils.findResource(classPath, packageName, l);

				if(in == null){
					String err = "Resource '" + packageName + " " + l + "' not found.";
					CorePlugin.getDefault().getLog().log(
							new Status(IStatus.ERROR, CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID, err));
					
				}

				if (!(in instanceof BufferedInputStream)) {
					/* buffer only if necessary */
					in = new BufferedInputStream(in);
				}

				streams.add(in);
			} catch (IOException e) {
				handleException(e);
			}
		}

		return streams;
	}
	
	/**
	 * Create a list of graphs.
	 * @return list of graphs
	 */
	public static List<InputStream> findClasses(ICompilationUnit unit, IJavaProject jp){

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
				else if(element instanceof IType) {                                                 
					collectNestedClasses(element, classList);
				}
			}

		} catch (JavaModelException e) {
			handleException(e);
		} catch (CoreException e) {
			handleException(e);
		}

		return getInputStreams(classList, classpath, packageName);

	}
	
	/**
	 * Exceptions handler. Just to reduce number of code lines.
	 * @param e
	 */
	private static void handleException(Throwable e){
		CorePlugin.getDefault().getLog().log(
				new Status(IStatus.ERROR, CoreConstants.BYTECODE_VISUALIZER_PLUGIN_ID, e.getMessage(), e));

	}
}
