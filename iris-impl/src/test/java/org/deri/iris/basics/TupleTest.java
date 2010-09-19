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

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.TERM;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.MiscHelper;
import org.deri.iris.ObjectTests;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.IConstructedTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;

/**
 * @author Darko Anicic, DERI Innsbruck
 * @date   24.07.2006 14:19:17
 * 
 */
public class TupleTest extends TestCase {

	private static final int ARITY = 3;
	
	private static final ITuple REFERENCE = MiscHelper.createTuple("a", "b", "c");
	
	private static final ITuple TUPLE = MiscHelper.createTuple("a", "b", "c");
	
	private static final ITuple MORE = MiscHelper.createTuple("a", "b", "d");
	
	private static final ITuple EVENMORE = MiscHelper.createTuple("a", "b", "d", "e");

	public static Test suite() {
		return new TestSuite(TupleTest.class, TupleTest.class
				.getSimpleName());
	}

	public void testSize() {
		assertEquals("The size method doesn't work properly", ARITY, TUPLE.size());
	}

	public void testGet() {
		assertEquals("The get method doesn't work properly", TERM.createString("a"), TUPLE.get(0));
		assertEquals("The get method doesn't work properly", TERM.createString("b"), TUPLE.get(1));
		assertEquals("The get method doesn't work properly", TERM.createString("c"), TUPLE.get(2));
	}

	public void testEquals() {
		ObjectTests.runTestEquals(REFERENCE, TUPLE, MORE);
	}
	
	public void testCompareTo() {
		ObjectTests.runTestCompareTo(REFERENCE, TUPLE, MORE, EVENMORE);
	}
	
	public void testHashCode() {
		ObjectTests.runTestHashCode(REFERENCE, TUPLE);
	}
	
	public void testVariables() {
		Set<IVariable> variables = new HashSet<IVariable>();
		IVariable x = TERM.createVariable("X");
		IVariable y = TERM.createVariable("Y");
		
		variables.add(x);
		variables.add(y);
		
		IConstructedTerm c1 = TERM.createConstruct("c1", y);
		IConstructedTerm c2 = TERM.createConstruct("c2", c1, x);
		List<ITerm> terms = new ArrayList<ITerm>();
		terms.addAll(REFERENCE);
		terms.add(c2);
		
		assertEquals(variables, BASIC.createTuple(terms).getVariables());
	}
}
