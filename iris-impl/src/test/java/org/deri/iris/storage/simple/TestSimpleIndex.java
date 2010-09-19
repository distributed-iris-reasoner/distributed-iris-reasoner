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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.rules.compiler.Helper;
import org.deri.iris.rules.compiler.Utils;
import org.deri.iris.rules.compiler.View;
import org.deri.iris.storage.IRelation;

public class TestSimpleIndex extends TestCase
{
	IRelation mRelation;
	View mView;
	SimpleIndex mIndex;

	protected void setUp() throws Exception
	{
		mRelation = new SimpleRelation();
		
		mRelation.add( Helper.createTuple( 1, 1, 1 ) );
		mRelation.add( Helper.createTuple( 1, 1, 2 ) );
		mRelation.add( Helper.createTuple( 1, 1, 3 ) );

		mRelation.add( Helper.createTuple( 1, 2, 1 ) );
		mRelation.add( Helper.createTuple( 2, 2, 2 ) );
		
		ITuple viewCriteria = Helper.createTuple( "X", "Y", "Z" );
		
		mView = new View( mRelation, viewCriteria, new SimpleRelationFactory() );
		
		mIndex = new SimpleIndex( mView, 0, 1 );
	}
	
	private static List<ITerm> makeKey( Object ... objects )
	{
		List<ITerm> key = new ArrayList<ITerm>( objects.length );
		
		for( Object o : objects )
		{
			ITerm term = Helper.createTerm( o );
			key.add( term );
		}
		
		return key;
	}

	public void testGet()
	{
		ITuple foreignTuple = Helper.createTuple( 3, 2, 1, 1 );
		
		List<ITuple> matchingTuples = mIndex.get( Utils.makeKey( foreignTuple, new int[] { 2, 3 } ) );
		
		assertNotNull( matchingTuples );
		assertEquals( 3, matchingTuples.size() );

		
		matchingTuples = mIndex.get( makeKey( 1, 2 ) );
		
		assertNotNull( matchingTuples );
		assertEquals( 1, matchingTuples.size() );

	
		matchingTuples = mIndex.get( makeKey( 2, 1 ) );
		
		assertEquals( 0, matchingTuples.size() );
	}
}
