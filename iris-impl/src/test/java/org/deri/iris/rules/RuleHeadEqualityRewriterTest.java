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
package org.deri.iris.rules;

import java.util.Collection;

import junit.framework.TestCase;

import org.deri.iris.api.basics.IRule;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;

/**
 * Test for EquivalenceRuleRewriter.
 * 
 * @author Adrian Marte
 */
public class RuleHeadEqualityRewriterTest extends TestCase {

	private RuleHeadEqualityRewriter rewriter;

	public RuleHeadEqualityRewriterTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		rewriter = new RuleHeadEqualityRewriter(false, true);
	}

	// Just a simple test, to test if enough rules are created.
	public void testRewrite() throws ParserException {
		String program = "q(?X, ?Y, ?Z) :- p(?X, ?Y), r(?Z).";

		Parser parser = new Parser();
		parser.parse(program);

		Collection<IRule> rules = parser.getRules();
		Collection<IRule> newRules = rewriter.rewrite(rules);

		assertEquals("Incorrect number of rules created.", 10, newRules.size());
	}

}
