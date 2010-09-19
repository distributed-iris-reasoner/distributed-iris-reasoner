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

import java.util.List;

import org.deri.iris.Configuration;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.evaluation.EvaluationTest;
import org.deri.iris.facts.IFacts;
import org.deri.iris.rules.IgnoreRuleHeadEquality;
import org.deri.iris.rules.RuleHeadEqualityRewriter;
import org.deri.iris.rules.safety.AugmentingRuleSafetyProcessor;
import org.deri.iris.utils.equivalence.IgnoreTermEquivalenceFactory;
import org.deri.iris.utils.equivalence.TermEquivalenceFactory;

/**
 * Test that compares the evaluation of a set of rules containing rules with
 * head equality using different evaluation techniques.
 * 
 * @author Adrian Marte
 */
public class EquivalenceStressTest extends EvaluationTest {

	private RandomProgramBuilder builder;
	
	private Program program;
	
	public EquivalenceStressTest(String name) {
		super(name);

		builder = new RandomProgramBuilder();
	}

	@Override
	protected void setUp() throws Exception {
		program = builder.build();
		
		super.setUp();
	}
	
	@Override
	protected IFacts createFacts() {
		return program.getFacts();
	}
	
	@Override
	protected List<IQuery> createQueries() {
		return program.getQueries();
	}
	
	@Override
	protected List<IRule> createRules() {
		return program.getRules();
	}
	
	public void testQuery1() throws Exception {
		executeQuery(queries.get(0));
	}

	private void executeQuery(IQuery query) throws Exception {
		Configuration config = new Configuration();

		// Use rewriting technique.
		config.equivalentTermsFactory = new IgnoreTermEquivalenceFactory();
		config.ruleHeadEqualityPreProcessor = new RuleHeadEqualityRewriter();
		config.ruleSafetyProcessor = new AugmentingRuleSafetyProcessor();

		evaluate(query, config);
		System.out.println("Rewriter: " + getDuration());

		config = new Configuration();

		// Use integrated rule head equality support.
		config.equivalentTermsFactory = new TermEquivalenceFactory();
		config.ruleHeadEqualityPreProcessor = new IgnoreRuleHeadEquality();

		evaluate(query, config);
		System.out.println("Integrated: " + getDuration());
	}


}
