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

import java.util.HashMap;
import java.util.Map;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.operation.Identity;
import cascading.operation.Insert;
import cascading.operation.aggregator.Count;
import cascading.pipe.Each;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import eu.larkc.iris.storage.FactsFactory;

/**
 * @author valer.roman@softgress.com
 *
 */
public class FlowAssembly {

	private static final String DELTA_PREDICATE_FIELD = "DELTA";
	private static final String FLOW_ID_SUBJECT_FIELD = "FLOW_ID";

	private Map<String, Tap> sources;
	private Tap resultSink;
	private Pipe resultPipe;
	
	public FlowAssembly (Map<String, Tap> sources, Tap resultSink, Pipe resultPipe) {
		this.sources = sources;
		this.resultSink = resultSink;
		this.resultPipe = resultPipe;
	}
	
	public Flow createFlow(String flowName) {
		Pipe countPipe = new Pipe("countTail", resultPipe);
		countPipe = new GroupBy(countPipe);
		countPipe = new Every(countPipe, new Count(new Fields("count")));
		countPipe = new Each( countPipe, new Insert( new Fields(DELTA_PREDICATE_FIELD), "http://eu.larkc/delta"), Fields.ALL );
		countPipe = new Each( countPipe, new Insert( new Fields(FLOW_ID_SUBJECT_FIELD), flowName), Fields.ALL );
		countPipe = new Each( countPipe, new Fields(DELTA_PREDICATE_FIELD, FLOW_ID_SUBJECT_FIELD, "count"), new Identity());
		Tap countSink = FactsFactory.getInstance("delta").getFacts();
		
		Map<String, Tap> sinks = new HashMap<String, Tap>();
		sinks.put(resultPipe.getName(), resultSink);
		sinks.put(countPipe.getName(), countSink);

		Flow flow = new FlowConnector().connect(flowName, sources, sinks, resultPipe, countPipe);
		
		if(flow != null) {
			flow.writeDOT("flow.dot");
		}
		
		return flow;
	}
}
