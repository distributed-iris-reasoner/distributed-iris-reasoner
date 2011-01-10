/**
 * 
 */
package eu.larkc.iris.rules.optimisation;

import java.io.IOException;

import eu.larkc.iris.evaluation.distributed.ProgramEvaluationTest;

/**
 * @author valer
 *
 */
public class JoinOptimizerTest extends ProgramEvaluationTest {

	public JoinOptimizerTest(String name) {
		super(name);
	}
	
	/* (non-Javadoc)
	 * @see eu.larkc.iris.evaluation.distributed.ProgramEvaluationTest#getRulesFile()
	 */
	@Override
	protected String getRulesFile() {
		return "/rules/join_optimizer.xml";
	}

	/* (non-Javadoc)
	 * @see eu.larkc.iris.evaluation.distributed.EvaluationTest#createFacts()
	 */
	@Override
	protected void createFacts() throws IOException {
		//defaultConfiguration.project = "test";
		//new Importer().processNTriple(defaultConfiguration, this.getClass().getResource("/facts/default.nt").getPath(), defaultConfiguration.project, "import");
	}

	public void testJoinOptimizer() {
		JoinOptimizer joinOptimizer = new JoinOptimizer();
		joinOptimizer.optimise(rules.get(0));
	}
}
