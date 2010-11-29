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
