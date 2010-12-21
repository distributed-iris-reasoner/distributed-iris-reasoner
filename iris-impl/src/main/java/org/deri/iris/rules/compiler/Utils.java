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
package org.deri.iris.rules.compiler;

import static org.deri.iris.factory.Factory.BASIC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.utils.equivalence.IEquivalentTerms;

/**
 * Utilities for classes in this package.
 */
public class Utils {

	/**
	 * Convert a list of integers to an array.
	 * 
	 * @param list The list of integers.
	 * @return An array of integers.
	 */
	public static int[] integerListToArray(List<Integer> list) {
		int[] result = new int[list.size()];

		for (int i = 0; i < list.size(); ++i)
			result[i] = list.get(i);

		return result;
	}

	/**
	 * Make a list of terms by selecting them from a tuple.
	 * 
	 * @param tuple The tuple containing terms to select.
	 * @param indices The indices of terms to use.
	 * @return The list of selected tuples.
	 */
	public static List<ITerm> makeKey(ITuple tuple, int[] indices) {
		List<ITerm> key = new ArrayList<ITerm>(indices.length);

		for (int i = 0; i < indices.length; ++i)
			key.add(tuple.get(indices[i]));

		return key;
	}

	/**
	 * Creates all possible combinations of the terms of a specified tuple with
	 * their equivalent terms.
	 * 
	 * @param tuple The tuple for which all possible combinations should
	 *            be created.
	 * @param equivalentTerms The equivalent terms.
	 * @return All possible combinations of the terms of the specified tuple
	 *         with their equivalent terms.
	 */
	public static List<ITuple> createAllCombinations(ITuple tuple,
			IEquivalentTerms equivalentTerms) {
		List<ITuple> tuples = new ArrayList<ITuple>();

		List<Set<ITerm>> sets = new ArrayList<Set<ITerm>>(tuple.size());
		for (ITerm term : tuple) {
			sets.add(equivalentTerms.getEquivalent(term));
		}

		tuples.addAll(createAllCombinations(sets));

		return tuples;
	}

	private static List<ITuple> createAllCombinations(List<Set<ITerm>> sets) {
		return createAllCombinations(sets, 0, new ArrayList<ITerm>());
	}

	private static List<ITuple> createAllCombinations(List<Set<ITerm>> sets,
			int currentIndex, List<ITerm> currentTuple) {
		if (currentIndex >= sets.size()) {
			return Collections.singletonList(BASIC.createTuple(currentTuple));
		}

		List<ITuple> tuples = new ArrayList<ITuple>();
		Set<ITerm> set = sets.get(currentIndex);

		for (ITerm term : set) {
			List<ITerm> newTuple = new ArrayList<ITerm>();
			newTuple.addAll(currentTuple);
			newTuple.add(term);

			tuples.addAll(createAllCombinations(sets, currentIndex + 1,
					newTuple));
		}

		return tuples;
	}

}
