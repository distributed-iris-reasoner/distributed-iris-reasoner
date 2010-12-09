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

import java.io.IOException;

import org.apache.commons.lang.NotImplementedException;
import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.facts.IFacts;
import org.deri.iris.storage.IRelation;

import cascading.flow.Flow;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntryIterator;

/**
 * CascadingCompiledRule encapsulates a rule that has been translated from the IRIS internal representation to a suitable cascading workflow.
 * This class also contains the basic logic that triggers a map-reduce computation, which is then simply called by an arbitrary IRuleEvaluator implementation
 * via the evaluate() method.
 * 
 * @history Oct 3, 2010, fisf, creation
 * @author Florian Fischer
 */
public class CascadingCompiledRule implements IDistributedCompiledRule {

	
	public CascadingCompiledRule(IPredicate headPredicate, FlowAssembly flowAssembly, eu.larkc.iris.Configuration configuration){
		this.mHeadPredicate = headPredicate;
		this.mFlowAssembly = flowAssembly;
		this.mConfiguration = configuration;
	}

	/*
	 * (non-Javadoc)
	 * @see eu.larkc.iris.rules.compiler.IDistributedCompiledRule#evaluate()
	 */
	@Override
	public boolean evaluate() throws EvaluationException {
		
		//start returns immediately		
		if(mFlowAssembly == null) {
			throw new IllegalArgumentException("Flow assembly must not be null");
		}
		String flowName = "flow" + System.currentTimeMillis();
		Flow flow = mFlowAssembly.createFlow(flowName);
		flow.complete();
		
		//TODO: jobconf is constructed within the rule compiler right now, which is likely not the right place.
		//this should either happen here or in a custom evaluator implementation		
		
		//FIXME (fisf): check for recursion / cycles.
		//Overall story:
		//Naive evaluation does several passes over the rules and finishes when no new tuples are added anymore.
		//Several passes are actually only needed if the dependency graph contains cycles.
		//The return here is needed to establish the (possible) delta between iterations.
		//Naive evaluation will terminate when evaluate returns null.
		//Until recursion is supported this code will work fine, then a more complex solution is needed.
		
		return hasNewInferences(flow);
	}

	/*
	 * Check if new inferences have been generated with the last evaluation
	 */
	private boolean hasNewInferences(Flow flow) throws EvaluationException {
		boolean hasNewInferences = false;
		try {
			TupleEntryIterator iterator = flow.openSink(mConfiguration.DELTA_TAIL_NAME);
			while (iterator.hasNext()) {
				Tuple tuple = iterator.next().getTuple();
				String delta = tuple.getString(0);
				if (Integer.valueOf(delta) > 0) {
					hasNewInferences = true;
				}
			}
		} catch (IOException e) {
			throw new EvaluationException("unable to open delta tail");
		}
		return hasNewInferences;
	}
	
	/*
	 * (non-Javadoc)
	 * @see eu.larkc.iris.rules.compiler.IDistributedCompiledRule#evaluateIteratively(org.deri.iris.facts.IFacts)
	 */
	@Override
	public IRelation evaluateIteratively(IFacts deltas)
			throws EvaluationException {
		// TODO (fisf) implement later
		throw new NotImplementedException("Semi-naive evaluation is not implemented yet.");
	}

	/* (non-Javadoc)
	 * @see org.deri.iris.rules.compiler.ICompiledRule#headPredicate()
	 */
	@Override
	public IPredicate headPredicate() {
		return mHeadPredicate;
	}
	
	
	private final eu.larkc.iris.Configuration mConfiguration;
	
	/**
	 * Describes the predicate to which the original rules output belongs.
	 */
	private final IPredicate mHeadPredicate;
	
	/**
	 * The internal representation as a cascading flow of this rule.
	 */
	private final FlowAssembly mFlowAssembly;

}