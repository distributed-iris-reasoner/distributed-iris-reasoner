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
public class BuiltinTest extends ProgramEvaluationTest {

	public BuiltinTest(String name) {
		super(name);
	}

	@Override
	public Collection<String> createExpressions() {
		Collection<String> expressions = new ArrayList<String>();

		// Create facts.
		expressions.add("lower('foobar').");
		expressions.add("lower('noob').");
		expressions.add("lower('pwnage').");
		
		expressions.add("upper('FOOBAR').");
		expressions.add("upper('NOOB').");
		expressions.add("upper('PWNAGE').");
		
		expressions.add("foo('foobar').");

		// Create rules.
		expressions.add("?X = ?Y :- lower(?X), upper(?Y), STRING_TO_UPPER(?X, ?Y).");

		return expressions;
	}

	public void testString() throws Exception {
		// The result should be: foobar, noob, pwnage, FOOBAR, NOOB, PWNAGE

		IRelation relation = evaluate("?- lower(?X).");

		assertTrue("foobar not in relation.", relation.contains(Helper
				.createConstantTuple("foobar")));
		assertTrue("noob not in relation.", relation.contains(Helper
				.createConstantTuple("noob")));
		assertTrue("pwnage not in relation.", relation.contains(Helper
				.createConstantTuple("pwnage")));
		assertTrue("FOOBAR not in relation.", relation.contains(Helper
				.createConstantTuple("FOOBAR")));
		assertTrue("NOOB not in relation.", relation.contains(Helper
				.createConstantTuple("NOOB")));
		assertTrue("PWNAGE not in relation.", relation.contains(Helper
				.createConstantTuple("PWNAGE")));
		
		assertEquals("Relation does not have correct size", 6, relation.size());
	}

	public void testFoo() throws Exception {
		// The result should be: foobar, FOOBAR

		IRelation relation = evaluate("?- foo(?X).");

		assertTrue("foobar not in relation.", relation.contains(Helper
				.createConstantTuple("foobar")));
		assertTrue("FOOBAR not in relation.", relation.contains(Helper
				.createConstantTuple("FOOBAR")));
		
		assertEquals("Relation does not have correct size", 2, relation.size());
	}
	
}
