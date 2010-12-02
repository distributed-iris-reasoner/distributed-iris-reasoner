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
package eu.larkc.iris.evaluation.distributed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.evaluation.IEvaluationStrategy;
import org.deri.iris.storage.IRelation;

import eu.larkc.iris.evaluation.bottomup.DistributedBottomUpEvaluationStrategyFactory;
import eu.larkc.iris.evaluation.bottomup.naive.DistributedNaiveEvaluatorFactory;
import eu.larkc.iris.storage.FactsFactory;

/**
 * 
 * @author Valer Roman
 */
public abstract class EvaluationTest extends TestCase {

	protected List<IRule> rules;

	protected List<IQuery> queries;

	protected eu.larkc.iris.Configuration defaultConfiguration;

	private long duration;

	public EvaluationTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		// Set up the knowledge base consisting of a set of facts and a set of
		// rules.

		// Create the default configuration.
		defaultConfiguration = new eu.larkc.iris.Configuration();
		defaultConfiguration.evaluationStrategyFactory = new DistributedBottomUpEvaluationStrategyFactory(new DistributedNaiveEvaluatorFactory());
		
		createFacts();
		
		// Create the rules.
		rules = createRules();

		// Create the queries.
		queries = createQueries();
	}

	protected abstract void createFacts() throws IOException;

	protected abstract List<IRule> createRules();

	protected abstract List<IQuery> createQueries();

	protected IRelation evaluate(FactsFactory facts, IQuery query) throws Exception {
		// Use default configuration.
		return evaluate(facts, query, new ArrayList<IVariable>(), defaultConfiguration);
	}

	protected IRelation evaluate(FactsFactory facts, IQuery query, eu.larkc.iris.Configuration configuration)
			throws Exception {
		return evaluate(facts, query, new ArrayList<IVariable>(), configuration);
	}

	protected IRelation evaluate(FactsFactory facts, IQuery query, List<IVariable> outputVariables)
			throws Exception {
		// Use default configuration.
		return evaluate(facts, query, outputVariables, defaultConfiguration);
	}

	protected IRelation evaluate(FactsFactory facts, IQuery query, List<IVariable> outputVariables,
			eu.larkc.iris.Configuration configuration) throws Exception {
		// Create strategy using factory.
		long begin = System.currentTimeMillis();
		//FIXME create a factory for the distributed environment without the facts parameter
		IEvaluationStrategy strategy = configuration.evaluationStrategyFactory
				.createEvaluator(facts, rules, configuration); 

		IRelation relation = strategy.evaluateQuery(query, outputVariables);

		duration = System.currentTimeMillis() - begin;

		return relation;
	}

	/**
	 * Returns the time in milliseconds it took to evaluate the previous query.
	 * 
	 * @return The time in milliseconds it took to evaluate the previous query.
	 */
	protected long getDuration() {
		return duration;
	}

}