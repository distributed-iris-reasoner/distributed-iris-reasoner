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

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.pipe.Pipe;
import cascading.tap.Hfs;
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
	
	//private static final String RESULT_TAIL = "resultTail";
	
	private Configuration mConfiguration;
	
	private Tap source;
	private Fields sinkFields;
	private Pipe pipe;
	
	private Flow flow = null;
	private String path = null;
	
	public FlowAssembly (Configuration configuration, Tap source, Fields sinkFields, Pipe pipe) {
		this.mConfiguration = configuration;
		this.source = source;
		this.sinkFields = sinkFields;
		this.pipe = pipe;
	}
	
	private Flow createFlow(String flowName, String output) {
		//Map<String, Tap> sinks = new HashMap<String, Tap>();
		Tap headSink = new Hfs(sinkFields, output, true );
		//sinks.put(RESULT_TAIL, headSink);
		
		Flow flow = new FlowConnector(mConfiguration.flowProperties).connect(flowName, source, headSink, pipe);
		
		if(flow != null) {
			flow.writeDOT("flow.dot");
		}
		
		return flow;
	}
	
	public boolean evaluate(EvaluationContext evaluationContext) {
		boolean hasNewInferences = false;
		
		String flowIdentificator = "_" + evaluationContext.getIterationNumber() + "_" + evaluationContext.getRuleNumber();
		String flowName = "flow" + flowIdentificator;
		String resultName = mConfiguration.keepResults ? mConfiguration.resultsName : "inference";
		String output = mConfiguration.project + "/tmp/inferences/" + resultName + flowIdentificator;
		
		flow = createFlow(flowName, output);
		flow.complete();
		
		path = mConfiguration.project + "/data/inferences/tmp/" + mConfiguration.resultsName + flowIdentificator;
		try {
			TupleEntryIterator iterator = flow.openSink();
			Hfs hfs = new Hfs(Fields.ALL, path);
			TupleEntryCollector tec = hfs.openForWrite(mConfiguration.jobConf);
			while(iterator.hasNext()) {
				if (!hasNewInferences) {
					hasNewInferences = true;
				}
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
		return hasNewInferences;
	}
	
	public TupleEntryIterator openSink() throws IOException {
		if (flow == null) {
			return null;
		}
		Hfs hfs = new Hfs(Fields.ALL, path);
		return hfs.openForRead(mConfiguration.jobConf);
	}
}
