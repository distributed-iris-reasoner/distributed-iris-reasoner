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
public class ObviousTest extends ProgramEvaluationTest {

	public ObviousTest(String name) {
		super(name);
	}

	@Override
	public Collection<String> createExpressions() {
		Collection<String> expressions = new ArrayList<String>();

		// Create facts.
		expressions.add("person('B1').");
		expressions.add("person('B2').");
		expressions.add("person('A1').");
		expressions.add("person('C3').");

		expressions.add("hasName('B1', 'barry').");
		expressions.add("hasName('B2', 'barry').");
		expressions.add("hasName('A1', 'anne').");
		expressions.add("hasName('C3', 'charlie').");

		expressions.add("atOffsite('B1').");
		expressions.add("atOffsite('A1').");

		expressions.add("atEswc('B2').");
		expressions.add("atEswc('C3').");

		// Create rules.
		expressions
				.add("?X = ?Y :- person(?X), person(?Y), hasName(?X, ?N1), hasName(?Y, ?N2), ?N1 = ?N2.");

		return expressions;
	}

	public void testAtOffSite() throws Exception {
		// The result should be: A1, B1, B2.
		// B1 and B2 are both persons and have both name "barry", therefore they
		// are equal according to rule1. Therefore B2 is also in the relation.

		IRelation relation = evaluate("?- atOffsite(?X).");
		
		assertTrue("A1 not in relation.", relation.contains(Helper
				.createConstantTuple("A1")));
		assertTrue("B1 not in relation.", relation.contains(Helper
				.createConstantTuple("B1")));
		assertTrue("B2 not in relation.", relation.contains(Helper
				.createConstantTuple("B2")));

		assertEquals("Relation does not have correct size", 3, relation.size());
	}

	public void testAtOffSiteAndAtEswc() throws Exception {
		// The result should be: B1, B2.
		// B1 and B2 are both persons and have both name "barry", therefore they
		// are equal according to rule1. Therefore, if B1 is atOffSite and B2 is
		// atEswc, both are atOffSite AND atEswc.

		IRelation relation = evaluate("?- atOffsite(?X), atEswc(?X).");

		assertTrue("B1 not in relation.", relation.contains(Helper
				.createConstantTuple("B1")));
		assertTrue("B2 not in relation.", relation.contains(Helper
				.createConstantTuple("B2")));

		assertEquals("Relation does not have correct size", 2, relation.size());
	}

}
