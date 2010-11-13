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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.deri.iris.Configuration;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.evaluation.IEvaluationStrategy;
import org.deri.iris.evaluation.stratifiedbottomup.naive.NaiveEvaluatorFactory;
import org.deri.iris.facts.IFacts;
import org.deri.iris.storage.IRelation;

/**
 * 
 * @author Valer Roman
 */
public abstract class EvaluationTest extends TestCase {

	protected List<IRule> rules;

	protected List<IQuery> queries;

	protected Configuration defaultConfiguration;

	private long duration;

	public EvaluationTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		// Set up the knowledge base consisting of a set of facts and a set of
		// rules.

		// Create the default configuration.
		defaultConfiguration = new Configuration();
		defaultConfiguration.evaluationStrategyFactory = new DistributedBottomUpEvaluationStrategyFactory(new NaiveEvaluatorFactory());
		
		// Create the rules.
		rules = createRules();

		// Create the queries.
		queries = createQueries();
	}

	protected abstract void createFacts();

	protected abstract List<IRule> createRules();

	protected abstract List<IQuery> createQueries();

	protected IRelation evaluate(IQuery query) throws Exception {
		// Use default configuration.
		return evaluate(query, new ArrayList<IVariable>(), defaultConfiguration);
	}

	protected IRelation evaluate(IQuery query, Configuration configuration)
			throws Exception {
		return evaluate(query, new ArrayList<IVariable>(), configuration);
	}

	protected IRelation evaluate(IQuery query, List<IVariable> outputVariables)
			throws Exception {
		// Use default configuration.
		return evaluate(query, outputVariables, defaultConfiguration);
	}

	protected IRelation evaluate(IQuery query, List<IVariable> outputVariables,
			Configuration configuration) throws Exception {
		// Create strategy using factory.
		long begin = System.currentTimeMillis();
		//FIXME create a factory for the distributed environment without the facts parameter
		IEvaluationStrategy strategy = configuration.evaluationStrategyFactory
				.createEvaluator(null, rules, configuration); 

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