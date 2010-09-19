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

import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;

/**
 * <p>
 * Implementation of some helper functions for relations.
 * </p>
 * @author Richard Pöttler (richard dot poettler at sti2 dot at)
 */
public class Relations {

	/**
	 * Returns a set backed by the given relation.
	 * @param relation the relation, which to represent as set
	 * @return the set representing the relation
	 * @throws IllegalArgumentException if the relation is <code>null</code>
	 */
	public static Set<ITuple> asSet(final IRelation relation) {
		if (relation == null) {
			throw new IllegalArgumentException("The relation must not be null");
		}

		return new RelationSet(relation);
	}

	/**
	 * Converts a predicate to relation mapping to a predicate to set of
	 * tuples mapping.
	 * @param mapping the predicate to relation mapping to convert
	 * @return the predicate to set of tuples mapping
	 * @throws IllegalArgumentException if the mapping is <code>null</code>
	 */
	public static Map<IPredicate, Set<ITuple>> toPredicateSetMapping(final Map<IPredicate, IRelation> mapping) {
		if (mapping == null) {
			throw new IllegalArgumentException("The mapping must not be null");
		}

		final Map<IPredicate, Set<ITuple>> result = new HashMap<IPredicate, Set<ITuple>>();
		for (Map.Entry<IPredicate, IRelation> entry : mapping.entrySet()) {
			result.put(entry.getKey(), asSet(entry.getValue()));
		}
		return result;
	}

	/**
	 * Returns a list backed by the given relation.
	 * @param relation the relation, which to represent as list
	 * @return the list representing the relation
	 * @throws IllegalArgumentException if the relation is <code>null</code>
	 */
	public static List<ITuple> asList(final IRelation relation) {
		if (relation == null) {
			throw new IllegalArgumentException("The relation must not be null");
		}

		return new RelationList(relation);
	}

	/**
	 * Converts a predicate to relation mapping to a predicate to list of
	 * tuples mapping.
	 * @param mapping the predicate to relation mapping to convert
	 * @return the predicate to list of tuples mapping
	 * @throws IllegalArgumentException if the mapping is <code>null</code>
	 */
	public static Map<IPredicate, List<ITuple>> toPredicateListMapping(final Map<IPredicate, IRelation> mapping) {
		if (mapping == null) {
			throw new IllegalArgumentException("The mapping must not be null");
		}

		final Map<IPredicate, List<ITuple>> result = new HashMap<IPredicate, List<ITuple>>();
		for (Map.Entry<IPredicate, IRelation> entry : mapping.entrySet()) {
			result.put(entry.getKey(), asList(entry.getValue()));
		}
		return result;
	}

	/**
	 * <p>
	 * List representation of a relation. All modifications done to the list
	 * will be applied to the backing relation.
	 * </p>
	 * <p>
	 * <b>This implementation is not thread save.</b>
	 * </p>
	 * @author Richard Pöttler (richard dot poettler at sti2 dot at)
	 */
	private static class RelationList extends AbstractList<ITuple> {

		/** The underlying relation. */
		final IRelation relation;

		/**
		 * Constructs a new object for the given relation.
		 * @param relation the relation, which to represent by this
		 * object
		 * @throws IllegalArgumentException if the relation is
		 * <code>null</code>
		 */
		public RelationList(final IRelation relation) {
			if (relation == null) {
				throw new IllegalArgumentException("The relation must not be null");
			}
			this.relation = relation;
		}

		public int size() {
			return relation.size();
		}

		public ITuple get(final int index) {
			return relation.get(index);
		}

		public void add(final int index, final ITuple element) {
			if (index != size()) {
				throw new UnsupportedOperationException(
						"It is only allowed to add to the end of a relation");
			}
			relation.add(element);
		}
	}
	/**
	 * <p>
	 * Set representation of a relation. All modifications done to the set
	 * will be applied to the backing relation.
	 * </p>
	 * <p>
	 * <b>This implementation is not thread save.</b>
	 * </p>
	 * @author Richard Pöttler (richard dot poettler at sti2 dot at)
	 */
	private static class RelationSet extends AbstractSet<ITuple> {

		/** The underlying relation. */
		final IRelation relation;

		/**
		 * Constructs a new object for the given relation.
		 * @param relation the relation, which to represent by this
		 * object
		 * @throws IllegalArgumentException if the relation is
		 * <code>null</code>
		 */
		public RelationSet(final IRelation relation) {
			if (relation == null) {
				throw new IllegalArgumentException("The relation must not be null");
			}
			this.relation = relation;
		}

		public int size() {
			return relation.size();
		}

		public void add(final int index, final ITuple element) {
			relation.add(element);
		}

		public Iterator<ITuple> iterator() {
			return asList(relation).iterator();
		}
	}
}
