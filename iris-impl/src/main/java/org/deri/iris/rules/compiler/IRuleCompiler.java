package org.deri.iris.rules.compiler;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IRule;

public interface IRuleCompiler {

	/**
	 * Compile a rule. No optimisations of any kind are attempted.
	 * 
	 * @param rule The rule to be compiled
	 * @return The compiled rule, ready to be evaluated
	 * @throws EvaluationException If the query can not be compiled for any
	 *             reason.
	 */
	public abstract ICompiledRule compile(IRule rule)
			throws EvaluationException;

}