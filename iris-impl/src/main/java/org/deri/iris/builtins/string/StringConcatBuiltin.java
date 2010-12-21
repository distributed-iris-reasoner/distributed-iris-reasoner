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
import org.deri.iris.builtins.datatype.ToStringBuiltin;
import org.deri.iris.factory.Factory;

/**
 * Represents the RIF built-in func:concat as defined in http://www.w3.org/2005
 * /rules/wiki/DTB#func:concat_.28adapted_from_fn:concat.29.
 */
public class StringConcatBuiltin extends FunctionalBuiltin {

	private static final String PREDICATE_STRING = "STRING_CONCAT";
	/** The predicate defining this built-in. */
	private static final IPredicate PREDICATE = BASIC.createPredicate(PREDICATE_STRING, -1);

	/**
	 * Constructor. Three terms must be passed to the constructor, otherwise an
	 * exception will be thrown.
	 * 
	 * @param terms The terms.
	 * @throws IllegalArgumentException If one of the terms is {@code null}.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             3.
	 * @throws IllegalArgumentException If terms is <code>null</code>.
	 */
	public StringConcatBuiltin(ITerm... terms) {
		// FIXME dw2ad: correct?
		// accept any amount of terms, at least 3
		super(BASIC.createPredicate(PREDICATE_STRING, terms.length), terms);
		
		if (terms.length < 3) {
			throw new IllegalArgumentException("The amount of terms <" + terms.length + "> must at least 3");
		}
	}

	protected ITerm computeResult(ITerm[] terms) throws EvaluationException {
		
		StringBuilder buffer = new StringBuilder();
		
		int endIndex = terms.length - 1;
		for (int i = 0; i < endIndex; i++) {
			IStringTerm string = ToStringBuiltin.toString(terms[i]);
			
			if (string == null)
				return null;
			
			buffer.append(string.getValue());
		}
		
		return Factory.TERM.createString(buffer.toString());
	}

}
