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
package org.deri.iris.builtins.string;

import static org.deri.iris.factory.Factory.BASIC;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.builtins.BooleanBuiltin;
import org.deri.iris.terms.StringTerm;

/**
 * Represents the RIF built-in func:contains as described in
 * http://www.w3.org/TR/xpath-functions/#func-contains. At the moment only
 * Unicode code point collation
 * (http://www.w3.org/2005/xpath-functions/collation/codepoint) is supported.
 */
public class StringContainsBuiltin extends BooleanBuiltin {

	private static final IPredicate PREDICATE = BASIC.createPredicate(
			"STRING_CONTAINS3", 3);

	/**
	 * Constructor.
	 * 
	 * @param terms The terms, where the term at the first position is the
	 *            <code>haystack</code>, the term at the second position is the
	 *            <code>needle</code> and the term at the third position is the
	 *            collation to be used. The <code>haystack</code> is the string
	 *            being searched for the occurrence of the <code>needle</code>.
	 *            The <code>needle</code> is the string to be searched for in
	 *            the <code>haystack</code>.
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 */
	public StringContainsBuiltin(ITerm... terms) {
		super(PREDICATE, terms);
	}

	@Override
	protected boolean computeResult(ITerm[] terms) {
		String haystack = null;
		String needle = null;
		String collation = null;

		if (terms[0] instanceof IStringTerm && terms[1] instanceof StringTerm
				&& terms[2] instanceof StringTerm) {
			haystack = ((IStringTerm) terms[0]).getValue();
			needle = ((IStringTerm) terms[1]).getValue();
			collation = ((IStringTerm) terms[2]).getValue();
		} else {
			return false;
		}

		return contains(haystack, needle, collation);
	}

	public static boolean contains(String haystack, String needle,
			String collation) {
		String defaultCollation = "http://www.w3.org/2005/xpath-functions/collation/codepoint";

		// Only "Unicode code point collation" is supported at the moment.
		if (collation != null && !collation.equalsIgnoreCase(defaultCollation)) {
			throw new IllegalArgumentException("Unsupported collation");
		}

		// If the value of haystack is the zero-length string, the function
		// returns false.
		if (haystack.length() == 0) {
			return false;
		}

		return haystack.contains(needle);
	}

}
