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
package org.deri.iris.terms.concrete;

import org.deri.iris.api.terms.INumericTerm;
import org.deri.iris.api.terms.ITerm;

/**
 * @author Adrian Marte
 */
public abstract class AbstractNumericTerm implements INumericTerm {

	public boolean isGround() {
		return true;
	}

	@Override
	public boolean equals(Object thatObject) {
		if (thatObject == null || !(thatObject instanceof INumericTerm)) {
			return false;
		}

		INumericTerm thatNumericTerm = (INumericTerm) thatObject;

		if ((isNotANumber() && thatNumericTerm.isNotANumber())
				|| (isPositiveInfinity() && thatNumericTerm
						.isPositiveInfinity())
				|| (isNegativeInfinity() && thatNumericTerm
						.isNegativeInfinity())) {
			return true;
		}

		return compareTo(thatNumericTerm) == 0;
	}

	public int compareTo(ITerm thatObject) {
		if (thatObject == null || !(thatObject instanceof INumericTerm)) {
			return 1;
		}

		INumericTerm thatNumeric = (INumericTerm) thatObject;

		// NaN = NaN
		if (isNotANumber() && thatNumeric.isNotANumber()) {
			return 0;
		}
		// NaN > non-NaN values
		else if (isNotANumber()) {
			return 1;
		}
		// non-NaN values < NaN
		else if (thatNumeric.isNotANumber()) {
			return -1;
		}

		// -INF = -INF
		if (isNegativeInfinity() && thatNumeric.isNegativeInfinity()) {
			return 0;
		}
		// +INF = +INF
		else if (isPositiveInfinity() && thatNumeric.isPositiveInfinity()) {
			return 0;
		}
		// non-+INF < +INF
		else if (thatNumeric.isPositiveInfinity()) {
			return -1;
		}
		// non--INF > -INF
		else if (thatNumeric.isNegativeInfinity()) {
			return 1;
		}
		// -INF < non--INF
		else if (isNegativeInfinity()) {
			return -1;
		}
		// +INF > non-+INF
		else if (isPositiveInfinity()) {
			return 1;
		}

		INumericTerm thatNumericTerm = (INumericTerm) thatObject;
		return getValue().compareTo(thatNumericTerm.getValue());
	}

	@Override
	public int hashCode() {
		return getValue().hashCode();
	}

	@Override
	public String toString() {
		return toCanonicalString();
	}

	public String toCanonicalString() {
		return getValue().toPlainString();
	}

}
