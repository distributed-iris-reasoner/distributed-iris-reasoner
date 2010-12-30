/**
 * 
 */
package eu.larkc.iris;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import org.deri.iris.evaluation.IEvaluationStrategy;
import org.deri.iris.storage.IRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.flow.FlowConnector;
import cascading.flow.MultiMapReducePlanner;
import cascading.operation.DebugLevel;
import cascading.tap.Hfs;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.TupleEntry;
import cascading.tuple.TupleEntryIterator;
import eu.larkc.iris.evaluation.bottomup.DistributedBottomUpEvaluationStrategyFactory;
import eu.larkc.iris.evaluation.bottomup.naive.DistributedNaiveEvaluatorFactory;
import eu.larkc.iris.imports.Importer;

/**
 * @author valer
 *
 */
public class Main extends Configured implements Tool {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public enum RULES_TYPE {
		DATALOG,
		RIF
	}
	
	private String project;
	
	private String importName = null;
	
	private boolean rdfImporter = false;
	private boolean ntripleImporter = false;
	private boolean tester = false;
	private boolean processer = false;
	
	//rdf importer args
	private String storageId = null;

	//ntriple importer
	private String inPath = null;

	//test args
	private String sourcePath = null;
	
	private RULES_TYPE rulesType;
	private String rulesFile;
	private boolean keepResults = false;
	private String resultsName;
	private String outputStorageId;
	
	private eu.larkc.iris.Configuration defaultConfiguration;
	//transient private static Map<Object, Object> properties = new HashMap<Object, Object>();
	
	private Parser parser;
	
	protected List<IRule> rules;
	
	private void printUsage() {
		System.out.println("<project_name> <-importRdf storage_id import_name | -importNTriple path_to_file import_name | -process rules_type:<DATALOG|RIF> rules_file_path keep_results:<true:false> results_name output_storage_id > ");
	}
	
	private void processUserArguments(String[] args) {
		if (args.length == 0) {
			printUsage();
			return;
		}
		
		project = args[0];
		
		String operation = args[1];
		if (operation.equalsIgnoreCase("-importRdf")) {
			rdfImporter = true;
			storageId = args[2];
			importName = args[3];
		} else if (operation.equalsIgnoreCase("-importNTriple")) {
			ntripleImporter = true;
			inPath = args[2];
			importName = args[3];
		} else if (operation.equalsIgnoreCase("-process")) {
			processer = true;
			rulesType = RULES_TYPE.valueOf(args[2].toUpperCase());
			rulesFile = args[3];
			if (args[4] != null && !"".equals(args[4])) {
				keepResults = Boolean.valueOf(args[4]);
				resultsName = args[5];
				if (args.length > 6) {
					outputStorageId = args[6];
				}
			}
		} else if (operation.equals("-test")) {
			tester = true;
			sourcePath = args[2];
		}
	}
	
	private JobConf setupJob(Configuration conf) {
		JobConf jobConf = new JobConf(conf, Main.class); 
	    // run the job here.
		
		jobConf.setBoolean("mapred.input.dir.recursive", true);
		
		defaultConfiguration.flowProperties.put("cascading.serialization.tokens", "130=eu.larkc.iris.storage.IRIWritable,131=eu.larkc.iris.storage.PredicateWritable,132=eu.larkc.iris.storage.StringTermWritable");
		
	    if( System.getProperty("log4j.logger") != null )
	    	defaultConfiguration.flowProperties.put( "log4j.logger", System.getProperty("log4j.logger") );

		jobConf.set("mapred.child.java.opts", "-Xms64m -Xmx512m");
		jobConf.setMapSpeculativeExecution(false);
		jobConf.setReduceSpeculativeExecution(false);

		jobConf.setNumMapTasks(8);
		jobConf.setNumReduceTasks(2);

		FlowConnector.setDebugLevel(defaultConfiguration.flowProperties, DebugLevel.VERBOSE);
		MultiMapReducePlanner.setJobConf( defaultConfiguration.flowProperties, jobConf );
		
		//Flow.setJobPollingInterval(defaultConfiguration.flowProperties, 500);
		
		return jobConf;
	}
	
	public int doRdfImport(eu.larkc.iris.Configuration configuration) {
		new Importer().importFromRdf(configuration, project, storageId, importName);
		return 0;
	}

	public int doNTripleImport(eu.larkc.iris.Configuration configuration) {
		new Importer().importFromFile(configuration, project, inPath, importName);
		return 0;
	}

	public int doTester(eu.larkc.iris.Configuration configuration) {
		logger.info("do tester ...");
		
		//JobConf jobConf = new JobConf();
		//jobConf.set("cascading.serialization.tokens", "130=eu.larkc.iris.storage.IRIWritable,131=eu.larkc.iris.storage.PredicateWritable,132=eu.larkc.iris.storage.StringTermWritable");
		//jobConf.setBoolean("mapred.input.dir.recursive", true);
		
		Tap source = new Hfs(Fields.ALL, project + "/" + sourcePath);
		TupleEntryIterator tei = null;
		try {
			tei = source.openForRead(configuration.jobConf);
		} catch (IOException e) {
			logger.error("io exception", e);
			return -1;
		}
		
		while (tei.hasNext()) {
			TupleEntry te = tei.next();
			logger.info(te.toString());
		}
		
		return 0;
	}
	
	public int doProcess() {
		defaultConfiguration.project = project;
		defaultConfiguration.keepResults = keepResults;
		defaultConfiguration.resultsName = resultsName;
		defaultConfiguration.outputStorageId = outputStorageId;
		try {
			evaluate(null, new ArrayList<IVariable>(), defaultConfiguration);
		} catch (EvaluationException e) {
			logger.error("evaluation exception", e);
			return -1;
		}

	    return 0; 		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int run(String[] args) throws Exception {
		GenericOptionsParser gop = new GenericOptionsParser(getConf(), new org.apache.commons.cli.Options(), args);

		processUserArguments(gop.getRemainingArgs());
		
		Collection<String> expressions = createExpressions();
		parser = new Parser();
		StringBuffer buffer = new StringBuffer();

		for (String expression : expressions) {
			buffer.append(expression);
		}

		try {
			parser.parse(buffer.toString());
		} catch (ParserException e) {
			logger.error("rules parser exception", e);
			throw new RuntimeException("rules parser exception", e);
		}
		
		rules = parser.getRules();

		Configuration hadoopConf = gop.getConfiguration();
		defaultConfiguration.hadoopConfiguration = hadoopConf;
		defaultConfiguration.jobConf = setupJob(hadoopConf);

		if (rdfImporter) {
			return doRdfImport(defaultConfiguration);
		} else if (ntripleImporter) {
			return doNTripleImport(defaultConfiguration);
		} else if (tester) {
			return doTester(defaultConfiguration);
		} else if (processer) {
			return doProcess();
		}
		
		return -1;
	}

	protected Collection<String> createExpressions() {
		Collection<String> expressions = new ArrayList<String>();
		if (rulesType == RULES_TYPE.DATALOG) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(new File(rulesFile)));
				String line = null;
				while ((line = br.readLine()) != null) {
					logger.info(line);
					expressions.add(line);
				}
			} catch (FileNotFoundException e) {
				logger.error("the rules files can not be located", e);
				throw new RuntimeException("the rules files can not be located", e);
			} catch (IOException e) {
				logger.error("io exception reading the rules files", e);
				throw new RuntimeException("io exception reading the rules files", e);
			}
			//expressions.add("subClassOf( ?X, ?Z ) :- subClassOf( ?X, ?Y ), subClassOf( ?Y, ?Z ).");
			//expressions.add("type( ?X, ?Z ) :- type( ?X, ?Y ), subClassOf( ?Y, ?Z ).");
		}
		
		if (rulesType == RULES_TYPE.RIF) {
			
		}
		
		return expressions;
	}

	//parseQuery("?- subClassOf(?X, ?Y).")
	private IQuery parseQuery(String query) {
		Parser parser = new Parser();
		try {
			parser.parse(query);
			List<IQuery> queries = parser.getQueries();

			if (queries.size() == 1) {
				return queries.get(0);
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Main() {
		logger.info("start iris distributed reasoner ...");
		
		org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
		setConf(configuration);
		
		defaultConfiguration = new eu.larkc.iris.Configuration();
		defaultConfiguration.evaluationStrategyFactory = new DistributedBottomUpEvaluationStrategyFactory(new DistributedNaiveEvaluatorFactory());
	}
	
	public static void main(String[] args) throws Exception {
		int ret = ToolRunner.run(new Main(), args); // calls your run() method. 
	    System.exit(ret);
	}
	
	private void evaluate(IQuery query, List<IVariable> outputVariables,
			eu.larkc.iris.Configuration configuration) throws EvaluationException {
		//IRelation relation = evaluate(FactsFactory.getInstance("default"), "?- p(?X, ?Y).");
		
		IEvaluationStrategy strategy = configuration.evaluationStrategyFactory
			.createEvaluator(rules, configuration); 
		
		IRelation relation = strategy.evaluateQuery(query, outputVariables);		
	}
	
}
