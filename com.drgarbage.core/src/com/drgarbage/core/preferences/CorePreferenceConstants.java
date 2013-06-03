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

package com.drgarbage.core.preferences;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.RGB;


/**
 * Constant definitions for plug-in preferences.
 * 
 * @author Peter Palaga
 * @version $Revision$
 * $Id$
 */
public interface CorePreferenceConstants {

	public static final RGB DEFAULT_WHITE_COLOR = new RGB(255, 255, 255);
	public static final RGB DEFAULT_BLACK_COLOR = new RGB(0, 0, 0);
	public static final RGB DEFAULT_RED_COLOR = new RGB(255, 0, 0);
	public static final RGB DEFAULT_GREEN_COLOR = new RGB(0, 255, 0);
	public static final RGB DEFAULT_BLUE_COLOR = new RGB(0, 0, 255);

	
	public static final RGB DEFAULT_INSTRUCTION_BGCOLOR = new RGB(206, 206, 255);
	public static final RGB DEFAULT_BASIC_BLOCK_BGCOLOR = new RGB(206, 206, 255);
	public static final RGB DEFAULT_ENTRY_END_BGCOLOR = new RGB(230, 230, 230);
	public static final RGB DEFAULT_DECISION_VERTEX_BGCOLOR = new RGB(180, 255, 180);
	public static final RGB DEFAULT_RETURN_VERTEX_BGCOLOR = new RGB(255, 220, 168);
	public static final RGB DEFAULT_INVOKE_VERTEX_BGCOLOR = new RGB(255, 255, 168);
	public static final RGB DEFAULT_GET_VERTEX_BGCOLOR = new RGB(255,  140,  140);
	public static final RGB DEFAULT_SWITCH_VERTEX_BGCOLOR = new RGB(252, 235, 165);
	public static final RGB DEFAULT_GOTO_JUMP_VERTEX_BGCOLOR = new RGB(208, 255, 255);

	public static final RGB DEFAULT_COMMENT_BGCOLOR = ColorConstants.tooltipBackground.getRGB();
	/* preferences constants */
	public static final String GRAPH_COLOR_PREFIX = "GRAPH_COLOR_";
	public static final String INSTRUCTION_BGCOLOR 		= GRAPH_COLOR_PREFIX + "INSTRUCTION_BGCOLOR";
	public static final String BASIC_BLOCK_BGCOLOR 		= GRAPH_COLOR_PREFIX + "BASIC_BLOCK_BGCOLOR";
	public static final String ENTRY_END_BGCOLOR 		= GRAPH_COLOR_PREFIX + "ENTRY_END_BGCOLOR";
	public static final String DECISION_VERTEX_BGCOLOR 	= GRAPH_COLOR_PREFIX + "DECISION_VERTEX_BGCOLOR";
	public static final String RETURN_VERTEX_BGCOLOR 	= GRAPH_COLOR_PREFIX + "RETURN_VERTEX_BGCOLOR";
	public static final String INVOKE_VERTEX_BGCOLOR 	= GRAPH_COLOR_PREFIX + "INVOKE_VERTEX_BGCOLOR";
	public static final String GET_VERTEX_BGCOLOR 		= GRAPH_COLOR_PREFIX + "GET_VERTEX_BGCOLOR";
	public static final String SWITCH_VERTEX_BGCOLOR 	= GRAPH_COLOR_PREFIX + "SWITCH_VERTEX_BGCOLOR";
	public static final String GOTO_JUMP_VERTEX_BGCOLOR = GRAPH_COLOR_PREFIX + "GOTO_JUMP_VERTEX_BGCOLOR";

	public static final String COMMENT_BGCOLOR 			= GRAPH_COLOR_PREFIX + "COMMENT_BGCOLOR";
	public static final String GRAPH_PANEL_LOCATION = "GRAPH_PANEL_LOCATION";
	public static final String GRAPH_PANEL_LOCATION_SEPARATE_VIEW = "GRAPH_PANEL_LOCATION_SEPARATE_VIEW";
	public static final String GRAPH_PANEL_LOCATION_EDITOR = "GRAPH_PANEL_LOCATION_EDITOR";

}
