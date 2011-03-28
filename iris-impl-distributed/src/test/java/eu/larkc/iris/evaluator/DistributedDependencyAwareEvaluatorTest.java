/**
 * 
 */
package eu.larkc.iris.evaluator;

import java.util.ArrayList;
import java.util.List;

import org.deri.iris.api.basics.IRule;

import eu.larkc.iris.evaluation.bottomup.IDistributedRuleEvaluator;
import eu.larkc.iris.evaluation.bottomup.naive.DistributedDependencyAwareEvaluator;
import eu.larkc.iris.functional.features.LangFeaturesTest;
import eu.larkc.iris.rules.compiler.CascadingRuleCompiler;
import eu.larkc.iris.rules.compiler.IDistributedCompiledRule;
import eu.larkc.iris.rules.compiler.IDistributedRuleCompiler;

/**
 * @author Florian Fischer
 */
public class DistributedDependencyAwareEvaluatorTest extends LangFeaturesTest {
	
	List<IDistributedCompiledRule> compiledRules = new ArrayList<IDistributedCompiledRule>();
	
	
	public DistributedDependencyAwareEvaluatorTest(String string) {
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
		IDistributedRuleEvaluator eval = new DistributedDependencyAwareEvaluator();
		DistributedCompiledRuleMock pathHead = ((DistributedCompiledRuleMock)compiledRules.get(0));
		pathHead.setMaxEvaluations(3);
		DistributedCompiledRuleMock edgeHead = ((DistributedCompiledRuleMock)compiledRules.get(1));
		edgeHead.setMaxEvaluations(3);
		
		eval.evaluateRules(stratum, compiledRules, super.defaultConfiguration);	
			
		//both count to 2, evaluation starts widh "edge"
		assertEquals(2, pathHead.getEvaluations()); 
			
		//final evaluation of "path" foces one more update for "edge" in which it returns no new inferences
		//-> no additional recomputation for "path"
		assertEquals(3, edgeHead.getEvaluations());	
	}
	
	public void testEvaluateSingleRule() throws Exception {
		program = "path(?X, ?Y) :- edge(?X, ?Y).";
		parser.parse(program);
		rules = createRules();
		
		compile();
		
		int stratum = 1;		
		IDistributedRuleEvaluator eval = new DistributedDependencyAwareEvaluator();		
		
		DistributedCompiledRuleMock pathHead = ((DistributedCompiledRuleMock)compiledRules.get(0));
		pathHead.setMaxEvaluations(2);
		eval.evaluateRules(stratum, compiledRules, super.defaultConfiguration);	
		
		assertEquals(1, pathHead.getEvaluations()); 		
	}
	
	public void testMultipleStratums() throws Exception {		
	
		IDistributedRuleEvaluator eval = new DistributedDependencyAwareEvaluator();		
				
		//stratum 1
		int stratum = 1;		
		program = "path(?X, ?Y) :- edge(?X, ?Y).";
		parser.parse(program);
		rules = createRules();
		
		compile();		
		
		DistributedCompiledRuleMock path = ((DistributedCompiledRuleMock)compiledRules.get(0));
		path.setMaxEvaluations(2);
		eval.evaluateRules(stratum, compiledRules, super.defaultConfiguration);	
		
		assertEquals(1, path.getEvaluations()); 
		
		//stratum 2
		stratum++;
		program = "path(?X, ?Y) :- edge(?X, ?Y)."
			+ "edge(?X, ?Y) :- path(?X, ?Z).";
		parser.parse(program);
		rules = createRules();
		
		compile();
		
		DistributedCompiledRuleMock pathHead = ((DistributedCompiledRuleMock)compiledRules.get(0));	
		pathHead.setMaxEvaluations(3);
		DistributedCompiledRuleMock edgeHead = ((DistributedCompiledRuleMock)compiledRules.get(1));	
		edgeHead.setMaxEvaluations(3);
		
		eval.evaluateRules(stratum, compiledRules, super.defaultConfiguration);	
		
		assertEquals(2, pathHead.getEvaluations()); 		
		assertEquals(3, edgeHead.getEvaluations());			
		
		//stratum 3
		stratum++;
		program = "kek(?X, ?Y) :- mu(?X, ?Y).";
		parser.parse(program);
		rules = createRules();
		
		compile();		
		
		DistributedCompiledRuleMock kekHead = ((DistributedCompiledRuleMock)compiledRules.get(0));	
		kekHead.setMaxEvaluations(3);
		
		eval.evaluateRules(stratum, compiledRules, super.defaultConfiguration);	
		assertEquals(2, pathHead.getEvaluations()); 		
	}
	
	public void testSamePredicateRecursive() throws Exception {
	
		
		program = "edge(?X, ?Z) :- edge(?X, ?Y), edge(?Y, ?Z).";		
		parser.parse(program);
		rules = createRules();

		compile();
		
		int stratum = 1;		
		IDistributedRuleEvaluator eval = new DistributedDependencyAwareEvaluator();
		DistributedCompiledRuleMock pathHead = ((DistributedCompiledRuleMock)compiledRules.get(0));
		pathHead.setMaxEvaluations(3); // 1 time internal + 1 additional simulated delta
		
		eval.evaluateRules(stratum, compiledRules, super.defaultConfiguration);				
	
		assertEquals(3, pathHead.getEvaluations()); 	
	}

	@Override
	protected void compile() throws Exception {		
		
		compiledRules.clear();
		
		for (IRule rule : rules) {
			IDistributedRuleCompiler rc = new CascadingRuleCompiler(
					defaultConfiguration);		
			IDistributedCompiledRule compiledRule = rc.compile(rule);
			
			compiledRules.add(new DistributedCompiledRuleMock(compiledRule));
		}
	}		
	
}
