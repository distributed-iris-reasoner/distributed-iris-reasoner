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
package org.deri.iris.builtins;

import java.util.HashSet;
import java.util.Set;

import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.storage.IRelation;

class ExecutionHelper
{
	/**
	 * Eexecutes a program and print results.
	 * 
	 * @param p	A program to be evaluated.
	 */
	public static void executeTest(final IKnowledgeBase p, IQuery q, IRelation res)throws Exception{
		System.out.println("--- input ---");
		for (final IRule rule : p.getRules()) {
			System.out.println(rule);
		}
		System.out.println("--- facts ---");
//		System.out.println(p.keySet());
//		for (final IPredicate pred : p.getPredicates()) {
//			System.out.printf("%s -> %s\n", pred.getPredicateSymbol(), p
//					.getFacts(pred));
//			for (ITuple t : p.getFacts(pred)) {
//				System.out.println(pred.getPredicateSymbol() + t);
//			}
//		}
		
//		IExpressionEvaluator method = new ExpressionEvaluator();
//		IExecutor exec = new Executor(p, method);
//		exec.execute();
		System.out.println("Result: ");
		IRelation actual = p.execute( q );
//		Map<IPredicate, IMixedDatatypeRelation> results = exec.computeSubstitutions();
//		ProgramTest.printResults(results);
		
//		assertTrue(results.get(results.keySet().iterator().next()).containsAll(res));
		junit.framework.Assert.assertTrue(same(res, actual ));
	}
		
	public static boolean same( IRelation actualResults, IRelation expectedResults )
	{
		Set<ITuple> actual = new HashSet<ITuple>();
		Set<ITuple> expected = new HashSet<ITuple>();
		
		for( int t = 0; t < actualResults.size(); ++t )
			actual.add( actualResults.get( t ) );
		
		for( int t = 0; t < expectedResults.size(); ++t )
			expected.add( expectedResults.get( t ) );
		
		return actual.equals( expected );
	}

}
