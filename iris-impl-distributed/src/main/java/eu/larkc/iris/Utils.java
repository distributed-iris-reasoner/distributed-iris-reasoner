/**
 * 
 */
package eu.larkc.iris;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.operation.Debug;
import cascading.operation.aggregator.Count;
import cascading.pipe.Each;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.scheme.Scheme;
import cascading.scheme.SequenceFile;
import cascading.tap.Hfs;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;
import cascading.tuple.TupleEntryIterator;
import eu.larkc.iris.evaluation.ConstantFilter;
import eu.larkc.iris.indexing.DistributedFileSystemManager;
import eu.larkc.iris.indexing.PredicateCount;
import eu.larkc.iris.indexing.PredicateData;
import eu.larkc.iris.storage.FieldsVariablesMapping;
import eu.larkc.iris.storage.IRIWritable;
import eu.larkc.iris.storage.WritableComparable;

/**
 * @author valer
 *
 */
public class Utils {

	private static final Logger logger = LoggerFactory.getLogger(Utils.class);
	
	public static Fields getFieldsForAtom(FieldsVariablesMapping fieldsVariablesMapping, IAtom atom) {
		ITuple tuple = atom.getTuple();
		Fields sourceFields = new Fields();
		if (fieldsVariablesMapping != null) {
			sourceFields = sourceFields.append(new Fields(fieldsVariablesMapping.getField(atom, atom.getPredicate())));
		} else {
			sourceFields = sourceFields.append(new Fields(atom.getPredicate().getPredicateSymbol()));
		}
		for (int i = 0; i < tuple.size(); i++) {
			ITerm term = tuple.get(i);
			String field = null;
			if (fieldsVariablesMapping != null) {
				field = fieldsVariablesMapping.getField(atom, term);
			} else {
				//when no field variable mapping is not give (normally this should not happen in real distributed iris usage)
				if (term instanceof IVariable) {
					field = ((IVariable) term).getValue();
				} else {
					field ="CNST";
				}
			}
			// TODO check which types can the value have. also decide what field
			// name should we give for constants
			sourceFields = sourceFields.append(new Fields(field));
		}
		return sourceFields;
	}

	public static Pipe buildPredicateCountPipe(Pipe previousPipe) {
		Pipe predicatesPipe = null;
		if (previousPipe != null) {
			predicatesPipe = new Pipe("predicatesPipe", previousPipe);
		} else {
			predicatesPipe = new Pipe("predicatesPipe");
		}
		predicatesPipe = new GroupBy(predicatesPipe, new Fields(0)); //group by predicates
		predicatesPipe = new Every(predicatesPipe, new Count(new Fields("count")), new Fields(0, "count"));
		predicatesPipe = new Each(predicatesPipe, new Debug(true));
		
		return predicatesPipe;
	}
	
	public static List<PredicateCount> readPredicateCounts(Flow flow, String sinkName) throws IOException {
		List<PredicateCount> predicateCounts = new ArrayList<PredicateCount>();
		
		TupleEntryIterator predicatesEntryIterator = null;
		if (sinkName == null) {
			predicatesEntryIterator = flow.openSink();
		} else {
			predicatesEntryIterator = flow.openSink(sinkName);
		}
		while (predicatesEntryIterator.hasNext()) {
			TupleEntry predicatesEntry = predicatesEntryIterator.next();
			Tuple predicatesTuple = predicatesEntry.getTuple();
			IRIWritable predicate = (IRIWritable) predicatesTuple.getObject(0);
			Long count = predicatesTuple.getLong(1);
			predicateCounts.add(new PredicateCount(predicate, count));
		}
		
		return predicateCounts;
	}
	
	public static void splitStreamPerPredicates(Configuration configuration, DistributedFileSystemManager distributedFileSystemManager, Tap source, List<PredicateCount> predicateCounts, String importName) {
		splitStreamPerPredicates(configuration, distributedFileSystemManager, true, source, predicateCounts, importName, null);
	}

	public static void splitStreamPerPredicates(Configuration configuration, DistributedFileSystemManager distributedFileSystemManager, Tap source, List<PredicateCount> predicateCounts, String resultName, String flowIdentificator) {
		splitStreamPerPredicates(configuration, distributedFileSystemManager, false, source, predicateCounts, resultName, flowIdentificator);
	}

	private static void splitStreamPerPredicates(Configuration configuration, DistributedFileSystemManager distributedFileSystemManager, boolean facts, Tap source, List<PredicateCount> predicateCounts, String locationName, String flowIdentificator) {
		Map<String, Tap> sinks = new HashMap<String, Tap>();
		List<Pipe> pipes = new ArrayList<Pipe>();
		Pipe sourcePipe = new Pipe("sourcePipe");
		Map<Integer, Set<WritableComparable>> locationPredicates = new HashMap<Integer, Set<WritableComparable>>();
		for (PredicateCount predicateCount : predicateCounts) {
			IRIWritable predicate = predicateCount.getPredicate();
			PredicateData predicateData = distributedFileSystemManager.getPredicateData(predicate);
			
			if (!locationPredicates.containsKey(predicateData.getLocation())) {
				locationPredicates.put(predicateData.getLocation(), new HashSet<WritableComparable>());
			}
			Set<WritableComparable> locationPredicate = locationPredicates.get(predicateData.getLocation());
			locationPredicate.add(predicate);
			logger.info("add predicate : " + predicate);
		}
		for (Integer location : locationPredicates.keySet()) {
			String streamId = locationName + String.valueOf(location);

			Scheme predicateScheme = new SequenceFile(new Fields(0, 1, 2));
			predicateScheme.setNumSinkParts(1);
			Tap predicateSink = null;
			if (facts) {
				predicateSink = new Hfs(predicateScheme, distributedFileSystemManager.getPredicateFactsImportPath(location, locationName), true);
			} else {
				predicateSink = new Hfs(predicateScheme, distributedFileSystemManager.getPredicateInferencesPath(location, locationName, flowIdentificator), true);
			}
			sinks.put(streamId, predicateSink);

			Pipe locationPipe = new Pipe(streamId, sourcePipe);
			locationPipe = new Each(locationPipe, new ConstantFilter(0, locationPredicates.get(location)));
			locationPipe = new GroupBy(locationPipe, new Fields(0, 1, 2)); //make group to force reduce
			//predicatePipe = new Each(predicatePipe, new Identity(), new Fields(1, 2));
			pipes.add(locationPipe);
		}
		Flow filterFlow = new FlowConnector(configuration.flowProperties).connect(source, sinks, pipes);
		filterFlow.complete();
	}
}
