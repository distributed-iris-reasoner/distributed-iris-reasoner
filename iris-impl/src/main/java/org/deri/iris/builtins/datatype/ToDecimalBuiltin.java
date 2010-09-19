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
import org.deri.iris.api.terms.concrete.IDecimalTerm;
import org.deri.iris.api.terms.concrete.IFloatTerm;

/**
 * Represents a data type conversion function, which converts supported data
 * type instances to Decimal instances. The following data types are supported:
 * <ul>
 * <li>Boolean</li>
 * <li>Double</li>
 * <li>Float</li>
 * <li>Integer</li>
 * <li>String</li>
 * </ul>
 */
public class ToDecimalBuiltin extends ConversionBuiltin {

	private static final IPredicate PREDICATE = BASIC.createPredicate(
			"TO_DECIMAL", 2);

	/**
	 * Creates a new instance of this builtin.
	 * 
	 * @param terms The term representing the data type instance to be
	 *            converted.
	 */
	public ToDecimalBuiltin(ITerm... terms) {
		super(PREDICATE, terms);
	}

	@Override
	protected ITerm convert(ITerm term) {
		if (term instanceof IBooleanTerm) {
			return toDecimal((IBooleanTerm) term);
		} else if (term instanceof INumericTerm) {
			return toDecimal((INumericTerm) term);
		} else if (term instanceof IStringTerm) {
			return toDecimal((IStringTerm) term);
		}

		return null;
	}

	/**
	 * Converts a Boolean term to a Decimal term. A Boolean term representing
	 * the value "True" is converted to a Decimal term representing "1.0". A
	 * Boolean term representing the value "False" is converted to a Decimal
	 * term representing "0.0".
	 * 
	 * @param term The Boolean term to be converted.
	 * @return A new Decimal term representing the result of the conversion.
	 */
	public static IDecimalTerm toDecimal(IBooleanTerm term) {
		if (term.getValue()) {
			return CONCRETE.createDecimal(1.0);
		}

		return CONCRETE.createDecimal(0.0);
	}

	/**
	 * Converts a Float term to a Decimal term.
	 * 
	 * @param term The Float term to be converted.
	 * @return A new Decimal term representing the result of the conversion.
	 */
	public static IDecimalTerm toDecimal(IFloatTerm term) {
		/*
		 * This is a workaround in order to keep precision.
		 */
		String floatString = String.valueOf(term.getValue());

		return CONCRETE.createDecimal(new BigDecimal(floatString));
	}

	/**
	 * Converts a Numeric term to a Decimal term.
	 * 
	 * @param term The Float term to be converted.
	 * @return A new Decimal term representing the result of the conversion.
	 */
	public static IDecimalTerm toDecimal(INumericTerm term) {
		if (term instanceof IFloatTerm) {
			return toDecimal((IFloatTerm) term);
		}

		return CONCRETE.createDecimal(term.getValue());
	}

	/**
	 * Converts a String term to a Decimal term.
	 * 
	 * @param term The String term to be converted.
	 * @return A new Decimal term representing the result of the conversion.
	 */
	public static IDecimalTerm toDecimal(IStringTerm term) {
		try {
			String string = term.getValue();

			return CONCRETE.createDecimal(new BigDecimal(string));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"The specified string does not represent a decimal number",
					e);
		}
	}

}
