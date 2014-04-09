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
 * The zoom out action for the compare dialog.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class CompareZoomOutAction  extends CompareZoomAction {

	/**
	 * Constructor for ZoomInAction.
	 * 
	 * @param zoomManagerLeft the ZoomManager used to zoom in or out in the left view
	 * @param zoomManagerRight the ZoomManager used to zoom in or out in the right view
	 */
	public CompareZoomOutAction(ZoomManager zoomManagerLeft, ZoomManager zoomManagerRight) {
		super(GEFMessages.ZoomIn_Label, InternalImages.DESC_ZOOM_OUT,
				zoomManagerLeft, zoomManagerRight);
		setToolTipText(GEFMessages.ZoomOut_Tooltip);
		setId(GEFActionConstants.ZOOM_OUT);
		setActionDefinitionId(GEFActionConstants.ZOOM_OUT);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		zoomManagerLeft.zoomOut();
		zoomManagerRight.zoomOut();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.ZoomListener#zoomChanged(double)
	 */
	public void zoomChanged(double zoom) {
		if(!zoomManagerLeft.canZoomOut() || !zoomManagerRight.canZoomOut()){
			setEnabled(false);
		}
		else{
			setEnabled(true);
		}
	}

}
