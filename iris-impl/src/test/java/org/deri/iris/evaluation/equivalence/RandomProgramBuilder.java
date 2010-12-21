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
package org.deri.iris.evaluation.equivalence;

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.TERM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.deri.iris.Configuration;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.builtins.EqualBuiltin;
import org.deri.iris.facts.Facts;
import org.deri.iris.facts.IFacts;
import org.deri.iris.storage.IRelation;

/**
 * @author Adrian Marte
 */
// FIXME Make this class "more" deterministic.
public class RandomProgramBuilder {

	private int numberOfRelations = 20;

	private int numberOfFactsPerRelation = 100;

	private int numberOfRules = 200;

	private int minimalArity = 1;

	private int maximalArity = 20;

	private int maximalConstants = 100;

	private String constantPrefix = "C";

	private String relationPrefix = "p";

	private String rulePrefix = "r";

	private String variablePrefix = "X";

	private int numberOfEqualityRules = 2;

	private Configuration configuration = new Configuration();

	private Random random;

	private List<IPredicate> relationPredicates;

	private List<IPredicate> rulePredicates;

	private List<IVariable> variables;

	private void createVariables() {
		variables = new ArrayList<IVariable>();

		for (int i = 0; i < maximalArity; i++) {
			String name = variablePrefix + i;
			IVariable variable = TERM.createVariable(name);
			variables.add(variable);
		}
	}

	private void createPredicates() {
		relationPredicates = new ArrayList<IPredicate>();
		rulePredicates = new ArrayList<IPredicate>();

		// Create relation predicates.
		for (int i = 1; i <= numberOfRelations; i++) {
			String symbol = relationPrefix + i;
			IPredicate predicate = createPredicate(symbol);
			relationPredicates.add(predicate);
		}

		// Create rule predicates.
		for (int i = 1; i <= numberOfRules; i++) {
			String symbol = rulePrefix + i;
			IPredicate predicate = createPredicate(symbol);
			rulePredicates.add(predicate);
		}
	}

	private IPredicate createPredicate(String symbol) {
		int arity = random.nextInt(maximalArity - minimalArity + 1)
				+ minimalArity;
		IPredicate predicate = BASIC.createPredicate(symbol, arity);
		return predicate;
	}

	private <T> T pickRandom(List<T> list) {
		int index = random.nextInt(list.size());
		return list.get(index);
	}

	private IFacts createFacts() {
		IFacts facts = new Facts(configuration.relationFactory);

		// Create facts.
		for (int i = 0; i < numberOfRelations; i++) {
			IPredicate predicate = relationPredicates.get(i);

			IRelation relation = facts.get(predicate);

			for (int k = 0; k < numberOfFactsPerRelation; k++) {
				List<ITerm> terms = new ArrayList<ITerm>(predicate.getArity());

				for (int j = 0; j < predicate.getArity(); j++) {
					int index = random.nextInt(maximalConstants) + 1;
					terms.add(TERM.createString(constantPrefix + index));
				}

				ITuple tuple = BASIC.createTuple(terms);
				relation.add(tuple);
			}
		}

		return facts;
	}

	private List<IRule> createRules() {
		List<IRule> rules = new ArrayList<IRule>();

		// Create rules.
		for (int i = 0; i < numberOfRules; i++) {
			IRule rule = createRule(false, i);

			rules.add(rule);
		}

		// Create equality rules.
		for (int i = 0; i < numberOfEqualityRules; i++) {
			IRule rule = createRule(true, i);

			rules.add(rule);
		}

		return rules;
	}

	private List<IQuery> createQueries() {
		// Create query.
		IPredicate predicate;

		// Use rule.
		if (random.nextDouble() < 0.5) {
			predicate = pickRandom(rulePredicates);
		}
		// Use relation.
		else {
			predicate = pickRandom(relationPredicates);
		}

		List<ITerm> terms = new ArrayList<ITerm>(predicate.getArity());

		for (int i = 0; i < predicate.getArity(); i++) {
			terms.add(TERM.createVariable(variablePrefix + i));
		}

		ITuple tuple = BASIC.createTuple(terms);
		ILiteral literal = BASIC.createLiteral(true, predicate, tuple);

		IQuery query = BASIC.createQuery(literal);
		return Collections.singletonList(query);
	}

	private IRule createRule(boolean isEqualityRule, int index) {
		int numOfLiterals = random.nextInt(numberOfRelations) + 1;

		Set<ITerm> variableSet = new HashSet<ITerm>();
		List<ILiteral> bodyLiterals = new ArrayList<ILiteral>(numOfLiterals);

		for (int j = 0; j < numOfLiterals; j++) {
			IPredicate predicate = pickRandom(relationPredicates);

			List<ITerm> terms = new ArrayList<ITerm>(predicate.getArity());

			for (int k = 0; k < predicate.getArity(); k++) {
				IVariable variable = pickRandom(variables);

				terms.add(variable);

				variableSet.add(variable);
			}

			ITuple tuple = BASIC.createTuple(terms);
			ILiteral literal = BASIC.createLiteral(true, predicate, tuple);
			bodyLiterals.add(literal);
		}

		IPredicate predicate;

		if (isEqualityRule) {
			EqualBuiltin builtin = new EqualBuiltin(TERM.createVariable("X"),
					TERM.createVariable("Y"));
			predicate = builtin.getPredicate();
		} else {
			predicate = pickRandom(rulePredicates);
		}

		List<ITerm> shuffledVariables = new ArrayList<ITerm>(variableSet);

		while (shuffledVariables.size() < predicate.getArity()) {
			shuffledVariables.add(pickRandom(shuffledVariables));
		}

		Collections.shuffle(shuffledVariables);
		List<ITerm> headVariables = shuffledVariables.subList(0, predicate
				.getArity());

		ITuple headTuple = BASIC.createTuple(headVariables);
		ILiteral headLiteral = BASIC.createLiteral(true, predicate, headTuple);

		IRule rule = BASIC.createRule(Collections.singletonList(headLiteral),
				bodyLiterals);

		return rule;
	}

	public int getNumberOfRelations() {
		return numberOfRelations;
	}

	public RandomProgramBuilder setNumberOfRelations(int numberOfRelations) {
		this.numberOfRelations = numberOfRelations;
		return this;
	}

	public int getNumberOfFactsPerRelation() {
		return numberOfFactsPerRelation;
	}

	public RandomProgramBuilder setNumberOfFactsPerRelation(int numberOfFactsPerRelation) {
		this.numberOfFactsPerRelation = numberOfFactsPerRelation;
		return this;
	}

	public int getNumberOfRules() {
		return numberOfRules;
	}

	public RandomProgramBuilder setNumberOfRules(int numberOfRules) {
		this.numberOfRules = numberOfRules;
		return this;
	}

	public int getMinimalArity() {
		return minimalArity;
	}

	public RandomProgramBuilder setMinimalArity(int minimalArity) {
		this.minimalArity = minimalArity;
		return this;
	}

	public int getMaximalArity() {
		return maximalArity;
	}

	public RandomProgramBuilder setMaximalArity(int maximalArity) {
		this.maximalArity = maximalArity;
		return this;
	}

	public int getMaximalConstants() {
		return maximalConstants;
	}

	public RandomProgramBuilder setMaximalConstants(int maximalConstants) {
		this.maximalConstants = maximalConstants;
		return this;
	}

	public String getConstantPrefix() {
		return constantPrefix;
	}

	public RandomProgramBuilder setConstantPrefix(String constantPrefix) {
		this.constantPrefix = constantPrefix;
		return this;
	}

	public String getRelationPrefix() {
		return relationPrefix;
	}

	public RandomProgramBuilder setRelationPrefix(String relationPrefix) {
		this.relationPrefix = relationPrefix;
		return this;
	}

	public String getRulePrefix() {
		return rulePrefix;
	}

	public RandomProgramBuilder setRulePrefix(String rulePrefix) {
		this.rulePrefix = rulePrefix;
		return this;
	}

	public String getVariablePrefix() {
		return variablePrefix;
	}

	public RandomProgramBuilder setVariablePrefix(String variablePrefix) {
		this.variablePrefix = variablePrefix;
		return this;
	}

	public int getNumberOfEqualityRules() {
		return numberOfEqualityRules;
	}

	public RandomProgramBuilder setNumberOfEqualityRules(int numberOfEqualityRules) {
		this.numberOfEqualityRules = numberOfEqualityRules;
		return this;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public RandomProgramBuilder setConfiguration(Configuration configuration) {
		this.configuration = configuration;
		return this;
	}

	public Program build() {
		random = new Random();
		createPredicates();
		createVariables();

		IFacts facts = createFacts();
		List<IRule> rules = createRules();
		List<IQuery> queries = createQueries();

		return new Program(facts, rules, queries);
	}
}
