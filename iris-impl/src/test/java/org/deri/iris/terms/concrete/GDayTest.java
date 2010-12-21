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

public class GDayTest extends TestCase {
	private static final int DAY = 13;

	public void testBasic() {
		final GDay gday = new GDay(DAY);

		assertEquals("Something wrong with getDay", DAY, gday.getDay());
	}

	public void testEquals() {
		ObjectTests.runTestEquals(new GDay(DAY), new GDay(DAY),
				new GDay(DAY + 1));
	}

	public void testCompareTo() {
		ObjectTests.runTestCompareTo(new GDay(DAY), new GDay(DAY), new GDay(
				DAY + 1), new GDay(DAY + 2));
	}

	public void testHashCode() {
		ObjectTests.runTestHashCode(new GDay(DAY), new GDay(DAY));
	}

	public static Test suite() {
		return new TestSuite(GDayTest.class, GDayTest.class.getSimpleName());
	}

	public void testGetMinValue() {
		TermTests.runTestGetMinValue(new GDay(2));
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
			new GDay(1, -1, 1);
			fail("It is possible to create a day with a negative tzHour and positive tzMinute");
		} catch (IllegalArgumentException e) {
		}

		try {
			new GDay(1, 1, -1);
			fail("It is possible to create a day with a positive tzHour and negative tzMinute");
		} catch (IllegalArgumentException e) {
		}

		// the following should be possible
		new GDay(1, 0, 0);
		new GDay(1, 1, 0);
		new GDay(1, 0, 1);
		new GDay(1, 1, 1);
		new GDay(1, -1, 0);
		new GDay(1, 0, -1);
		new GDay(1, -1, -1);
	}
}
