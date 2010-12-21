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
public class ChainTest extends ProgramEvaluationTest {

	public ChainTest(String name) {
		super(name);
	}

	@Override
	public Collection<String> createExpressions() {
		Collection<String> expressions = new ArrayList<String>();

		// Create facts.
		expressions.add("a('A').");
		expressions.add("b('B').");
		expressions.add("c('C').");
		expressions.add("d('D').");

		expressions.add("foo('B').");
		expressions.add("baz('C').");

		// Create rules.
		expressions.add("?X = ?Y :- a(?X), b(?Y).");
		expressions.add("?X = ?Y :- b(?X), c(?Y).");
		expressions.add("?X = ?Y :- c(?X), d(?Y).");

		expressions.add("bar(?X) :- foo(?X), baz(?X), ?X = 'A'.");

		return expressions;
	}

	public void testFoo() throws Exception {
		// The result should be: A, B, C, D

		IRelation relation = evaluate("?- foo(?X).");
		
		assertTrue("A not in relation.", relation.contains(Helper
				.createConstantTuple("A")));
		assertTrue("B not in relation.", relation.contains(Helper
				.createConstantTuple("B")));
		assertTrue("C not in relation.", relation.contains(Helper
				.createConstantTuple("C")));
		assertTrue("D not in relation.", relation.contains(Helper
				.createConstantTuple("D")));
		
		assertEquals("Relation does not have correct size", 4, relation.size());
	}

	public void testBar() throws Exception {
		// The result should be: A, B, C, D

		IRelation relation = evaluate("?- bar(?X).");
		
		assertTrue("A not in relation.", relation.contains(Helper
				.createConstantTuple("A")));
		assertTrue("B not in relation.", relation.contains(Helper
				.createConstantTuple("B")));
		assertTrue("C not in relation.", relation.contains(Helper
				.createConstantTuple("C")));
		assertTrue("D not in relation.", relation.contains(Helper
				.createConstantTuple("D")));
		
		assertEquals("Relation does not have correct size", 4, relation.size());
	}

}
