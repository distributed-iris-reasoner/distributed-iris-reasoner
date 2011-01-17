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
 * Tests if Global stratification works in combination with rule combination in order to minimize the recomputation required.
 * 
 * @author Florian Fischer, fisf, 16.01.2011
 *
 */
public class MinimizeRecomputationStratificationTest extends LangFeaturesTest {

	public MinimizeRecomputationStratificationTest(String string) {
		super(string);
		defaultConfiguration = new Configuration();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Check that rules containing a grounded term in the rule head are
	 * correctly evaluated.
	 * 
	 * @throws Exception
	 */
	public void testStratification() throws Exception {
		//strata 3
		program = "w(?X) :- p(?X, ?Y)." + 
		"p(?X) :- r(?X, ?Y)." + 
		//strata 1
		"r(?X, ?Y) :- k(?X, ?Y)." + 
		"k(?X, ?Y) :- r(?X, ?Y), q(?Y)." + 
		//strata 0
		"q(?X, ?Y) :- z(?X, ?Y)." + 
		//strata 3
		"i(?X) :- j(?X, ?Y).";

		parser.parse(program);
		rules = createRules();
		
		List<IRuleStratifier> stratifiers = defaultConfiguration.stratifiers;
		
		assertNotNull(stratifiers);
		IRuleStratifier strat = stratifiers.get(0);
		assertTrue(strat instanceof DependencyMinimizingStratifier);
		
		List<List<IRule>> stratifiedRules = strat.stratify(rules);
		
		for (List<IRule> list : stratifiedRules) {
			
			//assert
			//TODO
			
			compileStrata(list);		
		}

	}
	
	protected void compileStrata(List<IRule> strata) throws Exception {
		rules = strata;
		super.compile();
	}	
}
