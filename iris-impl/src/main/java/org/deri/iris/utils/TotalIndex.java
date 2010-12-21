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
package org.deri.iris.utils;

import java.util.HashSet;
import java.util.Set;

import org.deri.iris.api.basics.ITuple;
import org.deri.iris.storage.IRelation;

/**
 * Helper class for the semi-naive evaluator.
 * An object of this class indexes an entire relation on every term to enable fast detection
 * of the existence of a tuple within a relation.
 */
public class TotalIndex
{
	/**
	 * Constructor.
	 * @param relation The relation to index.
	 */
	public TotalIndex( IRelation relation )
	{
		mRelation = relation;
	}
	
	/**
	 * Discover if the indexed relation contains a specific tuple.
	 * @param tuple The tuple to test for.
	 * @return true, if a tuple with identical term values already exists in the relation.
	 */
	public boolean contains( ITuple tuple )
	{
		update();
		
		return mBag.contains( tuple );
	}
	
	/**
	 * Update the index with tuples not already seen by the index.
	 */
	private void update()
	{
		for( ; mLastIndexOfView < mRelation.size(); ++mLastIndexOfView )
		{
			ITuple viewTuple = mRelation.get( mLastIndexOfView );
			
			mBag.add( viewTuple );
		}
	}
	
	/** The index in to the relation of the last seen tuple. */
	private int mLastIndexOfView = 0;
	
	/** The set of tuples from the relation. */
	private final Set<ITuple> mBag = new HashSet<ITuple>();
	
	/** The relation being indexed. */
	private final IRelation mRelation;
}
