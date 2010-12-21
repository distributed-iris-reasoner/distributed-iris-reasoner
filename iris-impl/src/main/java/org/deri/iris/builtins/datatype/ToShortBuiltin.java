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
import org.deri.iris.api.terms.concrete.IShortTerm;

/**
 * Represents a data type conversion function, which converts supported data
 * type instances to {@link IShortTerm} instances. The following data types are
 * supported:
 * <ul>
 * <li>Boolean</li>
 * <li>Numeric</li>
 * <li>String</li>
 * </ul>
 */
public class ToShortBuiltin extends ConversionBuiltin {

	private static final IPredicate PREDICATE = BASIC.createPredicate(
			"TO_SHORT", 2);

	/**
	 * Creates a new instance of this builtin.
	 * 
	 * @param terms An array of terms, where first one is the term to convert
	 *            and the last term represents the result of this data type
	 *            conversion.
	 * @throws NullPointerException If <code>terms</code> is <code>null</code>.
	 * @throws NullPointerException If the terms contain a <code>null</code>
	 *             value.
	 * @throws IllegalArgumentException If the length of the terms and the arity
	 *             of the predicate do not match.
	 */
	public ToShortBuiltin(ITerm... terms) {
		super(PREDICATE, terms);
	}

	@Override
	protected IShortTerm convert(ITerm term) {
		if (term instanceof IShortTerm) {
			return (IShortTerm) term;
		} else if (term instanceof IBooleanTerm) {
			return toShort((IBooleanTerm) term);
		} else if (term instanceof INumericTerm) {
			return toShort((INumericTerm) term);
		} else if (term instanceof IStringTerm) {
			return toShort((IStringTerm) term);
		}

		return null;
	}

	/**
	 * Converts a {@link IBooleanTerm} term to a {@link IShortTerm} term. A
	 * {@link IBooleanTerm} term representing the value "True" is converted to a
	 * {@link IShortTerm} term representing "1". A {@link IBooleanTerm} term
	 * representing the value "False" is converted to a {@link IShortTerm} term
	 * representing "0".
	 * 
	 * @param term The {@link IBooleanTerm} term to be converted.
	 * @return A new {@link IShortTerm} term representing the result of the
	 *         conversion.
	 */
	public static IShortTerm toShort(IBooleanTerm term) {
		if (term.getValue()) {
			return CONCRETE.createShort((short) 1);
		}

		return CONCRETE.createShort((short) 0);
	}

	/**
	 * Converts a {@link INumericTerm} term to a {@link IShortTerm} term.
	 * 
	 * @param term The {@link INumericTerm} term to be converted.
	 * @return A new {@link IShortTerm} term representing the result of the
	 *         conversion.
	 */
	public static IShortTerm toShort(INumericTerm term) {
		return CONCRETE
				.createShort(term.getValue().toBigInteger().shortValue());
	}

	/**
	 * Converts a {@link IStringTerm} term to a {@link IShortTerm} term.
	 * 
	 * @param term The {@link IStringTerm} term to be converted.
	 * @return A new {@link IShortTerm} term representing the result of the
	 *         conversion, or <code>null</code> if the conversion fails.
	 */
	public static IShortTerm toShort(IStringTerm term) {
		try {
			String string = term.getValue();

			int indexOfDot = string.indexOf(".");
			if (indexOfDot > -1) {
				string = string.substring(0, indexOfDot);
			}

			return CONCRETE.createShort(Short.valueOf(string));
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
