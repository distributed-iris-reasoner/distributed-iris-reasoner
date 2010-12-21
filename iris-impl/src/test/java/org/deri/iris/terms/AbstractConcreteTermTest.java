/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2009 Semantic Technology Institute (STI) Innsbruck, 
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

import java.net.URI;

import junit.framework.TestCase;

import org.deri.iris.ObjectTests;
import org.deri.iris.api.terms.IConcreteTerm;

/**
 * @author Adrian Marte
 */
public abstract class AbstractConcreteTermTest extends TestCase {

	protected abstract IConcreteTerm createBasic();

	protected abstract IConcreteTerm createEqual();

	protected abstract IConcreteTerm createGreater();

	protected abstract String createBasicString();

	protected abstract String createEqualString();

	protected abstract String createGreaterString();

	protected abstract URI getDatatypeIRI();

	private IConcreteTerm basic;

	private IConcreteTerm equal;

	private IConcreteTerm greater;

	private String basicString;

	private String equalString;

	private String greaterString;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		basic = createBasic();
		equal = createEqual();
		greater = createGreater();

		basicString = createBasicString();
		equalString = createEqualString();
		greaterString = createGreaterString();
	}

	public void testNotNull() {
		assertNotNull(basic);
		assertNotNull(equal);
		assertNotNull(greater);

		assertNotNull(basicString);
		assertNotNull(equalString);
		assertNotNull(greaterString);
	}

	public void testToCanonicalString() {
		assertEquals(basicString, basic.toCanonicalString());
		assertEquals(equalString, equal.toCanonicalString());
		assertEquals(greaterString, greater.toCanonicalString());
	}

	public void testCompareTo() {
		ObjectTests.runTestCompareTo(basic, equal, greater);
	}

	public void testEquals() {
		ObjectTests.runTestEquals(basic, equal, greater);
	}

	public void testHashCode() {
		ObjectTests.runTestHashCode(basic, equal);
	}

	public void testDatatypeIRI() {
		assertEquals(getDatatypeIRI(), basic.getDatatypeIRI());
		assertEquals(getDatatypeIRI(), equal.getDatatypeIRI());
		assertEquals(getDatatypeIRI(), greater.getDatatypeIRI());
	}

}
