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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.ObjectTests;
import org.deri.iris.TermTests;

public class DurationTest extends TestCase {

	private static final int YEAR = 1;

	private static final int MONTH = 2;

	private static final int DAY = 3;

	private static final int HOUR = 4;

	private static final int MINUTE = 5;

	private static final int SECOND = 6;

	private static final int MILLISECOND = 7;

	public static Test suite() {
		return new TestSuite(DurationTest.class, DurationTest.class
				.getSimpleName());
	}
	
	public void testBasic() {
		Duration d1 = new Duration( true, YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, MILLISECOND);

		assertEquals("Something wrong with getYear", YEAR, d1.getYear());
		assertEquals("Something wrong with getMonth", MONTH, d1.getMonth());
		assertEquals("Something wrong with getDay", DAY, d1.getDay());
		assertEquals("Something wrong with getHour", HOUR, d1.getHour());
		assertEquals("Something wrong with getMinute", MINUTE, d1.getMinute());
		assertEquals("Something wrong with getSecond", SECOND, d1.getSecond());
		assertEquals("Something wrong with getMillisecond", MILLISECOND, d1.getMillisecond());
		
		Duration d2 = new Duration( true, YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, MILLISECOND);
		assertEquals( d1, d2 );
	}

	public void testDecimalSeconds() {
		double seconds = 1.234567;
		Duration d1 = new Duration(true, YEAR, MONTH, DAY, HOUR, MINUTE, seconds);
		assertEquals( seconds, d1.getDecimalSecond() );

		Duration d2 = new Duration(true, YEAR, MONTH, DAY, HOUR, MINUTE, seconds);
		assertEquals( d1, d2 );
	}
	
	public void testEquals() {
		ObjectTests.runTestEquals(new Duration(true, 2000, 1, 1, 12, 01, 00),
				new Duration(true, 2000, 1, 1, 12, 01, 00), new Duration(true, 2000, 1, 1,
						12, 02, 00));
	}

	public void testCompareTo() {
		ObjectTests.runTestCompareTo(
						new Duration(true, 2000, 1, 1, 11, 01, 00),
						new Duration(true, 2000, 1, 1, 11, 01, 00),
						new Duration(true, 2000, 1, 1, 11, 01, 01),
						new Duration(true, 2000, 1, 1, 11, 02, 00));
		
		ObjectTests.runTestCompareTo(
						new Duration(false, 2000, 1, 1, 11, 01, 00),
						new Duration(false, 2000, 1, 1, 11, 01, 00),
						new Duration(false, 2000, 1, 1, 11, 01, 01),
						new Duration(false, 2000, 1, 1, 11, 02, 00));
		
		// should be correct:  http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/
		ObjectTests.runTestCompareTo(
						new Duration(false, 2000, 1, 1, 11, 01, 00),
						new Duration(false, 2000, 1, 1, 11, 01, 00),
						new Duration(true, 2000, 1, 1, 11, 01, 01),
						new Duration(true, 2000, 1, 1, 11, 02, 00));
	}

	public void testHashCode() {
		ObjectTests.runTestHashCode(new Duration(true, YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, MILLISECOND), 
				new Duration(true, YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, MILLISECOND));
	}

	public void testGetMinValue() {
		TermTests.runTestGetMinValue(new Duration(true, 0, 0, 0, 0, 0, 1));
	}
}
