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
package org.deri.iris.facts;

import junit.framework.TestCase;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.storage.IRelationFactory;
import org.deri.iris.storage.simple.SimpleRelationFactory;

public class OriginalFactsPreservingFactsTest extends TestCase
{
	private IFacts originalFacts;
	private IFacts preservingFacts;

	protected void setUp() throws Exception
	{
		originalFacts = new Facts( mRelationFactory );
		preservingFacts = new OriginalFactsPreservingFacts( originalFacts, mRelationFactory );
	}

	public void testAdd()
	{
		FactsTest.helperTestAdd( preservingFacts );
	}

	public void testGetPredicates()
	{
		FactsTest.helperTestGetPredicates( preservingFacts );
	}
	
	public void testPreserve()
	{
		final IPredicate A = FactsTest.createPredicate( "PREDICATE", 1 );
		final IPredicate B = FactsTest.createPredicate( "PREDICATE", 2 );

		ITuple a1 = FactsTest.createTuple( 1 );
		ITuple b1 = FactsTest.createTuple( 1, 2 );
		originalFacts.get( A ).add( a1 );
		originalFacts.get( B ).add( b1 );

		preservingFacts = new OriginalFactsPreservingFacts( originalFacts, mRelationFactory );

		ITuple a2 = FactsTest.createTuple( 3 );
		ITuple b2 = FactsTest.createTuple( 4, 5 );
		preservingFacts.get( A ).add( a2 );
		preservingFacts.get( B ).add( b2 );
		
		assertEquals( 2, preservingFacts.get( A ).size() );
		assertEquals( 2, preservingFacts.get( B ).size() );
		assertEquals( a1, preservingFacts.get( A ).get( 0 ) );
		assertEquals( a2, preservingFacts.get( A ).get( 1 ) );
		assertEquals( b1, preservingFacts.get( B ).get( 0 ) );
		assertEquals( b2, preservingFacts.get( B ).get( 1 ) );

		assertEquals( 1, originalFacts.get( A ).size() );
		assertEquals( 1, originalFacts.get( B ).size() );
		assertEquals( a1, originalFacts.get( A ).get( 0 ) );
		assertEquals( b1, originalFacts.get( B ).get( 0 ) );
	}

	private static final IRelationFactory mRelationFactory = new SimpleRelationFactory();
}
