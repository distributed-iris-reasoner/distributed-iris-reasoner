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
package org.deri.iris.optimisations.magicsets;

import static org.deri.iris.MiscHelper.createLiteral;
import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.BUILTIN;
import static org.deri.iris.factory.Factory.CONCRETE;
import static org.deri.iris.factory.Factory.TERM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.api.IProgramOptimisation.Result;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Tests the magic sets.
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri dot org)
 */
public class MagicSetsTest extends TestCase {

	private static Logger logger = LoggerFactory.getLogger(MagicSetsTest.class);
	
	/**
	 * Creates a magic literal.
	 * 
	 * @param symbol the predicate symbot to use for the literal
	 * @param ad the adornments
	 * @param t the terms for the literal
	 * @return the constructed magic literal
	 */
	private static ILiteral createMagicLiteral(final String symbol,
			final Adornment[] ad, final ITerm[] t) {
		return createAdornedLiteral(MagicSets.MAGIC_PREFIX + "_"  + symbol, ad, t);
	}

	/**
	 * Creates a labeled literal.
	 *
	 * @param symbol the predicate symbot to use for the literal
	 * @param ad the adornments
	 * @param t the terms for the literal
	 * @return the constructed magic literal
	 */
	private static ILiteral createLabeledLiteral(final String symbol,
			final Adornment[] ad, final ITerm[] t) {
		return createAdornedLiteral(MagicSets.LABEL_PREFIX + "_" + symbol, ad, t);
	}

	/**
	 * Creates an adorned literal.
	 * 
	 * @param symbol
	 *            the predicate symbot to use for the literal
	 * @param ad
	 *            the adornments
	 * @param t
	 *            the terms for the literal
	 * @return the constructed magic literal
	 */
	private static ILiteral createAdornedLiteral(final String symbol,
			final Adornment[] ad, final ITerm[] t) {
		return createAdornedLiteral(true, symbol, ad, t);
	}

	/**
	 * Creates an adorned literal.
	 * 
	 * @param positive <code>true</code> whether the resulting literal
	 * should be positive, or not.
	 * @param symbol the predicate symbot to use for the literal
	 * @param ad the adornments
	 * @param t the terms for the literal
	 * @return the constructed magic literal
	 */
	private static ILiteral createAdornedLiteral(final boolean positive, final String symbol,
			final Adornment[] ad, final ITerm[] t) {
		assert symbol != null: "The symbol must not be null";
		assert symbol.length() > 0: "The symbol must not be an empty string";
		assert ad != null: "The adornments must not be null";
		assert !Arrays.asList(ad).contains(null): "The adornments must not contain null";
		assert t != null: "The terms must not be null";
		assert !Arrays.asList(t).contains(null): "The terms must not contain null";

		// creating the new symbol with the adronemnts in it
		final StringBuilder newSymbol = new StringBuilder();
		newSymbol.append(symbol).append("_");
		for (final Adornment a : ad) {
			newSymbol.append(a);
		}

		return BASIC.createLiteral(positive,
				BASIC.createPredicate(newSymbol.toString(), t.length),
				BASIC.createTuple(t));
	}

	/**
	 * Creates a seed rule for a given atom.
	 * @param a the atom for which to create the seed rule
	 * @return the created rule
	 */
	private static IRule seedRule(final IAtom a) {
		assert a != null: "The atom must not be null";

		return seedRule(BASIC.createLiteral(true, a));
	}

	/**
	 * Creates a seed rule for a given atom.
	 * @param a the atom for which to create the seed rule
	 * @return the created rule
	 */
	private static IRule seedRule(final ILiteral l) {
		assert l != null: "The literal must not be null";

		return BASIC.createRule(Arrays.asList(l), Collections.<ILiteral>emptyList());
	}

	/**
	 * Parses a program and returns the result of the magic sets
	 * transformation.
	 * @param s the program to parse
	 * @return the transformation result
	 */
	private static Result getResult(final String s) throws ParserException {
		assert s != null: "The string to parse must not be null";

		final Parser p = new Parser();
		p.parse(s);
		final IQuery q = p.getQueries().iterator().next();
		return (new MagicSets()).optimise(p.getRules(), q);
	}

	/**
	 * Asserts the result of the transformation.
	 * @param expected the expected transformation result
	 * @param result the real transformation result
	 */
	private void assertResults(final Result expected, final Result result) {
		if (expected == null) { // test the failed transformation
			assertNull("The transformation should fail", result);
		} else {
			Collections.sort(expected.rules, AdornmentsTest.RC);
			Collections.sort(result.rules, AdornmentsTest.RC);

			assertEquals("The rules are computed as expected", expected.rules, result.rules);
			assertEquals("The query is not correct", expected.query, result.query);
		}
	}

	/**
	 * Tests whether the seed was constructed as it should be.
	 */
	public void testMagic0() throws Exception {
		final String prog = "sg(?X, ?Y) :- flat(?X, ?Y)."
					      + "sg(?X, ?Y) :- up(?X, ?Z1), sg(?Z1, ?Z2), flat(?Z2, ?Z3), sg(?Z3, ?Z4), down(?Z4, ?Y)."
					      + "?- sg('john', ?Y).";
		final Result result = getResult(prog);

		final List<IRule> ref = new ArrayList<IRule>();

		final Adornment[] BF = new Adornment[]{Adornment.BOUND, Adornment.FREE};
		final IVariable X = TERM.createVariable("X");
		final IVariable Y = TERM.createVariable("Y");
		final IVariable Z1 = TERM.createVariable("Z1");
		final IVariable Z2 = TERM.createVariable("Z2");
		final IVariable Z3 = TERM.createVariable("Z3");
		final IVariable Z4 = TERM.createVariable("Z4");

		// constructing the rule for the seed
		ref.add(seedRule(createMagicLiteral("sg", BF, new ITerm[]{TERM.createString("john")})));

		// constructing the magic rules

		// magic_sg_bf(Z1) :- magic_sg_bf(X), up(X, Z1)
		List<ILiteral> head = Arrays.asList(createMagicLiteral("sg", BF, new ITerm[]{Z1}));
		List<ILiteral> body = Arrays.asList(createMagicLiteral("sg", BF, new ITerm[]{X}), createLiteral("up", "X", "Z1"));
		ref.add(BASIC.createRule(head, body));
		// magic_sg_bf(Z3) :- magic_sg_bf(X), up(X, Z1), sg(Z1, Z2), flat(Z2, Z3)
		head = Arrays.asList(createMagicLiteral("sg", BF, new ITerm[]{Z3}));
		body = Arrays.asList(createMagicLiteral("sg", BF, new ITerm[]{X}),
				createLiteral("up", "X", "Z1"),
				createLiteral("sg", "Z1", "Z2"),
				createLiteral("flat", "Z2", "Z3"));
		ref.add(BASIC.createRule(head, body));

		// constructing the rewritten rules out of the normal ones

		// sg(X,Y) :- magic_sg_bf(X), flat(X, Y)
		head = Arrays.asList(createLiteral("sg", "X", "Y"));
		body = Arrays.asList(createMagicLiteral("sg", BF, new ITerm[]{X}), createLiteral("flat", "X", "Y"));
		ref.add(BASIC.createRule(head, body));
		// sg(X,Y) :- magic_sg_bf(X), up(X, Z1), sg(Z1, Z2), flat(Z2, Z3), sg(Z3, Z4), down(Z4, Y)
		head = Arrays.asList(createLiteral("sg", "X", "Y"));
		body = Arrays.asList(createMagicLiteral("sg", BF, new ITerm[]{X}),
				createLiteral("up", "X", "Z1"),
				createLiteral("sg", "Z1", "Z2"),
				createLiteral("flat", "Z2", "Z3"),
				createLiteral("sg", "Z3", "Z4"),
				createLiteral("down", "Z4", "Y"));
		ref.add(BASIC.createRule(head, body));

		Collections.sort(ref, AdornmentsTest.RC);
		Collections.sort(result.rules, AdornmentsTest.RC);
		assertEquals("The rules don't match", ref, result.rules);
		// TODO: match the query
	}

	/**
	 * Tests whether the seed was constructed as it should be.
	 */
	public void testMagic1() throws Exception {
		final String prog = "a(?X, ?Y, ?Z) :- c(?X, ?Y, ?Z)."
						   + "a(?X, ?Y, ?Z) :- b(?X, ?A), a(?X, ?A, ?B), c(?B, ?Y, ?Z)."
						   + "?- a('john', 'mary', ?Y).";
		final Result result = getResult(prog);

		final List<IRule> ref = new ArrayList<IRule>();

		final Adornment[] bbf = new Adornment[]{Adornment.BOUND, Adornment.BOUND, Adornment.FREE};
		final Adornment[] bff = new Adornment[]{Adornment.BOUND, Adornment.FREE, Adornment.FREE};
		final IVariable A = TERM.createVariable("A");
		final IVariable B = TERM.createVariable("B");
		final IVariable X = TERM.createVariable("X");
		final IVariable Y = TERM.createVariable("Y");
		final IVariable Z = TERM.createVariable("Z");
		final IVariable[] XA = new IVariable[]{X, A};
		final IVariable[] XY = new IVariable[]{X, Y};

		// constructing the rule for the seed

		ref.add(seedRule(createMagicLiteral("a", bbf, new ITerm[]{TERM.createString("john"), TERM.createString("mary")})));

		// constructing the magic/labeled rules rules

		// label_a_1_bbf(X, A) :- magic_a_bbf(X, Y), b(X, A)
		List<ILiteral> head = Arrays.asList(createLabeledLiteral("a_1", bbf, XA));
		List<ILiteral> body = Arrays.asList(createMagicLiteral("a", bbf, XY), createLiteral("b", "X", "A"));
		ref.add(BASIC.createRule(head, body));
		// label_a_2_bff(X) :- magic_a_bbf(X, Y)
		head = Arrays.asList(createLabeledLiteral("a_2", bff, new ITerm[]{X}));
		body = Arrays.asList(createMagicLiteral("a", bbf, XY));
		ref.add(BASIC.createRule(head, body));
		// magic_a_bbf(X, A) :- label_a_1_bbf(X, A), label_a_2_bff(X, A)
		head = Arrays.asList(createMagicLiteral("a", bbf, XA));
		body = Arrays.asList(createLabeledLiteral("a_1", bbf, XA),
				createLabeledLiteral("a_2", bff, new ITerm[]{X}));
		ref.add(BASIC.createRule(head, body));

		// constructing the rewritten rules out of the normal ones

		// a(X, Y, Z) :- magic_a_bbf(X, Y), c(X, Y, Z)
		head = Arrays.asList(createLiteral("a", "X", "Y", "Z"));
		body = Arrays.asList(createMagicLiteral("a", bbf, XY),
				createLiteral("c", "X", "Y", "Z"));
		ref.add(BASIC.createRule(head, body));
		// a(X, Y, Z) :- magic_a_bbf(X, Y), b(X, A), a(X, A, B), c(B, Y, Z)
		head = Arrays.asList(createLiteral("a", "X", "Y", "Z"));
		body = Arrays.asList(createMagicLiteral("a", bbf, XY),
				createLiteral("b", "X", "A"),
				createLiteral("a", "X", "A", "B"),
				createLiteral("c", "B", "Y", "Z"));
		ref.add(BASIC.createRule(head, body));

		Collections.sort(ref, AdornmentsTest.RC);
		Collections.sort(result.rules, AdornmentsTest.RC);
		assertEquals("The rules don't match", ref, result.rules);
		// TODO: match the query
	}

	/**
	 * Tests that constatns in bodyliterals are determined as bound.
	 */
	public void testBoundConstant() throws Exception {
		final String prog = "a(?X, ?Y) :- b(?X, ?Z), c('a', ?Z, ?Y). \n" +
			"c(?X, ?Y, ?Z) :- x(?X, ?Y, ?Z). \n" +
			"?-a('john', ?Y).";

		final Result result = getResult(prog);

		final ITerm a = TERM.createString("a");
		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm Z = TERM.createVariable("Z");
		final ITerm[] XYZ = new ITerm[]{X, Y, Z};
		final Adornment[] bbf = new Adornment[]{Adornment.BOUND, Adornment.BOUND, Adornment.FREE};
		final Adornment[] bf = new Adornment[]{Adornment.BOUND, Adornment.FREE};
		final ILiteral b = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("b", 2), BASIC.createTuple(X, Z)));
		final ILiteral c = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 3), BASIC.createTuple(TERM.createString("a"), Z, Y)));
		final ILiteral x = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("x", 3), BASIC.createTuple(X, Y, Z)));

		final List<IRule> ref = new ArrayList<IRule>();
		// magic_c_bbf(a, Z) :- magic_a_bf(X), b(X, Z)
		ref.add(BASIC.createRule(Arrays.asList(createMagicLiteral("c", bbf, new ITerm[]{a, Z})),
					Arrays.asList(createMagicLiteral("a", bf, new ITerm[]{X}), b)));
		// a(X, Y) :- magic_a_bf(X), b(X, Z), c(a, Z, Y)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("a", "X", "Y")),
					Arrays.asList(createMagicLiteral("a", bf, new ITerm[]{X}),
						b,
						c)));
		// c(X, Y, Z) :- magic_c_bbf(X, Y), x(X, Y, Z)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("c", "X", "Y", "Z")),
					Arrays.asList(createMagicLiteral("c", bbf, new ITerm[]{X, Y}), x)));
		// magic_a_bf('john') :- .
		ref.add(seedRule(createMagicLiteral("a", bf, new ITerm[]{TERM.createString("john")})));

		Collections.sort(ref, AdornmentsTest.RC);
		Collections.sort(result.rules, AdornmentsTest.RC);
		assertEquals("The rules don't match", ref, result.rules);
	}

	/**
	 * Tests whether useless magic predicates (magic_q^f()) will be created,
	 * or not.
	 */
	public void testStupidRules() throws Exception {
		final String prog = "q(?X) :- s(?X), not p(?X).\n" +
			"p(?X) :- r(?X).\n" +
			"r(?X) :- t(?X).\n" +
			"?- q(?X).";

		assertNull("The trainsformation should fail.", getResult(prog));
	}

	/**
	 * Tests conjunctive queries.
	 * @see <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=1798276&group_id=167309&atid=842434">bug #1798276: Magic Sets evaluation does not allow conjunctive queries</a>
	 */
	public void testConjunctiveQuery0() throws Exception {
		final String prog = "p(?X, ?Y) :- c(?X, ?Y).\n" +
			"r(?X, ?Y, ?Z) :- c(?X, ?Y, ?Z).\n" +
			"s(?X, ?Y) :- c(?X, ?Y).\n" +
			"?- p(?X, 'a'), r('b', ?X, ?Y), s('e', ?Y).";

		final Result result = getResult(prog);

		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm Z = TERM.createVariable("Z");
		final ITerm[] XY = new ITerm[]{X, Y};
		final ITerm[] XYZ = new ITerm[]{X, Y, Z};
		final Adornment[] fb = new Adornment[]{Adornment.FREE, Adornment.BOUND};
		final Adornment[] bb = new Adornment[]{Adornment.BOUND, Adornment.BOUND};
		final Adornment[] bbf = new Adornment[]{Adornment.BOUND, Adornment.BOUND, Adornment.FREE};
		final ILiteral c2 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 2), BASIC.createTuple(XY)));
		final ILiteral c3 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 3), BASIC.createTuple(XYZ)));
		final ILiteral p2 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("p", 2), BASIC.createTuple(X, TERM.createString("a"))));
		final ILiteral r3 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("r", 3), BASIC.createTuple(TERM.createString("b"), X, Y)));

		final List<IRule> ref = new ArrayList<IRule>();
		// magic_r_bbf(b, X) :- p(X, a)
		ref.add(BASIC.createRule(Arrays.asList(createMagicLiteral("r", bbf,
							new ITerm[]{TERM.createString("b"), X})),
					Arrays.asList(p2)));
		// magic_s_bb(e, Y) :- p(X, a), r(b, X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createMagicLiteral("s", bb,
							new ITerm[]{TERM.createString("e"), Y})),
					Arrays.asList(p2, r3)));
		// p(X, Y) :- magic_p_fb(Y), c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("p", "X", "Y")),
					Arrays.asList(createMagicLiteral("p", fb, new ITerm[]{Y}), c2)));
		// r(X, Y, Z) :- magic_r_bbf(X, Y), c(X, Y, Z)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("r", "X", "Y", "Z")),
					Arrays.asList(createMagicLiteral("r", bbf, XY), c3)));
		// s(X, Y) :- magic_s_bb(X, Y), c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("s", "X", "Y")),
					Arrays.asList(createMagicLiteral("s", bb, XY), c2)));
		// p_fb('a') :- .
		ref.add(seedRule(createMagicLiteral("p", fb, new ITerm[]{TERM.createString("a")})));

		Collections.sort(ref, AdornmentsTest.RC);
		Collections.sort(result.rules, AdornmentsTest.RC);
		assertEquals("The rules don't match", ref, result.rules);
	}

	/**
	 * Tests conjunctive queries.
	 * @see <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=1798276&group_id=167309&atid=842434">bug #1798276: Magic Sets evaluation does not allow conjunctive queries</a>
	 */
	public void testConjunctiveQuery1() throws Exception {
		final String prog = "p(?X, ?Y) :- c(?X, ?Y).\n" +
			"r(?X, ?Y, ?Z) :- c(?X, ?Y, ?Z).\n" +
			"s(?X, ?Y) :- c(?X, ?Y).\n" +
			"?- p(?X, ?Y), r('b', ?X, ?Z), s('e', ?Z).";

		final Result result = getResult(prog);

		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm Z = TERM.createVariable("Z");
		final ITerm[] XY = new ITerm[]{X, Y};
		final ITerm[] XYZ = new ITerm[]{X, Y, Z};
		final Adornment[] ff = new Adornment[]{Adornment.FREE, Adornment.FREE};
		final Adornment[] bb = new Adornment[]{Adornment.BOUND, Adornment.BOUND};
		final Adornment[] bbf = new Adornment[]{Adornment.BOUND, Adornment.BOUND, Adornment.FREE};
		final ILiteral c2 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 2), BASIC.createTuple(XY)));
		final ILiteral c3 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 3), BASIC.createTuple(XYZ)));
		final ILiteral r3 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("r", 3), BASIC.createTuple(TERM.createString("b"), X, Z)));

		final List<IRule> ref = new ArrayList<IRule>();
		// magic_r_bbf(b, X) :- p(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createMagicLiteral("r", bbf,
							new ITerm[]{TERM.createString("b"), X})),
					Arrays.asList(createLiteral("p", "X", "Y"))));
		// magic_s_bb(e, Z) :- p(X, Y), r(b, X, Z)
		ref.add(BASIC.createRule(Arrays.asList(createMagicLiteral("s", bb,
							new ITerm[]{TERM.createString("e"), Z})),
					Arrays.asList(createLiteral("p", "X", "Y"), r3)));
		// r(X, Y, Z) :- magic_r_bbf(X, Y), c(X, Y, Z)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("r", "X", "Y", "Z")),
					Arrays.asList(createMagicLiteral("r", bbf, XY), c3)));
		// s(X, Y) :- magic_s_bb(X, Y), c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("s", "X", "Y")),
					Arrays.asList(createMagicLiteral("s", bb, XY), c2)));
		// p(X, Y) :- c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("p", "X", "Y")), Arrays.asList(c2)));
		// p^ff() :- TRUE
		ref.add(seedRule(createMagicLiteral("p", ff, new ITerm[]{})));

		Collections.sort(ref, AdornmentsTest.RC);
		Collections.sort(result.rules, AdornmentsTest.RC);
		assertEquals("The rules don't match", ref, result.rules);
	}

	/**
	 * Tests conjunctive queries.
	 * @see <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=1798276&group_id=167309&atid=842434">bug #1798276: Magic Sets evaluation does not allow conjunctive queries</a>
	 */
	public void testConjunctiveQuery2() throws Exception {
		final String prog = "p(?X, ?Y) :- c(?X, ?Y).\n" +
			"r(?X, ?Y, ?Z) :- c(?X, ?Y, ?Z).\n" +
			"s(?X, ?Y) :- c(?X, ?Y).\n" +
			"?- p('b', 'a'), r('b', ?X, ?Y), s('e', ?Y).";

		final Result result = getResult(prog);

		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm Z = TERM.createVariable("Z");
		final ITerm[] XY = new ITerm[]{X, Y};
		final ITerm[] XYZ = new ITerm[]{X, Y, Z};
		final Adornment[] bb = new Adornment[]{Adornment.BOUND, Adornment.BOUND};
		final Adornment[] bff = new Adornment[]{Adornment.BOUND, Adornment.FREE, Adornment.FREE};
		final ILiteral c2 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 2), BASIC.createTuple(XY)));
		final ILiteral c3 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 3), BASIC.createTuple(XYZ)));
		final ILiteral p2 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("p", 2),
					BASIC.createTuple(TERM.createString("b"), TERM.createString("a"))));
		final ILiteral r3 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("r", 3), BASIC.createTuple(TERM.createString("b"), X, Y)));

		final List<IRule> ref = new ArrayList<IRule>();
		// magic_r_bff(b) :- p(b, a)
		ref.add(BASIC.createRule(Arrays.asList(createMagicLiteral("r", bff,
							new ITerm[]{TERM.createString("b")})),
					Arrays.asList(p2)));
		// magic_s_bb(e, Y) :- p(b, a), r(b, X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createMagicLiteral("s", bb,
							new ITerm[]{TERM.createString("e"), Y})),
					Arrays.asList(p2, r3)));
		// p(X, Y) :- magic_p_bb(X, Y), c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("p", "X", "Y")),
					Arrays.asList(createMagicLiteral("p", bb, XY), c2)));
		// r(X, Y, Z) :- magic_r_bff(X), c(X, Y, Z)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("r", "X", "Y", "Z")),
					Arrays.asList(createMagicLiteral("r", bff, new ITerm[]{X}), c3)));
		// s(X, Y) :- magic_s_bb(X, Y), c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("s", "X", "Y")),
					Arrays.asList(createMagicLiteral("s", bb, XY), c2)));
		// p^ff() :- TRUE
		ref.add(seedRule(createMagicLiteral("p", bb, new ITerm[]{TERM.createString("b"), TERM.createString("a")})));

		Collections.sort(ref, AdornmentsTest.RC);
		Collections.sort(result.rules, AdornmentsTest.RC);
		assertEquals("The rules don't match", ref, result.rules);
	}

	/**
	 * Tests conjunctive queries.
	 * @see <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=1798276&group_id=167309&atid=842434">bug #1798276: Magic Sets evaluation does not allow conjunctive queries</a>
	 */
	public void testConjunctiveQuery3() throws Exception {
		final String prog = "p(?X, ?Y) :- c(?X, ?Y).\n" +
			"r(?X, ?Y) :- c(?X, ?Y).\n" +
			"s(?W, ?X, ?Y, ?Z) :- c(?W, ?X, ?Y, ?Z).\n" +
			"?- p(?W, ?X), r(?Y, ?Z), s(?W, ?X, ?Y, ?Z).";

		assertNull("The trainsformation should fail.", getResult(prog));
	}

	/**
	 * Tests conjunctive queries.
	 * @see <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=1798276&group_id=167309&atid=842434">bug #1798276: Magic Sets evaluation does not allow conjunctive queries</a>
	 */
	public void testComplicatedConjunctiveQuery0() throws Exception {
		final String prog = "p(?X, ?Y) :- c(?X, ?Y), r(?Z, ?Y, ?X).\n" +
			"r(?X, ?Y, ?Z) :- c(?X, ?Y, ?Z).\n" +
			"s(?X, ?Y) :- c(?X, ?Y).\n" +
			"?- p(?X, ?Y), r('b', ?X, ?Z), s('e', ?Z).";

		final Result result = getResult(prog);

		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm Z = TERM.createVariable("Z");
		final ITerm[] XY = new ITerm[]{X, Y};
		final ITerm[] YX = new ITerm[]{Y, X};
		final ITerm[] YZ = new ITerm[]{Y, Z};
		final ITerm[] XYZ = new ITerm[]{X, Y, Z};
		final ITerm[] ZYX = new ITerm[]{Z, Y, X};
		final Adornment[] ff = new Adornment[]{Adornment.FREE, Adornment.FREE};
		final Adornment[] bb = new Adornment[]{Adornment.BOUND, Adornment.BOUND};
		final Adornment[] fbb = new Adornment[]{Adornment.FREE, Adornment.BOUND, Adornment.BOUND};
		final Adornment[] bbf = new Adornment[]{Adornment.BOUND, Adornment.BOUND, Adornment.FREE};
		final ILiteral c2 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 2), BASIC.createTuple(XY)));
		final ILiteral c3 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 3), BASIC.createTuple(XYZ)));
		final ILiteral r3 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("r", 3), BASIC.createTuple(TERM.createString("b"), X, Z)));

		final List<IRule> ref = new ArrayList<IRule>();
		// magic_r_bbf(b, X) :- p(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createMagicLiteral("r", bbf, new ITerm[]{TERM.createString("b"), X})),
					Arrays.asList(createLiteral("p", "X", "Y"))));
		// magic_r_fbb(Y, X) :- c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createMagicLiteral("r", fbb, YX)), Arrays.asList(c2)));
		// magic_s_bb(e, Z) :- p(X, Y), r(b, X, Z)
		ref.add(BASIC.createRule(Arrays.asList(createMagicLiteral("s", bb, new ITerm[]{TERM.createString("e"), Z})),
					Arrays.asList(createLiteral("p", "X", "Y"), r3)));
		// r(X, Y, Z) :- magic_r_fbb(Y, Z), c(X, Y, Z)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("r", "X", "Y", "Z")),
					Arrays.asList(createMagicLiteral("r", fbb, YZ), c3)));
		// r(X, Y, Z) :- magic_r_bbf(X, Y), c(X, Y, Z)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("r", "X", "Y", "Z")),
					Arrays.asList(createMagicLiteral("r", bbf, XY), c3)));
		// p(X, Y) :- c(X, Y), r(Z, Y, X)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("p", "X", "Y")),
					Arrays.asList(c2, createLiteral("r", "Z", "Y", "X"))));
		// s(X, Y) :- magic_s_bb(X, Y), c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("s", "X", "Y")),
					Arrays.asList(createMagicLiteral("s", bb, XY), c2)));
		// p^ff() :- TRUE
		ref.add(seedRule(createMagicLiteral("p", ff, new ITerm[]{})));

		Collections.sort(ref, AdornmentsTest.RC);
		Collections.sort(result.rules, AdornmentsTest.RC);
		assertEquals("The rules don't match", ref, result.rules);
	}

	/**
	 * Tests conjunctive queries.
	 * @see <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=1798276&group_id=167309&atid=842434">bug #1798276: Magic Sets evaluation does not allow conjunctive queries</a>
	 */
	public void testComplicatedConjunctiveQuery1() throws Exception {
		final String prog = "p(?X, ?Y) :- c(?X, ?Y), s(?Z, ?T).\n" +
			"r(?X, ?Y, ?Z) :- c(?X, ?Y, ?Z).\n" +
			"s(?X, ?Y) :- c(?X, ?Y).\n" +
			"?- p(?X, ?Y), r('b', ?X, ?Z), s('e', ?Z).";

		final Result result = getResult(prog);

		final ITerm T = TERM.createVariable("T");
		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm Z = TERM.createVariable("Z");
		final ITerm[] XY = new ITerm[]{X, Y};
		final ITerm[] XYZ = new ITerm[]{X, Y, Z};
		final Adornment[] ff = new Adornment[]{Adornment.FREE, Adornment.FREE};
		final Adornment[] bb = new Adornment[]{Adornment.BOUND, Adornment.BOUND};
		final Adornment[] bbf = new Adornment[]{Adornment.BOUND, Adornment.BOUND, Adornment.FREE};
		final ILiteral c2 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 2), BASIC.createTuple(XY)));
		final ILiteral c3 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 3), BASIC.createTuple(XYZ)));
		final ILiteral r3 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("r", 3), BASIC.createTuple(TERM.createString("b"), X ,Z)));

		final List<IRule> ref = new ArrayList<IRule>();
		// magic_r_bbf(b, X) :- p(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createMagicLiteral("r", bbf, new ITerm[]{TERM.createString("b"), X})),
					Arrays.asList(createLiteral("p", "X", "Y"))));
		// magic_s_bb(e, Z) :- p(X, Y), r(b, X, Z)
		ref.add(BASIC.createRule(Arrays.asList(createMagicLiteral("s", bb, new ITerm[]{TERM.createString("e"), Z})),
					Arrays.asList(createLiteral("p", "X", "Y"), r3)));
		// r(X, Y, Z) :- magic_r_bbf(X, Y), c(X, Y, Z)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("r", "X", "Y", "Z")),
					Arrays.asList(createMagicLiteral("r", bbf, XY), c3)));
		// p(X, Y) :- c(X, Y), s(Z, T)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("p", "X", "Y")),
					Arrays.asList(c2, createLiteral("s", "Z", "T"))));
		// s(X, Y) :- magic_s_bb(X, Y), c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("s", "X", "Y")),
					Arrays.asList(createMagicLiteral("s", bb, XY), c2)));
		// s(X, Y) :- c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createLiteral("s", "X", "Y")), Arrays.asList(c2)));
		// magic_p_ff() :- .
		ref.add(seedRule(createMagicLiteral("p", ff, new ITerm[]{})));

		Collections.sort(ref, AdornmentsTest.RC);
		Collections.sort(result.rules, AdornmentsTest.RC);
		assertEquals("The rules don't match", ref, result.rules);
	}

	/**
	 * Test for repeated literals.
	 * @see <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=1829204&group_id=167309&atid=842434">bug #1829204: Repeated literal in query fails with magic sets</a>
	 */
	public void testRepeatedLiteralQuery() throws Exception {
		final String prog = "p(1).\n" +
			"?-p(1),p(1).\n";

		final Result result = getResult(prog);

		final Adornment[] b = new Adornment[]{Adornment.BOUND};
		final ILiteral magic_p = createMagicLiteral("p", b, new ITerm[]{CONCRETE.createInteger(1)});
		final ILiteral p1 = BASIC.createLiteral(true,
				BASIC.createAtom(BASIC.createPredicate("p", 1),
				BASIC.createTuple(CONCRETE.createInteger(1))));

		final List<IRule> ref = new ArrayList<IRule>();
		// magic_p^b(1) :- .
		ref.add(BASIC.createRule(Arrays.asList(magic_p), Collections.<ILiteral>emptyList()));
		// magic_p^b(1) :- p(1).
		ref.add(BASIC.createRule(Arrays.asList(magic_p), Arrays.asList(p1)));

		Collections.sort(ref, AdornmentsTest.RC);
		Collections.sort(result.rules, AdornmentsTest.RC);
		assertEquals("The rules don't match", ref, result.rules);
	}

	/**
	 * Test some transformations, which should fail.
	 */
	public void testFailingTransformation() throws Exception {
		assertNull("A query with only variables should fail",
				getResult("?- a(?A, ?B), b(?C, ?D), c(?E, ?F)."));
		assertNull("A query with only constants in builtins should fail",
				getResult("?- a(?A, ?B), LESS('a', ?D), c(?E, ?F)."));
		assertNotNull("A query with constants in literals should succeed",
				getResult("?- a(?A, ?B), b('a', ?D), c(?E, ?F)."));
	}

	/**
	 * Test to ensure the correct handling of conjunctive queries with
	 * negative literals.
	 * @see <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1904505&group_id=167309&atid=842434">bug #1904505: magic sets: negative rules with negative query literals</a>
	 */
	public void testNegativeConjunctiveQuery() throws Exception {
		final String prog = "?-p(?X, ?Y), not q(?X, 3).\n"
			+ "p(?X, ?Y) :- a(?X, ?Y).\n"
			+ "q(?X, ?Y) :- b(?X, ?Y).\n";

		final Result result = getResult(prog);

		final Adornment[] BB = new Adornment[]{Adornment.BOUND, Adornment.BOUND};
		final Adornment[] FF = new Adornment[]{Adornment.FREE, Adornment.FREE};
		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm[] XY = new ITerm[]{X, Y};
		final ITerm[] X3 = new ITerm[]{X, CONCRETE.createInteger(3)};

		final List<IRule> rules = new ArrayList<IRule>();

		// magic_p_ff() :- .
		rules.add(BASIC.createRule(Arrays.asList(createMagicLiteral("p", FF, new ITerm[]{})),
					Collections.<ILiteral>emptyList()));
		// magic_q_bb(?X, 3) :- p(?X, ?Y).
		rules.add(BASIC.createRule(Arrays.asList(createMagicLiteral("q", BB, X3)),
					Arrays.asList(createLiteral("p", "X", "Y"))));
		// p(?X, ?Y) :- a(?X, ?Y).
		rules.add(BASIC.createRule(Arrays.asList(createLiteral("p", "X", "Y")),
					Arrays.asList(createLiteral("a", "X", "Y"))));
		// q(?X, ?Y) :- magic_q_bb(?X, ?Y), b(?X, ?Y).
		rules.add(BASIC.createRule(Arrays.asList(createLiteral("q", "X", "Y")),
					Arrays.asList(createMagicLiteral("q", BB, XY), createLiteral("b", "X", "Y"))));

		// ?- p(?X, ?Y), !q(?X, 3).
		final IQuery query = BASIC.createQuery(Arrays.asList(createLiteral("p", "X", "Y"),
					BASIC.createLiteral(false, BASIC.createPredicate("q", 2), BASIC.createTuple(X3))));

		final Result expected = new Result(rules, query);

		assertResults(new Result(rules, query), result);
	}

	/**
	 * <p>
	 * Test to ensure correct creation of labaled rules.
	 * </p>
	 * <p>
	 * This test checks for 2 problems:
	 * <ul>
	 * <li>rule heads can not be negative (even if they are labeled)</li>
	 * <li>labeled rules must be save</li>
	 * </ul>
	 * </p>
	 * @see <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=1907086&group_id=167309&atid=842434">bug #1907086: magic sets: labeled rules are not consturced correctly</a>
	 */
	public void testUnsaveLabeledRuleCreation() throws Exception {
		final String prog = "c(?X, ?Y) :- e(?X, ?Y).\n"
			+ "b(?X, ?Y) :- d(?X, ?Y).\n"
			+ "a(?X, ?Y) :- b(?X, ?Y), not c(?X, ?Y).\n"
			+ "?- a(2, ?Y).\n";
		final Result result = getResult(prog);

		final Adornment[] BB = new Adornment[]{Adornment.BOUND, Adornment.BOUND};
		final Adornment[] BF = new Adornment[]{Adornment.BOUND, Adornment.FREE};
		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm[] XY = new ITerm[]{X, Y};

		// construct the rules
		final List<IRule> rules = new ArrayList<IRule>();

		// a(?X, ?Y) :- magic_a_bf(?X), b(?X, ?Y), !c(?X, ?Y).
		rules.add(BASIC.createRule(Arrays.asList(createLiteral("a", "X", "Y")),
					Arrays.asList(createMagicLiteral("a", BF, new ITerm[]{X}),
						createLiteral("b", "X", "Y"),
						createLiteral(false, "c", "X", "Y"))));
		// b(?X, ?Y) :- magic_b_bf(?X), d(?X, ?Y).
		rules.add(BASIC.createRule(Arrays.asList(createLiteral("b", "X", "Y")),
				Arrays.asList(createMagicLiteral("b", BF, new ITerm[]{X}),
					createLiteral("d", "X", "Y"))));
		// c(?X, ?Y) :- magic_c_bb(?X, ?Y), e(?X, ?Y).
		rules.add(BASIC.createRule(Arrays.asList(createLiteral("c", "X", "Y")),
				Arrays.asList(createMagicLiteral("c", BB, XY),
					createLiteral("e", "X", "Y"))));
		// magic_a_bf(2) :- .
		rules.add(seedRule(createMagicLiteral("a", BF, new ITerm[]{CONCRETE.createInteger(2)})));
		// magic_b_bf(?X) :- magic_a_bf(?X).
		rules.add(BASIC.createRule(Arrays.asList(createMagicLiteral("b", BF, new ITerm[]{X})),
				Arrays.asList(createMagicLiteral("a", BF, new ITerm[]{X}))));
		// label_c_1_bf(?X) :- magic_a_bf(?X).
		rules.add(BASIC.createRule(Arrays.asList(createLabeledLiteral("c_1", BF, new ITerm[]{X})),
				Arrays.asList(createMagicLiteral("a", BF, new ITerm[]{X}))));
		// label_c_2_bb(?X, ?Y) :- magic_a_bf(?X), b(?X, ?Y).
		rules.add(BASIC.createRule(Arrays.asList(createLabeledLiteral("c_2", BB, XY)),
				Arrays.asList(createMagicLiteral("a", BF, new ITerm[]{X}),
					createLiteral("b", "X", "Y"))));
		// magic_c_bb(?X, ?Y) :- label_c_1_bf(?X), label_c_2_bb(?X, ?Y).
		rules.add(BASIC.createRule(Arrays.asList(createMagicLiteral("c", BB, XY)),
				Arrays.asList(createLabeledLiteral("c_1", BF, new ITerm[]{X}),
					createLabeledLiteral("c_2", BB, XY))));

		// construct the query
		// ?- a(2, ?Y).
		final IQuery query = BASIC.createQuery(Arrays.asList(BASIC.createLiteral(
						true,
						BASIC.createPredicate("a", 2),
						BASIC.createTuple(CONCRETE.createInteger(2), Y))));

		assertResults(new Result(rules, query), result);
	}

	/**
	 * Checks, whether builtins are keept correctly after the magic sets
	 * transformation.
	 * @see <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1919554&group_id=167309&atid=842434">bug #1919554: magic sets with builtins don't evaluate correctly</a>
	 */
	public void testKeepBuiltins() throws Exception {
		final String prog = "a(?X, ?Y) :- b(?X, ?Z), ?Z+1=?Y.\n"
			+ "?- a('john', ?X).\n";
		final Result result = getResult(prog);

		final Adornment[] BF = new Adornment[]{Adornment.BOUND, Adornment.FREE};
		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm Z = TERM.createVariable("Z");
		final ITerm[] XY = new ITerm[]{X, Y};
		final ITerm john = TERM.createString("john");

		final List<IRule> rules = new ArrayList<IRule>();

		// a(?X, ?Y) :- magic_a_bf(?X), b(?X, ?Z), ADD(?Z, 1, ?Y).
		rules.add(BASIC.createRule(Arrays.asList(createLiteral("a", "X", "Y")),
					Arrays.asList(createMagicLiteral("a", BF, new ITerm[]{X}),
						createLiteral("b", "X", "Z"),
						BASIC.createLiteral(true,
							BUILTIN.createAddBuiltin(Z, CONCRETE.createInteger(1), Y)))));
		// magic_a_bf('john') :- .
		rules.add(seedRule(createMagicLiteral("a", BF, new ITerm[]{john})));

		// ?- a('john', ?X).
		final IQuery query = BASIC.createQuery(Arrays.asList(BASIC.createLiteral(true,
						BASIC.createPredicate("a", 2),
						BASIC.createTuple(john, X))));

		assertResults(new Result(rules, query), result);
	}

	/**
	 * Ensures, that no magic rules are created for builtins in conjunctive
	 * queries.
	 */
	public void testConjunctiveBuiltinAdorning() throws Exception {
		final String prog = "?- a('john'), ?X='john'.\n";
		final Result result = getResult(prog);

		final Adornment[] B = new Adornment[]{Adornment.BOUND};
		final ITerm X = TERM.createVariable("X");
		final ITerm john = TERM.createString("john");

		final List<IRule> rules = new ArrayList<IRule>();

		// magic_a_b('john') :- .
		rules.add(seedRule(createMagicLiteral("a", B, new ITerm[]{john})));

		// ?- a('john'), EQUAL(?X, 'john').
		final IQuery query = BASIC.createQuery(Arrays.asList(BASIC.createLiteral(true,
						BASIC.createPredicate("a", 1),
						BASIC.createTuple(john)),
					BASIC.createLiteral(true, BUILTIN.createEqual(X, john))));

		assertResults(new Result(rules, query), result);
	}

	/**
	 * Prints a program and the resulting magic program in a formated
	 * way.
	 * @param name the name to identify the test
	 * @param prog the input program
	 * @param result the magic set result
	 */
	private static void printDebug(final String name, final String prog, final Result result) {
		printDebug(name, prog, null, result);
	}

	/**
	 * Prints out a program, resulting magic program and the expected
	 * result.
	 * @param name the name to identify the printing
	 * @param prog the string representation of the input program
	 * @param expected the expected transformation result
	 * @param result the real outcome of the transformation
	 */
	private static void printDebug(final String name, final String prog,
			final Result expected, final Result result) {
		logger.debug("---");
		logger.debug(name);
		logger.debug("\tinput:");
		logger.debug(prog);

		if (expected != null) {
			logger.debug("\texpected:");
			logger.debug(resultString(expected));
		}

		logger.debug("\tresult:");
		logger.debug(resultString(result));
	}

	/**
	 * Transforms the result to a string.
	 * @param r the result
	 * @return the string representation
	 */
	private static String resultString(final Result r) {
		assert r != null: "The result must not be null";

		final StringBuilder buffer = new StringBuilder();

		// sorting the reslt rules
		final List<IRule> sortRules = new ArrayList<IRule>(r.rules);
		Collections.sort(sortRules, AdornmentsTest.RC);

		// printing the result rules
		for (final IRule rule : sortRules) {
			buffer.append(rule).append(System.getProperty("line.separator"));
		}
		buffer.append(System.getProperty("line.separator"));
		buffer.append(r.query).append(System.getProperty("line.separator"));
		return buffer.toString();
	}

	public static Test suite() {
		return new TestSuite(MagicSetsTest.class, MagicSetsTest.class.getSimpleName());
	}
}
