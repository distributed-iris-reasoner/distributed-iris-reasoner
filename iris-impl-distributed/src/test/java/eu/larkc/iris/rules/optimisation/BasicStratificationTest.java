/**
 * 
 */
package eu.larkc.iris.rules.optimisation;

import java.util.List;

import org.deri.iris.api.basics.IRule;
import org.deri.iris.rules.IRuleStratifier;

import eu.larkc.iris.Configuration;
import eu.larkc.iris.functional.features.LangFeaturesTest;
import eu.larkc.iris.rules.stratification.DependencyMinimizingStratifier;

/**
 * Basic test for dependency aware stratfier.
 * 
 * @author Florian Fischer, fisf, 16.01.2011
 *
 */
public class BasicStratificationTest extends LangFeaturesTest {
	
	private final static String test1 = "w(?X) :- p(?X, ?Y)." +
										"p(?X, ?Y) :- r(?X, ?Y)."; 

	public BasicStratificationTest(String string) {
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
		
		assertEquals(1, stratifiedRules.get(0).size());
		assertEquals(1, stratifiedRules.get(1).size());
		
		String headPredicateSymbol1 = stratifiedRules.get(0).get(0).getHead().get(0).getAtom().getPredicate().getPredicateSymbol();
		assertEquals(true, headPredicateSymbol1.equals("http://www.w3.org/2000/01/rdf-schema#p"));
		String headPredicateSymbol2 = stratifiedRules.get(1).get(0).getHead().get(0).getAtom().getPredicate().getPredicateSymbol();
		assertEquals(true, headPredicateSymbol2.equals("w"));
		
		//make sure the rules compile
		for (List<IRule> list : stratifiedRules) {			
			compileStrata(list);		
		}

	}	
	
	protected void compileStrata(List<IRule> strata) throws Exception {
		rules = strata;
		super.compile();
	}	
}
