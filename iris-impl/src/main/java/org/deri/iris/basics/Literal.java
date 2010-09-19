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
import org.deri.iris.api.basics.ILiteral;

/**
 * <p>
 * Simple literal implementation.
 * </p>
 * <p>
 * $Id$
 * </p>
 * @author Richard Pöttler, richard dot poettler at deri dot org
 * @author Darko Anicic, DERI Innsbruck
 * @version $Revision$
 */
public class Literal implements ILiteral {

	private final boolean positive;

	private final IAtom atom;

	Literal(final IAtom atom) {
		this(true, atom);
	}
	
	Literal(final boolean positive, final IAtom atom) {
		this.atom = atom;
		this.positive = positive;
	}
	
	public boolean isPositive() {
		return positive;
	}

	public IAtom getAtom() {
		return atom;
	}
	
	public int compareTo(final ILiteral oo) {
		ILiteral o = (ILiteral) oo;
		if ((positive != o.isPositive()) && positive) {
			return 1;
		} else if ((positive != o.isPositive()) && !positive) {
			return -1;
		}
		return atom.compareTo(o.getAtom());
	}

	public int hashCode() {
		int result = 17;
		result = result * 37 + atom.hashCode();
		result = result * 37 + (positive ? 1 : 0);
		return result;
	}

	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Literal)) {
			return false;
		}
		Literal l = (Literal) o;
		return atom.equals(l.atom) && (positive == l.positive);
	}
	
	public String toString() {
		return (positive ? "" : "!") + atom;
	}
}
