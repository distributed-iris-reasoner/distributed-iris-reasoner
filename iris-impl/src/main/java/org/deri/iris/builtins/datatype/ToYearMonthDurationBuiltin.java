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
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IDayTimeDuration;
import org.deri.iris.api.terms.concrete.IDuration;
import org.deri.iris.api.terms.concrete.IYearMonthDuration;

/**
 * Represents a data type conversion function, which converts supported data
 * type instances to YearMonthDuration instances. The following data types are
 * supported:
 * <ul>
 * <li>Duration</li>
 * <li>DayTimeDuration</li>
 * </ul>
 */
public class ToYearMonthDurationBuiltin extends ConversionBuiltin {

	private static final IPredicate PREDICATE = BASIC.createPredicate(
			"TO_YEARMONTHDURATION", 2);

	/**
	 * Creates a new instance of this builtin.
	 * 
	 * @param terms The term representing the data type instance to be
	 *            converted.
	 */
	public ToYearMonthDurationBuiltin(ITerm... terms) {
		super(PREDICATE, terms);
	}

	@Override
	protected ITerm convert(ITerm term) {
		if (term instanceof IYearMonthDuration) {
			return term;
		} else if (term instanceof IDuration) {
			return toYearMonthDuration((IDuration) term);
		} else if (term instanceof IDayTimeDuration) {
			return toYearMonthDuration((IDayTimeDuration) term);
		}

		return null;
	}

	/**
	 * Converts a Duration term to a YearMonthDuration term.
	 * 
	 * @param term The Duration term to be converted.
	 * @return A new YearMonthDuration term representing the result of the
	 *         conversion.
	 */
	public static IYearMonthDuration toYearMonthDuration(IDuration term) {
		return CONCRETE.createYearMonthDuration(term.isPositive(), term
				.getYear(), term.getMonth());
	}

	/**
	 * Converts a DayTimeDuration term to a YearMonthDuration term.
	 * 
	 * @param term The DayTimeDuration term to be converted.
	 * @return A new YearMonthDuration term representing the result of the
	 *         conversion.
	 */
	public static IYearMonthDuration toYearMonthDuration(IDayTimeDuration term) {
		return CONCRETE.createYearMonthDuration(term.isPositive(), 0, 0);
	}

}
