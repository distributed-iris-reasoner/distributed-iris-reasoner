/**
 * 
 */
package eu.larkc.iris.rules.compiler;

import org.apache.commons.lang.NotImplementedException;
import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.facts.IFacts;
import org.deri.iris.storage.IRelation;

import cascading.flow.Flow;
import eu.larkc.iris.storage.FactsTap;

/**
 * CascadingCompiledRule encapsulates a rule that has been translated from the IRIS internal representation to a suitable cascading workflow.
 * This class also contains the basic logic that triggers a map-reduce computation, which is then simply called by an arbitrary IRuleEvaluator implementation
 * via the evaluate() method.
 * 
 * @history Oct 3, 2010, fisf, creation
 * @author Florian Fischer
 */
public class CascadingCompiledRule implements IDistributedCompiledRule {

	
	public CascadingCompiledRule(IPredicate headPredicate, Flow flow, Configuration configuration){
		this.mHeadPredicate = headPredicate;
		this.mFlow = flow;
		this.mConfiguration = configuration;
	}

	/*
	 * (non-Javadoc)
	 * @see eu.larkc.iris.rules.compiler.IDistributedCompiledRule#evaluate()
	 */
	@Override
	public boolean evaluate() throws EvaluationException {
		
		//start returns immediately		
		if(mFlow == null) {
			throw new IllegalArgumentException("Flow must not be null");
		}
		mFlow.complete();
		
		//TODO: jobconf is constructed within the rule compiler right now, which is likely not the right place.
		//this should either happen here or in a custom evaluator implementation		
		
		//FIXME (fisf): check for recursion / cycles.
		//Overall story:
		//Naive evaluation does several passes over the rules and finishes when no new tuples are added anymore.
		//Several passes are actually only needed if the dependency graph contains cycles.
		//The return here is needed to establish the (possible) delta between iterations.
		//Naive evaluation will terminate when evaluate returns null.
		//Until recursion is supported this code will work fine, then a more complex solution is needed.
		
		return ((FactsTap) mFlow.getSink()).hasNewInferences();
	}

	/*
	 * (non-Javadoc)
	 * @see eu.larkc.iris.rules.compiler.IDistributedCompiledRule#evaluateIteratively(org.deri.iris.facts.IFacts)
	 */
	@Override
	public IRelation evaluateIteratively(IFacts deltas)
			throws EvaluationException {
		// TODO (fisf) implement later
		throw new NotImplementedException("Semi-naive evaluation is not implemented yet.");
	}

	/* (non-Javadoc)
	 * @see org.deri.iris.rules.compiler.ICompiledRule#headPredicate()
	 */
	@Override
	public IPredicate headPredicate() {
		return mHeadPredicate;
	}
	
	
	private final Configuration mConfiguration;
	
	/**
	 * Describes the predicate to which the original rules output belongs.
	 */
	private final IPredicate mHeadPredicate;
	
	/**
	 * The internal representation as a cascading flow of this rule.
	 */
	private final Flow mFlow;

}