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
package org.deri.iris.topdown.oldt;

import java.util.HashSet;

import junit.framework.TestCase;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import org.deri.iris.evaluation.topdown.CommonPredicateTagger;
import org.deri.iris.factory.Factory;

public class CommonPredicateTaggerTest extends TestCase {

	public void testImmediateRecursion() throws ParserException {
		String program =
		    "s(?X, ?Y) :- s(?X, ?Z), r(?Z, ?Y)." +
		    "s(?X, ?Y) :- r(?X, ?Y)." +
		    
		    "?- s(1, ?Y).";
		
		HashSet<IPredicate> expectedSet = new HashSet<IPredicate>();
		expectedSet.add( Factory.BASIC.createPredicate("r", 2) );
		
		Parser parser = new Parser();
		parser.parse( program );
		
		CommonPredicateTagger tagger = new CommonPredicateTagger(parser.getRules(), parser.getQueries().get(0));
		System.out.println(expectedSet + " " + tagger.getMemoPredicates());
		assertEquals( true , expectedSet.containsAll( tagger.getMemoPredicates() ) );
		assertEquals( expectedSet.size(), tagger.getMemoPredicates().size() );
	}
	
	public void testCommonPredicates() throws ParserException {
		String program =
		    "p(?X) :- a(?X), b(?X), a(?X), b(?X), c(?X), a(?X)." +
		    
		    "?- p(1).";
		
		HashSet<IPredicate> expectedSet = new HashSet<IPredicate>();
		expectedSet.add( Factory.BASIC.createPredicate("a", 1) );
		
		Parser parser = new Parser();
		parser.parse( program );
		
		CommonPredicateTagger tagger = new CommonPredicateTagger(parser.getRules(), parser.getQueries().get(0));
		System.out.println(expectedSet + " " + tagger.getMemoPredicates());
		assertEquals( true , expectedSet.containsAll( tagger.getMemoPredicates() ) );
		assertEquals( expectedSet.size(), tagger.getMemoPredicates().size() );
	}
	
	public void testSiblingExample() throws Exception
	{
		String program =
			"parent( '1a', '2a' )." +
			"parent( '2a', '3a' )." +

			"parent( '1b', '2b' )." +
			"parent( '1b', '2c' )." +

			"parent( '2b', '3b' )." +
			"parent( '2b', '3c' )." +
			"parent( '2c', '3d' )." +
			"parent( '2c', '3e' )." +
			
			"parent( '3b', '4b' )." +
			"parent( '3e', '4e' )." +

			"sibling(?X,?Y) :- parent(?Z,?X), parent(?Z,?Y), ?X != ?Y." +
			"cousin(?X,?Y) :- parent(?XP,?X), parent(?YP,?Y), sibling(?XP,?YP).\n" +
			"cousin(?X,?Y) :- parent(?XP,?X), parent(?YP,?Y), cousin(?XP,?YP).\n" +

			"?- cousin(?X,?Y).\n";
		
		HashSet<IPredicate> expectedSet = new HashSet<IPredicate>();
		expectedSet.add( Factory.BASIC.createPredicate("parent", 2) );
		
		Parser parser = new Parser();
		parser.parse( program );
		
		CommonPredicateTagger tagger = new CommonPredicateTagger(parser.getRules(), parser.getQueries().get(0));
		System.out.println(expectedSet + " " + tagger.getMemoPredicates());
		assertEquals( true , expectedSet.containsAll( tagger.getMemoPredicates() ) );
		assertEquals( expectedSet.size(), tagger.getMemoPredicates().size() );
	}
	
}
