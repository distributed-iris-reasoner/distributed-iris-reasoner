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
package org.deri.iris.evaluation.equivalence;

import java.util.ArrayList;
import java.util.Collection;

import org.deri.iris.evaluation.ProgramEvaluationTest;
import org.deri.iris.rules.compiler.Helper;
import org.deri.iris.storage.IRelation;

/**
 * Test for correct evaluation of examples with rule head equality.
 * 
 * @author Adrian Marte
 */
public class MultipleIterationsTest extends ProgramEvaluationTest {

	public MultipleIterationsTest(String name) {
		super(name);
	}

	@Override
	public Collection<String> createExpressions() {
		Collection<String> expressions = new ArrayList<String>();

		// Create facts.
		expressions.add("p('A1', 'A2', 'A').");
		expressions.add("p('A3', 'A4', 'C').");
		expressions.add("p('A', 'D', 'E').");

		expressions.add("q('A1', 'A2', 'B').");
		expressions.add("q('A3', 'A4', 'D').");
		expressions.add("q('B', 'C', 'F').");

		expressions.add("r('A').");
		expressions.add("r('C').");
		expressions.add("r('E').");

		expressions.add("s(?X, ?X) :- r(?X), q(?Y, ?Z, ?X).");

		// Create rules.
		expressions
				.add("?X3 = ?Y3 :- p(?X1, ?X2, ?X3), q(?Y1, ?Y2, ?Y3), ?X1 = ?Y1, ?X2 = ?Y2.");

		// The equivalence classes are {A, B}, {C, D}, {E, F}

		// {E, F} should be identified as equivalent, after the equivalence
		// classes {A, B} and {C, D} are identified.

		return expressions;
	}

	public void testR() throws Exception {
		// The result should be: A, B, C, D, E, F

		IRelation relation = evaluate("?- r(?X).");

		assertTrue("A not in relation.", relation.contains(Helper
				.createConstantTuple("A")));
		assertTrue("B not in relation.", relation.contains(Helper
				.createConstantTuple("B")));
		assertTrue("C not in relation.", relation.contains(Helper
				.createConstantTuple("C")));
		assertTrue("D not in relation.", relation.contains(Helper
				.createConstantTuple("D")));
		assertTrue("E not in relation.", relation.contains(Helper
				.createConstantTuple("E")));
		assertTrue("F not in relation.", relation.contains(Helper
				.createConstantTuple("F")));

		assertEquals("Relation does not have correct size", 6, relation.size());
	}

	public void testS() throws Exception {
		// The result should be: (A, B), (B, B), (A, A), (B, A), (C, D), (D, D),
		// (C, C), (D, C), (E, F), (F, F), (E, E), (F, E)

		IRelation relation = evaluate("?- s(?X, ?Y).");
		
		assertTrue("(A, B) not in relation.", relation.contains(Helper
				.createConstantTuple("A", "B")));
		assertTrue("(B, B) not in relation.", relation.contains(Helper
				.createConstantTuple("B", "B")));
		assertTrue("(A, A) not in relation.", relation.contains(Helper
				.createConstantTuple("A", "A")));
		assertTrue("(B, B) not in relation.", relation.contains(Helper
				.createConstantTuple("B", "A")));
		assertTrue("(C, D) not in relation.", relation.contains(Helper
				.createConstantTuple("C", "D")));
		assertTrue("(D, D) not in relation.", relation.contains(Helper
				.createConstantTuple("D", "D")));
		assertTrue("(C, C) not in relation.", relation.contains(Helper
				.createConstantTuple("C", "C")));
		assertTrue("(D, C) not in relation.", relation.contains(Helper
				.createConstantTuple("D", "C")));
		assertTrue("(E, F) not in relation.", relation.contains(Helper
				.createConstantTuple("E", "F")));
		assertTrue("(F, F) not in relation.", relation.contains(Helper
				.createConstantTuple("F", "F")));
		assertTrue("(E, E) not in relation.", relation.contains(Helper
				.createConstantTuple("E", "E")));
		assertTrue("(F, E) not in relation.", relation.contains(Helper
				.createConstantTuple("F", "E")));

		assertEquals("Relation does not have correct size", 12, relation.size());
	}

}
