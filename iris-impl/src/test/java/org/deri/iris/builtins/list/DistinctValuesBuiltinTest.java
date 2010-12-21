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

public class DistinctValuesBuiltinTest extends AbstractListBuiltinTest {

	private DistinctValuesBuiltin builtin;

	private IList list_1, list_2, list_3, expected;

	public void testBuiltin() throws EvaluationException {
		try {
			builtin = new DistinctValuesBuiltin();
			System.out.println(builtin.toString());
			fail("An IllegalArgumentException should be thrown if builtin has the wrong amount of paramenters.");
		} catch (IllegalArgumentException e) {
		}
		builtin = new DistinctValuesBuiltin(EMPTY_LIST);

		// 
		list_1 = new org.deri.iris.terms.concrete.List();
		expected = new org.deri.iris.terms.concrete.List();

		assertEquals(expected, builtin.computeResult(list_1));

		list_2 = new org.deri.iris.terms.concrete.List();
		list_2.add(ONE);
		expected = new org.deri.iris.terms.concrete.List();
		expected.add(ONE);
		assertEquals(expected, builtin.computeResult(list_2));

		list_3 = new org.deri.iris.terms.concrete.List();
		list_3.add(ONE);
		list_3.add(ONE);
		assertEquals(expected, builtin.computeResult(list_3));
		assertEquals(list_2, builtin.computeResult(list_3));

		// External( func:distinct-values(List(0 1 2 3 4)) ) = List(0 1 2 3 4)
		list_1.clear();
		list_1.add(ZERO);
		list_1.add(ONE);
		list_1.add(TWO);
		list_1.add(THREE);
		list_1.add(FOUR);

		expected.clear();
		expected.add(ZERO);
		expected.add(ONE);
		expected.add(TWO);
		expected.add(THREE);
		expected.add(FOUR);

		assertEquals(expected, builtin.computeResult(list_1));

		// External( func:distinct-values(List(0 1 2 3 4 0 4)) ) = List(0 1 2 3
		// 4)
		list_1.add(ZERO);
		list_1.add(FOUR);
		assertEquals(expected, builtin.computeResult(list_1));

		// External( func:distinct-values(List(3 3 3)) ) = List(3)
		list_1.clear();
		list_1.add(THREE);
		list_1.add(THREE);
		list_1.add(THREE);
		
		expected.clear();
		expected.add(THREE);
		assertEquals(expected, builtin.computeResult(list_1));
	}
	
	public void testTupleBuiltin() throws EvaluationException {
		list_1 = new org.deri.iris.terms.concrete.List();
		list_1.add(ONE);
		list_1.add(TWO);
		list_1.add(TWO);
		list_1.add(FOUR);
		
		expected = new org.deri.iris.terms.concrete.List();
		expected.add(ONE);
		expected.add(TWO);
		expected.add(FOUR);
		
		check(list_1, expected);
	}
	
	
	private void check(ITerm listOne, ITerm expectedResult) throws EvaluationException {
		builtin = new DistinctValuesBuiltin(listOne);
		
		ITuple arguments = BASIC.createTuple(X, Y, Z);
		
		ITuple expectedTuple = BASIC.createTuple(expectedResult);

		ITuple actualTuple = builtin.evaluate(arguments);

		assertEquals(expectedTuple, actualTuple);
	}
	

}
