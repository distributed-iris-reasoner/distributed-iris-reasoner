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

import org.deri.iris.api.basics.IPredicate;

/**
 * <p>
 * This is a simple IPredicate implementation.
 * </p>
 * <p>
 * NOTE: This implementation is immutable
 * </p>
 * <p>
 * $Id$
 * </p>
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 * @version $Revision$
 */
public class Predicate implements IPredicate {

	private final String symbol;
	
	/** A (unique) string containing the predicate name and arity. */
	private final String symbolPlusArity;

	private final int arity;
	
	Predicate(final String symbol, final int arity) {
		this.symbol = symbol;
		this.arity = arity;
		
		StringBuilder b = new StringBuilder();
		
		b.append( symbol ).append( '$' ).append( arity );
		symbolPlusArity = b.toString();
	}

	public String getPredicateSymbol() {
		return "http://larkc.eu/default/" + symbol;
	}

	public int getArity() {
		return arity;
	}

	public int hashCode() {
		return symbolPlusArity.hashCode();
	}

	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Predicate)) {
			return false;
		}
		Predicate p = (Predicate) o;
		return symbolPlusArity.equals(p.symbolPlusArity);
	}

	public int compareTo(IPredicate o) {
		Predicate predicate = (Predicate) o;
		return symbolPlusArity.compareTo( predicate.symbolPlusArity );
	}

	public String toString() {
		return symbol;
	}
}
