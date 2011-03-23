/**
 * 
 */
package eu.larkc.iris.evaluator;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.facts.IFacts;
import org.deri.iris.storage.IRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.iris.evaluation.EvaluationContext;
import eu.larkc.iris.evaluation.bottomup.naive.DistributedNaiveEvaluator;
import eu.larkc.iris.rules.compiler.CascadingCompiledRule;
import eu.larkc.iris.rules.compiler.FlowAssembly;
import eu.larkc.iris.rules.compiler.IDistributedCompiledRule;

/**
 * @author Florian Fischer, fisf
 */
public class DistributedCompiledRuleMock implements IDistributedCompiledRule {

	public DistributedCompiledRuleMock(IDistributedCompiledRule compiledRule){
		this.rule = compiledRule;		
	}
	
	/* (non-Javadoc)
	 * @see eu.larkc.iris.rules.compiler.IDistributedCompiledRule#evaluate(java.lang.Integer)
	 */
	@Override
	public void evaluate(Integer ruleNumber) throws EvaluationException {
		throw new RuntimeException();	
	}

	/* (non-Javadoc)
	 * @see eu.larkc.iris.rules.compiler.IDistributedCompiledRule#evaluate(eu.larkc.iris.evaluation.EvaluationContext)
	 */
	@Override
	public boolean evaluate(EvaluationContext evaluationContext)
			throws EvaluationException {
		
		evaluations++;
		System.out.println("Rule: " + rule.getRule() + " Internal iteration: " + evaluations);
				
		//simulate two iterations (excluding updated due to dependencies)
		if(evaluations < 3) {			
			return true;
		}				
		
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.larkc.iris.rules.compiler.IDistributedCompiledRule#evaluateIteratively(org.deri.iris.facts.IFacts)
	 */
	@Override
	public IRelation evaluateIteratively(IFacts deltas)
			throws EvaluationException {
		return rule.evaluateIteratively(deltas);
	}

	/* (non-Javadoc)
	 * @see eu.larkc.iris.rules.compiler.IDistributedCompiledRule#getFlowAssembly()
	 */
	@Override
	public FlowAssembly getFlowAssembly() {
		return rule.getFlowAssembly();
	}

	/* (non-Javadoc)
	 * @see eu.larkc.iris.rules.compiler.IDistributedCompiledRule#getRule()
	 */
	@Override
	public IRule getRule() {
		return rule.getRule();
	}	
	
	public int getEvaluations() {
		return evaluations;
	}

	private int evaluations = 0;
	
	private IDistributedCompiledRule rule;
}
