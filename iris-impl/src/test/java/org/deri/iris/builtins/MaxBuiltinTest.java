/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
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
package org.deri.iris.builtins;


import static org.deri.iris.factory.Factory.CONCRETE;
import static org.deri.iris.factory.Factory.TERM;
import junit.framework.TestCase;

import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.factory.Factory;

/**
 * <p>
 * Tests for the {@code MaxBuiltin}.
 * </p>
 */
public class MaxBuiltinTest extends TestCase {

	private final ITerm X = TERM.createVariable("X");
	private final ITerm Y = TERM.createVariable("Y");
	private final ITerm Z = TERM.createVariable("Z");
	private final ITerm T_1 = CONCRETE.createInteger(1);
	private final ITerm T_5 = CONCRETE.createInteger(5);

	public void testEvaluate() throws Exception {

		// max(1,5) = 5
		MaxBuiltin maxBuiltin = new MaxBuiltin(T_1, T_5, T_5);
		ITuple result = maxBuiltin.evaluate( Factory.BASIC.createTuple(T_1, T_5, T_5) );
		assertNotNull( result );

		// max(5,1) = 5
		maxBuiltin = new MaxBuiltin(T_5, T_1, T_5);
		result = maxBuiltin.evaluate( Factory.BASIC.createTuple(T_5, T_1, T_5) );
		assertNotNull( result );

		// max(1,5) != 1 
		maxBuiltin = new MaxBuiltin(T_1, T_5, T_1);
		result = maxBuiltin.evaluate( Factory.BASIC.createTuple(T_1, T_5, T_1) );
		assertNull( result );

		// max(5,1) != 1
		maxBuiltin = new MaxBuiltin(T_5, T_1, T_1);
		result = maxBuiltin.evaluate( Factory.BASIC.createTuple(T_5, T_1, T_1) );
		assertNull( result );

		maxBuiltin = new MaxBuiltin(X, Y, Z);
		result = maxBuiltin.evaluate(Factory.BASIC.createTuple(T_1, T_5, Z));
		assertEquals( Factory.BASIC.createTuple( T_5 ), result );
	}
	
	public void testWrongNumberOfArgument() throws Exception {
		
		try {
			new MaxBuiltin(T_1, T_5);
		}
		catch( IllegalArgumentException e ) {
			// Failed correctly
		}

		try {
			new MaxBuiltin(T_1, T_5, T_1, T_5);
		}
		catch( IllegalArgumentException e ) {
			// Failed correctly
		}
	}
}
