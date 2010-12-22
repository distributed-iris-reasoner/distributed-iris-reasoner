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

public class GetBuiltinTest extends AbstractListBuiltinTest {

	private GetBuiltin builtin;

	private IList list_1, list_2;

	public void testBuiltin() throws EvaluationException {
		try {
			builtin = new GetBuiltin();
			System.out.println(builtin.toString());
			fail("An IllegalArgumentException should be thrown if builtin has the wrong amount of paramenters.");
		} catch (IllegalArgumentException e) {
		}
		builtin = new GetBuiltin(EMPTY_LIST, EMPTY_LIST);

		list_1 = new org.deri.iris.terms.concrete.List();
		list_1.add(ONE);

		assertEquals(new IntTerm(1), builtin.computeResult(list_1, new IntTerm(
				0)));

		list_1.add(TWO);

		list_2 = new org.deri.iris.terms.concrete.List();
		list_2.add(THREE);
		list_2.add(list_1);

		assertEquals(list_1, builtin.computeResult(list_2, new IntTerm(1)));

		list_1 = new org.deri.iris.terms.concrete.List();
		list_2 = new org.deri.iris.terms.concrete.List();

		//
		list_1.add(ONE);

		assertEquals(ONE, builtin.computeResult(list_1, ZERO));

		// External( func:get(List(0 1 2 3 4) 1) ) = 1
		list_1.clear();
		list_1.add(ZERO);
		list_1.add(ONE);
		list_1.add(TWO);
		list_1.add(THREE);
		list_1.add(FOUR);

		assertEquals(THREE, builtin.computeResult(list_1, THREE));

		// External( func:get(List(0 1 2 3 4) -1) ) = 4
		assertEquals(FOUR, builtin.computeResult(list_1, new IntTerm(-1)));

		// External( func:get(List(0 1 2 3 4) -5) ) = 0
		assertEquals(ZERO, builtin.computeResult(list_1, new IntTerm(-5)));

		// External( func:get(List(0 1 2 3 4) -10) ) = (unspecified)
		try {
			builtin.computeResult(list_2, new IntTerm(-10));
		} catch (IndexOutOfBoundsException e) {
		}

		try {
			builtin.computeResult(list_2, new IntTerm(10));
		} catch (IndexOutOfBoundsException e) {
		}
	}
	
	public void testTupleBuiltin() throws EvaluationException {
		list_1 = new org.deri.iris.terms.concrete.List();
		list_1.add(ONE);
		list_1.add(TWO);
		list_1.add(TWO);
		list_1.add(THREE);
		list_1.add(FOUR);
		
		
		check(list_1, new IntTerm(3), THREE);
	}
	
	
	private void check(ITerm listOne, ITerm term2, ITerm expectedResult) throws EvaluationException {
		builtin = new GetBuiltin(listOne, term2);
		
		ITuple arguments = BASIC.createTuple(X, Y, Z);
		
		ITuple expectedTuple = BASIC.createTuple(expectedResult);

		ITuple actualTuple = builtin.evaluate(arguments);

		assertEquals(expectedTuple, actualTuple);
	}
}
