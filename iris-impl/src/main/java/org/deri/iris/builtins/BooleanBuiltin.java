/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
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
package org.deri.iris.builtins;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.ITerm;

/**
 * Base class of all boolean built-in predicates.
 * This kind of built-in ...
 * a) can have any arity
 * b) can only be evaluated when all the terms are known (i.e. no unknown variables)
 * c) evaluates to true or false
 */
public abstract class BooleanBuiltin extends AbstractBuiltin
{
	/**
	 * Constructor.
	 * @param predicate The predicate for this built-in.
	 * @param terms The collection of terms, must be length 2 for comparisons.
	 */
	public BooleanBuiltin( IPredicate predicate, final ITerm... terms )
	{
		super( predicate, terms );
	}

	protected ITerm evaluateTerms( ITerm[] terms, int[] variableIndexes )
	{
		assert variableIndexes.length == 0;

		return computeResult( terms ) ? EMPTY_TERM : null;
	}
	
	/**
	 * Compute the result of the comparison.
	 * @param terms The terms
	 * @return The result of the comparison.
	 */
	protected abstract boolean computeResult( ITerm[] terms );
}
