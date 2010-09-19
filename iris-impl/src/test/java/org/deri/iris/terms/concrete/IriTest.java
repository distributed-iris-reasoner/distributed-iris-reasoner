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

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.ObjectTests;
import org.deri.iris.TermTests;

public class IriTest extends TestCase {
	private static final URI REFERENCE;

	private static final String URISTR = "http://localhost:8080/test.jsp";

	private static final String URISTRMORE = "http://localhost:9080/test.jsp";

	private static final String URISTRMORE1 = "http://localhost:9080/test.jsp1";
	static {
		URI tmp = null;
		try {
			tmp = new URI("http://localhost:8080/test.jsp");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		REFERENCE = tmp;
	}

	public void testBasic() {
		Iri fix = new Iri(REFERENCE);
		Iri test = new Iri(URISTR);

		assertEquals("Something wrong with getUri", REFERENCE, fix.getURI());
		assertEquals("Something wrong with The toString", URISTR, fix
				.getValue());

		assertEquals("Something wrong with parsing", fix, test);

		assertEquals("Something wrong with the creation", fix, new Iri(
				REFERENCE));
		assertEquals("Something wrong with the creation", fix, new Iri(
				URISTR));
	}

	public void testEquals() {
		ObjectTests.runTestEquals(new Iri(URISTR), new Iri(URISTR),
				new Iri(URISTRMORE));
	}

	public void testCompareTo() {
		ObjectTests.runTestCompareTo(new Iri(URISTR), new Iri(URISTR),
				new Iri(URISTRMORE), new Iri(URISTRMORE1));
	}

	public void testHashCode() {
		ObjectTests.runTestHashCode(new Iri(URISTR), new Iri(URISTR));
	}

	public static Test suite() {
		return new TestSuite(IriTest.class, IriTest.class.getSimpleName());
	}
	
	public void testGetMinValue() {
		TermTests.runTestGetMinValue(new Iri("a"));
	}
}
