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
import static org.deri.iris.factory.Factory.TERM;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import org.deri.iris.graph.LabeledEdge;

/**
 * <p>
 * Runs various test on the LeftToRightSip.
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri dot org)
 */
public class LeftToRightSipTest extends TestCase {

	public static Test suite() {
		return new TestSuite(LeftToRightSipTest.class,
				LeftToRightSipTest.class.getSimpleName());
	}

	/**
	 * Parses a rule and a query and constructs the sip out of them.
	 * @param prog the program to parse
	 * @return the constructed sip
	 */
	private static LeftToRightSip parseProgram(final String prog) throws ParserException {
		assert prog != null: "The program must not be null";

		final Parser p = new Parser();
		p.parse(prog);

		assert !p.getRules().isEmpty(): "There are no rules parsed!";
		assert !p.getQueries().isEmpty(): "There are no queries parsed!";

		return new LeftToRightSip(p.getRules().get(0), p.getQueries().get(0));
	}

	/**
	 * Parses a single rule out of a string.
	 * @param prog the program to parse containing the rule
	 * @return the parsed rule
	 */
	private static IRule parseSingleRule(final String prog) throws ParserException {
		assert prog != null: "The program must not be null";

		final Parser p = new Parser();
		p.parse(prog);

		assert !p.getRules().isEmpty(): "There are no rules parsed!";

		return p.getRules().get(0);
	}

	/**
	 * Tests whether the sip contains all expected edges.
	 */
	public void testForEdges0() throws Exception {
		final String prog = "sg(?X, ?Y) :- up(?X, ?Z1), sg(?Z1, ?Z2), flat(?Z2, ?Z3), sg(?Z3, ?Z4), down(?Z4, ?Y).\n"
						 + "?- sg('john', ?X).";
		final LeftToRightSip sip = parseProgram(prog);

		final IVariable X = TERM.createVariable("X");
		final IVariable Z1 = TERM.createVariable("Z1");
		final IVariable Z2 = TERM.createVariable("Z2");
		final IVariable Z3 = TERM.createVariable("Z3");
		final IVariable Z4 = TERM.createVariable("Z4");

		final Set<LabeledEdge<ILiteral, Set<IVariable>>> edges = new HashSet<LabeledEdge<ILiteral, Set<IVariable>>>();
		edges.add(createEdge(createLiteral("sg", "X", "Y"), createLiteral("up", "X", "Z1"), X));
		edges.add(createEdge(createLiteral("up", "X", "Z1"), createLiteral("sg", "Z1", "Z2"), Z1));
		edges.add(createEdge(createLiteral("sg", "Z1", "Z2"), createLiteral("flat", "Z2", "Z3"), Z2));
		edges.add(createEdge(createLiteral("flat", "Z2", "Z3"), createLiteral("sg", "Z3", "Z4"), Z3));
		edges.add(createEdge(createLiteral("sg", "Z3", "Z4"), createLiteral("down", "Z4", "Y"), Z4));

		assertEquals("The edge set does not match.", edges, sip.getEdges());
	}

	/**
	 * Tests whether the sip contains all expected edges.
	 */
	public void testForEdges1() throws Exception {
		final String prog = "rsg(?X, ?Y) :- up(?X, ?X1), rsg(?Y1, ?X1), down(?Y1, ?Y).\n"
						  + "?- rsg('a', ?X).";
		final LeftToRightSip sip = parseProgram(prog);

		final IVariable X = TERM.createVariable("X");
		final IVariable X1 = TERM.createVariable("X1");
		final IVariable Y1 = TERM.createVariable("Y1");

		final Set<LabeledEdge<ILiteral, Set<IVariable>>> edges = new HashSet<LabeledEdge<ILiteral, Set<IVariable>>>();
		edges.add(createEdge(createLiteral("rsg", "X", "Y"), createLiteral("up", "X", "X1"), X));
		edges.add(createEdge(createLiteral("up", "X", "X1"), createLiteral("rsg", "Y1", "X1"), X1));
		edges.add(createEdge(createLiteral("rsg", "Y1", "X1"), createLiteral("down", "Y1", "Y"), Y1));

		assertEquals("The edge set does not match.", edges, sip.getEdges());
	}

	/**
	 * Tests whether the sip contains all expected edges.
	 */
	public void testEqualBuiltinSip() throws Exception {
		final String prog = "rsg(?X, ?Y) :- up(?X, ?X1), rsg(?Y1, ?X1), ?Y1 = ?Y.\n"
			+ "?- rsg('a', ?X).";
		final LeftToRightSip sip = parseProgram(prog);

		final IVariable X = TERM.createVariable("X");
		final IVariable X1 = TERM.createVariable("X1");
		final IVariable Y1 = TERM.createVariable("Y1");

		final Set<LabeledEdge<ILiteral, Set<IVariable>>> edges = new HashSet<LabeledEdge<ILiteral, Set<IVariable>>>();
		edges.add(createEdge(createLiteral("rsg", "X", "Y"), createLiteral("up", "X", "X1"), X));
		edges.add(createEdge(createLiteral("up", "X", "X1"), createLiteral("rsg", "Y1", "X1"), X1));
		edges.add(createEdge(createLiteral("rsg", "Y1", "X1"), 
					BASIC.createLiteral(true, BUILTIN.createEqual(TERM.createVariable("Y1"), TERM.createVariable("Y"))),
					Y1));

		assertEquals("The edge set does not match.", edges, sip.getEdges());
	}

	/**
	 * <p>
	 * Tests the behaviour for rules containing equal literals.
	 * </p>
	 * <p>
	 * <b>Note: Rules containing one literal multiple times will only
	 * contain one edge representing it, no matter, how often such a
	 * literal occurs in the rule.</b>
	 * </p>
	 */
	public void testEqualLiterals() throws Exception {
		final String prog = "tmp(?X) :- p(?X), p(?X), p(?X).\n"
					      + "?- tmp(?X).";
		final LeftToRightSip sip = parseProgram(prog);

		final IVariable X = TERM.createVariable("X");
		final ILiteral lit = createLiteral("p", "X");

		final Set<LabeledEdge<ILiteral, Set<IVariable>>> edges = new HashSet<LabeledEdge<ILiteral, Set<IVariable>>>();
		edges.add(createEdge(lit, lit, X));

		assertEquals("The edge set does not match.", edges, sip.getEdges());
	}

	/**
	 * Ensures, that negative literals don't produce variable passings, but
	 * can receive one.
	 */
	public void testNegativeLiteralPassingHandling() throws Exception {
		final String prog = "p(?Z) :- b(?Y), !a(?Y), c(?Y, ?Z).\n"
			+ "?- p(?Z).";
		final LeftToRightSip sip = parseProgram(prog);

		final IVariable Y = TERM.createVariable("Y");
		final ILiteral b = createLiteral("b", "Y");
		final ILiteral not_a = createLiteral(false, "a", "Y");
		final ILiteral c = createLiteral("c", "Y", "Z");

		final Set<LabeledEdge<ILiteral, Set<IVariable>>> edges = new HashSet<LabeledEdge<ILiteral, Set<IVariable>>>();
		// b(?Y) ->( [?Y] )-> !a(?Y)
		edges.add(createEdge(b, not_a, Y));
		// b(?Y) ->( [?Y] )-> c(?Y, ?Z)
		edges.add(createEdge(b, c, Y));

		assertEquals("The edge set does not match.", edges, sip.getEdges());
	}

	/**
	 * Checks whether the dependency retrieval of a literal depending on
	 * itself succeeds.
	 */
	public void testEqualLiteralsDependency() throws Exception {
		final String prog = "a(?X) :- a(?X).\n"
			+ "?- a(1).";
		final LeftToRightSip sip = parseProgram(prog);

		final ILiteral ax = createLiteral("a", "X");
		assertEquals(ax + " depends on itself.", Collections.singleton(ax), sip.getDepends(ax));
	}

	/**
	 * Tests whether the bound variables are correct.
	 */
	public void testBounds0() throws Exception {
		final String prog = "sg(?X, ?Y) :- up(?X, ?Z1), sg(?Z1, ?Z2), down(?X, ?Z1, ?Z2, ?Z3), again(?X, ?Z1, ?Z3, ?Y).\n"
						 + "?- sg('john', ?X).";
		final LeftToRightSip sip = parseProgram(prog);

		final IVariable X = TERM.createVariable("X");
		final IVariable Z1 = TERM.createVariable("Z1");
		final IVariable Z2 = TERM.createVariable("Z2");
		final IVariable Z3 = TERM.createVariable("Z3");

		final Set<IVariable> bound_up = new HashSet<IVariable>(Arrays.asList(new IVariable[]{X}));
		final Set<IVariable> bound_sg = new HashSet<IVariable>(Arrays.asList(new IVariable[]{Z1}));
		final Set<IVariable> bound_down = new HashSet<IVariable>(Arrays.asList(new IVariable[]{X, Z1, Z2}));
		final Set<IVariable> bound_again = new HashSet<IVariable>(Arrays.asList(new IVariable[]{X, Z1, Z3}));

		final ILiteral up = createLiteral("up", "X", "Z1");
		final ILiteral sg = createLiteral("sg", "Z1", "Z2");
		final ILiteral down = createLiteral("down", "X", "Z1", "Z2", "Z3");
		final ILiteral again = createLiteral("again", "X", "Z1", "Z3", "Y");

		assertEquals("Bounds of up wrong", bound_up, sip.getBoundVariables(up));
		assertEquals("Bounds of sg wrong", bound_sg, sip.getBoundVariables(sg));
		assertEquals("Bounds of down wrong", bound_down, sip.getBoundVariables(down));
		assertEquals("Bounds of again wrong", bound_again, sip.getBoundVariables(again));
	}

	/**
	 * Tests whether the bound variables are correct.
	 */
	public void testBounds1() throws Exception {
		final String prog = "rsg(?X, ?Y) :- up(?X, ?X1), rsg(?Y1, ?X1), down(?Y1, ?Y).\n"
						  + "?- rsg('a', ?X).";
		final LeftToRightSip sip = parseProgram(prog);

		final IVariable X = TERM.createVariable("X");
		final IVariable X1 = TERM.createVariable("X1");
		final IVariable Y1 = TERM.createVariable("Y1");

		final Set<IVariable> bound_up = new HashSet<IVariable>(Arrays.asList(new IVariable[]{X}));
		final Set<IVariable> bound_rsg0 = new HashSet<IVariable>(Arrays.asList(new IVariable[]{X1}));
		final Set<IVariable> bound_down = new HashSet<IVariable>(Arrays.asList(new IVariable[]{Y1}));

		final ILiteral up = createLiteral("up", "X", "X1");
		final ILiteral rsg0 = createLiteral("rsg", "Y1", "X1");
		final ILiteral down = createLiteral("down", "Y1", "Y");

		assertEquals("Bounds of up wrong", bound_up, sip.getBoundVariables(up));
		assertEquals("Bounds of rsg0 wrong", bound_rsg0, sip.getBoundVariables(rsg0));
		assertEquals("Bounds of down wrong", bound_down, sip.getBoundVariables(down));
	}

	/**
	 * Tests the behaviour of the literal comparator with recursive literal
	 * dependencies in rules.
	 */
	public void testRecursiveLiteralComparator() throws Exception {
		final String prog = "a(?X) :- b(?X), c(?X), b(?X).\n"
			+ "?- a('john').";
		final LeftToRightSip sip = parseProgram(prog);

		// the literal edges are:
		// a -> b -> c
		// b -> b
		// c -> b

		final Comparator<ILiteral> lc = sip.getLiteralComparator();
		final ILiteral a = createLiteral("a", "X");
		final ILiteral b = createLiteral("b", "X");
		final ILiteral c = createLiteral("c", "X");

		assertTrue(a + " must be equals to " + a, lc.compare(a, a) == 0);
		assertTrue(b + " must be equals to " + b, lc.compare(b, b) == 0);
		assertTrue(c + " must be equals to " + c, lc.compare(c, c) == 0);
		assertTrue(b + " must be equals to " + c, lc.compare(b, c) == 0);
		assertTrue(c + " must be equals to " + b, lc.compare(c, b) == 0);

		assertTrue(a + " must be smaller than " + b, lc.compare(a, b) < 0);
		assertTrue(a + " must be smaller than " + c, lc.compare(a, c) < 0);

		assertTrue(b + " must be bigger than " + a, lc.compare(b, a) > 0);
		assertTrue(c + " must be bigger than " + a, lc.compare(c, a) > 0);
	}

	/**
	 * Tests the behaviour of the literal comparator with literals which
	 * gain and give no passings.
	 */
	public void testUnconnectedLiteralComparator() throws Exception {
		final String prog = "a(?X) :- b(?X), c(?Y), d(?Z).\n"
			+ "?- a('john').";
		final LeftToRightSip sip = parseProgram(prog);

		// the literal edges are:
		// a -> b
		// c and d are not connected

		final Comparator<ILiteral> lc = sip.getLiteralComparator();
		final ILiteral a = createLiteral("a", "X");
		final ILiteral b = createLiteral("b", "X");
		final ILiteral c = createLiteral("c", "Y");
		final ILiteral d = createLiteral("d", "Z");

		assertTrue(a + " must be equals to " + a, lc.compare(a, a) == 0);
		assertTrue(b + " must be equals to " + b, lc.compare(b, b) == 0);
		assertTrue(c + " must be equals to " + c, lc.compare(c, c) == 0);
		assertTrue(d + " must be equals to " + d, lc.compare(d, d) == 0);

		assertTrue(a + " must be smaller than " + b, lc.compare(a, b) < 0);
		assertTrue(a + " must be smaller than " + c, lc.compare(a, c) < 0);
		assertTrue(a + " must be smaller than " + d, lc.compare(a, d) < 0);

		assertTrue(b + " must be bigger than " + a, lc.compare(b, a) > 0);
		assertTrue(c + " must be bigger than " + a, lc.compare(c, a) > 0);
		assertTrue(d + " must be bigger than " + a, lc.compare(d, a) > 0);

		assertTrue(b + " must be smaller than " + c, lc.compare(b, c) < 0);
		assertTrue(b + " must be smaller than " + d, lc.compare(b, d) < 0);

		assertTrue(c + " must be bigger than " + b, lc.compare(c, b) > 0);
		assertTrue(d + " must be bigger than " + b, lc.compare(d, b) > 0);

		assertTrue(c + " must be equal to " + d, lc.compare(c, d) == 0);
	}

	/**
	 * Tests whether unconnected literals are handeled correctly.
	 */
	public void testUnconnectedLiteral() throws Exception {
		final String prog = "i0(?H0) :- e1(?H0), i0(?X0)."
			+ "?- i0(11).";
		final LeftToRightSip sip = parseProgram(prog);

		final ILiteral i0h0 = createLiteral("i0", "H0");
		final ILiteral i0x0 = createLiteral("i0", "X0");

		assertEquals("getBoundVariables(...) must return an empty set for unconnected literals.",
				Collections.<IVariable>emptySet(),
				sip.getBoundVariables(i0x0));
		assertEquals("getDepends(...) must return an empty set for unconnected literals.",
				Collections.<ILiteral>emptySet(),
				sip.getDepends(i0x0));
		assertEquals("getEdgesEnteringLiteral(...) must return an empty set for unconnected literals.",
				Collections.<LabeledEdge<ILiteral, Set<IVariable>>>emptySet(),
				sip.getEdgesEnteringLiteral(i0x0));
		assertEquals("getEdgesLeavingLiteral(...) must return an empty set for unconnected literals.",
				Collections.<LabeledEdge<ILiteral, Set<IVariable>>>emptySet(),
				sip.getEdgesLeavingLiteral(i0x0));
		assertFalse("containsVertex(...) must return false for unconnected literals.",
				sip.containsVertex(i0x0));
		assertEquals("variablesPassedByLiteral(...) must return an empty set for unconnected literals #1.",
				Collections.<ILiteral>emptySet(),
				sip.variablesPassedByLiteral(i0h0, i0x0));
		assertEquals("variablesPassedByLiteral(...) must return an empty set for unconnected literals #2.",
				Collections.<ILiteral>emptySet(),
				sip.variablesPassedByLiteral(i0x0, i0h0));
	}

	/**
	 * Tests the correct behaviour with rules with multiple head literals
	 * and queries with multiple literals.
	 */
	public void testMultiHeadsMultiQueries() throws Exception {
		final String prog = "a(?A, ?B), b(?B, ?D), c(?E, ?F) :- d(?A, ?B), e(?E, ?B).\n"
			+ "?- a('john', ?X), c('john', ?Y).";
		final LeftToRightSip sip = parseProgram(prog);

		// list of edges:
		// a -> {A} -> d
		// c -> {E} -> e
		// d -> {B} -> e

		final IVariable A = TERM.createVariable("A");
		final IVariable B = TERM.createVariable("B");
		final IVariable E = TERM.createVariable("E");
		final ILiteral a = createLiteral("a", "A", "B");
		final ILiteral c = createLiteral("c", "E", "F");
		final ILiteral d = createLiteral("d", "A", "B");
		final ILiteral e = createLiteral("e", "E", "B");

		final Set<LabeledEdge<ILiteral, Set<IVariable>>> referenceEdges
			= new HashSet<LabeledEdge<ILiteral, Set<IVariable>>>();
		referenceEdges.add(createEdge(a, d, A));
		referenceEdges.add(createEdge(c, e, E));
		referenceEdges.add(createEdge(d, e, B));

		assertEquals("The edge set does not match.", referenceEdges, sip.getEdges());
	}

	/**
	 * Checks, whether not evaluable built-ins don't produce any passings.
	 */
	public void testNotEvaluableBuiltinPassing() throws Exception {
		final String prog = "i(?X) :- ?X < ?Y, e(?Y)."
			+ "?- i(?X).";
		final LeftToRightSip sip = parseProgram(prog);
		assertEquals("There must be not edges created",
				Collections.<LabeledEdge<ILiteral, Set<IVariable>>>emptySet(),
				sip.getEdges());
	}

	/**
	 * Checks, whether evaluable built-ins produce passings.
	 */
	public void testEvaluableBuiltinPassing() throws Exception {
		final String prog = "i(?X) :- e0(?X), e1(?Y), ?X < ?Y, e2(?Y)."
			+ "?- i(?X).";
		final LeftToRightSip sip = parseProgram(prog);

		final IVariable X = TERM.createVariable("X");
		final IVariable Y = TERM.createVariable("Y");
		final ILiteral e0 = createLiteral("e0", "X");
		final ILiteral e1 = createLiteral("e1", "Y");
		final ILiteral e2 = createLiteral("e2", "Y");
		final ILiteral less = BASIC.createLiteral(true, BUILTIN.createLess(X, Y));

		final Set<LabeledEdge<ILiteral, Set<IVariable>>> edges
			= new HashSet<LabeledEdge<ILiteral, Set<IVariable>>>();

		// e1(?Y) ->( [?Y] )-> LESS(?X, ?Y)
		edges.add(createEdge(e1, less, Y));
		// e0(?X) ->( [?X] )-> LESS(?X, ?Y)
		edges.add(createEdge(e0, less, X));
		// e1(?Y) ->( [?Y] )-> e2(?Y)
		edges.add(createEdge(e1, e2, Y));
		// LESS(?X, ?Y) ->( [?Y] )-> e2(?Y)
		edges.add(createEdge(less, e2, Y));

		assertEquals("The edge set does not match.", edges, sip.getEdges());
	}

	/**
	 * Creates a edge.
	 * 
	 * @param s the source literal
	 * @param t the target literal
	 * @param v the passed variables (label)
	 * @return the edge
	 */
	private static LabeledEdge<ILiteral, Set<IVariable>> createEdge(
			final ILiteral s, final ILiteral t, final IVariable... v) {
		assert s != null: "The source literal must not be null";
		assert t != null: "The target literal must not be null";
		assert v != null: "The variables must not be null";

		return new LabeledEdge<ILiteral, Set<IVariable>>(s, t,
				new HashSet<IVariable>(Arrays.asList(v)));
	}
}
