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
package org.deri.iris.terms.concrete;

import java.math.BigDecimal;
import java.net.URI;

import org.deri.iris.api.terms.concrete.IFloatTerm;

/**
 * <p>
 * Simple implementation of the IFloatTerm.
 * </p>
 */
public class FloatTerm extends AbstractNumericTerm implements IFloatTerm {

	private final Float value;

	private BigDecimal decimalValue;

	/**
	 * Constructs a new float with the given value.
	 * 
	 * @param value the float value for this object
	 * @throws NullPointerException if the float is null
	 */
	FloatTerm(float value) {
		this.value = value;
	}

	public BigDecimal getValue() {
		if (isNotANumber() || isPositiveInfinity() || isNegativeInfinity()) {
			return null;
		}

		if (decimalValue == null) {
			decimalValue = new BigDecimal(Float.toString(value));
		}

		return decimalValue;
	}

	public boolean isNotANumber() {
		return value.equals(Float.NaN);
	}

	public boolean isPositiveInfinity() {
		return value.equals(Float.POSITIVE_INFINITY);
	}

	public boolean isNegativeInfinity() {
		return value.equals(Float.NEGATIVE_INFINITY);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	public URI getDatatypeIRI() {
		return URI.create(IFloatTerm.DATATYPE_URI);
	}

}
