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

import org.deri.iris.utils.equivalence.IEquivalentTerms;

/**
 * The interface of all index factories.
 * 
 * @see IIndex
 */
public interface IIndexFactory {

	/**
	 * Creates a new index on the given relation on the given terms positions
	 * (indices).
	 * 
	 * @param relation The relation that the index will use.
	 * @param indices The ordered collection of indices. This collection must
	 *            have a size 0 <= size <= arity of the relation. e.g. to create
	 *            an index on terms c1 and c4 for a relation Q( c0, c1, c2, c3,
	 *            c4 ), the indices will be { 1, 4 }
	 * @return The new index instance.
	 */
	IIndex createIndex(IRelation relation, int... indices);

	/**
	 * Creates a new index on the given relation on the given terms positions
	 * (indices). The index uses the specified equivalent terms to identify
	 * equivalent terms. When tuples of this index are matched against a
	 * specific key, this index also returns tuples, whose corresponding terms
	 * are equivalent to the terms of the key.
	 * 
	 * @param relation The relation that the index will use.
	 * @param indices The ordered collection of indices. This collection must
	 *            have a size 0 <= size <= arity of the relation. e.g. to create
	 *            an index on terms c1 and c4 for a relation Q( c0, c1, c2, c3,
	 *            c4 ), the indices will be { 1, 4 }
	 * @param equivalentTerms The equivalent terms.
	 * @return The new index instance.
	 */
	IIndex createIndex(IRelation relation, IEquivalentTerms equivalentTerms,
			int... indices);

}
