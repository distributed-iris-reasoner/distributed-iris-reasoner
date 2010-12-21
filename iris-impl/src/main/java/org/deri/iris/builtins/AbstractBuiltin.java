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

import static org.deri.iris.factory.Factory.BASIC;

import java.util.Arrays;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.builtins.IBuiltinAtom;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.factory.Factory;
import org.deri.iris.utils.equivalence.IEquivalentTerms;
import org.deri.iris.utils.equivalence.IgnoreTermEquivalence;

/**
 * <p>
 * Serves as skeleton implementation for builtins. If you use this class as
 * superclass, you only have only to implement the evaluate method.
 * <p>
 * $Id: AbstractBuiltin.java,v 1.11 2007-10-19 07:37:16 poettler_ric Exp $
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri dot org)
 * @version $Revision: 1.11 $
 */
public abstract class AbstractBuiltin implements IBuiltinAtom {

	/** Holds the inner atom. */
	private IAtom a;
	
	private IEquivalentTerms equivalenceClasses = new IgnoreTermEquivalence();

	/**
	 * Constructs the builtin. More precisely it constructs the inner atom. The
	 * number of terms submitted to this constructor must match the arity of the
	 * predicate.
	 * 
	 * @param p
	 *            the special predicate for this builtin
	 * @param t
	 *            the terms defining the values and variables for this builtin
	 * @throws NullPointerException
	 *             if the perdicate or the terms is {@code null}
	 * @throws NullPointerException
	 *             if the terms contain {@code null}
	 * @throws IllegalArgumentException
	 *             if the length of the terms and the arity of the perdicate
	 *             doesn't match
	 */
	protected AbstractBuiltin(final IPredicate p, final ITerm... t) {
		if ((p == null) || (t == null)) {
			throw new NullPointerException(
					"The predicate and the terms must not be null");
		}
		if (Arrays.asList(t).contains(null)) {
			throw new NullPointerException("The terms must not contain null");
		}
		if (t.length != p.getArity()) {
			throw new IllegalArgumentException("The amount of terms <" + t.length + 
					"> must match the arity of the predicate <" + p.getArity() + ">");
		}
		this.a = Factory.BASIC.createAtom(p, Factory.BASIC.createTuple(t));
	}

	public IPredicate getPredicate() {
		return a.getPredicate();
	}

	public ITuple getTuple() {
		return a.getTuple();
	}

	public boolean isGround() {
		return a.isGround();
	}

	public int compareTo(IAtom o) {
		return a.compareTo(o);
	}

	/**
	 * <p>
	 * Returns a short description of the inner atom. <b>The format of the
	 * returned String is undocumented and subject to change.</b>
	 * </p>
	 * <p>
	 * An example String could be: <code>EQUALS(A, B)</code>
	 * </p>
	 * 
	 * @return the short description
	 */
	public String toString() {
		return a.toString();
	}

	public int hashCode() {
		return a.hashCode();
	}

	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof AbstractBuiltin)) {
			return false;
		}
		AbstractBuiltin ab = (AbstractBuiltin) o;
		return a.equals(ab.a);
	}

	public boolean isBuiltin() {
		return true;
	}
	
	public ITuple evaluate(final ITuple t) throws EvaluationException
	{
		if(t == null)
			throw new NullPointerException("The collection must not be null");

		// calculating the needed term indexes from the submitted tuple
		int[] outstanding = BuiltinHelper.determineUnground(getTuple());
		
		// retrieving the constants of this builtin
		final ITerm[] bCons = BuiltinHelper.getIndexes(getTuple(), 
				BuiltinHelper.complement(outstanding, getTuple().size()));

		// putting the term from this builtin and the submitted tuple together
		final ITerm[] complete = BuiltinHelper.concat(outstanding, 
				BuiltinHelper.getIndexes(t, outstanding), bCons);
		
		// determining the remaining vars of the terms
		final int[] vars = BuiltinHelper.determineUnground(Arrays.asList(complete));
		
		if( vars.length > maxUnknownVariables() )
			throw new IllegalArgumentException( "Can not evaluate " + getPredicate().toString() +
							" with more than " + maxUnknownVariables() + " unbound variables (had " + vars.length + ")." );
			
		ITerm result = evaluateTerms( complete, vars );

		if( result == null )
			return null;
		
		if( result == EMPTY_TERM )
			return BuiltinHelper.EMPTY_TUPLE;
		
		return BASIC.createTuple( result );
	}
	
	/**
	 * Evaluate the predicate once the terms and variable indexes have been found.
	 * 
	 * @param terms The array of all terms for this evaluation.
	 * @param variableIndexes the indexes of the terms which should be
	 * computed (starting at 0)
	 * @return The result of the evaluation.
	 * @throws EvaluationException 
	 */
	protected ITerm evaluateTerms( ITerm[] terms, int[] variableIndexes ) throws EvaluationException
	{
		return null;
	}
	
	public int maxUnknownVariables()
	{
		return 0;
	}
	
	@Override
	public void setEquivalenceClasses(IEquivalentTerms equivalenceClasses) {
		if (equivalenceClasses == null) {
			throw new NullPointerException(
					"The equivalence classes must not be null");
		}
		
		this.equivalenceClasses = equivalenceClasses;
	}
	
	@Override
	public IEquivalentTerms getEquivalenceClasses() {
		return equivalenceClasses;
	}

	/** Something to save creating an an empty tuple every time we just need 'any' tuple. */
	protected static final ITerm EMPTY_TERM = Factory.TERM.createString( "" );
}
