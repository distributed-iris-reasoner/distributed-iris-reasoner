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
import org.deri.iris.api.builtins.IBuiltinAtom;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.factory.Factory;

/**
 * Test for StringReplaceBuiltin.
 */
public class StringReplaceBuiltinTest extends TestCase {

	public static ITerm W = TERM.createVariable("W");

	public static ITerm X = TERM.createVariable("X");

	public static ITerm Y = TERM.createVariable("Y");

	public static ITerm Z = TERM.createVariable("Z");

	public static ITerm R = TERM.createVariable("R");

	public StringReplaceBuiltinTest(String name) {
		super(name);
	}

	public void testReplace1() throws EvaluationException {
		check("a*cada*", "abracadabra", "bra", "*");
		check("*", "abracadabra", "a.*a", "*");
		check("*c*bra", "abracadabra", "a.*?a", "*");
		check("brcdbr", "abracadabra", "a", "");
		check("abbraccaddabbra", "abracadabra", "a(.)", "a$1$1");
		check("b", "AAAA", "A+", "b");
		check("bbbb", "AAAA", "A+?", "b");
		check("carted", "darted", "^(.*?)d(.*)$", "$1c$2");
		check("[1=ab][2=]cd", "abcd", "(ab)|(a)", "[1=$1][2=$2]");
	}

	public void testReplace2() throws EvaluationException {
		check("hello world", "hello world", "hello world", "foobar", "x");
		check("foobar", "helloworld", "hello world", "foobar", "x");
		check("helloworld", "helloworld", "hello[ ]world", "foobar", "x");
		check("foobar", "hello world", "hello\\ sworld", "foobar", "x");
	}

	private void check(String expected, String string, String regex,
			String replacement) throws EvaluationException {
		IBuiltinAtom replace = new StringReplaceWithoutFlagsBuiltin(TERM
				.createString(string), TERM.createString(regex), TERM
				.createString(replacement), R);

		assertEquals(Factory.BASIC.createTuple(Factory.TERM
				.createString(expected)), replace.evaluate(Factory.BASIC
				.createTuple(X, Y, Z, R)));
	}

	private void check(String expected, String string, String regex,
			String replacement, String flags) throws EvaluationException {
		IBuiltinAtom replace = new StringReplaceBuiltin(TERM
				.createString(string), TERM.createString(regex), TERM
				.createString(replacement), TERM.createString(flags), R);

		assertEquals(Factory.BASIC.createTuple(Factory.TERM
				.createString(expected)), replace.evaluate(Factory.BASIC
				.createTuple(W, X, Y, Z, R)));
	}
}
