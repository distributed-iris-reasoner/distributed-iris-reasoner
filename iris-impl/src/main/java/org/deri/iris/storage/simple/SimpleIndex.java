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
package org.deri.iris.storage.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.storage.IIndex;
import org.deri.iris.storage.IRelation;
import org.deri.iris.utils.equivalence.IEquivalentTerms;
import org.deri.iris.utils.equivalence.IgnoreTermEquivalence;

/**
 * A simple, in-memory, hash-based index.
 */
public class SimpleIndex implements IIndex {

	/**
	 * Creates an index on the given relation, on the specified term indices.
	 * 
	 * @param relation The relation to index.
	 * @param indices The term indices using zero-based indexing. Each index
	 *            value must be greater than or equal to zero and less than the
	 *            arity if the relation.
	 */
	SimpleIndex(IRelation relation, int... indices) {
		this(relation, new IgnoreTermEquivalence(), indices);
	}

	/**
	 * Creates an index on the given relation, on the specified term indices.
	 * 
	 * @param relation The relation to index.
	 * @param indices The term indices using zero-based indexing. Each index
	 *            value must be greater than or equal to zero and less than the
	 *            arity if the relation.
	 * @param equivalentTerms The equivalent terms.
	 */
	SimpleIndex(IRelation relation, IEquivalentTerms equivalentTerms,
			int... indices) {
		mIndices = indices;
		mRelation = relation;
		mEquivalentTerms = equivalentTerms;
	}

	// TODO change to return Iterator<ITuple> - for really big relations??
	public List<ITuple> get(List<ITerm> key) {
		assert key.size() == mIndices.length;

		update();

		// Replace all terms of the key with the corresponding representatives,
		// according to the equivalent terms.
		List<ITerm> newKey = changeKey(key);

		List<ITuple> result = mBag.get(newKey);

		if (result == null)
			result = mEmptyTupleList;

		return result;
	}

	/**
	 * Update this index by reading any tuples not yet seen from the relation.
	 */
	private void update() {
		// The representatives of the terms of a key in the map (mBag) may
		// change due to a change in the equivalence relation, therefore we have
		// to check all keys and update them if any change to the term
		// equivalence relation has been applied.
		int hashCode = mEquivalentTerms.hashCode();
		
		if (hashCode != mPreviousHashCode) {
			// Update the keys in the map.
			updateKeys();
			
			// Reset the index of the last checked tuple.
			mLastIndexOfView = 0;

			// Store the current hash code of the equivalent terms.
			mPreviousHashCode = hashCode;
		}

		for (; mLastIndexOfView < mRelation.size(); ++mLastIndexOfView) {
			ITuple viewTuple = mRelation.get(mLastIndexOfView);
			List<ITerm> key = makeKey(viewTuple);

			List<ITuple> values = mBag.get(key);
			if (values == null) {
				values = new ArrayList<ITuple>();
				mBag.put(key, values);
			}
			values.add(viewTuple);
		}
	}

	private void updateKeys() {
		Map<List<ITerm>, List<ITuple>> toAdd = new HashMap<List<ITerm>, List<ITuple>>();
		Iterator<List<ITerm>> keyIterator = mBag.keySet().iterator();

		while (keyIterator.hasNext()) {
			List<ITerm> oldKey = keyIterator.next();
			List<ITerm> newKey = changeKey(oldKey);

			if (!oldKey.equals(newKey)) {
				// Add mapped values of the old key to a temporary map.
				toAdd.put(newKey, mBag.get(oldKey));

				// Remove the key and the corresponding mapped list of
				// tuples from the map.
				keyIterator.remove();
			}
		}

		// Add the temporary map.
		mBag.putAll(toAdd);
	}

	/**
	 * Creates a new key for the specified key. The new key consists of the
	 * corresponding representatives of the terms, according to the term
	 * equivalence relation.
	 * 
	 * @param oldKey
	 * @return
	 */
	private List<ITerm> changeKey(List<ITerm> oldKey) {
		// Create a new key of the same size.
		List<ITerm> newKey = new ArrayList<ITerm>(oldKey.size());

		// For each term in the key find the representative using
		// the equivalent terms.
		for (ITerm term : oldKey) {
			ITerm representative = mEquivalentTerms.findRepresentative(term);
			newKey.add(representative);
		}

		return newKey;
	}

	/**
	 * Make a key from the given tuples and the known term index positions.
	 * 
	 * @param tuple The tuples
	 * @return
	 */
	private List<ITerm> makeKey(ITuple tuple) {
		List<ITerm> key = new ArrayList<ITerm>(mIndices.length);

		// For each term in the tuple find the representative using
		// the equivalent terms.
		for (int i = 0; i < mIndices.length; ++i) {
			ITerm term = tuple.get(mIndices[i]);
			ITerm representative = mEquivalentTerms.findRepresentative(term);
			key.add(representative);
		}

		return key;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append("Indices: ");
		result.append(Arrays.toString(mIndices));
		result.append(", Values: ");
		result.append(mBag);

		return result.toString();
	}

	/** The index of the last last known tuple from the relation. */
	private int mLastIndexOfView = 0;

	/** The maps of unique key values to matching tuples. */
	private final Map<List<ITerm>, List<ITuple>> mBag = new HashMap<List<ITerm>, List<ITuple>>();

	/** The term indices to index the relation on. */
	private final int[] mIndices;

	/** The relation being indexed. */
	private final IRelation mRelation;

	/** The equivalent terms. */
	private IEquivalentTerms mEquivalentTerms;

	/** The previous hash code of the equivalent terms. */
	private int mPreviousHashCode;

	private static final List<ITuple> mEmptyTupleList = Collections
			.unmodifiableList(new ArrayList<ITuple>());

}
