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
package org.deri.iris.builtins.numeric;

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.CONCRETE;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.INumericTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.builtins.DivideBuiltin;

/**
 * <p>
 * Represents a integer divide operation, i.e. divides the first argument by the
 * second, and returns the integer obtained by truncating the fractional part of
 * the result. At the evaluation time there must only be one variable left for
 * computation, otherwise an exception will be thrown.
 * </p>
 * 
 * @author Adrian Marte
 */
public class NumericIntegerDivideBuiltin extends DivideBuiltin {

	/** The predicate defining this builtin. */
	private static final IPredicate PREDICATE = BASIC.createPredicate(
			"NUMERIC_INTEGER_DIVIDE", 3);

	/**
	 * Constructs a builtin. Three terms must be passed to the constructor,
	 * otherwise an exception will be thrown.
	 * 
	 * @param terms The terms.
	 * @throws NullPointerException If one of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             3.
	 * @throws NullPointerException If <code>t</code> is <code>null</code>.
	 */
	public NumericIntegerDivideBuiltin(ITerm... terms) {
		super(PREDICATE, terms);
	}

	protected ITerm computeMissingTerm(int missingTermIndex, ITerm[] terms)
			throws EvaluationException {
		ITerm result = super.computeMissingTerm(missingTermIndex, terms);

		// Truncate the fractional part of the result.
		if (result != null && result instanceof INumericTerm) {
			BigDecimal value = ((INumericTerm) result).getValue();
			BigInteger truncatedResult = value.toBigInteger();
			result = CONCRETE.createInteger(truncatedResult);
		}

		return result;
	}
}
