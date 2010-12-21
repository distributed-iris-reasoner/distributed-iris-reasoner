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
public class EquivalenceTest extends ProgramEvaluationTest {

	public EquivalenceTest(String name) {
		super(name);
	}

	@Override
	public Collection<String> createExpressions() {
		Collection<String> expressions = new ArrayList<String>();

		// Create facts.
		expressions.add("p('A1', 'B1', 'C4', 'D4').");
		expressions.add("p('A2', 'B1', 'C6', 'D3').");
		expressions.add("p('A3', 'B1', 'C5', 'D2').");
		expressions.add("p('A4', 'B1', 'C1', 'D5').");

		expressions.add("q('A1', 'B2', 'D2').");
		expressions.add("q('A2', 'B1', 'C3').");
		expressions.add("q('A3', 'B1', 'C2').");
		expressions.add("q('A4', 'B2', 'B3').");

		expressions.add("r('D2').");
		expressions.add("r('C3').");

		expressions.add("s(?X, ?Y) :- r(?X), q(?Y, ?Z, ?X).");

		// Create rules.
		expressions
				.add("?X4 = ?Y3 :- p(?X1, ?X2, ?X3, ?X4), q(?Y1, ?Y2, ?Y3), ?X1 = ?Y1, ?X2 = ?Y2.");

		return expressions;
	}

	public void testR() throws Exception {
		// The result should be: C2, C3, D2, D3

		IRelation relation = evaluate("?- r(?X).");

		assertTrue("C2 not in relation.", relation.contains(Helper
				.createConstantTuple("C2")));
		assertTrue("C3 not in relation.", relation.contains(Helper
				.createConstantTuple("C3")));
		assertTrue("D2 not in relation.", relation.contains(Helper
				.createConstantTuple("D2")));
		assertTrue("D3 not in relation.", relation.contains(Helper
				.createConstantTuple("D3")));
		
		assertEquals("Relation does not have correct size", 4, relation.size());
	}

	public void testS() throws Exception {
		// The result should be: (D2, A1), (C2, A1), (D2, A3), (D3, A2), (C3, A2), (C2, A3)

		IRelation relation = evaluate("?- s(?X, ?Y).");
		
		assertTrue("(D2, A1) not in relation.", relation.contains(Helper
				.createConstantTuple("D2", "A1")));
		assertTrue("(D2, A3) not in relation.", relation.contains(Helper
				.createConstantTuple("D2", "A3")));
		assertTrue("(C3, A2) not in relation.", relation.contains(Helper
				.createConstantTuple("C3", "A2")));
		assertTrue("(C2, A1) not in relation.", relation.contains(Helper
				.createConstantTuple("C2", "A1")));
		assertTrue("(C2, A3) not in relation.", relation.contains(Helper
				.createConstantTuple("C2", "A3")));
		assertTrue("(D3, A2) not in relation.", relation.contains(Helper
				.createConstantTuple("D3", "A2")));
		
		assertEquals("Relation does not have correct size", 6, relation.size());
	}

}
