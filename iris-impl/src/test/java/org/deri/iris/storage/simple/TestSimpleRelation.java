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
package org.deri.iris.storage.simple;

import junit.framework.TestCase;

import org.deri.iris.api.basics.ITuple;
import org.deri.iris.rules.compiler.Helper;
import org.deri.iris.storage.IRelation;

public class TestSimpleRelation extends TestCase
{
	IRelation mRelation;
	
	protected void setUp() throws Exception
	{
		mRelation = new SimpleRelation();
	}
	
	public void testAll()
	{
		// Ensure the relation is empty
		assertEquals( mRelation.size(), 0 );
		
		// Insert a new tuple
		ITuple t1 = Helper.createTuple( 2, 1 );
		mRelation.add( t1 );
		assertEquals( mRelation.size(), 1 );
		assertEquals( mRelation.get( 0 ), t1 );

		// Try adding same tuple again and it should not accept it
		mRelation.add( t1 );
		assertEquals( mRelation.size(), 1 );
		assertEquals( mRelation.get( 0 ), t1 );

		// Add a new tuple
		ITuple t2 = Helper.createTuple( 2, 2 );
		mRelation.add( t2 );
		assertEquals( mRelation.size(), 2 );
		assertEquals( mRelation.get( 0 ), t1 );
		assertEquals( mRelation.get( 1 ), t2 );

		// Create a new relation and check that addAll() works.
		IRelation r2 = new SimpleRelation();
		r2.addAll( mRelation );
		assertEquals( r2.size(), 2 );
		assertEquals( r2.get( 0 ), t1 );
		assertEquals( r2.get( 1 ), t2 );
		
		// Now check that it is not possible to add t1 and t2 to the new relation.
		r2.add( t1 );
		r2.add( t2 );
		assertEquals( r2.size(), 2 );
	}
}
