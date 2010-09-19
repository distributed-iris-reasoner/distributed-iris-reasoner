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
package org.deri.iris.builtins.date;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.factory.Factory;

/**
 */
public class DateGreaterEqualBuiltinTest extends AbstractDateBuiltinTest {

	public DateGreaterEqualBuiltinTest(String name) {
		super(name);
	}

	public void testBuiltin() throws EvaluationException {
		ITerm date1 = Factory.CONCRETE.createDate(2010, 4, 26);
		ITerm date2 = Factory.CONCRETE.createDate(2010, 4, 26);
		ITerm date3 = Factory.CONCRETE.createDate(2010, 5, 26);
		ITerm date4 = Factory.CONCRETE.createDate(1997, 3, 12);

		DateGreaterEqualBuiltin builtin = new DateGreaterEqualBuiltin(X, Y);

		ITuple args = Factory.BASIC.createTuple(date1, date2);
		ITuple actual = builtin.evaluate(args);
		// (date1 = date2) -> null
		assertEquals(EMPTY_TUPLE, actual);

		builtin = new DateGreaterEqualBuiltin(X, Y);
		args = Factory.BASIC.createTuple(date3, date4);
		actual = builtin.evaluate(args);
		// (date3 > date4) -> iTuple()
		assertEquals(EMPTY_TUPLE, actual);

		builtin = new DateGreaterEqualBuiltin(X, Y);
		args = Factory.BASIC.createTuple(date4, date3);
		actual = builtin.evaluate(args);
		// (date4 < date3) -> null
		assertEquals(null, actual);
	}

}
