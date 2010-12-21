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

import java.util.Collection;
import java.util.List;

import org.deri.iris.Configuration;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import org.deri.iris.facts.Facts;
import org.deri.iris.facts.IFacts;
import org.deri.iris.storage.IRelation;

/**
 * An abstract class for evaluation tests, where only the evaluations of queries
 * against a specific program have to be tested. The program is created using
 * the <code>createExpression</code> method, which creates a collection of rules
 * and facts represented as strings.
 * 
 * @author Adrian Marte
 */
public abstract class ProgramEvaluationTest extends EvaluationTest {

	private Parser parser;

	public ProgramEvaluationTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		// Set up the knowledge base consisting of a set of facts and a set of
		// rules.

		Collection<String> expressions = createExpressions();
		parser = new Parser();
		StringBuffer buffer = new StringBuffer();

		for (String expression : expressions) {
			buffer.append(expression);
		}

		parser.parse(buffer.toString());

		super.setUp();
	}

	@Override
	protected IFacts createFacts() {
		// Create the facts.
		Configuration config = new Configuration();
		return new Facts(parser.getFacts(), config.relationFactory);
	}

	@Override
	protected List<IQuery> createQueries() {
		// Create the queries.
		return parser.getQueries();
	}

	@Override
	protected List<IRule> createRules() {
		// Create the rules.
		return parser.getRules();
	}

	private IQuery parseQuery(String query) {
		Parser parser = new Parser();
		try {
			parser.parse(query);
			List<IQuery> queries = parser.getQueries();

			if (queries.size() == 1) {
				return queries.get(0);
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}

		return null;
	}

	protected IRelation evaluate(String query) throws Exception {
		return evaluate(parseQuery(query));
	}

	protected IRelation evaluate(String query, Configuration configuration)
			throws Exception {
		return evaluate(parseQuery(query), configuration);
	}

	protected IRelation evaluate(String query, List<IVariable> outputVariables)
			throws Exception {
		return evaluate(parseQuery(query), outputVariables);
	}

	protected IRelation evaluate(String query, List<IVariable> outputVariables,
			Configuration configuration) throws Exception {
		return evaluate(parseQuery(query), outputVariables, configuration);
	}

	/**
	 * Creates the Datalog program represented as a collection of rules and
	 * facts in string form. Each element in the collection represents either a
	 * rule or a fact.
	 * 
	 * @return The Datalog program represented as a collection of rules and
	 *         facts in string form.
	 */
	protected abstract Collection<String> createExpressions();

}
