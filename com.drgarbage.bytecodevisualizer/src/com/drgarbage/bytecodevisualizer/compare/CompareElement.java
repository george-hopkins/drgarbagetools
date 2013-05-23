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

package com.drgarbage.bytecodevisualizer.compare;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

import org.eclipse.compare.BufferedContent;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.drgarbage.asm.ClassReader;
import com.drgarbage.asm.render.impl.ClassFileDocument;
import com.drgarbage.asm.render.impl.ClassFileOutlineElement;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;


/**
 * A <code>CompareElement</code> is used as input for the differencing engine 
 * (interfaces <code>IStructureComparator</code> and <code>ITypedElement</code>).
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class CompareElement extends BufferedContent implements ITypedElement, IStructureComparator {

    private final IJavaElement javaElement;

    private final String className;

    /**
     * Type to represent the class content, used by Eclipse 
     * to recognize appropriated viewer. 
     */
    private String type;
  
    /** type constants */
    public static final String TYPE_BYTECODE = "class";
    public static final String TYPE_JAVA = "java";

    /**
     * Constructor for CompareElement.
     * @param className
     * @param type
     * @param javaElement
     * @param modes
     */
    public CompareElement(IJavaElement javaElement) {
        super();
        this.javaElement = javaElement;
        
        /* default type*/
        this.type = CompareElement.TYPE_JAVA;
        
        /* set the class name */
        this.className = getClassName(javaElement);
    }
    
    /**
     * Returns the class name for the given java element.
     * @param javaElement
     * @return class name
     */
    private static String getClassName(IJavaElement javaElement){
		String name;
		IClassFile classFile = (IClassFile) javaElement
				.getAncestor(IJavaElement.CLASS_FILE);
		if (classFile != null) {
			name = classFile.getPath().toOSString();
		} else {
			if(javaElement.getPath()!= null){
				name = javaElement.getPath().toOSString();
			}
			else{
				name = javaElement.getElementName();
			}
		}
		
		return name;
    }
    
    /**
     * Returns the underlying java element.
     * @return java element
     */
    public IJavaElement getJavaElement() {
		return javaElement;
	}

    /* (non-Javadoc)
     * @see org.eclipse.compare.ITypedElement#getName()
     */
    public String getName() {
        return className;
    }
    
    /**
     * Returns the javaElement javaElement.
     * @return the javaElement name
     */
    public String getElementName() {
    	return javaElement.getElementName();
    }

    /* (non-Javadoc)
     * @see org.eclipse.compare.ITypedElement#getType()
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the new type.
     * @param type
     */
    protected void setType(String type) {
        this.type = type;
    }

    /* (non-Javadoc)
     * @see org.eclipse.compare.ITypedElement#getImage()
     */
    public Image getImage() {
        return CompareUI.getImage("class");
    }

    /* (non-Javadoc)
     * @see org.eclipse.compare.structuremergeviewer.IStructureComparator#getChildren()
     */
    public Object[] getChildren() {
        return new CompareElement[0];
    }

    /* (non-Javadoc)
     * @see org.eclipse.compare.BufferedContent#createStream()
     */
    protected InputStream createStream() throws CoreException {    	
    	InputStream	stream = ClassFileMergeViewer.createStream(javaElement);
		if(stream == null){
			return null;
		}

    	if(!type.equals(TYPE_JAVA)){
    		return stream;
    	}
    	else{
    		ClassFileDocument doc = null;
    		byte[] bytes = null;
    		try {
    			int	max = stream.available();
    			bytes = new byte[max];
    			for(int i = 0; i < max; i++){
    				bytes[i] = (byte) stream.read();
    			}

    			InputStream in= new ByteArrayInputStream(bytes);
    			DataInputStream din = new DataInputStream(new BufferedInputStream(in));

    			ClassFileOutlineElement cv = new ClassFileOutlineElement();
    			doc = new ClassFileDocument(cv);
    			cv.setClassFileDocument(doc);

    			ClassReader cr = new ClassReader(din, doc);
    			cr.accept(doc, 0);

    		} catch (Exception e) {
    			throw new CoreException(new Status(IStatus.ERROR, 
    					BytecodeVisualizerPlugin.PLUGIN_ID, 
    					e.getMessage(), 
    					e));
    		}

    		if(doc != null){

    			char[] content = doc.toString().toCharArray();
    			final byte[] content2 = new byte[content.length];
    			for(int i = 0; i < content.length; i++){
    				content2[i] = (byte) content[i];
    			}

    			Display.getDefault().syncExec(new Runnable(){
    				public void run() {
    					setContent(content2);
    				}
    			});
    		}

    		return new ByteArrayInputStream(bytes);
    	}
    }
}
