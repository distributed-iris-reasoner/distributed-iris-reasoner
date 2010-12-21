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

/**
 * <p>
 * Tests the functionality of the <code>IntegerTerm</code>.
 * </p>
 * <p>
 * $Id$
 * </p>
 * 
 * @author Richard Pöttler, richard dot poettler at deri dot org
 * @version $Revision$
 */
public class IntegerTest extends TestCase {

	private final static int BASIC = 1;

	private final static int MORE = 2;

	private final static int MORE1 = 3;

	public static Test suite() {
		return new TestSuite(IntegerTest.class, IntegerTest.class
				.getSimpleName());
	}

	public void testBasic() {
		IntegerTerm basic = new IntegerTerm(BASIC);
		assertEquals("object not initialized correctly",
				Integer.valueOf(BASIC), Integer.valueOf(basic.getValue().intValue()));
	}

	public void testEquals() {
		ObjectTests.runTestEquals(new IntegerTerm(BASIC),
				new IntegerTerm(BASIC), new IntegerTerm(MORE));
	}

	public void testCompare() {
		ObjectTests.runTestCompareTo(new IntegerTerm(BASIC), new IntegerTerm(
				BASIC), new IntegerTerm(MORE), new IntegerTerm(MORE1));
	}

	public void testHashCode() {
		ObjectTests.runTestHashCode(new IntegerTerm(BASIC), new IntegerTerm(
				BASIC));
	}

	public void testGetMinValue() {
		TermTests.runTestGetMinValue(new IntegerTerm(Integer.MIN_VALUE + 1));
	}
}
