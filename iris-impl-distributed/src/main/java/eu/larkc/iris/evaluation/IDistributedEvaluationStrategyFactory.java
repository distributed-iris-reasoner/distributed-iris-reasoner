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

package eu.larkc.iris.evaluation;

import java.util.List;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.evaluation.IEvaluationStrategy;

import eu.larkc.iris.storage.FactsFactory;

public interface IDistributedEvaluationStrategyFactory {

	/**
	 * Create a new evaluation strategy.
	 * @param facts The facts to be used for evaluation.
	 * @param rules The rule-set to be used for evaluation.
	 * @return The new evaluator instance.
	 */
	IEvaluationStrategy createEvaluator( FactsFactory facts, List<IRule> rules, eu.larkc.iris.Configuration configuration ) throws EvaluationException;

}
