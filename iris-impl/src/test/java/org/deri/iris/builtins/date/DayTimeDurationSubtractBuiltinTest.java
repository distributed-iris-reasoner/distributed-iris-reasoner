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
public class DayTimeDurationSubtractBuiltinTest extends AbstractDateBuiltinTest {

	public DayTimeDurationSubtractBuiltinTest(String name) {
		super(name);
	}

	public void testBuiltin() throws EvaluationException {
		ITerm daytimeduration1 = Factory.CONCRETE.createDayTimeDuration(true,
				15, 23, 19, 12.0);
		ITerm daytimeduration2 = Factory.CONCRETE.createDayTimeDuration(true,
				14, 22, 18, 11.0);
		ITerm result = Factory.CONCRETE.createDayTimeDuration(true, 1, 1, 1,
				1.0);

		DayTimeDurationSubtractBuiltin builtin = new DayTimeDurationSubtractBuiltin(X, Y, Z);

		args = Factory.BASIC.createTuple(daytimeduration1, daytimeduration2,
				result);
		actual = builtin.evaluate(args);
		
		assertEquals(EMPTY_TUPLE, actual);

	

	}
}