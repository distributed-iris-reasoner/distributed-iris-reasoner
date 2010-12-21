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
package org.deri.iris.storage;

import org.deri.iris.api.basics.ITuple;

/**
 * The interface to all relation classes.
 * The order of the tuples is given by the order of insertion. 
 * The access via index position is intended to allow for smart indexing, caching etc when
 * using relation classes that support large amounts of data.
 */
public interface IRelation
{
	/**
	 * Add a tuple to the relation.
	 * The tuple MUST have the same arity as all other tuples in the relation.
	 * @param tuple The (unique) tuple to add.
	 * @return true, if it was added, false if a tuple already exists in the relation with
	 * the same term values.
	 */
	boolean add( ITuple tuple );

	/**
	 * Add all tuples in relation 'relation' to this relation. 
	 * The tuples in 'relation' MUST have the same arity as all other tuples in this relation.
	 * @param relation The relation containing tuples to add.
	 * @return true if any tuples were actually added.
	 */
	boolean addAll( IRelation relation );
	
	/**
	 * Get the current number of tuples in this relation.
	 * @return The number of tuples in the relation.
	 */
	int size();
	
	/**
	 * Get a tuple at a specific index.
	 * @param index The index of the tuple in the relation, 0 <= index < size().
	 * @return The tuple at the given index position.
	 */
	ITuple get( int index );
	
	boolean contains( ITuple tuple );
}
