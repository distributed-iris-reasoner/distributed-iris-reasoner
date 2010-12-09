/**
 * 
 */
package eu.larkc.iris.evaluation;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.hadoop.mapred.JobConf;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.operation.aggregator.Count;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.tap.Hfs;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntryIterator;
import eu.larkc.iris.Configuration;
import eu.larkc.iris.storage.FactsFactory;
import eu.larkc.iris.storage.FactsTap;

/**
 * @author valer
 *
 */
public class PredicateCounts {

	private static final Logger logger = LoggerFactory.getLogger(PredicateCounts.class);
	
	private static Map<FactsFactory, PredicateCounts> predicateCounts = new HashMap<FactsFactory, PredicateCounts>();
	
	private Configuration mConfiguration;
	private FactsFactory mFactsFactory;
	private List<IRule> mRules;
	private Map<IPredicate, Long> counts = new HashMap<IPredicate, Long>();
	
	public static PredicateCounts getInstance(Configuration configuration, FactsFactory factsFactory) {
		if (predicateCounts.containsKey(factsFactory)) {
			return predicateCounts.get(factsFactory);
		}
		predicateCounts.put(factsFactory, new PredicateCounts(configuration, factsFactory));
		return predicateCounts.get(factsFactory);
	}

	private PredicateCounts(Configuration configuration, FactsFactory factsFactory) {
		this.mConfiguration = configuration;
		this.mFactsFactory = factsFactory;
	}
	
	private void compute() {
		for (IRule rule : mRules) {
			ListIterator<ILiteral> iterator = rule.getBody().listIterator();
			while (iterator.hasNext()) {
				ILiteral literal = iterator.next();
				FactsTap facts = mFactsFactory.getFacts(literal.getAtom());
			}
		}

	}
	
	private void compute(IAtom atom) {
		String predicateSymbol = atom.getPredicate().getPredicateSymbol();
		
		String path = mConfiguration.PREDICATE_COUNT_TAIL_HFS_ROOT_PATH + "/" + mFactsFactory.getStorageId() + 
			"/" + predicateSymbol.replace("/", "").replace(":", "");
		Hfs sink = new Hfs(new Fields(predicateSymbol, "count"), path, true);
		
		Long count = findPredicate(sink, predicateSymbol, new JobConf());		
		if (count != null) {
			counts.put(atom.getPredicate(), count);
			return;
		}
		
		FactsTap facts = mFactsFactory.getFacts(atom);
		
		Pipe countPipe = new Pipe("predicate_count");
		countPipe = new GroupBy(countPipe, new Fields(predicateSymbol));
		countPipe = new Every(countPipe, new Count(new Fields("count")), new Fields(predicateSymbol, "count"));
		//countPipe = new Each( countPipe, new Fields(predicateSymbol, "count"), new Identity(new Fields("predicateSymbol", "count")));

		Flow flow = new FlowConnector(mConfiguration.flowProperties).connect(facts, sink, countPipe);
		flow.complete();
		
		count = findPredicate(sink,predicateSymbol,flow.getJobConf());		
		if (count != null) {
			counts.put(atom.getPredicate(), count);
		}
	}
	
	private Long findPredicate(Tap sink, String predicateSymbol, JobConf jobConf) {
		TupleEntryIterator tupleEntryIterator = null;
		try {
			tupleEntryIterator = sink.openForRead(jobConf);
		} catch (IOException e) {
			logger.error("io exception", e);
			throw new RuntimeException("io exception", e);
		}
		while (tupleEntryIterator.hasNext()) {
			Tuple tuple = tupleEntryIterator.next().getTuple();
			if (tuple.getString(0).equals(predicateSymbol)) {
				return tuple.getLong(1);
			}
		}	
		return null;
	}
	
	public Long getCount(IAtom atom) {
		if (!counts.containsKey(atom.getPredicate())) {
			compute(atom);
		}
		return counts.get(atom.getPredicate());
	}
}
