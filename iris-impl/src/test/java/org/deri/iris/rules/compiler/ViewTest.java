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
package org.deri.iris.rules.compiler;

import java.util.List;

import junit.framework.TestCase;

import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.api.terms.concrete.IIntegerTerm;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.simple.SimpleRelationFactory;

public class ViewTest extends TestCase
{
	IRelation mRelation;
	View mView;
	
	protected void setUp() throws Exception
	{
		mRelation = new SimpleRelationFactory().createRelation();
		
		// f(1,2,g(2,3))
		ITuple t1 = Helper.createTuple( 1, 2, Helper.createConstructedTerm( "g", 2, 3 ) );
		mRelation.add( t1 );

		// f(1,2,g(2,1))
		ITuple t2 = Helper.createTuple( 1, 2, Helper.createConstructedTerm( "g", 2, 1 ) );
		mRelation.add( t2 );
		
		// f(X,Y,g(Y,X))
		ITuple viewCriteria = Helper.createTuple( "X", "Y", Helper.createConstructedTerm( "g", "Y", "X" ) );
		
		mView = new View( mRelation, viewCriteria, new SimpleRelationFactory() );
	}

	public void testVariables()
	{
		List<IVariable> variables = mView.variables();
		
		assertEquals( variables.size(), 2 );
		
		assertEquals( variables.get( 0 ).getValue(), "X" );
		assertEquals( variables.get( 1 ).getValue(), "Y" );
	}
	
	public void testView()
	{
		IRelation v = mView;
		
		assertEquals( 1, v.size());
		
		ITuple tuple = v.get( 0 );
		
		assertEquals( ( (IIntegerTerm) tuple.get( 0 ) ).getValue().intValue(), 1 );
		assertEquals( ( (IIntegerTerm) tuple.get( 1 ) ).getValue().intValue(), 2 );
		
//		Iterator<ITuple> it = mView.iterator();
//		
//		assertTrue( it.hasNext() );
//		ITuple tuple = it.next();
//	
//		assertEquals( tuple.size(), 2 );
//		assertEquals( ( (IIntegerTerm) tuple.get( 0 ) ).getValue().intValue(), 1 );
//		assertEquals( ( (IIntegerTerm) tuple.get( 1 ) ).getValue().intValue(), 2 );
//		
//		assertFalse( it.hasNext() );
	}
}
