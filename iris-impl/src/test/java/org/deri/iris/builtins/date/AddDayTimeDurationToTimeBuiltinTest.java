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
import org.deri.iris.builtins.AddBuiltin;
import org.deri.iris.factory.Factory;

/**
 */
public class AddDayTimeDurationToTimeBuiltinTest extends
		AbstractDateBuiltinTest {

	public AddDayTimeDurationToTimeBuiltinTest(String name) {
		super(name);
	}

	public void testBuiltin() throws EvaluationException {
		
		ITerm daytimeduration = Factory.CONCRETE.createDayTimeDuration(true, 0, 2, 0, 0, 0);
		ITerm time1 = Factory.CONCRETE.createTime(10, 4, 26.3, 0, 0);
		ITerm result = Factory.CONCRETE.createTime(12, 4, 26.3, 0, 0);

		AddBuiltin builtin = new AddDayTimeDurationToTimeBuiltin(X, Y, Z);

		args = Factory.BASIC.createTuple(time1, daytimeduration, result);
		actual = builtin.evaluate(args);
		
		assertEquals(EMPTY_TUPLE, actual);

	}

}