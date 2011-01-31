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
package eu.larkc.iris.evaluation;

import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.WritableComparable;

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
	public ConstantFilter(Map<String, WritableComparable> expectedConstants) {
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
			
		Set<String> positions = mExpectedconstants.keySet();	
		for (String pos : positions) {
			//we only need to find one value that does not match the constant
			Object compareTo = mExpectedconstants.get(pos);
			if(!arguments.get(pos).equals(compareTo)) {
				remove = true;
				break;
			}
		}
				
		return remove;
	}
	
	private Map<String, WritableComparable> mExpectedconstants;
}
