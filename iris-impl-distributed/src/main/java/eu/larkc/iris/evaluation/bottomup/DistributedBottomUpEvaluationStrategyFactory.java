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
package eu.larkc.iris.evaluation.bottomup;

import java.util.List;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.evaluation.IEvaluationStrategy;

import eu.larkc.iris.evaluation.IDistributedEvaluationStrategyFactory;
import eu.larkc.iris.storage.FactsFactory;


/**
 * @history 15.09.2010, fisf, creation
 * @author Florian Fischer
 */
public class DistributedBottomUpEvaluationStrategyFactory implements
		IDistributedEvaluationStrategyFactory {

	public DistributedBottomUpEvaluationStrategyFactory(IDistributedRuleEvaluatorFactory ruleEvaluatorFactory) {
		this.mRuleEvaluatorFactory = ruleEvaluatorFactory;
	}
	
	/* (non-Javadoc)
	 * @see org.deri.iris.evaluation.IEvaluationStrategyFactory#createEvaluator(org.deri.iris.facts.IFacts, java.util.List, org.deri.iris.Configuration)
	 */
	@Override
	public IEvaluationStrategy createEvaluator(FactsFactory facts, List<IRule> rules,
			eu.larkc.iris.Configuration configuration) throws EvaluationException {
		
		return new DistributedBottomUpEvaluationStrategy(facts, configuration, mRuleEvaluatorFactory, rules);		
	}
	
	private final IDistributedRuleEvaluatorFactory mRuleEvaluatorFactory;

}
