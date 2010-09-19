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

import java.util.Set;

import org.deri.iris.api.terms.ITerm;

/**
 * Defines equivalence between terms.
 * 
 * @author Adrian Marte
 */
public interface IEquivalentTerms {

	/**
	 * Checks if the specified terms are equivalent.
	 * 
	 * @param x The first term.
	 * @param y The second term.
	 * @return <code>true</code> if the two given terms are equivalent,
	 *         <code>false</code> otherwise.
	 */
	public boolean areEquivalent(ITerm x, ITerm y);

	/**
	 * Defines the specified terms as equivalent.
	 * 
	 * @param x The first term.
	 * @param y The second term.
	 */
	public void setEquivalent(ITerm x, ITerm y);

	/**
	 * Returns a representative term for the specified terms. If two terms are
	 * equivalent, they have the same representative term.
	 * 
	 * @param term The term.
	 * @return The representative term for the specified term.
	 */
	public ITerm findRepresentative(ITerm term);

	/**
	 * Returns the set of terms which are equivalent to the specified term. The
	 * set also contains the term itself.
	 * 
	 * @param term The term.
	 * @return The set of terms which are equivalent to the specified term. The
	 *         set also contains the term itself.
	 */
	public Set<ITerm> getEquivalent(ITerm term);

}
