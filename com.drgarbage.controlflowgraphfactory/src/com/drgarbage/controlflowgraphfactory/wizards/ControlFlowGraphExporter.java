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

package com.drgarbage.controlflowgraphfactory.wizards;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.drgarbage.controlflowgraphfactory.export.AbstractExport2;
import com.drgarbage.controlflowgraphfactory.export.ExportException;
import com.drgarbage.controlflowgraphfactory.export.GraphDOTExport;
import com.drgarbage.controlflowgraphfactory.export.GraphMlExport;
import com.drgarbage.controlflowgraphfactory.export.GraphXMLExport;
import com.drgarbage.graph.DefaultGraphSpecification;
import com.drgarbage.graph.GraphConstants;
import com.drgarbage.javalang.JavalangConstants;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;

/**
 * Helper class for exporting resources to the file system.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: ControlFlowGraphExporter.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class ControlFlowGraphExporter extends DefaultGraphSpecification {
	
	private AbstractExport2 graphExport;

	/**
	 *  Creates the specified file system directory at <code>destinationPath</code>.
	 *  This creates a new file system directory.
	 *  
	 *  @param destinationPath location to which files will be written
	 */
	public void createFolder(IPath destinationPath) {
		new File(destinationPath.toOSString()).mkdir();
	}

	/**
	 * Returns xmlExporter object.
	 * @return xmlExporter
	 */
	protected AbstractExport2 getExporter() {
		if(graphExport == null) {
			
			switch (this.getExportFormat()) {
			case GraphConstants.EXPORT_FORMAT_DOT:
				graphExport = new GraphDOTExport();
				break;
			case GraphConstants.EXPORT_FORMAT_GRAPHXML:
				graphExport = new GraphXMLExport();
				break;
			case GraphConstants.EXPORT_FORMAT_GRAPHML:
				graphExport = new GraphMlExport();
				break;
			default:
				throw new IllegalStateException("Unexpected export format '"+ this.getExportFormat() +"'");
			}
			
			graphExport.setGraphSpecification(this);
			
		}
		return graphExport;
	}

	/**
	 *  Writes the passed resource to the specified location recursively.
	 *  
	 *  @param resource the resource to write out to the file system
	 *  @param destinationPath location where the resource will be written
	 *  @exception CoreException if the operation fails 
	 *  @exception IOException if an I/O error occurs when writing files
	 * @throws ClassNotFoundException 
	 * @throws ExportException 
	 */
	public void write(IResource resource, IPath destinationPath) throws CoreException, IOException, ClassNotFoundException, ExportException {
		if (resource.getType() == IResource.FILE) {
			writeFile((IFile) resource, destinationPath);
		} else {
			writeChildren((IContainer) resource, destinationPath);
		}
	}

	/**
	 *  Exports the passed container's children
	 * @throws ClassNotFoundException 
	 * @throws ExportException 
	 */
	protected void writeChildren(IContainer folder, IPath destinationPath)
	throws CoreException, IOException, ClassNotFoundException, ExportException {
		if (folder.isAccessible()) {
			IResource[] children = folder.members();
			for (int i = 0; i < children.length; i++) {
				IResource child = children[i];
				writeResource(child, destinationPath.append(child.getName()));
			}
		}
	}

	/**
	 *  Writes the passed file resource to the specified destination on the local
	 *  file system
	 * @throws ClassNotFoundException 
	 * @throws ExportException 
	 */
	protected void writeFile(IFile file, IPath destinationPath)
	throws IOException, CoreException, ClassNotFoundException, ExportException {

		ObjectInputStream in = new ObjectInputStream(file.getContents());
		ControlFlowGraphDiagram diagram = (ControlFlowGraphDiagram) in.readObject();
		in.close();

		/*set file name */
		diagram.setPropertyValue("name", file.getName());
		
		OutputStreamWriter w = null;
		OutputStream out = null; 
		
		try {
			out = new BufferedOutputStream(new FileOutputStream(new File(destinationPath.toOSString())));
			//FIXME: check if UTF-8 is what we want here
			w = new OutputStreamWriter(out, JavalangConstants.UTF_8);
			getExporter().write(diagram, w);
		} finally {
			if (w != null) {
				try {
					w.close();
				} catch (Throwable e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (Throwable e) {
				}
			}
		}
		

	}

	/**
	 *  Writes the passed resource to the specified location recursively
	 * @throws ClassNotFoundException 
	 * @throws ExportException 
	 */
	protected void writeResource(IResource resource, IPath destinationPath)
	throws CoreException, IOException, ClassNotFoundException, ExportException {
		if (resource.getType() == IResource.FILE) {
			writeFile((IFile) resource, destinationPath);
		} else {
			createFolder(destinationPath);
			writeChildren((IContainer) resource, destinationPath);
		}
	}
}
