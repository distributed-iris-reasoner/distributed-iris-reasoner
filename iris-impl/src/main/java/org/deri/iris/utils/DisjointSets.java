/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2009 Semantic Technology Institute (STI) Innsbruck, 
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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * A data structure for representing and managing partitions, i.e. families of
 * disjoint sets. Implemented as a disjoint-set data structure, which uses path
 * compression and union by rank.
 * </p>
 * <p>
 * Note that the corresponding <code>equals</code> and <code>hashCode</code>
 * methods of the type <code>T</code> are used to determine the <i>equality</i>
 * of two representative elements of type <code>T</code>. Therefore, the
 * <code>equals</code> and <code>hashCode</code> methods have to be implemented
 * correctly.
 * </p>
 * 
 * @author Uwe Keller
 * @author Adrian Marte
 * @see java.lang.Object#equals(java.lang.Object)
 * @see java.lang.Object#hashCode(java.lang.Object)
 * @see <a
 *      href="http://en.wikipedia.org/wiki/Disjoint-set_data_structure">Wikipedia
 *      article about Disjoint-set data structure</a>
 */
public class DisjointSets<T> {

	/**
	 * Map of the elements in the domain to their actual representation in the
	 * classes.
	 */
	private Map<T, Node<T>> elements;

	/**
	 * Creates a new empty equivalence relation.
	 */
	public DisjointSets() {
		elements = new HashMap<T, Node<T>>();
	}

	/**
	 * Adds a new element to the domain of the equivalence relation. In fact the
	 * new element corresponds to a new singleton set consisting only of the
	 * given object.
	 * 
	 * @param element The new element.
	 * @return <code>true</code> if this disjoint-set did not already contain
	 *         the specified element, <code>false</code> otherwise.
	 */
	public boolean add(T element) {
		if (elements.containsKey(element)) {
			return false;
		}

		Node<T> node = new Node<T>(element);
		elements.put(element, node);

		return true;
	}

	/**
	 * Removes an element from the disjoint-set. The element is removed from all
	 * sets.
	 * 
	 * @param element The element.
	 * @returns <code>true</code> if this disjoint-set contained the specified
	 *          element, <code>false</code> otherwise.
	 */
	public boolean remove(T element) {
		return (elements.remove(element) != null);
	}

	/**
	 * Checks if the two specified objects are in the same set. The
	 * corresponding <code>equals</code> method is used to determine the
	 * equality of elements of the given type.
	 * 
	 * @param x The first object.
	 * @param y The second object.
	 * @return <code>true</code> if the two given objects are in the same set,
	 *         <code>false</code> otherwise.
	 */
	public boolean areInSameSet(T x, T y) {
		T representativeX = find(x);
		T representativeY = find(y);

		if (representativeX != null && representativeY != null) {
			return representativeX.equals(representativeY);
		}

		return false;
	}

	/**
	 * Merges the two sets of the two specified elements into a single set.
	 * Creates a new singleton set for elements which have not been added
	 * before.
	 * 
	 * @param x The first element.
	 * @param y The second element.
	 * @return The representative object of the set that represents the merge of
	 *         the element's sets.
	 */
	public T putInSameSet(T x, T y) {
		// Only adds x and y if they have not been added before.
		add(x);
		add(y);

		Node<T> rootX = findRoot(x);
		Node<T> rootY = findRoot(y);

		if (rootX.rank > rootY.rank) {
			Node<T> temp = rootX;
			rootX = rootY;
			rootY = temp;
		} else if (rootX.rank == rootY.rank) {
			++rootY.rank;
		}

		rootX.parent = rootY;

		return rootY.object;
	}

	/**
	 * Retrieves the representative element of the set in which the given
	 * element resides.
	 * 
	 * @param element The element.
	 * @return The representative object for the set containing the specified
	 *         element, or <code>null</code> if the specified element has not
	 *         been added yet, i.e. there is no set containing the element.
	 */
	public T find(T element) {
		Node<T> root = findRoot(element);

		if (root != null) {
			return root.object;
		}

		return null;
	}

	private Node<T> findRoot(T o) {
		Node<T> node = elements.get(o);

		if (node != null) {
			return node.getRoot();
		}

		return null;
	}

	/**
	 * Returns a collection of all the sets of this disjoint-set.
	 * 
	 * @return A collection of all the sets of this disjoint-set.
	 */
	public Collection<Set<T>> getSets() {
		Map<T, Set<T>> equivalanceSets = new HashMap<T, Set<T>>();

		for (T x : elements.keySet()) {
			T representative = find(x);

			Set<T> equivalanceSet = equivalanceSets.get(representative);

			if (equivalanceSet == null) {
				equivalanceSet = new HashSet<T>();
				equivalanceSets.put(representative, equivalanceSet);
			}

			equivalanceSet.add(x);
		}

		return equivalanceSets.values();
	}

	/**
	 * Returns the set of elements in which the specified element resides.
	 * 
	 * @param element The element.
	 * @return The set of elements in which the specified element resides or an
	 *         empty set if has not been added to this disjoint-set yet.
	 */
	public Set<T> getSetOf(T element) {
		Set<T> equivalanceSet = new HashSet<T>();

		for (T y : elements.keySet()) {
			if (areInSameSet(element, y)) {
				equivalanceSet.add(y);
			}
		}

		return equivalanceSet;
	}

	/**
	 * Returns the number of sets.
	 * 
	 * @return The number of equivalence classes in the equivalence relation.
	 */
	public int getNumberOfSets() {
		return getSets().size();
	}

	@Override
	public int hashCode() {
		return elements.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof DisjointSets<?>)) {
			return false;
		}

		DisjointSets<?> thatSets = (DisjointSets<?>) obj;

		return elements.equals(thatSets.elements);
	}

	@Override
	public String toString() {
		return getSets().toString();
	}

	private static class Node<S> {
		private S object;

		private Node<S> parent;

		private int rank;

		private Node(S o) {
			object = o;
			parent = this;
			rank = 0;
		}

		private Node<S> getRoot() {
			if (parent.equals(this)) {
				return this;
			}

			parent = parent.getRoot();
			return parent;
		}

		@Override
		public int hashCode() {
			int hashCode = object.hashCode();
			hashCode += 31 * rank;

			if (!parent.equals(this)) {
				hashCode += 37 * parent.hashCode();
			}

			return hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}

			if (!(obj instanceof Node<?>)) {
				return false;
			}

			Node<?> thatNode = (Node<?>) obj;

			if (thatNode.object.equals(object) && thatNode.rank == rank) {
				if (thatNode.parent != thatNode) {
					return thatNode.parent.equals(parent);
				}

				return true;
			}

			return false;
		}
	}

}
