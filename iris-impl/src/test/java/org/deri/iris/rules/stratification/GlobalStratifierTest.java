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
package org.deri.iris.rules.stratification;

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.CONCRETE;
import static org.deri.iris.factory.Factory.TERM;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.builtins.EqualBuiltin;

public class GlobalStratifierTest extends TestCase
{
	ILiteral createLiteral( boolean positive, String predicateName, Object ... termObjects )
	{
		List<ITerm> terms = new ArrayList<ITerm>();

		for( Object o : termObjects )
		{
			if( o instanceof Integer )
				terms.add( CONCRETE.createInteger( (Integer) o ) );
			else if( o instanceof String )
				terms.add( TERM.createVariable( (String) o ) );
		}
		
		if( predicateName.equals( "=" ) )
			return BASIC.createLiteral( positive,
							new EqualBuiltin( terms.toArray( new ITerm[ 2 ] ) ) );
		
		return BASIC.createLiteral( positive, 
									BASIC.createAtom(
										BASIC.createPredicate( predicateName, terms.size() ), 
										BASIC.createTuple( terms )
								)
							);
	}
	
	IRule createRule( ILiteral head, ILiteral ... body )
	{
		List<ILiteral> h = new ArrayList<ILiteral>();
		h.add( head );
		
		List<ILiteral> b = new ArrayList<ILiteral>();
		for( ILiteral l : body )
			b.add( l );

		return BASIC.createRule( h, b );
	}

	public void testStratify()
	{
		List<IRule> rules = new ArrayList<IRule>();
		
		IRule p = createRule( 	createLiteral( true, "p", "X" ),
								createLiteral( true, "u", "X" ),
								createLiteral( false, "r", "X" ),
								createLiteral( false, "q", "X" )
								);
		
		IRule r = createRule( 	createLiteral( true, "r", "X" ),
								createLiteral( true, "s", "X" )
								);

		IRule q = createRule( 	createLiteral( true, "q", "X" ),
								createLiteral( true, "t", "X" ),
								createLiteral( false, "r", "X" )
						);
		
		rules.add( r );
		rules.add( p );
		rules.add( q );
		
		GlobalStratifier gs = new GlobalStratifier();
		
		List<List<IRule>> sRules = gs.stratify( rules );
		
		assertEquals( sRules.get( 0 ).iterator().next(), r );
		assertEquals( sRules.get( 1 ).iterator().next(), q );
		assertEquals( sRules.get( 2 ).iterator().next(), p );
	}
}
