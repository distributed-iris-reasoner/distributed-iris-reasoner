/**
 * 
 */
package eu.larkc.iris.rules.optimisation;

import java.util.List;

import org.deri.iris.api.basics.IRule;
import org.deri.iris.rules.IRuleStratifier;
import org.deri.iris.rules.stratification.GlobalStratifier;

import eu.larkc.iris.Configuration;
import eu.larkc.iris.functional.features.LangFeaturesTest;
import eu.larkc.iris.rules.stratification.DependencyMinimizingStratifier;

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
		IRuleStratifier strat = stratifiers.get(0);
		assertTrue(strat instanceof DependencyMinimizingStratifier);
		
		List<List<IRule>> stratifiedRules = strat.stratify(rules);
		int i =0;
		for (List<IRule> list : stratifiedRules) {			
			System.out.println(i + " " + list);
			i++;
			//TODO
			//compileStrata(list);		
		}
	}
	
	protected void compileStrata(List<IRule> strata) throws Exception {
		rules = strata;
		super.compile();
	}	
}
