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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.factory.Factory;
import org.deri.iris.storage.IRelation;


public class FactsWithExternalData implements IFacts
{
	/**
	 * Constructor.
	 */
	public FactsWithExternalData( IFacts facts, List<IDataSource> externalDataSources )
	{
		mFacts = facts;
		mExternalDataSources = new ArrayList<IDataSource>( externalDataSources );
	}
	

	public IRelation get( IPredicate predicate )
    {
	    IRelation result = mFacts.get( predicate );
	    
	    // If we haven't got the external data for this predicate yet...
	    if( mExternalPredicatesAlreadyFetched.add( predicate ) )
	    {
	    	// ... then get it now.
	    	
	    	ITuple from = Factory.BASIC.createTuple( new ITerm[ predicate.getArity() ] );
	    	ITuple to   = Factory.BASIC.createTuple( new ITerm[ predicate.getArity() ] );
	    	
	    	for( IDataSource dataSource : mExternalDataSources )
	    		dataSource.get( predicate, from, to, result );

	    }
	    
	    return result;
    }


	public Set<IPredicate> getPredicates()
    {
	    return mFacts.getPredicates();
    }

    /**
     * Return all facts. The format of the resulting string is parse-able.
     * @return a parse-able string containing all facts
     */
    public String toString()
    {
    	return mFacts.toString();
    }

	private final IFacts mFacts;
	
	private final List<IDataSource> mExternalDataSources;
	
	private Set<IPredicate> mExternalPredicatesAlreadyFetched = new HashSet<IPredicate>();
}
