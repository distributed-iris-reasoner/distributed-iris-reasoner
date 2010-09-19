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
package org.deri.iris.rules.ordering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.rules.IRuleReOrderingOptimiser;
import org.deri.iris.rules.RuleHeadEquality;

/**
 * Very naive proof of concept, but speeds up a few unit tests by a factor of 10.
 * Attempt to re-order rules by simply looking for the first positive ordinary
 * literal in each rule's body.
 */
public class SimpleReOrdering implements IRuleReOrderingOptimiser
{
	public List<IRule> reOrder( final Collection<IRule> rules )
    {
		tempRules = new HashSet<IRule>( rules );
		int inputRuleCount = tempRules.size();
		
		/*
		IPredicateGraph graph = GraphFactory.getInstance().createPredicateGraph( rules );
		
		graph. can't work this out at the moment.
		*/
		
		predRuleMap = new MultiMap<IPredicate, IRule>();
		predPredMap = new MultiMap<IPredicate, IPredicate>();
		
		for( IRule rule : rules )
		{
			// Make the predicate-rule multimap.
			predRuleMap.add( head( rule ), rule );
			
			// Make the predicate-predicate multimap.
			IPredicate h = head( rule );
			IPredicate b = body( rule );
			
			// Avoid immediate cycles
			if( ! h.equals( b ) )
				predPredMap.add( head( rule ), body( rule ) );
		}
		
		result = new ArrayList<IRule>();

		IPredicate predicate;
		while( (predicate = predPredMap.first()) != null )
			attempt( predicate );

		// Any left overs?
		for( IRule r : tempRules )
			result.add( r );

		// Reverse the order
		for( int i = 0; i < result.size() / 2; ++i )
		{
			int hi = (result.size() - 1) - i;
			IRule temp = result.get( i );
			
			result.set( i, result.get( hi ) );
			result.set( hi, temp );
		}
		
		List<IRule> temp = new LinkedList<IRule>();
		
		// For efficiency, move all rules with head equality to head of list.
		for (IRule rule : result) {
			if (RuleHeadEquality.hasRuleHeadEquality(rule)) {
				temp.add(0, rule);
			} else {
				temp.add(rule);
			}
		}
		
		result.clear();
		result.addAll(temp);
		
		assert result.size() == inputRuleCount: "Some rules lost while reordering";
	    return result;
    }
	
	void attempt( IPredicate predicate )
	{
		List<IRule> predRules = predRuleMap.remove( predicate );

		for( IRule r : predRules )
		{
			if( tempRules.remove( r ) )
				result.add( r );
		}
		
		List<IPredicate> depPreds = predPredMap.remove( predicate );
		
		for( IPredicate depPred : depPreds )
		{
			attempt( depPred );
		}
	}
	
	static class MultiMap<K,V>
	{
		void add( K key, V value )
		{
			List<V> list = get( key );
			list.add( value );
		}
		
		K first()
		{
			Iterator<Map.Entry<K,List<V>>> it = map.entrySet().iterator();

			if( it.hasNext() )
			{
				return it.next().getKey();
			}
			return null;
		}
		
		List<V> remove( K key )
		{
			List<V> result = map.remove( key );
			
			if( result == null )
				return new ArrayList<V>();
			else
				return result;
		}
		
		void clear()
		{
			map.clear();
		}
		
		List<V> get( K key )
		{
			List<V> list = map.get( key );
			
			if( list == null )
			{
				list = new ArrayList<V>();
				map.put( key, list );
			}
			return list;
		}
		
		final Map<K, List<V>> map = new HashMap<K, List<V>>();
	}
	
	static class Node
	{
		IPredicate predicate;
		List<IPredicate> children = new ArrayList<IPredicate>();
	}

	private IPredicate head( IRule rule )
	{
		return rule.getHead().get( 0 ).getAtom().getPredicate();
	}
	
	private IPredicate body( IRule rule )
	{
		for( ILiteral literal : rule.getBody() )
		{
			if( literal.isPositive() )
			{
				IAtom atom = literal.getAtom();
				if( ! atom.isBuiltin() )
				{
					return atom.getPredicate();
				}
			}
		}
		
		return null;
	}
	
	Set<IRule> tempRules;
	MultiMap<IPredicate, IRule> predRuleMap;
	MultiMap<IPredicate, IPredicate> predPredMap;
	List<IRule> result;
}
