/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
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
package org.deri.iris.optimisations.rulefilter;

import java.util.HashSet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.api.basics.IRule;
import org.deri.iris.compiler.Parser;

/**
 * Tests the rule filter.
 * @author Richard Pöttler (richard dot poettler at sti2 dot at)
 */
public class RuleFilterTest extends TestCase {

	/** String to parse for all available rules. */
	private static final String ALL_RULES = "r0(?X, ?Y) :- r11(?X, ?Y).\n"
		     + "r11(?X, ?Y) :- r21(?X), r22(?Y), r23(?X,?Y).\n"
		     + "r23(?X, ?Y) :- r31(?X, ?Y).\n"
		     + "r31(?X, ?Y) :- r11(?X, ?Y), r22(?Y).\n"
		     + "s0(?X, ?Y) :- s11(?X), s12(?X,?Y,?Z).\n"
		     + "s11(?X) :- s21(?X), r11(?X,?Y), s22(?Q,?R).\n"
		     + "s12(?X) :- s121(?X,?X), s122(?Q,?R).\n"
		     + "s21(?X) :- s21(?X).\n"
		     + "s22(?X, ?Z) :-  s0(?Z,?Z), s21(?X), s31(?Z, ?Z, ?X).\n"
		     + "s31(?X, ?Y, ?R) :-  s0(?Z,?Z), s21(?X), s31(?Z, ?Z, ?X).\n"
		     + "t0(?X) :-  t11(?Z,?Z).\n"
		     + "t11(?X,?Z) :-  t21a(?X), t22a(?X), t23a(?X), t24a(?Z).\n"
		     + "t11(?X,?Z) :-  t21b(?X), t22b(?X), t23b(?X), t24b(?Z).\n"
		     + "t21b(?X) :-  t21a(?X).\n"
		     + "t21a(?X) :-  t21b(?X).\n";

	public static Test suite() {
		return new TestSuite(RuleFilterTest.class, RuleFilterTest.class.getSimpleName());
	}

	/**
	 * Asserts the result of the filtering.
	 * @param query_and_result the parseable string with the input query and
	 * the resulting rules
	 */
	private void assertResult(final String query_and_result) throws Exception {
		assert query_and_result != null: "The string must not be null";

		final Parser resultParser = new Parser();
		resultParser.parse(query_and_result);

		final Parser rulesParser = new Parser();
		rulesParser.parse(ALL_RULES);

		assertEquals(new HashSet<IRule>(resultParser.getRules()), 
				RuleFilter.shrinkRules(rulesParser.getRules(), 
					resultParser.getQueries().get(0)));
	}

	public void testShrinkRulesR0() throws Exception {
		final String query_and_result = "?- r0('john', ?Y).\n"
				+ "r0(?X, ?Y) :- r11(?X, ?Y).\n"
				+ "r11(?X, ?Y) :- r21(?X), r22(?Y), r23(?X,?Y).\n"
				+ "r23(?X, ?Y) :- r31(?X, ?Y).\n"
				+ "r31(?X, ?Y) :- r11(?X, ?Y), r22(?Y).\n";
		assertResult(query_and_result);
	}

	public void testShrinkRulesS22() throws Exception {
		final String query_and_result = "?- s22(?Z, ?Z).\n"
				+ "r11(?X, ?Y) :- r21(?X), r22(?Y), r23(?X,?Y).\n"
				+ "r23(?X, ?Y) :- r31(?X, ?Y).\n"
				+ "r31(?X, ?Y) :- r11(?X, ?Y), r22(?Y).\n"
				+ "s0(?X, ?Y) :- s11(?X), s12(?X,?Y,?Z).\n"
				+ "s11(?X) :- s21(?X), r11(?X,?Y), s22(?Q,?R).\n"
				+ "s21(?X) :- s21(?X).\n"
				+ "s22(?X, ?Z) :-  s0(?Z,?Z), s21(?X), s31(?Z, ?Z, ?X).\n"
				+ "s31(?X, ?Y, ?R) :-  s0(?Z,?Z), s21(?X), s31(?Z, ?Z, ?X).\n";
		assertResult(query_and_result);
	}

	public void testShrinkRulesR0_empty() throws Exception {
		final String query_and_result = "?- r0(?X, ?Y, ?Z, ?D).\n";
		assertResult(query_and_result);
	}

	public void testShrinkRulesS21_R23_S12() throws Exception {
		final String query_and_result = "?- s21(?X), r23(?P, ?Q), s12(?Q).\n"
				+ "r11(?X, ?Y) :- r21(?X), r22(?Y), r23(?X,?Y).\n"
				+ "r23(?X, ?Y) :- r31(?X, ?Y).\n"
				+ "r31(?X, ?Y) :- r11(?X, ?Y), r22(?Y).\n"
				+ "s12(?X) :- s121(?X,?X), s122(?Q,?R).\n"
				+ "s21(?X) :- s21(?X).\n";
		assertResult(query_and_result);
	}

	public void testShrinkRulesT0() throws Exception {
		final String query_and_result = "?- t0(?X)\n."
				+ "t0(?X) :-  t11(?Z,?Z).\n"
				+ "t11(?X,?Z) :-  t21a(?X), t22a(?X), t23a(?X), t24a(?Z).\n"
				+ "t11(?X,?Z) :-  t21b(?X), t22b(?X), t23b(?X), t24b(?Z).\n"
				+ "t21b(?X) :-  t21a(?X).\n"
				+ "t21a(?X) :-  t21b(?X).\n";
		assertResult(query_and_result);
	}
}
