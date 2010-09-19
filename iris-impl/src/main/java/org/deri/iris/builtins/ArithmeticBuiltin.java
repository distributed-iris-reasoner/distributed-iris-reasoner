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

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.ITerm;

/**
 * Base class of arithmetic built-in predicates.
 * This kind of built-in has:
 * a) can have any arity
 * b) can be evaluated with up to 1 unknown variable
 * c) the unknown variable can be at any position
 * d) if all terms are known at evaluation time, the result is checked to indicate true or false
 */
public abstract class ArithmeticBuiltin extends AbstractBuiltin
{
	/**
	 * Constructor.
	 * @param predicate The predicate that identifies this built-in.
	 * @param terms The terms of the instance. Must be 3.
	 */
	public ArithmeticBuiltin(final IPredicate predicate, final ITerm... terms )
	{
		super( predicate, terms );
	}

	protected ITerm evaluateTerms( ITerm[] terms, int[] variableIndexes ) throws EvaluationException
	{
		assert variableIndexes.length == 0 || variableIndexes.length == 1;
		
		// run the evaluation
		if( variableIndexes.length == 0 )
		{
			int resultIndex = getPredicate().getArity() - 1;
			
			// Perform the operation
			ITerm result = computeMissingTerm( resultIndex, terms );
			
			// return nothing if the operation is invalid
			if( result == null )
				return null;
			
			// Indicate TRUE if the operation is equal to the constant result.
			return testForEquality( terms[ resultIndex ], result ) ? EMPTY_TERM : null;
		}
		else // variableIndexes.length == 1
		{
			assert variableIndexes[ 0 ] >= 0;
			assert variableIndexes[ 0 ] < getPredicate().getArity();
			
			return computeMissingTerm( variableIndexes[ 0 ], terms );
		}
	}
	
	protected boolean testForEquality( ITerm t1, ITerm t2 )
	{
		return BuiltinHelper.equal( t1, t2, getEquivalenceClasses() );
	}

	/**
	 * Compute the missing term when the other two are known.
	 * @param terms The collection of all terms.
	 * @return The computed value.
	 * @throws EvaluationException 
	 */
	protected abstract ITerm computeMissingTerm( int missingTermIndex, ITerm[] terms ) throws EvaluationException;

	public int maxUnknownVariables()
	{
		return 1;
	}
}
