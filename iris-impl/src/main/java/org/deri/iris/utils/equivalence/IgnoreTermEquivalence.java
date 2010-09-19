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

/**
 * An ineffective term equivalence implementation, i.e. this term equivalence
 * relation does not keep track of equivalent terms, but only provides the basic
 * required functionality. In particular:
 * <ul>
 * <li>
 * <code>areEquivalent</code> only checks for equality of the two terms
 * using the corresponding <code>equals</code> method.</li>
 * <li><code>findRepresentative</code> returns the term itself.</li>
 * <li><code>getEquivalent</code> returns a singleton set containing the term
 * itself.</li>
 * <li><code>setEquivalent</code> does nothing.</li>
 * </ul>
 * 
 * @author Adrian Marte
 */
public class IgnoreTermEquivalence implements IEquivalentTerms {

	public boolean areEquivalent(ITerm x, ITerm y) {
		return x.equals(y);
	}

	public ITerm findRepresentative(ITerm term) {
		return term;
	}

	public Set<ITerm> getEquivalent(ITerm term) {
		return Collections.singleton(term);
	}

	public void setEquivalent(ITerm x, ITerm y) {
		// Do nothing.
	}

}
