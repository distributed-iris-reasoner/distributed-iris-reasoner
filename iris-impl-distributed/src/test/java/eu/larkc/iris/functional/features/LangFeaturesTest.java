/**
 * 
 */
package eu.larkc.iris.functional.features;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.compiler.Parser;

import eu.larkc.iris.evaluation.distributed.EvaluationTest;
import eu.larkc.iris.rules.compiler.CascadingRuleCompiler;
import eu.larkc.iris.rules.compiler.IDistributedCompiledRule;
import eu.larkc.iris.rules.compiler.IDistributedRuleCompiler;
import eu.larkc.iris.storage.FactsFactory;

/**
 * A collection of tests that ensure the correction support of various Datalog language features.
 *  
 * @author Florian Fischer, fisf, 09-Dec-2010
 */
public abstract class LangFeaturesTest extends EvaluationTest {

	/**
	 * The Test program
	 */
	protected String program = "";
	
	/**
	 * Parser for test cases
	 */
	protected Parser parser;
	
	/**
	 * Facts to use
	 */
	protected FactsFactory facts;
	
	@Override
	protected void setUp() throws Exception {
		parser = new Parser();
		parser.parse(program);
		
		super.setUp();
	}

	/**
	 * @param string
	 */
	public LangFeaturesTest(String string) {
		super(string);
	}

	@Override
	protected void createFacts() throws IOException {
		Properties p = new Properties();
		p.put("PROPERTIES", "/facts-configuration-test.properties");
		p.put("STORAGE_PROPERTIES", "/facts-storage-configuration-test.properties");
		facts = FactsFactory.getInstance("default", p);		
	}

	@Override
	protected List<IQuery> createQueries() {
		return parser.getQueries();
	}

	@Override
	protected List<IRule> createRules() {
		return parser.getRules();
	}
	
	/**
	 * Starts compilation for a programm.
	 * 
	 * @return
	 * @throws Exception
	 */
	protected void compile() throws Exception {
		
		// compile to cascading
		//IDistributedRuleCompiler rc = new CascadingRuleCompiler(defaultConfiguration);
		for (IRule rule : rules) {
			IDistributedRuleCompiler rc = new CascadingRuleCompiler(defaultConfiguration);
			IDistributedCompiledRule compiledRule = rc.compile(rule);
		}	
	}
}
