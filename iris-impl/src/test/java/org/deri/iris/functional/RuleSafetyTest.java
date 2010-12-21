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

public class RuleSafetyTest extends TestCase
{

	/**
     * Check that a rule containing a negated sub-goal that has a variable that
     * is not in the rule head can still be evaluated.
     * 
     * In this case, the rule:
     * 
     * 		bachelor( ?X ) :- male( ?X ), not married( ?X, ?Y )
     * 
     * should effectively be rewritten as:
     * 
     * 		husband( ?X ) :- married( ?X, ?Y )
     * 		bachelor( ?X ) :- male( ?X ), not husband( ?X )
     * 
     * for the purposes of evaluation.
     * @throws Exception 
     */
    public void testSafe_Variable_InNegatedSubGoal_NotInRuleHead_NotInPositiveLiteral() throws Exception
    {
    	String program =
    		"married( 1, 2 )." +
    		"married( 3, 4 )." +
    		"married( 5, 6 )." +
    		"male( 1 )." +
    		"male( 3 )." +
    		"male( 5 )." +
    		"male( 7 )." +
    		"male( 9 )." +
    		"bachelor( ?X ) :- male( ?X ),not married( ?X, ?Y )." +
    		"?- bachelor( ?X ).";
    	
    	String expectedResults = "bachelor( 7 ).bachelor( 9 ).";
    	Helper.evaluateWithAllStrategies( program, expectedResults );
    }

	/**
     * Simple negation.
     * @throws Exception
     */
    public void testSafe_Variable_InNegatedSubGoal_InRuleHead_InPositiveLiteral() throws Exception
    {
    	String program = 
    		"s(1)." +
    		"p(2,2)." +
    		"p(3,9)." +
    		"r(9)." +
    		
    		"w(?X,?Y) :- s(?X), r(?Y), not p(2,?Y)." +
    	    "?- w(?X,?Y).";
        	
       	String expectedResults = 
    	    "w(1, 9).";
    
       	Helper.evaluateWithAllStrategies( program, expectedResults );
    }

	/**
     * Rule with unsafe negation.
     * @throws Exception
     */
    public void testUnsafe_Variable_InNegatedSubGoal_InRuleHead_NotInPositiveLiteral() throws Exception
    {
    	String program = "w(?X,?Y) :- s(?X), not p(2,?Y).";

    	Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
    }

	/**
     * Unsafe rule with head variable not in the body.
     * @throws Exception 
     */
    public void testUnsafe_Variable_InHead_NotInBody() throws Exception
    {
    	String program = "p( ?X, ?Y ) :- q( ?X ).";
    	
    	Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
    }

	/**
     * Simple rule with every head variable in a positive, ordinary predicate.
     * @throws Exception 
     */
    public void testSafe_Variable_InHead_InPositiveLiteral() throws Exception
    {
    	String program =
    		"a( 0 )." +
    		"b( 1 )." +
    		"c( 2 )." +
    		"d( 3 )." +
    		"e( 4 )." +
    		"f( 5 )." +
    		"g( 6 )." +
    		"h( 7 )." +
    		"i( 8 )." +
    		"j( 9 )." +
    		"all( ?A, ?B, ?C, ?D, ?E, ?F, ?G, ?H, ?I, ?J ) :- a( ?A ), b( ?B ), c( ?C ), d( ?D ), e( ?E ), f( ?F ), g( ?G ), h( ?H ), i( ?I ), j( ?J )." +
    		"?- all( ?A, ?B, ?C, ?D, ?E, ?F, ?G, ?H, ?I, ?J ).";
    	
    	String expectedResults = "all( 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 ).";
    	Helper.evaluateWithAllStrategies( program, expectedResults );
    }

	/**
     * Try to execute a rule with an unsafe use of a built-in operator.
     * 
     * (It is unsafe because every variable in the built-in predicate must appear
     * in a positive, ordinary predicate in the same rule.)
     * 
     * @throws Exception
     */
    public void testUnsafe_Variable_InNegatedBuiltin_InRuleHead_NotInPositiveLiteral()
    {
		// Same as: p(?X, ?Y) :- q(?X), not LESS( ?X, ?Y ).
    	String program = "p(?X, ?Y) :- q(?X), not ?X < ?Y.";
    
    	Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
    }

	/**
     * Try to execute a rule with an unsafe use of a built-in operator.
     * 
     * (It is unsafe because every variable in the built-in predicate must appear
     * in a positive, ordinary predicate in the same rule.)
     * 
     * @throws Exception
     */
    public void testUnsafe_Variable_InBuiltin_InRuleHead_NotInPositiveLiteral()
    {
    	String program = "p(?X, ?Y) :- q(?X), ?X < ?Y.";
    
    	Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
    }

	/**
     * A rule with a built-in equality that makes a variable limited
     * (and therefore the rule safe).
     * @throws Exception
     */
    public void testSafe_Variable_InEquality_InRuleHead_NotInPositiveLiteral() throws Exception
    {
    	String program =
    		"id(1)." +
    		"p(?X, ?Y) :- id(?X), ?X = ?Y." +
    		"?- p(?X, ?Y).";

    	String expectedResults = "p( 1, 1 ).";
    
    	Helper.evaluateWithAllStrategies( program, expectedResults );
    }

	/**
     * Unsafe unary predicate.
     * @throws Exception
     */
    public void testUnsafe_Variable_InUnaryBuiltin_NotInPositiveLiteral() throws Exception
    {
    	String program = "w(?X,?Y) :- s(?X), IS_STRING(?Y).";    

    	Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
    }
    
	/**
     * Unsafe unary predicate.
     * @throws Exception
     */
    public void testUnsafe_Variable_InNegatedUnaryBuiltin_NotInPositiveLiteral() throws Exception
    {
    	String program = "w(?X,?Y) :- s(?X), not IS_STRING(?Y).";    

    	Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
    }
    
	/**
     * Unsafe unary predicate.
     * @throws Exception
     */
	public void testUnsafe_Variable_InUnaryBuiltin_NotInPositiveLiteral_NotInHead() throws Exception
	{
		String program =
			"man('homer')." +
			"isMale(?x) :- man(?x), IS_STRING(?z)." +
			"?-isMale(?x).";
	
		Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
	}

	/**
     * Unsafe unary predicate.
     * @throws Exception
     */
    public void testSafe_Variable_InUnaryBuiltin_InPositiveLiteral() throws Exception
    {
    	String program =
    		"s(1)." +
    		"p(2)." +
    		"p('s')." +
    		"w(?X,?Y) :- s(?X), p(?Y), IS_STRING(?Y)." +
    		"?- w(?X, ?Y).";    

    	String expectedResults = "w( 1, 's' ).";
        
    	Helper.evaluateWithAllStrategies( program, expectedResults );
    }

	/**
     * Simple negation.
     * @throws Exception
     */
    public void testSafe_Variable_InNegatedBuiltin_InRuleHead_InPositiveLiteral() throws Exception
    {
    	String program = 
    		"s(1)." +
    		"r(9)." +
    		
    		"w(?X,?Y) :- s(?X), r(?Y), not 2 < ?Y.";
        	
       	Helper.evaluateWithAllStrategies( program, "" );
    }

	/**
     * Arithmetic predicate.
     * @throws Exception
     */
    public void testSafe_UnknownVariableFirstInArithmetic() throws Exception
    {
    	String program = 
    		"w(?X,?Y) :- s(?X), r(?Y), ?X + ?Y = ?Z.";
        	
       	Helper.evaluateWithAllStrategies( program, "" );
    }

	/**
     * Arithmetic predicate.
     * @throws Exception
     */
    public void testSafe_UnknownVariableSecondInArithmetic() throws Exception
    {
    	String program = 
    		"w(?X,?Y) :- s(?X), r(?Z), ?X + ?Y = ?Z.";
        	
       	Helper.evaluateWithAllStrategies( program, "" );
    }

	/**
     * Arithmetic predicate.
     * @throws Exception
     */
    public void testSafe_UnknownVariableThirsInArithmetic() throws Exception
    {
    	String program = 
    		"w(?X,?Y) :- s(?Y), r(?Z), ?X + ?Y = ?Z.";
        	
       	Helper.evaluateWithAllStrategies( program, "" );
    }

	/**
     * Unsafe negated arithmetic predicate.
     * @throws Exception
     */
	public void testUnsafe_UnknownVariableFirstInNegatedArithmetic() throws Exception
	{
		String program =
    		"w(?X,?Y) :- s(?X), r(?Y), not ?X + ?Y = ?Z.";
	
		Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
	}

	/**
     * Unsafe negated arithmetic predicate.
     * @throws Exception
     */
	public void testUnsafe_UnknownVariableSecondInNegatedArithmetic() throws Exception
	{
		String program =
    		"w(?X,?Y) :- s(?X), r(?Z), not ?X + ?Y = ?Z.";
	
		Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
	}

	/**
     * Unsafe negated arithmetic predicate.
     * @throws Exception
     */
	public void testUnsafe_UnknownVariableThirdInNegatedArithmetic() throws Exception
	{
		String program =
    		"w(?X,?Y) :- s(?Y), r(?Z), not ?X + ?Y = ?Z.";
	
		Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
	}

	/**
     * Arithmetic predicate.
     * @throws Exception
     */
    public void testSafe_SameVariableFirstAndSecondInArithmetic() throws Exception
    {
    	String program = 
    		"w(?X) :- s(?X), ?X + ?X = ?Z.";
        	
       	Helper.evaluateWithAllStrategies( program, "" );
    }

	/**
     * Arithmetic predicate.
     * @throws Exception
     */
    public void testSafe_SameVariableFirstAndThirdInArithmetic() throws Exception
    {
    	String program = 
    		"w(?X) :- s(?X), ?X + ?Y = ?X.";
        	
       	Helper.evaluateWithAllStrategies( program, "" );
    }

	/**
     * Arithmetic predicate.
     * @throws Exception
     */
    public void testSafe_SameVariableSecondAndThirdInArithmetic() throws Exception
    {
    	String program = 
    		"w(?X) :- s(?X), ?Y + ?X = ?X.";
        	
       	Helper.evaluateWithAllStrategies( program, "" );
    }

	/**
     * Arithmetic predicate.
     * @throws Exception
     */
    public void testUnsafe_SameUnknownVariableFirstAndSecondInArithmetic() throws Exception
    {
    	String program = 
    		"w(?X) :- s(?Z), ?X + ?X = ?Z.";
        	
		Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
    }

	/**
     * Arithmetic predicate.
     * @throws Exception
     */
    public void testUnsafe_SameUnknownVariableFirstAndThirdInArithmetic() throws Exception
    {
    	String program = 
    		"w(?X) :- s(?Y), ?X + ?Y = ?X.";
        	
		Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
    }

	/**
     * Arithmetic predicate.
     * @throws Exception
     */
    public void testUnsafe_SameUnknownVariableSecondAndThirdInArithmetic() throws Exception
    {
    	String program = 
    		"w(?X) :- s(?Y), ?Y + ?X = ?X.";
        	
		Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
    }

	/**
     * Arithmetic predicate.
     * @throws Exception
     */
    public void testSafe_SameVariableFirstSecondAndThirdInArithmetic() throws Exception
    {
    	String program = 
    		"w(?X) :- s(?X), ?X + ?X = ?X.";
        	
       	Helper.evaluateWithAllStrategies( program, "" );
    }

	/**
     * Arithmetic predicate.
     * @throws Exception
     */
    public void testUnsafe_SameUnknownVariableFirstSecondAndThirdInArithmetic() throws Exception
    {
    	String program = 
    		"w(?X) :- ?X + ?X = ?X.";
        	
		Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
    }

    public void testSafe_EquateToConstant() throws Exception
    {
    	String program = 
    		"p(?X) :- ?X = 1.";
        	
       	Helper.evaluateWithAllStrategies( program, "" );
    }

    public void testSafe_EquateToConstantNoHeadVariables() throws Exception
    {
    	String program = 
    		"p :- ?X = 1.";
        	
       	Helper.evaluateWithAllStrategies( program, "" );
    }

    public void testUnsafe_EquateToSelf() throws Exception
    {
    	String program = 
    		"p(?X) :- ?X = ?X.";
        	
		Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
    }
    
    public void testUnsafe_AddToSelf() throws Exception
    {
    	String program = 
    		"p(?X) :- ?X + ?X = 2.";
        	
		Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
    }

    public void testUnsafe_VariableInNegatedSubGoalAndHead() throws Exception
    {
    	String program = 
    		"p(?X) :- not q(?X).";
        	
		Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
    }

    public void testUnsafe_VariablesInNegatedSubGoalAndHead() throws Exception
    {
    	String program = 
    		"d(?X, ?Y) :- not s(?X, ?Y).";
        	
		Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
    }

    public void testSafe_SeveralUnboundVariablesInUnification() throws Exception
    {
    	String program =
    		"p(?v, ?w) :- q( ?x, ?y ), f( ?x, ?w ) = f( g(?v), h(?y) ).";
        	
       	Helper.evaluateWithAllStrategies( program, "" );
    }

    public void testUnsafe_SeveralUnboundVariablesInNegatedUnification() throws Exception
    {
    	String program =
    		"p(?v, ?w) :- q( ?x, ?y ), ! f( ?x, ?w ) = f( g(?v), h(?y) ).";
        	
		Helper.checkFailureWithAllSafeRulesOnly( program, RuleUnsafeException.class );
    }
}
