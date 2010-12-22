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
package org.deri.iris.builtins.datatype;

import static org.deri.iris.factory.Factory.BASIC;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IList;
import org.deri.iris.builtins.BooleanBuiltin;
import org.deri.iris.builtins.list.ListBuiltinHelper;

/**
 * <p>
 * Represents the RIF built-in predicate pred:is-list.
 * </p>
 * <p>
 * Checks if a given {@link ITerm} is an instance of {@link IList}.
 * </p>
 * @see <a href=
 * "http://www.w3.org/2005/rules/wiki/DTB#Functions_and_Predicates_on_RIF_Lists"
 * >http://www.w3.org/2005/rules/wiki/DTB#Functions_and_Predicates_on_RIF_Lists</a>
 * 
 */
public class IsListBuiltin extends BooleanBuiltin {

	/** The predicate defining this built-in. */
	private static final IPredicate PREDICATE = BASIC.createPredicate(
			"IS_LIST", 1);

	/**
	 * Creates the built-in for the specified terms.
	 * 
	 * @param terms
	 *            The terms.
	 * @throws NullPointerException
	 *             If one of the terms is <code>null</code>.
	 * @throws IllegalArgumentException
	 *             If the number of terms submitted is not 1.
	 */
	public IsListBuiltin(ITerm... terms) {
		super(PREDICATE, terms);
	}
	
	protected boolean computeResult(ITerm[] terms) {
		return ListBuiltinHelper.isList(terms[0]);
	}

}
