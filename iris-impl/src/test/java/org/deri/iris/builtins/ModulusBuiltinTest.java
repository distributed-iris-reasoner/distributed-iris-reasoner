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
 * Tests for the {@code ModulusBuiltin}.
 */
public class ModulusBuiltinTest extends TestCase {

	public static Test suite() {
		return new TestSuite(ModulusBuiltinTest.class, ModulusBuiltinTest.class
				.getSimpleName());
	}
	
	final ITerm X = TERM.createVariable("X");
	final ITerm Y = TERM.createVariable("Y");
	final ITerm Z = TERM.createVariable("Z");

	final ITerm S = TERM.createString( "a" );

	final ITerm T_18 = CONCRETE.createInteger(18);
	final ITerm T_17 = CONCRETE.createInteger(17);
	final ITerm T_15 = CONCRETE.createInteger(15);
	final ITerm T_5 = CONCRETE.createInteger(5);
	final ITerm T_3 = CONCRETE.createInteger(3);
	final ITerm T_2 = CONCRETE.createInteger(2);

	final ModulusBuiltin b = new ModulusBuiltin(X, Y, Z);
	
	public void testVariableFirst() throws Exception
	{
		final ModulusBuiltin b = new ModulusBuiltin(X, T_5, T_2);
		assertEquals(BASIC.createTuple(T_2), b.evaluate(BASIC.createTuple(X, X, X)));
	}

	public void testVariableSecond() throws Exception
	{
		final ModulusBuiltin b = new ModulusBuiltin(T_17, X, T_2);
		assertEquals(BASIC.createTuple(T_15), b.evaluate(BASIC.createTuple(X, X, X)));
	}
	
	public void testVariableThird() throws Exception
	{
		final ModulusBuiltin b = new ModulusBuiltin(T_17, T_5, X);
		assertEquals(BASIC.createTuple(T_2), b.evaluate(BASIC.createTuple(X, X, X)));
	}
	
	public void testCorrect() throws Exception
	{
		assertNotNull( b.evaluate(BASIC.createTuple(T_18, T_5, T_3)));
		assertNotNull( b.evaluate(BASIC.createTuple(T_17, T_5, T_2)));
		assertNotNull( b.evaluate(BASIC.createTuple(T_2, T_5, T_2)));
	}
	
	public void testFalse() throws Exception
	{
		assertNull( b.evaluate(BASIC.createTuple(T_18, T_5, T_2)));
		assertNull( b.evaluate(BASIC.createTuple(T_17, T_2, T_2)));
		assertNull( b.evaluate(BASIC.createTuple(T_5, T_5, T_2)));
	}

	public void testWrongDataType() throws Exception
	{
		assertNull( b.evaluate(BASIC.createTuple(S, T_5, T_2)));
		assertNull( b.evaluate(BASIC.createTuple(T_5, S, T_2)));
		assertNull( b.evaluate(BASIC.createTuple(T_5, T_5, S)));
	}
}
