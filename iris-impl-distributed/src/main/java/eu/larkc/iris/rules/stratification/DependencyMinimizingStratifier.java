/**
 * 
 */
package eu.larkc.iris.rules.stratification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.factory.IGraphFactory;
import org.deri.iris.api.graph.IPredicateGraph;
import org.deri.iris.graph.GraphFactory;
import org.deri.iris.rules.IRuleStratifier;

/**
 * @author Florian Fischer, fisf, 14.01.2011
 */
public class DependencyMinimizingStratifier implements IRuleStratifier {

	@Override
	public List<List<IRule>> stratify(List<IRule> rules) {		
	
		if(rules.size() < 2) {
			throw new IllegalArgumentException("Cannot order less than 2 rules...");
		}
		
		predicateGraph = graphFactory.createPredicateGraph(rules);	
		
		//establish initial ordering among rules
		Collections.sort(rules, rc);
		
		//split rules, one additional iteration			
		List<List<IRule>> result = new ArrayList<List<IRule>>();
		
		List<IRule> stratum = new ArrayList<IRule>();		
		IRule previousRule = rules.get(0);
		stratum.add(previousRule);
		
		for (int i = 1; i < rules.size(); i++) {
			
			IRule currentRule = rules.get(i);
			
			//not equal? start new stratum
			if(rc.compare(currentRule, previousRule) != 0) {
				result.add(stratum);
				stratum = new ArrayList<IRule>();
			}
			stratum.add(currentRule);			
			previousRule = currentRule;			
		}
		
		//split done
		
		return result;
	}
		
	
	/**
	 * The dependency graph.
	 */
	private IPredicateGraph predicateGraph;
		
	/**
	 * Graphfactory
	 */
	private IGraphFactory graphFactory = GraphFactory.getInstance();		
	
	/**
	 * Compares rules
	 */
	private RuleComparator rc = new RuleComparator();
	
	/**
	 * Compares predicates according to dependencies
	 */
	private PredicateComparator pc = new PredicateComparator();
	
	//-----------------------------------------------------------------
	//Comparator classes
	//-----------------------------------------------------------------
	
	private class RuleComparator implements Comparator<IRule> {

		public int compare(final IRule o1, final IRule o2) {
			if ((o1 == null) || (o2 == null)) {
				throw new NullPointerException("None of the rule must be null");
			}
			
			if ((o1.getHead().size() != 1) || (o2.getHead().size() != 1)) {
				throw new IllegalArgumentException(
						"Only rules with a headlength of 1 are allowed.");
			}
			
			//two rules are equal if they depend on each other
			//this means they go in the same strata
			
			return pc.compare(o1.getHead().get(0).getAtom().getPredicate(), 
					o2.getHead().get(0).getAtom().getPredicate());
		}
	}

		
	private class PredicateComparator implements Comparator<IPredicate> {
				
		public int compare(final IPredicate o1, final IPredicate o2) {
			
			if ((o1 == null) || (o2 == null)) {
				throw new NullPointerException(
						"None of the predicates must be null");
			}
		
			//equal, i.e. the predicates mutually depend on each other
			if(predicateGraph.getDepends(o1).contains(o2) && predicateGraph.getDepends(o2).contains(o1)) {
				return 0;
			}
				
			return predicateGraph.getDepends(o1).contains(o2) ? 1 : -1;
		}
	}	
}
