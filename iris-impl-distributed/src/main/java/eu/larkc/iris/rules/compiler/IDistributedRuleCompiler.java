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

package eu.larkc.iris.rules.compiler;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IRule;

public interface IDistributedRuleCompiler {

	/**
	 * Compile a rule. No optimisations of any kind are attempted.
	 * 
	 * @param rule The rule to be compiled
	 * @return The compiled rule, ready to be evaluated
	 * @throws EvaluationException If the query can not be compiled for any
	 *             reason.
	 */
	public abstract IDistributedCompiledRule compile(IRule rule)
			throws EvaluationException;

}
