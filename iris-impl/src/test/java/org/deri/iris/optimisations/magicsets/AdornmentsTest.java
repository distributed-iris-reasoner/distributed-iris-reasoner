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

// TODO: test handling of builtins

import static org.deri.iris.MiscHelper.createLiteral;
import static org.deri.iris.MiscHelper.createVarList;
import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.TERM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import org.deri.iris.optimisations.magicsets.AdornedProgram.AdornedPredicate;
import org.deri.iris.optimisations.magicsets.AdornedProgram.AdornedRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Tests the adornments.
 * </p>
 * <p>
 * $Id: AdornmentsTest.java,v 1.12 2007-10-30 10:35:50 poettler_ric Exp $
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri dot org)
 * @version $Revision: 1.12 $
 */
public class AdornmentsTest extends TestCase {
	
	private static Logger logger = LoggerFactory
			.getLogger(AdornmentsTest.class);

	public static final Comparator<IRule> RC = new RuleComparator();

	public static Test suite() {
		return new TestSuite(AdornmentsTest.class, AdornmentsTest.class
				.getSimpleName());
	}

	/**
	 * Parses a string to a program and then adorns it.
	 * @param prog the program to parse
	 * @return the adorned version of the program
	 */
	private static AdornedProgram getAdornedProgram(final String prog) throws ParserException {
		assert prog != null: "The prog string must not be null";

		final Parser p = new Parser();
		p.parse(prog);

		return new AdornedProgram(p.getRules(), p.getQueries().iterator().next());
	}

	/**
	 * Tests whether all adorned predicates are available.
	 */
	public void testAdornedPredicatesP0() throws Exception {
		final String prog = "sg(?X, ?Y) :- flat(?X, ?Y).\n"
				  + "sg(?X, ?Y) :- up(?X, ?Z1), sg(?Z1, ?Z2), flat(?Z2, ?Z3), sg(?Z3, ?Z4), down(?Z4, ?Y).\n"
				  + "?- sg('john', ?Y).\n";
		final AdornedProgram ap = getAdornedProgram(prog);

		// constructing the reference adorned predicate set
		final Set<AdornedPredicate> preds = new HashSet<AdornedPredicate>(1);
		preds.add(new AdornedPredicate("sg", new Adornment[] { Adornment.BOUND,
				Adornment.FREE }));

		// asserting the adorned predicates
		assertEquals("There are not all predicates created", preds, ap.getAdornedPredicates());
	}

	/**
	 * Tests whether all adorned predicates are available.
	 */
	public void testAdornedPredicatesP1() throws Exception {
		final String prog = "rsg(?X, ?Y) :- flat(?X, ?Y).\n"
				  + "rsg(?X, ?Y) :- up(?X, ?X1), rsg(?Y1, ?X1), down(?Y1, ?Y).\n"
				  + "?- rsg('a', ?Y).\n";
		final AdornedProgram ap = getAdornedProgram(prog);

		// constructing the reference adorned predicate set
		final Set<AdornedPredicate> preds = new HashSet<AdornedPredicate>(1);
		preds.add(new AdornedPredicate("rsg", new Adornment[] {
				Adornment.BOUND, Adornment.FREE }));
		preds.add(new AdornedPredicate("rsg", new Adornment[] { Adornment.FREE,
				Adornment.BOUND }));

		// asserting the adorned predicates
		assertEquals("There are not all predicates created", preds, ap.getAdornedPredicates());
	}

	/**
	 * <p>
	 * Tests whether all adorned rules are computed
	 * </p>
	 * <p>
	 * <b>ATTENTION: at the moment only the &quot;core&quot; rules are compared,
	 * because while the computation of the adorned rules different queries are
	 * created to get the corresponding sip. So the sip of the adorned rules
	 * isn't compared</b>
	 * </p>
	 */
	public void testAdornedRulesP0() throws Exception {
		final String prog = "sg(?X, ?Y) :- flat(?X, ?Y).\n"
				  + "sg(?X, ?Y) :- up(?X, ?Z1), sg(?Z1, ?Z2), flat(?Z2, ?Z3), sg(?Z3, ?Z4), down(?Z4, ?Y).\n"
				  + "?- sg('john', ?Y).\n";
		final AdornedProgram ap = getAdornedProgram(prog);

		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm Z1 = TERM.createVariable("Z1");
		final ITerm Z2 = TERM.createVariable("Z2");
		final ITerm Z3 = TERM.createVariable("Z3");
		final ITerm Z4 = TERM.createVariable("Z4");
		final ITerm[] XY = new ITerm[]{X, Y};
		final Adornment[] bf = new Adornment[]{Adornment.BOUND, Adornment.FREE};

		final Set<IRule> ref = new HashSet<IRule>();
		// sg^bf(X, Y) :- flat(X, Y)
		List<ILiteral> head = Arrays.asList(createAdornedLiteral("sg", bf, XY));
		List<ILiteral> body = Arrays.asList(createLiteral("flat", "X", "Y"));
		ref.add(BASIC.createRule(head, body));

		// sg^bf(X, Y) :- up(X, Z1), sg^bf(Z1, Z2), flat(Z2, Z3), sg^bf(Z3, Z4), down(Z4, Y)
		head = Arrays.asList(createAdornedLiteral("sg", bf, XY));
		body = Arrays.asList(createLiteral("up", "X", "Z1"), 
				createAdornedLiteral("sg", bf, new ITerm[]{Z1, Z2}), 
				createLiteral("flat", "Z2", "Z3"),
				createAdornedLiteral("sg", bf, new ITerm[]{Z3, Z4}), 
				createLiteral("down", "Z4", "Y"));
		ref.add(BASIC.createRule(head, body));

		// TODO: maybe look whether the sip contains all edges
		assertEquals("The rules are not adorned correctly", ref, plainRules(ap.getAdornedRules()));
	}

	/**
	 * <p>
	 * Tests whether all adorned rules are computed
	 * </p>
	 * <p>
	 * <b>ATTENTION: at the moment only the &quot;core&quot; rules are compared,
	 * because while the computation of the adorned rules different queries are
	 * created to get the corresponding sip. So the sip of the adorned rules
	 * isn't compared</b>
	 * </p>
	 */
	public void testAdornedRulesP1() throws Exception {
		final String prog = "rsg(?X, ?Y) :- flat(?X, ?Y).\n"
				  + "rsg(?X, ?Y) :- up(?X, ?X1), rsg(?Y1, ?X1), down(?Y1, ?Y).\n"
				  + "?- rsg('a', ?Y).\n";
		final AdornedProgram ap = getAdornedProgram(prog);

		final ITerm X = TERM.createVariable("X");
		final ITerm X1 = TERM.createVariable("X1");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm Y1 = TERM.createVariable("Y1");
		final ITerm[] XY = new ITerm[]{X, Y};
		final Adornment[] bf = new Adornment[]{Adornment.BOUND, Adornment.FREE};
		final Adornment[] fb = new Adornment[]{Adornment.FREE, Adornment.BOUND};

		final Set<IRule> ref = new HashSet<IRule>();
		// rsg^bf(X, Y) :- flat(X, Y)
		List<ILiteral> head = Arrays.asList(createAdornedLiteral("rsg", bf, XY));
		List<ILiteral> body = Arrays.asList(createLiteral("flat", "X", "Y"));
		ref.add(BASIC.createRule(head, body));

		// rsg^fb(X, Y) :- flat(X, Y)
		head = Arrays.asList(createAdornedLiteral("rsg", fb, XY));
		body = Arrays.asList(createLiteral("flat", "X", "Y"));
		ref.add(BASIC.createRule(head, body));

		// rsg^bf(X, Y) :- up(X, X1), rsg^fb(Y1, X1), down(Y1, Y)
		head = Arrays.asList(createAdornedLiteral("rsg", bf, XY));
		body = Arrays.asList(createLiteral("up", "X", "X1"), 
				createAdornedLiteral("rsg", fb, new ITerm[]{Y1, X1}), 
				createLiteral("down", "Y1", "Y"));
		ref.add(BASIC.createRule(head, body));

		// rsg^fb(X, Y) :- up(X, X1), rsg^fb(Y1, X1), down(Y1, Y)
		head = Arrays.asList(createAdornedLiteral("rsg", fb, XY));
		body = Arrays.asList(createLiteral("up", "X", "X1"), 
				createAdornedLiteral("rsg", fb, new ITerm[]{Y1, X1}), 
				createLiteral("down", "Y1", "Y"));
		ref.add(BASIC.createRule(head, body));

		// TODO: maybe look whether the sip contains all edges
		assertEquals("The rules are not adorned correctly", ref, plainRules(ap.getAdornedRules()));
	}

	/**
	 * Creates an unadorned version of an adorned rule.
	 * 
	 * @param r
	 *            the adorned rule
	 * @return the unadorned rule
	 * @throws NullPointerException
	 *             if the rule is {@code null}
	 */
	public static IRule unadornRule(final IRule r) {
		if (r == null) {
			throw new NullPointerException("The rule must not be null");
		}
		boolean changed = false;
		final List<ILiteral> head = new ArrayList<ILiteral>(r.getHead());
		int i = 0;
		for (final ILiteral l : head) {
			if (l.getAtom().getPredicate() instanceof AdornedPredicate) {
				head.set(i, BASIC.createLiteral(l.isPositive(),
						((AdornedPredicate) l.getAtom().getPredicate())
								.getUnadornedPredicate(), l.getAtom().getTuple()));
				changed = true;
			}
			i++;
		}
		final List<ILiteral> body= new ArrayList<ILiteral>(r.getBody());
		i = 0;
		for (final ILiteral l : body) {
			if (l.getAtom().getPredicate() instanceof AdornedPredicate) {
				body.set(i, BASIC.createLiteral(l.isPositive(),
						((AdornedPredicate) l.getAtom().getPredicate())
								.getUnadornedPredicate(), l.getAtom().getTuple()));
				changed = true;
			}
			i++;
		}
		return changed ? BASIC.createRule(head, body) : r;
	}

	/**
	 * <p>
	 * Tests whether all adorned rules are computed when the query got no
	 * arguments bound.
	 * </p>
	 * <p>
	 * <b>ATTENTION: at the moment only the &quot;core&quot; rules are compared,
	 * because while the computation of the adorned rules different queries are
	 * created to get the corresponding sip. So the sip of the adorned rules
	 * isn't compared</b>
	 * </p>
	 */
	public void testFreeQuery() throws Exception {
		final String prog = "w(?Y) :- k(?X, ?Y), l(?X).\n" + 
			"?- w(?X).";
		final AdornedProgram ap = getAdornedProgram(prog);

		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final Adornment[] f = new Adornment[]{Adornment.FREE};

		final Set<IRule> ref = new HashSet<IRule>();
		// w^f(Y) :- k(X, Y), l(X)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("w", f, new ITerm[]{Y})), 
					Arrays.asList(createLiteral("k", "X", "Y"), createLiteral("l", "X"))));

		assertEquals("The rules are not constructed correctly", ref, plainRules(ap.getAdornedRules()));

		// ?- w^f(X)
		final IQuery refQuery = BASIC.createQuery(createAdornedLiteral("w", f, new ITerm[]{X}));

		assertEquals("The query is not correct", refQuery, ap.getQuery());
	}

	/**
	 * <p>
	 * Tests whether all adorned rules are computed when the query got no
	 * arguments bound.
	 * </p>
	 * <p>
	 * <b>ATTENTION: at the moment only the &quot;core&quot; rules are compared,
	 * because while the computation of the adorned rules different queries are
	 * created to get the corresponding sip. So the sip of the adorned rules
	 * isn't compared</b>
	 * </p>
	 */
	public void testFreeQuery1() throws Exception {
		final String prog = "w(?X, ?Y) :- k(?X, ?B), l(?B, ?C), w(?C, ?Y).\n" + 
			"?- w(?X, ?Y).";
		final AdornedProgram ap = getAdornedProgram(prog);

		final ITerm C = TERM.createVariable("C");
		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm[] CY = new ITerm[]{C, Y};
		final ITerm[] XY = new ITerm[]{X, Y};
		final Adornment[] ff = new Adornment[]{Adornment.FREE, Adornment.FREE};
		final Adornment[] bf = new Adornment[]{Adornment.BOUND, Adornment.FREE};

		final Set<IRule> ref = new HashSet<IRule>();
		// w^ff(X, Y) :- k(X, B), l(B, C), w^bf(C, Y)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("w" ,ff, XY)), 
					Arrays.asList(createLiteral("k", "X", "B"), 
						createLiteral("l", "B", "C"), 
						createAdornedLiteral("w", bf, CY))));
		// w^bf(X, Y) :- k(X, B), l(B, C), w^bf(C, Y)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("w" ,bf, XY)), 
					Arrays.asList(createLiteral("k", "X", "B"), 
						createLiteral("l", "B", "C"), 
						createAdornedLiteral("w", bf, CY))));

		assertEquals("The rules are not constructed correctly", ref, plainRules(ap.getAdornedRules()));

		// ?- w^ff(X, Y)
		final IQuery refQuery = BASIC.createQuery(createAdornedLiteral("w", ff, XY));

		assertEquals("The query is not correct", refQuery, ap.getQuery());
	}

	/**
	 * <p>
	 * Tests whether all adorned rules are computed when the query got no
	 * arguments bound.
	 * </p>
	 * <p>
	 * <b>ATTENTION: at the moment only the &quot;core&quot; rules are compared,
	 * because while the computation of the adorned rules different queries are
	 * created to get the corresponding sip. So the sip of the adorned rules
	 * isn't compared</b>
	 * </p>
	 */
	public void testFreeQuery2() throws Exception {
		final String prog = "w(?X, ?Y) :- k(?X, ?B), l(?B, ?C), w(?D, ?Y).\n" + 
			"?- w(?X, ?Y).";
		final AdornedProgram ap = getAdornedProgram(prog);

		//final ITerm C = TERM.createVariable("C");
		final ITerm D = TERM.createVariable("D");
		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm[] DY = new ITerm[]{D, Y};
		final ITerm[] XY = new ITerm[]{X, Y};
		final Adornment[] ff = new Adornment[]{Adornment.FREE, Adornment.FREE};

		final Set<IRule> ref = new HashSet<IRule>();
		// w^ff(X, Y) :- k(X, B), l(B, C), w^ff(D, Y)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("w" ,ff, XY)), 
					Arrays.asList(createLiteral("k", "X", "B"), 
						createLiteral("l", "B", "C"), 
						createAdornedLiteral("w", ff, DY))));

		assertEquals("The rules are not constructed correctly", ref, plainRules(ap.getAdornedRules()));

		// ?- w^ff(X, Y)
		final IQuery refQuery = BASIC.createQuery(createAdornedLiteral("w", ff, XY));

		assertEquals("The query is not correct", refQuery, ap.getQuery());
	}

	public void testConstructedAdornment() throws ParserException {
		final String prog = "w(?X, ?Y, ?Z) :- k(?A, ?B, ?Y), w(const(?X, ?A, ?B), ?C, ?Y).\n"
						   + "?- w('john', 'mary', ?Z).";
		final AdornedProgram ap = getAdornedProgram(prog);

		// constructing the adorned rules
		// constructing the adorned predicates
		final AdornedPredicate bbf = new AdornedPredicate("w",
				new Adornment[] { Adornment.BOUND, Adornment.BOUND,
						Adornment.FREE });
		final AdornedPredicate bfb = new AdornedPredicate("w",
				new Adornment[] { Adornment.BOUND, Adornment.FREE,
						Adornment.BOUND });

		final Set<IRule> ref = new HashSet<IRule>();

		// w^bbf(X, Y, Z) :- k(A, B, Y), w^bfb(const[X, A, B], C, Y)
		List<ILiteral> head = Arrays.asList(BASIC.createLiteral(true, bbf,
				BASIC.createTuple(new ArrayList<ITerm>(createVarList("X", "Y",
						"Z")))));
		List<ILiteral> body = Arrays.asList(createLiteral("k", "A", "B", "Y"), BASIC
				.createLiteral(true, bfb, BASIC.createTuple(TERM
						.createConstruct("const", TERM.createVariable("X"),
								TERM.createVariable("A"), TERM
										.createVariable("B")), TERM
						.createVariable("C"), TERM.createVariable("Y"))));
		ref.add(BASIC.createRule(head, body));
		
		// w^bfb(X, Y, Z) :- k(A, B, Y), w^bfb(const[X, A, B], C, Y)
		head = Arrays.asList(BASIC.createLiteral(true, bfb,
				BASIC.createTuple(new ArrayList<ITerm>(createVarList("X", "Y",
				"Z")))));
		body = Arrays.asList(createLiteral("k", "A", "B", "Y"), BASIC
				.createLiteral(true, bfb, BASIC.createTuple(TERM
						.createConstruct("const", TERM.createVariable("X"),
								TERM.createVariable("A"), TERM
								.createVariable("B")), TERM
								.createVariable("C"), TERM.createVariable("Y"))));
		ref.add(BASIC.createRule(head, body));

		// asserting the result
		assertEquals("The rules are not constructed correctly", ref, plainRules(ap.getAdornedRules()));
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
	 * @throws NullPointerException
	 *             if the symbol is {@code null}
	 * @throws IllegalArgumentException
	 *             if the symbol is 0 sighns long
	 * @throws NullPointerException
	 *             if the adornments is or contains {@code null}
	 * @throws NullPointerException
	 *             if the terms is or contains {@code null}
	 */
	private static ILiteral createAdornedLiteral(final String symbol,
			final Adornment[] ad, final ITerm[] t) {
		if (symbol == null) {
			throw new NullPointerException("The symbol must not be null");
		}
		if (symbol.length() == 0) {
			throw new IllegalArgumentException(
					"The symbol must be longer than 0 characters");
		}
		if ((ad == null) || Arrays.asList(ad).contains(null)) {
			throw new NullPointerException(
					"The adornments must not be, or contain null");
		}
		if ((t == null) || Arrays.asList(t).contains(null)) {
			throw new NullPointerException(
					"The terms must not be, or contain null");
		}
		return BASIC.createLiteral(true, new AdornedProgram.AdornedPredicate(
				symbol, t.length, ad), BASIC.createTuple(t));
	}

	/**
	 * Tests that constants in literals in the body will be marked as bound.
	 */
	public void testConstantsInBody() throws Exception {
		final String prog = "a(?X, ?Y) :- b(?X, ?Z), c('a', ?Z, ?Y). \n" + 
			"c(?X, ?Y, ?Z) :- x(?X, ?Y, ?Z). \n" + 
			"?-a('john', ?Y).";
		final AdornedProgram ap = getAdornedProgram(prog);

		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm Z = TERM.createVariable("Z");
		final ITerm[] XYZ = new ITerm[]{X, Y, Z};
		final Adornment[] bbf = new Adornment[]{Adornment.BOUND, Adornment.BOUND, Adornment.FREE};
		final Adornment[] bf = new Adornment[]{Adornment.BOUND, Adornment.FREE};
		final ILiteral b = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("b", 2), BASIC.createTuple(X, Z)));
		final ILiteral x = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("x", 3), BASIC.createTuple(XYZ)));

		final Set<IRule> ref = new HashSet<IRule>();
		// a^bf(?X, ?Y) :- b(?X, ?Z), c^bbf('a', ?Z, ?Y)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("a", bf, new ITerm[]{X, Y})), 
						Arrays.asList(
							b, createAdornedLiteral("c", bbf, new ITerm[]{TERM.createString("a"), Z, Y}))));
		// c^bbf(?X, ?Y, ?Z) :- x(?X, ?Y, ?Z)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("c", bbf, XYZ)), Arrays.asList(x)));

		assertEquals("The rules are not constructed correctly", ref, plainRules(ap.getAdornedRules()));
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
		final AdornedProgram ap = getAdornedProgram(prog);

		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm Z = TERM.createVariable("Z");
		final ITerm[] XY = new ITerm[]{X, Y};
		final ITerm[] XYZ = new ITerm[]{X, Y, Z};
		final Adornment[] fb = new Adornment[]{Adornment.FREE, Adornment.BOUND};
		final Adornment[] bb = new Adornment[]{Adornment.BOUND, Adornment.BOUND};
		final Adornment[] bbf = new Adornment[]{Adornment.BOUND, Adornment.BOUND, Adornment.FREE};
		final ILiteral c2 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 2), BASIC.createTuple(XY)));
		final ILiteral c3= BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 3), BASIC.createTuple(XYZ)));

		final Set<IRule> ref = new HashSet<IRule>();
		// p^fb(?X, ?Y) :- c(?X, ?Y)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("p", fb, XY)), Arrays.asList(c2)));
		// r^bbf(?X, ?Y, ?Z) :- c(?X, ?Y, ?Z)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("r", bbf, XYZ)), Arrays.asList(c3)));
		// s^bb(?X, ?Y) :- c(?X, ?Y)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("s", bb, XY)), Arrays.asList(c2)));

		assertEquals("The rules are not constructed correctly", ref, plainRules(ap.getAdornedRules()));

		// ?- p^fb(X, 'a'), r^bbf('b', X, Y), s^bb('e', Y)
		final IQuery refQuery = BASIC.createQuery(createAdornedLiteral("p", fb, new ITerm[]{X, TERM.createString("a")}), 
				createAdornedLiteral("r", bbf, new ITerm[]{TERM.createString("b"), X, Y}), 
				createAdornedLiteral("s", bb, new ITerm[]{TERM.createString("e"), Y}));

		assertEquals("The query is not correct", refQuery, ap.getQuery());
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
		final AdornedProgram ap = getAdornedProgram(prog);

		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm Z = TERM.createVariable("Z");
		final ITerm[] XY = new ITerm[]{X, Y};
		final ITerm[] XYZ = new ITerm[]{X, Y, Z};
		final Adornment[] ff = new Adornment[]{Adornment.FREE, Adornment.FREE};
		final Adornment[] bb = new Adornment[]{Adornment.BOUND, Adornment.BOUND};
		final Adornment[] bbf = new Adornment[]{Adornment.BOUND, Adornment.BOUND, Adornment.FREE};
		final ILiteral c2 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 2), BASIC.createTuple(XY)));
		final ILiteral c3= BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 3), BASIC.createTuple(XYZ)));

		final Set<IRule> ref = new HashSet<IRule>();
		// p^ff(X, Y) :- c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("p", ff, XY)), Arrays.asList(c2)));
		// s^bb(X, Y) :- c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("s", bb, XY)), Arrays.asList(c2)));
		// r^bbf(X, Y, Z) :- c(X, Y, Z)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("r", bbf, XYZ)), Arrays.asList(c3)));

		assertEquals("The rules are not constructed correctly", ref, plainRules(ap.getAdornedRules()));

		// ?- p^ff(X, Y), r^bbf(b, X, Z), s^bb(e, Z)
		final IQuery refQuery = BASIC.createQuery(createAdornedLiteral("p", ff, XY), 
				createAdornedLiteral("r", bbf, new ITerm[]{TERM.createString("b"), X, Z}), 
				createAdornedLiteral("s", bb, new ITerm[]{TERM.createString("e"), Z}));

		assertEquals("The query is not correct", refQuery, ap.getQuery());
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
		final AdornedProgram ap = getAdornedProgram(prog);

		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm Z = TERM.createVariable("Z");
		final ITerm[] XY = new ITerm[]{X, Y};
		final ITerm[] XYZ = new ITerm[]{X, Y, Z};
		final Adornment[] bb = new Adornment[]{Adornment.BOUND, Adornment.BOUND};
		final Adornment[] bff = new Adornment[]{Adornment.BOUND, Adornment.FREE, Adornment.FREE};
		final ILiteral c2 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 2), BASIC.createTuple(XY)));
		final ILiteral c3= BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 3), BASIC.createTuple(XYZ)));

		final Set<IRule> ref = new HashSet<IRule>();
		// p^bb(X, Y) :- c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("p", bb, XY)), Arrays.asList(c2)));
		// r^bff(X, Y, Z) :- c(X, Y, Z)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("r", bff, XYZ)), Arrays.asList(c3)));
		// s^bb(X, Y) :- c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("s", bb, XY)), Arrays.asList(c2)));

		assertEquals("The rules are not constructed correctly", ref, plainRules(ap.getAdornedRules()));

		// ?- p^bb(b, a), r^bff(b, X, Y), s^bb(e, Y)
		final IQuery refQuery = BASIC.createQuery(createAdornedLiteral("p", bb, 
					new ITerm[]{TERM.createString("b"), TERM.createString("a")}), 
				createAdornedLiteral("r", bff, new ITerm[]{TERM.createString("b"), X, Y}), 
				createAdornedLiteral("s", bb, new ITerm[]{TERM.createString("e"), Y}));

		assertEquals("The query is not correct", refQuery, ap.getQuery());
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
		final AdornedProgram ap = getAdornedProgram(prog);

		final ITerm W = TERM.createVariable("W");
		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm Z = TERM.createVariable("Z");
		final ITerm[] WX = new ITerm[]{W, X};
		final ITerm[] YZ = new ITerm[]{Y, Z};
		final ITerm[] XY = new ITerm[]{X, Y};
		final ITerm[] WXYZ = new ITerm[]{W, X, Y, Z};
		final Adornment[] ff = new Adornment[]{Adornment.FREE, Adornment.FREE};
		final Adornment[] bbbb = new Adornment[]{Adornment.BOUND, Adornment.BOUND, Adornment.BOUND, Adornment.BOUND};
		final ILiteral c2 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 2), BASIC.createTuple(XY)));
		final ILiteral c4= BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 4), BASIC.createTuple(WXYZ)));

		final Set<IRule> ref = new HashSet<IRule>();
		// s^bbbb(W, Y, X, X) :- c(W, X, Y, Z)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("s", bbbb, WXYZ)), Arrays.asList(c4)));
		// p^ff(X, Y) :- c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("p", ff, XY)), Arrays.asList(c2)));
		// r^ff(X, Y) :- c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("r", ff, XY)), Arrays.asList(c2)));

		assertEquals("The rules are not constructed correctly", ref, plainRules(ap.getAdornedRules()));

		// ?- p^ff(W, X), r^ff(Y, Z), s^bbbb(W, X, Y, Z)
		final IQuery refQuery = BASIC.createQuery(createAdornedLiteral("p", ff, WX), 
				createAdornedLiteral("r", ff, YZ), 
				createAdornedLiteral("s", bbbb, WXYZ));

		assertEquals("The query is not correct", refQuery, ap.getQuery());
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
		final AdornedProgram ap = getAdornedProgram(prog);

		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm Z = TERM.createVariable("Z");
		final ITerm[] XY = new ITerm[]{X, Y};
		final ITerm[] XYZ = new ITerm[]{X, Y, Z};
		final ITerm[] ZYX = new ITerm[]{Z, Y, X};
		final Adornment[] ff = new Adornment[]{Adornment.FREE, Adornment.FREE};
		final Adornment[] bb = new Adornment[]{Adornment.BOUND, Adornment.BOUND};
		final Adornment[] fbb = new Adornment[]{Adornment.FREE, Adornment.BOUND, Adornment.BOUND};
		final Adornment[] bbf = new Adornment[]{Adornment.BOUND, Adornment.BOUND, Adornment.FREE};
		final ILiteral c2 = BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 2), BASIC.createTuple(XY)));
		final ILiteral c3= BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 3), BASIC.createTuple(XYZ)));

		final Set<IRule> ref = new HashSet<IRule>();
		// p^ff(X, Y) :- c(X, Y), r^fbb(Z, Y, X)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("p", ff, XY)), 
					Arrays.asList(c2, createAdornedLiteral("r", fbb, ZYX))));
		// s^bb(X, Y) :- c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("s", bb, XY)), 
					Arrays.asList(c2)));
		// r^fbb(X, Y, Z) :- c(X, Y, Z)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("r", fbb, XYZ)), 
					Arrays.asList(c3)));
		// r^bbf(X, Y, Z) :- c(X, Y, Z)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("r", bbf, XYZ)), 
					Arrays.asList(c3)));

		assertEquals("The rules are not constructed correctly", ref, plainRules(ap.getAdornedRules()));

		// ?- p^ff(X, Y), r^bbf(b, X, Z), s^bb(e, Z)
		final IQuery refQuery = BASIC.createQuery(createAdornedLiteral("p", ff, XY), 
				createAdornedLiteral("r", bbf, new ITerm[]{TERM.createString("b"), X, Z}), 
				createAdornedLiteral("s", bb, new ITerm[]{TERM.createString("e"), Z}));

		assertEquals("The query is not correct", refQuery, ap.getQuery());
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
		final AdornedProgram ap = getAdornedProgram(prog);

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
		final ILiteral c3= BASIC.createLiteral(true, BASIC.createAtom(BASIC.createPredicate("c", 3), BASIC.createTuple(XYZ)));

		final Set<IRule> ref = new HashSet<IRule>();
		// s^ff(X, Y) :- c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("s", ff, XY)), 
					Arrays.asList(c2)));
		// p^ff(X, Y) :- c(X, Y), s^ff(Z, T)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("p", ff, XY)), 
					Arrays.asList(c2, createAdornedLiteral("s", ff, new ITerm[]{Z, T}))));
		// s^bb(X, Y) :- c(X, Y)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("s", bb, XY)), 
					Arrays.asList(c2)));
		// r^bbf(X, Y, Z) :- c(X, Y, Z)
		ref.add(BASIC.createRule(Arrays.asList(createAdornedLiteral("r", bbf, XYZ)), 
					Arrays.asList(c3)));

		assertEquals("The rules are not constructed correctly", ref, plainRules(ap.getAdornedRules()));

		// ?- p^ff(X, Y), r^bbf(b, X, Z), s^bb(e, Z)
		final IQuery refQuery = BASIC.createQuery(createAdornedLiteral("p", ff, XY), 
				createAdornedLiteral("r", bbf, new ITerm[]{TERM.createString("b"), X, Z}), 
				createAdornedLiteral("s", bb, new ITerm[]{TERM.createString("e"), Z}));

		assertEquals("The query is not correct", refQuery, ap.getQuery());
	}

	/**
	 * Tests the correct transformation of rules where literals equal to
	 * the head literal pass variables to other literals. Those rules
	 * shouldn't be considered for the adornments.
	 */
	public void testUnproductiveLiteralPassings() throws Exception {
		final String prog = "i0(?X) :- i0(?X), i1(?X, ?Y).\n"
			+ "i1(?X, ?Y) :- i2(?X, ?Y).\n"
			+ "?- i0('john'), i0(?X).";
		final AdornedProgram ap = getAdornedProgram(prog);

		final Adornment[] B = new Adornment[]{Adornment.BOUND};
		final ITerm john = TERM.createString("john");

		final List<IRule> rules = new ArrayList<IRule>();

		// i0(?X) :- i0(?X), i1(?X, ?Y).
		rules.add(BASIC.createRule(Arrays.asList(createLiteral("i0", "X")),
					Arrays.asList(createLiteral("i0", "X"), createLiteral("i1", "X", "Y"))));
		// i1(?X, ?Y) :- i2(?X, ?Y).
		rules.add(BASIC.createRule(Arrays.asList(createLiteral("i1", "X", "Y")),
					Arrays.asList(createLiteral("i2", "X", "Y"))));

		assertEquals("The rules are not constructed correctly", Collections.<AdornedRule>emptySet(), ap.getAdornedRules());

		// ?- i0('john'), i0(?X).
		final IQuery query = BASIC.createQuery(Arrays.asList(BASIC.createLiteral(true,
						BASIC.createPredicate("i0", 1),
						BASIC.createTuple(john)),
					createLiteral("i0", "X")));

		assertEquals("The query is not correct", query, ap.getQuery());
	}

	/**
	 * Prints a program and the resulting adorned program in a formated
	 * way.
	 * @param name the name to identify the test
	 * @param prog the input program
	 * @param res the resulting program
	 */
	private static void printDebug(final String name, final String prog, final AdornedProgram ap) {
		logger.debug("---");
		logger.debug(name);
		logger.debug("\tinput: ");
		logger.debug(prog);
		logger.debug("\tadorned: ");
		logger.debug(ap.toString());
	}

	/**
	 * Prints 2 collections of rules to stdout.
	 * @param rules the outcome of the evaluation
	 * @param ref the reference rules
	 */
	private static void printSortedRules(final Collection<? extends IRule> ref, final Collection<? extends IRule> rules) {
		final List<IRule> lrules = new ArrayList<IRule>(rules);
		final List<IRule> lref = new ArrayList<IRule>(ref);
		Collections.sort(lrules, RC);
		Collections.sort(lref, RC);
		System.out.println("rules:");
		for (final IRule r : lrules) {
			System.out.println(r);
		}
		System.out.println();
		System.out.println("ref:");
		for (final IRule r : lref) {
			System.out.println(r);
		}
		System.out.println();
	}

	/**
	 * Transforms a collection of adorned rules to a set of plain rule objects.
	 * @param rules the adorned rules
	 * @return the list of plain rules
	 */
	private static Set<IRule> plainRules(final Collection<AdornedRule> rules) {
		assert rules != null: "The rules must not be null";

		final Set<IRule> res = new HashSet<IRule>();
		for (final AdornedRule r : rules) {
			res.add(r.getRule());
		}
		return res;
	}

	/**
	 * <p>
	 * Compares two rules according to their predicate symbols.
	 * </p>
	 * <p>
	 * $Id: AdornmentsTest.java,v 1.12 2007-10-30 10:35:50 poettler_ric Exp $
	 * </p>
	 * 
	 * @author Richard Pöttler (richard dot poettler at deri dot org)
	 * @version $Revision: 1.12 $
	 */
	private static class RuleComparator implements Comparator<IRule> {
		public int compare(IRule o1, IRule o2) {
			if ((o1 == null) || (o2 == null)) {
				throw new NullPointerException("The rules must not be null");
			}

			int res = 0;
			if ((res = o1.getHead().size() - o2.getHead().size()) != 0) {
				return res;
			}
			for (final Iterator<ILiteral> i1 = o1.getHead().iterator(), 
					i2 = o2.getHead().iterator(); i1.hasNext();) {
				if ((res = compareLiteral(i1.next(), i2.next())) != 0) {
					return res;
				}
			}
			if ((res = o1.getBody().size() - o2.getBody().size()) != 0) {
				return res;
			}
			for (final Iterator<ILiteral> i1 = o1.getBody().iterator(), 
					i2 = o2.getBody().iterator(); i1.hasNext();) {
				if ((res = compareLiteral(i1.next(), i2.next())) != 0) {
					return res;
				}
			}
			return 0;
		}

		private static int compareLiteral(final ILiteral l1, final ILiteral l2) {
			if ((l1 == null) || (l2 == null)) {
				throw new NullPointerException("The literals must not be null");
			}
			int res = 0;
			final IPredicate p1 = l1.getAtom().getPredicate();
			final IPredicate p2 = l2.getAtom().getPredicate();
			// comparing the predicate symbol
			if ((res = p1.getPredicateSymbol().compareTo(
					p2.getPredicateSymbol())) != 0) {
				return res;
			}
			// comparing the arity
			if ((res = p1.getArity() - p2.getArity()) != 0) {
				return res;
			}
			// comparing the adornments
			if ((p1 instanceof AdornedPredicate)
					&& !(p2 instanceof AdornedPredicate)) {
				return 1;
			} else if (!(p1 instanceof AdornedPredicate)
					&& (p2 instanceof AdornedPredicate)) {
				return -1;
			} else if ((p1 instanceof AdornedPredicate)
					&& (p2 instanceof AdornedPredicate)) {
				final Adornment[] a1 = ((AdornedPredicate) p1).getAdornment();
				final Adornment[] a2 = ((AdornedPredicate) p2).getAdornment();
				for (int i = 0; i < a1.length; i++) {
					if ((a1[i] != a2[i]) && (a1[i] == Adornment.BOUND)) {
						return -1;
					} else if ((a1[i] != a2[i]) && (a1[i] == Adornment.FREE)) {
						return 1;
					}
				}
			}
			return 0;
		}

	}

}
