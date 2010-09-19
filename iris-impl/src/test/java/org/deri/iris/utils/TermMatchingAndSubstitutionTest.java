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
package org.deri.iris.utils;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.factory.Factory;

public class TermMatchingAndSubstitutionTest extends TestCase
{
	public void testConstantMatch()
	{
		String someString = "test";
		ITerm body = Factory.TERM.createString( someString );
		ITerm relation = Factory.TERM.createString( someString );
		
		Map<IVariable, ITerm> variableMap = new HashMap<IVariable, ITerm>();
		
		assertTrue( TermMatchingAndSubstitution.match( body, relation, variableMap ) );
		
		assertEquals( variableMap.size(), 0 );
	}

	public void testConstantMisMatch()
	{
		ITerm body = Factory.TERM.createString( "test" );
		ITerm relation = Factory.TERM.createString( "different" );
		
		Map<IVariable, ITerm> variableMap = new HashMap<IVariable, ITerm>();
		
		assertFalse( TermMatchingAndSubstitution.match( body, relation, variableMap ) );
		
		assertEquals( variableMap.size(), 0 );
	}

	public void testVariable()
	{
		String someString = "test";
		String variableName = "X";
		ITerm body = Factory.TERM.createVariable( variableName );
		ITerm relation = Factory.TERM.createString( someString );
		
		Map<IVariable, ITerm> variableMap = new HashMap<IVariable, ITerm>();
		
		assertTrue( TermMatchingAndSubstitution.match( body, relation, variableMap ) );
		
		assertEquals( variableMap.size(), 1 );
		ITerm matchedTerm = variableMap.get( body );
		
		assertEquals( relation, matchedTerm );
	}

	public void testHeadSubstitution()
	{
		// X = 'x', Y = j('y')
		IVariable X = Factory.TERM.createVariable( "X" );
		IVariable Y = Factory.TERM.createVariable( "Y" );

		ITerm x = Factory.TERM.createString( "x" );
		ITerm y = Factory.TERM.createString( "y" );
		ITerm j = Factory.TERM.createConstruct( "j", y );
		
		Map<IVariable, ITerm> variableMap = new HashMap<IVariable, ITerm>();

		variableMap.put( X, x );
		variableMap.put( Y, j );

		// g(f(X,h(Y,X)))
		ITerm h = Factory.TERM.createConstruct( "h", Y, X );
		ITerm f = Factory.TERM.createConstruct( "f", X, h );
		ITerm g = Factory.TERM.createConstruct( "g", f );
		
		ITuple headTuple = Factory.BASIC.createTuple( g );

		ITuple substitutedHead = TermMatchingAndSubstitution.substituteVariablesInToTuple( headTuple, variableMap );
		
		// => g(f('x',h(j('y'),'x')))
		ITerm y2 = Factory.TERM.createString( "y" );
		ITerm j2 = Factory.TERM.createConstruct( "j", y2 );
		ITerm x2 = Factory.TERM.createString( "x" );
		ITerm h2 = Factory.TERM.createConstruct( "h", j2, x2 );
		ITerm f2 = Factory.TERM.createConstruct( "f", x2, h2 );
		ITerm g2 = Factory.TERM.createConstruct( "g", f2 );
		
		assertEquals( substitutedHead.iterator().next(), g2 );
	}
	
	public void testUnifyTwoGroundedTerms()
	{
		ITerm x = Factory.TERM.createString( "x" );
		ITerm y = Factory.TERM.createString( "y" );
		
		Map<IVariable, ITerm> variableMap = new HashMap<IVariable, ITerm>();
		
		assertFalse( TermMatchingAndSubstitution.unify( x, y, variableMap ) );
		assertTrue( TermMatchingAndSubstitution.unify( x, x, variableMap ) );
		assertEquals( 0, variableMap.size() );
	}

	public void testUnifyGroundedTermAndVariable()
	{
		ITerm x = Factory.TERM.createString( "x" );
		IVariable Y = Factory.TERM.createVariable( "Y" );
		
		Map<IVariable, ITerm> variableMap = new HashMap<IVariable, ITerm>();
		
		assertTrue( TermMatchingAndSubstitution.unify( x, Y, variableMap ) );
		assertEquals( 1, variableMap.size() );
		assertTrue( variableMap.get( Y ).equals( x ) );

		// And the other way round
		variableMap = new HashMap<IVariable, ITerm>();
		
		assertTrue( TermMatchingAndSubstitution.unify( Y, x, variableMap ) );
		assertEquals( 1, variableMap.size() );
		assertTrue( variableMap.get( Y ).equals( x ) );
	}

	public void testUnifyConstructedTerms()
	{
		// f( ?X, y ) = f( x, ?Y )
		// => ?X = x, ?Y = y
		
		IVariable X = Factory.TERM.createVariable( "X" );
		IVariable Y = Factory.TERM.createVariable( "Y" );

		ITerm x = Factory.TERM.createString( "x" );
		ITerm y = Factory.TERM.createString( "y" );
		
		ITerm c1 = Factory.TERM.createConstruct( "f", X, y );
		ITerm c2 = Factory.TERM.createConstruct( "f", x, Y );

		Map<IVariable, ITerm> variableMap = new HashMap<IVariable, ITerm>();
		
		assertTrue( TermMatchingAndSubstitution.unify( c1, c2, variableMap ) );
		assertEquals( 2, variableMap.size() );
		assertTrue( variableMap.get( X ).equals( x ) );
		assertTrue( variableMap.get( Y ).equals( y ) );
	}
}
