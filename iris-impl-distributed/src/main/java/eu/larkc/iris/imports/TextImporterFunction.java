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
				String aValue = null;
				if (value.startsWith("_:node")) {
					aValue = "http://www.w3.org/2011/node#" + value.substring(2);
				} else {
					aValue = value.substring(1, value.length() - 1);
				}
				iriWritable.setValue(aValue);
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
