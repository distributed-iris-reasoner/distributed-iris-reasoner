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
package org.deri.iris.api.factory;

import java.util.List;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;

/**
 * <p>
 * An interface that can be used to create set of basic logical entities 
 * such as predicates, atoms, rules, queries etc.
 * </p>
 * <p>
 * $Id: IBasicFactory.java,v 1.19 2007-10-30 09:15:07 bazbishop237 Exp $
 * </p>
 * @author Darko Anicic, DERI Innsbruck
 * @author Richard Pöttler (richard dot poettler at deri dot org)
 * @version $Revision: 1.19 $
 */
public interface IBasicFactory {

	public IAtom createAtom(IPredicate p, ITuple tuple);

	/**
	 * Creates a deep copy of an atom. The terms themsemves will remain 
	 * the same instances.
	 * @param a the atom to copy
	 * @return the copy
	 * @throws NullPointerException if the atom is <code>null</code>
	 * @throws IllegalArgumentException if the atom is a builtin one.
	 * @since 0.3
	 */
	public IAtom createAtom(final IAtom a);

	public ILiteral createLiteral(boolean isPositive, IAtom atom);
	public ILiteral createLiteral(boolean isPositive, IPredicate predicate,
			ITuple tuple);

	/**
	 * Creates a deep copy of a literal. The terms themsemves will remain 
	 * the same instances.
	 * @param l the tuple to copy
	 * @return the copy
	 * @throws NullPointerException if the literal is <code>null</code>
	 * @throws IllegalArgumentException if the underlying atom is a builtin one.
	 * @since 0.3
	 */
	public ILiteral createLiteral(final ILiteral l);
	
	public IPredicate createPredicate(String symbol, int arity);
	
	public IQuery createQuery(ILiteral... literals);
	public IQuery createQuery(List<ILiteral> literals);

	/**
	 * Creates a rule out of a list of head and a list of body literals.
	 * @param head the head literals
	 * @param body the body literals
	 */
	public IRule createRule(List<ILiteral> head, List<ILiteral> body);
	
	public ITuple createTuple(ITerm... terms);
	public ITuple createTuple(List<ITerm> terms);
}
