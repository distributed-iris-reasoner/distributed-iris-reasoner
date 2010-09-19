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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.ObjectTests;
import org.deri.iris.api.terms.ITerm;

/**
 * <p>
 * Class to test some basic functionality of ConstructedTerm.
 * </p>
 * <p>
 * $Id$
 * </p>
 * 
 * @author richi
 * @version $Revision$
 * @date $Date$
 */
public class ConstructedTermTest extends TestCase {

	private static final String SYMBOL = "and";

	private static ConstructedTerm BASIC;
	private static ConstructedTerm BASIC2;

	private static ConstructedTerm MORE;
	private static ConstructedTerm MORE2;

	private static ConstructedTerm NOT_GROUND;

	public static Test suite() {
		return new TestSuite(ConstructedTermTest.class,
				ConstructedTermTest.class.getSimpleName());
	}

	public void setUp() {
		List<ITerm> terms = new ArrayList<ITerm>();
		terms.add( new StringTerm("a"));
		terms.add( new StringTerm("b"));

		BASIC = new ConstructedTerm(SYMBOL, terms);

		terms = new ArrayList<ITerm>();
		terms.add( new StringTerm("a"));
		terms.add( new StringTerm("b"));

		BASIC2 = new ConstructedTerm(SYMBOL, terms);

		terms = new ArrayList<ITerm>();
		terms.add( new StringTerm("b"));
		terms.add( new StringTerm("a"));

		MORE = new ConstructedTerm(SYMBOL, terms);

		terms = new ArrayList<ITerm>();
		terms.add( new StringTerm("a"));
		terms.add( new StringTerm("c"));
		terms.add( new StringTerm("a"));

		MORE2 = new ConstructedTerm(SYMBOL, terms);
		
		terms = new ArrayList<ITerm>();
		terms.add( new StringTerm("a"));
		terms.add( new Variable("X"));
		terms.add( new StringTerm("a"));

		NOT_GROUND = new ConstructedTerm(SYMBOL, terms);
	}

	public void testBasic() {
		List<ITerm> terms = new ArrayList<ITerm>();
		terms.add(0, new StringTerm("a"));
		terms.add(1, new StringTerm("b"));

		assertEquals("Object not initialized correctly", SYMBOL, BASIC
				.getFunctionSymbol());
		assertEquals("The collections must have the same size", terms.size(),
				BASIC.getParameters().size());
		assertTrue("The collections must contain the same elements", BASIC
				.getParameters().containsAll(terms));
	}

	public void testEquals() {
		ObjectTests.runTestEquals(BASIC, BASIC2, MORE);
		ObjectTests.runTestEquals(BASIC, BASIC2, MORE2);
		
		assertFalse( BASIC.equals( MORE ) );
		assertFalse( MORE.equals( BASIC ) );
	}

	public void testHashCode() {
		ObjectTests.runTestHashCode(BASIC, BASIC2);
	}

	public void testCompareTo() {
		assertEquals( BASIC.compareTo( BASIC2 ), 0 );
		assertEquals( BASIC2.compareTo( BASIC ), 0 );

		assertTrue( BASIC.compareTo( MORE ) < 0 );
		assertTrue( MORE.compareTo( BASIC ) > 0 );

		assertTrue( BASIC.compareTo( MORE2 ) < 0 );
		assertTrue( MORE2.compareTo( BASIC ) > 0 );
	}
	
	public void testGetFunctionSymbol()
	{
		assertEquals( BASIC.getFunctionSymbol(), SYMBOL );
	}
	
	public void testGetParameters()
	{
		List<ITerm> terms = BASIC.getParameters();
		
		assertEquals( terms.size(), 2 );
		assertEquals( terms.get( 0 ), new StringTerm("a") );
		assertEquals( terms.get( 1 ), new StringTerm("b") );
	}
	
	public void testIsGround()
	{
		assertTrue( BASIC.isGround() );
		assertTrue( BASIC2.isGround() );
		assertTrue( MORE.isGround() );
		assertTrue( MORE2.isGround() );
		
		assertFalse( NOT_GROUND.isGround() );
	}
}
