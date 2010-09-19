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

import static org.deri.iris.builtins.BuiltinHelper.add;
import static org.deri.iris.builtins.BuiltinHelper.divide;
import static org.deri.iris.builtins.BuiltinHelper.multiply;
import static org.deri.iris.builtins.BuiltinHelper.subtract;
import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.CONCRETE;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.ITerm;

/**
 * <p>
 * A simple fahrenheit to celsius builtin. The first index is the fahrenheit
 * value, the second is the celsius value.
 * </p>
 * <p>
 * $Id: FahrenheitToCelsiusBuiltin.java,v 1.8 2007-10-12 12:52:13 bazbishop237 Exp $
 * </p>
 * @version $Revision: 1.8 $
 * @author Richard Pöttler (richard dot poettler at deri dot org)
 */
public class FahrenheitToCelsiusBuiltin extends ArithmeticBuiltin {

	/** Predicate holding the information about this builtin. */
	private static final IPredicate PREDICATE = BASIC.createPredicate("ftoc", 2);

	/** Term representing an int(5). */
	private static final ITerm t5 = CONCRETE.createInteger(5);

	/** Term representing an int(9). */
	private static final ITerm t9 = CONCRETE.createInteger(9);

	/** Term representing an int(32). */
	private static final ITerm t32 = CONCRETE.createInteger(32);

	/**
	 * Constructs this builtin.
	 * @param t the terms for this builtin. The first index is the
	 * fahrenheit value, the second is the celsius value.
	 */
	public FahrenheitToCelsiusBuiltin(final ITerm... t) {
		super(PREDICATE, t);
	}

	@Override
    protected ITerm computeMissingTerm( int missingTermIndex, ITerm[] terms ) throws EvaluationException
    {
		if( missingTermIndex == 0 ) // fahrenheit are requested
			return add(divide(multiply(terms[ 1 ], t9), t5), t32);
		else // celsius are requested
			return divide(multiply(subtract(terms[ 0 ], t32), t5), t9);
    }

	public IPredicate getBuiltinPredicate() {
		return PREDICATE;
	}
}
