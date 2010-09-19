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
public class TimeGreaterBuiltinTest extends AbstractDateBuiltinTest {

	public TimeGreaterBuiltinTest(String name) {
		super(name);
	}
	
	public void testBuiltin() throws EvaluationException {
		ITerm time1 = Factory.CONCRETE.createTime(20, 12, 43.5, 0, 0);
		ITerm time2 = Factory.CONCRETE.createTime(20, 12, 43.5, 0, 0);
		ITerm time3 = Factory.CONCRETE.createTime(6, 34, 20.8, 0, 0);

		// time1 = time1
		TimeGreaterBuiltin builtin = new TimeGreaterBuiltin(X,Y); 	
		args = Factory.BASIC.createTuple(time1, time1); 	
		actual = builtin.evaluate(args);
		assertEquals(null, actual );
		
		// time1 = time2
		builtin = new TimeGreaterBuiltin(X,Y); 	
		args = Factory.BASIC.createTuple(time1, time2); 	
		actual = builtin.evaluate(args);
		assertEquals(null, actual );
		
		// time1 > time3
		builtin = new TimeGreaterBuiltin(X,Y); 	
		args = Factory.BASIC.createTuple(time1, time3); 	
		actual = builtin.evaluate(args);
		assertEquals(EMPTY_TUPLE, actual );
		
		// time3 < time2
		builtin = new TimeGreaterBuiltin(X,Y); 	
		args = Factory.BASIC.createTuple(time3, time2); 	
		actual = builtin.evaluate(args);
		assertEquals(null, actual );
	}
}
