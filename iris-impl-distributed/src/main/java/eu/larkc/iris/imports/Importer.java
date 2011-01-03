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
	
	public void importFromRdf(Configuration configuration, String project, String storageId, String importName) {
		Tap source = FactsFactory.getInstance(storageId).getFacts();
		
		Tap sink = new Hfs(source.getSourceFields(), project + "/data/facts/" + importName, true );

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
	}
	
	public void processNTriple(Configuration configuration, String inPath, String project, String importName) {
		Tap source = new Hfs(new TextLine(), inPath);

		Tap sink = new Hfs(Fields.ALL, project + "/data/facts/" + importName, true );

		int[] groups = {2, 1, 3};
		RegexParser parser = new RegexParser(Fields.UNKNOWN, "^(<[^\\s]+>)\\s*(<[^\\s]+>)\\s*([<\"].*[^\\s])\\s*.\\s*$", groups);
		Pipe sourcePipe = new Each("sourcePipe", new Fields("line"), parser);
		
		sourcePipe = new Each(sourcePipe, Fields.ALL, new TextImporterFunction());
		
		Flow aFlow = new FlowConnector(configuration.flowProperties).connect(source, sink, sourcePipe);
		aFlow.complete();		
	}
}
