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

package com.drgarbage.controlflowgraphfactory.editors.dot;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;

import com.drgarbage.dot.DotAttributes;
import com.drgarbage.dot.DotKeywords;
import com.drgarbage.dot.DotValues;

/**
 * DOT Keywords scanner.
 * 
 * @author Sergej Alekseev
 * @version $Revision$ $Id: DOTScanner.java 1388 2009-10-23 15:13:24Z
 *          Peter Palaga $
 */
public class DOTScanner extends RuleBasedScanner {

	public DOTScanner(ColorManager manager) {

		Token keyword = new Token(new TextAttribute(manager.getColor(ColorManager.KEYWORDS), null, SWT.BOLD));
		Token attributes = new Token(new TextAttribute(manager.getColor(ColorManager.ATTRIBUTES)));
		Token values = new Token(new TextAttribute(manager.getColor(ColorManager.VALUES)));
		Token comment = new Token(new TextAttribute(manager.getColor(ColorManager.COMMENT)));
		Token string = new Token(new TextAttribute(manager.getColor(ColorManager.STRING)));

		
		IWordDetector keywordDetector = new IWordDetector() {
			public boolean isWordStart(char c) {
				return Character.isJavaIdentifierStart(c);
			}

			public boolean isWordPart(char c) {
				return Character.isJavaIdentifierPart(c);
			}
		};
		/* keywordRule is case insensitive - FIX for BUG#226  */
		WordRule keywordRule = new WordRule(keywordDetector, Token.UNDEFINED, true);
		/* keywords */
		for (String key : DotKeywords.ALL) {
			keywordRule.addWord(key, keyword);
		}


		WordRule rule = new WordRule(new IWordDetector() {
			public boolean isWordStart(char c) {
				return Character.isJavaIdentifierStart(c);
			}

			public boolean isWordPart(char c) {
				return Character.isJavaIdentifierPart(c);
			}
		});

		/* node attributes */
		for (String key : DotAttributes.ALL) {
			rule.addWord(key, attributes);
		}

		/* values */
		for (String key : DotValues.ALL) {
			rule.addWord(key, values);
		}

		setRules(new IRule[] { 
				keywordRule,
				rule, 
				new EndOfLineRule("//", comment),
				new MultiLineRule("/*", "*/", comment, '\\', false),
				new SingleLineRule("\"", "\"", string, '\\'),
				new SingleLineRule("'", "'", string, '\\'),
				new WhitespaceRule(new IWhitespaceDetector() {
					public boolean isWhitespace(char c) {
						return Character.isWhitespace(c);
					}
				}), });
	}

}
