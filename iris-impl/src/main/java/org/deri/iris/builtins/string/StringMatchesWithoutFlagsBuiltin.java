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

/**
 * Represents the RIF built-in func:matches as described in
 * http://www.w3.org/TR/xpath-functions/#func-matches., but restricts the flags
 * to empty flags.
 */
public class StringMatchesWithoutFlagsBuiltin extends BooleanBuiltin {

	private static final IPredicate PREDICATE = BASIC.createPredicate(
			"STRING_MATCHES2", 2);

	/**
	 * Constructor.
	 * 
	 * @param terms The terms, where the term at the first position is the
	 *            string, the terms at the second position is the pattern and
	 *            the term at the third position represents the flags. The
	 *            string is the string the regular expression is being matched
	 *            against. The patterns is the string representing the regular
	 *            expression. The flags are the flags as described in
	 *            http://www.w3.org/TR/xpath-functions/#flags.
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 */
	public StringMatchesWithoutFlagsBuiltin(ITerm... terms) {
		super(PREDICATE, terms);
	}

	@Override
	protected boolean computeResult(ITerm[] terms) {
		String string = null;
		String pattern = null;
		String flags = "";

		if (terms[0] instanceof IStringTerm && terms[1] instanceof IStringTerm) {
			string = ((IStringTerm) terms[0]).getValue();
			pattern = ((IStringTerm) terms[1]).getValue();
		} else {
			return false;
		}

		return StringMatchesBuiltin.matches(string, pattern, flags);
	}

}
