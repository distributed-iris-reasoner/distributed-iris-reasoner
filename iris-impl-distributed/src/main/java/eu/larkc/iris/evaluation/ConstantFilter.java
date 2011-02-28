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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.WritableComparable;

import cascading.flow.FlowProcess;
import cascading.operation.BaseOperation;
import cascading.operation.Filter;
import cascading.operation.FilterCall;
import cascading.tuple.TupleEntry;
import eu.larkc.iris.storage.IRIWritable;

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
	 * Filter by several fields
	 * 
	 * @param expectedConstants map of fields and their filtering values
	 */
	public ConstantFilter(Map<String, WritableComparable> expectedConstants) {
		this.mExpectedconstants = expectedConstants;
	}

	/**
	 * Filter the first position in the stream by this value
	 * 
	 * @param value an {@link IRIWritable} to use as filter 
	 */
	public ConstantFilter(IRIWritable value) {
		this(0, value);
	}
	
	/**
	 * Filter the field in {@code position} by this {@code value}
	 * 
	 * @param position the filtering position in the stream
	 * @param value an {@code IRIWritable} to use as filter
	 */
	public ConstantFilter(int position, IRIWritable value) {
		Set<eu.larkc.iris.storage.WritableComparable> set = new HashSet<eu.larkc.iris.storage.WritableComparable>();
		set.add(value);
		this.position = position;
		this.values = set;
	}

	/**
	 * Filter the field at {@code position} by the {@code values}. Use an OR operator for filter.
	 *  
	 * @param position the filtering position in the stream
	 * @param values a set of {@code eu.larkc.iris.storage.WritableComparable} used to filter. An OR operator is used
	 */
	public ConstantFilter(int position, Set<eu.larkc.iris.storage.WritableComparable> values) {
		this.position = position;
		this.values = values;
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
		
		if (!mExpectedconstants.isEmpty()) {
			if(mExpectedconstants.size() > arguments.size()) {
				throw new IllegalArgumentException("Filtering for more constants than actual terms in tuple! Number of constants: " 
						+ mExpectedconstants.size() + " Size of TupleEntry: " + arguments.size());
			}
			Set<String> fieldNames = mExpectedconstants.keySet();	
			for (String fieldName : fieldNames) {
				//we only need to find one value that does not match the constant
				Object compareTo = mExpectedconstants.get(fieldName);
				if(!arguments.get(fieldName).equals(compareTo)) {
					remove = true;
					break;
				}
			}
		} else if (values != null) {
			boolean found = false;
			for (eu.larkc.iris.storage.WritableComparable comparable : values) {
				if(arguments.get(position).equals(comparable)) {
					found =  true;
					break;
				}
			}
			remove = !found;
		}
		
		return remove;
	}
	
	private Map<String, WritableComparable> mExpectedconstants = new HashMap<String, WritableComparable>();
	
	private int position;
	private Set<eu.larkc.iris.storage.WritableComparable> values;
}
