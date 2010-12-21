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

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.CONCRETE;

import java.math.BigDecimal;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.INumericTerm;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IBooleanTerm;

/**
 * Represents a data type conversion function, which converts supported data
 * type instances to Boolean instances. The following data types are supported:
 * <ul>
 * <li>Float</li>
 * <li>Double</li>
 * <li>Decimal</li>
 * <li>Integer</li>
 * <li>String</li>
 * </ul>
 */
public class ToBooleanBuiltin extends ConversionBuiltin {

	private static final IPredicate PREDICATE = BASIC.createPredicate(
			"TO_BOOLEAN", 2);

	/**
	 * Creates a new instance of this builtin.
	 * 
	 * @param terms The term representing the data type instance to be
	 *            converted.
	 */
	public ToBooleanBuiltin(ITerm... terms) {
		super(PREDICATE, terms);
	}

	@Override
	protected ITerm convert(ITerm term) {
		if (term instanceof IBooleanTerm) {
			return term;
		} else if (term instanceof IStringTerm) {
			return toBoolean((IStringTerm) term);
		} else if (term instanceof INumericTerm) {
			return toBoolean((INumericTerm) term);
		}

		return null;
	}

	/**
	 * Converts a String term to a Boolean term.
	 * 
	 * @param term The String term to be converted.
	 * @return A new Boolean term representing the result of the conversion.
	 */
	public static IBooleanTerm toBoolean(IStringTerm term) {
		String value = term.getValue();
		boolean result = Boolean.parseBoolean(value);

		return CONCRETE.createBoolean(result);
	}

	/**
	 * Converts a Numeric term to a Boolean term. The Boolean term represents
	 * <code>true</code>, if the numeric term represents any number not equal to
	 * 0.0, false<code>false</code> otherwise.
	 * 
	 * @param term The Numeric term to be converted.
	 * @return A new Boolean term representing the result of the conversion.
	 */
	public static IBooleanTerm toBoolean(INumericTerm term) {
		BigDecimal value = term.getValue();

		boolean result = value.compareTo(BigDecimal.ZERO) != 0;

		return CONCRETE.createBoolean(result);
	}

}
