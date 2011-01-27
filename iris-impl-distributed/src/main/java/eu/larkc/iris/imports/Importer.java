/**
 * 
 */
package eu.larkc.iris.imports;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.operation.Identity;
import cascading.operation.regex.RegexParser;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.scheme.SequenceFile;
import cascading.scheme.TextLine;
import cascading.tap.Hfs;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import eu.larkc.iris.Configuration;
import eu.larkc.iris.storage.FactsFactory;

/**
 * @author valer
 *
 */
public class Importer {

	private static final Logger logger = LoggerFactory.getLogger(Importer.class);
	
	/*
	private class PredicateCount {
		private PredicateWritable predicate;
		private Long count;
		
		private PredicateCount(PredicateWritable predicate, Long count) {
			this.predicate = predicate;
			this.count = count;
		}
	}
	*/
	
	public void importFromRdf(Configuration configuration, String project, String storageId, String importName) {
		Tap source = FactsFactory.getInstance(storageId).getFacts();
		
		SequenceFile sinkScheme = new SequenceFile(source.getSourceFields());
		sinkScheme.setNumSinkParts(1);
		Tap sink = new Hfs(sinkScheme, project + "/facts/" + importName, true );

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

	public void importFromFile(Configuration configuration, String project, String inputPath, String importName) {
		String outPath = "import_ntriple_" + System.currentTimeMillis() + ".nt";
		FileSystem fs = null;
		try {
			fs = FileSystem.get(configuration.hadoopConfiguration);
			FileUtil.copy(new File(inputPath), fs, new Path(outPath), false, configuration.hadoopConfiguration);
		} catch (IOException e) {
			logger.error("io exception", e);
			return;
		}
		
		processNTriple(configuration, outPath, project, importName);
		
		try {
			if (fs.exists(new Path(outPath))) {
				fs.delete(new Path(outPath), false);
			}
		} catch (IOException e) {
			logger.error("io exception", e);
			return;
		}
		
		/*
		try {
			processIndexing(configuration, project, importName);
		} catch (IOException e) {
			logger.error("io exception", e);
			throw new RuntimeException("io exception", e);
		}
		*/
	}
	
	public void processNTriple(Configuration configuration, String inPath, String project, String importName) {
		Tap source = new Hfs(new TextLine(), inPath);

		SequenceFile sinkScheme = new SequenceFile(new Fields(0, 1, 2));
		sinkScheme.setNumSinkParts(1);
		Tap sink = new Hfs(sinkScheme, project + "/facts/" + importName, true );

		int[] groups = {2, 1, 3};
		RegexParser parser = new RegexParser(Fields.UNKNOWN, "^(<[^\\s]+>|_:node\\w+)\\s*(<[^\\s]+>|_:node\\w+)\\s*([<\"].*[^\\s]|_:node\\w+)\\s*.\\s*$", groups); //_ is for generated nodes like _:node15n67q1f2x14
		Pipe sourcePipe = new Each("sourcePipe", new Fields("line"), parser);
		
		sourcePipe = new Each(sourcePipe, Fields.ALL, new TextImporterFunction());
				
		Flow aFlow = new FlowConnector(configuration.flowProperties).connect(source, sink, sourcePipe);
		aFlow.complete();
	}
	
	/*
	private void processIndexing(Configuration configuration, String project, String importName) throws IOException {
		//process indexing
		
		Tap predicatesSource = new Hfs(new Fields(0, 1, 2), project + "/facts/" + importName, true );
		Tap predicatesSink = new Hfs(new Fields(0, 1), project + "/facts/predicates/" + importName);
		Pipe predicatesPipe = new Pipe("predicatesPipe");
		predicatesPipe = new GroupBy(predicatesPipe, new Fields(0)); //group by predicates
		predicatesPipe = new Every(predicatesPipe, new Count(new Fields("count")), new Fields(0, "count"));
		Flow predicatesFlow = new FlowConnector(configuration.flowProperties).connect(predicatesSource, predicatesSink, predicatesPipe);
		predicatesFlow.complete();
		
		List<PredicateCount> predicateCounts = new ArrayList<PredicateCount>();
		
		TupleEntryIterator predicatesEntryIterator = predicatesFlow.openSink();
		while (predicatesEntryIterator.hasNext()) {
			TupleEntry predicatesEntry = predicatesEntryIterator.next();
			Tuple predicatesTuple = predicatesEntry.getTuple();
			PredicateWritable predicate = (PredicateWritable) predicatesTuple.getObject(0);
			Long count = predicatesTuple.getLong(1);
			predicateCounts.add(new PredicateCount(predicate, count));
		}

		Tap importSource = new Hfs(new Fields(0, 1, 2), project + "/facts/" + importName, true );
		Map<String, Tap> sinks = new HashMap<String, Tap>();
		List<Pipe> pipes = new ArrayList<Pipe>();
		Pipe sourcePipe = new Pipe("sourcePipe");
		for (PredicateCount predicateCount : predicateCounts) {
			PredicateWritable predicate = predicateCount.predicate;
			
			Scheme predicateScheme = new SequenceFile(new Fields(0, 1));
			predicateScheme.setNumSinkParts(1);
			Tap predicateSink = new Hfs(predicateScheme, project + "/facts/" + getPredicateId(predicate) + "/data/" + importName, true);
			sinks.put(getPredicateId(predicate), predicateSink);
			
			Pipe predicatePipe = new Pipe(getPredicateId(predicate), sourcePipe);
			predicatePipe = new Each(predicatePipe, new PredicateFilter(predicate));
			predicatePipe = new Each(predicatePipe, new Identity(), new Fields(1, 2));
			pipes.add(predicatePipe);
		}
		Flow filterFlow = new FlowConnector(configuration.flowProperties).connect(importSource, sinks, pipes);
		filterFlow.complete();
		
		for (PredicateCount predicateCount : predicateCounts) {
			PredicateWritable predicate = predicateCount.predicate;
			Long count = predicateCount.count;
			
			writePredicateCount(configuration, project, predicate, count);
		}
	}
	
	private void writePredicateCount(Configuration configuration, String project, PredicateWritable predicate, Long count) throws IOException {
		FileSystem fs = FileSystem.get(configuration.hadoopConfiguration);
		Path countPath = new Path(project + "/facts/" + getPredicateId(predicate) + "/count");
		Long totalCount = new Long(0);
		if(fs.exists(countPath)) {
			FSDataInputStream countDIS = fs.open(countPath);
			totalCount = countDIS.readLong();
			countDIS.close();
		}
		totalCount += count;
		FSDataOutputStream countDOS = fs.create(countPath, true);
		countDOS.writeLong(totalCount);
		countDOS.close();			
	}

	private String getPredicateId(PredicateWritable predicate) {
		String symbol = predicate.getURI().replace("/", "|");
		return (symbol.length() > 10 ? symbol.substring(symbol.length() - 10) : symbol) + "$" + predicate.getArity();
	}
	*/
}
