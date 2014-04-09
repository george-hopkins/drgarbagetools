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

import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.gef.internal.InternalImages;
import org.eclipse.gef.ui.actions.GEFActionConstants;


/**
 * The zoom in action for the compare dialog.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class CompareZoomInAction  extends CompareZoomAction {

	/**
	 * Constructor for ZoomInAction.
	 * 
	 * @param zoomManagerLeft the ZoomManager used to zoom in or out in the left view
	 * @param zoomManagerRight the ZoomManager used to zoom in or out in the right view
	 */
	public CompareZoomInAction(ZoomManager zoomManagerLeft, ZoomManager zoomManagerRight) {
		super(GEFMessages.ZoomIn_Label, InternalImages.DESC_ZOOM_IN,
				zoomManagerLeft, zoomManagerRight);
		setToolTipText(GEFMessages.ZoomIn_Tooltip);
		setId(GEFActionConstants.ZOOM_IN);
		setActionDefinitionId(GEFActionConstants.ZOOM_IN);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		zoomManagerLeft.zoomIn();
		zoomManagerRight.zoomIn();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.ZoomListener#zoomChanged(double)
	 */
	public void zoomChanged(double zoom) {
		if(!zoomManagerLeft.canZoomIn() || !zoomManagerRight.canZoomIn()){
			setEnabled(false);
		}
		else{
			setEnabled(true);
		}
	}

}
