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

package com.drgarbage.logger;

/**
 * Pugin Trace Topics. 
 *
 * @author Sergej Alekseev
 * @version $Revision:25 $
 * $Id:TraceTopics.java 25 2007-04-01 17:56:22Z aleks $
 */
public class TraceTopics {

    /**
     * Set this JVM System property to true to switch on logging.
     * For example: -Dcom.drgarbage.classfile.debug=true
     */
    
    public static final boolean LOG_CLASSFILE = Boolean.getBoolean("com.drgarbage.classfile.debug");
    
    public static final boolean LOG_PLUGIN = Boolean.getBoolean("com.drgarbage.bytecodevisualizer.plugin.debug");
    
    public static final boolean LOG_VISUALGRAPHIC = Boolean.getBoolean("com.drgarbage.visualgraphic.debug");
     
}
