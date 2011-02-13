/**
 * 
 */
package eu.larkc.iris.rules.stratification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.factory.IGraphFactory;
import org.deri.iris.api.graph.IPredicateGraph;
import org.deri.iris.graph.GraphFactory;
import org.deri.iris.rules.IRuleStratifier;

import eu.larkc.iris.Configuration;

/**
 * @author Florian Fischer, fisf, 14.01.2011
 */
public class DependencyMinimizingStratifier implements IRuleStratifier {

	/**
	 * Configuration
	 * 
	 * @param configuration
	 */
	public DependencyMinimizingStratifier(Configuration configuration) {
		this.configuration = configuration;
	}
	
	
	/**
	 * Invoke any post-stratification optimizations (e.g. language specific adjustments).
	 * 
	 * @param rules
	 * @return
	 */
	protected List<List<IRule>> invokePostProcessing(List<List<IRule>> rules) {
		
		List<IPostStratificationOptimization> post = configuration.postStratificationOptimizations;
		List<List<IRule>> result = rules;
		
		for (IPostStratificationOptimization iPostStratificationOptimization : post) {
			result = iPostStratificationOptimization.doPostProcessing(result);
		}
		
		return result;
	}
	
	/**
	 * Invoke and pre-stratification optimizations (e.g. language specific adjustments).
	 * 
	 * @param rules
	 * @return
	 */
	protected List<IRule> invokePreProcessing(List<IRule> rules) {
		
		List<IPreStratificationOptimization> pre = configuration.preStratificationOptimizer;
		List<IRule> result = rules;
		
		for (IPreStratificationOptimization iPreStratificationOptimization : pre) {
			result = iPreStratificationOptimization.doPreProcessing(result);
		}
		
		return result;	
	}
	
	@Override
	public List<List<IRule>> stratify(List<IRule> rules) {		
	
		if(rules.size() < 2) {
			throw new IllegalArgumentException("Cannot order less than 2 rules...");
		}
		
		predicateGraph = graphFactory.createPredicateGraph(rules);	
		
		//pre-processing hook
		rules = invokePreProcessing(rules);
		
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
		result.add(stratum);		
		//split done
		
		//post-processing hook
		result = invokePostProcessing(result);
		
		return result;
	}
		
	/**
	 * Configuration objects
	 */
	protected Configuration configuration;
	
	/**
	 * The dependency graph.
	 */
	protected IPredicateGraph predicateGraph;
		
	/**
	 * Graphfactory
	 */
	protected IGraphFactory graphFactory = GraphFactory.getInstance();		
	
	/**
	 * Compares rules
	 */
	protected RuleComparator rc = new RuleComparator();
	
	/**
	 * Compares predicates according to dependencies
	 */
	protected PredicateComparator pc = new PredicateComparator();
	
	
	//-----------------------------------------------------------------
	//Comparator classes
	//-----------------------------------------------------------------
	
	protected class RuleComparator implements Comparator<IRule> {

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
			
			//calculate return value on the dependencies between the head predicates
			//this leaves the case open where multiple rules feed into one head predicate
			
			IPredicate o1Head = o1.getHead().get(0).getAtom().getPredicate();
			IPredicate o2Head = o2.getHead().get(0).getAtom().getPredicate();
			
			int retVal =  pc.compare(o1Head, o2Head);
			
			boolean dep = false;
			
			//check if there really is a dependency
			if(retVal == 0) {
				
				List<ILiteral> o1body = o1.getBody();
				for (ILiteral iLiteral : o1body) {
					IPredicate bodyP = iLiteral.getAtom().getPredicate();
					if(predicateGraph.getDepends(bodyP).contains(o2Head)) {
						dep = true;
					}
				}	
				
				if(dep == false) {
					return -1;
				}
				
				dep = false;
				List<ILiteral> o2body = o2.getBody();
				for (ILiteral iLiteral : o2body) {
					IPredicate bodyP = iLiteral.getAtom().getPredicate();
					
					if(predicateGraph.getDepends(bodyP).contains(o1Head)) {
						dep = true;
					}					
				}
				
				if(dep == false) {
					return 1;
				}
			}
			
			return retVal;
		}
	}

		
	protected class PredicateComparator implements Comparator<IPredicate> {
				
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
