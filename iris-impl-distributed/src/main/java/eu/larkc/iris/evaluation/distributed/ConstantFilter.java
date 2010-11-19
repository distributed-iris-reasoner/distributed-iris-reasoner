/**
 * 
 */
package eu.larkc.iris.evaluation.distributed;

import java.util.Map;
import java.util.Set;

import cascading.flow.FlowProcess;
import cascading.operation.BaseOperation;
import cascading.operation.Filter;
import cascading.operation.FilterCall;
import cascading.tuple.TupleEntry;

/**
 * This filters according to constants in rules and is set-up during
 * rule-compilation dynamically.
 * 
 * @history Nov 1, 2010, fisf, creation
 * @author Florian Fischer
 */
@SuppressWarnings("rawtypes")
public class ConstantFilter extends BaseOperation implements Filter {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -2355263716242780692L;

	/**
	 * @param expectedConstants Constants in a subgoal.
	 */
	public ConstantFilter(Map<Integer, Object> expectedConstants) {
		this.mExpectedconstants = expectedConstants;
	}
	
	/**
	 * Removes all the tuples that do not match with the specified constants
	 */
	@Override
	public boolean isRemove(FlowProcess flowProcess, FilterCall filterCall) {
		// get the arguments TupleEntry
		TupleEntry arguments = filterCall.getArguments();

		// filter out the current Tuple if values do not match specified constants
		boolean remove = false;
		
		if(mExpectedconstants.size() > arguments.size()) {
			throw new IllegalArgumentException("Filtering for more constants than actual terms in tuple! Number of constants: " 
					+ mExpectedconstants.size() + " Size of TupleEntry: " + arguments.size());
		}
			
		Set<Integer> positions = mExpectedconstants.keySet();	
		for (Integer pos : positions) {
			//we only need to find one value that does not match the constant
			if(!arguments.get(pos).equals(mExpectedconstants.get(pos))) {
				remove = true;
				break;
			}
		}
				
		return remove;
	}
	
	private Map<Integer, Object> mExpectedconstants;
}
