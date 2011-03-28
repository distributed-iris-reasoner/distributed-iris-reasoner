/**
 * 
 */
package eu.larkc.iris.evaluator;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.facts.IFacts;
import org.deri.iris.storage.IRelation;

import eu.larkc.iris.evaluation.EvaluationContext;
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
		if(evaluations < maxEvaluations) {			
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
	

	public int getMaxEvaluations() {
		return maxEvaluations;
	}

	public void setMaxEvaluations(int maxEvaluations) {
		this.maxEvaluations = maxEvaluations;
	}
	
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof CascadingCompiledRule == false) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		final CascadingCompiledRule otherObject = (CascadingCompiledRule) obj;

		return new EqualsBuilder().append(this.rule, otherObject.getRule())
				.isEquals();

	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(rule.toString()).toHashCode();
	}

	@Override
	public String toString() {
		return rule.toString();
	}


	private int maxEvaluations = 3;

	private int evaluations = 0;
	
	private IDistributedCompiledRule rule;
}
