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
package org.deri.iris.rules.safety;

import junit.framework.TestCase;

import org.deri.iris.RuleUnsafeException;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;

public class StandardRuleSafetyProcessorTest extends TestCase
{
	StandardRuleSafetyProcessor mProcessor;

	@Override
    protected void setUp() throws Exception
    {
		mProcessor = new StandardRuleSafetyProcessor( true, true );
    }
	
	public void testSimpleSafe() throws Exception
	{
		IRule rule = makeRule( "p(?x) :- q(?x)." );
		
		mProcessor.process( rule );
	}
	
	public void testConstantsInHeadAndEmptyBodySafe() throws Exception
	{
		IRule rule = makeRule( "p(2) :- ." );
		
		mProcessor.process( rule );
	}
	
	public void testConstantsInHeadAndNegationInBodySafe() throws Exception
	{
		IRule rule = makeRule( "p(2) :- not q(3)." );
		
		mProcessor.process( rule );
	}
	
	public void testConstantsInHeadAndNegationWithVariablesInBodySafe() throws Exception
	{
		IRule rule = makeRule( "p(2) :- not q(?x)." );
		
		mProcessor.process( rule );
	}
	
	public void testThroughEqualitySafe() throws Exception
	{
		IRule rule = makeRule( "p(?y) :- q(?x), ?x = ?y." );
		
		mProcessor.process( rule );
	}
	
	public void testThroughInequalityUnSafe() throws Exception
	{
		IRule rule = makeRule( "p(?y) :- q(?x), ?x != ?y." );
		
		checkUnsafe( rule );
	}
	
	public void testArithmeticTernarySafe() throws Exception
	{
		IRule rule = makeRule( "p(?z) :- q(?x, ?y), ?x + ?y = ?z." );
		
		mProcessor.process( rule );
	}
	
	public void testArithmeticTernaryUnSafe() throws Exception
	{
		IRule rule = makeRule( "p(?z) :- q(?x, ?y), ?x + ?y = ?z." );

		mProcessor = new StandardRuleSafetyProcessor( true, false );
		checkUnsafe( rule );
	}
	
	public void testSimpleUnSafe() throws Exception
	{
		IRule rule = makeRule( "p(?x) :- q(?y)." );
		
		checkUnsafe( rule );
	}

	public void testNegatedVariablesSafe() throws Exception
	{
		IRule rule = makeRule( "p(?x) :- q(?x), not r(?y)." );
		
		mProcessor.process( rule );
	}

	public void testNegatedVariablesUnSafe() throws Exception
	{
		IRule rule = makeRule( "p(?x) :- q(?x), not r(?y)." );
		
		mProcessor = new StandardRuleSafetyProcessor( false, true );
		checkUnsafe( rule );
	}

	private void checkUnsafe( IRule rule )
	{
		try
		{
			mProcessor.process( rule );
			fail( "RuleUnsafeException not thrown." );
		}
		catch( RuleUnsafeException e )
		{
		}
	}
	
	private IRule makeRule( String strRule ) throws ParserException
	{
		Parser parser = new Parser();
		parser.parse( strRule );
		
		return parser.getRules().get( 0 );
	}
}
