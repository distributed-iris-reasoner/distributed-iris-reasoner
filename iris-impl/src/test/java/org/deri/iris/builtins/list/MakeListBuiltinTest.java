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
import org.deri.iris.terms.concrete.IntTerm;

public class MakeListBuiltinTest extends AbstractListBuiltinTest {

	private MakeListBuiltin builtin;

	private IList list_1, expected;

	public void testBuiltin() throws EvaluationException {
		try {
			builtin = new MakeListBuiltin();
			System.out.println(builtin.toString());
			fail("An IllegalArgumentException should be thrown if builtin has the wrong amount of paramenters.");
		} catch (IllegalArgumentException e) {
		}

		builtin = new MakeListBuiltin(EMPTY_LIST, EMPTY_LIST);

		// External( func:make-list() ) = List()
		list_1 = new org.deri.iris.terms.concrete.List();
		expected = new org.deri.iris.terms.concrete.List();

		assertEquals(expected, builtin.computeResult());

		list_1.clear();
		expected.clear();
		expected.add(EMPTY_LIST);

		assertEquals(expected, builtin.computeResult(EMPTY_LIST));

		// External( func:make-list(0 1 List(20 21))) = List(0 1 List(20 21))
		list_1.clear();
		list_1.add(new IntTerm(20));
		list_1.add(new IntTerm(21));
		expected.clear();
		expected.add(ZERO);
		expected.add(ONE);
		expected.add(list_1);

		assertEquals(expected, builtin.computeResult(ZERO, ONE, list_1));
	}

	public void testTupleBuiltin() throws EvaluationException {
		expected = new org.deri.iris.terms.concrete.List();
		expected.add(ONE);
		expected.add(TWO);
		expected.add(TWO);
		expected.add(THREE);

		check(expected, ONE, TWO, TWO, THREE);
	}

	private void check(ITerm expectedResult, ITerm... values)
			throws EvaluationException {
		builtin = new MakeListBuiltin(values);

		ITuple arguments = BASIC.createTuple(X, Y, Z);

		ITuple expectedTuple = BASIC.createTuple(expectedResult);

		ITuple actualTuple = builtin.evaluate(arguments);

		assertEquals(expectedTuple, actualTuple);
	}
}
