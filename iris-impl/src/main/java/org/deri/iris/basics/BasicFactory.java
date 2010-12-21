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
package org.deri.iris.basics;

import java.util.Arrays;
import java.util.List;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.api.terms.ITerm;

/**
 * <p>
 * Simple implementatiion of the basic factory.
 * </p>
 * <p>
 * $Id$
 * </p>
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 * @author Darko Anicic, DERI Innsbruck
 * @version $Revision$
 */
public class BasicFactory implements IBasicFactory {

	private static final IBasicFactory FACTORY = new BasicFactory();

	private BasicFactory() {
		// this is a singelton
	}

	public IAtom createAtom(IPredicate p, ITuple tuple) {
		return new Atom(p, tuple);
	}

	public ILiteral createLiteral(boolean isPositive, IAtom atom) {
		return new Literal(isPositive, atom);
	}

	public ILiteral createLiteral(boolean positive, IPredicate p,
			ITuple tuple) {
		return createLiteral(positive, createAtom(p, tuple));
	}

	public IPredicate createPredicate(String symbol, int arity) {
		return new Predicate(symbol, arity);
	}

	public IQuery createQuery(ILiteral... literals) {
		return createQuery(Arrays.asList(literals));
	}

	public IQuery createQuery(List<ILiteral> literals) {
		return new Query(literals);
	}

	public IRule createRule(List<ILiteral> head, List<ILiteral> body) {
		return new Rule(head, body); 
	}

	public ITuple createTuple(ITerm... terms) {
		return createTuple(Arrays.asList(terms));
	}
	
	public ITuple createTuple(List<ITerm> terms) {
		return new Tuple(terms);
	}

	public IAtom createAtom(final IAtom a) {
		if (a == null) {
			throw new NullPointerException("The atom must not be null");
		}
		if (a.isBuiltin()) {
			throw new IllegalArgumentException("The atom must not be a builtin atom");
		}
		return createAtom(a.getPredicate(), createTuple(a.getTuple()));
	}

	public ILiteral createLiteral(final ILiteral l) {
		if (l == null) {
			throw new NullPointerException("The literal must not be null");
		}
		return createLiteral(l.isPositive(), createAtom(l.getAtom()));
	}

	public static IBasicFactory getInstance() {
		return FACTORY;
	}
}
