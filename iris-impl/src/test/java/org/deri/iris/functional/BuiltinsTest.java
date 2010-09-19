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

import org.deri.iris.RuleUnsafeException;
import org.deri.iris.compiler.ParserException;

/**
 * Tests for IRIS built-in predicates.
 */
public class BuiltinsTest extends TestCase
{
	/**
	 * Test that the evaluation strategy correctly handles relations with
	 * arguments of different data types occurring at the same position.
	 * 
	 * Need to check that = is always false when comparing arguments
	 * of non-compatible data types, i.e.
	 * any numeric can be tested against any other numeric, otherwise
	 * the datatypes must exactly match.
	 */
	public void testMixedDataTypes_IncompatibleDataTypesNeverEqual() throws Exception
	{
		String program =
			"p( 'a', 'b' )." +
			"p( 'c', 7 )." +
			"p( 1.23, _datetime(2000,1,1,2,2,2) )." +
			
			"q( ?X, ?Y ) :- p( ?X, ?Y ), ?Y = 7 ." +
			
			"?- q(?X, ?Y ).";
		
       	String expectedResults = 
			"q( 'c', 7 ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Test that the evaluation strategy correctly handles relations with
	 * arguments with different data types occurring at the same position.
	 * 
	 * Need to check that != is always true when comparing arguments
	 * of non-compatible data types, i.e.
	 * any numeric can be tested against any other numeric, otherwise
	 * the datatypes must exactly match.
	 */
	public void testMixedDataTypes_IncompatibleDataTypesAlwaysNotEqual() throws Exception
	{
		String program =
			"p( 'a', 'b' )." +
			"p( 'c', 7 )." +
			"p( 1.23, _datetime(2000,1,1,2,2,2) )." +
			
			"q( ?X, ?Y ) :- p( ?X, ?Y ), ?Y != 7 ." +
			
			"?- q( ?X, ?Y ).";
		
       	String expectedResults = 
			"q( 'a', 'b' )." +
			"q( 1.23, _datetime(2000,1,1,2,2,2) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Test that the evaluation strategy correctly handles relations with
	 * arguments with different data types occurring at the same position.
	 * 
	 * Need to check the behaviour when using <, <=, >, >= when comparing arguments
	 * with different data types.
	 */
	public void testMixedDataTypes_ComparingDifferingDatatypes() throws Exception
	{
		String program =
			"p( 'a', 'b' )." +
			"p( 'b', 5 )." +
			"p( 'c', 1.23 )." +
			"p( 'd', _datetime(2000,1,1,2,2,2) )." +
			
			"q( ?X, ?Y ) :- p(?X, ?Y ), ?Y < 7, ?Y <= 6, ?Y > 3, ?Y >= 4 ." +
			
			"?- q( ?X, ?Y ).";
		
       	String expectedResults =
       		"q( 'b', 5 ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	private static final String mNumerics =
		"p( _decimal( 1.0 ) )." +
		"p( _double( 2.0 ) )." +
		"p( _float( 3.0 ) )." +
		"p( _integer( 4 ) ).";
		
	private static final String mAllDataTypes =
		mNumerics +
		
		"p( _string( 'text' ) )." +
		"p( _iri( 'http://example.org/PersonOntology#Person' ) )." +
		"p( _sqname( foaf#name ) )." +
		"p( _boolean( 'true' ) )." +

		"p( _duration( 1970, 2, 2, 2, 2, 2 ) )." +
		"p( _datetime( 1980, 3, 3, 3, 3, 3 ) )." +
		"p( _date( 1990, 4, 4 ) )." +
		"p( _time( 5, 5, 5 ) )." +
		
		"p( _gyear( 1991 ) )." +
		"p( _gyearmonth( 1992, 2 ) )." +
		"p( _gmonth( 3 ) )." +
		"p( _gmonthday( 4, 4 ) )." +
		"p( _gday( 5 ) )." +
		
		"p( _hexbinary( '0FB7abcd' ) )." +
		"p( _base64binary( 'QmFycnkgQmlzaG9w' ) )." +
		"";

	/**
	 * Assert that every built in predicate functions correctly with every
	 * possible data type.
	 */
	public void testLess_AllDataTypes() throws Exception
	{
		String program = mAllDataTypes +
			"?- p(?X), ?X < 100.";
		
       	String expectedResults = mNumerics;

       	Helper.evaluateWithAllStrategies( program, expectedResults );

		program = mAllDataTypes +
			"?- p(?X), ?X < 0.";
	
		Helper.evaluateWithAllStrategies( program, "" );

		program = mAllDataTypes +
			"?- p(?X), ?X < _duration( 1970, 2, 2, 2, 2, 3 ).";

		Helper.evaluateWithAllStrategies( program, "p( _duration( 1970, 2, 2, 2, 2, 2 ) )." );

		program = mAllDataTypes +
			"?- p(?X), ?X < _datetime( 1980, 3, 3, 3, 3, 4 ).";

		Helper.evaluateWithAllStrategies( program, "p( _datetime( 1980, 3, 3, 3, 3, 3 ) )." );

		program = mAllDataTypes +
			"?- p(?X), ?X < _date( 1990, 4, 5 ).";

		Helper.evaluateWithAllStrategies( program, "p( _date( 1990, 4, 4 ) )." );

		program = mAllDataTypes +
			"?- p(?X), ?X < _time( 5, 5, 6 ).";

		Helper.evaluateWithAllStrategies( program, "p( _time( 5, 5, 5 ) )." );

		program = mAllDataTypes +
			"?- p(?X), ?X < _gyear( 1992 ).";

		Helper.evaluateWithAllStrategies( program, "p( _gyear( 1991 ) )." );

		program = mAllDataTypes +
			"?- p(?X), ?X < _gyearmonth( 1992, 3 ).";
		
		Helper.evaluateWithAllStrategies( program, "p( _gyearmonth( 1992, 2 ) )." );
		
		program = mAllDataTypes +
			"?- p(?X), ?X < _gmonth( 4 ).";
		
		Helper.evaluateWithAllStrategies( program, "p( _gmonth( 3 ) )." );
		
		program = mAllDataTypes +
			"?- p(?X), ?X < _gmonthday( 4, 5 ).";
		
		Helper.evaluateWithAllStrategies( program, "p( _gmonthday( 4, 4 ) )." );
		
		program = mAllDataTypes +
			"?- p(?X), ?X < _gday( 6 ).";
		
		Helper.evaluateWithAllStrategies( program, "p( _gday( 5 ) )." );
		
		program = mAllDataTypes +
			"?- p(?X), ?X < _hexbinary( '0FB7abce' ).";
		
		Helper.evaluateWithAllStrategies( program, "p( _hexbinary( '0FB7abcd' ) )." );
		
		program = mAllDataTypes +
			"?- p(?X), ?X < _base64binary( 'QmFycnkgQmlzaG9y' ).";
		
		Helper.evaluateWithAllStrategies( program, "p( _base64binary( 'QmFycnkgQmlzaG9w' ) )." );
	}

	/**
	 * Check that a program containing the built-in equality predicate is
	 * correctly evaluated.
	 * @throws Exception
	 */
	public void testBuiltIn_Equality() throws Exception
	{
		String program = 
 			"s(1)." +
		    "s(2)." +
		    "s(3)." +
		    
		    "s('a')." +
		    "s('b')." +
		    "s('c')." +
		    
		    "s(1.2)." +
		    "s(2.0)." +
		    "s(_float(2.0))." +

		    "s(_date(1997,2,20))." +
		    
		    "p(?X) :- s(?X), ?X = 2." +
		    "p(?X) :- s(?X), ?X = 'b'." +
		    "?- p(?X).";

		String expectedResults =
		    "p(2)." +
		    "p(2.0)." +
		    "p(_float(2.0))." +
		    "p('b').";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check that a program containing the built-in equality predicate is
	 * correctly evaluated.
	 * @throws Exception
	 */
	public void testBuiltIn_ExactEquality() throws Exception
	{
		String program = 
 			"s(1)." +
		    "s(2)." +
		    "s(3)." +
		    
		    "s('a')." +
		    "s('b')." +
		    "s('c')." +
		    
		    "s(1.2)." +
		    "s(2.0)." +
		    "s(_float(2.0))." +

		    "s(_date(1997,2,20))." +
		    
		    "p(?X) :- s(?X), EXACT_EQUAL(?X, _float(2.0))." +
		    "p(?X) :- s(?X), EXACT_EQUAL(?X, 'b')." +
		    "?- p(?X).";

		String expectedResults =
		    "p(_float(2.0))." +
		    "p('b').";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check that a program containing the built-in equality predicate is
	 * correctly evaluated.
	 * @throws Exception
	 */
	public void testBuiltIn_NotExactEquality() throws Exception
	{
		String program = 
 			"s(1)." +
		    "s(2)." +
		    "s(3)." +
		    
		    "s('a')." +
		    "s('b')." +
		    "s('c')." +
		    
		    "s(1.2)." +
		    "s(2.0)." +
		    "s(_float(2.0))." +

		    "s(_date(1997,2,20))." +
		    
		    "p(?X) :- s(?X), NOT_EXACT_EQUAL(?X, _float(2.0))." +
		    "?- p(?X).";

		String expectedResults =
 			"p(1)." +
		    "p(2)." +
		    "p(3)." +
		    
		    "p('a')." +
		    "p('b')." +
		    "p('c')." +

		    "p(1.2)." +
		    "p(2.0)." +
		    "p(_date(1997,2,20)).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Negated equality as negation as failure.
	 * correctly evaluated.
	 * @throws Exception
	 */
	public void testBuiltIn_NegatedEquality() throws Exception
	{
		String program = 
 			"s(1)." +
		    "s(2)." +
		    "s(3)." +
		    
		    "s('a')." +
		    "s('b')." +
		    "s('c')." +
		    
		    "s(1.2)." +
		    "s(2.0)." +
		    "s(_float(2.0))." +

		    "s(_date(1997,2,20))." +
		    
		    "p(?X) :- s(?X), not ?X = 2, not ?X = 'b'." +
		    "?- p(?X).";

		String expectedResults =
			"p(1)." +
		    "p(3)." +
		    "p('a')." +
		    "p('c')." +
		    "p(1.2)." +
		    "p(_date(1997,2,20)).";
		
		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check that a program containing the built-in inequality predicate is
	 * correctly evaluated.
	 * @throws Exception
	 */
	public void testBuiltIn_Inequality() throws Exception
	{
		String program = 
 			"s(1)." +
		    "s(2)." +
		    "s(3)." +
		    
		    "s('a')." +
		    "s('b')." +
		    "s('c')." +
		    
		    "s(1.2)." +
		    "s(2.0)." +
		    "s(_float(2.0))." +

		    "s(_date(1997,2,20))." +
		    
		    "p(?X) :- s(?X), ?X != 2, ?X != 'b'." +
		    "?- p(?X).";

		String expectedResults =
 			"p(1)." +
		    "p(3)." +
		    
		    "p('a')." +
		    "p('c')." +
		    
		    "p(1.2)." +

		    "p(_date(1997,2,20)).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Negated equality as negation as failure.
	 * @throws Exception
	 */
	public void testBuiltIn_NegatedInequality() throws Exception
	{
		String program = 
 			"s(1)." +
		    "s(2)." +
		    "s(3)." +
		    
		    "s('a')." +
		    "s('b')." +
		    "s('c')." +
		    
		    "s(1.2)." +
		    "s(2.0)." +
		    "s(_float(2.0))." +

		    "s(_date(1997,2,20))." +
		    
		    "p(?X) :- s(?X), not ?X != 2." +
		    "?- p(?X).";

		String expectedResults = 
		    "p(2)." +
		    "p(2.0)." +
		    "p(_float(2.0)).";		
		
		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check that a program containing the built-in predicate is
	 * correctly evaluated.
	 * @throws Exception
	 */
	public void testBuiltIn_Less() throws Exception
	{
		String program = 
			"s(1)." +
			"s(2)." +
			"s(3)." +
			"s(4)." +
			
			"p(2)." +
			"p(3)." +
			
		    "w(?X) :- s(?X), p(?Y), ?X < ?Y." +
		    "?- w(?X).";

		String expectedResults =
		    "w(1)." +
		    "w(2).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check that a program containing the built-in predicate is
	 * correctly evaluated.
	 * @throws Exception
	 */
	public void testBuiltIn_LessEqual() throws Exception
	{
		String program = 
			"s(1)." +
			"s(2)." +
			"s(3)." +
			"s(4)." +
			
			"p(2)." +
			"p(3)." +
			
		    "w(?X) :- s(?X), p(?Y), ?X <= ?Y." +
		    "?- w(?X).";

		String expectedResults =
		    "w(1)." +
		    "w(2)." +
		    "w(3).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check that a program containing the built-in predicate is
	 * correctly evaluated.
	 * @throws Exception
	 */
	public void testBuiltIn_Greater() throws Exception
	{
		String program = 
			"s(1)." +
			"s(2)." +
			"s(3)." +
			"s(4)." +
			
			"p(2)." +
			"p(3)." +
			
		    "w(?X) :- s(?X), p(?Y), ?X > ?Y." +
		    "?- w(?X).";

		String expectedResults =
		    "w(3)." +
		    "w(4).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check that a program containing the built-in predicate is
	 * correctly evaluated.
	 * @throws Exception
	 */
	public void testBuiltIn_GreaterEqual() throws Exception
	{
		String program = 
			"s(1)." +
			"s(2)." +
			"s(3)." +
			"s(4)." +
			
			"p(2)." +
			"p(3)." +
			
		    "w(?X) :- s(?X), p(?Y), ?X >= ?Y." +
		    "?- w(?X).";

		String expectedResults =
		    "w(2)." +
		    "w(3)." +
		    "w(4).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check that a program containing the built-in predicate is
	 * correctly evaluated.
	 * @throws Exception
	 */
	public void testBuiltIn_Add() throws Exception
	{
		String program = 
			"s(1)." +
			
			"p(2)." +
			"p(3)." +
			
		    "w(?X,?Z) :- s(?X), p(?Y), ?X + ?Y = ?Z." +
		    "?- w(?X,?Z).";

		String expectedResults =
		    "w(1, 3)." +
		    "w(1, 4).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Mixed data type add - compute target.
	 * @throws Exception
	 */
	public void testBuiltIn_Add_MixedDataTypesComputeTarget() throws Exception
	{
		String program = 
			"s(1)." +
			"s('a')." +
			
			"p(2)." +
			"p(3)." +
			"p('a')." +
			
		    "w(?Z) :- s(?X), p(?Y), ?X + ?Y = ?Z." +
		    "?- w(?Z).";

		String expectedResults =
		    "w(3)." +
		    "w(4).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Mixed data type add - compute target.
	 * @throws Exception
	 */
	public void testBuiltIn_Add_MixedDataTypesValidateTarget() throws Exception
	{
		String program = 
			"s(1)." +
			"s('a')." +
			
			"p(2)." +
			"p(3)." +
			"p('a')." +
			
			"q(4)." +
			"q('c')." +
			
		    "w(?Z) :- s(?X), p(?Y), q(?Z), ?X + ?Y = ?Z." +
		    "?- w(?Z).";

		String expectedResults =
		    "w(4).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Mixed data type add - compute target.
	 * @throws Exception
	 */
	public void testBuiltIn_NegatedAdd_MixedDataTypes() throws Exception
	{
		String program =
			"p( 2, 3, 5 )." +
			"p( 3, 3, 6 )." +
			"p( 2, 'a', 0 )." +
			"p( 'b', 3, 1 )." +
			
		    "w(?Z) :- p( ?X, ?Y, ?Z ), not ?X + ?Y = ?Z." +
		    "?- w(?Z).";

		String expectedResults =
			"w(0)." +
			"w(1).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check that a program containing the built-in predicate is
	 * correctly evaluated.
	 * @throws Exception
	 */
	public void testBuiltIn_Subtract() throws Exception
	{
		String program = 
			"s(1)." +
			
			"p(2)." +
			"p(3)." +
			
		    "w(?X,?Z) :- s(?X), p(?Y), ?X - ?Y = ?Z." +
		    "?- w(?X,?Z).";

		String expectedResults =
		    "w(1, -1)." +
		    "w(1, -2).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Mixed data type add - compute target.
	 * @throws Exception
	 */
	public void testBuiltIn_Subtract_MixedDataTypesComputeTarget() throws Exception
	{
		String program = 
			"s(1)." +
			"s('a')." +
			
			"p(2)." +
			"p(3)." +
			"p('a')." +
			
		    "w(?Z) :- s(?X), p(?Y), ?X - ?Y = ?Z." +
		    "?- w(?Z).";

		String expectedResults =
		    "w(-1)." +
		    "w(-2).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Mixed data type add - compute target.
	 * @throws Exception
	 */
	public void testBuiltIn_Subtract_MixedDataTypesValidateTarget() throws Exception
	{
		String program = 
			"s(1)." +
			"s('a')." +
			
			"p(2)." +
			"p(3)." +
			"p('a')." +
			
			"q(-2)." +
			"q('c')." +
			
		    "w(?Z) :- s(?X), p(?Y), q(?Z), ?X - ?Y = ?Z." +
		    "?- w(?Z).";

		String expectedResults =
		    "w(-2).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Mixed data type add - compute target.
	 * @throws Exception
	 */
	public void testBuiltIn_NegatedSubtract_MixedDataTypes() throws Exception
	{
		String program =
			"p( 2, 3, -1 )." +
			"p( 3.3, 3.0, 0.3 )." +
			"p( 2, 'a', 0 )." +
			"p( 'b', 3, 1 )." +
			
		    "w(?Z) :- p( ?X, ?Y, ?Z ), not ?X - ?Y = ?Z." +
		    "?- w(?Z).";

		String expectedResults =
			"w(0)." +
			"w(1).";

		Helper.evaluateWithAllStrategies( program, expectedResults );

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check that a program containing the built-in predicate is
	 * correctly evaluated.
	 * @throws Exception
	 */
	public void testBuiltIn_Multiply() throws Exception
	{
		String program = 
			"s(7)." +
			
			"p(2)." +
			"p(3)." +
			
		    "w(?X,?Z) :- s(?X), p(?Y), ?X * ?Y = ?Z." +
		    "?- w(?X,?Z).";

		String expectedResults =
		    "w(7, 14)." +
		    "w(7, 21).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Mixed data type add - compute target.
	 * @throws Exception
	 */
	public void testBuiltIn_Multiply_MixedDataTypesComputeTarget() throws Exception
	{
		String program = 
			"s(4)." +
			"s('a')." +
			
			"p(2)." +
			"p(3)." +
			"p('a')." +
			
		    "w(?Z) :- s(?X), p(?Y), ?X * ?Y = ?Z." +
		    "?- w(?Z).";

		String expectedResults =
		    "w(8)." +
		    "w(12).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Mixed data type add - compute target.
	 * @throws Exception
	 */
	public void testBuiltIn_Multiply_MixedDataTypesValidateTarget() throws Exception
	{
		String program = 
			"s(3)." +
			"s('a')." +
			
			"p(2)." +
			"p(3)." +
			"p('a')." +
			
			"q(6)." +
			"q('c')." +
			
		    "w(?Z) :- s(?X), p(?Y), q(?Z), ?X * ?Y = ?Z." +
		    "?- w(?Z).";

		String expectedResults =
		    "w(6).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Mixed data type add - compute target.
	 * @throws Exception
	 */
	public void testBuiltIn_NegatedMultiply_MixedDataTypes() throws Exception
	{
		String program =
			"p( 2, 3, 6 )." +
			"p( 3.1, 3, 9.3 )." +
			"p( 2, 'a', 0 )." +
			"p( 'b', 3, 1 )." +
			
		    "w(?Z) :- p( ?X, ?Y, ?Z ), not ?X * ?Y = ?Z." +
		    "?- w(?Z).";

		String expectedResults =
			"w(0)." +
			"w(1).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check that a program containing the built-in predicate is
	 * correctly evaluated.
	 * @throws Exception
	 */
	public void testBuiltIn_Modulus_Integer() throws Exception
	{
		String program = 
			"s(7)." +
			
			"p(1)." +
			"p(2)." +
			"p(3)." +
			"p(4)." +
			"p(5)." +
			"p(6)." +
			"p(7)." +
			"p(8)." +
			"p(9)." +
			
		    "w(?Z) :- s(?X), p(?Y), MODULUS(?X, ?Y, ?Z)." +
		    "?- w(?Z).";

		String expectedResults =
		    "w(0)." +
		    "w(1)." +
		    "w(3)." +
		    "w(2)." +
		    "w(7).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Check that a program containing the built-in predicate is
	 * correctly evaluated.
	 * @throws Exception
	 */
// Problem! There are round-off errors in Tuple, which is hashes its entire contents
//	public void testBuiltIn_Modulus_FloatingPoint() throws Exception
//	{
//		String program = 
//			"s(0.7)." +
//			
//			"p(0.1)." +
//			"p(0.2)." +
//			"p(0.3)." +
//			"p(0.4)." +
//			"p(0.5)." +
//			"p(0.6)." +
//			"p(0.7)." +
//			"p(0.8)." +
//			"p(0.9)." +
//			
//		    "w(?Z) :- s(?X), p(?Y), MODULUS(?X, ?Y, ?Z)." +
//		    "?- w(?Z).";
//
//		String expectedResults =
//		    "w(0.0)." +
//		    "w(0.1)." +
//		    "w(0.3)." +
//		    "w(0.2)." +
//		    "w(0.7).";
//
//		Helper.evaluateWithAllStrategies( program, expectedResults );
//	}

	/**
	 * Mixed data type add - compute target.
	 * @throws Exception
	 */
	public void testBuiltIn_Modulus_MixedDataTypesComputeTarget() throws Exception
	{
		String program = 
			"s(9)." +
			"s('a')." +
			
			"p(3)." +
			"p(7)." +
			"p('a')." +
			
		    "w(?Z) :- s(?X), p(?Y), ?X % ?Y = ?Z." +
		    "?- w(?Z).";

		String expectedResults =
		    "w(0)." +
		    "w(2).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Mixed data type add - compute target.
	 * @throws Exception
	 */
	public void testBuiltIn_Modulus_MixedDataTypesValidateTarget() throws Exception
	{
		String program = 
			"s(9)." +
			"s('a')." +
			
			"p(3)." +
			"p(7)." +
			"p('a')." +
			
			"q(2)." +
			"q('c')." +
			
		    "w(?Z) :- s(?X), p(?Y), q(?Z), ?X % ?Y = ?Z." +
		    "?- w(?Z).";

		String expectedResults =
		    "w(2).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Mixed data type negated built-in.
	 * @throws Exception
	 */
	public void testBuiltIn_NegatedModulus_MixedDataTypes() throws Exception
	{
		String program =
			"p( 17, 5, 2 )." +
			"p( 12, 8, 4 )." +
			"p( 'b', 3, 0 )." +
			"p( 12, 'a', 1 )." +
			"p( 3, 8, 2 )." +
			
		    "w(?Z) :- p( ?X, ?Y, ?Z ), not ?X % ?Y = ?Z." +
		    "?- w(?Z).";

		String expectedResults =
			"w(0)." +
			"w(1)." +
			"w(2).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check that a program containing the built-in predicate is
	 * correctly evaluated.
	 * @throws Exception
	 */
	public void testBuiltIn_Divide() throws Exception
	{
		String program = 
			"s(12)." +
			
			"p(2)." +
			"p(3)." +
			
		    "w(?X,?Z) :- s(?X), p(?Y), ?X / ?Y = ?Z." +
		    "?- w(?X,?Z).";

		String expectedResults =
		    "w(12, 6)." +
		    "w(12, 4).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Mixed data type add - compute target.
	 * @throws Exception
	 */
	public void testBuiltIn_Divide_MixedDataTypesComputeTarget() throws Exception
	{
		String program = 
			"s(12)." +
			"s('a')." +
			
			"p(2)." +
			"p(3)." +
			"p('a')." +
			
		    "w(?Z) :- s(?X), p(?Y), ?X / ?Y = ?Z." +
		    "?- w(?Z).";

		String expectedResults =
		    "w(6)." +
		    "w(4).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Mixed data type add - compute target.
	 * @throws Exception
	 */
	public void testBuiltIn_Divide_MixedDataTypesValidateTarget() throws Exception
	{
		String program = 
			"s(12)." +
			"s('a')." +
			
			"p(2)." +
			"p(3)." +
			"p('a')." +
			
			"q(6)." +
			"q('c')." +
			
		    "w(?Z) :- s(?X), p(?Y), q(?Z), ?X / ?Y = ?Z." +
		    "?- w(?Z).";

		String expectedResults =
		    "w(6).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Mixed data type add - compute target.
	 * @throws Exception
	 */
	public void testBuiltIn_NegatedDivide_MixedDataTypes() throws Exception
	{
		String program =
			"p( 12, 3, 4 )." +
			"p( 12, 4, 3 )." +
			"p( 12, 'a', 1 )." +
			"p( 'b', 3, 0 )." +
			
		    "w(?Z) :- p( ?X, ?Y, ?Z ), not ?X / ?Y = ?Z." +
		    "?- w(?Z).";

		String expectedResults =
			"w(0)." +
			"w(1).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Test for correct mixed data type behaviour.
	 * @throws Exception
	 */
	public void testBuiltIn_Less_MixedDataTypes() throws Exception
	{
		String program = 
			"s(1,2)." +
			"s(2,2)." +
			"s(3,2)." +
			"s(3,4)." +
			"s(3,'a')." +
			"s('b',4)." +
			
		    "t(?X, ?Y) :- s(?X, ?Y), ?X < ?Y." +
		    "?- t(?X, ?Y).";

		String expectedResults =
			"t(1,2)." +
			"t(3,4).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	/**
	 * Test for correct NAF behaviour.
	 * @throws Exception
	 */
	public void testBuiltIn_Less_NegationAsFailure() throws Exception
	{
		String program = 
			"s(1,2)." +
			"s(2,2)." +
			"s(3,2)." +
			"s(3,4)." +
			"s(3,'a')." +
			"s('b',4)." +
			
		    "t(?X, ?Y) :- s(?X, ?Y), not ?X < ?Y." +
		    "?- t(?X, ?Y).";

		String expectedResults =
			"t(2,2)." +
			"t(3,2)." +
			"t(3,'a')." +
			"t('b',4).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Test for correct mixed data type behaviour.
	 * @throws Exception
	 */
	public void testBuiltIn_Greater_MixedDataTypes() throws Exception
	{
		String program = 
			"s(1,2)." +
			"s(2,2)." +
			"s(3,2)." +
			"s(3,4)." +
			"s(3,'a')." +
			"s('b',4)." +
			
		    "t(?X, ?Y) :- s(?X, ?Y), ?X > ?Y." +
		    "?- t(?X, ?Y).";

		String expectedResults =
			"t(3,2).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	/**
	 * Test for correct NAF behaviour.
	 * @throws Exception
	 */
	public void testBuiltIn_Greater_NegationAsFailure() throws Exception
	{
		String program = 
			"s(1,2)." +
			"s(2,2)." +
			"s(3,2)." +
			"s(3,4)." +
			"s(3,'a')." +
			"s('b',4)." +
			
		    "t(?X, ?Y) :- s(?X, ?Y), not ?X > ?Y." +
		    "?- t(?X, ?Y).";

		String expectedResults =
			"t(1,2)." +
			"t(2,2)." +
			"t(3,4)." +
			"t(3,'a')." +
			"t('b',4).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Test for correct mixed data type behaviour.
	 * @throws Exception
	 */
	public void testBuiltIn_LessEquals_MixedDataTypes() throws Exception
	{
		String program = 
			"s(1,2)." +
			"s(2,2)." +
			"s(3,2)." +
			"s(3,4)." +
			"s(3,'a')." +
			"s('b',4)." +
			
		    "t(?X, ?Y) :- s(?X, ?Y), ?X <= ?Y." +
		    "?- t(?X, ?Y).";

		String expectedResults =
			"t(1,2)." +
			"t(2,2)." +
			"t(3,4).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	/**
	 * Test for correct NAF behaviour.
	 * @throws Exception
	 */
	public void testBuiltIn_LessEquals_NegationAsFailure() throws Exception
	{
		String program = 
			"s(1,2)." +
			"s(2,2)." +
			"s(3,2)." +
			"s(3,4)." +
			"s(3,'a')." +
			"s('b',4)." +
			
		    "t(?X, ?Y) :- s(?X, ?Y), not ?X <= ?Y." +
		    "?- t(?X, ?Y).";

		String expectedResults =
			"t(3,2)." +
			"t(3,'a')." +
			"t('b',4).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Test for correct mixed data type behaviour.
	 * @throws Exception
	 */
	public void testBuiltIn_GreaterEquals_MixedDataTypes() throws Exception
	{
		String program = 
			"s(1,2)." +
			"s(2,2)." +
			"s(3,2)." +
			"s(3,4)." +
			"s(3,'a')." +
			"s('b',4)." +
			
		    "t(?X, ?Y) :- s(?X, ?Y), ?X >= ?Y." +
		    "?- t(?X, ?Y).";

		String expectedResults =
			"t(2,2)." +
			"t(3,2).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Test for correct NAF behaviour.
	 * @throws Exception
	 */
	public void testBuiltIn_GreaterEquals_NegationAsFailure() throws Exception
	{
		String program = 
			"s(1,2)." +
			"s(2,2)." +
			"s(3,2)." +
			"s(3,4)." +
			"s(3,'a')." +
			"s('b',4)." +
			
		    "t(?X, ?Y) :- s(?X, ?Y), not ?X >= ?Y." +
		    "?- t(?X, ?Y).";

		String expectedResults =
			"t(1,2)." +
			"t(3,4)." +
			"t(3,'a')." +
			"t('b',4).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Test for correct behaviour.
	 * @throws Exception
	 */
	public void testBuiltIn_SameType() throws Exception
	{
		String program = 
			"s(1)." +
			"s('s')." +
			"s(_time(12,30,0))." +
			"s(_date(2007,2,02))." +
			"t(3)." +
			"t('t')." +
			"t(_time(14,45,30))." +
			"t(_float(12.3))." +
			
		    "p(?X, ?Y) :- s(?X), t(?Y), SAME_TYPE(?X, ?Y)." +
		    "?- p(?X, ?Y).";

		String expectedResults =
			"p(1,3)." +
			"p('s','t')." +
			"p(_time(12,30,0),_time(14,45,30)).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Test for correct behaviour.
	 * @throws Exception
	 */
	public void testBuiltIn_NegatedSameType() throws Exception
	{
		String program = 
			"s1(1)." +
			"s1('s')." +
			"t1(3)." +
			"t1('t')." +
			
		    "p(?X, ?Y) :- s1(?X), t1(?Y), not SAME_TYPE(?X, ?Y)." +
		    "?- p(?X, ?Y).";

		String expectedResults =
			"p(1,'t')." +
			"p('s',3).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testOnlyEquality_OneVariableInRuleBodyAndHead() throws Exception
	{
		String program = 
		    "p(?X) :- ?X=3." +
		    "?- p(?X).";

		String expectedResults =
			"p(3).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testOnlyEquality_NoVariablesInRuleHeadOrBody() throws Exception
	{
		String program = 
		    "p :- 3=3." +
		    "?- p.";

		String expectedResults =
			"p.";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testOnlyEquality_OneVariableInRuleBodyNoVariablesInRuleHead() throws Exception
	{
		String program = 
		    "p :- ?X=3." +
		    "?- p.";

		String expectedResults =
			"p.";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testOnlyAddInRuleBody() throws Exception
	{
		String program = 
		    "p(?X) :- 1 + 2 = ?X." +
		    "?- p(?X).";

		String expectedResults =
			"p(3).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testOnlyAddInRuleBodyNoVariables() throws Exception
	{
		String program = 
		    "p :- 1 + 2 = 3." +
		    "?- p.";

		String expectedResults =
			"p.";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testTrue() throws Exception
	{
		String program = 
		    "p(?X) :- ?X = 1, TRUE." +
		    "?- p(?X).";

		String expectedResults =
			"p(1).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testFalse() throws Exception
	{
		String program = 
		    "p(?X) :- ?X = 1, ! FALSE." +
		    "?- p(?X).";

		String expectedResults =
			"p(1).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testRegex() throws Exception
	{
		String program =
			"p(2)." +
			"p(_float(3.3))." +

			"p('aaaaa')." +
			"p('aaaab')." +
			"p('aaabb')." +
			
			"p('BAZ')." +
			"p('AaA')." +
			
			"p('Hello this is a sentence that end with milk')." +

			"a_b(?x) :- p(?x), REGEX(?x, 'a*b?')." +
		    "upper(?x) :- p(?x), REGEX(?x, '[A-Z]*')." +
		    "ends_milk(?x) :- p(?x), REGEX(?x, '.*milk').";
		    
		Helper.evaluateWithAllStrategies( program + "?- a_b(?X).", "a_b('aaaaa').a_b('aaaab')." );
		Helper.evaluateWithAllStrategies( program + "?- upper(?X).", "upper('BAZ')." );
		Helper.evaluateWithAllStrategies( program + "?- ends_milk(?X).", "ends_milk('Hello this is a sentence that end with milk')." );
	}

	public void testRegexIllegalPattern() throws Exception
	{
		String program =
			"p(?x) :- p(?x), REGEX(?x, 2).";
		
		Helper.checkFailureWithAllStrategies( program, ParserException.class );
	}

	public void testUnsafeAdd_UnboundVariableAppearsTwice() throws Exception
	{
		String program = 
		    "p(?X) :- ?X + ?X = 2." +
		    "?- p(?X).";

		Helper.checkFailureWithNaive( program, RuleUnsafeException.class );
		Helper.checkFailureWithSemiNaive( program, RuleUnsafeException.class );
	}
}
