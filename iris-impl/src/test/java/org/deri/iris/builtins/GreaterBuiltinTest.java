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

/**
 * <p>
 * Tests for the greater builtin.
 * </p>
 * <p>
 * $Id: GreaterBuiltinTest.java,v 1.5 2007-10-10 14:58:27 bazbishop237 Exp $
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri dot org)
 * @version $Revision: 1.5 $
 */
public class GreaterBuiltinTest extends TestCase {

	public static Test suite() {
		return new TestSuite(GreaterBuiltinTest.class, GreaterBuiltinTest.class
				.getSimpleName());
	}

	public void testEvaluation() throws Exception {
		final GreaterBuiltin xy = new GreaterBuiltin(TERM.createVariable("X"), TERM.createVariable("Y"));

		assertNull("5 shouldn't be greater than 5", xy.evaluate(
					BASIC.createTuple(CONCRETE.createInteger(5), CONCRETE.createInteger(5))));
		assertNull("5 shouldn't be greater than 5.0", xy.evaluate(
					BASIC.createTuple(CONCRETE.createInteger(5), CONCRETE.createDouble(5d))));

		assertNull("2 shouldn't be greater than 5.0", xy.evaluate(
					BASIC.createTuple(CONCRETE.createInteger(2), CONCRETE.createDouble(5d))));
		assertNotNull("5 should be greater than 2", xy.evaluate(
					BASIC.createTuple(CONCRETE.createInteger(5), CONCRETE.createInteger(2))));

		assertNull("a shouldn't be greater than b", xy.evaluate(
					BASIC.createTuple(TERM.createString("a"), TERM.createString("b"))));
		assertNull("a shouldn't be greater to a", xy.evaluate(
					BASIC.createTuple(TERM.createString("a"), TERM.createString("a"))));

		assertEquals( null, xy.evaluate(BASIC.createTuple(CONCRETE.createInteger(5), TERM.createString("a")) ));
	}

	public void test_isBuiltin() {
		assertTrue("buitin predicates should be identifiable as builtins", (new GreaterBuiltin(CONCRETE
				.createInteger(2), CONCRETE.createInteger(5)).isBuiltin()));
	}

}
