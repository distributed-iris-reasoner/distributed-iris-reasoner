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

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.CONCRETE;
import static org.deri.iris.factory.Factory.TERM;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IConstructedTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.builtins.EqualBuiltin;

public class RuleManipulatorTest extends TestCase
{
	ILiteral createLiteral( boolean positive, String predicateName, Object ... termObjects )
	{
		List<ITerm> terms = new ArrayList<ITerm>();

		for( Object o : termObjects )
		{
			if( o instanceof Integer )
				terms.add( CONCRETE.createInteger( (Integer) o ) );
			else if( o instanceof String )
				terms.add( TERM.createVariable( (String) o ) );
		}
		
		if( predicateName.equals( "=" ) )
			return BASIC.createLiteral( positive,
							new EqualBuiltin( terms.toArray( new ITerm[ 2 ] ) ) );
		
		return BASIC.createLiteral( positive, 
									BASIC.createAtom(
										BASIC.createPredicate( predicateName, terms.size() ), 
										BASIC.createTuple( terms )
								)
							);
	}

	final ITerm X = TERM.createVariable("X");
	final ITerm X1 = TERM.createVariable("X1");
	
	public void testReplaceTermInAtom() {
		RuleManipulator rm = new RuleManipulator();
		
		// f(?X)
		IAtom atom = BASIC.createAtom(BASIC.createPredicate("f", 1), BASIC.createTuple(X));
		
		// Replace the ?X in f(?X)...
		IAtom newAtom = rm.replace(atom, X, X1);
		
		// to get f(?X1)		
		assertEquals("f(?X1)", newAtom.toString());
	}
	
	public void testReplaceTermInAtomWithConstructedTerm() {
		RuleManipulator rm = new RuleManipulator();
		
		// q( f(?X), ?X )
		IConstructedTerm constructedTerm = TERM.createConstruct("f", X);
		IAtom atom = BASIC.createAtom(BASIC.createPredicate("q", 2), BASIC.createTuple(constructedTerm, X));
		
		// Replace the f(?X) in q( f(?X), ?X ) ...
		IConstructedTerm fX = TERM.createConstruct("f", X);
		IConstructedTerm fX1 = TERM.createConstruct("f", X1);
		IAtom newAtom = rm.replace(atom, fX, fX1);
		
		// to get q( f(?X1), ?X )
		assertEquals("q(f(?X1), ?X)", newAtom.toString());
	}
	
	public void testReplaceTermInAtomWithDoubleConstructedTerm() {
		RuleManipulator rm = new RuleManipulator();
		
		// q( f(g(?X)) )
		IConstructedTerm constructedTerm = TERM.createConstruct("f", TERM.createConstruct("g", X));
		IAtom atom = BASIC.createAtom(BASIC.createPredicate("q", 1), BASIC.createTuple(constructedTerm));
		
		// Replace the f(g(?X)) in q( f(g(?X)) ) ...
		IConstructedTerm fgX = TERM.createConstruct("f", TERM.createConstruct("g", X));
		IConstructedTerm fgX1 = TERM.createConstruct("f", TERM.createConstruct("g", X1));
		IAtom newAtom = rm.replace(atom, fgX, fgX1);
		
		// to get q( f(g(?X1)) )
		assertEquals("q(f(g(?X1)))", newAtom.toString());
	}
	
	public void testReplaceTermInAtomWithDoubleConstructedTermNestedReplace() {
		RuleManipulator rm = new RuleManipulator();
		
		// q( f(g(?X)), g(?X) )
		IConstructedTerm constructedTerm = TERM.createConstruct("f", TERM.createConstruct("g", X));
		IAtom atom = BASIC.createAtom(BASIC.createPredicate("q", 2), BASIC.createTuple(constructedTerm, TERM.createConstruct("g", X)) );
		
		// Replace the g(?X) in q( f(g(?X)), g(?X) ) ...
		IConstructedTerm gX = TERM.createConstruct("g", X);
		IConstructedTerm gX1 = TERM.createConstruct("g", X1);
		IAtom newAtom = rm.replace(atom, gX, gX1);
		
		// to get q( f(g(?X1)), g(?X1) )
		assertEquals("q(f(g(?X1)), g(?X1))", newAtom.toString());
	}
	
	public void testReplaceTermInAtomWithConstructedTermNoReplace() {
		RuleManipulator rm = new RuleManipulator();
		
		// q( f(?X), ?X )
		IConstructedTerm constructedTerm = TERM.createConstruct("f", X);
		IAtom atom = BASIC.createAtom(BASIC.createPredicate("q", 2), BASIC.createTuple(constructedTerm, X));
		
		// Try to replace g(?X) in q( f(?X), ?X ) ...
		IConstructedTerm gX = TERM.createConstruct("g", X);
		IConstructedTerm gX1 = TERM.createConstruct("g", X1);
		IAtom newAtom = rm.replace(atom, gX, gX1);
		
		// the result should not be different from the original
		assertEquals(atom.toString(), newAtom.toString());
	}
	
	public void testAll()
	{
		List<ILiteral> head = new ArrayList<ILiteral>();
		
		// p(X,Y)
		head.add( createLiteral( true, "p", "X", "Y" ) );

		// r(X)
		List<ILiteral> body = new ArrayList<ILiteral>();

		body.add( createLiteral( true, "r", "X" ) );

		// not p(Z,Y)
		body.add( createLiteral( false, "p", "Z", "Y" ) );

		// X=2
		body.add( createLiteral( true, "=", "X", 2 ) );
		
		// Z=1
		body.add( createLiteral( true, "=", "Z", 1 ) );
		
		IRule rule = BASIC.createRule( head, body );
		
		//LocalStratifier ls = new LocalStratifier
		RuleManipulator rm = new RuleManipulator();
		
		IRule rule2 = rm.replaceVariablesWithConstants( rule, true );
		IRule rule3 = rm.removeUnnecessaryEqualityBuiltins( rule2 );

		IRule rule4 = rm.addEquality( rule3, TERM.createVariable( "Y" ), CONCRETE.createInteger( 3 ) );

		IRule rule5 = rm.replaceVariablesWithConstants( rule4, false );
		IRule rule6 = rm.removeUnnecessaryEqualityBuiltins( rule5 );

		ILiteral h1 = rule6.getHead().get( 0 );
		assertEquals( h1, createLiteral( true, "p", 2, 3 ) );
		
		ILiteral b1 = rule6.getBody().get( 0 );
		assertEquals( b1, createLiteral( true, "r", 2 ) );

		ILiteral b2 = rule6.getBody().get( 1 );
		assertEquals( b2, createLiteral( false, "p", 1, 3 ) );

		IRule rule7 = rm.addBodyLiteral( rule6, createLiteral( false, "p", 1, 3 ) );
		IRule rule8 = rm.removeDuplicateLiterals( rule7 );
		
		assertEquals( rule8, rule6 );
	}
}
