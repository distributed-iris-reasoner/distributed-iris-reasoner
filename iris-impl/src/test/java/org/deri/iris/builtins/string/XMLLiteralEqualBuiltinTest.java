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
package org.deri.iris.builtins.string;

import junit.framework.TestCase;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.factory.Factory;

/**
 */
public class XMLLiteralEqualBuiltinTest extends TestCase {

	private static final ITerm X = Factory.TERM.createVariable("X");

	private static final ITerm Y = Factory.TERM.createVariable("Y");
	
	private static final ITuple EMPTY_TUPLE = Factory.BASIC.createTuple();

	public XMLLiteralEqualBuiltinTest(String name) {
		super(name);
	}


	public void testBuiltin() throws EvaluationException {
		
		ITerm term1 = Factory.CONCRETE.createXMLLiteral("XML Literal");
		ITerm term2 = Factory.CONCRETE.createXMLLiteral("XML Literal");
		ITerm term3 = Factory.CONCRETE.createXMLLiteral("blabla");
		
		ITuple arguments = Factory.BASIC.createTuple(X, Y);

		XMLLiteralEqualBuiltin builtin = new XMLLiteralEqualBuiltin(term1 ,term1);
		ITuple actualTuple = builtin.evaluate(arguments);
		assertEquals(EMPTY_TUPLE, actualTuple);
		

		builtin = new XMLLiteralEqualBuiltin(term1, term2);
		actualTuple = builtin.evaluate(arguments);
		assertEquals(EMPTY_TUPLE, actualTuple);

		builtin = new XMLLiteralEqualBuiltin(term1, term3);
		actualTuple = builtin.evaluate(arguments);
		assertEquals(null, actualTuple);
	}

}
