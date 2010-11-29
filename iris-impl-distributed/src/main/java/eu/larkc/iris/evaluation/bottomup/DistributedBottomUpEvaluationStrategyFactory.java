/**
 * 
 */
package eu.larkc.iris.evaluation.bottomup;

import java.util.List;

import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.evaluation.IEvaluationStrategy;

import eu.larkc.iris.evaluation.IDistributedEvaluationStrategyFactory;


/**
 * @history 15.09.2010, fisf, creation
 * @author Florian Fischer
 */
public class DistributedBottomUpEvaluationStrategyFactory implements
		IDistributedEvaluationStrategyFactory {

	public DistributedBottomUpEvaluationStrategyFactory(IDistributedRuleEvaluatorFactory ruleEvaluatorFactory) {
		this.mRuleEvaluatorFactory = ruleEvaluatorFactory;
	}
	
	/* (non-Javadoc)
	 * @see org.deri.iris.evaluation.IEvaluationStrategyFactory#createEvaluator(org.deri.iris.facts.IFacts, java.util.List, org.deri.iris.Configuration)
	 */
	@Override
	public IEvaluationStrategy createEvaluator(List<IRule> rules,
			Configuration configuration) throws EvaluationException {
		
		return new DistributedBottomUpEvaluationStrategy(configuration, mRuleEvaluatorFactory, rules);		
	}
	
	private final IDistributedRuleEvaluatorFactory mRuleEvaluatorFactory;

}
