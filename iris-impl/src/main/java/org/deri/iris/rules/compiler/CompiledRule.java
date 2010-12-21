/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.deri.iris.rules.compiler;

import java.util.ArrayList;
import java.util.List;

import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.factory.Factory;
import org.deri.iris.facts.IFacts;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.simple.SimpleRelationFactory;

/**
 * A compiled rule.
 */
public class CompiledRule implements ICompiledRule
{
	/**
	 * Constructor.
	 * @param elements The rule elements produced by the rule compiler.
	 * @param headPredicate The head predicate of the original rule.
	 */
	public CompiledRule( List<RuleElement> elements, IPredicate headPredicate, Configuration configuration )
	{
		assert elements.size() > 0;
		assert configuration != null;
		
		mConfiguration = configuration;
		
		mHeadPredicate = headPredicate;
		
		mElements = elements;
	}

	/**
	 * Evaluate the rule.
	 * Each element is called in turn to produce tuples to pass on to the next rule element.
	 * If any rule element outputs an empty relation, then stop.
	 * @throws EvaluationException 
	 */
	public IRelation evaluate() throws EvaluationException
	{
		// The first literal receives the starting relation (which has one zero length tuple in it). */
		IRelation output = mStartingRelation;
		
		for( RuleElement element : mElements )
		{
			output = element.process( output );
			
			// Must always get some output relation, even if it is empty.
			assert output != null;
			
			// All literals are conjunctive, so if any literal produces no results,
			// then the whole rule produces no results.
			if( output.size() == 0 )
				break;
		}
		
		return output;
	}
	
	public IRelation evaluateIteratively( IFacts deltas ) throws EvaluationException
	{
		IRelation union = mConfiguration.relationFactory.createRelation();
	
		/*
		for each literal (rule element) for which there exists a delta substitution
			substitute the rule element with the delta
			evaluate the whole rule
			store the results
		combine all the results and return
		*/
		for( int r = 0; r < mElements.size(); ++r )
		{
			RuleElement original = mElements.get( r );
			
			RuleElement substitution = original.getDeltaSubstitution( deltas );

			if( substitution != null )
			{
				mElements.set( r, substitution );
	
				// Now just evaluate the modified rule
				IRelation output = evaluate();
				
				for( int t = 0; t < output.size(); ++t )
					union.add( output.get( t ) );
				
				// Put the original rule element back the way it was
				mElements.set( r, original );
			}
		}
		
		return union;
	}
	
	public IPredicate headPredicate()
    {
	    return mHeadPredicate;
    }

	public List<IVariable> getVariablesBindings()
	{
		if( mElements.size() > 0 )
			return mElements.get( mElements.size() - 1 ).getOutputVariables();
		else
			return new ArrayList<IVariable>();
	}
	
	/** The starting relation for evaluating each sub-goal. */
	private static final IRelation mStartingRelation = new SimpleRelationFactory().createRelation();

	static
	{
		// Start the evaluation with a single, zero-length tuple.
		mStartingRelation.add( Factory.BASIC.createTuple() );
	}
	
	/** The rule elements in order. */
	private final List<RuleElement> mElements;
	
	/** The head predicate. */
	private final IPredicate mHeadPredicate;
	
	private final Configuration mConfiguration;
}
