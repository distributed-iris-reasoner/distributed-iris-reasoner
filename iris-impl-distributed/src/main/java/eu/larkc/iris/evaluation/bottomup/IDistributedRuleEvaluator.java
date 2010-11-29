package eu.larkc.iris.evaluation.bottomup;

import java.util.List;

import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;

import eu.larkc.iris.rules.compiler.IDistributedCompiledRule;

public interface IDistributedRuleEvaluator {

	/**
	 * Evaluate rules.
	 * @param rules The collection of compiled rules.
	 * @param facts Where to store the newly deduced tuples.
	 * @param configuration The knowledge-base configuration object.
	 * @throws EvaluationException 
	 */
	void evaluateRules( List<IDistributedCompiledRule> rules, Configuration configuration ) throws EvaluationException;

}
