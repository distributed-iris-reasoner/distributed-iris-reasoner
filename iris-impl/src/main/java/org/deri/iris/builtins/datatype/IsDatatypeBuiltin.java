/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2009 Semantic Technology Institute (STI) Innsbruck, 
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
package org.deri.iris.builtins.datatype;

import static org.deri.iris.factory.Factory.BASIC;

import java.net.URI;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.IConcreteTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IIri;
import org.deri.iris.builtins.BooleanBuiltin;

/**
 * Represents the RIF built-in <code>isLiteralOfType</code> as defined in
 * http://www.w3.org/2005/rules/wiki/DTB#pred:isLiteralOfType.
 */
public class IsDatatypeBuiltin extends BooleanBuiltin {

	/** The predicate defining this built-in. */
	private static final IPredicate PREDICATE = BASIC.createPredicate(
			"IS_DATATYPE", 2);

	/**
	 * Constructor. At least two terms must be passed to the constructor,
	 * otherwise an exception will be thrown.
	 * 
	 * @param terms The terms.
	 * @throws IllegalArgumentException if one of the terms is {@code null}
	 * @throws IllegalArgumentException if the number of terms submitted is not
	 *             1
	 * @throws IllegalArgumentException if t is <code>null</code>
	 */
	public IsDatatypeBuiltin(ITerm... terms) {
		super(PREDICATE, terms);
	}

	@Override
	protected boolean computeResult(ITerm[] terms) {
		if (terms[0] instanceof IConcreteTerm) {
			IConcreteTerm term = (IConcreteTerm) terms[0];

			if (terms[1] instanceof IIri) {
				URI iri = ((IIri) terms[1]).getURI();

				return iri.equals(term.getDatatypeIRI());
			}
		}

		return false;
	}

}
