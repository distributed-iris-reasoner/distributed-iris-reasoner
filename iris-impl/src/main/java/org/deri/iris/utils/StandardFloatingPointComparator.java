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
 * Utility class for floating point comparison.
 * This class allows for round-off errors by using only the specified most significant bits
 * of the operands, after allowing for scale.
 */
public class StandardFloatingPointComparator implements IFloatingPointComparator
{
	/**
	 * Singleton-like method to get the standard setup for dealing with double type.
	 * @return The standard double-configured FloatingPoint object.
	 */
	public static IFloatingPointComparator getDouble()
	{
		return mStandardDoubleInstance;
	}
	
	/**
	 * Singleton-like method to get the standard setup for dealing with float type.
	 * @return The standard float-configured FloatingPoint object.
	 */
	public static IFloatingPointComparator getFloat()
	{
		return mStandardFloatInstance;
	}
	
	/**
	 * Constructor.
	 * @param numberOfSignificantBits The number of significant bits in the significand to use when
	 * comparing values. If a non-standard setup is used then it is up to the user to set this
	 * value correctly.
	 */
	public StandardFloatingPointComparator( int numberOfSignificantBits )
	{
		MAX_DIFFERENCE_FROM_ONE = Math.pow( 2.0, -numberOfSignificantBits );
	}
	
	public int compare( double a, double b )
	{
		if( equals( a, b ) )
			return 0;

		return a < b ? -1 : +1;
	}
	
	public boolean less( double a, double b )
	{
		return(a < b && notEquals( a, b ));
	}

	public boolean greater( double a, double b )
	{
		return less( b, a );
	}

	public boolean greaterOrEquals( double a, double b )
	{
		return !less( a, b );
	}

	public boolean lessOrEquals( double a, double b )
	{
		return !greater( a, b );
	}

	/**
	 * Test two double values for equality. The two values are determined to be
	 * equal if they are bit-wise identical or their values lie very close
	 * together. In order to discover whether any difference between the two
	 * numbers is significant, a maximum allowed difference is calculated.
	 * @param a A double value
	 * @param b A double value
	 * @return true If a and b are exactly the same or close enough to being equal.
	 */
	public boolean equals( double a, double b )
	{
		// Optimisation!
		if( a == b )
			return true;
		
		double diff = Math.abs( a - b );

		if( a < 0 )
			a = -a;

		if( b < 0 )
			b = -b;
		
		// Need some bottom limit, otherwise it's impossible to properly compare anything to zero.
		// When the magnitude of both numbers is below this then treat both as zero.
		if( a < MAX_DIFFERENCE_FROM_ONE && b < MAX_DIFFERENCE_FROM_ONE )
			return true;

		double maxAllowableDifference = (MAX_DIFFERENCE_FROM_ONE * (a + b) ) / 2;

		return diff <= maxAllowableDifference;
	}

	public boolean notEquals( double a, double b )
	{
		return !equals( a, b );
	}

	public boolean isIntValue( double value )
	{
		return equals( Math.rint( value ), value );
	}

	/**
	 * This number is used to find the maximum difference between two numbers
	 * that can be considered logically the same. Any difference greater than
	 * this is considered significant and must therefore indicate different
	 * numbers.
	 * 
	 * This number must be scaled as it indicates the maximum allowed difference
	 * from 1.0
	 */
	public final double MAX_DIFFERENCE_FROM_ONE;
	
	/** ieee754 (double) uses 52 bits to represent the significand. */
	public static final int BINARY_DIGITS_OF_PRECISION_DOUBLE = 52;

	/** ieee754 (float) uses 23 bits to represent the significand. */
	public static final int BINARY_DIGITS_OF_PRECISION_FLOAT = 23;

	/** The standard number of least significant bits to ignore for a double type. */
	public static final int LEAST_SIGNIFICANT_BINARY_DIGITS_TO_IGNORE_DOUBLE = 10;

	/** The standard number of least significant bits to ignore for a float type. */
	public static final int LEAST_SIGNIFICANT_BINARY_DIGITS_TO_IGNORE_FLOAT = 4;

	/** The standard comparator for double type. */
	public static final IFloatingPointComparator mStandardDoubleInstance =
		new StandardFloatingPointComparator( BINARY_DIGITS_OF_PRECISION_DOUBLE - LEAST_SIGNIFICANT_BINARY_DIGITS_TO_IGNORE_DOUBLE );

	/** The standard comparator for float types. */
	public static final IFloatingPointComparator mStandardFloatInstance =
		new StandardFloatingPointComparator( BINARY_DIGITS_OF_PRECISION_FLOAT - LEAST_SIGNIFICANT_BINARY_DIGITS_TO_IGNORE_FLOAT );
}
