/**
 * 
 */
package eu.larkc.iris.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.factory.IGraphFactory;
import org.deri.iris.api.graph.IPredicateGraph;
import org.deri.iris.graph.GraphFactory;

/**
 * A simple implementation that detects recursion in rule sets and splits input rule sets into the recursive and non-recursive parts, if so possible.
 * 
 * @author fisf, Florian Fischer
 */
public class NonOptimizingRecursiveRulePreProcessor implements
		IRecursiveRulePreProcessor {

	/**
	 * Set of recursive rules, initialized by process().
	 */
	private List<IRule> recursive = null;
	
	/**
	 * Set of non-recursive rules, initialized by process();
	 */
	private List<IRule> nonrecursive = null;
	
	/**
	 * Boolean that indicates if a rule set is recursive.
	 */
	private Boolean isRecursive = false;
	
	private IGraphFactory graphFactory = GraphFactory.getInstance();	
	

	public List<IRule> getRecursive() {
		return recursive;
	}

	public List<IRule> getNonrecursive() {
		return nonrecursive;
	}

	public Boolean getIsRecursive() {
		return isRecursive;
	}
	
	/* (non-Javadoc)
	 * @see eu.larkc.iris.IRecursiveRulePreProcessor#process(java.util.List, org.deri.iris.facts.IFacts)
	 */
	@Override
	public List<IRule> process(List<IRule> rules)
			throws EvaluationException {
		
		IPredicateGraph predicateGraph = graphFactory.createPredicateGraph(rules);
		isRecursive = predicateGraph.detectCycles();
		
		//get predicates that are involved in a cycle
		Set<IPredicate> recursivePredicates = predicateGraph.findVertexesForCycle();
		
		nonrecursive = new ArrayList<IRule>();
		recursive = new ArrayList<IRule>();
		
		boolean recursionFound = false;
		
		//filter recursive rules. For now we just look for the predicate in the head or the body
		for (IRule rule : rules) {
			recursionFound = false;
			List<ILiteral> allLiterals = new ArrayList<ILiteral>();
			allLiterals.addAll(rule.getBody());
			allLiterals.addAll(rule.getHead());
			
			for (ILiteral iLiteral : allLiterals) {
				if(recursivePredicates.contains(iLiteral.getAtom().getPredicate())) {
					recursive.add(rule);
					recursionFound = true;
					break;
				}
			}
			//break to here	
			
			//if we didn't find a rule to be recursive, then its nonrecursive
			if(! recursionFound) {
				nonrecursive.add(rule);
			}		
		}
		
		return rules;
	}

}
