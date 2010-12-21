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
package org.deri.iris.rules.stratification;

import java.util.ArrayList;
import java.util.List;

import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;

/**
 * A rule adaptor that decorates a rule with adornments for local stratification.
 */
public class LocalStratificationDecorator
{
	/**
	 * Indication of how well a term (or rule head) matches another term (or rule head).
	 */
	public enum MatchType
	{
		NONE,
		EXACT,
		CONSUMES_SUBSET,
		CONSUMES_ALL
	}

	/**
	 * An immutable adornment for a single term of a rule head.
	 */
	public static class Adornment
	{
		/**
		 * Find how well a term (from a dependant rule's negated sub-goal)
		 * matches the term decorated with this adornment.
		 * @param termFromNegatedSubgoal The 'interesting' negated sub goal from some rule
		 * @return An indication of how well this term can fulfil the corresponding term
		 * in the negated sub-goal
		 */
		public MatchType match( ITerm termFromNegatedSubgoal )
		{
			if( termFromNegatedSubgoal.isGround() )
			{
				if( mPositiveConstant != null )
				{
					// 'this' is a constant so check if they are the same 
					if( termFromNegatedSubgoal.equals( mPositiveConstant ) )
						return MatchType.EXACT;
					else
						return MatchType.NONE;
				}
				
				// If 'this' is a list of what the rule can not produce for the term
				for( ITerm negatedTerm : mNegatedConstants )
				{
					if( termFromNegatedSubgoal.equals( negatedTerm ) )
						return MatchType.NONE;
				}

				// 'this' must be a variable
				return MatchType.CONSUMES_SUBSET;
			}
			else
			{
				// termFromNegatedSubgoal is a variable, so we can't say anything about it
				return MatchType.CONSUMES_ALL;
			}
		}

		/**
		 * Indicate that the rule will never produce
		 * 'constantTerm' values in the term decorated with this adornment.
		 * @param constantTerm The constant term.
		 * @return The new adornment
		 */
		public Adornment addNegatedConstant( ITerm constantTerm )
		{
			assert !(constantTerm instanceof IVariable);
			
			Adornment result = new Adornment();
			
			result.mNegatedConstants.addAll( mNegatedConstants );
			result.mNegatedConstants.add( constantTerm );
			
			return result;
		}
		
		/**
		 * Indicate that the rule will ONLY produce 'constantTerm' values
		 * in the term decorated with this adornment.
		 * @param constantTerm The constant term.
		 * @return The new adornment
		 */
		public Adornment setConstantTerm( ITerm constantTerm )
		{
			assert !(constantTerm instanceof IVariable);
			
			Adornment result = new Adornment();

			result.mPositiveConstant = constantTerm;
			
			return result;
		}
		
		@Override
	    public String toString()
	    {
		    if( mPositiveConstant == null && mNegatedConstants.size() == 0 )
		    	return "?";
		    
		    if( mPositiveConstant != null )
		    	return mPositiveConstant.toString();
		    
		    StringBuilder buffer = new StringBuilder();
		    
		    for( ITerm constant : mNegatedConstants )
		    	buffer.append( '!' ).append( constant.toString() );
		    
		    return buffer.toString();
	    }
		
		/**
		 * Get the negated constants (if any) that this adornment represents.
		 * @return The list of negated constants.
		 */
		public List<ITerm> getNegatedConstants()
		{
			return mNegatedConstants;
		}
		
		/**
		 * Get the positive constant (if any) that this adornment represents.
		 * @return The positive adornment or null if there isn't one.
		 */
		public ITerm getPositiveConstant()
		{
			return mPositiveConstant;
		}

		/** The list of negated constant terms (the values will never occur here). */
		private final List<ITerm> mNegatedConstants = new ArrayList<ITerm>();
		
		/** The constant term that will always occur here. */
		private ITerm mPositiveConstant;
	}

	/**
	 * Constructor.
	 * For use when the adornment values are already calculated.
	 * @param rule The rule to decorate.
	 * @param adornments The list of adornments.
	 */
	public LocalStratificationDecorator( IRule rule, List<Adornment> adornments )
	{
		assert rule.getHead().get( 0 ).getAtom().getTuple().size() == adornments.size();
		
		mRule = rule;
		mAdornments = adornments;
	}

	/**
	 * Indicate how well the given tuple (of constants and variables) matches the
	 * adornments for this rule.
	 * @param tuple The tuple to compare with.
	 * @return The match type.
	 */
	public MatchType match( ITuple tuple )
	{
		assert mAdornments.size() == tuple.size();
		
		boolean partialmatch = false;
		
		for( int t = 0; t < mAdornments.size(); ++t )
		{
			Adornment adornment = mAdornments.get( t );
			ITerm term = tuple.get( t );
			
			MatchType matchType = adornment.match( term );
			
			if( matchType == MatchType.NONE )
			{
				return MatchType.NONE;
			}
			else if( matchType == MatchType.EXACT )
			{
			}
			else if( matchType == MatchType.CONSUMES_SUBSET )
			{
				partialmatch = true;
			}
			else if( matchType == MatchType.CONSUMES_ALL )
			{
			}
		}
		
		return partialmatch ? MatchType.CONSUMES_SUBSET : MatchType.CONSUMES_ALL;
	}

	/**
	 * Get the decorated rule.
	 * @return The rule.
	 */
	public IRule getRule()
	{
		return mRule;
	}
	
	/**
	 * Get the adornments for this rule.
	 * @return The list of adornments.
	 */
	public List<Adornment> getAdornments()
	{
		return mAdornments;
	}
	
	@Override
    public String toString()
    {
	    return mAdornments.toString() + ": " + mRule.toString();
    }

	/** All the adornments for this rule. */
	private final List<Adornment> mAdornments;
	
	/** The rule. */
	private final IRule mRule;
}
