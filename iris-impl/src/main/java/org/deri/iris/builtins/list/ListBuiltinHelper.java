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
package org.deri.iris.builtins.list;

import org.deri.iris.api.terms.IConcreteTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IIntTerm;
import org.deri.iris.api.terms.concrete.IList;
import org.deri.iris.terms.concrete.IntTerm;

public class ListBuiltinHelper {

	/**
	 * Checks if a given <code>Object</code> is a <code>List</code>
	 * 
	 * @param term
	 *            the Term to check
	 * @return true if the term is a List false otherwise
	 */
	public static boolean isList(ITerm term) {
		if (term == null) {
			throw new NullPointerException("The Term must not be null.");
		}
		if (term instanceof org.deri.iris.api.terms.concrete.IList) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if a given <code>Object</code> is in a <code>List</code>
	 * 
	 * @param term
	 *            the List
	 * @param term
	 *            the Term to check
	 * @return true if the term is in the List false otherwise
	 */
	public static boolean listContains(ITerm[] terms) {
		if (terms == null)
			throw new NullPointerException("The Terms must not be null.");
		if (terms.length != 2) {
			throw new IllegalArgumentException("There must be 2 Arguments.");
		}
		if (terms[0] == null || !(terms[0] instanceof IList)) {
			throw new IllegalArgumentException(
					"The first Argument must be a List");
		}
		IList list = (org.deri.iris.terms.concrete.List) terms[0];
		return list.contains(terms[1]);
	}

	/**
	 * Returns the number of entries in the list (the length of the list).
	 * 
	 * @param term
	 *            the List
	 * @return the length of the list
	 */
	public static int countList(ITerm term) {
		if (term == null)
			throw new NullPointerException("The Term must not be null.");
		if (!(term instanceof org.deri.iris.api.terms.concrete.IList)) {
			throw new IllegalArgumentException("The Argument must be a List.");
		}
		IList list = (org.deri.iris.terms.concrete.List) term;
		return list.size();
	}

	/**
	 * Returns the item at the given position in the list.
	 * 
	 * @param term
	 *            the List
	 * @param term
	 *            the position (index) of the item
	 * @return the item
	 */
	public static ITerm get(ITerm[] terms) {
		if (terms == null)
			throw new NullPointerException("The Terms must not be null.");
		if (terms.length != 2) {
			throw new IllegalArgumentException("There must be 2 Arguments.");
		}
		if (!(terms[0] instanceof IList)) {
			throw new IllegalArgumentException(
					"First Argument has to be a List.");
		}
		if (!(terms[1] instanceof IIntTerm)) {
			throw new IllegalArgumentException(
					"Second Argument has to be a IntTerm.");
		}
		IList list = (org.deri.iris.terms.concrete.List) terms[0];
		int pos = Integer.parseInt(terms[1].toString());
		if (pos < 0) {
			pos = list.size() + pos;
		}

		return list.get(pos);
	}

	/**
	 * Returns a list, containing (in order) the items starting at position
	 * <code>start</code> and continuing up to, but not including, the
	 * <code>stop</code> position, if <code>start</code> is before
	 * <code>stop</code>. The <code>stop</code> position may be omitted, in
	 * which case it defaults to the length of the list.
	 * 
	 * @param term
	 *            the List
	 * @param term
	 *            the position (index) of the item
	 * @return the item
	 */
	public static IList subList(ITerm[] terms) {
		if (terms == null)
			throw new NullPointerException("The Terms must not be null.");
		if (terms.length < 2) {
			throw new IllegalArgumentException("There must be 2 Arguments.");
		}
		if (!(terms[0] instanceof IList)) {
			throw new IllegalArgumentException(
					"First Argument has to be a List.");
		}
		if (!(terms[1] instanceof IIntTerm)) {
			throw new IllegalArgumentException(
					"Second Argument has to be a IntTerm.");
		}
		IList newList = new org.deri.iris.terms.concrete.List();
		newList.addAll((IList) terms[0]);
		int pos1 = Integer.parseInt(terms[1].toString());
		if (pos1 < 0) {
			pos1 = newList.size() + pos1;
		}
		if (pos1 < 0) {
			return null;
		}
		if (terms.length != 3) {
			return new org.deri.iris.terms.concrete.List(newList.subList(pos1,
					newList.size()));
		} else {
			int pos2 = Integer.parseInt(terms[2].toString());
			if (pos2 < 0) {
				pos2 = newList.size() + pos2;
			}
			if (pos2 < 0) {
				return null;
			}
			if (pos2 > newList.size()) {
				return new org.deri.iris.terms.concrete.List(newList.subList(
						pos1, newList.size()));
			}
			return new org.deri.iris.terms.concrete.List(newList.subList(pos1,
					pos2));
		}

	}

	/**
	 * Returns a list consisting of all the items in <code>list</code>, followed
	 * by <code>item_i</code>, for each <code>i</code>, 1 <= i <= n.
	 * 
	 * @param term
	 *            the List
	 * @param terms
	 *            the IConcreteTerms to add to the list.
	 * @return the List with all terms added.
	 */
	public static IList append(ITerm[] terms) {
		if (terms == null)
			throw new NullPointerException("The Terms must not be null.");
		if (!(terms[0] instanceof IList)) {
			throw new IllegalArgumentException("First Argument must be a List.");
		}

		IList newList = new org.deri.iris.terms.concrete.List();
		newList.addAll((IList) terms[0]);
		for (int i = 1; i < terms.length; i++) {
			if (!(terms[i] instanceof IConcreteTerm)) {
				throw new IllegalArgumentException("Argument " + i
						+ " must be an IConcreteTerm to be appended to a List.");
			} else {
				newList.add((IConcreteTerm) terms[i]);
			}
		}
		return newList;
	}

	/**
	 * Returns a list consisting of all the items in <code>list_1</code>,
	 * followed by all the items in <code>list_i</code>, for each i <= n.
	 * 
	 * @param terms
	 *            [] terms the Lists
	 * @return a list with all lists concatenated
	 */
	public static IList concatenate(ITerm[] terms) {
		if (terms == null)
			throw new NullPointerException("The Terms must not be null.");
		if (!(terms[0] instanceof IList)) {
			throw new IllegalArgumentException("Arguments must be a List.");
		}

		IList newList = new org.deri.iris.terms.concrete.List();

		for (int i = 0; i < terms.length; i++) {
			if (!(terms[i] instanceof IList)) {
				throw new IllegalArgumentException("Argument " + i
						+ " must be a List to be concatenated to a List.");
			} else {
				newList.addAll((IList) terms[i]);
			}
		}
		return newList;
	}

	/**
	 * Returns a list of the arguments <code>item_1</code>, ...
	 * <code>item_n</code>, in the same order they appear as arguments.
	 * 
	 * @param terms
	 *            [] terms
	 * @return a list with all terms inserted.
	 */
	public static IList makeList(ITerm[] terms) {
		if (terms == null || terms.length == 0) {
			return new org.deri.iris.terms.concrete.List();
		}
		if ((terms[0] == null)) {
			throw new NullPointerException("The Terms must not be null.");
		}
		IList list = new org.deri.iris.terms.concrete.List();
		for (int i = 0; i < terms.length; i++) {
			if (!(terms[i] instanceof IConcreteTerm)) {
				throw new IllegalArgumentException(
						"Arguments must be instances of ConcreteTerm.");
			} else {
				list.add((IConcreteTerm) terms[i]);
			}
		}
		return list;
	}

	/**
	 * Return a list which is <code>list</code>, except that
	 * <code>newItem</code> is inserted at the given <code>position</code>, with
	 * the item (if any) that was at that position, and all following items,
	 * shifted down one position.
	 * 
	 * @param term
	 *            the list.
	 * @param term
	 *            the position to insert the item before.
	 * @param term
	 *            the item to insert
	 * @return list with inserted item
	 */
	public static IList insertBefore(ITerm[] terms) {
		if (terms == null)
			throw new NullPointerException("The Terms must not be null.");
		if (!(terms[0] instanceof IList)) {
			throw new IllegalArgumentException("First Argument must be a List.");
		}
		IList list = new org.deri.iris.terms.concrete.List();
		list.addAll((IList) terms[0]);
		if (!(terms[1] instanceof IIntTerm)) {
			throw new IllegalArgumentException(
					"Second Argument must be a IntTerm.");
		}
		if (!(terms[2] instanceof IConcreteTerm)) {
			throw new IllegalArgumentException(
					"Third Argument must be a ConcreteTerm.");
		}
		IntTerm position = (IntTerm) terms[1];
		IConcreteTerm item = (IConcreteTerm) terms[2];
		int index = Integer.parseInt(position.toString());
		if (index >= list.size())
			return null;
		if (index < 0) {
			index = list.size() + index;
		}
		if (index < 0) {
			return null;
		}
		list.add(index, item);
		return list;
	}

	/**
	 * Returns a list which is <code>list</code> except that the item at the
	 * given <code>position</code> has been removed.
	 * 
	 * @param term
	 *            the complete list
	 * @param term
	 *            the position of the item to remove
	 * @return list with removed item.
	 */
	public static IList remove(ITerm[] terms) {
		if (terms == null)
			throw new NullPointerException("The Terms must not be null.");
		if (!(terms[0] instanceof IList)) {
			throw new IllegalArgumentException("First Argument must be a List.");
		}
		if (terms.length != 2) {
			throw new IllegalArgumentException("There must be 2 Arguments.");
		}
		IList list = new org.deri.iris.terms.concrete.List();
		list.addAll((IList) terms[0]);
		if (!(terms[1] instanceof IIntTerm)) {
			throw new IllegalArgumentException(
					"Second Argument must be a IntTerm.");
		}
		IntTerm position = (IntTerm) terms[1];
		int index = Integer.parseInt(position.toString());
		if (index >= list.size())
			return null;
		if (index < 0) {
			index = list.size() + index;
		}
		if (index < 0) {
			return null;
		}
		list.remove(index);
		return list;
	}

	/**
	 * Returns a list with all the items in <code>list</code>, but in reverse
	 * order.
	 * 
	 * @param term
	 *            the complete list
	 * @return list in reverse order.
	 */
	public static IList reverse(ITerm[] terms) {
		if (terms == null)
			throw new NullPointerException("The Terms must not be null.");
		if (!(terms[0] instanceof IList)) {
			throw new IllegalArgumentException("First Argument must be a List.");
		}
		IList list = new org.deri.iris.terms.concrete.List();
		list.addAll((IList) terms[0]);
		IList reverse_list = new org.deri.iris.terms.concrete.List();
		int i = list.size();
		while (!list.isEmpty()) {
			reverse_list.add(list.remove(--i));
		}
		return reverse_list;
	}

	/**
	 * Returns the ascending list of all integers, <code>i>=0</code>, such that
	 * External <code>(func:get(list,i) ) = ?matchValue</code>.
	 * 
	 * @param term
	 *            the list
	 * @param term
	 *            the matchingItem
	 * @return list with all indices of the occurrence of item.
	 */
	public static IList indexof(ITerm[] terms) {
		if (terms == null)
			throw new NullPointerException("The Terms must not be null.");
		if (!(terms[0] instanceof IList)) {
			throw new IllegalArgumentException("First Argument must be a List.");
		}
		IList list = new org.deri.iris.terms.concrete.List();
		list.addAll((IList) terms[0]);
		if (terms[1] == null || !(terms[1] instanceof IConcreteTerm)) {
			throw new IllegalArgumentException(
					"Third Argument must be a ConcreteTerm.");
		}

		IConcreteTerm item = (IConcreteTerm) terms[1];
		IList list2 = new org.deri.iris.terms.concrete.List();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(item)) {
				list2.add(new IntTerm(i));
			}
		}
		return list2;

	}

	/**
	 * Returns a list containing all the items in
	 * <code>list_1, ..., list_n</code>, in the same order, but with all
	 * duplicates removed.
	 * 
	 * @param terms
	 *            lists to union
	 * @return list with duplicates removed
	 */
	public static IList union(ITerm[] terms) {
		// union = distinct_values(concatenate(terms));
		return distinct_values(concatenate(terms));

	}

	/**
	 * Returns a list which contains exactly those items which are in
	 * <code>list</code>, in the order of first appearance, except that all
	 * except the first occurrence of any item are deleted.
	 * 
	 * @param term
	 *            the list
	 * @return list with unique items
	 */
	public static IList distinct_values(ITerm term) {
		if (term == null)
			throw new NullPointerException("The Terms must not be null.");
		if (!(term instanceof IList)) {
			throw new IllegalArgumentException("Argument must be a List.");
		}
		IList list = new org.deri.iris.terms.concrete.List();
		list.addAll((IList) term);
		java.util.List<IConcreteTerm> uniques = new java.util.ArrayList<IConcreteTerm>();
		for (int i = 0; i < list.size(); i++) {
			if (!(uniques.contains(list.get(i)))) {
				uniques.add(list.get(i));
			}
		}
		return new org.deri.iris.terms.concrete.List(uniques);
	}

	/**
	 * Returns a list which contains exactly those items which are in
	 * <code>list</code>, in the order of first appearance, except that all
	 * except the first occurrence of any item are deleted.
	 * 
	 * @param terms
	 * @return
	 */
	public static IList intersect(ITerm[] terms) {
		if (terms == null)
			throw new NullPointerException("The Terms must not be null.");
		if (!(terms[0] instanceof IList)) {
			throw new IllegalArgumentException("Arguments must be Lists.");
		}

		IList list = new org.deri.iris.terms.concrete.List();
		list.addAll((IList) terms[0]);
		IList intersect_list = new org.deri.iris.terms.concrete.List();
		for (int i = 1; i < terms.length; i++) {
			if (!(terms[i] instanceof IList)) {
				throw new IllegalArgumentException("Arguments must be Lists.");
			}
			IList list_i = new org.deri.iris.terms.concrete.List();
			list_i.addAll((IList) terms[i]);
			for (int k = 0; k < list.size(); k++) {
				if (list_i.contains(list.get(k))) {
					intersect_list.add(list.get(k));
				}
			}
			list = intersect_list;
		}
		return distinct_values(intersect_list);
	}

	/**
	 * Returns a list which contains exactly those items which are in
	 * <code>list_1</code> and not in <code>list_2</code>. The order of the
	 * items is the same as in <code>list_1</code>.
	 * 
	 * @param terms
	 *            list_1 list_2
	 * @return list
	 */
	public static IList except(ITerm[] terms) {
		if (terms == null)
			throw new NullPointerException("The Terms must not be null.");
		if (!(terms[0] instanceof IList)) {
			throw new IllegalArgumentException("Arguments must be Lists.");
		}
		if (!(terms[1] instanceof IList)) {
			throw new IllegalArgumentException("Arguments must be Lists.");
		}
		IList list_1 = new org.deri.iris.terms.concrete.List();
		list_1.addAll((IList) terms[0]);
		IList list_2 = new org.deri.iris.terms.concrete.List();
		list_2.addAll((IList) terms[1]);
		IList list_except = new org.deri.iris.terms.concrete.List();
		for (ITerm t : list_1) {
			if (!(list_2.contains(t))) {
				list_except.add((IConcreteTerm) t);
			}
		}
		return list_except;
	}

}
