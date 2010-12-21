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
package org.deri.iris.builtins.datatype;

import static org.deri.iris.factory.Factory.BASIC;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IList;
import org.deri.iris.builtins.datatype.IsListBuiltin;
import org.deri.iris.builtins.list.AbstractListBuiltinTest;

public class IsListBuiltinTest extends AbstractListBuiltinTest {

	private IsListBuiltin builtin;

	private IList list_1, list_2;

	public void testBuiltin() throws EvaluationException {
		try {
			builtin = new IsListBuiltin();
			System.out.println(builtin.toString());
			fail("An IllegalArgumentException should be thrown if builtin has the wrong amount of paramenters.");
		} catch (IllegalArgumentException e) {
		}
		builtin = new IsListBuiltin(EMPTY_LIST);
		ITerm[] terms = {EMPTY_LIST, null};
		
		assertEquals(true, builtin.computeResult(terms));
		list_1 = new org.deri.iris.terms.concrete.List();
		list_2 = new org.deri.iris.terms.concrete.List();

		// External(pred:is-list(1)) will evaluate to f in any interpretation.
		terms[0] = ONE;
		assertEquals(false, builtin.computeResult(terms));
		terms[1] = list_1;
		assertEquals(false, builtin.computeResult(terms));

		list_1.add(ONE);
		assertFalse(list_1.isEmpty());
		terms[0] = list_1;
		terms[1] = null;
		assertEquals(true, builtin.computeResult(terms));

		list_2.add(ONE);
		list_2.add(ONE);
		list_2.add(TWO);
		list_2.add(list_1);
		terms[0] = list_2;
		terms[1] = null;

		ITerm[] terms2 = {list_2};
		assertEquals(true, builtin.computeResult(terms));
		assertFalse(list_2.equals(list_1));
		assertEquals(builtin.computeResult(terms), builtin
				.computeResult(terms2));
	}

	public void testTupleBuiltin() throws EvaluationException {
		list_1 = new org.deri.iris.terms.concrete.List();
		list_1.add(ONE);
		list_1.add(TWO);
		list_1.add(TWO);
		list_1.add(THREE);

		check(list_1, true);
	}

	private void check(ITerm listOne, boolean expected)
			throws EvaluationException {
		builtin = new IsListBuiltin(listOne);

		ITuple arguments = BASIC.createTuple(X, Y, Z);

		ITuple actualTuple = builtin.evaluate(arguments);

		if (expected) {
			assertNotNull(actualTuple);
		} else {
			assertNull(actualTuple);
		}
	}
}
