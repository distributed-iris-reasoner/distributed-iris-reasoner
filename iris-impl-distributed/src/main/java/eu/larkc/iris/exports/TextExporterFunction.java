/**
 * 
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
 * @author valer
 *
 */
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
