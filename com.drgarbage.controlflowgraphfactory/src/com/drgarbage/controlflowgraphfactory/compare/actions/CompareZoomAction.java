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
package com.drgarbage.controlflowgraphfactory.compare.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.gef.Disposable;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;

/**
 * Base zoom action for compare dialog. Sub-classes can perform zoom in or zoom out.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
abstract class CompareZoomAction extends Action implements ZoomListener, Disposable {

	/**
	 * The zoom manager used to zoom in or out in the left view.
	 */
	protected ZoomManager zoomManagerLeft;
	
	/**
	 * The zoom manager used to zoom in or out in the right view.
	 */
	protected ZoomManager zoomManagerRight;

	/**
	 * Constructor
	 * 
	 * @param text the action's text, or <code>null</code> if there is no text
	 * @param image the action's image, or <code>null</code> if there is no image
	 * @param zoomManagerLeft the ZoomManager used to zoom in or out in the left view
	 * @param zoomManagerRight the ZoomManager used to zoom in or out in the right view
	 */
	public CompareZoomAction(String text, ImageDescriptor image,
			ZoomManager zoomManagerLeft, ZoomManager zoomManagerRight) {
		super(text, image);
		
		this.zoomManagerLeft = zoomManagerLeft;
		zoomManagerLeft.addZoomListener(this);
		
		this.zoomManagerRight = zoomManagerRight;
		zoomManagerRight.addZoomListener(this);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.Disposable#dispose()
	 */
	public void dispose() {
		zoomManagerLeft.removeZoomListener(this);
		zoomManagerRight.removeZoomListener(this);
	}

}
