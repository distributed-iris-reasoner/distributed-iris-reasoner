package eu.larkc.iris.functional;

import java.util.Properties;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.operation.aggregator.Count;
import cascading.operation.regex.RegexSplitGenerator;
import cascading.pipe.Each;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.scheme.Scheme;
import cascading.scheme.TextLine;
import cascading.tap.Hfs;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import eu.larkc.iris.storage.RdfTripleScheme;

public class DistributedReasoner {

	public static void main(String[] args) throws Exception {
		String inputPath = args[0];
		String outputPath = args[1];

		//Scheme inputScheme = new TextLine(new Fields("offset", "line"));
		Scheme inputScheme = new RdfTripleScheme();
		Scheme outputScheme = new TextLine();

		//hdfs://localhost:9000/user/valer/input
			
		/*
		Tap sourceTap = inputPath.matches("^[^:]+://.*") ? new Hfs(inputScheme,
				inputPath) : new Lfs(inputScheme, inputPath);
		*/
		
		Tap sourceTap = new Hfs(inputScheme, inputPath);

		/*
		Tap sinkTap = inputPath.matches("^[^:]+://.*") ? new Hfs(outputScheme,
				outputPath) : new Lfs(outputScheme, outputPath);
		*/
		
		Tap sinkTap = new Hfs(outputScheme, outputPath);

		Pipe wcPipe = new Each("wordcount", new Fields("predicate"),
				new RegexSplitGenerator(new Fields("word"), "\\s+"));

		wcPipe = new GroupBy(wcPipe, new Fields("word"));
		wcPipe = new Every(wcPipe, new Count(), new Fields("count", "word"));

		Properties properties = new Properties();
		FlowConnector.setApplicationJarClass(properties,
				DistributedReasoner.class);

		Flow parsedLogFlow = new FlowConnector(properties).connect(sourceTap, sinkTap, wcPipe);
		parsedLogFlow.start();
		parsedLogFlow.complete();
	}

}
