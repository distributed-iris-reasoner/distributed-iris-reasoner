/**
 * 
 */
package eu.larkc.iris.evaluation.bottomup.naive;

import java.util.List;

import org.deri.iris.EvaluationException;

import eu.larkc.iris.Configuration;
import eu.larkc.iris.evaluation.EvaluationContext;
import eu.larkc.iris.evaluation.bottomup.IDistributedRuleEvaluator;
import eu.larkc.iris.rules.compiler.IDistributedCompiledRule;

/**
 * @author Florian Fischer, fisf, 13.01.2011
 *
 */
public class DistributedOnePassEvaluator implements IDistributedRuleEvaluator {

	/* (non-Javadoc)
	 * @see eu.larkc.iris.evaluation.bottomup.IDistributedRuleEvaluator#evaluateRules(java.util.List, eu.larkc.iris.Configuration)
	 */
	@Override
	public void evaluateRules(List<IDistributedCompiledRule> rules,
			Configuration configuration) throws EvaluationException {
		
		int ruleNumber = 1;
		for (final IDistributedCompiledRule rule : rules )
		{
			rule.evaluate(ruleNumber);
			ruleNumber++;
		}
	}

}
