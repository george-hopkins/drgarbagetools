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

package com.drgarbage.visualgraphic.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import com.drgarbage.visualgraphic.editparts.VertexBaseEditPart;
import com.drgarbage.visualgraphic.model.VertexBase;

/**
 * A command to execute vertex order action.
 *
 *  @version $Revision: 1523 $
 *  $Id: HorizontalLeftOrderCommand.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class HorizontalLeftOrderCommand extends Command {

	/** the list of the selected models coresponded to the edit parts */
	private List<VertexBase> models = new ArrayList<VertexBase>();
	
	/** list of old node locations */
	private List<Point> oldLocations = null;
	
	/**
	 * Create a command that can execute a layout algorithm. 
	 */
	public HorizontalLeftOrderCommand(List editParts) {
		super();
		
		oldLocations = new ArrayList<Point>();

		VertexBase vb = null;
		for(int i = 0; i < editParts.size(); i++){
			Object o = editParts.get(i);
			if(o instanceof VertexBaseEditPart){
				VertexBaseEditPart part = (VertexBaseEditPart)o;
				o = part.getModel();
				vb = (VertexBase)o;			
				models.add(vb);
				oldLocations.add(new Point(vb.getLocation()));
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		if(models != null && oldLocations!= null
				&& oldLocations.size()== models.size())
			return true;
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		redo();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		
		VertexBase vb = null;
		
		/* find the min x */
		int min = Integer.MAX_VALUE;
		int x = 0;
		for(int i = 0; i < models.size(); i++){
			vb = models.get(i);
			x = vb.getLocation().x;
			if(x < min){
				min = x;
			}
		}
		
		/* set new locations */
		Point newLocation = null;
		for(int i = 0; i < models.size(); i++){
			vb = models.get(i);
			newLocation = new Point(min, vb.getLocation().y);
			vb.setLocation(newLocation);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		VertexBase vb = null;
		for(int i = 0; i < models.size(); i++){
			vb = models.get(i);
			vb.setLocation(oldLocations.get(i));
		}
		
	}
}
