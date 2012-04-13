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

import org.eclipse.gef.commands.Command;

import com.drgarbage.visualgraphic.model.Connection;


/**
 * A command to disconnect (remove) a connection from its endpoints.
 * The command can be undone or redone.
 * @version $Revision:204 $
 * $Id:ConnectionDeleteCommand.java 204 2007-06-08 13:19:58Z aleks $
 */
public class ConnectionDeleteCommand extends Command {

	/** Connection instance to disconnect. */
	private final Connection connection;
	
	/** 
	 * Create a command that will disconnect a connection from its endpoints.
	 * @param conn the connection instance to disconnect (non-null)
	 * @throws IllegalArgumentException if conn is null
	 */ 
	public ConnectionDeleteCommand(Connection conn) {
		if (conn == null) {
			throw new IllegalArgumentException();
		}
		setLabel("connection deletion");
		this.connection = conn;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		connection.disconnect();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		connection.reconnect();
	}
}
