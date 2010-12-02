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

import java.util.Map;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.pipe.Pipe;
import cascading.tap.Tap;

/**
 * @author valer.roman@softgress.com
 *
 */
public class FlowAssembly {


	private Map<String, Tap> sources;
	private Map<String, Tap> sinks;
	private Pipe[] pipes;
	
	public FlowAssembly (Map<String, Tap> sources, Map<String, Tap> sinks, Pipe... pipes) {
		this.sources = sources;
		this.sinks = sinks;
		this.pipes = pipes;
	}
	
	public Flow createFlow(String flowName) {

		Flow flow = new FlowConnector().connect(flowName, sources, sinks, pipes);
		
		if(flow != null) {
			flow.writeDOT("flow.dot");
		}
		
		return flow;
	}
}
