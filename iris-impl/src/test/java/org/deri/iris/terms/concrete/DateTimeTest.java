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

public class DateTimeTest extends TestCase {

	private static final int YEAR = 2005;

	private static final int MONTH = 3;

	private static final int DAY = 10;

	private static final int HOUR = 13;

	private static final int MINUTE = 56;

	private static final int SECOND = 0;

	private static final int TZ_HOUR = 1;

	private static final int TZ_MINUTE = 0;

	public void testBasic() {
		final DateTime dt = new DateTime(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, TZ_HOUR, TZ_MINUTE);

		assertEquals("Something wrong with getYear", YEAR, dt.getYear());
		assertEquals("Something wrong with getMonth", MONTH, dt .getMonth());
		assertEquals("Something wrong with getDay", DAY, dt.getDay());
		assertEquals("Something wrong with getHour", HOUR, dt.getHour());
		assertEquals("Something wrong with getMinute", MINUTE, dt.getMinute());
		assertEquals("Something wrong with getSecond", SECOND, dt.getSecond());

		DateTime dt0 = new DateTime(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, TZ_HOUR, TZ_MINUTE);
		assertEquals("Something wrong with setting or equals", dt, dt0);
	}

	public void testFloatingPointSeconds() {
		double seconds = 12.34567;
		final DateTime dt = new DateTime(YEAR, MONTH, DAY, HOUR, MINUTE, seconds, TZ_HOUR, TZ_MINUTE);

		assertEquals("Something wrong with getYear", YEAR, dt.getYear());
		assertEquals("Something wrong with getMonth", MONTH, dt .getMonth());
		assertEquals("Something wrong with getDay", DAY, dt.getDay());
		assertEquals("Something wrong with getHour", HOUR, dt.getHour());
		assertEquals("Something wrong with getMinute", MINUTE, dt.getMinute());
		assertEquals("Something wrong with getSecond", seconds, dt.getDecimalSecond());

		DateTime dt0 = new DateTime(YEAR, MONTH, DAY, HOUR, MINUTE, seconds, TZ_HOUR, TZ_MINUTE);
		assertEquals("Something wrong with setting or equals", dt, dt0);
	}

	public void testEquals() {
		ObjectTests.runTestEquals(new DateTime(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, TZ_HOUR, TZ_MINUTE),
				new DateTime(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, TZ_HOUR, TZ_MINUTE),
				new DateTime(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND + 1, TZ_HOUR, TZ_MINUTE));
	}

	public void testCompareTo() {
		ObjectTests.runTestCompareTo(new DateTime(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, TZ_HOUR, TZ_MINUTE), 
				new DateTime(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, TZ_HOUR, TZ_MINUTE), 
				new DateTime(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND + 1, TZ_HOUR, TZ_MINUTE), 
				new DateTime(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND + 2, TZ_HOUR, TZ_MINUTE));
	}

	public void testHashCode() {
		ObjectTests.runTestHashCode(new DateTime(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, TZ_HOUR, TZ_MINUTE), 
				new DateTime(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, TZ_HOUR, TZ_MINUTE));
	}

	public static Test suite() {
		return new TestSuite(DateTimeTest.class, DateTimeTest.class
				.getSimpleName());
	}

	public void testGetMinValue() {
		// somehow the year, month and day must not be 0...
		TermTests.runTestGetMinValue(new DateTime(1, 1, 1, 0, 0, 1, 0, 0));
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
			new DateTime(2000, 1, 1, 0, 0, 0, -1, 1);
			fail("It is possible to create a datetime with a negative tzHour and positive tzMinute");
		} catch (IllegalArgumentException e) {
		}

		try {
			new DateTime(2000, 1, 1, 0, 0, 0, 1, -1);
			fail("It is possible to create a datetime with a positive tzHour and negative tzMinute");
		} catch (IllegalArgumentException e) {
		}

		// the following should be possible
		new DateTime(2000, 1, 1, 0, 0, 0, 0, 0);
		new DateTime(2000, 1, 1, 0, 0, 0, 1, 0);
		new DateTime(2000, 1, 1, 0, 0, 0, 0, 1);
		new DateTime(2000, 1, 1, 0, 0, 0, 1, 1);
		new DateTime(2000, 1, 1, 0, 0, 0, -1, 0);
		new DateTime(2000, 1, 1, 0, 0, 0, 0, -1);
		new DateTime(2000, 1, 1, 0, 0, 0, -1, -1);
	}

	/**
	 * <p>
	 * Chechs whether the months are handeled correctly. The months should
	 * count from 1-12.
	 * </p>
	 * @see <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=1792385&group_id=167309&atid=842434">bug #1792385: DateTime datatype handles months incorrectly</a>
	 */
	public void testCorrectMonthBehaviour() {
		final DateTime y2000m1d1 = new DateTime(2000, 1, 1, 0, 0, 0, 0, 0);
		// if mounthts woule be from 0 - 11 this would shift to year 2001
		final DateTime y2000m12d1 = new DateTime(2000, 12, 1, 0, 0, 0, 0, 0);
		assertTrue(y2000m1d1 + " must be smaller than " + y2000m12d1, y2000m1d1.compareTo(y2000m12d1) < 0);
	}
}
