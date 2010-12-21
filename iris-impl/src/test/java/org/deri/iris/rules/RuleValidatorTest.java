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
package org.deri.iris.rules;

import static org.deri.iris.factory.Factory.TERM;
import junit.framework.TestCase;

import org.deri.iris.api.basics.IRule;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;

public class RuleValidatorTest extends TestCase
{
	public void testSafeWithConstructedTerm() throws Exception
	{
		assertSafe( "p(?y) :- q(?x), f(?x)=?y." );
	}
	
	public void testSafeWithComplexConstructedTerm() throws Exception
	{
		assertSafe( "p(?z) :- q(?x), r(?y), f(?x, ?y)=?z." );
	}
	
	public void testUnsafeWithConstructedTerm() throws Exception
	{
		RuleValidator validator = validate( "p(?y) :- f(?x)=?y." );
		
		assertUnsafe( validator );
		assertContains( validator, "y" );
	}
	
	public void testUnsafeWithConstructedTerm2() throws Exception
	{
		RuleValidator validator = validate( "p(?y) :- ! f(?x)=?y." );
		
		assertUnsafe( validator );
		assertContains( validator, "y" );
	}
	
	public void testUnsafeWithConstructedTerm3() throws Exception
	{
		RuleValidator validator = validate( "p(?y) :- q(?x), ! f(?x)=?y." );
		
		assertUnsafe( validator );
		assertContains( validator, "y" );
	}
	
	public void testUnsafeWithComplexConstructedTerm() throws Exception
	{
		RuleValidator validator = validate( "p(?z) :- q(?x), r(?y), f(?x, ?y, ?w)=?z." );
		
		assertUnsafe( validator );
		assertContains( validator, "z" );
	}
	
	public void testSafeWithUnlimitedBodyVariable() throws Exception
	{
		assertSafe( "p(?x) :- q(?x), r(?y)." );
	}
	
	public void testSafeWithUnlimitedNegatedBodyVariable() throws Exception
	{
		assertSafe( "p(?x) :- q(?x), not r(?y)." );
	}
	
	public void testUnsafeWithNegatedBodyVariableOnly() throws Exception
	{
		RuleValidator validator = validate( "p(?x) :- not q(?x)." );
		
		assertUnsafe( validator );
		assertContains( validator, "x" );
	}
	
	public void testUnsafeWithUnlimitedNegatedBodyVariable() throws Exception
	{
		RuleValidator validator = validate( "p(?x) :- q(?x), not r(?y).", false, true );
		
		assertUnsafe( validator );
		assertContains( validator, "y" );
	}
	
	public void testSafeWithComputedEqualityArgument() throws Exception
	{
		assertSafe( "p(?y) :- q(?x), ?x = ?y." );
	}

	public void testSafeWithComputedEqualityArgumentAndNoArithmeticImplications() throws Exception
	{
		RuleValidator validator = validate( "p(?y) :- q(?x), ?x = ?y.", true, false );
		
		assertSafe( validator );
	}
	
	public void testSafeWithComputedArithmeticArgument() throws Exception
	{
		assertSafe( "p(?y) :- q(?x), ?x + 1 = ?y." );
	}

	public void testUnsafeWithComputedArithmeticArgument() throws Exception
	{
		RuleValidator validator = validate( "p(?y) :- q(?x), ?x + 1 = ?y.", true, false );
		
		assertUnsafe( validator );
		assertContains( validator, "y" );
	}
	
	public void testUnsafeWithNegatedComputedArithmeticArgument() throws Exception
	{
		RuleValidator validator = validate( "p(?y) :- q(?x), ! ?x + 1 = ?y.", true, false );
		
		assertUnsafe( validator );
		assertContains( validator, "y" );
	}
	

	public void testUnsafeWithUnlimitedHeadVariable() throws Exception
	{
		IRule rule = parseRule( "p(?y) :- q(?x)." );
		
		RuleValidator validator = new RuleValidator( rule, true, true );
		
		assertUnsafe( validator );
		assertContains( validator, "y" );
	}
	
	private static void assertContains( RuleValidator validator, String variable )
	{
		assertTrue( validator.getAllUnlimitedVariables().contains( TERM.createVariable( variable ) ) );
	}
	
	private static void assertSafe( String rule ) throws ParserException
	{
		assertSafe( validate( rule ) );
	}
	
	private static void assertSafe( RuleValidator validator )
	{
		assertTrue( validator.getAllUnlimitedVariables().size() == 0 );
	}
	
	private static void assertUnsafe( RuleValidator validator )
	{
		assertTrue( validator.getAllUnlimitedVariables().size() > 0 );
	}

	
	
	private static RuleValidator validate( String rule, boolean allowNotLimitedVariablesInNegatedSubGoals, boolean allowArithmeticPredicatesToImplyLimited ) throws ParserException
	{
		return new RuleValidator( parseRule( rule ), allowNotLimitedVariablesInNegatedSubGoals, allowArithmeticPredicatesToImplyLimited );
	}
	
	private static RuleValidator validate( String rule ) throws ParserException
	{
		return new RuleValidator( parseRule( rule ), true, true );
	}

	private static IRule parseRule(String program) throws ParserException
	{
		assert program != null;

		final Parser parser = new Parser();
		parser.parse(program);

		assert parser.getRules().size() == 1;

		return parser.getRules().get(0);
	}
}
