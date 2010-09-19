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
package org.deri.iris.querycontainment;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.compiler.Parser;

/**
 * <p>
 * Tests the query containment functionality.
 * </p>
 * <p>
 * $Id: QueryContainmentTest.java,v 1.1 2007-11-07 16:13:31 nathaliest Exp $
 * </p>
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.1 $
 */
public class QueryContainmentTest extends TestCase {

	public static Test suite() {
		return new TestSuite(QueryContainmentTest.class, QueryContainmentTest.class.getSimpleName());
	}

	/**
	 * Tests whether one query is contained within another query.
	 * 
	 * @throws Exception 
	 */
	public void testQueryContaiment() throws Exception {
		final String prog =
			"vehicle(?X) :- car(?X)." +
			"?-car(?x)." +
			"?-vehicle(?x).";
		
		Parser parser = new Parser();
		parser.parse( prog );
		List<IRule> rules = parser.getRules();
		List<IQuery> queries = parser.getQueries();

		final IQuery query1 = queries.get( 0 );
		final IQuery query2 = queries.get( 1 );
		
		final QueryContainment queryCont = new QueryContainment(rules);

		boolean result = queryCont.checkQueryContainment(query1, query2);
		
		assertTrue(result);
	}
	
	/**
	 * Tests whether one query is not contained within another query.
	 * 
	 * @throws Exception 
	 */
	public void testQueryContaiment2() throws Exception {
		final String prog =
			"vehicle(?X) :- car(?X)." +
			"?-vehicle(?x)." +
			"?-car(?x).";
		
		Parser parser = new Parser();
		parser.parse( prog );
		List<IRule> rules = parser.getRules();
		List<IQuery> queries = parser.getQueries();

		final IQuery query1 = queries.get( 0 );
		final IQuery query2 = queries.get( 1 );
		
		final QueryContainment queryCont = new QueryContainment(rules);

		boolean result = queryCont.checkQueryContainment(query1, query2);
		
		assertFalse(result);
	}
	
	/**
	 * Tests whether one query is contained within another query.
	 * 
	 * @throws Exception 
	 */
	public void testTransitiveQueryContaiment() throws Exception {
		final String prog = 
			"path(?X, ?Y) :- path(?X, ?Z), path(?Z, ?Y)." +
			"?-path(?X, ?Y)." +
			"?-path(?X, ?Z), path(?Z, ?Y).";
		
		Parser parser = new Parser();
		parser.parse( prog );
		List<IRule> rules = parser.getRules();
		List<IQuery> queries = parser.getQueries();

		final IQuery query1 = queries.get( 0 );
		final IQuery query2 = queries.get( 1 );
		
		final QueryContainment queryCont = new QueryContainment(rules);

		boolean result = queryCont.checkQueryContainment(query1, query2);
		
		assertFalse(result);
	}

	/**
	 * Tests whether one query is contained within another query.
	 * 
	 * @throws Exception 
	 */
	public void testTransitiveQueryContaiment2() throws Exception {
		final String prog = 
			"path(?X, ?Y) :- path(?X, ?Z), path(?Z, ?Y)." +
			"?-path(?X, ?Z), path(?Z, ?Y)." +
			"?-path(?X, ?Z1), path(?Z1, ?Y).";
		
		Parser parser = new Parser();
		parser.parse( prog );
		List<IRule> rules = parser.getRules();
		List<IQuery> queries = parser.getQueries();

		final IQuery query1 = queries.get( 0 );
		final IQuery query2 = queries.get( 1 );
		
		final QueryContainment queryCont = new QueryContainment(rules);

		boolean result = queryCont.checkQueryContainment(query1, query2);
		
		assertTrue(result);
	}

}
