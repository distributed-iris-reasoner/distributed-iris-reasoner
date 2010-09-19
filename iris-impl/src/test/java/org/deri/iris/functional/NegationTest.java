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

import org.deri.iris.ProgramNotStratifiedException;
import org.deri.iris.RuleUnsafeException;

public class NegationTest extends TestCase
{
	/**
	 * Assert that evaluations of logic programs containing globally stratified
	 * negated subgoals give the correct results.
	 * @throws Exception 
	 */
	public void testGloballyStratified1() throws Exception
	{
		String program = 
 			"s('d')." +
		    "s('b')." +
		    "s('a')." +
		    "s('q')." +
		    
		    "r('d')." +
		    "r('c')." +
		    
		    "p('b')." +
		    "p('e')." +
		    
		    "t('a')." +
		    
		    "q(?X) :- s(?X), not p(?X)." +
		    "p(?X) :- r(?X)." +
		    "r(?X) :- t(?X)." +
		    "?- q(?X).";
		
		String expectedResults = "q('q').";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * A logic program with stratified negation.
	 * @throws Exception
	 */
	public void testGloballyStratified2() throws Exception
	{
    	String program = 
 			"u('d')." +
		    "u('b')." +
		    "u('a')." +
		    "u('q')." +
		    
		    "s('d')." +
		    "s('c')." +
		    
		    "p('b')." +
		    "p('e')." +
		    
		    "q('a')." +
		    
		    "p(?X) :- q(?X), not r(?X)." +
		    "r(?X) :- s(?X), not t(?X)." +
		    "t(?X) :- u(?X)." +
		    "?- p(?X).";
        	
       	String expectedResults = 
		    "p('a')." +
		    "p('b')." +
		    "p('e').";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * This test is to ensure that both rules are propely stratified. The rule with the empty body
	 * could be in stratum 0, but the second rule means it must be one higher than predicate 's'.
	 * @throws Exception
	 */
	public void testGloballyStratifiedEmptyRuleBody() throws Exception
	{
    	String program = 
			"p('a'):-." +
			"p(?X) :- r(?X), not s(?Y)." +
			"?- p(?X).";
        	
       	String expectedResults = 
		    "p('a').";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * This test makes sure that sub-goals are evaluated in the correct order,
	 * i.e. positive literals first, even when the negative literal appears first. 
	 * @throws Exception
	 */
	public void testNegatedSubGoalFirst() throws Exception
	{
    	String program = 
		    "r('a', 'a')." +
		    "r('b', 'a')." +
		    
		    "s('a', 'a')." +
		    "s('b', 'b')." +
		    
		    "p(?X) :- not s(?X, 'a'), r(?X, ?Y)." +
		    "?- p(?X).";
        	
       	String expectedResults = 
		    "p('b').";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Test stratification algorithm.
	 */
	public void testIsStratified1() throws Exception
	{
		String program =
			"p( ?X ) :- r( ?X ), not q(?X).";
		
       	Helper.evaluateWithAllStrategies( program, "" );
	}

	/**
	 * Test stratification algorithm.
	 */
	public void testIsStratified2() throws Exception
	{
		String program =
			"p1( ?X ) :- r( ?X ), not q(?X)." +
			"p2( ?X ) :- r( ?X ), not q(?X).";
		
       	Helper.evaluateWithAllStrategies( program, "" );
	}

	/**
	 * Test stratification algorithm.
	 */
	public void testIsStratified3() throws Exception
	{
		String program =
			"p1( ?X ) :- r( ?X ), not q(?X)." +
			"p2( ?X ) :- p1( ?X ), not q(?X).";
		
       	Helper.evaluateWithAllStrategies( program, "" );
	}

	/**
	 * Test stratification algorithm.
	 */
	public void testIsStratified4() throws Exception
	{
		String program =
			"p1( ?X ) :- r( ?X ), not q(?X)." +
			"p2( ?X ) :- r( ?X ), not p1(?X).";
		
       	Helper.evaluateWithAllStrategies( program, "" );
	}

	/**
	 * Test stratification algorithm.
	 */
	public void testIsStratified5() throws Exception
	{
		String program =
			"p1( ?X ) :- r( ?X ),not q(?X)." +
			"p2( ?X ) :- r( ?X ),not q(?X)." +
		
			"p3( ?X ) :- s(?X), not p1( ?X )." +
			"p4( ?X ) :- s(?X), not p2( ?X )." +
			
			"p5( ?X ) :- p3(?X)." +
			"p6( ?X ) :- p4(?X)." +
			"p7( ?X ) :- p6(?X).";
		
       	Helper.evaluateWithAllStrategies( program, "" );
	}

	/**
	 * Test stratification algorithm.
	 */
	public void testIsStratified6() throws Exception
	{
		String program =
			"p( ?X ) :- r( ?X ), s(?X)." +
			"p( ?X ) :- r( ?X ), t(?X)." +
			"p( ?X ) :- s( ?X ), t(?X)." +
			"p( ?X ) :- not u( ?X ), t(?X)." +
			"p( ?X ) :- s(?X), u( ?X )." +
			
			"u( ?X ) :- s(?X)." +
			"u( ?X ) :- t(?X)." +
			"u( ?X ) :- v(?X).";
		
       	Helper.evaluateWithAllStrategies( program, "" );
	}

	/**
	 * Test stratification algorithm.
	 */
	public void testNotStratified1()
	{
		String program =
			"p( ?X ) :- r( ?X ),not q(?X)." +
			"r( ?X ) :- s( ?X ),not p(?X).";
		
		Helper.checkFailureWithNaive( program, ProgramNotStratifiedException.class );
		Helper.checkFailureWithSemiNaive( program, ProgramNotStratifiedException.class );
	}

	/**
	 * Test stratification algorithm.
	 */
	public void testNotStratified2()
	{
		String program =
			"p( ?X ) :- r( ?X ), not q(?X)." +
			"q( ?X ) :- r( ?X ), p(?X).";
		
		Helper.checkFailureWithNaive( program, ProgramNotStratifiedException.class );
		Helper.checkFailureWithSemiNaive( program, ProgramNotStratifiedException.class );
	}

	/**
	 * Test stratification algorithm.
	 */
	public void testNotStratified3()
	{
		String program =
			"p1( ?X ) :- r( ?X ),not q(?X)." +
			"p2( ?X ) :- r( ?X ),not q(?X)." +
		
			"p3( ?X ) :- s(?X), not p1( ?X )." +
			"p4( ?X ) :- s(?X), not p2( ?X )." +
			
			"p5( ?X ) :- p3(?X)." +
			"p6( ?X ) :- p4(?X)." +
			
			"p7( ?X ) :- t(?X), not p6(?X)." +
			"p6( ?X ) :- t(?X), p7(?X).";
			
		Helper.checkFailureWithNaive( program, ProgramNotStratifiedException.class );
		Helper.checkFailureWithSemiNaive( program, ProgramNotStratifiedException.class );
	}

	/**
	 * Test stratification algorithm.
	 */
	public void testNotStratifiedMultipleRulesWithSameHeadPredicate()
	{
		String program =
			"p( ?X ) :- r( ?X ), s(?X)." +
			"p( ?X ) :- r( ?X ), t(?X)." +
			"p( ?X ) :- s( ?X ), t(?X)." +
			"p( ?X ) :- not u( ?X ), t(?X)." +
			"p( ?X ) :- s(?X), u( ?X )." +
			"p( ?X ) :- s(?X), w( ?X )." +
			
			"u( ?X ) :- s(?X)." +
			"u( ?X ) :- w(?X)." +
			"u( ?X ) :- t(?X), not p(?X)." +
			"u( ?X ) :- x(?X)." +
			"u( ?X ) :- v(?X).";
			
		Helper.checkFailureWithNaive( program, ProgramNotStratifiedException.class );
		Helper.checkFailureWithSemiNaive( program, ProgramNotStratifiedException.class );
	}

	/**
	 * Test stratification algorithm.
	 */
	public void testNotStratifiedLongCyclicDependancy()
	{
		String program =
			"p1( ?X ) :- r( ?X ),not p8(?X)." +
			"p2( ?X ) :- r( ?X ),not p1(?X)." +
			"p3( ?X ) :- r( ?X ),not p2(?X)." +
			"p4( ?X ) :- r( ?X ),not p3(?X)." +
			"p5( ?X ) :- r( ?X ),not p4(?X)." +
			"p6( ?X ) :- r( ?X ),not p5(?X)." +
			"p7( ?X ) :- r( ?X ),not p6(?X)." +
			"p8( ?X ) :- r( ?X ),not p7(?X).";
			
		Helper.checkFailureWithNaive( program, ProgramNotStratifiedException.class );
		Helper.checkFailureWithSemiNaive( program, ProgramNotStratifiedException.class );
	}

	/**
	 * Simple negation of a builtin binary predicate.
	 * @throws Exception
	 */
    public void testNegatedLess() throws Exception
    {
    	String program = 
    		"r(0)." +
    		"r(1)." +
    		"r(2)." +
    		"r(3)." +
    		"r(4)." +
    		
    		"v(?X) :- r(?X), not  ?X > 2." +
    		"w(?X) :- r(?X),      ?X > 2.";
    	
       	Helper.evaluateWithAllStrategies( program + "?- v(?X).", "v(0).v(1).v(2)." );
       	Helper.evaluateWithAllStrategies( program + "?- w(?X).", "w(3).w(4)." );
    }

	/**
	 * Simple negation of a builtin unary predicate.
	 * @throws Exception
	 */
    public void testNegatedIsString() throws Exception
    {
    	String program = 
    		"r(1)." +
    		"r(2)." +
    		"r('a')." +
    		"r('b')." +
    		
    		"v(?X) :- r(?X), not  IS_STRING( ?X )." +
    		"w(?X) :- r(?X),      IS_STRING( ?X ).";

       	Helper.evaluateWithAllStrategies( program + "?- v(?X).", "v(1).v(2)." );
       	Helper.evaluateWithAllStrategies( program + "?- w(?X).", "w('a').w('b')." );
    }

	/**
	 * Simple negation of a builtin unary predicate.
	 * @throws Exception
	 */
    public void testNegatedAdd() throws Exception
    {
    	String program = 
    		"r(1)." +
    		"r(2)." +
    		"r(3)." +
    		"r(4)." +
    		
    		"s(5)." +
    		"s(6)." +
    		"s(7)." +
    		"s(8)." +
    		
    		"v(?X,?Y) :- r(?X), s(?Y),     ?X + ?Y = 7." +
    		"w(?X,?Y) :- r(?X), s(?Y), not ?X + ?Y = 7.";
        	
       	String expectedResults = 
    	    "v(1,6)." +
    	    "v(2,5).";
    	    
       	Helper.evaluateWithAllStrategies( program + "?- v(?X,?Y).", expectedResults );

       	expectedResults = 
    	    "w(1,5)." +
    	    "w(1,7)." +
    	    "w(1,8)." +
    	    "w(2,6)." +
    	    "w(2,7)." +
    	    "w(2,8)." +
    	    "w(3,5)." +
    	    "w(3,6)." +
    	    "w(3,7)." +
    	    "w(3,8)." +
    	    "w(4,5)." +
    	    "w(4,6)." +
    	    "w(4,7)." +
    	    "w(4,8).";
    
       	Helper.evaluateWithAllStrategies( program + "?- w(?X,?Y).", expectedResults );
    }

	public void testLocallyStratified_SelfDependency() throws Exception
	{
		String program = 
 			"s(1)." +
		    "s(2)." +
		    "s(3)." +
		    "s(4)." +
		    
		    "p('b', 2)." +
		    "p('b', 4)." +
		    
		    "p('a', ?X) :- s(?X), not p('b', ?X)." +
		    "?- p(?X,?Y).";
		
		String expectedResults =
		    "p('b', 2)." +
		    "p('b', 4)." +
			"p('a', 1)." +
			"p('a', 3).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testLocallyStratified_SelfDependency_NotEqualFromRuleBody() throws Exception
	{
		String program = 
 			"s(1)." +
		    "s(2)." +
		    "s(3)." +
		    "s(4)." +
		    
		    "p('b', 2)." +
		    "p('b', 4)." +
		    
		    "p(?Y, ?X) :- s(?X), not p('b', ?X), p(?Y, ?Z), ?Y != 'b'." +
		    "?- p(?X,?Y).";
		
		String expectedResults =
		    "p('b', 2)." +
		    "p('b', 4).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testLocallyStratified_SelfDependency_NotEqualFromRuleBody2() throws Exception
	{
		String program = 
 			"s(1)." +
		    "s(2)." +
		    "s(3)." +
		    "s(4)." +
		    
		    "p('b', 2)." +
		    "p('b', 4)." +
		    
		    "p(?Y, ?X) :- s(?X), not p('b', ?X), p(?Y, ?Z), not ?Y >= 'b'." +
		    "?- p(?X,?Y).";
		
		String expectedResults =
		    "p('b', 2)." +
		    "p('b', 4).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testLocallyStratified_CircularDependency() throws Exception
	{
		String program = 
 			"r(1)." +
		    "r(2)." +
		    "r(3)." +
		    "r(4)." +
		    
		    "q('b', 2)." +
		    "q('b', 4)." +
		    
		    "p('a',?X) :- r(?X), not q('b',?X)." +
		    "q(?X,?Y) :- p(?X,?Y)." +

		    "?- p(?X, ?Y).";
		
		String expectedResults =
			"p('a', 1)." +
			"p('a', 3).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testLocallyStratified_DoubleNegativeDependency() throws Exception
	{
		String program = 
 			"r(1)." +
		    "r(2)." +
		    "r(3)." +
		    "r(4)." +
		    
		    "p('b', 2)." +
		    "p('b', 4)." +
		    
		    "p('a',?X) :- r(?X), not p('b',?X)." +
		    "p('c',?X) :- r(?X), not p('a',?X)." +

		    "?- p(?X, ?Y).";
		
		String expectedResults =
			"p('b', 2)." +
			"p('b', 4)." +
			"p('a', 1)." +
			"p('a', 3)." +
			"p('c', 2)." +
			"p('c', 4).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Test stratification algorithm.
	 */
	public void testNotLocallyStratified_RuleDependsOnSelfWithConstant()
	{
		String program = 
 			"r(1)." +
		    "r(2)." +
		    "r(3)." +
		    "r(4)." +
		    
		    "q('b', 2)." +
		    "q('b', 4)." +
		    
		    "p('b',?X) :- r(?X), not q('b',?X)." +
		    "q(?X,?Y) :- p(?X,?Y).";
			
		Helper.checkFailureWithNaive( program, ProgramNotStratifiedException.class );
		Helper.checkFailureWithSemiNaive( program, ProgramNotStratifiedException.class );
	}

	public void testNotLocallyStratified_RuleDependsOnSelfWithVariable()
	{
		String program = 
			"p('b', ?X) :- q(?X,?Y), ! p(?X,?Y).";
			
		Helper.checkFailureWithNaive( program, ProgramNotStratifiedException.class );
		Helper.checkFailureWithSemiNaive( program, ProgramNotStratifiedException.class );
	}
	
	/**
	 * Test stratification algorithm.
	 */
	public void testNotLocallyStratified_Recursion()
	{
		String program = 
		    "p('b',?X) :- r(?X), not q('c',?X)." +
		    "p(?X, ?Y) :- r(?X), p(?X, ?Y)." +
		    
		    "q(?X, ?Y) :- r(?X), q(?X, ?Y)." +
		    "q('c',?X) :- r(?X), not p('b',?X).";
			
		Helper.checkFailureWithNaive( program, ProgramNotStratifiedException.class );
		Helper.checkFailureWithSemiNaive( program, ProgramNotStratifiedException.class );
	}

	/**
	 * Test stratification algorithm.
	 */
	public void testLocallyStratified_OverlappingCycles() throws Exception
	{
		String program = 
			"p(1,?x) :- r(?x), not q(2,?x)." +

			"q(?x,?y) :- p(?x,?y), s(?x,?y)." +

			"s(3,?x) :- r(?x), not q(4,?x).";
			
		Helper.evaluateWithAllStrategies( program, "" );
	}

	public void testLocallyStratified_NotEqualInBody() throws Exception
	{
		String program = 
 			"r(1, 2)." +
		    "r(2, 3)." +
		    "r(3, 4)." +
		    "r(4, 5)." +
		    
		    "q('b', 2)." +
		    "q('b', 4)." +
		    
		    "p(?X,?Y) :- r(?X,?Y), not p(2,?Y), ?X != 2." +

		    "?- p(?X, ?Y).";
		
		String expectedResults =
			"p(1, 2)." +
			"p(3, 4)." +
			"p(4, 5).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testLocallyStratified_NotEqualImpliedByLess() throws Exception
	{
		String program = 
 			"r(1, 2)." +
		    "r(2, 3)." +
		    "r(3, 4)." +
		    "r(4, 5)." +
		    
		    "q('b', 2)." +
		    "q('b', 4)." +
		    
		    "p(?X,?Y) :- r(?X,?Y), not p(2,?Y), ?X < 2." +

		    "?- p(?X, ?Y).";
		
		String expectedResults =
			"p(1, 2).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	public void testRuleWithOnlyNegatedGroundedLiteral_FactExists() throws Exception
	{
		String program =
			"p('b') :- not r('a')." +
			"r('a')." +
			"?- p(?x).";		

		String expectedResults =
			"";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testRuleWithOnlyNegatedGroundedLiteral_FactDoesNotExists() throws Exception
	{
		String program =
			"p('b') :- not r('a')." +
			"r('b')." +
			"?- p(?x).";		

		String expectedResults =
			"p('b').";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testRuleWithOnlyNegatedNonGroundLiteral_FactExists() throws Exception
	{
		String program =
			"p('b') :- not r(?X)." +
			"r('a')." +
			"?- p(?x).";		

		String expectedResults =
			"";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testRuleWithOnlyNegatedNonGroundLiteral_FactsDoesNotExist() throws Exception
	{
		String program =
			"p('b') :- not r(?X)." +
			"?- p(?x).";		

		String expectedResults =
			"p('b').";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testUnsafeRuleWithOnlyNegatedNonGroundLiteral() throws Exception
	{
		String program =
			"p(?Y) :- not r(?X)." +
			"?- p(?x).";		

		Helper.checkFailureWithNaive( program, RuleUnsafeException.class );
		Helper.checkFailureWithSemiNaive( program, RuleUnsafeException.class );
	}

	public void testNegatedLiteralWithUnboundVariable() throws Exception
	{
		String program =
			"person(1)." +
			"person(2)." +
			"person(3)." +
			"person(4)." +
			"person(5)." +
			"person(6)." +

			"married(1,2)." +
			"married(3,4)." +

			"married(?X,?Y) :- married(?Y,?X)." +

			"single(?X) :- person(?X), not married(?X, ?Y).";
		
		String expectedResults = "dummy(5).dummy(6).";

		Helper.evaluateWithAllStrategies( program + "?- single(?X).", expectedResults );
		Helper.evaluateWithAllStrategies( program + "?- person(?X), not married(?X, ?Y).", expectedResults );
	}
}
