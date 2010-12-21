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
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.storage.IIndex;
import org.deri.iris.storage.IRelation;
import org.deri.iris.utils.equivalence.IEquivalentTerms;

/**
 * A compiled rule element representing a negated ordinary literal.
 */
public class Differ extends RuleElement
{
	
	/**
	 * Constructor.
	 * @param inputVariables The variable bindings from previous rule elements.
	 * @param thisLiteralsRelation The relation to use for this literal.
	 * @param viewCriteria The view criteria (tuple in the sub-goal instance in the rule).
	 * @param equivalentTerms The equivalent terms.
	 */
	public Differ( List<IVariable> inputVariables, IRelation thisLiteralsRelation, ITuple viewCriteria, IEquivalentTerms equivalentTerms, Configuration configuration )
	{
		// If there are no input variables then this is the first sub-goal.
		assert thisLiteralsRelation != null;
		assert viewCriteria != null;
		assert configuration != null;
		
		mEquivalentTerms = equivalentTerms;
		
		mConfiguration = configuration;
		
		mView = new View( thisLiteralsRelation, viewCriteria, equivalentTerms, mConfiguration.relationFactory );
		
		// Find the indices of the variables used in the natural join
		List<Integer> join1 = new ArrayList<Integer>();
		List<Integer> join2 = new ArrayList<Integer>();
		
		List<IVariable> vars2 = mView.variables();
		
		if( inputVariables != null )
		{
			for( int i1 = 0; i1 < inputVariables.size(); ++i1 )
			{
				IVariable var1 = inputVariables.get( i1 );
				
				for( int i2 = 0; i2 < vars2.size(); ++i2 )
				{
					IVariable var2 = vars2.get( i2 );
					
					if( var1.equals( var2 ) )
					{
						join1.add( i1 );
						join2.add( i2 );
						
						// NB Variables in views occur only once
						break;
					}
				}
			}
		}
		
		mJoinIndices1 = Utils.integerListToArray( join1 );
		mJoinIndices2 = Utils.integerListToArray( join2 );

		// Create the index for the second relation
		mIndex2 = mConfiguration.indexFactory.createIndex( mView, mEquivalentTerms, mJoinIndices2 );
		
		mOutputVariables = inputVariables == null ? new ArrayList<IVariable>() : inputVariables;
	}

	@Override
    public IRelation process( IRelation leftRelation )
    {
		assert leftRelation != null;

		// TODO Create special performance test for this and measure how useful this short-cut might be,
//		// Special case. First sub-goal with variables, so can do a short-cut
//		if( previous.size() == 1 && previous.get( 0 ).size() == 0 )
//		{
//			// In this case (where the negated literal is first) the negated literal
//			// must be grounded, so there is nothing to index,
//			// the view either contains the expected tuple or not.
//			if( mView.getView().size() == 0 )
//			{
//				// No it doesn't, so the negated sub-goal is true.
//				return previous;
//			}
//			else
//				return mConfiguration.relationFactory.createRelation();
//		}
		
		IRelation result = mConfiguration.relationFactory.createRelation();
		for( int left = 0; left < leftRelation.size(); ++left )
		{
			ITuple leftTuple = leftRelation.get( left );
			
			List<ITuple> matchingRightTuples = mIndex2.get( Utils.makeKey( leftTuple, mJoinIndices1 ) );

			// Only add those tuples, which do not match with the left tuple.
			if( matchingRightTuples.size() == 0 ) {
				result.add(leftTuple);
			}
		}
		
		return result;
	}
	
	/** The equivalent terms. */
	private IEquivalentTerms mEquivalentTerms;
	
	/** The view on the relation (as seen with the view criteria). */
	private final View mView;

	/** The join indices from the previous rule element's output tuple. */
	private final int[] mJoinIndices1;

	/** The join indices from this rule element's view of its relation. */
	private final int[] mJoinIndices2;
	
	/** An index on the view of this literal's relation. */
	private final IIndex mIndex2;
	
	private final Configuration mConfiguration;
}
