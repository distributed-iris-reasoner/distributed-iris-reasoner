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

import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;

import eu.larkc.iris.evaluation.bottomup.IDistributedRuleEvaluator;
import eu.larkc.iris.rules.compiler.IDistributedCompiledRule;

public class DistributedNaiveEvaluator implements IDistributedRuleEvaluator {

	@Override
	public void evaluateRules(List<IDistributedCompiledRule> rules, Configuration configuration)
			throws EvaluationException {
		boolean cont = true;
		while( cont )
		{
			cont = false;
			
			// For each rule in the collection (stratum)
			for (final IDistributedCompiledRule rule : rules )
			{
				boolean delta = rule.evaluate();
				cont = delta;
			}
		}
	}

}
