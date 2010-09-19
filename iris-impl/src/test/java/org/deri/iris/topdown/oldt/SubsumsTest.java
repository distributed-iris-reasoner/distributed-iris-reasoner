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

import junit.framework.TestCase;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.compiler.ParserException;
import org.deri.iris.factory.Factory;
import org.deri.iris.utils.TermMatchingAndSubstitution;

/**
 * 
 * Test class for TermMatchingAndSubstitution.subsums(atom1, atom2)
 * which is used by OLDT evaluation function.
 * 
 * @author gigi
 * @see TermMatchingAndSubstitution.subsums( IAtom atom1, IAtom atom2 )
 */
public class SubsumsTest extends TestCase {

	private static final IStringTerm STR_B = Factory.TERM.createString("b");
	private static final IStringTerm STR_A = Factory.TERM.createString("a");
	private static final IVariable VAR_B = Factory.TERM.createVariable("B");
	private static final IVariable VAR_A = Factory.TERM.createVariable("A");
	private static final IVariable VAR_Y = Factory.TERM.createVariable("Y");
	private static final IVariable VAR_X = Factory.TERM.createVariable("X");

	private ITuple emptyTuple = Factory.BASIC.createTuple();
	private ITuple tupleX = Factory.BASIC.createTuple(VAR_X);
	private ITuple tupleY = Factory.BASIC.createTuple(VAR_Y);
	private ITuple tupleXY = Factory.BASIC.createTuple(VAR_X, VAR_Y);
	private ITuple tupleYX = Factory.BASIC.createTuple(VAR_Y, VAR_X);
	private ITuple tupleAB = Factory.BASIC.createTuple(VAR_A, VAR_B);
	
	private ITuple tupleab = Factory.BASIC.createTuple(STR_A, STR_B);
	private ITuple tupleaB = Factory.BASIC.createTuple(STR_A, VAR_B);
	private ITuple tuplebB = Factory.BASIC.createTuple(STR_B, VAR_B);
	private ITuple tupleBa = Factory.BASIC.createTuple(VAR_B, STR_A);
	private ITuple tupleaX = Factory.BASIC.createTuple(STR_A, VAR_X);

	private ITuple tupleFX = Factory.BASIC.createTuple(
			Factory.TERM.createConstruct("f",
					Factory.BASIC.createTuple(
							VAR_X))
			);
	
	private ITuple tupleFa = Factory.BASIC.createTuple(
			Factory.TERM.createConstruct("f",
					Factory.BASIC.createTuple(
							STR_A))
			);

	

	/*
	 * True Outcome
	 */
	
	/**
	 * p(). p().
	 * @throws ParserException
	 */
	public void testSamePredicate() throws ParserException {		
		IPredicate predicate = Factory.BASIC.createPredicate("p", 0);
		ILiteral lit1 = Factory.BASIC.createLiteral(true, predicate, emptyTuple);
		ILiteral lit2 = Factory.BASIC.createLiteral(true, predicate, emptyTuple);
		
		boolean actual = TermMatchingAndSubstitution.subsums(lit1.getAtom(), lit2.getAtom());
		
		assertEquals(true, actual);
	}
	
	/**
	 * p(?X, ?Y). p(?X, ?Y).
	 * @throws ParserException
	 */
	public void testSamePredicateSameAritySameVariables() throws ParserException {		
		IPredicate predicate = Factory.BASIC.createPredicate("p", 2);
		ILiteral lit1 = Factory.BASIC.createLiteral(true, predicate, tupleXY);
		ILiteral lit2 = Factory.BASIC.createLiteral(true, predicate, tupleXY);
		
		boolean actual = TermMatchingAndSubstitution.subsums(lit1.getAtom(), lit2.getAtom());
		
		assertEquals(true, actual);
	}
	
	/**
	 * p(?X, ?Y). p(?Y, ?X).
	 * @throws ParserException
	 */
	public void testSamePredicateSameAritySameVariablesSwitched() throws ParserException {		
		IPredicate predicate = Factory.BASIC.createPredicate("p", 2);
		ILiteral lit1 = Factory.BASIC.createLiteral(true, predicate, tupleXY);
		ILiteral lit2 = Factory.BASIC.createLiteral(true, predicate, tupleYX);
		
		boolean actual = TermMatchingAndSubstitution.subsums(lit1.getAtom(), lit2.getAtom());
		
		assertEquals(true, actual);
	}
	
	/**
	 * p(?X, ?Y). p(?A, ?B).
	 * @throws ParserException
	 */
	public void testSamePredicateSameArityDifferentVariables() throws ParserException {
		IPredicate predicate = Factory.BASIC.createPredicate("p", 2);
		ILiteral lit1 = Factory.BASIC.createLiteral(true, predicate, tupleXY);
		ILiteral lit2 = Factory.BASIC.createLiteral(true, predicate, tupleAB);
		
		boolean actual = TermMatchingAndSubstitution.subsums(lit1.getAtom(), lit2.getAtom());
		
		assertEquals(true, actual);
	}
	
	/**
	 * p('a', 'b'). p('a', 'b').
	 * @throws ParserException
	 */
	public void testSamePredicateSameAritySameGrounded() throws ParserException {		
		IPredicate predicate = Factory.BASIC.createPredicate("p", 2);
		
		ILiteral lit1 = Factory.BASIC.createLiteral(true, predicate, tupleab);
		ILiteral lit2 = Factory.BASIC.createLiteral(true, predicate, tupleab);
		
		boolean actual = TermMatchingAndSubstitution.subsums(lit1.getAtom(), lit2.getAtom());
		
		assertEquals(true, actual);
	}
	
	/**
	 * p(?X, ?Y). p('a', 'b').
	 * @throws ParserException
	 */
	public void testSamePredicateSameArityVariablesAndGrounded() throws ParserException {		
		IPredicate predicate = Factory.BASIC.createPredicate("p", 2);
		
		ILiteral lit1 = Factory.BASIC.createLiteral(true, predicate, tupleAB);
		ILiteral lit2 = Factory.BASIC.createLiteral(true, predicate, tupleab);
		
		boolean actual = TermMatchingAndSubstitution.subsums(lit1.getAtom(), lit2.getAtom());
		
		assertEquals(false, actual);
	}
	
	
	/**
	 * p(f(?X)). p(f(?X)).
	 * @throws ParserException
	 */
	public void testSamePredicateSameAritySameConstructedTerm() throws ParserException {		
		IPredicate predicate = Factory.BASIC.createPredicate("p", 1);
		
		ILiteral lit1 = Factory.BASIC.createLiteral(true, predicate, tupleFX);
		ILiteral lit2 = Factory.BASIC.createLiteral(true, predicate, tupleFX);
		
		boolean actual = TermMatchingAndSubstitution.subsums(lit1.getAtom(), lit2.getAtom());
		
		assertEquals(true, actual);
	}
	
	/**
	 * p(f(?X)). p(f('a')).
	 * @throws ParserException
	 */
	public void testSamePredicateSameArityConstructedTermAndGroundedConstructedTerm() throws ParserException {		
		IPredicate predicate = Factory.BASIC.createPredicate("p", 1);
		
		ILiteral lit1 = Factory.BASIC.createLiteral(true, predicate, tupleFX);
		ILiteral lit2 = Factory.BASIC.createLiteral(true, predicate, tupleFa);
		
		boolean actual = TermMatchingAndSubstitution.subsums(lit1.getAtom(), lit2.getAtom());
		
		assertEquals(false, actual);
	}

	/**
	 * p(?Y). p(f(?X)).
	 * @throws ParserException
	 */
	public void testSamePredicateSameArityConstructedTermAndGrounded() throws ParserException {		
		IPredicate predicate = Factory.BASIC.createPredicate("p", 1);
		
		ILiteral lit1 = Factory.BASIC.createLiteral(true, predicate, tupleY);
		ILiteral lit2 = Factory.BASIC.createLiteral(true, predicate, tupleFX);
		
		boolean actual = TermMatchingAndSubstitution.subsums(lit1.getAtom(), lit2.getAtom());
		
		assertEquals(true, actual);
	}
	
	/*
	 * False Outcome
	 */
	
	/**
	 * p(?X, ?Y). s(?X, ?Y).
	 */
	public void testDifferentPredicateSameAritySameVariables() throws ParserException {
		IPredicate pred1 = Factory.BASIC.createPredicate("p", 2);
		IPredicate pred2 = Factory.BASIC.createPredicate("s", 2);
		ILiteral lit1 = Factory.BASIC.createLiteral(true, pred1, tupleXY);
		ILiteral lit2 = Factory.BASIC.createLiteral(true, pred2, tupleAB);
		
		boolean actual = TermMatchingAndSubstitution.subsums(lit1.getAtom(), lit2.getAtom());
		
		assertEquals(false, actual);
	}
	
	/**
	 * p(?X, ?Y). p(?X).
	 * @throws ParserException
	 */
	public void testSamePredicateDifferentArity() throws ParserException {
		IPredicate pred1 = Factory.BASIC.createPredicate("p", 1);
		IPredicate pred2 = Factory.BASIC.createPredicate("p", 2);
		ILiteral lit1 = Factory.BASIC.createLiteral(true, pred1, tupleX);
		ILiteral lit2 = Factory.BASIC.createLiteral(true, pred2, tupleXY);
		
		boolean actual = TermMatchingAndSubstitution.subsums(lit1.getAtom(), lit2.getAtom());
		
		assertEquals(false, actual);
	}
	
	/**
	 * p('a', 'b'). p(?X, ?Y).
	 * @throws ParserException
	 */
	public void testSamePredicateSameArityGroundedAndVariables() throws ParserException {
		IPredicate predicate = Factory.BASIC.createPredicate("p", 2);
		
		ILiteral lit1 = Factory.BASIC.createLiteral(true, predicate, tupleab);
		ILiteral lit2 = Factory.BASIC.createLiteral(true, predicate, tupleAB);
		
		boolean actual = TermMatchingAndSubstitution.subsums(lit1.getAtom(), lit2.getAtom());
		
		assertEquals(false, actual);
	}
	
	/**
	 * p(f('a')). p(f(?X)).
	 * @throws ParserException
	 */
	public void testSamePredicateSameArityGroundedAndConstructedTerm() throws ParserException {		
		IPredicate predicate = Factory.BASIC.createPredicate("p", 1);
		
		ILiteral lit1 = Factory.BASIC.createLiteral(true, predicate, tupleFa);
		ILiteral lit2 = Factory.BASIC.createLiteral(true, predicate, tupleFX);
		
		boolean actual = TermMatchingAndSubstitution.subsums(lit1.getAtom(), lit2.getAtom());
		
		assertEquals(false, actual);
	}
	
	/**
	 * p('a', ?B). p(?X, ?Y).
	 * @throws ParserException
	 */
	public void testSamePredicateSameArityOneGroundedAndTwoVariables() throws ParserException {
		IPredicate predicate = Factory.BASIC.createPredicate("p", 2);
		
		ILiteral lit1 = Factory.BASIC.createLiteral(true, predicate, tupleaB);
		ILiteral lit2 = Factory.BASIC.createLiteral(true, predicate, tupleXY);
		
		boolean actual = TermMatchingAndSubstitution.subsums(lit1.getAtom(), lit2.getAtom());
		
		assertEquals(false, actual);
	}
	
	/**
	 * p(?X, ?Y). p('a', ?B).
	 * Same as above test, but with switched literals
	 * @throws ParserException
	 */
	public void testSamePredicateSameArityTwoVariablesAndOneGrounded() throws ParserException {
		IPredicate predicate = Factory.BASIC.createPredicate("p", 2);
		
		ILiteral lit1 = Factory.BASIC.createLiteral(true, predicate, tupleXY);
		ILiteral lit2 = Factory.BASIC.createLiteral(true, predicate, tupleaB);
		
		boolean actual = TermMatchingAndSubstitution.subsums(lit1.getAtom(), lit2.getAtom());
		
		assertEquals(false, actual);
	}
	
	/**
	 * p('a', ?B). p(?B, 'a').
	 * @throws ParserException
	 */
	public void testSamePredicateSameArityOneGroundedAndOneVariableSwitched() throws ParserException {
		IPredicate predicate = Factory.BASIC.createPredicate("p", 2);
		
		ILiteral lit1 = Factory.BASIC.createLiteral(true, predicate, tupleaB);
		ILiteral lit2 = Factory.BASIC.createLiteral(true, predicate, tupleBa);
		
		boolean actual = TermMatchingAndSubstitution.subsums(lit1.getAtom(), lit2.getAtom());
		
		assertEquals(false, actual);
	}
	
	/**
	 * p('a', ?B). p('b', ?B).
	 * @throws ParserException
	 */
	public void testSamePredicateSameArityOneGroundedAndOneVariableFalse() throws ParserException {
		IPredicate predicate = Factory.BASIC.createPredicate("p", 2);
		
		ILiteral lit1 = Factory.BASIC.createLiteral(true, predicate, tupleaB);
		ILiteral lit2 = Factory.BASIC.createLiteral(true, predicate, tuplebB);
		
		boolean actual = TermMatchingAndSubstitution.subsums(lit1.getAtom(), lit2.getAtom());
		
		assertEquals(false, actual);
	}
	
	/**
	 * p('a', ?B). p('a', ?X).
	 * @throws ParserException
	 */
	public void testSamePredicateSameArityOneGroundedAndOneVariableTrue() throws ParserException {
		IPredicate predicate = Factory.BASIC.createPredicate("p", 2);
		
		ILiteral lit1 = Factory.BASIC.createLiteral(true, predicate, tupleaB);
		ILiteral lit2 = Factory.BASIC.createLiteral(true, predicate, tupleaX);
		
		boolean actual = TermMatchingAndSubstitution.subsums(lit1.getAtom(), lit2.getAtom());
		
		assertEquals(true, actual);
	}
	
}
