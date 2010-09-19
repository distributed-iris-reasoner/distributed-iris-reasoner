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

import static org.deri.iris.factory.Factory.BASIC;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.ITerm;

/**
 * <p>
 * Represents an x = max(y,z) operation. At evaluation time only the result can be unknown.
 * </p>
 * <p>
 * The syntax in Datalog will be, e.g.
 * p(?max) :- q(?x,?y), MAX(?x, ?y, ?max).
 * </p>
 */
public class MaxBuiltin extends ArithmeticBuiltin
{
	/**
	 * Constructor. Three terms must be passed to the constructor,
	 * otherwise an exception will be thrown.
	 * 
	 * @param t the terms
	 */
	public MaxBuiltin(final ITerm... terms) {
		super(PREDICATE, terms);

		if( terms.length != 3 )
			throw new IllegalArgumentException( getClass().getSimpleName() + ": Constructor requires exactly three parameters" );
	}

	protected ITerm computeMissingTerm( int missingTermIndex, ITerm[] terms )
	{
		switch( missingTermIndex )
		{
		case 0:
			return null;
			
		case 1:
			return null;
			
		default:
			if( BuiltinHelper.less( terms[ 0 ], terms[ 1 ] ) )
				return terms[ 1 ];
			else
				return terms[ 0 ];
		}
	}
	
	/** The predicate defining this built-in. */
	private static final IPredicate PREDICATE = BASIC.createPredicate( "MAX", 3 );
}
