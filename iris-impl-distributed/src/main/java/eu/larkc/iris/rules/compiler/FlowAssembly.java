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

import org.deri.iris.EvaluationException;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.pipe.Pipe;
import cascading.tap.Hfs;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.TupleEntryIterator;
import eu.larkc.iris.Configuration;

/**
 * @author valer.roman@softgress.com
 *
 */
public class FlowAssembly {

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
	
	private Flow createFlow(String flowName) {

		Map<String, Tap> sinks = new HashMap<String, Tap>();
		sinks.putAll(this.sinks);
		String output = mConfiguration.project + "/results/result" + System.currentTimeMillis();
		Tap headSink = new Hfs(Fields.ALL, output, true );
		sinks.put("resultTail", headSink);
		
		Flow flow = new FlowConnector(mConfiguration.flowProperties).connect(flowName, source, sinks, pipes);
		
		if(flow != null) {
			flow.writeDOT("flow.dot");
		}
		
		return flow;
	}
	
	public void evaluate() {
		String flowName = "flow" + System.currentTimeMillis();
		flow = createFlow(flowName);
		flow.complete();
	}
	
	/*
	 * Check if new inferences have been generated with the last evaluation
	 */
	public boolean hasNewInferences() throws EvaluationException {
		try {
			TupleEntryIterator iterator = flow.openSink("resultTail");
			while (iterator.hasNext()) {
				return true;
			}
		} catch (IOException e) {
			throw new EvaluationException("unable to open result tail");
		}
		return false;
	}

	public TupleEntryIterator openSink() throws IOException {
		if (flow == null) {
			return null;
		}
		return flow.openSink();
	}
}
