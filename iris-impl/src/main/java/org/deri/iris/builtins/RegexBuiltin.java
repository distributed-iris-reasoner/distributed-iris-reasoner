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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.factory.Factory;

/**
 * Built-in to do regular expression matching.
 */
public class RegexBuiltin extends BooleanBuiltin
{
	/**
	 * Constructs a built-in. Two terms must be passed to the constructor,
	 * the second one must be a string with the regular expression pattern.
	 * @param terms the terms
	 */
	public RegexBuiltin( final ITerm... terms )
	{
		super( PREDICATE, terms );

		if( terms.length != 2 )
			throw new IllegalArgumentException( getClass().getSimpleName() + ": Constructor requires exactly two parameters (ITerm term, IStringTerm pattern)" );
		
		if( ! (terms[ 1 ] instanceof IStringTerm) )
			throw new IllegalArgumentException( getClass().getSimpleName() + ": The second argument of the constructor must be a string pattern" );

		String pattern = (String) terms[ 1 ].getValue();
		
		mPattern = Pattern.compile( pattern );
	}

	protected boolean computeResult( ITerm[] terms )
	{
		assert terms.length == 2;
		
		if( terms[ 0 ] instanceof IStringTerm )
		{
			String testString = (String) terms[ 0 ].getValue();
			Matcher m = mPattern.matcher( testString );
			boolean result = m.matches();
			
			return result;
		}
		else
			return false;
	}

	private final Pattern mPattern;

	/** The predicate defining this built-in. */
	private static final IPredicate PREDICATE = Factory.BASIC.createPredicate( "REGEX", 2 );
}
