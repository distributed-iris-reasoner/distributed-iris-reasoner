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

public class FloatTest extends TestCase {

	public void testConstruct() {
		FloatTerm ft = new FloatTerm(0.123f);
		assertEquals( 0.123f, ft.getValue().floatValue() );
	}
	
	public void testEquals() {
		checkEqual( +0.0f, +0.0f );
		checkEqual( -0.0f, -0.0f );
		checkEqual( 1.1f, 1.1f );
		checkEqual( -1.1f, -1.1f );
		checkEqual( Float.NaN, Float.NaN );
		checkEqual( Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY );
		checkEqual( Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY );
		checkEqual( Float.MAX_VALUE, Float.MAX_VALUE );
		checkEqual( Float.MIN_VALUE, Float.MIN_VALUE );
//		checkEqual( Float.MIN_NORMAL, Float.MIN_NORMAL );	// MIN_NORMAL does not exist for Java 1.5
	}
	
	public void testNotEqualPositiveAndNegativeZero() {
		checkNotEqual( +0.0f, -0.0f );
		checkNotEqual( -0.0f, +0.0f );
	}
	
	public void testNotEquals() {
		checkNotEqual( 0.0f, Float.MIN_VALUE );
		checkNotEqual( 0.0f, -Float.MIN_VALUE );
		checkNotEqual( Float.NaN, 0.0f );
		checkNotEqual( Float.POSITIVE_INFINITY, 0.0f );
		checkNotEqual( Float.NEGATIVE_INFINITY, 0.0f );
		checkNotEqual( Float.NaN, 0.0f );
	}

	public void testCompare() {
		checkLess( 0.0f, Float.MIN_VALUE );
		checkLess( -Float.MIN_VALUE, 0.0f );
		checkLess( Float.NEGATIVE_INFINITY, 0.0f );
		checkLess( 0.0f, Float.POSITIVE_INFINITY );
	}

	public void testHashCode() {
		checkSameHashCode( 1.234f, 1.234f );
		checkSameHashCode( 0.0f, 0.0f );
		checkSameHashCode( Float.NaN, Float.NaN );
	}

	private void checkSameHashCode( float f1, float f2 ) {
		FloatTerm ft1 = new FloatTerm( f1 );
		FloatTerm ft2 = new FloatTerm( f2 );
		
		assertEquals( ft1.hashCode(), ft2.hashCode() );
	}

	private void checkEqual( float f1, float f2 ) {
		FloatTerm ft1 = new FloatTerm( f1 );
		FloatTerm ft2 = new FloatTerm( f2 );
		
		assertEquals( ft1, ft2 );
		assertEquals( ft2, ft1 );
		assertTrue( ft1.compareTo( ft2 ) == 0 );
		assertTrue( ft2.compareTo( ft1 ) == 0 );
	}

	private void checkNotEqual( float f1, float f2 ) {
		FloatTerm ft1 = new FloatTerm( f1 );
		FloatTerm ft2 = new FloatTerm( f2 );
		
		assertFalse( ft1.equals( ft2 ) );
		assertFalse( ft2.equals( ft1 ) );
		assertTrue( ft1.compareTo( ft2 ) != 0 );
		assertTrue( ft2.compareTo( ft1 ) != 0 );
	}

	private void checkLess( float f1, float f2 ) {
		FloatTerm ft1 = new FloatTerm( f1 );
		FloatTerm ft2 = new FloatTerm( f2 );
		
		assertTrue( ft1.compareTo( ft2 ) < 0 );
		assertTrue( ft2.compareTo( ft1 ) > 0 );

		assertFalse( ft1.equals( ft2 ) );
		assertFalse( ft2.equals( ft1 ) );
	}
}
