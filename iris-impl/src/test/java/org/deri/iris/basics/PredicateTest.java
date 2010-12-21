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
package org.deri.iris.basics;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.ObjectTests;

/**
 * @author richi
 * 
 * Revision 1.1  26.07.2006 12:09:22  Darko Anicic, DERI Innsbruck
 */
public class PredicateTest extends TestCase {

	private static final int ARITY = 3;

	private static final int ARITYMORE = 4;

	private static final String SYMBOL = "date";

	private static final String SYMBOLMORE = "date1";

	public static Test suite() {
		return new TestSuite(PredicateTest.class, PredicateTest.class
				.getSimpleName());
	}

	public void testBasic() {
		final Predicate REFERENCE = new Predicate(SYMBOL, ARITY);
		
		assertEquals("getPredicateSymbol doesn't work properly", SYMBOL,
				REFERENCE.getPredicateSymbol());
		assertEquals("getArity doesn't work properly", ARITY, REFERENCE
				.getArity());
	}

	public void testEquals() {
		ObjectTests.runTestEquals(new Predicate(SYMBOL, ARITY), new Predicate(
				SYMBOL, ARITY), new Predicate(SYMBOL, ARITYMORE));
	}

	public void testHashCode() {
		ObjectTests.runTestHashCode(new Predicate(SYMBOL, ARITY), new Predicate(
				SYMBOL, ARITY));
	}

	public void testCompareTo() {
		ObjectTests.runTestCompareTo(new Predicate(SYMBOL, ARITY),
				new Predicate(SYMBOL, ARITY), new Predicate(SYMBOL, ARITYMORE),
				new Predicate(SYMBOLMORE, ARITY));
	}
}
