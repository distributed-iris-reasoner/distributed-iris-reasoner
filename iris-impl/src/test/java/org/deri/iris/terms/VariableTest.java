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
package org.deri.iris.terms;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.ObjectTests;

/**
 * @author richi
 * 
 */
public class VariableTest extends TestCase {

	private static final String BASIC = "aaa";

	private static final String MORE = "aab";

	private static final String MORE1 = "aac";

	public static Test suite() {
		return new TestSuite(VariableTest.class, VariableTest.class
				.getSimpleName());
	}

//	public void testBasic() {
//		Variable basic = new Variable(BASIC);
//		Variable changed = new Variable(MORE);
//		changed.setName(BASIC);
//		assertEquals("object not initialized correctly", BASIC, basic
//				.getName());
//		assertEquals("setValue(..) doesn't work correctly", basic, changed);
//	}

	public void testEquals() {
		ObjectTests.runTestEquals(new Variable(BASIC), new Variable(BASIC),
				new Variable(MORE1));
	}

	public void testHashCode() {
		ObjectTests
				.runTestHashCode(new Variable(BASIC), new Variable(BASIC));
	}

	public void testCompareTo() {
		ObjectTests.runTestCompareTo(new Variable(BASIC),
				new Variable(BASIC), new Variable(MORE), new Variable(
						MORE1));
	}
}
