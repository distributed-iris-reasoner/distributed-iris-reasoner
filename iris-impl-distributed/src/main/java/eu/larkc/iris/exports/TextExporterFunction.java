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
package eu.larkc.iris.exports;

import cascading.flow.FlowProcess;
import cascading.operation.BaseOperation;
import cascading.operation.Function;
import cascading.operation.FunctionCall;
import cascading.tuple.Tuple;
import eu.larkc.iris.storage.IRIWritable;
import eu.larkc.iris.storage.StringTermWritable;

/**
 * User defined cascading function used to export data to n-triple format, text based files
 * 
 * @author valer.roman@softgress.com
 *
 */
@SuppressWarnings({ "unchecked" })
public class TextExporterFunction extends BaseOperation implements Function {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7343658068858763394L;

	@Override
	public void operate(FlowProcess flowProcess, FunctionCall functionCall) {
		Tuple tuple = functionCall.getArguments().getTuple();
		Tuple outTuple = new Tuple();
		for (int i = 0; i < tuple.size(); i++) {
			Object value = tuple.getObject(i);
			if (value instanceof IRIWritable) {
				IRIWritable iri = (IRIWritable) value;
				outTuple.add("<" + iri.getValue() + ">");
			} else if (value instanceof StringTermWritable) {
				StringTermWritable stringTerm = (StringTermWritable) value;
				outTuple.add(stringTerm.getValue());
			}
		}
		functionCall.getOutputCollector().add(outTuple);
	}

}
