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

package com.drgarbage.controlflowgraphfactory.actions;

import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryPlugin;
import com.drgarbage.controlflowgraphfactory.dialogs.ExportGraphSaveAsDialog;
import com.drgarbage.controlflowgraphfactory.export.AbstractExport2;
import com.drgarbage.controlflowgraphfactory.export.ExportException;
import com.drgarbage.controlflowgraphfactory.export.GraphDOTExport;
import com.drgarbage.controlflowgraphfactory.export.GraphMlExport;
import com.drgarbage.controlflowgraphfactory.export.GraphXMLExport;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.graph.GraphConstants;
import com.drgarbage.graph.IGraphSpecification;
import com.drgarbage.io.FileExtensions;
import com.drgarbage.utils.Messages;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;

/**
 * Action for generation of graphs from the byte code
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: GenerateGraphAction.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public abstract class GenerateGraphAction implements IObjectActionDelegate {
	private IGraphSpecification graphSpecification;

	private int graphType = GraphConstants.GRAPH_TYPE_SOURCE_GRAPH; 

	/**
	 * Active workbench page. 
	 */
	private IWorkbenchPage page = null;

	/**
	 * Current selection object.
	 */
	private Object selection = null;

	public GenerateGraphAction(int graphType) {
		super();
		this.graphType = graphType;
	}

	/**
	 * Process all parents from the selected node by calling getParent() method.
	 * Tree                             | Interfaces
	 * ---------------------------------+-----------
	 * Project                          | IJavaElement, IJavaProject
	 *   + Package                      | IJavaElement, IPackageFragment
	 *     + Source: *.java or *.class  | IJavaElement
	 *       + Class                    | IJavaElement, IType
	 *         + Method					| IJavaElement, IMethod
	 *         
	 * Classpath for the selected class files can be resolved from the 
	 * project tree node by calling getPath() method.
	 * Classpath for the source files should be resolved via Java runtime,
	 * because it can be different with the source path.
	 * 
	 */
	private void createControlFlowGraph(TreeSelection treeSel){
		
		String mMethodName=null;
		String mMethodSignature=null;
		String mClassName=null;
		String mPackage=null;
		List<String> mPath=new ArrayList<String>();
		
		/* java model elements */
		IMethod iMethod = null;
		IJavaProject jp = null;
		
			try {
				
				/* Method Name */
				iMethod = (IMethod)treeSel.getFirstElement();
				
				if(!hasCode(iMethod)){
					Messages.info(MessageFormat.format(CoreMessages.CannotGenerateGraph_MethodIsAnAbstractMethod, new Object[]{iMethod.getElementName()}));
					return;
					
				}
				
				if(iMethod.isConstructor()){
					mMethodName = "<init>";
				}
				else{				
					mMethodName = iMethod.getElementName();
				}
				
				/** 
				 * Method Signature:
				 * NOTE: if class file is selected then the method signature is resolved. 
				 */
				if(iMethod.isBinary()){
					mMethodSignature = iMethod.getSignature();
				}
				else{
					try{
						/* resolve parameter signature */
						StringBuffer buf = new StringBuffer("(");
						String[] parameterTypes = iMethod.getParameterTypes();
						String res = null;
						for(int i = 0; i < parameterTypes.length; i++){
							res = ActionUtils.getResolvedTypeName(parameterTypes[i], iMethod.getDeclaringType());
							buf.append(res);
						}						
						buf.append(")");
						
						res = ActionUtils.getResolvedTypeName(iMethod.getReturnType(), iMethod.getDeclaringType());
						buf.append(res);
						
						mMethodSignature=buf.toString();
						
					}catch(IllegalArgumentException e){
						ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
						Messages.error(e.getMessage() + CoreMessages.ExceptionAdditionalMessage);
						return;
					}
				}
				
				IType type = iMethod.getDeclaringType();
				mClassName = type.getFullyQualifiedName();
				
				mPackage = type.getPackageFragment().getElementName();
				mClassName = mClassName.replace(mPackage + ".", "");
				
				if(iMethod.isBinary()){
					/* Classpath for selected class files */
					mPath.add(type.getPackageFragment().getPath().toString());					
				}
				
				/* Classpath for selected source code files */
				jp = iMethod.getJavaProject();
				try {
					String[] str = JavaRuntime.computeDefaultRuntimeClassPath(jp);
					for(int i=0; i < str.length; i++){
						mPath.add(str[i]);
					}		
				} catch (CoreException e) {
					ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
					Messages.error(e.getMessage() + CoreMessages.ExceptionAdditionalMessage);
					return;
				}
				
			} 
			catch(ClassCastException e){
				ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
				Messages.error(e.getMessage() + CoreMessages.ExceptionAdditionalMessage);
				return;
			}
			catch (JavaModelException e) {
				ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
				Messages.error(e.getMessage() + CoreMessages.ExceptionAdditionalMessage);
				return;
			}
		
	
		/* convert classpath to String array */
		String[] classPath=new String[mPath.size()];
		for(int i=0; i <mPath.size();i++){
			classPath[i]=mPath.get(i);
		}
		
		/* create control flow graph diagram */
		final ControlFlowGraphDiagram controlFlowGraphDiagram = createDiagram(classPath, mPackage, mClassName, mMethodName, mMethodSignature);
	
		if(controlFlowGraphDiagram == null){
			Messages.warning(ControlFlowFactoryMessages.DiagramIsNullMessage);
			return;
		}
		
		/* create empty shell */	
		Shell shell = page.getActivePart().getSite().getShell();
		
		/* Show a SaveAs dialog */
		ExportGraphSaveAsDialog dialog = new ExportGraphSaveAsDialog(shell);

		try {
			IPath path = jp.getCorrespondingResource().getFullPath();
			if(iMethod.isConstructor()){
				/* use class name for constructor */
				path = path.append(IPath.SEPARATOR + mClassName + "." + mClassName + "."+ GraphConstants.graphTypeSuffixes[getGraphType()]);
			}
			else {
				path = path.append(IPath.SEPARATOR + mClassName + "."+ mMethodName + "."+ GraphConstants.graphTypeSuffixes[getGraphType()]);
			}
			path = path.addFileExtension(FileExtensions.GRAPH);
			
			/* get file and set in the dialog */
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			dialog.setOriginalFile(file);
			
		} catch (JavaModelException e) {
			ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
			Messages.error(e.getMessage() + CoreMessages.ExceptionAdditionalMessage);
			return;
		}

		/* open SaveAS dialog */
		dialog.open();		

		IPath path = dialog.getResult();		
		if (path == null) {/* action canceled */
			return;
		}

		graphSpecification = dialog.getGraphSpecification();

		/* convert if necessary and start an editor */
		switch(graphSpecification.getExportFormat()){
			case GraphConstants.EXPORT_FORMAT_DRGARBAGE_GRAPH:
				ActionUtils.saveDiagramInFileAndOpenEditor(path, shell, page, controlFlowGraphDiagram, graphSpecification.isOpenInEditor());    
				break;
			default:

				AbstractExport2 exporter = null;
				
				switch (graphSpecification.getExportFormat()) {
				case GraphConstants.EXPORT_FORMAT_DOT:
					exporter = new GraphDOTExport();
					break;
				case GraphConstants.EXPORT_FORMAT_GRAPHXML:
					exporter = new GraphXMLExport();
					break;
				case GraphConstants.EXPORT_FORMAT_GRAPHML:
					exporter = new GraphMlExport();
					break;
				default:
					throw new IllegalStateException("Unexpected export format '"+ graphSpecification.getExportFormat() +"'.");
				}
				exporter.setGraphSpecification(graphSpecification);
				StringWriter sb = new StringWriter();
				try {
					exporter.write(controlFlowGraphDiagram, sb);
				} catch (ExportException e) {
					/* This will never happen as
					 * StringBuilder.append(*) does not throw IOException*/
					throw new RuntimeException(e);
				}
				ActionUtils.saveContentInFileAndOpenEditor(path, shell, page, sb.toString(), graphSpecification.isOpenInEditor());
				break;
				
		}
		
		dialog = null;
	}

	/**
	 * Creates the diagrapm
	 */
	abstract protected ControlFlowGraphDiagram createDiagram( String[] classPath, String mPackage, String mClassName, String mMethodName, String mMethodSignature);
	
	public IGraphSpecification getGraphSpecification() {
		return graphSpecification;
	}

	/**
	 * Gets graph type: <code>BYTECODE_GRAPH</code>, <code>BASICBLOCK_GRAPH</code> 
	 * or <code>SOURCE_GRAPH</code>.
	 * @return graph type
	 */
	public int getGraphType() {
		return graphType;
	}
	
	/**
	 * Returns true if the method has code.
	 * @param method
	 * @return true or false
	 * @throws JavaModelException
	 */
	public boolean hasCode(IMethod method) throws JavaModelException{
		
		int flags = method.getFlags();
		
		if (Flags.isAbstract(flags)){
			return false;
		}
		
		IJavaElement parent = method.getParent();
		
		if(parent.getElementType() == IJavaElement.TYPE){
			IType type =(IType)parent;
			if(type.isInterface()
					|| type.isEnum()
					|| type.isAnnotation()
			){
				return false;
			}			
		}
		
		return true;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if(selection instanceof TreeSelection){
			createControlFlowGraph((TreeSelection)selection);
		}
		else{
			Messages.error("TreeSelection-Interface is not compartible.");
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection=selection;
	}
	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
		page = targetPart.getSite().getWorkbenchWindow().getActivePage();
	}

	/**
	 * 	Sets graph type. The valid values are: <code>BYTECODE_GRAPH</code>, 
	 * <code>BASICBLOCK_GRAPH</code> or <code>SOURCE_GRAPH</code>.
	 * @param graph type
	 */
	public void setGraphType(int graphType) {
		this.graphType = graphType;
	}
}
