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
			+ "edge(?X, ?Y) :- path(?X, ?Z).";
		parser.parse(program);
		rules = createRules();

		compile();
		
		int stratum = 1;		
		IDistributedRuleEvaluator eval = new DistributedNaiveEvaluator();
		eval.evaluateRules(stratum, compiledRules, super.defaultConfiguration);	
		
		DistributedCompiledRuleMock pathHead = ((DistributedCompiledRuleMock)compiledRules.get(0));
		//both count to 2, evaluation starts widh "edge"
		assertEquals(2, pathHead.getEvaluations()); 
		
		DistributedCompiledRuleMock edgeHead = ((DistributedCompiledRuleMock)compiledRules.get(1));
		//final evaluation of "path" foces one more update for "edge" in which it returns no new inferences
		//-> no additional recomputation for "path"
		assertEquals(3, edgeHead.getEvaluations());	
	}

	@Override
	protected void compile() throws Exception {		
		
		for (IRule rule : rules) {
			IDistributedRuleCompiler rc = new CascadingRuleCompiler(
					defaultConfiguration);		
			IDistributedCompiledRule compiledRule = rc.compile(rule);
			
			compiledRules.add(new DistributedCompiledRuleMock(compiledRule));
		}
	}	
	
	
}
