package eu.larkc.iris.evaluation;

import java.util.List;

import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.evaluation.IEvaluationStrategy;

public interface IDistributedEvaluationStrategyFactory {

	/**
	 * Create a new evaluation strategy.
	 * @param facts The facts to be used for evaluation.
	 * @param rules The rule-set to be used for evaluation.
	 * @return The new evaluator instance.
	 */
	IEvaluationStrategy createEvaluator( List<IRule> rules, Configuration configuration ) throws EvaluationException;

}
