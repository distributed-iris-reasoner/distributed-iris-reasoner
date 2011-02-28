/*
 * Copyright 2010 Softgress - http://www.softgress.com/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.larkc.iris.evaluation.bottomup;

import java.util.ArrayList;
import java.util.List;

import org.deri.iris.EvaluationException;
import org.deri.iris.ProgramNotStratifiedException;
import org.deri.iris.RuleUnsafeException;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.evaluation.IEvaluationStrategy;
import org.deri.iris.evaluation.stratifiedbottomup.EvaluationUtilities;
import org.deri.iris.storage.IRelation;

import eu.larkc.iris.rules.IRecursiveRulePreProcessor;
import eu.larkc.iris.rules.NonOptimizingRecursiveRulePreProcessor;
import eu.larkc.iris.rules.compiler.CascadingRuleCompiler;
import eu.larkc.iris.rules.compiler.IDistributedCompiledRule;
import eu.larkc.iris.rules.compiler.IDistributedRuleCompiler;

/**
 * Iris bottom up evaluation strategy adapter for our distributed environment
 * 
 * @History 14.09.2010 fisf, Creation
 * @author Florian Fischer
 */
public class DistributedBottomUpEvaluationStrategy implements
		IEvaluationStrategy {

	//private static final Logger logger = LoggerFactory.getLogger(DistributedBottomUpEvaluationStrategy.class);
	
	/** 
	 * @param configuration
	 * @param ruleEvaluatorFactory
	 * @param rules
	 * @param facts
	 */
	public DistributedBottomUpEvaluationStrategy(eu.larkc.iris.Configuration configuration,
			IDistributedRuleEvaluatorFactory ruleEvaluatorFactory, List<IRule> rules) {
		this.mRuleEvaluatorFactory = ruleEvaluatorFactory;
		this.mConfiguration = configuration;
		this.mRules = rules;
		this.utils = new EvaluationUtilities(mConfiguration);
	}

	/*
	 * @see
	 * org.deri.iris.evaluation.IEvaluationStrategy#evaluateQuery(org.deri.iris
	 * .api.basics.IQuery, java.util.List)
	 */
	@Override
	public IRelation evaluateQuery(IQuery query, List<IVariable> outputVariables)
			throws ProgramNotStratifiedException, RuleUnsafeException,
			EvaluationException {
			
		initBottomUpEvaluation(mRules);
		
		return null;		
	}

	/**
	 * Starts a distributed bottom up evaluation of a rule set.
	 * 
	 * @param rules
	 * @throws EvaluationException
	 */
	public void initBottomUpEvaluation(List<IRule> rules) throws EvaluationException {		
		
		List<IRule> safeRules = utils.applyRuleSafetyProcessor(rules);		
		
		//TODO (fisf): make it possible to enable/disable this with a switch on mConfiguration
		//this should be encapsulated in EvaluationUtilities when there are multiple implementations that e.g. can be chained
		IRecursiveRulePreProcessor recursiveRuleProcessor = new NonOptimizingRecursiveRulePreProcessor();
		recursiveRuleProcessor.process(safeRules);
		List<IRule> singlePassRules = recursiveRuleProcessor.getNonrecursive();
		List<IRule> recursiveRules = recursiveRuleProcessor.getRecursive();
		
		evaluateSinglePassRules(singlePassRules);
		
		evaluateRecursiveRules(recursiveRules);	
	}
	
	protected void evaluateSinglePassRules(List<IRule> rules) throws EvaluationException {	
		IDistributedRuleEvaluator singlePass = mRuleEvaluatorFactory.createEvaluator(IDistributedRuleEvaluatorFactory.SINGLEPASSEVALUATOR);
		IDistributedRuleCompiler rc = new CascadingRuleCompiler(mConfiguration);
		
		List<IRule> reorderedRules = utils.reOrderRules(rules);		
		List<IRule> optimisedRules = utils.applyRuleOptimisers(reorderedRules);

		List<IDistributedCompiledRule> compiledRules = new ArrayList<IDistributedCompiledRule>();
		
		for (IRule rule : optimisedRules) {
			IDistributedCompiledRule compiledRule = rc.compile(rule);
			compiledRules.add(compiledRule);
		}

		singlePass.evaluateRules(null, compiledRules, mConfiguration);		
	}
	
	protected void evaluateRecursiveRules(List<IRule> rules) throws EvaluationException {		
		//Done by DependencyMinimizingStratifier
		List<List<IRule>> stratifiedRules = utils.stratify(rules);
	
		IDistributedRuleCompiler rc = new CascadingRuleCompiler(mConfiguration);		
		IDistributedRuleEvaluator evaluator = mRuleEvaluatorFactory.createEvaluator(IDistributedRuleEvaluatorFactory.RECURSIONAWAREEVALUATOR);		
		//PredicateCounts predicateCounts = PredicateCounts.getInstance(mConfiguration);
		
		//process each rule layer independently, no recomputations outside of each layer are required
		Integer stratumNumber = 1;
		for (List<IRule> stratum : stratifiedRules) {
			
			//reorder rules within stratum
			List<IRule> reorderedRules = utils.reOrderRules(stratum);		
			List<IRule> optimisedRules = utils.applyRuleOptimisers(reorderedRules);

			List<IDistributedCompiledRule> compiledRules = new ArrayList<IDistributedCompiledRule>();			
			for (IRule rule : optimisedRules) {
				IDistributedCompiledRule compiledRule = rc.compile(rule);
				compiledRules.add(compiledRule);
			}

			evaluator.evaluateRules(stratumNumber, compiledRules, mConfiguration);
			
			stratumNumber++;
		}
	}
	
	protected EvaluationUtilities utils;

	protected final IDistributedRuleEvaluatorFactory mRuleEvaluatorFactory;

	protected final List<IRule> mRules;

	protected final eu.larkc.iris.Configuration mConfiguration;
	
}
