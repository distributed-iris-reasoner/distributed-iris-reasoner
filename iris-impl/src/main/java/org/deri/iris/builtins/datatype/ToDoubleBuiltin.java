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

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.INumericTerm;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IBooleanTerm;
import org.deri.iris.api.terms.concrete.IDoubleTerm;
import org.deri.iris.api.terms.concrete.IFloatTerm;

/**
 * Represents a data type conversion function, which converts supported data
 * type instances to Double instances. The following data types are supported:
 * <ul>
 * <li>Float</li>
 * <li>Decimal</li>
 * <li>Integer</li>
 * <li>Boolean</li>
 * <li>String</li>
 * </ul>
 */
public class ToDoubleBuiltin extends ConversionBuiltin {

	private static final IPredicate PREDICATE = BASIC.createPredicate(
			"TO_DOUBLE", 2);

	/**
	 * Creates a new instance of this builtin.
	 * 
	 * @param terms The term representing the data type instance to be
	 *            converted.
	 */
	public ToDoubleBuiltin(ITerm... terms) {
		super(PREDICATE, terms);
	}

	@Override
	protected ITerm convert(ITerm term) {
		if (term instanceof IBooleanTerm) {
			return toDouble((IBooleanTerm) term);
		} else if (term instanceof INumericTerm) {
			return toDouble((INumericTerm) term);
		} else if (term instanceof IStringTerm) {
			return toDouble((IStringTerm) term);
		}

		return null;
	}

	/**
	 * Converts a Boolean term to a Double term. A Boolean term representing the
	 * value "True" is converted to a Double term representing "1.0". A Boolean
	 * term representing the value "False" is converted to a Double term
	 * representing "0.0".
	 * 
	 * @param term The Boolean term to be converted.
	 * @return A new Double term representing the result of the conversion.
	 */
	public static IDoubleTerm toDouble(IBooleanTerm term) {
		if (term.getValue()) {
			return CONCRETE.createDouble(1.0);
		}

		return CONCRETE.createDouble(0.0);
	}

	/**
	 * Converts a Float term to a Double term.
	 * 
	 * @param term The Float term to be converted.
	 * @return A new Double term representing the result of the conversion.
	 */
	public static IDoubleTerm toDouble(IFloatTerm term) {
		/*
		 * This is a workaround for casting from float to double in order to
		 * keep precision.
		 */
		String floatString = String.valueOf(term.getValue());
		double value = Double.parseDouble(floatString);

		return CONCRETE.createDouble(value);
	}

	/**
	 * Converts a Numeric term to a Double term.
	 * 
	 * @param term The Numeric term to be converted.
	 * @return A new Double term representing the result of the conversion.
	 */
	public static IDoubleTerm toDouble(INumericTerm term) {
		if (term instanceof IDoubleTerm) {
			return (IDoubleTerm) term;
		} else if (term instanceof IFloatTerm) {
			return toDouble((IFloatTerm) term);
		}

		Number number = term.getValue();
		double value = number.doubleValue();

		return CONCRETE.createDouble(value);
	}

	/**
	 * Converts a String term to a Double term.
	 * 
	 * @param term The String term to be converted.
	 * @return A new Double term representing the result of the conversion.
	 */
	public static IDoubleTerm toDouble(IStringTerm term) {
		try {
			String string = term.getValue();
			double value = Double.parseDouble(string);

			return CONCRETE.createDouble(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"The specified term does not represent a double value", e);
		}
	}

}
