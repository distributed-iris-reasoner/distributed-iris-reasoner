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

import org.deri.iris.api.basics.IPredicate;

import cascading.flow.FlowProcess;
import cascading.operation.BaseOperation;
import cascading.operation.Filter;
import cascading.operation.FilterCall;
import cascading.tuple.TupleEntry;
import eu.larkc.iris.storage.PredicateWritable;

/**
 * This filters by predicate value.
 * 
 * @history Dec 15, 2010, vroman, creation
 * @author Valer Roman
 */
@SuppressWarnings("rawtypes")
public class PredicateFilter extends BaseOperation implements Filter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4348939007588874129L;
	
	//private static final Logger logger = LoggerFactory.getLogger(CascadingRuleCompiler.class);
	
	/**
	 * @param expectedConstants Constants in a subgoal.
	 */
	public PredicateFilter(IPredicate predicate) {
		//this.mPredicateField = predicateField;
		this.mPredicate = predicate;
	}
	
	/**
	 * Removes all the tuples that do not match with the specified constants
	 */
	@Override
	public boolean isRemove(FlowProcess flowProcess, FilterCall filterCall) {
		// get the arguments TupleEntry
		TupleEntry arguments = filterCall.getArguments();

		// filter out the current Tuple if values do not match specified constants
		boolean remove = true;
		
		Comparable comparable = arguments.get(0);
		if (comparable.equals(new PredicateWritable(mPredicate))) {
			remove = false;
		}
		
		return remove;
	}
	
	//private String mPredicateField;
	private IPredicate mPredicate;
}
