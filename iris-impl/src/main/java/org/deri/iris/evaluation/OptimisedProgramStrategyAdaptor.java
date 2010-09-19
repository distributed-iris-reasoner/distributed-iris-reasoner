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
package org.deri.iris.evaluation;

import java.util.Collections;
import java.util.List;

import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.IProgramOptimisation;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.facts.IFacts;
import org.deri.iris.facts.OriginalFactsPreservingFacts;
import org.deri.iris.storage.IRelation;

/**
 * An evaluation strategy adaptor that uses program optimisations. <br />
 * This adaptor applies the optimisations each time a query is executed.
 * This requires a new evaluation strategy to also be created for each query.
 */
public class OptimisedProgramStrategyAdaptor implements IEvaluationStrategy
{
	/**
	 * Constructor.
	 * @param facts The original program's facts (which will not get modified).
	 * @param rules The original program's rules.
	 * @param configuration The knowledge-base configuration object.
	 */
	public OptimisedProgramStrategyAdaptor( IFacts facts, List<IRule> rules, Configuration configuration )
	{
		mFacts = facts;
		mRules = rules;
		mConfiguration = configuration;
	}
	
	// Need to think about this.
	// This implementation remembers if it failed to optimise anything and then will never try to
	// optimise again. This makes sense for bottom-up evaluation strategies, but not for anything else.
	// However, magic sets is only for optimising bottom-up evaluation......
	public IRelation evaluateQuery( IQuery query, List<IVariable> outputVariables ) throws EvaluationException
	{
		if( mMinimalModelComputed )
		{
			return mStrategy.evaluateQuery( query, outputVariables );
		}
		else
		{
			List<IRule> rules = mRules;
			boolean optimised = false;
	
			for( IProgramOptimisation optimisation : mConfiguration.programOptmimisers )
			{
				IProgramOptimisation.Result result = optimisation.optimise( rules, query );
				
				// If the optimisation succeeded then replace the rules and query with the optimised version. 
				if( result != null )
				{
					query = result.query;
					rules = result.rules;
					optimised = true;
				}
			}

			// Uncomment for dumping the optimised rule set
//			System.out.println( "==================================================================");
//			for( IRule rule : rules ) {
//				System.out.println( rule );
//			}
//			System.out.println( "Rule count: " + rules.size() );
//			System.out.println( "==================================================================");
			
			if( optimised )
			{
				IFacts facts = new OriginalFactsPreservingFacts( mFacts, mConfiguration.relationFactory );
				
				mStrategy = mConfiguration.evaluationStrategyFactory.createEvaluator( facts, rules, mConfiguration );
				
				return mStrategy.evaluateQuery( query, outputVariables );
			}
			else
			{
				// Couldn't optimise at all, so the entire minimal model must be calculated
				// (assuming a bottom-up evaluation strategy is used!)
				mStrategy = mConfiguration.evaluationStrategyFactory.createEvaluator( mFacts, mRules, mConfiguration );
				
				mMinimalModelComputed = true;

				return mStrategy.evaluateQuery( query, outputVariables );
			}
		}
	}

	/** The original facts. */
	private final IFacts mFacts;
	
	/** The original rules. */
	private final List<IRule> mRules;
	
	/** The knowledge-base configuration. */
	private final Configuration mConfiguration;
	
	/** This flag is set of no optimisations can be made. */
	private boolean mMinimalModelComputed = false;
	
	/** The last 'real' evaluation strategy used to answer a query. */ 
	private IEvaluationStrategy mStrategy;
}
