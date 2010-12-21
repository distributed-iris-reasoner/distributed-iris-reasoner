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
package org.deri.iris.rules;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;

/**
 * <p>
 * Tests the rule analyser.
 * </p>
 *
 * @author Richard Pöttler (richard dot poettler at sti2 dot at)
 */
public class RuleAnalyserTest extends TestCase {

	public static Test suite() {
		return new TestSuite(RuleAnalyserTest.class, RuleAnalyserTest.class.getSimpleName());
	}

	/**
	 * Tests the <code>hasHeadLiteralInBody(...)</code> method.
	 */
	public void testHasHeadLiteralInBody() throws ParserException {
		final String[] trueRules = new String[]{"p(?X) :- a(1), p(?X), b(2)."};
		for (final String rule : trueRules) {
			assertTrue("hasHeadLiteralInBody returns false for \"" + rule + "\".",
					RuleAnalyser.hasHeadLiteralInBody(parseRule(rule)));
		}

		final String[] falseRules = new String[]{"p(?X) :- a(1), !p(?X), b(2)."};
		for (final String rule : falseRules) {
			assertFalse("hasHeadLiteralInBody returns true for \"" + rule + "\".",
					RuleAnalyser.hasHeadLiteralInBody(parseRule(rule)));
		}

		final String[] illegalArguemntExceptionRules = new String[]{"p(?X), a(?X) :- p(?X)."};
		for (final String rule : illegalArguemntExceptionRules) {
			try {
				RuleAnalyser.hasHeadLiteralInBody(parseRule(rule));
				fail(rule + " is supposed to throw an IllegalArgumentException.");
			} catch (final IllegalArgumentException e) {
				// a exception is supposed to be thrown
			}
		}
	}

	public void testUnsatisfiableVariableAssignment() throws ParserException, EvaluationException {
		final String[] trueRules = new String[]{
			"p(?X) :- q(?X), ?X=2, ?X=3.",
			"p(?X) :- q(?X, ?Y), ?X=2, ?Y=3, ?X=?Y.",
			"p(?X) :- q(?X, ?Y), ?A=2, ?B=3, ?A=?B.",
			"p(?X) :- q(?X, ?Y), ?X=2, ?Y=2, ?X!=?Y.",
			"p(?X) :- q(?X, ?Y), ?A=2, ?B=2, ?A!=?B.",
			"p(?X) :- q(?X), r(?X), f(2, ?X) = f(3, ?X).",
			"p(?X) :- q(?X), r(?X), f(3, ?X) != f(3, ?X).",
			"p(?X) :- q(?X), r(?X), ! f(3, ?X) = f(3, ?X).",
			"p(?X) :- q(?X), r(?X), ! f(3, ?X) != f(4, ?X).",
			};
		for (final String rule : trueRules) {
			assertFalse("hasSatisfiableVariableAssignment returns true for \"" + rule + "\".",
					RuleAnalyser.hasSatisfiableVariableAssignment(parseRule(rule)));
		}
	}

	public void testSatisfiableVariableAssignment() throws ParserException, EvaluationException {
		final String[] falseRules = new String[]{
			"p(?X) :- q(?X), ?X=2, ?X=2.",
			"p(?X) :- q(?X, ?Y), ?X=2, ?Y=2, ?X=?Y.",
			"p(?X) :- q(?X, ?Y), ?A=2, ?B=2, ?A=?B.",
			"p(?X) :- q(?X, ?Y), ?X=2, ?Y=3, ?X!=?Y.",
			"p(?X) :- q(?X, ?Y), ?A=2, ?B=3, ?A!=?B.",
			"p(?X) :- q(?X), r(?Y), ?X=?Y, ?Y=3.",
			"p(?X) :- q(?X, ?Y), ?A=2, ?B=2, ?A=?B.",
			"p(?X) :- q(?X), r(?X), f(3, ?X) = f(3, ?X).",
			"p(?X) :- q(?X), r(?X), f(3, ?X) != f(4, ?X).",
			"p(?X) :- q(?X), r(?X), ! f(3, ?X) = f(4, ?X).",
			"p(?X) :- q(?X), r(?X), ! f(3, ?X) != f(3, ?X).",
		};
		for (final String rule : falseRules) {
			assertTrue("hasSatisfiableVariableAssignment returns false for \"" + rule + "\".",
					RuleAnalyser.hasSatisfiableVariableAssignment(parseRule(rule)));
		}
	}

	/**
	 * Parses a program string and returns the first rule of it.
	 * @param program the program to parse
	 * @return the first parsed rule
	 */
	private static IRule parseRule(final String program) throws ParserException {
		assert program != null: "The program must not be null";

		final Parser parser = new Parser();
		parser.parse(program);

		assert !parser.getRules().isEmpty(): "There must be at least one rule parsed";

		return parser.getRules().get(0);
	}
}
