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
 
package com.drgarbage.classfile.editors;

import org.eclipse.jface.text.rules.*;

/**
 * The scanner that exclusively uses predicate rules.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class ClassFilePartitionScanner extends RuleBasedPartitionScanner {

	public final static String MULTILINE_COMMENT = "__multiline_comment";
	
	public ClassFilePartitionScanner() {
	    IToken comment = new Token(MULTILINE_COMMENT);
	    IPredicateRule[] rules = new IPredicateRule[1];
	    rules[0] = new MultiLineRule("/*", "*/", comment, '\\', false);
	    setPredicateRules(rules);
	}
	
}

