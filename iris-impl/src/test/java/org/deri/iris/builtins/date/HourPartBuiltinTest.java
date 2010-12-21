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

import static org.deri.iris.factory.Factory.TERM;
import junit.framework.TestCase;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.factory.Factory;

/**
 * Test for HourPartBuiltin.
 */
public class HourPartBuiltinTest extends TestCase {

	private static final ITerm X = Factory.TERM.createVariable("X");

	private static final ITerm Y = TERM.createVariable("Y");

	public HourPartBuiltinTest(String name) {
		super(name);
	}

	public void testHoursFromTime() throws EvaluationException {
		ITerm time = Factory.CONCRETE.createTime(8, 53, 23.5, 0, 0);
		check(8, time);

		time = Factory.CONCRETE.createTime(24, 0, 0.0, 0, 0);
		check(0, time);
	}

	public void testHoursFromDateTime() throws EvaluationException {
		ITerm dateTime = Factory.CONCRETE.createDateTime(1999, 5, 31, 24, 0,
				00, 0, 0);
		check(0, dateTime);
	}

	public void testHoursFromDuration() throws EvaluationException {
		ITerm duration = Factory.CONCRETE.createDuration(true, 0, 0, 3, 10, 0,
				0);
		check(10, duration);

		duration = Factory.CONCRETE.createDuration(true, 0, 0, 3, 12, 32, 12);
		check(12, duration);

		duration = Factory.CONCRETE.createDuration(true, 0, 0, 0, 123, 0, 0);
		check(3, duration);

		duration = Factory.CONCRETE.createDuration(false, 0, 0, 3, 10, 0, 0);
		check(-10, duration);
	}

	private void check(int expected, ITerm time) throws EvaluationException {
		HourPartBuiltin builtin = new HourPartBuiltin(time, Y);

		ITuple arguments = Factory.BASIC.createTuple(X, Y);
		ITuple expectedTuple = Factory.BASIC.createTuple(Factory.CONCRETE
				.createInteger(expected));
		ITuple actual = builtin.evaluate(arguments);

		assertEquals(expectedTuple, actual);
	}
}
