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
package org.deri.iris.builtins.list;

import static org.deri.iris.factory.Factory.BASIC;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IList;

public class IntersectBuiltinTest extends AbstractListBuiltinTest {

	private IntersectBuiltin builtin;

	private IList list_1, list_2, list_3, expected;

	public void testBuiltin() throws EvaluationException {
		try {
			builtin = new IntersectBuiltin();
			System.out.println(builtin.toString());
			fail("An IllegalArgumentException should be thrown if builtin has the wrong amount of paramenters.");
		} catch (IllegalArgumentException e) {
		}

		builtin = new IntersectBuiltin(EMPTY_LIST, EMPTY_LIST);

		//
		list_1 = new org.deri.iris.terms.concrete.List();
		expected = new org.deri.iris.terms.concrete.List();

		assertEquals(expected, builtin.computeResult(list_1, EMPTY_LIST));

		// 
		list_1 = new org.deri.iris.terms.concrete.List();
		list_1.add(ONE);

		expected = new org.deri.iris.terms.concrete.List();

		assertEquals(expected, builtin.computeResult(list_1, EMPTY_LIST));

		// 
		list_1 = new org.deri.iris.terms.concrete.List();
		list_1.add(ONE);

		list_2 = new org.deri.iris.terms.concrete.List();
		list_2.add(ONE);
		list_2.add(TWO);

		expected = new org.deri.iris.terms.concrete.List();
		expected.add(ONE);

		assertEquals(expected, builtin.computeResult(list_1, list_2));

		// 
		list_1 = new org.deri.iris.terms.concrete.List();
		list_1.add(ONE);

		list_2 = new org.deri.iris.terms.concrete.List();
		list_2.add(TWO);

		list_3 = new org.deri.iris.terms.concrete.List();
		list_3.add(ONE);
		list_3.add(THREE);

		expected = new org.deri.iris.terms.concrete.List();

		assertEquals(expected, builtin.computeResult(list_1, list_2, list_3));

		// 
		list_1 = new org.deri.iris.terms.concrete.List();
		list_1.add(ONE);
		list_1.add(TWO);

		list_2 = new org.deri.iris.terms.concrete.List();
		list_2.add(TWO);
		list_2.add(list_1);

		list_3 = new org.deri.iris.terms.concrete.List();
		list_3.add(THREE);

		expected = new org.deri.iris.terms.concrete.List();
		expected.add(TWO);

		assertFalse(list_3
				.equals(builtin.computeResult(list_1, list_2, list_3)));
		assertEquals(expected, builtin.computeResult(list_1, list_2, list_3));

		list_1 = new org.deri.iris.terms.concrete.List();
		list_1.add(ONE);
		list_1.add(TWO);

		list_2 = new org.deri.iris.terms.concrete.List();
		list_2.add(TWO);
		list_2.add(list_1);

		list_3 = new org.deri.iris.terms.concrete.List();
		list_3.add(list_2);
		list_3.add(list_1);

		expected = new org.deri.iris.terms.concrete.List();
		expected.add(TWO);

		assertFalse(list_3
				.equals(builtin.computeResult(list_1, list_2, list_3)));
		assertEquals(expected, builtin.computeResult(list_1, list_2, list_3));

		// External( func:intersect(List(0 1 2 3 4) List(3 1)) ) = List(1 3)
		list_1.clear();
		list_2.clear();

		list_1.add(ZERO);
		list_1.add(ONE);
		list_1.add(TWO);
		list_1.add(THREE);
		list_1.add(FOUR);

		list_2.add(THREE);
		list_2.add(ONE);

		expected.clear();
		expected.add(ONE);
		expected.add(THREE);
		assertEquals(expected, builtin.computeResult(list_1, list_2));
	}

	public void testTupleBuiltin() throws EvaluationException {
		list_1 = new org.deri.iris.terms.concrete.List();
		list_1.add(ONE);
		list_1.add(TWO);
		list_1.add(TWO);
		list_1.add(THREE);

		list_2 = new org.deri.iris.terms.concrete.List();
		list_2.add(ONE);

		expected = new org.deri.iris.terms.concrete.List();
		expected.add(ONE);

		check(list_1, list_2, expected);
	}

	private void check(ITerm listOne, ITerm term2, ITerm expectedResult)
			throws EvaluationException {
		builtin = new IntersectBuiltin(listOne, term2);

		ITuple arguments = BASIC.createTuple(X, Y, Z);

		ITuple expectedTuple = BASIC.createTuple(expectedResult);

		ITuple actualTuple = builtin.evaluate(arguments);

		assertEquals(expectedTuple, actualTuple);
	}
}
