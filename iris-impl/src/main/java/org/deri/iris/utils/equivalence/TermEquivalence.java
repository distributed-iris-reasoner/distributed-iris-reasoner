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
package org.deri.iris.utils.equivalence;

import java.util.Collections;
import java.util.Set;

import org.deri.iris.api.terms.ITerm;
import org.deri.iris.utils.DisjointSets;

/**
 * An utility class to define equivalence between terms. Uses the disjoint-set
 * data structure.
 * 
 * @see org.deri.iris.utils.DisjointSets
 * @author Adrian Marte
 */
public class TermEquivalence implements IEquivalentTerms {

	/**
	 * The disjoint-set data structure.
	 */
	private final DisjointSets<ITerm> disjointSets = new DisjointSets<ITerm>();

	/**
	 * Creates a new term equivalence relation.
	 */
	public TermEquivalence() {
	}

	public boolean areEquivalent(ITerm x, ITerm y) {
		if (x.equals(y)) {
			return true;
		}

		return disjointSets.areInSameSet(x, y);
	}

	public void setEquivalent(ITerm x, ITerm y) {
		disjointSets.putInSameSet(x, y);
	}

	public ITerm findRepresentative(ITerm term) {
		ITerm representative = disjointSets.find(term);

		if (representative == null) {
			return term;
		}

		return representative;
	}

	public Set<ITerm> getEquivalent(ITerm term) {
		Set<ITerm> set = disjointSets.getSetOf(term);

		if (set.isEmpty()) {
			return Collections.singleton(term);
		}

		return set;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof TermEquivalence)) {
			return false;
		}

		TermEquivalence thatEquivalence = (TermEquivalence) obj;

		return disjointSets.equals(thatEquivalence.disjointSets);
	}

	@Override
	public int hashCode() {
		return disjointSets.hashCode();
	}

	@Override
	public String toString() {
		return disjointSets.toString();
	}

}
