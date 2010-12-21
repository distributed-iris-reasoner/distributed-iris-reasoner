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

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.ObjectTests;
import org.deri.iris.TermTests;

public class HexBinaryTest extends TestCase {
	private static final String BASIC = "adf3";

	private static final String BASICMORE = "adf4";

	private static final String BASICMORE1 = "adf5";

	private static final String[] INVALID = new String[] { "12fff", "45ka" };

	private static final String[] VALID = new String[] { "0123456789abcdef" };

	public void testBasic() {
		HexBinary hb = new HexBinary(BASIC);
		assertEquals("Something wrong with getHexBinary", BASIC.toUpperCase(),
				hb.getValue());
		assertEquals("Something wrong with the parsing", hb, new HexBinary(
				BASIC));
	}

	public void testEquals() {
		ObjectTests.runTestEquals(new HexBinary(BASIC), new HexBinary(
				BASIC), new HexBinary(BASICMORE));
	}

	public void testCompareTo() {
		ObjectTests.runTestCompareTo(new HexBinary(BASIC),
				new HexBinary(BASIC), new HexBinary(BASICMORE),
				new HexBinary(BASICMORE1));
	}

	public void testHashCode() {
		ObjectTests.runTestHashCode(new HexBinary(BASIC), new HexBinary(
				BASIC));
	}

	public void testInValidParsing() {
		for (String str : INVALID) {
			boolean gotException = false;
			try {

				new HexBinary(str);
			} catch (IllegalArgumentException e) {
				gotException = true;
			}
			if (!gotException) {
				throw new AssertionFailedError("The parsing of " + str
						+ " shouldn't work");
			}
		}
	}

	public void testValidParsing() {
		for (String str : VALID) {
			assertEquals("The parsing of " + str + "should work", str
					.toUpperCase(), new HexBinary(str).getValue());
		}
	}

	public static Test suite() {
		return new TestSuite(HexBinaryTest.class, HexBinaryTest.class
				.getSimpleName());
	}
	
	public void testGetMinValue() {
		TermTests.runTestGetMinValue(new HexBinary("0a"));
	}
}
