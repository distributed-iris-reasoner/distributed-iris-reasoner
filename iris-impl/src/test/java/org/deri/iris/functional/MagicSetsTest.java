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
package org.deri.iris.functional;

import java.util.List;
import org.deri.iris.Configuration;
import org.deri.iris.KnowledgeBaseFactory;
import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.compiler.Parser;
import org.deri.iris.functional.Helper.Timer;
import org.deri.iris.optimisations.magicsets.MagicSets;
import org.deri.iris.optimisations.rulefilter.RuleFilter;
import org.deri.iris.storage.IRelation;
import junit.framework.TestCase;

/**
 * Tests for magic sets.
 */
public class MagicSetsTest extends TestCase
{
	public void testTranslation() throws Exception
	{
		String program =
			"triple(?y, ?a, ?z) :- triple(?x, '88', ?y), triple(?x, ?a, ?z), NOT_EQUAL(?a, '88'), NOT_EQUAL(?x, ?y). " +
			"triple(?z, ?a, ?y) :- triple(?x, '88', ?y), triple(?z, ?a, ?x), NOT_EQUAL(?a, '88'), NOT_EQUAL(?x, ?y). " +
			"?- triple('184', ?p, ?o). ";		
       	String expectedResults = 
			"a( 1, 2 )." +
			"a( 0, 3 ).";

       	try { evaluateSemiNaiveAndOptimisations( program, expectedResults ); } catch( Exception e ) {}
       	try { evaluateSemiNaiveAndOptimisations( program, expectedResults ); } catch( Exception e ) {}
       	try { evaluateSemiNaiveAndOptimisations( program, expectedResults ); } catch( Exception e ) {}
       	try { evaluateSemiNaiveAndOptimisations( program, expectedResults ); } catch( Exception e ) {}
       	try { evaluateSemiNaiveAndOptimisations( program, expectedResults ); } catch( Exception e ) {}
       	int z = 1;
	}

	public static void evaluateSemiNaiveAndOptimisations( String program, String expectedResults ) throws Exception
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();
		
		configuration.programOptmimisers.add( new RuleFilter() );
		configuration.programOptmimisers.add( new MagicSets() );

		executeAndCheckResults( program, expectedResults, configuration, "Semi-Naive and Magic Sets" );
	}
	
	public static void executeAndCheckResults( String program, String expected, Configuration configuration, String evaluationName ) throws Exception
	{
		Parser parser = new Parser();
		parser.parse( program );
		List<IQuery> queries = parser.getQueries();

		assert queries.size() <= 1;
		
		IQuery query = null;
		if( queries.size() == 1 )
			query = queries.get( 0 );
		
		// Instantiate the knowledge-base
		IKnowledgeBase kb = KnowledgeBaseFactory.createKnowledgeBase( parser.getFacts(), parser.getRules(), configuration );
		
		// Execute the query
		if( query != null )
			kb.execute( query );
	}
}
