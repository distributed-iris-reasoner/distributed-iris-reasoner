/*
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.deri.iris.EvaluationException;
import org.deri.iris.ProgramNotStratifiedException;
import org.deri.iris.RuleUnsafeException;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.evaluation.IEvaluationStrategy;
import org.deri.iris.evaluation.stratifiedbottomup.EvaluationUtilities;
import org.deri.iris.storage.IRelation;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.operation.Identity;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.tap.Hfs;
import cascading.tap.Tap;

import eu.larkc.iris.evaluation.PredicateCounts;
import eu.larkc.iris.rules.IRecursiveRulePreProcessor;
import eu.larkc.iris.rules.NonOptimizingRecursiveRulePreProcessor;
import eu.larkc.iris.rules.compiler.CascadingRuleCompiler;
import eu.larkc.iris.rules.compiler.IDistributedCompiledRule;
import eu.larkc.iris.rules.compiler.IDistributedRuleCompiler;

/**
 * 
 * @History 14.09.2010 fisf, Creation
 * @author Florian Fischer
 */
public class DistributedBottomUpEvaluationStrategy implements
		IEvaluationStrategy {

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
		
		// Real query answering should be delegated to an external store.
		// this requires 1.) Wrapping up e.g. a sparql query in a IQuery implementation, and 2.) accessing the external store hidden behind
		// an IFacts implementation
		
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

		singlePass.evaluateRules(compiledRules, mConfiguration);		
	}
	
	protected void evaluateRecursiveRules(List<IRule> rules) throws EvaluationException {		
		//Done by DependencyMinimizingStratifier, TODO (fisf) complete 
		List<List<IRule>> stratifiedRules = utils.stratify(rules);
	
		IDistributedRuleCompiler rc = new CascadingRuleCompiler(mConfiguration);		
		IDistributedRuleEvaluator evaluator = mRuleEvaluatorFactory.createEvaluator(IDistributedRuleEvaluatorFactory.RECURSIONAWAREEVALUATOR);		
		PredicateCounts predicateCounts = PredicateCounts.getInstance(mConfiguration);
		
		//process each rule layer independently, no recomputations outside of each layer are required
		for (List<IRule> stratum : stratifiedRules) {
			
			for (IRule rule : stratum) {
				ListIterator<ILiteral> iterator = rule.getBody().listIterator();
				while (iterator.hasNext()) {
					ILiteral literal = iterator.next();
					//TODO: fisf, attach predicatecount to each literal so that it can be fed to the ruleoptimizer.
					//Should then be processed in applyRuleOptimizers similarly to ReOrderLiteralsOptimiser
					//Long count = predicateCounts.getCount(literal.getAtom());
				}
			}
			
			//reorder rules within stratum
			List<IRule> reorderedRules = utils.reOrderRules(stratum);		
			//TODO(fisf): apply optimizer for outer joins
			List<IRule> optimisedRules = utils.applyRuleOptimisers(reorderedRules);

			List<IDistributedCompiledRule> compiledRules = new ArrayList<IDistributedCompiledRule>();			
			for (IRule rule : optimisedRules) {
				IDistributedCompiledRule compiledRule = rc.compile(rule);
				compiledRules.add(compiledRule);
			}

			evaluator.evaluateRules(compiledRules, mConfiguration);
		}
	}
	
	protected void loadDataHFS(List<IRule> rules) {
		//extract all predicates, used to load all data into HFS
		/*
		Set<IPredicate> predicates = new HashSet<IPredicate>();
		for (IRule rule : optimisedRules) {
			predicates.add(rule.getHead().get(0).getAtom().getPredicate());
			for (ILiteral literal : rule.getBody()) {
				IPredicate predicate = literal.getAtom().getPredicate();
				predicates.add(predicate);
			}
		}
		FactsTap source = mFacts.getFacts(predicates.toArray(new IPredicate[0]));
		//FactsTap source = mFacts.getFacts(optimisedRules.get(0).getHead().get(0).getAtom());
		
		String output = mConfiguration.HADOOP_HFS_PATH + "/" + mFacts.getStorageId();
		Tap sink = new Hfs(source.getSourceFields(), output , true );

		//String output1 = mConfiguration.HADOOP_HFS_PATH + "/" + mFacts.getStorageId() + "1";
		//Tap sink1 = new Hfs( source.getSourceFields(), output1 , true );

		Map<String, Tap> sources = new HashMap<String, Tap>();
		sources.put("source", source);

		Map<String, Tap> sinks = new HashMap<String, Tap>();
		sinks.put("sink", sink);
		//sinks.put("sink1", sink1);

		Pipe sourcePipe = new Pipe("source");
		sourcePipe = new Each(sourcePipe, source.getSourceFields(), new Identity(source.getSourceFields()));
		Pipe identity = new Pipe("sink", sourcePipe);
		//identity = new Each(identity, source.getSourceFields(), new Identity(source.getSourceFields()));
		//Pipe identity1 = new Pipe("sink1", sourcePipe);
		//identity1 = new Each(identity1, source.getSourceFields(), new Identity(source.getSourceFields()));
		
		//Flow aFlow = new FlowConnector(mConfiguration.flowProperties).connect(sources, sink, identity);
		Flow aFlow = new FlowConnector(mConfiguration.flowProperties).connect(sources, sinks, identity);
		aFlow.complete();
		*/
	}
	
	protected EvaluationUtilities utils;

	protected final IDistributedRuleEvaluatorFactory mRuleEvaluatorFactory;

	protected final List<IRule> mRules;

	protected final eu.larkc.iris.Configuration mConfiguration;
	
}
