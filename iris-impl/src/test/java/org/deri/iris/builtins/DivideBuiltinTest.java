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

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.CONCRETE;
import static org.deri.iris.factory.Factory.TERM;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.api.terms.ITerm;

/**
 * <p>
 * Tests for the {@code DivideBuiltin}.
 * </p>
 * <p>
 * $Id: DivideBuiltinTest.java,v 1.5 2007-05-10 09:02:29 poettler_ric Exp $
 * </p>
 * 
 * @author Richard Pöttler, richard dot poettler at deri dot org
 * @version $Revision: 1.5 $
 */
public class DivideBuiltinTest extends TestCase {

	public static Test suite() {
		return new TestSuite(DivideBuiltinTest.class, DivideBuiltinTest.class
				.getSimpleName());
	}

	public void testEvaluate() throws Exception {
		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm Z = TERM.createVariable("Z");
		final ITerm T_10 = CONCRETE.createInteger(10);
		final ITerm T_5 = CONCRETE.createInteger(5);
		final ITerm T_2 = CONCRETE.createInteger(2);

		// X / 5 = 2
		final DivideBuiltin b_x52 = new DivideBuiltin(X, T_5, T_2);
		assertEquals(BASIC.createTuple(T_10), b_x52.evaluate(BASIC.createTuple(X, X, X)));
		// 10 / X = 2
		final DivideBuiltin b_10x2 = new DivideBuiltin(T_10, X, T_2);
		assertEquals(BASIC.createTuple(T_5), b_10x2.evaluate(BASIC.createTuple(X, X, X)));
		// 10 / 5 = X
		final DivideBuiltin b_105x = new DivideBuiltin(T_10, T_5, X);
		assertEquals(BASIC.createTuple(T_2), b_105x.evaluate(BASIC.createTuple(X, X, X)));
		// 10 / X = Y
		final DivideBuiltin b_10xy = new DivideBuiltin(T_10, X, Y);
		assertEquals(BASIC.createTuple(T_5), b_10xy.evaluate(BASIC.createTuple(X, X, T_2)));
		assertEquals(BASIC.createTuple(T_2), b_10xy.evaluate(BASIC.createTuple(X, T_5, X)));
		// X / 5 = Y
		final DivideBuiltin b_x5y = new DivideBuiltin(X, T_5, Y);
		assertEquals(BASIC.createTuple(T_10), b_x5y.evaluate(BASIC.createTuple(X, X, T_2)));
		assertEquals(BASIC.createTuple(T_2), b_x5y.evaluate(BASIC.createTuple(T_10, X, X)));
		// X / Y = 2
		final DivideBuiltin b_xy2 = new DivideBuiltin(X, Y, T_2);
		assertEquals(BASIC.createTuple(T_10), b_xy2.evaluate(BASIC.createTuple(X, T_5, X)));
		assertEquals(BASIC.createTuple(T_5), b_xy2.evaluate(BASIC.createTuple(T_10, X, X)));
		// X / Y = Z
		final DivideBuiltin b_xyz = new DivideBuiltin(X, Y, Z);
		assertEquals(BASIC.createTuple(T_10), b_xyz.evaluate(BASIC.createTuple(X, T_5, T_2)));
		assertEquals(BASIC.createTuple(T_5), b_xyz.evaluate(BASIC.createTuple(T_10, X, T_2)));
		assertEquals(BASIC.createTuple(T_2), b_xyz.evaluate(BASIC.createTuple(T_10, T_5, X)));

		// test the checking for correctness
		assertNotNull(b_x52.evaluate(BASIC.createTuple(T_10, T_5, T_2)));
		assertNotNull(b_10x2.evaluate(BASIC.createTuple(T_10, T_5, T_2)));
		assertNotNull(b_105x.evaluate(BASIC.createTuple(T_10, T_5, T_2)));
		assertNotNull(b_10xy.evaluate(BASIC.createTuple(T_10, T_5, T_2)));
		assertNotNull(b_x5y.evaluate(BASIC.createTuple(T_10, T_5, T_2)));
		assertNotNull(b_xy2.evaluate(BASIC.createTuple(T_10, T_5, T_2)));
		assertNotNull(b_xyz.evaluate(BASIC.createTuple(T_10, T_5, T_2)));

		assertNull(b_x52.evaluate(BASIC.createTuple(T_5, T_2, T_10)));
		assertNull(b_10x2.evaluate(BASIC.createTuple(T_5, T_2, T_10)));
		assertNull(b_105x.evaluate(BASIC.createTuple(T_5, T_2, T_10)));
		assertNull(b_10xy.evaluate(BASIC.createTuple(T_5, T_2, T_10)));
		assertNull(b_x5y.evaluate(BASIC.createTuple(T_5, T_2, T_10)));
		// changed this because 2.5 would be casted to 2
		assertNull(b_xy2.evaluate(BASIC.createTuple(CONCRETE.createFloat(5f), T_2, T_10)));
		assertNull(b_xyz.evaluate(BASIC.createTuple(T_5, T_2, T_10)));
	}
}
