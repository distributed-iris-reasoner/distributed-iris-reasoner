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
package org.deri.iris.facts;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.IRelationFactory;

/**
 * A manager for all facts stored in a knowledge-base.
 */
public class Facts implements IFacts
{
	/**
	 * Constructor.
	 */
	public Facts( IRelationFactory relationFactory )
	{
		mRelationFactory = relationFactory;
	}
	
	/**
	 * Construct a Facts object from a predicate-relation map. 
	 * @param rawFacts The facts to add.
	 */
	public Facts( Map<IPredicate,IRelation> rawFacts, IRelationFactory relationFactory )
	{
		mRelationFactory = relationFactory;
		mPredicateRelationMap.putAll( rawFacts );
	}
	
	/* (non-Javadoc)
     * @see org.deri.iris.new_stuff.facts.IFacts#get(org.deri.iris.api.basics.IPredicate)
     */
	public IRelation get( IPredicate predicate )
	{
		IRelation relation = mPredicateRelationMap.get( predicate );
		
		if( relation == null )
		{
			relation = mRelationFactory.createRelation();
			mPredicateRelationMap.put( predicate, relation );
		}
		
		return relation;
	}
	
	/* (non-Javadoc)
     * @see org.deri.iris.new_stuff.facts.IFacts#getPredicates()
     */
	public Set<IPredicate> getPredicates()
	{
		return mPredicateRelationMap.keySet();
	}
	
	@Override
    public String toString()
    {
		StringBuilder result = new StringBuilder();
		
		for( Map.Entry<IPredicate, IRelation> entry : mPredicateRelationMap.entrySet() )
		{
			IRelation relation = entry.getValue();
			IPredicate predicate = entry.getKey();
			
			for( int t = 0; t < relation.size(); ++t )
			{
				ITuple tuple = relation.get( t );
				result.append( predicate.getPredicateSymbol() );
				result.append( tuple );
				result.append( '.' );
			}
		}

	    return result.toString();
    }

	/** The map storing the predicate-relation relationship. */
	protected final Map<IPredicate, IRelation> mPredicateRelationMap = new HashMap<IPredicate, IRelation>();
	
	protected final IRelationFactory mRelationFactory;
}
