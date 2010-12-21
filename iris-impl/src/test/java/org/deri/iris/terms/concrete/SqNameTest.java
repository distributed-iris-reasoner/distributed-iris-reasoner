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
import org.deri.iris.api.terms.concrete.IIri;

public class SqNameTest extends TestCase {

	private static final String NAME = "sqName";

	private static final String NAMEMORE = "sqNbme";

	private static final String NAMEMORE1 = "sqNbmf";

	private static final String SPACE = "http://www.deri.org/reasoner";

	private static final IIri SPACEIRI = new Iri(SPACE);

	public void testBasic() {
		SqName fix = new SqName(SPACEIRI, NAME);
		SqName test = new SqName(SPACE + "#" + NAME);

		assertEquals("Something wrong whith instanciation", fix, test);
		assertEquals("Something wrong with name", NAME, test.getName());
		assertEquals("Something wrong with name", fix.getName(), test.getName());
		assertEquals("Something wrong with namespace", SPACEIRI, test
				.getNamespace());
		assertEquals("Something wrong with namespace", fix.getNamespace(), test
				.getNamespace());
	}

	public void testEquals() {
		ObjectTests.runTestEquals(new SqName(SPACE, NAME), new SqName(
				SPACE, NAME), new SqName(SPACE, NAMEMORE));
	}

	public void testCompareTo() {
		ObjectTests.runTestCompareTo(new SqName(SPACE, NAME),
				new SqName(SPACE, NAME), new SqName(SPACE, NAMEMORE),
				new SqName(SPACE, NAMEMORE1));
	}

	public void testHashCode() {
		ObjectTests.runTestHashCode(new SqName(SPACE, NAME), new SqName(
				SPACE, NAME));
	}

	public static Test suite() {
		return new TestSuite(SqNameTest.class, SqNameTest.class.getSimpleName());
	}
	
	public void testGetMinValue() {
		TermTests.runTestGetMinValue(new SqName("", "a"));
	}
}
