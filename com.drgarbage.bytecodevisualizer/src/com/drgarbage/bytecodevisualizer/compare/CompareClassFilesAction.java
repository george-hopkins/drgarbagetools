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

package com.drgarbage.bytecodevisualizer.compare;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IObjectActionDelegate;

/**
 * The action to open the compare dialog for selected classes.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class CompareClassFilesAction extends AbstractCompareClassFileAction implements IObjectActionDelegate {

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        IJavaElement[] resources = getSelectedResources();        
        try {
            run(resources[0], resources[1]);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

}