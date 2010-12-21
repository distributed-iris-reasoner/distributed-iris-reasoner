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

import junit.framework.TestCase;

import org.deri.iris.ObjectTests;

/**
 * Simple tests for the implementation of the rdf:XMLLiteral
 * 
 * @author gigi
 * 
 */
public class XMLLiteralTest extends TestCase {

	private static final String STRING = "<tag>Text</tag>";
	private static final String LANG = "en";

	public void testEquals() {
		XMLLiteral literal1 = new XMLLiteral(STRING, LANG);
		XMLLiteral literal2 = new XMLLiteral(STRING, LANG);
		XMLLiteral literal3 = new XMLLiteral(STRING, "de");
		XMLLiteral literal4 = new XMLLiteral(STRING, "en");
		XMLLiteral literal5 = new XMLLiteral("<tag>Foobar</tag>", LANG);

		ObjectTests.runTestEquals(literal1, literal2, literal5);
		ObjectTests.runTestEquals(literal3, literal4, literal5);
	}

	public void testHashCode() {
		XMLLiteral literal1 = new XMLLiteral(STRING, LANG);
		XMLLiteral literal2 = new XMLLiteral(STRING, LANG);

		ObjectTests.runTestHashCode(literal1, literal2);
	}

}
