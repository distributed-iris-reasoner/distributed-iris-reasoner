/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2009 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.deri.iris.parser;

import java.util.List;

import junit.framework.TestCase;

import org.deri.iris.api.basics.IRule;
import org.deri.iris.compiler.Parser;
import org.deri.iris.rules.RuleHeadEquality;

/**
 * Test for correct parsing of rules with rule head equality.
 * 
 * @author Adrian Marte
 */
public class RuleHeadEqualityParserTest extends TestCase {

	private Parser parser;

	private List<IRule> rules;

	public RuleHeadEqualityParserTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		parser = new Parser();

		String program = "?X = ?Y :- foo(?X), bar(?Y), ?X = ?Y.";
		program += "'A' = 'B' :- bar(?X).";
		program += "'A' = 'B' :- .";

		parser.parse(program);
		rules = parser.getRules();
	}

	public void testPredicate() {
		IRule rule = rules.get(0);

		assertTrue("Rule head equality not recognized.", RuleHeadEquality
				.hasRuleHeadEquality(rule));
		
		rule = rules.get(1);

		assertTrue("Rule head equality not recognized.", RuleHeadEquality
				.hasRuleHeadEquality(rule));
		
		rule = rules.get(2);

		assertTrue("Rule head equality not recognized.", RuleHeadEquality
				.hasRuleHeadEquality(rule));
	}

}
