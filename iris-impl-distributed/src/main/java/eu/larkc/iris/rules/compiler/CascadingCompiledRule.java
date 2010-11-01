/**
 * 
 */
package eu.larkc.iris.rules.compiler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.facts.IFacts;
import org.deri.iris.rules.compiler.ICompiledRule;
import org.deri.iris.storage.IRelation;

import cascading.flow.Flow;

/**
 * CascadingCompiledRule encapsulates a rule that has been translated from the IRIS internal representation to a suitable cascading workflow.
 * This class also contains the basic logic that triggers a map-reduce computation, which is then simply called by an arbitrary IRuleEvaluator implementation
 * via the evaluate() method.
 * 
 * @history Oct 3, 2010, fisf, creation
 * @author Florian Fischer
 */
public class CascadingCompiledRule implements ICompiledRule {

	
	public CascadingCompiledRule(IPredicate headPredicate, Flow flow, Configuration configuration){
		this.mHeadPredicate = headPredicate;
		this.mFlow = flow;
		this.mConfiguration = configuration;
	}
	
	/* (non-Javadoc)
	 * @see org.deri.iris.rules.compiler.ICompiledRule#evaluate()
	 */
	@Override
	public IRelation evaluate() throws EvaluationException {
		
		//start returns immediately
		mFlow.start();
		
		//TODO: jobconf is constructed within the rule compiler right now, which is likely not the right place.
		//this should either happen here or in a custom evaluator implementation		
		
		//FIXME (fisf): check for recursion / cycles.
		//Overall story:
		//Naive evaluation does several passes over the rules and finishes when no new tuples are added anymore.
		//Several passes are actually only needed if the dependency graph contains cycles.
		//The return here is needed to establish the (possible) delta between iterations.
		//Naive evaluation will terminate when evaluate returns null.
		//Until recursion is supported this code will work fine, then a more complex solution is needed.
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.deri.iris.rules.compiler.ICompiledRule#evaluateIteratively(org.deri.iris.facts.IFacts)
	 */
	@Override
	public IRelation evaluateIteratively(IFacts deltas)
			throws EvaluationException {
		// TODO (fisf) implement later
		throw new NotImplementedException("Semi-naive evaluation is not implemented yet.");
	}

	/* (non-Javadoc)
	 * @see org.deri.iris.rules.compiler.ICompiledRule#getVariablesBindings()
	 */
	@Override
	public List<IVariable> getVariablesBindings() {
		return new ArrayList<IVariable>(); //extension is only required if the rule represents a query
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
