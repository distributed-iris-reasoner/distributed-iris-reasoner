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
package eu.larkc.iris.evaluation.bottomup.naive;

import java.util.List;

import org.deri.iris.EvaluationException;

import eu.larkc.iris.Configuration;
import eu.larkc.iris.evaluation.bottomup.IDistributedRuleEvaluator;
import eu.larkc.iris.rules.compiler.IDistributedCompiledRule;

/**
 * Evaluator used for non-recursive rules.
 * TODO ??? Shouldn't this also be integrated with the dependency optimization? Because a non-recursive rule might depend on other 
 * rules and other rules depend on it, so should be part of the stratification.
 * 
 * @author Florian Fischer, fisf, 13.01.2011
 *
 */
public class DistributedOnePassEvaluator implements IDistributedRuleEvaluator {

	/* (non-Javadoc)
	 * @see eu.larkc.iris.evaluation.bottomup.IDistributedRuleEvaluator#evaluateRules(java.util.List, eu.larkc.iris.Configuration)
	 */
	@Override
	public void evaluateRules(Integer stratumNumber, List<IDistributedCompiledRule> rules,
			Configuration configuration) throws EvaluationException {
		
		int ruleNumber = 1;
		for (final IDistributedCompiledRule rule : rules )
		{
			rule.evaluate(ruleNumber);
			ruleNumber++;
		}
	}

}
