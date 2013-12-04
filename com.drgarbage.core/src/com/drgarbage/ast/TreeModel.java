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
package com.drgarbage.ast;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * The view model for abstract tree represenattion.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class TreeModel {
	
	private List<TreeModel> children = new ArrayList<TreeModel>();
	private ASTNode node;
	private Object data;

	/**
	 * Construct an empty tree model object.
	 */
	public TreeModel() {
		node = null;
	}
	
	/**
	 * Constructs a tree model object.
	 * @param node the reference to the AST node
	 * @see ASTNode
	 */
	public TreeModel(ASTNode node) {
		this.node = node;
	}

	/**
	 * Returns the corresponding AST node.
	 * @return the node
	 */
	public ASTNode getNode() {
		return node;
	}

	/**
	 * Returns the the list of children.
	 * @return the children
	 */
	public List<TreeModel> getChildren() {
		return children;
	}

	/**
	 * Adds a child to the list of children.
	 * @param child
	 */
	public void addChild(TreeModel child) {
		children.add(child);
	}

	/**
	 * Sets the data object.
	 * @param o the data
	 */
	public void setData(Object o) {
		data = o;
	}

	/**
	 * Returns the referenced data object.
	 * @return the data
	 */
	public Object getData() {
		return data;
	}
}
