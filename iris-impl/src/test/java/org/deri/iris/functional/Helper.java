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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.deri.iris.Configuration;
import org.deri.iris.KnowledgeBaseFactory;
import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.compiler.Parser;
import org.deri.iris.evaluation.stratifiedbottomup.StratifiedBottomUpEvaluationStrategyFactory;
import org.deri.iris.evaluation.stratifiedbottomup.naive.NaiveEvaluatorFactory;
import org.deri.iris.evaluation.topdown.oldt.OLDTEvaluationStrategyFactory;
import org.deri.iris.evaluation.topdown.sldnf.SLDNFEvaluationStrategyFactory;
import org.deri.iris.evaluation.wellfounded.WellFoundedEvaluationStrategyFactory;
import org.deri.iris.optimisations.magicsets.MagicSets;
import org.deri.iris.optimisations.rulefilter.RuleFilter;
import org.deri.iris.rules.safety.AugmentingRuleSafetyProcessor;
import org.deri.iris.storage.IRelation;

public class Helper
{
	public static final boolean PRINT_RESULTS = false;
	public static final boolean PRINT_TIMINGS = false;
	
	static class Timer
	{
		Timer()
		{
			mTime = System.currentTimeMillis();
		}
		
		void show( String event )
		{
			long span = System.currentTimeMillis() - mTime;
			
			if( PRINT_TIMINGS )
				System.out.println( event + ": " + span + "ms" );
			
			mTime = System.currentTimeMillis();
		}
		
		long mTime;
	}
	
	/**
	 * Evaluate a logic program using every combination of evaluation strategy
	 * and optimization.
	 * Assert that all evaluations produce the same, expected results.
	 * @throws Exception on failure 
	 */
	public static void evaluateWithAllStrategies( String program, String expectedResults ) throws Exception
	{
//		evaluateNotOptimised( program, expectedResults );
		evaluateNaive( program, expectedResults );
		evaluateSemiNaive( program, expectedResults );
		evaluateUnsafeRules( program, expectedResults );
		evaluateWellFounded( program, expectedResults );
		evaluateSemiNaiveAndOptimisations( program, expectedResults );
		
//		evaluateOLDT( program, expectedResults );
//		evaluateSLDNF( program, expectedResults );
	}
	
	public static void evaluateNotOptimised( String program, String expectedResults ) throws Exception
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();
		
		configuration.evaluationStrategyFactory = new StratifiedBottomUpEvaluationStrategyFactory( new NaiveEvaluatorFactory() );
		configuration.reOrderingOptimiser = null;
		configuration.ruleOptimisers.clear();
		configuration.programOptmimisers.clear();
		
		executeAndCheckResults( program, expectedResults, configuration, "Un-optimised" );
	}
	
	public static void evaluateNaive( String program, String expectedResults ) throws Exception
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();
		
		configuration.evaluationStrategyFactory = new StratifiedBottomUpEvaluationStrategyFactory( new NaiveEvaluatorFactory() );
		
		executeAndCheckResults( program, expectedResults, configuration, "Naive" );
	}
	
	public static void evaluateSemiNaive( String program, String expectedResults ) throws Exception
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();
		
		executeAndCheckResults( program, expectedResults, configuration, "Semi-Naive" );
	}
	
	public static void evaluateUnsafeRules( String program, String expectedResults ) throws Exception
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();
		
		configuration.ruleSafetyProcessor = new AugmentingRuleSafetyProcessor();
		
		executeAndCheckResults( program, expectedResults, configuration, "Semi-Naive with unsafe-rules" );
	}
	
	public static void evaluateWellFounded( String program, String expectedResults ) throws Exception
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();
		
		configuration.ruleSafetyProcessor = new AugmentingRuleSafetyProcessor();
		configuration.evaluationStrategyFactory = new WellFoundedEvaluationStrategyFactory();
		configuration.stratifiers.clear();
		
		executeAndCheckResults( program, expectedResults, configuration, "Well-founded semantics and unsafe rules" );
	}
	
	public static void evaluateSemiNaiveAndOptimisations( String program, String expectedResults ) throws Exception
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();
		
		configuration.programOptmimisers.add( new RuleFilter() );
		configuration.programOptmimisers.add( new MagicSets() );

		executeAndCheckResults( program, expectedResults, configuration, "Semi-Naive and Magic Sets" );
	}
	
	public static void evaluateSLDNF( String program, String expectedResults ) throws Exception
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();
		configuration.evaluationStrategyFactory = new SLDNFEvaluationStrategyFactory();

		executeAndCheckResults( program, expectedResults, configuration, "SLDNF" );
	}
	
	public static void evaluateOLDT( String program, String expectedResults ) throws Exception
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();
		configuration.evaluationStrategyFactory = new OLDTEvaluationStrategyFactory();

		executeAndCheckResults( program, expectedResults, configuration, "OLDT" );
	}
	
	public static void executeAndCheckResults( String program, String expected, Configuration configuration, String evaluationName ) throws Exception
	{
		Parser parser = new Parser();
		parser.parse( program );
		List<IQuery> queries = parser.getQueries();

		assert queries.size() <= 1;
		
		IQuery query = null;
		if( queries.size() == 1 )
			query = queries.get( 0 );
		
		Timer timer = new Timer();

		// Instantiate the knowledge-base
		IKnowledgeBase kb = KnowledgeBaseFactory.createKnowledgeBase( parser.getFacts(), parser.getRules(), configuration );
		
		// Execute the query
		IRelation actualResults = null;
		
		if( query != null )
			actualResults = kb.execute( query );
		
		timer.show( evaluationName + " evaluation" );

		checkResults( expected, actualResults );
	}
	
	private static void checkResults( String expected, IRelation actualResults ) throws Exception
	{
		if ( PRINT_RESULTS )
			if( actualResults != null )
				System.out.println( resultsTostring( actualResults ) );

		if( expected == null || expected.trim().length() == 0 )
		{
			Assert.assertEquals("There are no results expected.",
					0, actualResults == null ? 0 : actualResults.size());
		}
		else
		{
			Assert.assertNotNull("The result was null.", actualResults);

			Parser parser = new Parser();
			parser.parse( expected );

			Map<IPredicate,IRelation> expectedFacts = parser.getFacts();
			Set<IPredicate> predicates = expectedFacts.keySet();
			
			assert predicates.size() == 1;
			IRelation expectedResults = expectedFacts.get( predicates.iterator().next() );
			Assert.assertEquals("The resulting relation is not correct.",
					getTupleSet(expectedResults), getTupleSet(actualResults));
		}
	}

	/**
	 * Transforms a relation to a tuple set.
	 * @param r the relation to transform
	 * @return the tuple set
	 */
	private static Set<ITuple> getTupleSet(final IRelation r) {
		assert r != null: "The relation must not be null";

		final Set<ITuple> result = new HashSet<ITuple>(r.size());
		for (int i = 0, max = r.size(); i < max; i++) {
			result.add(r.get(i));
		}
		return result;
	}

	/**
	 * Evaluate the given logic program with all evaluation strategies and ensure that
	 * each fails with the expected exception. 
	 * @param program The logic program
	 * @param expectedExceptionClass The exception class object expected or null for an
	 * unknown exception type. 
	 */
	public static void checkFailureWithAllStrategies( String program, Class<?> expectedExceptionClass )
	{
		checkFailureWithNaive( program, expectedExceptionClass );
		checkFailureWithSemiNaive( program, expectedExceptionClass );
		checkFailureWithUnSafeRules( program, expectedExceptionClass );
		checkFailureWithWellFounded( program, expectedExceptionClass );
	}
	
	public static void checkFailureWithAllSafeRulesOnly( String program, Class<?> expectedExceptionClass )
	{
		checkFailureWithNaive( program, expectedExceptionClass );
		checkFailureWithSemiNaive( program, expectedExceptionClass );
	}
	
	public static void checkFailureWithNaive( String program, Class<?> expectedExceptionClass )
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();

		configuration.evaluationStrategyFactory = new StratifiedBottomUpEvaluationStrategyFactory( new NaiveEvaluatorFactory() );
		
		checkFailure( program, expectedExceptionClass, configuration, "Naive" );
	}

	public static void checkFailureWithSemiNaive( String program, Class<?> expectedExceptionClass )
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();

		checkFailure( program, expectedExceptionClass, configuration, "Semi-Naive" );
	}

	public static void checkFailureWithUnSafeRules( String program, Class<?> expectedExceptionClass )
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();

		configuration.ruleSafetyProcessor = new AugmentingRuleSafetyProcessor();

		checkFailure( program, expectedExceptionClass, configuration, "Unsafe rules" );
	}

	public static void checkFailureWithWellFounded( String program, Class<?> expectedExceptionClass )
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();

		configuration.ruleSafetyProcessor = new AugmentingRuleSafetyProcessor();
		configuration.evaluationStrategyFactory = new WellFoundedEvaluationStrategyFactory();
		
		checkFailure( program, expectedExceptionClass, configuration, "Well-founded semantics" );
	}

	public static void checkFailureWithOLDT( String program, Class<?> expectedExceptionClass )
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();
		
		configuration.evaluationStrategyFactory = new OLDTEvaluationStrategyFactory();

		checkFailure( program, expectedExceptionClass, configuration, "OLDT" );
	}

	/**
	 * Evaluate the given logic program with all evaluation strategies and ensure that
	 * each fails with the expected exception. 
	 * @param program The logic program
	 * @param expectedExceptionClass The exception class object expected or null for an
	 * unknown exception type. 
	 */
	public static void checkFailure( String knowledgeBase, Class<?> expectedExceptionClass, Configuration configuration, String evaluation )
	{
		try
		{
			// Parse the program (facts and rules)
			Parser parser = new Parser();
			parser.parse( knowledgeBase );
			
			Map<IPredicate,IRelation> facts = parser.getFacts();
			List<IRule> rules = parser.getRules();
			List<IQuery> queries = parser.getQueries();

			assert queries.size() <= 1;
			
			IQuery query = null;
			if( queries.size() == 1 )
				query = queries.get( 0 );
			
			IKnowledgeBase kb = KnowledgeBaseFactory.createKnowledgeBase( facts, rules, configuration );

			kb.execute( query, null );

			Assert.fail( evaluation + " evaluation did not throw the correct exception." );
		}
		catch( Exception e )
		{
			if ( expectedExceptionClass != null )
			{
				Assert.assertTrue( expectedExceptionClass.isInstance( e ) );
			}
		}
	}

	/**
	 * Format logic program evaluation results as a string.
	 * @param results The map of results.
	 * @return The human-readable results.
	 */
	public static String resultsTostring( IRelation results )
	{
		StringBuilder result = new StringBuilder();

		formatResults( result, results );

		return result.toString();
    }

	public static void formatResults( StringBuilder builder, IRelation m )
	{
		for(int t = 0; t < m.size(); ++t )
		{
			ITuple tuple = m.get( t );
			builder.append( tuple.toString() ).append( "\r\n" );
		}
    }
}
