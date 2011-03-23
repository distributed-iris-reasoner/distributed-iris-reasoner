/**
 * 
 */
package eu.larkc.iris.functional.features;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MiniMRCluster;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.compiler.Parser;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.flow.MultiMapReducePlanner;
import cascading.operation.DebugLevel;

import eu.larkc.iris.Configuration;
import eu.larkc.iris.evaluation.bottomup.DistributedBottomUpEvaluationStrategyFactory;
import eu.larkc.iris.evaluation.bottomup.naive.DistributedEvaluatorFactory;
import eu.larkc.iris.evaluation.distributed.EvaluationTest;
import eu.larkc.iris.rules.compiler.CascadingRuleCompiler;
import eu.larkc.iris.rules.compiler.IDistributedCompiledRule;
import eu.larkc.iris.rules.compiler.IDistributedRuleCompiler;
import eu.larkc.iris.storage.FactsConfigurationFactory;
import eu.larkc.iris.storage.FactsFactory;

/**
 * A collection of tests that ensure the correction support of various Datalog
 * language features.
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
	 * Hadoop specific
	 */
	protected static FileSystem fileSys;
	int numMapTasks = 1;
	int numReduceTasks = 1;

	/**
	 * hadoop jobconf
	 */
	protected JobConf jobConf;

	/**
	 * Facts to use
	 */
	protected FactsFactory facts;

	@Override
	protected void setUp() throws Exception {

		defaultConfiguration = new Configuration();
		defaultConfiguration.ruleEvaluationBlockers.clear();
		defaultConfiguration.preStratificationOptimizer.clear();
		defaultConfiguration.postStratificationOptimizations.clear();
		defaultConfiguration.recursiveRulePreProcessors.clear();
		defaultConfiguration.stratifiers.clear();
		defaultConfiguration.ruleOptimisers.clear();

		defaultConfiguration.evaluationStrategyFactory = new DistributedBottomUpEvaluationStrategyFactory(
				new DistributedEvaluatorFactory());

		if (jobConf != null)
			return;

		org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
		defaultConfiguration.hadoopConfiguration = conf;
		jobConf = new JobConf();

		jobConf.set("mapred.child.java.opts", "-Xmx512m");
		jobConf.setMapSpeculativeExecution(false);
		jobConf.setReduceSpeculativeExecution(false);

		jobConf.setBoolean("mapred.input.dir.recursive", true);
		jobConf.setNumMapTasks(numMapTasks);
		jobConf.setNumReduceTasks(numReduceTasks);

		defaultConfiguration.flowProperties
				.put(
						"cascading.serialization.tokens",
						"130=eu.larkc.iris.storage.IRIWritable,131=eu.larkc.iris.storage.PredicateWritable,132=eu.larkc.iris.storage.StringTermWritable");

		Flow.setJobPollingInterval(defaultConfiguration.flowProperties, 500); 
		FlowConnector.setDebugLevel(defaultConfiguration.flowProperties,
				DebugLevel.VERBOSE);
		MultiMapReducePlanner.setJobConf(defaultConfiguration.flowProperties,
				jobConf);

		defaultConfiguration.jobConf = jobConf;

		FactsFactory.PROPERTIES = "/facts-configuration-test.properties";

		FactsConfigurationFactory.STORAGE_PROPERTIES = "/facts-storage-configuration-test.properties";

		parser = new Parser();
		parser.parse(program);

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
	
		super.tearDown();
		FileUtil.fullyDelete(new File("test/inferences"));
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
		p.put("STORAGE_PROPERTIES",
				"/facts-storage-configuration-test.properties");
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

		for (IRule rule : rules) {
			IDistributedRuleCompiler rc = new CascadingRuleCompiler(
					defaultConfiguration);
			@SuppressWarnings("unused")
			IDistributedCompiledRule compiledRule = rc.compile(rule);
		}
	}
}
