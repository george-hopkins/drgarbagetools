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

import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;
import com.drgarbage.controlflowgraph.intf.INodeListExt;
import com.drgarbage.visualgraphic.model.VertexBase;

/**
 * A command to execute placing algorithm.
 * The command can be undone or redone.
 *
 *  @version $Revision$
 *  $Id: LayoutAlgorithmCommand.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class LayoutAlgorithmCommand extends Command {

	/** new graph structure */
	private IDirectedGraphExt graph = null;
	
	/** list of old node locations */
	private List<Point> oldLocations = null;
	
	/**
	 * Create a command that can execute a layout algorithm. 
	 */
	public LayoutAlgorithmCommand(IDirectedGraphExt newGraph) {
		super();
		this.graph = newGraph;
		
		oldLocations = new ArrayList<Point>();
		INodeExt node = null;
		INodeListExt listNode = graph.getNodeList(); 
		VertexBase vb = null;
		for(int i = 0; i < listNode.size(); i++){
			node = listNode.getNodeExt(i);
			vb = (VertexBase)node.getData();			
			oldLocations.add(new Point(vb.getLocation()));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		if(graph != null && oldLocations!= null
				&& oldLocations.size()== graph.getNodeList().size())
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
		INodeExt node = null;
		INodeListExt listNode = graph.getNodeList(); 
		VertexBase vb = null;
		Point newLocation = null;
		for(int i = 0; i < listNode.size(); i++){
			node = listNode.getNodeExt(i);
			vb = (VertexBase)node.getData();			
			newLocation = new Point(node.getX(), node.getY());
			vb.setLocation(newLocation);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		INodeExt node = null;
		INodeListExt listNode = graph.getNodeList(); 
		VertexBase vb = null;
		for(int i = 0; i < listNode.size(); i++){
			node = listNode.getNodeExt(i);
			vb = (VertexBase)node.getData();
			vb.setLocation(oldLocations.get(i));		
		}
	}
}
