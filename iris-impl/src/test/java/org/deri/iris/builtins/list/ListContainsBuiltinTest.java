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

public class ListContainsBuiltinTest extends AbstractListBuiltinTest {

	private ListContainsBuiltin builtin;

	private IList list_1, list_2;

	public void testBuiltin() throws EvaluationException {
		try {
			builtin = new ListContainsBuiltin();
			System.out.println(builtin.toString());
			fail("An IllegalArgumentException should be thrown if builtin has the wrong amount of paramenters.");
		} catch (IllegalArgumentException e) {
		}
		builtin = new ListContainsBuiltin(EMPTY_LIST, EMPTY_LIST);

		list_1 = new org.deri.iris.terms.concrete.List();
		list_2 = new org.deri.iris.terms.concrete.List();
		assertFalse(builtin.computeResult(toArray(list_1, EMPTY_LIST)));

		// External(pred:list-contains(List(0 1 2 3 4) 2) will evaluate to t in
		// any interpretation.
		list_1.add(ZERO);
		list_1.add(ONE);
		list_1.add(TWO);
		list_1.add(THREE);
		list_1.add(FOUR);
		assertTrue(builtin.computeResult(toArray(list_1, TWO)));

		// External(pred:list-contains(List(2 2 3 4 5 2 2) 1) will evaluate to f
		// in any interpretation.
		list_1.clear();
		list_1.add(TWO);
		list_1.add(TWO);
		list_1.add(THREE);
		list_1.add(FOUR);
		list_1.add(new IntTerm(5));
		list_1.add(TWO);
		list_1.add(TWO);
		assertFalse(builtin.computeResult(toArray(list_1, ONE)));

		// External(pred:list-contains(List() 1) will evaluate to f in any
		// interpretation.
		list_1.clear();
		assertFalse(builtin.computeResult(toArray(list_1, ONE)));

		list_2.add(ONE);
		list_2.add(THREE);

		list_1.add(list_2);
		list_1.add(ONE);
		assertTrue(builtin.computeResult(toArray(list_1, list_2)));
	}
	
	public void testTupleBuiltin() throws EvaluationException {
		list_1 = new org.deri.iris.terms.concrete.List();
		list_1.add(ONE);
		list_1.add(TWO);
		list_1.add(TWO);
		list_1.add(THREE);

		check(list_1, TWO, true);
	}

	private void check(ITerm listOne, ITerm term2, boolean expected)
			throws EvaluationException {
		builtin = new ListContainsBuiltin(listOne, term2);

		ITuple arguments = BASIC.createTuple(X, Y, Z);

		ITuple actualTuple = builtin.evaluate(arguments);

		if (expected) {
			assertNotNull(actualTuple);
		} else {
			assertNull(actualTuple);
		}
	}

	private ITerm[] toArray(ITerm... terms) {
		ITerm[] result = new ITerm[terms.length];
		int i = 0;
		for (ITerm t : terms) {
			result[i] = t;
			i++;
		}
		return result;
	}
	
	
}
