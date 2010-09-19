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
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.factory.Factory;

/**
 */
public class DurationEqualBuiltinTest extends AbstractDateBuiltinTest {

	public DurationEqualBuiltinTest(String name) {
		super(name);
	}

	public void testBuiltin() throws EvaluationException {
		ITerm duration1 = Factory.CONCRETE.createDuration(true, 1988, 5, 12, 6,
				2, 11);
		ITerm duration2 = Factory.CONCRETE.createDuration(true, 1988, 5, 12, 6,
				2, 11);
		ITerm duration3 = Factory.CONCRETE.createDuration(true, 1979, 10, 28,
				12, 56, 23);
		ITerm duration4 = Factory.CONCRETE.createDuration(false, 1991, 1, 8,
				12, 56, 23);

		DurationEqualBuiltin builtin = new DurationEqualBuiltin(X,Y);
		args = Factory.BASIC.createTuple(duration1, duration1);
		actual = builtin.evaluate(args);
		// duration1 = duration1
		assertEquals(EMPTY_TUPLE, actual);

		builtin = new DurationEqualBuiltin(X,Y);
		args = Factory.BASIC.createTuple(duration1, duration2);
		actual = builtin.evaluate(args);
		// duration1 = duration2
		assertEquals(EMPTY_TUPLE, actual);

		builtin = new DurationEqualBuiltin(X,Y);
		args = Factory.BASIC.createTuple(duration4, duration3);
		actual = builtin.evaluate(args);
		// duration1 > duration3
		assertEquals(null, actual);

		builtin = new DurationEqualBuiltin(duration3, duration4);
		args = Factory.BASIC.createTuple(X);
		actual = builtin.evaluate(args);
		// duration3 < duration4
		assertEquals(null, actual);

	}

}