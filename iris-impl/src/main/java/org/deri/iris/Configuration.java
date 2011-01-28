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

import java.util.ArrayList;
import java.util.List;

import org.deri.iris.api.IProgramOptimisation;
import org.deri.iris.evaluation.IEvaluationStrategyFactory;
import org.deri.iris.evaluation.stratifiedbottomup.StratifiedBottomUpEvaluationStrategyFactory;
import org.deri.iris.evaluation.stratifiedbottomup.seminaive.SemiNaiveEvaluatorFactory;
import org.deri.iris.facts.IDataSource;
import org.deri.iris.rules.IRuleHeadEqualityPreProcessor;
import org.deri.iris.rules.IRuleOptimiser;
import org.deri.iris.rules.IRuleReOrderingOptimiser;
import org.deri.iris.rules.IRuleSafetyProcessor;
import org.deri.iris.rules.IRuleStratifier;
import org.deri.iris.rules.IgnoreRuleHeadEquality;
import org.deri.iris.rules.optimisation.JoinConditionOptimiser;
import org.deri.iris.rules.optimisation.ReOrderLiteralsOptimiser;
import org.deri.iris.rules.optimisation.RemoveDuplicateLiteralOptimiser;
import org.deri.iris.rules.optimisation.ReplaceVariablesWithConstantsOptimiser;
import org.deri.iris.rules.ordering.SimpleReOrdering;
import org.deri.iris.rules.safety.StandardRuleSafetyProcessor;
import org.deri.iris.rules.stratification.GlobalStratifier;
import org.deri.iris.rules.stratification.LocalStratifier;
import org.deri.iris.storage.IIndexFactory;
import org.deri.iris.storage.IRelationFactory;
import org.deri.iris.storage.simple.SimpleIndexFactory;
import org.deri.iris.storage.simple.SimpleRelationFactory;
import org.deri.iris.utils.equivalence.IEquivalentTermsFactory;
import org.deri.iris.utils.equivalence.TermEquivalenceFactory;

/**
 * This class holds all configuration data for a knowledge base.
 */
public class Configuration
{
	/** The evaluation strategy to use. */
	public IEvaluationStrategyFactory evaluationStrategyFactory = new StratifiedBottomUpEvaluationStrategyFactory( new SemiNaiveEvaluatorFactory() );

	/** The evaluation timeout in milliseconds. Zero means no timeout. */
	public int evaluationTimeoutMilliseconds = 0;

	/**
	 * The maximum number of tuples that are allowed to be generated during evaluation
	 * before evaluation is terminated.
	 * Zero means that there is no maximum.
	 */
	public int evaluationMaxTuples = 0;
	
	/**
	 * The maximum complexity (tree depth) of inferred constructed terms.
	 * This is a constraint that can avoid the non-convergence problem
	 * for knowledge-bases containing rules such as:
	 * p( f(X) ) :- p( X )
	 */
	public int evaluationMaxComplexity = 0;

	/**
	 * The different options for handling divide by zero in arithmetic built-ins during evaluation.
	 */
	public static enum DivideByZeroBehaviour
	{
		STOP,
		DISCARD_AND_IGNORE
	}
	
	/** The desired divide-by-zero behaviour. */
	public DivideByZeroBehaviour evaluationDivideByZeroBehaviour = DivideByZeroBehaviour.DISCARD_AND_IGNORE;

	/** The factory for creating relations required during evaluation. */
	public IRelationFactory relationFactory = new SimpleRelationFactory();
	
	/** The factory for creating indexes required during evaluation. */
	public IIndexFactory indexFactory = new SimpleIndexFactory();

	/** The number of bits of precision to use for comparing double term values. */ 
	public int floatingPointDoublePrecision = 42;

	/** The number of bits of precision to use for comparing float term values. */ 
	public int floatingPointFloatPrecision = 19;
	
	/** Add external data sources here. */
	public final List<IDataSource> externalDataSources = new ArrayList<IDataSource>();
	
	/** The collection of rule set stratifiers. */
	public final List<IRuleStratifier> stratifiers = new ArrayList<IRuleStratifier>();
	
	/** The collection of rule optimisers. */
	public final List<IRuleOptimiser> ruleOptimisers = new ArrayList<IRuleOptimiser>();
	
	/** The collection of rule-reordering optimisers. */
	public IRuleReOrderingOptimiser reOrderingOptimiser = new SimpleReOrdering();
	
	/** Collection of program optimisations. */
	public final List<IProgramOptimisation> programOptmimisers = new ArrayList<IProgramOptimisation>();
	
	/** Rule safety processors (e.g. standard rule-safety check and augmented-unsafe-rule modifier). */
	public IRuleSafetyProcessor ruleSafetyProcessor = new StandardRuleSafetyProcessor();
	
	/**	The rule head equality pre-processor. */
	public IRuleHeadEqualityPreProcessor ruleHeadEqualityPreProcessor = new IgnoreRuleHeadEquality();
	
	/** The equivalent terms factory to use. */
	public IEquivalentTermsFactory equivalentTermsFactory = new TermEquivalenceFactory();
	
	/**
	 * Constructor.
	 */
	public Configuration()
	{
		//Not applicable for distributed evaluation
//		stratifiers.add( new GlobalStratifier() );
//		stratifiers.add( new LocalStratifier( true ) );
//		stratifiers.add( new LocalStratifier( false ) );
		
		ruleOptimisers.add( new JoinConditionOptimiser() );
		ruleOptimisers.add( new ReplaceVariablesWithConstantsOptimiser() );
		ruleOptimisers.add( new ReOrderLiteralsOptimiser() );
		ruleOptimisers.add( new RemoveDuplicateLiteralOptimiser() );
	}
}
