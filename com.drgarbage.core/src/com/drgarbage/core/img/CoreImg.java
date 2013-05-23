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

package com.drgarbage.core.img;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * BytecodevisualizerImg class.
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: CoreImg.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class CoreImg {
	public static final ImageDescriptor drgarbage_16x16 = ImageDescriptor.createFromFile(CoreImg.class, "dr.garbage-16x16.png");
	public static final ImageDescriptor basicblockViewIcon_16x16 = ImageDescriptor.createFromFile(CoreImg.class, "basicblockview-16x16.png");
	public static final ImageDescriptor bytecodeViewIcon_16x16 = ImageDescriptor.createFromFile(CoreImg.class, "bytecodeview-16x16.png");
	public static final ImageDescriptor aboutDrGarbageIcon_16x16 = ImageDescriptor.createFromFile(CoreImg.class, "dr.garbage-16x16.png");
	public static final ImageDescriptor aboutDrGarbageIcon_32x32 = ImageDescriptor.createFromFile(CoreImg.class, "dr.garbage-16x16.png");
	public static final ImageDescriptor labelDrGarbage_148x30 = ImageDescriptor.createFromFile(CoreImg.class, "dr.garbage-148x30.png");
	public static final ImageDescriptor createBytecodeGraphIcon_16x16 = ImageDescriptor.createFromFile(CoreImg.class, "create-bytecodegraph-16x16.png");
	
	/* copied from control ControlFlowFactoryResource */
	public static final ImageDescriptor outline_16x16 = ImageDescriptor.createFromFile(CoreImg.class, "outline_16x16.gif");
	public static final ImageDescriptor bytecodeViewer_16x16 = ImageDescriptor.createFromFile(CoreImg.class, "bytecode-viewer-16x16.png");
	
	/* source code visualizer icons */
	public static final ImageDescriptor hideConstructorAction_16x16 = ImageDescriptor.createFromFile(CoreImg.class, "hide_constructor_action-16x16.png");
	public static final ImageDescriptor hideMethodAction_16x16 = ImageDescriptor.createFromFile(CoreImg.class, "hide_method_action-16x16.png");
	
	/* breakpoint */
	public static final ImageDescriptor breakpoint_action_icon = ImageDescriptor.createFromFile(CoreImg.class, "brkp_obj.gif");
}
