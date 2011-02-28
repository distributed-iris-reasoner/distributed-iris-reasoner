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

import eu.larkc.iris.rules.compiler.IDistributedCompiledRule;

/**
 * A Rule evaluator for the distributed environment
 * 
 * @author valer.rpman@softgress.com
 *
 */
public interface IDistributedRuleEvaluator {

	/**
	 * Evaluate rules.
	 * @param rules The collection of compiled rules.
	 * @param facts Where to store the newly deduced tuples.
	 * @param configuration The knowledge-base configuration object.
	 * @throws EvaluationException 
	 */
	void evaluateRules( Integer stratumNumber, List<IDistributedCompiledRule> rules, eu.larkc.iris.Configuration configuration ) throws EvaluationException;

}
