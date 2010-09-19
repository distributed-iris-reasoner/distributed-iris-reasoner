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
package org.deri.iris.parser;

import static org.deri.iris.MiscHelper.createLiteral;
import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.BUILTIN;
import static org.deri.iris.factory.Factory.CONCRETE;
import static org.deri.iris.factory.Factory.TERM;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import org.deri.iris.factory.Factory;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.Relations;
import org.deri.iris.storage.simple.SimpleRelationFactory;

/**
 * <p>
 * Tests for the datalog parser.
 * </p>
 * @author Joachim Adi Schuetz, DERI Innsbruck
 * @author Richard Pöttler (richard dot poettler at sti2 dot at)
 */
public class ParserTest extends TestCase {

	public static Test suite() {
		return new TestSuite(ParserTest.class, ParserTest.class.getSimpleName());
	}

	/**
	 * Parses a given string and asserts the parsed results
	 * @param prog the string to parse
	 * @param rules the expected rules
	 * @param facts the expected facts
	 * @param queries the expected queries
	 */
	private void assertResult(final String prog,
			final Collection<IRule> rules,
			final Map<IPredicate, IRelation> facts,
			final Collection<IQuery> queries) throws ParserException {
		Parser parser = new Parser();
		parser.parse(prog);

		// assert the rules
		final Set<IRule> parsedRules = new HashSet<IRule>(parser.getRules());
		final Set<IRule> expectedRules = (rules == null)
			? Collections.<IRule>emptySet()
			: new HashSet<IRule>(rules);
		assertEquals("The rules were not parsed correctly", expectedRules, parsedRules);

		// assert the facts
		final Map<IPredicate, IRelation> parsedFacts = parser.getFacts();
		final Map<IPredicate, IRelation> expectedFacts = (facts == null)
			? Collections.<IPredicate, IRelation>emptyMap()
			: facts;
		assertEquals("The facts were not parsed correctly",
				Relations.toPredicateSetMapping(expectedFacts),
				Relations.toPredicateSetMapping(parsedFacts));

		// assert the queries
		final Set<IQuery> parsedQueries = new HashSet<IQuery>(parser.getQueries());
		final Set<IQuery> expectedQueries = (queries == null)
			? Collections.<IQuery>emptySet()
			: new HashSet<IQuery>(queries);
		assertEquals("The queries were not parsed correctly", expectedQueries, parsedQueries);
	}

	/**
	 * Creates a predicate to relation map with only one fact.
	 * @param predicate the predicate of the fact
	 * @param fact which to put into the relation
	 */
	private static Map<IPredicate, IRelation> singletonFact(final IPredicate predicate, final ITuple fact) {
		assert predicate != null: "The predicate must not be null";
		assert fact != null: "The fact must not be null";

		final IRelation relation = (new SimpleRelationFactory()).createRelation();
		relation.add(fact);

		return Collections.singletonMap(predicate, relation);
	}

	/**
	 * s(X, Y) :- p(Y, Z), r(Y, Z)
	 */
	public void testParser() throws Exception {
		final String expr = "s(?X, ?Y) :- p(?X, ?Z), r(?Y, ?Z).";
		
		final IRule rule = Factory.BASIC.createRule(Arrays.asList(createLiteral("s", "X", "Y")),
				Arrays.asList(createLiteral("p", "X", "Z"), createLiteral("r", "Y", "Z")));

		assertResult(expr, Collections.singleton(rule), null, null);
	}

	/**
	 * p(?X,?Y) :- r(?Z, ?Y) and ?X='a'
	 */
	public void testParser_1a() throws Exception {
		final String expr = "p(?X, ?Y) :- r(?Z, ?Y), ?X='a'.";
		
		final IRule rule = Factory.BASIC.createRule(Arrays.asList(createLiteral("p", "X", "Y")),
				Arrays.asList(createLiteral("r", "Z", "Y"),
					BASIC.createLiteral(true,
						BUILTIN.createEqual(TERM.createVariable("X"), TERM.createString("a")))));

		assertResult(expr, Collections.singleton(rule), null, null);
	}

	/**
	 * p(?X,?Y) :- r(?X, ?Y) and ?X!='a'
	 */
	public void testParser_1b() throws Exception {
		final String expr = "p(?X, ?Y) :- r(?Z, ?Y), ?X!='a'.";

		final IRule rule = Factory.BASIC.createRule(Arrays.asList(createLiteral("p", "X", "Y")),
				Arrays.asList(createLiteral("r", "Z", "Y"),
					BASIC.createLiteral(true,
						BUILTIN.createUnequal(TERM.createVariable("X"), TERM.createString("a")))));

		assertResult(expr, Collections.singleton(rule), null, null);
	}

	/**
	 * Tests whether all terms are created correctly.
	 */
	public void testTerms() throws Exception {
		// TODO: test the function term
		final IPredicate PRED = BASIC.createPredicate("fact", 1);
		
		// asserting the short int
		assertResult("fact(1).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createInteger(1))), null);
		// asserting the long int
		assertResult("fact(_integer(1)).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createInteger(1))), null);
		// asserting the short string
		assertResult("fact('string').", null, singletonFact(PRED, BASIC.createTuple(TERM.createString("string"))), null);
		// asserting the long string
		assertResult("fact(_string('string')).", null, singletonFact(PRED, BASIC.createTuple(TERM.createString("string"))), null);
		// asserting the short decimal
		assertResult("fact(1.5).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createDecimal(1.5))), null);
		// asserting the long decimal
		assertResult("fact(_decimal(1.5)).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createDecimal(1.5))), null);
		// asserting the short sqname
		assertResult("fact(sq#name).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createSqName("sq#name"))), null);
		// asserting the long sqname
		assertResult("fact(_sqname(sq#name)).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createSqName("sq#name"))), null);
		// asserting the short iri
		assertResult("fact(_'http://some/#iri').", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createIri("http://some/#iri"))), null);
		// asserting the long iri
		assertResult("fact(_iri('http://some/#iri')).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createIri("http://some/#iri"))), null);

		// asserting the bool
		assertResult("fact(_boolean('false')).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createBoolean(false))), null);
		// asserting the double
		assertResult("fact(_double(1.5)).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createDouble(1.5))), null);
		// asserting the float
		assertResult("fact(_float(1.5)).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createFloat(1.5f))), null);
		// asserting the date
		assertResult("fact(_date(2007, 2, 6)).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createDate(2007, 2, 6))), null);
		// asserting the date with timezone
		assertResult("fact(_date(2007, 2, 6, 2, 30)).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createDate(2007, 2, 6, 2, 30))), null);
		// asserting the duration
		assertResult("fact(_duration(2007, 2, 6, 12, 45, 11)).",
				null,
				singletonFact(PRED, BASIC.createTuple(CONCRETE.createDuration(true,2007, 2, 6, 12, 45, 11))),
				null);
		// asserting the duration with milliseconds
		assertResult("fact(_duration(2007, 2, 6, 12, 45, 11, 500)).",
				null,
				singletonFact(PRED, BASIC.createTuple(CONCRETE.createDuration(true,2007, 2, 6, 12, 45, 11, 500))),
				null);
		// asserting the datetime
		assertResult("fact(_datetime(2007, 2, 6, 12, 45, 11)).",
				null,
				singletonFact(PRED, BASIC.createTuple(CONCRETE.createDateTime(2007, 2, 6, 12, 45, 11, 0, 0))),
				null);
		// asserting the datetime with timezone
		assertResult("fact(_datetime(2007, 2, 6, 12, 45, 11, 1, 30)).",
				null,
				singletonFact(PRED, BASIC.createTuple(CONCRETE.createDateTime(2007, 2, 6, 12, 45, 11, 1, 30))),
				null);
		// asserting the datetime with timezone and milliseconds
		assertResult("fact(_datetime(2007, 2, 6, 12, 45, 11, 500, 1, 30)).",
				null,
				singletonFact(PRED, BASIC.createTuple(CONCRETE.createDateTime(2007, 2, 6, 12, 45, 11, 500, 1, 30))),
				null);
		// asserting the time
		assertResult("fact(_time(12, 45, 11)).",
				null,
				singletonFact(PRED, BASIC.createTuple(CONCRETE.createTime(12, 45, 11, 0, 0))),
				null);
		// asserting the time with timezone
		assertResult("fact(_time(12, 45, 11, 1, 30)).",
				null,
				singletonFact(PRED, BASIC.createTuple(CONCRETE.createTime(12, 45, 11, 1, 30))),
				null);
		// asserting the time with timezone and milliseconds
		assertResult("fact(_time(12, 45, 11, 500, 1, 30)).",
				null,
				singletonFact(PRED, BASIC.createTuple(CONCRETE.createTime(12, 45, 11, 500, 1, 30))),
				null);
		// asserting the gday
		assertResult("fact(_gday(4)).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createGDay(4))), null);
		// asserting the gmonth
		assertResult("fact(_gmonth(4)).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createGMonth(4))), null);
		// asserting the gyear
		assertResult("fact(_gyear(4)).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createGYear(4))), null);
		// asserting the gmonthday
		assertResult("fact(_gmonthday(2, 6)).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createGMonthDay(2, 6))), null);
		// asserting the gyearmonth
		assertResult("fact(_gyearmonth(2007, 2)).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createGYearMonth(2007, 2))), null);
		// asserting the base64 binary
		assertResult("fact(_base64binary('45df')).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createBase64Binary("45df"))), null);
		// asserting the hex binary
		assertResult("fact(_hexbinary('a1df')).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createHexBinary("a1df"))), null);
		
		// Testing YearMonthDuration.
		assertResult("fact(_yearmonthduration(2009, 07)).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createYearMonthDuration(true, 2009, 7))), null);
		
		// Testing DayTimeDuration.
		assertResult("fact(_daytimeduration(1, 2, 5, 6.0)).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createDayTimeDuration(true, 1, 2, 5, 6.0))), null);
		assertResult("fact(_daytimeduration(1, 2, 5, 6, 500)).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createDayTimeDuration(true, 1, 2, 5, 6, 500))), null);
		
		// Testing RDF Text.
		assertResult("fact(_plainliteral('Pwnage', 'en')).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createPlainLiteral("Pwnage", "en"))), null);
		
		// Testing XMLLiteral.
		assertResult("fact(_xmlliteral('<tag attribute=\\\"value\\\">Text</tag>')).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createXMLLiteral("<tag attribute=\"value\">Text</tag>"))), null);
		assertResult("fact(_xmlliteral('<tag attribute=\\\"value\\\">Text</tag>', 'en')).", null, singletonFact(PRED, BASIC.createTuple(CONCRETE.createXMLLiteral("<tag attribute=\"value\">Text</tag>", "en"))), null);
	}

	public void testParseBinaryBuiltins() throws Exception {
		final String prog = "x(?X) :- 1 < 2, 3 <= 4, 5 > 6, 7 >= 8, 9 = 10, 11 != 12.";
		final IRule rule = BASIC.createRule(Arrays.asList(createLiteral("x", "X")),
				Arrays.asList(BASIC.createLiteral(true, BUILTIN.createLess(CONCRETE.createInteger(1), CONCRETE.createInteger(2))),
					BASIC.createLiteral(true, BUILTIN.createLessEqual(CONCRETE.createInteger(3), CONCRETE.createInteger(4))),
					BASIC.createLiteral(true, BUILTIN.createGreater(CONCRETE.createInteger(5), CONCRETE.createInteger(6))),
					BASIC.createLiteral(true, BUILTIN.createGreaterEqual(CONCRETE.createInteger(7), CONCRETE.createInteger(8))),
					BASIC.createLiteral(true, BUILTIN.createEqual(CONCRETE.createInteger(9), CONCRETE.createInteger(10))),
					BASIC.createLiteral(true, BUILTIN.createUnequal(CONCRETE.createInteger(11), CONCRETE.createInteger(12)))));
		assertResult(prog, Collections.singleton(rule), null, null);
	}

	public void testParseTenaryBuiltins() throws Exception {
		final String prog = "x(?X) :- 1 + 2 = 3, 4 - 5 = 6, 7 * 8 = 9, 10 / 11 = 12.";

		final IRule rule = BASIC.createRule(Arrays.asList(createLiteral("x", "X")),
				Arrays.asList(
					BASIC.createLiteral(true, BUILTIN.createAddBuiltin(CONCRETE.createInteger(1),
							CONCRETE.createInteger(2),
							CONCRETE.createInteger(3))),
					BASIC.createLiteral(true, BUILTIN.createSubtractBuiltin(CONCRETE.createInteger(4),
							CONCRETE.createInteger(5),
							CONCRETE.createInteger(6))),
					BASIC.createLiteral(true, BUILTIN.createMultiplyBuiltin(CONCRETE.createInteger(7),
							CONCRETE.createInteger(8),
							CONCRETE.createInteger(9))),
					BASIC.createLiteral(true, BUILTIN.createDivideBuiltin(CONCRETE.createInteger(10),
							CONCRETE.createInteger(11),
							CONCRETE.createInteger(12)))));

		assertResult(prog, Collections.singleton(rule), null, null);
	}

	/**
	 * Tests a single line comments.
	 */
	public void testComment() throws Exception {
		final IRule rulex = BASIC.createRule(Arrays.asList(createLiteral("x", "X")),
				Arrays.asList(createLiteral("x", "X")));
		final IRule ruley = BASIC.createRule(Arrays.asList(createLiteral("y", "X")),
				Arrays.asList(createLiteral("y", "X")));

		assertResult("//", null, null, null);
		assertResult("x(?X) :- x(?X). // asdf", Arrays.asList(rulex), null, null);
		assertResult("//\nx(?X) :- x(?X).", Arrays.asList(rulex), null, null);
		assertResult("x(?X) :- x(?X). // asdf\ny(?X) :- y(?X).", Arrays.asList(rulex, ruley), null, null);
	}

	/**
	 * Tests long comments.
	 */
	public void testLongComments() throws Exception {
		final IRule rulex = BASIC.createRule(Arrays.asList(createLiteral("x", "X")),
				Arrays.asList(createLiteral("x", "X")));
		final IRule ruley = BASIC.createRule(Arrays.asList(createLiteral("y", "X")),
				Arrays.asList(createLiteral("y", "X")));

		assertResult("/**/", null, null, null);
		assertResult("/* comment */", null, null, null);
		assertResult("/* com * ment */", null, null, null);
		assertResult("/* com * / ment */", null, null, null);
		assertResult("/* comment \n comment */", null, null, null);
		assertResult("/* comment */ x(?X) :- x(?X).", Arrays.asList(rulex), null, null);
		assertResult("/* comment \n comment */ x(?X) :- x(?X).", Arrays.asList(rulex), null, null);
		assertResult("x(?X) :- x(?X). /* comment */", Arrays.asList(rulex), null, null);
		assertResult("x(?X) :- x(?X). /* comment \n comment */", Arrays.asList(rulex), null, null);
		assertResult("x(?X) :- x(?X). /* comment */ y(?X) :- y(?X).", Arrays.asList(rulex, ruley), null, null);
		assertResult("x(?X) :- x(?X). /* comment \n comment */ y(?X) :- y(?X).", Arrays.asList(rulex, ruley), null, null);
	}


	/**
	 * Tests whether string terms can be delimited with single and double
	 * quotes.
	 */
	public void testQuotedString() throws Exception {
		final IPredicate PRED = BASIC.createPredicate("fact", 1);
		assertResult("fact('string').", null, singletonFact(PRED, BASIC.createTuple(TERM.createString("string"))), null);
		assertResult("fact(\"string\").", null, singletonFact(PRED, BASIC.createTuple(TERM.createString("string"))), null);
	}

	/**
	 * Tests whether various characters can be parsed as string term
	 * characters.
	 */
	public void testAnyCharStrings() throws Exception {
		final IPredicate PRED = BASIC.createPredicate("fact", 1);
		assertResult("fact('ß!§$%&/()=?*#äöüÄÖÜ;:_,.-€<>|').",
				null,
				singletonFact(PRED, BASIC.createTuple(TERM.createString("ß!§$%&/()=?*#äöüÄÖÜ;:_,.-€<>|"))),
				null);
	}

	/**
	 * Tests the correct parsing of quoted strings.
	 */
	public void testEscapedChars() throws Exception {
		final IPredicate PRED = BASIC.createPredicate("fact", 1);
		// tests \\
		assertResult("fact('str\\\\ing').", null, singletonFact(PRED, BASIC.createTuple(TERM.createString("str\\ing"))), null);
		// tests \t
		assertResult("fact('str\\ting').", null, singletonFact(PRED, BASIC.createTuple(TERM.createString("str\ting"))), null);
		// tests \n
		assertResult("fact('str\\ning').", null, singletonFact(PRED, BASIC.createTuple(TERM.createString("str\ning"))), null);
		// tests \r
		assertResult("fact('str\\ring').", null, singletonFact(PRED, BASIC.createTuple(TERM.createString("str\ring"))), null);
		// tests \f
		assertResult("fact('str\\fing').", null, singletonFact(PRED, BASIC.createTuple(TERM.createString("str\fing"))), null);
		// tests \'
		assertResult("fact('str\\'ing').", null, singletonFact(PRED, BASIC.createTuple(TERM.createString("str'ing"))), null);
		// tests \"
		assertResult("fact('str\\\"ing').", null, singletonFact(PRED, BASIC.createTuple(TERM.createString("str\"ing"))), null);
		// tests unicode escapes
		assertResult("fact('str\\uDEADing').", null, singletonFact(PRED, BASIC.createTuple(TERM.createString("str\uDEADing"))), null);
	}

	/**
	 * Test that the parsing of negated built-ins works as expected.
	 * @throws Exception
	 */
	public void testNegatedPredicateAndBuiltinEquivalence() throws Exception
	{
		String program1 = "p(?X, ?Y) :- q(?X), not LESS( ?X, ?Y ), not ADD( ?X, ?Y, 3 ).";
		String program2 = "p(?X, ?Y) :- q(?X), not ?X < ?Y, not ?X +?Y = 3.";
		
		Parser parser1 = new Parser();
		Parser parser2 = new Parser();
		parser1.parse( program1 );
		parser2.parse( program2 );
		
		IRule rule1 = parser1.getRules().iterator().next();
		IRule rule2 = parser2.getRules().iterator().next();
		
		assertEquals( rule1, rule2 );
	}
}
