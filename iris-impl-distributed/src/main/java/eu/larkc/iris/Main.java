/**
 * 
 */
package eu.larkc.iris;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import cascading.flow.MultiMapReducePlanner;
import eu.larkc.iris.evaluation.bottomup.DistributedBottomUpEvaluationStrategyFactory;
import eu.larkc.iris.evaluation.bottomup.naive.DistributedNaiveEvaluatorFactory;
import eu.larkc.iris.storage.FactsFactory;

/**
 * @author valer
 *
 */
public class Main extends Configured implements Tool {

	private eu.larkc.iris.Configuration defaultConfiguration;
	transient private static Map<Object, Object> properties = new HashMap<Object, Object>();
	
	private Parser parser;
	
	protected List<IRule> rules;
	
	@Override
	public int run(String[] args) throws Exception {
		GenericOptionsParser gop = new GenericOptionsParser(getConf(), new org.apache.commons.cli.Options(), args);
		
		JobConf conf = new JobConf(gop.getConfiguration(), Main.class); 
	    // run the job here.
		MultiMapReducePlanner.setJobConf( properties, conf );
		
		evaluate(FactsFactory.getInstance("default"), parseQuery("?- p(?X, ?Y)."), new ArrayList<IVariable>(), defaultConfiguration);

	    return 0; 
	}

	protected Collection<String> createExpressions() {
		Collection<String> expressions = new ArrayList<String>();

		expressions.add("p( ?X, ?Y ) :- q( ?X, ?Y ), r( ?Y, ?Z ).");

		return expressions;
	}

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
		org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
		setConf(configuration);
		
		defaultConfiguration = new eu.larkc.iris.Configuration();
		defaultConfiguration.evaluationStrategyFactory = new DistributedBottomUpEvaluationStrategyFactory(new DistributedNaiveEvaluatorFactory());
		
		Collection<String> expressions = createExpressions();
		parser = new Parser();
		StringBuffer buffer = new StringBuffer();

		for (String expression : expressions) {
			buffer.append(expression);
		}

		try {
			parser.parse(buffer.toString());
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		rules = parser.getRules();
	}
	
	public static void main(String[] args) throws Exception {
		int ret = ToolRunner.run(new Main(), args); // calls your run() method. 
	    System.exit(ret); 
	}
	
	private void evaluate(FactsFactory facts, IQuery query, List<IVariable> outputVariables,
			eu.larkc.iris.Configuration configuration) throws EvaluationException {
		//IRelation relation = evaluate(FactsFactory.getInstance("default"), "?- p(?X, ?Y).");
		
		IEvaluationStrategy strategy = configuration.evaluationStrategyFactory
			.createEvaluator(facts, rules, configuration); 

		IRelation relation = strategy.evaluateQuery(query, outputVariables);		
	}
	
}
