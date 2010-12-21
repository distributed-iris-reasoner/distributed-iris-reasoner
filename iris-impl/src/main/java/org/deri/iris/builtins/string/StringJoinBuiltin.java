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

/**
 * Represents the RIF built-in func:string-join as defined in
 * http://www.w3.org/2005/rules/
 * wiki/DTB#func:string-join_.28adapted_from_fn:string-join.29.
 */
public class StringJoinBuiltin extends FunctionalBuiltin {

	private static final String PREDICATE_STRING = "STRING_JOIN";
	/**
	 * The predicate defining this built-in. Here special because RIF built-in
	 * StringJoin can have arity X (RIF: func:string-join<N> string x N).
	 */
	private static final IPredicate PREDICATE = BASIC.createPredicate(PREDICATE_STRING, -1);
	
	// TODO mp : handle arity of N length ! 
	// private IPredicate predicate = BASIC.createPredicate("STRING_JOIN", 4);

	/**
	 * Constructor. Four terms must be passed to the constructor, otherwise an
	 * exception will be thrown.
	 * 
	 * @param terms The terms.
	 * @throws IllegalArgumentException If one of the terms is {@code null}.
	 * @throws IllegalArgumentException If the number of terms submitted is not
	 *             4.
	 * @throws IllegalArgumentException If terms is <code>null</code>.
	 */
	public StringJoinBuiltin(ITerm... terms) {
		// FIXME dw2ad: correct?
		super(BASIC.createPredicate(PREDICATE_STRING, terms.length), terms);
		if (terms.length < 2) {
			throw new IllegalArgumentException("The amount of terms <" + terms.length + "> must at least 2");
		}
	}
	

	protected ITerm computeResult(ITerm[] terms) throws EvaluationException {
		StringBuffer buffer = new StringBuffer();
		String separator = null;

		// the second-last one of the strings is the separator
		int endIndex = terms.length - 2;
		if (terms[endIndex] instanceof IStringTerm) {
			separator = ((IStringTerm) terms[endIndex]).getValue();
		} else {
			return null;
		}

		for (int i = 0; i < endIndex; i++) {
			if (terms[i] instanceof IStringTerm) {
				String string = ((IStringTerm) terms[i]).getValue();

				buffer.append(string);
				
				// do not append separator after the last string
				if (i < endIndex - 1) {
					buffer.append(separator);
				}
			} else {
				return null;
			}
		}

		String result = buffer.toString();
		return Factory.TERM.createString(result);
	}
}
