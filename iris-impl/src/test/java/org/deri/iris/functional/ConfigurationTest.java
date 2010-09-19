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

import junit.framework.TestCase;

import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
import org.deri.iris.KnowledgeBaseFactory;

public class ConfigurationTest extends TestCase
{
	public void testDivideByZeroDiscradAndIgnore() throws Exception
	{
		String program =
			"a(0)." +
			"a(1)." +
			"b(0)." +
			"d(?Z) :- a(?X), b(?Y), ?X / ?Y = ?Z." +
			"?-d(?X).";
		
		Helper.evaluateSemiNaive( program, "" );
	}

	public void testDivideByZeroStop() throws Exception
	{
		String program =
			"a(0)." +
			"a(1)." +
			"b(0)." +
			"d(?Z) :- a(?X), b(?Y), ?X / ?Y = ?Z." +
			"?-d(?X).";
		
		try
		{
			Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();
			configuration.evaluationDivideByZeroBehaviour = Configuration.DivideByZeroBehaviour.STOP;
			
			Helper.executeAndCheckResults( program, "", configuration, "Divide by zero should stop" );

			fail( "Should have thrown an EvaluationException" );
		}
		catch( EvaluationException e )
		{
			
		}
	}
}
