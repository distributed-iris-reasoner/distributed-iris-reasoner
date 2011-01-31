/**
 * 
 */
package eu.larkc.iris.imports;

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
public class TextImporterFunction extends BaseOperation implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7477883423115109241L;

	@Override
	public void operate(FlowProcess flowProcess, FunctionCall functionCall) {
		Tuple tuple = functionCall.getArguments().getTuple();
		Tuple outTuple = new Tuple();
		for (int i = 0; i < tuple.size(); i++) {
			String value = tuple.getString(i);
			if ((value.startsWith("<") && value.endsWith(">") || value.startsWith("_:node"))) {
				IRIWritable iriWritable = new IRIWritable();
				iriWritable.setValue(value.substring(1, value.length() - 1));
				outTuple.add(iriWritable);
			} else {
				StringTermWritable stWritable = new StringTermWritable();
				stWritable.setValue(value);
				outTuple.add(stWritable);
			}
		}
		functionCall.getOutputCollector().add(outTuple);
	}

}
