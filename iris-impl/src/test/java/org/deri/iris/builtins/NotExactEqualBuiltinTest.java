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

import org.deri.iris.api.builtins.IBuiltinAtom;

/**
 * Tests for the not exact equals built-in.
 */
public class NotExactEqualBuiltinTest extends TestCase {

	public static Test suite() {
		return new TestSuite(NotExactEqualBuiltinTest.class, NotExactEqualBuiltinTest.class.getSimpleName());
	}

	public void testEvaluation() throws Exception {
		final IBuiltinAtom xy = BuiltinsFactory.getInstance().createNotExactEqual( TERM.createVariable("X"), TERM.createVariable("Y"));

		assertNull("5 should not be not exactly equal to 5", xy.evaluate(
					BASIC.createTuple(CONCRETE.createInteger(5), CONCRETE.createInteger(5))));

		assertNull("10.0d should not be not exactly equal to 10.0d", xy.evaluate(
						BASIC.createTuple(CONCRETE.createDouble(10), CONCRETE.createDouble(10))));
		
		assertNull("10.0f should not be not exactly equal to 10.0f", xy.evaluate(
						BASIC.createTuple(CONCRETE.createFloat(10), CONCRETE.createFloat(10))));
		
		assertNull("+0.0d should not be not exactly equal to -0.0d", xy.evaluate(
						BASIC.createTuple(CONCRETE.createDouble(+0.0d), CONCRETE.createDouble(-0.0d))));
		
		assertNull("+0.0f should not be not exactly equal to -0.0f", xy.evaluate(
						BASIC.createTuple(CONCRETE.createFloat(+0.0f), CONCRETE.createFloat(-0.0f))));
		
		assertNotNull("5 should be not exactly equal to 5.0", xy.evaluate(
					BASIC.createTuple(CONCRETE.createInteger(5), CONCRETE.createDouble(5))));
		
		assertNotNull("5.0f should be not exactly equal to 5.0d", xy.evaluate(
						BASIC.createTuple(CONCRETE.createFloat(5), CONCRETE.createDouble(5))));
			
		assertNotNull("5 should be not exactly equal to 2", xy.evaluate(
					BASIC.createTuple(CONCRETE.createInteger(2), CONCRETE.createInteger(5))));
		
		assertNotNull("5 should be not exactly equal to a", xy.evaluate(
					BASIC.createTuple(CONCRETE.createInteger(5), TERM.createString("a"))));
	}
	
	public void test_isBuiltin() {
		final IBuiltinAtom xy = BuiltinsFactory.getInstance().createNotExactEqual( TERM.createVariable("X"), TERM.createVariable("Y"));
		assertTrue("buitin predicates should be identifiable as builtins", xy.isBuiltin());
	}
}
