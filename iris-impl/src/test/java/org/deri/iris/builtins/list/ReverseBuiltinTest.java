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

public class ReverseBuiltinTest extends AbstractListBuiltinTest {

	private ReverseBuiltin builtin;

	private IList list_1, list_2, expected;

	public void testBuiltin() throws EvaluationException {

		try {
			builtin = new ReverseBuiltin();
			System.out.println(builtin.toString());
			fail("An IllegalArgumentException should be thrown if built-in has the wrong amount of paramenters.");
		} catch (IllegalArgumentException e) {
		}

		builtin = new ReverseBuiltin(EMPTY_LIST);
		//
		list_1 = new org.deri.iris.terms.concrete.List();
		list_2 = new org.deri.iris.terms.concrete.List();
		expected = new org.deri.iris.terms.concrete.List();

		// External( func:reverse(List()) ) = List()
		assertEquals(EMPTY_LIST, builtin.computeResult(EMPTY_LIST));

		// External( func:reverse(List(1)) ) = List(1)
		list_1.add(ONE);
		expected.add(ONE);
		assertEquals(expected, builtin.computeResult(list_1));

		// External( func:reverse(List(0 1 2 3 4)) ) = List(4 3 2 1 0)
		list_1.clear();
		list_1.add(ZERO);
		list_1.add(ONE);
		list_1.add(TWO);
		list_1.add(THREE);
		list_1.add(FOUR);

		expected.clear();
		expected.add(FOUR);
		expected.add(THREE);
		expected.add(TWO);
		expected.add(ONE);
		expected.add(ZERO);

		list_2.clear();
		list_2.addAll(list_1);

		assertEquals(list_1, list_2);
		assertEquals(expected, builtin.computeResult(list_1));
		assertEquals(list_1, list_2);

		// 
		list_1.add(list_2);
		expected.clear();
		expected.add(list_2);
		expected.add(FOUR);
		expected.add(THREE);
		expected.add(TWO);
		expected.add(ONE);
		expected.add(ZERO);
		assertEquals(expected, builtin.computeResult(list_1));
	}

	public void testTupleBuiltin() throws EvaluationException {
		list_1 = new org.deri.iris.terms.concrete.List();
		list_1.add(FOUR);
		list_1.add(THREE);
		list_1.add(TWO);
		list_1.add(ONE);

		expected = new org.deri.iris.terms.concrete.List();
		expected.add(ONE);
		expected.add(TWO);
		expected.add(THREE);
		expected.add(FOUR);

		check(list_1, expected);
	}

	private void check(ITerm listOne, ITerm expectedResult)
			throws EvaluationException {
		builtin = new ReverseBuiltin(listOne);

		ITuple arguments = BASIC.createTuple(X, Y, Z);

		ITuple expectedTuple = BASIC.createTuple(expectedResult);

		ITuple actualTuple = builtin.evaluate(arguments);

		assertEquals(expectedTuple, actualTuple);
	}
}
