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

package com.drgarbage.controlflowgraph;

import org.eclipse.osgi.util.NLS;

public class VisualGraphicMessages extends NLS {
	private static final String BUNDLE_NAME= VisualGraphicMessages.class.getName();

	static {
		NLS.initializeMessages(BUNDLE_NAME, VisualGraphicMessages.class);
	}

	public static String Decision;
	public static String Get;
	public static String Goto_jump;
	public static String Invoke;
	public static String Instruction;
	public static String Basic_block;
	public static String Return;
	public static String Entry;
	public static String Exit;
	public static String Switch;
	public static String Comment;
	
	private VisualGraphicMessages() {
	}

}
