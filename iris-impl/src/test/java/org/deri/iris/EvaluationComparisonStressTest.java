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
package org.deri.iris;

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.BUILTIN;
import static org.deri.iris.factory.Factory.CONCRETE;
import static org.deri.iris.factory.Factory.TERM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.evaluation.stratifiedbottomup.StratifiedBottomUpEvaluationStrategyFactory;
import org.deri.iris.evaluation.stratifiedbottomup.seminaive.SemiNaiveEvaluatorFactory;
import org.deri.iris.evaluation.topdown.oldt.OLDTEvaluationStrategyFactory;
import org.deri.iris.evaluation.wellfounded.WellFoundedEvaluationStrategyFactory;
import org.deri.iris.optimisations.magicsets.MagicSets;
import org.deri.iris.optimisations.rulefilter.RuleFilter;
import org.deri.iris.rules.RuleValidator;
import org.deri.iris.rules.safety.AugmentingRuleSafetyProcessor;
import org.deri.iris.rules.stratification.GlobalStratifier;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.Relations;
import org.deri.iris.storage.simple.SimpleRelationFactory;

/**
 * Generate random logic programs and compare the evaluation results using
 * different evaluation criterion.
 */
public class EvaluationComparisonStressTest extends TestCase
{
	public static boolean SHOW_FAILURE_PROGRAMS = true;
	
	public static final int NUM_KNOWLEDGEBASES = 120;
	
	public static final int NUM_FACTS_PER_PREDICATE = 6;
	public static final int MAX_ARITY = 5;
	public static final int MAX_TERM_VALUE = 5;

	public static final int MAX_RULES = 20;
	public static final int PERCENT_VARIABLES_IN_RULE_HEAD = 80;
	public static final int MAX_RULE_BODY_LITERALS = 5;
	public static final int MAX_RULE_VARIABLES = 8;
	public static final int PERCENT_NEGATED_LITERALS_IN_RULE = 20;
	public static final int PERCENT_EDB_PREDICATES_IN_RULE = 70;
	public static final int PERCENT_VARIABLES_IN_RULE_BODY = 70;
	public static final int MAX_UNLIMITED_VARIABLES = 2;
	public static final int MAX_UNSAFE_RULES = 2;
	public static final int PERCENT_BUILTINS_IN_RULE_BODY = 20;

	public static final int NUM_QUERIES = 10;
	public static final int MAX_QUERY_LITERALS = 5;
	public static final int MAX_QUERY_VARIABLES = 8;
	public static final int PERCENT_NEGATED_LITERALS_IN_QUERY = 30;
	public static final int PERCENT_EDB_PREDICATES_IN_QUERY = 70;
	public static final int PERCENT_VARIABLES_IN_QUERY = 70;
	public static final int PERCENT_BUILTINS_IN_QUERY = 20;
	
	public static final String IDB = "i";
	public static final String EDB = "e";
	public static final String VARIABLE = "v";
	
	// ========================================================================
	
	private boolean mGenerateSafeRulesOnly;
	
	private Random mRandom;
	
	private int mNumPrograms;
	private int mNumValidPrograms;
	private int mNumValidProgramsWithOutput;
	private int mNumTotalFailures;
	
	enum Strategy {
		SemiNaive,
		WellFounded,
		OLDT
	}
	
	private List<Config> mConfigs;
	
	class Config
	{
		Config( Configuration configuration, String name )
		{
			this.configuration = configuration;
			this.name = name;
		}
		
		final Configuration configuration;
		final String name;
		int comparisonFailures = 0;
		int evaluationFailures = 0;
	}
	
	@Override
    protected void setUp() throws Exception
    {
		// Always the same seed in order to be deterministic, i.e always generate the same number sequence
		mRandom = new Random( 0 );
		
		mNumPrograms = 0;
		mNumValidPrograms = 0;
		mNumValidProgramsWithOutput = 0;
		mNumTotalFailures = 0;
		
		mGenerateSafeRulesOnly = false;
		
		mConfigs = new ArrayList<Config>();
    }

	private Map<IPredicate,IRelation> createFacts()
	{
		Map<IPredicate,IRelation> facts = new HashMap<IPredicate,IRelation>();

		for( int arity = 0; arity <= MAX_ARITY; ++arity )
		{
			IRelation relation = new SimpleRelationFactory().createRelation();
			
			ITerm[] terms = new ITerm[ arity ];
			
			for( int fact = 0; fact < NUM_FACTS_PER_PREDICATE; ++fact )
			{
				for( int t = 0; t < arity; ++t )
				{
					terms[ t ] = getTerm();
				}
				
				relation.add( BASIC.createTuple( terms ) );
			}
			
			IPredicate predicate = predicate( false, arity );
			facts.put( predicate, relation );
		}
		
		return facts;
	}
	
	private int random( int limit )
	{
		return mRandom.nextInt( limit );
	}
	
	private int percentage()
	{
		return random( 100 );
	}
	
	private String idbName( int arity )
	{
		return IDB + arity;
	}
	
	private String edbName( int arity )
	{
		return EDB + arity;
	}
	
	private IPredicate predicate( boolean edb, int arity )
	{
		return BASIC.createPredicate( edb ? edbName( arity ) : idbName( arity ), arity );
	}
	
	private String variableName( int index )
	{
		return VARIABLE + index;
	}
	
	private ITerm getTerm()
	{
		return CONCRETE.createInteger( random( MAX_TERM_VALUE + 1 ) );
	}
	
	private IVariable getVariable( int index )
	{
		return TERM.createVariable( variableName( index ) );
	}

	private List<IRule> createRules()
	{
		boolean generateSafeRulesOnly = mGenerateSafeRulesOnly;
		int numUnsafeRules = 0;

		List<IRule> rules = new ArrayList<IRule>();
		
		int numRules = random( MAX_RULES ) + 1;
		
		for( int r = 0; r < numRules; ++r )
		{
			// Head
			int headArity = random( MAX_ARITY + 1 );

			List<ILiteral> headLiterals = new ArrayList<ILiteral>();
			ILiteral headLiteral = literal( true, headArity, true, PERCENT_VARIABLES_IN_RULE_HEAD, MAX_RULE_VARIABLES );
			headLiterals.add( headLiteral );
			
			// Body
			int numLiterals = random( MAX_RULE_BODY_LITERALS ) + 1;
			List<ILiteral> bodyLiterals = new ArrayList<ILiteral>();
			
			for( int l = 0; l < numLiterals; ++l )
			{
				boolean positive = percentage() < 100 - PERCENT_NEGATED_LITERALS_IN_RULE;

				if( percentage() < PERCENT_BUILTINS_IN_RULE_BODY )
				{
					bodyLiterals.add( builtinLiteral( positive, PERCENT_VARIABLES_IN_RULE_BODY, MAX_RULE_VARIABLES ) );
				}
				else
				{
					boolean edb = percentage() < PERCENT_EDB_PREDICATES_IN_RULE;
					int arity = random( MAX_ARITY ) + 1;
					
					bodyLiterals.add( literal( edb, arity, positive, PERCENT_VARIABLES_IN_RULE_BODY, MAX_RULE_VARIABLES ) );
				}
			}
			
			IRule rule;
			
			rule = BASIC.createRule( headLiterals, bodyLiterals );
			
			RuleValidator rv = new RuleValidator( rule, true, true );

			List<IVariable> unlimitedVariables = rv.getAllUnlimitedVariables();

			// Check if we have an unsafe rule
			if( unlimitedVariables.size() > 0 )
				++numUnsafeRules;
			
			if( numUnsafeRules > MAX_UNSAFE_RULES )
				generateSafeRulesOnly = true;
			
			if( ! generateSafeRulesOnly )
			{
//				if( unlimitedVariables.size() > MAX_UNLIMITED_VARIABLES )
				for( int i = 0; i < MAX_UNLIMITED_VARIABLES; ++i )
					if( unlimitedVariables.size() > 0 )
						unlimitedVariables.remove( 0 );
			}
			
			if( unlimitedVariables.size() > 0 )
			{
				List<ITerm> terms = new ArrayList<ITerm>();
				
				for( IVariable variable : unlimitedVariables )
					terms.add( variable );
				
				ITuple tuple = BASIC.createTuple( terms );
				
				ILiteral literal = BASIC.createLiteral( true, predicate( false, terms.size() ), tuple );
				bodyLiterals.add( literal );
			}
			
//			while( unlimitedVariables.size() > MAX_UNLIMITED_VARIABLES || 
//							( mGenerateSafeRulesOnly && unlimitedVariables.size() > 0 ) )
//			{
//				IVariable variable = unlimitedVariables.remove( 0 );
//
//				IPredicate predicate = predicate( false, 1 );
//				
//				ITerm[] terms = new ITerm[ 1 ];
//				terms[ 0 ] = variable;
//				
//				ITuple tuple = BASIC.createTuple( terms );
//				
//				ILiteral literal = BASIC.createLiteral( true, predicate, tuple );
//				bodyLiterals.add( literal );
//			}
			
			rule = BASIC.createRule( headLiterals, bodyLiterals );
			
			rules.add( rule );
		}
		
		return rules;
	}
	
	private ITuple tuple( int arity, int percentVariables, int maxVariables )
	{
		ITerm[] terms = new ITerm[ arity ];

		for( int t = 0; t < arity; ++t )
		{
			ITerm term;

			if( percentage() < percentVariables )
				term = getVariable( random( maxVariables ) );
			else
				term = getTerm();
			
			terms[ t ] = term;
		}
		
		return BASIC.createTuple( terms );
	}
	
	private ILiteral literal( boolean edb, int arity, boolean positive, int percentVariables, int maxVariables )
	{
		IPredicate predicate = predicate( edb, arity );
		
		ITuple tuple = tuple( arity, percentVariables, maxVariables );
		
		return BASIC.createLiteral( positive, predicate, tuple );
	}
	
	private ILiteral builtinLiteral( boolean positive, int percentVariables, int maxVariables )
	{
		ITuple tuple = tuple( 2, percentVariables, maxVariables );

		IAtom builtinAtom;
		if( percentage() < 50 ) 
			builtinAtom = BUILTIN.createEqual( tuple.get( 0 ), tuple.get( 1 ) );
		else
			builtinAtom = BUILTIN.createUnequal( tuple.get( 0 ), tuple.get( 1 ) );
		
		return BASIC.createLiteral( positive, builtinAtom );
	}
	
	private IQuery createQueryFromRuleHead( IRule rule ) {
		ILiteral ruleHead = rule.getHead().get( 0 );
		
		IPredicate predicate = ruleHead.getAtom().getPredicate();
		int arity = predicate.getArity();
		ITerm[] terms = new ITerm[ arity ];
		for( int a = 0; a < arity; ++a )
			terms[ a ] = getVariable( a );
		ITuple tuple = BASIC.createTuple( terms );
		ILiteral queryLiteral = BASIC.createLiteral( true, predicate, tuple );

		List<ILiteral> queryLiterals = new ArrayList<ILiteral>();
		queryLiterals.add( queryLiteral );
		
		return BASIC.createQuery( queryLiterals );
	}
	
	private IQuery createQuery()
	{
		int numLiterals = random( MAX_QUERY_LITERALS ) + 1;
		List<ILiteral> literals = new ArrayList<ILiteral>();
		
		for( int l = 0; l < numLiterals; ++l )
		{
			boolean positive = percentage() < 100 - PERCENT_NEGATED_LITERALS_IN_QUERY;
			
			if( percentage() < PERCENT_BUILTINS_IN_QUERY )
			{
				ILiteral literal = builtinLiteral( positive, PERCENT_VARIABLES_IN_QUERY, MAX_QUERY_VARIABLES );
				literals.add( literal );
				
				// Ensure that all variables of built-in are bound
				if( mGenerateSafeRulesOnly )
				{
					for( ITerm term : literal.getAtom().getTuple() )
					{
						if( term instanceof IVariable )
						{
							IPredicate predicate = predicate( false, 1 );
							
							ITuple tuple = BASIC.createTuple( term );
							
							literals.add( 0, BASIC.createLiteral( true, predicate, tuple ) );
						}
					}
				}
			}
			else
			{
				boolean edb = percentage() < PERCENT_EDB_PREDICATES_IN_QUERY;
				int arity = random( MAX_ARITY ) + 1;
				
				ILiteral literal = literal( edb, arity, positive, PERCENT_VARIABLES_IN_QUERY, MAX_QUERY_VARIABLES );
				literals.add( literal );
			}
		}
		
		return BASIC.createQuery( literals );
	}
	
	public void testCompareWithDefault() throws Exception
	{
		mGenerateSafeRulesOnly = true;
		
		makeConfiguration( Strategy.SemiNaive, false, false );
		makeConfiguration( Strategy.SemiNaive, true, false );
		makeConfiguration( Strategy.WellFounded, false, false );
		makeConfiguration( Strategy.WellFounded, true, false );
		
		doComparisons();
		outputResults();
		
		assertEquals( 0, mNumTotalFailures );
	}

	public void testCompareWithDefaultUnsafeRules() throws Exception
	{
		mGenerateSafeRulesOnly = false;
		
		makeConfiguration( Strategy.SemiNaive, false, true );
		makeConfiguration( Strategy.SemiNaive, true, true );
		makeConfiguration( Strategy.WellFounded, false, true );
		makeConfiguration( Strategy.WellFounded, true, true );
		
		doComparisons();
		outputResults();
		
		assertEquals( 0, mNumTotalFailures );
	}
	
	private void makeConfiguration( Strategy strategy, boolean magicSets, boolean unsafeRules )
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();

		// ***** TEMPORARY UNTIL THE LOCAL STRATIFIER INFINITE LOOP BUG IS FIXED ***** 
		configuration.stratifiers.clear();
		configuration.stratifiers.add( new GlobalStratifier() );
		// ***************************************************************************
		
		String name = "";

		switch( strategy ) {
		case SemiNaive:
			configuration.evaluationStrategyFactory = new StratifiedBottomUpEvaluationStrategyFactory( new SemiNaiveEvaluatorFactory() );
			name += "Semi-naive bottom-up";
			break;
			
		case WellFounded:
			configuration.evaluationStrategyFactory = new WellFoundedEvaluationStrategyFactory();
			configuration.stratifiers.clear();
			name += "Well-founded semantics (bottom-up)";
			break;

		case OLDT:
			configuration.evaluationStrategyFactory = new OLDTEvaluationStrategyFactory();
			name += "OLDT";
			break;
		}
		
		if( magicSets )
		{
			configuration.programOptmimisers.add( new RuleFilter() );
			configuration.programOptmimisers.add( new MagicSets() );
			name += ", Magic sets";
		}
		
		if( unsafeRules )
		{
			configuration.ruleSafetyProcessor = new AugmentingRuleSafetyProcessor();
			name += ", Unsafe Rules";
		}
		
		mConfigs.add( new Config( configuration, name ) );
	}

	public void testCompareWithWellFounded() throws Exception
	{
		mGenerateSafeRulesOnly = true;
		
		makeConfiguration( Strategy.WellFounded, false, false );
		makeConfiguration( Strategy.WellFounded, true, false );
		
		doComparisons();
		outputResults();
		
		assertEquals( 0, mNumTotalFailures );
	}

	public void testCompareWithWellFoundedUnsafeRules() throws Exception
	{
		mGenerateSafeRulesOnly = false;
		
		makeConfiguration( Strategy.WellFounded, false, true );
		makeConfiguration( Strategy.WellFounded, true, true );
		
		doComparisons();
		outputResults();
		
		assertEquals( 0, mNumTotalFailures );
	}

	public void testCompareSemiNaiveAndOLDT() throws Exception
	{
		mGenerateSafeRulesOnly = false;
		
		makeConfiguration( Strategy.SemiNaive, false, false );
		makeConfiguration( Strategy.OLDT, false, false );
		
		doComparisons();
		outputResults();
		
		assertEquals( 0, mNumTotalFailures );
	}

	private void outputResults()
	{
		System.out.println( "Number of programs generated:         " + mNumPrograms );
		System.out.println( "Number of valid programs:             " + mNumValidPrograms );
		System.out.println( "Number of valid programs with output: " + mNumValidProgramsWithOutput );
		
		System.out.println( "Comparing with: " + mConfigs.get( 0 ).name );

		mNumTotalFailures = 0;
		for( int c = 1; c < mConfigs.size(); ++c  )
		{
			Config config = mConfigs.get( c );
			
			mNumTotalFailures += config.comparisonFailures;
			mNumTotalFailures += config.evaluationFailures;
				
			System.out.println( c + ") " + config.name + ": " +
							config.comparisonFailures + " comparison failure(s), " +
							config.evaluationFailures + " evaluation failure(s)" );
		}
	}
	
	private void doComparisons()
	{
		for( int r = 0; r < NUM_KNOWLEDGEBASES; ++r )
		{
			List<IRule> rules = createRules();
			
			Map<IPredicate,IRelation> facts = createFacts();

			// Do random conjunctive queries
			for( int q = 0; q < NUM_QUERIES; ++q )
			{
				IQuery query = createQuery();
				
				compareEvaluations( query, rules, facts );
			}
			
			// And query every rule head as well
			Set<IQuery> ruleHeadQueries = new HashSet<IQuery>();
			for( IRule rule : rules )
				ruleHeadQueries.add( createQueryFromRuleHead( rule ) );
			
			for( IQuery query : ruleHeadQueries )
				compareEvaluations( query, rules, facts );
		}
	}
	
	private void compareEvaluations( IQuery query, List<IRule> rules, Map<IPredicate,IRelation> facts )
	{
		// ==================================
		// Normal
		IRelation reference;
		
		assert mConfigs.size() > 1;
		
		++mNumPrograms;
		try
		{
			reference = evaluate( query, rules, facts, mConfigs.get( 0 ).configuration );
			++mNumValidPrograms;
			
			if( reference.size() > 0 )
				++mNumValidProgramsWithOutput;
		}
		catch( Exception e )
		{
			return;
		}
		
		for( int c = 1; c < mConfigs.size(); ++c  )
		{
			Config config = mConfigs.get( c );
			try
			{
				IRelation output = evaluate( query, rules, facts, config.configuration );
				
				if( ! same( reference, output ) )
				{
					++config.comparisonFailures;
					outputProgram( query, rules, facts, config.name, "Different query results" );
				}
			}
			catch( Exception e )
			{
				++config.evaluationFailures;
				outputProgram( query, rules, facts, config.name, e.toString() );
			}
		}
	}

	private void outputProgram( IQuery query, List<IRule> rules, Map<IPredicate,IRelation> facts, String evaluation, String message )
	{
		System.out.println( "=============================================" );
		System.out.println( evaluation );
		System.out.println( message );
		
		if( SHOW_FAILURE_PROGRAMS )
		{
			System.out.println();
			System.out.println( toString( query, rules, facts ) );
		}
	}
	
	private String toString( IQuery query, List<IRule> rules, Map<IPredicate,IRelation> facts )
	{
		String lineFeed = System.getProperty("line.separator");
		
		final StringBuilder buffer = new StringBuilder();

		buffer.append(query).append(lineFeed).append(lineFeed);

		for (final IRule rule : rules)
			buffer.append(rule).append(lineFeed);
		buffer.append(lineFeed);

		for (final Map.Entry<IPredicate, List<ITuple>> entry
				: Relations.toPredicateListMapping(facts).entrySet())
		{
			for (final ITuple tuple : entry.getValue())
			{
				buffer.append(entry.getKey()).append(tuple).append(".");
				buffer.append(lineFeed);
			}
		}
		buffer.append(lineFeed);

		return buffer.toString();
	}
	
	private boolean same( IRelation a, IRelation b )
	{
		return Relations.asSet( a ).equals( Relations.asSet( b ) );
	}

	private IRelation evaluate( IQuery query, List<IRule> rules, Map<IPredicate,IRelation> facts, Configuration configuration ) throws Exception
	{
		IKnowledgeBase kb = KnowledgeBaseFactory.createKnowledgeBase( facts, rules, configuration );
		
		return kb.execute( query );
	}
}
