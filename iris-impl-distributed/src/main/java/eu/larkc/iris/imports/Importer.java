/*
 * Copyright 2010 Softgress - http://www.softgress.com/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.larkc.iris.imports;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
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
import cascading.tap.Lfs;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import eu.larkc.iris.Configuration;
import eu.larkc.iris.Utils;
import eu.larkc.iris.indexing.DistributedFileSystemManager;
import eu.larkc.iris.indexing.PredicateCount;
import eu.larkc.iris.storage.FactsFactory;

/**
 * Imports facts from different types of sources.
 * Current source types are n-triple file and Sesame RDF HTTP/memory repositories
 * 
 * @author valer.roman@softgress.com
 *
 */
public class Importer {

	private static final Logger logger = LoggerFactory.getLogger(Importer.class);

	private Configuration configuration = null;
	private DistributedFileSystemManager distributedFileSystemManager = null;
	
	public Importer(Configuration configuration) {
		this.configuration = configuration;
		this.distributedFileSystemManager = new DistributedFileSystemManager(configuration);
	}
	
	/**
	 * Imports facts into {@code importName} from an RDF storage identified by {@code storageId}
	 * 
	 * @param storageId the RDF storage id
	 * @param importName the name to give to the import
	 */
	public void importFromRdf(String storageId, String importName) {
		Tap source = FactsFactory.getInstance(storageId).getFacts(new Fields(0, 1, 2));
		
		SequenceFile sinkScheme = new SequenceFile(source.getSourceFields());
		sinkScheme.setNumSinkParts(1);
		String importPath = distributedFileSystemManager.getImportPath(importName);
		Tap sink = new Hfs(sinkScheme, importPath, true );

		Map<String, Tap> sources = new HashMap<String, Tap>();
		sources.put("source", source);

		Map<String, Tap> sinks = new HashMap<String, Tap>();
		sinks.put("sink", sink);

		Pipe sourcePipe = new Pipe("source");
		sourcePipe = new Each(sourcePipe, Fields.ALL, new Identity());
		Pipe identity = new Pipe("sink", sourcePipe);
		
		Flow aFlow = new FlowConnector(configuration.flowProperties).connect(sources, sink, identity);
		aFlow.complete();
		
		if (configuration.doPredicateIndexing) {
			try {
				processIndexing(importName);
				
				FileSystem fs = FileSystem.get(configuration.hadoopConfiguration);
				fs.delete(new Path(importPath), true);
			} catch (IOException e) {
				logger.error("io exception", e);
				throw new RuntimeException("io exception", e);				
			}
		}
	}

	/**
	 * Imports facts into {@code importName} from an n-triple format file located at {@code inputPath}
	 * 
	 * @param inputPath the file path
	 * @param importName the name of the import
	 */
	public void importFromFile(String inputPath, String importName) {
		try {
			processNTriple(inputPath, importName);
		} catch (IOException e) {
			logger.error("io exception", e);
			throw new RuntimeException("io exception", e);
		}
	}
	
	/*
	 * Does the actual import from a n-triple file
	 */
	public void processNTriple(String inPath, String importName) throws IOException {
		Tap source = new Lfs(new TextLine(), inPath);

		SequenceFile sinkScheme = new SequenceFile(new Fields(0, 1, 2));
		sinkScheme.setNumSinkParts(1);
		String importPath = distributedFileSystemManager.getImportPath(importName);
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
	
	/*
	 * perform an indexing of the data based on the RDF predicates
	 * It groups the data by predicate into different locations 
	 */
	private void processIndexing(String importName) throws IOException {
		//process indexing
		String predicateGroupsTempPath = distributedFileSystemManager.getPredicateGroupsTempPath(importName);
		FileSystem fs = FileSystem.get(configuration.hadoopConfiguration);
		if (fs.exists(new Path(predicateGroupsTempPath))) {
			fs.delete(new Path(predicateGroupsTempPath), true);
		}
		
		Tap predicatesSource = new Hfs(new Fields(0, 1, 2), distributedFileSystemManager.getImportPath(importName), true );
		Tap predicatesSink = new Hfs(new Fields(0, 1), predicateGroupsTempPath);
		Pipe predicatesPipe = Utils.buildPredicateCountPipe(null);
		Flow predicatesFlow = new FlowConnector(configuration.flowProperties).connect(predicatesSource, predicatesSink, predicatesPipe);
		predicatesFlow.complete();
		
		List<PredicateCount> predicateCounts = Utils.readPredicateCounts(predicatesFlow, null);
		
		distributedFileSystemManager.addPredicates(predicateCounts);
		
		Tap importSource = new Hfs(new Fields(0, 1, 2), distributedFileSystemManager.getImportPath(importName), true );
		Utils.splitStreamPerPredicates(configuration, distributedFileSystemManager, importSource, predicateCounts, importName);

		distributedFileSystemManager.savePredicateConfig();
		fs.delete(new Path(predicateGroupsTempPath), true);
	}

}
