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

public class EvaluationTest extends TestCase
{
	/**
	 * Test that a simple logic program of predicate logic is correctly
	 * evaluated.
	 * @throws Exception 
	 */
	public void testPredicateLogic() throws Exception
	{
		String program =
			"p( ?X, ?Y ) :- q( ?X, ?Y ), r( ?Y, ?Z ), s( ?X, ?Z )." +
			"q( 1, 2 )." +
			"r( 2, 3 )." +
			"s( 1, 3 )." +
			"?- p(?X, ?Y).";
		
		String expectedResults = "p( 1, 2 ).";
		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check that IRIS copes with predicates with the same name, but different arities.
	 * @throws Exception 
	 */
	public void testPredicateWithSeveralArities() throws Exception
	{
		String facts =
			"p(8,9,10,11)." +
			"p(12,13,14,15)." +
			"p(5,6,7)." +
			"p(6,6,8)." +
			"p(3,4)." +
			"p(1)." +
			"p(2)." +
			"p.";
		
		Helper.evaluateWithAllStrategies( facts + "?-p(?x,?y,?z,?a).", "p(8,9,10,11).p(12,13,14,15)." );
		
		Helper.evaluateWithAllStrategies( facts + "?-p(?x,?y,?z).", "p(5,6,7).p(6,6,8)." );
		Helper.evaluateWithAllStrategies( facts + "?-p(?x,?y).", "p(3,4)." );
		Helper.evaluateWithAllStrategies( facts + "?- p(?x).", "p(1).p(2)." );
		Helper.evaluateWithAllStrategies( facts + "?- p.", "p." );
	}

	
	/**
	 * Test that a query with more than one predicate is correctly
	 * evaluated.
	 * @throws Exception 
	 */
	public void testConjunctiveQuery() throws Exception
	{
		String program =
			"p( _datetime(2000,1,1,2,2,2) )." +
			"p( _datetime(2000,12,1,2,2,2) )." +
			"?- p( ?X ), p( ?Y ), ?X < ?Y.";
		
		String expectedResults =
			"p( _datetime(2000,1,1,2,2,2), _datetime(2000,12,1,2,2,2) ).";
		
		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
		
	public void testConjunctiveQueryWithNegatedLiterals() throws Exception
	{
		String natural = "n(0).n(1).n(2).n(3).n(4).n(5).n(6).n(7).n(8).n(9).";
		String even = "e(0).e(2).e(4).e(6).e(8).";
		String odd = "o(1).o(3).o(5).o(7).o(9).";
		
		String program = natural + even + odd;

		Helper.evaluateWithAllStrategies( program + "?-n(?x).", natural );
		Helper.evaluateWithAllStrategies( program + "?-n(?x), !e(?x).", odd );
		Helper.evaluateWithAllStrategies( program + "?-n(?x), !o(?x).", even );
		Helper.evaluateWithAllStrategies( program + "?-n(?x), !e(?x), !o(?x).", "" );
	}

	/**
	 * Tests, whether programs with equal literals in the query would
	 * result in programs with unsave magic rules.
	 */
	public void testConjunctiveQueryWithEqualLiterals() throws Exception {
		final String rule = "i(?X) :- e(?X).";
		final String facts = "e(0). e(1). e(2). e(3). e(4). e(5). e(6). e(7). e(8). e(9).";
		final String prog = rule + facts;

		Helper.evaluateWithAllStrategies(prog + " ?- i(1), i(?X), i(?X).", facts);
		Helper.evaluateWithAllStrategies(prog + " ?- i(?X), i(1), i(?X).", facts);
		Helper.evaluateWithAllStrategies(prog + " ?- i(?X), i(?X), i(1).", facts);
	}
		
	/**
	 * Test that logic programs containing only propositional terms
	 * are correctly evaluated.
	 */
	public void testPropositionalLogicTrueOutcome() throws Exception
	{
		String program =
			"p :- a, b, c." +
			"a." +
			"b." +
			"c." +
			"?- p.";
		
		String expectedResults = "p.";
		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Test that logic programs containing only propositional terms
	 * are correctly evaluated.
	 */
	public void testPropositionalLogicFalseOutcome() throws Exception
	{
		String program =
			"p :- a, b, c." +
			"a." +
			"c." +
			"?- p.";
		
		String expectedResults = "";
		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Test for recursive rules.
	 */
	public void testRecursiveRules() throws Exception
	{
		String program =
			"parent( '1a', '2a' )." +
			"parent( '2a', '3a' )." +

			"parent( '1b', '2b' )." +
			"parent( '1b', '2c' )." +

			"parent( '2b', '3b' )." +
			"parent( '2b', '3c' )." +
			"parent( '2c', '3d' )." +
			"parent( '2c', '3e' )." +
			
			"parent( '3b', '4b' )." +
			"parent( '3e', '4e' )." +

			"sibling(?X,?Y) :- parent(?Z,?X), parent(?Z,?Y), ?X != ?Y." +
			"cousin(?X,?Y) :- parent(?XP,?X), parent(?YP,?Y), sibling(?XP,?YP).\n" +
			"cousin(?X,?Y) :- parent(?XP,?X), parent(?YP,?Y), cousin(?XP,?YP).\n" +

			"?- cousin(?X,?Y).\n";
		
		String expectedResults =	"cousin( '3b', '3d' )." +
									"cousin( '3b', '3e' )." +
									"cousin( '3c', '3d' )." +
									"cousin( '3c', '3e' )." +
									"cousin( '3d', '3b' )." +
									"cousin( '3e', '3b' )." +
									"cousin( '3d', '3c' )." +
									"cousin( '3e', '3c' )." +
									"cousin( '4b', '4e' )." +
									"cousin( '4e', '4b' ).";
		
		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Test for recursive rules.
	 */
	public void testRecursiveRules2() throws Exception
	{
		String facts =
   			"parent('c','a')." +
			"parent('d','a')." +
			"parent('d','b')." +
			"parent('e','b')." +
			"parent('f','c')." +
			"parent('f','e')." +
			"parent('g','c')." +
			"parent('h','d')." +
			"parent('i','d')." +
			"parent('i','e')." +
			"parent('j','f')." +
			"parent('j','h')." +
			"parent('k','g')." +
			"parent('k','i')." +
			
		    "sibling(?X,?Y) :- parent(?X, ?Z), parent(?Y, ?Z), ?X != ?Y." +
		    "cousin(?X,?Y)  :- parent(?X, ?Xp), parent(?Y, ?Yp), sibling(?Xp,?Yp)." +
		    "cousin(?X,?Y)  :- parent(?X, ?Xp), parent(?Y, ?Yp), cousin(?Xp,?Yp)." +
		    
		    "related(?X,?Y) :- sibling(?X, ?Y)." +
		    "related(?X,?Y) :- related(?X, ?Z), parent(?Y, ?Z)." +
		    "related(?X,?Y) :- related(?Z, ?Y), parent(?X, ?Z).";
		    
		String expectedResults =
    		"sibling('c','d')." +
    		"sibling('d','c')." +
			"sibling('d','e')." +
			"sibling('e','d')." +
			"sibling('f','g')." +
			"sibling('g','f')." +
			"sibling('h','i')." +
			"sibling('i','h')." +
			"sibling('f','i')." +
    		"sibling('i','f').";

		Helper.evaluateWithAllStrategies( facts + "?- sibling(?X,?Y).", expectedResults );

		expectedResults =
    		"cousin('f','h')." +
    		"cousin('h','f')." +
			"cousin('f','i')." +
			"cousin('i','f')." +
			"cousin('i','i')." +
			"cousin('g','h')." +
			"cousin('h','g')." +
			"cousin('g','i')." +
			"cousin('i','g')." +
    		"cousin('h','i')." +
			"cousin('i','h')." +
    		"cousin('j','k')." +
			"cousin('k','j')." +
			"cousin('j','j')." +
			"cousin('k','k').";
			
		Helper.evaluateWithAllStrategies( facts + "?- cousin (?X,?Y).", expectedResults );

		expectedResults =
    		"related('c','d')." +
    		"related('d','c')." +
			"related('d','e')." +
			"related('e','d')." +
			"related('f','g')." +
			"related('g','f')." +
			"related('h','i')." +
			"related('i','h')." +
			"related('f','i')." +
    		"related('i','f')." +
    		
			"related('d','f')." +
    		"related('f','d')." +
			"related('d','g')." +
			"related('g','d')." +
			"related('c','h')." +
			"related('h','c')." +
			"related('d','i')." +
    		"related('i','d')." +
			"related('c','i')." +
			"related('i','c')." +
			"related('e','h')." +
			"related('h','e')." +
			"related('e','i')." +
			"related('i','e')." +
			"related('g','j')." +
    		"related('j','g')." +
			"related('f','k')." +
    		"related('k','f')." +
			"related('h','k')." +
			"related('k','h')." +
			"related('i','j')." + 
	    	"related('j','i')." +
	    	
			"related('f','h')." +
			"related('h','f')." +
			"related('d','j')." +
			"related('j','d')." +
			"related('g','h')." +
			"related('h','g')." +
			"related('j','k')." +
			"related('k','j')." +
			"related('g','i')." +
			"related('i','g')." +
			"related('d','k')." +
			"related('k','d')." +
			"related('c','j')." +
			"related('j','c')." +
			"related('i','i')." +
			"related('c','k')." +
			"related('k','c')." +
			"related('e','j')." +
			"related('j','e')." +
			"related('e','k')." +
			"related('k','e')." +
			
			"related('f','j')." +
			"related('j','f')." +
			"related('h','j')." +
			"related('j','h')." +
			"related('g','k')." +
			"related('k','g')." +
			"related('i','k')." +
			"related('k','i')." +
			
			"related('j','j')." +
			"related('k','k').";

		Helper.evaluateWithAllStrategies( facts + "?- related(?X,?Y).", expectedResults );
	}

	/**
	 * Test for recursive rules.
	 */
	public void testRecursiveRules3() throws Exception
	{
		String program =
    		"down('g', 'b')." +
    		"down('h', 'c')." +
    		"down('i', 'd')." +
    		"down('l', 'f')." +
    		"down('m', 'f')." +
    		"down('p', 'k')." +
    		
    		"flat('g', 'f')." +
    		"flat('m', 'n')." +
    		"flat('m', 'o')." +
    		"flat('p', 'm')." +
    		
    		"up('a', 'e')." +
    		"up('a', 'f')." +
    		"up('f', 'm')." +
    		"up('g', 'n')." +
    		"up('h', 'n')." +
    		"up('i', 'o')." +
    		"up('j', 'o')." +
    		
		    "rsg(?X, ?Y) :- up(?X, ?W), rsg(?Q, ?W), down(?Q, ?Y)." +
		    "rsg(?X, ?Y) :- flat(?X, ?Y)." +
		    "?- rsg(?X, ?Y).";
		
		String expectedResults =
		    "rsg('a','b')." +
		    "rsg('a','c')." +
		    "rsg('a','d')." +
		    "rsg('f','k')." +
		    "rsg('g','f')." +
		    "rsg('h','f')." +
		    "rsg('i','f')." +
		    "rsg('j','f')." +
		    "rsg('m','n')." +
		    "rsg('m','o')." +
		    "rsg('p','m').";
		
		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Test for recursive rules. Added for OLDT evaluation.
	 * @see Deduktive Datenbanken - Example 4.14 [Cremers 94]
	 * 
	 * @author gigi
	 */
	public void testRecursiveRules4() throws Exception
	{
		String program =
    		"r(1, 2)." +
    		"r(1, 3)." +
    		"r(2, 1)." +
    		"r(2, 4)." +
    		
		    "s(?X, ?Y) :- s(?X, ?Z), r(?Z, ?Y)." +
		    "s(?X, ?Y) :- r(?X, ?Y)." +
		    "?- s(1, ?Y).";
		
		String expectedResults =
		    "dummy(2)." +
		    "dummy(3)." +
		    "dummy(1)." +
		    "dummy(4).";
		
		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Same as testRecursiveRules4, but querying s(?X, ?Y) instead of s(1, ?Y).
	 * @see testRecursiveRules4
	 */
	public void testRecursiveRules4b() throws Exception
	{
		String program =
    		"r(1, 2)." +
    		"r(1, 3)." +
    		"r(2, 1)." +
    		"r(2, 4)." + 
    		
		    "s(?X, ?Y) :- s(?X, ?Z), r(?Z, ?Y)." +
		    "s(?X, ?Y) :- r(?X, ?Y)." +
		    "?- s(?X, ?Y).";
		
		String expectedResults =
			"dummy(1, 2)." +
			"dummy(1, 3)." +
			"dummy(2, 1)." +
			"dummy(2, 4)." +
		    "dummy(1, 1)." +
		    "dummy(1, 4)." +
		    "dummy(2, 2)." +
		    "dummy(2, 3).";
		
		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Test for rules with a cyclic dependency.
	 * @throws Exception If something goes very wrong.
	 */
	public void testRecursionBetweenRules() throws Exception
	{
		String program = 
		    "edge('a', 'b')." +
		    "path('b', 'c')." +
		    "edge('c', 'd')." +
		    
		    "path(?X, ?Y) :- edge(?X, ?Y)." +
		    "edge(?X, ?Y) :- path(?X, ?Y)." +	
		    "path(?X, ?Y) :- edge(?X, ?Z), path(?Z, ?Y)." +
		    "?- path(?X, ?Y).";
		
    	String expectedResults = 
		    "path('a','b')." +
		    "path('a','c')." +
		    "path('a','d')." +
		    "path('b','c')." +
		    "path('b','d')." +
		    "path('c','d').";

    	Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check that rules containing a grounded term in the rule head are
	 * correctly evaluated.
	 * @throws Exception
	 */
	public void testGroundedTermInRuleHead() throws Exception
	{
    	String program = 
		    "r('a', 'a')." +
		    "r('b', 'c')." +
		    "r('c', 'd')." +
		    
		    "p(?X, 'a') :- r(?X, ?Y)." +
		    "?- p(?X, ?Y).";
    	
    	String expectedResults = 
		    "p('a','a')." +
		    "p('b','a')." +
		    "p('c','a').";

    	Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check that rules that make a self join on a relation are correctly evaluated. 
	 * @throws Exception
	 */
	public void testSelfJoin() throws Exception
	{
    	String program = 
		    "s('a', 'a')." +
		    "s('a', 'b')." +
		    "s('a', 'c')." +
		    "s('a', 'd')." +
		    "s('a', 'e')." +
		    "s('a', 'f')." +
		    "s('a', 'g')." +
		    "s('a', 'h')." +
		    "s('a', 'i')." +
		    
		    "s('b', 'a')." +
		    "s('b', 'b')." +
		    "s('b', 'c')." +
		    "s('b', 'd')." +
		    "s('b', 'e')." +
		    "s('b', 'f')." +
		    "s('b', 'g')." +
		    "s('b', 'h')." +
		    "s('b', 'i')." +
		    
		    "s('c', 'a')." +
		    "s('c', 'b')." +
		    "s('c', 'c')." +
		    "s('c', 'd')." +
		    "s('c', 'e')." +
		    "s('c', 'f')." +
		    "s('c', 'g')." +
		    "s('c', 'h')." +
		    "s('c', 'i')." +
		    
		    "s('f', 'f')." +
		    "s('f', 'g')." +
		    "s('f', 'h')." +
		    "s('f', 'i')." +
		    
		    "s('g', 'f')." +
		    "s('g', 'g')." +
		    "s('g', 'h')." +
		    "s('g', 'i')." +
		    
		    "s('h', 'f')." +
		    "s('h', 'g')." +
		    "s('h', 'h')." +
		    "s('h', 'i')." +
		    
		    "p(?X) :- s(?X, ?Y), s(?Y, ?X)." +
		    "?- p(?X).";
        	
       	String expectedResults = 
		    "p('a')." +
		    "p('h')." +
		    "p('f')." +
		    "p('c')." +
		    "p('b')." +
		    "p('g').";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Check that rules that involve a cartesian product are correctly evaluated. 
	 * @throws Exception
	 */
	public void testCartesianProduct() throws Exception
	{
    	String program = 
   			"s(1)." +
   			"s(2)." +
			
			"p(3)." +
			"p(4)." +
			
		    "w(?X,?Y) :- s(?X), p(?Y)." +
		    "?- w(?Y,?X).";
        	
       	String expectedResults = 
		    "w(1, 3)." +
		    "w(1, 4)." +
		    "w(2, 3)." +
		    "w(2, 4).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testTransitiveClosure() throws Exception
	{
		String program = 
		    "edge('a', 'b')." +
		    "path('b', 'c')." +
		    "edge('c', 'd')." +
		    
		    "path(?X, ?Y) :- edge(?X, ?Y)." +	
		    "path(?X, ?Y) :- path(?X, ?Z), path(?Z, ?Y)." +
		    "?- path(?X, ?Y).";
  	
		String expectedResults = 
		    "path('a','b')." +
		    "path('a','c')." +
		    "path('a','d')." +
		    "path('b','c')." +
		    "path('b','d')." +
		    "path('c','d').";
		
		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	public void testGroundedTermInQuery() throws Exception
	{
    	String program = 
		    "in('galway', 'ireland')." +
		    "in('dublin', 'ireland')." +
		    "in('innsbruck', 'austria')." +
		    "in('ireland', 'europe')." +
		    "in('austria', 'europe')." +
		    
		    "in(?X, ?Z) :- in(?X, ?Y), in(?Y, ?Z)." +
		    "?- in('galway', ?Z).";
		
		String expectedResults = 
    		"in('europe')." +
		    "in('ireland').";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check that a long chain of rules can be correctly evaluated, i.e. a implies b,
	 * b implies c, c implies d, etc.
	 * We do this here for a 'chain' of 676 rules.
	 * @throws Exception
	 */
	public void testLongChainOfRules() throws Exception
	{
		StringBuilder buffer = new StringBuilder();
		
		final String p = "p";
		
		// Starting facts
		buffer.append( "p1" ).append( "('a','b')." ).append( "p1" ).append( "(1,2)." );
		
		// Some more facts along the chain.
		buffer.append( "p10" ).append( "(3,4)." );
		buffer.append( "p15" ).append( "(5,6)." );
		buffer.append( "p50" ).append( "(7,8)." );
		buffer.append( "p52" ).append( "(9,10)." );
		
		final int count = 1000;
		
		for ( int predicate = 1; predicate < count; ++predicate )
		{
			buffer.append( p + (predicate + 1) ).append( "(?X,?Y ) :- " ).append( p + predicate ).append( "(?X,?Y )." );
		}
		buffer.append( "?- " + p + count + "(?x,?y)." );
		
		String program = buffer.toString();
		String expectedResults =
			"dummy('a','b')." + 
			"dummy(1,2)." +
			"dummy(3,4)." +
			"dummy(5,6)." +
			"dummy(7,8)." +
			"dummy(9,10).";
		
		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check for the correct evaluation of a logic program that contains rules that require a great
	 * deal of modification during rectification.
	 * @throws Exception 
	 */
	public void testRuleRectification() throws Exception
	{
		String program =
			"q( 1 )." +
			"r( 2 )." +
			"s( 3 )." +
			"t( 1 )." +
			"u( 4 )." +
			"p( ?X, ?Y, ?Z ) :- q( ?X ), r( ?Y ), s( ?Z )." +
			"p( ?X, ?Y, ?X ) :- q( ?X ), r( ?Y ), t( ?X )." +
			"p( ?X, 3, ?Z ) :- q( ?X ), u( ?Z )." +
			"p( ?X, 5, 5 ) :- q( ?X )." +
			"p( 5, 5, 5 ) :- q( ?X )." +
			"p( ?X, ?X, ?X ) :- q( ?X )." +
			"?- p( ?X, ?Y, ?Z ).";
		
		String expectedResults =
			"p( 1, 2, 3 )." +
			"p( 1, 2, 1 )." +
			"p( 1, 3, 4 )." +
			"p( 1, 5, 5 )." +
			"p( 5, 5, 5 )." +
			"p( 1, 1, 1 ).";
		
		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * This was supposed to be a test involving relations with thousands of tuples, but as
	 * it turns out, IRIS runs out of heap when there are 100 rows in each predicate.
	 * About 50 rows takes about 20 seconds to evaluate with naive.
	 * 
	 * @throws Exception
	 */
	public void testJoinWithLargeDataSets() throws Exception
	{
		StringBuilder p = new StringBuilder();
		StringBuilder r = new StringBuilder();
		
		final int MAX = 500;
		
		for( int i = 0; i < MAX; ++i )
		{
			p.append( "p(" + i + ")." );
			p.append( "q(" + i + ")." );
			p.append( "r(" + i + ")." );
			
			r.append( "t(" + i + "," + i + "," + i + ")." );
		}
		
		p.append( "t(?X,?Y,?Z) :- p(?X), q(?Y), r(?Z), ?X = ?Y, ?Y = ?Z." );
		p.append( "?- t( ?X, ?Y, ?Z )." );
		
		String program = p.toString();
		String expectedResults = r.toString();

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testQueryForUnknownPredicate() throws Exception
	{
    	String program = 
		    "p(1)." +
		    "p(2)." +
		    
		    "q(?X) :- p(?X)." +
		    "?- r(?x).";
		
		String expectedResults = "";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Assert that the evaluation of 3 rules that have a unified head predicate evaluate correctly.
	 * Internally, the 3 rules for p(X) will be converted to relational algebra with a union.
	 */
	public void testUnion() throws Exception
	{
		String program = 
 			"r(1)." +
		    "r(2)." +
		    
 			"s(3)." +
		    "s(4)." +
		    
 			"t(5)." +
		    "t(6)." +
		    
		    "p(?X) :- r(?X)." +
		    "p(?X) :- s(?X)." +
		    "p(?X) :- t(?X)." +
		    "?- p(?X).";
		
		String expectedResults =
			"p(1)." +
			"p(2)." +
			"p(3)." +
			"p(4)." +
			"p(5)." +
			"p(6).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}	

	/**
	 * Assert that the evaluation of 3 rules that have a unified head predicate evaluate correctly.
	 * Internally, the 3 rules for p(X) will be converted to relational algebra with a union.
	 */
	public void testFibonacci() throws Exception
	{
		String program = 
			"fib(0,0)." +
			"fib(1,1)." +

			"fib(?n, ?f ) :- ?n > 1," +
			                "?n1 + 1 = ?n," +
			                "?n2 + 2 = ?n," +
			                "fib( ?n1, ?f1 )," +
			                "fib( ?n2, ?f2 )," +
			                "?f1 + ?f2 = ?f," +
			                "?f < 1000000000," +  // To add a higher limit
			                "?f > 0." +           // To catch integer overflow
			"?- fib(?n,?f).";

		String expectedResults =
			"fib(0, 0)." +
			"fib(1, 1)." +
			"fib(2, 1)." +
			"fib(3, 2)." +
			"fib(4, 3)." +
			"fib(5, 5)." +
			"fib(6, 8)." +
			"fib(7, 13)." +
			"fib(8, 21)." +
			"fib(9, 34)." +
			"fib(10, 55)." +
			"fib(11, 89)." +
			"fib(12, 144)." +
			"fib(13, 233)." +
			"fib(14, 377)." +
			"fib(15, 610)." +
			"fib(16, 987)." +
			"fib(17, 1597)." +
			"fib(18, 2584)." +
			"fib(19, 4181)." +
			"fib(20, 6765)." +
			"fib(21, 10946)." +
			"fib(22, 17711)." +
			"fib(23, 28657)." +
			"fib(24, 46368)." +
			"fib(25, 75025)." +
			"fib(26, 121393)." +
			"fib(27, 196418)." +
			"fib(28, 317811)." +
			"fib(29, 514229)." +
			"fib(30, 832040)." +
			"fib(31, 1346269)." +
			"fib(32, 2178309)." +
			"fib(33, 3524578)." +
			"fib(34, 5702887)." +
			"fib(35, 9227465)." +
			"fib(36, 14930352)." +
			"fib(37, 24157817)." +
			"fib(38, 39088169)." +
			"fib(39, 63245986)." +
			"fib(40, 102334155)." +
			"fib(41, 165580141)." +
			"fib(42, 267914296)." +
			"fib(43, 433494437)." +
			"fib(44, 701408733).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}	

	/**
	 * This program is known to cause the magic sets evaluation to give incorrect results
	 * if the Facts class does not hash the predicate names correctly.
	 * See bug 1822055
	 */
	public void testMagic1() throws Exception
	{
		String program = 
			"p(?X,?Y) :- b(?X,?Y)." +
			"p(?X,?Y) :- b(?X,?U), p(?U,?Y)." +

			"e(?X,?Y) :- g(?X,?Y)." +
			"e(?X,?Y) :- g(?X,?U), e(?U,?Y)." +

			"a(?X,?Y) :- e(?X,?Y), not p(?X,?Y)." +

			"b(1,2)." +
			"b(2,1)." +
			"g(2,3)." +
			"g(3,2)." +
			"?- a(2,?Y).";
		
		String expectedResults =
			"a(3).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}	

	/**
	 * This reproduces bug: 1829204 Repeated literal in query fails with magic sets
	 * @throws Exception
	 */
	public void testMagic2() throws Exception
	{
		String program = 
			"p(1)."+
			"?-p(1),p(1).";
		
		String expectedResults =
			"p.";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}	
	
	/**
	 * This reproduces bug: 1829204 Repeated literal in query fails with magic sets
	 * @throws Exception
	 */
	public void testMagic3() throws Exception
	{
		String program = 
			"a(?X, ?Y) :- a(?Y, ?X)." +
			"?- a(1, ?X)." +
			"a(1, 2)." +
			"a(3, 1).";
		
		String expectedResults =
			"dummy(2).dummy(3).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}	
	
	/**
	 * This reproduces bug: 1919554 magic sets with builtins don't evaluate correctly
	 */
	public void testMagic4() throws Exception
	{
		String program = 
			"parent(1,2)." +
			"parent(?n1,?n2) :- parent( ?n, ?n1 ), ?n+1=?n1, ?n+2=?n2, ?n1 < 10." +
			"parent(10,1)." +
			"tc(?x,?y):- tc(?x,?z),parent(?z,?y)." +
			"tc(?x,?y):- parent(?x,?y)." +
			"?-tc(10,9).";
		
		String expectedResults =
			"dummy.";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}	
	
	public void testIncompatibaleAssignments() throws Exception
	{
		String program = 
			"p(?X) :- ?X = 1, ?X = 2." +
			"?-p(?X).";
		
		String expectedResults = "";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}	

	public void testCompatibaleAssignments() throws Exception
	{
		String program = 
			"p(?X) :- ?X = 2, ?X = 2." +
			"?-p(?X).";
		
		String expectedResults =
			"p(2).";

		Helper.evaluateWithAllStrategies( program, expectedResults );
	}	

	/**
     * Test lots of sequences of built-ins and ordinary predicates to check for
     * errors in relational algebra expression evaluation.
     * @throws Exception
     */
    public void testMultipleOrdinaryBuiltinSequences() throws Exception
    {
    	String program =
    		"r(1)." +
    		"r(2)." +
    		"r(3)." +
    		
    		"s(2)." +
    		"s(3)." +

    		"p1(?X) :- ?X = 1." +
    		"p2(?X) :- ?X = 1, r(?X)." +
    		"p3(?X) :- r(?X), ?X = 1." +
    		"p4(?X) :- ?X = 1, ?X = 2." +
    		"p5(?X) :- ?X = 2, ?X = 2.";
        	
       	Helper.evaluateWithAllStrategies( program + "?-p1(?X).", "p1(1)." );
       	Helper.evaluateWithAllStrategies( program + "?-p2(?X).", "p2(1)." );
       	Helper.evaluateWithAllStrategies( program + "?-p3(?X).", "p3(1)." );
       	Helper.evaluateWithAllStrategies( program + "?-p4(?X).", "" );
       	Helper.evaluateWithAllStrategies( program + "?-p5(?X).", "p5(2)." );
    }
	/**
     * Test lots of sequences of built-ins and ordinary predicates to check for
     * errors in relational algebra expression evaluation.
     * @throws Exception
     */
    public void testMultipleOrdinaryBuiltinNegationSequences() throws Exception
    {
    	String program =
    		"r(1)." +
    		"r(2)." +
    		"r(3)." +
    		
    		"s(2)." +
    		"s(3)." +

    		"s1(?X) :- ?X = 1, not s(?X)." +
    		"s2(?X) :- not s(?X), ?X = 1." +

    		"s3(?X) :- ?X = 1, r(?X), not s(?X)." +
    		"s4(?X) :- not s(?X), ?X = 1, r(?X)." +
    		
    		"s5(?X) :- r(?X), ?X = 1, not s(?X)." +
    		"s6(?X) :- not s(?X), r(?X), ?X = 1." +
    		
    		"s7(?X) :- ?X = 1, ?X = 2, not s(?X)." +
    		"s7(?X) :- not s(?X), ?X = 1, ?X = 2." +
    		
    		"s9(?X) :- ?X = 1, ?X = 1, not s(?X)." +
    		"s0(?X) :- not s(?X), ?X = 1, ?X = 1.";
    		
       	Helper.evaluateWithAllStrategies( program + "?-s1(?X).", "dummy(1)." );
       	Helper.evaluateWithAllStrategies( program + "?-s2(?X).", "dummy(1)." );
       	Helper.evaluateWithAllStrategies( program + "?-s3(?X).", "dummy(1)." );
       	Helper.evaluateWithAllStrategies( program + "?-s4(?X).", "dummy(1)." );
       	Helper.evaluateWithAllStrategies( program + "?-s5(?X).", "dummy(1)." );
       	Helper.evaluateWithAllStrategies( program + "?-s6(?X).", "dummy(1)." );
       	Helper.evaluateWithAllStrategies( program + "?-s7(?X).", "" );
       	Helper.evaluateWithAllStrategies( program + "?-s8(?X).", "" );
       	Helper.evaluateWithAllStrategies( program + "?-s9(?X).", "dummy(1)." );
       	Helper.evaluateWithAllStrategies( program + "?-s0(?X).", "dummy(1)." );
    }

    /**
     * Check that the evaluation produces tuples for the rule head of the correct arity,
     * despite the fact that some of the terms in the head are constants.
     * @throws Exception
     */
	public void testConstantsInRuleHead() throws Exception
	{
    	String program = 
		    "r('a', 'a')." +
		    "r('b', 'c')." +
		    "r('c', 'd')." +
		    
		    "p(?X, 'a') :- r(?X, ?Y)." +
		    "?- p(?X, ?Y).";
	  	
    	String expectedResults =
		    "p('a','a')." +
		    "p('b','a')." +
		    "p('c','a').";
		    
    	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * A similar test was failing in GeneralProgramTest.testEvaluate1()
	 */
	public void testOldCopiedTest() throws Exception
	{
    	String program =
    		"p(?U, ?V, ?W) :- r(?V, ?W), ?W = ?U." +
    		"r('b', 'b')." +
    		"r('c', 'c')." +
    		"?-p(?U, ?V, ?W).";
    	
    	String expectedResults =
    		"p('b', 'b', 'b')." +
    		"p('c', 'c', 'c').";
    	
    	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Test for bug 1871777 - rule optimisation error.
	 */
	public void testRuleOptimisation_JoinConditionOptimisation() throws Exception
	{
    	String program =
    		"triple(0,0,0,1)." +
    		"triple(?n, ?x, ?y, ?z) :- triple(?n1, ?x1, ?y1, ?z1), ?n1 + 1 = ?n, ?n/16=?x, ?n%16=?y2, ?y2/4=?y, ?n%4=?zz, ?zz+1=?z, ?n < 64." +
    		"congruent( ?X, ?Y, ?K ) :- triple(?N, ?X, ?Y, ?K ), ?X % ?K = ?XK, ?Y % ?K = ?YK, ?XK = ?YK." +
    		"mul( ?x, ?y, ?n ) :- congruent( ?a1, ?a2, ?n ), congruent( ?b1, ?b2, ?n ), ?a1*?b1=?x, ?a2*?b2=?y." +
    		"left_over( ?x,?y,?n ) :- mul( ?x,?y,?n), ?x % ?n = ?x1, ?y % ?n = ?y1, ?x1 != ?y1." +
    		"?- lef_over( ?x,?y, ?n ).";
    	
    	String expectedResults = "";
    	
    	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testRuleWithOnlyConstantsInHead() throws Exception
	{
    	String program =
    		"p('a') :- TRUE." +
    		"?-p(?X).";
    	
    	String expectedResults =
    		"p('a').";
    	
    	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testRuleWithNoBodyAndOnlyConstantsInHead() throws Exception
	{
    	String program =
    		"p('a') :- ." +
    		"?-p(?X).";
    	
    	String expectedResults =
    		"p('a').";
    	
    	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * The point of this test is that the rule body can legally be
	 * evaluated in any order. If the negated subgoal is first then
	 * the whole rule body will fail if *any* facts for q exist.
	 * If the positive subgoal is evaluated first, then only those
	 * facts in r() that also appear in q() are removed.
	 * i.e. The rule produces different results.
	 * However, it should be the case that positive literals are
	 * evaluated first when there are negative literals that share
	 * some of the same variables.
	 * If rule optimisation is turned off (see ReOrderLiteralsOptimiser)
	 * then the rule compiler will just try to compile the rule in the
	 * order given. This test is to catch situations when this re-ordering
	 * has not occurred for some reason.
	 * @throws Exception
	 */
	public void testSubgoalOrdering() throws Exception
	{
    	String program =
    		"p(?x, ?y) :- not q(?x, ?y), r(?x, ?y)." +

    		"r(1, 2)." +
    		"r(3, 4)." +
    		"q(1, 2)." +
    		
    		"?-p(?x, ?y).";
    	
    	String expectedResults =
    		"p(3, 4).";	// Will be empty if 'not q(?x, ?y)' is evaluated first.
    	
    	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * This test came about as a result of bug 1899920.
	 * There was a problem where an index on a view was accessing the view's output
	 * relation without the view updating itself first.
	 */
	public void testIndexOnView() throws Exception
	{
    	String program =
    		"hv('me', 'christianName', 'Adrian')." +
    		"hv('me', 'surname', 'Mocan')." +
    		"me('me', 'Person')." +

    		"hv(m(?X13,'Citizen'), 'hasName', m(?X13,'Name')) :-" +
    		"	me(?X13, 'Person')," +
    		"	me(m(?X13,'Name'), 'Name')," +
    		"	hv(m(?X13,'Name'), ?A14, ?V15)." +

    		"me(m(?X13,'Citizen'), 'Citizen') :-" +
    		"	me(?X13, 'Person')," +
    		"	me(m(?X13,'Name'), 'Name')," +
    		"	hv(m(?X13,'Name'), ?A14, ?V15)." +

    		"me(m(?X5,'Name'), 'Name') :- " +
    		"	me(?X5, 'Person')," +
    		"	hv(?X5, 'surname', ?Y6)," +
    		"	me(?X5, ?SC7)," +
    		"	mappedConcepts(?SC7, 'Name', ?X5)." +

    		"me(m(?X3,'Name'), 'Name') :- me(?X3, 'Person')." +

    		"me(m(?X1,'Citizen'), 'Citizen') :- me(?X1, 'Person')." +

    		"me(m(?X9,'Name'), 'Name') :-" +
    		"	hv(?X9, 'christianName', ?Y10)," +
    		"	me(?X9, 'Person')," +
    		"	me(?X9, ?SC11)," +
    		"	mappedConcepts(?SC11, 'Name', ?X9)." +

    		"mappedConcepts('Person', 'Citizen', ?X1) :- me(?X1, 'Person')." +

    		"mappedConcepts('Person', 'Name', ?X3) :- me(?X3, 'Person')." +

    		"hv(m(?X9,'Name'), 'hasFirstName', ?Y10) :-" +
    		"	hv(?X9, 'christianName', ?Y10)," +
    		"	me(?X9, 'Person')," +
    		"	me(?X9, ?SC11)," +
    		"	mappedConcepts(?SC11, 'Name', ?X9)." +

    		"hv(m(?X5,'Name'), 'surname', ?Y6) :-" +
    		"	me(?X5, 'Person')," +
    		"	hv(?X5, 'surname', ?Y6)," +
    		"	me(?X5, ?SC7), " +
    		"	mappedConcepts(?SC7, 'Name', ?X5)." +
    	
    		"?- hv(m(?X13,'Citizen'), 'hasName', m(?X13,'Name')).";

    	String expectedResults =
    		"dummy( 'me' ).";
    	
    	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	/**
	 * Tests the correct evaluation of a program containing built-ins and
	 * transformed with the magic sets.
	 * @see <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=1919554&group_id=167309&atid=842434">bug #1919554: magic sets with builtins don't evaluate correctly</a>
	 */
	public void testMagicBuiltinsTransformation() throws Exception {
		final String prog = "parent(1,2).\n"
			+ "parent(?n1,?n2) :- parent( ?n, ?n1 ), ?n+1=?n1, ?n+2=?n2, ?n1 < 10.\n"
			+ "parent(10,1).\n"
			+ "tc(?x,?y):- tc(?x,?z),parent(?z,?y).\n"
			+ "tc(?x,?y):- parent(?x,?y).\n"
			+ "?-tc(10,9).\n";
		final String expected = "dummy.";

		Helper.evaluateWithAllStrategies(prog, expected);
	}

	public void testEmptyResultSet() throws Exception
	{
		String program =
			"?- p(?X, ?Y).";
		
		String expectedResults = "";
		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
}
