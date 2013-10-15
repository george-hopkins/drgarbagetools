/**
 * Copyright (c) 2008-2013, Dr. Garbage Community
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

package com.drgarbage.bytecode.jdi.dialogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.internal.ui.sourcelookup.SourceContainerLabelProvider;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.WorkbenchContentProvider;

/**
 * The tree viewer to show the build path class folders.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
@SuppressWarnings("restriction")
public class ProjectBuildPathViewer extends TreeViewer {
	
	/**
	 * The current java project.
	 */
	private IJavaProject fJavaProject;
	
	/**
	 * Class folder images.
	 * @see ImageDescriptorRegistry
	 */
	private ImageDescriptorRegistry fRegistry;
	
	/**
	 * List of existing class folders in the project.
	 */
	Set<IFolder> libs = null;
	
	/**
	 * Creates a runtime classpath viewer for the given project resource.
	 *
	 * @param parent the parent control
	 * @param javaProject java project root
	 */
	public ProjectBuildPathViewer(Composite parent, IJavaProject javaProject) {
		super(parent);
		fJavaProject = javaProject;
		fRegistry= JavaPlugin.getImageDescriptorRegistry();

		fillLibList();

		setContentProvider(new WorkbenchContentProvider(){
			/* (non-Javadoc)
			 * @see org.eclipse.ui.model.BaseWorkbenchContentProvider#getElements(java.lang.Object)
			 */
			public Object[] getElements(Object element) {
				Object[] obj = super.getElements(element); 			

				List<Object> list = new ArrayList<Object>();
				for(Object o: obj){
					if(o instanceof IProject){
						/* shows only the current project */
						if(o.equals(fJavaProject.getProject())){
							list.add(o);
						}
					}
				}

				return list.toArray();
			}

			/* (non-Javadoc)
			 * @see org.eclipse.ui.model.BaseWorkbenchContentProvider#getChildren(java.lang.Object)
			 */
			public Object[] getChildren(Object element) {
				Object[] obj = super.getChildren(element);

				List<Object> list = new ArrayList<Object>();
				for(Object o: obj){
					if(o instanceof IProject){
						list.add(o);
					}
					if(o instanceof IFolder){
						if(libs.contains(o)){
							list.add(o);
						}
					}
				}

				return list.toArray();
			}
		});

		setLabelProvider(new SourceContainerLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.debug.internal.ui.sourcelookup.SourceContainerLabelProvider#getImage(java.lang.Object)
			 */
			public Image getImage(Object element) {

				if(element instanceof IProject)
					return super.getImage(element);

				return 	fRegistry.get(JavaPluginImages.DESC_OBJS_CLASSFOLDER);

			}
		});
		
		expandAll();
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.StructuredViewer#refresh()
	 */
	public void refresh() {
		super.refresh();
		fillLibList();
		expandAll();
	}
	
	
	/**
	 * Fills the list of existing class folders in the project.
	 */
	private void fillLibList(){
		libs = new HashSet<IFolder>();
		
		/* fill a set with classpath entries */
		IClasspathEntry[] classpathEntries = fJavaProject.readRawClasspath();
		for(IClasspathEntry ice: classpathEntries){
			if(ice.getEntryKind() == IClasspathEntry.CPE_LIBRARY){
				IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
				IResource resource= root.findMember(ice.getPath());
				if (resource instanceof IFolder) {
					IFolder f = (IFolder)resource;
					libs.add(f);
				}
			}
		}
	}
}
