/**
 * 
 */
package eu.larkc.iris.imports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.operation.Debug;
import cascading.operation.Identity;
import cascading.operation.aggregator.Count;
import cascading.operation.regex.RegexParser;
import cascading.pipe.Each;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.scheme.Scheme;
import cascading.scheme.SequenceFile;
import cascading.scheme.TextLine;
import cascading.tap.Hfs;
import cascading.tap.Lfs;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;
import cascading.tuple.TupleEntryIterator;
import eu.larkc.iris.Configuration;
import eu.larkc.iris.evaluation.ConstantFilter;
import eu.larkc.iris.indexing.IndexingManager;
import eu.larkc.iris.indexing.PredicateCount;
import eu.larkc.iris.indexing.PredicateData;
import eu.larkc.iris.storage.FactsFactory;
import eu.larkc.iris.storage.IRIWritable;
import eu.larkc.iris.storage.WritableComparable;

/**
 * @author valer
 *
 */
public class Importer {

	private static final Logger logger = LoggerFactory.getLogger(Importer.class);

	private Configuration configuration = null;
	private IndexingManager indexingManager = null;
	
	public Importer(Configuration configuration) {
		this.configuration = configuration;
		this.indexingManager = new IndexingManager(configuration);
	}
	
	public void importFromRdf(String storageId, String importName) {
		IndexingManager indexingManager = new IndexingManager(configuration);
		
		Tap source = FactsFactory.getInstance(storageId).getFacts();
		
		SequenceFile sinkScheme = new SequenceFile(source.getSourceFields());
		sinkScheme.setNumSinkParts(1);
		Tap sink = new Hfs(sinkScheme, indexingManager.getImportPath(importName), true );

		Map<String, Tap> sources = new HashMap<String, Tap>();
		sources.put("source", source);

		Map<String, Tap> sinks = new HashMap<String, Tap>();
		sinks.put("sink", sink);
		//sinks.put("sink1", sink1);

		Pipe sourcePipe = new Pipe("source");
		sourcePipe = new Each(sourcePipe, Fields.ALL, new Identity());
		Pipe identity = new Pipe("sink", sourcePipe);
		//identity = new Each(identity, source.getSourceFields(), new Identity(source.getSourceFields()));
		//Pipe identity1 = new Pipe("sink1", sourcePipe);
		//identity1 = new Each(identity1, source.getSourceFields(), new Identity(source.getSourceFields()));
		
		Flow aFlow = new FlowConnector(configuration.flowProperties).connect(sources, sink, identity);
		aFlow.complete();
	}

	public void importFromFile(String inputPath, String importName) {
		try {
			processNTriple(inputPath, importName);
		} catch (IOException e) {
			logger.error("io exception", e);
			throw new RuntimeException("io exception", e);
		}
	}
	
	public void processNTriple(String inPath, String importName) throws IOException {
		Tap source = new Lfs(new TextLine(), inPath);

		SequenceFile sinkScheme = new SequenceFile(new Fields(0, 1, 2));
		sinkScheme.setNumSinkParts(1);
		String importPath = indexingManager.getImportPath(importName);
		Tap sink = new Hfs(sinkScheme, importPath, true );
		
		int[] groups = {2, 1, 3};
		RegexParser parser = new RegexParser(Fields.UNKNOWN, "^(<[^\\s]+>|_:node\\w+)\\s*(<[^\\s]+>|_:node\\w+)\\s*([<\"].*[^\\s]|_:node\\w+)\\s*.\\s*$", groups); //_ is for generated nodes like _:node15n67q1f2x14
		Pipe sourcePipe = new Each("sourcePipe", new Fields("line"), parser);
		sourcePipe = new Each(sourcePipe, Fields.ALL, new TextImporterFunction());
		
		Flow aFlow = new FlowConnector(configuration.flowProperties).connect(source, sink, sourcePipe);
		aFlow.complete();
		
		if (configuration.doPredicateIndexing) {
			processIndexing(importName);
			
			FileSystem fs = FileSystem.get(configuration.hadoopConfiguration);
			fs.delete(new Path(importPath), true);
		}
	}
	
	private void processIndexing(String importName) throws IOException {
		//process indexing
		String predicateGroupsTempPath = indexingManager.getPredicateGroupsTempPath(importName);
		FileSystem fs = FileSystem.get(configuration.hadoopConfiguration);
		if (fs.exists(new Path(predicateGroupsTempPath))) {
			fs.delete(new Path(predicateGroupsTempPath), true);
		}
		
		Tap predicatesSource = new Hfs(new Fields(0, 1, 2), indexingManager.getImportPath(importName), true );
		Tap predicatesSink = new Hfs(new Fields(0, 1), predicateGroupsTempPath);
		Pipe predicatesPipe = new Pipe("predicatesPipe");
		predicatesPipe = new GroupBy(predicatesPipe, new Fields(0)); //group by predicates
		predicatesPipe = new Every(predicatesPipe, new Count(new Fields("count")), new Fields(0, "count"));
		predicatesPipe = new Each(predicatesPipe, new Debug(true));
		Flow predicatesFlow = new FlowConnector(configuration.flowProperties).connect(predicatesSource, predicatesSink, predicatesPipe);
		predicatesFlow.complete();
		
		List<PredicateCount> predicateCounts = new ArrayList<PredicateCount>();
		
		TupleEntryIterator predicatesEntryIterator = predicatesFlow.openSink();
		while (predicatesEntryIterator.hasNext()) {
			TupleEntry predicatesEntry = predicatesEntryIterator.next();
			Tuple predicatesTuple = predicatesEntry.getTuple();
			IRIWritable predicate = (IRIWritable) predicatesTuple.getObject(0);
			Long count = predicatesTuple.getLong(1);
			predicateCounts.add(new PredicateCount(predicate, count));
		}

		indexingManager.addPredicates(predicateCounts);
		
		Tap importSource = new Hfs(new Fields(0, 1, 2), indexingManager.getImportPath(importName), true );
		Map<String, Tap> sinks = new HashMap<String, Tap>();
		List<Pipe> pipes = new ArrayList<Pipe>();
		Pipe sourcePipe = new Pipe("sourcePipe");
		Map<Integer, Set<WritableComparable>> locationPredicates = new HashMap<Integer, Set<WritableComparable>>();
		for (PredicateCount predicateCount : predicateCounts) {
			IRIWritable predicate = predicateCount.getPredicate();
			PredicateData predicateData = indexingManager.getPredicateData(predicate);
			
			if (!locationPredicates.containsKey(predicateData.getLocation())) {
				locationPredicates.put(predicateData.getLocation(), new HashSet<WritableComparable>());
			}
			Set<WritableComparable> locationPredicate = locationPredicates.get(predicateData.getLocation());
			locationPredicate.add(predicate);
			logger.info("add predicate : " + predicate);
		}
		for (Integer location : locationPredicates.keySet()) {
			String streamId = importName + String.valueOf(location);

			Scheme predicateScheme = new SequenceFile(new Fields(0, 1, 2));
			predicateScheme.setNumSinkParts(1);
			Tap predicateSink = new Hfs(predicateScheme, indexingManager.getPredicateFactsImportPath(location, importName), true);
			sinks.put(streamId, predicateSink);

			Pipe locationPipe = new Pipe(streamId, sourcePipe);
			locationPipe = new Each(locationPipe, new ConstantFilter(0, locationPredicates.get(location)));
			locationPipe = new GroupBy(locationPipe, new Fields(0, 1, 2)); //make group to force reduce
			//predicatePipe = new Each(predicatePipe, new Identity(), new Fields(1, 2));
			pipes.add(locationPipe);
		}
		Flow filterFlow = new FlowConnector(configuration.flowProperties).connect(importSource, sinks, pipes);
		filterFlow.complete();

		fs.delete(new Path(predicateGroupsTempPath), true);
	}

}
