/**
 * 
 */
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

package eu.larkc.iris.rules.compiler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.deri.iris.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.pipe.Pipe;
import cascading.tap.Hfs;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.TupleEntry;
import cascading.tuple.TupleEntryCollector;
import cascading.tuple.TupleEntryIterator;
import eu.larkc.iris.Configuration;
import eu.larkc.iris.evaluation.EvaluationContext;

/**
 * @author valer.roman@softgress.com
 *
 */
public class FlowAssembly {

	private static final Logger logger = LoggerFactory.getLogger(FlowAssembly.class);
	
	private static final String RESULT_TAIL = "resultTail";
	
	private Configuration mConfiguration;
	
	private Tap source;
	private Map<String, Tap> sinks;
	private Pipe[] pipes;
	
	private Flow flow = null;
	
	public FlowAssembly (Configuration configuration, Tap source, Map<String, Tap> sinks, Pipe... pipes) {
		this.mConfiguration = configuration;
		this.source = source;
		this.sinks = sinks;
		this.pipes = pipes;
	}
	
	private Flow createFlow(String flowName, String output) {
		Map<String, Tap> sinks = new HashMap<String, Tap>();
		sinks.putAll(this.sinks);
		Tap headSink = new Hfs(Fields.ALL, output, true );
		sinks.put(RESULT_TAIL, headSink);
		
		Flow flow = new FlowConnector(mConfiguration.flowProperties).connect(flowName, source, sinks, pipes);
		
		if(flow != null) {
			flow.writeDOT("flow.dot");
		}
		
		return flow;
	}
	
	public void evaluate(EvaluationContext evaluationContext) {
		String flowIdentificator = "_" + evaluationContext.getIterationNumber() + "_" + evaluationContext.getRuleNumber();
		String flowName = "flow" + flowIdentificator;
		String resultName = mConfiguration.keepResults ? mConfiguration.resultsName : "inference";
		String output = mConfiguration.project + "/tmp/inferences/" + resultName + flowIdentificator;
		
		flow = createFlow(flowName, output);
		flow.complete();
		
		String outputPath = mConfiguration.project + "/data/inferences/tmp/" + mConfiguration.resultsName + flowIdentificator;
		try {
			TupleEntryIterator iterator = flow.openSink(RESULT_TAIL);
			Hfs hfs = new Hfs(Fields.ALL, outputPath);
			TupleEntryCollector tec = hfs.openForWrite(mConfiguration.jobConf);
			hasNewInferences = iterator.hasNext();
			while(iterator.hasNext()) {
				TupleEntry te = iterator.next();
				//logger.info("add : " + te.getTuple());
				tec.add(te);
			}
			tec.close();
			FileSystem fs = FileSystem.get(mConfiguration.hadoopConfiguration);
			fs.delete(new Path(output), true);
		} catch (IOException e) {
			logger.error("io exception", e);
			throw new RuntimeException("io exception", e);
		}
	}
	
	private boolean hasNewInferences = true;
	
	/*
	 * Check if new inferences have been generated with the last evaluation
	 */
	public boolean hasNewInferences() throws EvaluationException {
		return hasNewInferences;
	}

	public TupleEntryIterator openSink() throws IOException {
		if (flow == null) {
			return null;
		}
		return flow.openSink();
	}
}
