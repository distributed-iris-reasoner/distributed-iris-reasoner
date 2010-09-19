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

import junit.framework.TestCase;

public class StandardFloatingPointComparatorTest extends TestCase
{
	public static final double lower = 1.5;

	public static final double same1 = lower + 0.000000001;

	public static final double same2 = same1 + 0.0000000000001;
	
	public void testCompare()
	{
		for( int scale = -10; scale <= +40; ++scale )
		{
			double power = Math.pow( 10, scale );
			
			double scaledLower = lower * power;
			double scaledSame1 = same1 * power;
			double scaledSame2 = same2 * power;
			
			assertEquals( StandardFloatingPointComparator.getDouble().compare( scaledLower, scaledSame1 ), -1 );
			assertEquals( StandardFloatingPointComparator.getDouble().compare( scaledSame1, scaledLower ), +1 );
	
			assertEquals( StandardFloatingPointComparator.getDouble().compare( scaledSame1, scaledSame2 ), 0 );
			assertEquals( StandardFloatingPointComparator.getDouble().compare( scaledSame2, scaledSame1 ), 0 );
		}
	}

	public void testLess()
	{
		for( int scale = -10; scale <= +40; ++scale )
		{
			double power = Math.pow( 10, scale );
			
			double scaledLower = lower * power;
			double scaledSame1 = same1 * power;
			double scaledSame2 = same2 * power;
			
			assertTrue( StandardFloatingPointComparator.getDouble().less( scaledLower, scaledSame1 ) );
			assertFalse( StandardFloatingPointComparator.getDouble().less( scaledSame1, scaledSame2 ) );
			assertFalse( StandardFloatingPointComparator.getDouble().less( scaledSame2, scaledSame1 ) );
		}
	}

	public void testGreater()
	{
		for( int scale = -10; scale <= +40; ++scale )
		{
			double power = Math.pow( 10, scale );
			
			double scaledLower = lower * power;
			double scaledSame1 = same1 * power;
			double scaledSame2 = same2 * power;
			
			assertTrue( StandardFloatingPointComparator.getDouble().greater( scaledSame1, scaledLower ) );
			assertFalse( StandardFloatingPointComparator.getDouble().greater( scaledSame1, scaledSame2 ) );
			assertFalse( StandardFloatingPointComparator.getDouble().greater( scaledSame2, scaledSame1 ) );
		}
	}

	public void testGreaterOrEquals()
	{
		for( int scale = -10; scale <= +40; ++scale )
		{
			double power = Math.pow( 10, scale );
			
			double scaledLower = lower * power;
			double scaledSame1 = same1 * power;
			double scaledSame2 = same2 * power;
			
			assertTrue( StandardFloatingPointComparator.getDouble().greaterOrEquals( scaledSame1, scaledLower ) );
			assertTrue( StandardFloatingPointComparator.getDouble().greaterOrEquals( scaledSame1, scaledSame2 ) );
			assertTrue( StandardFloatingPointComparator.getDouble().greaterOrEquals( scaledSame2, scaledSame1 ) );
		}
	}

	public void testLessOrEquals()
	{
		for( int scale = -10; scale <= +40; ++scale )
		{
			double power = Math.pow( 10, scale );
			
			double scaledLower = lower * power;
			double scaledSame1 = same1 * power;
			double scaledSame2 = same2 * power;
			
			assertTrue( StandardFloatingPointComparator.getDouble().lessOrEquals( scaledLower, scaledSame1 ) );
			assertTrue( StandardFloatingPointComparator.getDouble().lessOrEquals( scaledSame1, scaledSame2 ) );
			assertTrue( StandardFloatingPointComparator.getDouble().lessOrEquals( scaledSame2, scaledSame1 ) );
		}
	}

	public void testEqualsDoubleDouble()
	{
		for( int scale = -10; scale <= +40; ++scale )
		{
			double power = Math.pow( 10, scale );
			
			double scaledLower = lower * power;
			double scaledSame1 = same1 * power;
			double scaledSame2 = same2 * power;
			
			assertTrue( StandardFloatingPointComparator.getDouble().equals( scaledSame1, scaledSame2 ) );
			assertTrue( StandardFloatingPointComparator.getDouble().equals( scaledSame2, scaledSame1 ) );
			assertFalse( StandardFloatingPointComparator.getDouble().equals( scaledSame1, scaledLower ) );
		}
	}

	public void testNotEquals()
	{
		for( int scale = -10; scale <= +40; ++scale )
		{
			double power = Math.pow( 10, scale );
			
			double scaledLower = lower * power;
			double scaledSame1 = same1 * power;
			double scaledSame2 = same2 * power;
			
			assertFalse( StandardFloatingPointComparator.getDouble().notEquals( scaledSame1, scaledSame2 ) );
			assertFalse( StandardFloatingPointComparator.getDouble().notEquals( scaledSame2, scaledSame1 ) );
			assertTrue( StandardFloatingPointComparator.getDouble().notEquals( scaledSame1, scaledLower ) );
		}
	}

	public void testIsIntValue()
	{
		assertTrue( StandardFloatingPointComparator.getDouble().isIntValue( 5.00000000000001 ) );
		assertFalse( StandardFloatingPointComparator.getDouble().isIntValue( 5.0000000001 ) );

		assertFalse( StandardFloatingPointComparator.getDouble().isIntValue( 0.50000000000001 ) );

		assertTrue( StandardFloatingPointComparator.getDouble().isIntValue( 5000000.00000001 ) );
		assertFalse( StandardFloatingPointComparator.getDouble().isIntValue( 5000000.0001 ) );
	}

	/**
	 * Test the comparison methods with known input and results.
	 */
	public void testComparisons()
	{
		final double small = Math.pow( 2.0, -42 );
		
		// Input: a, b
		double[][] input = new double[][]
		              {
						new double[] { 1, 1 },
						new double[] { 1, 2 },
		                new double[] { 1, 1 - small / 10 },
		                new double[] { 1, 1 + small / 10 },

		                new double[] { 0, 1 },
		                new double[] { 0, 0 },
		                new double[] { 0, -1 },
		                new double[] { 0, small / 100 },
		                new double[] { 2100000001.0001, 2100000001.0003 },
		              };

		// Output: less, equals
		boolean[][] output = new boolean[][]
		              {
						new boolean[] { false, true },
						new boolean[] { true, false },
		                new boolean[] { false, true },
		                new boolean[] { false, true },

		                new boolean[] { true, false },
		                new boolean[] { false, true },
		                new boolean[] { false, false },
		                new boolean[] { false, true },
		                new boolean[] { false, true },
		              };

		for( int i = 0; i < input.length; ++i )
		{
			boolean l = StandardFloatingPointComparator.getDouble().less( input[ i ][ 0 ], input[ i ][ 1 ] );
			boolean e = StandardFloatingPointComparator.getDouble().equals( input[ i ][ 0 ], input[ i ][ 1 ] );

			boolean ne = StandardFloatingPointComparator.getDouble().notEquals( input[ i ][ 0 ], input[ i ][ 1 ] );
			boolean le = StandardFloatingPointComparator.getDouble().lessOrEquals( input[ i ][ 0 ], input[ i ][ 1 ] );
			boolean g = StandardFloatingPointComparator.getDouble().greater( input[ i ][ 0 ], input[ i ][ 1 ] );
			boolean ge = StandardFloatingPointComparator.getDouble().greaterOrEquals( input[ i ][ 0 ], input[ i ][ 1 ] );

			boolean less = output[ i ][ 0 ];
			boolean equals = output[ i ][ 1 ];

			boolean notEquals = !equals;
			boolean lessOrEquals = less || equals;
			boolean greater = !lessOrEquals;
			boolean greaterOrEquals = !less;

			assertFalse( l != less );
			assertFalse( g != greater );
			assertFalse( e != equals );
			assertFalse( ne != notEquals );
			assertFalse( le != lessOrEquals );
			assertFalse( ge != greaterOrEquals );
		}
	}
}
