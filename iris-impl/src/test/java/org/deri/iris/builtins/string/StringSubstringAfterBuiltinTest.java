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

import junit.framework.TestCase;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.factory.Factory;

/**
 * Test for StringSubstringAfterBuiltin.
 */
public class StringSubstringAfterBuiltinTest extends TestCase {

	public static ITerm X = Factory.TERM.createVariable("X");

	public static ITerm Y = Factory.TERM.createVariable("Y");

	public static ITerm Z = Factory.TERM.createVariable("Z");

	public static ITerm R = Factory.TERM.createVariable("R");

	public StringSubstringAfterBuiltinTest(String name) {
		super(name);
	}

	public void testSubstringAfter1() throws EvaluationException {
		check("too", "tattoo", "tat");
		check("", "tattoo", "tattoo");
		check("tattoo", "tattoo", "");
		check("", "tattoo", "foobar");
	}

	public void testSubstringAfter2() throws EvaluationException {
		try {
			String collation = "http://www.w3.org/2005/xpath-functions/collation/codepoint";

			check("too", "tattoo", "tat", collation);
			check("", "tattoo", "tattoo", collation);
			check("tattoo", "tattoo", "", collation);
			check("", "tattoo", "foobar", collation);
		} catch (IllegalArgumentException iae) {
			fail("Unicode code point collation not supported.");
		}
	}

	private void check(String expected, String haystack, String needle)
			throws EvaluationException {
		StringSubstringAfterWithoutCollationBuiltin substring = new StringSubstringAfterWithoutCollationBuiltin(
				Factory.TERM.createString(haystack), Factory.TERM
						.createString(needle), R);

		assertEquals(Factory.BASIC.createTuple(Factory.TERM
				.createString(expected)), substring.evaluate(Factory.BASIC
				.createTuple(X, Y, R)));
	}

	private void check(String expected, String haystack, String needle,
			String collation) throws EvaluationException {
		StringSubstringAfterBuiltin substring = new StringSubstringAfterBuiltin(
				Factory.TERM.createString(haystack), Factory.TERM
						.createString(needle), Factory.TERM
						.createString(collation), R);

		assertEquals(Factory.BASIC.createTuple(Factory.TERM
				.createString(expected)), substring.evaluate(Factory.BASIC
				.createTuple(X, Y, Z, R)));
	}
}
