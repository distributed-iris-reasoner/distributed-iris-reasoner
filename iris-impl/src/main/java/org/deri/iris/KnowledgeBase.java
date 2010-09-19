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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.evaluation.IEvaluationStrategy;
import org.deri.iris.evaluation.OptimisedProgramStrategyAdaptor;
import org.deri.iris.facts.Facts;
import org.deri.iris.facts.FactsWithExternalData;
import org.deri.iris.facts.IFacts;
import org.deri.iris.rules.RuleManipulator;
import org.deri.iris.storage.IRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The concrete knowledge-base.
 */
public class KnowledgeBase implements IKnowledgeBase {
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Constructor.
	 * 
	 * @param facts
	 *            The starting facts for the knowledge-base.
	 * @param rules
	 *            The rules of the knowledge-base.
	 * @param configuration
	 *            The configuration object for the knowledge-base.
	 * @throws EvaluationException
	 * @throws EvaluationException
	 */
	public KnowledgeBase(Map<IPredicate, IRelation> inputFacts,
			List<IRule> rules, Configuration configuration)
			throws EvaluationException {
		if (inputFacts == null)
			inputFacts = new HashMap<IPredicate, IRelation>();

		if (rules == null)
			rules = new ArrayList<IRule>();

		if (configuration == null)
			configuration = new Configuration();

		mConfiguration = configuration;

		// Store the configuration object against the current thread.
		ConfigurationThreadLocalStorage.setConfiguration(mConfiguration);

		// Set up the rule-base
		mRules = rules;

		// Set up the facts object(s)
		IFacts facts = new Facts(inputFacts, mConfiguration.relationFactory);

		if (mConfiguration.externalDataSources.size() > 0)
			facts = new FactsWithExternalData(facts,
					mConfiguration.externalDataSources);

		mFacts = facts;

		if (logger.isDebugEnabled()) {
			logger.debug("IRIS knowledge-base init");
			logger.debug("========================");

			for (IRule rule : rules) {
				logger.debug(rule.toString());
			}

			logger.debug("------------------------");

			for (IPredicate f : mFacts.getPredicates()) {
				IRelation relation = mFacts.get(f);
				for (int i = 0; i < relation.size(); i++) {
					ITuple tuple = relation.get(i);
					logger.debug(f + " " + tuple);
				}
			}
		}

		if (mConfiguration.programOptmimisers.size() > 0)
			mEvaluationStrategy = new OptimisedProgramStrategyAdaptor(facts,
					rules, mConfiguration);
		else
			mEvaluationStrategy = mConfiguration.evaluationStrategyFactory
					.createEvaluator(facts, rules, configuration);
	}

	public IRelation execute(IQuery query, List<IVariable> variableBindings)
			throws EvaluationException {
		if (query == null)
			throw new IllegalArgumentException(
					"KnowledgeBase.execute() - the query is null.");

		// This prevents every strategy having to check for this.
		if (variableBindings == null)
			variableBindings = new ArrayList<IVariable>();

		// Store the configuration object against the current thread.
		ConfigurationThreadLocalStorage.setConfiguration(mConfiguration);

		logger.debug("IRIS query");
		logger.debug("==========");
		logger.debug(query.toString());

		IRelation result = mEvaluationStrategy.evaluateQuery(RuleManipulator
				.removeDuplicateLiterals(query), variableBindings);

		logger.debug("------------");
		logger.debug(result.toString());

		return result;
	}

	public IRelation execute(IQuery query) throws EvaluationException {
		return execute(query, null);
	}

	public List<IRule> getRules() {
		return mRules;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		for (IRule rule : mRules)
			result.append(rule.toString());

		result.append(mFacts.toString());

		return result.toString();
	}

	/** The facts of the knowledge-base. */
	private final IFacts mFacts;

	/** The rules of the knowledge-base. */
	private final List<IRule> mRules;

	/** The configuration object for the knowledge-base. */
	private final Configuration mConfiguration;

	/** The evaluation strategy for the knowledge-base. */
	private IEvaluationStrategy mEvaluationStrategy;
}
