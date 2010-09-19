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

import java.util.Iterator;

import junit.framework.TestCase;

public class UniqueListTest extends TestCase
{
	UniqueList<Integer> mList;

	@Override
    protected void setUp() throws Exception
    {
		mList = new UniqueList<Integer>();
    }

	public void testAdd()
	{
		mList.add( 0 );
		mList.add( 1 );
		mList.add( 2 );
		
		assertEquals( mList.size(), 3 );
		assertEquals( mList.get( 0 ), new Integer( 0 ) );
		assertEquals( mList.get( 1 ), new Integer( 1 ) );
		assertEquals( mList.get( 2 ), new Integer( 2 ) );
		
		mList.add( 1 );
		assertEquals( mList.size(), 3 );

		mList.add( 1, 3 );
		assertEquals( mList.size(), 4 );
		assertEquals( mList.get( 0 ), new Integer( 0 ) );
		assertEquals( mList.get( 1 ), new Integer( 3 ) );
		assertEquals( mList.get( 2 ), new Integer( 1 ) );
		assertEquals( mList.get( 3 ), new Integer( 2 ) );
	}
	
	public void testIterator()
	{
		mList.add( 0 );
		mList.add( 1 );
		
		Iterator<Integer> it = mList.iterator();

		assertTrue( it.hasNext() );
		assertEquals( it.next().intValue(), 0 );

		assertTrue( it.hasNext() );
		assertEquals( it.next().intValue(), 1 );

		assertFalse( it.hasNext() );
	}
}
