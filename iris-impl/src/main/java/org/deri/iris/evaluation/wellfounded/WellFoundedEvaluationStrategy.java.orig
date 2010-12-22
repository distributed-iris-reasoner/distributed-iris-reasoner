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
package org.deri.iris.evaluation.wellfounded;

import java.util.ArrayList;
import java.util.List;

import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.evaluation.IEvaluationStrategy;
import org.deri.iris.evaluation.stratifiedbottomup.EvaluationUtilities;
import org.deri.iris.evaluation.stratifiedbottomup.IRuleEvaluator;
import org.deri.iris.evaluation.stratifiedbottomup.naive.NaiveEvaluator;
import org.deri.iris.evaluation.stratifiedbottomup.seminaive.SemiNaiveEvaluator;
import org.deri.iris.facts.FiniteUniverseFacts;
import org.deri.iris.facts.IFacts;
import org.deri.iris.rules.RuleHeadEquality;
import org.deri.iris.rules.compiler.ICompiledRule;
import org.deri.iris.rules.compiler.IRuleCompiler;
import org.deri.iris.rules.compiler.RuleCompiler;
import org.deri.iris.storage.IRelation;
import org.deri.iris.utils.equivalence.IEquivalentTerms;

/**
 * A well-founded evaluation strategy that uses an alternating fixed-point procedure.
 * TODO This is a first implementation and needs to be significantly improved, particularly
 * in the area of rule-compilation (should only be done once - requires a reset() method on ICompiledRule)
 */
public class WellFoundedEvaluationStrategy implements IEvaluationStrategy
{

	/**
	 * Constructor.
	 * @param facts The starting facts and the storage ares for deduced facts.
	 * @param rules The starting rule set.
	 * @param configuration The knowledge-base configuration object.
	 * @throws EvaluationException
	 */
	WellFoundedEvaluationStrategy( IFacts facts, List<IRule> rules, Configuration configuration )
	                throws EvaluationException
	{
		if( facts == null )
			throw new IllegalArgumentException( "'facts' argument must not be null.");
		
		if( rules == null )
			throw new IllegalArgumentException( "'rules' argument must not be null.");
		
		if( configuration == null )
			throw new IllegalArgumentException( "'configuration' argument must not be null.");
		
		mConfiguration = configuration;
//		mFacts = facts;
		mEquivalentTerms = mConfiguration.equivalentTermsFactory.createEquivalentTerms();

		List<IRule> allRules = mConfiguration.ruleHeadEqualityPreProcessor
				.process(rules, facts);

		EvaluationUtilities utils = new EvaluationUtilities( mConfiguration );

		// Re-order stratum
		List<IRule> reorderedRules = utils.reOrderRules( allRules );

		// Rule optimisation
//		List<IRule> optimisedRules = utils.optimiseRules( reorderedRules );

		mFacts = calculateWellFoundedModel( reorderedRules, facts );

	}
	
	/**
	 * Compile rules.
	 * @param rules Input rules.
	 * @param facts Input facts.
	 * @return The compiled rules.
	 * @throws EvaluationException If a rule can not be compiled (e.g. if a rule is unsafe).
	 */
	private List<ICompiledRule> compile( List<IRule> rules, IFacts facts ) throws EvaluationException
	{
		assert rules != null;
		assert facts != null;
		assert mConfiguration != null;
		
		List<ICompiledRule> compiledRules = new ArrayList<ICompiledRule>();
		
		IRuleCompiler rc = new RuleCompiler( facts, mEquivalentTerms, mConfiguration );

		for( IRule rule : rules )
			compiledRules.add( rc.compile( rule ) );
		
		return compiledRules;
	}

	/**
	 * Run the alternating fixed point algorithm until the model for the positive
	 * program does not grow.
	 * @param rules The rules from the original program.
	 * @param startingFacts The facts from the original program.
	 * @return The well-founded model for the original program.
	 * @throws EvaluationException If a rule can not be compiled (e.g. if a rule is unsafe)
	 */
	private final IFacts calculateWellFoundedModel( List<IRule> rules, IFacts startingFacts )
	                throws EvaluationException
	{
		assert rules != null;
		assert startingFacts != null;
		assert mConfiguration != null;

		EvaluationUtilities utils = new EvaluationUtilities( mConfiguration );
		ProgramDoubler doubler = new ProgramDoubler( rules, startingFacts, mConfiguration );

		// Compile all the rules
		List<IRule> startingRules = doubler.getStartingRuleBase();
		startingRules = utils.applyRuleSafetyProcessor( startingRules );
		startingRules = utils.applyRuleOptimisers( startingRules );

//		List<ICompiledRule> startingCompiledRules = compile( startingRules );

		List<IRule> negativeRules = doubler.getNegativeRuleBase();
		negativeRules = utils.applyRuleSafetyProcessor( negativeRules );
		negativeRules = utils.applyRuleOptimisers( negativeRules );
//		List<ICompiledRule> negativeCompiledRules = compile( negativeRules );

		List<IRule> positiveRules = doubler.getPositiveRuleBase();
		positiveRules = utils.applyRuleSafetyProcessor( positiveRules );
		positiveRules = utils.applyRuleOptimisers( positiveRules );
//		List<ICompiledRule> positiveCompiledRules = compile( positiveRules );
		

		// Do starting positive evaluation.
		IFacts simpleFacts = doubler.getPositiveStartingFacts();
		IFacts finiteFacts = new FiniteUniverseFacts( simpleFacts, startingRules );

		List<ICompiledRule> startingCompiledRules = compile( startingRules, finiteFacts );
		
		// TODO Enable rule head equality support for semi-naive evaluation.
		// Choose the correct evaluation technique for the specified rules.
		IRuleEvaluator evaluator = chooseEvaluator(rules);
		
		evaluator.evaluateRules( startingCompiledRules, finiteFacts, mConfiguration );

		int currentDefinitelyTrueSize = size( simpleFacts );

		// Keep these facts and re-use. The positive side is monotonic
		// increasing,
		// so we don't need to throw away and start again each time.
		// However, the negative side is monotonic decreasing, so we do have to
		// throw away each time.

		for( ;; )
		{
			// Do negative evaluation

			// simpleFacts = doubler.extractPositiveFacts( simpleFacts );
			merge( simpleFacts, doubler.getNegativeStartingFacts() );
			finiteFacts = new FiniteUniverseFacts( simpleFacts, negativeRules );

			List<ICompiledRule> negativeCompiledRules = compile( negativeRules, finiteFacts );
			evaluator.evaluateRules( negativeCompiledRules, finiteFacts, mConfiguration );
			simpleFacts = doubler.extractNegativeFacts( simpleFacts );

			// Do positive evaluation

			// simpleFacts = doubler.extractNegativeFacts( simpleFacts );
			merge( simpleFacts, doubler.getPositiveStartingFacts() );
			finiteFacts = new FiniteUniverseFacts( simpleFacts, positiveRules );

			List<ICompiledRule> positiveCompiledRules = compile( positiveRules, finiteFacts );
			evaluator.evaluateRules( positiveCompiledRules, finiteFacts, mConfiguration );
			simpleFacts = doubler.extractPositiveFacts( simpleFacts );

			int newDefinitelyTrueSize = size( simpleFacts );
			if( newDefinitelyTrueSize == currentDefinitelyTrueSize )
			{
				return simpleFacts;
			}
			currentDefinitelyTrueSize = newDefinitelyTrueSize;
		}
	}

	private IRuleEvaluator chooseEvaluator(List<IRule> rules) {
		for (IRule rule : rules) {
			if (RuleHeadEquality.hasRuleHeadEquality(rule)) {
				return new NaiveEvaluator();
			}
		}
		
		return new SemiNaiveEvaluator();
	}
	
	/**
	 * Add one collection of facts to another.
	 * @param target The facts to append to.
	 * @param source The facts to append.
	 */
	private final void merge( IFacts target, IFacts source )
	{
		assert target != null;
		assert source != null;

		for( IPredicate predicate : source.getPredicates() )
		{
			target.get( predicate ).addAll( source.get( predicate ) );
		}
	}

	/**
	 * Compute the number of facts in a collection of facts.
	 * @param facts The facts collection to 'count'.
	 * @return The number of individual facts.
	 */
	private final int size( IFacts facts )
	{
		assert facts != null;

		int numTuples = 0;
		for( IPredicate predicate : facts.getPredicates() )
		{
			numTuples += facts.get( predicate ).size();
		}

		return numTuples;
	}

	public IRelation evaluateQuery( IQuery query, List<IVariable> outputVariables ) throws EvaluationException
	{
		if( query == null )
			throw new IllegalArgumentException( "StratifiedBottomUpEvaluationStrategy.evaluateQuery() - query must not be null." ); 

		if( outputVariables == null )
			throw new IllegalArgumentException( "StratifiedBottomUpEvaluationStrategy.evaluateQuery() - outputVariables must not be null." ); 

		RuleCompiler compiler = new RuleCompiler( mFacts, mEquivalentTerms, mConfiguration );

		ICompiledRule compiledQuery = compiler.compile( query );

		IRelation result = compiledQuery.evaluate();

		outputVariables.clear();
		outputVariables.addAll( compiledQuery.getVariablesBindings() );

		return result;
	}

	/** The equivalent terms. */
	private IEquivalentTerms mEquivalentTerms;
	
	/** The knowledge base configuration object. */
	private final Configuration mConfiguration;

	/** The collection of facts that holds the well-founded model. */
	private final IFacts mFacts;
}
