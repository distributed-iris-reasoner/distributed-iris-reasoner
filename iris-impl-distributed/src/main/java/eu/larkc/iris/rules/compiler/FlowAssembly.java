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
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.pipe.Pipe;
import cascading.scheme.Scheme;
import cascading.scheme.SequenceFile;
import cascading.tap.Hfs;
import cascading.tap.MultiSourceTap;
import cascading.tap.Tap;
import cascading.tuple.Fields;
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
	
	private Fields fields;
	private Pipe pipe;
	
	private Flow flow = null;
	private String path = null;
	
	public FlowAssembly (Configuration configuration, Fields fields, Pipe pipe) {
		this.mConfiguration = configuration;
		this.fields = fields;
		this.pipe = pipe;
	}
	
	private Flow createFlow(String flowName, String output) {
		Tap source = prepareSourceTaps(fields);
		
		//Map<String, Tap> sinks = new HashMap<String, Tap>();
		SequenceFile sinkScheme = new SequenceFile(fields);
		sinkScheme.setNumSinkParts(1);
		Tap headSink = new Hfs(sinkScheme, output, true );
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
		String resultName = mConfiguration.resultsName != null ? mConfiguration.resultsName : "inference";
		String flowName = resultName + flowIdentificator;
		path = mConfiguration.project + "/inferences/" + resultName + "/" + resultName + flowIdentificator;
		
		flow = createFlow(flowName, path);
		flow.complete();
		
		try {
			TupleEntryIterator iterator = flow.openSink();
			if(iterator.hasNext()) {
				hasNewInferences = true;
			}
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
		return flow.openSink();
		//Hfs hfs = new Hfs(Fields.ALL, path);
		//return hfs.openForRead(mConfiguration.jobConf);
	}
	
	private MultiSourceTap prepareSourceTaps(Fields fields) {
		SequenceFile sourceScheme = new SequenceFile(fields);
		List<Tap> sources = new ArrayList<Tap>();
		Tap factsTap = new Hfs(sourceScheme, mConfiguration.project + "/facts/");
		Tap inferencesTap = getInferencesTap(sourceScheme);
		sources.add(factsTap);
		if (inferencesTap != null) {
			sources.add(inferencesTap);
		}
		return new MultiSourceTap(sources.toArray(new Tap[0])); //we can assume that the number of fields are the same as the head;s tuple size + 1 (the predicate)		
	}
	
	private Tap getInferencesTap(Scheme scheme) {
		try {
			String path = mConfiguration.project + "/inferences/";
			FileSystem fs = FileSystem.get(mConfiguration.hadoopConfiguration);
			if (!fs.exists(new Path(path))) {
				return null;
			}
			return new Hfs(scheme, path);
		} catch (IOException e) {
			logger.error("io exception", e);
			throw new RuntimeException("io exception", e);
		}		
	}

}
