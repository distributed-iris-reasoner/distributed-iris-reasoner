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

import static org.deri.iris.factory.Factory.BASIC;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.deri.iris.Configuration;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.facts.Facts;
import org.deri.iris.facts.IFacts;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.simple.SimpleRelationFactory;


public class CompiledRuleTest extends TestCase
{
	public void testEvaluate() throws Exception
	{
		IFacts facts = new Facts( new SimpleRelationFactory() );

		IRelation Q = facts.get( BASIC.createPredicate( "q", 2 ) );
		// q( 1, f(2) )
		Q.add( Helper.createTuple( 1, Helper.createConstructedTerm( "f", 2 ) ) );
		// q( 3, f(4) )
		Q.add( Helper.createTuple( 3, Helper.createConstructedTerm( "f", 4 ) ) );
		// q( 5, g(6) )
		Q.add( Helper.createTuple( 5, Helper.createConstructedTerm( "g", 6 ) ) );
		// q( 7, f(8) )
		Q.add( Helper.createTuple( 7, Helper.createConstructedTerm( "f", 8 ) ) );
		// q( 9, f(10) )
		Q.add( Helper.createTuple( 9, Helper.createConstructedTerm( "f", 10 ) ) );
		// q( 11, 12 )
		Q.add( Helper.createTuple( 11, 12 ) );
		
		
		IRelation R = facts.get( BASIC.createPredicate( "r", 2 ) );
		// r( 1, h(2) )
		R.add( Helper.createTuple( 1, Helper.createConstructedTerm( "h", 2 ) ) );
		// r( 4, h(5) )
		R.add( Helper.createTuple( 4, Helper.createConstructedTerm( "h", 5 ) ) );
		// r( 4, h(6) )
		R.add( Helper.createTuple( 4, Helper.createConstructedTerm( "h", 6 ) ) );
		// r( 6, h(7) )
		R.add( Helper.createTuple( 6, Helper.createConstructedTerm( "h", 7 ) ) );
		// r( 8, j(9) )
		R.add( Helper.createTuple( 8, Helper.createConstructedTerm( "j", 9 ) ) );
		// r( 10, 11) )
		R.add( Helper.createTuple( 10, 11 ) );
		
		
		// p( f(X, Y), 2, Z ) :- q( X, f(Y) ) & r( Y, h(Z) )
		List<ILiteral> bodyLiterals = new ArrayList<ILiteral>();
		bodyLiterals.add( Helper.createLiteral( true, "q", "X", Helper.createConstructedTerm( "f", "Y" ) ) );
		bodyLiterals.add( Helper.createLiteral( true, "r", "Y", Helper.createConstructedTerm( "h", "Z" ) ) );
		
		List<ILiteral> headLiterals = new ArrayList<ILiteral>();
		headLiterals.add( Helper.createLiteral( true, "p", Helper.createConstructedTerm( "f", "X", "Y" ), 2, "Z" ) );

		IRule rule = BASIC.createRule( headLiterals, bodyLiterals );
		
		RuleCompiler compiler = new RuleCompiler( facts, new Configuration() );
		ICompiledRule cRule = compiler.compile( rule );
		
		IRelation P = cRule.evaluate();
		
		assertEquals( 2, P.size() );		
	}
	
	public void testLargeDataSetWithFunctionSymbols() throws Exception
	{
		IFacts facts = new Facts( new SimpleRelationFactory() );

		IRelation Q = facts.get( BASIC.createPredicate( "q", 2 ) );
		IRelation R = facts.get( BASIC.createPredicate( "r", 2 ) );
		IRelation S = facts.get( BASIC.createPredicate( "s", 2 ) );
		
		int v = 0;
		final int count = 20000;
		for( int i = 0; i < count; ++i, ++v )
		{
			Q.add( Helper.createTuple( v, Helper.createConstructedTerm( "f", v+1 ) ) );
			R.add( Helper.createTuple( v+1, Helper.createConstructedTerm( "h", v ) ) );
			S.add( Helper.createTuple( v*2, v*2 ) );
		}
		
		// p( f(X, Y), 2, Z ) :- q( X, f(Y) ) & r( Y, h(Z) ) & not s( X, Z )
		List<ILiteral> bodyLiterals = new ArrayList<ILiteral>();
		bodyLiterals.add( Helper.createLiteral( true, "q", "X", Helper.createConstructedTerm( "f", "Y" ) ) );
		bodyLiterals.add( Helper.createLiteral( true, "r", "Y", Helper.createConstructedTerm( "h", "Z" ) ) );
		bodyLiterals.add( Helper.createLiteral( false, "s", "X", "Z" ) );
		
		List<ILiteral> headLiterals = new ArrayList<ILiteral>();
		headLiterals.add( Helper.createLiteral( true, "p", Helper.createConstructedTerm( "f", "X", "Y" ), 2, "Z" ) );
		
		IRule rule = BASIC.createRule( headLiterals, bodyLiterals );
		
		long t = System.currentTimeMillis();

		RuleCompiler compiler = new RuleCompiler( facts, new Configuration() );
		ICompiledRule cRule = compiler.compile( rule );
		
		IRelation P = cRule.evaluate();
		
		long elapsed = System.currentTimeMillis() - t;
		System.out.println( "Rule: " + rule );
		System.out.println( "Relation size = " + count );
		System.out.println( "Elapsed time = " + elapsed + "ms" );
		
		assertEquals( count / 2, P.size() );
	}

	public void testLargeDataSetWithoutFunctionSymbols() throws Exception
	{
		IFacts facts = new Facts( new SimpleRelationFactory() );

		IRelation Q = facts.get( BASIC.createPredicate( "q", 2 ) );
		IRelation R = facts.get( BASIC.createPredicate( "r", 2 ) );
		IRelation S = facts.get( BASIC.createPredicate( "s", 2 ) );
		
		int v = 0;
		final int count = 20000;
		for( int i = 0; i < count; ++i, ++v )
		{
			Q.add( Helper.createTuple( v, v+1 ) );
			R.add( Helper.createTuple( v+1, v ) );
			S.add( Helper.createTuple( v*2, v*2 ) );
		}
		
		// p( X, Y, Z ) :- q( X, Y ) & r( Y, Z ), not s( X, Z )
		List<ILiteral> bodyLiterals = new ArrayList<ILiteral>();
		bodyLiterals.add( Helper.createLiteral( true, "q", "X", "Y" ) );
		bodyLiterals.add( Helper.createLiteral( true, "r", "Y", "Z" ) );
		bodyLiterals.add( Helper.createLiteral( false, "s", "X", "Z" ) );
		
		List<ILiteral> headLiterals = new ArrayList<ILiteral>();
		headLiterals.add( Helper.createLiteral( true, "p", "X", "Y", "Z" ) );
		
		IRule rule = BASIC.createRule( headLiterals, bodyLiterals );

		long t = System.currentTimeMillis();

		RuleCompiler compiler = new RuleCompiler( facts, new Configuration() );
		ICompiledRule cRule = compiler.compile( rule );
		
		IRelation P = cRule.evaluate();
		
		long elapsed = System.currentTimeMillis() - t;
		System.out.println( "Rule: " + rule );
		System.out.println( "Relation size = " + count );
		System.out.println( "Elapsed time = " + elapsed + "ms" );
		
		assertEquals( count / 2, P.size() );
	}
}
