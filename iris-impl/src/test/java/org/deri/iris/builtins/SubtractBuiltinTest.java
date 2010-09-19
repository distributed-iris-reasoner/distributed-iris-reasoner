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
 * Tests for the {@code SubtractBuiltin}.
 * </p>
 * <p>
 * $Id: SubtractBuiltinTest.java,v 1.4 2007-05-10 09:02:29 poettler_ric Exp $
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri dot org)
 * @version $Revision: 1.4 $
 */
public class SubtractBuiltinTest extends TestCase {

	public static Test suite() {
		return new TestSuite(SubtractBuiltinTest.class, SubtractBuiltinTest.class
				.getSimpleName());
	}

	public void testEvaluate() throws Exception {
		final ITerm X = TERM.createVariable("X");
		final ITerm Y = TERM.createVariable("Y");
		final ITerm Z = TERM.createVariable("Z");
		final ITerm T_11 = CONCRETE.createInteger(11);
		final ITerm T_5 = CONCRETE.createInteger(5);
		final ITerm T_6 = CONCRETE.createInteger(6);

		// X - 5 = 6
		final SubtractBuiltin b_x56 = new SubtractBuiltin(X, T_5, T_6);
		assertEquals(BASIC.createTuple(T_11), b_x56.evaluate(BASIC.createTuple(X, X, X)));
		// 11 - X = 6
		final SubtractBuiltin b_11x6 = new SubtractBuiltin(T_11, X, T_6);
		assertEquals(BASIC.createTuple(T_5), b_11x6.evaluate(BASIC.createTuple(X, X, X)));
		// 11 - 5 = X
		final SubtractBuiltin b_115x = new SubtractBuiltin(T_11, T_5, X);
		assertEquals(BASIC.createTuple(T_6), b_115x.evaluate(BASIC.createTuple(X, X, X)));
		// 11 - X = Y
		final SubtractBuiltin b_11xy = new SubtractBuiltin(T_11, X, Y);
		assertEquals(BASIC.createTuple(T_5), b_11xy.evaluate(BASIC.createTuple(X, X, T_6)));
		assertEquals(BASIC.createTuple(T_6), b_11xy.evaluate(BASIC.createTuple(X, T_5, X)));
		// X - 5 = Y
		final SubtractBuiltin b_x5y = new SubtractBuiltin(X, T_5, Y);
		assertEquals(BASIC.createTuple(T_11), b_x5y.evaluate(BASIC.createTuple(X, X, T_6)));
		assertEquals(BASIC.createTuple(T_6), b_x5y.evaluate(BASIC.createTuple(T_11, X, X)));
		// X - Y = 6
		final SubtractBuiltin b_xy6 = new SubtractBuiltin(X, Y, T_6);
		assertEquals(BASIC.createTuple(T_11), b_xy6.evaluate(BASIC.createTuple(X, T_5, X)));
		assertEquals(BASIC.createTuple(T_5), b_xy6.evaluate(BASIC.createTuple(T_11, X, X)));
		// X - Y = Z
		final SubtractBuiltin b_xyz = new SubtractBuiltin(X, Y, Z);
		assertEquals(BASIC.createTuple(T_11), b_xyz.evaluate(BASIC.createTuple(X, T_5, T_6)));
		assertEquals(BASIC.createTuple(T_5), b_xyz.evaluate(BASIC.createTuple(T_11, X, T_6)));
		assertEquals(BASIC.createTuple(T_6), b_xyz.evaluate(BASIC.createTuple(T_11, T_5, X)));

		// test the checking for correctness
		assertNotNull(b_x56.evaluate(BASIC.createTuple(T_11, T_5, T_6)));
		assertNotNull(b_11x6.evaluate(BASIC.createTuple(T_11, T_5, T_6)));
		assertNotNull(b_115x.evaluate(BASIC.createTuple(T_11, T_5, T_6)));
		assertNotNull(b_11xy.evaluate(BASIC.createTuple(T_11, T_5, T_6)));
		assertNotNull(b_x5y.evaluate(BASIC.createTuple(T_11, T_5, T_6)));
		assertNotNull(b_xy6.evaluate(BASIC.createTuple(T_11, T_5, T_6)));
		assertNotNull(b_xyz.evaluate(BASIC.createTuple(T_11, T_5, T_6)));

		assertNull(b_x56.evaluate(BASIC.createTuple(T_5, T_6, T_11)));
		assertNull(b_11x6.evaluate(BASIC.createTuple(T_5, T_6, T_11)));
		assertNull(b_115x.evaluate(BASIC.createTuple(T_5, T_6, T_11)));
		assertNull(b_11xy.evaluate(BASIC.createTuple(T_5, T_6, T_11)));
		assertNull(b_x5y.evaluate(BASIC.createTuple(T_5, T_6, T_11)));
		assertNull(b_xy6.evaluate(BASIC.createTuple(T_5, T_6, T_11)));
		assertNull(b_xyz.evaluate(BASIC.createTuple(T_5, T_6, T_11)));
	}
}
