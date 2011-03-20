/**
 * 
 */
package eu.larkc.iris.evaluator;

import java.util.ArrayList;
import java.util.List;

import org.deri.iris.api.basics.IRule;

import eu.larkc.iris.evaluation.bottomup.IDistributedRuleEvaluator;
import eu.larkc.iris.evaluation.bottomup.naive.DistributedNaiveEvaluator;
import eu.larkc.iris.functional.features.LangFeaturesTest;
import eu.larkc.iris.rules.compiler.CascadingRuleCompiler;
import eu.larkc.iris.rules.compiler.IDistributedCompiledRule;
import eu.larkc.iris.rules.compiler.IDistributedRuleCompiler;

/**
 * @author Florian Fischer
 *
 */
public class DistributedNaiveEvaluatorTest extends LangFeaturesTest {
	
	List<IDistributedCompiledRule> compiledRules = new ArrayList<IDistributedCompiledRule>();
	
	
	public DistributedNaiveEvaluatorTest(String string) {
		super(string);		
	}
	
	@Override
	protected void setUp() throws Exception {	
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
	
		super.tearDown();
	}	

	public void testEvaluateRulesRecursive() throws Exception	{
		program = "path(?X, ?Y) :- edge(?X, ?Y)."
			+ "path(?X, ?Y) :- path(?X, ?Z), path(?Z, ?Y).";
		parser.parse(program);
		rules = createRules();

		compile();
		
		int stratum = 1;		
		IDistributedRuleEvaluator eval = new DistributedNaiveEvaluator();
		eval.evaluateRules(stratum, compiledRules, super.defaultConfiguration);
		//TODO: finish and adapt DistributedNaiveEvaluator
	}

	@Override
	protected void compile() throws Exception {		
		
		for (IRule rule : rules) {
			IDistributedRuleCompiler rc = new CascadingRuleCompiler(
					defaultConfiguration);		
			IDistributedCompiledRule compiledRule = rc.compile(rule);
			compiledRules.add(compiledRule);
		}
	}	
	
	
}
