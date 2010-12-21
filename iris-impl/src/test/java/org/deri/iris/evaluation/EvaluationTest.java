/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2009 Semantic Technology Institute (STI) Innsbruck, 
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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.deri.iris.Configuration;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.facts.IFacts;
import org.deri.iris.storage.IRelation;

/**
 * An abstract class for evaluation tests, where only the evaluations of queries
 * against a specific program have to be tested.
 * 
 * @author Adrian Marte
 */
public abstract class EvaluationTest extends TestCase {

	protected List<IRule> rules;

	protected List<IQuery> queries;

	protected IFacts facts;

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

		// Create the facts.
		facts = createFacts();

		// Create the rules.
		rules = createRules();

		// Create the queries.
		queries = createQueries();
	}

	protected abstract IFacts createFacts();

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
