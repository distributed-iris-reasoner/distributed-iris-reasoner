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
package org.deri.iris.builtins.datatype;

import static org.deri.iris.factory.Factory.CONCRETE;

import java.lang.reflect.InvocationTargetException;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.terms.ITerm;

public class IsByteBuiltinTest extends AbstractBooleanBuiltinTest {
	
	public IsByteBuiltinTest(String name) {
		super(name);
	}
	
	public void testByte() throws SecurityException, IllegalArgumentException,
			EvaluationException, ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {

		String iri = "http://www.w3.org/2001/XMLSchema#byte";
		String builtinName = IsByteBuiltin.class.getName();
		
		ITerm term = CONCRETE.createByte((byte)1);
		
		// is also numeric
		checkBuiltin(iri, term, builtinName,
				IsDecimalBuiltin.class.getName(),
				IsLongBuiltin.class.getName(),
				IsByteBuiltin.class.getName(),
				IsIntBuiltin.class.getName(),
				IsIntegerBuiltin.class.getName(),
				IsNumericBuiltin.class.getName(),
				IsShortBuiltin.class.getName());
	}

}
