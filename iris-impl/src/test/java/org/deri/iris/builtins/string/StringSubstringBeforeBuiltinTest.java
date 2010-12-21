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
package org.deri.iris.builtins.string;

import static org.deri.iris.factory.Factory.TERM;
import junit.framework.TestCase;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.factory.Factory;

/**
 * Test for StringSubstringBeforBuiltin.
 */
public class StringSubstringBeforeBuiltinTest extends TestCase {

	public static ITerm X = TERM.createVariable("X");

	public static ITerm Y = TERM.createVariable("Y");

	public static ITerm Z = TERM.createVariable("Z");

	public static ITerm R = TERM.createVariable("R");

	public StringSubstringBeforeBuiltinTest(String name) {
		super(name);
	}

	public void testSubstringBefore1() throws EvaluationException {
		check("t", "tattoo", "attoo");
		check("", "tattoo", "tattoo");
		check("", "tattoo", "");
		check("", "tattoo", "foobar");
	}

	public void testSubstringBefore2() throws EvaluationException {
		try {
			String collation = "http://www.w3.org/2005/xpath-functions/collation/codepoint";

			check("t", "tattoo", "attoo", collation);
			check("", "tattoo", "tattoo", collation);
			check("", "tattoo", "", collation);
			check("", "tattoo", "foobar", collation);
		} catch (IllegalArgumentException iae) {
			fail("Unicode code point collation not supported.");
		}
	}

	private void check(String expected, String haystack, String needle)
			throws EvaluationException {
		StringSubstringBeforeWithoutCollationBuiltin substring = new StringSubstringBeforeWithoutCollationBuiltin(
				TERM.createString(haystack), TERM.createString(needle), R);

		assertEquals(Factory.BASIC.createTuple(Factory.TERM
				.createString(expected)), substring.evaluate(Factory.BASIC
				.createTuple(X, Y, R)));
	}

	private void check(String expected, String haystack, String needle,
			String collation) throws EvaluationException {
		StringSubstringBeforeBuiltin substring = new StringSubstringBeforeBuiltin(
				TERM.createString(haystack), TERM.createString(needle), TERM
						.createString(collation), R);

		assertEquals(Factory.BASIC.createTuple(Factory.TERM
				.createString(expected)), substring.evaluate(Factory.BASIC
				.createTuple(X, Y, Z, R)));
	}
}
