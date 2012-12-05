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

package com.drgarbage.controlflowgraphfactory.export;

import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryPlugin;
import com.drgarbage.utils.Messages;

/**
 * Utility for export of images.
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: ImageExport.java 1526 2012-04-13 15:11:59Z Sergej Alekseev $
 */
public class ImageExport {

	public static void export(GraphicalViewer viewer, String fname, File graphFile, int format)
	{
		try
		{
			IFigure figure = ((AbstractGraphicalEditPart) viewer.getRootEditPart()).getFigure();
		
			File file = new File(fname);
			
			/* check if the path exist */
			File p = file.getParentFile();
			if(p == null){
				/* use project directory */
				String newFileName = graphFile.getParentFile().getPath() + File.separator +  fname;
				file = new File(newFileName);				
			}
			else{
				if(!p.exists()){
					Messages.error(ControlFlowFactoryMessages.EXPORT_ERROR_WRONGPATH);
					return;
				}
			}
			
			if (file.exists()){	
				if(!Messages.openConfirm(ControlFlowFactoryMessages.EXPORT_FILE_OVERWRITE_TITLE, ControlFlowFactoryMessages.EXPORT_FILE_OVERWRITE_MESSAGE))
				{
					return;
				}
			}
			else{
				/* create a directory if nessesary*/
				file.getParentFile().mkdirs();

				/* create file */
				file.createNewFile();
			}
		
			FileOutputStream fos = new FileOutputStream(file);

			if (figure instanceof Viewport){
				/* Reinit the figure */
				Viewport viewPort = (Viewport) figure;                         
				Rectangle rect = viewPort.getContents().getBounds();
				viewPort.setViewLocation(rect.x, rect.y);                              
			}

			Dimension size = figure.getPreferredSize();
			Image image = new Image(Display.getDefault(), size.width, size.height);
			GC gc = new GC(image);
			SWTGraphics graphics = new SWTGraphics(gc);
			figure.paint(graphics);
			
			ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] {image.getImageData()};
			loader.save(fos, format);
			
			fos.close();
		
			Messages.info(ControlFlowFactoryMessages.ExportAsImageAction_HeaderText, ControlFlowFactoryMessages.EXPORT_INFO + file.toString());

		}
		catch (Exception e)
		{
			ControlFlowFactoryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,ControlFlowFactoryPlugin.PLUGIN_ID, e.getMessage() , e));
			Messages.error(ControlFlowFactoryMessages.EXPORT_ERROR);
		}
	}
}
