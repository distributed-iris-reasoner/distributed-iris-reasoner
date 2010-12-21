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
package org.deri.iris.functional;

import junit.framework.TestCase;

/**
 * Tests for arithmetic operators.
 */
public class ArithmeticTest extends TestCase
{
	public void testAddTwoVariables() throws Exception
	{
		String program =
			"p( 0 )." +
			"p( 1 )." +
			"p( 2 )." +
			
			"q( 2 )." +
			"q( 3 )." +
			"q( 4 )." +
			
			// Rule with operator predicate
			"r( ?X, ?Y ) :- p( ?X ), q( ?Y ), ?X + ?Y = 3." +

			// Same rule with named predicate
			"s( ?X, ?Y ) :- p( ?X ), q( ?Y ), ADD( ?X, ?Y, 3 )." +
			
			// Same rule with all variables
			"t( ?X, ?Y ) :- p( ?X ), q( ?Y ), ?X + ?Y = ?Z, ?Z = 3." +

			// Combine all the rules
			"a( ?X, ?Y ) :- r( ?X, ?Y ), s( ?X, ?Y ), t( ?X, ?Y )." +
			
			"?- a(?X, ?Y ).";
		
       	String expectedResults = 
			"a( 1, 2 )." +
			"a( 0, 3 ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testAddAllVariables() throws Exception
	{
		String program =
			"p( 0 )." +
			"p( 1 )." +
			
			"q( 2 )." +
			"q( 3 )." +
			
			// Rule with operator predicate
			"r( ?Z ) :- p( ?X ), q( ?Y ), ?X + ?Y = ?Z." +

			"?- r( ?Z ).";
		
       	String expectedResults = 
			"r( 2 )." +
			"r( 3 )." +
			"r( 4 ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testAddAllConstantsTrue() throws Exception
	{
		String program =
			"p( 0 )." +
			"p( 1 )." +
			
			// Rule with operator predicate
			"r( ?X ) :- p( ?X ), 1 + 2 = 3." +

			"?- r( ?Z ).";
		
       	String expectedResults = 
			"r( 0 )." +
			"r( 1 ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testAddAllConstantsFalse() throws Exception
	{
		String program =
			"p( 0 )." +
			"p( 1 )." +
			
			// Rule with operator predicate
			"r( ?X ) :- p( ?X ), 1 + 2 = 2." +

			"?- r( ?Z ).";
		
       	String expectedResults = "";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testAddConstantOperandSecond() throws Exception
	{
		String program =
			"p( 0 )." +
			"p( 1 )." +
			"p( 2 )." +
			
			"q( 2 )." +
			"q( 3 )." +
			"q( 4 )." +
			
			// Rule with operator predicate
			"r( ?X, ?Y ) :- p( ?X ), q( ?Y ), ?X + 3 = ?Y." +

			// Same rule with named predicate
			"s( ?X, ?Y ) :- p( ?X ), q( ?Y ), ADD( ?X, 3, ?Y )." +
			
			// Same rule with all variables
			"t( ?X, ?Y ) :- p( ?X ), q( ?Y ), ?X + ?Z = ?Y, ?Z = 3." +

			// Combine both the rules
			"a( ?X, ?Y ) :- r( ?X, ?Y ), s( ?X, ?Y ), t( ?X, ?Y )." +
			
			"?- a(?X, ?Y ).";
		
       	String expectedResults = 
			"a( 0, 3 )." +
			"a( 1, 4 ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testAddConstantOperandFirst() throws Exception
	{
		String program =
			"p( 0 )." +
			"p( 1 )." +
			"p( 2 )." +
			
			"q( 2 )." +
			"q( 3 )." +
			"q( 4 )." +
			
			// Rule with operator predicate
			"r( ?X, ?Y ) :- p( ?X ), q( ?Y ), 3 + ?X = ?Y." +

			// Same rule with named predicate
			"s( ?X, ?Y ) :- p( ?X ), q( ?Y ), ADD( 3, ?X, ?Y )." +
			
			// Same rule with all variables
			"t( ?X, ?Y ) :- p( ?X ), q( ?Y ), ADD( ?Z, ?X, ?Y ), ?Z = 3." +
			
			// Combine all the rules
			"a( ?X, ?Y ) :- r( ?X, ?Y ), s( ?X, ?Y ), t( ?X, ?Y )." +
			
			"?- a(?X, ?Y ).";
		
       	String expectedResults = 
			"a( 0, 3 )." +
			"a( 1, 4 ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testAddRearrangeVariables() throws Exception
	{
		String program =
			"one( 1 )." +
			"nine( 9 )." +
			"ten( 10 )." +
			
			"r( ?Z ) :- one( ?X ), ten( ?Y ), ?X + ?Y = ?Z." +
			"s( ?Z ) :- one( ?X ), nine( ?Z ), ten( ?Y ), ?Z + ?X = ?Y." +
			"t( ?Z ) :- nine( ?X ), one( ?Z ), ten( ?Y ), ?X + ?Z = ?Y.";

       	Helper.evaluateWithAllStrategies( program + "?- r(?X).", "r( 11 )." );
       	Helper.evaluateWithAllStrategies( program + "?- s(?X).", "s( 9 )." );
       	Helper.evaluateWithAllStrategies( program + "?- t(?X).", "t( 1 )." );
	}

	public void testSubtractTwoVariables() throws Exception
	{
		String program =
			"p( 3 )." +
			"p( 4 )." +
			"p( 5 )." +
			
			"q( 1 )." +
			"q( 2 )." +
			"q( 3 )." +
			
			// Rule with operator predicate
			"r( ?X, ?Y ) :- p( ?X ), q( ?Y ), ?X - ?Y = 3." +
			
			// Same rule with named predicate
			"s( ?X, ?Y ) :- p( ?X ), q( ?Y ), SUBTRACT( ?X, ?Y, 3 )." +
			
			// Same rule with all variables
			"t( ?X, ?Y ) :- p( ?X ), q( ?Y ), SUBTRACT( ?X, ?Y, ?Z ), ?Z = 3." +
			
			// Combine all the rules
			"a( ?X, ?Y ) :- r( ?X, ?Y ), s( ?X, ?Y ), t( ?X, ?Y )." +
			
			"?- a(?X, ?Y ).";
		
       	String expectedResults = 
			"a( 4, 1 )." +
			"a( 5, 2 ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testSubtractAllVariables() throws Exception
	{
		String program =
			"p( 0 )." +
			"p( 1 )." +
			
			"q( 2 )." +
			"q( 3 )." +
			
			// Rule with operator predicate
			"r( ?Z ) :- p( ?X ), q( ?Y ), ?X - ?Y = ?Z." +

			"?- r( ?Z ).";
		
       	String expectedResults = 
			"r( -3 )." +
			"r( -2 )." +
			"r( -1 ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testSubtractConstantOperandSecond() throws Exception
	{
		String program =
			"p( 3 )." +
			"p( 4 )." +
			"p( 5 )." +
			
			"q( 1 )." +
			"q( 2 )." +
			"q( 3 )." +
			
			// Rule with operator predicate
			"r( ?X, ?Y ) :- p( ?X ), q( ?Y ), ?X - 3 = ?Y." +
			
			// Same rule with named predicate
			"s( ?X, ?Y ) :- p( ?X ), q( ?Y ), SUBTRACT( ?X, 3, ?Y )." +
			
			// Same rule with all variables
			"t( ?X, ?Y ) :- p( ?X ), q( ?Y ), SUBTRACT( ?X, ?Z, ?Y ), ?Z = 3." +
			
			// Combine both the rules
			"a( ?X, ?Y ) :- r( ?X, ?Y ), s( ?X, ?Y ), t( ?X, ?Y )." +
			
			"?- a(?X, ?Y ).";
		
       	String expectedResults = 
			"a( 4, 1 )." +
			"a( 5, 2 ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testSubtractConstantOperandFirst() throws Exception
	{
		String program =
			"p( -1 )." +
			"p( 0 )." +
			"p( 1 )." +
			
			"q( 1 )." +
			"q( 2 )." +
			"q( 3 )." +
			
			// Rule with operator predicate
			"r( ?X, ?Y ) :- p( ?X ), q( ?Y ), 3 - ?X = ?Y." +
			
			// Same rule with named predicate
			"s( ?X, ?Y ) :- p( ?X ), q( ?Y ), SUBTRACT( 3, ?X, ?Y )." +
			
			// Same rule with named predicate
			"t( ?X, ?Y ) :- p( ?X ), q( ?Y ), SUBTRACT( ?Z, ?X, ?Y ), ?Z = 3." +
			
			// Combine both the rules
			"a( ?X, ?Y ) :- r( ?X, ?Y ), s( ?X, ?Y ), t( ?X, ?Y )." +
			
			"?- a(?X, ?Y ).";
		
       	String expectedResults = 
			"a( 0, 3 )." +
			"a( 1, 2 ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check for round-off errors (see bug 1808292).
	 * @throws Exception
	 */
	public void testFloatingPointRoundOffError() throws Exception
	{
		String program =
			"a(0.5)." +
			"a(0.6)." +
			"a(4.2)." +
	
			"b(0.1)." +
	
			"c(5.0)." +
			"c(6.0)." +
			"c(42.0)." +
	
			"d(?Z) :- a(?X), b(?Y), c(?Z), ?X / ?Y = ?Z." +
	
			"?- d(?x).";

       	String expectedResults = 
       		"d(5.0)." +
       		"d(6.0)." +
       		"d(42.0).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check for round-off errors converting 'int' to 'float' (see bug 1808896). 
	 * @throws Exception
	 */
	public void testFloatingPointIntegerToFloatRoundOffError() throws Exception
	{
		String program =
			"a(2000000000)." +
			"a(2000000001)." +
	
			"c(_float(1.0))." +
	
			"e(?X) :- a(?X), c(?Y), ?X * ?Y = ?X." +
	
			"?- e(?x).";

       	String expectedResults =
			"e(2000000000)." +
			"e(2000000001).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Predict behaviour of divide by zero (infinity) and zero-divide-by-zero (indeterminate) for
	 * integer type (see bug 1808309)
	 * @throws Exception
	 */
	public void testIntegerDivideByZero() throws Exception
	{
		String program =
			"a(-1)." +
			"a(0)." +
			"a(1)." +
			"b(0)." +
			"d(?Z) :- a(?X), b(?Y), ?X / ?Y = ?Z." +
			"?-d(?X).";

       	String expectedResults = "";	// No special values for indeterminate or +/- infinity

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Predict behaviour of divide by zero (infinity) and zero-divide-by-zero (indeterminate) for
	 * type 'double'
	 * (see bug 1808309)
	 * @throws Exception
	 */
	public void testFloatingPointDivideByZero() throws Exception
	{
		String program =
			"a(-1.0)." +
			"a(0.0)." +
			"a(1.0)." +
			"b(0.0)." +
			"d(?Z) :- a(?X), b(?Y), ?X / ?Y = ?Z." +
			"?-d(?X).";

       	String expectedResults = "";	// NaN and +/- infinity should not be produced.

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Check for round-off errors and proper +/- comparison (see bug 1832140).
	 * @throws Exception
	 */
	public void testFloatingPointComparison() throws Exception
	{
		String program =
			"a(0.0)." +
			"a(1.0000000000001)." +
			"a(_float(2.000001))." +

			"b(-0.0)." +
			"b(1.0000000000002)." +
			"b(_float(2.000002))." +
			
			"p(?x) :- a(?x), b(?y), ! ?x < ?y, ! ?x > ?y." +
	
			"?- p(?x).";

       	String expectedResults = 
       		"p(0.0)." +
       		"p(1.0000000000001)." +
       		"p(_float(2.000001)).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}
}
