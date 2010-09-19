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

import java.math.BigDecimal;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.ObjectTests;
import org.deri.iris.TermTests;

/**
 * <p>
 * Tests the functionality of the <code>DecimalTerm</code>.
 * </p>
 * <p>
 * $Id$
 * </p>
 * 
 * @author Richard Pöttler, richard dot poettler at deri dot org
 * @version $Revision$
 */
public class DecimalTest extends TestCase {

	private final static BigDecimal BASIC = new BigDecimal(0.1d);

	private final static BigDecimal MORE = new BigDecimal(0.2d);

	private final static BigDecimal MORE1 = new BigDecimal(0.3d);

	public static Test suite() {
		return new TestSuite(DecimalTest.class, DecimalTest.class
				.getSimpleName());
	}

	public void testBasic() {
		DecimalTerm basic = new DecimalTerm(BASIC);

		assertEquals("object not initialized correctly", BASIC, basic
				.getValue());
	}

	public void testEquals() {
		ObjectTests.runTestEquals(new DecimalTerm(BASIC),
				new DecimalTerm(BASIC), new DecimalTerm(MORE));
	}

	public void testCompare() {
		ObjectTests.runTestCompareTo(new DecimalTerm(BASIC), new DecimalTerm(
				BASIC), new DecimalTerm(MORE), new DecimalTerm(MORE1));
	}

	public void testEqualsPositiveNegativeZero() {
		ObjectTests.runTestCompareTo(new DecimalTerm(+0.0),
				new DecimalTerm(-0.0), new DecimalTerm(0.000000001), new DecimalTerm(
								0.000000002));
	}

	public void testHashCode() {
		ObjectTests.runTestHashCode(new DecimalTerm(BASIC), new DecimalTerm(
				BASIC));
	}

	public void testGetMinValue() {
		TermTests.runTestGetMinValue(new DecimalTerm(Double.MIN_VALUE + 0.0001));
	}
}
