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
package org.deri.iris.api.factory;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.builtins.IBuiltinAtom;
import org.deri.iris.api.terms.ITerm;

/**
 * <p>
 * An interface that can be used to create set of built-ins supported by this
 * engine.
 * </p>
 * <p>
 * $Id: IBuiltInsFactory.java,v 1.7 2007-10-12 13:00:42 bazbishop237 Exp $
 * </p>
 * 
 * @author Darko Anicic, DERI Innsbruck
 * @author Richard Pöttler, richard dot poettler at deri dot org
 * @date 17.03.2006 11:55:35
 * @version $Revision: 1.7 $
 */
public interface IBuiltinsFactory {

	/*
	 * Arithmetic built-ins.
	 */

	/**
	 * Creates an add builtin.
	 * 
	 * @param t0 the first summand
	 * @param t1 the second summand
	 * @param t2 the sum
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 */
	public IBuiltinAtom createAddBuiltin(final ITerm t0, final ITerm t1,
			final ITerm t2);

	/**
	 * Creates an add builtin.
	 * 
	 * @param terms The terms, must be three.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if not exactly two three are specified
	 */
	public IBuiltinAtom createAddBuiltin(ITerm... terms);

	/**
	 * Creates a subtract builtin.
	 * 
	 * @param t0 the minuend
	 * @param t1 the subtrahend
	 * @param t2 the difference
	 * @return the constructed builtin
	 * @throws NullPointerException if one of the terms is <code>null</code>
	 */
	public IBuiltinAtom createSubtractBuiltin(final ITerm t0, final ITerm t1,
			final ITerm t2);

	/**
	 * Creates an subtract builtin.
	 * 
	 * @param terms The terms, must be three.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if not exactly three terms are specified
	 */
	public IBuiltinAtom createSubtractBuiltin(ITerm... terms);

	/**
	 * Creates a multiply builtin.
	 * 
	 * @param t0 the first factor
	 * @param t1 the second factor
	 * @param t2 the product
	 * @return the constructed builtin
	 * @throws NullPointerException if one of the terms is <code>null</code>
	 */
	public IBuiltinAtom createMultiplyBuiltin(final ITerm t0, final ITerm t1,
			final ITerm t2);

	/**
	 * Creates an multiply builtin.
	 * 
	 * @param terms The terms, must be three.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if not exactly three terms are specified
	 */
	public IBuiltinAtom createMultiplyBuiltin(ITerm... terms);

	/**
	 * Creates a divide builtin.
	 * 
	 * @param t0 the dividend
	 * @param t1 the diviso
	 * @param t2 the quotient
	 * @return the constructed builtin
	 * @throws NullPointerException if one of the terms is <code>null</code>
	 */
	public IBuiltinAtom createDivideBuiltin(final ITerm t0, final ITerm t1,
			final ITerm t2);

	/**
	 * Creates an divide builtin.
	 * 
	 * @param terms The terms, must be three.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if not exactly three terms are specified
	 */
	public IBuiltinAtom createDivideBuiltin(ITerm... terms);

	/**
	 * Creates a modulus builtin.
	 * 
	 * @param t0 the numerator
	 * @param t1 the denominator
	 * @param t2 the result
	 * @return the constructed builtin
	 * @throws NullPointerException if one of the terms is <code>null</code>
	 */
	public IBuiltinAtom createModulusBuiltin(final ITerm t0, final ITerm t1,
			final ITerm t2);

	/**
	 * Creates an modulus builtin.
	 * 
	 * @param terms The terms, must be three.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if not exactly three terms are specified
	 */
	public IBuiltinAtom createModulusBuiltin(ITerm... terms);

	/**
	 * Creates an equal builtin.
	 * 
	 * @param t0 the first term
	 * @param t1 the second term
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 */
	public IBuiltinAtom createEqual(final ITerm t0, final ITerm t1);

	/**
	 * Creates an equal builtin.
	 * 
	 * @param terms The terms, must be two.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if not exactly two terms are specified
	 */
	public IBuiltinAtom createEqual(ITerm... terms);

	/**
	 * Creates an unequal builtin.
	 * 
	 * @param t0 the first term
	 * @param t1 the second term
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 */
	public IBuiltinAtom createUnequal(final ITerm t0, final ITerm t1);

	/**
	 * Creates an unequal builtin.
	 * 
	 * @param terms The terms, must be two.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if not exactly two terms are specified
	 */
	public IBuiltinAtom createUnequal(ITerm... terms);

	/**
	 * Create an EXACT_EQUAL built-in.
	 * 
	 * @param t0 The first term.
	 * @param t1 The second term.
	 * @return The built-in instance
	 */
	IBuiltinAtom createExactEqual(final ITerm t0, final ITerm t1);

	/**
	 * Creates an EXACT_EQUAL builtin.
	 * 
	 * @param terms The terms, must be two.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if not exactly two terms are specified
	 */
	IBuiltinAtom createExactEqual(ITerm... terms);

	/**
	 * Create a NOT_EXACT_EQUAL built-in.
	 * 
	 * @param t0 The first term.
	 * @param t1 The second term.
	 * @return The built-in instance
	 */

	IBuiltinAtom createNotExactEqual(final ITerm t0, final ITerm t1);

	/**
	 * Creates an NOT_EXACT_EQUAL builtin.
	 * 
	 * @param terms The terms, must be two.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if not exactly two terms are specified
	 */
	IBuiltinAtom createNotExactEqual(ITerm... terms);

	/**
	 * Creates a less builtin.
	 * 
	 * @param t0 the first term
	 * @param t1 the second term
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 */
	public IBuiltinAtom createLess(final ITerm t0, final ITerm t1);

	/**
	 * Creates an less builtin.
	 * 
	 * @param terms The terms, must be two.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if not exactly two terms are specified
	 */
	public IBuiltinAtom createLess(ITerm... terms);

	/**
	 * Creates an less-equal builtin.
	 * 
	 * @param t0 the first term
	 * @param t1 the second term
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 */
	public IBuiltinAtom createLessEqual(final ITerm t0, final ITerm t1);

	/**
	 * Creates an less-equal builtin.
	 * 
	 * @param terms The terms, must be two.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if not exactly two terms are specified
	 */
	public IBuiltinAtom createLessEqual(ITerm... terms);

	/**
	 * Creates a greater builtin.
	 * 
	 * @param t0 the first term
	 * @param t1 the second term
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 */
	public IBuiltinAtom createGreater(final ITerm t0, final ITerm t1);

	/**
	 * Creates an greater builtin.
	 * 
	 * @param terms The terms, must be two.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if not exactly two terms are specified
	 */
	public IBuiltinAtom createGreater(ITerm... terms);

	/**
	 * Creates a greater-equal builtin.
	 * 
	 * @param t0 the first term
	 * @param t1 the second term
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 */
	public IBuiltinAtom createGreaterEqual(final ITerm t0, final ITerm t1);

	/**
	 * Creates an greater-equal builtin.
	 * 
	 * @param terms The terms, must be two.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if not exactly two terms are specified
	 */
	public IBuiltinAtom createGreaterEqual(ITerm... terms);

	/* Numeric built-ins */

	/**
	 * Creates the NumericAdd built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createNumericAdd(ITerm... terms);

	/**
	 * Creates the NumericSubtract built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createNumericSubtract(ITerm... terms);

	/**
	 * Creates the NumericMultiply built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createNumericMultiply(ITerm... terms);

	/**
	 * Creates the NumericDividebuilt-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createNumericDivide(ITerm... terms);

	/**
	 * Creates the NumericIntegerDivide built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>. -
	 *             * @throws IllegalArgumentException If the number of terms
	 *             submitted is not correct.
	 */
	public IBuiltinAtom createNumericIntegerDivide(ITerm... terms);

	/**
	 * Creates NumericModulus builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createNumericModulus(ITerm... terms);

	/**
	 * Creates the NumericEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createNumericEqual(ITerm... terms);

	/**
	 * Creates the NumericNotEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createNumericNotEqual(ITerm... terms);

	/**
	 * Creates the NumericLess built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createNumericLess(ITerm... terms);

	/**
	 * Creates the NumericGreater built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createNumericGreater(ITerm... terms);

	/**
	 * Creates the NumericLessEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createNumericLessEqual(ITerm... terms);

	/**
	 * Creates the NumericGreaterEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createNumericGreaterEqual(ITerm... terms);

	/*
	 * String built-ins.
	 */

	/**
	 * Creates NumericModulus builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createStringCompare(ITerm... terms);

	/**
	 * Creates StringConcat builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createStringConcat(ITerm... terms);

	/**
	 * Creates StringJoin builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createStringJoin(ITerm... terms);

	/**
	 * Creates StringSubstring builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             1
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createStringSubstring(ITerm... terms);

	/**
	 * Creates StringLength builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createStringLength(ITerm... terms);

	/**
	 * Creates StringToUpper builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createStringToUpper(ITerm... terms);

	/**
	 * Creates StringToLower builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createStringToLower(ITerm... terms);

	/**
	 * Creates StringUriEncode builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createStringUriEncode(ITerm... terms);

	/**
	 * Creates StringIriToUri builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createStringIriToUri(ITerm... terms);

	/**
	 * Creates StringEscapeHtmlUri builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createStringEscapeHtmlUri(ITerm... terms);

	/**
	 * Creates StringSubstringBefore builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createStringSubstringBefore(ITerm... terms);

	/**
	 * Creates StringSubstringAfter builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createStringSubstringAfter(ITerm... terms);

	/**
	 * Creates StringReplace builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createStringReplace(ITerm... terms);

	/**
	 * Creates StringContains builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createStringContains(ITerm... terms);

	/**
	 * Creates StringStartsWith builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createStringStartsWith(ITerm... terms);

	/**
	 * Creates StringEndsWith builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createStringEndsWith(ITerm... terms);

	/**
	 * Creates StringMatches builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createStringMatches(ITerm... terms);

	/*
	 * Date built-ins.
	 */

	/**
	 * Creates the YearFromDateTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createYearFromDateTime(ITerm... terms);

	/**
	 * Creates the MonthFromDateTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createMonthFromDateTime(ITerm... terms);

	/**
	 * Creates the DayFromDateTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDayFromDateTime(ITerm... terms);

	/**
	 * Creates the HoursFromDateTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createHoursFromDateTime(ITerm... terms);

	/**
	 * Creates the MinutesFromDateTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createMinutesFromDateTime(ITerm... terms);

	/**
	 * Creates the SecondsFromDateTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createSecondsFromDateTime(ITerm... terms);

	/**
	 * Creates the YearFromDate built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createYearFromDate(ITerm... terms);

	/**
	 * Creates the MonthFromDate built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createMonthFromDate(ITerm... terms);

	/**
	 * Creates the DayFromDate built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDayFromDate(ITerm... terms);

	/**
	 * Creates the HoursFromTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createHoursFromTime(ITerm... terms);

	/**
	 * Creates the MinutesFromTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createMinutesFromTime(ITerm... terms);

	/**
	 * Creates the SecondsFromTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createSecondsFromTime(ITerm... terms);

	/**
	 * Creates the YearsFromDuration built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createYearsFromDuration(ITerm... terms);

	/**
	 * Creates the MonthsFromDuration built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createMonthsFromDuration(ITerm... terms);

	/**
	 * Creates the DaysFromDuration built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDaysFromDuration(ITerm... terms);

	/**
	 * Creates the HoursFromDuration built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createHoursFromDuration(ITerm... terms);

	/**
	 * Creates the MinutesFromDuration built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createMinutesFromDuration(ITerm... terms);

	/**
	 * Creates the SecondsFromDuration built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createSecondsFromDuration(ITerm... terms);

	/**
	 * Creates the TimezoneFromDateTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createTimezoneFromDateTime(ITerm... terms);

	/**
	 * Creates the TimezoneFromDate built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createTimezoneFromDate(ITerm... terms);

	/**
	 * Creates the TimezoneFromTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createTimezoneFromTime(ITerm... terms);

	/**
	 * Creates YearPart builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createYearPart(ITerm... terms);

	/**
	 * Creates MonthPart builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createMonthPart(ITerm... terms);

	/**
	 * Creates DayPart builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createDayPart(ITerm... terms);

	/**
	 * Creates HourPart builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createHourPart(ITerm... terms);

	/**
	 * Creates MinutePart builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createMinutePart(ITerm... terms);

	/**
	 * Creates SecondPart builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createSecondPart(ITerm... terms);

	/**
	 * Creates TimezonePart builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createTimezonePart(ITerm... terms);

	/**
	 * Creates the DateTimeSubtract built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDateTimeSubtract(ITerm... terms);

	/**
	 * Creates the TimeSubtract built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createTimeSubtract(ITerm... terms);

	/**
	 * Creates the YearMonthDurationAdd built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createYearMonthDurationAdd(ITerm... terms);

	/**
	 * Creates the YearMonthDurationSubtract built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createYearMonthDurationSubtract(ITerm... terms);

	/**
	 * Creates the YearMonthDurationMultiply built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createYearMonthDurationMultiply(ITerm... terms);

	/**
	 * Creates the YearMonthDurationDivide built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createYearMonthDurationDivide(ITerm... terms);

	/**
	 * Creates the YearMonthDurationDivideByYearMonthduration built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createYearMonthDurationDivideByYearMonthDuration(
			ITerm... terms);

	/**
	 * Creates the DayTimeDurationAdd built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDayTimeDurationAdd(ITerm... terms);

	/**
	 * Creates the DayTimeDurationSubtract built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDayTimeDurationSubtract(ITerm... terms);

	/**
	 * Creates the DayTimeDurationMultiply built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDayTimeDurationMultiply(ITerm... terms);

	/**
	 * Creates the DayTimeDurationDivide built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDayTimeDurationDivide(ITerm... terms);

	/**
	 * Creates the DayTimeDurationDivideByDayTimeDuration built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDayTimeDurationDivideByDayTimeDuration(
			ITerm... terms);
	
	/**
	 * Creates the AddYearMonthDurations built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createAddYearMonthDurations(ITerm... terms);

	/**
	 * Creates the AddYearMonthDurationToDateTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createAddYearMonthDurationToDateTime(ITerm... terms);

	/**
	 * Creates the AddYearMonthDurationToDate built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createAddYearMonthDurationToDate(ITerm... terms);

	/**
	 * Creates the AddDayTimeDurations built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createAddDayTimeDurations(ITerm... terms);
	
	/**
	 * Creates the AddDayTimeDurationToDateTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createAddDayTimeDurationToDateTime(ITerm... terms);

	/**
	 * Creates the AddDayTimeDurationToDate built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createAddDayTimeDurationToDate(ITerm... terms);

	/**
	 * Creates the AddDayTimeDurationToTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createAddDayTimeDurationToTime(ITerm... terms);

	/**
	 * Creates the SubtractYearMonthDurationFromDateTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createSubtractYearMonthDurationFromDateTime(
			ITerm... terms);

	/**
	 * Creates the SubtractYearMonthDurationToDate built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createSubtractYearMonthDurationFromDate(ITerm... terms);

	/**
	 * Creates the SubtractDayTimeDurationToDateTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createSubtractDayTimeDurationFromDateTime(
			ITerm... terms);

	/**
	 * Creates the SubtractDayTimeDurationToDate built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createSubtractDayTimeDurationFromDate(ITerm... terms);

	/**
	 * Creates the SubtractDayTimeDurationFromTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createSubtractDayTimeDurationFromTime(ITerm... terms);

	/**
	 * Creates the DateTimeEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDateTimeEqual(ITerm... terms);

	/**
	 * Creates the DateTimeLess built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDateTimeLess(ITerm... terms);

	/**
	 * Creates the DateTimeGreater built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDateTimeGreater(ITerm... terms);

	/**
	 * Creates the DateEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDateEqual(ITerm... terms);

	/**
	 * Creates the DateLess built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDateLess(ITerm... terms);

	/**
	 * Creates the DateGreater built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDateGreater(ITerm... terms);

	/**
	 * Creates the TimeEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createTimeEqual(ITerm... terms);

	/**
	 * Creates the TimeLess built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createTimeLess(ITerm... terms);

	/**
	 * Creates the TimeGreater built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createTimeGreater(ITerm... terms);

	/**
	 * Creates the DurationEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDurationEqual(ITerm... terms);

	/**
	 * Creates the DurationNotEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDurationNotEqual(ITerm... terms);

	/**
	 * Creates the DayTimeDurationLess built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDayTimeDurationLess(ITerm... terms);

	/**
	 * Creates the DayTimeDurationLess built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDayTimeDurationGreater(ITerm... terms);

	/**
	 * Creates the YearMonthDurationLess built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createYearMonthDurationLess(ITerm... terms);

	/**
	 * Creates the YearMonthDurationGreater built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createYearMonthDurationGreater(ITerm... terms);

	/**
	 * Creates the DateTimeNotEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDateTimeNotEqual(ITerm... terms);

	/**
	 * Creates the DateTimeLessEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDateTimeLessEqual(ITerm... terms);

	/**
	 * Creates the DateTimeGreaterEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDateTimeGreaterEqual(ITerm... terms);

	/**
	 * Creates the DateNotEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDateNotEqual(ITerm... terms);

	/**
	 * Creates the DateSubtract built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDateSubtract(ITerm... terms);

	/**
	 * Creates the DateLessEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDateLessEqual(ITerm... terms);

	/**
	 * Creates the DateGreaterEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDateGreaterEqual(ITerm... terms);

	/**
	 * Creates the TimeNotEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createTimeNotEqual(ITerm... terms);

	/**
	 * Creates the TimeLessEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createTimeLessEqual(ITerm... terms);

	/**
	 * Creates the TimeGreaterEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createTimeGreaterEqual(ITerm... terms);

	/**
	 * Creates the DayTimeDurationLessEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDayTimeDurationLessEqual(ITerm... terms);

	/**
	 * Creates the DayTimeDurationGreaterEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createDayTimeDurationGreaterEqual(ITerm... terms);

	/**
	 * Creates the YearMonthDurationLessEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createYearMonthDurationLessEqual(ITerm... terms);

	/**
	 * Creates the YearMonthDurationGreaterEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createYearMonthDurationGreaterEqual(ITerm... terms);

	/*
	 * PlaintLiteral built-ins.
	 */

	/**
	 * Creates TextFromStringLang builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createTextFromStringLang(ITerm... terms);

	/**
	 * Creates TextFromString builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createTextFromString(ITerm... terms);

	/**
	 * Creates StringFromText builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createStringFromText(ITerm... terms);

	/**
	 * Creates LangFromText builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createLangFromText(ITerm... terms);

	/**
	 * Creates TextLenght builtin.
	 * 
	 * @param terms The terms
	 * @return The builtin
	 * @throws NullPointerException Iif any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createTextLength(ITerm... terms);

	/**
	 * Creates TextCompare builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createTextCompare(ITerm... terms);

	/* XMLLiteral built-ins. */

	/**
	 * Creates the XMLLiteralEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createXMLLiteralEqual(ITerm... terms);

	/**
	 * Creates the XMLLiteralNotEqual built-in.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createXMLLiteralNotEqual(ITerm... terms);

	/*
	 * Data type conversion built-ins.
	 */

	/**
	 * Creates ToBase64Binary builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToBase64Binary(ITerm... terms);

	/**
	 * Creates ToBoolean builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToBoolean(ITerm... terms);

	/**
	 * Creates ToDate builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToDate(ITerm... terms);

	/**
	 * Creates ToDateTime builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToDateTime(ITerm... terms);

	/**
	 * Creates ToDayTimeDuration builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToDayTimeDuration(ITerm... terms);

	/**
	 * Creates ToDecimal builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToDecimal(ITerm... terms);

	/**
	 * Creates ToDouble builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToDouble(ITerm... terms);

	/**
	 * Creates ToDuration builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToDuration(ITerm... terms);

	/**
	 * Creates ToFloat builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToFloat(ITerm... terms);

	/**
	 * Creates ToGDay builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToGDay(ITerm... terms);

	/**
	 * Creates ToGMonth builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToGMonth(ITerm... terms);

	/**
	 * Creates ToGMonthDay builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToGMonthDay(ITerm... terms);

	/**
	 * Creates ToGYear builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToGYear(ITerm... terms);

	/**
	 * Creates ToGYearMonth builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToGYearMonth(ITerm... terms);

	/**
	 * Creates ToHexBinary builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToHexBinary(ITerm... terms);

	/**
	 * Creates ToInteger builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToInteger(ITerm... terms);

	/**
	 * Creates ToIRI builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToIRI(ITerm... terms);

	/**
	 * Creates ToString builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToString(ITerm... terms);

	/**
	 * Creates ToText builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToText(ITerm... terms);

	/**
	 * Creates ToTime builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToTime(ITerm... terms);

	/**
	 * Creates ToXMLLiteral builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToXMLLiteral(ITerm... terms);

	/**
	 * Creates ToYearMonthDuration builtin.
	 * 
	 * @param terms The terms.
	 * @return the builtin
	 * @throws NullPointerException if any of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             correct
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IBuiltinAtom createToYearMonthDuration(ITerm... terms);

	/*
	 * Data type guard built-ins.
	 */

	/**
	 * Creates a IsDatatype built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public abstract IBuiltinAtom createIsDatatype(ITerm... terms);

	/**
	 * Creates a IsNotDatatype built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public abstract IBuiltinAtom createIsNotDatatype(ITerm... terms);

	/**
	 * Creates the IsBase64Binary buil-tin.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsBase64Binary(ITerm... terms);

	/**
	 * Creates the IsBoolean built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsBoolean(ITerm... terms);

	/**
	 * Creates the IsDate built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsDate(ITerm... terms);

	/**
	 * Creates the IsDateTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsDateTime(ITerm... terms);

	/**
	 * Creates the IsDateTimeStamp built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsDateTimeStamp(ITerm... terms);

	/**
	 * Creates the IsDayTimeDuration built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsDayTimeDuration(ITerm... terms);

	/**
	 * Creates the IsDecimal built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsDecimal(ITerm... terms);

	/**
	 * Creates the IsDouble built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsDouble(ITerm... terms);

	/**
	 * Creates the IsDuration built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsDuration(ITerm... terms);

	/**
	 * Creates the IsFloat built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsFloat(ITerm... terms);

	/**
	 * Creates the IsGDay built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsGDay(ITerm... terms);

	/**
	 * Creates the IsGMonth built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsGMonth(ITerm... terms);

	/**
	 * Creates the IsGMonthDay built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsGMonthDay(ITerm... terms);

	/**
	 * Creates the IsGYear built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsGYear(ITerm... terms);

	/**
	 * Creates the IsGYearMonth built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsGYearMonth(ITerm... terms);

	/**
	 * Creates the IsHexBinary built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsHexBinary(ITerm... terms);

	/**
	 * Creates the IsInteger built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsInteger(ITerm... terms);

	/**
	 * Creates the IsIRI built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsIRI(ITerm... terms);

	/**
	 * Creates the IsString built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsString(ITerm... terms);

	/**
	 * Creates the IsPlainLiteral built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsPlainLiteral(ITerm... terms);

	/**
	 * Creates the IsTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsTime(ITerm... terms);

	/**
	 * Creates the IsXMLLiteral built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsXMLLiteral(ITerm... terms);

	/**
	 * Creates the IsYearMonthDuration built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsYearMonthDuration(ITerm... terms);

	/**
	 * Creates the IsAnyURI built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsAnyURI(ITerm... terms);

	/**
	 * Creates the IsLong built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsLong(ITerm... terms);

	/**
	 * Creates the IsShort built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsShort(ITerm... terms);

	/**
	 * Creates the IsByte built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsByte(ITerm... terms);

	/**
	 * Creates the IsNonNegativeInteger built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNonNegativeInteger(ITerm... terms);

	/**
	 * Creates the IsPositiveInteger built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsPositiveInteger(ITerm... terms);

	/**
	 * Creates the IsUnsignedLong built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsUnsignedLong(ITerm... terms);

	/**
	 * Creates the IsUnsignedInt built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsUnsignedInt(ITerm... terms);

	/**
	 * Creates the IsUnsignedShort built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsUnsignedShort(ITerm... terms);

	/**
	 * Creates the IsUnsignedByte built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsUnsignedByte(ITerm... terms);

	/**
	 * Creates the IsNonPositiveInteger built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNonPositiveInteger(ITerm... terms);

	/**
	 * Creates the IsNegativeInteger built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNegativeInteger(ITerm... terms);

	/**
	 * Creates the IsNormalizedString built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNormalizedString(ITerm... terms);

	/**
	 * Creates the IsToken built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsToken(ITerm... terms);

	/**
	 * Creates the IsLanguage built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsLanguage(ITerm... terms);

	/**
	 * Creates the IsName built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsName(ITerm... terms);

	/**
	 * Creates the IsNCName built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNCName(ITerm... terms);

	/**
	 * Creates the IsNMTOKEN built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNMTOKEN(ITerm... terms);

	/**
	 * Creates the IsInt built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsInt(ITerm... terms);

	/*
	 * Negative data type guard built-ins.
	 */

	/**
	 * Creates the IsNotBase64Binary buil-tin.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotBase64Binary(ITerm... terms);

	/**
	 * Creates the IsNotBoolean built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotBoolean(ITerm... terms);

	/**
	 * Creates the IsNotDate built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotDate(ITerm... terms);

	/**
	 * Creates the IsNotDateTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotDateTime(ITerm... terms);

	/**
	 * Creates the IsNotDateTimeStamp built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotDateTimeStamp(ITerm... terms);

	/**
	 * Creates the IsNotDayTimeDuration built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotDayTimeDuration(ITerm... terms);

	/**
	 * Creates the IsNotDecimal built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotDecimal(ITerm... terms);

	/**
	 * Creates the IsNotDouble built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotDouble(ITerm... terms);

	/**
	 * Creates the IsNotDuration built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotDuration(ITerm... terms);

	/**
	 * Creates the IsNotFloat built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotFloat(ITerm... terms);

	/**
	 * Creates the IsNotGDay built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotGDay(ITerm... terms);

	/**
	 * Creates the IsNotGMonth built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotGMonth(ITerm... terms);

	/**
	 * Creates the IsNotGMonthDay built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotGMonthDay(ITerm... terms);

	/**
	 * Creates the IsNotGYear built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotGYear(ITerm... terms);

	/**
	 * Creates the IsNotGYearMonth built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotGYearMonth(ITerm... terms);

	/**
	 * Creates the IsNotHexBinary built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotHexBinary(ITerm... terms);

	/**
	 * Creates the IsNotInteger built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotInteger(ITerm... terms);

	/**
	 * Creates the IsNotIRI built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotIRI(ITerm... terms);

	/**
	 * Creates the IsNotString built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotString(ITerm... terms);

	/**
	 * Creates the IsPlainLiteral built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotPlainLiteral(ITerm... terms);

	/**
	 * Creates the IsNotTime built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotTime(ITerm... terms);

	/**
	 * Creates the IsNotXMLLiteral built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotXMLLiteral(ITerm... terms);

	/**
	 * Creates the IsNotYearMonthDuration built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotYearMonthDuration(ITerm... terms);

	/**
	 * Creates the IsNotAnyURI built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotAnyURI(ITerm... terms);

	/**
	 * Creates the IsNotLong built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotLong(ITerm... terms);

	/**
	 * Creates the IsNotShort built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotShort(ITerm... terms);

	/**
	 * Creates the IsNotByte built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotByte(ITerm... terms);

	/**
	 * Creates the IsNotNonNegativeInteger built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotNonNegativeInteger(ITerm... terms);

	/**
	 * Creates the IsNotPositiveInteger built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotPositiveInteger(ITerm... terms);

	/**
	 * Creates the IsNotUnsignedLong built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotUnsignedLong(ITerm... terms);

	/**
	 * Creates the IsNotUnsignedInt built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotUnsignedInt(ITerm... terms);

	/**
	 * Creates the IsNotUnsignedShort built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotUnsignedShort(ITerm... terms);

	/**
	 * Creates the IsNotUnsignedByte built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotUnsignedByte(ITerm... terms);

	/**
	 * Creates the IsNotNonPositiveInteger built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotNonPositiveInteger(ITerm... terms);

	/**
	 * Creates the IsNotNegativeInteger built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotNegativeInteger(ITerm... terms);

	/**
	 * Creates the IsNotNormalizedString built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotNormalizedString(ITerm... terms);

	/**
	 * Creates the IsNotToken built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotToken(ITerm... terms);

	/**
	 * Creates the IsNotLanguage built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotLanguage(ITerm... terms);

	/**
	 * Creates the IsNotName built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotName(ITerm... terms);

	/**
	 * Creates the IsNotNCName built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotNCName(ITerm... terms);

	/**
	 * Creates the IsNotNMTOKEN built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotNMTOKEN(ITerm... terms);

	/**
	 * Creates the IsNotInt built-in.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIsNotInt(ITerm... terms);

	/*
	 * Built-ins representing boolean constant values.
	 */

	/**
	 * Creates the builtin representing true.
	 * 
	 * @return The builtin representing true.
	 */
	public IAtom createTrue();

	/**
	 * Creates the builtin representing false.
	 * 
	 * @return The builtin representing false.
	 */
	public IAtom createFalse();

	/*
	 * Boolean built-ins.
	 */

	/**
	 * Creates the built-in for checking if a boolean term has the value
	 * <code>false</code>.
	 * 
	 * @param terms The terms.
	 * @return The builtin.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createBooleanNot(ITerm... terms);

	/**
	 * Creates the built-in for checking if two boolean terms have the same
	 * value.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createBooleanEqual(ITerm... terms);

	/**
	 * Creates the built-in for checking if a boolean terms is less than another
	 * term.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createBooleanLess(ITerm... terms);

	/**
	 * Creates the built-in for checking if a boolean terms is greater than
	 * another term.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createBooleanGreater(ITerm... terms);

	/**
	 * Creates the built-in for checking if a given IRI equals a given string.
	 * 
	 * @param terms The terms.
	 * @return The built-in.
	 * @throws NullPointerException If any of the terms is <code>null</code>.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             correct.
	 */
	public IBuiltinAtom createIriString(ITerm... terms);

}
