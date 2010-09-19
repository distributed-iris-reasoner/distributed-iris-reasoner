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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.rules.IRuleStratifier;
import org.deri.iris.rules.RuleHeadEquality;

/**
 * The global stratification algorithm.
 * This algorithm does not modify/create/delete any rules.
 * @see org.deri.iris.rules.IRuleStratifier#stratify()
 */
public class GlobalStratifier implements IRuleStratifier
{
	public List<List<IRule>> stratify( List<IRule> rules )
	{
		final int ruleCount = rules.size();
		int highest = 0;
		boolean change = true;

		// Clear the strata map, i.e. set all strata to 0
		mStrata.clear();
		
		while ((highest <= ruleCount ) && change)
		{
			change = false;
			for (final IRule r : rules)
			{
				for (final ILiteral hl : r.getHead())
				{
					final IPredicate hp = hl.getAtom().getPredicate();

					if( r.getBody().size() == 0 )
					{
						// Has the effect of setting to stratum to zero if this predicate
						// has not been seen before
						getStratum( hp );
					}
					else
					{
						for (final ILiteral bl : r.getBody())
						{
							final IPredicate bp = bl.getAtom().getPredicate();
	
							if (bl.isPositive())
							{
								int greater = Math.max(getStratum(hp), 
												getStratum(bp));
								if (getStratum(hp) < greater)
								{
									setStratum(hp, greater);
									change = true;
								}
								highest = Math.max(highest, greater);
							}
							else
							{
								int current = getStratum(bp);
								if (current >= getStratum(hp))
								{
									setStratum(hp, current + 1);
									highest = Math.max(highest, current + 1);
									change = true;
								}
							}
						}
					}
				}
			}
		}
		
		if( highest <= ruleCount )
		{
			List<List<IRule>> result = new ArrayList<List<IRule>>();
			
			for( int stratum = 0; stratum <= highest; ++stratum )
				result.add( new ArrayList<IRule>() );

			for( Map.Entry<IPredicate, Integer> entry : mStrata.entrySet() )
			{
				// Identify the stratum and predicate
				int stratum = entry.getValue();
				IPredicate predicate = entry.getKey();
				
				// Now search for all rules that have this predicate as the head
				for( IRule rule : rules )
				{
					if (!checkRuleHeadEquality(rule, stratum)) {
						return null;
					}
					
					if( rule.getHead().get( 0 ).getAtom().getPredicate().equals( predicate ) )
					{
						result.get( stratum ).add( rule );
					}
				}
			}
			
			return result;
		}
		else
			return null;
	}
	
	/**
	 * Get the stratum for a particular (rule head) predicate.
	 * @param predicate The rule-head predicate.
	 * @return The stratum level.
	 */
	private int getStratum( final IPredicate predicate )
	{
		assert predicate!= null;
		
		Integer stratum = mStrata.get( predicate );
		
		if( stratum == null )
		{
			stratum = 0;
			mStrata.put( predicate, stratum );
		}
		
		return stratum;
	}
	
	/**
	 * Set the stratum for a (rule-head) predicate.
	 * @param predicate predicate
	 * @param stratum stratum level
	 */
	private void setStratum(final IPredicate predicate, final int stratum)
	{
		assert predicate != null;
		assert stratum >= 0 : "The stratum must not be negative, but was: " + stratum;
		
		mStrata.put(predicate, Integer.valueOf(stratum));
	}
	
	
	/**
	 * Checks if a rule with head equality is stratified. 
	 * A program is stratified if and only if rules with head equality are
	 * in the bottom stratum and they don't have negated predicates.
	 * 
	 * @param rule The rule to check if stratified.
	 * @param stratum The stratum of the rule.
	 * @return <code>true</code> if the rule is stratified, <code>false</code> otherwise.
	 */
	public static boolean checkRuleHeadEquality(IRule rule, int stratum) {
		if (RuleHeadEquality.hasRuleHeadEquality(rule)) {
			if (stratum != 0) {
				return false;
			}
			
			for (ILiteral literal : rule.getBody()) {
				if (!literal.isPositive()) {
					return false;
				}
			}
		}
		
		return true;
	}

	/** Map for the strata of the different predicates. */
	private final Map<IPredicate, Integer> mStrata = new HashMap<IPredicate, Integer>();
}
