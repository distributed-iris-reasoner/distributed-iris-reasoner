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

public class GYearTest extends TestCase {

	private static final int YEAR = 2005;

	public void testBasic() {
		final GYear gyear = new GYear(YEAR);

		assertEquals("Something wrong with getYear", YEAR, gyear.getYear());
	}

	public void testEquals() {
		ObjectTests.runTestEquals(new GYear(YEAR), new GYear(YEAR),
				new GYear(YEAR + 1));
	}

	public void testCompareTo() {
		ObjectTests.runTestCompareTo(new GYear(YEAR), new GYear(YEAR),
				new GYear(YEAR + 1), new GYear(YEAR + 2));
	}

	public void testHashCode() {
		ObjectTests.runTestHashCode(new GYear(YEAR), new GYear(YEAR));
	}

	public static Test suite() {
		return new TestSuite(GYearTest.class, GYearTest.class.getSimpleName());
	}
	
	public void testGetMinValue() {
		TermTests.runTestGetMinValue(new GYear(2));
	}

	/**
	 * <p>
	 * This test checks whether it is possible to specify inconsisntent
	 * timezones. E.g. a timezone with positive hours and negative minutes.
	 * </p>
	 * @see <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=1778705&group_id=167309&atid=842434">bug #1778705: it is possible to specify inconsistent timezones</a>
	 */
	public void testConsistentTimezones() {
		try {
			new GYear(2000, -1, 1);
			fail("It is possible to create a year with a negative tzHour and positive tzMinute");
		} catch (IllegalArgumentException e) {
		}

		try {
			new GYear(2000, 1, -1);
			fail("It is possible to create a year with a positive tzHour and negative tzMinute");
		} catch (IllegalArgumentException e) {
		}

		// the following should be possible
		new GYear(2000, 0, 0);
		new GYear(2000, 1, 0);
		new GYear(2000, 0, 1);
		new GYear(2000, 1, 1);
		new GYear(2000, -1, 0);
		new GYear(2000, 0, -1);
		new GYear(2000, -1, -1);
	}
}
