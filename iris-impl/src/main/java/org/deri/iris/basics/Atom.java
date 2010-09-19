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

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;

/**
 * <p>
 * A simple Atom implementation.
 * </p>
 * <p>
 * $Id$
 * </p>
 *
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 * @author Darko Anicic, DERI Innsbruck
 * @version $Revision$
 */
public class Atom implements IAtom {

	private final IPredicate predicate;
	
	private final ITuple tuple;

	Atom(final IPredicate predicate, final ITuple tuple) {
		if (predicate == null || tuple == null) {
			throw new IllegalArgumentException("The parameters must not be null");
		}
		if (predicate.getArity() != tuple.size()) {
			throw new IllegalArgumentException("Cannot create an atom when" +
					" a tuple's arity does not match the predicate's arity.");
		}
		this.predicate = predicate;
		this.tuple = tuple;
	}

	public IPredicate getPredicate() {
		return predicate;
	}
	
	public ITuple getTuple() {
		return this.tuple;
	}

	public boolean isGround() {
		return this.tuple.isGround();
	}

	public int compareTo(IAtom o) {
		int res = 0;
		if ((res = predicate.compareTo(o.getPredicate())) != 0) {
			return res;
		}
		if ((res = this.tuple.compareTo(o.getTuple())) != 0) {
			return res;
		}
		return 0;
	}

	public int hashCode() {
		int result = 17;
		result = result * 37 + this.predicate.hashCode();
		result = result * 37 + this.tuple.hashCode();
		
		return result;
	}

	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Atom)) {
			return false;
		}
		Atom a = (Atom) o;
		
		return (this.predicate.equals(a.predicate)) && 
				this.tuple.equals(a.getTuple());
	}

	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(this.predicate);
		buffer.append(this.tuple);
		
		return buffer.toString();
	}

	public boolean isBuiltin() {
		return false;
	}
}
