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

import junit.framework.TestCase;

import org.deri.iris.ObjectTests;
import org.deri.iris.api.terms.concrete.IAnyURI;

/**
 * <p>
 * Test the implementation of the AnyURI data-type.
 * </p>
 * 
 * @author Adrian Marte
 */
public class AnyURITest extends TestCase {

	private static final String URI1_STRING = "http://www.sti-innsbruck.at";
	
	private static final URI URI1 = URI.create(URI1_STRING);
	
	private static final String URI2_STRING = "http://www.sti2.at";
	
	private static final URI URI2 = URI.create(URI2_STRING);

	public void testBasic() {
		IAnyURI anyUri = new AnyURI(URI1);
		
		assertEquals(URI1, anyUri.getValue());
	}

	public void testToCanonicalString() {
		IAnyURI anyUri = new AnyURI(URI1);
		assertEquals(URI1_STRING, anyUri.toCanonicalString());
	}

	public void testCompareTo() {
		IAnyURI uri1 = new AnyURI(URI1);
		IAnyURI uri2 = new AnyURI(URI1);
		IAnyURI uri3 = new AnyURI(URI2);
		
		ObjectTests.runTestCompareTo(uri1, uri2, uri3);
	}

	public void testEquals() {
		IAnyURI uri1 = new AnyURI(URI1);
		IAnyURI uri2 = new AnyURI(URI2);
		IAnyURI uri3 = new AnyURI(URI2);

		ObjectTests.runTestEquals(uri2, uri3, uri1);
	}

	public void testHashCode() {
		IAnyURI uri1 = new AnyURI(URI1);
		IAnyURI uri2 = new AnyURI(URI1);

		ObjectTests.runTestHashCode(uri1, uri2);
	}

}
