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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.builtins.IBuiltinAtom;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.compiler.BuiltinRegister;
import org.deri.iris.compiler.Parser;
import org.deri.iris.factory.Factory;

/**
 * <p>
 * Test the possibility of custom made builtins.
 * </p>
 * <p>
 * $Id: FahrenheitBuiltinTest.java,v 1.6 2007-10-30 08:28:31 poettler_ric Exp $
 * </p>
 * @version $Revision: 1.6 $
 * @author Richard Pöttler, richard dot poettler at deri dot org
 */
public class FahrenheitBuiltinTest extends TestCase {

	public static Test suite() {
		return new TestSuite(FahrenheitBuiltinTest.class, FahrenheitBuiltinTest.class.getSimpleName());
	}

	public void testAdding() {
		
		final BuiltinRegister reg = new BuiltinRegister();
		reg.registerBuiltin( instance );
		final IPredicate ftoc = instance.getBuiltinPredicate();
		assertNotNull("It seems that the builtin wasn't registered correctly", 
				reg.getBuiltinClass(ftoc.getPredicateSymbol()));
		assertEquals("The class of the builtin wasn't returned correctly", 
				FahrenheitToCelsiusBuiltin.class, reg.getBuiltinClass(ftoc.getPredicateSymbol()));
		assertEquals("The arity of the builtin wasn't returned correctly", 
				ftoc.getArity(), reg.getBuiltinArity(ftoc.getPredicateSymbol()));
	}

	public void testParsing() throws Exception {
		final BuiltinRegister reg = new BuiltinRegister();
		reg.registerBuiltin( instance );

		Parser parser = new Parser( reg );
		parser.parse("fahrenheit(?X) :- ftoc(?X, 10).");
		final ILiteral b = parser.getRules().iterator().next().getBody().get(0);
		assertTrue("The atom must be a IBuiltInAtom", b.getAtom() instanceof IBuiltinAtom);
	}

	private final ITerm t1 = Factory.TERM.createVariable( "a" );
	private final ITerm t2 = Factory.TERM.createVariable( "a" );
	private final FahrenheitToCelsiusBuiltin instance = new FahrenheitToCelsiusBuiltin( t1, t2 );
}
