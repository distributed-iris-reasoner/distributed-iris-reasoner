package eu.larkc.iris.evaluation.bottomup.naive;

import eu.larkc.iris.evaluation.bottomup.IDistributedRuleEvaluator;
import eu.larkc.iris.evaluation.bottomup.IDistributedRuleEvaluatorFactory;


public class DistributedNaiveEvaluatorFactory implements IDistributedRuleEvaluatorFactory {

	@Override
	public IDistributedRuleEvaluator createEvaluator() {
		return new DistributedNaiveEvaluator();
	}

}
