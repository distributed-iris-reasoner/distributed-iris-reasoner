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

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.builtins.FunctionalBuiltin;
import org.deri.iris.factory.Factory;
import org.deri.iris.terms.StringTerm;

/**
 * Represents the RIF built-in func:substring-after as described in
 * http://www.w3.org/TR/xpath-functions/#func-substring-after. At the moment
 * only Unicode code point collation
 * (http://www.w3.org/2005/xpath-functions/collation/codepoint) is supported.
 */
public class StringSubstringAfterBuiltin extends FunctionalBuiltin {

	private static final IPredicate PREDICATE = BASIC.createPredicate(
			"STRING_SUBSTRING_AFTER3", 4);

	/**
	 * Constructor.
	 * 
	 * @param terms The terms, where the term at the first position is the
	 *            <code>haystack</code>, the term at the second position is the
	 *            <code>needle</code>, the term at the third position is the
	 *            collation and the term at the last position represents the
	 *            result. The <code>haystack</code> is the string being searched
	 *            for the occurrence of the <code>needle</code>. The
	 *            <code>needle</code> is the string to be searched for in the
	 *            <code>haystack</code>.
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 */
	public StringSubstringAfterBuiltin(ITerm... terms) {
		super(PREDICATE, terms);
	}

	protected ITerm computeResult(ITerm[] terms) throws EvaluationException {
		String haystack = null;
		String needle = null;
		String collation = null;

		if (terms[0] instanceof IStringTerm && terms[1] instanceof StringTerm
				&& terms[2] instanceof StringTerm) {
			haystack = ((IStringTerm) terms[0]).getValue();
			needle = ((IStringTerm) terms[1]).getValue();
			collation = ((IStringTerm) terms[2]).getValue();
		} else {
			return null;
		}

		String result = substring(haystack, needle, collation);

		if (result != null) {
			return Factory.TERM.createString(result);
		}

		return null;
	}

	public static String substring(String haystack, String needle,
			String collation) {
		String defaultCollation = "http://www.w3.org/2005/xpath-functions/collation/codepoint";

		// Only "Unicode code point collation" is supported at the moment.
		if (collation != null && !collation.equalsIgnoreCase(defaultCollation)) {
			throw new IllegalArgumentException("Unsupported collation");
		}

		if (needle.length() == 0) {
			return haystack;
		}

		int index = haystack.indexOf(needle);

		if (index >= 0 && (index + needle.length()) < haystack.length()) {
			return haystack.substring(index + needle.length());
		} else {
			return "";
		}
	}

}
