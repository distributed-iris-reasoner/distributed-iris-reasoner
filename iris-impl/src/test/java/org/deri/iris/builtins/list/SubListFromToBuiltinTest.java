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

import org.deri.iris.EvaluationException;
import org.deri.iris.api.terms.concrete.IList;
import org.deri.iris.terms.concrete.IntTerm;

public class SubListFromToBuiltinTest extends AbstractListBuiltinTest {

	private SubListFromToBuiltin builtin;

	private IList list_1, list_2, expected;

	public void testBuiltin() throws EvaluationException {

		try {
			builtin = new SubListFromToBuiltin();
			System.out.println(builtin.toString());
			fail("An IllegalArgumentException should be thrown if built-in has the wrong amount of paramenters.");
		} catch (IllegalArgumentException e) {
		}

		builtin = new SubListFromToBuiltin(EMPTY_LIST, new IntTerm(0), new IntTerm(
				0));
		//
		list_1 = new org.deri.iris.terms.concrete.List();
		list_2 = new org.deri.iris.terms.concrete.List();
		expected = new org.deri.iris.terms.concrete.List();

		// External( func:sublist(List(0 1 2 3 4) 0 0) ) = List()
		assertEquals(expected, builtin.computeResult(list_1, ZERO, ZERO));
		list_1.add(ZERO);
		list_1.add(ONE);
		list_1.add(TWO);
		list_1.add(THREE);
		list_1.add(FOUR);
		assertEquals(expected, builtin.computeResult(list_1, ZERO, ZERO));

		// External( func:sublist(List(0 1 2 3 4) 0 1) ) = List(0)
		list_2.clear();
		list_2.add(ZERO);
		assertEquals(list_2, builtin.computeResult(list_1, ZERO, ONE));

		// External( func:sublist(List(0 1 2 3 4) 0 4) ) = List(0 1 2 3)
		expected.clear();
		expected.add(ZERO);
		expected.add(ONE);
		expected.add(TWO);
		expected.add(THREE);
		assertEquals(expected, builtin.computeResult(list_1, ZERO, FOUR));

		// External( func:sublist(List(0 1 2 3 4) 0 10) ) = List(0 1 2 3 4)
		expected.add(FOUR);
		assertEquals(expected, builtin.computeResult(list_1, ZERO, new IntTerm(
				10)));

		// External( func:sublist(List(0 1 2 3 4) 0 -2) ) = List(0 1 2)
		expected.clear();
		expected.add(ZERO);
		expected.add(ONE);
		expected.add(TWO);
		assertEquals(expected, builtin.computeResult(list_1, ZERO, new IntTerm(-2)));
		
		// External( func:sublist(List(0 1 2 3 4) 2 4) ) = List(2 3)
		expected.clear();
		expected.add(TWO);
		expected.add(THREE);
		assertEquals(expected, builtin.computeResult(list_1, TWO, FOUR));
		
		// External( func:sublist(List(0 1 2 3 4) 2 -2) ) = List(2)
		expected.clear();
		expected.add(TWO);
		assertEquals(expected, builtin.computeResult(list_1, TWO, new IntTerm(-2)));

	}
}
