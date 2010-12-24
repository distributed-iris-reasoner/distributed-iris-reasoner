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

import java.util.Collection;
import java.util.List;

import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import org.deri.iris.storage.IRelation;

import eu.larkc.iris.storage.FactsFactory;

/**
 * 
 * @author Valer Roman
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

	protected IRelation evaluate(FactsFactory facts, String query, eu.larkc.iris.Configuration configuration)
			throws Exception {
		return evaluate(facts, parseQuery(query), configuration);
	}

	protected IRelation evaluate(FactsFactory facts, String query, List<IVariable> outputVariables)
			throws Exception {
		return evaluate(facts, parseQuery(query), outputVariables);
	}

	protected IRelation evaluate(FactsFactory facts, String query, List<IVariable> outputVariables,
			eu.larkc.iris.Configuration configuration) throws Exception {
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
