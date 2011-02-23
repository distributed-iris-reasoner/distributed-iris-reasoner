/**
 * 
 */
package eu.larkc.iris.rules.optimisation;

import java.util.ArrayList;
import java.util.List;

import org.deri.iris.api.basics.IRule;
import org.deri.iris.rules.IRuleStratifier;
import org.deri.iris.rules.stratification.GlobalStratifier;

import eu.larkc.iris.Configuration;
import eu.larkc.iris.functional.features.LangFeaturesTest;
import eu.larkc.iris.rules.stratification.DependencyMinimizingStratifier;
import eu.larkc.iris.rules.stratification.IPostStratificationOptimization;
import eu.larkc.iris.rules.stratification.IPreStratificationOptimization;

/**
 * Tests if multiple rules are stratified correctly according to their dependencies in the presence of recursion.
 * 
 * @author Florian Fischer, fisf, 16.01.2011
 *
 */
public class RecursiveDependencyStratificationTest extends LangFeaturesTest {
	
	private final static String test1 = "w(?X) :- p(?X, ?Y)." +
										//stratum
										"p(?X, ?Y) :- r(?X, ?Y)." +
										//stratum
										"r(?X, ?Y) :- k(?X, ?Y)." + 
										"k(?X, ?Y) :- r(?X, ?Y)." + 										
										//stratum, r and k depend on each other, but this rule only "feeds" into r
										"r(?X, ?Y) :- i(?X, ?Y)." +
										//unrelated, would actually be split away by other optimizations
										"q(?X, ?Y) :- m(?X, ?Y)."; 

	public RecursiveDependencyStratificationTest(String string) {
		super(string);
		defaultConfiguration = new Configuration();	
		//not enabled here
		defaultConfiguration.postStratificationOptimizations = new ArrayList<IPostStratificationOptimization>();
		defaultConfiguration.preStratificationOptimizer = new ArrayList<IPreStratificationOptimization>();
	}

	@Override
	protected void setUp() throws Exception {		
		program = test1;
		super.setUp();
	}

	/**
	 * Check that rules containing a grounded term in the rule head are
	 * correctly evaluated.
	 * 
	 * @throws Exception
	 */
	public void testStratification() throws Exception {
		
		rules = createRules();
		
		List<IRuleStratifier> stratifiers = defaultConfiguration.stratifiers;
		
		assertNotNull(stratifiers);
		IRuleStratifier strat = new DependencyMinimizingStratifier(defaultConfiguration);
		assertTrue(strat instanceof DependencyMinimizingStratifier);
		
		List<List<IRule>> stratifiedRules = strat.stratify(rules);
		
		//needs to result in 5 layers
		assertEquals(5, stratifiedRules.size());
		List<IRule> secondStratum = stratifiedRules.get(1);
		
		//second stratum contains 2 rules
		assertEquals(2, secondStratum.size());
		
		//problematic case in first stratum
		String bodyPredicateSymbol = stratifiedRules.get(0).get(0).getBody().get(0).getAtom().getPredicate().getPredicateSymbol();
		assertEquals("i", bodyPredicateSymbol);
		
		//fourth stratum depends on p, third stratum outputs p
		String bodyPredicateSymbolFourth = stratifiedRules.get(3).get(0).getBody().get(0).getAtom().getPredicate().getPredicateSymbol();
		assertEquals("http://www.w3.org/2000/01/rdf-schema#p", bodyPredicateSymbolFourth);
		
		String headPredicateSymbolThird = stratifiedRules.get(2).get(0).getHead().get(0).getAtom().getPredicate().getPredicateSymbol();
		assertEquals("http://www.w3.org/2000/01/rdf-schema#p", headPredicateSymbolThird);
		
	}
}
