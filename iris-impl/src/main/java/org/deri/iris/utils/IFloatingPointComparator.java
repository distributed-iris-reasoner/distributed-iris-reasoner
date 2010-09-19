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
package org.deri.iris.utils;

/**
 * A floating-point number comparator.
 * Classes that implement this interface can either perform strict comparison or
 * allow for round-off errors.
 */
public interface IFloatingPointComparator
{
	/**
	 * An error-safe comparison in the java style.
	 * 
	 * @param a A double value
	 * @param b A double value
	 * @return -1 if a is significantly less than b, +1 if a is significantly greater than b, 0 if a and b are close enough.
	 */
	int compare( double a, double b );

	/**
	 * Floating-point error safe comparison.
	 * 
	 * @param a A double value
	 * @param b A double value
	 * @return true If a is less than b.
	 */
	boolean less( double a, double b );

	/**
	 * Floating-point error safe comparison.
	 * 
	 * @param a A double value
	 * @param b A double value
	 * @return true If a is greater than b.
	 */
	boolean greater( double a, double b );

	/**
	 * Floating-point error safe comparison.
	 * 
	 * @param a A double value
	 * @param b A double value
	 * @return true If a is greater than or close enough to be equal to b.
	 */
	boolean greaterOrEquals( double a, double b );

	/**
	 * Floating-point error safe comparison.
	 * 
	 * @param a A double value
	 * @param b A double value
	 * @return true If a is less than or close enough to be equal to b.
	 */
	boolean lessOrEquals( double a, double b );

	/**
	 * Test two double values for equality.
	 * @param a A double value
	 * @param b A double value
	 * @return true If a and b are considered equal.
	 */
	boolean equals( double a, double b );

	/**
	 * Floating-point error safe comparison.
	 * 
	 * @param a
	 * @param b
	 * @return true If a and b are significantly different.
	 */
	boolean notEquals( double a, double b );

	/**
	 * Indicates whether a double value contains an integer or a number very,
	 * very close to an integer.
	 * @param value The value to test
	 * @return true If value holds an integer.
	 */
	boolean isIntValue( double value );
}
