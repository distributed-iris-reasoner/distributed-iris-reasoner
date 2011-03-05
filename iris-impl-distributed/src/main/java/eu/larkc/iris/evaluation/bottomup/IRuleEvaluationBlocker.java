/**
 * 
 */
package eu.larkc.iris.evaluation.bottomup;

import org.deri.iris.api.basics.IRule;

/**
 * Implementations of this interface allow to define a set of domain specific rules that are completely
 * from being re-evaluted in the fixpoint iteration of the bottom-up computation.
 * Use with care. 
 * 
 * Applying it leads in general to incomplete inference (an approximiation of the complete result) but can result in huge performance gains depending
 * on the set of blocked rules.
 * 
 * @author florian.fischer@softgress.com
 */
public interface IRuleEvaluationBlocker {

	public boolean block(IRule rule);
}
