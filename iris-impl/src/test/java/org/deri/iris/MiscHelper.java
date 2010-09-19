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
package org.deri.iris;

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.TERM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;

/**
 * <p>
 * Summary of some helpermethods to overcome the shortcommings of the basics and
 * term factory.
 * </p>
 * <p>
 * $Id$
 * </p>
 * 
 * @author Richard Pöttler
 * @version $Revision$
 */
public final class MiscHelper {

	private MiscHelper() {
		// prevent subclassing
	}

	/**
	 * Creates a tuple consisting of IStringTerms of the submitted strings
	 * 
	 * @param s
	 *            the Strings to add to the tuple
	 * @return the tuple
	 */
	public static ITuple createTuple(final String... s) {
		List<ITerm> termList = new LinkedList<ITerm>();
		for (String str : s) {
			if(str != null){
				termList.add(TERM.createString(str));
			} else {
				termList.add(null);
			}
		
		}
		return BASIC.createTuple(termList);
	}

	public static ITuple createTuple(final ITerm... t) {
		List<ITerm> termList = new LinkedList<ITerm>();
		for (ITerm term : t) {
			termList.add(term);
		}
		return BASIC.createTuple(termList);
	}

	/**
	 * Creates a positive literal out of a predicate name and a set of variable
	 * names.
	 * 
	 * @param pred
	 *            the predicate name
	 * @param vars
	 *            the variable names
	 * @return the constructed literal
	 * @throws NullPointerException
	 *             if the predicate name or the set of variable names is
	 *             {@code null}
	 * @throws NullPointerException
	 *             if the set of variable names contains {@code null}
	 * @throws IllegalArgumentException
	 *             if the name of the predicate is 0 characters long
	 */
	public static ILiteral createLiteral(final String pred,
			final String... vars) {
		return createLiteral(true, pred, vars);
	}

	/**
	 * Creates a positive literal out of a predicate name and a set of variable
	 * names.
	 * 
	 * @param pos <code>true</code> if the literal should be positive,
	 * otherwise <code>false</code>
	 * @param pred
	 *            the predicate name
	 * @param vars
	 *            the variable names
	 * @return the constructed literal
	 * @throws NullPointerException
	 *             if the predicate name or the set of variable names is
	 *             {@code null}
	 * @throws NullPointerException
	 *             if the set of variable names contains {@code null}
	 * @throws IllegalArgumentException
	 *             if the name of the predicate is 0 characters long
	 */
	public static ILiteral createLiteral(final boolean pos, final String pred,
			final String... vars) {
		if ((pred == null) || (vars == null)) {
			throw new NullPointerException(
					"The predicate and the vars must not be null");
		}
		if (pred.length() <= 0) {
			throw new IllegalArgumentException(
					"The predicate name must be longer than 0 chars");
		}
		if (Arrays.asList(vars).contains(null)) {
			throw new NullPointerException("The vars must not contain null");
		}

		return BASIC.createLiteral(pos, BASIC.createPredicate(pred,
				vars.length), BASIC.createTuple(new ArrayList<ITerm>(
				createVarList(vars))));
	}

	/**
	 * Creates a list of variables out of a list of strings.
	 * 
	 * @param vars
	 *            the variable names
	 * @return the list of correspoinding variables
	 * @throws NullPointerException
	 *             if the vars is null, or contains null
	 */
	public static List<ITerm> createVarList(final String... vars) {
		if ((vars == null) || Arrays.asList(vars).contains(null)) {
			throw new NullPointerException(
					"The vars must not be null and must not contain null");
		}
		final List<ITerm> v = new ArrayList<ITerm>(vars.length);
		for (final String var : vars) {
			v.add(TERM.createVariable(var));
		}
		return v;
	}

	/**
	 * Creates a atom with string constants.
	 * 
	 * @param symbol
	 *            the predicate symbol
	 * @param cons
	 *            the constants for this atom
	 * @return the computed atom
	 * @throws NullPointerException
	 *             if the symbol is {@code null}
	 * @throws IllegalArgumentException
	 *             if the predicate symbol is an empty stirng
	 * @throws NullPointerException
	 *             if the constans are {@code null}
	 */
	public static IAtom createFact(final String symbol, final String... cons) {
		if (symbol == null) {
			throw new NullPointerException("The symbol must not be null");
		}
		if (symbol.length() == 0) {
			throw new IllegalArgumentException(
					"The symbol must not be an empty string");
		}
		if ((cons == null) || (Arrays.asList(cons).contains(null))) {
			throw new NullPointerException(
					"The constanst must not be or contain null");
		}
		return BASIC.createAtom(BASIC.createPredicate(symbol, cons.length),
				createTuple(cons));
	}

	/**
	 * Compares two Collections according to a comparator.
	 * @param c0 the first collection
	 * @param c1 the second collection
	 * @param c the comparator
	 * @return <code>true</code> if the two collections are equal according
	 * to the comparator, otherwise <code>false</code>
	 * @throws NullPointerException if one collection is <code>null</code>
	 * @throws NullPointerException if the comparator is <code>null</code>
	 * @since 0.3
	 */
	public static <Type> boolean compare(final Collection<? extends Type> c0, final Collection<? extends Type> c1, final Comparator<Type> c) {
		if ((c0 == null) || (c1 == null)) {
			throw new NullPointerException("The collections must not be null");
		}
		if (c == null) {
			throw new NullPointerException("The comparator must not be null");
		}

		if (c0.size() != c1.size()) {
			return false;
		}

		final List<Type> l0 = new ArrayList<Type>(c0);
		final List<Type> l1 = new ArrayList<Type>(c1);
		Collections.sort(l0, c);
		Collections.sort(l1, c);

		for (final Iterator<Type> i0 = l0.iterator(), i1 = l1.iterator(); i0.hasNext(); ) {
			if (c.compare(i0.next(), i1.next()) != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Behaves like Perl's join function. Concats all elements of the
	 * colleciton separated by the given delimiter.
	 * @param d the delimiter to put between the elements
	 * @param c the collection from where to take the elements
	 * @return the constructed string
	 * @throws NullPointerException if the delimiter is <code>null</code>
	 * @throws NullPointerException if the collection is <code>null</code>
	 * @since 0.3
	 */
	public static String join(final String d, final Collection<? extends Object> c) {
		if (d == null) {
			throw new NullPointerException("The delimiter must not be null");
		}
		if (c == null) {
			throw new NullPointerException("The collection must not be null");
		}

		if (c.isEmpty()) {
			return "";
		}

		final StringBuilder b = new StringBuilder();
		boolean first = true;
		for (final Object o : c) {
			if( first )
				first = false;
			else
				b.append( d );
			b.append(o);
		}
		return b.toString();
	}
}
