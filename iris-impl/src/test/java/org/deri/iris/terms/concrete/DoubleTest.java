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
package org.deri.iris.terms.concrete;

import junit.framework.TestCase;

public class DoubleTest extends TestCase {

	public void testConstruct() {
		DoubleTerm dt = new DoubleTerm(0.123);
		assertEquals( 0.123, dt.getValue().doubleValue() );
	}
	
	public void testEquals() {
		checkEqual( +0.0, +0.0 );
		checkEqual( -0.0, -0.0 );
		checkEqual( 1.1, 1.1 );
		checkEqual( -1.1, -1.1 );
		checkEqual( Double.NaN, Double.NaN );
		checkEqual( Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY );
		checkEqual( Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY );
		checkEqual( Double.MAX_VALUE, Double.MAX_VALUE );
		checkEqual( Double.MIN_VALUE, Double.MIN_VALUE );
//		checkEqual( Double.MIN_NORMAL, Double.MIN_NORMAL );	// MIN_NORMAL does not exist for Java 1.5
	}

	public void testNotEqualPositiveAndNegativeZero() {
		checkNotEqual( +0.0, -0.0 );
		checkNotEqual( -0.0, +0.0 );
	}
	
	public void testNotEquals() {
		checkNotEqual( 0.0, Double.MIN_VALUE );
		checkNotEqual( 0.0, -Double.MIN_VALUE );
		checkNotEqual( Double.NaN, 0.0 );
		checkNotEqual( Double.POSITIVE_INFINITY, 0.0 );
		checkNotEqual( Double.NEGATIVE_INFINITY, 0.0 );
		checkNotEqual( Double.NaN, 0.0 );
	}

	public void testCompare() {
		checkLess( 0.0, Double.MIN_VALUE );
		checkLess( -Double.MIN_VALUE, 0.0 );
		checkLess( Double.NEGATIVE_INFINITY, 0.0 );
		checkLess( 0.0, Double.POSITIVE_INFINITY );
	}

	public void testHashCode() {
		checkSameHashCode( 1.234, 1.234 );
		checkSameHashCode( 0.0, 0.0 );
		checkSameHashCode( Double.NaN, Double.NaN );
	}

	private void checkSameHashCode( double f1, double f2 ) {
		DoubleTerm ft1 = new DoubleTerm( f1 );
		DoubleTerm ft2 = new DoubleTerm( f2 );
		
		assertEquals( ft1.hashCode(), ft2.hashCode() );
	}

	private void checkEqual( double f1, double f2 ) {
		DoubleTerm ft1 = new DoubleTerm( f1 );
		DoubleTerm ft2 = new DoubleTerm( f2 );
		
		assertEquals( ft1, ft2 );
		assertEquals( ft2, ft1 );
		assertTrue( ft1.compareTo( ft2 ) == 0 );
		assertTrue( ft2.compareTo( ft1 ) == 0 );
	}

	private void checkNotEqual( double f1, double f2 ) {
		DoubleTerm ft1 = new DoubleTerm( f1 );
		DoubleTerm ft2 = new DoubleTerm( f2 );
		
		assertFalse( ft1.equals( ft2 ) );
		assertFalse( ft2.equals( ft1 ) );
		assertTrue( ft1.compareTo( ft2 ) != 0 );
		assertTrue( ft2.compareTo( ft1 ) != 0 );
	}

	private void checkLess( double f1, double f2 ) {
		DoubleTerm ft1 = new DoubleTerm( f1 );
		DoubleTerm ft2 = new DoubleTerm( f2 );
		
		assertTrue( ft1.compareTo( ft2 ) < 0 );
		assertTrue( ft2.compareTo( ft1 ) > 0 );

		assertFalse( ft1.equals( ft2 ) );
		assertFalse( ft2.equals( ft1 ) );
	}
}
