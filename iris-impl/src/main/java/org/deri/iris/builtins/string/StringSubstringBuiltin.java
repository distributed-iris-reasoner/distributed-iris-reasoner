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
import org.deri.iris.api.terms.INumericTerm;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.builtins.FunctionalBuiltin;
import org.deri.iris.factory.Factory;

/**
 * Represents a string substring operation.
 * 
 * @author gigi
 * @author Adrian Marte
 */
public class StringSubstringBuiltin extends FunctionalBuiltin {

	private static final IPredicate PREDICATE = BASIC.createPredicate("STRING_SUBSTRING3", 4);

	/**
	 * Constructor.
	 * 
	 * @param terms The terms, where the first term is the string, the second
	 *            term is the begin index, the third term is the endIndex and
	 *            the fourth term represents the result.
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 */
	public StringSubstringBuiltin(ITerm... terms) {
		super(PREDICATE, terms);
	}

	protected ITerm computeResult(ITerm[] terms) throws EvaluationException {
		// FIXME value spaces of xs:integer, xs:double, xs:float and xs:decimal for numeric terms
		
		if (terms[0] instanceof IStringTerm 
				&& terms[1] instanceof INumericTerm
				&& terms[2] instanceof INumericTerm) {
			IStringTerm string = (IStringTerm) terms[0];
			INumericTerm beginIndex = (INumericTerm) terms[1];
			INumericTerm lengthIndex = (INumericTerm) terms[2];

			int start = beginIndex.getValue().intValue();
			int length = lengthIndex.getValue().intValue();
			String substring = string.getValue().substring(
					start,
					start + length);

			return Factory.TERM.createString(substring);
		}

		return null;
	}

}
