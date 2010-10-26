/**
 * 
 */
package eu.larkc.iris.evaluation.distributed;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
import org.deri.iris.ProgramNotStratifiedException;
import org.deri.iris.RuleUnsafeException;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.evaluation.IEvaluationStrategy;
import org.deri.iris.evaluation.stratifiedbottomup.EvaluationUtilities;
import org.deri.iris.evaluation.stratifiedbottomup.IRuleEvaluator;
import org.deri.iris.evaluation.stratifiedbottomup.IRuleEvaluatorFactory;
import org.deri.iris.evaluation.stratifiedbottomup.StratifiedBottomUpEvaluationStrategyFactory;
import org.deri.iris.evaluation.stratifiedbottomup.naive.NaiveEvaluator;
import org.deri.iris.facts.FiniteUniverseFacts;
import org.deri.iris.facts.IFacts;
import org.deri.iris.rules.compiler.ICompiledRule;
import org.deri.iris.rules.compiler.IRuleCompiler;
import org.deri.iris.rules.compiler.RuleCompiler;
import org.deri.iris.rules.optimisation.JoinConditionOptimiser;
import org.deri.iris.rules.optimisation.ReOrderLiteralsOptimiser;
import org.deri.iris.rules.optimisation.RemoveDuplicateLiteralOptimiser;
import org.deri.iris.rules.optimisation.ReplaceVariablesWithConstantsOptimiser;
import org.deri.iris.rules.safety.AugmentingRuleSafetyProcessor;
import org.deri.iris.storage.IRelation;

import eu.larkc.iris.rules.compiler.CascadingRuleCompiler;

/**
 * For now we can choose the standard interface of IEvaluation Strategy as
 * starting point. In the long term IKnowledgebase should be reimplemented to
 * encapsulate 1.) a set fo RIF rules, 2.) references (URIs) that identify RDF
 * graphs to work on.
 * 
 * 
 * @History 14.09.2010 fisf, Creation
 * @author Florian Fischer
 */
public class DistributedBottomUpEvaluationStrategy implements
		IEvaluationStrategy {

	/**
	 * TODO (fisf): IRule should encapsulate parsed RIF rules in this case. TODO
	 * (fisf): IFacts should encapsulate a set of datasources and predicates
	 * they contain. This directly results in suitable taps and tuples for
	 * cascading.
	 * 
	 * @param configuration
	 * @param ruleEvaluatorFactory
	 * @param rules
	 * @param facts
	 */
	public DistributedBottomUpEvaluationStrategy(Configuration configuration,
			IRuleEvaluatorFactory ruleEvaluatorFactory, List<IRule> rules,
			IFacts facts) {
		
		this.mRuleEvaluatorFactory = ruleEvaluatorFactory;
		this.mConfiguration = configuration;
		this.mRules = rules;
		this.mFacts = facts;	
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.deri.iris.evaluation.IEvaluationStrategy#evaluateQuery(org.deri.iris
	 * .api.basics.IQuery, java.util.List)
	 */
	@Override
	public IRelation evaluateQuery(IQuery query, List<IVariable> outputVariables)
			throws ProgramNotStratifiedException, RuleUnsafeException,
			EvaluationException {

		// TODO (fisf): for testing, where we only care about evaluating the
		// rules. So we directly invoke initBottomUpEvaluation(mRules);

		
		// Real query answering should then be delegated to an external store.
		// this requires 1.) Wrapping up e.g. a sparql query in a IQuery implementation, and 2.) accessing the external store hidden behind
		// an IFacts implementation
		
		throw new NotImplementedException("Query answering not yet delegated to external store");		
		
		//TODO: Unwrap query, convert to suitable format, execute over external store, process answers
		
	}

	/**
	 * Starts a distributed bottom up evaluation of a rule set.
	 * 
	 * @param rules
	 * @throws EvaluationException
	 */
	public void initBottomUpEvaluation(List<IRule> rules) throws EvaluationException {
		// setup of utils (stratification, etc.) according to configuration
		// objects
		EvaluationUtilities utils = new EvaluationUtilities(mConfiguration);

		// are rules safe? TODO (fisf): check close compliance with
		// http://www.w3.org/TR/rif-core/#Safeness_Criteria
		List<IRule> safeRules = utils.applyRuleSafetyProcessor(rules);

		// order rules into different strata for execution. TODO (fisf,
		// optimization): This might be more expensive than needed in the
		// absence of negation,
		// a simple dependency graph might be enough
		List<List<IRule>> stratifiedRules = utils.stratify(safeRules);

		// compile to cascading
		IRuleCompiler rc = new CascadingRuleCompiler(mConfiguration, mFacts);

		IRuleEvaluator evaluator = mRuleEvaluatorFactory.createEvaluator();
		// A naive evaluator should work here, otherwise a new factory simply
		// needs to be passed in
		assert evaluator instanceof NaiveEvaluator : "Only naiveEvaluator for now";

		// for each rule layer, reorder and optimize, compile, evaluate
		for (List<IRule> stratum : stratifiedRules) {
			// Re-order stratum, this could also work on the whole program at
			// once, see above
			List<IRule> reorderedRules = utils.reOrderRules(stratum);

			// Rule optimisation, per default
			List<IRule> optimisedRules = utils
					.applyRuleOptimisers(reorderedRules);

			List<ICompiledRule> compiledRules = new ArrayList<ICompiledRule>();

			// TODO (fisf, optimization): essentially each rule is compiled
			// independently and than evaluated by the naive evaluator
			// one-by-one
			// until no further change occurs. However, it should be possible to
			// evaluate rules in each stratum in parallel.
			// This requires a new implementation of 1.) an Evaluator that
			// replaces the naive one, 2.) a new factory that is passed along as
			// argument
			// This would basically combine the different flows to a cascade.
			for (IRule rule : optimisedRules) {
				ICompiledRule compiledRule = rc.compile(rule);
				compiledRules.add(compiledRule);
			}

			evaluator.evaluateRules(compiledRules, mFacts, mConfiguration);
		}
	}

	protected final IRuleEvaluatorFactory mRuleEvaluatorFactory;

	protected final List<IRule> mRules;

	protected final Configuration mConfiguration;
	
	/**
	 * We only keep that around as a wrapper around some external store in order to delegate the query to it.
	 * See FactsWithExternalData.
	 */
	protected final IFacts mFacts;

}
